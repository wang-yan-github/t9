package t9.core.funcs.attendance.personal.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9AttendOutLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void addOut(Connection dbConn, T9AttendOut out) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, out);  
  }
  public List<T9AttendOut> selectOut(Connection dbConn,Map map) throws Exception {
    List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    T9ORM orm = new T9ORM();
    outList = orm.loadListSingle(dbConn, T9AttendOut.class, map);
    return outList;
  }
  public List<T9AttendOut> selectOut(Connection dbConn,String[] str) throws Exception {
    List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    T9ORM orm = new T9ORM();
    outList = orm.loadListSingle(dbConn, T9AttendOut.class, str);
    return outList;
  }
  public T9AttendOut selectOutById(Connection dbConn,String seqId) throws Exception {
    T9AttendOut out = new T9AttendOut();
    T9ORM orm = new T9ORM();
    out = (T9AttendOut) orm.loadObjSingle(dbConn, T9AttendOut.class, Integer.parseInt(seqId));
    return out;
  }
  
  public void deleteOutById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9AttendOut.class, Integer.parseInt(seqId));
  }
  
  public void updateStatus(Connection dbConn,Map map) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, "attendOut",map);
  }
  
  public void updateOut(Connection dbConn,T9AttendOut out) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, out);
  }
  
  public List<T9AttendOut>  selectHistoryOut(Connection dbConn,String[] map) throws Exception {
    List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    T9ORM orm = new T9ORM();
    outList = orm.loadListSingle(dbConn, T9AttendOut.class, map);
    return outList;
  }
  
  public int getAttendOutCountLogic(Connection dbConn, String year, String month, String userId) throws Exception {
    int result = 0;
    String sql = "";
    String ymd = year + "-" + month + "-" + "01";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if(!T9Utility.isNullorEmpty(ymd)){
        sql = "select count(*) from ATTEND_OUT where USER_ID = '" + userId + "' and ALLOW = '1' and STATUS = '1' and "
        + T9DBUtility.getMonthFilter("SUBMIT_TIME", T9Utility.parseDate(ymd));
      }
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
}
