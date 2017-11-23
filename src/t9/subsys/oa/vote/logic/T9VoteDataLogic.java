package t9.subsys.oa.vote.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vote.data.T9VoteData;

public class T9VoteDataLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.vote.logic.T9VoteDataLogic");
  /**
   * 新建
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static int addData(Connection dbConn,T9VoteData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, data);
    return 0;
  }
  /**
   * 编辑 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateData(Connection dbConn,T9VoteData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, data);
  }
  /**
   * 删除BySeqId
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delDataById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9VoteData.class, Integer.parseInt(seqId));
  }
  /**
   * 删除ByItemIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delDataByItemIds(Connection dbConn,String itemIds,String type) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "delete from VOTE_DATA where ITEM_ID in(" + itemIds + ")";
   if(!T9Utility.isNullorEmpty(type)){
     if(type.equals("0")){
       sql = sql + " and FIELD_NAME = '0'";
     }else{
       sql = sql + " and FIELD_NAME <> '0'";
     }
   }
   try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
   }catch(Exception ex) {
     throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
  }
  /**
   * 全部删除
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delAllData(Connection dbConn) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "delete from VOTE_DATA";
   try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
   }catch(Exception ex) {
     throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
  }
  /**
   * 查询BySeqId
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static T9VoteData selectDataById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9VoteData data = (T9VoteData) orm.loadObjSingle(dbConn, T9VoteData.class, Integer.parseInt(seqId));
    return data;
  }
  /**
   * 删除ByItemIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static List<T9VoteData> selectDataByItemId(Connection dbConn,String itemId,String fieldName) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "select * from VOTE_DATA where ITEM_ID = " + itemId;

   List<T9VoteData> dataList = new ArrayList<T9VoteData>();
   if(T9Utility.isInteger(itemId)){
     sql = sql + " and FIELD_NAME = '"+fieldName+"'";
   }
   try {
     stmt = dbConn.createStatement();
     rs = stmt.executeQuery(sql);
     while(rs.next()){
       T9VoteData data =  new T9VoteData();
       data.setSeqId(rs.getInt("seq_id"));
       data.setFieldData(rs.getString("field_data"));
       data.setFieldName(rs.getString("field_name"));
       data.setItemId(rs.getInt("item_id"));
       dataList.add(data);
     }
   }catch(Exception ex) {
     throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
   return dataList;
  }
}
