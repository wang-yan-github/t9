package t9.core.funcs.system.censorwords.logic;

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

import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.data.T9DsField;
import t9.core.data.T9DsTable;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.censorwords.data.T9CensorModule;
import t9.core.funcs.system.censorwords.data.T9CensorWords;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9CensorModuleLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public boolean existsCensorModule(Connection dbConn, String moduleCode)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM CENSOR_MODULE WHERE MODULE_CODE = '" + moduleCode
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

  public ArrayList<T9CensorModule> getCensorModule(Connection dbConn)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorModule censorModule = null;
    List list = new ArrayList();
    ArrayList<T9CensorModule> moduleList = new ArrayList<T9CensorModule>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, MODULE_CODE, USE_FLAG, CHECK_USER, SMS_REMIND, SMS2_REMIND, BANNED_HINT, MOD_HINT, FILTER_HINT from CENSOR_MODULE order by MODULE_CODE";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorModule = new T9CensorModule();
        censorModule.setSeqId(rs.getInt("SEQ_ID"));
        censorModule.setModuleCode(rs.getString("MODULE_CODE"));
        censorModule.setUseFlag(rs.getString("USE_FLAG"));
        censorModule.setCheckUser(rs.getString("CHECK_USER"));
        censorModule.setSmsRemind(rs.getString("SMS_REMIND"));
        censorModule.setSms2Remind(rs.getString("SMS2_REMIND"));
        censorModule.setBannedHint(rs.getString("BANNED_HINT"));
        censorModule.setModHint(rs.getString("MOD_HINT"));
        censorModule.setFilterHint(rs.getString("FILTER_HINT"));
        moduleList.add(censorModule);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return moduleList;
  }

  public void deleteAll(Connection dbConn, String seqId) throws Exception {

    String sql = "DELETE FROM CENSOR_MODULE WHERE SEQ_ID IN (" + seqId + ")";
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

  public void updateSingleWords(Connection dbConn, String find,
      String replacement) throws Exception {

    String sql = "update CENSOR_WORDS set REPLACEMENT='" + replacement
        + "' WHERE FIND='" + find + "'";
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

  public void deleteAllFast(Connection dbConn, int userId) throws Exception {
    String sql = "DELETE FROM CENSOR_WORDS WHERE USER_ID=" + userId;
    //System.out.println(sql);
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public void deleteAllWords(Connection dbConn) throws Exception {
    String sql = "DELETE FROM CENSOR_WORDS";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public String getUserId(Connection conn, String idStrs) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String userId = "";
    try {
      if(T9Utility.isNullorEmpty(idStrs)){
        userId = "" + ",";
      }else{
      String[] ids = idStrs.split(",");
      for (int i = 0; i < ids.length; i++) {
        stmt = conn.createStatement();
        String queryStr = "select USER_NAME from PERSON where SEQ_ID = "
            + Integer.parseInt(ids[i]);
        rs = stmt.executeQuery(queryStr);
        while (rs.next()) {
          userId += rs.getString("USER_NAME") + ",";
        }
      }
    }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return userId;
  }
}
