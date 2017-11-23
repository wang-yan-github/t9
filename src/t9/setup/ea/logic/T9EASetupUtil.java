package t9.setup.ea.logic;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.auth.T9Authenticator;
import t9.setup.util.T9ERPSetupUitl;


/**
 * 进销存安装处理
 * 
 * @author tulaike
 * 
 */
public class T9EASetupUtil {

  private static Logger log = Logger.getLogger("cy.t9.setup.ea.logic.T9EASetupUtil");

  public T9EASetupUtil() {
    // TODO Auto-generated constructor stub
  }
  private String dbmsName = null;
  private Map<String, String> dbConfigPopsMap = null;
  /**
   * 构造方法
   * @param paramMap
   * @param dbmsName
   * @throws Exception 
   */
  public T9EASetupUtil(Map paramMap,String dbmsName,String contextPath) throws Exception{
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
   * 创建进销存的数据库 -- 安装TDERP库
   * 1.安装TDERP数据库
   * 2.注册TDERP到TDSYS
   * 
   * @throws Exception 
   */
  public void createErpDb(String erpcontextPath) throws Exception {
   // tdErpDbSqlFiles
    String installPath = T9SysProps.getRootPath();
    String path = installPath + "\\webroot\\" + erpcontextPath + "\\sqlfiles\\";
    String tdErpDbSqlFiles = T9SysProps.getProp("tdErpDbSqlFiles");
    String tdErpRegDbSqlFiles = T9SysProps.getProp("tdErpRegDbSqlFiles");
    String t9MenuRegDbSqlFiles = T9SysProps.getProp("t9MenuEaRegDbSqlFiles." + T9SysProps.getString("db.jdbc.dbms"));
    String t9sysDbName = T9SysProps.getProp("t9sysDbName." + T9SysProps.getString("db.jdbc.dbms"));
    String[] strSrc = tdErpDbSqlFiles.split(",");
    String[] strRegSrc = tdErpRegDbSqlFiles.split(",");
    String[] strMenuSrc = t9MenuRegDbSqlFiles.split(",");
    if (T9ERPSetupUitl.isDbActive("TDERP",dbmsName)) {
      try {
        T9ERPSetupUitl.backupDb("TDERP", "installbackup",dbmsName);
      }catch(Exception ex) {
        log.debug(ex.getMessage(), ex);
      }
      try {
        T9ERPSetupUitl.forceDropDb("TDERP",dbmsName);
      }catch(Exception ex) {
        log.debug(ex.getMessage(), ex);
      }        
    }
    T9ERPSetupUitl.executionSQLFile(path, "TDERP", strSrc);//创建TDERP数据库
    T9ERPSetupUitl.regEaDbms2TDSYS(path, "TDSYS", strRegSrc);//注册TDSYS数据库
    T9ERPSetupUitl.deleteErpMenu2T9("89",t9sysDbName);
    insertErpMeun2T9(path, strMenuSrc,t9sysDbName);
  }
  

  /**
   * 向T9中插入erp的菜单
   * @throws Exception 
   */
  public void insertErpMeun2T9(String path,String[] strSrc,String sysDbName) throws Exception {
    for (int i = 0; i < strSrc.length; i++) {
      String filePath = path + strSrc[i];
      T9ERPSetupUitl.exeSql(filePath,T9SysProps.getProp("db.jdbc.dbms"),sysDbName);
    }
  }

  /**
   * 清除数据库
   */
  public void clearEaDbInfo(){
    
  }
  
}
