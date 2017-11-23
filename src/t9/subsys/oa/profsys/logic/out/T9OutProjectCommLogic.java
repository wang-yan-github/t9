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
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectComm;
public class T9OutProjectCommLogic {
  /**
   *添加数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void addProjectComm(Connection dbConn,T9ProjectComm comm) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn,comm);
  }
  /**
   *修改数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void updateProjectComm(Connection dbConn,T9ProjectComm comm) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn,comm);
  }
  /**
   * 分页列表
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static String toSearchData(Connection dbConn,Map request,String projId) throws Exception{
    String sql = "select SEQ_ID,COMM_NUM,COMM_NAME" 
      + ",COMM_MEM_CN,COMM_MEM_FN,COMM_TIME,COMM_PLACE,ATTACHMENT_ID,ATTACHMENT_NAME"
      + ",COMM_CONTENT,COMM_NOTE,PROJ_COMM_TYPE,PROJ_DATE,PROJ_ID from PROJECT_COMM "
      + " where PROJ_ID = " + projId  + " and PROJ_COMM_TYPE = '1'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 删除会议纪要
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static void deleteCommById(Connection dbConn,String seqId) throws Exception{
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn,T9ProjectComm.class,Integer.parseInt(seqId));
  }
  /**
   *删除
   * @return
   * @throws Exception
   */
  public static void delComm(Connection dbConn,int projId) throws Exception {
    String sql = "delete from project_comm where proj_id=? ";
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
  /**
   * 查询会议纪要
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static T9ProjectComm getCommById(Connection dbConn,String seqId) throws Exception{
    T9ORM orm = new T9ORM();
    T9ProjectComm comm = (T9ProjectComm)orm.loadObjSingle(dbConn,T9ProjectComm.class,Integer.parseInt(seqId));
    return comm;
  }

  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String profsysSelectComm(Connection dbConn,T9ProjectComm comm) throws Exception {
    String sql = "select PROJ_ID "
      + " FROM PROJECT_COMM WHERE PROJ_COMM_TYPE='" + comm.getProjCommType() + "' ";

    if (!T9Utility.isNullorEmpty(comm.getCommNum())) {
      sql += " and COMM_NUM like '%" +  T9Utility.encodeLike(comm.getCommNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommMemCn())) {
      sql += " and COMM_MEM_CN like '%" +  T9Utility.encodeLike(comm.getCommMemCn()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommMemFn())) {
      sql += " and COMM_MEM_FN like '%" +  T9Utility.encodeLike(comm.getCommMemFn()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommName())) {
      sql += " and COMM_NAME like '%" +  T9Utility.encodeLike(comm.getCommName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommPlace())) {
      sql += " and COMM_PLACE like '%" +  T9Utility.encodeLike(comm.getCommPlace()) + "%' " + T9DBUtility.escapeLike();
    }
    if (comm.getCommTime() != null) {
      String str =  T9DBUtility.getDateFilter("COMM_TIME", T9Utility.getDateTimeStr(comm.getCommTime()), "=");
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
  public static String profsysCommList(Connection dbConn,Map request,String projType,T9ProjectComm comm) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,b.BUDGET_ITEM,de.DEPT_NAME,code.class_desc,son.user_name,code2.class_desc" 
      + ",p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS,p.PROJ_TYPE"
      + ",p.PROJ_STATUS,p.PROJ_VISIT_TYPE,p.PROJ_LEADER,p.PROJ_ACTIVE_TYPE FROM project p"
      + " left outer join BUDGET_APPLY b on p.BUDGET_ID=b.SEQ_ID"
      + " left outer join department de on de.seq_id =p.DEPT_ID"
      + " left outer join CODE_ITEM code on code.seq_id =p.PROJ_VISIT_TYPE"
      + " left outer join person son on son.seq_id =p.PROJ_LEADER"
      + " left outer join CODE_ITEM code2 on code2.seq_id=p.PROJ_ACTIVE_TYPE,PROJECT_COMM pcom "
      + " where p.PROJ_TYPE='" + projType + "' and pcom.PROJ_ID = p.SEQ_ID"
      + " and pcom.PROJ_COMM_TYPE='" + comm.getProjCommType() + "' ";

    if (!T9Utility.isNullorEmpty(comm.getCommNum())) {
      sql += " and pcom.COMM_NUM like '%" +  T9Utility.encodeLike(comm.getCommNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommMemCn())) {
      sql += " and pcom.COMM_MEM_CN like '%" +  T9Utility.encodeLike(comm.getCommMemCn()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommMemFn())) {
      sql += " and pcom.COMM_MEM_FN like '%" +  T9Utility.encodeLike(comm.getCommMemFn()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommName())) {
      sql += " and pcom.COMM_NAME like '%" +  T9Utility.encodeLike(comm.getCommName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(comm.getCommPlace())) {
      sql += " and pcom.COMM_PLACE like '%" +  T9Utility.encodeLike(comm.getCommPlace()) + "%' " + T9DBUtility.escapeLike();
    }
    if (comm.getCommTime() != null) {
      String str =  T9DBUtility.getDateFilter("pcom.COMM_TIME", T9Utility.getDateTimeStr(comm.getCommTime()), "=");
      sql += " and " + str;
    }
    //p.PROJ_STATUS <> '1' and  
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
}
