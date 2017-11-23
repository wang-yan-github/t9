package t9.subsys.oa.hr.score.logic;

import java.sql.Connection;
import java.util.Map;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreGroup;

public class T9ScoreGroupLogic {
  
  /**
   * 考核指标集管理列表--cc
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public String getScoreGroupList(Connection dbConn, Map request) throws Exception {
    String sql = "select " 
             + "  SEQ_ID" 
             + ", GROUP_FLAG" 
             + ", GROUP_NAME" 
             + ", GROUP_DESC" 
             + " from SCORE_GROUP order by SEQ_ID DESC";
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
  
  /**
   * 新建考核指标集--cc
   * @param dbConn
   * @param scoreGroup
   * @throws Exception
   */
  public void addScoreGroup(Connection dbConn, T9ScoreGroup scoreGroup) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, scoreGroup);
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
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9ScoreGroup.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 获取考核指标集管理一条记录 --cc
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ScoreGroup getScoreGroupDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9ScoreGroup) orm.loadObjSingle(conn, T9ScoreGroup.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }

  /**
   * 编辑考核指标集管理 --cc
   * @param conn
   * @param scoreGroup
   * @throws Exception
   */
  public void updateScoreGroup(Connection conn, T9ScoreGroup scoreGroup) throws Exception {
    try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, scoreGroup);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }
}
