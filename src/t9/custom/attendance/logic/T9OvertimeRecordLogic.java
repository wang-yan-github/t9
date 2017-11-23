package t9.custom.attendance.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9OvertimeRecord;

public class T9OvertimeRecordLogic {
  private static Logger log = Logger
  .getLogger("ljf.t9.core.act.action.T9SysMenuLog");

  public void addOvertime(Connection dbConn, T9OvertimeRecord overtime) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, overtime);
  }
  public void updateOvertimeById(Connection dbConn, T9OvertimeRecord overtime) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, overtime);
  }
  public void delOvertimeById(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9OvertimeRecord.class, Integer.parseInt(seqId));
  }
  public T9OvertimeRecord selectOvertimeById(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9OvertimeRecord overtime = (T9OvertimeRecord) orm.loadObjSingle(dbConn, T9OvertimeRecord.class, Integer.parseInt(seqId));
    return overtime;
  }
  public List<T9OvertimeRecord> selectOvertime(Connection dbConn, String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9OvertimeRecord> overtimeList = new ArrayList<T9OvertimeRecord>();
    overtimeList = orm.loadListSingle(dbConn, T9OvertimeRecord.class, str);
    return overtimeList;
  }
  
  public void updateOvertimeById(Connection dbConn,String seqId,String status,String reason)throws Exception{ 
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "update  OVERTIME_RECORD set STATUS = '"  + status + "'";
    if(!T9Utility.isNullorEmpty(reason)){
      sql = sql + " , REASON = '" + reason.replace("'", "''")  + "'";
    }
    sql = sql  + " where SEQ_ID = " + seqId;
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
     T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 平时加班总时长
   * @param dbConn
   * @param beginDate
   * @param endDate
   * @param userId
   * @param status
   * @return
   * @throws Exception
   */
  public double getNormalAddLogic(Connection dbConn, String beginDate, String endDate, String userId, String status, String curDateStr, String year, String month) throws Exception {
    double result = 0;
    double score = 0;
    String sql = "";
    String whereStr = "";
    String ymd = year + "-" + month +"-01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(beginDate)){ 
        whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">=");
      } 
      if(!T9Utility.isNullorEmpty(endDate)){ 
       whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=");
      }
      
      if(T9Utility.isNullorEmpty(beginDate) && T9Utility.isNullorEmpty(endDate)){
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "'and STATUS = '1' and "
        + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }else{
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "'" + whereStr + "";
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        double normalAdd = rs.getDouble(1);
        score += normalAdd;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return score;
  }
  
  /**
   * 周末加班总时长
   * @param dbConn
   * @param beginDate
   * @param endDate
   * @param userId
   * @param status
   * @return
   * @throws Exception
   */
  public double getWeekAddLogic(Connection dbConn, String beginDate, String endDate, String userId, String status, String curDateStr, String year, String month) throws Exception {
    double result = 0;
    String sql = "";
    String whereStr = "";
    String ymd = year + "-" + month +"-01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(beginDate)){ 
        whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">=");
      } 
      if(!T9Utility.isNullorEmpty(endDate)){ 
       whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=");
      }
      
      if(T9Utility.isNullorEmpty(beginDate) && T9Utility.isNullorEmpty(endDate)){
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "' and "
        + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }else{
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "'" + whereStr + "";
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        double normalAdd = rs.getDouble(1);
        result += normalAdd;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 节假日加班总时长
   * @param dbConn
   * @param beginDate
   * @param endDate
   * @param userId
   * @param status
   * @return
   * @throws Exception
   */
  public double getFestivalAddLogic(Connection dbConn, String beginDate, String endDate, String userId, String status, String curDateStr, String year, String month) throws Exception {
    double result = 0;
    String sql = "";
    String whereStr = "";
    String ymd = year + "-" + month +"-01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(beginDate)){ 
        whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">=");
      } 
      if(!T9Utility.isNullorEmpty(endDate)){ 
       whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=");
      }
      
      if(T9Utility.isNullorEmpty(beginDate) && T9Utility.isNullorEmpty(endDate)){
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "' and "
        + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }else{
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and OVERTIME_TYPE = '" + status + "'" + whereStr + "";
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        double normalAdd = rs.getDouble(1);
        result += normalAdd;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 取得用户名称--cc
   * 
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */

  public String getUserNameLogic(Connection conn, int userId) throws Exception {
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID = " + userId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 加班总时长
   * @param dbConn
   * @param beginDate
   * @param endDate
   * @param userId
   * @return
   * @throws Exception
   */
  public double getOverTimeHourLogic(Connection dbConn, String year, String month, String userId) throws Exception {
    double result = 0;
    double score = 0;
    String sql = "";
    String whereStr = "";
    String ymd = year + "-" + month + "-" + "01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(ymd)){
        sql = "select HOUR from OVERTIME_RECORD where USER_ID = '" + userId + "' and STATUS = '1' and "
        + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        double normalAdd = 0;
        try {
          normalAdd = rs.getDouble(1);
        } catch (Exception e) {
          try {
            normalAdd = Double.parseDouble(rs.getString(1));
          } catch (Exception ex) {
            
          }
        }
        
        score += normalAdd;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return score;
  }
  
  public double getOverTimeMoneyLogic(Connection dbConn, String year, String month, String userId) throws Exception {
    double result = 0;
    double score = 0;
    String sql = "";
    String whereStr = "";
    String ymd = year + "-" + month + "-" + "01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(ymd)){
        sql = "select OVERTIME_MONEY from OVERTIME_RECORD where USER_ID = '" + userId + "' and STATUS = '1' and "
        + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        double normalAdd = rs.getDouble(1);
        score += normalAdd;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return score;
  }
}
