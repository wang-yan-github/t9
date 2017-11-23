package raw.cy.db.generics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class T9JObject2SQLHepler {

  public static Map<String, Object> javaObject2InsertSQL(
      Map<String, Object> fieldInfo, Connection conn, PreparedStatement pstmt)
      throws Exception {

    System.out.println("FieldInfoMap's SIZE before removed : >>> "
        + fieldInfo.size());

    String tableName = (String) fieldInfo.get("tableName");
    fieldInfo.remove("tableName");
    fieldInfo.remove("SEQ_ID");
    StringBuffer sql = new StringBuffer("insert into ").append(tableName)
        .append(" ( ");
    StringBuffer values = new StringBuffer(" values ( ");

    int length = fieldInfo.size();
    Object[] params = new Object[length];
    int i = 0;
    Set set = fieldInfo.keySet();
    Object[] keys = set.toArray();
    for (int j = 0; j < keys.length; j++) {
      String key = (String) keys[j];
      Object value = fieldInfo.get(keys[j]);
      // System.out.println("key : " + key + " value : " + value);
      // 判断是否为list类型，如果是list类型则为从表信息,忽略
      if (value != null && List.class.isAssignableFrom(value.getClass())) {
        continue;
      }

      sql.append(key);
      values.append(" ? ");
      params[i] = value;
      i++;

      // 为pstmt传值
      if (j < (keys.length - 1)) {
        sql.append(" , ");
        values.append(" , ");
      }
    }
    sql.append(" ) ").append(values).append(" ) ");
    // System.out.println(sql.toString() + " params length : "+ params.length);
    try {
      pstmt = conn.prepareStatement(sql.toString());
      T9SQLParamHepler.javaParam2SQLParam(params, pstmt);
      pstmt.executeUpdate();
      conn.commit();
    } catch (SQLException e) {
      try {
        conn.rollback();
      } catch (SQLException e1) {
        throw e1;
      }
      e.printStackTrace();
      throw e;
    } finally {
      try {
        pstmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }
    System.out.println("FieldInfoMap's SIZE after removed : >>> "
        + fieldInfo.size());
    System.out.println("SQL for Insert : >>> " + sql.toString());
    return fieldInfo;
  }

  /**
   * 2.0版 组织SQL的update语句 需要注意的特殊情况 更新主表是的级联更新从表
   * 采取的策略是：查出所有从表信息然后删除所有从表，更新主表，跟新从表，将从表信息再插入数据库中
   * 总的实现思路为：从数据字典中查询出从表的外键关联字段，通过主表的seq_id查询出次关联字段对应的值，再通过这个值进行操作
   * 
   * @param fieldInfo
   * @param sqls
   * @throws Exception
   */
  public static void javaObject2UpdateSQL(Map<String, Object> fieldInfo,
      Connection conn, PreparedStatement pstmt) throws Exception {

    /*
     * 此处得到的数据有两种可能： 1.SEQ_ID 表明主从表之间是通过SEQ_ID关联的，主要是业务实体之间关联
     * 2.编码字段，表明主从表之间是通过编码关联的，主要应用场景有如数据字典的维护
     */
    String tableName = (String) fieldInfo.get("tableName");
    fieldInfo.remove("tableName");

    int id = (Integer) fieldInfo.get("SEQ_ID");
    // System.out.println("seqId >>>>> "+id +fieldInfo);
    int length = fieldInfo.size();
    fieldInfo.remove("SEQ_ID");

    StringBuffer sql = new StringBuffer("update ").append(tableName).append(
        " set ");
    Object[] params = new Object[length];
    int i = 0;
    String key = null;
    Object value = null;
    Set set = fieldInfo.keySet();
    Object[] keys = set.toArray();
    for (int j = 0; j < keys.length; j++) {
      key = (String) keys[j];
      value = fieldInfo.get(key);
      // 判断是否为list类型，如果是list类型则为从表信息
      if (value != null && List.class.isAssignableFrom(value.getClass())) {
        continue;
      }

      sql.append(key).append(" = ").append(" ? ");
      params[i] = value;
      // System.out.println(value+" >>>> "+i);
      i++;
      if (j < keys.length - 1) {
        sql.append(" , ");
      }

    }

    sql.append(" where SEQ_ID = ? ");
    params[i] = id;
    try {
      pstmt = conn.prepareStatement(sql.toString());
      T9SQLParamHepler.javaParam2SQLParam(params, pstmt);
      pstmt.executeUpdate();
      conn.commit();
    } catch (SQLException e) {
      try {
        conn.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
        throw e1;
      }
      e.printStackTrace();
      throw e;
    } finally {
      try {
        pstmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * 2.0版 组织SQL的delete语句
   * 
   * @param fieldInfo
   * @param sqls
   * @throws Exception
   */
  public static void javaObject2DeleteSQL(Map<String, Object> fieldInfo,
      Connection conn, PreparedStatement pstmt) throws Exception {
    // 业务代码
    String tableName = (String) fieldInfo.get("tableName");
    fieldInfo.remove("tableName");
    int id = (Integer) fieldInfo.get("SEQ_ID");
    fieldInfo.remove("SEQ_ID");
    StringBuffer sql = new StringBuffer("delete from  ").append(tableName)
        .append(" where SEQ_ID = ? ");
    try {
      pstmt = conn.prepareStatement(sql.toString());
      pstmt.setInt(1, id);
      pstmt.executeUpdate();
      conn.commit();
    } catch (Exception e) {
      conn.rollback();
      e.printStackTrace();
      throw e;
    } finally {
      try {
        pstmt.close();
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }

  }

  /**
   * 2.0版 组织SQL的select语句
   * 
   * @param fieldInfo
   * @param sqls
   * @throws Exception
   */
  public static PreparedStatement javaObject2QuerySQL(
      Map<String, Object> fieldInfo, Connection conn, Map<String, Object> filter)
      throws Exception {
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String tableName = (String) fieldInfo.get("tableName");
    fieldInfo.remove("tableName");
    StringBuffer sql = new StringBuffer("select ");
    StringBuffer where = new StringBuffer("where ");
    System.out.println("filter >>> " + filter);
    int length = filter.size();
    Object[] params = new Object[length];
    int i = 0;
    Iterator iter = fieldInfo.keySet().iterator();
    while (iter.hasNext()) {
      String key = (String) iter.next();
      Object value = fieldInfo.get(key);
      // System.out.println("key : " + key + " value : " + value);
      // 判断是否为list类型，如果是list类型则为表信息
      if (value != null && List.class.isAssignableFrom(value.getClass())) {
        List clsInfo = (List) value;
        Iterator clsInfoIter = clsInfo.iterator();
        while (clsInfoIter.hasNext()) {
          String val = (String) clsInfoIter.next();
          sql.append(val);
          if (clsInfoIter.hasNext()) {
            sql.append(" , ");
          }
        }
        continue;
      }
    }

    iter = filter.keySet().iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      String subKey = (String) o;
      Object va = filter.get(o);
      where.append(subKey).append(" = ").append(" ? ");
      params[i] = va;
      i++;
    }
    System.out.println(" >>> " + i);
    sql.append(" from ").append(tableName);
    sql.append(" ").append(where);
    sql.append(" ORDER BY SEQ_ID ");
    System.out.println("sql >> " + sql.toString());
    try {
      pstmt = conn.prepareStatement(sql.toString());
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    T9SQLParamHepler.javaParam2SQLParam(params, pstmt);

    return pstmt;
  }
}
