package t9.custom.attendance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9PersonalLeave;

public class T9PersonalLeaveLogic{
  private static Logger log = Logger.getLogger("ljf.t9.core.act.action.T9SysMenuLog");
  public void addLeave(Connection dbConn, T9PersonalLeave leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, leave);  
  }
  public void updateLeave(Connection dbConn,T9PersonalLeave leave) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, leave);
  }
  public List<T9PersonalLeave>  selectLeave(Connection dbConn,String[] map) throws Exception {
    List<T9PersonalLeave> leaveList = new ArrayList<T9PersonalLeave>();
    T9ORM orm = new T9ORM();
    leaveList = orm.loadListSingle(dbConn, T9PersonalLeave.class, map);
    return leaveList;
  }
  public void deleteLeaveById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9PersonalLeave.class, Integer.parseInt(seqId));
  }
  public T9PersonalLeave selectLeaveById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9PersonalLeave leave = (T9PersonalLeave) orm.loadObjSingle(dbConn, T9PersonalLeave.class, Integer.parseInt(seqId));
    return leave;
  }
  
  public void updateLeaveAllow(Connection dbConn,String seqId,String allow ,String reason) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String sql = " update PERSONAL_LEAVE set ALLOW = '" + allow +  "'" ;
    if(!T9Utility.isNullorEmpty(allow)){
      sql = sql + " , REASON = '" + reason.replace("'", "''")  + "'";
    }
    sql = sql + " where  SEQ_ID = " + seqId;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
    }catch(Exception ex) {
         throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    } 
  }
  public void updateLeaveStatus(Connection dbConn,String seqId,String status)throws Exception{ 
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "update  PERSONAL_LEAVE set STATUS = '"  + status + "' where SEQ_ID = " + seqId;
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
