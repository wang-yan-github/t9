package t9.core.funcs.doc.util;
import java.sql.Connection;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.util.T9Utility;
/**
 * 工作流的后台服务

 * @author liuhan
 *
 */
public class T9WorkFlowAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("t9.core.funcs.doc.util.T9WorkFlowAutoService");

  /**
   *  设置工作超时标志

   */
  public void doTask() {
    //System.out.println("T9WorkFlowAutoService doTask Run " + T9Utility.getCurDateTimeStr());
    try {
     // requestDbConn = new T9RequestDbConn(acsetDbNo);
      Connection conn = getRequestDbConn().getSysDbConn();
      T9FlowRunUtility util = new T9FlowRunUtility();
      util.setTimeOutFlag(conn, "/t9");
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(),e);
    }
    //System.out.println("T9WorkFlowAutoService doTask Run END " + T9Utility.getCurDateTimeStr());
  }
}
