package test.cy.vel;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

import t9.rad.velocity.createtable.T9CreateTableUtil;
import t9.rad.velocity.createtable.T9DBDialectUtil;
import test.core.util.db.TestDbUtil;

public class T9CreateTableUtilTest {

  @Test
  public void testCreateTableByName() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateTableByIdConnectionStringArray() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateTableByIdConnectionString() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
      T9CreateTableUtil.createTableById(dbConn, "11114",T9DBDialectUtil.ORACLEDIALECT);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
   
  }

}
