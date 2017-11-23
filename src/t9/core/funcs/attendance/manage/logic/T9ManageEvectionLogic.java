package t9.core.funcs.attendance.manage.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.util.db.T9ORM;

public class T9ManageEvectionLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public List<T9AttendEvection> selectEvectionManage(Connection dbConn,Map map) throws Exception {
    List<T9AttendEvection> evectionList = new ArrayList<T9AttendEvection>();
    T9ORM orm = new T9ORM();
    evectionList = orm.loadListSingle(dbConn, T9AttendEvection.class, map);
    return evectionList;
  }
  public List<T9AttendEvection> selectEvectionManage(Connection dbConn,String[] str) throws Exception {
    List<T9AttendEvection> evectionList = new ArrayList<T9AttendEvection>();
    T9ORM orm = new T9ORM();
    evectionList = orm.loadListSingle(dbConn, T9AttendEvection.class, str);
    return evectionList;
  }
}
