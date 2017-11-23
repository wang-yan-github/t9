package raw.ljf.curd;

import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9DeleteCodeItemAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9DeleteCodeItemAct");
  public String deleteCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sqlId = request.getParameter("sqlId");
    Connection dbConn = null;
    Statement stmt = null;
 
    String queryStr = "delete from CODE_ITEM where SEQ_ID= " + sqlId;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();

      stmt.executeUpdate(queryStr);
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, null, log);
    }
    
    return "/raw/ljf/html/listchild.jsp";
  }
}
