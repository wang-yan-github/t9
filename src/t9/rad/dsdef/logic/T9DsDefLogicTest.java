package t9.rad.dsdef.logic;


import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

import test.core.util.db.TestDbUtil;

public class T9DsDefLogicTest {

  @Test
  public void testCreatePhyics() {
    Connection conn = null;
    try {
       conn = TestDbUtil.getConnection(false, "TD_OA2");
       System.out.println(conn);
       T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
       ddl.createPhyics(conn, "10001");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetDbExecType() {
    fail("Not yet implemented");
  }

  @Test
  public void testParserFactory() {
    fail("Not yet implemented");
  }

}
