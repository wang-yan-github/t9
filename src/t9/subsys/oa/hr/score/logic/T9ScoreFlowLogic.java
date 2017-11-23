package t9.subsys.oa.hr.score.logic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
import t9.subsys.oa.hr.score.data.T9ScoreFlow;


public class T9ScoreFlowLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.hr.score.logic.T9ScoreFlowLogic");

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--lz
   * @return
   * @throws Exception 
   */
  public static String selectFlow(Connection dbConn,Map request) throws Exception {
    String sql = "select ex.SEQ_ID,son.SEQ_ID,son.GROUP_FLAG,ex.FLOW_TITLE,ex.RANKMAN,ex.PARTICIPANT,son.GROUP_NAME,ex.ANONYMITY"
      + ",ex.BEGIN_DATE,ex.END_DATE,ex.FLOW_DESC,ex.FLOW_FLAG,ex.SEND_TIME FROM score_flow ex "
      + " left outer join score_group son on son.seq_id = ex.GROUP_ID ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页--lz
   * @return
   * @throws Exception 
   */
  public static String selectFlow2(Connection dbConn,Map request,String dayTime,String seqId) throws Exception {
    String sql = "select ex.SEQ_ID as SEQ_ID,son.SEQ_ID as SEQ_IDS,son.GROUP_FLAG,ex.FLOW_TITLE,ex.RANKMAN,ex.PARTICIPANT,son.GROUP_NAME,ex.ANONYMITY"
      + ",ex.BEGIN_DATE,ex.END_DATE,ex.FLOW_DESC,ex.FLOW_FLAG,ex.SEND_TIME FROM score_flow ex "
      + " left outer join score_group son on son.seq_id = ex.GROUP_ID "
      + " WHERE 1=1 ";
    sql += " and " + T9DBUtility.findInSet(seqId,"ex.RANKMAN");
    sql += " and " + T9DBUtility.getDateFilter("ex.BEGIN_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), "<=");
    sql += " and (" + T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(T9Utility.parseDate(dayTime)), ">")
    + "or ex.END_DATE is null)  order by ex.SEQ_ID desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String selectList(Connection dbConn,Map request,T9ScoreFlow flow, String beginDate1,String endDate1,String cd) throws Exception {
    String sql = "select ex.SEQ_ID as SEQ_ID,son.SEQ_ID as SEQ_IDS,ex.FLOW_TITLE,ex.RANKMAN,ex.PARTICIPANT,son.GROUP_NAME,ex.ANONYMITY"
      + ",ex.BEGIN_DATE,ex.END_DATE,ex.FLOW_DESC,ex.FLOW_FLAG,ex.SEND_TIME FROM score_flow ex "
      + " left outer join score_group son on son.seq_id = ex.GROUP_ID "
      + " WHERE 1=1 ";
    if (!T9Utility.isNullorEmpty(flow.getFlowTitle())) {
      sql += " and ex.FLOW_TITLE like '%" + T9DBUtility.escapeLike(flow.getFlowTitle()) + "%' " + T9DBUtility.escapeLike();
    }
    if (flow.getGroupId() > 0) {
      sql += " and ex.GROUP_ID=" + flow.getGroupId();
    }
    if (!T9Utility.isNullorEmpty(flow.getRankman())) {
      sql += " and " + T9DBUtility.findInSet(flow.getRankman(),"ex.RANKMAN");
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
      sql += " and " +  T9DBUtility.getDateFilter("ex.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), "<=") + " and  ex.END_DATE is not null ";
    }
    sql += " order by ex.SEND_TIME desc ";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据--lz
   * @return
   * @throws Exception 
   */
  public static List<T9ScoreFlow> expList(Connection dbConn,String seqId) throws Exception {
    String sql = "select ex.SEQ_ID,ex.anonymity as anonymity,son.seq_id as groupId,item.item_name as itemName"
      + ",sd.RANKMAN as RANKMAN,sd.PARTICIPANT as PARTICIPANT,sd.score as score, sd.memo as memo FROM score_flow ex"
      + " left outer join score_group son on son.seq_id = ex.GROUP_ID"
      + " left outer join score_item item on item.group_id = son.seq_id"
      + " left outer join score_data sd on sd.flow_id = ex.SEQ_ID"
      + " where ex.seq_id='" + seqId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9ScoreFlow> list = new ArrayList<T9ScoreFlow>();
    T9ScoreFlow flow = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        flow = new T9ScoreFlow();
        flow.setRankman(rs.getString("RANKMAN"));
        flow.setAnonymity(rs.getString("anonymity"));
        flow.setParticipant(rs.getString("PARTICIPANT"));
        flow.setFlowTitle(rs.getString("memo"));
        flow.setFlowDesc(rs.getString("itemName"));
        flow.setFlowFlag(rs.getString("score"));
        list.add(flow);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return list;
  }
  
  public static List<T9ScoreFlow> expScoreDataList(Connection dbConn,String seqId, String groupId) throws Exception {
    String sql = "select PARTICIPANT, SCORE, MEMO from SCORE_DATA where FLOW_ID='" + seqId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9ScoreFlow> list = new ArrayList<T9ScoreFlow>();
    T9ScoreFlow flow = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        flow = new T9ScoreFlow();
        flow.setParticipant(rs.getString(1)); //PARTICIPANT
        flow.setFlowFlag(rs.getString(2));    //SCORE
        flow.setFlowTitle(rs.getString(3));
        list.add(flow);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return list;
  }
  
  public static List<T9ScoreFlow> expScoreItemList(Connection dbConn,String seqId, String groupId) throws Exception {
    String sql = "select ITEM_NAME from SCORE_ITEM where GROUP_ID='" + groupId + "'";
    String sql2 = "select ITEM_NAME from SCORE_ANSWER where GROUP_ID='" + groupId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;
    List<T9ScoreFlow> list = new ArrayList<T9ScoreFlow>();
    T9ScoreFlow flow = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        flow = new T9ScoreFlow();
        flow.setFlowDesc(rs.getString(1)); //ITEM_NAME
        list.add(flow);
      }
      ps2 = dbConn.prepareStatement(sql2);
      rs2 = ps2.executeQuery();
      while (rs2.next()) {
        flow = new T9ScoreFlow();
        flow.setFlowDesc(rs2.getString(1)); //ITEM_NAME
        list.add(flow);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
      T9DBUtility.close(ps2, rs2, log);
    }
    return list;
  }
  
  /**
   * 导出分数明细
   * @param dbConn
   * @param scoreDataList
   * @param scoreItemlist
   * @return
   * @throws Exception
   */
  public static ArrayList<T9DbRecord> getDbRecord(Connection dbConn,List<T9ScoreFlow> scoreDataList, List<T9ScoreFlow> scoreItemlist) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    T9DbRecord dbrec = null;
    T9ScoreFlow scoreFlow = new T9ScoreFlow();
    T9ScoreFlow scoreFlow2 = new T9ScoreFlow();
    T9ScoreFlow scoreFlow3 = new T9ScoreFlow();
    if (scoreItemlist.size() <= 0) {
      dbrec = new T9DbRecord();
      dbrec.addField("部门","");
      dbrec.addField("姓名","");
      dbrec.addField("角色","");
      dbL.add(dbrec);
    } else {
      for (int i = 0; i < scoreDataList.size(); i++) {
        scoreFlow = scoreDataList.get(i);
        dbrec = new T9DbRecord();
        T9Person person = showPerson(dbConn,scoreFlow.getParticipant());
        dbrec.addField("部门",person.getUserId());
        dbrec.addField("姓名",person.getUserName());
        dbrec.addField("角色",person.getUserPriv());  
        
        scoreFlow2 = scoreDataList.get(i);
        String[] scoreStr = scoreFlow2.getFlowFlag().split(",");
        String[] memoStr = scoreFlow2.getFlowTitle().split(",");
        for(int h = 0; h < scoreItemlist.size(); h++){
          scoreFlow3 = scoreItemlist.get(h);
          String score = "";
          if(h > scoreStr.length-1){
            score = "";
          }else{
            score = scoreStr[h];
          }
          if(h > memoStr.length-1){
            score += "";
          }else{
            score += "(批注:"+memoStr[h]+")";
          }
          dbrec.addField(scoreFlow3.getFlowDesc(),score);  
        }
        dbL.add(dbrec);
      }
    }
    return dbL;
  }
  
  /**
   * 导出总分
   * @param dbConn
   * @param scoreDataList
   * @param scoreItemlist
   * @return
   * @throws Exception
   */
  public static ArrayList<T9DbRecord> getDbRecord2(Connection dbConn,List<T9ScoreFlow> scoreDataList, List<T9ScoreFlow> scoreItemlist) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    T9DbRecord dbrec = null;
    T9ScoreFlow scoreFlow = new T9ScoreFlow();
    T9ScoreFlow scoreFlow2 = new T9ScoreFlow();
    T9ScoreFlow scoreFlow3 = new T9ScoreFlow();
    int count = 0;
    if (scoreItemlist.size() <= 0) {
      dbrec = new T9DbRecord();
      dbrec.addField("部门","");
      dbrec.addField("姓名","");
      dbrec.addField("角色","");
      dbL.add(dbrec);
    } else {
      for (int i = 0; i < scoreDataList.size(); i++) {
        scoreFlow = scoreDataList.get(i);
        dbrec = new T9DbRecord();
        T9Person person = showPerson(dbConn,scoreFlow.getParticipant());
        dbrec.addField("部门",person.getUserId());
        dbrec.addField("姓名",person.getUserName());
        dbrec.addField("角色",person.getUserPriv());  
        
        scoreFlow2 = scoreDataList.get(i);
        String[] scoreStr = scoreFlow2.getFlowFlag().split(",");
        String[] memoStr = scoreFlow2.getFlowTitle().split(",");
        for(int h = 0; h < scoreItemlist.size(); h++){
          scoreFlow3 = scoreItemlist.get(h);
          String score = "";
          if(h > scoreStr.length - 1){
            score = "";
          }else{
            score = scoreStr[h];
            String scoreFlag = "0";
            if(!T9Utility.isNullorEmpty(scoreStr[h])){
              scoreFlag = scoreStr[h];
            }
            count = count + Integer.parseInt(scoreFlag);
          }
          dbrec.addField(scoreFlow3.getFlowDesc(),score);  
        }
        dbrec.addField("总分",count);  
        count = 0;
        dbL.add(dbrec);
      }
    }
    return dbL;
  }
  
  public List getItemName(Connection dbConn, String groupId) throws Exception {
    List list = new ArrayList();
    Statement stmt = null;
    ResultSet rs = null;
    list.add("部门");
    list.add("名称");
    list.add("角色");
    String sql = "SELECT ITEM_NAME from SCORE_ITEM where GROUP_ID= '" + groupId + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String itemName = rs.getString(1);
        list.add(itemName);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }

  /**
   * 人员查询
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
   * 单个查询byId
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9ScoreFlow getFlowById(Connection dbConn,String seqId)throws Exception {
    T9ORM orm = new T9ORM();
    T9ScoreFlow flow = (T9ScoreFlow) orm.loadObjSingle(dbConn, T9ScoreFlow.class, Integer.parseInt(seqId));
    return flow;
  }

  public void addScoreFlow(Connection dbConn, T9ScoreFlow scoreFlow) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, scoreFlow);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }

  /**
   * 删除考核任务一条记录--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9ScoreFlow.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  public void deleteSingleScoreData(Connection conn, String flowId) throws Exception {
    String sql = "DELETE FROM SCORE_DATA WHERE FLOW_ID = '" + flowId + "'";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  
  public void deleteSingleScoreFlow(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9ScoreFlow.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }

  public T9ScoreFlow getScoreFlowDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9ScoreFlow) orm.loadObjSingle(conn, T9ScoreFlow.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }

  public void updateScoreFlow(Connection conn, T9ScoreFlow scoreFlow) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, scoreFlow);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }

  /**
   * 立即生效,立即终止,恢复终止
   * @return
   * @throws Exception
   */
  public static void updateBeginDate(Connection dbConn,int seqId,String tdName,Date beginDate) throws Exception {
    String sql = "update SCORE_FLOW set " + tdName + "=? where SEQ_ID=?";
    PreparedStatement ps = null;
    try{
      ps = dbConn.prepareStatement(sql);
      ps.setDate(1,beginDate);
      ps.setInt(2,seqId);
      ps.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      //T9DBUtility.close(ps,null, log);
    }
  }

  public String getGroupDescLogic(Connection conn, int seqId) throws Exception {
    String result = "";
    String sql = " select GROUP_DESC from SCORE_GROUP where SEQ_ID = " + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

  public String getCheckFlagLogic(Connection conn, int seqId) throws Exception {
    String result = "";
    String sql = " select CHECK_FLAG from SCORE_FLOW where SEQ_ID = " + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 获取考核任务标题
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getFlowTitleName(Connection conn, int seqId) throws Exception {
    String result = "";
    String sql = " select FLOW_TITLE from SCORE_FLOW where SEQ_ID = " + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 获取设定考核依据模块工作日志和日程
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getGroupRefer(Connection conn, int seqId) throws Exception {
    String result = "";
    String sql = " select GROUP_REFER from SCORE_GROUP where SEQ_ID = " + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  public static List<T9Person> showMan(Connection dbConn,String participant) throws Exception {
    String sql = "select son.SEQ_ID as seqId,son.USER_NAME as userName,dep.DEPT_NAME as deptName "
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
        person.setSeqId(rs.getInt("seqId"));
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
   * 判断打分状态是否完成
   * @param dbConn
   * @param userId
   * @param flowId
   * @return
   * @throws Exception
   */
  public boolean getFinishFlag(Connection dbConn, String userId, int flowId)
  throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String[] participantCountStr = getParticipantCount(dbConn, flowId).split(",");
    long participantCount = participantCountStr.length;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) from SCORE_DATA where FLOW_ID = " + flowId + " and RANKMAN = '" + userId + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == participantCount) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 判断考核指标集是否已经被应用
   * @param dbConn
   * @param groupId
   * @return
   * @throws Exception
   */
  public boolean getScoreFlowFlag(Connection dbConn, String groupId)
  throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) from SCORE_FLOW where GROUP_ID = " + groupId ;
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count > 0) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public String getParticipantCount(Connection conn, int seqId) throws Exception {
    String result = "";
    String sql = " select PARTICIPANT from SCORE_FLOW where SEQ_ID = " + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

}
