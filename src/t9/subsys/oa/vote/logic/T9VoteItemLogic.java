package t9.subsys.oa.vote.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.vote.data.T9VoteItem;

public class T9VoteItemLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.vote.logic.T9VoteItemLogic");
  /**
   * 新增
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static int addItem(Connection dbConn,T9VoteItem item) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, item);
    return 0;
  }
  /**
   * 编辑 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateItem(Connection dbConn,T9VoteItem item) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, item);
  }
  /**
   * 编辑 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateItem(Connection dbConn,String seqId,String itemName) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String sql = "update  VOTE_ITEM set ITEM_NAME = ? where SEQ_ID = " + seqId ;
    try {
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, itemName);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
  /**
   * 编辑 更新投票人和票数
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateItemUserId(Connection dbConn,int seqId,String anonymity,String voteUser) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String sql = "";
    if(!T9Utility.isNullorEmpty(anonymity)&& anonymity.equals("1")){
      sql = "update VOTE_ITEM set VOTE_COUNT=VOTE_COUNT+1  where SEQ_ID = " + seqId ;
    }else{
      sql = "update VOTE_ITEM set VOTE_COUNT=VOTE_COUNT+1, VOTE_USER = '"+voteUser+"' where SEQ_ID = " + seqId ;
    }
   try {
      pstmt = dbConn.prepareStatement(sql);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
  /**
   * 删除By seqId
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delItemById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9VoteItem.class, Integer.parseInt(seqId));
  }
  /**
   * 删除ByVoteIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delItemByVoteIds(Connection dbConn,String voteIds) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "delete from VOTE_ITEM where VOTE_ID in(" + voteIds + ")";
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
   * 删除ByVoteIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void delAllItem(Connection dbConn) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "delete from VOTE_ITEM ";
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
   * 更新itemByVoteIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateItemByVoteIds(Connection dbConn,String voteIds) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "update  VOTE_ITEM set VOTE_COUNT=0, VOTE_USER='' where vote_id in (" + voteIds + ")";
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
   * 查询By seqId
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static T9VoteItem selectItemById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9VoteItem item = (T9VoteItem) orm.loadObjSingle(dbConn, T9VoteItem.class, Integer.parseInt(seqId));
    return item;
  }
  /**
   * 按条件查询
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static List<T9VoteItem> selectItem(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9VoteItem>  itemList = new ArrayList<T9VoteItem>();
    itemList = orm.loadListSingle(dbConn, T9VoteItem.class, str);
    return itemList;
  }
  /**
   * 查询投票数量
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static String getCount(Connection dbConn,String voteId) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String totalCount = "0";
   String maxCount = "0";
   String sql = "select sum(VOTE_COUNT),max(VOTE_COUNT) from VOTE_ITEM where VOTE_ID = " + voteId;
   try {
     stmt = dbConn.createStatement();
     rs = stmt.executeQuery(sql);
     if(rs.next()){
       if(!T9Utility.isNullorEmpty(rs.getString(1))){
         totalCount = rs.getString(1);
         maxCount = rs.getString(2);
       }
     }
   }catch(Exception ex) {
     throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
   return totalCount + "," + maxCount ;
  }
}
