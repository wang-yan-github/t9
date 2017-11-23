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
import t9.subsys.oa.profsys.data.T9ProjectFile;

public class T9OutProjectFileLogic {
  /**
   *添加数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void addProjectFile(Connection dbConn,T9ProjectFile file) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn,file);
  }
  /**
   *查询数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static T9ProjectFile getFileById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9ProjectFile file = (T9ProjectFile)orm.loadObjSingle(dbConn, T9ProjectFile.class,Integer.parseInt(seqId));
    return file;
  }

  /**
   *查询数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void updateProjectFile(Connection dbConn,T9ProjectFile file) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, file);
  }
  /**
   * 分页列表
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static String toSearchData(Connection dbConn,Map request,String projId) throws Exception{
    String sql = "select p.SEQ_ID,p.FILE_NUM,p.FILE_NAME"
      + ",p.FILE_TYPE,son.USER_NAME,p.FILE_TITLE,p.ATTACHMENT_ID,p.ATTACHMENT_NAME,p.FILE_CONTENT"
      + ",p.FILE_NOTE,p.PROJ_FILE_TYPE,p.PROJ_CREATOR,p.PROJ_DATE,p.PROJ_ID from project_file p "
      + " left outer join person son on son.seq_id=p.PROJ_CREATOR"
      + " where PROJ_ID = " + projId  + " and PROJ_FILE_TYPE = '1'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  /**
   *删除数据 
   * @param dbConn
   * @throws Exception 
   * @throws Exception
   */
  public static void deleteFileById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9ProjectFile.class,Integer.parseInt(seqId));
  }
  /**
   *删除
   * @return
   * @throws Exception
   */
  public static void delFile(Connection dbConn,int projId) throws Exception {
    String sql = "delete from project_file where proj_id=? ";
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
  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String profsysSelectFile(Connection dbConn,T9ProjectFile file) throws Exception {
    String sql = "select PROJ_ID "
      + " FROM project_file WHERE PROJ_FILE_TYPE='" + file.getProjFileType()+ "' ";

    if (!T9Utility.isNullorEmpty(file.getFileNum())) {
      sql += " and FILE_NUM like '%" +  T9Utility.encodeLike(file.getFileNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getFileName())) {
      sql += " and FILE_NAME like '%" +  T9Utility.encodeLike(file.getFileName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getFileType())) {
      sql += " and FILE_TYPE like '%" +  T9Utility.encodeLike(file.getFileType()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getProjCreator())) {
      sql += " and PROJ_CREATOR ='" + file.getProjCreator() + "'";
    }
    if (!T9Utility.isNullorEmpty(file.getFileTitle())) {
      sql += " and FILE_TITLE like '%" +  T9Utility.encodeLike(file.getFileTitle()) + "%' " + T9DBUtility.escapeLike();
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
  public static String profsysFileList(Connection dbConn,Map request,String projType,T9ProjectFile file) throws Exception {
    String sql = "select p.SEQ_ID,p.PROJ_NUM,b.BUDGET_ITEM,de.DEPT_NAME,code.class_desc,son.user_name,code2.class_desc" 
      + ",p.PROJ_START_TIME,p.PROJ_END_TIME,p.P_YX,p.P_TOTAL,p.PRINT_STATUS,p.PROJ_TYPE"
      + ",p.PROJ_STATUS,p.PROJ_VISIT_TYPE,p.PROJ_LEADER,p.PROJ_ACTIVE_TYPE FROM project p"
      + " left outer join BUDGET_APPLY b on p.BUDGET_ID=b.SEQ_ID"
      + " left outer join department de on de.seq_id =p.DEPT_ID"
      + " left outer join CODE_ITEM code on code.seq_id =p.PROJ_VISIT_TYPE"
      + " left outer join person son on son.seq_id =p.PROJ_LEADER"
      + " left outer join CODE_ITEM code2 on code2.seq_id=p.PROJ_ACTIVE_TYPE,project_file pfi"
      + " where  p.PROJ_TYPE='" + projType + "' and pfi.PROJ_ID = p.SEQ_ID "
      + " and pfi.PROJ_FILE_TYPE='" + file.getProjFileType()+ "' ";
    if (!T9Utility.isNullorEmpty(file.getFileNum())) {
      sql += " and pfi.FILE_NUM like '%" +  T9Utility.encodeLike(file.getFileNum()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getFileName())) {
      sql += " and pfi.FILE_NAME like '%" +  T9Utility.encodeLike(file.getFileName()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getFileType())) {
      sql += " and pfi.FILE_TYPE like '%" +  T9Utility.encodeLike(file.getFileType()) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(file.getProjCreator())) {
      sql += " and pfi.PROJ_CREATOR ='" + file.getProjCreator() + "'";
    }
    if (!T9Utility.isNullorEmpty(file.getFileTitle())) {
      sql += " and pfi.FILE_TITLE like '%" +  T9Utility.encodeLike(file.getFileTitle()) + "%' " + T9DBUtility.escapeLike();
    }
    //p.PROJ_STATUS <> '1' and 
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
}
