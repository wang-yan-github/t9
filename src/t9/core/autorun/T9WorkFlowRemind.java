package t9.core.autorun;

import t9.core.util.T9Utility;

public class T9WorkFlowRemind extends T9AutoRun {
  /**
   * 抽取文件信息到文件中心
   */
  public void doTask() {
    System.out.println("T9WorkFlowRemind doTask Run" + T9Utility.getCurDateTimeStr());
  }
}
