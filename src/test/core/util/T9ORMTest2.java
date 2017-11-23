/**
 * 
 */
package test.core.util;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.util.db.T9ORM;
import test.core.util.db.TestDbUtil;

/**
 * @author yzq
 * 
 */
public class T9ORMTest2 {

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {

  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#save(java.sql.Connection, java.lang.Object)}.
   */
  @Test
  public void testSave() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      e.printStackTrace();
    }
    T9DsTable tab = new T9DsTable();
    tab.setTableNo("10079");
    tab.setTableName("USER_INFO");
    tab.setDbNo("101");
    ArrayList<T9DsField> fieldList = new ArrayList<T9DsField>();
    T9DsField dsField = new T9DsField();
    dsField.setFieldName("city");
    dsField.setTableNo("10079");
    dsField.setFieldNo("10079001");
    fieldList.add(dsField);
    tab.setFieldList(fieldList);
    T9ORM t = new T9ORM();
    try {
      t.saveComplex(dbConn, tab);
    } catch (Exception e) {
      e.printStackTrace();
    }
    // fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#update(java.sql.Connection, java.lang.Object)}
   * .
   */
  @Test
  public void testUpdate() {
    try {
      T9ORM t = new T9ORM();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");

      T9DsTable dsTable = (T9DsTable) t.loadObjComplex(dbConn, T9DsTable.class, 134);
      dsTable.setTableNo("10115");
      ArrayList<T9DsField> list = dsTable.getFieldList();
      for (T9DsField t9DsField : list) {
        t9DsField.setTableNo("10115");
      }
      dsTable.setFieldList(list);
      t.updateComplex(dbConn, dsTable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#delete(java.sql.Connection, int)}.
   */
  @Test
  public void testDeleteConnectionInt() {
    fail("Not yet implemented");
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#delete(java.sql.Connection, java.lang.Object)}
   * .
   */
  @Test
  public void testDeleteConnectionObject() {
    try {
      T9ORM t = new T9ORM();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");

      T9DsTable dsTable = (T9DsTable) t.loadObjComplex(dbConn, T9DsTable.class, 134);
      t.deleteComplex(dbConn, dsTable);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#loadObj(java.sql.Connection, java.lang.Class, int)}
   * .
   */
  @Test
  public void testLoadObj() {
    try {
      T9ORM t = new T9ORM();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");

      T9DsTable dsTable = (T9DsTable) t.loadObjComplex(dbConn, T9DsTable.class, 261);
      T9DsTable dsTable2 = (T9DsTable) t.loadObjSingle(dbConn, T9DsTable.class, 261);
      System.out.println("dsTable 主子表 : " + dsTable);
      System.out.println("dsTable 单表 : " + dsTable2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Test method for
   * {@link t9.core.util.db.T9ORM#loadList(java.sql.Connection, java.lang.Class, java.util.Map)}
   * .
   */
  @Test
  public void testLoadList() {
    try {
      T9ORM t = new T9ORM();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      Map filters = new HashMap();
      filters.put("tableNo", "10006");
      List<T9DsField> dsField = t.loadListComplex(dbConn, T9DsField.class, filters);
      System.out.println("dsTable : " + dsField);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
