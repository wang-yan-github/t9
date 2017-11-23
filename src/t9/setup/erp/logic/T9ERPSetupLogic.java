package t9.setup.erp.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.auth.T9Authenticator;
import t9.core.util.file.T9PropUtility;
import t9.setup.util.T9ERPSetupUitl;

public class T9ERPSetupLogic {
  private static Logger log = Logger.getLogger("cy.t9.setup.erp.logic.T9ERPSetupLogic");

  /**
   * 创建erp运行平台的数据库 1.判断TDSYS库是否存在，如果存在不按装，否则需要创建TDSYS
   * 1.创建TDSYS数据库
   * 2.注册T9数据库到TDSYS
   * @throws Exception 
   */
  public  void createSysDb(String erpcontextPath) throws Exception {
    String installPath = T9SysProps.getRootPath();
    //String installPath = "D";
    String path = installPath + "\\webroot\\" + erpcontextPath + "\\sqlfiles\\";
    String tdSysDbSQLFILES = T9SysProps.getProp("tdSysDbSQLFILES");
    String t9dbRegDbSqlFiles = T9SysProps.getProp("t9dbRegDbSqlFiles." + T9SysProps.getProp("db.jdbc.dbms"));
    String[] strt9RegSrc = t9dbRegDbSqlFiles.split(",");

    String strSrc[] = tdSysDbSQLFILES.split(",");
    if (T9ERPSetupUitl.isDbActive("TDSYS",dbmsName)) {
      try {
        T9ERPSetupUitl.backupDb("TDSYS", "installbackup",dbmsName);
      }catch(Exception ex) {
        log.debug(ex.getMessage(), ex);
      }
      try {
        T9ERPSetupUitl.forceDropDb("TDSYS",dbmsName);
      }catch(Exception ex) {
        log.debug(ex.getMessage(), ex);
      }        
    }
    T9ERPSetupUitl.executionSQLFile(path, "TDSYS", strSrc);
    T9ERPSetupUitl.regEaDbms2TDSYS(path, "TDSYS", strt9RegSrc); //注册T9数据库
  }
  
  private String dbmsName = null;
  private Map<String, String> dbConfigPopsMap = null;
  /**
   * 构造方法
   * @param paramMap
   * @param dbmsName
   * @throws Exception 
   */
  public T9ERPSetupLogic(Map paramMap,String dbmsName,String contextPath,String erpContextPath) throws Exception{
    String installPath = T9SysProps.getRootPath();
    String t9erpsetupConfFile = installPath + "\\webroot\\" + contextPath + "\\WEB-INF\\config\\erp\\t9erpsetup.properties";
    
    String t9erpSysConfFile = installPath + "\\webroot\\" + erpContextPath + "\\WEB-INF\\config\\sysconfig.properties";
    String t9SysConfFile = installPath + "\\webroot\\" + contextPath + "\\WEB-INF\\config\\sysconfig.properties";

    T9SysProps.addProps(T9ConfigLoader.loadSysProps(t9erpsetupConfFile));
    
    this.dbmsName = dbmsName;
    dbConfigPopsMap = new HashMap<String, String>();
    
    String conIp = ((String[])paramMap.get("conIp"))[0];
    String conPort = ((String[])paramMap.get("conPort"))[0];
    String driver = ((String[]) paramMap.get("driver"))[0];
    String conurl = "";
    String userName = ((String[]) paramMap.get("userName"))[0];
    String passward = ((String[]) paramMap.get("passward"))[0];
    passward = T9Authenticator.ciphEncryptStr(passward);
    conurl = "jdbc:microsoft:sqlserver://" + conIp + ":" + conPort + ";";
    dbConfigPopsMap.put("db.jdbc.driver." + dbmsName,driver);
    dbConfigPopsMap.put("db.jdbc.conurl." + dbmsName,conurl);
    dbConfigPopsMap.put("db.jdbc.userName." + dbmsName,userName);
    dbConfigPopsMap.put("db.jdbc.passward." + dbmsName,passward);
    setDbPops(dbConfigPopsMap);
    dbConfigPopsMap = addT9DbConfig(dbConfigPopsMap);
    T9SysProps.updateProp("useT9Erp", "1");
//    dbConfigPopsMap.put("useT9Erp", "1");
    T9PropUtility.updateProp(t9SysConfFile, "useT9Erp", "1");
    T9ERPSetupUitl.modifySysConfig(dbConfigPopsMap, t9erpSysConfFile);
  }
  
  public  static Map<String, String> addT9DbConfig(Map<String, String> dbConfigPopsMap){
    String t9Dbms = T9SysProps.getString("db.jdbc.dbms");
    dbConfigPopsMap.put("db.jdbc.driver." + t9Dbms,T9SysProps.getString("db.jdbc.driver." + t9Dbms));
    dbConfigPopsMap.put("db.jdbc.conurl." + t9Dbms,T9SysProps.getString("db.jdbc.conurl." + t9Dbms));
    dbConfigPopsMap.put("db.jdbc.userName." + t9Dbms,T9SysProps.getString("db.jdbc.userName." + t9Dbms));
    dbConfigPopsMap.put("db.jdbc.passward." + t9Dbms,T9SysProps.getString("db.jdbc.passward." + t9Dbms));
    return dbConfigPopsMap;
  }
  
  /**
   * 设置数据库的配置项
   * @param paramMap
   * @param dbmsName
   */
  public void setDbPops(Map paramMap){
    T9SysProps.addProps(paramMap);
  }
}
