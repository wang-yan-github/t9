package t9.subsys.portal.bjca.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9BjcaSSOLogic {
  public String getUserNameByCa(Connection conn,String userId) throws Exception{
    String sql = "select USER_ID FROM PERSON WHERE UNIQUE_ID='" + userId + "'";
    Statement st = null;
    ResultSet rs = null;
    String result = "";
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        result =  rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return T9Utility.encodeSpecial(result);
  }
}
