package t9.core.funcs.doc.send.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.workflow.logic.T9ConfigLogic;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowRunData;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowManageLogic;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.funcs.doc.receive.data.T9DocConst;
import t9.core.funcs.doc.send.data.T9DocFlowRun;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.funcs.doc.send.logic.T9DocSendLogic;
import t9.subsys.oa.rollmanage.data.T9RmsFile;
import t9.subsys.oa.rollmanage.logic.T9RmsFileLogic;

/**
 * ddddd
 * @author liuhan
 *
 */
public class T9DocSendAct {
  
  private T9RmsFileLogic logic = new T9RmsFileLogic();
  
  
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.send.act.T9DocSendAct");
  /**
   * 取得发文的状态

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSendState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sRunId = request.getParameter("runId");
      String sFlowId = request.getParameter("flowId");
      String sPrcsId = request.getParameter("flowPrcs");
      int prcsId = 0 ;
      int flowId = 0 ;
      int runId = 0 ;
      if (T9Utility.isInteger(sRunId)) {
        runId = Integer.parseInt(sRunId);
      }
      if (T9Utility.isInteger(sFlowId)) {
        flowId = Integer.parseInt(sFlowId);
      }
      if (T9Utility.isInteger(sPrcsId)) {
        prcsId = Integer.parseInt(sPrcsId);
      }
      T9DocLogic logic = new T9DocLogic();
      boolean flag = logic.getSendState(runId, prcsId, flowId, dbConn);
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, flag + "");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 取得发文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSendMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sRunId = request.getParameter("runId");
      T9DocLogic logic = new T9DocLogic();
      String data = logic.getSendMesage(Integer.parseInt(sRunId), dbConn , request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  } 
  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRemindInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      Statement stm = null;
      ResultSet rs = null;
      String title = "";
      String toDept = "";
      String doc = "";
      String query = "select"
        + " TITLE"
        + " , DOC_FLOW_RUN.DOC"
        + ", DOC_SEND.DOC_NAME"
        + ", DOC_SEND.DOC_ID"
        + ", TO_DEPT"
        + ", IS_OUT"
        + " from DOC_SEND ,DOC_FLOW_RUN  where DOC_FLOW_RUN.RUN_ID = DOC_SEND.RUN_ID AND DOC_SEND.seq_id = " + seqId;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()) {
          title = T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("TITLE")));
          doc =  T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("DOC")));
          toDept = T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("TO_DEPT")));
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
      
      String type = request.getParameter("type");
      String content = "";
      String url = "";
      String users = "";
      if ("1".equals(type)) {
        T9DocSendLogic logic = new T9DocSendLogic();
        String[] depts = new String[1];
        depts[0] = toDept;
        users = logic.getRoleByDepts(dbConn,depts );
        content = "收到来文：" + title + "，对方文号："+ doc +"，请签收！";
        url = "/core/funcs/doc/receive/sign/sign.jsp";
      } else if ("2".equals(type)) {
        users = this.getUsers(dbConn, toDept);
        content = "收到未登记的来文：" + title + "，对方文号："+ doc +"，请尽快登记并办理！";
      } else if ("3".equals(type)) {
        T9DocSendLogic send = new T9DocSendLogic();
        users = send.getRegisterUser(dbConn, seqId);
        content = "你登记的来文：" + title + "，对方文号："+ doc +"，已经收回，请停止办理！";
      }
      T9PersonLogic pL = new T9PersonLogic();
      String userNames = pL.getNameBySeqIdStr(users, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, content);
      request.setAttribute(T9ActionKeys.RET_DATA, "{userIds:\""+T9WorkFlowUtility.getOutOfTail(users)+"\" , userNames:\""+userNames+"\"}");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  
  public String getUsers(Connection conn , String deptId) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    String r = "";
    String query = "select SEQ_ID from PERSON  where DEPT_ID = '" + deptId + "'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        r += rs.getInt("SEQ_ID") + ",";
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return r;
  }
  public String remindUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String toId = request.getParameter("toId");
    String content = request.getParameter("content");
    String type = request.getParameter("type");
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String url = "";
      if ("1".equals(type)) {
        url =  "/core/funcs/doc/receive/sign/sign.jsp";
      } else if ("2".equals(type)) {
        url =  "/core/funcs/doc/receive/register/docReg.jsp?rec_seqId=" + seqId;
      } else if ("3".equals(type)) {
      }
      if (!T9Utility.isNullorEmpty(toId)) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType(T9DocConst.remindType);
        sb.setContent(content);
        sb.setFromId(loginUser.getSeqId());
        sb.setToId(toId);
        sb.setRemindUrl(url);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "催办短信已发送 ");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String cancel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String status = request.getParameter("status");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9DocSendLogic send = new T9DocSendLogic();
      send.cancel(dbConn, seqId, status, loginUser.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "催办短信已发送 ");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String resend(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String status = request.getParameter("status");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9DocSendLogic send = new T9DocSendLogic();
      send.resend(dbConn, seqId, status, loginUser.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "催办短信已发送 ");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String cancelAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runId = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      Statement stm = null;
      ResultSet rs = null;
      String query = "select"
        + " SEQ_ID"
        + ", STATUS "
        + " from  DOC_SEND  where RUN_ID  = " + runId + " and is_cancel = '0'";
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()) {
           String seqId=  String.valueOf(rs.getInt("SEQ_ID"));
           String status =  rs.getString("STATUS");
           T9DocSendLogic send = new T9DocSendLogic();
           send.cancel(dbConn, seqId, status, loginUser.getSeqId());
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "催办短信已发送 ");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得提醒方式
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRemindType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9ConfigLogic logic = new T9ConfigLogic();
      
      StringBuffer sb = new StringBuffer();
      String paraValue = logic.getSysPar("SMS_REMIND", conn);
      String[] remindArray = paraValue.split("\\|");
      String smsRemind = "";
      String sms2remind = "";
      String smsRemindPrivStr = "";
      
      if (remindArray.length == 1) {
        smsRemind = remindArray[0];
      } else if (remindArray.length  == 2) {
        smsRemind = remindArray[0];
        sms2remind = remindArray[1];
      } else if (remindArray.length == 3) {
        smsRemind = remindArray[0];
        sms2remind = remindArray[1];
        smsRemindPrivStr = remindArray[2];
      }
      boolean smsPriv = false ;
      if (T9WorkFlowUtility.findId(smsRemindPrivStr, "70")) {
        smsPriv = true;
      }
      boolean smsRemindB = false;
      if (T9WorkFlowUtility.findId(smsRemind, "70")) {
        smsRemindB = true;
      }
      boolean sms2RemindB = false;
      if (T9WorkFlowUtility.findId(sms2remind, "70")) {
        sms2RemindB = true;
      }
      sb.append("{smsPriv:"+ smsPriv +",smsRemind:"  + smsRemindB + ", sms2Remind:" + sms2RemindB + "");
      String query = "select TYPE_PRIV,SMS2_REMIND_PRIV from SMS2_PRIV";
      String typePriv = "";
      String sms2RemindPriv = "";
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        if(rs.next()){
          typePriv = rs.getString("TYPE_PRIV");
          sms2RemindPriv = rs.getString("SMS2_REMIND_PRIV");
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null); 
      }
      //检查该模块是否允许手机提醒
      boolean sms2Priv = false ;
      if (T9WorkFlowUtility.findId(typePriv, "70") 
          && T9WorkFlowUtility.findId(sms2RemindPriv , String.valueOf(loginUser.getSeqId()))) {
        sms2Priv = true;
      }
      sb.append (", sms2Priv:" + sms2Priv + "}") ;
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 发文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String sendDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String runIdStr = request.getParameter("runId");
      int runId = Integer.parseInt(runIdStr);
      String deptId = request.getParameter("deptId");
      String deptId2 = T9Utility.null2Empty(request.getParameter("deptId2"));
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DocSendLogic docSendLogic = new T9DocSendLogic();
      String remindDocSend = request.getParameter("remindDocSend");
      String remindDocSend2 = request.getParameter("remindDocSend2");
      boolean remind = false;
      if (remindDocSend != null && remindDocSend.equals("on")) {
        remind = true;
      }
      boolean remind2 = false;
      if (remindDocSend2 != null && remindDocSend2.equals("on")) {
        remind2 = true;
      }
      String webroot = request.getRealPath("/");
      //String attachmentId = request.getParameter("recDocId");
      //String attachmentName = request.getParameter("recDocName");
      if (!T9Utility.isNullorEmpty(deptId)) {
        docSendLogic.sendDocToDept(deptId, runId, dbConn, webroot, loginUser.getSeqId() ,  remind  , remind2 );
      }
      if (!T9Utility.isNullorEmpty(deptId2)) {
        docSendLogic.sendDocToEsbDept(deptId2, runId, dbConn, webroot, loginUser.getSeqId() ,  remind );
      }
      //docSendLogic.setSendFlag(runId , deptId , deptId2, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
}
