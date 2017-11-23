package t9.subsys.oa.examManage.logic;
import java.sql.Connection;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamQuizSet;

public class T9ExamQuizSetLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.examManage.logic.T9ExamQuizSetLogic.java");

  /**
   * 新建题库
   * @param dbConn
   * @param quiz
   * @throws Exception
   */
  public void addBank(Connection dbConn, T9ExamQuizSet quiz) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, quiz);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 获取题库列表--cc
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public String getExamQuizSetList(Connection dbConn, Map request) throws Exception {
    String sql = "select " 
              + " SEQ_ID" 
              + ", ROOM_CODE" 
              + ", ROOM_NAME" 
              + ", ROOM_DESC" 
              + " from EXAM_QUIZ_SET order by ROOM_CODE";
      
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
  
  /**
   * 删除题库--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9ExamQuizSet.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 获取题库详情(修改题库)--cc
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ExamQuizSet getExamQuizSetDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9ExamQuizSet) orm.loadObjSingle(conn, T9ExamQuizSet.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 修改题库--cc
   * @param conn
   * @param record
   * @throws Exception
   */
  public void updateExamQuizSet(Connection conn, T9ExamQuizSet record) throws Exception {
    try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, record);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }
}
