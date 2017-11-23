package t9.core.funcs.doc.send.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.funcs.doc.send.data.T9SubjectTerm;
import t9.core.funcs.doc.send.logic.T9WordSelectLogic;

public class T9WordSelectAct {
  public String getTree(HttpServletRequest request,HttpServletResponse response) throws Exception {
  	String idStr = request.getParameter("id");
  	String name=null;
  	int id = 0;
  	if(idStr!=null && !"".equals(idStr)){
  	  id = Integer.parseInt(idStr);
  	 }
  	Connection dbConn = null;
  	Statement stmt = null;
    ResultSet rs = null;
    String queryStr = "SELECT * FROM subject_term WHERE type_flag=0 and parent_id= " + id+" ORDER BY SORT_NO asc";
    try {
		  T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		  dbConn = requestDbConn.getSysDbConn();
		  stmt = dbConn.createStatement();
		  rs = stmt.executeQuery(queryStr);
		  StringBuffer sb = new StringBuffer("[");
		  ArrayList<T9SubjectTerm> words = new ArrayList<T9SubjectTerm>();
		  while(rs.next()){
		    T9SubjectTerm word = new T9SubjectTerm();
		    word.setSeqId(rs.getInt("SEQ_ID"));
		    word.setWord(rs.getString("WORD"));
		    word.setTypeFlag(rs.getInt("type_flag"));
		    words.add(word);
		  }
		  T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
		  String postDept = person.getPostDept();
		  String postPriv = person.getPostPriv();
		  T9PersonLogic pl = new T9PersonLogic();
		  for (T9SubjectTerm d : words) {
		    if (d.getSeqId() != Integer.parseInt(idStr)) {
		      int nodeId = d.getSeqId();
		      String wordId = String.valueOf(nodeId);
		      name = T9Utility.encodeSpecial(d.getWord());
		      int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
		      String extData = "";
		      if (person.isAdminRole() || postPriv.equals("1")) {
		        extData = "isPriv";
		      } else {
		        if (pl.findId(postDept, wordId)) {
		          extData = "isPriv";
		        } else {
		          extData = "";
		        }
	        }
		      String imgAddress=null;
		      if(d.getTypeFlag()==0){
		        imgAddress =  request.getContextPath() + "/core/styles/style1/img/dtree/folder.gif";
		      }else{
		        imgAddress = request.getContextPath() + "/core/styles/style1/img/dtree/file.jpg";
		      }
		      sb.append("{");
		      sb.append("nodeId:\"" + nodeId + "\"");
		      sb.append(",name:\"" + name + "\"");
		      sb.append(",isHaveChild:" + isHaveChild + "");
		      sb.append(",extData:\"" + extData + "\"");
		      sb.append(",imgAddress:\"" + imgAddress + "\"");
		      sb.append(",title:\"" + name + "\"");
		      sb.append("},");
	      }
	    }
		  sb.deleteCharAt(sb.length() - 1);
		  sb.append("]");
		  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		  request.setAttribute(T9ActionKeys.RET_MSRG, name);
		  request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());   
    }catch(Exception ex) {
	    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	    ex.printStackTrace();
	    throw ex;
	  }finally {
	    T9DBUtility.close(stmt, rs, null);
	  }
    return "/core/inc/rtjson.jsp";
  }
		    
  public int IsHaveChild(Connection conn, int id) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    try {
      String str = "SELECT * FROM subject_term WHERE type_flag = 0 and parent_id = " + id+" ORDER BY SORT_NO asc";
      stm = conn.createStatement();
      rs = stm.executeQuery(str);
      if (rs.next()) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
  }
		    
  public String getWordBySort(HttpServletRequest request, HttpServletResponse response) throws Exception {
  	String wordId = request.getParameter("deptId");
  	Connection dbConn = null;
  	try {
  	  T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
  	  dbConn = requestDbConn.getSysDbConn();
  	  T9WordSelectLogic wordlogic = new T9WordSelectLogic();
  	  List<T9SubjectTerm> list = wordlogic.getPersonsByDept(dbConn, Integer.parseInt(wordId));
  	  String wordName = wordlogic.getNameById(Integer.parseInt(wordId), dbConn);
  	  StringBuffer data = new StringBuffer("[");
  	  StringBuffer sb = new StringBuffer();
  	  for (T9SubjectTerm p : list) {
  	    if(!"".equals(sb.toString())){
  	      sb.append(",");
  	    }
    	  String userName = p.getWord();
    	  sb.append("\"" + T9Utility.encodeSpecial(userName) + "\"");
  	  }
  	  data.append(sb).append("]");
  	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
  	  request.setAttribute(T9ActionKeys.RET_MSRG, wordName);
  	  request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
  	} catch (Exception ex) {
  	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  	  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
  	  throw ex;
    }
  	return "/core/inc/rtjson.jsp";
  }
  
  public String getWordBySearch(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
    	T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userName = request.getParameter("userName");
      T9WordSelectLogic osl = new T9WordSelectLogic();
      StringBuffer data = osl.getQueryUser2Json(dbConn, userName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception ex) {
  		request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  		request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
  		throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
