package test.core.data;

public class T9TestDataWrap {
  //单位KB
  private static final int size = 1;
  private static String bufStr = null;
  private static long beginTime = 0;
  private static long lastTime = 0;
  private static int accessCnt = 0;
  
  static {
    StringBuffer buf = new StringBuffer();
    char[] fillChars = new char[1024];
    for (int i = 0; i < fillChars.length; i++) {
      fillChars[i] = (char)((int)'a' + (i % 26));
    }
    for (int i = 0; i < size; i++) {
      buf.append(fillChars);
    }
    bufStr = buf.toString();
  }
  
  public static void clearAccess() {
    beginTime = 0;
    accessCnt = 0;
  }
  public static int getAccess() {
    return accessCnt;
  }
  public static int getTimeSpan() {
    return (int)(lastTime - beginTime);
  }
  public static long getTotalSize() {
    return size * accessCnt;
  }
  public static long getThroughOutputSec() {
    if (getTimeSpan() == 0) {
      return 0;
    }
    return getTotalSize() * 1000 / getTimeSpan();
  }
  
  public static String getStr() {
    if (beginTime == 0) {
      beginTime = System.currentTimeMillis();
    }
    lastTime = System.currentTimeMillis();
    accessCnt++;
    return bufStr;
  }
}
