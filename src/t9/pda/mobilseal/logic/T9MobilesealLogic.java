package t9.pda.mobilseal.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import t9.core.util.db.T9DBUtility;

public class T9MobilesealLogic {

	
	
	
	/**
	 * 执行更新
	 * @param conn
	 * @param updateString
	 * @throws Exception
	 */
	public static boolean updateSql(Connection conn, String updateString)
	throws Exception {
	PreparedStatement pstmt = null;
	boolean isSuccess = false;
	try {
		conn.setAutoCommit(false);
		String sSql = new String("");
		sSql = updateString;
		pstmt = conn.prepareStatement(sSql);
		isSuccess = pstmt.execute();
		conn.commit();
	} catch (Exception ex) {
		ex.printStackTrace();
	} finally {
			try {
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
				}
			if (conn != null) {
				conn.close();
				conn = null;
				}
			} catch (Exception localException1) {
			}
		}
	return isSuccess;
	}
	/**
	 * 
	 * @param conn
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public String getSealByUid(Connection conn , String uid) throws Exception{
	    String result = "";
	    String sql = " select SEAL_DATA from mobile_seal where id like " + uid ;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      if(rs.next()){
	        String toId = rs.getString("SEAL_DATA");
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
	/**
	 * 判断 设备是否存在
	 * @param conn
	 * @param dName
	 * @return
	 * @throws Exception
	 */
	public static boolean devIsExit(Connection conn , String dName) throws Exception{
	    boolean result = false;
	    String sql = " select DEVICE_NAME from mobile_device where DEVICE_NAME = '" + dName+"'" ;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      if(rs.next()){
	    	  result = true;
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, null);
	    }
	    return result;
	  }
	public static boolean checkSign(Connection conn , int uid ,String md5) throws Exception{
	    boolean result = false;
	    String sql = " select md5 from sealdata where uid = '" + uid+"'" ;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      if(rs.next()){
	    	  String oldmd5  = rs.getString("md5");
	    	  if(oldmd5.equals(md5)){
	    		  result = true;
	    	  }
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, null);
	    }
	    return result;
	  }
	public static boolean checkUserisExit(Connection conn , int uid ) throws Exception{
	    boolean result = false;
	    String sql = " select seq_id from sealdata where uid = '" + uid+"'" ;
	    PreparedStatement ps = null;
	    ResultSet rs = null ;
	    
	    try {
	      ps = conn.prepareStatement(sql);
	      rs = ps.executeQuery();
	      if(rs.next()){
	    	  result = true;
	    	 
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(ps, rs, null);
	    }
	    return result;
	  }
}
