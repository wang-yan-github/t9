package raw.cy.db.generics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class T9ORMInsert {

  /**
   * 2.0版 执行sql语句
   * 
   * @param conn
   *          数据库连接
   * @param fieldInfo
   *          Object的属性字段---值 map 通过 T9ORMReflect的getFieldInfo()得到
   * @throws Exception
   */
  public static void doInsert(Connection conn, Map<String, Object> fieldInfo)
      throws Exception {

    //System.out.println("=======================================");
    //System.out.println("       do T9ORMInsert.doInsert method");
    PreparedStatement pstmt = null;
    String tableName = (String) fieldInfo.get("tableName");
    String fkTableNo = T9SQLExecuteHepler.getTableNo(conn, tableName);
    //System.out.println("tableName >>>>>>>>>>>> " + tableName);

    T9JObject2SQLHepler.javaObject2InsertSQL(fieldInfo, conn, pstmt);

    if (fieldInfo.size() <= 0) {// 判断是否还有从表 ,如果fieldInfo的size<=0 则表示没有从表
      return;
    } else {
      Iterator iter = fieldInfo.keySet().iterator();
      while (iter.hasNext()) {
        String key = (String) iter.next();
        Object value = fieldInfo.get(key);
        // 判断value的类型是否为list
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
          List<Map<String, Object>> subs = (List<Map<String, Object>>) value;
          for (Map<String, Object> subMap : subs) {
            // 加入外键关联
            String subTableName = (String) subMap.get("tableName");
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
            System.out.println("fKFieldName :" + fKFieldName + " realFieldNo :"
                + realFieldNo + " fkTableNo :");
            if (realFieldName != null && "SEQ_ID".equals(realFieldName.trim())) {
              subMap.remove(fKFieldName);
              Object o = T9SQLExecuteHepler.getSeqIdValue(conn, tableName);
              subMap.put(fKFieldName, o);
            } else {
              subMap.remove(fKFieldName);
              Object o = fieldInfo.get(realFieldName);
              subMap.put(fKFieldName, o);
            }
            //System.out.println(fieldInfo);
           // System.out.println(subMap);
            doInsert(conn, subMap);
          }
        }
      }
    }
    //System.out.println("=======================================");
  }

}
