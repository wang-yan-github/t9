package t9.custom.attendance.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.db.T9ORM;
import t9.custom.attendance.data.T9Duty;
public class T9DutyLogic {
  
  private static Logger log = Logger
  .getLogger("cc.t9.core.act.action.T9SysMenuLog");
  
  public void addDuty(Connection dbConn, T9Duty duty) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, duty);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  public List<T9Duty> getDutyList(Connection dbConn, String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9Duty> dutyList = new ArrayList<T9Duty>();
    dutyList = orm.loadListSingle(dbConn, T9Duty.class, str);
    return dutyList;
  }
  
  /**
   * 删除一条记录--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9Duty.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  public T9Duty getDutyDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9Duty) orm.loadObjSingle(conn, T9Duty.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  public void updateDuty(Connection conn, T9Duty record) throws Exception {
    try {
          T9ORM orm = new T9ORM();
          orm.updateSingle(conn, record);
        } catch (Exception ex) {
          throw ex;
        } finally {
      }
    }
  
  public void updateDutyStatus(Connection dbConn,Map map) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, "attendOut",map);
  }
}
