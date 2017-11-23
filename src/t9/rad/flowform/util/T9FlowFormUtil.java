package t9.rad.flowform.util;

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
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.global.T9BeanKeys;
import t9.core.util.db.T9DBUtility;
import t9.rad.flowform.data.T9FlowFormType;

public class T9FlowFormUtil {
  private static Logger log = Logger.getLogger("t9.core.funcs.person.act");
  
  public int deleteDeptMul(Connection dbConn, int seqId) {
    int deptName = 0;
    String name = "";
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT DEPT_PARENT FROM DEPARTMENT WHERE SEQ_ID = '" + seqId + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        name = rs.getString("DEPT_PARENT");
        deptName = Integer.parseInt(name);
      }
      if(deptName != 0){
        seqId = deleteDeptMul(dbConn,deptName);
      }
    } catch (Exception ex) {
      // throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return seqId;
  }
  
  public String deleteDept(Connection dbConn, int seqId) {
    int deptName = 0;
    String name = "";
    String str = "";
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT MANAGER FROM DEPARTMENT WHERE SEQ_ID = '" + seqId + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        name = rs.getString("MANAGER");
      }
    } catch (Exception ex) {
      // throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return name;
  }
  
}
