package t9.core.funcs.workflow.util;
import java.sql.Connection;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.util.db.T9DBUtility;
/**
 * 工作流的后台服务
 * @author liuhan
 *
 */
public class T9WorkFlowAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("t9.core.funcs.workflow.util.T9WorkFlowAutoService");

  /**
   *  设置工作超时标志
   */
  public void doTask() {
    Connection conn = null;
    try {
      conn = getRequestDbConn().getSysDbConn();
      T9FlowRunUtility util = new T9FlowRunUtility();
      util.setTimeOutFlag(conn, "/t9");
      conn.commit();
    } catch (Exception e) {
      log.debug(e.getMessage(),e);
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
  }
}
