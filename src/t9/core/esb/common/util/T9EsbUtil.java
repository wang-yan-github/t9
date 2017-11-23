package t9.core.esb.common.util;

import java.io.PrintStream;

import org.apache.log4j.Logger;

public class T9EsbUtil {
  private static Logger log = Logger
      .getLogger("t9");
  private static PrintStream out = System.out;
  public static boolean isDebug = PropertiesUtil.getDebug();
  public static void println(Object o) {
//    if (out != null) {
//      out.println(o);
//    }
  }
  
  public static void print(Object o) {
//    if (out != null) {
//      out.print(o);
//    }
  }
  
  public static void setPrintStream(PrintStream out) {
    T9EsbUtil.out = out;
  }
  
  public static void debug(Object o) {
    if (isDebug) {
      log.debug(o);
    }
  }
}
