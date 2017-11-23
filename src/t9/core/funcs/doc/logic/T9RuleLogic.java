package t9.core.funcs.doc.logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.data.T9DocFlowRule;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9RuleLogic {
  public void addRule(int userId , int toId , int flowId , String bgDate , String endDate , Connection conn) throws Exception{
    String query = "insert into "+ T9WorkFlowConst.FLOW_RULE +"(USER_ID,TO_ID,FLOW_ID,BEGIN_DATE,END_DATE,STATUS) values ("+ userId +","+ toId +","+ flowId +",?,?,1)";
    Date begindate =  null ;
    if (bgDate != null ){
      begindate = T9Utility.parseSqlDate(bgDate);
    }
    Date dEndDate =  null ;
    if (endDate != null ){
      dEndDate = T9Utility.parseSqlDate(endDate);
    }
    PreparedStatement stm3 = null;
    try {
      stm3 = conn.prepareStatement(query);
      stm3.setDate(1, begindate);
      stm3.setDate(2, dEndDate);
      stm3.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, null, null); 
    }
  }
//{ruleId : 11, status:1 , flowName:'ddd' , toUserName : 'dd', formUserName:'dddd' , available:''}
  public String loadRule(int userId , int ruleState  , Connection conn, String sortId) throws Exception{
    T9ORM orm = new T9ORM();
    Map map = new HashMap();
    if (ruleState == 0) {
      map.put("USER_ID", userId);
    } else {
      map.put("TO_ID", userId);
    }
    List<T9DocFlowRule> ruleList = orm.loadListSingle(conn, T9DocFlowRule.class, map);
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    int count = 0 ;
    for (T9DocFlowRule rule : ruleList) {
      int flowId = rule.getFlowId();
      Map map2 = this.getFlowName(flowId, conn);
      String flowName  = (String)map2.get("flowName");
      int flowSort = (Integer) map2.get("flowSort");
      if (!"".equals(sortId) && !T9WorkFlowUtility.findId(sortId, String.valueOf(flowSort))) {
        continue;
      }
      sb.append("{");
      sb.append("ruleId:" + rule.getSeqId());
      
      sb.append(",flowName:'" + flowName + "'");
      if (ruleState == 0) {
        int userId2 = rule.getToId();
        String userName = this.getUserName(userId2, conn);
        sb.append(",toUserName:'"+ userName +"'" );
      } else {
        int toId = rule.getUserId();
        String userName = this.getUserName(toId, conn);
        sb.append(",formUserName:'"+ userName +"'" );
      }
      int status = 0 ;
      String desc = "";
      java.util.Date bgDate = rule.getBeginDate();
      java.util.Date endDate = rule.getEndDate();
      java.util.Date curDate = new java.util.Date();
      
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      if (rule.getStatus() == 1) {
        if (bgDate != null && endDate != null) {
          desc = format.format(bgDate ) + "--" +  format.format(endDate);
          int con1 =curDate.compareTo(bgDate);
          int con2 =  curDate.compareTo(endDate);
          if (con1 >= 0 && con2 <=0) {
            status = 1;
          }
        } else if (bgDate != null) {
          int con1 =curDate.compareTo(bgDate);
          desc = "开始于" + format.format(bgDate);
          if (con1 >= 0) {
            status = 1;
          }
        } else if (endDate != null) {
          desc = "截止于" + format.format(endDate);
          int con2 =  curDate.compareTo(endDate);
          if (con2 <= 0) {
            status = 1;
          }
        } else {
          desc = "一直有效";
          status = 1;
        }
      } 
      sb.append(",available:'" + desc + "'");
      sb.append(",status:" + status);
      sb.append(",isOpen:" + rule.getStatus());
      sb.append("},");
      count ++ ;
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString() ;
  }
  public String getUserName(int userId , Connection conn) throws Exception {
    String query = "select USER_NAME from PERSON where SEQ_ID=" + userId;
    String userName = "";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        userName = rs.getString("USER_NAME");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , rs , null);
    }
    return userName;
  }
  public Map getFlowName(int flowId , Connection conn) throws Exception {
    String query = "select FLOW_NAME,flow_sort from "+ T9WorkFlowConst.FLOW_TYPE +" where SEQ_ID=" + flowId;
    String flowName = "";
    int flow_sort = 0 ;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        flowName = rs.getString("FLOW_NAME");
        flow_sort = rs.getInt("flow_sort");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , rs , null);
    }
    Map map = new HashMap();
    map.put("flowSort", flow_sort);
    map.put("flowName", flowName);
    return map;
  }
  public String getProcessName(int flowId , int prcsId , Connection conn) throws Exception {
    String query = "select PRCS_NAME from "+ T9WorkFlowConst.FLOW_PROCESS +" where FLOW_SEQ_ID=" + flowId + " AND PRCS_ID=" + prcsId;
    String prcsName = "";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        prcsName = rs.getString("PRCS_NAME");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , rs , null);
    }
    return prcsName;
  }
  /**
   * 打开或关闭
   * @param ruleId
   * @param isOpened 现在的状态
   * @param conn
   * @throws Exception 
   */
  public void openOrClose(int ruleId , boolean isOpened , Connection conn) throws Exception {
    String update = "";
    if (isOpened) {
      update = "update "+ T9WorkFlowConst.FLOW_RULE +" SET status = 0 where SEQ_ID=" + ruleId;
    } else {
      update = "update "+ T9WorkFlowConst.FLOW_RULE +" SET status = 1 where SEQ_ID=" + ruleId;
    }
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(update);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , null , null);
    }
  }
  /**
   *  
   * @param ruleId
   * @param conn
   * @throws Exception
   */
  public void delRule(String ruleId , Connection conn) throws Exception {
    String del = "delete from "+ T9WorkFlowConst.FLOW_RULE +" where SEQ_ID=" + ruleId;
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(del);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , null , null);
    }
  }
  public void closeOrOpenAll(String ruleIds , boolean isOpen , Connection conn) throws Exception {
    String[] ruleId = ruleIds.split(",");
    for (String tmp : ruleId) {
      String update = "";
      //打开　
      if (isOpen) {
        update = "update "+ T9WorkFlowConst.FLOW_RULE +" SET status = 1 where SEQ_ID=" + tmp;
      } else {
        update = "update "+ T9WorkFlowConst.FLOW_RULE +" SET status = 0 where SEQ_ID=" + tmp;
      }
      Statement stmt = null;
      try {
        stmt = conn.createStatement();
        stmt.executeUpdate(update);
      }catch(Exception ex) {
        throw ex;
      }finally {
        T9DBUtility.close(stmt , null , null);
      }
    }
  }
  public void delAll(String ruleIds  , Connection conn) throws Exception {
    String[] ruleId = ruleIds.split(",");
    for (String tmp : ruleId) {
      this.delRule(tmp, conn);
    }
  }
  public String getRuleById(int ruleId  , Connection conn) throws Exception {
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer();
    T9DocFlowRule flowRule = (T9DocFlowRule) orm.loadObjSingle(conn, T9DocFlowRule.class, ruleId);
    sb.append("{");
    sb.append("flowId:" + flowRule.getFlowId());
    sb.append(",userId:" + flowRule.getUserId());
    sb.append(",toId:" + flowRule.getToId() );
    sb.append(",toName:'" + this.getUserName(flowRule.getToId(), conn) + "'"); 
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String bgDate = "";
    if (flowRule.getBeginDate() != null) {
      bgDate = format.format(flowRule.getBeginDate());
    }
    String endDate = "";
    if (flowRule.getEndDate() != null) {
      endDate = format.format(flowRule.getEndDate());
    }
    sb.append(",beginDate:'" + bgDate + "'");
    sb.append(",endDate:'" + endDate + "'" );
   
    sb.append("}");
    return sb.toString();
  }
  public void updateRule(int ruleId ,int userId, int toId, int flowId, String bgDate,
      String endDate, Connection conn) throws Exception {
    // TODO Auto-generated method stub
    String query = "update "+ T9WorkFlowConst.FLOW_RULE +" set TO_ID="+ toId +",FLOW_ID="+ flowId +",BEGIN_DATE=?,END_DATE=? where SEQ_ID=" + ruleId ;
    Date begindate =  null ;
    if (bgDate != null ){
      begindate = T9Utility.parseSqlDate(bgDate);
    }
    Date dEndDate =  null ;
    if (endDate != null ){
      dEndDate = T9Utility.parseSqlDate(endDate);
    }
    PreparedStatement stm3 = null;
    try {
      stm3 = conn.prepareStatement(query);
      stm3.setDate(1, begindate);
      stm3.setDate(2, dEndDate);
      stm3.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm3, null, null); 
    }
  }
  public String getList(Connection conn , int otherUser , Map request , boolean hasDelegate, String sortId) throws Exception{
    String result = "";
    String sql =  "select a.RUN_ID AS runId "
      + ",b.RUN_NAME AS runName " 
      + ",b.FLOW_ID AS FLOW_ID "
      + ",a.FLOW_PRCS AS FLOW_PRCS "
      + ",a.PRCS_FLAG AS PRCS_FLAG " ;
    if (hasDelegate) {
      sql += ",a.USER_ID AS USER_ID " ;
    } else {
      sql += ",a.OTHER_USER AS USER_ID " ;
    }
    sql += ",a.CREATE_TIME AS ruleTime " 
      + ",a.PRCS_ID AS PRCS_ID " 
      + " from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" a LEFT OUTER join "+ T9WorkFlowConst.FLOW_RUN +" b on a.RUN_ID = b.RUN_ID LEFT OUTER join "+ T9WorkFlowConst.FLOW_TYPE +" c on b.FLOW_ID = c.SEQ_ID  where  " 
      + " b.DEL_FLAG='0' ";
    if (hasDelegate) {
      sql += " AND a.OTHER_USER = " + otherUser  ;
    } else {
      sql += " AND a.USER_ID = "+ otherUser  +  " and a.OTHER_USER<>0 " ;
    }
    if (!"".equals(sortId)) {
      sortId = T9WorkFlowUtility.getOutOfTail(sortId);
      sql += " and c.flow_sort in (" + sortId + ") ";
    }
    sql  += " order by a.RUN_ID  DESC";
    
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    for (int i = 0 ; i < pageDataList.getRecordCnt() ; i ++) {
      T9DbRecord record = pageDataList.getRecord(i);
      Object oFlowPrcs = record.getValueByName("prcsName");
      if (oFlowPrcs != null) {
        long flowPrcs = T9Utility.cast2Long(oFlowPrcs);
        long flowId =  T9Utility.cast2Long(record.getValueByName("flowId"));
        String prcsName = this.getProcessName((int)flowId, (int)flowPrcs, conn);
        record.updateField("prcsName", prcsName);
      } else {
        record.updateField("prcsName", "");
      }
      Object oNowState = record.getValueByName("nowState");
      String state = "";
      int nowState =  Integer.parseInt((String)oNowState);
      switch(nowState) {
        case 1:
          state = "未接收";
          break;
        case 2:
          state = "办理中";
          break;
        case 3:
          state = "已办结";
          break;
        case 4:
          state = "已办结";
          break;
      }
      record.updateField("nowState", state);
      Object oToName = record.getValueByName("toName");
      long toId =  T9Utility.cast2Long(oToName);
      String userName = this.getUserName((int)toId, conn);
      record.updateField("toName", userName);
    }
    result = pageDataList.toJson();
    return result;
  }
}
