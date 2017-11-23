package t9.setup.fis.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.auth.T9Authenticator;
import t9.setup.ea.logic.T9EASetupUtil;
import t9.setup.util.T9ERPSetupUitl;

public class T9FISSetupLogic {
  private static Logger log = Logger.getLogger("cy.t9.setup.fis.logic.T9FISSetupLogic");

  private String dbmsName = null;
  private Map<String, String> dbConfigPopsMap = null;

  /**
   * 构造方法
   * @param paramMap
   * @param dbmsName
   * @param contextPath
   * @throws Exception
   */
  public T9FISSetupLogic(Map paramMap,String dbmsName,String contextPath) throws Exception{
    String installPath = T9SysProps.getRootPath();
    String t9erpsetupConfFile = installPath + "\\webroot\\" + contextPath + "\\WEB-INF\\config\\erp\\t9erpsetup.properties";
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
  }
  /**
   * 设置数据库的配置项
   * @param paramMap
   * @param dbmsName
   */
  public void setDbPops(Map paramMap){
    T9SysProps.addProps(paramMap);
  }
  /**
   * 创建财务账套的数据库 -- 安装TD01,TD02...库
   * 1.安装TD01,TD02...数据库
   * 2.注册TD01到TDSYS
   * @param erpcontextPath  财务安装路径
   * @param sysdbName 财务系统库，TDSYS
   * @throws Exception 
   */
  public void createFisDb(String erpcontextPath,String sysdbName) throws Exception {
   // tdErpDbSqlFiles
    String installPath = T9SysProps.getRootPath();
    T9FISSetupUtil fisu = new T9FISSetupUtil();
    fisu.createDefaultAcset(installPath, erpcontextPath, dbmsName, sysdbName);
  }
  
  /**
   * 向T9中插入erp的菜单
   * @throws Exception 
   */
  public void insertErpMeun2T9(String path,String[] strSrc,String sysDbName) throws Exception {
    T9ERPSetupUitl.deleteErpMenu2T9("87",sysDbName);
    T9EASetupUtil easu  =  new T9EASetupUtil();
    easu.insertErpMeun2T9(path, strSrc,sysDbName);
  }
  
  /**
   * 向T9中插入erp的菜单
   * @throws Exception 
   */
  public void insertErpMeun2T9(String erpcontextPath) throws Exception {
    String installPath = T9SysProps.getRootPath();
    String path = installPath + "\\webroot\\" + erpcontextPath + "\\sqlfiles\\";
    String t9MenuRegDbSqlFiles = T9SysProps.getProp("t9MenuFisRegDbSqlFiles." + T9SysProps.getString("db.jdbc.dbms"));
    String t9sysDbName = T9SysProps.getProp("t9sysDbName." + T9SysProps.getString("db.jdbc.dbms"));

    String[] strMenuSrc = t9MenuRegDbSqlFiles.split(",");
    insertErpMeun2T9(path, strMenuSrc,t9sysDbName);
  }
  
  public void clearFisDbInfo(){
    
  }
}
