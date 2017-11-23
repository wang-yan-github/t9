package raw.lh.generate.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9SysTableLogic {
  public StringBuffer getTableList( Connection conn, String  queryTableName , Map request)
  throws Exception {
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      sql = "select SEQ_ID , TABLE_NAME FROM sys_table WHERE 1=1 ";
      if (!T9Utility.isNullorEmpty(queryTableName)) {
        sql += " AND TABLE_NAME like '%" + T9Utility.encodeLike(queryTableName) + "%'";
      }
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return resualt;
  }
  public void delTable( Connection conn, String  queryTableName)
  throws Exception {
    String sql = "";
      sql = "DELETE FROM sys_table WHERE SEQ_ID IN (" + queryTableName + ") ";
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
  public void addTable( Connection conn, String  tableName , String fieldPre , String isFieldDefault , String fieldLength , String fieldStart)
  throws Exception {
    String sql = "insert into sys_table (TABLE_NAME, NO_FIELD_PRE, NO_FIELD_LENGTH, NO_FIELD_START, NO_FIELD_DEFAULT)  VALUES ('" 
      + tableName + "','"
      + fieldPre +"','"
      + fieldLength+"','" 
      + fieldStart+"','" 
      + isFieldDefault+"') ";
    this.exSql(conn, sql);
  }
  public void updateTable(Connection dbConn, String seqId, String tableName,
      String fieldPre, String isFieldDefault, String fieldLength,
      String fieldStart) throws Exception {
    // TODO Auto-generated method stub
    String sql = "";
    sql = "update  sys_table "
    		+ " set TABLE_NAME = '"  + tableName + "'"
    		+ ",NO_FIELD_PRE = '" + fieldPre +"'"
        + ",NO_FIELD_LENGTH ='" +  fieldLength+"'" 
        + ",NO_FIELD_START ='" +  fieldStart+"'" 
        + ",NO_FIELD_DEFAULT ='" +  isFieldDefault+"' where SEQ_ID = '" + seqId +"'";
    this.exSql(dbConn, sql);
  }
  public void exSql(Connection conn , String sql) throws Exception {
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
  public String getTableInfo(Connection conn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    Statement stm = null;
    ResultSet rs = null;
    String sql = "select * from SYS_TABLE WHERE SEQ_ID=" + seqId;
    try {
      stm = conn.createStatement();
      rs =  stm.executeQuery(sql);
      if (rs.next()) {
        String o = "{";
         o += rsToJson(rs  , "TABLE_NAME, NO_FIELD_PRE, NO_FIELD_LENGTH, NO_FIELD_START, NO_FIELD_DEFAULT");
          o +="}";
        return o;
      } 
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return "{}";
  }
  public String rsToJson(ResultSet rs , String fieldNames) throws Exception{
    if (T9Utility.isNullorEmpty(fieldNames)) {
      return "";
    }
    String[] fieldName = fieldNames.split(",");
    String o = "";
    for (String fi : fieldName) {
      String v = rs.getString(fi.trim());
      o += fi + ":\"" + T9Utility.encodeSpecial(v) + "\",";
    }
    if (o.endsWith(",")) {
      o = o.substring(0, o.length() - 1);
    }
    return o;
  }
  public StringBuffer getTableFieldList(Connection dbConn,
      String queryTable, Map request) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer resualt = new StringBuffer();
    String sql = "";
    try {
      sql = "select SEQ_ID , FIELD_NAME ,FIELD_TYPE , FIELD_LENGTH FROM sys_table_field WHERE 1=1 AND TABLE_ID = '" + queryTable + "'";
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
      resualt.append(pageDataList.toJson());
    } catch (Exception ex) {
      throw ex;
    }
    return resualt;
  }
  public void addTableField(Connection dbConn, String field_name_edit,
      String field_type_edit, String field_length_edit , String tableId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "insert into sys_table_field ( FIELD_NAME, FIELD_TYPE, FIELD_LENGTH, TABLE_ID)  VALUES ('" 
      + field_name_edit + "','"
      + field_type_edit +"','"
      + field_length_edit +"','"
      + tableId+"' ) ";
    this.exSql(dbConn, sql);
  }
  public void updateTableField(Connection dbConn, String seqId,
      String field_name_edit, String field_type_edit, String field_length_edit) throws Exception {
    // TODO Auto-generated method stub
    String sql = "";
    sql = "update  sys_table_field "
        + " set FIELD_NAME = '"  + field_name_edit + "'"
        + ",FIELD_TYPE = '" + field_type_edit +"'"
        + ",FIELD_LENGTH ='" +  field_length_edit+"' where SEQ_ID = '" + seqId +"'";
    this.exSql(dbConn, sql);
  }
  public void delTableField(Connection dbConn, String seqIds, String tableId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "";
    sql = "DELETE FROM sys_table_field WHERE SEQ_ID IN (" + seqIds + ") and TABLE_ID = '" + tableId + "'";
  Statement stm = null;
  try {
    stm = dbConn.createStatement();
     stm.executeUpdate(sql);
  } catch(Exception ex) {
    throw ex;
  } finally {
    T9DBUtility.close(stm, null, null); 
  }
  }
}
