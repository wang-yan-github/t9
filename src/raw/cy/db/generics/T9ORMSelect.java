package raw.cy.db.generics;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import raw.cy.db.T9StringFormat;
import t9.core.util.db.T9DBUtility;

public class T9ORMSelect {

  /**
   *doselect
   * 
   * @param cls
   * @param conn
   * @param fieldInfo
   * @param fielter
   * @return
   * @throws Exception
   */
  public static Object doSelect(Class cls, Connection conn,
      Map<String, Object> fieldInfo, Map<String, Object> filter)
      throws Exception {
    String tableName = (String) fieldInfo.get("tableName");
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    System.out.println("tableName >>>>>>>>>>>> " + tableName);
    Object pojo = null;
    PreparedStatement pstmt = null;
    ResultSetMetaData rsmd = null;
    if (fieldInfo.size() <= 0 && pojo != null) {// 判断是否还有从表 ,如果fieldInfo的size<=0
      // 则表示没有从表
      return null;
    } else {
      ResultSet rs = null;
      try {
        pstmt = T9JObject2SQLHepler
            .javaObject2QuerySQL(fieldInfo, conn, filter);
        rs = pstmt.executeQuery();
        rsmd = pstmt.getMetaData();
        while (rs.next()) {
          pojo = T9SQLParamHepler.sQLParam2JavaParam(cls, rs, rsmd);
        }
        System.out.println(pojo);
      } catch (SQLException e) {
        throw e;
      } finally {
        T9DBUtility.close(pstmt, rs, null);
      }
      Iterator iter = fieldInfo.keySet().iterator();
      try {
        while (iter.hasNext()) {
          String key = (String) iter.next();
          Object value = fieldInfo.get(key);
          // 判断value的类型是否为Map,则表示存在从表
          if (value != null && Map.class.isAssignableFrom(value.getClass())) {
            // 得到从表的信息
            Map<String, Object> subMap = (Map<String, Object>) value;
            // 得到从表的表名称
            String subTableName = (String) subMap.get("tableName");
            // 定义一个List 用来存储从表的数据
            List<Object> pojoList = new ArrayList<Object>();
            // 得到从表的 类对象Class
            Class subClass = (Class) subMap.get("Class");
            // 定义一个查询条件的哈希表
            Map<String, Object> subMapFilter = new HashMap<String, Object>();
            // 得到查询条件
            // 1.得到外键关联的字段名
            String fKFieldName = T9SQLExecuteHepler.getDsFKFieldName(conn,
                fkTableNo, subTableName, "FIELD_NAME");
            // 2.得到对应主表的字段编码
            System.out.println(fKFieldName);
            String realFieldNo = T9SQLExecuteHepler.getDsFKFieldName(conn,
                fkTableNo, subTableName, "FK_RELA_FIELD_NO");
            System.out.println(realFieldNo);
            // 3.得到对应主表的字段名
            String realFieldName = T9SQLExecuteHepler.getDsFieldName(conn,
                realFieldNo, "FIELD_NAME");
            // 4.得到外键的值 通过主表的seq_id进行查询
            System.out.println("realFieldName :" + realFieldName);
            if (realFieldName == null) {
              continue;
            }
            String methodName = T9StringFormat.unformat(realFieldName);
            methodName = "get" + methodName.substring(0, 1).toUpperCase()
                + methodName.substring(1);
            Method m = cls.getDeclaredMethod(methodName);
            Object realValue = m.invoke(pojo); // 组装查询条件
            subMapFilter.put(fKFieldName, realValue);

            List l = doSelectList(subClass, conn, subMap, subMapFilter);
            String setMethodName = T9StringFormat.unformat(key);
            setMethodName = "set" + setMethodName.substring(0, 1).toUpperCase()
                + setMethodName.substring(1);

            Method ml = cls.getDeclaredMethod(setMethodName, l.getClass());
            ml.invoke(pojo, l);
            continue;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }

    }

    return pojo;
  }

  /**
   * 加载list
   * 
   * @param cls
   * @param conn
   * @param fieldInfo
   * @param filter
   * @return
   * @throws Exception
   */
  public static List doSelectList(Class cls, Connection conn,
      Map<String, Object> fieldInfo, Map<String, Object> filter)
      throws Exception {
    Object pojo = null;
    PreparedStatement pstmt = null;
    ResultSetMetaData rsmd = null;
    List<Object> objectList = new ArrayList<Object>();
    String tableName = (String) fieldInfo.get("tableName");
    System.out.println("tableName >>>>>>>>>>>> " + tableName);
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    if (fieldInfo.size() <= 0 && pojo != null) {// 判断是否还有从表 ,如果fieldInfo的size<=0
      // 则表示没有从表
      return null;
    } else {
      try {
        pstmt = T9JObject2SQLHepler
            .javaObject2QuerySQL(fieldInfo, conn, filter);
        ResultSet rs = pstmt.executeQuery();
        rsmd = pstmt.getMetaData();
        try {
          rs = pstmt.executeQuery();
        } catch (SQLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }

        while (rs.next()) {
          pojo = T9SQLParamHepler.sQLParam2JavaParam(cls, rs, rsmd);
          // 假定有sql_Id
          Iterator iter = fieldInfo.keySet().iterator();
          while (iter.hasNext()) {
            String key = (String) iter.next();
            Object value = fieldInfo.get(key);
            // 判断value的类型是否为Map,则表示存在从表
            if (value != null && Map.class.isAssignableFrom(value.getClass())) {
              // 得到从表的信息
              Map<String, Object> subMap = (Map<String, Object>) value;
              // 得到从表的表名称
              String subTableName = (String) subMap.get("tableName");
              // 定义一个List 用来存储从表的数据
              List<Object> pojoList = new ArrayList<Object>();
              // 得到从表的 类对象Class
              Class subClass = (Class) subMap.get("Class");
              // 定义一个查询条件的哈希表
              Map<String, Object> subMapFilter = new HashMap<String, Object>();

              // 得到查询条件
              // 1.得到外键关联的字段名
              String fKFieldName = T9SQLExecuteHepler.getDsFKFieldName(conn,
                  fkTableNo, subTableName, "FIELD_NAME");
              // 2.得到对应主表的字段编码
              String realFieldNo = T9SQLExecuteHepler.getDsFKFieldName(conn,
                  fkTableNo, subTableName, "FK_RELA_FIELD_NO");
              // 3.得到对应主表的字段名
              String realFieldName = T9SQLExecuteHepler.getDsFieldName(conn,
                  realFieldNo, "FIELD_NAME");
              // 4.得到外键的值 通过主表的seq_id进行查询
              if (realFieldName == null) {
                continue;
              }
              String methodName = T9StringFormat.unformat(realFieldName);
              methodName = "get" + methodName.substring(0, 1).toUpperCase()
                  + methodName.substring(1);
              Method m = cls.getDeclaredMethod(methodName);
              Object realValue = m.invoke(pojo);

              // 组装查询条件
              subMapFilter.put(fKFieldName, realValue);

              // 得到从表的结果集
              doSelectList(subClass, conn, subMap, subMapFilter);
              // 组装
            }
          }
          objectList.add(pojo);
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
    return objectList;
  }
}
