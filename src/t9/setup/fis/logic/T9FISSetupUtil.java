package t9.setup.fis.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;



import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.setup.fis.acset.data.TDAcsetBuildParam;
import t9.setup.fis.acset.data.TDAcsetCreateDatabase;
import t9.setup.fis.acset.data.TDCSystemParams;
import t9.setup.fis.acset.global.TDAcsetConst;
import t9.setup.fis.acset.util.TDDbFileUpdater;
import t9.setup.util.T9ERPSetupUitl;


public class T9FISSetupUtil {
  private static Logger log = Logger.getLogger("cy.t9.setup.fis.logic.T9FISSetupUtil");

  /**
   * 创建默认账套库
   * @param installPath
   * @param contextPath
   * @param dbmsName  财务进销存的数据库类型
   * @param sysdbName 财务进销存的系统库
   * @throws Exception
   */
  public  void createDefaultAcset(String installPath, String contextPath,String dbmsName,String sysdbName) throws Exception {
    String acsetPrefix = T9SysProps.getProp("acsetDbPrefix");
    if(acsetPrefix == null || "".equals(acsetPrefix.trim())){
      acsetPrefix = "TD";
    }
    for (int i = 1; i <= 9; i++) {
      String dbName = acsetPrefix + "0" + i;
      if (T9ERPSetupUitl.isDbActive(dbName,dbmsName)) {
        try {
          T9ERPSetupUitl.backupDb(dbName, "installbackup",dbmsName);
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }
        try {
          T9ERPSetupUitl.forceDropDb(dbName,dbmsName);
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }        
      }
     // deleteDbFile(installPath, dbName);
    }
    Connection sysDbConn = null;
    Connection acsetDbConn = null;
    Statement sysStmt = null;
    Statement acsetStmt = null;
    try {      
      T9DBUtility dbUtil = null;
      dbUtil = new T9DBUtility(dbmsName);
      sysDbConn = dbUtil.getConnection(true, sysdbName);
      //删除原来的账套注册
      sysStmt = sysDbConn.createStatement();
      String sql = "delete from ACCOUNT_SET";
      sysStmt.executeUpdate(sql);
      sql = "delete from SYS_DATABASES where left(DS_NAME, 12)='mssql/acset/'";
      sysStmt.executeUpdate(sql);
      String currTimeStr = T9Utility.getCurDateTimeStr();
      TDCSystemParams sysParams = new TDCSystemParams();
      TDAcsetBuildParam acsetBuildParam = new TDAcsetBuildParam();
      acsetBuildParam.setAccountDbDesc(T9SysProps.getString("shortProductName")+"协同财务演示帐套");
      acsetBuildParam.setPeriodCntrl("1");
      
      String acctYear = currTimeStr.substring(0, 4);
      acsetBuildParam.setMakeYM(currTimeStr.substring(0, 7));
      acsetBuildParam.setAcctYear(acctYear);
      acsetBuildParam.setStartYM(acctYear + "-01");
      acsetBuildParam.setDeptId("1");
      acsetBuildParam.setContextPath(installPath + "\\webroot\\" + contextPath + "\\");
      acsetBuildParam.setRateLength(18);
      acsetBuildParam.setRateDecimalLength(4);
      acsetBuildParam.setMoneyLength(18);
      acsetBuildParam.setMoneyDecimalLength(2);
      acsetBuildParam.setHomeCurrDesc("人民币");
      acsetBuildParam.setHomeCurrSign("￥");
      TDAcsetCreateDatabase.createDatabase(
          sysDbConn,
          sysParams,
          acsetBuildParam,
          dbmsName);
      //插入测试数据
      insertTestData(installPath, contextPath,dbmsName);
      
      //更新余额表和凭证表的会计年度
      String accountDbName = TDAcsetConst.getAcsetDbPrefix() + "01";
      acsetDbConn = dbUtil.getConnection(true, accountDbName);
      acsetStmt = acsetDbConn.createStatement();
      sql = "update ACCTBLNS set ACCT_YEAR='" + acctYear + "'";
      acsetStmt.executeUpdate(sql);
      sql = "update VOUCHER set ACCT_YEAR='" + acctYear + "'";
      acsetStmt.executeUpdate(sql);
      sql = "update VOUCHER set VOUC_DATE='" + acctYear + "-01-01 00:00:00" + "'";
      acsetStmt.executeUpdate(sql);
      sql = "update REPT_TEMPLT set REPT_PATH=replace(REPT_PATH, 'D:\\TD_ERP\\webroot\\fis', '" + installPath + "\\webroot\\" + contextPath + "')";
      acsetStmt.executeUpdate(sql);
      String roleSql = setRoleByTest(T9SysProps.getString("db.jdbc.dbms"), T9SysProps.getProp("t9sysDbName." + T9SysProps.getString("db.jdbc.dbms")), "01");
      sysStmt.executeUpdate(roleSql);
      //关闭期初录入
      closeInit(acsetDbConn);
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
      throw ex;
    }finally {
      T9DBUtility.close(sysStmt, null, log);
      T9DBUtility.closeDbConn(sysDbConn, log);
      T9DBUtility.close(acsetStmt, null, log);
      T9DBUtility.closeDbConn(acsetDbConn, log);
    }
  }

 /**
  * 
  * @param t9dbms
  * @param t9sysdbname
  * @param acsetNo
  * @return
  * @throws Exception
  */
  public String setRoleByTest(String t9dbms,String t9sysdbname,String acsetNo) throws Exception{
    Connection t9sysDbConn = null;
    Statement t9st = null;
    ResultSet t9rs = null;
    T9DBUtility dbutil = null;
    String rtSql = "";
    int adminSeqId = 0;
    try {
      dbutil = new T9DBUtility(t9dbms);
      t9sysDbConn = dbutil.getConnection(false, t9sysdbname);
      t9st = t9sysDbConn.createStatement();
      String sql = "select seq_id from person where user_id='admin'";
      t9rs = t9st.executeQuery(sql);
      if(t9rs.next()){
        adminSeqId = t9rs.getInt(1);
      }
      rtSql = "insert into USER_ACSET(USER_SEQ_ID,ACSET_NO,IF_DEFAULT) values(" + adminSeqId + ",'" + acsetNo + "','" + 1 + "') ";
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(t9st, t9rs, log);
      T9DBUtility.closeDbConn(t9sysDbConn, log);
    }
    return rtSql;
  }
  /**
   * 关闭期初录入
   * @param dbConn
   * @throws Exception
   */
  public  void closeInit(Connection dbConn) throws Exception {
    Statement stmt = null;
    try {
      stmt = dbConn.createStatement();
      String sql = null;
      if (!T9DBUtility.existsTable(dbConn, "ACSETPROPS")) {
        sql = "CREATE TABLE [dbo].[ACSETPROPS] ("
          + "[SEQ_ID] [int] IDENTITY (1, 1) NOT NULL ,"
          + "[PROP_KEY] [varchar] (50) NOT NULL ,"
          + "[PROP_VALUE] [varchar] (200) COLLATE Chinese_PRC_CI_AS NOT NULL"
          + ") ON [PRIMARY]";
        stmt.executeUpdate(sql);
      }
      sql = "delete from ACSETPROPS where PROP_KEY='" + TDAcsetConst.TDFIS_CLOSE_INIT + "'";
      stmt.executeUpdate(sql);
      sql = "insert into ACSETPROPS (PROP_KEY, PROP_VALUE)"
        + " values ('" + TDAcsetConst.TDFIS_CLOSE_INIT + "', '1')";
      stmt.executeUpdate(sql);
    }catch(Exception ex) {      
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 插入测试数据
   */
  private  void insertTestData(String installPath, String contextPath,String dbmsName) {    
    String insertTestDataFiles = T9SysProps.getString("insertSqlTestFiles");
    if (T9Utility.isNullorEmpty(insertTestDataFiles)) {
      return;
    }
    String[] fileArray = insertTestDataFiles.split(",");
    String sqlFilePath = installPath + "\\webroot\\" + contextPath + "\\sqlfiles\\";
    for (int i = 0; i < fileArray.length; i++) {
      try {
        TDDbFileUpdater.exectSqlInfileAsWhole(sqlFilePath + fileArray[i].trim(),dbmsName);
      }catch(Exception ex) {
        log.debug(ex.getMessage(), ex);
      }
    }
  }
}
