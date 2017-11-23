package t9.core.funcs.doc.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.logic.T9ConfigLogic;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9ConfigAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9ConfigAct");
  public String getParam(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ConfigLogic logic = new T9ConfigLogic();
      String sealForm = logic.getSysPar("SEAL_FROM", dbConn);
      if (sealForm == null  || "".equals(sealForm)) {
        sealForm = "1";
      }
      String flowRemindAfter = logic.getSysPar("FLOW_REMIND_AFTER", dbConn);
      String unitAfter = "d";
      if (flowRemindAfter != null  && !"".equals(flowRemindAfter)) {
        unitAfter =  flowRemindAfter.substring(flowRemindAfter.length() - 1);
        if (flowRemindAfter.length() == 1) {
          flowRemindAfter = "";
        } else {
          flowRemindAfter =  flowRemindAfter.substring(0 , flowRemindAfter.length() - 1);
        }
      } else {
        flowRemindAfter = "";
      }
      String flowRemindBefore = logic.getSysPar("FLOW_REMIND_BEFORE", dbConn);
      String unitBefore = "m";
      if (flowRemindBefore != null && !"".equals(flowRemindBefore)) {
        unitBefore =  flowRemindBefore.substring(flowRemindBefore.length() - 1);
        if (flowRemindBefore.length() == 1) {
          flowRemindBefore = "";
        } else {
          flowRemindBefore =  flowRemindBefore.substring(0 , flowRemindBefore.length() - 1);
        }
      } else {
        flowRemindBefore = "";
      }
      String mobile = logic.getSysPar("FLOW_MOBILE_REMIND", dbConn);
      request.setAttribute("flowMobileRemind", mobile);
      request.setAttribute("flowRemindBefore", flowRemindBefore);
      request.setAttribute("unitBefore", unitBefore);
      request.setAttribute("flowRemindAfter", flowRemindAfter);
      request.setAttribute("unitAfter", unitAfter);
      request.setAttribute("sealForm", sealForm);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return T9WorkFlowConst.MODULE_CONTEXT_PATH + "/config/index.jsp";
  }
  public String saveConfig(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sealForm = request.getParameter("sealForm");
      String flowMobileRemind = request.getParameter("flowMobileRemind");
      if (flowMobileRemind == null) {
        flowMobileRemind = "";
      }
      if ("on".equals(flowMobileRemind)) {
        flowMobileRemind = "1";
      }
      String flowRemindBefore = request.getParameter("flowRemindBefore");
      if (flowRemindBefore == null) {
        flowRemindBefore = "";
      }
      String unitBefore = request.getParameter("unitBefore");
      flowRemindBefore = flowRemindBefore + unitBefore;
      String flowRemindAfter = request.getParameter("flowRemindAfter");
      if (flowRemindAfter == null) {
        flowRemindAfter = "";
      }
      String unitAfter = request.getParameter("unitAfter");
      flowRemindAfter = flowRemindAfter + unitAfter;
      T9ConfigLogic logic = new T9ConfigLogic();
      logic.updateSysPar("SEAL_FROM", sealForm, dbConn);
      logic.updateSysPar("FLOW_MOBILE_REMIND", flowMobileRemind, dbConn);
      logic.updateSysPar("FLOW_REMIND_AFTER", flowRemindAfter, dbConn);
      logic.updateSysPar("FLOW_REMIND_BEFORE", flowRemindBefore, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
