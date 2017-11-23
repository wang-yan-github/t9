package t9.core.funcs.demo.logic;

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

public class T9DeviceLogic {

	public String getDevicelList(Connection conn,Map request , String deviceType , String uid , String beginTime , String endTime) throws Exception{
	    String sql =  "select mobile_device.SEQ_ID, DEVICE_NAME,USER_NAME, DEVICE_INFO,DEVICE_TYPE ,SUBMIT_TIME" +
	                  " from mobile_device, PERSON where PERSON.SEQ_ID = mobile_device.UID ";
      
	    if (!T9Utility.isNullorEmpty(deviceType)) {
	      sql += " and DEVICE_TYPE = '" + deviceType + "'";
	    }
      
	    if (!T9Utility.isNullorEmpty(uid)) {
        sql += " and UID = '" + uid + "'";
      }
	    
	    if (!T9Utility.isNullorEmpty(beginTime)) {
        sql += " and " + T9DBUtility.getDateFilter("SUBMIT_TIME", beginTime, ">") ;
      }
	    
	    if (!T9Utility.isNullorEmpty(endTime)) {
        sql += " and " + T9DBUtility.getDateFilter("SUBMIT_TIME", endTime, "<") ;
      }
	    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
	    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
	    return pageDataList.toJson();
	  }
	public String getDeviceInfo(Connection conn , int seqId) throws Exception{
	    String result = "";
	    String sql = " select DEVICE_INFO from mobile_device where SEQ_ID ="+seqId;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    String toId = "";
	    try {
	      ps = conn.prepareStatement(sql);
	      //System.out.println(sql);
	      rs = ps.executeQuery();
	      while(rs.next()){
	        toId += rs.getString(1);
	        if(toId != null){
	          result = toId;
	        }
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, null);
	    }
	    return result;
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
	  
	  public void deleteDevice(Connection conn, String seqIds) throws Exception {
		    String sql = "DELETE FROM mobile_device WHERE SEQ_ID IN(" + seqIds + ")";
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
	  public static void updateSql(Connection conn ,String updateString)
	    throws Exception
	  {
	    PreparedStatement pstmt = null;
	    try
	    {
	      conn.setAutoCommit(false);
	      String sSql = new String("");
	      sSql = updateString;
	      pstmt = conn.prepareStatement(sSql);
	      pstmt.execute();
	      conn.commit();
	    }
	    catch (Exception ex)
	    {
	      ex.printStackTrace();
	    }
	    finally
	    {
	      try {
	        if (pstmt != null)
	        {
	          pstmt.close();
	          pstmt = null;
	        }
	       if(conn != null){
	    	   conn.close();
	    	   conn =null;
	       }
	      }
	      catch (Exception localException1)
	      {
	      }
	    }
	  }
	
}
