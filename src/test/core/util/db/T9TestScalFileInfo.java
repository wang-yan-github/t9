package test.core.util.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import t9.core.autorun.T9FileInfoExtract;
import t9.core.util.db.T9DBUtility;

public class T9TestScalFileInfo {
  public static void main(String[] args) throws Exception {
    //String str = URLEncoder.encode("您好", "UTF-8");
    //T9Out.println(str);
    //test11();
    //test2();
  }
  
  public static void test2() throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TD_OA2");

      String sql = "select NODES from MATE_NODE where NODES like '/'%' {escape '/'}";
      //String sql = "select NODES from MATE_NODE where NODES like '\\%%'";
      stmt = dbConn.createStatement();
      //stmt.setEscapeProcessing(false);
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        System.out.println(rs.getString(1));
      }
    } catch (Exception e) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      e.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
      T9DBUtility.close(stmt, rs, null);
    }
  }
}
