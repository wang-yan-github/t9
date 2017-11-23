package t9.core.funcs.system.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.menu.data.T9SysMenu;
import t9.core.funcs.menu.data.T9SysFunction;
import t9.core.util.db.T9DBUtility;

public class T9MenuCache {
  private static Map<Integer , T9SysMenu> sysMenuMap = new HashMap();
  private static Map<Integer , T9SysFunction> sysFunctionMap = new HashMap();
  
  public static Map<Integer , T9SysFunction> getSysFunctionMap() {
    return sysFunctionMap;
  }
  public static Map<Integer , T9SysMenu> getSysMenuMap() {
    return sysMenuMap;
  }
  
  public static T9SysFunction getSysFunctionCache(Connection dbConn , Integer s) throws Exception {
    T9SysFunction menu = (T9SysFunction) T9MenuCache.sysFunctionMap.get(s);
    if (menu == null) {
      menu = getFunc(dbConn , s);
      if (menu != null)
        sysFunctionMap.put(s, menu);
    }
    return menu ;
  }
  public static T9Menu getSysFunction(Connection dbConn , Integer s) throws Exception {
    T9SysFunction menu =  getSysFunctionCache( dbConn ,  s) ;
    if (menu != null) {
      T9Menu menu2 = new T9Menu();
      menu2.setId(menu.getMenuId());
      menu2.setText(menu.getFuncName());
      menu2.setSeqId(menu.getSeqId());
      menu2.setUrl(menu.getFuncCode());
      menu2.setIcon(menu.getIcon());
      menu2.setOpenFlag(menu.getOpenFlag());
      return menu2;
    } else {
      return null;
    }
  }
  
  public static T9SysMenu getSysMenuCache(Connection dbConn , Integer s) throws Exception {
    T9SysMenu menu = (T9SysMenu) T9MenuCache.getSysMenuMap().get(s);
    if (menu == null) {
      menu = getMenu(dbConn , s);
      if (menu != null) {
        T9MenuCache.getSysMenuMap().put(s, menu);
      }
    }
    return menu ;
  }
  public static T9Menu getMenuCache(Connection dbConn , Integer s) throws Exception {
    T9SysMenu menu =  getSysMenuCache( dbConn ,  s) ;
    if (menu != null) {
      T9Menu menu2 = new T9Menu();
      menu2.setId(menu.getMenuId());
      menu2.setIcon(menu.getImage());
      menu2.setSeqId(menu.getSeqId());
      menu2.setText(menu.getMenuName());
      return menu2;
    } else {
      return null;
    }
  }
  
  public static T9SysMenu getMenu(Connection conn, int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String sql = "select SEQ_ID" +
          ",MENU_ID" +
          ",MENU_NAME" +
          ",IMAGE" +
          " from SYS_MENU" +
          " where MENU_ID = ?" +
          " order by MENU_ID";
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      
      T9SysMenu menu = null;
      if (rs.next()){
        menu = new T9SysMenu();
        menu.setMenuId(rs.getString("MENU_ID"));
        menu.setMenuName(parseString(rs.getString("MENU_NAME")));
        menu.setSeqId(rs.getInt("SEQ_ID"));
        menu.setImage(parseString(rs.getString("IMAGE")));
      }
      return menu;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
  }
  public static T9SysFunction getFunc(Connection conn, int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    try{
      String sql = "select SEQ_ID" +
          ",MENU_ID" +
          ",FUNC_NAME" +
          ",FUNC_CODE" +
          ",ICON" +
          ",OPEN_FLAG" +
          " from SYS_FUNCTION" +
          " where MENU_ID = ?" +
          " order by MENU_ID";
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      
      T9SysFunction menu = null;
      if (rs.next()){
        menu = new T9SysFunction();
        menu.setMenuId(parseString(rs.getString("MENU_ID")));
        menu.setFuncName(parseString(rs.getString("FUNC_NAME")));
        menu.setSeqId(rs.getInt("SEQ_ID"));
        menu.setFuncCode(parseString(rs.getString("FUNC_CODE")));
        menu.setIcon(parseString(rs.getString("ICON")));
        menu.setOpenFlag(parseString(rs.getString("OPEN_FLAG"), "0"));
      }
      return menu;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
  }
  public static void removeAll() {
    sysMenuMap.clear();
    sysFunctionMap.clear();
  }
  public static String parseString(String s){
    if (s == null){
      return "";
    }
    else {
      return s.trim();
    }
  }
  public static String parseString(String s, String defaultValue){
    if (s == null || s.trim().equals("")){
      return defaultValue;
    }
    else {
      return s.trim();
    }
  }
}
