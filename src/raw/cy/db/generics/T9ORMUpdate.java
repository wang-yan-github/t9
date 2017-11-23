package raw.cy.db.generics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class T9ORMUpdate {

  public static void doUpdate(Connection conn, Map<String, Object> fieldInfo)
      throws Exception {

    PreparedStatement pstmt = null;
    String tableName = (String) fieldInfo.get("tableName");
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    int id = (Integer) fieldInfo.get("SEQ_ID");

    if (fieldInfo.size() <= 0) {// 判断是否还有从表 ,如果fieldInfo的size<=0 则表示没有从表
      return;
    } else {
      Iterator iter = fieldInfo.keySet().iterator();
      while (iter.hasNext()) {
        String key = (String) iter.next();
        Object value = fieldInfo.get(key);
        // 判断value的类型是否为list
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
          // 删除所有从表
          List<Map<String, Object>> sublist = new ArrayList<Map<String, Object>>();
          List<Map<String, Object>> subs = (List<Map<String, Object>>) value;
          System.out.println("删除从表");
          for (Map<String, Object> subMap : subs) {
            String subTableName = (String) subMap.get("tableName");

            String fKFieldName = T9SQLExecuteHepler.getDsFKFieldName(conn,
                fkTableNo, subTableName, "FIELD_NAME");
            String realFieldNo = T9SQLExecuteHepler.getDsFKFieldName(conn,
                fkTableNo, subTableName, "FK_RELA_FIELD_NO");
            String realFieldName = T9SQLExecuteHepler.getDsFieldName(conn,
                realFieldNo, "FIELD_NAME");
            Object realValue = null;

            if ("SEQ_ID".equals(realFieldName.trim())) {

              deleteSub(conn, subTableName, fKFieldName, id);
            } else {
              realValue = T9SQLExecuteHepler.getFieldValue(conn, realFieldName,
                  tableName, id);
              deleteSub(conn, subTableName, fKFieldName, realValue);
            }
            System.out.println("for");
            /*
             * subMap.remove(fKFieldName); subMap.put(fKFieldName, realValue);
             * sublist.add(subMap);
             */
            System.out.println(subMap);
            System.out.println("删除字表");

          }
        }
        System.out.println("while");
      }
      // 跟新主表
      System.out.println("跟新主表");
      T9JObject2SQLHepler.javaObject2UpdateSQL(fieldInfo, conn, pstmt);
      conn.commit();
      // 插入所有字表
      System.out.println("开始插入所有字表");
      iter = fieldInfo.keySet().iterator();
      while (iter.hasNext()) {
        System.out.println("插入字表");
        String key = (String) iter.next();
        Object value = fieldInfo.get(key);
        // 判断value的类型是否为list
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
          List<Map<String, Object>> subs = (List<Map<String, Object>>) value;
          for (Map<String, Object> subMap : subs) {
            System.out.println(subMap);
            T9ORMInsert.doInsert(conn, subMap);
          }
        }
      }
    }
  }

  public static void deleteSub(Connection conn, String tableName,
      String fkFieldName, Object fkValue) throws Exception {
    String sql = "delete from " + tableName + " where " + fkFieldName + " = ? ";
    System.out.println("delete from " + tableName + " where " + fkFieldName
        + " =  " + fkValue);
    PreparedStatement ps = null;
    Object[] param = new Object[1];
    param[0] = fkValue;
    System.out
        .println("fkValue :  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "
            + fkValue);
    try {
      ps = conn.prepareStatement(sql);

      T9SQLParamHepler.javaParam2SQLParam(param, ps);
      System.out.println("开始executeUpdate 方法");
      int fag = ps.executeUpdate();
      System.out.println("结束executeUpdate 方法");
      System.out.println("fag : " + fag);
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
    System.out.println("end T9ORMUpdate.deleteSub method ");
  }
}
