package t9.core.util.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.global.T9RegistProps;
import t9.core.util.db.T9DBUtility;

public class T9AuthAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("yzq.t9.core.util.auth.T9AuthAutoService");

  public T9AuthAutoService() {
    setIntervalSeconds(60 * 60 * 3);
    setPause(false);
  }
  /**
   * 更新注册信息
   */
  public void doTask() {
    PreparedStatement ps = null;
    try {
      Connection conn = getRequestDbConn().getSysDbConn();
      int userCnt = T9RegistProps.getInt("im.userCnt.t9");
      if (userCnt <= 0) {
        userCnt = 30;
      }
      
      String sql = null;
      
      if (this.hasProperty(conn, "IM_USER_CNT")) {
        sql = "update SYS_PARA" +
        " set PARA_VALUE = ?" +
        " where PARA_NAME = 'IM_USER_CNT'";
      }
      else {
        sql = "insert into SYS_PARA(PARA_NAME, PARA_VALUE) values('IM_USER_CNT', ?)";
      }
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, userCnt);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(), e);
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  private boolean hasProperty(Connection dbConn, String name) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select count(1) AMOUNT" +
      		" from SYS_PARA" +
          " where PARA_NAME = ?";
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, name);
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("AMOUNT") > 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(), e);
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return false;
  }
}
