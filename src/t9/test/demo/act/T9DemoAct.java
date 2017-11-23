package t9.test.demo.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.column.logic.T9ColumnLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.test.demo.data.T9HrStaffCare;
import t9.test.demo.logic.T9DemoLogic;

public class T9DemoAct {
  public static final String attachmentFolder = "hr";

  /**
   *员工关怀 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getStaffCareListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9DemoLogic logic = new T9DemoLogic();
      String data = logic.getStaffCareJsonLogic(dbConn, request.getParameterMap(), person);
      
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9DemoLogic logic = new T9DemoLogic();
      String data = logic.getUserName(dbConn, seqId);
      data = "{userName:\""+data+"\"}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新建员工关怀
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addStaffCareInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    String contexPath = request.getContextPath();
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DemoLogic logic = new T9DemoLogic();
      logic.setNewStaffCareValueLogic(dbConn, fileForm, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    response.sendRedirect(contexPath + "/test/demo/newRemind.jsp");
    return null;
  }
  
  /**
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCareDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DemoLogic logic = new T9DemoLogic();
      T9HrStaffCare care = (T9HrStaffCare) logic.getCareDetailLogic(dbConn, Integer.parseInt(seqId));
      if (care == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未找到相应记录");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(care);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑员工关怀
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateCareInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    String contexPath = request.getContextPath();
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DemoLogic logic = new T9DemoLogic();
      logic.updateCareInfoLogic(dbConn,fileForm,person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    response.sendRedirect(contexPath + "/test/demo/manage.jsp");
    return null;
  }
  
  /**
   * 删除文件--wyw
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String filePath = T9SysProps.getAttachPath() + File.separator + this.attachmentFolder + File.separator;
      T9DemoLogic logic = new T9DemoLogic();
      logic.deleteFileLogic(dbConn, seqIdStr, filePath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 员工关怀查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryCareListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("careType", T9DBUtility.escapeLike(request.getParameter("careType")));
      map.put("byCareStaffs", T9DBUtility.escapeLike(request.getParameter("byCareStaffs")));
      map.put("careDate1", T9DBUtility.escapeLike(request.getParameter("careDate1")));
      map.put("careDate2", T9DBUtility.escapeLike(request.getParameter("careDate2")));
      map.put("careFees1", T9DBUtility.escapeLike(request.getParameter("careFees1")));
      map.put("careFees2", T9DBUtility.escapeLike(request.getParameter("careFees2")));
      map.put("participants", T9DBUtility.escapeLike(request.getParameter("participants")));
      map.put("careContent", T9DBUtility.escapeLike(request.getParameter("careContent")));
      String data = "";
      T9DemoLogic logic = new T9DemoLogic();
      data = logic.queryCareListJsonLogic(dbConn, request.getParameterMap(), map, person);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 获取树型结构
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String id = request.getParameter("id");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9DemoLogic logic = new T9DemoLogic();
      String data = "";
      data = logic.getTree(dbConn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * T9FOM orm 联合使用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addFO(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9HrStaffCare care = (T9HrStaffCare)T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, care);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
