package t9.subsys.oa.profsys.logic;

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

public class T9ProjectFileLogic {
 
  /**
   * 分页列表
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static String toSearchData(Connection dbConn,Map request,String projId,String projFileType) throws Exception{
    String sql = "select SEQ_ID,FILE_NUM,FILE_NAME"
      + ",FILE_TYPE,FILE_CREATOR,FILE_TITLE,ATTACHMENT_ID,ATTACHMENT_NAME,FILE_CONTENT"
      + ",FILE_NOTE,PROJ_FILE_TYPE,PROJ_CREATOR,PROJ_DATE,PROJ_ID from project_file "
      + " where PROJ_ID = " + projId ;
    if(projFileType.equals("0")){
      sql = sql + " and (PROJ_FILE_TYPE = '0' or PROJ_FILE_TYPE is null)";
    }else{
      sql = sql + " and PROJ_FILE_TYPE = '" + projFileType + "'";
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据的projId
   * @return
   * @throws Exception 
   */
  public static String queryCommToProjId(Connection dbConn,String fileNum,String fileName,String fileType,String projCreator,String fileTitle,String projFileType) throws Exception {
    String sql = "select PROJ_ID from project_file WHERE PROJ_FILE_TYPE='" + projFileType+ "' ";

    if (!T9Utility.isNullorEmpty(fileNum)) {
      sql += " and FILE_NUM like '%" +  T9Utility.encodeLike(fileNum) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(fileName)) {
      sql += " and FILE_NAME like '%" +  T9Utility.encodeLike(fileName) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(fileType)) {
      sql += " and FILE_TYPE like '%" +  T9Utility.encodeLike(fileType) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(projCreator)) {
      sql += " and PROJ_CREATOR = '" + projCreator + "'";
    }
    if (!T9Utility.isNullorEmpty(fileTitle)) {
      sql += " and FILE_TITLE like '%" +  T9Utility.encodeLike(fileTitle) + "%' " + T9DBUtility.escapeLike();
    }
    sql = sql + " group by PROJ_ID";
    PreparedStatement ps = null;
    ResultSet rs = null;
    ps = dbConn.prepareStatement(sql);
    rs = ps.executeQuery();
    String projId = "";
    while (rs.next()) {
      if(!T9Utility.isNullorEmpty(rs.getString("PROJ_ID"))){
        projId += rs.getString("PROJ_ID") + ",";
      }
      
    }
    if (!T9Utility.isNullorEmpty(projId)) {
      projId = projId.substring(0,projId.length() - 1);
    }
    return projId;
  }
}
