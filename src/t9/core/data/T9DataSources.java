package t9.core.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import t9.core.exps.T9InvalidParamException;
import t9.core.global.T9Const;
import t9.core.global.T9DbKeys;
import t9.core.global.T9MessageKeys;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.load.T9DataSourceLoader;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9Authenticator;

/**
 * 系统数据源存储

 * @author YZQ
 * @version 1.0
 * @date 2006-8-29
 */
public class T9DataSources {
  /**
   * log
   */
  private static final Logger log = Logger.getLogger(
      "yzq.t9.core.data.TDCDataSources");

  /**
   * 数据库列表

   */
  private static HashMap databaseMap = new LinkedHashMap();  
  /**
   * 数据源哈希表
   */
  private static HashMap dataSourceMap = new HashMap();
  
  /**
   * 清理所有资源

   */
  public static void clearAll() {
    databaseMap.clear();
    dataSourceMap.clear();
  }
  
  /**
   * 构建缺省数据源

   */
  public static void buildDataSourceMap(String fileName) throws Exception {
    ArrayList databaseList = T9DataSourceLoader.loadDataBases(fileName);
    addDatabase(databaseList);
    
    //构建数据源

    int databaseCnt = databaseList.size();
    for (int i = 0; i < databaseCnt; i++) {
      T9Database database = (T9Database)databaseList.get(i);
      registerDataSource(database);
    }
  }
  
  /**
   * 构建数据源

   */
  public static void buildDataSourceMap(Connection dbConn) throws Exception {
    ArrayList databaseList = T9DataSourceLoader.loadDataBases(
        dbConn);
    addDatabase(databaseList);
    
    //构建数据源

    int databaseCnt = databaseList.size();
    for (int i = 0; i < databaseCnt; i++) {
      T9Database database = (T9Database)databaseList.get(i);
      registerDataSource(database);
    }
  }
  
  /**
   * 注册数据源

   * @param database    数据库定义对象

   * @throws Exception
   */
  public static void registerDataSource(T9Database database) throws Exception {
    
    String dbName = database.getDbName();
    String dsName = database.getDsName();
    String dbmsName = database.getDbmsName();
    String driver = T9SysProps.getString(T9SysPropKeys.DBCONN_DRIVER + "." + dbmsName);
    
    DataSource ds = null;
    HashMap dbConnProps = new HashMap();
    dbConnProps.put(T9DbKeys.DSPARAM_DRIVER_CLASS_NAME,
        T9SysProps.getString(T9SysPropKeys.DBCONN_DRIVER + "." + dbmsName));
    String url = null;
    url = T9SysProps.getString(T9SysPropKeys.DBCONN_CONURL + "." + dbmsName);
    if (dbmsName.startsWith(T9Const.DBMS_SQLSERVER)) {
      url += "DatabaseName=" + dbName + ";SelectMethod=Cursor";
    }else if (dbmsName.startsWith(T9Const.DBMS_MYSQL)) {
      url += dbName + "?characterEncoding=UTF8";
    }
    dbConnProps.put(T9DbKeys.DSPARAM_URL, url);
    String userName = T9SysProps.getString(T9SysPropKeys.DBCONN_USER_NAME + "." + dbmsName);
    if (dbmsName.startsWith(T9Const.DBMS_ORACLE)) {
      if (!T9Utility.isNullorEmpty(dbName)) {
        userName = dbName;
      }
    }
    dbConnProps.put(T9DbKeys.DSPARAM_USER_NAME, userName);
    String pass = T9SysProps.getString(T9SysPropKeys.DBCONN_PASSWARD + "." + dbmsName);
    if (dbmsName.startsWith(T9Const.DBMS_ORACLE)) {
      if (!T9Utility.isNullorEmpty(database.getPassword())) {
        pass = database.getPassword();
      }
    }
    dbConnProps.put(T9DbKeys.DSPARAM_PASS_WORD, T9Authenticator.ciphDecryptStr(pass));
    dbConnProps.put(T9DbKeys.DSPARAM_MAX_ACTIVE, 
        T9SysProps.getString(T9SysPropKeys.DBCONN_MAX_ACTIVE + "." + dbmsName));
    int maxIdle = T9SysProps.getInt(T9SysPropKeys.DBCONN_MAX_IDLE + "." + dbmsName);
    if (maxIdle > 0) {
      dbConnProps.put(T9DbKeys.DSPARAM_MAX_IDLE, String.valueOf(maxIdle));
    }
    dbConnProps.put(T9DbKeys.DSPARAM_MAX_WAIT, 
        T9SysProps.getString(T9SysPropKeys.DBCONN_MAX_WAIT + "." + dbmsName));
    dbConnProps.put(T9DbKeys.DSPARAM_DEFAULT_AUTO_COMMIT, 
        T9SysProps.getString(T9SysPropKeys.DBCONN_DEFAULT_AUTO_COMMIT + "." + dbmsName));
    dbConnProps.put(T9DbKeys.DSPARAM_DEFAULT_READ_ONLY, 
        T9SysProps.getString(T9SysPropKeys.DBCONN_DEFAULT_READONLY + "." + dbmsName));
    dbConnProps.put("removeAbandoned", "true");
    dbConnProps.put("logAbandoned", "true");
    dbConnProps.put("removeAbandonedTimeout", "60");
//    dbConnProps.put("logAbandoned", "true");
    ds = (DataSource)T9ServletUtility.applicationInstance(
        T9SysProps.getString(T9SysPropKeys.DBCONN_DATASOURCE_TYPE));
    BeanUtils.populate(ds, dbConnProps);
    dataSourceMap.put(dsName, ds);
    
    log.debug("datasource " + dsName + " is built");
    System.out.println("datasource " + dsName + " is built");
  }
  
  /**
   * 用数据库编码取得数据源名称

   * @return
   */
  public static String getDsNameByDbNo(String dbNo) 
    throws T9InvalidParamException {
    
    if (dbNo == null) {
      new T9InvalidParamException(T9MessageKeys.COMMON_ERROR_INVALID_DB_NO);
    }

    T9Database database = (T9Database)databaseMap.get(dbNo);
    return database.getDsName();
  }
   
  /**
   * 批量增加数据库定义

   * @param databaseList
   */
  public static void addDatabase(ArrayList databases) {
    if (databases == null) {
      return;
    }
    for (int i = 0; i < databases.size(); i++) {
      T9Database database = (T9Database)databases.get(i);
      databaseMap.put(database.getDbNo(), database);
    }
  }
  
  /**  by cly
   * 
   * 单个增加数据库定义  
   * @param databaseList
   */
  public static void addDatabase(T9Database database) {
    if (database == null) {
      return;
    }
      databaseMap.put(database.getDbNo(), database);
  }
  
  /**
   * 用数据库编码取得数据库对象

   * @param dbNo          数据库编码

   * @return
   */
  public static T9Database getDb(String dbNo) {
    
    return (T9Database)databaseMap.get(dbNo);
  }
    
  /**
   * 取得数据源

   * @param key    数据源名称

   * @return
   */
  public static DataSource getDataSource(String key) {
    return (DataSource)dataSourceMap.get(key);
  }
  
  /**
   * 添加数据源

   * @param key              数据源名称

   * @param dataSource       数据源

   */   
  public static void addDataSource(String key, DataSource dataSource) {
    dataSourceMap.put(key, dataSource);
  }

  /**
   * 关闭某个帐套库的连接
   * @param dbNo
   * @return
   * @throws Exception
   */
  public static void closeDbConn(String dbNo) throws Exception {
    String dsName = getDsNameByDbNo(dbNo);
    if (dsName == null) {
      throw new T9InvalidParamException(T9MessageKeys.COMMON_ERROR_INVALID_DB_NO);
    }
    BasicDataSource ds = (BasicDataSource)getDataSource(dsName);
    ds.close(); 
  }

  /**
   * 关闭所有数据库连接池

   * @throws Exception
   */
  public static void closeConnPool() {
    for (Iterator iEntry = dataSourceMap.entrySet().iterator(); iEntry.hasNext();) {
      Map.Entry entry = (Map.Entry)iEntry.next();
      String dsName = (String)entry.getKey();
      String logMsrg = "Datasource " + dsName + " is closed.";
      log.debug(logMsrg);
      BasicDataSource ds = (BasicDataSource)entry.getValue();
      try {
        ds.close();
      }catch(Exception ex) {        
      }
    }
  }
}
