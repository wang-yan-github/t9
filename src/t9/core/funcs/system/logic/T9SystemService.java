package t9.core.funcs.system.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9SystemService extends T9AutoRun {
  public static byte[] onlineSync = new byte[1];
  private static final Logger log = Logger.getLogger("t9.core.funcs.system.logic.T9SystemService");
  public void doTask() {
    Connection conn = null;
    try {
      conn = getRequestDbConn().getSysDbConn();
      synchronized(T9SystemService.onlineSync) {
        this.clearOnlineStatus(conn);
        conn.commit();
      }
    } catch (Exception e) {
      log.debug(e.getMessage(),e);
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
  }
  public void clearOnlineStatus(Connection conn) throws Exception {
    String ref = T9SysProps.getProp("$ONLINE_REF_SEC");
    
    if (T9Utility.isNullorEmpty(ref)) {
      ref = "120";
    }
    int refInt = (Integer.parseInt(ref) + 5) * 1000 ;
    long time = new Date().getTime() - refInt;
    String update ="delete from USER_ONLINE where LOGIN_TIME < ?" ;
    PreparedStatement ps = null;
    try{
      ps = conn.prepareStatement(update);
      ps.setTimestamp(1, new Timestamp(time));
      ps.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, null, log);
    }
  }
}
