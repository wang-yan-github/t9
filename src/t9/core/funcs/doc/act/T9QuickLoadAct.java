package t9.core.funcs.doc.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.logic.T9DelegateLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9QuickLoadLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9QuickLoadAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9QuickLoadAct");
  public String getQuickLoad(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      int itemId = Integer.parseInt(request.getParameter("itemId"));
      String selectedItem = request.getParameter("selectedItem");
      T9QuickLoadLogic logic = new T9QuickLoadLogic();
      String str = logic.getQuickLoad(dbConn, flowId, runId, itemId, user, selectedItem);
      str = str.replaceAll("\r", "&#13;");
      str = str.replaceAll("\n", "&#10;");
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "ok");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
