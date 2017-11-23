package t9.subsys.oa.vehicle.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.vehicle.data.T9VehicleUsage;

public class T9VehicleUsageLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void addVehicleUsage(Connection dbConn, T9VehicleUsage vcusage) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, vcusage);  
  }
  public ArrayList<Map<String,String>> selectVehicleApply(Connection dbConn, String status) throws Exception{
    ArrayList<Map<String,String>> vehicleapplyList = new ArrayList<Map<String,String>>();
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select vc.VC_NUM as VC_NUM ,vu.VC_ID as VC_ID,vu.SEQ_ID as SEQ_ID,vu.VU_REQUEST_DATE as VU_REQUEST_DATE,vu.VU_PROPOSE as VU_PROPOSE," 
      + "vu.VU_USER as VU_USER ,vu.VU_START as VU_START,vu.VU_END as VU_END,vu.VU_MILEAGE as VU_MILEAGE,vu.VU_STATUS as VU_STATUS,vu.VU_DEPT as VU_DEPT,"
      + "vu.VU_DESTINATION as VU_DESTINATION,vu.VC_DRIVER as VC_DIRVER,vu.VU_OPERATOR as VU_OPERATOR,vu.DEPT_MANAGER as DEPT_MANAGER,"
      + "vu.VU_REASON as VU_REASON,vu.VU_REMARK as VU_REMARK,vu.SMS_REMIND as SMS_REMIND,vu.SMS_NOTIFY as SMS_NOTIFY,vu.SHOW_FLAG as SHOW_FLAG,vu.DMER_STATUS as DMER_STATUS"
      + " from VEHICLE vc,VC_USAGE vu where vu.VC_ID = vc.SEQ_ID and vu.VU_STATUS = " + status;
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        String seqId = rs.getString("SEQ_ID");
        String vcId  = rs.getString("VC_ID");
        String vcNum  = rs.getString("VC_NUM");
        String vuRequestDate = rs.getString("VU_REQUEST_DATE");
        String vuPropose = rs.getString("VU_PROPOSE");
        String vuUser = rs.getString("VU_USER");
        String vuStart = rs.getString("VU_START");
        String vuEnd = rs.getString("VU_END");
        String vuMileage = rs.getString("VU_MILEAGE");
        String vuStatus  = rs.getString("VU_STATUS");
        String vuDept  = rs.getString("VU_DEPT");
        String vuDestination = rs.getString("VU_DESTINATION");
        String vc_Driver = rs.getString("VC_DIRVER");
        String vuOperator = rs.getString("VU_OPERATOR");
        String deptManager = rs.getString("DEPT_MANAGER");
        String vuReason = rs.getString("VU_REASON");
        String vuRemark = rs.getString("VU_REMARK");
        String smsRemind = rs.getString("SMS_REMIND");
        String smsNotify = rs.getString("SMS_NOTIFY");
        String showFlag = rs.getString("SHOW_FLAG");
        String dmerStatus = rs.getString("DMER_STATUS");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vuRequestDate = dateFormat.format(T9Utility.parseDate(vuRequestDate));
        vuStart = vuRequestDate = dateFormat.format(T9Utility.parseDate(vuStart));
        vuEnd = vuRequestDate = dateFormat.format(T9Utility.parseDate(vuEnd));
        //System.out.println(vcId+"----------------->");
        map.put("seqId", seqId);
        map.put("vcId", vcId);  
        map.put("vcNum", vcNum);
        map.put("vuRequestDate", vuRequestDate);
        map.put("vuPropose", vuPropose);
        map.put("vuUser", vuUser);  
        map.put("vuStart", vuStart);
        map.put("vuEnd", vuEnd);
        map.put("vuMileage", vuMileage);
        map.put("vuStatus", vuStatus);  
        map.put("vuDept", vuDept);
        map.put("vuDestination", vuDestination);
        map.put("vc_Driver", vc_Driver);
        map.put("vuOperator", vuOperator);  
        map.put("deptManager", deptManager);
        map.put("vuReason", vuReason);
        map.put("vuRemark", vuRemark);
        map.put("smsRemind", smsRemind);  
        map.put("smsNotify", smsNotify);
        map.put("showFlag", showFlag);
        map.put("dmerStatus", dmerStatus);
        vehicleapplyList.add(map);
      }
      return vehicleapplyList;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public Map<String,String> selectVehicleApplyById(Connection dbConn, String seqIds) throws Exception{
    Map<String,String>  map = new HashMap<String,String>();
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select vc.VC_NUM as VC_NUM ,vu.VC_ID as VC_ID,vu.SEQ_ID as SEQ_ID,vu.VU_REQUEST_DATE as VU_REQUEST_DATE,vu.VU_PROPOSE as VU_PROPOSE," 
      + "vu.VU_USER as VU_USER ,vu.VU_START as VU_START,vu.VU_END as VU_END,vu.VU_MILEAGE as VU_MILEAGE,vu.VU_STATUS as VU_STATUS,vu.VU_DEPT as VU_DEPT,"
      + "vu.VU_DESTINATION as VU_DESTINATION,vu.VC_DRIVER as VC_DIRVER,vu.VU_OPERATOR as VU_OPERATOR,vu.DEPT_MANAGER as DEPT_MANAGER,"
      + "vu.VU_REASON as VU_REASON,vu.VU_REMARK as VU_REMARK,vu.SMS_REMIND as SMS_REMIND,vu.SMS_NOTIFY as SMS_NOTIFY,vu.SHOW_FLAG as SHOW_FLAG,vu.DMER_STATUS as DMER_STATUS"
      + " from VEHICLE vc,VC_USAGE vu where vu.VC_ID = vc.SEQ_ID and vu.SEQ_ID = " + seqIds;
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        String seqId = rs.getString("SEQ_ID");
        String vcId  = rs.getString("VC_ID");
        String vcNum  = rs.getString("VC_NUM");
        String vuRequestDate = rs.getString("VU_REQUEST_DATE");
        String vuPropose = rs.getString("VU_PROPOSE");
        String vuUser = rs.getString("VU_USER");
        String vuStart = rs.getString("VU_START");
        String vuEnd = rs.getString("VU_END");
        String vuMileage = rs.getString("VU_MILEAGE");
        String vuStatus  = rs.getString("VU_STATUS");
        String vuDept  = rs.getString("VU_DEPT");
        String vuDestination = rs.getString("VU_DESTINATION");
        String vc_Driver = rs.getString("VC_DIRVER");
        String vuOperator = rs.getString("VU_OPERATOR");
        String deptManager = rs.getString("DEPT_MANAGER");
        String vuReason = rs.getString("VU_REASON");
        String vuRemark = rs.getString("VU_REMARK");
        String smsRemind = rs.getString("SMS_REMIND");
        String smsNotify = rs.getString("SMS_NOTIFY");
        String showFlag = rs.getString("SHOW_FLAG");
        String dmerStatus = rs.getString("DMER_STATUS");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vuRequestDate = dateFormat.format(T9Utility.parseDate(vuRequestDate));
        vuStart = vuRequestDate = dateFormat.format(T9Utility.parseDate(vuStart));
        vuEnd = vuRequestDate = dateFormat.format(T9Utility.parseDate(vuEnd));
        //System.out.println(vcId+"----------------->");
        map.put("seqId", seqId);
        map.put("vcId", vcId);  
        map.put("vcNum", vcNum);
        map.put("vuRequestDate", vuRequestDate);
        map.put("vuPropose", vuPropose);
        map.put("vuUser", vuUser);  
        map.put("vuStart", vuStart);
        map.put("vuEnd", vuEnd);
        map.put("vuMileage", vuMileage);
        map.put("vuStatus", vuStatus);  
        map.put("vuDept", vuDept);
        map.put("vuDestination", vuDestination);
        map.put("vc_Driver", vc_Driver);
        map.put("vuOperator", vuOperator);  
        map.put("deptManager", deptManager);
        map.put("vuReason", vuReason);
        map.put("vuRemark", vuRemark);
        map.put("smsRemind", smsRemind);  
        map.put("smsNotify", smsNotify);
        map.put("showFlag", showFlag);
        map.put("dmerStatus", dmerStatus);
        return map;
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return map;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String vehicleSelect(Connection dbConn,Map request,String status,String seqId) throws Exception {
    //  left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where 1=1  
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM,pe.USER_NAME,veus.VU_REASON,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_DESTINATION,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where 1=1 and (veus.VU_PROPOSER=" + seqId + " or veus.VU_USER=" + seqId + ")";
    if (!T9Utility.isNullorEmpty(status) && !status.equals("3")) { 
      sql += " and veus.VU_STATUS='" + status + "' and veus.dmer_status <> 3 ";
    }
    if (status.equals("3")) {
      sql += " and (veus.dmer_status=3 or veus.VU_STATUS=3)";
    }
    sql += " order by veus.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    pageDataList=this.getHookQuery(dbConn, pageDataList);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String vehicleQuery(Connection dbConn,Map request,String status) throws Exception {
    //  left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where 1=1  
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM,pe.USER_NAME,veus.VU_REASON,veus.VU_DESTINATION,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where 1=1 ";
    if (!T9Utility.isNullorEmpty(status) && !status.equals("3")) { 
      sql += " and veus.VU_STATUS=" + status + " and veus.dmer_status <> 3 ";
    }
    if (status.equals("3")) {
      sql += " and (veus.dmer_status=3 or veus.VU_STATUS=3)";
    }
    sql += " order by veus.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
  
    return pageDataList.toJson();
  }

  public T9PageDataList getHookQuery(Connection dbConn,T9PageDataList pageDataList)throws Exception{
    try{
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      
      for(int i=0;i < pageDataList.getRecordCnt();i++){
        T9DbRecord record=pageDataList.getRecord(i);
        String seqId= ""+record.getValueByName("seqId");
        int runId = fu.isRunHook(dbConn, "VU_ID", seqId);
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        record.addField("isHookRun", runId);
        record.addField("flowId", flowId);
      }      
    }catch(Exception ex) {
      throw ex;
    }
    return pageDataList;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String vehicleAll(Connection dbConn,Map request,T9VehicleUsage usage,String vuRequestDateMax,String vuStartMax,String vuEndMax) throws Exception {
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM,pe.USER_NAME,veus.VU_REASON,veus.VU_DESTINATION,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_OPERATOR,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER where 1=1 ";
    if (!T9Utility.isNullorEmpty(usage.getVuStatus())) {
      sql += " and veus.VU_STATUS=" + usage.getVuStatus();
    }
    if (!T9Utility.isNullorEmpty(usage.getVId())) {
      sql += " and veus.V_ID=" + usage.getVId();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuDriver())) {
      sql += " and veus.VU_DRIVER=" + usage.getVuDriver();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuUser())) {
      sql += " and veus.vu_user=" + usage.getVuUser();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuDept())) {
      sql += " and veus.VU_DEPT=" + usage.getVuDept();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuProposer())) {
      sql += " and veus.VU_PROPOSER=" + usage.getVuProposer();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuReason())) {
      sql += " and veus.vu_reason like '%" + T9DBUtility.escapeLike(usage.getVuReason()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuRemark())) {
      sql += " and veus.VU_REMARK like '%" + T9DBUtility.escapeLike(usage.getVuRemark()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(usage.getVuOperator())) {
      sql += " and veus.VU_OPERATOR=" + usage.getVuOperator();
    }
    if (usage.getVuRequestDate() != null) {
      String str =  T9DBUtility.getDateFilter("veus.vu_request_date", T9Utility.getDateTimeStr(usage.getVuRequestDate()), ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(vuRequestDateMax)) {
      String str =  T9DBUtility.getDateFilter("veus.vu_request_date", T9Utility.getDateTimeStr(T9Utility.parseDate(vuRequestDateMax)), "<=");
      sql += " and " + str;
    }
    if (usage.getVuStart() != null) {
      String str =  T9DBUtility.getDateFilter("veus.VU_START", T9Utility.getDateTimeStr(usage.getVuStart()), ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(vuStartMax)) {
      String str =  T9DBUtility.getDateFilter("veus.VU_START", T9Utility.getDateTimeStr(T9Utility.parseDate(vuStartMax)), "<=");
      sql += " and " + str;
    }
    if (usage.getVuEnd() != null) {
      String str =  T9DBUtility.getDateFilter("veus.VU_END", T9Utility.getDateTimeStr(usage.getVuEnd()), ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(vuEndMax)) {
      String str =  T9DBUtility.getDateFilter("veus.VU_END", T9Utility.getDateTimeStr(T9Utility.parseDate(vuEndMax)), "<=");
      sql += " and " + str;
    }
    sql += " order by veus.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 取得最大的seqId-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static int getMaxSeqId(Connection dbConn) throws Exception{
    int result = 0;
    String sql = "select max(SEQ_ID) from vehicle_usage";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

  /**
   * 取得最大的seqId
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String getSeqIdStr(Connection dbConn) throws Exception{
    String result = "0";
    String sql = "select operator_id from vehicle_operator";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString("operator_id");
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  /**
   * 修改status=4 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateStatus(Connection dbConn,int seqId) throws Exception{
    String sql = "update vehicle_usage set vu_status=4 where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,seqId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 修改 dmer_status -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateDmerStatus(Connection dbConn,int seqId,String dmerStatus) throws Exception{
    String sql = "";
    String showFlag = "1";
    if (!dmerStatus.equals("3")) {
      if (dmerStatus.equals("0")) {
        showFlag = "0";
      }
      sql = "update vehicle_usage set dmer_status='" + dmerStatus +"',show_flag='" + showFlag +"'  where seq_id=" + seqId;
    }else {
      sql = "update vehicle_usage set dmer_status='" + dmerStatus +"' where seq_id=" + seqId;
    }
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }

  /**
   * 修改 vu_status -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateStatus(Connection dbConn,int seqId,String status) throws Exception{
    String sql = "update vehicle_usage set vu_status=? where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,status);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }

  /**
   * 查询 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String selectDeptReason(Connection dbConn,int seqId) throws Exception{
    String deptReason = "";
    String sql = "select dept_reason from vehicle_usage where seq_id=?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,seqId);
      rs = ps.executeQuery();
      if (rs.next()) {
        deptReason = rs.getString("dept_reason");
      }
      if (T9Utility.isNullorEmpty(deptReason)) {
        deptReason = "";
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
    return deptReason;
  }

  /**
   * 查询 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String operatorReason(Connection dbConn,int seqId) throws Exception{
    String operatorReason = "";
    String sql = "select operator_reason from vehicle_usage where seq_id=?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,seqId);
      rs = ps.executeQuery();
      if (rs.next()) {
        operatorReason = rs.getString("operator_reason");
      }
      if (T9Utility.isNullorEmpty(operatorReason)) {
        operatorReason = "";
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
    return operatorReason;
  }
  /**
   * 修改原因 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateDeptReason(Connection dbConn,int seqId,String deptReason) throws Exception{
    String sql = "update vehicle_usage set dept_reason=? where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,deptReason);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 修改原因 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateoPeratorReason(Connection dbConn,int seqId,String peratorReason) throws Exception{
    String sql = "update vehicle_usage set operator_reason=? where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setString(1,peratorReason);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页 -lz
   * @return
   * @throws Exception 
   */
  public String vehicleDept(Connection dbConn,Map request,String dmerStatus,String seqId) throws Exception {
    //  left outer join budget_Apply bu on bu.seq_Id=p.BUDGET_ID where 1=1  
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM,pe.USER_NAME,veus.VU_START,veus.VU_END"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_REASON,veus.VU_REMARK,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_DESTINATION,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where 1=1 and veus.DEPT_MANAGER=" + seqId;
    if (!T9Utility.isNullorEmpty(dmerStatus)) { 
      sql += " and veus.dmer_status=" + dmerStatus;
    }
    sql += " order by veus.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    pageDataList=this.getHookQuery(dbConn, pageDataList);
    return pageDataList.toJson();
  }

  /**
   * 按状态查询进行自动使用和自动回收-syl
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public List<T9VehicleUsage> selectVUByVuStatus(Connection dbConn,String vuStatus , String starEnd,String date,String opt) throws Exception{
    String sql = "select SEQ_ID,V_ID from VEHICLE_USAGE where VU_STATUS = '" + vuStatus + "'";
    if(!T9Utility.isNullorEmpty(starEnd)&&!T9Utility.isNullorEmpty(date)&&!T9Utility.isNullorEmpty(opt)){
      sql = sql + " and " + T9DBUtility.getDateFilter(starEnd, date, opt) ;
    }
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9VehicleUsage> usageList = new ArrayList<T9VehicleUsage>();
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        T9VehicleUsage usage = new T9VehicleUsage();
        usage.setSeqId(rs.getInt(1));
        usage.setVId(rs.getString("V_ID"));
        usageList.add(usage);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
    return usageList;
  }
  /**自动使用和自动回收
   * 修改VU_STATUS-syl
   * @param conn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateVuStatus(Connection dbConn,int seqId,String vuStatus) throws Exception{
    String sql = "update vehicle_usage set vu_status=? where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, vuStatus);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 按状态查询进行自动使用和自动回收-syl
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public List<T9VehicleUsage>  selectVUByVuStatus(Connection dbConn, String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9VehicleUsage> usageList = new ArrayList<T9VehicleUsage>();
    usageList = orm.loadListSingle(dbConn, T9VehicleUsage.class, str);
    return usageList;
  }
  /**
   * 查询多少预约情况-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static List<T9VehicleUsage> showDetailSize(Connection dbConn,String[] str) throws Exception{
    T9ORM orm = new T9ORM();
    List<T9VehicleUsage> usageList = new ArrayList<T9VehicleUsage>();
    usageList = orm.loadListSingle(dbConn,T9VehicleUsage.class,str);
    return usageList;
  }

  /**
   * 查询预约情况-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static List<T9VehicleUsage>  showDetail(Connection dbConn, String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9VehicleUsage> usageList = new ArrayList<T9VehicleUsage>();
    usageList = orm.loadListSingle(dbConn, T9VehicleUsage.class, str);
    return usageList;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String selectVID(Connection dbConn,Map request,int vId) throws Exception {
    String sql = "select veus.SEQ_ID,veus.VU_STATUS,ve.V_NUM,pe.USER_NAME,veus.VU_REASON,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.dmer_status,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_DESTINATION,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where veus.V_ID='" + vId +"' and veus.VU_STATUS <> '4' order by veus.VU_STATUS,veus.VU_START,veus.SEQ_ID desc";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String showDetailAll(Connection dbConn,Map request) throws Exception {
    String sql = "select veus.SEQ_ID,ve.V_NUM,veus.VU_STATUS,pe.USER_NAME,veus.VU_REASON,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.dmer_status,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_DESTINATION,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where veus.VU_STATUS <> '4'  order by veus.VU_STATUS,veus.VU_START,veus.SEQ_ID desc";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页-lz
   * @return
   * @throws Exception 
   */
  public String useManage(Connection dbConn,Map request,String vuStatus,String userPriv,String personId) throws Exception {
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM,pe.USER_NAME,veus.VU_START,veus.VU_END"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_REASON,veus.VU_REMARK,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_DESTINATION,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER "
      + " where 1=1 ";
    if (userPriv.equals("1")) {
      //sql += " and (veus.SHOW_FLAG=1 or (veus.dept_manager is not null and veus.dmer_status=1))";
      sql += " and veus.SHOW_FLAG=1 ";
    }
    if (!userPriv.equals("1")) {
      sql += "and veus.VU_OPERATOR='" + personId + "'" + " and veus.SHOW_FLAG=1 ";
    }
    if (!T9Utility.isNullorEmpty(vuStatus)) { 
      sql += " and veus.VU_STATUS='" + vuStatus + "'";
    }
    sql += " order by veus.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    
    pageDataList=this.getHookQuery(dbConn, pageDataList);
    return pageDataList.toJson();
  }

  /**
   * 查询-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static T9VehicleUsage selectNotes(Connection dbConn,int seqId) throws Exception{
    String deptReason = "";
    String sql = "select vu_mileage_true,vu_parking_fees from vehicle_usage where seq_id=?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    T9VehicleUsage usage = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,seqId);
      rs = ps.executeQuery();
      if (rs.next()) {
        usage = new T9VehicleUsage();
        usage.setVuMileageTrue(rs.getInt("vu_mileage_true"));
        usage.setVuParkingFees(rs.getInt("vu_parking_fees"));
      }
      if (T9Utility.isNullorEmpty(deptReason)) {
        deptReason = "";
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return usage;
  }
  /**
   * 修改-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void updateNotes(Connection dbConn,T9VehicleUsage usage) throws Exception{
    String sql = "update vehicle_usage set vu_mileage_true=?,vu_parking_fees=? where seq_id=?";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,usage.getVuMileageTrue());
      ps.setInt(2,usage.getVuParkingFees());
      ps.setInt(3,usage.getSeqId());
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  /**
   * 删除-lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static void deleteVID(Connection dbConn,String seqId) throws Exception{
    String sql = "delete from vehicle_usage where v_id in(" + seqId +")";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, null, log);
    }
  }
  
  public String getDeptName(Connection conn,String deptId)throws Exception{
    Statement stmt=null;
    ResultSet rs=null;
    String name="";
    try{
      String sql="select dept_name from department where seq_id='"+deptId+"'";
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next()){
        name=rs.getString("dept_name");
      }
    }catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(stmt, null, log);
    }
    return name;
  }
  
  public void deleteVehicleUse(Connection conn,int seqId){
    
    Statement stmt=null;
    try{
      T9ORM orm = new T9ORM();
      T9VehicleUsage vehicleUsage = (T9VehicleUsage) orm.loadObjSingle(conn, T9VehicleUsage.class, seqId);
      String sql="update vehicle set USEING_FLAG = 0 where seq_id="+vehicleUsage.getVId();
      stmt=conn.createStatement();
      stmt.executeUpdate(sql);
      orm.deleteSingle(conn,T9VehicleUsage.class,seqId);
    }catch (Exception e) {
      e.printStackTrace();
    } finally{
      T9DBUtility.close(stmt, null, log);
    }
  }
}
