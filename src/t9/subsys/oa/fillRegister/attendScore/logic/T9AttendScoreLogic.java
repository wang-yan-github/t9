package t9.subsys.oa.fillRegister.attendScore.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.attendScore.data.T9AttendScore;

public class T9AttendScoreLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.fillRigister.attendScore.logic.T9AttendScoreLogic.java");

  public void add(Connection dbConn, T9AttendScore record) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, record);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  public void updateRecord(Connection conn, T9AttendScore record) throws Exception {
    try {
          T9ORM orm = new T9ORM();
          orm.updateSingle(conn, record);
        } catch (Exception ex) {
          throw ex;
        } finally {
      }
    }
  
  public boolean getAttendScoreFlag(Connection dbConn, String userId, int flowId)
  throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) from SCORE_DATA where FLOW_ID = " + flowId + " and RANKMAN = '" + userId + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if(count > 0){
        return true;
      }else{
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 出差统计天数－－进行自动补登记
   * @param conn
   * @param year
   * @param month
   * @param userId
   * @return
   * @throws Exception
   */
  public long getAttendEvection(Connection conn, String year, String month, String userId) throws Exception {
    long result = 0;
    String ymd = "";
    if(year == null){
      ymd = year+"-"+month+"-"+"07";
   }else{
      ymd = year+"-"+month+"-"+"07";
   }
    long totalScore = 0;
    String sql = " select EVECTION_DATE1, EVECTION_DATE2 from ATTEND_EVECTION where ALLOW ='1' and STATUS = '1' and PARTICIPANT='"+userId+"' and "+ T9DBUtility.getMonthFilter("EVECTION_DATE1", T9Utility.parseDate(ymd));
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Date beginDate = rs.getDate(1);
        Date endDate = rs.getDate(2);
        long datSpace = T9Utility.getDaySpan(beginDate,endDate) + 1;
        totalScore += datSpace * 9;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return totalScore;
  }
}
