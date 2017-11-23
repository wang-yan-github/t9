package t9.core.funcs.system.sealmanage.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;

import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9SealLogLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String getSealList(Connection conn,Map request) throws Exception{
    String sql =  "select SEQ_ID" +
                  ",S_ID" +
                  ",LOG_TYPE" +
                  ",USER_ID" +
                  ",CLIENT_TYPE" +
                  ",LOG_TIME" +
                  ",RESULT" +
                  ",IP_ADD from SEAL_LOG";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }

  
  public String getSearchList(Connection conn,Map request, String logType, String sealName, String beginTime, String endTime, String userId) throws Exception{
    String whereStr = "";
    if(!T9Utility.isNullorEmpty(userId)){
      whereStr = " and SEAL_LOG.USER_ID = "+userId;
    }
    String sql = "select SEAL_LOG.SEQ_ID" +
                 ",SEAL_LOG.S_ID" +
                 ",SEAL_LOG.LOG_TYPE" +
                 ",SEAL_LOG.USER_ID" +
                 ",SEAL_LOG.CLIENT_TYPE" +
                 ",SEAL_LOG.LOG_TIME" +
                 ",SEAL_LOG.RESULT" +
                 ",SEAL_LOG.IP_ADD from SEAL,SEAL_LOG where SEAL.SEAL_ID = SEAL_LOG.S_ID"+whereStr;
    if(!T9Utility.isNullorEmpty(logType)){ 
      sql = sql + " and SEAL_LOG.LOG_TYPE like '%" + logType + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(sealName)){ 
      sql = sql + " and SEAL.SEAL_NAME like '%" + sealName + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(beginTime)){
      beginTime = T9DBUtility.getDateFilter("CREATE_TIME", beginTime, ">=");
      sql = sql + " and " + beginTime; 
    }else if(!T9Utility.isNullorEmpty(endTime)){
      endTime = T9DBUtility.getDateFilter("CREATE_TIME", endTime, "<=");
      sql = sql + " and " + endTime; 
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  public String getSealNameLogic(Connection dbConn , String sId) throws Exception{
    String result = "";
    String sql = " select SEAL_NAME from SEAL where SEAL_ID = '" + sId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
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
   * 取得用户名称(单个)
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  
  public String getUserNameLogic(Connection conn , int userId) throws Exception{
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID = " + userId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
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
   * 删除印章日志
   * @param conn
   * @param seqIds   PERSON表中的SEQ_ID串（以逗号为分隔）
   * @throws Exception
   */
  
  public void deleteSealLog(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM SEAL_LOG WHERE SEQ_ID IN(" + seqIds + ")";
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
}
