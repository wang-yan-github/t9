package t9.core.funcs.system.extuser.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.util.db.T9DBUtility;

public class T9ExtUserLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public boolean existsTableNo(Connection dbConn, String userId)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM EXT_USER WHERE USER_ID = '" + userId
          + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public long existsCount(Connection dbConn, int userId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM EXT_USER WHERE SYS_USER='0'";
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        count = rs.getLong(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return count;
  }

  public ArrayList<T9ExtUser> getExtUser(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9ExtUser extUser = null;
    List list = new ArrayList();
    ArrayList<T9ExtUser> extList = new ArrayList<T9ExtUser>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, USER_ID, PASSWORD, AUTH_MODULE, POSTFIX, USE_FLAG, SYS_USER, REMARK from EXT_USER WHERE SYS_USER='0' order by USER_ID";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        extUser = new T9ExtUser();
        extUser.setSeqId(rs.getInt("SEQ_ID"));
        extUser.setUserId(rs.getString("USER_ID"));
        extUser.setPassword(rs.getString("PASSWORD"));
        extUser.setAuthModule(rs.getString("AUTH_MODULE"));
        extUser.setPostfix(rs.getString("POSTFIX"));
        extUser.setUseFlag(rs.getString("USE_FLAG"));
        extUser.setRemark(rs.getString("REMARK"));
        extUser.setSysUser(rs.getString("SYS_USER"));
        extList.add(extUser);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return extList;
  }

  public void deleteAll(Connection dbConn, int seqId) throws Exception {
    String sql = "DELETE FROM EXT_USER WHERE SEQ_ID=" + seqId
        + " AND SYS_USER='0'";
    //System.out.println(sql);
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
}
