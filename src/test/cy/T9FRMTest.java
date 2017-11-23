package test.cy;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import raw.cy.db.frm.T9FOM;
import raw.cy.db.frm.T9FRM;
import t9.core.data.T9DsField;
import test.core.util.db.TestDbUtil;

public class T9FRMTest {

  @Test
  public void testSave() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "t9");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Map m =new HashMap();
    m.put("tableNo", "10081");
    m.put("tableName", "CHEN_YI");
    m.put("className", "T9ChenYi");
    m.put("tableDesc", "yyyy");
    m.put("dbNo", "101");
    m.put("categoryNo", "1");
    ArrayList fieldList = new ArrayList();
    Map m2 =new HashMap();
   
    m2.put("fieldNo", "10081001");
    m2.put("fieldName", "cc");
    m2.put("tableNo", "10081");
    fieldList.add(m2);
    m.put("dsField", fieldList);
    
    T9FRM fr = new T9FRM();
    try {
      fr.save(dbConn, "dsTable", m);
      System.out.println("m : "+m);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testUpdate() {
  }

  @Test
  public void testDelete() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "t9");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    T9FRM fr = new T9FRM();
    Map m =new HashMap();
    m.put("seqId", "257");
    try {
      fr.delete(dbConn, "dsTable", m);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Test
  public void testLoadData() {
    Connection dbConn = null;
    try {
      dbConn = TestDbUtil.getConnection(false, "T9");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    List formInfo = new ArrayList();
    List subInfo = new ArrayList();
    subInfo.add("dsField");
   // List subInfo2 = new ArrayList();
   // subInfo2.add("chenYi");
    //subformInfo.add(subInfo2);
    formInfo.add("dsTable");
    //formInfo.add("dsField");
    formInfo.add(subInfo);
    T9FRM fr = new T9FRM();
    Map m =new HashMap();
    m.put("TABLE_NO", "10003");
    
    try {
      Map te = fr.loadData(dbConn, formInfo, m);
    
      String s = T9FOM.map2Json(te).toString();
      System.out.println("sss >> "+formInfo);
      System.out.println("map : >> "+te);
      System.out.println("size : "+te.size()+" tables: "+s);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
