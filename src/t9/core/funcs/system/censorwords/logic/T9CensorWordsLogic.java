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
import t9.core.funcs.system.censorwords.data.T9CensorWords;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9CensorWordsLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public boolean existsCensorWords(Connection dbConn, String find)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM CENSOR_WORDS WHERE FIND = '" + find
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

  /**
   * 获取词语过滤过滤信息
   * @param dbConn
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorWords> getCensorWords(Connection dbConn)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9CensorWords censorWords = null;
    List list = new ArrayList();
    ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, USER_ID, FIND, REPLACEMENT from CENSOR_WORDS order by FIND";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorWords = new T9CensorWords();
        censorWords.setSeqId(rs.getInt("SEQ_ID"));
        censorWords.setUserId(rs.getInt("USER_ID"));
        censorWords.setFind(rs.getString("FIND"));
        censorWords.setReplacement(rs.getString("REPLACEMENT"));
        wordList.add(censorWords);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return wordList;
  }

  /**
   * 词语过滤查询
   * @param dbConn
   * @param userId
   * @param find
   * @param replacement
   * @return
   * @throws Exception
   */
  public ArrayList<T9CensorWords> getCensorWordsSearch(Connection dbConn,
      int userId, String find, String replacement) throws Exception {

    Statement stmt = null;
    ResultSet rs = null;
    T9CensorWords censorWords = null;
    List list = new ArrayList();
    ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
    ArrayList<T9CensorWords> result = new ArrayList<T9CensorWords>();
    try {
      stmt = dbConn.createStatement();

      String sql = "select SEQ_ID, USER_ID, FIND, REPLACEMENT from CENSOR_WORDS where USER_ID="
          + userId;
//      if (userId == 1) {
//        sql += " where USER_ID=" + userId;
//      }
      if (!T9Utility.isNullorEmpty(find)) {
        sql += " and FIND like '%" + find + "%'"  + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(replacement)) {
        sql += " and REPLACEMENT like '%" + replacement + "%'" + T9DBUtility.escapeLike();
      }
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorWords = new T9CensorWords();
        censorWords.setSeqId(rs.getInt("SEQ_ID"));
        censorWords.setUserId(rs.getInt("USER_ID"));
        censorWords.setFind(rs.getString("FIND"));
        censorWords.setReplacement(rs.getString("REPLACEMENT"));

        // 处理content，符合条件的加到wordlist里        wordList.add(censorWords);
      }
      int num = 200;
      if (wordList.size() > num) {
        result.addAll(wordList.subList(0, num));
      } else {
        result = wordList;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }

  public void deleteAll(Connection dbConn, String seqId) throws Exception {

    String sql = "DELETE FROM CENSOR_WORDS WHERE SEQ_ID IN(" + seqId + ")";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      //System.out.println(sql);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  /**
   * 词语过滤查询中的删除
   * @param dbConn
   * @param userId
   * @param find
   * @param replacement
   * @throws Exception
   */
  public void deleteSearch(Connection dbConn, int userId, String find,
      String replacement) throws Exception {
    String LOGIN_USER_PRIV = "1";
    String sql = "DELETE FROM CENSOR_WORDS WHERE 1=1";
    if (LOGIN_USER_PRIV != "1") {
      sql += " and USER_ID=" + userId;
    }
    if (find != "") {
      sql += " and FIND like '%" + find + "%'" + T9DBUtility.escapeLike();
    }
    if (replacement != "") {
      sql += " and REPLACEMENT like '%" + replacement + "%'" + T9DBUtility.escapeLike();
    }
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
  
  public ArrayList<T9CensorWords> getCensorWordsTxtList(Connection dbConn,
      int userId, String find, String replacement) throws Exception {

    Statement stmt = null;
    ResultSet rs = null;
    T9CensorWords censorWords = null;
    List list = new ArrayList();
    ArrayList<T9CensorWords> wordList = new ArrayList<T9CensorWords>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, USER_ID, FIND, REPLACEMENT from CENSOR_WORDS where USER_ID="
          + userId;
      if (!T9Utility.isNullorEmpty(find)) {
        sql += " and FIND like '%" + find + "%'" + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(replacement)) {
        sql += " and REPLACEMENT like '%" + replacement + "%'" + T9DBUtility.escapeLike();
      }
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        censorWords = new T9CensorWords();
        censorWords.setSeqId(rs.getInt("SEQ_ID"));
        censorWords.setUserId(rs.getInt("USER_ID"));
        censorWords.setFind(rs.getString("FIND"));
        censorWords.setReplacement(rs.getString("REPLACEMENT"));

        // 处理content，符合条件的加到wordlist里
        wordList.add(censorWords);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return wordList;
  }
}
