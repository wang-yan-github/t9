package t9.core.funcs.system.attendance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.system.attendance.data.T9AttendConfig;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9AttendConfigLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void addConfig(Connection dbConn, T9AttendConfig config) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, config);  
  }
  public List<T9AttendConfig> selectConfig(Connection dbConn,Map map) throws Exception {
    List<T9AttendConfig> configList = new ArrayList<T9AttendConfig>();
    T9ORM orm = new T9ORM();
    configList = orm.loadListSingle(dbConn, T9AttendConfig.class, map);
    return configList;
  }
  public T9AttendConfig selectConfigById(Connection dbConn,String seqIds) throws Exception {
    T9ORM orm = new T9ORM();
    T9AttendConfig config = new T9AttendConfig ();
    int seqId = 0;
    if(!seqIds.equals("")){
      seqId = Integer.parseInt(seqIds);
    }
    config = (T9AttendConfig) orm.loadObjSingle(dbConn, T9AttendConfig.class, seqId);
    return config;
  }
  public void updateConfig(Connection dbConn, T9AttendConfig config) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, config);
  }
  public void deleteConfig(Connection dbConn, String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9AttendConfig.class, Integer.parseInt(seqId));
  }
  public void updateConfigGenaralById(Connection dbConn,String seqId,  String general) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "update ATTEND_CONFIG set GENERAL = '" + general + "' where SEQ_ID = " + seqId;
    //System.out.println(sql);
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
