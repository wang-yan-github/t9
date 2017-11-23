package raw.cy.db.frm;

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

import t9.core.util.db.T9StringFormat;
import t9.core.util.db.generics.T9JObject2SQLHepler;
import t9.core.util.db.generics.T9SQLExecuteHepler;

public class T9FRMSelect {

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
  public static Map<String, Object> doSelect(Connection conn,
      Map<String, Object> fieldInfo, Map<String, Object> filter)
      throws Exception {
    Map<String, Object> m = new HashMap<String, Object>();
    List<Object> objectList = new ArrayList<Object>();
    String tableName = (String) fieldInfo.get("tableName");
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    System.out.println("tableName >>>>>>>>>>>> " + tableName);
    Map<String, Object> res = new HashMap<String, Object>();
    PreparedStatement pstmt = null;
    ResultSetMetaData rsmd = null;
    if (fieldInfo.size() <= 0) {// 判断是否还有从表 ,如果fieldInfo的size<=0
      // 则表示没有从表
      return null;
    } else {
      System.out.println("MAP value :" + m);
      pstmt = T9JObject2SQLHepler.javaObject2QuerySQL(fieldInfo, conn, filter);
      ResultSet rs = pstmt.executeQuery();
      rsmd = pstmt.getMetaData();
      while (rs.next()) {
        T9FRMUtil.sQLParam2JavaParam(m, rs, rsmd);

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
              // 定义一个查询条件的哈希表
              Map<String, Object> subMapFilter = new HashMap<String, Object>();
              // 得到查询条件
              // 1. 得到外键关联的字段名
              String fKFieldName = T9SQLExecuteHepler.getDsFKFieldName(conn, fkTableNo, subTableName, "FIELD_NAME");
              // 2. 得到对应主表的字段编码
              String realFieldNo = T9SQLExecuteHepler.getDsFKFieldName(conn,
                  fkTableNo, subTableName, "FK_RELA_FIELD_NO");
              // 3.得到对应主表的字段名
              String realFieldName = T9SQLExecuteHepler.getDsFieldName(conn,
                  realFieldNo, "FIELD_NAME");
              // 4.得到外键的值 通过主表的seq_id进行查询
              if (realFieldName == null) {
                continue;
              }

              Object realValue = m.get(T9StringFormat.unformat(realFieldName)); // 组装查询条件
              subMapFilter.put(fKFieldName, realValue);

              List l = doSelectList(conn, subMap, subMapFilter);
              m.put(subTableName, l);
              continue;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          throw e;
        }
        objectList.add(m);
      }
    }

    res.put(tableName, objectList);
    System.out.println(" res +++++++++++++++++++++++++++" + res);
    return res;
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
  public static List doSelectList(Connection conn,
      Map<String, Object> fieldInfo, Map<String, Object> filter)
      throws Exception {
    Map<String, Object> m = null;
    PreparedStatement pstmt = null;
    ResultSetMetaData rsmd = null;
    List<Object> objectList = new ArrayList<Object>();
    String tableName = (String) fieldInfo.get("tableName");
    System.out.println("tableName >>>>>>>>>>>> " + tableName);
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    if (fieldInfo.size() <= 0) {// 判断是否还有从表 ,如果fieldInfo的size<=0
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
          m = new HashMap<String, Object>();
          T9FRMUtil.sQLParam2JavaParam(m, rs, rsmd);

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
              System.out.println("m >>>> " + m);
              Object realValue = m.get(T9StringFormat.unformat(realFieldName));

              // 组装查询条件
              subMapFilter.put(fKFieldName, realValue);

              // 得到从表的结果集
              doSelectList(conn, subMap, subMapFilter);
              // 组装
            }
          }
          objectList.add(m);
        }
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
    return objectList;
  }
}
