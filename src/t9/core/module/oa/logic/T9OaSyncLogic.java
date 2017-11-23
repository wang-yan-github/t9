package t9.core.module.oa.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.core.global.T9SysProps;
import t9.core.module.report.logic.T9UserPrivSyncLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

public class T9OaSyncLogic {
  public static String OA_DB_NAME = T9SysProps.getProp("OA_DATABASE_NAME");
  public static String OA_DB_URL = T9SysProps.getProp("oa.conurl.mysql");
  public static String OA_DB_USER = T9SysProps.getProp("oa.userName.mysql");
  public static String OA_DB_PWD = T9SysProps.getProp("oa.passward.mysql");
  
  public static Connection getOAConn() throws Exception {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(OA_DB_URL + OA_DB_NAME, OA_DB_USER, OA_DB_PWD);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }
  public static Connection getOAConn2() throws Exception {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3396/TD_OA", "root", "myoa888");
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }
  public static void updateTableBySql(String sql , Connection conn) throws Exception{
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  //创建组织单位
  public static void createUnit(Connection conn) throws Exception {
    String table_3 = "create table DEPARTMENT_TMP as select * from DEPARTMENT";
    String table_2 = "create table UNIT_TMP as select * from UNIT";
    String table_1 = "create table USER_TMP as select * from USER";
    String table_4 = "create table USER_PRIV_TMP as select * from USER_PRIV";
    updateTableBySql(table_1, conn);
    updateTableBySql(table_2, conn);
    updateTableBySql(table_3, conn);
    updateTableBySql(table_4, conn);
    
     table_3 = "drop view DEPARTMENT";
     table_2 = "drop view UNIT";
     table_1 = "drop view USER";
     table_4 = "drop view USER_PRIV";
     
     updateTableBySql(table_1, conn);
     updateTableBySql(table_2, conn);
     updateTableBySql(table_3, conn);
     updateTableBySql(table_4, conn);
     
     table_3 = "ALTER TABLE DEPARTMENT_TMP  RENAME TO DEPARTMENT";
     table_2 = "ALTER TABLE UNIT_TMP RENAME TO UNIT";
     table_1 = "ALTER TABLE USER_TMP RENAME TO USER";
     table_4 = "ALTER TABLE USER_PRIV_TMP RENAME TO USER_PRIV";
     
     updateTableBySql(table_1, conn);
     updateTableBySql(table_2, conn);
     updateTableBySql(table_3, conn);
     updateTableBySql(table_4, conn);
     
     String ss = "ALTER TABLE unit ADD COLUMN SEQ_ID INTEGER UNSIGNED NOT NULL";
     updateTableBySql(ss, conn);
     ss = "ALTER TABLE user_priv MODIFY COLUMN FUNC_ID_STR TEXT CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL";
     updateTableBySql(ss, conn);
  }
  public static void unitSync(Connection conn , Connection reportDbConn) throws Exception {
    T9NnitSyncLogic unit = new T9NnitSyncLogic();
    unit.synNunit(conn, reportDbConn);
    T9DepartmentSyncLogic department = new T9DepartmentSyncLogic();
    department.syncDepartment(conn, reportDbConn);
    T9UserPrivSyncLogic userPriv = new T9UserPrivSyncLogic();
    userPriv.syncUserPriv(conn, reportDbConn);
    T9PersonSyncLogic person = new T9PersonSyncLogic();
    person.syncPerson(conn, reportDbConn);
  }
  public static void createOaMenu(Connection conn, String contextPath ) throws Exception {
    String filePath = contextPath  + "core"+ File.separator + "module"+ File.separator + "oa" + File.separator + "oa.sql";
    List<String> sqls = new ArrayList();
    T9FileUtility.loadLine2Array(filePath, sqls);
    Statement stm = null;
    for (String sql : sqls) {
      if (!T9Utility.isNullorEmpty(sql.trim())) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
          sql = sql.substring(0 , sql.length() - 1);
          try {
            stm = conn.createStatement();
            stm.execute(sql);
          } catch (Exception ex) {
            ex.printStackTrace();
          } finally {
            T9DBUtility.close(stm, null, null);
          }
        }
      }
    }
  }
  public static int getMax(Connection conn , String sql) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (Exception ex) {
       throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return 0;
  }
  public static boolean hasSync = getSync();
  public static boolean getSync() {
    if ("1".equals(T9SysProps.getProp("OA_SYNC"))) {
      return true;
    } else {
      return false;
    }
  }
  public static void main(String[] args) throws Exception {
    Connection conn = getOAConn2();
    createUnit(conn);
   // unitSync();
  }
}
