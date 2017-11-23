package t9.core.autorun;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9AuthAutoService;
import t9.core.util.file.T9FileUtility;

public class T9AutoRunThread extends Thread {
  //log
  private static Logger log = Logger.getLogger("yzq.t9.core.autorun.T9AutoRunThread");
  
  //相邻两次运行的时间间隔 单位是秒
  private int sleepTime = 1;
  //后台服务对象链
  private Map<String, T9AutoRun> autoRuns = new LinkedHashMap<String, T9AutoRun>();
  //运行标志
  private boolean runFlag = true;
  //运行实例
  private static T9AutoRunThread mainService = null;
  
  /**
   * 加载后台线程配置
   */
  private void loadAutoRunConfig() {
    String confFile = T9SysProps.getWebInfPath() + File.separator + "config" + File.separator + "autoruntasksconfig.properties";
    Map<String, String> rawConfMap = new HashMap<String, String>();
    try {
      try {
        //增加系统内置的后台服务
        autoRuns.put("t9.sys.services.auth.T9AuthService", new T9AuthAutoService());
      }catch(Exception ex) {        
      }
      T9FileUtility.load2Map(confFile, rawConfMap);
      Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "autoRunTask");
      Iterator<String> iKeys = confMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String confJson = confMap.get(key);
        if (T9Utility.isNullorEmpty(confJson)) {
          continue;
        }
        try {
          T9AutoRun autoRun = T9AutoRun.buildAutoRun(confJson);
          if (!autoRun.isPause()) {
            autoRuns.put(key, autoRun);
          }
        }catch(Exception ex) {
          log.debug(ex.getMessage(), ex);
        }
      }
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
    }
  }

  //防止用户使用默认的构造函数  private T9AutoRunThread() {
  }

  /**
   * 构造函数 相邻两次运行的时间间隔 单位是分钟   * @param sleepTime
   */
  private T9AutoRunThread(int sleepTime) {
    this.sleepTime = sleepTime;
    loadAutoRunConfig();
  }
  
  /**
   * 取得当前运行实例
   * @return
   */
  public static T9AutoRunThread currInstance() {
    return mainService;
  }

  /**
   * 注册释放资源的对象   * @param releasor
   */
  public synchronized void registAutoRun(String key, T9AutoRun autoRun) {
    autoRuns.put(key, autoRun);
  }
  /**
   * 注册服务
   * @param releasor
   */
  public synchronized void registAutoRun(String key, String configJson) {
    try {
      this.registAutoRun(key, T9AutoRun.buildAutoRun(configJson));
    }catch(Exception ex) {      
    }
  }
  /**
   * 删除服务
   * @param key
   */
  public synchronized void removeAutoRun(String key) {
    T9AutoRun autoRun = this.autoRuns.get(key);
    if (autoRun != null) {
      autoRun.stopRun();
      autoRuns.remove(key);
    }
  }
  /**
   * 清除服务
   * @param key
   */
  public synchronized void clearAutoRun() {
    Iterator iKeys = autoRuns.keySet().iterator();
    while (iKeys.hasNext()) {
      T9AutoRun autoRun = (T9AutoRun)autoRuns.get(iKeys.next());
      autoRun.stopRun();
    }
    autoRuns.clear();
  }
  /**
   * 手工启动服务
   * @param key
   * @return 1=没有找到该服务；2=该服务正在运行；0=正常启动
   */
  public synchronized int manuStartAutoRun(String key) {
    T9AutoRun autoRun = this.autoRuns.get(key);
    if (autoRun == null) {
      return 1;
    }
    if (autoRun.isRunning()) {
      return 2;
    }
    try {
      autoRun.menuStartRun();
      
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
    }finally {
    }
    return 0;
  }

  /**
   * 释放资源
   */
  private void doRun() {
    try {
      Iterator iKeys = autoRuns.keySet().iterator();
      while (iKeys.hasNext()) {
        T9AutoRun autoRun = (T9AutoRun)autoRuns.get(iKeys.next());
        try {
          autoRun.startRun();
        }catch (Throwable ex) {
          try {
            log.debug(ex.getMessage(), ex);
          }catch(Throwable t) {          
          }
        }
      }
    }catch(Exception ex) {
      log.debug(ex.getMessage(), ex);
    }finally {
    }
  }

  /**
   * 启动该线程   */
  public static void startAutoRun(int sleepTime) {
    stopRun();
    mainService = new T9AutoRunThread(sleepTime);
    mainService.setRunFlag(true);
    mainService.start();
  }
  
  /**
   * 终止线程
   */
  public static void stopRun() {
    if (mainService == null) {
      return;
    }
    try {
      mainService.setRunFlag(false);
      mainService.interrupt();
      mainService.clearAutoRun();
    }catch(Exception ex) {      
    }finally {
      mainService = null;
    }
  }

  /**
   * 重载父类方法run
   */
  public void run() {
    System.out.println("后台线程开始运行...");
    log.debug("后台线程开始运行...");
    while (runFlag) {
      try {        
        sleep(sleepTime * T9Const.DT_S);
        doRun();
      }catch (Throwable ex) {
        try {
          log.debug(ex.getMessage(), ex);
        }catch(Throwable t) {          
        }
      }
    }
    System.out.println("后台线程停止运行.");
  }

  /**
   * 设置runFlag
   * @param runFlag
   */
  public void setRunFlag(boolean runFlag) {
    this.runFlag = runFlag;
  }
}
