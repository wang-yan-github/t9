package t9.plugins.workflow.system;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import t9.core.funcs.workflow.util.T9IWFHookPlugin;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;

public class T9AttendOut implements T9IWFHookPlugin {

  @Override
  public String execute(Connection conn, int runId, Map arrayHandler,
      Map formData, boolean agree) throws Exception {
    String outId = (String)arrayHandler.get("KEY");
    String reason = (String)arrayHandler.get("REASON");
    if (agree){
      String sql="select * from ATTEND_OUT where SEQ_ID='"+outId+"' and ALLOW='0'";
      Statement stmt=null;
      ResultSet rs=null;
      try { 
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next())
      {
         String SUBMIT_TIME=rs.getString("SUBMIT_TIME"); 
         String OUT_TIME1=rs.getString("OUT_TIME1");
         String OUT_TIME2=rs.getString("OUT_TIME2");
         String OUT_TYPE=rs.getString("OUT_TYPE");
         String USER_ID1=rs.getString("USER_ID");
         
         String OUT_DAY =  SUBMIT_TIME.substring(0, 10);
         String CAL_TIME = OUT_DAY+" "+OUT_TIME1;
         String END_TIME = OUT_DAY+" "+OUT_TIME2;  
 
       String update="insert into CALENDAR(USER_ID,CAL_TIME,END_TIME,CAL_TYPE,CAL_LEVEL,CONTENT,OVER_STATUS) values ('"+USER_ID1+"','"+CAL_TIME+"','"+END_TIME+"','1','','"+OUT_TYPE+"','0')";
         T9WorkFlowUtility.updateTableBySql(update, conn);
      }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stmt, rs, null); 
      }
      sql="update ATTEND_OUT set ALLOW='1' where SEQ_ID='"+outId+"'"; 
      T9WorkFlowUtility.updateTableBySql(sql, conn);
      
      
      
    }
    else{
      
      String query="update ATTEND_OUT set ALLOW='2',REASON='"+reason+"' where SEQ_ID='"+outId+"'";
      T9WorkFlowUtility.updateTableBySql(query, conn);
    }
    
    return null;
  }

}
