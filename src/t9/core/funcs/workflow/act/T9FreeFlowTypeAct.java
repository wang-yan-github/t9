package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FlowUserSelectLogic;
import t9.core.funcs.workflow.logic.T9FreeFlowLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9FreeFlowTypeAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.act.T9FreeFlowTypeAct");
  public String getNewPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int flowId = Integer.parseInt(sFlowId);
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      String data = flowTypeLogic.getNewPriv(flowId, dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateNewPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String userId = request.getParameter("user");
    String dept = request.getParameter("dept");
    String role = request.getParameter("role");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      flowTypeLogic.updateNewPriv(flowId, userId, dept, role, dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得自由流程流转页面相关数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTurnData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String sIsManage = request.getParameter("isManage");
    if (sIsManage == null ) {
      sIsManage = "false";
    }
    boolean isManage = Boolean.valueOf(sIsManage);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser, dbConn);
      if("".equals(roleStr) && !isManage){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程办理权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FreeFlowLogic flowRunLogic = new T9FreeFlowLogic();
        //取转交相关数据
        String msg = flowRunLogic.getTurnData(loginUser , flowId , runId , prcsId  ,dbConn , isManage);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
        request.setAttribute(T9ActionKeys.RET_DATA, msg);
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String turnNext(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String itemOld = request.getParameter("freeItemOld");
    String remindContent = request.getParameter("smsContent");
    String sPreSet = request.getParameter("preSet");
    String sMaxPrcs = request.getParameter("maxPrcs");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String sms2Remind = request.getParameter("sms2Remind") ;
      String smsRemind = request.getParameter("smsRemind") ;
      
      boolean preSet = false ;
      if (sPreSet != null) {
        preSet= true;
      }
      int maxPrcs = prcsId + 1;
      if (sMaxPrcs != null) {
        maxPrcs = Integer.parseInt(sMaxPrcs);
      }
      List<Map> preList = new ArrayList();
      for (int i = prcsId + 1 ; i <= maxPrcs ; i ++){
        Map map = new HashMap();
        map.put("prcsId", i);
        String tmp = "";
        if (i != prcsId + 1) {
          tmp = String.valueOf(i);
        } 
        map.put("prcsUser", request.getParameter("prcsUser" + tmp));
        map.put("prcsOpUser", request.getParameter("prcsOpUser" + tmp));
        map.put("freeItem", request.getParameter("freeItem" + tmp));
        map.put("topFlag", request.getParameter("topFlag" + tmp));
        preList.add(map);
      }
      //T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      //String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      T9FreeFlowLogic freeFlowLogic = new T9FreeFlowLogic();
      String remindUser = freeFlowLogic.turnNext(loginUser, flowId, runId, prcsId, dbConn, preSet, preList, itemOld);
      if ("on".equals(sms2Remind)) {
        T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
        ms2l.remindByMobileSms(dbConn, remindUser, loginUser.getSeqId(), remindContent, null);
      }
      if ("on".equals(smsRemind)) {
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        String sortId = request.getParameter("sortId");
        if (sortId == null) {
          sortId = "";
        }
        String skin = request.getParameter("skin");
        if (skin == null) {
          skin = "";
        }
        flowRunLogic.remindNext(dbConn, runId ,  flowId , prcsId + 1 ,  0 , remindContent, request.getContextPath(), remindUser, loginUser.getSeqId() ,sortId , skin);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功转交!");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String stop(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      T9FreeFlowLogic freeFlowLogic = new T9FreeFlowLogic();
      String msg = freeFlowLogic.stop(runId ,flowId , prcsId, loginUser , dbConn);
      if (msg == null) {
        msg = "操作成功！";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msg);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
