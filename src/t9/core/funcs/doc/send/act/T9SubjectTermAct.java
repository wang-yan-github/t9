package t9.core.funcs.doc.send.act;

import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.core.funcs.doc.send.data.T9SubjectTerm;
import t9.core.funcs.doc.send.logic.T9SubjectTermLogic;

public class T9SubjectTermAct {
  public String insertWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String word=request.getParameter("word");
      int parentId=Integer.parseInt(request.getParameter("parentId"));
      int sortNo=Integer.parseInt(request.getParameter("sortNo"));
      int typeFlag=Integer.parseInt(request.getParameter("typeFlag"));
      T9SubjectTerm st = new T9SubjectTerm();
      st.setParentId(parentId);
      st.setWord(word);
      st.setSortNo(sortNo);
      st.setTypeFlag(typeFlag);
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, st);
      T9SubjectTerm st1 = new T9SubjectTerm();
      T9SubjectTermLogic stl = new T9SubjectTermLogic();
      st1=stl.getMaxSeqId(dbConn);
      int nodeId=st1.getSeqId();
      String name=st1.getWord();
      int typeFlag1 = st1.getTypeFlag();
      String data = "[{nodeId:\"" + nodeId + "\",name:\"" + name + "\",typeFlag:\""+typeFlag1+"\" }]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
  	  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  	  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
  	  throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
	
  public String updateWord(HttpServletRequest request,HttpServletResponse response) throws Exception{
	  Connection dbConn=null;
	  T9ORM orm = new T9ORM();
	  try{ 
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("treeId");
      String word=request.getParameter("word");
      int parentId=Integer.parseInt(request.getParameter("parentId"));
      int sortNo=Integer.parseInt(request.getParameter("sortNo"));
      int typeFlag=Integer.parseInt(request.getParameter("typeFlag"));
      int seqId = 0;
      if (!T9Utility.isNullorEmpty(seqIdStr)) {
        seqId = Integer.parseInt(seqIdStr);
      }
      T9SubjectTerm st = (T9SubjectTerm) orm.loadObjSingle(dbConn, T9SubjectTerm.class, seqId);
      st.setParentId(parentId);
      st.setSortNo(sortNo);
      st.setTypeFlag(typeFlag);
      st.setWord(word);
      orm.updateSingle(dbConn,st);
      int nodeId=st.getSeqId();
      String name=st.getWord();
      int typeFlag1 = st.getTypeFlag();
      String data = "[{nodeId:\"" + nodeId + "\",name:\"" + name + "\",typeFlag:\""+typeFlag1+"\" }]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
	  } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
	  }
	  return "/core/inc/rtjson.jsp";
  }
  public String getDept(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Object obj = orm.loadObjSingle(dbConn, T9SubjectTerm.class, treeId);
      data = T9FOM.toJson(obj).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
	
  public String selectWordToAttendance(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute( T9Const.LOGIN_USER);
      T9SubjectTermLogic wordLogic = new T9SubjectTermLogic();
      int userDeptIdFunc = Integer.parseInt(request.getParameter("userDeptId"));
      String data = "";
      data = wordLogic.getWordTreeJson1(0, dbConn, userDeptIdFunc);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
	    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	    throw ex;
	  }
    return "/core/inc/rtjson.jsp";
  }
  
  public static boolean findId(String str, String id) {
	  if(str == null || id == null || "".equals(str) || "".equals(id)){
      return false;
    }
    String[] aStr = str.split(",");
    for(String tmp : aStr){
      if(tmp.equals(id)){
        return true;
      }
    }
	  return false;
  }
  public String deleteDept(HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
		int seqId = Integer.parseInt(request.getParameter("treeId"));
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
    T9SubjectTerm st = new T9SubjectTerm();
    st.setSeqId(seqId);
    T9ORM orm = new T9ORM();
    T9SubjectTermLogic wordlogic = new T9SubjectTermLogic();
    List lista = new ArrayList();
    List lidd = wordlogic.deleteWordMul(dbConn, seqId);
    lidd.add(st);
    for(int i = 0; i < lidd.size(); i++){
      T9SubjectTerm deptent = (T9SubjectTerm) lidd.get(i);
      String[] filters = new String[]{"DEPT_ID = " + deptent.getSeqId() + ""};
      List<T9Person> listPer = orm.loadListSingle(dbConn, T9Person.class, filters);
      for(int x = 0; x < listPer.size(); x++){
        T9Person per = (T9Person) listPer.get(x);
        if(per.isAdmin()){
          per.setDeptId(0);
          orm.updateSingle(dbConn, per);
          continue;
        }else{
          wordlogic.deleteDepPerson(dbConn, deptent.getSeqId());
        }
      }
      wordlogic.deleteWord(dbConn, deptent.getSeqId());
    }
	    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	    request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
    }catch(Exception ex){
	    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	    throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }	
  
  /**
   * 导出到EXCEL表格中
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportToExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    response.setCharacterEncoding("GBK");
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("主题词.csv", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9SubjectTermLogic ieml = new T9SubjectTermLogic();
      ArrayList<T9DbRecord > dbL = ieml.toExportWordData(conn);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
    }
    return null;
  }
  
  /**
   * 导入到EXCEL表格中
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String importWord(HttpServletRequest request,HttpServletResponse response) throws Exception{
    InputStream is = null;
    Connection conn = null;
    String data = null;
    int isCount = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      is = fileForm.getInputStream();
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is,"gbk");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      StringBuffer sb = new StringBuffer("[");
      T9SubjectTermLogic dl = new T9SubjectTermLogic();
      String word = "";
      String parentId = "";
      String sortNo="";
      String typeFlag="";
      String infoStr= "";
      String color = "red";
      int typeFlagNo=0;
      int deptParentNo = 0;
      Map map = new HashMap();
      String remark = "成功导入主题词：";
      boolean hasSucess = false;
      for(int i = 0; i < drl.size(); i++){
        word = (String) drl.get(i).getValueByName("主题词");
        if(T9Utility.isNullorEmpty(word)){
          continue;
        }
        word = getOutOf(word);
        parentId = getOutOf((String) drl.get(i).getValueByName("类别"));  
        sortNo = getOutOf((String) drl.get(i).getValueByName("排序号")); 
        typeFlag = getOutOf((String) drl.get(i).getValueByName("类型")); 
        if(T9Utility.isNullorEmpty(typeFlag)){
          typeFlag="主题词";
        }
        infoStr = "导入失败,主题词 " + word + " 已经存在";
        if(dl.existsWordName(conn, word)){
          color = "red";
          infoStr = "导入失败,主题词" + word + " 已经存在";
          sb.append("{");
          sb.append("word:\"" + (word == null ? "" : word)+ "\"");
          sb.append(",parentId:\"" + (parentId == null ? "" : parentId) + "\"");
          sb.append(",sortNo:\"" + (sortNo == null ? "0" : sortNo)+ "\"");
          sb.append(",typeFlag:\"" + (typeFlag == null ? "" : typeFlag)+ "\"");
          sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
          sb.append(",color:\"" + (color == null ? "" : color) + "\"");
          sb.append("},");
        }else{
          if(T9Utility.isNullorEmpty(parentId) && "主题词".equals(typeFlag)){
            color = "red";
            infoStr = "导入失败,主题词" + word + "的类别为空";
            sb.append("{");
            sb.append("word:\"" + (word == null ? "" : word)+ "\"");
            sb.append(",parentId:\"" + (parentId == null ? "" : parentId) + "\"");
            sb.append(",sortNo:\"" + (sortNo == null ? "0" : sortNo)+ "\"");
            sb.append(",typeFlag:\"" + (typeFlag == null ? "主题词" : typeFlag)+ "\"");
            sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
            sb.append(",color:\"" + (color == null ? "" : color) + "\"");
            sb.append("},");
          }else{
            isCount++;
            infoStr = "成功";
            color = "black";
            sb.append("{");
            sb.append("word:\"" + (word == null ? "" : word) + "\"");
            sb.append(",parentId:\"" + (parentId == null ? "" : parentId) + "\"");
            sb.append(",sortNo:\"" + (sortNo == null ? "0" : sortNo)+ "\"");
            sb.append(",typeFlag:\"" + (typeFlag == null ? "主题词" : typeFlag)+ "\"");
            sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
            sb.append(",color:\"" + (color == null ? "" : color) + "\"");
            sb.append("},");
            if(T9Utility.isNullorEmpty(parentId)){
              deptParentNo = 0;
            }else{
              deptParentNo = dl.getWordIdLogic(conn, parentId);
            }
            typeFlagNo=1;
            if("类别".equals(typeFlag)){
              typeFlagNo=0;
            }
            dl.saveWord(conn, word, deptParentNo,sortNo,typeFlagNo);
            remark += word + ",";
            hasSucess = true;
          }
        }
      }
      if (sb.charAt(sb.length() - 1) == ','){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      data = sb.toString();
      if (hasSucess) {
        T9SysLogLogic.addSysLog(conn, T9LogConst.ADD_DEPT, remark, person.getSeqId(), request.getRemoteAddr());
      }
      request.setAttribute("contentList", data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return "/subsys/inforesource/docmgr/docword/importword.jsp?data="+data+"&isCount="+isCount;
  }  
  public String getOutOf(String str) {
    if (str != null) {
      str = str.replace("'", "");
      str = str.replace("\"", "");
      str = str.replace("\\", "");
      str = str.replaceAll("\n", "");
      str = str.replaceAll("\r", "");
    }
    return str;
  }
  
  public String getAjaxCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn=null;
    try{
      T9RequestDbConn requestDbConn=(T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn=requestDbConn.getSysDbConn();
      String parentId=request.getParameter("parentId");
      String typeFlag = request.getParameter("typeFlag");
      String word = request.getParameter("word");
      T9SubjectTermLogic stl = new T9SubjectTermLogic();
      String data="";
      data=stl.getAjaxCheckLogic(dbConn,parentId,typeFlag,word);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSeqId(HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn=null;
    Statement stmt = null;
    ResultSet rs = null;
    String data="";
    try{
      T9RequestDbConn requestDbConn=(T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn=requestDbConn.getSysDbConn();
      String query="SELECT seq_id FROM subject_term WHERE type_flag = 0 ORDER BY seq_id asc";
      stmt = dbConn.createStatement();
      rs=stmt.executeQuery(query);
      while(rs.next()){
        data = data + rs.getInt("seq_id")+",";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" +data+ "\"" );
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
