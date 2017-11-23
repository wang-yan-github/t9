package t9.subsys.inforesouce.docmgr.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.docmgr.data.T9SubjectTerm;
import t9.subsys.inforesouce.docmgr.logic.T9SubjectTermLogic;

public class T9WordTreeAct {
  public String getTree(HttpServletRequest request,HttpServletResponse response) throws Exception {
    String idStr = request.getParameter("id");
    int id = 0;
    if (idStr != null && !"".equals(idStr) && !"root".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String queryStr = "SELECT * FROM subject_term WHERE parent_id= " + id+" ORDER BY SORT_NO asc";
    try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       stmt = dbConn.createStatement();
       rs = stmt.executeQuery(queryStr);
       StringBuffer sb = new StringBuffer("[");
       if (id == 0 && !"root".equals(idStr)) {
  		   sb.append("{");
  			 sb.append("nodeId:\"" + "root" + "\"");
  			 sb.append(",name:\"" + "主题词(类别)列表" + "\"");
  			 sb.append(",isHaveChild:" + 1 + "");
  			 sb.append("}");
  			 sb.append("]");
       }else{
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
         T9PersonLogic dl = new T9PersonLogic();
         for (T9SubjectTerm d : words) {
           int nodeId = d.getSeqId();
           String deptId = String.valueOf(nodeId);
           String name = T9Utility.encodeSpecial(d.getWord());
           int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
           String extData = "";
           if (person.isAdminRole() || postPriv.equals("1")) {
             extData = "isPriv";
           } else {
             if(dl.findId(postDept, deptId)) {
               extData = "isPriv";
             } else {
               extData = "";
             }
           }
           String imgAddress=null;
           if(d.getTypeFlag()==0){
             imgAddress = "/t9/core/styles/style1/img/dtree/folder.gif";
           }else{
             imgAddress = "/t9/core/styles/style1/img/dtree/file.jpg";
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
         if (words.size() > 0) {
           sb.deleteCharAt(sb.length() - 1);
         }
         sb.append("]");
       }
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
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
      String str = "SELECT * FROM subject_term WHERE parent_id = " + id+" ORDER BY SORT_NO asc";
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
  
  
  public String selectWordToAttendance(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SubjectTermLogic wordLogic = new T9SubjectTermLogic();
      String data = "";
      data = wordLogic.getWordTreeJson(0, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}