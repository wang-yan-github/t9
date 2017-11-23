package t9.core.funcs.doc.flowrunRec.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.flowrunRec.logic.T9FlowManageLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9FlowManageAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.doc.flowrunRec.act.T9FlowManageAct");


  public String focus(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String sRunId = request.getParameter("runId");
    int runId = Integer.parseInt(sRunId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person u = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      String focusUser = manage.getFocusUser(runId, dbConn);
      if (T9WorkFlowUtility.findId(focusUser, String.valueOf(u.getSeqId()))) {
        this.setRequestSuccess(request, "您已经关注了此工作！");
        return "/core/inc/rtjson.jsp";
      } else {
        manage.focus(u, focusUser, runId, request.getContextPath(), dbConn);
      }
      this.setRequestSuccess(request, "操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }


  /**
   * 设置错误信息
   * 
   * @param request
   * @param message
   */
  public void setRequestError(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   */
  public void setRequestSuccess(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   * @param data
   */
  public void setRequestSuccess(HttpServletRequest request, String message,
      String data) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }

}