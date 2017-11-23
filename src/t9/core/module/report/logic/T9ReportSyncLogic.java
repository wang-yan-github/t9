package t9.core.module.report.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import t9.core.data.T9Organization;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.global.T9SysProps;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9ReportSyncLogic {
  public static String REPORT_DB_NAME = T9SysProps.getProp("REPORT_DATABASE_NAME");
  public static String REPORT_DB_URL = T9SysProps.getProp("report.conurl.mysql");
  public static String REPORT_DB_USER = T9SysProps.getProp("report.userName.mysql");
  public static String REPORT_DB_PWD = T9SysProps.getProp("report.passward.mysql");
  
  public static Connection getReportConn() throws Exception {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(REPORT_DB_URL + REPORT_DB_NAME, REPORT_DB_USER, REPORT_DB_PWD);
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return conn;
  }
  public static Connection getReportConn2() throws Exception {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3396/crscell", "root", "myoa888");
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
  public static void createReportMenu(Connection conn) throws Exception {
    String menuId = "insert into sys_menu (menu_id,menu_name,image) values ('91','报表系统','org.gif')";
    updateTableBySql(menuId ,conn );
    String functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('9101','报表管理','@2/general/reportshop/workshop/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
     functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('9110','模板管理','org','org.gif','0')";
    updateTableBySql(functionId ,conn );
     functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911001','设计模板','@2/general/reportshop/design/report/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
     functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911005','模板分类','@2/general/reportshop/console/repkind/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
     functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911015','基础代码','@2/general/reportshop/console/code/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
     functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911020','自动编号','@2/general/reportshop/console/autocode/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
    functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911025','数据源','@2/general/reportshop/console/database/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
    functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911030','系统工具','@2/general/reportshop/system/exchange/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
    functionId = "INSERT INTO sys_function (menu_id, func_name, func_code, icon, open_flag) VALUES ('911035','注册信息','@2/general/reportshop/system/reg_view/index.php','org.gif','0')";
    updateTableBySql(functionId ,conn );
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
    if ("1".equals(T9SysProps.getProp("REPORT_SYNC"))) {
      return true;
    } else {
      return false;
    }
  }
  public static void main(String[] args) throws Exception {
    Connection conn = getReportConn2();
    createUnit(conn);
   // unitSync();
  }
}
