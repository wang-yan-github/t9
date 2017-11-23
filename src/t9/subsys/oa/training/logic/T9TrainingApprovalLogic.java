package t9.subsys.oa.training.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.training.data.T9HrTrainingPlan;

public class T9TrainingApprovalLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.training.logic.T9TrainingApprovalLogic.java");

	 /**
   * 培训计划名称--计划编号--模糊查找 最多显示50条--cc

   * 1.查找用户的部门id
   * 2.查找图书的借阅范围包含部门id
   * @param dbConn
   * @param condition 查询条件
   * @param user 用户
   * @return
   * @throws Exception 
   * @throws SQLException 
   */
  public List<T9HrTrainingPlan> findTrainingPlanNo(Connection dbConn, T9Person user, String condition) throws SQLException, Exception{     

    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "SELECT SEQ_ID,T_PLAN_NO,T_PLAN_NAME, T_INSTITUTION_NAME from HR_TRAINING_PLAN where 1=1" ;
    if(!T9Utility.isNullorEmpty(condition)){
       sql += " and T_PLAN_NAME like '%" + T9DBUtility.escapeLike(condition) +"%'"; 
    }
    sql += " order by T_PLAN_NO";
    List<T9HrTrainingPlan> hrTrainingPlan = new ArrayList<T9HrTrainingPlan>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();  
      int cont = 0;
      while(rs.next() && ++cont < 50){
        T9HrTrainingPlan trainingPlan = new T9HrTrainingPlan();
        trainingPlan.setSeqId(rs.getInt("SEQ_ID"));
        trainingPlan.setTPlanNo(rs.getString("T_PLAN_NO"));
        trainingPlan.setTPlanName(rs.getString("T_PLAN_NAME"));
        trainingPlan.setTInstitutionName(rs.getString("T_INSTITUTION_NAME"));
        hrTrainingPlan.add(trainingPlan);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return hrTrainingPlan;
  }
  
  /**
   * 获取用户信息--cc
   * @param dbConn
   * @param user
   * @param condition
   * @return
   * @throws SQLException
   * @throws Exception
   */
  public List<T9Person> findTrainingUserSelect(Connection dbConn, T9Person user, String condition) throws SQLException, Exception{     

    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "SELECT SEQ_ID,USER_ID,USER_NAME from PERSON where 1=1" ;
    if(!T9Utility.isNullorEmpty(condition)){
       sql += " and USER_NAME like '%" + T9DBUtility.escapeLike(condition) +"%'"; 
    }
    sql += " order by USER_ID";
    List<T9Person> result = new ArrayList<T9Person>();
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();  
      int cont = 0;
      while(rs.next() && ++cont < 50){
        T9Person per = new T9Person();
        per.setSeqId(rs.getInt("SEQ_ID"));
        per.setUserId(rs.getString("USER_ID"));
        per.setUserName(rs.getString("USER_NAME"));
        result.add(per);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  /**
   * 会议管理通用列表--cc
   * 
   * @param dbConn
   * @param request
   * @param mStatus
   * @param person
   * @return
   * @throws Exception
   */
  public String getTrainingApprovalListJson(Connection dbConn, Map request, String assessingStatus, T9Person person) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select " 
        + "  SEQ_ID" 
        + ", ASSESSING_STATUS" 
        + ", T_PLAN_NO" 
        + ", T_PLAN_NAME" 
        + ", T_CHANNEL" 
        + ", T_COURSE_TYPES" 
        + ", T_ADDRESS" 
        + " from HR_TRAINING_PLAN where ASSESSING_OFFICER = '" + person.getSeqId() + "' and ASSESSING_STATUS = '" + assessingStatus + "' ORDER BY SEQ_ID desc";
    }else{
      sql = "select " 
        + "  SEQ_ID" 
        + ", ASSESSING_STATUS" 
        + ", T_PLAN_NO" 
        + ", T_PLAN_NAME" 
        + ", T_CHANNEL" 
        + ", T_COURSE_TYPES" 
        + ", T_ADDRESS" 
        + " from HR_TRAINING_PLAN where CREATE_USER_ID = '" + person.getSeqId() + "' and ASSESSING_OFFICER = '" + person.getSeqId() + "' and ASSESSING_STATUS = '" + assessingStatus + "' ORDER BY SEQ_ID desc";
    }
     

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
  
  /**
   * 获取培训计划详细信息--cc
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9HrTrainingPlan getPlanDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9HrTrainingPlan) orm.loadObjSingle(conn, T9HrTrainingPlan.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 获取审批人名称--cc
   * 
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */

  public String getUserNameLogic(Connection conn, int userId) throws Exception {
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID = " + userId;
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
   * 短信提醒(带时间)--cc
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @param sendDate
   * @throws Exception
   */
  public static void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
      throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    sb.setSendDate(sendDate);
    T9SmsUtil.smsBack(conn, sb);
  }
  
  /**
   * 培训计划(审批)查询--cc
   * @param dbConn
   * @param request
   * @param person
   * @param tPlanName
   * @param tChannel
   * @param assessingOfficer
   * @param assessingStatus
   * @param beginDate
   * @param endDate
   * @return
   * @throws Exception
   */
  public String getTrainingApprovalSearchList(Connection dbConn, Map request, T9Person person, String tPlanName, String tChannel, String assessingOfficer,
      String assessingStatus, String beginDate, String endDate) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select " 
        + "SEQ_ID" 
        + ", T_PLAN_NO" 
        + ", T_PLAN_NAME" 
        + ", T_CHANNEL" 
        + ", T_COURSE_TYPES" 
        + ", T_ADDRESS" 
        + ", ASSESSING_STATUS" 
        + " from HR_TRAINING_PLAN where 1=1 ";
    }else{
      sql = "select " 
        + "SEQ_ID" 
        + ", T_PLAN_NO" 
        + ", T_PLAN_NAME" 
        + ", T_CHANNEL" 
        + ", T_COURSE_TYPES" 
        + ", T_ADDRESS" 
        + ", ASSESSING_STATUS" 
        + " from HR_TRAINING_PLAN where CREATE_USER_ID = '" + person.getSeqId() + "'";
    }

    if (!T9Utility.isNullorEmpty(tPlanName)) {
      sql = sql + " and T_PLAN_NAME = '" + tPlanName + "'";
    }
    if (!T9Utility.isNullorEmpty(tChannel)) {
      sql = sql + " and T_CHANNEL = '" + tChannel + "'";
    }
    if (!T9Utility.isNullorEmpty(beginDate)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("ASSESSING_TIME", beginDate, ">=");
    }

    if (!T9Utility.isNullorEmpty(endDate)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("ASSESSING_TIME", endDate, "<=");
    }

    if (!T9Utility.isNullorEmpty(assessingOfficer)) {
      sql = sql + " and ASSESSING_OFFICER = '" + assessingOfficer + "'";
    }

    if (!T9Utility.isNullorEmpty(assessingStatus)) {
      sql = sql + " and ASSESSING_STATUS = '" + assessingStatus + "'";
    }
    sql = sql + " ORDER BY SEQ_ID desc";

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);

    return pageDataList.toJson();
  }
}
