package raw.cy.db.frm;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raw.cy.db.generics.T9ORMDelete;
import raw.cy.db.generics.T9ORMInsert;
import raw.cy.db.generics.T9ORMUpdate;

public class T9FRM {

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
  public void save(Connection dbConn, String tableName, Map formInfo)
      throws Exception {
    Map<String, Object> m = T9FRMUtil.form2TableInfo(dbConn, tableName,
        formInfo);
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
  public void update(Connection dbConn, String tableName, Map formInfo)
      throws Exception {
    Map<String, Object> m = T9FRMUtil.form2TableInfo(dbConn, tableName,
        formInfo);
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
  public void delete(Connection conn, String tableName, Map formInfo)
      throws Exception {
    Map<String, Object> m = T9FRMUtil.form2TableInfo(conn, tableName, formInfo);
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
  public Map loadData(Connection dbConn, List<Object> formInfo, Map filters)
      throws Exception {

    Map<String, Object> fieldInfo = T9FRMUtil.form2TableInfo(dbConn, formInfo);
    Map<String, Object> m = new HashMap<String, Object>();
    m = T9FRMSelect.doSelect(dbConn, fieldInfo, filters);
    System.out.println("+++++++++++++++++++++++++" + m);
    return m;
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
  /*
   * public Map loadDataList(Connection dbConn,List<Object> formInfo,Map
   * filters) throws Exception { Map<String, Object> fieldInfo =
   * T9FRMUtil.form2TableInfo(dbConn, formInfo) ;
   * 
   * Map<String, Object> m = new HashMap<String, Object>();
   * 
   * Map l = T9FRMSelect.doSelectList(dbConn, fieldInfo, filters);
   * System.out.println("fieldInfo>>>> "+fieldInfo); return l; }
   */

}
