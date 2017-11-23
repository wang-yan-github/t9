package t9.core.funcs.system.security.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.funcs.system.security.data.T9Security;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9SecurityLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public String getVerificationCode(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select PARA_VALUE from SYS_PARA WHERE PARA_NAME='VERIFICATION_CODE'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      String value = null;
      if (rs.next()) {
        value = rs.getString("PARA_VALUE");
        if (!T9Utility.isNullorEmpty(value)) {
          return value.trim();
        }
      }
      return "";
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public String getProp(Connection conn, String key) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select PARA_VALUE from SYS_PARA WHERE PARA_NAME='" + key + "'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      String value = null;
      if (rs.next()) {
        value = rs.getString("PARA_VALUE");
        if (!T9Utility.isNullorEmpty(value)) {
          return value.trim();
        }
      }
      return "";
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public T9Security get(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_INIT_PASS'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityPassFlag(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_PASS_FLAG'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityPassTime(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_PASS_TIME'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityPassMin(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_PASS_MIN'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityPassMax(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_PASS_MAX'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityPassSafe(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_PASS_SAFE'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityUserMen(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_USER_MEM'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityRetryBan(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_RETRY_BAN'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityRetryTimes(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_RETRY_TIMES'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityLoginKey(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='LOGIN_KEY'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityBenTime(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_BAN_TIME'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityKeyuser(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_KEY_USER'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  
  public T9Security getSecrityImModule(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='IM_MODULE'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  
  public T9Security getSecritySecureKey(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='LOGIN_SECURE_KEY'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityUseRtx(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_USE_RTX'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityShowIp(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_SHOW_IP'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityOnStatus(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_ON_STATUS'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityOcMark(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_OC_MARK'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityMarkDefault(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_OC_MARK_DEFAULT'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Security getSecrityOcRevision(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Security org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_OC_REVISION'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = new T9Security();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotify(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_TOP_DAYS'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public T9Diary getNotifyAE(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Diary org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA where PARA_NAME='NOTIFY_AUDITING_EXCEPTION'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        org = new T9Diary();
        org.setSeqId(rs.getInt("SEQ_ID"));
        org.setParaName(rs.getString("PARA_NAME"));
        org.setParaValue(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public void updateDiary(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where SEQ_ID=" + seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public boolean hasProp(Connection conn, String key) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "select count(1) as AMOUNT from SYS_PARA where PARA_NAME = '" + key + "'" ;
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        return rs.getInt("AMOUNT") > 0;
      }
      return false;
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public void addProp(Connection conn, String key, String value) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "insert into SYS_PARA (PARA_NAME, PARA_VALUE)" +
      		" values ('" + key + "','" + value + "')" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateProp(Connection conn, String key, String value) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + value + "' where PARA_NAME = '" + key + "'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateInitPass(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_INIT_PASS'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }

  public void updatePassFlag(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_PASS_FLAG'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updatePassTime(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_PASS_TIME'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updatePassMin(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_PASS_MIN'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updatePassMax(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_PASS_MAX'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updatePassSafe(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_PASS_SAFE'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateRetryBan(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_RETRY_BAN'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateRetryTimes(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_RETRY_TIMES'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateBanTime(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_BAN_TIME'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateUserMen(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_USER_MEM'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateLoginKey(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'LOGIN_KEY'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateKeyUser(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_KEY_USER'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateSecureKey(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'LOGIN_SECURE_KEY'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateUseRtx(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_USE_RTX'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateShowIp(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_SHOW_IP'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateOnStatus(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_ON_STATUS'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateOcMark(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_OC_MARK'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateOcMarkDefault(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_OC_MARK_DEFAULT'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateOcRevision(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'SEC_OC_REVISION'" ;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  
  public void updateImModule(Connection conn, String sumStr) throws Exception{
    Statement stmt = null;
    ResultSet rs=null;
    try {
      //判断
      String sql=" select * from SYS_PARA where PARA_NAME='IM_MODULE' ";
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next()){
        //更新
        stmt = conn.createStatement();
        String queryStr = "update SYS_PARA set PARA_VALUE='" + sumStr + "' where PARA_NAME = 'IM_MODULE'" ;
        stmt.executeUpdate(queryStr);
      }else{
        // 插入
        T9ORM orm =new T9ORM();
        T9SysPara sp= new T9SysPara();
        sp.setParaName("IM_MODULE");
        sp.setParaValue(sumStr);
        orm.saveSingle(conn, sp);
      
      }  
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }

  public void updateSecOcMarkDefault(Connection conn, String secOcMarkDefault) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      String query = "select 1 from SYS_PARA where PARA_NAME = 'SEC_OC_MARK_DEFAULT'" ;
      rs = stmt.executeQuery(query);
      String queryStr = "";
      if (rs.next()) {
         queryStr = "update SYS_PARA set PARA_VALUE='" + secOcMarkDefault + "' where PARA_NAME = 'SEC_OC_MARK_DEFAULT'" ;
      } else {
        queryStr = "insert into SYS_PARA (PARA_NAME,PARA_VALUE) values('SEC_OC_MARK_DEFAULT','" + secOcMarkDefault + "')" ;
      }
      stmt2 = conn.createStatement();
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
      T9DBUtility.close(stmt2, null, log);
    }
  }
}
