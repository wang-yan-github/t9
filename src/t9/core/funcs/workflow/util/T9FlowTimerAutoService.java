package t9.core.funcs.workflow.util;
import java.sql.Connection;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.logic.T9FlowTimerLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
/**
 * 工作流的后台服务
 * @author liuhan
 *
 */
public class T9FlowTimerAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("t9.core.funcs.workflow.util.T9FlowTimerAutoService");

  public void doTask() {
    Connection conn = null;
    try {
      conn = getRequestDbConn().getSysDbConn();
      T9FlowTimerLogic util = new T9FlowTimerLogic();
      util.timeRun(conn);
    } catch (Exception e) {
      log.debug(e.getMessage(),e);
    } finally {
      T9DBUtility.closeDbConn(conn, null);
    }
  }
}
