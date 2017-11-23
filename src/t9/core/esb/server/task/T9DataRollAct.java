package t9.core.esb.server.task;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.server.act.T9RangeUploadAct;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.task.T9EsbServerTasksMgr;
import t9.core.esb.server.taskstatus.logic.T9TaskStatusLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
public class T9DataRollAct {
  public String roll(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String time = request.getParameter("time");
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date time1 = sdf.parse(time);
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");  
      String timeStr = sdf1.format(time1);
      this.doTask(dbConn, timeStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "归档成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String deleteMsg(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null;
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      PreparedStatement ps = null;
      try {
        ps = dbConn.prepareStatement("delete from ESB_SYS_MSG WHERE TO_ID = '' OR TO_ID IS NULL");
        ps.executeUpdate();
      } catch (Exception ex) {
        throw ex;
      } finally{
        T9DBUtility.close(ps, null, null);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "清理成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getDataCount(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      String str = this.getCount(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "备份成功");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public static final String ESB_TRANSFER_TABLE_PRE = "ESB_TRANSFER";
  public static final String ESB_TRANSFER_STATUS_TABLE_PRE = "ESB_TRANSFER_STATUS";
  public static final String ESB_SYS_MSG = "ESB_SYS_MSG";
  private static final Logger log = Logger.getLogger("t9.core.esb.server.task.T9EsbServerTasksBack");
  public void doTask(Connection conn , String time ) {
    try {
      if (!hasBack(conn , time)) {
        //创建表复制数据
        this.createTable(conn);
        //改表名
        this.alertTableName(conn, time);
        this.alertTableName2(conn);
        
        this.saveInfo(conn, time, new Date());
      }
    } catch (Exception e) {
      log.debug(e.getMessage(),e);
      e.printStackTrace();
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
  }
  public static String fileDir( String time) {
    String UPLOAD_PATH = PropertiesUtil.getUploadPath();
    String fileName = "";
    if (!UPLOAD_PATH.endsWith(File.separator)) {
      fileName = UPLOAD_PATH  + time; 
    } else {
      UPLOAD_PATH = UPLOAD_PATH.substring(0 , UPLOAD_PATH.length() - 1);
      fileName = UPLOAD_PATH  + time; 
    }
    File file = new File(fileName);
    if (!file.exists()) {
      file.mkdirs();
    }
    return fileName;
  }
  
  public void backFile(Connection conn , String time) throws Exception {
     String fileName = fileDir(time);
     String sql = "select file_path, guid from " + ESB_TRANSFER_TABLE_PRE + "_" + time + " where STATUS = '" + T9EsbServerLogic.TRANSFER_STATUS_ALLCOMPLETE + "'";
     Statement stm = null;
     ResultSet rs = null;
     try {
       stm = conn.createStatement();
       rs = stm.executeQuery(sql);
       while (rs.next()) {
         String filePath = rs.getString("file_path");
         String guid = rs.getString("guid");
         moveFile(filePath ,guid, fileName);
       }
     } catch(Exception ex) {
       throw ex;
     } finally {
       T9DBUtility.close(stm, rs, null); 
     }
  }
  public static void moveFile(String filePath , String guid ,  String dir ) throws Exception {
    String name = T9FileUtility.getFileName(filePath);
    dir += File.separator + guid + File.separator + name;
    T9FileUtility.copyFile(filePath, dir);
    File file2 = new File(filePath);
    if (file2.exists()) {
      File dir2 = file2.getParentFile();
      file2.delete();
      if (dir2.exists()) {
        dir2.delete();
      }
    }
  }
  public String getCount(Connection conn) throws Exception {
    String sql = "select count(*) from ESB_TRANSFER";
    int esbTrCount = this.getCount(conn, sql);
     sql = "select count(*) from ESB_TRANSFER_STATUS";
     int esbTrStCount = this.getCount(conn, sql);
     sql = "select count(*) from ESB_SYS_MSG";
     int esbMsg = this.getCount(conn, sql);
     String str = "{ESB_TRANSFER:" + esbTrCount + ",ESB_TRANSFER_STATUS:" + esbTrStCount + ",ESB_SYS_MSG:" + esbMsg + "}";
     return str;
  }
  public int getCount(Connection conn , String sql) throws Exception {
    Statement stm = null;
    ResultSet rs = null;
    int seqId = 0;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      } 
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return 0;
  }
  
  public static void main(String[] args) throws Exception {
    String dir = T9EsbServerTasksBack.fileDir("201109");
    System.out.println(dir);
    //T9EsbServerTasksBack.moveFile("d:\\cache\\esb更新.txt" , "d:\\cache111");
  }
  public boolean hasBack(Connection conn , String time) throws Exception {
    String sql = "select 1 from backup_info where TABLE_NAME = '" + ESB_TRANSFER_TABLE_PRE + "_" + time + "'";
    Statement stm = null;
    ResultSet rs = null;
    int seqId = 0;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(sql);
      if (rs.next()) {
        return true;
      } else {
        return false;
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
  }
  public void saveInfo (Connection conn , String time , Date date) throws Exception {
    this.saveBackupInof(conn, ESB_TRANSFER_TABLE_PRE, ESB_TRANSFER_TABLE_PRE + "_" + time, date);
    this.saveBackupInof(conn, ESB_TRANSFER_STATUS_TABLE_PRE, ESB_TRANSFER_STATUS_TABLE_PRE + "_" + time, date);
    this.saveBackupInof(conn, ESB_SYS_MSG, ESB_SYS_MSG + "_" + time, date);
  }
  public  void saveBackupInof(Connection conn , String table , String reTable , Date date) throws Exception {
    String sql = "insert into backup_info (TYPE , DATETIME , TABLE_NAME) VALUES ('"+table+"',? , '"+reTable+"')" ;
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      Timestamp ts = new Timestamp(date.getTime());
      ps.setTimestamp(1, ts);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  public void alertTableName(Connection conn , String time) throws Exception {
    String table_1 = "alter table "+ ESB_TRANSFER_TABLE_PRE + " rename to " + ESB_TRANSFER_TABLE_PRE + "_" + time;
    String table_2 = "alter table "+ ESB_TRANSFER_STATUS_TABLE_PRE + " rename to " + ESB_TRANSFER_STATUS_TABLE_PRE + "_" + time;
    String table_3 = "alter table "+ ESB_SYS_MSG + " rename to " + ESB_SYS_MSG + "_" + time;
    
    updateTableBySql(table_1, conn);
    updateTableBySql(table_2, conn);
    updateTableBySql(table_3, conn);
  }
  public void alertTableName2(Connection conn) throws Exception {
    String table_1 = "alter table "+ ESB_TRANSFER_TABLE_PRE + "_tmp rename to " + ESB_TRANSFER_TABLE_PRE ;
    String table_2 = "alter table "+ ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp rename to " + ESB_TRANSFER_STATUS_TABLE_PRE;
    String table_3 = "alter table "+ ESB_SYS_MSG + "_tmp rename to " + ESB_SYS_MSG ;
    
    updateTableBySql(table_1, conn);
    updateTableBySql(table_2, conn);
    updateTableBySql(table_3, conn);
  }
  
  public void createTable(Connection conn) throws Exception {
    try {
      String table_1_1 = "drop table " + ESB_TRANSFER_TABLE_PRE + "_tmp";
      String table_2_1 = "drop table " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp";
      String table_3_1 = "drop table " + ESB_SYS_MSG + "_tmp";
      updateTableBySql(table_1_1, conn);
      updateTableBySql(table_2_1, conn);
      updateTableBySql(table_3_1, conn);
    } catch (Exception ex) {
      
    }
    //create table esb_transfer_tmp as select * from esb_transfer where STATUS <> '3'
    //create table esb_transfer_status_tmp as select * from esb_transfer_status b where where b.TRANS_ID in (select a.GUID from esb_transfer a where a.status <> '3'
    //create table esb_sys_msg_tmp as select * from esb_sys_msg where status <> '1'
    //alter table esb_transfer rename to esb_transfer_20120919
    //alter table esb_transfer_status rename to esb_transfer_status_20120919
    //alter table esb_sys_msg rename to esb_sys_msg_20120919
    //alter table esb_transfer_tmp rename to esb_transfer
    //alter table esb_transfer_status_tmp rename to esb_transfer_status
    //alter table esb_sys_msg_tmp rename to esb_sys_msg
    String table_1 = "create table " + ESB_TRANSFER_TABLE_PRE + "_tmp as select * from " + ESB_TRANSFER_TABLE_PRE  + " where STATUS <> '" + T9EsbServerLogic.TRANSFER_STATUS_ALLCOMPLETE + "'";
    String table_2 = "create table "  + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp as select * from " + ESB_TRANSFER_STATUS_TABLE_PRE + " b  where b.TRANS_ID in (select a.GUID from " + ESB_TRANSFER_TABLE_PRE  + " a where  a.STATUS <>'" + T9EsbServerLogic.TRANSFER_STATUS_ALLCOMPLETE + "')";
    String table_3 = "create table " + ESB_SYS_MSG + "_tmp as select * from " + ESB_SYS_MSG + " where STATUS <>'1'";
    
    updateTableBySql(table_1, conn);
    updateTableBySql(table_2, conn);
    updateTableBySql(table_3, conn);
    
    String alter1= "ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp MODIFY COLUMN seq_id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY (seq_id)";
    updateTableBySql2(alter1, conn);
    String alter2= "ALTER TABLE " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp MODIFY COLUMN seq_id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY (seq_id)";
    updateTableBySql2(alter2, conn);
    String alter3= "ALTER TABLE " + ESB_SYS_MSG + "_tmp MODIFY COLUMN seq_id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,ADD PRIMARY KEY (seq_id)";
    updateTableBySql2(alter3, conn);
    String addIndex1= " ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_fromId_Index(from_id)";
    String addIndex14= " ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_guid_Index(guid)";
    String addIndex11= " ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_status_Index(status)";
    String addIndex12= " ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_type_Index(type)";
    String addIndex13= " ALTER TABLE " + ESB_TRANSFER_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_createTime_Index(create_time)";
    updateTableBySql2(addIndex1, conn);
    updateTableBySql2(addIndex11, conn);
    updateTableBySql2(addIndex12, conn);
    updateTableBySql2(addIndex13, conn);
    updateTableBySql2(addIndex14, conn);
    
    String addIndex21= " ALTER TABLE " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_status_transId_Index(trans_id)";
    String addIndex22= " ALTER TABLE " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_status_status_Index(status)";
    String addIndex23= " ALTER TABLE " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_status_toId_Index(to_id)";
    String addIndex24= " ALTER TABLE " + ESB_TRANSFER_STATUS_TABLE_PRE + "_tmp  ADD INDEX esb_transfer_status_createTime_Index(create_time)";
    updateTableBySql2(addIndex21, conn);
    updateTableBySql2(addIndex22, conn);
    updateTableBySql2(addIndex23, conn);
    updateTableBySql2(addIndex24, conn);
    
    String addIndex31= " ALTER TABLE " + ESB_SYS_MSG + "_tmp  ADD INDEX esb_sys_msg_guid_Index(guid)";
    String addIndex32= " ALTER TABLE " + ESB_SYS_MSG + "_tmp  ADD INDEX esb_sys_msg_toId_Index(to_id)";
    String addIndex33= " ALTER TABLE " + ESB_SYS_MSG + "_tmp  ADD INDEX esb_sys_msg_status_Index(status)";
    updateTableBySql2(addIndex31, conn);
    updateTableBySql2(addIndex32, conn);
    updateTableBySql2(addIndex33, conn);
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
  public static void updateTableBySql2(String sql , Connection conn) throws Exception{
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.execute(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  
}
