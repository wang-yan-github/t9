package t9.core.funcs.setdescktop.notes.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.funcs.system.url.data.T9Url;

public class T9NotesLogic {
  
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.fav.act");
  
  public String getNotes(Connection conn,int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String note = "";
      String sql = "select CONTENT" +
      		" from NOTES" +
      		" where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      if (rs.next()){
        note = rs.getString("CONTENT");
      }
      
      return note;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public void saveNote(Connection conn, int seqId, String note) throws Exception{
    PreparedStatement ps1 = null;
    PreparedStatement ps2 = null;
    
    try{
      String sql = "update NOTES" +
      " set CONTENT = ?" +
      " where SEQ_ID = ?";
      ps1 = conn.prepareStatement(sql);
      ps1.setString(1, note);
      ps1.setInt(2, seqId);
      if (ps1.executeUpdate() == 0){
        sql = "insert into NOTES (SEQ_ID,CONTENT)" +
        " values (?,?)";
        ps2 = conn.prepareStatement(sql);
        ps2.setInt(1, seqId);
        ps2.setString(2, note);
        ps2.executeUpdate();
      }
      
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps1, null, log);
      T9DBUtility.close(ps2, null, log);
    }
  }
}
