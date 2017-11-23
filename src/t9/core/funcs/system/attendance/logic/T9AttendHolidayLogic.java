package t9.core.funcs.system.attendance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import t9.core.funcs.system.attendance.data.T9AttendHoliday;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9AttendHolidayLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void addHoliday(Connection dbConn, T9AttendHoliday holiday) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, holiday);  
  }
  public List<T9AttendHoliday> selectHoliday(Connection dbConn,String[] str) throws Exception {
    List<T9AttendHoliday> holidayList = new ArrayList<T9AttendHoliday>();
    T9ORM orm = new T9ORM();
    holidayList = orm.loadListSingle(dbConn, T9AttendHoliday.class, str);
    return holidayList;
  }
  public T9AttendHoliday selectHolidayById(Connection dbConn,String seqIds) throws Exception {
    T9ORM orm = new T9ORM();
    T9AttendHoliday holiday = new T9AttendHoliday ();
    int seqId = 0;
    if(!seqIds.equals("")){
      seqId = Integer.parseInt(seqIds);
      holiday = (T9AttendHoliday) orm.loadObjSingle(dbConn, T9AttendHoliday.class, seqId);
    }
   
    return holiday;
  }
  public void updateHoliday(Connection dbConn, T9AttendHoliday holiday) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, holiday);
  }
  public void deleteHoliday(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9AttendHoliday.class, Integer.parseInt(seqId));
  }
  public void deleteAllHoliday(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_HOLIDAY";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }   
  }
}
