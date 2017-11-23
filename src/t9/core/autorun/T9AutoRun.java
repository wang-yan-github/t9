package t9.core.autorun;

import java.util.Calendar;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public abstract class T9AutoRun implements Runnable {
  /**
   * log
   */
  private static final Logger log = Logger.getLogger("yzq.t9.core.autorun.T9AutoRun");
  //最后一次执行检查的时间
  protected long lastRuntime = System.currentTimeMillis();
  //运行间隔时间，运行周期驱动情况下单位是秒，时间点驱动下，单位是天
  protected int intervalSeconds = 300;
  //时间点驱动的时间，格式HH:mm
  protected String runTime = null;
  //时间点驱动-小时, 取值范围：[0,23]
  protected int runTimeHour = 0;
  //时间点驱动-分钟, 取值范围：[0,59]
  protected int runTimeMinit = 0;
  //时间点驱动的下一个时间点
  protected long runTimePoint = 0;
  //数据库连接资源
  protected T9RequestDbConn requestDbConn = null;
  //是否被暂停
  protected boolean isPause = false;
  //当前线程
  protected Thread currThread = null;

  /**
   * 执行任务
   */
  public abstract void doTask() throws Exception;
  
  
  /**
   * 构造T9AutoRun 对象
   * @param jsonStr
   * @return
   * @throws Exception
   */
  public static T9AutoRun buildAutoRun(String jsonStr) throws Exception {
    T9AutoRunConfig config = (T9AutoRunConfig)T9FOM.json2Obj(jsonStr, T9AutoRunConfig.class);
    T9AutoRun autoRun = (T9AutoRun)Class.forName(config.getCls()).newInstance();
    autoRun.setIntervalSeconds(config.getIntervalSecond());
    autoRun.setRunTime(config.getRunTime());
    autoRun.setPause(config.getIsUsed().equals("0"));
    return autoRun;
  }
  /**
   * 开始执行线程   */
  public synchronized void startRun() {
    if (!shouldRun()) {
      return;
    }
    this.currThread = new Thread(this);
    this.currThread.start();
  }
  /**
   * 手工执行线程
   */
  public synchronized void menuStartRun() {
    if (this.currThread != null) {
      return;
    }
    this.currThread = new Thread(this);
    this.currThread.start();
  }
  /**
   * 终止线程执行
   */
  public synchronized void stopRun() {
    if (currThread == null) {
      return;
    }
    try {
      if (!currThread.isInterrupted()) {
        this.currThread.interrupt();
      } 
    }catch(Exception ex) {      
    }finally {
      this.currThread = null;
    }
  }
  /**
   * 执行任务
   */
  public void run() {
    this.setRequestDbConn(new T9RequestDbConn(""));
    try {
      doTask();
      this.requestDbConn.commitAllDbConns();
    }catch(Exception ex) {
      this.requestDbConn.rollbackAllDbConns();
      log.debug(ex);
    }finally {
      setLastRuntime(System.currentTimeMillis());
      if (!T9Utility.isNullorEmpty(this.runTime)) {
        this.runTimePoint = this.findNextRuntime();
      }
      if (this.requestDbConn != null) {
        this.requestDbConn.closeAllDbConns();
      }
      this.currThread = null;
      this.requestDbConn = null;
    }
  }
  /**
   * 设置间隔时间
   */
  public void setIntervalSeconds(int intervalSeconds) {
    this.intervalSeconds = intervalSeconds;
  }
  /**
   * 设置最后一次运行的时间
   */
  protected void setLastRuntime(long lastRuntime) {
    this.lastRuntime = lastRuntime;
  }
  /**
   * 是否正在运行
   * @return
   */
  public boolean isRunning() {
    return this.currThread != null;
  }
  /**
   * 是否应该运行
   */
  protected synchronized boolean shouldRun() {
    if (this.isPause()) {
      return false;
    }
    if (this.currThread != null) {
      return false;
    }
    //时间间隔驱动的任务
    if (T9Utility.isNullorEmpty(runTime)) {
      if (System.currentTimeMillis() - lastRuntime > intervalSeconds * T9Const.DT_S) {
        return true;
      }else {
        return false;
      }
    //时间点驱动的任务
    }else {
      if (System.currentTimeMillis() > this.runTimePoint) {
        return true;
      }else {
        return false;
      }
    }
  }
  
  protected long findNextRuntime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(lastRuntime);
    
    calendar.set(Calendar.HOUR_OF_DAY, this.runTimeHour);
    calendar.set(Calendar.MINUTE, this.runTimeMinit);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    //这里用天(T9Const.DT_D)是正确的，往前推移一小时以弥补线程执行周期造成的时间差
    long nextTimeCompare = lastRuntime
      + this.intervalSeconds * T9Const.DT_D
      - T9Const.DT_H;
    for (int i = 0; ;i++) {
      calendar.add(Calendar.DATE, 1);
      long nextTime = calendar.getTime().getTime();
      if (nextTime > nextTimeCompare) {
        return nextTime;
      }
    }
  }

  protected T9RequestDbConn getRequestDbConn() {
    return requestDbConn;
  }

  private void setRequestDbConn(T9RequestDbConn requestDbConn) {
    this.requestDbConn = requestDbConn;
  }

  public String getRunTime() {
    return runTime;
  }

  public void setRunTime(String runTime) {
    this.runTime = runTime;
    if (T9Utility.isNullorEmpty(this.runTime)) {
      return;
    }
    this.runTime = this.runTime.trim();
    int tmpInt = this.runTime.indexOf(":");
    if (tmpInt <= 0) {
      this.runTime = null;
      return;
    }
    try {
      this.runTimeHour = Integer.parseInt(this.runTime.substring(0, tmpInt));
      this.runTimeMinit = Integer.parseInt(this.runTime.substring(tmpInt + 1));
      
      this.runTimePoint = this.findNextRuntime();
    }catch(Exception ex) {
      this.runTime = null;
    }
  }

  public boolean isPause() {
    return isPause;
  }

  public void setPause(boolean isPause) {
    this.isPause = isPause;
  }
}
