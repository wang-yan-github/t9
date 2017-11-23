package t9.core.load;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9Database;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

/**
 * 数据源加载
 * @author YZQ
 * @version 1.0
 * @date 2006-8-29
 */
public class T9DataSourceLoader {
  /**
   * log
   */
  private static final Logger log = Logger.getLogger(
      "yzq.t9.core.load.TDCDataSourceLoader");
  
  /**
   * 加载缺省数据库定义
   * @return
   * @throws Exception
   */
  public static ArrayList loadDataBases(String fileName) throws Exception {
    ArrayList rtList = new ArrayList();
    
    if (new File(fileName).exists()) {
      Map<String, String> dbConfMap = new HashMap<String, String>();
      T9FileUtility.load2Map(fileName, dbConfMap);
      Map<String, String> dbMap = T9Utility.startsWithMap(dbConfMap, "db");
      Iterator<String> iKeys = dbMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String dbConfJson = dbMap.get(key).trim();
        if (T9Utility.isNullorEmpty(dbConfJson)) {
          continue;
        }
        try {
          rtList.add(T9FOM.json2Obj(dbConfJson, T9Database.class));
        }catch(Exception ex) {
          log.debug(ex);
        }
      }
    }
    //没有自定义数据库，系统提供的缺省数据库配置
    if (rtList.size() < 1) {
      T9Database database = new T9Database();
      database.setSeqId(0);
      database.setDbNo("0");
      database.setDbName("TRANSLATE");
      database.setDbDesc("MsSqlserver");
      database.setDsName("mssql/TRANSLATE");
      database.setDbmsName("sqlserver");
      rtList.add(database);
      
      database = new T9Database();
      database.setSeqId(2);
      database.setDbNo("2");
      database.setDbName("");
      database.setDbDesc("oracleDb");
      database.setDsName("oracle/T9DB");
      database.setDbmsName("oracle");
      rtList.add(database);
    }
    
    return rtList;
  }
  
  /**
   * 加载数据库定义
   * @param dbConn     系统库连接
   * @return
   * @throws Exception
   */
  public static ArrayList loadDataBases(Connection dbConn) throws Exception {
    
    ArrayList rtList = new ArrayList();
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "select"
        + " SEQ_ID"
        + ", DB_NO"
        + ", DB_NAME"
        + ", DB_DESC"
        + ", DS_NAME"
        + ", DBMS_NAME"
        + " from SYS_DATABASES"
        + " order by DB_NO";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9Database database = new T9Database();
        database.setSeqId(rs.getInt(1));
        database.setDbNo(rs.getString(2));
        database.setDbName(rs.getString(3));
        database.setDbDesc(rs.getString(4));
        database.setDsName(rs.getString(5));
        database.setDbmsName(rs.getString(6));
        rtList.add(database);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return rtList;
  }
  
  /**
   * 加载数据库定义
   * @param dbConn     系统库连接
   * @return
   * @throws Exception
   */
  public static T9Database loadDataBase(Connection dbConn, String seqId) throws Exception {
    T9Database database = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "select"
        + " SEQ_ID"
        + ", DB_NO"
        + ", DB_NAME"
        + ", DB_DESC"
        + ", DS_NAME"
        + ", DBMS_NAME"
        + " from SYS_DATABASES"
        + " where SEQ_ID = " + seqId 
        + " order by DB_NO";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);

      if (rs.next()) {
        database = new T9Database();
        database.setSeqId(rs.getInt(1));
        database.setDbNo(rs.getString(2));
        database.setDbName(rs.getString(3));
        database.setDbDesc(rs.getString(4));
        database.setDsName(rs.getString(5));
        database.setDbmsName(rs.getString(6));
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return database;
  }
  
  /**
   * 加载数据库定义
   * @param dbConn     系统库连接
   * @return
   * @throws Exception
   */
  public static T9Database loadDataBaseByNo(Connection dbConn, String dbNo) throws Exception {
    T9Database database = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "select"
        + " SEQ_ID"
        + ", DB_NO"
        + ", DB_NAME"
        + ", DB_DESC"
        + ", DS_NAME"
        + ", DBMS_NAME"
        + " from SYS_DATABASES"
        + " where DB_NO='" + dbNo + "'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        database = new T9Database();
        database.setSeqId(rs.getInt(1));
        database.setDbNo(rs.getString(2));
        database.setDbName(rs.getString(3));
        database.setDbDesc(rs.getString(4));
        database.setDsName(rs.getString(5));
        database.setDbmsName(rs.getString(6));
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return database;
  }
}
