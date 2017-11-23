package t9.core.esb.server.system.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import t9.core.esb.server.user.data.TdUser;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;

public class SystemLoginLogic {

  public boolean validateUser(Connection dbConn , String userCode){
    
    String sql = " SELECT 1 FROM td_user where user_code ='" + userCode +"'";
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      return rs.next();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  }
  
  public boolean checkPwd(Connection dbConn , String userCode , String pwd){
    String pwdMd5Str = T9DigestUtility.md5Hex(pwd.getBytes());
    String sql = " SELECT seq_id , password  FROM td_user where user_code ='" + userCode +"'";
    TdUser user = new TdUser();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        user.setSeqId(rs.getInt("seq_id"));
        user.setPassword(rs.getString("password"));
      }
      return pwdMd5Str.equals(user.getPassword());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  } 
  
  public TdUser queryPerson(Connection dbConn, String userCode){
    
    String sql = " SELECT * FROM td_user where user_code ='" + userCode +"'";
    TdUser user = new TdUser();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        user.setSeqId(rs.getInt("seq_id"));
        user.setUserCode(rs.getString("user_code"));
        user.setUserName(rs.getString("user_name"));
        user.setDescription(rs.getString("description"));
        user.setAppId(rs.getInt("app_id"));
        user.setUserType(rs.getInt("user_type"));
        user.setStatus(rs.getInt("status"));
        user.setIsOnline(rs.getInt("is_online"));
        user.setOnlineIp(rs.getString("online_Ip"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return user;
  }
    
}
