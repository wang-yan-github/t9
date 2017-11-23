package t9.subsys.shtest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SealLogic {

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
	public static String getsealdate11(Connection conn ,String uid)
    throws Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs= null;
    String date = null;
    try
    {
      conn.setAutoCommit(false);
      String sSql = new String("Select SEAL_DATA from mobile_seal m where m.CREATE_USER = '"+uid+"'");
      pstmt = conn.prepareStatement(sSql);
      rs = pstmt.executeQuery();
      if(rs.next()){
    	  date = rs.getString("SEAL_DATA");
    	//  System.out.println("blob:"+date);
      }
      
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      try {
    	 if(rs != null){
    		rs.close();
    		rs = null;
    	 }
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
    return date;
  }
	public static int getmax(Connection conn )
    throws Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs= null;
    int date = 0;
    try
    {
      conn.setAutoCommit(false);
      String sSql = new String("Select max(seq_id) from seal_testdate");
      pstmt = conn.prepareStatement(sSql);
      rs = pstmt.executeQuery();
      if(rs.next()){
    	  date = rs.getInt(1);
    	//  System.out.println("blob:"+date);
      }
      
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    
    return date;
  }
	public static String getsealdate(Connection conn ,String uid)
    throws Exception
  {
    PreparedStatement pstmt = null;
    ResultSet rs= null;
    Blob sDate = null;
    InputStream is = null;
    String date = null;
    byte[] Buffer = new byte[4096];
    try
    {
      conn.setAutoCommit(false);
      String sSql = new String("Select SEAL_DATA from mobile_seal m where m.CREATE_USER = '"+uid+"'");
      pstmt = conn.prepareStatement(sSql);
      rs = pstmt.executeQuery();
      if(rs.next()){
    	  is = rs.getBinaryStream("SEAL_DATA");
      }
      
      ByteArrayInputStream msgContent =(ByteArrayInputStream) rs.getBinaryStream("SEAL_DATA");
      byte[] byte_data = new byte[msgContent.available()];
      msgContent.read(byte_data, 0,byte_data.length);
      date = new String(byte_data);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      try {
    	 if(rs != null){
    		rs.close();
    		rs = null;
    	 }
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
    return date;
  }
}
