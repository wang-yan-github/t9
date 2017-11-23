package t9.core.funcs.youhua.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

public class T9YouhuaLogic {
  public void createIndex(Connection conn , String contextPath) throws Exception {
    String type = T9SysProps.getProp("db.jdbc.dbms");
    String filePath = contextPath  + "core"+ File.separator + "funcs"+ File.separator + "youhua" + File.separator + type + ".sql";
    List<String> sqls = new ArrayList();
    T9FileUtility.loadLine2Array(filePath, sqls);
    Statement stm = null;
    for (String sql : sqls) {
      if (!T9Utility.isNullorEmpty(sql.trim())) {
        sql = sql.trim();
        if (sql.endsWith(";")) {
          sql = sql.substring(0 , sql.length() - 1);
          try {
            stm = conn.createStatement();
            stm.execute(sql);
            //System.out.println(sql);
          } catch (Exception ex) {
            //ex.printStackTrace();
          } finally {
            T9DBUtility.close(stm, null, null);
          }
        }
      }
    }
  }
  
}
