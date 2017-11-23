package t9.plugins.workflow.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9AttendLeave implements T9IWFHookPlugin {

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData , boolean agree) throws Exception {
    // TODO Auto-generated method stub
    String leaveId = (String)arrayHandler.get("KEY");
    String peason =  (String)arrayHandler.get("REASON");
    if (agree) {
      String query = "SELECT LEAVE_TYPE from ATTEND_LEAVE where SEQ_ID='"+leaveId+"'";
      Statement stm3 = null; 
      ResultSet rs3 = null; 
      String LEAVE_TYPE = "";
      try { 
        stm3 = conn.createStatement(); 
        rs3 = stm3.executeQuery(query); 
        if (rs3.next()) {
          LEAVE_TYPE =T9Utility.null2Empty(rs3.getString("LEAVE_TYPE"));
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm3, rs3, null); 
      }
      String update = "update ATTEND_LEAVE set ALLOW='1' where SEQ_ID='"+leaveId+"'";
      if (LEAVE_TYPE.trim().startsWith("补假：")) {
        update = "update ATTEND_LEAVE set ALLOW='3',STATUS='2' where SEQ_ID='"+leaveId+"'";
      }
      T9WorkFlowUtility.updateTableBySql(update, conn);
    } else {
      String update = "update ATTEND_LEAVE set ALLOW='2'";
      if (peason != null || "null".equals(peason)) {
        update += ",REASON='"+peason+"' ";
      }
      update += " where SEQ_ID='"+leaveId+"'";
      T9WorkFlowUtility.updateTableBySql(update, conn);
    }
    return null;
  }

}
