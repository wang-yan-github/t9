package t9.core.funcs.workflow.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9MyDocLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9MyDocAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9MyDocAct");
  public String getMyWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyDocLogic myWorkLogic = new T9MyDocLogic();
      str = myWorkLogic.getMyWork(dbConn, user, 10  , sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyDocLogic myWorkLogic = new T9MyDocLogic();
      str = myWorkLogic.getSign(dbConn, user, 10 , sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getFocusWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyDocLogic myWorkLogic = new T9MyDocLogic();
      str = myWorkLogic.getFocusWork(dbConn, user, 10, sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
