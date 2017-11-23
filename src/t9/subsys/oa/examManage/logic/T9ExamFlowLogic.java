package t9.subsys.oa.examManage.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamData;
import t9.subsys.oa.examManage.data.T9ExamFlow;
import t9.subsys.oa.examManage.data.T9ExamPaper;

public class T9ExamFlowLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.examManage.logic.T9ExamFlowLogic");
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--lz
   * @return
   * @throws Exception 
   */
  public static String selectFlow(Connection dbConn,Map request,String dayTime) throws Exception {
    String sql = "select ex.SEQ_ID,ex.FLOW_TITLE,ex.PARTICIPANT,son.PAPER_TIMES"
      + ",ex.BEGIN_DATE,ex.END_DATE,ex.PAPER_ID,ex.FLOW_DESC,ex.FLOW_FLAG"
      + ",ex.SEND_TIME,ex.RANKMAN,ex.ANONYMITY from EXAM_FLOW ex "
      + " left outer join EXAM_PAPER son on son.seq_id = ex.PAPER_ID "
      + " WHERE 1=1 ";
    sql += " and " + T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), "<=");
    sql += " and (" + T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), ">")
    + "or ex.END_DATE is null)  order by ex.SEND_TIME desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--lz
   * @return
   * @throws Exception 
   */
  public static String selectList(Connection dbConn,Map request,T9ExamFlow flow, String beginDate1,String endDate1,String cd) throws Exception {
    String sql = "select ex.SEQ_ID,ex.FLOW_TITLE,ex.PARTICIPANT,son.PAPER_TIMES"
      + ",ex.BEGIN_DATE,ex.END_DATE,ex.PAPER_ID,ex.FLOW_DESC,ex.FLOW_FLAG"
      + ",ex.SEND_TIME,ex.RANKMAN,ex.ANONYMITY from EXAM_FLOW ex "
      + " left outer join EXAM_PAPER son on son.seq_id = ex.PAPER_ID "
      + " WHERE 1=1 ";
    if (!T9Utility.isNullorEmpty(flow.getFlowTitle())) {
      sql += " and ex.FLOW_TITLE like '%" + T9DBUtility.escapeLike(flow.getFlowTitle()) + "%' " + T9DBUtility.escapeLike();
    }
    if (flow.getPaperId() > 0) {
      sql += " and ex.PAPER_ID=" + flow.getPaperId();
    }
    if (!T9Utility.isNullorEmpty(flow.getParticipant())) {
      sql += " and " + T9DBUtility.findInSet(flow.getParticipant(),"ex.PARTICIPANT");
    }
    if (flow.getBeginDate() != null) {
      sql += " and " +  T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(flow.getBeginDate()), ">=");
    }
    if (!T9Utility.isNullorEmpty(beginDate1)) {
      sql += " and " +  T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(beginDate1)), "<=");
    }
    if (flow.getEndDate() != null) {
      sql += " and " +  T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(flow.getEndDate()), ">=");
    }
    if (!T9Utility.isNullorEmpty(endDate1)) {
      sql += " and " +  T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(endDate1)), "<=");
    }
    if (cd.equals("2")) {
      sql += " and " +  T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(new Date()), "<=") + " and  ex.END_DATE is not null ";
    }
    sql += " order by ex.SEND_TIME desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /**
   * 新建--lz
   * 
   * @return
   * @throws Exception
   */
  public static void add(Connection dbConn,T9ExamFlow flow) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, flow);
  }

  /**
   * 删除--lz
   * 
   * @return
   * @throws Exception
   */
  public static void deleteFlow(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn,T9ExamFlow.class,Integer.parseInt(seqId));
  }


  /**
   * 查询--lz
   * 
   * @return
   * @throws Exception
   */
  public static T9ExamFlow showFlow(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9ExamFlow flow = (T9ExamFlow)orm.loadObjComplex(dbConn,T9ExamFlow.class,Integer.parseInt(seqId));
    return flow;
  }
  /**
   * 修改--lz
   * 
   * @return
   * @throws Exception
   */
  public static void updateFlow(Connection dbConn,T9ExamFlow flow) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateComplex(dbConn, flow);
  }
  /**
   * 参加考试人员查询--lz
   * 
   * @return
   * @throws Exception
   */
  public static List<T9Person> showMan(Connection dbConn,String participant) throws Exception {
    String sql = "select son.SEQ_ID,son.USER_NAME as userName,dep.DEPT_NAME as deptName "
      + ",priv.PRIV_NAME as privName FROM PERSON son "
      + " left outer join DEPARTMENT dep on dep.SEQ_ID = son.DEPT_ID "
      + " left outer join USER_PRIV priv on priv.SEQ_ID = son.USER_PRIV "
      + " WHERE son.SEQ_ID in (" + participant +")";
    List<T9Person> list = new ArrayList<T9Person>();
    T9Person  person = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        person = new T9Person();
        person.setUserName(rs.getString("userName"));
        person.setUserId(rs.getString("deptName"));
        person.setUserPriv(rs.getString("privName"));
        list.add(person);
      }
    }catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs,log);
    }
    return list;
  }


  /**
   * 参加考试人员查询--lz
   * 
   * @return
   * @throws Exception
   */
  public static T9Person showPerson(Connection dbConn,String participant) throws Exception {
    String sql = "select son.SEQ_ID as seqId,son.USER_NAME as userName,dep.DEPT_NAME as deptName "
      + ",priv.PRIV_NAME as privName FROM PERSON son "
      + " left outer join DEPARTMENT dep on dep.SEQ_ID = son.DEPT_ID "
      + " left outer join USER_PRIV priv on priv.SEQ_ID = son.USER_PRIV "
      + " WHERE son.SEQ_ID =" + participant;
    T9Person  person = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        person = new T9Person();
        person.setSeqId(rs.getInt("seqId"));
        person.setUserName(rs.getString("userName"));
        person.setUserId(rs.getString("deptName"));
        person.setUserPriv(rs.getString("privName"));
      }
    }catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs,log);
    }
    return person;
  }
  /**
   * 立即终止--lz
   * 
   * @return
   * @throws Exception
   */
  public static void updateStatus(Connection dbConn,String seqId,String endTime) throws Exception {
    String sql = " update exam_flow set end_date=? where seq_id=? ";
    PreparedStatement ps = null;
    ps = dbConn.prepareStatement(sql);
    try {
      ps.setDate(1,T9Utility.parseSqlDate(endTime));
      ps.setInt(2,Integer.parseInt(seqId));
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps,null,log);
    }
  }
  /***
   * 数据导出--lz
   * @return
   * @throws Exception 
   * @throws Exception 
   */
  public static ArrayList<T9DbRecord> getDbRecord(Connection dbConn,List<T9ExamData> list,int paperGrade,int count) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    T9DbRecord dbrec = null;
    T9ExamData examDate = new T9ExamData();
    if (list.size() <= 0) {
      dbrec = new T9DbRecord();
      dbrec.addField("部门","");
      dbrec.addField("人员","");
      dbrec.addField("角色","");
      dbrec.addField("分数","");
      dbL.add(dbrec);
    } else {
      for (int i = 0; i < list.size(); i ++) {
        examDate = list.get(i);
        int num = 0;
        if (!T9Utility.isNullorEmpty(examDate.getParticipant())) {
          T9Person person = showPerson(dbConn,examDate.getParticipant());
          dbrec = new T9DbRecord();
          dbrec.addField("部门",person.getUserId());
          dbrec.addField("人员",person.getUserName());
          dbrec.addField("角色",person.getUserPriv());
          //答对多少题
          if (examDate.getScore().split(",").length > 0) {
            for (int j = 0; j < examDate.getScore().split(",").length; j ++) {
              if (examDate.getScore().split(",")[j].equals("1")) {
                num ++;
              }
            }
          }
          dbrec.addField("分数",paperGrade*num/count);
          dbL.add(dbrec);
        }
      }
    }
    return dbL;
  }

  /***
   * 数据导出,取试卷ID，考试分数,题数--lz
   * @return
   * @throws Exception 
   * @throws Exception
   */
  public static T9ExamPaper selectPaper(Connection dbConn,String seqId) throws Exception{
    T9ORM orm = new T9ORM();
    T9ExamPaper paper = (T9ExamPaper)orm.loadObjComplex(dbConn, T9ExamPaper.class,Integer.parseInt(seqId));
    return paper;
  }

  /***
   * 数据导出,取试卷考试人,答对数量--lz
   * @return
   * @throws Exception 
   * @throws Exception 
   */
  public static List<T9ExamData> selectListData(Connection dbConn,String str[]) throws Exception{
    T9ORM orm = new T9ORM();
    List<T9ExamData> examData = orm.loadListSingle(dbConn,T9ExamData.class,str);
    return examData;
  }

  /***
   * 考试结果统计数据--lz
   * @return
   * @throws Exception 
   */
  public static String selectQIZ(Connection dbConn,Map request,String paperId,String questionsList) throws Exception {
    String sql = "select SEQ_ID,QUESTIONS,QUESTIONS_RANK,QUESTIONS_TYPE,ANSWERS FROM EXAM_QUIZ "
      + " WHERE SEQ_ID in (" + questionsList + ")";
    //    String sql = "select ex.SEQ_ID,quiz.QUESTIONS,quiz.QUESTIONS_RANK"
    //      + ",quiz.QUESTIONS_TYPE,quiz.ANSWERS from EXAM_PAPER ex "
    //      + " left outer join EXAM_QUIZ quiz on quiz.ROOM_ID = ex.ROOM_ID "
    //      //+ " left outer join EXAM_DATA data on data.FLOW_ID =" + flowId
    //      + " WHERE 1=1 and ex.SEQ_ID=" + paperId ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 考试结果统计数据(试题查询)--lz
   * @return
   * @throws Exception 
   */
  public static String selectQuestionsList(Connection dbConn,String paperId) throws Exception {
    String sql = "select QUESTIONS_LIST from EXAM_PAPER "
      + " WHERE SEQ_ID='" + paperId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String questionsList = "0";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        if(!T9Utility.isNullorEmpty(rs.getString("QUESTIONS_LIST"))){
          questionsList = rs.getString("QUESTIONS_LIST");
        }
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return questionsList;
  }

  /***
   *取试卷标题--lz
   * @return
   * @throws Exception 
   */
  public static T9ExamPaper showTitle(Connection dbConn,String paperId) throws Exception {
    T9ORM orm = new T9ORM();
    return (T9ExamPaper)orm.loadObjSingle(dbConn,T9ExamPaper.class,Integer.parseInt(paperId));
  }
  /***
   * 查题--lz
   * @return
   * @throws Exception 
   * @throws Exception 
   */
  public static List<T9ExamFlow> showMan2(Connection dbConn,List<T9ExamData> list,int paperGrade,int count) throws Exception{
    T9ExamData examDate = new T9ExamData();
    List<T9ExamFlow> examFlow = new ArrayList<T9ExamFlow>();
    T9ExamFlow flow = null;
    for (int i = 0; i < list.size(); i ++) {
      examDate = list.get(i);
      int num = 0;
      if (!T9Utility.isNullorEmpty(examDate.getParticipant())) {
        T9Person person = showPerson(dbConn,examDate.getParticipant());
        flow = new T9ExamFlow();
        flow.setParticipant(person.getUserId());//部门
        flow.setFlowTitle(person.getUserName());//部人员
        flow.setRankman(person.getUserPriv());//角色
        flow.setFlowDesc(String.valueOf(person.getSeqId()));//ID
        //答对多少题
        if (examDate.getScore().split(",").length > 0) {
          for (int j = 0; j < examDate.getScore().split(",").length; j ++) {
            if (examDate.getScore().split(",")[j].equals("1")) {
              num ++;
            }
          }
        }
        flow.setFlowFlag(String.valueOf(paperGrade*num/count));//分数
        examFlow.add(flow);
      } 
    }
    return examFlow;
  }

  /**
   * 查询考试次数 -lz
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String showCount(Connection dbConn,int seqId) throws Exception{
    String count = "0";
    String sql = "select count(*) from exam_data where flow_id=?";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,seqId);
      rs = ps.executeQuery();
      if (rs.next()) {
        if(!T9Utility.isNullorEmpty(rs.getString(1))){
          count = rs.getString(1);
        }
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return count;
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--syl
   * @return
   * @throws Exception 
   */
  public static String selectFlowOnLine(Connection dbConn,Map request,String dayTime,String userId) throws Exception {
    String sql = "select ex.SEQ_ID,ex.FLOW_TITLE,ex.PARTICIPANT,son.PAPER_TIMES"
      + ",ex.BEGIN_DATE,ex.END_DATE,son.PAPER_GRADE,ex.FLOW_DESC"
      + ",ex.SEND_TIME,ex.RANKMAN,ex.ANONYMITY from EXAM_FLOW ex "
      + " left outer join EXAM_PAPER son on son.seq_id = ex.PAPER_ID "
      + " WHERE 1=1 ";
    sql += " and " + T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), "<=");
    sql += " and (" + T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), ">")
    + " or ex.END_DATE is null) and " + T9DBUtility.findInSet(userId, "ex.PARTICIPANT")+ " order by ex.SEND_TIME desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    //(find_in_set('$LOGIN_USER_ID',PARTICIPANT)) 
    // and BEGIN_DATE<='$CUR_DATE' and (END_DATE>='$CUR_DATE' or END_DATE is null) 
    //order by SEND_TIME desc";
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 根据条件查询数据的记录树--syl
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public static String getCount(Connection dbConn,Map request,String dayTime) throws Exception{
    String count = "0";
    String sql = "select count(*) from EXAM_FLOW ex "
      + " left outer join EXAM_PAPER son on son.seq_id = ex.PAPER_ID "
      + " WHERE 1=1 ";
    sql += " and " + T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), "<=");
    sql += " and (" + T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), ">")
    + " or ex.END_DATE is null)";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        if(!T9Utility.isNullorEmpty(rs.getString(1))){
          count = rs.getString(1);
        }
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return count;
  }
}
