package t9.core.funcs.demo.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;

import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9SealLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String getSealList(Connection conn,Map request) throws Exception{
	  String sql = "select mobile_seal.SEQ_ID,USER_NAME,SEAL_NAME,DEVICE_LIST,CREATE_TIME" + 
	      " from mobile_seal, PERSON where PERSON.SEQ_ID = mobile_seal.CREATE_USER ";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    for (int i = 0 ; i < pageDataList.getRecordCnt() ; i ++) {
      T9DbRecord r = pageDataList.getRecord(i);
      String id = (String)r.getValueByName("deviceList");
      String name = this.getDeviceName(conn, id);
      r.updateField("deviceList", name);
    }
    return pageDataList.toJson();
  }
  public String getDeviceName(Connection conn,String ids) throws Exception{
    if (T9Utility.isNullorEmpty(ids) ) return "";
    if (ids.endsWith(",")) ids = ids.substring(0, ids.length() -1);
    
    String sql = "select DEVICE_NAME" + 
        " from mobile_device where SEQ_ID in (" + ids + ")";
    String devices = "";
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      //System.out.println(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        devices += rs.getString("DEVICE_NAME") + ",";
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    
    return devices;
  }
  
  public String getSearchList(Connection conn,Map request, String sealName, String beginTime, String endTime) throws Exception{
	  String sql = "select mobile_seal.SEQ_ID,USER_NAME,SEAL_NAME,DEVICE_LIST,CREATE_TIME" + 
      " from mobile_seal, PERSON where PERSON.SEQ_ID = mobile_seal.CREATE_USER ";
    
    if(!T9Utility.isNullorEmpty(sealName)){ 
      sql = sql + " and SEAL_NAME like '%" + sealName + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(beginTime)){
      beginTime = T9DBUtility.getDateFilter("CREATE_TIME", beginTime, ">=");
      sql = sql+" and " + beginTime; 
    }else if(!T9Utility.isNullorEmpty(endTime)){
      endTime = T9DBUtility.getDateFilter("CREATE_TIME", endTime, "<=");
      sql = sql + " and " + endTime; 
    }
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    for (int i = 0 ; i < pageDataList.getRecordCnt() ; i ++) {
      T9DbRecord r = pageDataList.getRecord(i);
      String id = (String)r.getValueByName("deviceList");
      String name = this.getDeviceName(conn, id);
      r.updateField("deviceList", name);
    }
    return pageDataList.toJson();
  }
  
  /**
   * 取得用户名称
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  
  public String getUserNameLogic(Connection conn , String userId) throws Exception{
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID in (" + userId +")";
    PreparedStatement ps = null;
    ResultSet rs = null ;
    String toId = "";
    try {
      ps = conn.prepareStatement(sql);
      //System.out.println(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        toId += rs.getString("USER_NAME") + ",";
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
   * 删除印章权限管理数据
   * @param conn
   * @param seqIds   PERSON表中的SEQ_ID串（以逗号为分隔）
   * @throws Exception
   */
  
  public void deleteSeal(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM mobile_seal WHERE SEQ_ID IN (" + seqIds + ")";
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
  
  public String getCounterLogic(Connection conn , String SEAL_ID) throws Exception{
    String result = "";
    String sql = " select max(SEAL_ID) from SEAL where SEAL_ID like '" + SEAL_ID + "%'";
    //System.out.println(sql);
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      //System.out.println(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        String toId = rs.getString(1);
        //System.out.println(toId);
        if(!T9Utility.isNullorEmpty(toId)){
          int maxNum = Integer.parseInt(toId.substring(toId.length()-4, toId.length()))+1;
          String maxStr = String.valueOf(maxNum);
          //System.out.println(maxStr.length());
          if(maxStr.length() == 1){
            result = "000" + maxStr;
            //System.out.println(result+"KLKLK");
          }else if(maxStr.length() == 2){
            result = "00" + maxStr;
            //System.out.println(result+"CC");
          }else if(maxStr.length() == 3){
            result = "0" + maxStr;
          }else if(maxStr.length() == 4){
            result = maxStr;
          }
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    if(T9Utility.isNullorEmpty(result)){
      result = "0001";
    }
    return result;
  }
  
  public String getShowInfo(Connection conn , int seqId) throws Exception{
    String result = "";
    String sql = " select SEAL_DATA from mobile_seal where SEQ_ID ="+seqId;
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
      T9DBUtility.close(ps, rs, log);
    }
    result = result.replaceAll("\n\r","");
    return result;
  }
  
  public String getSealId(Connection conn , int seqId) throws Exception{
    String result = "";
    String sql = " select SEAL_ID from SEAL where SEQ_ID = " + seqId ;
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  public String getdevicelist(Connection conn , int seqId) throws Exception{
	    String result = "";
	    String sql = " select DEVICE_LIST from mobile_seal where SEQ_ID ="+seqId;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      if (rs.next()){
	        result = rs.getString(1);
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, log);
	    }
	    if (T9Utility.isNullorEmpty(result)) return "{user:\"\",userDesc:\"\"}";
	    result = T9WorkFlowUtility.getOutOfTail(result);
	    String user = "";
	    String userDesc = "";
      
      sql = " select SEQ_ID  , DEVICE_NAME from mobile_device where SEQ_ID IN ("+result + ")";
      try {
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()){
          user += rs.getInt("SEQ_ID") + ",";
          userDesc += T9Utility.null2Empty(rs.getString("DEVICE_NAME")) + ",";
        }
      } catch (Exception e) {
        throw e;
      } finally {
        T9DBUtility.close(ps, rs, log);
      }
	    return "{user:\""+user+"\",userDesc:\""+T9Utility.encodeSpecial(userDesc)+"\"}";
	  }
}
