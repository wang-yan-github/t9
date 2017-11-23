package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9DelegateLogic {
  /**
   * 委托界面的一些相关数据
   * @param runId
   * @param prcsId
   * @param flowId
   * @param flowPrcs
   * @param conn
   * @param user
   * @return {runName:'dd',flowName:'ddd' ,userOldId:11,opFlag:1, freeOther:2 ,smsRemind:true,noUser:false,prcsList:[{id : 1, prcsName : 'ssss' , userName:'ssss,ssss,sss,'},{id : 2, prcsName : 'ssss' , userName:'ssss,ssss,sss,'},{id : 3, prcsName : 'ssss' , userName:'ssss,ssss,sss,'}]};
   * @throws Exception
   */
  public String getDelegateMsg(int runId , int prcsId , int flowId , int flowPrcs , Connection conn, T9Person user ,boolean isManage) throws Exception{
    StringBuffer sb = new StringBuffer("{");
    String query = "SELECT USER_ID,OP_FLAG "
      + " from FLOW_RUN_PRCS WHERE "
      + " RUN_ID="+ runId 
      + " AND PRCS_ID="+prcsId  
      + " AND FLOW_PRCS=" + flowPrcs ;
    if (isManage) {
      query += " and OP_FLAG='1'";
    } else {
      query  += " AND USER_ID=" + user.getSeqId();
    }
    
    int opFlag = 0; 
    int userOldId = user.getSeqId();
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        opFlag = Integer.parseInt(rs.getString("OP_FLAG")) ;
        userOldId = rs.getInt("USER_ID");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    sb.append("opFlag:" + opFlag);
    sb.append(",userOldId:" + userOldId);
    
    String runNameQuery  = "SELECT RUN_NAME from FLOW_RUN WHERE RUN_ID=" + runId;
    String runName = "";
    Statement stm5 = null;
    ResultSet rs5 = null;
    try {
      stm5 = conn.createStatement();
      rs5 = stm5.executeQuery(runNameQuery);
      if (rs5.next()) {
        runName = rs5.getString("RUN_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, rs5, null); 
    }
    runName = T9WorkFlowUtility.getRunName(runName);
    sb.append(",runName:'" + runName + "'");
    String flowNameQuery  = "SELECT FLOW_NAME,SEQ_ID AS FLOW_ID,FREE_OTHER from FLOW_TYPE WHERE SEQ_ID=" + flowId;
    String flowName = "";
    String freeOther = "2";
    Statement stm6 = null;
    ResultSet rs6 = null;
    try {
      stm6 = conn.createStatement();
      rs6 = stm6.executeQuery(flowNameQuery);
      if (rs6.next()) {
        flowName = rs6.getString("FLOW_NAME");
        freeOther = rs6.getString("FREE_OTHER");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm6, rs6, null); 
    }
    sb.append(",flowName:'" + flowName + "'");
    sb.append(",freeOther:" + freeOther);
    
    sb.append(",prcsList:[");
    Map prcsName = new HashMap();
    for (int i = 1 ;i <= prcsId ; i++) {
      String query1 = "SELECT USER_ID,FLOW_PRCS,OP_FLAG from FLOW_RUN_PRCS where RUN_ID="+ runId +" and PRCS_ID=" + i ;
      Statement stm1 = null;
      ResultSet rs1 = null;
      try {
        stm1 = conn.createStatement();
        rs1 = stm1.executeQuery(query1);
        int flowPrcsCount = 0 ;
        if (rs1.next()) {
          flowPrcsCount++;
          int flowPrcsI = rs1.getInt("FLOW_PRCS");
          String userStr = "";
          String query2 = "select USER_ID from FLOW_RUN_PRCS WHERE RUN_ID="+ runId +" AND PRCS_ID="+ i +" AND FLOW_PRCS=" + flowPrcsI;
          Statement stm2 = null;
          ResultSet rs2 = null;
          try {
            stm2 = conn.createStatement();
            rs2 = stm2.executeQuery(query2);
            while (rs2.next()) {
              String tmp = rs2.getString("USER_ID");
              if (!T9WorkFlowUtility.findId(userStr, tmp)) {
                userStr += tmp + ",";
              }
            }
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm2, rs2, null); 
          }
          T9PersonLogic logic = new T9PersonLogic();
          String userName = logic.getNameBySeqIdStr(userStr, conn);
          if (userName.endsWith(",")) {
            userName = userName.substring(0, userName.length() - 1);
          }
          String sFlowPrcsI = String.valueOf(flowPrcsI);
          if (!prcsName.containsKey(sFlowPrcsI)) {
            String query3 = "select PRCS_NAME from FLOW_PROCESS WHERE FLOW_SEQ_ID="+ flowId +" AND PRCS_ID=" + flowPrcsI;
            Statement stm3 = null;
            ResultSet rs3 = null;
            try {
              stm3 = conn.createStatement();
              rs3 = stm3.executeQuery(query3);
              if (rs3.next()) {
                String tmp = rs3.getString("PRCS_NAME");
                prcsName.put(sFlowPrcsI, tmp);
              }
            } catch(Exception ex) {
              throw ex;
            } finally {
              T9DBUtility.close(stm3, rs3, null); 
            }
            
          }
          sb.append("{id:" + i) ;
          sb.append(",prcsName:'" + prcsName.get(sFlowPrcsI) +"'");
          sb.append(",userName:'" + userName + "'},");
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm1, rs1, null); 
      }
      
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    T9ConfigLogic logic = new T9ConfigLogic();
    String paraValue = logic.getSysPar("SMS_REMIND", conn);
    String[] remindArray = paraValue.split("\\|");
    String smsRemind = "";
    String sms2remind = "";
    if (remindArray.length == 1) {
      smsRemind = remindArray[0];
    } else if (remindArray.length  >= 2) {
      smsRemind = remindArray[0];
      sms2remind = remindArray[1];
    }
    boolean bSmsRemind = false ;
    if (T9WorkFlowUtility.findId(smsRemind, "7")) {
      bSmsRemind = true;
    }
    boolean bSms2Remind = false ;
    if (T9WorkFlowUtility.findId(sms2remind, "7")) {
      bSms2Remind = true;
    }
    sb.append(",smsRemind:"  + bSmsRemind + ",sms2Remind:" + bSms2Remind);
    query = "select TYPE_PRIV from SMS2_PRIV";
    String typePriv = "";
    String sms2RemindPriv = "";
    Statement stm1 = null;
    ResultSet rs1 = null;
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(query);
      if(rs1.next()){
        typePriv = rs1.getString("TYPE_PRIV");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null); 
    }
    //检查该模块是否允许手机提醒
    boolean sms2Priv = false ;
    if (T9WorkFlowUtility.findId(typePriv, "7")) {
      sms2Priv = true;
    }
    
    sb.append(",sms2Priv:" + sms2Priv);
    sb.append("}");
    return sb.toString();
  }
  /**
   * 检查是否主办人且正在办理
   * @param runId
   * @param prcsId
   * @param userId
   * @return
   * @throws Exception 
   */
  public boolean checkHandlerState(int runId , int prcsId , int userId , Connection conn) throws Exception{
    String query ="select 1 from FLOW_RUN_PRCS WHERE RUN_ID="+ runId + " AND PRCS_ID="+ prcsId +" AND USER_ID="+ userId +" AND (PRCS_FLAG='1' OR PRCS_FLAG='2')";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (!rs.next()) {
        return false;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return true;
  }
  
  /**
   * 取得委托选项
   * @param flowId
   * @param conn
   * @return
   * @throws Exception
   */
  public int getFreeOther(int flowId , Connection conn) throws Exception {
    String query  = "select FREE_OTHER FROM FLOW_TYPE where SEQ_ID=" +  flowId;
    Statement stm = null;
    ResultSet rs = null;
    String freeOther = "0";//默认为禁止委托
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next() && rs.getString("FREE_OTHER") != null) {
        freeOther = rs.getString("FREE_OTHER");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return Integer.parseInt(freeOther);
  }
  public String delegate(int runId , int prcsId , int flowId ,int flowPrcs ,T9Person user ,int toId,String opFlag , String remindFlag  , String  remindFlag2, int userOldId, String remindContent , String contextPath,String sortId,  String skin, Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    String query = "select PARENT,TOP_FLAG from FLOW_RUN_PRCS where RUN_ID="+ runId +" and PRCS_ID="+ prcsId +" and FLOW_PRCS="+ flowPrcs +" and USER_ID=" + userOldId;
    String parent = "";
    String topFlag = "0";
    
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        parent = rs.getString("PARENT");
        topFlag = rs.getString("TOP_FLAG");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    //主办人:转交主办权后 作为经办人; 经办人:增加经办人    String query2 = "select OP_FLAG "
      + " from FLOW_RUN_PRCS WHERE "
      + " RUN_ID =" + runId 
      + " and PRCS_ID=" + prcsId 
      + " and FLOW_PRCS="  + flowPrcs
      + " and USER_ID=" + toId;
    
    Statement stm2 = null;
    ResultSet rs2 = null;
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query2);
      if (!rs2.next()) {
        T9FlowRunPrcs r = new T9FlowRunPrcs();
        r.setRunId(runId);
        r.setPrcsId(prcsId);
        r.setUserId(toId);
        r.setFlowPrcs(flowPrcs);
        r.setPrcsFlag("1");
        r.setOpFlag(opFlag);
        r.setTopFlag(topFlag);
        r.setParent(parent);
        r.setOtherUser(userOldId);
        r.setCreateTime(new Date());
        orm.saveSingle(conn, r);
      } else {
        //已经是经办人的 更新为主办人
        if ("0".equals(rs2.getString("OP_FLAG")) && "1".equals(opFlag)) {
          String update = "update FLOW_RUN_PRCS set  " 
            + " OP_FLAG='1' " 
            + " ,PRCS_FLAG='2'  " 
            + " where  " 
            + " RUN_ID="+runId+"  " 
            + " and PRCS_ID="+prcsId+"  " 
            + " and FLOW_PRCS="+ flowPrcs +"  " 
            + " AND USER_ID=" + toId;
          Statement stm3 = null;
          try {
            stm3 = conn.createStatement();
            stm3.executeUpdate(update);
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm3, null, null); 
          }
        } else {
          String userName = this.getUserNameBySeqId(toId, conn);
          return "["+ userName +"] 已经是本步骤主办人!您不必委托。";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null); 
    }
  //更新本步骤为结束 统一为经办人
    String query1 = "update FLOW_RUN_PRCS set PRCS_TIME=? where " 
      + " RUN_ID=" + runId  
      + " and PRCS_ID=" + prcsId 
      + " AND USER_ID=" + userOldId
      + " and FLOW_PRCS=" + flowPrcs
      + "  AND PRCS_FLAG=1" 
      + " AND PRCS_TIME is null";
    Timestamp time = new Timestamp(new Date().getTime());
    PreparedStatement stm4 = null;
    try {
      stm4 = conn.prepareStatement(query1);
      stm4.setTimestamp(1, time);
      stm4.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm4, null, null); 
    }
    //更新本步骤为结束 统一为经办人
    String query3 = "update FLOW_RUN_PRCS set PRCS_FLAG=4 " 
      + " ,OP_FLAG=0 " 
      + " ,DELIVER_TIME=?" 
      + " where  " 
      + " RUN_ID=" + runId 
      + " and FLOW_PRCS=" + flowPrcs
      + " and PRCS_ID=" + prcsId
      + " AND USER_ID=" + userOldId;
    PreparedStatement stm5 = null;
    try {
      stm5 = conn.prepareStatement(query3);
      stm5.setTimestamp(1, time);
      stm5.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, null, null); 
    }
    //工作流日志    String toName = this.getUserNameBySeqId(toId, conn);
    String oldName  = this.getUserNameBySeqId(userOldId, conn);
    String content = "["+ oldName +"]的工作委托给["+ toName +"]";
    T9FlowRunLogLogic logLogic = new T9FlowRunLogLogic();
    String ip = "";
    logLogic.runLog(runId, prcsId, flowPrcs, user.getSeqId(), 2, content , ip , conn);
    if ("on".equals(remindFlag)) {
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("7");
      sb.setContent(remindContent);
      sb.setFromId(user.getSeqId());
      String id = String.valueOf(toId);
      sb.setToId(id);
      sb.setRemindUrl( "/core/funcs/workflow/flowrun/list/inputform/index.jsp?skin="+ skin +"&sortId="+sortId+"&runId="+runId+"&flowId="+flowId+"&prcsId="+prcsId+"&flowPrcs=" + flowPrcs);
      T9SmsUtil.smsBack(conn, sb);
    } 
    if ("on".equals(remindFlag2)) {
      T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
      ms2l.remindByMobileSms(conn, String.valueOf(toId), user.getSeqId(), content, null);
    }
    return "委托操作已成功！";
  }
  /**
   * 根据seqId查询人名
   * @param seqId
   * @param conn
   * @return
   * @throws Exception
   */
  public String getUserNameBySeqId (int seqId , Connection conn) throws Exception {
    String queryName ="select USER_NAME from PERSON where SEQ_ID=" + seqId;
    String userName = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(queryName);
      if (rs.next()) {
        userName = rs.getString("USER_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return userName;
  }
  /**
   * 取得步骤的所有经办人
   * @param prcsId
   * @param runId
   * @return
   * @throws Exception 
   */
  public String getPrcsOpUsers(int prcsId  , int runId , String search , Connection conn) throws Exception {
    StringBuffer sb = new StringBuffer("[");
  //============================ 显示人员信息 =======================================
    String queryUserId = "SELECT F.USER_ID " 
      + " ,U.USER_NAME" 
      + " ,D.DEPT_NAME" 
      + "  from FLOW_RUN_PRCS F , PERSON U , DEPARTMENT D where " 
      + "  F.RUN_ID="+ runId 
      + "  and F.PRCS_ID=" + prcsId
      + "  and F.USER_ID=U.SEQ_ID " 
      + "  and U.DEPT_ID=D.SEQ_ID";
    if (search != null && !"".equals(search)) {
      queryUserId += "  and U.USER_NAME LIKE '" + T9Utility.encodeLike(search) + "%' " + T9DBUtility.escapeLike() ;
    }
    String userIds = "";
    int count = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(queryUserId);
      while (rs.next()) {
        String userId = rs.getString("USER_ID");
        if (!T9WorkFlowUtility.findId(userIds, userId)) {
          sb.append("{");
          sb.append("userId:" + userId);
          sb.append(",userName:'" + rs.getString("USER_NAME") + "'");
          sb.append(",dept:'" + rs.getString("DEPT_NAME") + "'");
          sb.append("},");
          count ++;
          userIds += userId + ",";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  /**
   * 取得自由相关信息
   * @param runId
   * @param prcsId
   * @param flowId
   * @param conn
   * @param user
   * @return
   * @throws Exception 
   */
  public String getFreeDelegateMsg(int runId, int prcsId, int flowId,
      Connection conn, T9Person user , boolean isManage) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer("{");
    String query = "SELECT USER_ID,OP_FLAG,TOP_FLAG "
      + " from FLOW_RUN_PRCS WHERE "
      + " RUN_ID="+ runId 
      + " AND PRCS_ID="+prcsId ;
      if (isManage) {
        query += " and OP_FLAG='1'";
      } else {
        query  += " AND USER_ID=" + user.getSeqId();
      }
//    if(stristr($HTTP_REFERER,"/workflow/manage/"))
//      $query .=" and OP_FLAG=1";
//    else
//      $query .=" and USER_ID='$LOGIN_USER_ID'";
    int opFlag = 0; 
    int userOldId = user.getSeqId();
    int topFlag = 0 ;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        opFlag = Integer.parseInt(rs.getString("OP_FLAG")) ;
        userOldId = rs.getInt("USER_ID");
        topFlag = Integer.parseInt(rs.getString("TOP_FLAG"));
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    sb.append("opFlag:" + opFlag);
    sb.append(",userOldId:" + userOldId);
    sb.append(",topFlag:" + topFlag);
    
    String flowNameQuery  = "SELECT FLOW_NAME,SEQ_ID AS FLOW_ID,FREE_OTHER from FLOW_TYPE WHERE SEQ_ID=" + flowId;
    String flowName = "";
    String freeOther = "2";
    Statement stm6 = null;
    ResultSet rs6 = null;
    try {
      stm6 = conn.createStatement();
      rs6 = stm6.executeQuery(flowNameQuery);
      if (rs6.next()) {
        flowName = rs6.getString("FLOW_NAME");
        freeOther = rs6.getString("FREE_OTHER");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm6, rs6, null); 
    }
    sb.append(",flowName:'" + flowName + "'");
    sb.append(",freeOther:" + freeOther);
    
    String runNameQuery  = "SELECT RUN_NAME from FLOW_RUN WHERE RUN_ID=" + runId;
    String runName = "";
    Statement stm5 = null;
    ResultSet rs5 = null;
    try {
      stm5 = conn.createStatement();
      rs5 = stm5.executeQuery(runNameQuery);
      if (rs5.next()) {
        runName = rs5.getString("RUN_NAME");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, rs5, null); 
    }
    runName = T9WorkFlowUtility.getRunName(runName);
    sb.append(",runName:'" + runName + "'");
    T9ConfigLogic logic = new T9ConfigLogic();
    String paraValue = logic.getSysPar("SMS_REMIND", conn);
    String[] remindArray = paraValue.split("\\|");
    String smsRemind = "";
    String sms2remind = "";
    if (remindArray.length == 1) {
      smsRemind = remindArray[0];
    } else if (remindArray.length  >= 2) {
      smsRemind = remindArray[0];
      sms2remind = remindArray[1];
    }
    boolean bSmsRemind = false ;
    if (T9WorkFlowUtility.findId(smsRemind, "7")) {
      bSmsRemind = true;
    }
    boolean bSms2Remind = false ;
    if (T9WorkFlowUtility.findId(sms2remind, "7")) {
      bSms2Remind = true;
    }
    sb.append(",smsRemind:"  + bSmsRemind + ",sms2Remind:" + bSms2Remind);
    query = "select TYPE_PRIV from SMS2_PRIV";
    String typePriv = "";
    String sms2RemindPriv = "";
    Statement stm1 = null;
    ResultSet rs1 = null;
    try {
      stm1 = conn.createStatement();
      rs1 = stm1.executeQuery(query);
      if(rs1.next()){
        typePriv = rs1.getString("TYPE_PRIV");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm1, rs1, null); 
    }
    //检查该模块是否允许手机提醒
    boolean sms2Priv = false ;
    if (T9WorkFlowUtility.findId(typePriv, "7")) {
      sms2Priv = true;
    }
    
    sb.append(",sms2Priv:" + sms2Priv);
    sb.append("}");
    return sb.toString();
  }
  public String freeDelegate(int runId , int prcsId , int flowId  ,T9Person user ,int toId,String opFlag , String remindFlag  , String remindFlag2 , int userOldId, String remindContent , String contextPath,String sortId , String skin, Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    String query = "select PARENT,TOP_FLAG,FREE_ITEM from FLOW_RUN_PRCS where RUN_ID="+ runId +" and PRCS_ID="+ prcsId +"  and USER_ID=" + userOldId;
    String parent = "";
    String topFlag = "0";
    String freeItem = "";
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        parent = rs.getString("PARENT");
        topFlag = rs.getString("TOP_FLAG");
        freeItem = rs.getString("FREE_ITEM");
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    //主办人:转交主办权后 作为经办人; 经办人:增加经办人
    String query2 = "select OP_FLAG "
      + " from FLOW_RUN_PRCS WHERE "
      + " RUN_ID =" + runId 
      + " and PRCS_ID=" + prcsId 
      + " and USER_ID=" + toId;
    
    int flowPrcs1 = 0 ;
    Statement stm2 = null;
    ResultSet rs2 = null;
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query2);
      if (!rs2.next()) {
        if (prcsId == 1) {
          flowPrcs1 = 1; //兼容自由流程第一步FLOW_PRCS=1
        }
        T9FlowRunPrcs r = new T9FlowRunPrcs();
        r.setRunId(runId);
        r.setPrcsId(prcsId);
        r.setUserId(toId);
        r.setFlowPrcs(flowPrcs1);
        r.setPrcsFlag("1");
        r.setOpFlag(opFlag);
        r.setTopFlag(topFlag);
        r.setParent(parent);
        r.setFreeItem(freeItem);
        r.setOtherUser(userOldId);
        r.setCreateTime(new Date());
        orm.saveSingle(conn, r);
      } else {
        //已经是经办人的 更新为主办人
        if ("0".equals(rs2.getString("OP_FLAG")) && "1".equals(opFlag)) {
          String update = "update FLOW_RUN_PRCS set  " 
            + " OP_FLAG='1' " 
            + " ,PRCS_FLAG='2' " 
            + "  where  " 
            + " RUN_ID="+runId+"  " 
            + " and PRCS_ID="+prcsId +"  " 
            + " AND USER_ID=" + toId;
          Statement stm3 = null;
          try {
            stm3 = conn.createStatement();
            stm3.executeUpdate(update);
          } catch(Exception ex) {
            throw ex;
          } finally {
            T9DBUtility.close(stm3, null, null); 
          }
        } else {
          String userName = this.getUserNameBySeqId(toId, conn);
          return "["+ userName +"] 已经是本步骤主办人!您不必委托。";
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null); 
    }
   //更新本步骤为结束 统一为经办人
    String query1 = "update FLOW_RUN_PRCS set PRCS_TIME=? where " 
      + " RUN_ID=" + runId  
      + " and PRCS_ID=" + prcsId 
      + " AND USER_ID=" + userOldId
      + "  AND PRCS_FLAG=1" 
      + " AND PRCS_TIME is null";
    Timestamp time =  new  Timestamp(new Date().getTime());
    PreparedStatement stm4 = null;
    try {
      stm4 = conn.prepareStatement(query1);
      stm4.setTimestamp(1, time);
      stm4.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm4, null, null); 
    }
    //更新本步骤为结束 统一为经办人
    String query3 = "update FLOW_RUN_PRCS set PRCS_FLAG=4 " 
      + " ,OP_FLAG=0 " 
      + " ,DELIVER_TIME=?" 
      + " where  " 
      + " RUN_ID=" + runId 
      + " and PRCS_ID=" + prcsId
      + " AND USER_ID=" + userOldId;
    PreparedStatement stm5 = null;
    try {
      stm5 = conn.prepareStatement(query3);
      stm5.setTimestamp(1, time);
      stm5.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, null, null); 
    }
    //工作流日志
    String toName = this.getUserNameBySeqId(toId, conn);
    String oldName  = this.getUserNameBySeqId(userOldId, conn);
    String content = "["+ oldName +"]的工作委托给["+ toName +"]";
    T9FlowRunLogLogic logLogic = new T9FlowRunLogLogic();
    String ip = "";
    logLogic.runLog(runId, prcsId, 0, user.getSeqId(), 2, content , ip , conn);
    if ("on".equals(remindFlag)) {
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("7");
      sb.setContent(remindContent);
      sb.setFromId(user.getSeqId());
      Map map = new HashMap();
      map.put("RUN_ID", runId);
      String id = String.valueOf(toId);
      sb.setToId(id);
      sb.setRemindUrl( "/core/funcs/workflow/flowrun/list/inputform/index.jsp?skin="+ skin +"&sortId="+sortId+"&runId="+runId+"&flowId="+flowId+"&prcsId="+prcsId +"&flowPrcs=" + flowPrcs1);
      T9SmsUtil.smsBack(conn, sb);
    }
    if ("on".equals(remindFlag2)) {
      T9MobileSms2Logic ms2l = new T9MobileSms2Logic(); 
      ms2l.remindByMobileSms(conn, String.valueOf(toId), user.getSeqId(), content, null);
    }
    return "委托操作已成功！";
  }
  
}
