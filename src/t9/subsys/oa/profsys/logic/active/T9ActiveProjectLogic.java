package t9.subsys.oa.profsys.logic.active;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9Project;

public class T9ActiveProjectLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.profsys.act.T9InProjectLogic");
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projType,String projNum
      ,String projActiveType,String projStartTime1,String projStartTime2,
      String projGropName,String projVisitType,String projEndTime1,String projEndTime2,
      String projLeader,String deptId,String managerStr,String projStatus,int userId) throws Exception{
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.PROJ_TYPE = '" + projType + "'";
    if(!T9Utility.isNullorEmpty(projStatus)){
      if(projStatus.equals("0")){
        sql = sql + " and (p.PROJ_STATUS = '0' or p.PROJ_STATUS is null)";
      }else{
        sql = sql + " and p.PROJ_STATUS = '" + projStatus + "'";
      }
    }
    if(!T9Utility.isNullorEmpty(managerStr)){
      sql = sql + " and (p.DEPT_ID " + managerStr + " or p.CREATOR = '" + userId + "')";
    }
    if(!T9Utility.isNullorEmpty(projNum)){
      sql = sql + " and p.PROJ_NUM like '%" + T9DBUtility.escapeLike(projNum) + "%'" + T9DBUtility.escapeLike() ;
    }
    if(!T9Utility.isNullorEmpty(projActiveType)){
      sql = sql + " and p.PROJ_ACTIVE_TYPE = '" + projActiveType + "'";
    }

    if(!T9Utility.isNullorEmpty(projStartTime1)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_START_TIME",projStartTime1, ">=");
    }
    if(!T9Utility.isNullorEmpty(projStartTime2)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_START_TIME",projStartTime2 + " 23:59:59", "<=");
    }
    if(!T9Utility.isNullorEmpty(projGropName)){
      sql = sql + " and ba.BUDGET_ITEM like '%" + T9DBUtility.escapeLike(projGropName) + "%'" + T9DBUtility.escapeLike() ;
    }
    if(!T9Utility.isNullorEmpty(projVisitType)){
      sql = sql + " and p.PROJ_VISIT_TYPE ='" + projVisitType + "'";
    }

    if(!T9Utility.isNullorEmpty(projEndTime1)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_END_TIME",projEndTime1, ">=");
    }
    if(!T9Utility.isNullorEmpty(projEndTime2)){
      sql = sql + " and " + T9DBUtility.getDateFilter("p.PROJ_END_TIME",projEndTime2 + " 23:59:59", "<=");
    }
    if(!T9Utility.isNullorEmpty(projLeader)){
      sql = sql + " and p.PROJ_LEADER = '" + projLeader + "'";
    }
    if(!T9Utility.isNullorEmpty(deptId)&& !deptId.equals("0")){
      sql = sql + " and p.DEPT_ID in(" + deptId + ")";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public String toSearchData(Connection conn,Map request,String projType) throws Exception{
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.PROJ_TYPE = '" + projType + "' order by p.PROJ_START_TIME";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 导出
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public static ArrayList<T9DbRecord> toInExp(Connection dbConn,String seqIds) throws Exception{
    ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
    String sql = "select p.SEQ_ID,p.PROJ_ARRIVE_TIME,p.PROJ_LEAVE_TIME,ba.BUDGET_ITEM"
      + ",ci.CLASS_DESC,p.COUNTRY_TOTAL,p.PURPOSE_COUNTRY,pn.USER_NAME,p.P_TOTAL,p.P_COUNCIL,p.P_YX,p.P_GUEST"
      + ",p.PRINT_STATUS"
      + " from PROJECT p left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.SEQ_ID in(" + seqIds + ")";
    Statement stmt = null;
    ResultSet rs = null; 
    String budgetItem = "";
    int i = 0;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        T9DbRecord dbrec = new T9DbRecord();
        dbrec.addField("序号",++i);
        dbrec.addField("开始时间",T9Utility.getDateTimeStr(rs.getTimestamp(2)).substring(0, 10));
        dbrec.addField("结束时间",T9Utility.getDateTimeStr(rs.getTimestamp(3)).substring(0, 10));
        dbrec.addField("活动名称",rs.getString(4));
        dbrec.addField("活动类别",rs.getString(5));
        dbrec.addField("国家数",rs.getString(6));
        dbrec.addField("国家名",rs.getString(7));
        dbrec.addField("负责人",rs.getString(8));
        dbrec.addField("参与总人数",rs.getString(9));
        dbrec.addField("理事人数",rs.getString(10));
        dbrec.addField("外宾人数",rs.getString(11));
        dbrec.addField("外办人数",rs.getString(12));
        dbL.add(dbrec);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return dbL;
  }
  /**
   * 查询结果，显示分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public static String profsysSelectActive(Connection dbConn,Map request,T9Project project,Date start,Date end) throws Exception{
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID where p.PROJ_TYPE = '" + project.getProjType() + "'";
    if (!T9Utility.isNullorEmpty(project.getProjNum())) {
      sql += " and p.PROJ_NUM like '%" +  T9DBUtility.escapeLike(project.getProjNum())+ "%' " + T9DBUtility.escapeLike();
    }
    if(!T9Utility.isNullorEmpty(project.getProjActiveType())) {
      sql = sql + " and p.PROJ_ACTIVE_TYPE = '" + project.getProjActiveType() + "'";
    }
    if(project.getProjStartTime() != null) {
      String str =  T9DBUtility.getDateFilter("p.PROJ_START_TIME", T9Utility.getDateTimeStr(project.getProjStartTime()), ">=");
      sql += " and " + str;
    }
    if(start != null) {
      String str =  T9DBUtility.getDateFilter("p.PROJ_START_TIME", T9Utility.getDateTimeStr(start), "<=");
      sql += " and " + str;
    }
    if(project.getBudgetId() > 0) {
      sql = sql + " and p.BUDGET_ID = '" + project.getBudgetId() + "'";
    }
    if (!T9Utility.isNullorEmpty(project.getProjLeader())) {
      sql += " and p.PROJ_LEADER = '" +  project.getProjLeader()+ "'";
    }
    
    if(project.getProjEndTime() != null) {
      String str =  T9DBUtility.getDateFilter("p.PROJ_END_TIME", T9Utility.getDateTimeStr(project.getProjEndTime()), ">=");
      sql += " and " + str;
    }
    if(end != null) {
      String str =  T9DBUtility.getDateFilter("p.PROJ_END_TIME", T9Utility.getDateTimeStr(end), "<=");
      sql += " and " + str;
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String queryProjectBySeqIds(Connection dbConn,Map request,String projId,String projType) throws Exception {
    if (T9Utility.isNullorEmpty(projId)) {
      projId = "0"; 
    }
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " where p.PROJ_TYPE='" + projType + "' and p.SEQ_ID in (" + projId + ")";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页_人员
   * @return
   * @throws Exception 
   */
  public static String queryProjectMem(Connection dbConn,Map request,String projType,String memNum,String memPosition
      ,String memName,String memSex,String unitNum,String unitName,String projMemType) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " ,project_mem pm "
      + " where pm.proj_id = p.seq_id and p.PROJ_TYPE='" + projType + "'"
      + " and  pm.PROJ_MEM_TYPE='" + projMemType+ "' ";
      if (!T9Utility.isNullorEmpty(memNum)) {
        sql += " and pm.MEM_NUM like '%" +  T9Utility.encodeLike(memNum) + "%' " + T9DBUtility.escapeLike();
      }
      
      if (!T9Utility.isNullorEmpty(memPosition)) {
        sql += " and " + T9DBUtility.findInSet(memPosition, "pm.MEM_POSITION");
      }
      if (!T9Utility.isNullorEmpty(memName)) {
        sql += " and pm.MEM_NAME like '%" + T9Utility.encodeLike(memName) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(unitNum)) {
        sql += " and pm.UNIT_NUM like '%" + T9Utility.encodeLike(unitNum) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(memSex)) {
        sql += " and pm.MEM_SEX='" + memSex + "'";
      }
      if (!T9Utility.isNullorEmpty(unitName)) {
        sql += " and pm.UNIT_NAME like '%" + T9Utility.encodeLike(unitName) + "%' " + T9DBUtility.escapeLike();
      }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页_纪要
   * @return
   * @throws Exception 
   */
  public static String queryProjectComm(Connection dbConn,Map request,String projType,String commNum,String commMemCn,
      String commMemFn,String commName,String commTime,String commPlace,String projCommType) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " ,PROJECT_COMM pc where pc.SEQ_ID = p.PROJ_ID and p.PROJ_TYPE='" + projType + "'"
      +  " and pc.PROJ_COMM_TYPE='" + projCommType + "' ";
      if (!T9Utility.isNullorEmpty(commNum)) {
        sql += " and pc.COMM_NUM like '%" +  T9Utility.encodeLike(commNum) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(commMemCn)) {
        sql += " and pc.COMM_MEM_CN like '%" +  T9Utility.encodeLike(commMemCn) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(commMemFn)) {
        sql += " and pc.COMM_MEM_FN like '%" +  T9Utility.encodeLike(commMemFn) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(commName)) {
        sql += " and pc.COMM_NAME like '%" +  T9Utility.encodeLike(commName) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(commPlace)) {
        sql += " and pc.COMM_PLACE like '%" +  T9Utility.encodeLike(commPlace) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(commTime)) {
        String str =  T9DBUtility.getDateFilter("pc.COMM_TIME", commTime, "=");
        sql += " and " + str;
      }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据,通用列表显示数据,实现分页_日程
   * @return
   * @throws Exception 
   */
  public static String queryProjectCalendar(Connection dbConn,Map request,String projType,String activeType,String activeContent,String activeLeader,
      String activePartner,String startTime,String startTime1,String endTime,String endTime1,String projCalendarType) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " ,PROJECT_CALENDAR pc "
      + " where pc.proj_id = p.seq_id and p.PROJ_TYPE='" + projType + "'"
    + " and pc.PROJ_CALENDAR_TYPE='" + projCalendarType + "'";
    if (!T9Utility.isNullorEmpty(activeType)) {
      sql += " and pc.ACTIVE_TYPE='" + activeType + "'";
    }
    if (!T9Utility.isNullorEmpty(activeContent)) {
      sql += " and pc.ACTIVE_CONTENT like '%" +  T9Utility.encodeLike(activeContent) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(activeLeader)) {
      sql += " and pc.ACTIVE_LEADER = '" +  activeLeader + "'";
    }
    if (!T9Utility.isNullorEmpty(activePartner)) {
      sql += " and pc.ACTIVE_PARTNER like '%" +  T9Utility.encodeLike(activePartner) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(startTime)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("pc.START_TIME", startTime, ">=");
    }
    if (!T9Utility.isNullorEmpty(startTime1)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("pc.START_TIME", startTime1, "<=");
    }
    if (!T9Utility.isNullorEmpty(endTime)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("pc.END_TIME", endTime, ">=");
    }
    if (!T9Utility.isNullorEmpty(endTime1)) {
      sql = sql + " and " + T9DBUtility.getDateFilter("pc.END_TIME", endTime1, "<=");
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页_人员
   * @return
   * @throws Exception 
   */
  public static String queryProjectFile(Connection dbConn,Map request,String projType,String fileNum
      ,String fileName,String fileType,String projCreator,String fileTitle,String projFileType) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,ba.BUDGET_ITEM,p.DEPT_ID,dep.DEPT_NAME"
      + ",pn.USER_NAME,ci.CLASS_DESC,p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX"
      + ",p.P_TOTAL,p.PRINT_STATUS,p.PROJ_ACTIVE_TYPE,p.BUDGET_ID,p.PROJ_TYPE,p.PROJ_LEADER"
      + " from PROJECT p left outer join DEPARTMENT dep on p.DEPT_ID = dep.SEQ_ID"
      + " left outer join CODE_ITEM ci on p.PROJ_ACTIVE_TYPE = ci.SEQ_ID "
      + " left outer join BUDGET_APPLY ba on p.BUDGET_ID = ba.SEQ_ID "
      + " left outer join PERSON pn on p.PROJ_LEADER = pn.SEQ_ID "
      + " ,project_file pf "
      + " where pf.proj_id = p.seq_id and p.PROJ_TYPE='" + projType + "'"
      + " and  pf.PROJ_FILE_TYPE='" + projFileType+ "' ";
      if (!T9Utility.isNullorEmpty(fileNum)) {
        sql += " and pf.FILE_NUM like '%" +  T9Utility.encodeLike(fileNum) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(fileName)) {
        sql += " and pf.FILE_NAME like '%" +  T9Utility.encodeLike(fileName) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(fileType)) {
        sql += " and pf.FILE_TYPE like '%" +  T9Utility.encodeLike(fileType) + "%' " + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(projCreator)) {
        sql += " and pf.PROJ_CREATOR = '" + projCreator + "'";
      }
      if (!T9Utility.isNullorEmpty(fileTitle)) {
        sql += " and pf.ILE_TITLE like '%" +  T9Utility.encodeLike(fileTitle) + "%' " + T9DBUtility.escapeLike();
      }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
}
