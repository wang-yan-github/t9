package t9.core.esb.client.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import org.apache.http.util.ByteArrayBuffer;

import t9.core.esb.client.data.T9DocSendMessage;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.data.T9EsbMessage;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.esb.client.logic.T9EsbClientUtility;
import t9.core.esb.client.logic.T9ObjectUtility;
import t9.core.funcs.doc.send.logic.T9DocSendLogic;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

public class OAWebservice {
  public  String updateState(String guid, int state, String to) {
    System.out.println(state + ":" + to);
    return "state: " + guid + " -- " + state;
  }
  public  String doMessage(String fromId, String message) {
    return "";
  }
  public String recvMessage(String filePath, String guid, String from , String optGuid , String message) throws Exception {
    File file = new File(filePath);
    String fileName = file.getName();
    if (fileName.endsWith("xml")) {
      StringBuffer sb = T9FileUtility.loadLine2Buff(filePath);
      T9EsbMessage message1 = T9EsbMessage.xmlToObj(sb.toString());
      if (T9EsbConst.SYS_DEPT.equals(message1.getMessage())) {
        T9DeptTreeLogic logic = new T9DeptTreeLogic();
        logic.updateDept(message1.getData());
        return null;
      }
    }
    if (fileName.endsWith("zip")) {
      try {
        Map<String , ByteArrayBuffer> map = T9EsbClientUtility.getFileList(filePath);
        ByteArrayBuffer bb = map.get(T9EsbMessage.KEY_MESSAGE_FILE);
        if (bb != null) {
          String xml  = new String(bb.toByteArray());
          T9EsbMessage message1 = T9EsbMessage.xmlToObj(xml);
          if (message1.getMessage().equals(T9DocSendMessage.KEY_SEND_DOC_MESSAGE)) {
            T9DocSendMessage dsm = (T9DocSendMessage) T9ObjectUtility.readObject(message1.getData());
            T9DocSendLogic docSendLogic = new T9DocSendLogic();
            docSendLogic.receiveFormEsb(dsm , map.get(dsm.getDocName()));
          }
        }
      }catch(Exception ex){
        ex.printStackTrace();
      }
    }
    return "RECVOK" + guid;
  }
  public static void main(String args[]) {
    Connection conn = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3396/t9" , "root" , "myoa888");
      
      
      StringBuffer sb = new StringBuffer();
      //"guid\r\n"
      for (int i = 0 ;i < 50 ;i++) {
        String uuid = UUID.randomUUID().toString();
        create(conn , uuid);
        create2(conn , uuid);
        sb.append(uuid + "\r\n");
      }
      String fileName = "d:\\test\\my50.txt";
      T9FileUtility.storeString2File(fileName, sb.toString());
      
      //clear(conn);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void clear(Connection conn ) throws Exception {
    exeSql(conn,"delete from esb_transfer");
    exeSql(conn,"delete from esb_transfer_status");
    exeSql(conn,"delete from esb_upload_task");
    exeSql(conn,"delete from esb_down_task");
    exeSql(conn,"delete from esb_sys_msg");
  }
  public static void exeSql(Connection conn ,String query) throws Exception {
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(query);
      stmt.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  public static void create(Connection conn , String guid) throws Exception {
    PreparedStatement stmt = null;
    try {
      String query = " insert into esb_transfer "
        + " (from_id, file_path, content, status, guid, type, create_time, to_id, complete_time, failed_message) "
        + " values (1,?, NULL,2,?,'0','2012-07-04 11:11:33', '1,','2012-07-04 11:11:33',NULL)";
      stmt = conn.prepareStatement(query);
      stmt.setString(1, "d:\\test\\big.jpg");
      stmt.setString(2, guid);
      stmt.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  public static void create2(Connection conn , String guid) throws Exception {
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String query = "insert into esb_transfer_status "
        + " (trans_id, status, to_id, create_time, complete_time, failed_message) "
        + " values ('"+guid +"', '0', '1',NULL,NULL,NULL)";
      stmt.executeUpdate(query);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
    
  }
}
