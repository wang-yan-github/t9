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
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectComm;

public class T9ProjectCommLogic {
  /**
   * 分页列表
   * @param dbConn
   * @param request
   * @return
   * @throws Exception
   */
  public static String toSearchData(Connection dbConn,Map request,String projId,String projCommType) throws Exception{
    String sql = "select SEQ_ID,COMM_NUM,COMM_NAME" 
      + ",COMM_MEM_CN,COMM_MEM_FN,COMM_TIME,COMM_PLACE,ATTACHMENT_ID,ATTACHMENT_NAME"
      + ",COMM_CONTENT,COMM_NOTE,PROJ_COMM_TYPE,PROJ_DATE,PROJ_ID from PROJECT_COMM "
      + " where PROJ_ID = " + projId  + " and PROJ_COMM_TYPE = '"+projCommType+"'" ;
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
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
  public static String queryCommToProjId(Connection dbConn,String commNum,String commMemCn,String commMemFn,String commName,String commTime,String commPlace,String projCommType) throws Exception {
    String sql = "select PROJ_ID FROM PROJECT_COMM WHERE PROJ_COMM_TYPE='" + projCommType + "' ";

    if (!T9Utility.isNullorEmpty(commNum)) {
      sql += " and COMM_NUM like '%" +  T9Utility.encodeLike(commNum) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(commMemCn)) {
      sql += " and COMM_MEM_CN like '%" +  T9Utility.encodeLike(commMemCn) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(commMemFn)) {
      sql += " and COMM_MEM_FN like '%" +  T9Utility.encodeLike(commMemFn) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(commName)) {
      sql += " and COMM_NAME like '%" +  T9Utility.encodeLike(commName) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(commPlace)) {
      sql += " and COMM_PLACE like '%" +  T9Utility.encodeLike(commPlace) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(commTime)) {
      String str =  T9DBUtility.getDateFilter("COMM_TIME", commTime, "=");
      sql += " and " + str;
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
