package raw.cy.db.frm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.data.T9DsType;
import t9.core.util.db.T9StringFormat;

public class T9FRMUtil {

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
      String tableName, Map<String, Object> formInfo) throws Exception {
    System.out.println("=======================================");
    System.out.println("       do T9FRMUtil.form2TableInfo method");
    Map<String, Object> result = new HashMap<String, Object>();
    Set<String> keys = formInfo.keySet();
    // System.out.println("tableName >> "+tableName);
    tableName = T9StringFormat.format(tableName);
    result.put("tableName", tableName);
    for (String key : keys) {
      Object value = formInfo.get(key);
      // 判断从类信息
      if (List.class.isInstance(value)) {
        List l = (List) value;
        ArrayList newSub = new ArrayList();
        for (Object subMap : l) {
          Map m = form2TableInfo(conn, key, (Map<String, Object>) subMap);
          newSub.add(m);
        }
        result.put(key, newSub);
        continue;
      }
      key = T9StringFormat.format(key);
      String tableNo = getTableNo(conn, tableName);
      int typeInt = getTypeInt(conn, tableNo, key);
      // System.out.println(value+ " >>> "+typeInt);
      Object realValue = paramHelper(typeInt, (String) value);
      // System.out.println(realValue+ "  >>> "+typeInt);
      result.put(key, realValue);
    }
    System.out.println("========================================");
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
      List<Object> formInfo) throws Exception {
    Map<String, Object> result = new HashMap<String, Object>();
    String tableName = null;
    List<String> tableInfo = new ArrayList<String>();
    for (Object tabInfo : formInfo) {
      if (List.class.isInstance(tabInfo)) {
          Map m = form2TableInfo(conn, (List<Object>) tabInfo);
          System.out.println("sub map +++++++++++++ " + m + tabInfo);
          String subTable = (String) m.get("tableName");
          result.put(subTable, m);
          // subList.add(m);
        continue;
      }
      tableName = (String) tabInfo;
      System.out.println("tableName >> " + tableName);
      tableName = T9StringFormat.format(tableName);
      result.put("tableName", tableName);
      getTaleInfo(conn, tableInfo, tableName);
      System.out.println("tableInfo +++++++++ " + tableInfo);
      result.put(tableName, tableInfo);

    }
    return result;
  }

  /**
   * 
   * @param tableInfo
   * @return
   * @throws Exception
   */
  public static List<String> getTaleInfo(Connection conn,
      List<String> tableInfo, String tableName) throws Exception {
    String sql = "select FIELD_NAME from DS_FIELD where TABLE_NO in ( select TABLE_NO from DS_TABLE where TABLE_NAME = ?)";
    String sql2 = "select FIELD_NAME from DS_FIELD where TABLE_NO in ( select TABLE_NO from DS_TABLE where TABLE_NAME = '"
        + tableName + "')";
    System.out.println(sql2);
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, tableName);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      tableInfo.add(rs.getString(1));
    }
    return tableInfo;
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
      return Boolean.valueOf(value);
    } else if (T9DsType.isLongType(typeInt)) {
      return Long.valueOf(value);
    } else if (T9DsType.isCharType(typeInt)) {
      return String.valueOf(value);
    } else if (T9DsType.isDateType(typeInt)) {
      return Date.valueOf(value);
    } else if (T9DsType.isDecimalType(typeInt)) {
      return Double.valueOf(value);
    } else if (T9DsType.isIntType(typeInt)) {
      return Integer.valueOf(value);
    } else {
      throw new Exception("数据库中包含不支持的自动映射数据类型：" + T9DsType.getTypeName(typeInt));
    }
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
    String sql = "select DATA_TYPE from DS_FIELD where FIELD_NO IN ( select FIELD_NO from DS_FIELD where FIELD_NAME = ? and TABLE_NO = ?)";
    PreparedStatement ps = conn.prepareStatement(sql);
    String sqlTest = "select DATA_TYPE from DS_FIELD where FIELD_NO IN ( select FIELD_NO from DS_FIELD where FIELD_NAME = '"
        + fieldName + "' and TABLE_NO = '" + tableNo + "')";
    System.out.println(sqlTest);
    ps.setString(1, fieldName);
    ps.setString(2, tableNo);
    ResultSet rs = ps.executeQuery();
    int typeInt = 0;
    while (rs.next()) {
      typeInt = rs.getInt(1);
      System.out.println(typeInt);
    }
    return typeInt;
  }

  /**
   * 1.0版
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
    String sqlTest = "select TABLE_NO from DS_TABLE where TABLE_NAME = '"
        + tableName + "' ";
    System.out.println(sqlTest);
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

  /**
   * 1.0版 frm
   * 
   * @param m
   * @param rs
   * @param rsmd
   * @return
   * @throws Exception
   */
  public static Map<String, Object> sQLParam2JavaParam(Map<String, Object> m,
      ResultSet rs, ResultSetMetaData rsmd) throws Exception {
    int index = 0;
    try {
      index = rsmd.getColumnCount();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      for (int i = 1; i <= index; i++) {

        // 遍历数据集的每一列，通过共同遵守的Pascal命名规则反射查找并执行对应
        // pojo 类的赋值(getter)方法以实现结果集到pojo泛型集合的自动映射

        // 取得第i列列名
        String fieldName = rsmd.getColumnName(i);
        // 通过命名规则处理第i列列名,取得 pojo 中对应字段的取值(setter)方法名
        // 转换set方法
        String javaName = T9StringFormat.unformat(fieldName);
        // 取得第i列的数据类型
        int dbType = rsmd.getColumnType(i);
        // 当前反射方法
        Method method = null;
        // 对应第i列的SQL数据类型人工映射到对应的Java数据类型，
        // 并反射执行该列的在 pojo 中对应属性的 setter 方法完成赋值
        if (dbType == Types.TINYINT) {
          m.put(javaName, rs.getByte(i));
        } else if (dbType == Types.SMALLINT) {
          m.put(javaName, rs.getShort(i));
        } else if (dbType == Types.INTEGER || dbType == Types.NUMERIC) {
          m.put(javaName, rs.getInt(i));
        } else if (dbType == Types.BIGINT) {
          m.put(javaName, rs.getLong(i));
        } else if (dbType == Types.FLOAT || dbType == Types.REAL) {
          m.put(javaName, rs.getFloat(i));
        } else if (dbType == Types.DOUBLE) {
          m.put(javaName, rs.getDouble(i));
        } else if (dbType == Types.DECIMAL) {
          m.put(javaName, rs.getBigDecimal(i));
        } else if (dbType == Types.BIT) {
          m.put(javaName, rs.getBoolean(i));
        } else if (dbType == Types.CHAR || dbType == Types.VARCHAR
            || dbType == Types.LONGVARCHAR || dbType == Types.CLOB) {
          m.put(javaName, rs.getString(i));
        } else if (dbType == Types.DATE) { // 继承于 java.util.Date 类
          m.put(javaName, rs.getDate(i));
        } else if (dbType == Types.TIME) { // 继承于 java.util.Date 类
          m.put(javaName, rs.getTime(i));
        } else if (dbType == Types.TIMESTAMP) { // 继承于 java.util.Date 类
          m.put(javaName, rs.getTimestamp(i));
        } else if (dbType == Types.BINARY || dbType == Types.VARBINARY
            || dbType == Types.LONGVARBINARY || dbType == Types.BLOB) {
          m.put(javaName, rs.getBytes(i));
        } else {
          throw new Exception("数据库中包含不支持的自动映射数据类型：" + dbType);
        }
      }
    } catch (InstantiationException ex) {
      throw new Exception("异常信息：指定的类对象无法被 Class 类中的 newInstance 方法实例化！\r\n"
          + ex.getMessage());
    } catch (NoSuchMethodException ex) {
      throw new Exception("异常信息：无法找到某一特定的方法！\r\n" + ex.getMessage());
    } catch (IllegalAccessException ex) {
      throw new Exception("异常信息：对象定义无法访问，无法反射性地创建一个实例！\r\n" + ex.getMessage());
    } catch (InvocationTargetException ex) {
      throw new Exception("异常信息：由调用方法或构造方法所抛出异常的经过检查的异常！\r\n" + ex.getMessage());
    } catch (SecurityException ex) {
      throw new Exception("异常信息：安全管理器检测到安全侵犯！\r\n" + ex.getMessage());
    } catch (IllegalArgumentException ex) {
      throw new Exception("异常信息：向方法传递了一个不合法或不正确的参数！\r\n" + ex.getMessage());
    } catch (SQLException ex) {
      throw new Exception("异常信息：获取数据库连接对象错误！\r\n" + ex.getMessage());
    } catch (Exception ex) {
      throw new Exception("异常信息：程序兼容问题！\r\n" + ex.getMessage());
    }
    // 返回结果
    return m;
  }
}