package raw.ljf.curd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9TableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9TableAct");
  List<T9CodeClass> codeList = null;
  public String getTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
 
    String queryStr = "select * from CODE_CLASS";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9CodeClass codeClass = null;
      codeList = new ArrayList<T9CodeClass>();
      
      if (rs.next()){
        codeClass = new T9CodeClass();
        codeClass.setSqlId(rs.getInt("SEQ_ID"));
        codeClass.setClassNo(rs.getString("CLASS_NO"));
        codeClass.setSortNo(rs.getString("SORT_NO"));
        codeClass.setClassDesc(rs.getString("CLASS_DESC"));
        codeClass.setClassLevel(rs.getString("CLASS_LEVEL"));
        codeList.add(codeClass);
      }
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    
    request.setAttribute("codeList", codeList);
    return "/raw/ljf/html/content.jsp";
  }
}
