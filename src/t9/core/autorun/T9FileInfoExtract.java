package t9.core.autorun;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import t9.core.data.T9DataSources;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.cmd.T9CmdFileUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

/**
 * 文件抽取执行类
 * @author yzq
 *
 */
public class T9FileInfoExtract {
  /**
   * log
   */
  private static final Logger log = Logger.getLogger(
      "yzq.t9.core.autorun.T9FileInfoExtract");
  //文件抽取配置信息类
  private List<T9ModuleFileConf> confList = new ArrayList<T9ModuleFileConf>();
  //最后一次执行检查的时间
  private long lastScanTime = 0;

  /**
   * 家在配置信息
   */
  private void loadConfList() {
    String confFile = T9SysProps.getWebInfPath() + File.separator + "config" + File.separator + "inforesconfig.properties";
    Map<String, String> rawConfMap = new HashMap<String, String>();
    try {
      T9FileUtility.load2Map(confFile, rawConfMap);
      Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "fileInfoSrc");
      
      Iterator<String> iKeys = confMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String dbConfJson = confMap.get(key).trim();
        if (T9Utility.isNullorEmpty(dbConfJson)) {
          continue;
        }
        try {
          confList.add((T9ModuleFileConf)T9FOM.json2Obj(dbConfJson, T9ModuleFileConf.class));
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }
      }
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
    }
  }
  /**
   * 缺省构造函数
   */
  public T9FileInfoExtract() {
    loadConfList();
  }
  
  /**
   * 查找新文件列表
   * @param attachPath
   * @return
   */
  private List findFileList(String attachPath) {
    List rtList = new ArrayList();
    //第一次扫描
    File pathFile = new File(attachPath);
    if (!pathFile.exists() || !pathFile.isDirectory()) {
      return rtList;
    }
    File[] ymDirArray = pathFile.listFiles();
    for (int i = 0; i < ymDirArray.length; i++) {
      File ymDir = ymDirArray[i];
      System.out.println("currDir>>" + ymDir.getAbsolutePath());
      File[] fileArray = ymDir.listFiles();
      for (int j = 0; j < fileArray.length; j++) {
        File file = fileArray[j];
        if (this.lastScanTime != 0 && file.lastModified() < this.lastScanTime) {
          continue;
        }
        String fileName = file.getName();
        if (fileName.length() < 32) {
          continue;
        }
        rtList.add(file);
      }
    }
    
    return rtList;
  }
  
  
  /**
   * 把文件信息插入到数据库中
   * @param stmt
   * @param moduleId
   * @param fileList
   */
  private void inserFile2Db(PreparedStatement preStmt, PreparedStatement preStmtDelete, String moduleId, List fileList) throws Exception {
    for (int i = 0; i < fileList.size(); i++) {
      File file = (File)fileList.get(i);
      String ymDir = file.getParentFile().getName();
      String fileName = file.getName();
      String fileId = null;
      if (ymDir.equalsIgnoreCase("old")) {
        int tmpIndex = fileName.lastIndexOf(".");
        if (tmpIndex < 0) {
          continue;
        }
        fileId = fileName.substring(0, tmpIndex);
      }else {
        if (fileName.length() < 32) {
          continue;
        }
        fileId = ymDir + "_" + fileName.substring(0, 32);
        int tmpIndex = fileName.lastIndexOf(".");
      }
      preStmt.clearParameters();
      preStmtDelete.clearParameters();
      preStmt.setString(1, moduleId);      
      preStmt.setInt(2, 0);
      preStmt.setString(3, fileId);
      preStmt.setString(4, file.getAbsolutePath());
      preStmt.setTimestamp(5, T9Utility.parseTimeStamp(file.lastModified()));
      preStmt.setTimestamp(6, T9Utility.parseTimeStamp(file.lastModified()));
      String abstractStr = T9CmdFileUtility.extractWord(file.getAbsolutePath());
      abstractStr = abstractStr.replace("\r\n", "").replace("\n", "");
      if (abstractStr.length() > 100) {
        abstractStr = abstractStr.substring(0, 100);
      }
      String title = abstractStr;
      if (title.length() > 20) {
        title = title.substring(0, 20);
      }
      preStmt.setString(7, abstractStr);
      preStmt.setString(8, title);
      preStmt.setLong(9, file.length());
      
      preStmtDelete.setString(1, fileId);
      if (i > 0 && i % 1000 == 0) {
        System.out.println("currIndex>>" + i + ">>" + file.getAbsolutePath());
      }
      //先删除，后新增加
      preStmtDelete.executeUpdate();
      preStmt.executeUpdate();
    }
  }
  /**
   * 扫描文件
   * @param dbConn
   */
  public void scanFiles(Connection dbConn) throws Exception {
    scanFiles(dbConn, null, false);
  }
  
  /**
   * 扫描文件
   * @param dbConn
   * @param isClearAll
   */
  public void scanFiles(Connection dbConn, String attachPath, boolean isClearAll) throws Exception {
    long startTime = System.currentTimeMillis();
    PreparedStatement preStmt = null;
    PreparedStatement preStmtDelete = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String sql = null;
      //删除文件
      if (isClearAll) {
        stmt = dbConn.createStatement();
        sql = "delete from FILE_ATTRS01";
        stmt.executeUpdate(sql);
        sql = "delete from FILE_ATTRS02";
        stmt.executeUpdate(sql);
        sql = "delete from SIGN_FILES";
        stmt.executeUpdate(sql);        
        sql = "delete from SYS_PARA where PARA_NAME='INFO_RES_LAST_SCANTIME'";
        stmt.executeUpdate(sql);
        this.lastScanTime = 0;
        stmt.close();
      }
      //获取最后一次扫描的时间
      if (this.lastScanTime == 0) {
        stmt = dbConn.createStatement();
        sql = "select PARA_VALUE from SYS_PARA where PARA_NAME='INFO_RES_LAST_SCANTIME'";
        rs = stmt.executeQuery(sql);
        String lastScanTimeStr = null;
        if (rs.next()) {
          lastScanTimeStr = rs.getString(1);
          if (!T9Utility.isNullorEmpty(lastScanTimeStr)) {
            try {
              this.lastScanTime = Long.parseLong(lastScanTimeStr);
            }catch(Exception ex) {
              log.debug(ex.getMessage(), ex);
            }
          }
        }else {
          //this.lastScanTime == 0，将全部文件都插入的文件中心中
        }
        stmt.close();
      }
      if (T9Utility.isNullorEmpty(attachPath)) {
        attachPath = T9SysProps.getAttachPath();
      }
      preStmt = dbConn.prepareStatement("insert into SIGN_FILES (MODULE_NO,RECORD_ID,FILE_ID,FILE_PATH,CREATE_TIME,UPDATE_TIME, ABSTRACT, TITLE,FILE_SIZE) values (?,?,?,?,?,?,?,?,?)");
      preStmtDelete = dbConn.prepareStatement("delete from SIGN_FILES where FILE_ID=?");
      for (int i = 0; i < this.confList.size(); i++) {
        T9ModuleFileConf conf = (T9ModuleFileConf)this.confList.get(i);
        List fileList = this.findFileList(attachPath + File.separator + conf.getModuleNo());
        inserFile2Db(preStmt, preStmtDelete, conf.getModuleNo(), fileList);
      }
      
      //成功后，更新扫描时间
      stmt = dbConn.createStatement();
      sql = "delete from SYS_PARA where PARA_NAME='INFO_RES_LAST_SCANTIME'";
      stmt.executeUpdate(sql);
      sql = "insert into SYS_PARA (PARA_NAME, PARA_VALUE) values ('INFO_RES_LAST_SCANTIME', '" + String.valueOf(startTime) + "')";
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(preStmt, rs, log);
      T9DBUtility.close(preStmtDelete, null, log);
      T9DBUtility.close(stmt, null, log);
      this.lastScanTime = startTime;
    }
  }
  
  /**
   * 更新文件的时间和文件中心数据中的CREATE_TIME/UPDATE_TIME
   * @param dbConn
   * @param path
   * @throws Exception
   */
  public void updateFileTime(Connection dbConn, String path) throws Exception {
    File pathFile = new File(path);
    if (!pathFile.isDirectory()) {
      return;
    }
    PreparedStatement preStmt = null;
    try {
      preStmt = dbConn.prepareStatement("update SIGN_FILES set CREATE_TIME=?,UPDATE_TIME=? where FILE_ID=?");
      
      String[] moduleArray = new String[]{"email", "file_folder", "news", "notify"};
      for (int i = 0; i < moduleArray.length; i++) {
        String module = moduleArray[i];
        String modulePath = pathFile + File.separator + module;
        File modulePathFile = new File(modulePath);
        if (!modulePathFile.exists() || !modulePathFile.isDirectory()) {
          continue;
        }
        String[] fileNameArray = modulePathFile.list();
        for (int j = 0; j < fileNameArray.length; j++) {
          String fileName = fileNameArray[j];
          if (!Pattern.matches("\\d{4}", fileName)) {
            continue;
          }
          updateFileTimeInOnDir(preStmt, modulePath + File.separator + fileName);
        }
      }
    }catch(Exception ex) {        
    }finally {
      T9DBUtility.close(preStmt, null, log);
    } 
  }
  /**
   * 更新文件的时间和文件中心数据中的CREATE_TIME/UPDATE_TIME
   * @param dbConn
   * @param path
   * @throws Exception
   */
  public void deleteHtmlFlag(Connection dbConn) throws Exception {
    List recordList = new ArrayList();
    PreparedStatement preStmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      preStmt = dbConn.prepareStatement("update SIGN_FILES set TITLE=?, ABSTRACT=? where SEQ_ID=?");
      
      rs = stmt.executeQuery("select SEQ_ID, TITLE, ABSTRACT from SIGN_FILES order by SEQ_ID");
      while (rs.next()) {
        String seqId = rs.getString(1);
        String title = T9Utility.null2Empty(rs.getString(2));
        String sbt = T9Utility.null2Empty(rs.getString(3));
        
        title = truncateHtml(title);
        sbt = truncateHtml(sbt);
        title = title.replace("\\r\\n", "").replace("\\n", "");
        sbt = sbt.replace("\\r\\n", "").replace("\\n", "");
        recordList.add(new String[]{seqId, title, sbt});
      }
      for (int i = 0; i < recordList.size(); i++) {
        String[] record = (String[])recordList.get(i);
        preStmt.clearParameters();
        preStmt.setString(1, record[1]);
        preStmt.setString(2, record[2]);
        preStmt.setInt(3, Integer.parseInt(record[0]));
        preStmt.executeUpdate();
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(preStmt, null, log);
      T9DBUtility.close(stmt, rs, log);
    } 
  }
  
  public static String truncateHtml(String srcStr) {
    int p1 = srcStr.indexOf("<");
    if (p1 < 0) {
      return srcStr;
    }
    int p2 = srcStr.indexOf(">", p1);
    while (p1 > 0 && p2 > p1) {
      String t1 = srcStr.substring(0, p1);
      if (p2 < srcStr.length() - 1) {
        srcStr = t1 + srcStr.substring(p2 + 1);
      }else {
        srcStr = t1;
      }
      
      p1 = srcStr.indexOf("<", p2);
      p2 = srcStr.indexOf(">", p1);
    }
    srcStr = srcStr.replaceAll("<[a-zA-Z]+", "");
    return srcStr;
  }
  /**
   * 更新单个目录下的时间
   * @param dbConn
   * @param path
   * @throws Exception
   */
  private void updateFileTimeInOnDir(PreparedStatement preStmt, String path) throws Exception {
    
    File pathFile = new File(path);
    if (!pathFile.exists() || !pathFile.isDirectory()) {
      return;
    }
    long yearSpan = T9Const.DT_Y * 3;
    long currTime = System.currentTimeMillis();
    String[] fileNameArray = pathFile.list();
    for (int i = 0; i < fileNameArray.length; i++) {
      String fileName = fileNameArray[i];
      if (!Pattern.matches("^\\w{32}.+", fileName)) {
        continue;
      }
      File file = new File(path + File.separator + fileName);
      long rdNum = (long)(yearSpan * Math.random());
      long rdTime = currTime - rdNum;
      file.setLastModified(rdTime);
      String fileId = path.substring(path.lastIndexOf(File.separator) + 1) + "_"
        + fileName.substring(0, 32);
      preStmt.clearParameters();
      preStmt.setDate(1, new java.sql.Date(rdTime));
      preStmt.setDate(2, new java.sql.Date(rdTime));
      preStmt.setString(3, fileId);
      
      preStmt.executeUpdate();
    }
  }
  
  /**
   * 把多个测试文件分发到各个模块的路径之下
   * @param pathFrom
   * @param pathTo
   * @throws Exception
   */
  public void updateFileName(String attachPath) throws Exception {
    File attapchPathFile = new File(attachPath);
    
    if (!attapchPathFile.isDirectory()) {
      return;
    }
    String[] moduleArray = new String[]{"email", "file_folder", "news", "notify"};
    String[] ymArray = new String[] {
        "0901", "0902", "0903", "0904", "0905", "0906", "0907", "0908", "0909", "0910", "0911", "0912",
        "1001", "1002", "1003", "1004", "1005", "1006", "1007", "1008", "1009", "1010", "1011", "1012",
        "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112"};

    for (int i = 0; i < moduleArray.length; i++) {
      for (int j = 0; j < ymArray.length; j++) {
        String path = attachPath + File.separator + moduleArray[i] + File.separator + ymArray[j];
        File pathFile = new File(path);
        String[] fileArray = pathFile.list();
        if (fileArray == null) {
          continue;
        }
        for (int k = 0; k < fileArray.length; k++) {
          String fileName = fileArray[i];
          if (fileName.length() < 66) {
            continue;
          }
          File currFile = new File(path + File.separator + fileName);
          currFile.renameTo(new File(path + File.separator + fileName.substring(33)));
        }
      }
    }
  }
 
  /**
   * 把多个测试文件分发到各个模块的路径之下
   * @param pathFrom
   * @param pathTo
   * @throws Exception
   */
  public void dispatchFiles(String pathFrom, String pathTo) throws Exception {
    File fileFrom = new File(pathFrom);
    File fileTo = new File(pathTo);
    
    if (!fileFrom.isDirectory() || !fileTo.isDirectory()) {
      return;
    }
    String[] moduleArray = new String[]{"email", "file_folder", "news", "notify"};
    String[] fileArray = fileFrom.list();
    String[] ymArray = new String[] {
        "0901", "0902", "0903", "0904", "0905", "0906", "0907", "0908", "0909", "0910", "0911", "0912",
        "1001", "1002", "1003", "1004", "1005", "1006", "1007", "1008", "1009", "1010", "1011", "1012",
        "1101", "1102", "1103", "1104", "1105", "1106", "1107", "1108", "1109", "1110", "1111", "1112"};

    for (int i = 0; i < fileArray.length; i++) {
      String filePathFrom = pathFrom + File.separator + fileArray[i];
      String moduleName = moduleArray[i % moduleArray.length];
      String ymDir = ymArray[i % 36];
      String filePathTo = pathTo + File.separator + moduleName + File.separator + ymDir + File.separator
        + fileArray[i];
      if (i > 0 && i % 1000 == 0) {
        System.out.println("currIndex>>" + i);
      }
      T9FileUtility.copyFile(filePathFrom, filePathTo);
    }
  }
  
  /**
   * 抽取文件信息到文件中心
   */
  public void doTask() {
    System.out.println("T9FileInfoExtract doTask Run" + T9Utility.getCurDateTimeStr());
    if (true) {
      return;
    }
    Connection dbConn = null;
    try {
      DataSource ds = T9DataSources.getDataSource(T9SysProps.getSysDbDsName());
      if (ds == null) {
        return;
      }
      dbConn = ds.getConnection();
      if (dbConn == null) {
        return;
      }
      dbConn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
      scanFiles(dbConn);
      dbConn.commit();
    }catch(Exception ex) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      log.debug(ex.getMessage(), ex); 
    }finally {
      T9DBUtility.closeDbConn(dbConn, log);
    }
  }
}
