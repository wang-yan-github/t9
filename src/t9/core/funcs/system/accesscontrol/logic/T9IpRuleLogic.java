package t9.core.funcs.system.accesscontrol.logic;

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
import t9.core.funcs.system.accesscontrol.data.T9AccessControl;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.util.db.T9DBUtility;
public class T9IpRuleLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.act");

  public ArrayList<T9IpRule> getIpRule(Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9IpRule rule = null;
    List list = new ArrayList();
    ArrayList<T9IpRule> ruleList = new ArrayList<T9IpRule>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, BEGIN_IP, END_IP, TYPE, REMARK from IP_RULE";
      rs = stmt.executeQuery(sql);
      while (rs.next()) { 
        rule = new T9IpRule();
        rule.setSeqId(rs.getInt("SEQ_ID"));
        rule.setBeginIp(rs.getString("BEGIN_IP"));
        rule.setEndIp(rs.getString("END_IP"));
        rule.setType(rs.getString("TYPE"));
        rule.setRemark(rs.getString("REMARK"));
        ruleList.add(rule);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return ruleList;
  }
  public ArrayList<T9IpRule> getIpRule(Connection dbConn,String type) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9IpRule rule = null;
    List list = new ArrayList();
    ArrayList<T9IpRule> ruleList = new ArrayList<T9IpRule>();
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID, BEGIN_IP, END_IP, TYPE, REMARK from IP_RULE where TYPE = " + type;
      rs = stmt.executeQuery(sql);
      while (rs.next()) { 
        rule = new T9IpRule();
        rule.setSeqId(rs.getInt("SEQ_ID"));
        rule.setBeginIp(rs.getString("BEGIN_IP"));
        rule.setEndIp(rs.getString("END_IP"));
        rule.setType(rs.getString("TYPE"));
        rule.setRemark(rs.getString("REMARK"));
        ruleList.add(rule);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return ruleList;
  }
  
  /**
   * 删除所有IP规则
   * @param conn
   * @param seqIds
   * @throws Exception
   */
  
  public void deleteAll(Connection conn, String seqIds) throws Exception {
    String seqIdStr = seqIds.substring(0, seqIds.length() - 1);
    String sql = "DELETE FROM IP_RULE WHERE SEQ_ID IN(" + seqIdStr + ")";
    PreparedStatement pstmt = null;
    try {
      //System.out.println(sql);
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
}
