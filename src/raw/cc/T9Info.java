package raw.cc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;

public class T9Info {
  /**
   * log
   */
  private static Logger log = Logger.getLogger("cc.t9.core.act.action.T9TestAct");
  
  
  public String testMethod(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    HttpSession session = request.getSession();
    T9DsDefAct im = new T9DsDefAct();
    String tableNo1 = request.getParameter("tableNo1");
    String tableName = request.getParameter("tableName");
    String tableDesc = request.getParameter("tableDesc");
    String categoryNo = request.getParameter("categoryNo");
    
    
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      //T9DBUtility dbUtil = new T9DBUtility();
      //dbConn = dbUtil.getConnection(false, "sampledb");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery("select * from customers");
      while (rs.next()) {
        T9Out.println("name>>" + rs.getString("name"));
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.closeDbConn(dbConn, log);
      T9DBUtility.close(stmt, rs, log);
    }
    return "core/1.jsp";
  }
}
