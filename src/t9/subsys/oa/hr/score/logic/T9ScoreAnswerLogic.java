package t9.subsys.oa.hr.score.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreAnswer;

public class T9ScoreAnswerLogic {
  
  /**
   * 添加选择方式考核信息--cc
   * @param dbConn
   * @param scoreAnswer
   * @throws Exception
   */
  public void addScoreAnswer(Connection dbConn, T9ScoreAnswer scoreAnswer) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, scoreAnswer);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 获取选择方式考核指标集明细 列表--cc
   * @param dbConn
   * @param request
   * @param itemId
   * @return
   * @throws Exception
   */
  public String getScoreAnswerList(Connection dbConn, Map request, int itemId) throws Exception {
    String sql = "select " 
             + "  SEQ_ID" 
             + ", ITEM_NAME" 
             + " from SCORE_ANSWER where GROUP_ID = '" + itemId + "' order by SEQ_ID DESC";
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
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
      orm.deleteSingle(conn, T9ScoreAnswer.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 获取选择方式考核项目选项信息--cc
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ScoreAnswer getScoreAnswerDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9ScoreAnswer) orm.loadObjSingle(conn, T9ScoreAnswer.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 编辑选择方式考核项目选项信息--cc
   * @param conn
   * @param scoreGroup
   * @throws Exception
   */
  public void updateScoreAnswer(Connection conn, T9ScoreAnswer scoreAnswer) throws Exception {
    try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, scoreAnswer);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }
  /**
   * 根据考核任务Seq_Id得到 相关联的考核指标明细做分页
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public List<T9ScoreAnswer> getAnswerByGroupId(Connection dbConn,String[] str)throws Exception {
    T9ORM orm = new T9ORM();
    List<T9ScoreAnswer>  itemList = new ArrayList<T9ScoreAnswer>();
    itemList = orm.loadListSingle(dbConn, T9ScoreAnswer.class, str);
    return itemList;
  }
  /**
   * 根据考核任务Seq_Id得到 相关联的考核指标明细的总数
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int selectAnswerCount(Connection dbConn ,int groupId) throws Exception {
    String sql = "select count(*) from SCORE_ANSWER where GROUP_ID = " + groupId ;
    Statement st = dbConn.createStatement();
    ResultSet rs = st.executeQuery(sql);
    int count = 0;
    try {
      if (rs.next()) {
        if (!T9Utility.isNullorEmpty(rs.getString(1))) {
          count = rs.getInt(1);
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    } finally {
      rs.close();
      st.close();
    }
    return count;
  }
}
