package t9.core.funcs.system.remind.logic;

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
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.remind.data.T9Remind;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.util.db.T9DBUtility;

public class T9RemindLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");

  public T9Remind get(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9Remind org = null;
    try {
      String queryStr = "select SEQ_ID, PARA_NAME, PARA_VALUE from SYS_PARA WHERE PARA_NAME='SMS_REMIND'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      //System.out.println(queryStr);
      while (rs.next()) {
        org = new T9Remind();
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

  public void updateRemind(Connection conn, int seqId, String sumStr) throws Exception{
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String queryStr = "update SYS_PARA set PARA_VALUE='"+sumStr+"' where SEQ_ID="+seqId;
      stmt.executeUpdate(queryStr);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
}
