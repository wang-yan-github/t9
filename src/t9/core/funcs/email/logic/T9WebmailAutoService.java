package t9.core.funcs.email.logic;

import java.sql.Connection;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRun;
import t9.core.util.T9Utility;
/**
 * 抽取外部邮件的后台服务
 * @author tulaike
 *
 */
public class T9WebmailAutoService extends T9AutoRun {
  private static final Logger log = Logger.getLogger("yzq.t9.core.funcs.email.logic.T9WebmailAutoService");

  /**
   * 抽取webEmail到邮件中心
   */
  public void doTask() {
    T9WebmailLogic wml = new T9WebmailLogic();
    try {
      wml.loadWebMail();
    } catch (Exception e) {
      log.debug(e.getMessage(),e);
    }
  }
}
