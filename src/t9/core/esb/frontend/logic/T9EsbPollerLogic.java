package t9.core.esb.frontend.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import t9.core.esb.frontend.T9EsbPoller;
import t9.core.esb.frontend.data.T9EsbDownTask;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.user.api.core.db.T9DbconnWrap;

public class T9EsbPollerLogic {
  public T9EsbDownTask getDownTaskByStatus(String status ) throws Exception{
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    
    Connection conn2 = null;
    String query = "select * from ESB_DOWN_TASK WHERE STATUS in (" + status + ") ";
    
    if (!T9Utility.isNullorEmpty(T9EsbPoller.nowDownTaskGuid)) {
      query += " and GUID = '" + T9EsbPoller.nowDownTaskGuid + "' ";
      T9EsbPoller.nowDownTaskGuid = "";
    }
    query += " order by SEQ_ID desc";
    Statement stm2 = null; 
    ResultSet rs2 = null; 
    T9EsbDownTask task = null;
    try { 
      conn2 = dbUtil.getSysDbConn();
      stm2 = conn2.createStatement(); 
      rs2 = stm2.executeQuery(query); 
      if (rs2.next()){ 
        int seqId = rs2.getInt("SEQ_ID");
        String fileName = rs2.getString("FILE_NAME");
        String guid = rs2.getString("GUID");
        int st = rs2.getInt("STATUS");
        String fromId = rs2.getString("FROM_ID");
        String message = rs2.getString("MESSAGE");
        String optGuid = rs2.getString("OPT_GUID");
        
        
        task = new T9EsbDownTask();
        task.setSeqId(seqId);
        task.setFileName(fileName);
        task.setGuid(guid);
        task.setStatus(st);
        task.setFromId(fromId);
        task.setMessage(message);
        task.setOptGuid(optGuid);
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm2, rs2, null); 
      T9DBUtility.closeDbConn(conn2, null);
    }
    return task;
  }
  public void updateStatus( String guid , String status)  {
    String query = "update ESB_DOWN_TASK set STATUS = " + status + " where guid='" + guid + "'";
    Statement stm2 = null; 
   T9DbconnWrap dbUtil = new T9DbconnWrap();
    
    Connection conn2 = null;
    try { 
      conn2 = dbUtil.getSysDbConn();
      stm2 = conn2.createStatement(); 
      stm2.executeUpdate(query); 
      conn2.commit();
    } catch(Exception ex) { 
      //throw ex; 
      ex.printStackTrace();
    } finally { 
      T9DBUtility.close(stm2, null, null); 
      T9DBUtility.closeDbConn(conn2, null);
    }
  }
  public void addEsbDownTask( String fileName , String guid , int status , String fromId, String optGuid, String message) throws Exception {
    String query = "insert into ESB_DOWN_TASK ( FILE_NAME, GUID, STATUS , FROM_ID , OPT_GUID , MESSAGE ,CREATE_TIME) values(?,?,? ,? , ? , ? ,?) ";
    PreparedStatement stm2 = null; 
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    
    Connection conn2 = null;
    try { 
      conn2 = dbUtil.getSysDbConn();
      stm2 = conn2.prepareStatement(query);
      stm2.setString(1, fileName);
      stm2.setString(2, guid);
      stm2.setInt(3, status);
      stm2.setString(4, fromId);
      stm2.setString(5, optGuid);
      stm2.setString(6, message);
      stm2.setTimestamp(7, new Timestamp(new Date().getTime()));
      stm2.executeUpdate(); 
      conn2.commit();
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm2, null, null); 
      T9DBUtility.closeDbConn(conn2, null);
    }
  }
  
  public boolean hasEsbDownTask( String guid) {
    // TODO Auto-generated method stub
    PreparedStatement ps = null;
    ResultSet rs = null;
   T9DbconnWrap dbUtil = new T9DbconnWrap();
    
    Connection conn2 = null;
    try {
      conn2 = dbUtil.getSysDbConn();
      String sql = "select 1 " +
          " from ESB_DOWN_TASK" +
          " where GUID =?";
      ps = conn2.prepareStatement(sql);
      ps.setString(1, guid);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, null);
      T9DBUtility.closeDbConn(conn2, null);
    }
    return false;
  }
  public boolean hasEsbDownTaskField( String guid) {
    // TODO Auto-generated method stub
    PreparedStatement ps = null;
    ResultSet rs = null;
 T9DbconnWrap dbUtil = new T9DbconnWrap();
    
    Connection conn2 = null;
    try {
      conn2 = dbUtil.getSysDbConn();
      String sql = "select 1 " +
          " from ESB_DOWN_TASK" +
          " where GUID =? and STATUS = '-3'";
      ps = conn2.prepareStatement(sql);
      ps.setString(1, guid);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, null);
      T9DBUtility.closeDbConn(conn2, null);
    }
    return false;
  }
  
  
  /***
   * 根据guid获取对象 -syl
   * @param status
   * @return
   * @throws Exception
   */
  public static T9EsbDownTask getTaskInfo(  Connection conn2 , String guid) throws Exception{
    String query = "select * from ESB_DOWN_TASK WHERE guid ='" + guid + "'";
    Statement stm2 = null; 
    ResultSet rs2 = null; 
    T9EsbDownTask task = null;
    try { 
      stm2 = conn2.createStatement(); 
      rs2 = stm2.executeQuery(query); 
      if(rs2.next()){ 
        
        task = new T9EsbDownTask();
        int seqId = rs2.getInt("SEQ_ID");
        String fileName = rs2.getString("FILE_NAME");
        int st = rs2.getInt("STATUS");
        String fromId = rs2.getString("FROM_ID");
        String message = rs2.getString("MESSAGE");
        task.setSeqId(seqId);
        task.setFileName(fileName);
        task.setGuid(guid);
        task.setStatus(st);
        task.setFromId(fromId);
        task.setMessage(message);
        task.setOptGuid(rs2.getString("OPT_GUID"));
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm2, rs2, null); 
    }
    return task;
  }
}
