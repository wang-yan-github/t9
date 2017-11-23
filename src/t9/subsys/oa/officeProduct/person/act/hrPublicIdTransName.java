package t9.subsys.oa.officeProduct.person.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
