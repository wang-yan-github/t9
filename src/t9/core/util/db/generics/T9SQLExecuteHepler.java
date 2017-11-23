package t9.core.util.db.generics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.data.T9DsType;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9StringFormat;

/**
 * SQL语句操作的组装及执行
 * 
 * @author TTlang
 * 
 */
public class T9SQLExecuteHepler {
  private static Map<String, String> tableNoMap = new HashMap<String, String>();
  private static Map<String, List> tableInfoMap = new HashMap<String, List>();
  private static Map<String, Integer> fieldTypeMap = new HashMap<String, Integer>();
  private static Map<String, String> fkFieldNamMap = new HashMap<String, String>();
  private static Map<String, String> fieldNameMap = new HashMap<String, String>();
  
  /**
   * 得到外键关联的值
   * 
   * @param fieldName
   * @param tableName
   * @param seq_id
   * @return
   * @throws Exception
   */
  public static Object getFieldValue(Connection conn, String fieldName,
      String tableName, Object seq_id) throws Exception {
    String sql = "select " + fieldName + " from " + tableName
        + " where SEQ_ID = ? ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    Object[] param = new Object[1];
    String[] fN = new String[1];
    param[0] = seq_id;
    fN[0] = "SEQ_ID";
    try {
      ps = conn.prepareStatement(sql);
      T9SQLParamHepler.javaParam2SQLParam(param, ps, tableName);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getString(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  /**
   * 2.0版 得到从表所对应的外键关联字段
   * 
   * @param Connection
   *          数据库对象
   * @param tableNo
   *          主表在数据字典中的表编码
   * @param tableName
   *          从表的表明
   * @return 从表所对应的外键关联字段 sql：select fieldName from tableName where FK_TBALE_NO
   *         = tableNo and TABLE_NO = 'getTableNo(tableName)';
   * @throws Exception
   */
  public static int getSeqIdValue(Connection conn, String tableName)
      throws Exception {
    String sql2 = "select max(SEQ_ID) from  " + tableName;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int result = 0;
    try {
      ps = conn.prepareStatement(sql2);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  public static Object getDataFieldValue(Connection conn, String tableName,
      String filter) throws Exception {
    String sql = "select ? from ? ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    Object result = 0;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, filter);
      ps.setString(2, tableName);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getObject(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  /**
   * 2.0版 得到从表所对应的外键关联字段
   * 
   * @param Connection
   *          数据库对象
   * @param tableNo
   *          主表在数据字典中的表编码
   * @param tableName
   *          从表的表明
   * @return 从表所对应的外键关联字段 sql：select FIELD_NAME from DS_FIELD where FK_TBALE_NO
   *         = tableNo and TABLE_NO = 'getTableNo(tableName)';
   * @throws Exception
   */
  public static String getDsFKFieldName(Connection conn, String fkTableNo,
      String tableName, String field) throws Exception {
    String key = fkTableNo + "_" + tableName + "_" + field;
    String fieldName = fkFieldNamMap.get(key);
    if (fieldName != null) {
      return fieldName;
    }
    
    String sql = "select " + field
        + " from DS_FIELD where FK_TABLE_NO = ? and TABLE_NO = ? ";
    String tableNo = getTableNo(conn, tableName);
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, fkTableNo);
      ps.setString(2, tableNo);
      rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getString(1);
        fkFieldNamMap.put(key, result);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  public static String getDsFieldName(Connection conn, String fieldNo,
      String filter) throws Exception {
    String fieldName = fieldNameMap.get(fieldNo);
    if (fieldName != null) {
      return fieldName;
    }
    
    String sql = "select " + filter + " from DS_FIELD where FIELD_NO = ? ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, fieldNo);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getString(filter);
        fieldNameMap.put(fieldNo, result);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  /**
   * 2.0版
   * 
   * @param conn
   *          数据库对象
   * @param tableName
   *          数据库表的表名称
   * @return 数据表在数据字典中的tableNo sql：select TABLE_NO from DS_TABLE where
   *         TABLE_NAME = tableName;
   * @throws Exception
   */
  public static String getTableNo(Connection conn, String tableName)
      throws Exception {
    if (tableNoMap.get(tableName) != null) {
      return tableNoMap.get(tableName);
    }
    String sql = "select TABLE_NO from DS_TABLE where TABLE_NAME = ? ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, tableName);
      rs = ps.executeQuery();
      if (rs.next()) {
        result = rs.getString("TABLE_NO");
        tableNoMap.put(tableName, result);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  /**
   * 从数据库中得到自动映射的类型
   * 
   * @param fieldName
   * @return
   * @throws Exception
   */
  public static int getTypeInt(Connection conn, String tableNo, String fieldName)
      throws Exception {
    String key = tableNo + "." + fieldName;
    Integer intValue = fieldTypeMap.get(key);
    if (intValue != null) {
      return intValue.intValue();
    }
    String sql = "select DATA_TYPE from DS_FIELD where FIELD_NO IN ( select FIELD_NO from DS_FIELD where FIELD_NAME = ? and TABLE_NO = ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      ps = conn.prepareStatement(sql);
      ps.setString(1, fieldName);
      ps.setString(2, tableNo);
      rs = ps.executeQuery();
      int typeInt = 0;
      if (rs.next()) {
        typeInt = rs.getInt(1);
        fieldTypeMap.put(key, new Integer(typeInt));
      }
      return typeInt;
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
  
  }
  /**
   * 根据数据类型得到，将字符串值转换成对象的值
   * 
   * @param typeInt
   *          sql数据类型常量
   * @param value
   *          需要转换的值
   * @return
   * @throws Exception
   */
  public static Object paramHelper(int typeInt, String value) throws Exception {
 
    if (T9DsType.isBitType(typeInt)) {
      if (T9Utility.isNullorEmpty(value)) {
        return null;
      }
      return Boolean.valueOf(value);
    } else if (T9DsType.isLongType(typeInt)) {
      if (T9Utility.isNullorEmpty(value)) {
        return null;
      }
      return Long.valueOf(value);
    } else if (T9DsType.isCharType(typeInt)) {
      if (value == null) {
        return null;
      }
      return String.valueOf(value);
    } else if (T9DsType.isDateType(typeInt)) {
      if (T9Utility.isNullorEmpty(value)) {
        return null;
      }
      
      return T9Utility.parseTimeStamp(value);
    } else if (T9DsType.isDecimalType(typeInt)) {
      if (T9Utility.isNullorEmpty(value)) {
        return null;
      }
      return Double.valueOf(value);
    } else if (T9DsType.isIntType(typeInt)) {
      if (T9Utility.isNullorEmpty(value)) {
        return null;
      }
      return Integer.valueOf(value);
    } else if (T9DsType.isClobType(typeInt)) {
      if (value == null) {
        return null;
      }
      return String.valueOf(value);
    } else {
      throw new Exception("数据库中包含不支持的自动映射数据类型：" + T9DsType.getTypeName(typeInt));
    }
  }
  /**
   * 
   * @param tableInfo
   * @return
   * @throws Exception
   */
  private static List<String> getTaleInfo(Connection conn,
      String tableName) throws Exception {
    List<String> tableInfo = tableInfoMap.get(tableName);
    if (tableInfo != null) {
      return tableInfo;
    }
    tableInfo = new ArrayList<String>();
    String sql = "select FIELD_NAME from DS_FIELD where TABLE_NO in ( select TABLE_NO from DS_TABLE where TABLE_NAME = ?)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      ps = conn.prepareStatement(sql);
      ps.setString(1, tableName);
      rs = ps.executeQuery();
      while (rs.next()) {
        tableInfo.add(rs.getString(1));
      }
      tableInfoMap.put(tableName, tableInfo);
      return tableInfo;
    } catch (Exception e){
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, null);
    }
   
  }
  /**
   * 数据结构转换
   * 
   * @param tableName
   *          数据库表名
   * @param formInfo
   *          form表单信息Map<String, String>
   * @return Map<String, Object>
   * @throws Exception
   */
  public static Map<String, Object> form2TableInfo(Connection conn,
      List<Object> formInfo,boolean isCascade) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    String tableName = null;
    List<String> tableInfo = null;
    for (Object tabInfo : formInfo) {
      //表示有从表
      if (List.class.isInstance(tabInfo)) {
        if(!isCascade){
          continue;
        }
          Map m = form2TableInfo(conn, (List<Object>) tabInfo,isCascade);
          String subTable = (String) m.get("tableName");
          result.put(subTable, m);
        continue;
      }
      tableName = (String) tabInfo;
      tableName = T9StringFormat.format(tableName);
      result.put("tableName", tableName);
      tableInfo = getTaleInfo(conn, tableName);
      result.put(tableName, tableInfo);
    }
    return result;
  }

  /**
   * 数据结构转换
   * 
   * @param tableName
   *          数据库表名
   * @param formInfo
   *          form表单信息Map<String, String>
   * @return Map<String, Object>
   * @throws Exception
   */
  public static Map<String, Object> form2TableInfo(Connection conn,
      String tableName, Map<String, Object> formInfo,boolean isCascade) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    Set<String> keys = formInfo.keySet();
    tableName = T9StringFormat.format(tableName);
    result.put("tableName", tableName);
    for (String key : keys) {
      Object value = formInfo.get(key);
      if(value == null){
        continue;
      }
      // 判断从类信息
      if (List.class.isInstance(value)) {
        if(!isCascade){
          continue;
        }
        List l = (List) value;
        ArrayList newSub = new ArrayList();
        for (Object subMap : l) {
          Map m = form2TableInfo(conn, key, (Map<String, Object>) subMap,isCascade);
          newSub.add(m);
        }
        result.put(key, newSub);
        continue;
      }
      key = T9StringFormat.format(key);
      String tableNo = getTableNo(conn, tableName);
      int typeInt = getTypeInt(conn, tableNo, key);
      Object realValue = paramHelper(typeInt, value.toString());
      result.put(key, realValue);
    }
    return result;
  }

}