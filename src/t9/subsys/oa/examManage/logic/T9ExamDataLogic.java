package t9.subsys.oa.examManage.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.examManage.data.T9ExamData;
import t9.subsys.oa.examManage.data.T9ExamPaper;
import t9.subsys.oa.examManage.data.T9ExamQuiz;

public class T9ExamDataLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.examManage.logic.T9ExamDataLogic");
  /**
   * 新建
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static int addData(Connection dbConn,T9ExamData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, data);
    return getMaSeqId(dbConn, "EXAM_DATA");
  }
  public static int getMaSeqId(Connection dbConn,String tableName)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    int maxSeqId = 0;
    String sql = "select max(SEQ_ID) as SEQ_ID from " + tableName;
    try{
     stmt = dbConn.createStatement();
     rs = stmt.executeQuery(sql);
     if(rs.next()){
       maxSeqId = rs.getInt("SEQ_ID");
     }
      
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return maxSeqId;
  }
  /**
   * 编辑 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateData(Connection dbConn,T9ExamData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, data);
  }
  /**
   *  查询
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static List<T9ExamData> selectData(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9ExamData> dataList = new ArrayList<T9ExamData>();
    dataList = orm.loadListSingle(dbConn, T9ExamData.class, str);
    return dataList;
  }
  /**
   *  查询ById
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static T9ExamData selectDataById(Connection dbConn,String seqId)  throws Exception {
    T9ORM orm = new T9ORM();
    T9ExamData data = (T9ExamData) orm.loadObjSingle(dbConn, T9ExamData.class, Integer.parseInt(seqId));
    return data;
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
    orm.deleteSingle(dbConn, T9ExamData.class, Integer.parseInt(seqId));
  }
  /**
   * 删除ByItemIds
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateDate(Connection dbConn,String seqId,String score,String answer) throws Exception {
   Statement stmt = null;
   ResultSet rs = null;
   String sql = "update EXAM_DATA set SCORE = '" + score + "' ,ANSWER = '" + answer + "' ,EXAMED ='1'  where seq_id = " + seqId ;
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
   * 试卷管理表BySeqId
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static T9ExamPaper getParerBySeqId(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9ExamPaper paper =(T9ExamPaper)orm.loadObjSingle(dbConn, T9ExamPaper.class, seqId);
    return paper;
  }
  /**
   * 得到试题列表
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static List<T9ExamQuiz> getQuiz(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9ExamQuiz> quizList =(List<T9ExamQuiz>)orm.loadListSingle(dbConn, T9ExamQuiz.class, str);
    return quizList;
  }
  public int selectQuizCount(Connection dbConn ,String seqIds) throws Exception {
    String sql = "select count(*) from EXAM_QUIZ where SEQ_ID in (" + seqIds + ")";
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

  public List selectQuizSeqId(Connection dbConn,String seqIds) throws Exception {
    String sql = "select seq_id from EXAM_QUIZ where SEQ_ID in (" + seqIds + ")";
    Statement st = dbConn.createStatement();
    ResultSet rs = st.executeQuery(sql);
    List seqIdList = new ArrayList();
    try {
      while (rs.next()) {
        seqIdList.add(rs.getString(1));
      }
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    } finally {
      rs.close();
      st.close();
    }
    return seqIdList;
  }
}
