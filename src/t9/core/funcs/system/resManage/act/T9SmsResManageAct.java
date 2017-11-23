package t9.core.funcs.system.resManage.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.resManage.logic.T9EmailResManageLogic;
import t9.core.funcs.system.resManage.logic.T9SmsResManageLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SmsResManageAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.system.resManage.act.T9SmsResManageAct");
  
  public String searchSms(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String toId = request.getParameter("toId");
      String copyToId = request.getParameter("copyToId");
      String content = request.getParameter("content");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      
      T9SmsResManageLogic logic = new T9SmsResManageLogic();
      String data = logic.searchSms(toId, copyToId, beginDate, endDate, content, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String deleteSms(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String idStr = request.getParameter("smsId");
      T9SmsResManageLogic logic = new T9SmsResManageLogic();
      logic.deleteSms(dbConn , idStr);
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
