package t9.mobile.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9PdaLoginLogic {
  public String getT9Version(Connection dbConn) throws Exception {
    String result = "";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select VER from version";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        result = rs.getString("VER");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    
    return result;
  }
}
