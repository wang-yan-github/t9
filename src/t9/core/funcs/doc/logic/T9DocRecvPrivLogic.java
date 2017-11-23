package t9.core.funcs.doc.logic;

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

public class T9DocRecvPrivLogic {

  public String getAllPriv(Connection conn) throws Exception{
    String data = "";
    String sql = " select SEQ_ID, DEPT_ID, USER_ID from doc_recv_priv where DEPT_ID = '-1' ";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String seqId = rs.getString("SEQ_ID");
        String userId = rs.getString("USER_ID");
        data = "{seqId:" + seqId + ",userId:\"" + userId + "\"}";
      }
      else{
        data = "{userId:0}";
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return data;
  }
  
  public void addAllPriv(Connection conn, String privIdStr) throws Exception{
    
    String isHaveSql = " select 1 from doc_recv_priv where DEPT_ID = '-1' ";
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(isHaveSql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String sql = " update doc_recv_priv set user_id = '"+privIdStr+"' where DEPT_ID = '-1' ";
        PreparedStatement ps1 = null;
        ps1 = conn.prepareStatement(sql);
        ps1.executeUpdate();
      }
      else{
        String sql = " insert into doc_recv_priv(DEPT_ID, USER_ID) values('"+-1+"','"+privIdStr+"') ";
        PreparedStatement ps1 = null;
        ps1 = conn.prepareStatement(sql);
        ps1.executeUpdate();
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  public void addDeptPriv(Connection conn, String deptIdStr, String userIdStr) throws Exception{
    
    String[] deptStr = deptIdStr.split(",");
    for(String deptId : deptStr){
      String isHaveSql = " select 1 from doc_recv_priv where DEPT_ID = '"+deptId+"' ";
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        ps = conn.prepareStatement(isHaveSql);
        rs = ps.executeQuery();
        if (rs.next()) {
          String sql = " update doc_recv_priv set user_id = '"+userIdStr+"' where DEPT_ID = '"+deptId+"' ";
          PreparedStatement ps1 = null;
          ps1 = conn.prepareStatement(sql);
          ps1.executeUpdate();
        }
        else{
          String sql = " insert into doc_recv_priv(DEPT_ID, USER_ID) values('"+deptId+"','"+userIdStr+"') ";
          PreparedStatement ps1 = null;
          ps1 = conn.prepareStatement(sql);
          ps1.executeUpdate();
        }
      }catch (Exception e) {
        throw e;
      } finally {
        T9DBUtility.close(ps, rs, null);
      }
    }
  }
  
  public void deleteAllPriv(Connection conn, String seqId) throws Exception{
    String sql = "";
    if("0".equals(seqId)){
      sql = " delete from doc_recv_priv where DEPT_ID = -1 ";
    }
    else{
      sql = " delete from doc_recv_priv where SEQ_ID ="+seqId;
    }
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
  
  public String getDeptPrivListJson(Connection dbConn, Map request) throws Exception {
    try {
      String sql = " select d1.SEQ_ID, d1.DEPT_ID, d2.DEPT_NAME, d1.USER_ID USER_NAME, d1.USER_ID from doc_recv_priv d1 " 
                 + " join department d2 on d1.DEPT_ID = d2.SEQ_ID "
      		       + " where d1.DEPT_ID != -1 ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public String getPrivNameLogic(Connection conn, String privIdStr) throws Exception {
    if (T9Utility.isNullorEmpty(privIdStr)) {
      privIdStr = "-1";
    }
    if (privIdStr.endsWith(",")) {
      privIdStr = privIdStr.substring(0, privIdStr.length() - 1);
    }
    String result = "";
    String sql = " select PRIV_NAME from user_priv where SEQ_ID IN (" + privIdStr + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String toId = rs.getString(1);
        if (!"".equals(result)) {
          result += ",";
        }
        result += toId;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
}
