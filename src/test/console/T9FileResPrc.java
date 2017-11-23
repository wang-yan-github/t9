package test.console;

import java.sql.Connection;

import t9.core.autorun.T9FileInfoExtract;
import t9.core.global.T9SysProps;
import t9.core.load.T9ConfigLoader;
import t9.core.util.T9TimeCounter;
import t9.core.util.db.T9DBUtility;
import t9.subsys.inforesouce.db.T9MetaDbHelper;

public class T9FileResPrc {
  static {
    String installPath = "D:\\project\\t9";
    //加载数据库配置信息
    String sysConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\sysconfig.properties";
    T9SysProps.setProps(T9ConfigLoader.loadSysProps(sysConfFile));
    String selfConfFile = installPath + "\\webroot\\t9\\WEB-INF\\config\\selfconfig.properties";
    T9SysProps.addProps(T9ConfigLoader.loadSysProps(selfConfFile));
  }
  public static void main(String[] args) throws Exception {
    T9TimeCounter t = new T9TimeCounter();
    
    //generateMetaData("TD_OA5");
//    dispatchFiles("D:\\tmp\\splitexcel\\output", "D:\\T9\\attach2");
//    dispatchFiles("D:\\tmp\\txt2word\\output", "D:\\T9\\attach3");
//    t.logTime("dispatchFiles>>");
//    fillSignFiles("TD_OA5", "D:\\T9\\attach2");
    //fillSignFiles("TD_OA6", "D:\\T9\\attach3");
//    t.logTime("fillSignFiles>>");
//    updateFileTime("TD_OA5", "D:\\T9\\attach2");
    
    deleteHtmlFlag("TD_OA6");
    t.logTime("updateFileTime>>");
    t.logTime("costTime>>");
  }
  /**
   * 更新文件时间
   * @throws Exception
   */
  public static void updateFileTime(String dbName, String attachPath) throws Exception {
    Connection dbConn = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, dbName);
      
      T9FileInfoExtract ext = new T9FileInfoExtract();
      ext.updateFileTime(dbConn, attachPath);
      
      dbConn.commit();
    } catch (Exception e) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      e.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  /**
   * 生成测试元数据
   */
  public static void generateMetaData(String dbName) {
    Connection dbConn = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, dbName);
      
      T9MetaDbHelper help = new T9MetaDbHelper();
      help.genTestMetadata(dbConn);
      
      dbConn.commit();
    } catch (Exception e) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      e.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  
  /**
   * 随机文件分发
   * @throws Exception
   */
  public static void dispatchFiles(String fromPath, String toPath) throws Exception {
    T9FileInfoExtract ext = new T9FileInfoExtract();
    ext.dispatchFiles(fromPath, toPath);
  }
  
  /**
   * 填充文件中心表
   * @param dbName
   * @param attachPath
   */
  public static void fillSignFiles(String dbName, String attachPath) {
    Connection dbConn = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, dbName);
      T9FileInfoExtract ext = new T9FileInfoExtract();
      ext.scanFiles(dbConn, attachPath, true);
      
      dbConn.commit();
    } catch (Exception e) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      e.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
  
  /**
   * 填充文件中心表
   * @param dbName
   * @param attachPath
   */
  public static void deleteHtmlFlag(String dbName) {
    Connection dbConn = null;
    try {
      T9DBUtility dbUtil = new T9DBUtility();
      dbConn = dbUtil.getConnection(false, dbName);
      T9FileInfoExtract ext = new T9FileInfoExtract();
      ext.deleteHtmlFlag(dbConn);
      
      dbConn.commit();
    } catch (Exception e) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      e.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
    }
  }
}
