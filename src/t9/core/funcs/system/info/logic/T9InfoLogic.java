package t9.core.funcs.system.info.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserOnline;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.data.T9LoginUsers;
import t9.core.funcs.system.data.T9Menu;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9RegistProps;
import t9.core.global.T9SysProps;

public class T9InfoLogic {
  
  private static Logger log = Logger.getLogger("t9.core.funcs.system.info.act");
  
  /**
   * 获取版本信息
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public Map<String,String> getVersion(Connection conn) throws Exception{
    Statement st = null;
    ResultSet rs = null;
    try{
      Map<String,String> map = new HashMap<String,String>();
      String sql = "select VER" +
          ",VERSION_NUM" +
          ",SN" +
          ",CODE" +
          ",USER_VERSION" +
          " from VERSION";
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if (rs.next()){
        map.put("ver", T9SystemLogic.parseString(rs.getString("VER")));
        map.put("userVer", T9SystemLogic.parseString(rs.getString("USER_VERSION")));
        map.put("versionNum", T9SystemLogic.parseString(String.valueOf(rs.getInt("VERSION_NUM")), "1"));
        map.put("sn", T9SystemLogic.parseString(rs.getString("SN")));
        map.put("code", T9SystemLogic.parseString(rs.getString("CODE")));
      }
      return map;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(st, rs, log);
    }
  }
  
  public int getUserAmount(Connection conn) throws Exception{
    Statement st = null;
    ResultSet rs = null;
    try{
      int amount = 0;
      
      String sql = "select count(1) as COUNT" +
      " from PERSON";
      
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      
      if (rs.next()){
        amount = rs.getInt("COUNT");
      }
      return amount;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(st, rs, log);
    }
  }
  
  public int getUserAmountNotLogin(Connection conn) throws Exception{
    Statement st = null;
    ResultSet rs = null;
    try{
      int amount = 0;
      
      String sql = "select count(1) as COUNT" +
      " from PERSON" +
      " where NOT_LOGIN = '1'";
      
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      
      if (rs.next()){
        amount = rs.getInt("COUNT");
      }
      return amount;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(st, rs, log);
    }
  }
  
  public String getUnitName(Connection conn) throws Exception{
    Statement st = null;
    ResultSet rs = null;
    try{
      String sql = "select UNIT_NAME" +
      " from ORGANIZATION";
      
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      
      if (rs.next()){
        return T9SystemLogic.parseString(rs.getString("UNIT_NAME"));
      }
      
      return "";
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(st, rs, log);
    }
  }
  
  public void updateSN(Connection conn, String sn) throws Exception{
    PreparedStatement ps = null;
    try{
      String sql = "update VERSION" +
      		" set SN = ?";
      
      ps = conn.prepareStatement(sql);
      ps.setString(1, sn);
      ps.executeUpdate();
      
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  /**
   * 更新注册信息
   */
  public void updateIM(Connection conn, int imUserCnt) {
    PreparedStatement ps = null;
    try {
      int userCnt = imUserCnt;
      if (userCnt <= 0) {
        userCnt = 30;
      }
      
      String sql = null;
      
      if (this.hasProperty(conn, "IM_USER_CNT")) {
        sql = "update SYS_PARA" +
        " set PARA_VALUE = ?" +
        " where PARA_NAME = 'IM_USER_CNT'";
      }
      else {
        sql = "insert into SYS_PARA(PARA_NAME, PARA_VALUE) values('IM_USER_CNT', ?)";
      }
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, userCnt);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(), e);
    } finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  private boolean hasProperty(Connection dbConn, String name) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select count(1) AMOUNT" +
          " from SYS_PARA" +
          " where PARA_NAME = ?";
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, name);
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("AMOUNT") > 0;
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.debug(e.getMessage(), e);
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return false;
  }
}
