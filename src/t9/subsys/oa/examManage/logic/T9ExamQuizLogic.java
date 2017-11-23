package t9.subsys.oa.examManage.logic;
import java.sql.Connection;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamQuiz;

public class T9ExamQuizLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.examManage.logic.T9ExamQuizSetLogic.java");

  /**
   * 新建试卷
   * @param dbConn
   * @param quiz
   * @throws Exception
   */
  public void addQuiz(Connection dbConn, T9ExamQuiz quiz) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, quiz);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  /**
   * 得到试卷BySeqId   ---syl
   * @param dbConn
   * @param quiz
   * @throws Exception
   */
  public T9ExamQuiz selectQuizById(Connection dbConn,int seqId) throws Exception {
    T9ExamQuiz quiz = null;
    try {
      T9ORM orm = new T9ORM();
      quiz = (T9ExamQuiz) orm.loadObjSingle(dbConn, T9ExamQuiz.class, seqId);
    } catch (Exception ex) {
      throw ex;
    }
    return quiz;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--lz
   * @return
   * @throws Exception 
   */
  public static String selectQuiz(Connection dbConn,Map request,String roomId) throws Exception {
    String sql = "select quiz.SEQ_ID,quiz.ROOM_ID,qset.ROOM_NAME,quiz.QUESTIONS_TYPE"
      + ",quiz.QUESTIONS_RANK,quiz.QUESTIONS,quiz.ANSWERS FROM EXAM_QUIZ quiz "
      + " left outer join EXAM_QUIZ_SET qset on qset.seq_id = quiz.ROOM_ID "
      + " WHERE 1=1 ";
    if (!roomId.equals("0")) {
      sql += " and quiz.ROOM_ID=" + roomId;
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 删除--lz
   * @return
   * @throws Exception 
   */
  public static void deleteQuiz(Connection dbConn,String roomId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn,T9ExamQuiz.class,Integer.parseInt(roomId));
  }
  /***
   * 修改--lz
   * @return
   * @throws Exception 
   */
  public static void updateQuiz(Connection dbConn,T9ExamQuiz quiz) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateComplex(dbConn, quiz);
  }
  /**
   * 查询--lz
   * 
   * @return
   * @throws Exception
   */
  public static T9ExamQuiz showQuiz(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9ExamQuiz quiz = (T9ExamQuiz)orm.loadObjComplex(dbConn,T9ExamQuiz.class,Integer.parseInt(seqId));
    return quiz;
  }
}
