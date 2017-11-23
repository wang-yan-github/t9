package t9.subsys.oa.meeting.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.meeting.data.T9MeetingOperator;

public class T9MeetingOperatorLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  
  public T9MeetingOperator getMeetingOperator(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9MeetingOperator org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='MEETING_OPERATOR'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        org = new T9MeetingOperator();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public void updateMeetingOperator(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public T9MeetingOperator getMeetingRoomRule(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9MeetingOperator org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='MEETING_ROOM_RULE'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        org = new T9MeetingOperator();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public void updateMeetingRoomRule(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
}
