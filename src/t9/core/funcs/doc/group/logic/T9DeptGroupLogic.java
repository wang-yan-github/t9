package t9.core.funcs.doc.group.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9DeptGroupLogic {
  public void addDeptGroup(Connection conn , String groupName , String orderNo , String userStr) throws Exception {
    String sql = "INSERT INTO  DEPT_GROUP (GROUP_NAME ,ORDER_NO , DEPT_STR) VALUES (? , ? , ?) ";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, groupName);
      pstmt.setString(2, orderNo);
      pstmt.setString(3, userStr);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  public String getDeptGroup(Connection conn , int seqId) throws Exception {
    String sql = "select *  from DEPT_GROUP WHERE SEQ_ID =" + seqId;
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      if (rs.next()) {
        sb.append("groupName:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("GROUP_NAME")))).append("\"");
        sb.append(",orderNo:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ORDER_NO")))).append("\"");
        String dept = rs.getString("DEPT_STR");
        T9DeptLogic logic = new T9DeptLogic();
        String deptName = logic.getNameByIdStr(dept, conn);
        sb.append(",dept:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(dept))).append("\"");
        sb.append(",deptDesc:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(deptName))).append("\"");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    sb.append("}");
    return sb.toString();
  }
  public String getDeptGroups(Connection conn) throws Exception {
    String sql = "select *  from  DEPT_GROUP order by ORDER_NO ";
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int count = 0 ;
    T9DeptLogic logic = new T9DeptLogic();
    try {
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while (rs.next()) {
        sb.append("{seqId:" + rs.getInt("SEQ_ID"));
        sb.append(",groupName:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("GROUP_NAME")))).append("\"");
        sb.append(",orderNo:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(rs.getString("ORDER_NO")))).append("\"");
        String dept = rs.getString("DEPT_STR");
        String deptName = logic.getNameByIdStr(dept, conn);
        sb.append(",dept:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(dept))).append("\"");
        sb.append(",deptDesc:\"").append(T9Utility.encodeSpecial(T9Utility.null2Empty(deptName))).append("\"},");
        count++;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public void deleteDeptGroup(Connection conn, String seqId) throws Exception {
    String sql = "DELETE FROM DEPT_GROUP WHERE SEQ_ID IN(" + seqId + ")";
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
  public void updateDeptGroup(Connection conn, String groupName,
      String orderNo, String userStr, String seqId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "update DEPT_GROUP set GROUP_NAME=?, ORDER_NO=? , DEPT_STR=? where SEQ_ID =" + seqId;
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.setString(1, groupName);
      pstmt.setString(2, orderNo);
      pstmt.setString(3, userStr);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  public String getDeptByGroup(Connection conn, String groupId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "select *  from DEPT_GROUP WHERE SEQ_ID =" + groupId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String dept = "";
    try {
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      if (rs.next()) {
         dept = T9Utility.null2Empty(rs.getString("DEPT_STR"));
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    if (T9Utility.isNullorEmpty(dept)) {
      return "[]";
    }
    if (dept.endsWith(",")) {
      dept = dept.substring(0, dept.length() - 1);
    }
    String query = "select DEPT_NAME , DEPARTMENT.SEQ_ID FROM DEPARTMENT  WHERE SEQ_ID IN ("  + dept + ")";
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    PreparedStatement pstmt2 = null;
    ResultSet rs2 = null;
    int count = 0 ;
    try {
      pstmt2 = conn.prepareStatement(query);
      rs2 = pstmt2.executeQuery();
      while (rs2.next()) {
        sb.append("{");
        sb.append("deptName:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(rs2.getString("DEPT_NAME"))) + "\",");
        sb.append("deptId:'" + rs2.getInt("SEQ_ID") + "'");
        sb.append("},");
        count++;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt2, rs2, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
}
