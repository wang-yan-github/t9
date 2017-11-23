package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.logic.T9ConfigLogic;
import t9.core.funcs.workflow.logic.T9FlowTimerLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9FlowTimerAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9FlowTimerAct");
  public String saveTimer(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String flowId = request.getParameter("flowId");
      String type = request.getParameter("TYPE");
      String privUser = request.getParameter("privUser");
      
      
      String dateVar = "REMIND_DATE" + type;
      String timeVar = "REMIND_TIME" + type;
      
      String remindDateS = request.getParameter(dateVar);
      String remindTimeS = T9Utility.null2Empty(request.getParameter(timeVar));
      
      
      if ("1".equals(type)) {
        remindDateS = request.getParameter("REMIND_TIME1");
        if (!T9Utility.isNullorEmpty(remindDateS)) {
          remindTimeS = remindDateS.split(" ")[1];
          remindDateS = remindDateS.split(" ")[0];
        }
      }
      
      if ("5".equals(type)) {
        String mon = T9Utility.null2Empty(request.getParameter("REMIND_DATE5_MON")) ;
        String day =  T9Utility.null2Empty(request.getParameter("REMIND_DATE5_DAY")) ;
        remindDateS = mon + "-" + day;
      }
      
      
      T9FlowTimerLogic logic = new T9FlowTimerLogic();
      logic.saveTimer(dbConn, seqId, flowId, type, privUser, remindDateS, remindTimeS);
      
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getTimers(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowId = request.getParameter("flowId");

      T9FlowTimerLogic logic = new T9FlowTimerLogic();
      String str = logic.getTimers(dbConn, flowId);
      
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getTimer(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");

      T9FlowTimerLogic logic = new T9FlowTimerLogic();
      String str = logic.getTimer(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delTimer(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9FlowTimerLogic logic = new T9FlowTimerLogic();
      logic.delTimer(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
