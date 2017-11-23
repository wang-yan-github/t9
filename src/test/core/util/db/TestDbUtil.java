package test.core.util.db;

import java.sql.Connection;

import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.db.T9DBUtility;

/**
 * @author Administrator
 */
public class TestDbUtil {
  private static String sysConfigFile = "D:\\project\\test\\sysconfig.properties";
  
  static {
    T9SysProps.setProps(T9ConfigLoader.loadSysProps(sysConfigFile));
  }
  public static Connection getConnection(boolean autoCommit, String dbName) throws Exception {
    T9DBUtility dbUtil = new T9DBUtility();
    return dbUtil.getConnection(autoCommit, dbName);
  }
}
