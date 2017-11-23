package t9.core.funcs.dept.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.data.T9DepartmentCache;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.system.ispirit.n12.org.act.T9IsPiritOrgAct;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.module.report.logic.T9DepartmentSyncLogic;
import t9.core.module.report.logic.T9PersonSyncLogic;
import t9.core.module.report.logic.T9ReportSyncLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9DeptAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String insertDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String deptParentDesc = request.getParameter("deptParentDesc");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String deptNo = request.getParameter("deptNo");
      int newSeqId = Integer.parseInt(getDeptAddSeq(dbConn));
      String remark = getDeptLog(dbConn, newSeqId);
      
      T9DeptLogic dl = new T9DeptLogic();
//      if(dl.existsTableNo(dbConn, deptNo)){
//        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
//        request.setAttribute(T9ActionKeys.RET_MSRG, "部门排序号以存在，请重新填写！");
//        return "/core/inc/rtjson.jsp";
//      }else{
//        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
//      }
      T9Department dpt = (T9Department) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, dpt);
      
      
      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      if (T9ReportSyncLogic.hasSync) {
        int max = T9ReportSyncLogic.getMax(dbConn, "select max(SEQ_ID) from Department");
        dpt.setSeqId(max);
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9DepartmentSyncLogic logic = new T9DepartmentSyncLogic();
        logic.addDepartment(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        int max = T9OaSyncLogic.getMax(dbConn, "select max(SEQ_ID) from Department");
        dpt.setSeqId(max);
        Connection oaConn = T9OaSyncLogic.getOAConn();
        t9.core.module.oa.logic.T9DepartmentSyncLogic logic = new t9.core.module.oa.logic.T9DepartmentSyncLogic();
        logic.addDepartment(dpt, oaConn);
        if (oaConn != null) {
          oaConn.close();
        }
      }
      
      T9DepartmentCache.removeAll();
      T9SysLogLogic.addSysLog(dbConn, T9LogConst.ADD_DEPT, remark, person.getSeqId(), request.getRemoteAddr());
      //dbConn.close();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
  
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getDept(HttpServletRequest request, HttpServletResponse response)
      throws Exception{
    Connection dbConn = null;
    try{
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      Object obj = orm.loadObjSingle(dbConn, T9Department.class, treeId);
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

  public String getGroupDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Department dpt = null;
      String data = null;
      T9ORM orm = new T9ORM();
      data = T9FOM.toJson(orm.loadObjSingle(dbConn, dpt.getClass(), 6)).toString();
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
  
  /**
   * 系统日志：获取部门DEPT_ID,DEPT_NAME,DEPT_PARENT
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getDeptLog(Connection dbConn, int deptId) throws Exception {
    String data = "";
    T9DeptLogic dl = new T9DeptLogic();
    data = dl.getDeptNameLogic(dbConn, deptId);
    return data;
  }
  
  /**
   * 获取新建部门SEQ_ID（用于系统日志）
   * @param dbConn
   * @param deptId
   * @return
   * @throws Exception
   */
  
  public String getDeptAddSeq(Connection dbConn) throws Exception {
    String data = "";
    T9DeptLogic dl = new T9DeptLogic();
    data = dl.getDeptAddSeqLogic(dbConn);
    return data;
  }
  
  public String updateDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String managerDesc = request.getParameter("managerDesc");
      int treeId = Integer.parseInt(request.getParameter("treeId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String deptNo = request.getParameter("deptNo");
      String deptNoOld = request.getParameter("deptNoOld");
      T9DeptLogic dl = new T9DeptLogic();
      T9Department dpt = (T9Department) T9FOM.build(request.getParameterMap());
      dpt.setSeqId(treeId);
      String remark = getDeptLog(dbConn, treeId);
      
      T9ORM orm = new T9ORM();
      if((deptNoOld.trim()).equals(deptNo.trim())){
        orm.updateSingle(dbConn, dpt);
        T9SysLogLogic.addSysLog(dbConn, T9LogConst.EIDT_DEPT, remark, person.getSeqId(), request.getRemoteAddr());
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
      }else{
        if(dl.existsTableNo(dbConn, deptNo)){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "部门排序号已存在，请重新填写！");
          return "/core/inc/rtjson.jsp";
        }else{
          orm.updateSingle(dbConn, dpt);
          T9SysLogLogic.addSysLog(dbConn, T9LogConst.EIDT_DEPT, remark, person.getSeqId(), request.getRemoteAddr());
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
        }
      }

      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9DepartmentSyncLogic logic = new T9DepartmentSyncLogic();
        logic.editDepartment(dpt, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection oaConn = T9OaSyncLogic.getOAConn();
        t9.core.module.oa.logic.T9DepartmentSyncLogic logic = new t9.core.module.oa.logic.T9DepartmentSyncLogic();
        logic.editDepartment(dpt, oaConn);
        if (oaConn != null) {
          oaConn.close();
        }
      }
      T9DepartmentCache.removeAll();
      
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 级联删除部门及部门下的人员，如果该部门下有系统管理员（不能删除），系统管理员着转到离职人员中
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      int seqId = Integer.parseInt(request.getParameter("treeId"));
      String seqdd = request.getParameter("treeId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Department dt = null;
      T9Department dtt = new T9Department();
      dtt.setSeqId(seqId);
      T9Department ment = null;
      T9ORM orm = new T9ORM();
      T9DeptLogic deptlogic = new T9DeptLogic();
      //deptlogic.deleteDeptMul(dbConn, seqId);
      List lista = new ArrayList();
      List lidd = deptlogic.deleteDeptMul(dbConn, seqId);
      
      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
      
      
      
      lidd.add(dtt);
      String remark = getDeptLog(dbConn, seqId);
      T9SysLogLogic.addSysLog(dbConn, T9LogConst.DELETE_DEPT, remark, person.getSeqId(), request.getRemoteAddr());
      for(int i = 0; i < lidd.size(); i++){
       //ment =  (T9Department) deptlogic.deleteDeptMul(dbConn, seqId).get(i);
        //dt = (T9Department)orm.loadObjComplex(dbConn, T9Department.class, deptent.getSeqId());
        //dt.setSeqId(deptent.getSeqId());
        T9Department deptent = (T9Department) lidd.get(i);
        String[] filters = new String[]{"DEPT_ID = " + deptent.getSeqId() + ""};
        List<T9Person> listPer = orm.loadListSingle(dbConn, T9Person.class, filters);
        for(int x = 0; x < listPer.size(); x++){
          T9Person per = (T9Person) listPer.get(x);
          if(per.isAdmin()){
            per.setDeptId(0);
            orm.updateSingle(dbConn, per);
          }else{
            deptlogic.deleteDepPerson(dbConn, deptent.getSeqId());
          }
          if (T9ReportSyncLogic.hasSync) {
            Connection reportConn = T9ReportSyncLogic.getReportConn();
            T9PersonSyncLogic logic = new T9PersonSyncLogic();
            logic.delPersonByDept(deptent.getSeqId(), reportConn, per.isAdmin());
            if (reportConn != null) {
              reportConn.close();
            }
          }
          if (T9OaSyncLogic.hasSync) {
            Connection oaConn = T9OaSyncLogic.getOAConn();
            t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
            logic.delPersonByDept(deptent.getSeqId(), oaConn, per.isAdmin());
            if (oaConn != null) {
              oaConn.close();
            }
          }
        }
        deptlogic.deleteDept(dbConn, deptent.getSeqId());
        //orm.deleteComplex(dbConn, dt);
      }
     // T9Department dt = (T9Department)orm.loadObjComplex(dbConn, T9Department.class, seqId);
      //T9Department dpt = (T9Department) T9FOM.build(request.getParameterMap());
      //dt.setSeqId(seqId);
      //orm.deleteComplex(dbConn, dt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
      T9DepartmentCache.removeAll();
      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(dbConn);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getTree(HttpServletRequest request, HttpServletResponse response)
  throws Exception{
    String idStr = request.getParameter("id");
    int id = 0;
    if(idStr != null && !"".equals(idStr.trim())){
      id = Integer.parseInt(idStr);
    }
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/xml");
    response.setHeader("Cache-Control", "no-cache");
    PrintWriter out = response.getWriter();
    out.print("<?xml version=\'1.0\' encoding=\'utf-8'?>");
    out.print("<menus>");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("deptId", id);
      List<T9Person> list = orm.loadListSingle(dbConn, T9Person.class, map);
      for(T9Person d : list){
        out.print("<menu>");
        out.print("<id>" + d.getSeqId() + "</id>");
        out.print("<name>" + d.getUserId() + "</name>");
        out.print("<parentId>" + d.getDeptId() + "</parentId>");
        out.print("<isHaveChild>0</isHaveChild>");
        out.print("</menu>");
      }
      map.remove("deptId");
      map.put("deptParent", id);
      List<T9Department> deptList = orm.loadListSingle(dbConn, T9Department.class, map);
      for(T9Department t : deptList) {
        out.print("<menu>");
        out.print("<id>" + t.getSeqId() + "</id>");
        out.print("<name>" + t.getDeptName() + "</name>");
        out.print("<parentId>" + t.getDeptParent() + "</parentId>");
        out.print("<isHaveChild>" + IsHaveChild(request, response, String.valueOf(t.getSeqId())) + "</isHaveChild>");
        out.print("</menu>");      
      }    
      out.print("<parentNodeId>" + id + "</parentNodeId>");
      out.print("<count>" + (list.size()+deptList.size()) + "</count>");
      out.print("</menus>");
      out.flush();
      out.close();
      //dbConn.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public int IsHaveChild(HttpServletRequest request,
      HttpServletResponse response,String id) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      Map map = new HashMap();
      map.put("DEPT_PARENT", id);
      List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class, map);
      if(list.size() != -1){
        return 1;
      }else{
        return 0;
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  
  public String selectDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    response.setContentType("text/html;charset=UTF-8");
    request.setCharacterEncoding("UTF-8");
    String treeId = request.getParameter("treeId");
    String TO_ID = request.getParameter("TO_ID");
    String TO_NAME = request.getParameter("TO_NAME");
    //String deptLocal = new String(request.getParameter("deptLocal").getBytes("ISO-8859-1"),"UTF-8");
    String deptLocal = request.getParameter("deptLocal");
    String managerList = request.getParameter("deptList");
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      ArrayList<T9Person> personList = null;
      Map map = new HashMap();
      map.put("DEPT_ID", treeId);
      //map.put("manager", managerList);
      personList = (ArrayList<T9Person>)orm.loadListSingle(dbConn, T9Person.class, map);
      request.setAttribute("personList", personList);
      //dbConn.close();
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "submit failed");
      throw ex;
    }
    return "/core/funcs/dept/user.jsp?TO_ID=" + TO_ID + "&TO_NAME=" + TO_NAME + "&deptLocal=" + deptLocal;
  }
  
  /**
   * 判段id是不是在str里面
   * @param str
   * @param id
   * @return
   */
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
  
  public String selectDeptToAttendance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9DeptLogic deptLogic = new T9DeptLogic();
      String postpriv = user.getPostPriv();// 管理范围
      String postDept = user.getPostDept();// 管理范围指定部门
      String deptId = String.valueOf(user.getDeptId());
      int deptIdTmp = user.getDeptId();
      String postDeptTmp = user.getPostDept();
      String deptParent = request.getParameter("deptParent");
      String parentId = request.getParameter("parentId");
      int postDeptId = 0;
      String postDeptStr = "";
      int userDeptId = user.getDeptId();
      int userDeptIdFunc = Integer.parseInt(request.getParameter("userDeptId"));
      String userDeptIdStr = request.getParameter("userDeptId");

      String data = "";
      if (T9Utility.isNullorEmpty(postpriv)) {
        String[] postDeptArray = { String.valueOf(userDeptId) };
        data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";
      } else {
        if (postpriv.equals("0")) {
          if (findId(deptId, userDeptIdStr)) {
            data = "[{text:'" + deptParent + "'" + ", value: '" + parentId + "'}]";
          } else {
            String deptLog = deptLogic.getChildDeptId(dbConn, deptIdTmp);
            if (deptLog.length() > 0) {
              deptId += "," + deptLog.substring(0, deptLog.length() - 1);
            }
            String[] deptArray = deptId.split(",");
            data = "["
                + deptLogic.getDeptTreeJsonSelf(0, dbConn, deptArray,
                    userDeptIdFunc) + "]";
            // String[] postDeptArray1 = {String.valueOf(userDeptId)};
            // data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray1)
            // + "]";
          }
        }
        if (postpriv.equals("1")) {
          data = deptLogic.getDeptTreeJson1(0, dbConn, userDeptIdFunc);
        }
        if (postpriv.equals("2")) {
          if (postDept == null || postDept.equals("")) {
            data = "[]";
          } else {
            String[] postDeptFunc = postDept.split(",");
            for (int i = 0; i < postDeptFunc.length; i++) {
              postDeptId = Integer.parseInt(postDeptFunc[i]);
              postDeptStr += deptLogic.getChildDeptId(dbConn, postDeptId);
            }
            postDept += "," + postDeptStr;
            String[] postDeptArray = postDept.split(",");
            if (findId(postDeptTmp, userDeptIdStr)) {
              data = "[{text:'" + deptParent + "'" + ", value: '" + parentId + "'}]";
            } else {
              // getDeptTreeJson
              data = "["
                  + deptLogic.getDeptTreeJsonSelf(0, dbConn, postDeptArray,
                      userDeptIdFunc) + "]";
            }
          }
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId)
          + "," + postpriv);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
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
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    OutputStream ops = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("OA部门.csv", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9DeptLogic ieml = new T9DeptLogic();
      ArrayList<T9DbRecord > dbL = ieml.toExportDeptData(conn);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
    }
    return null;
  }
  
  public String importDept(HttpServletRequest request,HttpServletResponse response) throws Exception{
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
      ArrayList<T9DbRecord> drl = T9CSVUtil.CVSReader(is, T9Const.CSV_FILE_CODE);
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      StringBuffer sb = new StringBuffer("[");
      T9DeptLogic dl = new T9DeptLogic();
      String deptName = "";
      String deptParent = "";
      String deptNo = "";
      String infoStr= "";
      String telNo = "";
      String faxNo = "";
      String deptFunc = "";
      String color = "red";
      int deptParentNo = 0;
      
      String remark = "成功导入部门：";
      boolean hasSucess = false;
      T9DeptLogic logic = new T9DeptLogic();
      for(int i = 0; i < drl.size(); i++){
        deptName = (String) drl.get(i).getValueByName("部门名称");
        if(T9Utility.isNullorEmpty(deptName)){
          continue;
        }
        deptName = getOutOf(deptName);
        deptParent = getOutOf((String) drl.get(i).getValueByName("上级部门"));
        deptNo = getOutOf((String) drl.get(i).getValueByName("部门排序号"));
        telNo = getOutOf((String) drl.get(i).getValueByName("部门电话"));
        faxNo = getOutOf((String) drl.get(i).getValueByName("部门传真"));
        deptFunc = getOutOf((String) drl.get(i).getValueByName("部门职能"));
        
        infoStr = "导入失败,部门 " + deptName + " 已经存在";
        if(dl.existsDeptName(conn, deptName)){
          color = "red";
          infoStr = "导入失败,部门 " + deptName + " 已经存在";
          sb.append("{");
          sb.append("deptName:\"" + (deptName == null ? "" : deptName)+ "\"");
          sb.append(",deptNo:\"" + (deptNo == null ? "" : deptNo) + "\"");
          sb.append(",deptParent:\"" + (deptParent == null ? "" : deptParent) + "\"");
          sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
          sb.append(",color:\"" + (color == null ? "" : color) + "\"");
          sb.append("},");
        }else{
          isCount++;
          infoStr = "成功";
          color = "black";
          if (!T9Utility.isInteger(telNo)) {
            telNo = "";
          }
          if (!T9Utility.isInteger(faxNo)) {
            faxNo = "";
          }
          if (!T9Utility.isNumber(deptNo)) {
            deptNo = "0";
          }
          sb.append("{");
          sb.append("deptName:\"" + (deptName == null ? "" : deptName) + "\"");
          sb.append(",deptNo:\"" + (deptNo == null ? "" : deptNo) + "\"");
          sb.append(",deptParent:\"" + (deptParent == null ? "" : deptParent) + "\"");
          sb.append(",info:\"" + (infoStr == null ? "" : infoStr) + "\"");
          sb.append(",color:\"" + (color == null ? "" : color) + "\"");
          sb.append("},");
          if(T9Utility.isNullorEmpty(deptParent)){
            deptParentNo = 0;
          }else{
            deptParentNo = dl.getDeptIdLogic(conn, deptParent);
          }
          logic.saveDept(conn, deptName, deptParentNo, deptNo, telNo, faxNo, deptFunc);
          //T9ORM orm = new T9ORM();
          //orm.saveSingle(conn , "department" , map);
          remark += deptName + ",";
          hasSucess = true;
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
      
      //生成org.xml文件
      T9IsPiritOrgAct.getOrgDataStream(conn);
      if (T9ReportSyncLogic.hasSync) {
        Connection reportConn = T9ReportSyncLogic.getReportConn();
        T9DepartmentSyncLogic logic2 = new T9DepartmentSyncLogic();
        logic2.syncDepartment(conn, reportConn);
        if (reportConn != null) {
          reportConn.close();
        }
      }
      if (T9OaSyncLogic.hasSync) {
        Connection oaConn = T9OaSyncLogic.getOAConn();
        t9.core.module.oa.logic.T9DepartmentSyncLogic logic2 = new t9.core.module.oa.logic.T9DepartmentSyncLogic();
        logic2.syncDepartment(conn, oaConn);
        if (oaConn != null) {
          oaConn.close();
        }
      }
      T9DepartmentCache.removeAll();
      
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    } 
    return "/core/funcs/dept/importDept.jsp?data="+data+"&isCount="+isCount;
  }
  
  public String getDeptSelectFunc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int deptId = Integer.parseInt(request.getParameter("deptId"));
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();// 角色
      String postpriv = user.getPostPriv();// 管理范围
      String postDept = user.getPostDept();// 管理范围指定部门
      int userDeptId = user.getDeptId();
      T9DeptLogic deptLogic = new T9DeptLogic();
      boolean isAdminRole = user.isAdminRole();
      boolean isAdmin = user.isAdmin();
      String userDeptName = deptLogic.getNameByIdStr(
          String.valueOf(userDeptId), dbConn);
      String data = "";
      if (T9Utility.isNullorEmpty(postpriv)) {
        if (isAdminRole && !isAdmin) {
          if (userDeptId != deptId) {
            String deptName = deptLogic.getDeptName(dbConn, deptId);
            data = "[{text:'" + deptName + "',value:" + deptId + "}]";
          } else {
            String[] postDeptArray = { String.valueOf(userDeptId) };
            data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                + "]";
          }

        } else {
          String[] postDeptArray = { String.valueOf(userDeptId) };
          data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
              + "]";
        }
      } else {
        if (postpriv.equals("0")) {
          // data = "[{text:\"" + userDeptName + "\",value:" + userDeptId +
          // "}]";
          if (isAdminRole && !isAdmin) {
            if (userDeptId != deptId) {
              String deptName = deptLogic.getDeptName(dbConn, deptId);
              data = "[{text:'" + deptName + "',value:" + deptId + "}]";
            } else {
              String[] postDeptArray = { String.valueOf(userDeptId) };
              data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                  + "]";
            }

          } else {
            String[] postDeptArray = { String.valueOf(userDeptId) };
            data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                + "]";
          }
        }
        if (postpriv.equals("1")) {
          data = deptLogic.getDeptTreeJson(0, dbConn);
        }
        if (postpriv.equals("2")) {
          if (isAdminRole && !isAdmin) {
            if (!findId(postDept, String.valueOf(deptId))) {
              String deptName = deptLogic.getDeptName(dbConn, deptId);
              data = "[{text:'" + deptName + "',value:" + deptId + "}]";
            } else {
              String[] postDeptArray = postDept.split(",");
              data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                  + "]";

            }
          } else {
            if (postDept == null || postDept.equals("")) {
              data = "[]";
            } else {
              String[] postDeptArray = postDept.split(",");
              data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                  + "]";

            }
          }
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId)
          + "," + postpriv);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据用户的管理权限得到所有部门

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      // int deptId = Integer.parseInt(request.getParameter("deptId"));
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();// 角色
      String postpriv = user.getPostPriv();// 管理范围
      String postDept = user.getPostDept();// 管理范围指定部门
      int userDeptId = user.getDeptId();
      T9DeptLogic deptLogic = new T9DeptLogic();
      boolean isAdminRole = user.isAdminRole();
      boolean isAdmin = user.isAdmin();
      String userDeptName = deptLogic.getNameByIdStr(
          String.valueOf(userDeptId), dbConn);
      String data = "";
      if (T9Utility.isNullorEmpty(postpriv)) {
        String[] postDeptArray = { String.valueOf(userDeptId) };
        data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";
      } else {
        if (postpriv.equals("0")) {
          // data = "[{text:\"" + userDeptName + "\",value:" + userDeptId +
          // "}]";
          String[] postDeptArray = { String.valueOf(userDeptId) };
          data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
              + "]";
        }
        if (postpriv.equals("1")) {
          data = deptLogic.getDeptTreeJson(0, dbConn);
        }
        if (postpriv.equals("2")) {
          if (postDept == null || postDept.equals("")) {
            data = "[]";
          } else {
            String[] postDeptArray = postDept.split(",");
            data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                + "]";

          }
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId)
          + "," + postpriv);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
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
}
