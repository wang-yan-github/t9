package t9.plugins.workflow.system;

import java.sql.Connection;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Out;

public class T9VehicleApply implements T9IWFHookPlugin{

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData, boolean agree) throws Exception {
      String OPERATOR_REASON=(String)arrayHandler.get("OPERATOR_REASON");
      String VU_ID=(String)arrayHandler.get("KEY");
        if(agree)
        { 
           String update="update VEHICLE_USAGE set VU_STATUS='1' where SEQ_ID='"+VU_ID+"'";
           T9WorkFlowUtility.updateTableBySql(update, conn);  
        }
        else
        {
           String query="update VEHICLE_USAGE set VU_STATUS='3',OPERATOR_REASON='"+OPERATOR_REASON+"' where SEQ_ID='"+VU_ID+"'";
           T9WorkFlowUtility.updateTableBySql(query, conn);
         
       }
    return null;
  }

      

  
}
