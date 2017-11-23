package t9.core.funcs.doc.receive.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.data.T9ExtDept;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
public class T9DocRegisterLogic{
  /*
   * 本部门的，和自己外来发文登记权限
   */
  public String getSendMesage2(T9Person user, Connection conn, Map request , String webroot, String isSign) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      String fromDeptName = request.get("fromDeptName") != null ? ((String[])request.get("fromDeptName"))[0] : null;
      String sendDocNo = request.get("sendDocNo") != null ? ((String[])request.get("sendDocNo"))[0] : null;
      String title = request.get("title") != null ? ((String[])request.get("title"))[0] : null;
      String endTime = request.get("endTime") != null ? ((String[])request.get("endTime"))[0] : null;
      String startTime = request.get("startTime") != null ? ((String[])request.get("startTime"))[0] : null;
      
      String deptIds = T9WorkFlowUtility.getInStr(user.getDeptIdOther());
      deptIds +=  "'" +user.getDeptId() + "'";
      
      T9DocUtility docUtility = new T9DocUtility();
      if (docUtility.usingEsb() && docUtility.haveEsbRecRight(user, conn)) {
        T9EsbClientConfig config = T9EsbClientConfig.builder(webroot + T9EsbConst.CONFIG_PATH) ;
        T9DeptTreeLogic logic = new T9DeptTreeLogic();
        T9ExtDept dept = logic.getDeptByEsbUser(conn, config.getUserId());
        if (!T9Utility.isNullorEmpty(deptIds)) {
          deptIds += ",'" ;
        }
        deptIds +=  dept.getDeptId() + "'";
      }
      if (T9Utility.isNullorEmpty(deptIds)) {
        sql += " AND 1<>1 ";
      } else {
        sql += " AND TO_DEPT IN (" + deptIds + ") ";
      }
      
      if (T9Utility.isNullorEmpty(isSign)) {
        isSign = "1";
      }
      if (!T9Utility.isNullorEmpty(title)) {
        sql += " and TITLE like '%" + T9DBUtility.escapeLike(title) + "%'";
      }
      if (!T9Utility.isNullorEmpty(sendDocNo)) {
        sql += " and DOC_FLOW_RUN.DOC like '%" + T9DBUtility.escapeLike(sendDocNo) + "%'";
      }
      if (!T9Utility.isNullorEmpty(fromDeptName)) {
        sql += " and (DEPARTMENT.DEPT_NAME like '%" + T9DBUtility.escapeLike(fromDeptName) + "%' OR SEND_UNIT like '%" + T9DBUtility.escapeLike(fromDeptName) + "%')";
      }
      if(startTime != null && !"".equals(startTime)){
        startTime +=  " 00:00:00";
        String dbDateF = T9DBUtility.getDateFilter("SIGN_TIME", startTime, " >= ");
        sql += " and " + dbDateF;
      }
      if(endTime != null && !"".equals(endTime)){
        endTime +=  " 23:59:59";
        String dbDateF = T9DBUtility.getDateFilter("SIGN_TIME", endTime, " <= ");
        sql += " and " + dbDateF;
      }
      
      sql += " AND STATUS = '" + isSign + "'";
      sql = "select"
        + " TITLE"
        + " , SEND_DOC_NO"
        + ", DOC_NAME"
        + ", DOC_ID"
        + " ,SEND_UNIT"
        + " ,SEND_TIME " 
        + " ,SIGN_TIME " 
        + " ,STATUS "
        + " ,IS_OUT "
        + " , DOC_SEND.SEQ_ID"
        + " , DEPT_ID"
        + " from DOC_SEND left outer join DEPARTMENT ON DEPARTMENT.SEQ_ID = DOC_SEND.DEPT_ID   where  IS_CANCEL='0' " +  sql;
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      T9DeptTreeLogic logic2 = new T9DeptTreeLogic();
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        int isOut = T9Utility.cast2Long(record.getValueByName("isOut")).intValue();
        String fromDept = (String)record.getValueByName("fromDeptId");
        String deptName = "";
        if (isOut == 1) {
          deptName = logic2.getDeptName(conn, fromDept);
          record.updateField("fromDept", deptName);
        } 
      }
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt.toString();
  }

  public String getRecReg(Connection conn, String rec_seqId) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer().append("{");
    Statement stm = null;
    ResultSet rs = null;
    String query = "select"
      + " TITLE"
      + " , SEND_DOC_NO"
      + ", DOC_NAME"
      + ", DOC_ID"
      + ", DEPT_ID"
      + ", IS_OUT"
      + " , IS_CANCEL"
      + ", SEND_UNIT"
      + ", SEND_TIME"
      + ", DOC_RUN.ATTACHMENT_NAME "
      + " , DOC_RUN.ATTACHMENT_ID"
      + " from DOC_SEND LEFT OUTER JOIN DOC_RUN ON DOC_RUN.RUN_ID = DOC_SEND.RUN_ID  where  DOC_SEND.seq_id = " + rec_seqId;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        sb.append("title:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("TITLE"))) + "\"");
        sb.append(",recDoc:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("SEND_DOC_NO"))) + "\"");
        sb.append(",recDocName:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("DOC_NAME"))) + "\"");
        sb.append(",recDocId:\"" + rs.getString("DOC_ID") + "\"");
        sb.append(",fromDeptId:\"" + rs.getString("DEPT_ID") + "\"");
        int isOut = rs.getInt("IS_OUT");
        String deptName = "";
        T9DeptTreeLogic logic2 = new T9DeptTreeLogic();
        if (isOut == 1) {
          deptName = logic2.getDeptName(conn, rs.getString("DEPT_ID"));
        } else {
          sb.append(",attachmentId:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("ATTACHMENT_ID"))) + "\"");
          sb.append(",attachmentName:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("ATTACHMENT_NAME"))) + "\"");
          deptName = T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("SEND_UNIT")));
        }
        sb.append(",fromDeptName:\"" + deptName + "\"");
        sb.append(",sendTime:\"" + T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME")) + "\"");
        sb.append(",isCancel:\"" + rs.getString("IS_CANCEL") + "\"");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    sb.append("}");
    return sb.toString();
  }

  public int getMaxOrderNo(Connection conn, String type) throws Exception {
    // TODO Auto-generated method stub
    Statement stm = null;
    ResultSet rs = null;
    int result = 0 ;
    String query = "select max(REC_NO) from doc_rec_register   where REC_TYPE=  '" + type + "'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return result;
  }

  public void register(Connection conn, String recId, String recType,
      String recNo, String fromDeptName, String fromDeptId,
      String secretsLevel, String sendDocNo, String title, String copies,
      String recDocId, String recDocName, String attachmentId,
      String attachmentName , int userId) throws Exception {
    // TODO Auto-generated method stub
    String update = "insert into doc_rec_register (REC_ID, FROM_DEPT_ID, FROM_DEPT_NAME, REC_DOC_NAME, REC_DOC_ID, REC_TYPE, REC_NO, TITLE, REGISTER_USER, ATTACHMENT_ID, ATTACHMENT_NAME, COPIES, SEND_DOC_NO, SECRETS_LEVEL,REGISTER_TIME)" 
      + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement stm = null; 
    try { 
      Timestamp time =  new  Timestamp(new Date().getTime());
      stm = conn.prepareStatement(update);
      if (T9Utility.isNullorEmpty(recId)) {
        recId = "0";
      }
      stm.setInt(1, Integer.parseInt(recId));
      stm.setString(2, fromDeptId);
      stm.setString(3, fromDeptName);
      stm.setString(4, recDocName);
      stm.setString(5, recDocId);
      stm.setString(6, recType);
      stm.setInt(7,  Integer.parseInt(recNo));
      stm.setString(8, title);
      stm.setInt(9, userId);
      stm.setString(10, attachmentId);
      stm.setString(11, attachmentName);
      stm.setInt(12, Integer.parseInt( copies));
      stm.setString(13, sendDocNo);
      stm.setString(14, secretsLevel);
      stm.setTimestamp(15 , time);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    } 
  }

  public void update(Connection conn, String seqId, String recId,
      String recType, String recNo, String fromDeptName, String fromDeptId,
      String secretsLevel, String sendDocNo, String title, String copies,
      String recDocId, String recDocName, String attachmentId,
      String attachmentName) throws Exception {
    // TODO Auto-generated method stub
    String update = "update doc_rec_register set  FROM_DEPT_NAME=?, REC_NO=?, TITLE=?, ATTACHMENT_ID=?, ATTACHMENT_NAME=?, COPIES=?, SEND_DOC_NO=?, SECRETS_LEVEL=?, REC_TYPE=?" 
      + " where seq_Id= " + seqId;
    PreparedStatement stm = null; 
    try { 
      stm = conn.prepareStatement(update);
      stm.setString(1, fromDeptName);
      
      stm.setInt(2,  Integer.parseInt(recNo));
      stm.setString(3, title);
      stm.setString(4, attachmentId);
      stm.setString(5, attachmentName);
      stm.setInt(6, Integer.parseInt( copies));
      stm.setString(7, sendDocNo);
      stm.setString(8, secretsLevel);
      stm.setString(9, recType);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void updateStatus(Connection dbConn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    String update = "UPDATE DOC_SEND SET STATUS = '2'  WHERE SEQ_ID = " + seqId ;
    PreparedStatement stm = null; 
    try { 
      stm = dbConn.prepareStatement(update);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    } 
  }
  /**
   * 查询
   * @param user
   * @param conn
   * @param request
   * @param realPath
   * @return
   * @throws Exception
   */
  public String queryRegList(T9Person user, Connection conn, Map request,
      String realPath) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      String fromDeptName = request.get("fromDeptName") != null ? ((String[])request.get("fromDeptName"))[0] : null;
      String sendDocNo = request.get("sendDocNo") != null ? ((String[])request.get("sendDocNo"))[0] : null;
      String title = request.get("title") != null ? ((String[])request.get("title"))[0] : null;
      String endTime = request.get("endTime") != null ? ((String[])request.get("endTime"))[0] : null;
      String startTime = request.get("startTime") != null ? ((String[])request.get("startTime"))[0] : null;
      String secretsLevel = request.get("secretsLevel") != null ? ((String[])request.get("secretsLevel"))[0] : null;
      String recType = request.get("recType") != null ? ((String[])request.get("recType"))[0] : null;
      String registerStatus = request.get("registerStatus") != null ? ((String[])request.get("registerStatus"))[0] : null;
      
      
      sql = "select"
        + " TITLE"
        + " , SEND_DOC_NO"
        + ", REC_DOC_NAME"
        + ", REC_DOC_ID"
        + " ,FROM_DEPT_NAME "
        + ", SECRETS_LEVEL"
        + " ,REC_TYPE " 
        + " ,REGISTER_TIME "
        + " , 1"
        + " , DELETE_FLAG"
        + " ,doc_rec_register.SEQ_ID "
        + " , doc_rec_register.RUN_ID"
        + ", 1"
        + ", REC_NO"
        + " , DOC_RUN.FLOW_ID"
        + " , COPIES"
        + " from doc_rec_register left outer join DOC_RUN ON doc_rec_register.RUN_ID = DOC_RUN.RUN_ID   where register_user = '" + user.getSeqId() + "'";
     
      if (!T9Utility.isNullorEmpty(recType)) {
        sql += " and REC_TYPE like '%" + T9DBUtility.escapeLike(recType) + "%'";
      }
      if (!T9Utility.isNullorEmpty(secretsLevel)) {
        sql += " and SECRETS_LEVEL like '%" + T9DBUtility.escapeLike(secretsLevel) + "%'";
      }
      if (!T9Utility.isNullorEmpty(title)) {
        sql += " and TITLE like '%" + T9DBUtility.escapeLike(title) + "%'";
      }
      if (!T9Utility.isNullorEmpty(fromDeptName)) {
        sql += " and FROM_DEPT_NAME like '%" + T9DBUtility.escapeLike(fromDeptName) + "%'";
      }
      if (!T9Utility.isNullorEmpty(sendDocNo)) {
        sql += " and SEND_DOC_NO like '%" + T9DBUtility.escapeLike(sendDocNo) + "%'";
      }
      if(startTime != null && !"".equals(startTime)){
        startTime +=  " 00:00:00";
        String dbDateF = T9DBUtility.getDateFilter("REGISTER_TIME", startTime, " >= ");
        sql += " and " + dbDateF;
      }
      if(endTime != null && !"".equals(endTime)){
        endTime +=  " 23:59:59";
        String dbDateF = T9DBUtility.getDateFilter("REGISTER_TIME", endTime, " <= ");
        sql += " and " + dbDateF;
      }
      if (!T9Utility.isNullorEmpty(registerStatus)) {
        if ("1".equals(registerStatus)) {
          sql += " and DELETE_FLAG = '1'";
        } else {
          sql += " and (DELETE_FLAG <> '1' or DELETE_FLAG IS NULL)";
        }
      }
      sql += " order by REGISTER_TIME desc";
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        int runId = T9Utility.cast2Long(record.getValueByName("runId")).intValue();
        int flowId = T9Utility.cast2Long(record.getValueByName("flowId")).intValue();
        if (runId == 0) {
          record.updateField("status", "未办理");
        } else {
          record.updateField("status", this.getPrcsName(conn, runId , flowId));
        }
        record.updateField("runEnd", this.isRunEnd(conn, runId));
      }
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt.toString();
  }
  public String getRegList(T9Person user, Connection conn, Map request,
      String realPath) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      String type = ((String[])request.get("type"))[0];
      String fromDeptName = request.get("fromDeptName") != null ? ((String[])request.get("fromDeptName"))[0] : null;
      String sendDocNo = request.get("sendDocNo") != null ? ((String[])request.get("sendDocNo"))[0] : null;
      String title = request.get("title") != null ? ((String[])request.get("title"))[0] : null;
      String endTime = request.get("endTime") != null ? ((String[])request.get("endTime"))[0] : null;
      String startTime = request.get("startTime") != null ? ((String[])request.get("startTime"))[0] : null;
      String secretsLevel = request.get("secretsLevel") != null ? ((String[])request.get("secretsLevel"))[0] : null;
      String recType = request.get("recType") != null ? ((String[])request.get("recType"))[0] : null;
      
      sql = "select"
        + " TITLE"
        + " , SEND_DOC_NO"
        + ", REC_DOC_NAME"
        + ", REC_DOC_ID"
        + " ,FROM_DEPT_NAME "
        + ", SECRETS_LEVEL"
        + " ,REC_TYPE " 
        + " ,REGISTER_TIME "
        + " , 1"
        + " ,doc_rec_register.SEQ_ID "
        + " , doc_rec_register.RUN_ID"
        + ", 1"
        + ", REC_NO"
        + " , DOC_RUN.FLOW_ID"
        + " , COPIES"
        + " from doc_rec_register left outer join DOC_RUN ON doc_rec_register.RUN_ID = DOC_RUN.RUN_ID   where register_user = '" + user.getSeqId() + "'";
      if ("1".equals(type)) {
        sql += " and (doc_rec_register.RUN_ID = 0 or  doc_rec_register.RUN_ID IS NULL)";
      } else if ("2".equals(type)) {
        sql += " and (doc_rec_register.RUN_ID <> 0 or doc_rec_register.RUN_ID IS not NULL) and  DOC_RUN.END_TIME IS  NULL";
      } else if ("3".equals(type)) {
        sql += " and DOC_RUN.END_TIME IS NOT NULL";
      } 
      if (!T9Utility.isNullorEmpty(recType)) {
        sql += " and REC_TYPE like '%" + T9DBUtility.escapeLike(recType) + "%'";
      }
      if (!T9Utility.isNullorEmpty(secretsLevel)) {
        sql += " and SECRETS_LEVEL like '%" + T9DBUtility.escapeLike(secretsLevel) + "%'";
      }
      if (!T9Utility.isNullorEmpty(title)) {
        sql += " and TITLE like '%" + T9DBUtility.escapeLike(title) + "%'";
      }
      if (!T9Utility.isNullorEmpty(fromDeptName)) {
        sql += " and FROM_DEPT_NAME like '%" + T9DBUtility.escapeLike(fromDeptName) + "%'";
      }
      if (!T9Utility.isNullorEmpty(sendDocNo)) {
        sql += " and SEND_DOC_NO like '%" + T9DBUtility.escapeLike(sendDocNo) + "%'";
      }
      if(startTime != null && !"".equals(startTime)){
        startTime +=  " 00:00:00";
        String dbDateF = T9DBUtility.getDateFilter("REGISTER_TIME", startTime, " >= ");
        sql += " and " + dbDateF;
      }
      if(endTime != null && !"".equals(endTime)){
        endTime +=  " 23:59:59";
        String dbDateF = T9DBUtility.getDateFilter("REGISTER_TIME", endTime, " <= ");
        sql += " and " + dbDateF;
      }
      sql += " and (DELETE_FLAG <> '1' OR DELETE_FLAG IS NULL)";
      sql += " order by REGISTER_TIME desc";
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        int runId = T9Utility.cast2Long(record.getValueByName("runId")).intValue();
        int flowId = T9Utility.cast2Long(record.getValueByName("flowId")).intValue();
        if (runId == 0) {
          record.updateField("status", "未办理");
        } else {
          record.updateField("status", this.getPrcsName(conn, runId , flowId));
        }
        record.updateField("runEnd", this.isRunEnd(conn, runId));
      }
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt.toString();
  }
  public String getPrcsName(Connection conn , int runId , int flowId) throws Exception {
    String prcsName = "";
    String flowType = "1";
    Statement stm = null;
    ResultSet rs = null;
    String query = "select flow_type from doc_flow_type, DOC_RUN  where FLOW_ID = doc_flow_type.seq_id AND  run_Id=  '" + runId + "'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        flowType = rs.getString("flow_type");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    
    Statement stm2 = null;
    ResultSet rs2 = null;
    int prcsId = 0 ;
    List<Integer> flowPrcs = new ArrayList();
    String query2 = "select PRCS_ID, FLOW_PRCS from DOC_FLOW_RUN_PRCS  where run_Id=  '" + runId + "' AND PRCS_ID = (SELECT MAX(PRCS_ID) FROM DOC_FLOW_RUN_PRCS WHERE run_Id=  '" + runId + "')";
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query2);
      while (rs2.next()) {
        prcsId = rs2.getInt("PRCS_ID");
        flowPrcs.add(rs2.getInt("FLOW_PRCS"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null);
    }
    if ("2".equals(flowType)) {
      return "自由流程：第" + prcsId + "步"; 
    } else {
      return this.getPrcsName(flowPrcs, flowId, conn);
    }
  }
  public String getPrcsName(List<Integer> flowPrcs , int flowId , Connection conn) throws Exception {
    Statement stm2 = null;
    ResultSet rs2 = null;
    String name = "";
    String str = "";
    for (Integer i : flowPrcs) {
      str += "'" + i + "',";
    }
    if (str.endsWith("," ))
      str = str.substring(0, str.length() - 1);
    String query2 = "select PRCS_NAME from DOC_FLOW_PROCESS  where FLOW_SEQ_ID=  '" + flowId + "' AND PRCS_ID IN ("+str+")";
    try {
      stm2 = conn.createStatement();
      rs2 = stm2.executeQuery(query2);
      while (rs2.next()) {
        name += rs2.getString("PRCS_NAME") + ",";
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm2, rs2, null);
    }
    if (name.endsWith(",")) 
      name = name.substring(0, name.length() - 1);
    if (flowPrcs.size() > 1) {
      return "并发：" + name;
    }
    return name;
  }
  public String isRunEnd(Connection conn , int runId) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    String query = "select 1 from DOC_RUN  where run_Id=  '" + runId + "' AND END_TIME IS NOT NULL";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        return "1";
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    return "0";
  }
  public String[] getAttach(Connection conn , String seqId ) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    String[] result = new String[2];
    String query = "select attachment_name , attachment_id , REC_DOC_NAME  , REC_DOC_ID from doc_rec_register   where SEQ_ID=  '" + seqId + "'";
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        result[1] = rs.getString(1);
        result[0] = rs.getString(2);
        
          if (T9Utility.isNullorEmpty(result[1]) || result[1].endsWith("*")) {
            result[1] += T9Utility.null2Empty(rs.getString(3));
          } else {
            result[1] += "*" + T9Utility.null2Empty(rs.getString(3));
          }
          if (T9Utility.isNullorEmpty(result[0]) ||result[0].endsWith(",")) {
            result[0] += T9Utility.null2Empty(rs.getString(4));
          } else {
            result[0] += "," + T9Utility.null2Empty(rs.getString(4));
          }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    
    return result;
  }
  public void delRun(Connection conn, int runId) throws Exception {
    String sql = "update doc_rec_register set RUN_ID = null  where RUN_ID='" + runId + "'";
    PreparedStatement ps = null;
    try{
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (SQLException e){      
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  public String getRecRegBySeqId(Connection conn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    String query = "select * from doc_rec_register where SEQ_ID=  '" + seqId + "'";
    StringBuffer sb = new StringBuffer().append("{");
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        sb.append("title:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("TITLE"))) + "\"");
        sb.append(",sendDocNo:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("SEND_DOC_NO"))) + "\"");
        sb.append(",copies:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("COPIES"))) + "\"");
        sb.append(",recNo:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("REC_NO"))) + "\"");
        sb.append(",recDocName:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("REC_DOC_NAME"))) + "\"");
        sb.append(",recDocId:\"" + rs.getString("REC_DOC_ID") + "\"");
        sb.append(",attachmentName:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("ATTACHMENT_NAME"))) + "\"");
        sb.append(",attachmentId:\"" + rs.getString("ATTACHMENT_ID") + "\"");
        sb.append(",recType:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("REC_TYPE"))) + "\"");
        sb.append(",secretsLevel:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("SECRETS_LEVEL"))) + "\"");
        sb.append(",fromDeptName:\"" + T9Utility.null2Empty(T9Utility.encodeSpecial(rs.getString("FROM_DEPT_NAME"))) + "\"");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null);
    }
    sb.append("}");
    return sb.toString();
  }

  public void delRegister(Connection dbConn,String seqId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "update doc_rec_register set DELETE_FLAG = '1' where SEQ_ID = "+ seqId;
    PreparedStatement stm = null; 
    try { 
      stm = dbConn.prepareStatement(sql);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    } 
  }

  public void restoreDelRegister(Connection dbConn,String seqId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "update doc_rec_register set DELETE_FLAG = '0' where SEQ_ID = "+ seqId;
    PreparedStatement stm = null; 
    try { 
      stm = dbConn.prepareStatement(sql);
      stm.executeUpdate();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, null, null); 
    } 
  }
}
