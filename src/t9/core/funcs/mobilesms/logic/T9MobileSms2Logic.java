package t9.core.funcs.mobilesms.logic;

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

import t9.core.util.db.T9ORM;
import t9.core.funcs.mobilesms.data.T9Sms2;
import t9.core.global.T9SysProps;
import t9.core.menu.data.T9SysMenu;
import t9.core.util.db.T9DBUtility;

public class T9MobileSms2Logic {
  private static Logger log = Logger.getLogger("t9.core.funcs.setdescktop.shortcut.act");
  
  /**
   * 获取有外发权限的用户串
   * @param conn
   * @return outPriv
   * @throws Exception
   */
  public Map queryOutPriv(Connection conn , int seqId,int deptId) throws Exception{
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      String outPriv = null;
      
      String sql = "";
      
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
        sql = "select " +
        "(select OUT_PRIV" +
        " from SMS2_PRIV) as OUT_PRIV" +
        ",(select USER_NAME" +
        " from PERSON where SEQ_ID = ?) as USER_NAME" +
        ",(select DEPT_NAME" +
        " from DEPARTMENT where SEQ_ID = ?) as DEPT_NAME";
     
      }else if (dbms.equals("mysql")) {
        sql = "select " +
        "(select OUT_PRIV" +
        " from SMS2_PRIV) as OUT_PRIV" +
        ",(select USER_NAME" +
        " from PERSON where SEQ_ID = ?) as USER_NAME" +
        ",(select DEPT_NAME" +
        " from DEPARTMENT where SEQ_ID = ?) as DEPT_NAME";
      
      }else if (dbms.equals("oracle")) {
        sql = "select " +
          "(select OUT_PRIV" +
          " from SMS2_PRIV) as OUT_PRIV" +
          ",(select USER_NAME" +
          " from PERSON where SEQ_ID = ?) as USER_NAME" +
          ",(select DEPT_NAME" +
          " from DEPARTMENT where SEQ_ID = ?) as DEPT_NAME" +
          " from DUAL";
      
      }else {
        throw new SQLException("not accepted dbms");
      }
      
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      ps.setInt(2, deptId);
      rs = ps.executeQuery();
      return this.resultSet2Map(rs);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  /**
   * 查询用户手机号
   * @param conn
   * @return outPriv
   * @throws Exception
   */
  public static String queryMobileNo(Connection conn, int seqId) throws Exception{
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      String mobileNo = null;
      String sql = "select MOBIL_NO" +
      " from PERSON" +
      " where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      if (rs.next()){
        mobileNo = rs.getString("MOBIL_NO");
      }
      return mobileNo;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  
  /**
   * 查询用户姓名
   * @param conn
   * @return outPriv
   * @throws Exception
   */
  public String queryUserName(Connection conn, int seqId) throws Exception{
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      String userName = null;
      String sql = "select USER_NAME" +
      " from PERSON" +
      " where SEQ_ID = ?";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      rs = ps.executeQuery();
      if (rs.next()){
        userName = rs.getString("USER_NAME");
      }
      return userName;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  
  /**
   * 增加短信
   * @param conn
   * @return
   * @throws Exception
   */
  public static void addSms(Connection conn,T9Sms2 sms) throws Exception{
    
    try{
      sms.setContent(sms.getContent().replace("\"", "\\\"").replace("\r", "").replace("\n", ""));
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, sms);
    }catch(Exception ex) {
      throw ex;
    }finally {
    }
  }
  
  
  /**
   * 删除短信
   * @param conn
   * @return
   * @throws Exception
   */
  public void deleteSms(Connection conn, int userSeqId, int seqId) throws Exception{
    PreparedStatement ps = null;
    try{
      String sql = "delete" +
          " from SMS2" +
          " where SEQ_ID = ?" +
          " and FROM_ID = ?" +
          " and SEND_FLAG != '1'";
      ps = conn.prepareStatement(sql);
      ps.setInt(1, seqId);
      ps.setInt(2, userSeqId);
      ps.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps, null, log);
    }
  }
  
  /**
   * 执行sql语句
   * @param conn
   * @return
   * @throws Exception
   */
  public void excuteSql(Connection conn, String sql) throws Exception{
    Statement st = null;
    try{
      st = conn.createStatement();
      st.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(st, null, log);
    }
  }

  /**
   * ResultSet转化为<Map>
   * @param rs
   * @return
   * @throws SQLException
   */
  
  private Map resultSet2Map(ResultSet rs) throws SQLException{
    Map<String,String> map = new HashMap<String,String>();
    if(rs.next()){
      ResultSetMetaData rsMeta = rs.getMetaData();
      for(int i = 0; i < rsMeta.getColumnCount(); ++i){   
        String columnName = rsMeta.getColumnName(i+1);   
        map.put(rsMeta.getColumnName(i+1), null == rs.getString(columnName)?"":rs.getString(columnName)); 
     }
    }
    return map;
  }
  /**
   * 手机短信提醒
   * @param conn
   * @param toId 收件人ID串
   * @param fromId 登录人ID
   * @param content 短信息类容
   * @param sendTime 发送时间
   * @throws Exception
   */
  public void remindByMobileSms (Connection conn,String toId , int fromId,String content,Date sendTime ) throws Exception{
    T9Sms2 sms2 = new T9Sms2();
    sms2.setFromId(String.valueOf(fromId));
    sms2.setContent(content);
    if(sendTime == null){
      sendTime = new Date();
    }
    sms2.setSendTime(sendTime);
    sms2.setSendFlag("0");
    ArrayList<String>  moblieNos = getRemindPriv(conn, toId);
    for (String phone : moblieNos) {
      if (phone == null || "".equals(phone.trim())) {
        continue;
      }
      sms2.setPhone(phone);
      addSms(conn, sms2);
    }
  }
  /**
   * 
   * @param conn
   * @param toId
   * @return
   * @throws Exception
   */
  public ArrayList<String> getRemindPriv(Connection conn,String  toId) throws Exception{
    ArrayList<String> result = new ArrayList<String>();
    String sql = " select REMIND_PRIV FROM SMS2_PRIV ";
    String remindPriv = "";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery() ;
      if (rs.next()) {
        remindPriv = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    String[] toIds = toId.split(",");
    T9MobileSelectLogic msl = new T9MobileSelectLogic();
    for (int i = 0; i < toIds.length; i++) {
      if ("".equals(toIds[i])) {
        continue;
      }
      if (msl.findId(remindPriv, toIds[i])) {
        String mobileNo = queryMobileNo(conn, Integer.parseInt(toIds[i]));
        result.add(mobileNo);
      }
    }
    
    return result;
  }
  
  /**
   * select 审核不通过后的信息
   * @param dbConn
   * @param moduleCode
   * @return
   * @throws Exception
   */
  public String getModHint(Connection dbConn, String moduleCode)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String modHint = "";
    try {
      String queryStr = "select MOD_HINT from CENSOR_MODULE where MODULE_CODE="
          + moduleCode;
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        modHint = rs.getString("MOD_HINT");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return modHint;
  }
}
