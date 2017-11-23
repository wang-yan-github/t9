package t9.core.funcs.system.data;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.person.data.T9UserPriv;
import t9.core.util.db.T9ORM;

public class T9UserPrivCache {
  private static Map<Integer , T9UserPriv> userPrivMap = new HashMap();
  public static Map<Integer , T9UserPriv> getUserPrivMap() {
    return userPrivMap;
  }

  public static void setUserPrivMap(Map userPrivMap) {
    T9UserPrivCache.userPrivMap = userPrivMap;
  }
  
  public static T9UserPriv getUserPrivCache(Connection dbConn , Integer s) throws Exception {
    T9ORM r = new T9ORM();
    T9UserPriv up = (T9UserPriv) getUserPrivMap().get(s);
    if (up == null) {
      Map<String,Integer> query = new HashMap<String,Integer>();
      query.put("SEQ_ID", s);
      up = (T9UserPriv) r.loadObjSingle(dbConn, T9UserPriv.class,
          query);
      if (up != null)
        getUserPrivMap().put(s, up);
    }
    return up ;
  }
  public static void removeUserPrivCache(int s) {
    if (userPrivMap.containsKey(s)) 
        userPrivMap.remove(s);
  }
  public static void removeAll() {
    getUserPrivMap().clear();
  }
  public static T9UserPriv getUserPrivCache(Connection dbConn , String s) throws Exception {
    return getUserPrivCache( dbConn , Integer.parseInt(s)) ;
  }
}
