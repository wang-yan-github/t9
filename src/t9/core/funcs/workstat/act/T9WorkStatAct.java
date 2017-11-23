package t9.core.funcs.workstat.act;

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

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.funcs.workstat.logic.T9WorkStatLogic;

public class T9WorkStatAct {
  public T9WorkStatLogic logic = new T9WorkStatLogic();

  /**
   * 所属部门下拉框
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToAttendance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      String data = "";
      data = this.logic.getDeptTreeJson(0, dbConn, user);
      if (T9Utility.isNullorEmpty(data)) {
        data = "[]";
      }
      // System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getUserDeptPrivAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      String data = ""; // this.logic.getUserDeptPrivLogic(dbConn,userId);

      data = "{deptId:'" + user.getDeptId() + "'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getIsStaticAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      int userId = user.getSeqId();

      String data = this.logic.getIsStaticLogic(dbConn, userId);

      data = "{deptPriv:'" + data + "'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取数据
   * 
   * @param request
   * @param response
   * */
  public String getDataAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("deptId");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");
      String deptMore = request.getParameter("deptMore");
      String minNum=request.getParameter("minNum");
      String maxNum=request.getParameter("maxNum");

      String data = this.logic.getDataLogic(dbConn, user, deptId, startDate,
          endDate, deptMore,minNum,maxNum);

      data = "{startDate:'" + startDate + "',endDate:'" + endDate
          + "',userData:[" + data + "]}";
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 获取数据
   * 
   * @param request
   * @param response
   * */
  public String getDataToExeclAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("deptId");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");
      String deptMore = request.getParameter("deptMore");

      List<Map<String, String>> dataList = this.logic.getDataToExeclLogic(
          dbConn, user, deptId, startDate, endDate, deptMore);

      String name = "工作统计报表（" + startDate + " 至   " + endDate + "）.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }

  /**
   * 日程安排 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCalFinishAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map<String, String> map = new HashMap<String, String>();

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      map.put("userId", request.getParameter("userId"));
      map.put("startDate", request.getParameter("startDate"));
      map.put("endDate", request.getParameter("endDate"));
      map.put("status", request.getParameter("status"));
      String data = this.logic.getCalFinishLogic(dbConn, map);
      data = "{calendar:[" + data + "]}";
           
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());

      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询日志
   * */
  public String searchDiarySelf(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userId");
      int userId = Integer.parseInt(userIdStr);
      // T9Person person =
      // (T9Person)request.getSession().getAttribute("LOGIN_USER");
      // int userId = person.getSeqId();
      // T9DiaryLogic dl = new T9DiaryLogic();
      String data = this.logic.toSearchData(dbConn, request.getParameterMap(),
          userId);
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
   * 工作流主办
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getWorkFlowAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map<String, String> map = new HashMap<String, String>();
    try {
      map.put("userId", request.getParameter("userId"));
      map.put("startDate", request.getParameter("startDate"));
      map.put("endDate", request.getParameter("endDate"));
      map.put("flag", request.getParameter("flag"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String data = this.logic.getWorkFlowLogic(dbConn, request
          .getParameterMap(), map);
      PrintWriter pw = response.getWriter();
      // System.out.println(data);
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
   * 工作流名称
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowNameAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {

      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowId = request.getParameter("flowId");
      String data = this.logic.getFlowNameLogic(dbConn, flowId);
      data = "{flowName:'" + data + "'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 工作流会签
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowSignAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map<String, String> map = new HashMap<String, String>();
    try {
      map.put("userId", request.getParameter("userId"));
      map.put("startDate", request.getParameter("startDate"));
      map.put("endDate", request.getParameter("endDate"));
      map.put("flag", request.getParameter("flag"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String data = this.logic.getFlowSignLogic(dbConn, request
          .getParameterMap(), map);
      PrintWriter pw = response.getWriter();
      // System.out.println(data);
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

}
