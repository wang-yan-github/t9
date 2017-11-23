package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunAssistLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9WorkHandleAct {
  private static Logger log = Logger
  .getLogger("t9.core.funcs.workflow.act.T9WorkHandleAct");
  /**
   * 取得工作办理界面的一相关数据,主要有表单，附件，会签意见
   * @param request
   * @param response
   * @return json对象 rtData的格式为 {formMsg:'',attach:[],feedBack:[]}
   * @throws Exception
   */
  public String getHandlerData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String isWriteLog =request.getParameter("isWriteLog");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //验证用户是否登陆
      if(loginUser == null){
        String message = T9WorkFlowUtility.Message("用户未登录，请<a href='" + request.getContextPath() +"'>重新登录!</a>",2);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        if (T9Utility.isNullorEmpty(runIdStr)) {
          String tmp = "此工作未新建成功，请重新办理！";
          this.setRequestError(request, tmp);
          return "/core/inc/rtjson.jsp";
        }
        int runId = Integer.parseInt(runIdStr);
        int prcsId = Integer.parseInt(prcsIdStr);
        int flowId = Integer.parseInt(flowIdStr);
        
        int flowPrcs = 0;
        if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)){
          flowPrcs = Integer.parseInt(flowPrcsStr);
        }
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        if (!flowRunLogic.canHandlerWrok(runId, prcsId, flowPrcs, loginUser.getSeqId(), dbConn)) {
          String tmp = "此工作已经收回或转交至下一步或结束，您不能办理！";
          this.setRequestError(request, tmp);
          return "/core/inc/rtjson.jsp";
        }
        if (flowRunLogic.hasDelete(runId, dbConn)) {
          String tmp = "此工作已经删除，您不能办理！";
          this.setRequestError(request, tmp);
        } else {
        //取表单相关信息          String imgPath = T9WorkFlowUtility.getImgPath(request);
          String msg = flowRunLogic.getHandlerMsg(loginUser , runId , prcsId , flowPrcsStr   , request.getRemoteAddr() , dbConn , imgPath ,isWriteLog);
          this.setRequestSuccess(request, "get Success", msg);
        }
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public boolean getWebsign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sql = "select 1 FROM FLOW_TYPE , FLOW_FORM_ITEM WHERE FLOW_TYPE.SEQ_ID=" + flowIdStr + " and FLOW_TYPE.FORM_SEQ_ID = FLOW_FORM_ITEM.FORM_ID AND CLAZZ='SIGN'";
      Statement stm1 = null;
      ResultSet rs1 = null;
      try {
        stm1 = dbConn.createStatement();
        rs1 = stm1.executeQuery(sql);
        if (rs1.next()) {
          return true;
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm1, rs1, null); 
      }
      return false;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  public boolean getAip(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String flowPrcsStr =  request.getParameter("flowPrcs");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypeLogic =  new T9FlowTypeLogic();
      T9FlowType flowType = flowTypeLogic.getFlowTypeById(Integer.parseInt(flowIdStr), dbConn);
      String dispAip = "";
      if ("1".equals(flowType.getFlowType())) {
        T9FlowProcessLogic flowPrcsLogic = new  T9FlowProcessLogic();
        //查出相关步骤
        T9FlowProcess flowProcess = flowPrcsLogic.getFlowProcessById(Integer.parseInt(flowIdStr), flowPrcsStr , dbConn);
        dispAip = flowProcess.getDispAip() + "";
      }
      if (dispAip == null || "0".equals(dispAip) || "".equals(dispAip)) {
        return false;
      }
      return true;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  /**
         * 　取得草稿箱的内容
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOutline(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    int flowId = Integer.parseInt(flowIdStr);
    int runId = Integer.parseInt(runIdStr);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowRunAssistLogic logic = new T9FlowRunAssistLogic();
      String array = logic.getOutline(flowId , runId , loginUser.getSeqId(), dbConn);
      this.setRequestSuccess(request, "get Success", array);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存表单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveFormData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String hiddenStr = request.getParameter("hiddenStr");
    String readOnlyStr = request.getParameter("readOnlyStr");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      if (hiddenStr == null ) {
        hiddenStr = "";
      }
      if (readOnlyStr == null) {
        readOnlyStr = "";
      }
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if("".equals(roleStr)){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        //取表单相关信息
        String msg = flowRunLogic.saveFormData(loginUser, flowId, runId, prcsId, flowPrcs, request , hiddenStr , readOnlyStr,dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功!");
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 取消工作 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String cancelRun(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if(!T9WorkFlowUtility.findId(roleStr, "2")){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        //取表单相关信息
        flowRunLogic.cancelRun( flowId, runId, prcsId, flowPrcs , dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 取出以前的所有步骤
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPreRunPrcs(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
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
      //验证是否有权限
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if(!T9WorkFlowUtility.findId(roleStr, "2")){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        //取表单相关信息
        String str = flowRunLogic.getPreRunPrcs(runId , prcsId , flowId , dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功!");
        request.setAttribute(T9ActionKeys.RET_DATA, "[" + str + "]");
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 设置错误信息
   * @param request
   * @param message
   */
  public  void setRequestError(HttpServletRequest request , String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }
  /**
   * 设置成功信息
   * @param request
   * @param message
   */
  public  void setRequestSuccess(HttpServletRequest request , String message) {
    request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }
  /**
   * 设置成功信息 
   * @param request  
   * @param message 
   * @param data
   */
  public  void setRequestSuccess(HttpServletRequest request , String message , String data) {
    request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }
}
