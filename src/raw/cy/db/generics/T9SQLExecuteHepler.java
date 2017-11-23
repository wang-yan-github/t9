package raw.cy.db.generics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * SQL语句操作的组装及执行
 * 
 * @author TTlang
 * 
 */
public class T9SQLExecuteHepler {

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
    param[0] = seq_id;
    try {
      ps = conn.prepareStatement(sql);
      T9SQLParamHepler.javaParam2SQLParam(param, ps);
      // ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getString(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
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
    String sql = "select max(SEQ_ID) from ? ";
    // System.out.println(
    // "select FIELD_NAME from DS_FIELD where FK_TABLE_NO = '"+fkTableNo+"' and TABLE_NO = '"+tableNo+"' ");
    PreparedStatement ps = null;
    ResultSet rs = null;
    int result = 0;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, tableName);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
    return result;
  }

  public static Object getDataFieldValue(Connection conn, String tableName,
      String filter) throws Exception {
    String sql = "select ? from ? ";
    // System.out.println(
    // "select FIELD_NAME from DS_FIELD where FK_TABLE_NO = '"+fkTableNo+"' and TABLE_NO = '"+tableNo+"' ");
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
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
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
      String tableName, String filter) throws Exception {
    String sql = "select " + filter
        + " from DS_FIELD where FK_TABLE_NO = ? and TABLE_NO = ? ";
    String tableNo = getTableNo(conn, tableName);
    String sql2 = "select " + filter + " from DS_FIELD where FK_TABLE_NO = "
        + fkTableNo + " and TABLE_NO = " + tableNo;
    System.out.println(sql2);
    // System.out.println(
    // "select FIELD_NAME from DS_FIELD where FK_TABLE_NO = '"+fkTableNo+"' and TABLE_NO = '"+tableNo+"' ");
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, fkTableNo);
      ps.setString(2, tableNo);
      // ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      ResultSetMetaData stmy = ps.getMetaData();
      while (rs.next()) {
        System.out.println("filter >>> " + filter + " : fkTableNo : "
            + fkTableNo + " : tableName : " + filter);
        System.out.println(stmy.getColumnName(1));
        result = rs.getString(1);
        System.out.println("result >>> " + result);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
    return result;
  }

  public static String getDsFieldName(Connection conn, String fieldNo,
      String filter) throws Exception {
    String sql = "select " + filter + " from DS_FIELD where FIELD_NO = ? ";
    // System.out.println(
    // "select FIELD_NAME from DS_FIELD where FK_TABLE_NO = '"+fkTableNo+"' and TABLE_NO = '"+tableNo+"' ");
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, fieldNo);
      rs = ps.executeQuery();
      System.out.println("sql >>> " + sql);
      while (rs.next()) {
        result = rs.getString(filter);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
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
    String sql = "select TABLE_NO from DS_TABLE where TABLE_NAME = ? ";
    // System.out.println(tableName);
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.setString(1, tableName);
      rs = ps.executeQuery();
      while (rs.next()) {
        result = rs.getString("TABLE_NO");
        // System.out.println(result);
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
      }
    }
    return result;
  }
}