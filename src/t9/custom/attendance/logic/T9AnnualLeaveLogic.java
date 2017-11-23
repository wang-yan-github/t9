package t9.custom.attendance.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9AnnualLeave;

public class T9AnnualLeaveLogic {
  private static Logger log = Logger.getLogger("ljf.t9.core.act.action.T9SysMenuLog");
  public void addLeave(Connection dbConn, T9AnnualLeave leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, leave);  
  }
  public void updateLeave(Connection dbConn,T9AnnualLeave leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, leave);
  }
  public List<T9AnnualLeave>  selectLeave(Connection dbConn,String[] map) throws Exception {
    List<T9AnnualLeave> leaveList = new ArrayList<T9AnnualLeave>();
    T9ORM orm = new T9ORM();
    leaveList = orm.loadListSingle(dbConn, T9AnnualLeave.class, map);
    return leaveList;
  }
  public void deleteLeaveById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9AnnualLeave.class, Integer.parseInt(seqId));
  }
  public T9AnnualLeave selectLeaveById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9AnnualLeave leave = (T9AnnualLeave) orm.loadObjSingle(dbConn, T9AnnualLeave.class, Integer.parseInt(seqId));
    return leave;
  }
  
  public String selectPersonAnnualDays(Connection dbConn,String userId,String year) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String leaveDays = "0";
    String ymd = year + "-10-10";
    String sql = " select sum(LEAVE_DAYS) as LEAVE_DAYS_SUM from ANNUAL_LEAVE where USER_ID = '" + userId + "' and " + T9DBUtility.getYearFilter("LEAVE_DATE2", T9Utility.parseDate(ymd)) + " and allow = '1'" ;
    //System.out.println(sql);
      try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        if(rs.next()){
          if(!T9Utility.isNullorEmpty(rs.getString(1))){
            leaveDays = rs.getString(1);
          }
        }
      
      }catch(Exception ex) {
         throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, log);
    } 
      return leaveDays;
  }
  
  public String selectPersonLeaveDays(Connection dbConn,String userId,String year) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String ymd = year + "-10-10";
    String leaveDays = "0";
    String sql = " select sum(LEAVE_DAYS) as LEAVE_DAYS_SUM from ATTEND_LEAVE where USER_ID = '" + userId + "' and " + T9DBUtility.getYearFilter("LEAVE_DATE2", T9Utility.parseDate(ymd)) + " and allow = '1'" ;
    //System.out.println(sql);
      try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        if(rs.next()){
          if(!T9Utility.isNullorEmpty(rs.getString(1))){
            leaveDays = rs.getString(1);
          }
        }
      
      }catch(Exception ex) {
         throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, log);
    } 
      return leaveDays;
  }
  
  public void updateLeaveAllow(Connection dbConn,String seqId,String allow ,String reason) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = " update ANNUAL_LEAVE set ALLOW = '" + allow +  "'" ;
    if(!T9Utility.isNullorEmpty(allow)){
      sql = sql + " , REASON = '" + reason.replace("'", "''") + "'";
    }
    sql = sql + " where  SEQ_ID = " + seqId;
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
   * 展示自动补登记、不需要审核日期--cc 20101126
   * @param dbConn
   * @param beginDate
   * @param endDate
   * @return
   * @throws Exception
   */
  public String showTimeStr(Connection dbConn, String beginDate, String endDate) throws Exception{
    String data = "";
    String dataNo = "";
    String firstDate = "";
    String lastDate = "";
    StringBuffer sb = new StringBuffer();
    List list = getDateValue(beginDate, endDate);      
    if(list.size() == 2){
      for(int i = 0; i < list.size(); i++){
        String lenStr = String.valueOf(list.get(i));
        if(i == 0){
          firstDate = lenStr;
        }
        if(i == list.size()-1){
          lastDate = lenStr;
        }
      }
      data = firstDate + "～" + lastDate +",";
    }
    if(list.size() > 2){
      for(int i = 0; i < list.size(); i++){
        String lenStr = String.valueOf(list.get(i));
        if(lenStr.length() == 7){
          dataNo += lenStr + ",";
        }
        if(lenStr.length() == 10){
          if(i == 0){
            int maxDays = getMaxDay(lenStr);
            String endTime = beginDate.substring(0, 7) + "-" + String.valueOf(maxDays);
            data += lenStr + "～" + endTime + ",";
          }
          if(i == list.size()-1){
            String beginDates = lenStr.substring(0, 7) + "-" + "01";
            data += beginDates + "～" + lenStr + ",";
          }
        }
      }
    }
    if(data.length() > 0){
      data = data.substring(0, data.length()-1);
    }
    if(dataNo.length() > 0){
      dataNo = dataNo.substring(0, dataNo.length()-1);
    }
    sb.append("{");
    sb.append("data:\"" + data + "\"");
    sb.append(",dataNo:\"" + dataNo + "\"");
    sb.append("}");
    return sb.toString();
  }
  
  /**
   * 返回两个日期的相隔月份--cc 20101126
   * @param startDate
   * @param endDate
   * @return
   * @throws Exception
   */
  public List<String> getDateValue(String startDateStr, String endDateStr) throws Exception {
    List<String> list = new ArrayList<String>();
    if (T9Utility.isNullorEmpty(endDateStr) && !T9Utility.isNullorEmpty(startDateStr)) {
      endDateStr = startDateStr;
      list.add(startDateStr);
      list.add(endDateStr);
      return list;
    } else if (T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
      startDateStr = endDateStr;
      list.add(startDateStr);
      list.add(endDateStr);
      return list;
    }
    try {
      if (!T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
        String startDateArry[] = startDateStr.split("-");
        String endDateArry[] = endDateStr.split("-");
        int startYear = Integer.parseInt(startDateArry[0]);
        int startMonth = Integer.parseInt(startDateArry[1]);
        int endMonth = Integer.parseInt(endDateArry[1]);
        String result = "";
        if (startMonth < endMonth) {
          list.add(startDateStr);
          int tmp = endMonth - startMonth;
          if (tmp <= 11) {
            for (int i = 1; i < tmp; i++) {
              int tmpMonth = startMonth + i;
              String str = "";
              if (tmpMonth < 10) {
                str = "0";
              }
              result = startYear + "-" + str + tmpMonth;
              list.add(result);
            }
          }
          list.add(endDateStr);
        } else if (startMonth == endMonth) {
          list.add(startDateStr);
          list.add(endDateStr);

        } else if (startMonth > endMonth) {
        }
      }
    } catch (Exception e) {
      throw e;
    }
    return list;
  }
  
  /**
   * 取得当前月的最大天数--cc 20101126
   * @param dbConn
   * @param record
   * @param person
   * @throws Exception
   */
  public int getMaxDay(String ymd) throws Exception{
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance(); 
    calendar.setTime(dateFormat1.parse(ymd + "-07")); 
    int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
    return maxDay;
  }
  
  public double getAnnualLeaveDayLogic(Connection dbConn, String year, String month, String userId) throws Exception {
    double score = 0;
    String sql = "";
    String ymd = year + "-" + month + "-" + "01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(ymd)){
        sql = "select LEAVE_DAYS from ANNUAL_LEAVE where USER_ID = '" + userId + "' and ALLOW = '1' and STATUS = '1' and "
        + T9DBUtility.getMonthFilter("LEAVE_DATE2", T9Utility.parseDate(ymd));
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
