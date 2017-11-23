package test.core.util.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import t9.core.data.T9DsTable;
import t9.core.util.db.T9ORM;
import test.core.util.db.TestDbUtil;

public class T9ORMTest {

  @Test
  public void testSaveSingleConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testSaveComplexConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testSaveSingleConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testSaveComplexConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testUpdateSingleConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testUpdateComplexConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testUpdateSingleConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testUpdateComplexConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testDeleteSingleConnectionClassInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testDeleteSingleConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testDeleteComplexConnectionStringMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testDeleteSingleConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testDeleteComplexConnectionObject() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadObjSingleConnectionClassInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadDataSingle() {
    try {
      T9ORM t = new T9ORM();
      Connection dbConn = TestDbUtil.getConnection(false, "t9");
      List formInfo = new ArrayList();
      List subInfo = new ArrayList();
      subInfo.add("dsField");
      formInfo.add("dsTable");
      formInfo.add(subInfo);
      //Map m =new HashMap();
      //m.put("TABLE_NO", "10003");
      String[] m = new String[]{"TABLE_NO like '10%'","TABLE_NAME = 'sd'"};
      //WHERE TABLE_NO LIKE '10%' AND TABNLE_NAME='sd';
      Map ma = t.loadDataSingle(dbConn, formInfo, m);
    //  Map ma2 = t.loadDataComplex(dbConn, formInfo, m);
      System.out.println("dsTable 单表 : " + ma);
   //   System.out.println("dsTable 单表 : " + ma2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testLoadDataComplex() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadObjComplexConnectionClassInt() {
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

  @Test
  public void testLoadObjSingleConnectionClassMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadObjComplexConnectionClassMap() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadListSingle() {
    fail("Not yet implemented");
  }

  @Test
  public void testLoadListComplex() {
    fail("Not yet implemented");
  }

}
