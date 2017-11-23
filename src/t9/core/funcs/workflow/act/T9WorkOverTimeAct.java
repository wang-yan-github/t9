package t9.core.funcs.workflow.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9FlowOverTimeLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9WorkOverTimeAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9WorkQueryAct");

  public String getWorkOverTimeList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowList");
    if ("".equals(flowId) || flowId == null || flowId.equals("undefined") || flowId =="undefined") {
      flowId = "0";
    }
    String flowStatus = request.getParameter("flowStatus");
    String starttime = request.getParameter("statrTime");
    String endtime = request.getParameter("endTime");
    String sortId = request.getParameter("sortId");
    if (sortId == null) {
      sortId = "";
    }
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      T9FlowOverTimeLogic myWorkLogic = new T9FlowOverTimeLogic();
      String result = myWorkLogic.getWorkOverTimeList(Integer.parseInt(flowId),flowStatus, starttime, endtime, dbConn,  request.getParameterMap(),loginUser , sortId);
      PrintWriter pw = response.getWriter();
      pw.println(result);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 工作超时统计重写的方法
   */
  public String getOverTimeTotal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    int flag = 0;
    String sFlag = request.getParameter("flag");
    if (T9Utility.isInteger(sFlag)) {
      flag = Integer.parseInt(sFlag);
    }
    String flowquery = request.getParameter("flowquery");
    String bumenquery = request.getParameter("STATCS_MANNER_QUERY"); 
    String starttime = request.getParameter("statrTime");
    String endtime = request.getParameter("endTime");
    String user = request.getParameter("user"); 
    String dept = request.getParameter("dept");
    String role = request.getParameter("role");
    T9Person loginUser = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      StringBuffer sb = new StringBuffer();
      T9FlowOverTimeLogic myWorkLogic = new T9FlowOverTimeLogic();
      Map map = myWorkLogic.getOverTimeTotal(flowquery, bumenquery, starttime, endtime, user, dept, role, conn, loginUser);
      if (flag != 2) {
        request.setAttribute("flowData", map);
      } else {
        response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
        InputStream is = null;
        try{
          String fileName = URLEncoder.encode("超时统计结果.csv","UTF-8");
          fileName = fileName.replaceAll("\\+", "%20");
          response.setHeader("Cache-control","private");
          response.setContentType("application/vnd.ms-excel");
          response.setHeader("Cache-Control","maxage=3600");
          response.setHeader("Pragma","public");
          response.setHeader("Accept-Ranges","bytes");
          response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
          ArrayList<T9DbRecord > dbL = myWorkLogic.covertToExportCsvData(map);
          T9CSVUtil.CVSWrite(response.getWriter(), dbL);
        } catch (Exception ex) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
          throw ex;
        }
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    //返回到柱状图页面
    if (flag == 1) {
      return "/core/funcs/workflow/flowrun/overtime/columnChart.jsp";
    } else if (flag == 2) {
      return null;
    } else {
      return "/core/funcs/workflow/flowrun/overtime/overouttime.jsp";
    }
  }
  public String viewDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sFlowId = request.getParameter("flowId");
      int flowId= 0 ;
      if (sFlowId != null && !"".equals(sFlowId)) {
        flowId = Integer.parseInt(sFlowId);
      }
      String sUserId = request.getParameter("userId");
      int userId = 0 ;
      if (sUserId != null && !"".equals(sUserId)) {
        userId = Integer.parseInt(sUserId);
      }
      String prcsDate1Query = request.getParameter("prcsDate1Query");
      String prcsDate2Query = request.getParameter("prcsDate2Query");
      T9FlowOverTimeLogic logic = new T9FlowOverTimeLogic();
      String data = logic.viewDetail(dbConn, request.getParameterMap(), flowId, userId, prcsDate1Query, prcsDate2Query);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String exportCsv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowList");
    if ("".equals(flowId) || flowId == null || flowId.equals("undefined") || flowId =="undefined") {
      flowId = "0";
    }
    String flowStatus = request.getParameter("flowStatus");
    String starttime = request.getParameter("statrTime");
    String endtime = request.getParameter("endTime");
    String sortId = request.getParameter("sortId");
    if (sortId == null) {
      sortId = "";
    }
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    T9Person loginUser = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String fileName = URLEncoder.encode("超时工作记录.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9FlowOverTimeLogic myWorkLogic = new T9FlowOverTimeLogic();
      ArrayList<T9DbRecord > dbL = myWorkLogic.getOverTimeList(Integer.parseInt(flowId),flowStatus, starttime, endtime, conn,  request.getParameterMap(),loginUser , sortId);
      T9CSVUtil.CVSWrite(response.getWriter(), dbL);
    } catch (Exception ex) {
      throw ex;
    }
    return null;
  }
}
