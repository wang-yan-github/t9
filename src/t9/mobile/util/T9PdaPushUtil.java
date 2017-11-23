package t9.mobile.util;

import java.io.IOException;

import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.util.T9Utility;

public class T9PdaPushUtil {
  public static void mobilePushNotification(String uidSend, String content, String module) throws Exception {
    if (T9Utility.isNullorEmpty(uidSend))  return ;
    if (!uidSend.endsWith(",")) {
      uidSend += ",";
    }
    module = module.toLowerCase();
    T9MsgPusher.push("C^m^n^" +uidSend+"^"+module+"^"+content);
  }
  public static void main(String[] args) {
    String strUDP = "C^m^l^admin^.*^a71f4a06451aa4e0";
    try {
      T9MsgPusher.push(strUDP);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      T9MsgPusher.push("192.168.0.85", 1188, strUDP, "UTF-8");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
