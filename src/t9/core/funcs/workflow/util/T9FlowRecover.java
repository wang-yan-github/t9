package t9.core.funcs.workflow.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.util.db.T9DBUtility;

public class T9FlowRecover {
  public static String PRE_OLD = "T9ProductOld.dbo.";
  public static String PRE_NEW = "T9Product.dbo.";
  
  public static void recoverFlowRun(Connection dbConn, int runId) {
    //如果没有runId
    String sql6 = "select 1 from  "+ PRE_NEW +"flow_run  where run_id="+ runId;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql6);
      if (rs.next()) {
        return ;
      }
    } catch (Exception ex) {
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    insertTable(dbConn , "FLOW_RUN",  runId);
    insertTable(dbConn , "flow_run_prcs",  runId);
    insertTable(dbConn , "flow_run_feedback",  runId);
    insertTable(dbConn , "flow_run_log",  runId);
  }
  public static void insertTable(Connection  dbConn , String tableName, int runId) {
    String field = getFields( dbConn , "select * from "+ PRE_OLD + tableName);
    String sql = "insert  "+ PRE_NEW + tableName + "  select " +  field +" from "+ PRE_OLD + tableName +"  as b where b.run_id = "+runId+"";
    exeSql(dbConn , sql);
  }
  
  public static void recoverByFlowId(Connection dbConn , Connection newConn, int oldFlowId ) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    
    String ss = " update "+ PRE_OLD +"FLOW_TYPE set FLOW_NAME = '工程信息跟踪情况表old' where SEQ_ID = " + oldFlowId;
    exeSql(dbConn , ss);
    
    ss = "insert  "+ PRE_NEW +"flow_type select b.FLOW_NAME, b.FORM_SEQ_ID, b.FLOW_DOC, b.FLOW_TYPE, b.MANAGE_USER, b.FLOW_NO, b.FLOW_SORT, b.AUTO_NAME, b.AUTO_NUM, b.AUTO_LEN, b.QUERY_USER, b.FLOW_DESC, b.AUTO_EDIT, b.NEW_USER, b.QUERY_ITEM, b.COMMENT_PRIV, b.DEPT_ID, b.FREE_PRESET, b.FREE_OTHER, b.QUERY_USER_DEPT, b.MANAGE_USER_DEPT, b.EDIT_PRIV, b.MODEL_ID, b.MODEL_NAME, b.LIST_FLDS_STR, b.ALLOW_PRE_SET, b.IS_SYSTEM, b.VIEW_USER, b.VIEW_PRIV from "+ PRE_OLD +"FLOW_TYPE as b where b.SEQ_ID = " + oldFlowId;
    exeSql(dbConn , ss);
    
    int newFlowId = getFirstColValue(dbConn , "select max(SEQ_ID) FROM "+ PRE_NEW +"flow_type ");
    String oldFlowPrcs = "update "+ PRE_OLD +"flow_process set flow_seq_id = "+newFlowId+" where flow_seq_id=" + oldFlowId;
    exeSql(dbConn , oldFlowPrcs);
    String oldFlow1 = "update "+ PRE_OLD +"flow_run set flow_id = "+newFlowId+" where flow_id=" + oldFlowId;
    exeSql(dbConn , oldFlow1);
   
    String sql1 = "insert "+ PRE_NEW +"flow_process "
        + " select FLOW_SEQ_ID, PRCS_ID, PRCS_NAME, PRCS_USER, PRCS_ITEM, HIDDEN_ITEM, PRCS_DEPT, PRCS_PRIV, PRCS_TO, SET_LEFT, SET_TOP, PLUGIN, PRCS_ITEM_AUTO, PRCS_IN, PRCS_OUT, FEEDBACK, PRCS_IN_SET, PRCS_OUT_SET, AUTO_TYPE, AUTO_USER_OP, AUTO_USER, USER_FILTER, TIME_OUT, TIME_EXCEPT, SIGNLOOK, TOP_DEFAULT, USER_LOCK, MAIL_TO, SYNC_DEAL, SYNC_DEAL_CHECK, TURN_PRIV, CHILD_FLOW, GATHER_NODE, ALLOW_BACK, ATTACH_PRIV, AUTO_BASE_USER, CONDITION_DESC, RELATION, REMIND_FLAG, DISP_AIP, TIME_OUT_TYPE, METADATA_ITEM, EXTEND, EXTEND1, AUTO_SELECT_ROLE, DOC_CREATE, DOC_ATTACH_PRIV, VIEW_PRIV, CONTROL_MODE from "+ PRE_OLD +"flow_process where FLOW_SEQ_ID="+newFlowId+"";
    exeSql(dbConn , sql1);
    
    int oldFormId = 759;
    int newFormId = 786;
    
  
    //流程版本是最新的，应该更新到最新
    exeSql(dbConn , "update "+ PRE_NEW +"FLOW_TYPE SET FORM_SEQ_ID =" + newFormId + " where SEQ_ID = " + newFlowId);
    //数据表，应该建多个，一个老的，按照原来的。一个新的。
    T9WorkflowSave2DataTableLogic logic = new T9WorkflowSave2DataTableLogic();
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery("select SEQ_ID FROM "+ PRE_NEW +"flow_form_type where FORM_ID = " + newFormId);
      while (rs.next()) {
        int formId = rs.getInt(1);
        logic.createTable(newConn, "FORM_DATA_" + newFlowId + "_" + formId, String.valueOf(formId));
        System.out.println("FORM_DATA_" + newFlowId + "_" + formId);
        insertTable( newConn ,  newFlowId  ,   oldFlowId ,  formId ,  formId) ;
      }
    } catch (Exception ex) {
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    
    logic.createTable(newConn, "FORM_DATA_" + newFlowId + "_" + newFormId, String.valueOf(newFormId));
    insertTable( newConn ,  newFlowId  ,   oldFlowId ,  newFormId ,  oldFormId) ;
   
    //复制FLOW_RUN
    try {
      stmt = dbConn.createStatement();
      String query = "select run_id from "+ PRE_OLD +"flow_run where FLOW_ID = " + newFlowId;
      System.out.println(query);
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        int runId = rs.getInt(1);
        System.out.println(runId);
        recoverFlowRun(dbConn,  runId);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  public static void insertTable(Connection  dbConn , int newFlowId  ,  int oldFlowId , int newFormId , int oldFormId) {
    String field = getFields( dbConn , "select * from "+ PRE_OLD +"FORM_DATA_" + oldFlowId + "_" + oldFormId);
    Statement stmt2 = null;
    try {
      stmt2 = dbConn.createStatement();
      String sql = "SET IDENTITY_INSERT [FORM_DATA_" + newFlowId + "_" + newFormId + "] ON";
      //stmt2.execute(sql);
      String insertInto = "insert FORM_DATA_" + newFlowId + "_" + newFormId +  " select " + field +" from "+ PRE_OLD +"FORM_DATA_" + oldFlowId + "_" + oldFormId +" as b";
      System.out.println(insertInto);
      stmt2.execute(insertInto);
      sql = "SET IDENTITY_INSERT [FORM_DATA_" + newFlowId + "_" + newFormId + "] OFF";
      //stmt2.execute(sql);
      //dbConn.commit();
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      T9DBUtility.close(stmt2, null, null);
    }
  }
  
  public static String getFields(Connection conn , String sql) {
    Statement stmt = null;
    ResultSet rs = null;
    String field = "";
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      ResultSetMetaData md = rs.getMetaData();
      for (int i = 1 ; i <= md.getColumnCount() ;i++) {
        String colName = md.getColumnName(i);
        if (!"SEQ_ID".equals(colName)) {
          field += "b." + colName + ",";
        }
      }
    } catch (Exception ex) {
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (field.endsWith(",")) {
      field = field.substring(0 , field.length() - 1);
    }
    return field;
  }
  
  public static int getFirstColValue(Connection conn , String sql) {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (Exception ex) {
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return 0;
  }
  public static Connection getOAConn(String db) throws Exception {
    Connection conn = null;
    try {
      Class.forName("net.sourceforge.jtds.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:jtds:sqlserver://localhost:1433;" +db
      		//"DatabaseName=T9PRODUCT_TEST" 
          , "sa", "myoa888" );
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return conn;
  }
  public static void main(String args[]) throws Exception {
    Connection old = getOAConn("");
    Connection newConn = getOAConn("DatabaseName=T9Product29");
    recoverByFlowId(old, newConn,  652);
  }
  public static void exeSql(Connection conn , String sql) {
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      System.out.println(sql);
      ex.printStackTrace();
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  
  
}
