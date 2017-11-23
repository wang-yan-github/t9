package t9.plugins.workflow.system;

import java.sql.Connection;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;

public class T9AttendEvection implements T9IWFHookPlugin {

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData, boolean agree) throws Exception {
    String evectionId = (String)arrayHandler.get("KEY");
    String not_reason = (String)arrayHandler.get("NOT_REASON");
    if (agree) {
      String query="update ATTEND_EVECTION set ALLOW='1' where SEQ_ID='"+evectionId+"'";
      T9WorkFlowUtility.updateTableBySql(query, conn);
   }
   else
   {
      String query="update ATTEND_EVECTION set ALLOW='2',NOT_REASON='"+not_reason+"' where SEQ_ID='"+evectionId+"'";
      T9WorkFlowUtility.updateTableBySql(query, conn);
   }
      
    
    return null;
  }

}
