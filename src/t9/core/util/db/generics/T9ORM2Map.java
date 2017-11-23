package t9.core.util.db.generics;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.db.generics.T9ORMDelete;
import t9.core.util.db.generics.T9ORMInsert;
import t9.core.util.db.generics.T9ORMSelect;
import t9.core.util.db.generics.T9ORMUpdate;

public class T9ORM2Map {

  /**
   * 将Map提供的数据添加到数据库中 思路：将Map<String,String> formInfo 转换成 Map<String,Object>
   * tableInfo
   * 
   * @param dbConn
   * @param tableName
   * @param formInfo
   *          Map<String,String>
   * @throws Exception
   */
  public static void save(Connection dbConn, String tableName, Map formInfo,boolean isCascade)
      throws Exception {
    Map<String, Object> m = T9SQLExecuteHepler.form2TableInfo(dbConn, tableName,
        formInfo,isCascade);
    T9ORMInsert.doInsert(dbConn, m);
  }

  /**
   * 根据Map提供的数据更新指定的数据库表
   * 
   * @param dbConn
   * @param tableName
   * @param formInfo
   * @throws Exception
   */
  public static void update(Connection dbConn, String tableName, Map formInfo,boolean isCascade)
      throws Exception {
    Map<String, Object> m = T9SQLExecuteHepler.form2TableInfo(dbConn, tableName,
        formInfo,isCascade);
    T9ORMUpdate.doUpdate(dbConn, m);
  }

  /**
   * 根据seqId删除数据库信息
   * 
   * @param conn
   * @param tableName
   * @param seqId
   * @throws Exception
   */
  public static void delete(Connection conn, String tableName, Map formInfo,boolean isCascade)
      throws Exception {
    Map<String, Object> m = T9SQLExecuteHepler.form2TableInfo(conn, tableName, formInfo,isCascade);
    //System.out.println("==============" + m );
    T9ORMDelete.doDelete(conn, m);
  }

  /**
   * 加载数据表中的信息
   * 
   * @param dbConn
   * @param tableName
   * @param tableName
   * @return
   * @throws Exception
   */
  public static Map loadData(Connection dbConn, List<Object> formInfo, Map filters,boolean isCascade)
      throws Exception {

    Map<String, Object> fieldInfo = T9SQLExecuteHepler.form2TableInfo(dbConn, formInfo,isCascade);
    Map<String, Object> m = new HashMap<String, Object>();
    m = T9ORMSelect.doSelect(dbConn, fieldInfo, filters);
    return m;
  }

  public static Map loadData(Connection dbConn, List<Object> formInfo, String[] filters,boolean isCascade)
  throws Exception {
    Map<String, Object> fieldInfo = T9SQLExecuteHepler.form2TableInfo(dbConn, formInfo,isCascade);
    Map<String, Object> m = new HashMap<String, Object>();
    m = T9ORMSelect.doSelect(dbConn, fieldInfo, filters);
    return m;
  }
}
