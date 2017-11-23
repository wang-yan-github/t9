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
import t9.subsys.oa.profsys.data.T9ProjectMem;

public class T9OutProjectMemLogic {
  /**
   * 分页列表
   * @param conn
   * @param request
   * @return
   * @throws Exception
   */
  public static String toSearchData(Connection conn,Map request,String projId) throws Exception{
    String sql = "select pm.SEQ_ID,pm.MEM_NUM,pm.MEM_POSITION,pm.MEM_NAME,pm.MEM_SEX,pm.MEM_BIRTH,pm.MEM_ID_NUM,"
      + "pm.MEM_PHONE,pm.MEM_MAIL,pm.MEM_FAX,pm.MEM_ADDRESS,pm.ATTACHMENT_ID,pm.ATTACHMENT_NAME"
      +" from PROJECT_MEM pm "
      +" where pm.PROJ_ID = " + projId  + " and pm.PROJ_MEM_TYPE = '1'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }

  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String profsysSelectMem(Connection dbConn,T9ProjectMem mem) throws Exception {
    String sql = "select PROJ_ID "
      + " from PROJECT_MEM where PROJ_MEM_TYPE ='" + mem.getProjMemType() + "'";
    if (!T9Utility.isNullorEmpty(mem.getMemNum())) {
      sql += " and MEM_NUM like '%" + T9Utility.encodeLike(mem.getMemNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemPosition())) {
      sql += " and " + T9DBUtility.findInSet(mem.getMemPosition(), "MEM_POSITION");
      //sql += " and MEM_POSITION in (" + mem.getMemPosition()+ ") ";
    }
    if (!T9Utility.isNullorEmpty(mem.getMemName())) {
      sql += " and MEM_NAME like '%" + T9Utility.encodeLike(mem.getMemName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemIdNum())) {
      sql += " and MEM_ID_NUM like '%" + T9Utility.encodeLike(mem.getMemIdNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemSex())) {
      sql += " and MEM_SEX='" + mem.getMemSex() + "'";
    }
    if (mem.getMemBirth() != null) {
      String str =  T9DBUtility.getDateFilter("MEM_BIRTH", T9Utility.getDateTimeStr(mem.getMemBirth()), "=");
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
    }catch (Exception e) {
      throw e;
    }finally {
      T9DBUtility.close(ps, rs, null);
    }
    return projId;
  }

  /***
   * 根据条件查询数据,通用列表显示数据,实现分页
   * @return
   * @throws Exception 
   */
  public static String profsysMemList(Connection dbConn,Map request,String projType,T9ProjectMem mem) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,b.BUDGET_ITEM,de.DEPT_NAME,code.class_desc,son.user_name,code2.class_desc" 
      + ",p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS,p.PROJ_TYPE"
      + ",p.PROJ_STATUS,p.PROJ_VISIT_TYPE,p.PROJ_LEADER,p.PROJ_ACTIVE_TYPE FROM project p"
      + " left outer join BUDGET_APPLY b on p.BUDGET_ID=b.SEQ_ID"
      + " left outer join department de on de.seq_id =p.DEPT_ID"
      + " left outer join CODE_ITEM code on code.seq_id =p.PROJ_VISIT_TYPE"
      + " left outer join person son on son.seq_id =p.PROJ_LEADER"
      + " left outer join CODE_ITEM code2 on code2.seq_id=p.PROJ_ACTIVE_TYPE,PROJECT_MEM pmem "
      + " where  p.PROJ_TYPE='" + projType + "' and pmem.PROJ_ID=p.SEQ_ID "
      + " and pmem.PROJ_MEM_TYPE ='" + mem.getProjMemType() + "'";
    if (!T9Utility.isNullorEmpty(mem.getMemNum())) {
      sql += " and pmem.MEM_NUM like '%" + T9Utility.encodeLike(mem.getMemNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemPosition())) {
      sql += " and " + T9DBUtility.findInSet(mem.getMemPosition(), "pmem.MEM_POSITION");
      //sql += " and MEM_POSITION in (" + mem.getMemPosition()+ ") ";
    }
    if (!T9Utility.isNullorEmpty(mem.getMemName())) {
      sql += " and pmem.MEM_NAME like '%" + T9Utility.encodeLike(mem.getMemName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemIdNum())) {
      sql += " and pmem.MEM_ID_NUM like '%" + T9Utility.encodeLike(mem.getMemIdNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(mem.getMemSex())) {
      sql += " and pmem.MEM_SEX='" + mem.getMemSex() + "'";
    }
    if (mem.getMemBirth() != null) {
      String str =  T9DBUtility.getDateFilter("pmem.MEM_BIRTH", T9Utility.getDateTimeStr(mem.getMemBirth()), "=");
      sql += " and " + str;
    }
    //p.PROJ_STATUS <> '1' and 
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /**
   *添加数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void addProjectMem(Connection dbConn,T9ProjectMem mem) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, mem);
  }

  /**
   * 修改项目
   * 
   * @return
   * @throws Exception
   */
  public static void updateProjectMem(Connection dbConn,T9ProjectMem mem) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn,mem);
  }

  //seqId串转换成NAME串
  public static String userName(Connection dbConn,String seqId) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String sql = "select priv_name from user_priv where seq_id in (" + seqId + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String name = "";
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        name += rs.getString("priv_name") + ",";
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
  /**
   *删除
   * @return
   * @throws Exception
   */
  public static void delMem(Connection dbConn,int projId) throws Exception {
    String sql = "delete from project_mem where proj_id=? ";
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
