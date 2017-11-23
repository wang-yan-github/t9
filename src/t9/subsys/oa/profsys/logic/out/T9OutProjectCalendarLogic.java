package t9.subsys.oa.profsys.logic.out;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectCalendar;

public class T9OutProjectCalendarLogic {
  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String profsysSelectCalendar(Connection dbConn,T9ProjectCalendar calendar,String start,String start1,String end,String end1) throws Exception {
    String sql = "select PROJ_ID"
      + " from project_calendar"
      + " where PROJ_CALENDAR_TYPE='" + calendar.getProjCalendarType() + "'";
    if (!T9Utility.isNullorEmpty(calendar.getActiveType())) {
      sql += " and ACTIVE_TYPE='" + calendar.getActiveType() + "'";
    }
    if (!T9Utility.isNullorEmpty(calendar.getActiveContent())) {
      sql += " and ACTIVE_CONTENT like '%" +  T9Utility.encodeLike(calendar.getActiveContent()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(calendar.getActiveLeader())) {
      sql += " and ACTIVE_LEADER ='" +  calendar.getActiveLeader() + "'";
    }
    if (!T9Utility.isNullorEmpty(calendar.getActivePartner())) {
      sql += " and ACTIVE_PARTNER like '%" +  T9Utility.encodeLike(calendar.getActivePartner()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(start)) {
      String str =  T9DBUtility.getDateFilter("START_TIME",start, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(start1)) {
      String str =  T9DBUtility.getDateFilter("START_TIME", start1, "<=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(end)) {
      String str =  T9DBUtility.getDateFilter("END_TIME",end, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(end1)) {
      String str =  T9DBUtility.getDateFilter("END_TIME",end1, "<=");
      sql += " and " + str;
    }
    sql += " group by PROJ_ID ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String projId = "";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        projId += rs.getString("PROJ_ID") + ",";
      }
      if (!T9Utility.isNullorEmpty(projId)) {
        projId = projId.substring(0,projId.length() - 1);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps,rs,null);
    }
    return projId;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String profsysCalendarList(Connection dbConn,Map request,String projType,T9ProjectCalendar calendar,String start,String start1,String end,String end1) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,b.BUDGET_ITEM,de.DEPT_NAME,code.class_desc,son.user_name,code2.class_desc" 
      + ",p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS,p.PROJ_TYPE"
      + ",p.PROJ_STATUS,p.PROJ_VISIT_TYPE,p.PROJ_LEADER,p.PROJ_ACTIVE_TYPE FROM project p"
      + " left outer join BUDGET_APPLY b on p.BUDGET_ID = b.SEQ_ID"
      + " left outer join department de on de.seq_id = p.DEPT_ID"
      + " left outer join CODE_ITEM code on code.seq_id = p.PROJ_VISIT_TYPE"
      + " left outer join person son on son.seq_id = p.PROJ_LEADER"
      + " left outer join CODE_ITEM code2 on code2.seq_id = p.PROJ_ACTIVE_TYPE "
      + " ,project_calendar pca "
      + " where p.PROJ_TYPE='" + projType + "' and pca.PROJ_ID = p.SEQ_ID and "
      + " pca.PROJ_CALENDAR_TYPE='" + calendar.getProjCalendarType() + "'";
    if (!T9Utility.isNullorEmpty(calendar.getActiveType())) {
      sql += " and pca.ACTIVE_TYPE='" + calendar.getActiveType() + "'";
    }
    if (!T9Utility.isNullorEmpty(calendar.getActiveContent())) {
      sql += " and pca.ACTIVE_CONTENT like '%" +  T9Utility.encodeLike(calendar.getActiveContent()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(calendar.getActiveLeader())) {
      sql += " and pca.ACTIVE_LEADER ='" +  calendar.getActiveLeader() + "'";
    }
    if (!T9Utility.isNullorEmpty(calendar.getActivePartner())) {
      sql += " and pca.ACTIVE_PARTNER like '%" +  T9Utility.encodeLike(calendar.getActivePartner()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(start)) {
      String str =  T9DBUtility.getDateFilter("pca.START_TIME",start, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(start1)) {
      String str =  T9DBUtility.getDateFilter("pca.START_TIME", start1, "<=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(end)) {
      String str =  T9DBUtility.getDateFilter("pca.END_TIME",end, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(end1)) {
      String str =  T9DBUtility.getDateFilter("pca.END_TIME",end1, "<=");
      sql += " and " + str;
    }
    //System.out.println(sql);
    // p.PROJ_STATUS <> '1' and 
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   *删除
   * @return
   * @throws Exception
   */
  public static void delCalendar(Connection dbConn,int projId) throws Exception {
    String sql = "delete from project_calendar where proj_id=? ";
    PreparedStatement ps = null;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1,projId);
      ps.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(ps,null,null);
    }
  }
}
