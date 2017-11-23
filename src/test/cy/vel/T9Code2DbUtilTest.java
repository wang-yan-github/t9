package test.cy.vel;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.Map;

import org.junit.Test;

import t9.rad.velocity.T9Code2DbUtil;
import t9.rad.velocity.T9velocityUtil;
import test.core.util.db.TestDbUtil;

public class T9Code2DbUtilTest {

  @Test
  public void testDb2JavaCodefName() {
    fail("Not yet implemented");
  }

  @Test
  public void testDb2JavaCodefNo() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    T9Code2DbUtil cd = new T9Code2DbUtil();
    String packageName = "act";
    Map result = null;
    try {
      result = cd.db2JavaCodefNo(dbConn, "11111",packageName );
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String url = "D:\\project\\t9\\src\\t9\\core\\act";
    String templateName = "db2JavaCode.vm";
    System.out.println(result);
    T9velocityUtil.velocity(result, url, templateName, null);
  }

  @Test
  public void testTransMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTypeName() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetImportValue() {
    fail("Not yet implemented");
  }

}
