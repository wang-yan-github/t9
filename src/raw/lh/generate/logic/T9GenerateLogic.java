package raw.lh.generate.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.db.T9DBUtility;

public class T9GenerateLogic {

  public List<Map> getFields(Connection conn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    Statement stm = null;
    ResultSet rs = null;
    List<Map> list = new ArrayList();
    String sql = "select * from SYS_TABLE_FIELD WHERE TABLE_ID=" + seqId;
    try {
      stm = conn.createStatement();
      rs =  stm.executeQuery(sql);
      while (rs.next()) {
        Map map = new HashMap();
        map.put("name", rs.getString("FIELD_NAME"));
        map.put("type", rs.getString("FIELD_TYPE"));
        map.put("length", rs.getString("FIELD_LENGTH"));
        list.add(map);
      } 
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return list;
  }
  public String getOracleSql( List<Map> fields , String tableName) {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    sb.append("create table "+tableName+" (\r\n");
    sb.append("SEQ_ID NUMBER primary key,\r\n");
    sb.append("NO VARCHAR2(200),\r\n");
    for (Map m : fields) {
      String fieldName = (String) m.get("name");
      String type = (String) m.get("type");
      String length = (String) m.get("length");
      String temp = "";
      if ("1".equals(type)) {
        temp = "VARCHAR2(" + length + ") ";
      } else if ("2".equals(type)) {
        temp = "NUMBER(" + length + ") ";
      } else if ("3".equals(type)) {
        temp = "NUMBER(" + length + ") ";
      } else if ("4".equals(type)) {
        temp = "TIMESTAMP";
      }
      sb.append(fieldName + " ");
      sb.append(temp);
      sb.append(",\r\n");
    }
    //if (fields.size() > 0) {
      sb.deleteCharAt(sb.length() - 2);
      sb.append("\r\n");
    //} 
    sb.append(");\r\n");
    sb.append("exec pr_createidentitycolumn('"+tableName+"','SEQ_ID');");
    return sb.toString();
  }
  public String getMysqlSql( List<Map> fields , String tableName) {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    sb.append("create table "+tableName+" (\r\n");
    sb.append("SEQ_ID  INTEGER UNSIGNED NOT NULL AUTO_INCREMENT primary key,\r\n");
    sb.append("NO VARCHAR(200) ,\r\n");
    for (Map m : fields) {
      String fieldName = (String) m.get("name");
      String type = (String) m.get("type");
      String length = (String) m.get("length");
      String temp = "";
      if ("1".equals(type)) {
        temp = "VARCHAR(" + length + ") ";
      } else if ("2".equals(type)) {
        temp = "INTEGER(" + length + ") ";
      } else if ("3".equals(type)) {
        temp = "FLOAT(" + length + ") ";
      } else if ("4".equals(type)) {
        temp = "TIMESTAMP";
      }
      sb.append(fieldName + " ");
      sb.append(temp);
      sb.append(",");
    }
    //if (fields.size() > 0) {
      sb.deleteCharAt(sb.length() - 1);
      sb.deleteCharAt(sb.length() - 1);
      sb.append("\r\n");
    //} 
    sb.append(")ENGINE = InnoDB;\r\n");
    return sb.toString();
  }
  public String getMssqlSql(List<Map> fields , String tableName) {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    sb.append("create table "+tableName+" (\r\n");
    sb.append("SEQ_ID  numeric(11) NOT NULL IDENTITY UNIQUE primary key,\r\n");
    sb.append("NO VARCHAR(200) ,\r\n");
    for (Map m : fields) {
      String fieldName = (String) m.get("name");
      String type = (String) m.get("type");
      String length = (String) m.get("length");
      String temp = "";
      if ("1".equals(type)) {
        temp = "VARCHAR(" + length + ") ";
      } else if ("2".equals(type)) {
        temp = "numeric(" + length + ") ";
      } else if ("3".equals(type)) {
        temp = "decimal(" + length + ") ";
      } else if ("4".equals(type)) {
        temp = "datetime";
      }
      sb.append(fieldName + " ");
      sb.append(temp);
      sb.append(",\r\n");
    }
    //if (fields.size() > 0) {
      sb.deleteCharAt(sb.length() - 2);
      sb.append("\r\n");
    //} 
    sb.append(");\r\n");
    return sb.toString();
  }
}
