package t9.core.funcs.workflow.act;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9WorkTurnAct {
  private static Logger log = Logger
  .getLogger("t9.core.funcs.workflow.act.T9WorkTurnAct");
  public static String PLUGINPACKAGE = "t9.plugins.workflow";
  public String getTurnData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String sIsManage = request.getParameter("isManage");
    boolean isManage = false;
    if (sIsManage != null || "".equals(sIsManage)) {
      isManage = Boolean.valueOf(sIsManage);
    }
    
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
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        //取转交相关数据        String msg = flowRunLogic.getTurnData(loginUser , runId , prcsId , flowPrcsStr ,dbConn , isManage);
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
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String prcsChoose = request.getParameter("prcsChoose");
    String remindContent = request.getParameter("smsContent");
    String sIsManage = request.getParameter("isManage");
    boolean isManage = false;
    if (sIsManage != null || "".equals(sIsManage)) {
      isManage = Boolean.valueOf(sIsManage);
    }
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
      String sSmsRemindNext = request.getParameter("smsRemindNext");
      String sWebMailRemindNext = request.getParameter("webMailRemindNext");
      String sSmsRemindStart = request.getParameter("smsRemindStart");
      String sWebMailRemindStart = request
          .getParameter("webMailRemindStart");
      String sSmsRemindAll = request.getParameter("smsRemindAll");
      String sWebMailRemindAll = request.getParameter("webMailRemindAll");
      String sSms2RemindAll = request.getParameter("sms2RemindAll");
      String sSms2RemindNext = request.getParameter("sms2RemindNext");
      String sSms2RemindStart = request.getParameter("sms2RemindStart");
      int remindFlag = T9WorkFlowUtility.getRemindFlag( sSmsRemindNext ,  sSms2RemindNext ,  sWebMailRemindNext
          ,  sSmsRemindStart,  sSms2RemindStart,  sWebMailRemindStart 
          ,  sSmsRemindAll ,  sSms2RemindAll ,  sWebMailRemindAll);
      //T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      //String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
      T9FlowProcessLogic fp = new T9FlowProcessLogic();
      //固定流程
      String pluginName =  fp.getPluginStr(flowPrcs, flowId, dbConn);
      T9IWFPlugin  pluginObj = null;
      if (pluginName != null
          && !"".equals(pluginName)) {
        String className = PLUGINPACKAGE + "." + pluginName;
        try{
          pluginObj = (T9IWFPlugin) Class.forName(className).newInstance();
        }catch(ClassNotFoundException ex){
        }
      }
      if (pluginObj != null) {
        String str = pluginObj.before(request, response);
        if (str != null) {
          request.setAttribute(T9ActionKeys.RET_STATE, "3");
          request.setAttribute(T9ActionKeys.RET_MSRG, str);
          return "/core/inc/rtjson.jsp";
        }
      }
      String sortId = request.getParameter("sortId");
      if (sortId == null) {
        sortId = "";
      }
      String skin = request.getParameter("skin");
      if (skin == null) {
        skin = "";
      }
      //结束流程
      //发送短信提醒      flowRunLogic.remindAllAndSend(dbConn, remindFlag, remindContent, request.getContextPath(), runId, loginUser.getSeqId() , flowId);
      String imgPath = T9WorkFlowUtility.getImgPath(request);
      flowRunLogic.remaindEmail(flowId, prcsId, runId, dbConn, loginUser , imgPath , request.getContextPath());
      if(prcsChoose== null || "".equals(prcsChoose) || "0".equals(prcsChoose)|| "0,".equals(prcsChoose)){
        String prcsUser = request.getParameter("prcsUser_0");
        String prcsOpUser = request.getParameter("prcsOpUser_0");
        String topFlag = request.getParameter("topFlag_0");
        String prcsBack = request.getParameter("prcsBack");
        String viewUser = request.getParameter("VIEW_USER_ID");
        if (prcsBack == null) {
          prcsBack = "";
        }
        flowRunLogic.turnEnd(loginUser, runId, flowId, prcsId, flowPrcs, prcsUser, prcsOpUser, topFlag  , request.getRemoteAddr() , dbConn , prcsBack , viewUser);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "办理完毕!");
      }else{
        Map opUserMap = new HashMap();
        String[] aStr = prcsChoose.split(",");
        for(int i = 0 ;i < aStr.length ; i++){
          String prcsUser = request.getParameter("prcsUser_" + aStr[i]);
          String prcsOpUser = request.getParameter("prcsOpUser_" + aStr[i]);
          String topFlag = request.getParameter("topFlag_" + aStr[i]);
          opUserMap.put("prcsOpUser_" + aStr[i], prcsOpUser);
          opUserMap.put("prcsUser_" + aStr[i], prcsUser);
          opUserMap.put("topFlag_" + aStr[i], topFlag);
        }
        flowRunLogic.turnNext(loginUser  , runId , flowId , prcsId   , flowPrcs  , prcsChoose 
            , opUserMap   , request.getRemoteAddr() , dbConn);
        String[] ss = prcsChoose.split(",");
        for (int i = 0 ;i < ss.length ;i++) {
          String s = ss[i];
          if (!"".equals(s) && T9Utility.isInteger(s)) {
            int nextFlowPrcs = Integer.parseInt(s);
            //短信提醒下一步经办人
            //为什么不用上面的prcsUser?因为下面这个有可能是委托之后的用户．．．
            String prcsUser2 = (String)opUserMap.get("prcsUser_" + s);
            if ((remindFlag&0x100)>0) {
              String childFlow = (String)opUserMap.get("nextFlow_" + s);
              if (childFlow == null) {
                flowRunLogic.remindNext(dbConn,  runId ,  flowId ,  prcsId + 1,  nextFlowPrcs, remindContent, request.getContextPath(), prcsUser2 , loginUser.getSeqId() , sortId , skin);
              } else {
                int runIdNew = (Integer)opUserMap.get("nextRun_" + s);
                flowRunLogic.remindNext(dbConn,  runIdNew ,  Integer.parseInt(childFlow) ,  1,  1 , remindContent, request.getContextPath(), prcsUser2 , loginUser.getSeqId() , sortId , skin);
              }
              
            }
            if ((remindFlag&0x40)>0 ) {
              
            }
            if ((remindFlag&0x80)>0) {
              T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
              ms2l.remindByMobileSms(dbConn, prcsUser2 , loginUser.getSeqId(), remindContent, null);
            }
          }
        }
        if (isManage) {
          flowRunLogic.manageTurnNext(dbConn, runId, prcsId, flowPrcs);
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功转交!");
      }
      if (pluginObj != null) {
        pluginObj.after(request, response);
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String backTo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String prcsIdPre = request.getParameter("prcsIdPre");
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
      //验证是否有权限,并取出权限字符串
      //String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
      //取转交相关数据
      String sortId = request.getParameter("sortId");
      if (sortId == null) {
        sortId = "";
      }
      String skin = request.getParameter("skin");
      if (skin == null) {
        skin = "";
      }
      String content = request.getParameter("content");
      if (!T9Utility.isNullorEmpty(content)) {
        Timestamp time =  new  Timestamp(new Date().getTime());
        String insert = "INSERT INTO FLOW_RUN_FEEDBACK (RUN_ID,PRCS_ID,FLOW_PRCS,USER_ID,CONTENT,ATTACHMENT_ID,ATTACHMENT_NAME,EDIT_TIME,SIGN_DATA) VALUES ('"+runId+"','"+prcsId+"','"+flowPrcs+"','"+loginUser.getSeqId()+"',?,'','',?,'')";
        PreparedStatement stm5 = null;
        try {
          stm5 = dbConn.prepareStatement(insert);
          stm5.setString(1, content);
          stm5.setTimestamp(2, time);
          stm5.executeUpdate();
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm5, null, null); 
        }
      }
      flowRunLogic.backTo(loginUser , runId , flowId, prcsId , flowPrcs , prcsIdPre, request.getRemoteAddr(), request.getContextPath() ,sortId , skin , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "回退成功!");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
