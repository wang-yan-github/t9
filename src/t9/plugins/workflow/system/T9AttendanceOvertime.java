package t9.plugins.workflow.system;

import java.sql.Connection;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;

public class T9AttendanceOvertime implements T9IWFHookPlugin{

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData, boolean agree) throws Exception {


       String OVERTIME_ID=(String)arrayHandler.get("KEY");
        String REASON=(String)arrayHandler.get("REASON");

        if(agree)
        {
           String update="update overtime_record set status='1' where SEQ_ID='"+OVERTIME_ID+"'";   
           T9WorkFlowUtility.updateTableBySql(update, conn);
        }
        else
        {
           String update="update overtime_record set status='2',REASON='"+REASON+"' where SEQ_ID='"+OVERTIME_ID+"'";
           T9WorkFlowUtility.updateTableBySql(update, conn);
        }

    
    return null;
  }
  
  
  
}
