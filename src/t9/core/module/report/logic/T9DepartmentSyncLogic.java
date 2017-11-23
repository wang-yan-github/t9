package t9.core.module.report.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;

import raw.cy.db.generics.T9ORM;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.util.db.T9DBUtility;

public class T9DepartmentSyncLogic {
  public void editDepartment(T9Department o , Connection conn) throws Exception {
    String cols = "update DEPARTMENT set DEPT_NAME=?, TEL_NO=?, FAX_NO=?, DEPT_NO=?, DEPT_PARENT=?, MANAGER=?, LEADER1=?, LEADER2=?, DEPT_FUNC=?, DEPT_CODE=? where DEPT_ID = ?";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(11, o.getSeqId());
      stm.setString(1, o.getDeptName());
      stm.setString(2, o.getTelNo());
      stm.setString(3, o.getFaxNo());
      stm.setString(4, o.getDeptNo());
      stm.setInt(5, o.getDeptParent());
      stm.setString(6, o.getManager());
      stm.setString(7, o.getLeader1());
      stm.setString(8, o.getLeader2());
      stm.setString(9, o.getDeptFunc());
      stm.setString(10, o.getDeptCode());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void addDepartment(T9Department o , Connection conn) throws Exception {
    String cols = "insert into DEPARTMENT (DEPT_ID, DEPT_NAME, TEL_NO, FAX_NO, DEPT_NO, DEPT_PARENT, MANAGER, LEADER1, LEADER2, DEPT_FUNC, DEPT_CODE) values (?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.setInt(1, o.getSeqId());
      stm.setString(2, o.getDeptName());
      stm.setString(3, o.getTelNo());
      stm.setString(4, o.getFaxNo());
      stm.setString(5, o.getDeptNo());
      stm.setInt(6, o.getDeptParent());
      stm.setString(7, o.getManager());
      stm.setString(8, o.getLeader1());
      stm.setString(9, o.getLeader2());
      stm.setString(10, o.getDeptFunc());
      stm.setString(11, o.getDeptCode());
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void syncDepartment(Connection conn , Connection reportConn) throws Exception {
    T9DeptLogic logic = new T9DeptLogic();
    List list = logic.getDeptList(conn);
    this.delDepartment(reportConn);
    for (Object o : list) {
      T9Department u = (T9Department)o;
      this.addDepartment(u, reportConn);
    }
  }
  public void delDepartment(Connection conn) throws Exception {
    String cols = "delete from  DEPARTMENT";
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public void delDepartment(int seqId , Connection conn) throws Exception {
    String cols = "delete from  DEPARTMENT where DEPT_ID=" + seqId;
    PreparedStatement stm = null;
    try {
      stm = conn.prepareStatement(cols);
      stm.executeUpdate();
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
}
