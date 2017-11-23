package t9.core.funcs.system.data;

import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.person.data.T9UserPriv;

public class T9DataCache {
  private static Map<Integer , T9UserPriv> userPrivMap = new HashMap();
  public static Map<Integer , T9UserPriv> getUserPrivMap() {
    return userPrivMap;
  }

  public static void setUserPrivMap(Map userPrivMap) {
    T9DataCache.userPrivMap = userPrivMap;
  }
}
