package t9.pda2.login.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import t9.core.esb.server.user.data.TdUser;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;

public class T9PdaSystemLoginLogic {
  
  public boolean validateUser(Connection dbConn , String userId){
    String sql = " SELECT 1 FROM person where USER_ID ='" + userId +"'";
    
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
  
  public boolean checkPwd(Connection dbConn , String userId , String pwd){
    String pwdMd5Str = T9DigestUtility.md5Hex(pwd.getBytes());
    String sql = " SELECT seq_id , password FROM person where USER_ID ='" + userId +"'";
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
}
