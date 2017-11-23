package test.core.util.db;

import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;

import t9.core.funcs.workflow.util.T9FlowFormLogic;
import t9.core.util.db.T9DTJ;

public class T9DTJTest {

  @Test
  public void testToSqlString() {
   
   Connection dbConn;
  try {
    dbConn = TestDbUtil.getConnection(false, "TEST");
    T9DTJ tdj = new T9DTJ();
    System.out.println(tdj.toJson2Flex(dbConn, "10004",0,2,null));
  } catch (Exception e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
  }

  @Test
  public void testLoadData() {
    fail("Not yet implemented");
  }

  @Test
  public void testToJson() {
    try {
      T9DTJ dtt = new T9DTJ();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      StringBuffer sb = dtt.toJson(dbConn, "10004", null);
      System.out.println("tojson : "+sb.toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  @Test
  public void testToJsonPage() {
    try {
      T9DTJ dtt = new T9DTJ();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      String sb = dtt.toJson(dbConn, "10004",0,4,null );
      System.out.println("tojson : "+sb);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  @Test
  public void testFormUtil() {
    try {
      T9FlowFormLogic dtt = new T9FlowFormLogic();
      Connection dbConn = TestDbUtil.getConnection(false, "TEST");
      int seqID = dtt.deleteDeptMul(dbConn, 15);
      String func = "";
      String dd = dtt.deleteDept(dbConn, seqID);
      String[]str = dd.split(",");
      for(int i = 0; i<str.length; i++){
        func = str[i];
        System.out.println("tojsontojsontojsontojson++++ : "+func);
      }
     // System.out.println("tojsontojsontojsontojson++++ : "+dd);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
