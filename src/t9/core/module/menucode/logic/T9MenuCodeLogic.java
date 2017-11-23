package t9.core.module.menucode.logic;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.email.data.T9EmailBox;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9MenuCodeLogic{

  /**
   * 取得FLOW_NAME
   * @param conn
   * @param seqId
   * @return
   * @throws Exception 
   */
  public String getFlowName(Connection dbConn ,int seqId) throws Exception{
    //SELECT  SEQ_ID FROM EMAIL_BODY WHERE SEQ_ID IN( SELECT BODY_ID FROM EMAIL WHERE BOX_ID = 0)
    String sql = " SELECT FLOW_NAME FROM FLOW_TYPE WHERE SEQ_ID=" + seqId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  public String getSortName(Connection dbConn ,int seqId) throws Exception{
    String sql = " SELECT SORT_NAME FROM FILE_SORT WHERE SEQ_ID=" + seqId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  public String getDiskName(Connection dbConn ,int seqId) throws Exception{
    String sql = " SELECT DISK_NAME FROM NETDISK WHERE SEQ_ID=" + seqId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  public String getPicName(Connection dbConn ,int seqId) throws Exception{
    String sql = " SELECT PIC_NAME FROM PICTURE WHERE SEQ_ID=" + seqId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
  
  public String getConfidentialFile(Connection dbConn ,int seqId) throws Exception{
    String sql = " SELECT SORT_NAME FROM CONFIDENTIAL_SORT WHERE SEQ_ID=" + seqId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return result;
  }
}
