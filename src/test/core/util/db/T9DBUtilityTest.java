package test.core.util.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9DBUtilityTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testGetConnection(){
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {            
      dbConn = TestDbUtil.getConnection(false, "TD_OA2");
      stmt = dbConn.createStatement();
      String sql = null;
//      String dateFilter = T9DBUtility.getDateFilter("VOUC_DATE", "2009-01-01 12:34:23", ">");
      sql = "select LAST_VISIT_TIME from PERSON";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
//        String name = rs.getString(1);
        Date date = rs.getTimestamp(1);
        System.out.println("LAST_VISIT_TIME>>" + T9Utility.getDateTimeStr(date));
      }
      dbConn.commit();
    }catch(Exception ex) {
      try {
        dbConn.rollback();
      }catch(Exception ex2) {        
      }
      ex.printStackTrace();
    }finally {
      T9DBUtility.closeDbConn(dbConn, null);
      T9DBUtility.close(stmt, rs, null);
    }
  }

  @Test
  public void testGetRSStringArray() {
    //fail("Not yet implemented");
  }

}
