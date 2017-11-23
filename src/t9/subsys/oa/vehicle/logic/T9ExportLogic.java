package t9.subsys.oa.vehicle.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import t9.core.data.T9DbRecord;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.vehicle.data.T9VehicleUsage;

public class T9ExportLogic {
  /**
   * lz
   * 
   * */
  public static ArrayList<T9DbRecord> getDbRecord(List<T9VehicleUsage> usageList,Connection dbConn) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    T9VehicleUsage usage = new T9VehicleUsage();
    for (int i = 0; i < usageList.size(); i++) {
      usage = usageList.get(i);

      T9DbRecord dbrec = new T9DbRecord();
      dbrec.addField("车牌号",usage.getVId());
      if (!T9Utility.isNullorEmpty(usage.getVuDriver())) {
        dbrec.addField("司机",getUser(dbConn,usage.getVuDriver()));
      }
      if (T9Utility.isNullorEmpty(usage.getVuDriver())) {
        dbrec.addField("司机","");
      }
      if (!T9Utility.isNullorEmpty(usage.getVuProposer())) {
        dbrec.addField("申请人",getUser(dbConn,usage.getVuProposer()));
      }
      if (T9Utility.isNullorEmpty(usage.getVuProposer())) {
        dbrec.addField("申请人","");
      }
      dbrec.addField("申请时间 ",usage.getVuRequestDate());
      dbrec.addField("用车人 ",usage.getVuUser());
      if (!T9Utility.isNullorEmpty(usage.getVuDept())) {
        dbrec.addField("用车部门",getDept(dbConn,usage.getVuDept()));
      }
      if (T9Utility.isNullorEmpty(usage.getVuDept())) {
        dbrec.addField("用车部门","");
      }
      dbrec.addField("事由",usage.getVuReason());
      dbrec.addField("开始时间",usage.getVuStart());
      dbrec.addField("结束时间",usage.getVuEnd());
      dbrec.addField("目的地 里程(公里)",usage.getVuDestination());
      dbrec.addField("里程(公里)",usage.getVuMileage());
      if (!T9Utility.isNullorEmpty(usage.getVuOperator())) {
        dbrec.addField("调度员",getUser(dbConn,usage.getVuOperator()));
      }
      if (T9Utility.isNullorEmpty(usage.getVuOperator())) {
        dbrec.addField("调度员",getUser(dbConn,usage.getVuOperator()));
      }
      if (usage.getVuStatus().equals("0")) {
        dbrec.addField("当前状态","待批");
      }
      if (usage.getVuStatus().equals("1")) {
        dbrec.addField("当前状态","已准");
      }
      if (usage.getVuStatus().equals("2")) {
        dbrec.addField("当前状态","使用中");
      }
      if (usage.getVuStatus().equals("3")) {
        dbrec.addField("当前状态","未准");
      }
      if (usage.getVuStatus().equals("4")) {
        dbrec.addField("当前状态","结束");
      }
      dbrec.addField("备注",usage.getVuRemark());
      dbL.add(dbrec);
    }
    return dbL;
  }
  /***
   * 根据条件查询数据,导出数据
   * @return
   * @throws Exception 
   */
  public static List<T9VehicleUsage> vehicleAll(Connection dbConn,T9VehicleUsage usage,String vuRequestDateMax,String vuStartMax,String vuEndMax) throws Exception {
    String sql = "select veus.SEQ_ID,veus.V_ID,ve.V_NUM as Vnum,pe.USER_NAME as userName,veus.VU_REASON,veus.VU_DESTINATION,veus.VU_START,veus.VU_END,veus.VU_REMARK"
      + ",veus.VU_STATUS,veus.dmer_status,veus.VU_REQUEST_DATE,veus.VU_PROPOSER,veus.VU_MILEAGE"
      + ",veus.VU_DEPT,veus.VU_OPERATOR,veus.VU_DRIVER,veus.SMS_REMIND"
      + ",veus.SMS2_REMIND,veus.DEPT_MANAGER,veus.SHOW_FLAG,veus.DEPT_REASON,veus.OPERATOR_REASON,veus.VU_MILEAGE_TRUE,veus.VU_PARKING_FEES "
      + " from vehicle_usage veus left outer join vehicle ve on ve.seq_id=veus.V_ID " 
      + " left outer join person pe on pe.seq_id=veus.VU_USER where 1=1 ";
    ResultSet rs = null;
    PreparedStatement stmt = null ;
    List<T9VehicleUsage> usageList = new ArrayList<T9VehicleUsage>();
    T9VehicleUsage vehicle = null;
    try {
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
        //System.out.println(usage.getVuReason());
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
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        vehicle = new T9VehicleUsage(); 
        vehicle.setVId(rs.getString("Vnum"));
        vehicle.setVuDriver(rs.getString("vu_driver"));
        vehicle.setVuProposer(rs.getString("vu_proposer"));
        vehicle.setVuRequestDate(rs.getDate("vu_request_date"));
        vehicle.setVuUser(rs.getString("userName"));
        vehicle.setVuDept(rs.getString("vu_dept"));
        vehicle.setVuReason(rs.getString("vu_reason"));
        vehicle.setVuStart(rs.getDate("vu_start"));
        vehicle.setVuEnd(rs.getDate("vu_end"));
        vehicle.setVuDestination(rs.getString("vu_destination"));
        vehicle.setVuOperator(rs.getString("vu_operator"));
        vehicle.setVuStatus(rs.getString("vu_status"));
        vehicle.setVuRemark(rs.getString("vu_remark"));
        vehicle.setVuMileage(rs.getInt("vu_mileage"));
        usageList.add(vehicle);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt,rs,null);
    }
    return usageList;
  }


  //部门
  public static String getDept(Connection dbConn,String deptId) {
    ResultSet rs = null;
    PreparedStatement ps = null;
    String deptName = null;
    try {
      String sql = "select DEPT_NAME from DEPARTMENT where SEQ_ID=" + deptId;
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        deptName = rs.getString("DEPT_NAME");
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return deptName;
  }

  //人员
  public static String getUser(Connection dbConn,String managerDesc) {
    ResultSet rs = null;
    PreparedStatement ps = null;
    String managerName = null;
    try {
      String sql = "select USER_NAME from PERSON where SEQ_ID=" + managerDesc;
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        managerName = rs.getString("USER_NAME");
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return managerName;
  }
}
