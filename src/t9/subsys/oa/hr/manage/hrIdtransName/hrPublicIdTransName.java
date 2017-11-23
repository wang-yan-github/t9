package t9.subsys.oa.hr.manage.hrIdtransName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class hrPublicIdTransName {
	 //id 转化为name
	 public static String getUserName(Connection dbConn, int seqId) throws Exception{ 
		 String sql = "select USER_NAME from person dr where dr.SEQ_ID=" + seqId; 
		 PreparedStatement ps = null; 
		 ResultSet rs = null; 
		 try{ 
				 ps = dbConn.prepareStatement(sql); 
				 rs = ps.executeQuery(); 
			 if(rs.next()){ 
			   return rs.getString("USER_NAME"); 
			 } 
		 } catch (Exception e){ 
		     throw e; 
		 }finally{ 
		     T9DBUtility.close(ps, null, null); 
		 } 
		     return null; 
	 }
	/**
	 * 获得码表id 对应的名称
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	 public static String getCodeUserName(Connection dbConn, String seqId) throws Exception{
	   if (T9Utility.isNullorEmpty(seqId)) {
	     return "";
	   }
		 String sql = "select CODE_NAME from HR_CODE where SEQ_ID='" + seqId + "'"; 
		 PreparedStatement ps = null; 
		 ResultSet rs = null; 
		 try{ 
				 ps = dbConn.prepareStatement(sql); 
				 rs = ps.executeQuery(); 
			 if(rs.next()){ 
			   return rs.getString("CODE_NAME"); 
			 } 
		 } catch (Exception e){ 
		     throw e; 
		 }finally{ 
		     T9DBUtility.close(ps, null, null); 
		 } 
		     return null; 
	 }
	
	 
}
