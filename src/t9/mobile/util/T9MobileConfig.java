package t9.mobile.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9SysProps;

public class T9MobileConfig {
  public static String LANG_COOKIE = "zh-CN";
  public static int PAGE_SIZE = 7;
  public static Map PRIV_MODULES = new HashMap();
  
  public static Map<String , String> getPrivModules() {
    Map r = new HashMap();
    r.put("0204", "email");
    r.put("0506", "notify");
    r.put("04", "workflow");
    r.put("0224", "calendar");
    r.put("0228", "diary");
    r.put("0250", "file_folder");
    r.put("0232", "address");
    r.put("2301", "tel_no");
    r.put("0513", "news");
    return r;
  }
  public static String getFuncStr(List<String> list) {
    Map r = getPrivModules();
    String res = "";
    Set<String> set = r.keySet();
    for (String str : set) {
      for (String ss : list) {
        if (T9WorkFlowUtility.findId(ss, str)) {
          res += r.get(str) + ",";
        }
      }
    }
    return res;
  }
  public static String MSG_LIST_REF_SEC = T9SysProps.getString("$SMS_REF_SEC");
  public static int MSG_DIOG_REF_SEC = 5;
  public static int SEARCH_REF_SEC = 5;
  
  public static String tdMyOaVersion = null;
  public static String MYOA_TDIM_PORT = T9SysProps.getString("IM_SERVER_PORT");
}
