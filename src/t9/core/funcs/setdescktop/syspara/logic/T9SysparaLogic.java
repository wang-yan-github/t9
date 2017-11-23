package t9.core.funcs.setdescktop.syspara.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.util.db.T9DBUtility;

public class T9SysparaLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.syspara.act");
  
  public String queryLogoutText(Connection conn) throws Exception{
    Statement sm = null;
    ResultSet rs = null;
    try{
      String logoutText = null;
      String sql = "select" +
      		" PARA_VALUE" +
      		" from SYS_PARA" +
      		" where PARA_NAME = 'LOG_OUT_TEXT'";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      if (rs.next()){
        logoutText = rs.getString("PARA_VALUE");
        if (logoutText == null){
          logoutText = "";
        }
      }
      return logoutText;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
  }
  
  public String queryStatusMarquee(Connection conn) throws Exception{
    Statement sm = null;
    ResultSet rs = null;
    try{
      String sql = "select" +
      " PARA_VALUE" +
      " from SYS_PARA" +
      " where PARA_NAME = 'STATUS_TEXT_MARQUEE'";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      
      String value = null;
      
      if (rs.next()) {
        value = rs.getString("PARA_VALUE");
      }
      
      return value;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
  }
  
  
  public int queryUserCount(Connection conn) throws Exception{
    int count = 0 ;
    synchronized(T9SystemService.onlineSync) {
    Statement sm = null;
    ResultSet rs = null;
    try{
      String sql = "select count(distinct(user_ID)) as COUNT" +
      		" from user_online";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      
      
      if (rs.next()) {
        count =  rs.getInt("COUNT");
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
    conn.commit();
    }
    return count;
  }
  
  public String queryStatusText(Connection conn) throws Exception{
    Statement sm = null;
    ResultSet rs = null;
    try{
      String sql = "select STATUS_TEXT from INTERFACE";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      
      String value = null;
      
      if (rs.next()) {
        value = rs.getString("STATUS_TEXT");
      }
      
      return value;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
  }
  
  public String queryFuncId(Connection conn, String name) throws Exception{
    Statement sm = null;
    ResultSet rs = null;
    try{
      String sql = "select MENU_ID" +
      		" from SYS_FUNCTION" +
      		" where FUNC_NAME = '" + name + "'";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      
      String value = null;
      
      if (rs.next()) {
        value = rs.getString("MENU_ID");
      }
      
      return value;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
  }
  
  public Map<String,String> queryHeaderImg(Connection conn) throws Exception{
    Statement sm = null;
    ResultSet rs = null;
    try{
      String path = "";
      String sql = "select" +
            " ATTACHMENT_ID" +
            ",ATTACHMENT_NAME" +
            ",IMG_WIDTH" +
            ",IMG_HEIGHT" +
            " from INTERFACE";
      
      sm = conn.createStatement();
      rs = sm.executeQuery(sql);
      
      Map<String,String> map = new HashMap<String,String>();
      if (rs.next()){
        String id = T9SystemLogic.parseString(rs.getString("ATTACHMENT_ID"));
        String name = T9SystemLogic.parseString(rs.getString("ATTACHMENT_NAME"));
        path = id + System.getProperty("file.separator") + name;
        int width = rs.getInt("IMG_WIDTH");
        int height = rs.getInt("IMG_HEIGHT");
        
        map.put("id", id);
        map.put("name", name);
        map.put("width", width + "");
        map.put("height", height + "");
      }
      return map;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(sm, rs, log);
    }
  }
}
