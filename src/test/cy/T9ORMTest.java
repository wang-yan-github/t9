package test.cy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import raw.cy.db.generics.T9ORM;
import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import test.core.util.db.TestDbUtil;


public class T9ORMTest {

  @Test
  public void testSave() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "TEST");
    } catch (Exception e) {
      // TODO Auto-generated catch block
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
      t.save(dbConn, tab);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testUpdate() {
    int i = 2;
    System.out.println((i|1)==0);
    System.out.println(i|2);
  }

  @Test
  public void testDeleteConnectionInt() {
        System.out.println("not tests");
  }

  @Test
  public void testDeleteConnectionObject() {
  /*  Connection conn = DBUtils.getCon() ;
    T9ORM t = new T9ORM();
    UserM u = new UserM();
    u.setBrithday(new Date());
    u.setUserNameCn("sss");
    u.setUserNameEn("sdfsd");
    u.setSeqId(10);
    try {
     t.delete(conn, u);
     
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  @Test
  public void testLoadObj() {
    try {
      T9ORM t = new T9ORM();
      System.out.println("ddddd");
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      System.out.println(dbConn);
      
      T9DsTable dsTable = (T9DsTable) t.loadObj(dbConn, T9DsTable.class,134);
      /*t.delete(dbConn, dsTable);
      t.save(dbConn, dsTable);*/
      dsTable.setTableNo("10115");
      ArrayList<T9DsField> list = dsTable.getFieldList();
      for (T9DsField t9DsField : list) {
        t9DsField.setTableNo("10115");
      }
      dsTable.setFieldList(list);
      t.update(dbConn, dsTable);
      System.out.println("dddd .>>> "+dsTable);
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testLoadList() {
   /* Connection conn = DBUtils.getCon() ;
    List list = null;
    T9ORM t = new T9ORM();
    Map m = new HashMap();
    WhereCondition where = new WhereCondition();
    where.appendClauses("userNameCn", "fs")
          .appendClauses("userNameEn", "che");
    
    where.setTarken("and");
    m.put(where.getWhereSqlString(), where);
    try {
      list = t.loadList(conn,UserM.class , m);
      for (Object object : list) {
        System.out.println(((UserM)object).getSeqId()+" : "+((UserM)object).getUserNameEn());
      }
     
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

}
