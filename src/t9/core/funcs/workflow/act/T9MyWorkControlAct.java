package t9.core.funcs.workflow.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9FlowWorkControlLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9MyWorkControlAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.act.T9MyWorkAct");
  // 工作监控 ACT
  public String getMyManagerWork1(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String sSortId =  request.getParameter("sortId");
      T9FlowWorkControlLogic myWorkLogic = new T9FlowWorkControlLogic();
      StringBuffer result = myWorkLogic.getFlowRunManager(dbConn, request.getParameterMap(), loginUser ,  sSortId);
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
//工作监控 ACT
  public String getMyManagerWork(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String sSortId =  request.getParameter("sortId");
      T9FlowWorkControlLogic myWorkLogic = new T9FlowWorkControlLogic();
      StringBuffer result = myWorkLogic.getFlowRunManager1(dbConn, request.getParameterMap(), loginUser , sSortId);
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  
}
