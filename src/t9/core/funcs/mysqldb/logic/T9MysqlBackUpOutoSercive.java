package t9.core.funcs.mysqldb.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9MysqlBackUpOutoSercive extends T9AutoRun {
  private static final Logger log = Logger.getLogger("yzq.t9.core.funcs.email.logic.T9WebmailAutoService");
  private static T9MySqlDBLogic msl = new T9MySqlDBLogic();
  /**
   * 抽取webEmail到邮件中心
   */
  public void doTask() {
    //System.out.println("T9WebmailAutoService doTask Run" + T9Utility.getCurDateTimeStr());
    Connection conn = null;
    try {
     // requestDbConn = new T9RequestDbConn(acsetDbNo);
       conn = getRequestDbConn().getSysDbConn();
      if(msl.checkDb() && canRun(conn)){
        msl.backUpauto(conn);
        msl.updateLastTime(conn);
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(),e);
    } finally {
      //if (conn != null) {
        T9DBUtility.closeDbConn(conn, null);
     // }
    }
    //System.out.println("T9WebmailAutoService doTask Run END " + T9Utility.getCurDateTimeStr());
  }
  
  public boolean canRun(Connection conn) throws Exception{
    String sql = "select " + " SEQ_ID" + ",TASK_TYPE" + ",`INTERVAL`"
    + ",EXEC_TIME" + ",LAST_EXEC" + ",TASK_NAME" + ",TASK_DESC"
    + ",USE_FLAG " + " from OFFICE_TASK where TASK_CODE = ?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, "db_backup");
      rs = ps.executeQuery();
      if(rs.next()){
        String exctTimeTurn = "";
        String lastExec = rs.getString(5);
        String execTmie = rs.getString(4);
        String taskUse = rs.getString(8);
        int interval = rs.getInt(3);
        if(taskUse.equals("0")){
          return false;
        }
        if(interval == 0){
          lastExec = T9Utility.getDateTimeStr(new Date());
        }
        if(lastExec == null){
          lastExec = T9Utility.getDateTimeStr(new Date());
        }
        if(lastExec.length() >= 10){
          lastExec = (String) lastExec.subSequence(0, 10);
        }
        exctTimeTurn = lastExec + " " + execTmie.trim(); 
        Date exctTimeTurnDate = T9Utility.parseDate("yy-MM-dd HH:mm:ss",exctTimeTurn);
        //Date nowDate = new Date();
        Calendar c = Calendar.getInstance(); 
        c.setTime(exctTimeTurnDate);
        
        
        
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        
        int day= (int) ((c2.getTimeInMillis() - c.getTimeInMillis())/(24*60*60*1000));    

        if(interval != 0 && day > interval  ){
          c.add(Calendar.DATE, day);
        }else{
          c.add(Calendar.DATE, interval);
        }
        long exctTimeTurnSeconds = c.getTimeInMillis();
        long currTimeSecondes = c2.getTimeInMillis();
        if(currTimeSecondes < exctTimeTurnSeconds){
          return false;
        }else{
          if((currTimeSecondes - exctTimeTurnSeconds) <=  (intervalSeconds*1000) ){
            return true;
          }else{
            return false;
          }
        }
      }else{
        return false;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
}
