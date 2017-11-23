package test.cy;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import raw.cy.db.frm.T9FRMUtil;
import test.core.util.db.TestDbUtil;

public class T9FRMUtilTest {

  @Test
  public void testForm2TableInfoConnectionStringMapOfStringObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testForm2TableInfoConnectionListOfObject() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    List formInfo = new ArrayList();
    List subformInfo = new ArrayList();
    List subInfo = new ArrayList();
    subInfo.add("dsField");
    List subInfo2 = new ArrayList();
    subInfo2.add("chenYi");
    subformInfo.add(subInfo);
    subformInfo.add(subInfo2);
    formInfo.add("dsTable");
    formInfo.add(subformInfo);
    try {
      Map m = T9FRMUtil.form2TableInfo(dbConn, formInfo);
      System.out.println(m);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testGetTaleInfo() {
    fail("Not yet implemented");
  }

  @Test
  public void testParamHelper() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTypeInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTableNo() {
    fail("Not yet implemented");
  }

  @Test
  public void testSQLParam2JavaParam() {
    fail("Not yet implemented");
  }

}
