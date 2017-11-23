package t9.core.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRunThread;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;

public class T9ServletContextListener implements ServletContextListener {
  private static Logger log = Logger.getLogger("yzq.t9.core.servlet.ServletContextListener");
  public void contextInitialized(ServletContextEvent event) {
  }

  public void contextDestroyed(ServletContextEvent event) {
  }
}
