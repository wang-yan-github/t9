package t9.core.funcs.workplan.logic;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workplan.data.T9WorkPlan;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
public class T9WorkLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.workplan.act.T9PlanWorkAct");
  /**
   * 增加数据
   * @param dbConn
   * @param workPlan
   * @throws Exception 
   */
  public void add(Connection dbConn,T9WorkPlan workPlan) throws Exception {
    String content = "";
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String sql = "insert into WORK_PLAN(NAME,CONTENT,BEGIN_DATE,END_DATE,TYPE,TO_ID,MANAGER," +
    "PARTICIPATOR,CREATOR,CREATE_DATE,ATTACHMENT_ID,ATTACHMENT_NAME,ATTACHMENT_COMMENT," +
    "REMARK,TO_PERSON_ID,SUSPEND_FLAG,PUBLISH,OPINION_LEADER,SMS_FLAG) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    if (!T9Utility.isNullorEmpty(workPlan.getContent())) {
      content = workPlan.getContent().replaceAll("'","\"");
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1,workPlan.getName().replaceAll("\"","'"));
      stmt.setString(2,content);
      stmt.setDate(3,workPlan.getStatrTime());
      stmt.setDate(4,workPlan.getEndTime());
      stmt.setString(5,workPlan.getType());
      stmt.setString(6,workPlan.getDeptParentDesc());
      stmt.setString(7,workPlan.getLeader2Desc());
      stmt.setString(8,workPlan.getLeader1Desc());
      stmt.setString(9,workPlan.getCreator());
      stmt.setDate(10,workPlan.getCreatedate());
      stmt.setString(11,workPlan.getAttachmentid());
      stmt.setString(12,workPlan.getAttachmentname());
      stmt.setString(13,workPlan.getAttachmentcomment());
      stmt.setString(14,workPlan.getRemark());
      stmt.setString(15,workPlan.getManagerDesc());
      stmt.setString(16,workPlan.getSuspendflag());
      stmt.setString(17,workPlan.getPublish());
      stmt.setString(18,workPlan.getLeader3Desc());
      stmt.setString(19,workPlan.getSmsflag());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  /**
   * 删除数据
   * @throws SQLException 
   */
  public void deleteWork(Connection dbConn,int seqid) throws SQLException {
    PreparedStatement stmt = null ; 
    String sql = "delete from WORK_Plan where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,seqid);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 删除所有数据
   * @throws SQLException 
   */
  public void deleteWorkAll(Connection dbConn,String seqId) throws SQLException {
    PreparedStatement stmt = null ; 
    String sql = "delete from WORK_Plan where 1=1";
    if (!seqId.equals("1")) {
      String manager = T9DBUtility.findInSet(seqId,"MANAGER");
      sql += " and (CREATOR=" + seqId + " or " + manager +")";
    }
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (SQLException e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /***
   * 修改状态数据
   * @return
   * @throws Exception 
   */
  public void updatePub(Connection dbConn,int seqid,String pub) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String sql = "update WORK_Plan set publish=? where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(2,seqid);
      stmt.setString(1,pub);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
  }
  /***
   * 修改状态数据
   * @return
   * @throws Exception 
   */
  public void updatePub4(Connection dbConn,int seqid,String pub,Date endTime) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String sql = "update WORK_Plan set publish=?,END_DATE=? where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(3,seqid);
      stmt.setDate(2,endTime);
      stmt.setString(1,pub);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
  }

  /***
   * 修改状态数据

   * @return
   * @throws Exception 
   */
  public void updatePub8(Connection dbConn,int seqid,String pub,Date endTime) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String sql = "update WORK_Plan set publish=?,begin_date=? where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(3,seqid);
      stmt.setDate(2,endTime);
      stmt.setString(1,pub);
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
  }
  /***
   * 先查询再，修改数据
   * @return
   * @throws Exception 
   */
  public T9WorkPlan updateWork(Connection dbConn,int seqid) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkPlan workData = null;
    T9WorkPlan workPlan = new T9WorkPlan();
    String sql = "select CREATE_DATE,CREATOR,NAME,SEQ_ID,begin_date," +
    "end_Date,type,MANAGER,PARTICIPATOR,ATTACHMENT_NAME,publish," +
    "CONTENT,to_id,attachment_id,attachment_comment,remark," +
    "to_person_id,suspend_flag,opinion_leader,sms_flag from WORK_Plan where SEQ_ID=? order by seq_id";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1,seqid);
      rs = stmt.executeQuery();
      while (rs.next()) {
        workData = new T9WorkPlan(); 
        workData.setName(rs.getString("NAME"));
        workData.setSeqId(rs.getInt("SEQ_ID"));  
        workData.setStatrTime(rs.getDate("begin_date"));
        workData.setEndTime(rs.getDate("end_Date"));
        workData.setType(rs.getString("type"));
        workData.setLeader2Desc(rs.getString("MANAGER"));
        workData.setLeader1Desc(rs.getString("PARTICIPATOR"));
        workData.setAttachmentname(rs.getString("ATTACHMENT_NAME"));
        workData.setPublish(rs.getString("publish"));
        workData.setContent(rs.getString("CONTENT"));
        workData.setDeptParentDesc(rs.getString("to_id"));
        workData.setAttachmentid(rs.getString("attachment_id"));
        workData.setAttachmentcomment(rs.getString("attachment_comment"));
        workData.setRemark(rs.getString("remark"));
        workData.setManagerDesc(rs.getString("to_person_id"));
        workData.setSuspendflag(rs.getString("suspend_flag"));
        workData.setLeader3Desc(rs.getString("opinion_leader"));
        workData.setSmsflag(rs.getString("sms_flag")); 
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
    return workPlan;
  }
  /***
   * 根据ID查询数据
   * @return
   * @throws Exception 
   */
  public  T9WorkPlan  selectId(Connection dbConn,int seqid) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkPlan workData = null;
    String sql = "select CREATE_DATE,CREATOR,NAME,SEQ_ID,begin_date," +
    "end_Date,type,MANAGER,PARTICIPATOR,ATTACHMENT_NAME,publish," +
    "CONTENT,to_id,attachment_id,attachment_comment,remark," +
    "to_person_id,suspend_flag,opinion_leader,sms_flag from work_plan where seq_id=? order by seq_id";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, seqid);
      rs = stmt.executeQuery();
      if(rs.next()) {
        workData = new T9WorkPlan(); 
        workData.setName(rs.getString("NAME"));
        workData.setSeqId(rs.getInt("SEQ_ID"));  
        workData.setStatrTime(rs.getDate("begin_date"));
        workData.setEndTime(rs.getDate("end_Date"));
        workData.setType(rs.getString("type"));
        workData.setLeader2Desc(rs.getString("MANAGER"));
        workData.setLeader1Desc(rs.getString("PARTICIPATOR"));
        workData.setAttachmentname(rs.getString("ATTACHMENT_NAME"));
        workData.setPublish(rs.getString("publish"));
        workData.setContent(rs.getString("CONTENT"));
        workData.setDeptParentDesc(rs.getString("to_id"));
        workData.setAttachmentid(rs.getString("attachment_id"));
        workData.setAttachmentcomment(rs.getString("attachment_comment"));
        workData.setRemark(rs.getString("remark"));
        workData.setSmsflag(rs.getString("SMS_FLAG"));
        workData.setManagerDesc(rs.getString("to_person_id"));
        workData.setSuspendflag(rs.getString("suspend_flag"));
        workData.setLeader3Desc(rs.getString("opinion_leader"));
        workData.setSmsflag(rs.getString("sms_flag")); 
        workData.setCreatedate(rs.getDate("CREATE_DATE"));
        workData.setCreator(rs.getString("CREATOR"));
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return workData;  
  }
  /***
   * 修改数据
   * @return
   * @throws Exception 
   */
  public void workUpd(Connection dbConn,T9WorkPlan plan) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String sql = "update WORK_Plan set remark=?,SMS_FLAG=?,begin_date=?, OPINION_LEADER=? where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1, plan.getRemark());
      stmt.setString(2, plan.getSmsflag());
      stmt.setDate(3, plan.getStatrTime());
      stmt.setString(4,plan.getLeader3Desc());
      stmt.setInt(5, plan.getSeqId());
      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
  }
  /***
   * 修改查询数据
   * @return
   * @throws Exception 
   */
  public void updateWork2(Connection dbConn,T9WorkPlan plan) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    String content = "";
    if (!T9Utility.isNullorEmpty(plan.getContent())) {
      content = plan.getContent().replaceAll("'","\"");
    }
    String sql = "update WORK_Plan set remark=?,SMS_FLAG=?,begin_date=?," +
    "NAME=?,CONTENT=?,END_DATE=?,TYPE=?,TO_ID=?,MANAGER=?," +
    "PARTICIPATOR=?,ATTACHMENT_NAME=?," +
    "attachment_id=?,attachment_comment=?,to_person_id=?," +
    "suspend_flag=?,opinion_leader=?,PUBLISH=? where SEQ_ID=?";
    try {
      stmt = dbConn.prepareStatement(sql);

      stmt.setString(1, plan.getRemark());
      stmt.setString(2, plan.getSmsflag());
      stmt.setDate(3, plan.getStatrTime());
      stmt.setString(4,plan.getName().replaceAll("\"","'"));
      stmt.setString(5,content);
      stmt.setDate(6,plan.getEndTime());
      stmt.setString(7,plan.getType());
      stmt.setString(8,plan.getDeptParentDesc());
      stmt.setString(9, plan.getLeader2Desc());
      stmt.setString(10, plan.getLeader1Desc());
      stmt.setString(11,plan.getAttachmentname());
      stmt.setString(12, plan.getAttachmentid());
      stmt.setString(13,plan.getAttachmentcomment());
      stmt.setString(14,plan.getManagerDesc());
      stmt.setString(15,plan.getSuspendflag());
      stmt.setString(16,plan.getLeader3Desc());
      stmt.setString(17,plan.getPublish());
      stmt.setInt(18, plan.getSeqId());

      stmt.executeUpdate();
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    } 
  }

  /***
   * 根据条件查询数据
   * @return
   * @throws Exception 
   */
  public List<T9WorkPlan> selectRes(Connection dbConn,T9WorkPlan plan , T9Person user) throws Exception {
    ResultSet rs = null;
    PreparedStatement stmt = null ; 
    T9WorkPlan workData = null;
    List<T9WorkPlan> list = new ArrayList<T9WorkPlan>();
    String sql = "select CREATE_DATE,CREATOR,NAME,SEQ_ID,begin_date," +
    "end_Date,type,MANAGER,PARTICIPATOR,ATTACHMENT_NAME,publish," +
    "CONTENT,to_id,attachment_id,attachment_comment,remark," +
    "to_person_id,suspend_flag,opinion_leader,sms_flag from work_plan where 1=1";
    try {
      String query = "";
      if(!T9Utility.isNullorEmpty(plan.getName())){
        query += " and NAME like '%" + T9DBUtility.escapeLike(plan.getName()) + "%' " + T9DBUtility.escapeLike();
      }
      if(!T9Utility.isNullorEmpty(plan.getLeader1Desc())){
        query += " and PARTICIPATOR like '%" + plan.getLeader1Desc()+ "%'";
      }
      if(!T9Utility.isNullorEmpty(plan.getLeader2Desc())){
        query += " and MANAGER like '%" + plan.getLeader2Desc()+ "%'";
      }
      if(!T9Utility.isNullorEmpty(plan.getDeptParentDesc())){
        query += " and TO_ID like '%" + plan.getDeptParentDesc() + "%'";
      }
      if(!T9Utility.isNullorEmpty(plan.getManagerDesc())){
        query += " and TO_PERSON_ID like '%" + plan.getManagerDesc()+ "%'";
      }
      if(!T9Utility.isNullorEmpty(plan.getContent())){
        query += " and content like '%" + T9DBUtility.escapeLike(plan.getContent()) + "%' " + T9DBUtility.escapeLike();
      }
      if(!T9Utility.isNullorEmpty(plan.getRemark())){
        sql += " and REMARK like '%" + T9DBUtility.escapeLike(plan.getRemark()) + "%' " + T9DBUtility.escapeLike();
      }
      if(!T9Utility.isNullorEmpty(plan.getLeader3Desc())){
        query += " and OPINION_LEADER like '%" + plan.getLeader3Desc() + "%'";
      }
      if(plan.getStatrTime() != null){
        String str =  T9DBUtility.getDateFilter("BEGIN_DATE", T9Utility.getDateTimeStr(plan.getStatrTime()), ">=");
        query += " and " + str;
      }
      if(plan.getEndTime() != null){
        String str =  T9DBUtility.getDateFilter("END_DATE", T9Utility.getDateTimeStr(plan.getEndTime()), "<=");
        query += " and " + str;
      }
      if(!T9Utility.isNullorEmpty(plan.getType()) && !plan.getType().equals("")){
        query += " and TYPE='" + plan.getType() + "'";
      }
    //------------------------------------------------------------------------------
      if (!user.isAdmin()) {
         sql += " and (TO_ID='ALL_DEPT' or TO_ID='0' or "+ T9DBUtility.findInSet(user.getDeptId() + "", "TO_ID") 
                          +" or " +  T9DBUtility.findInSet(user.getSeqId() + "", "TO_PERSON_ID")
                            +" or " +  T9DBUtility.findInSet(user.getSeqId() + "", "MANAGER")
                              +" or " +  T9DBUtility.findInSet(user.getSeqId() + "", "PARTICIPATOR") + ")";
      }
      sql += query ;
      sql += " order by BEGIN_DATE desc";
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        workData = new T9WorkPlan(); 
        workData.setCreatedate(rs.getDate("CREATE_DATE"));
        workData.setCreator(rs.getString("CREATOR"));
        workData.setName(rs.getString("NAME"));
        workData.setSeqId(rs.getInt("SEQ_ID"));  
        workData.setStatrTime(rs.getDate("begin_date"));
        workData.setEndTime(rs.getDate("end_Date"));
        workData.setType(rs.getString("type"));
        workData.setLeader2Desc(rs.getString("MANAGER"));
        workData.setLeader1Desc(rs.getString("PARTICIPATOR"));
        workData.setAttachmentname(rs.getString("ATTACHMENT_NAME"));
        workData.setPublish(rs.getString("publish"));
        workData.setContent(rs.getString("CONTENT"));
        workData.setDeptParentDesc(rs.getString("to_id"));
        workData.setAttachmentid(rs.getString("attachment_id"));
        workData.setAttachmentcomment(rs.getString("attachment_comment"));
        workData.setRemark(rs.getString("remark"));
        workData.setManagerDesc(rs.getString("to_person_id"));
        workData.setSuspendflag(rs.getString("suspend_flag"));
        workData.setLeader3Desc(rs.getString("opinion_leader"));
        workData.setSmsflag(rs.getString("sms_flag")); 
        list.add(workData);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;  
  }
  /**
   * 取得最大的seqId
   * @param conn
   * @param tableName
   * @return
   * @throws Exception
   */
  public int getMaxSeqId(Connection conn) throws Exception{
    int result = 0;
    String sql = "select max(SEQ_ID) from work_plan";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String applySelect(Connection dbConn,Map request,String createTime,String type,String create,String seqId,String deptIdStr) throws Exception {
    String beginTime = "";
    String endTime = "";
    String andOr = "";
    if (createTime.trim().equals("1")) {
      //T9DBUtility.curDayFilter("begin_date");
      beginTime  = T9DBUtility.getDateFilter("work.begin_date", T9Utility.getDateTimeStr(new java.util.Date()), "<=");
      endTime =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), ">=");
      if (T9Utility.isNullorEmpty(endTime)) {
        endTime  = T9DBUtility.curMonthFilter(T9Utility.getCurDateTimeStr());
      }
      andOr = "and";
    }
    if (createTime.trim().equals("2")) {
      beginTime  = T9DBUtility.curWeekFilter("work.begin_date");
      //endTime = T9DBUtility.curWeekFilter("end_Date");
      endTime =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), ">=");
      if (T9Utility.isNullorEmpty(endTime)) {
        endTime  = T9DBUtility.curMonthFilter(T9Utility.getCurDateTimeStr());
      }
      andOr = "or";
    }
    if (createTime.trim().equals("3")) {
      beginTime  = T9DBUtility.curMonthFilter("work.begin_date");
      endTime = T9DBUtility.curMonthFilter("work.end_Date");
      if (T9Utility.isNullorEmpty(endTime)) {
        endTime  = T9DBUtility.curMonthFilter(T9Utility.getCurDateTimeStr());
      }
      andOr = "or";
    }
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select work.SEQ_ID,work.NAME,work.begin_date,work.end_Date,[plan].type_name,"
        + " work.MANAGER,work.PARTICIPATOR,work.attachment_id,work.ATTACHMENT_NAME,work.publish,"
        + " work.CONTENT,work.to_id,work.attachment_comment,work.remark,"
        + " work.to_person_id,work.suspend_flag,work.opinion_leader,work.sms_flag from work_plan work"
        + " left outer join plan_type [plan] on work.type=[plan].seq_id"
        + " where (" + beginTime + " " + andOr + " " + endTime + " or work.end_Date is null) ";
    }else {
      sql = "select work.SEQ_ID,work.NAME,work.begin_date,work.end_Date,plan.type_name,"
        + " work.MANAGER,work.PARTICIPATOR,work.attachment_id,work.ATTACHMENT_NAME,work.publish,"
        + " work.CONTENT,work.to_id,work.attachment_comment,work.remark,"
        + " work.to_person_id,work.suspend_flag,work.opinion_leader,work.sms_flag from work_plan work"
        + " left outer join plan_type plan on work.type=plan.seq_id"
        + " where (" + beginTime + " " + andOr + " " + endTime + " or work.end_Date is null) ";
    }
    if (!type.equals("0")) {
      sql += " and work.type=" + type;
    }
    if (create.equals("2")) {
      String str =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), "<=");
      sql += " and (" + str + " and work.END_DATE is not null) ";
    }
    if (create.equals("1")) {
      String str =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), ">");
      sql += " and (" + str + " or work.END_DATE is null) ";
    }
    String  manager = T9DBUtility.findInSet(seqId, "work.MANAGER");
    String  participator = T9DBUtility.findInSet(seqId, "work.PARTICIPATOR");
    String  toPersonId = T9DBUtility.findInSet(seqId, "work.TO_PERSON_ID");
    String deptId = T9DBUtility.findInSet(deptIdStr, "work.TO_ID");
    sql += " and (work.TO_ID='0' or work.TO_ID='ALL_DEPT' or work.creator='" + seqId + "'" 
    + " or " + manager + " or " + participator + " or " + toPersonId + " or " + deptId +" ) ";

    //数据库中一个数字串跟输入的一个数字怎么比较。比如2,4,5,6,7 跟 4 比较
    //+ "' or FIND_IN_SET('" + seqId + "',work.MANAGER)"

    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  //seqId串转换成NAME串
  public String userName(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select user_name from  person where seq_id in (" + seqId + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += rs.getString("user_name") + ",";
      }
      if (name.length() > 0) {
        name = name.substring(0,name.length()-1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public String workSelect(Connection dbConn,Map request,String type,String status,String seqId) throws Exception {
 
    String sql = null;
    String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
    if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
      sql = "select work.SEQ_ID,work.NAME,work.begin_date,work.end_Date,[plan].type_name,"
        + " work.MANAGER,work.PARTICIPATOR,work.attachment_id,work.ATTACHMENT_NAME,work.publish,"
        + " work.CONTENT,work.creator,work.to_id,work.attachment_comment,work.remark,"
        + " work.to_person_id,work.suspend_flag,work.opinion_leader,work.sms_flag from work_plan work"
        + " left outer join plan_type [plan] on work.type=[plan].seq_id"
        + " where 1=1";
    }else {
      sql = "select work.SEQ_ID,work.NAME,work.begin_date,work.end_Date,plan.type_name,"
        + " work.MANAGER,work.PARTICIPATOR,work.attachment_id,work.ATTACHMENT_NAME,work.publish,"
        + " work.CONTENT,work.creator,work.to_id,work.attachment_comment,work.remark,"
        + " work.to_person_id,work.suspend_flag,work.opinion_leader,work.sms_flag from work_plan work"
        + " left outer join plan_type plan on work.type=plan.seq_id"
        + " where 1=1";
    }
    if (!type.equals("0")) {
      sql += " and work.type=" + type;
    }
    if (status.equals("2")) {
      String str =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), "<=");
      sql += " and (" + str + " and work.END_DATE is not null) ";
    }
    if (status.equals("1")) {
      String str =  T9DBUtility.getDateFilter("work.END_DATE", T9Utility.getDateTimeStr(new java.util.Date()), ">");
      sql += " and (" + str + " or work.END_DATE is null) ";
    }
    if (!seqId.equals("1")) {
      String  manager = T9DBUtility.findInSet(seqId, "work.MANAGER");
      sql += " and (work.creator='" + seqId + "'" 
      + " or " + manager + " ) ";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  //seqId串转换成NAME串
  public String getName(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select seq_id,user_name from  person where seq_id in (" + seqId + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += "<a href=javascript:workYes(" + rs.getString("seq_id") + ")>" + rs.getString("user_name") + "</a>,";
      }
      if (name.length() > 0) {
        name = name.substring(0,name.length()-1);
      }
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return name;
  }
}
