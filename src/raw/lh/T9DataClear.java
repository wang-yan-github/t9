package raw.lh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;

public class T9DataClear {
  public static void main1(String[] args) throws Exception {
    
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3396/t9" , "root" , "myoa888");
    List<String> rtList = new ArrayList();
    Statement stm = null;
    try {
      T9FileUtility.loadLine2Array("d:\\temp\\dataTable.txt", rtList);
      for(String str : rtList) {
        String delete = "delete from " + str ;
        if ("PERSON".equals(str)) {
          delete += " where SEQ_ID <> 1 ";
          System.out.print(delete);
        }
        if ("USER_PRIV".equals(str)) {
          delete += " where SEQ_ID <> 1 ";
          System.out.print(delete);
        }
        stm = conn.createStatement();
        int result = 0;
        if (!"PERSON".equals(str) && !"USER_PRIV".equals(str) ) {
          result = stm.executeUpdate("Truncate table " + str);
        } else {
          result = stm.executeUpdate(delete);
        }
        if (result < 0) {
          System.out.println(str);
        } else {
          System.out.println("已清空:" + str);
        }
      }
      //update person set dept_id = (select seq_id from department);
      //conn.commit();
    } catch (Exception ex ) {
      conn.rollback();
      ex.printStackTrace();
    } finally {
      if (stm != null) {
        stm.close();
      } 
      if (conn != null) {
        conn.close();
      }
    }
  }
  
  public static void copyTable(String name , Connection srcConn , Connection desConn,String  type) throws Exception {
    String deleteTable = "delete from " + name;
    exSql(desConn,deleteTable);
    String alterTag = "alter trigger TRG_"+ name +" disable";
//    if ("mssql".equals(type)) {
//      alterTag = "SET IDENTITY_INSERT [" + name +"] OFF";
//    }
    exSql(desConn,alterTag);
//    
    String query = "select * from " + name;
    String insertInto = "insert into " + name;
    Statement stm = null;
    ResultSet rs = null;
    String field = "";
    String val = "";
    try {
      stm = srcConn.createStatement();
      rs = stm.executeQuery(query);
      ResultSetMetaData rsm = rs.getMetaData();
      int count = rsm.getColumnCount();
      for (int i = 1 ;i <= count ; i++) {
        field += rsm.getColumnName(i) + ",";
        val += "?,";
      }
      field = T9WorkFlowUtility.getOutOfTail(field);
      val = T9WorkFlowUtility.getOutOfTail(val);
      insertInto += " (" + field + ") values (" + val + ")";
      while (rs.next()) {
        PreparedStatement stm2 = null;
        try {
          stm2 = desConn.prepareStatement(insertInto);
          for (int i = 1 ;i <= count ; i++) {
            stm2.setObject(i, rs.getObject(i));
          }
          stm2.executeUpdate();
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm2, null, null); 
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    String alterTag2 = "alter trigger TRG_"+ name +" enable";
    //if ("mssql".equals(type)) {
    //  alterTag2 = "SET IDENTITY_INSERT [" + name +"] ON";
   // } 
    exSql(desConn,alterTag2);
  }
  
  public static void exSql(Connection conn , String sql) throws Exception {
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
public static void main(String[] args) throws Exception {
  String tables = "";
  String type = "";
  explort(tables , type);
  //getAlterTrg2(tables , type);
      //"video_meeting_manager,bus,attend_manager,port,port_style,portal,portal_port,department,person,user_priv,flow_sort,flow_type,flow_form_type,flow_process,flow_form_item,sys_menu,sys_function,code_class,code_item,sys_para,hr_code");
}
public static void explort(String tables ,String  type)  throws Exception{
  Class.forName("oracle.jdbc.driver.OracleDriver");
  Connection conn2 = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl" , "T9PRO_1" , "test");
  //Class.forName("net.sourceforge.jtds.jdbc.Driver");
  //Connection conn2 = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.0.3:1433;DatabaseName=T9PRODUCT" , "sa" , "tongda");
  
  Class.forName("com.mysql.jdbc.Driver");
  Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3396/t9-product" , "root" , "myoa888");
  String[] ta = tables.split(",");
  for (String s : ta) {
    copyTable(s ,conn , conn2 ,type ) ;
  }
  //conn2.commit();
  conn2.close();
  conn.close();
}

public static void getAlterTrg(String ss ,String  type){
  String[] sss = ss.split(",");
  for (String s : sss) {
    String alterTag2 = "alter trigger TRG_"+ s +" enable;\r\n";
    if ("mssql".equals(type)) {
      alterTag2 = "SET IDENTITY_INSERT [" + s +"] ON;\r\n";
    }
    
    System.out.append(alterTag2);
    
  }
}
public static void getAlterTrg2(String ss ,String  type){
  String[] sss = ss.split(",");
  for (String s : sss) {
    String alterTag2 = "alter trigger TRG_"+ s +" enable;\r\n";
    if ("mssql".equals(type)) {
      alterTag2 = "SET IDENTITY_INSERT [" + s +"] OFF;\r\n";
    }
    
    System.out.append(alterTag2);
  }
}
}
