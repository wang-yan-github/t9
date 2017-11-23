package t9.subsys.oa.hr.score.logic;

import java.sql.Connection;

import t9.core.util.db.T9ORM;
import t9.subsys.oa.hr.score.data.T9ScoreItem;

public class T9ScoreItemLogic {
  
  /**
   * 增加考核指标集明细--cc
   * @param dbConn
   * @param scoreItem
   * @throws Exception
   */
  public void addScoreFlow(Connection dbConn, T9ScoreItem scoreItem) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, scoreItem);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 删除一条记录--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteItem(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9ScoreItem.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 修改考核指标集明细--cc
   * @param conn
   * @param scoreItem
   * @throws Exception
   */
  public void updateScoreItem(Connection conn, T9ScoreItem scoreItem) throws Exception {
    try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, scoreItem);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }
  
  public T9ScoreItem getItemBId(Connection dbConn,int seqId)throws Exception {
    T9ORM orm = new T9ORM();
    T9ScoreItem item  = (T9ScoreItem) orm.loadObjSingle(dbConn, T9ScoreItem.class, seqId);
    return item;
  }
  
}
