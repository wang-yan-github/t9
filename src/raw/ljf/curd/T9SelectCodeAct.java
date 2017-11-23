package raw.ljf.curd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9SelectCodeAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9SelectCodeAct");
  public String selectCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sqlId = request.getParameter("sqlId");
    T9CodeClass codeClass = null;
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
 
    String queryStr = "select SEQ_ID, CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL from CODE_CLASS where SEQ_ID= " + sqlId;
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
     
      while(rs.next()){
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt(1));
        codeClass.setClassNo(rs.getString(2));
        codeClass.setSortNo(rs.getString(3));
        codeClass.setClassDesc(rs.getString(4));
        codeClass.setClassLevel(rs.getString(5));
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }

    request.setAttribute("codeClass", codeClass);
    return "/raw/ljf/html/update.jsp";
  }
}
