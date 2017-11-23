package t9.core.funcs.doc.flowrunRec.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.doc.data.T9DocFlowRunLog;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9FlowManageLogic {
  public String getFocusUser(int runId , Connection conn) throws Exception {
    String query = "select FOCUS_USER from "+ T9WorkFlowConst.FLOW_RUN +" where RUN_ID=" + runId;
    String focusUser = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()){
        focusUser = rs.getString("FOCUS_USER");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return focusUser ;
  }
  public void focus(T9Person u , String focusUser , int runId , String contextPath , Connection conn) throws Exception {
    if (focusUser == null) {
      focusUser = "";
    }
    focusUser += u.getSeqId() + ",";
    String query = "SELECT USER_ID from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" where RUN_ID=" + runId;
    String userIdStr = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()){
        int userId = rs.getInt("USER_ID");
        if (userId != u.getSeqId()) {
          userIdStr += userId + ",";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    if (!"".equals(userIdStr)) {
       query =  "SELECT RUN_NAME,FLOW_ID from "+ T9WorkFlowConst.FLOW_RUN +" where RUN_ID=" + runId;
       String runName = "";
       int flowId = 0 ;
       Statement stm2 = null;
       ResultSet rs2 = null;
       try {
         stm2= conn.createStatement();
         rs2 = stm2.executeQuery(query);
         if  (rs2.next()){
           flowId = rs2.getInt("FLOW_ID");
           runName = rs2.getString("RUN_NAME");
         }
       } catch(Exception ex) {
         throw ex;
       } finally {
         T9DBUtility.close(stm2, rs2, null); 
       }
       String content = "您所经办的工作[" + runName + "]已经被" + u.getUserName() + " 关注";
       T9SmsBack sb = new T9SmsBack();
       sb.setSmsType("7");
       sb.setContent(content);
       sb.setFromId(u.getSeqId());
       sb.setToId(userIdStr);
       sb.setRemindUrl( T9WorkFlowConst.MODULE_CONTEXT_PATH + "/flowrunRec/list/print/index.jsp?runId="+runId+"&flowId="+ flowId + "&openFlag=1&width=800&height=600");
       T9SmsUtil.smsBack(conn, sb);
    }
    this.updateFocus(focusUser, runId, conn);
  }
  public void updateFocus(String focusUser , int runId , Connection conn) throws Exception {
    String query = "update "+ T9WorkFlowConst.FLOW_RUN +" set FOCUS_USER='"+focusUser+"' where RUN_ID=" + runId;
    PreparedStatement stm2 = null;
    try {
      stm2 = conn.prepareStatement(query);
      stm2.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, null, null); 
    }
  }
}

