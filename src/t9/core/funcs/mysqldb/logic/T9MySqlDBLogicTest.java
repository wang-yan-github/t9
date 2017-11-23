package t9.core.funcs.mysqldb.logic;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;


public class T9MySqlDBLogicTest {

  @Test
  public void testGetTableInfo() {
      T9MySqlDBLogic my = new T9MySqlDBLogic();
      try {
       // Connection conn = TestDbUtil.getConnection(false, "t9");
       // my.getTableInfo(conn);
      } catch (Exception e) {
        e.printStackTrace();
      }
     
  }

}
