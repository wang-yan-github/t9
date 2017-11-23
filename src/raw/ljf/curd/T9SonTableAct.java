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

public class T9SonTableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9SonTableAct");
  List<T9CodeItem> codeList = null;
  public String getSonTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 
    String classNo = request.getParameter("classNo");
    
    String queryStr = "select * from CODE_ITEM where CLASS_NO = " + classNo;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      T9CodeItem codeItem = null;
      codeList = new ArrayList<T9CodeItem>();
      
      while(rs.next()){
        codeItem = new T9CodeItem();
        codeItem.setSqlId(rs.getInt("SEQ_ID"));
        codeItem.setClassNo(rs.getString("CLASS_NO"));
        codeItem.setSortNo(rs.getString("SORT_NO"));
        codeItem.setClassDesc(rs.getString("CLASS_DESC"));
        codeItem.setClassCode(rs.getString("CLASS_CODE"));
        codeList.add(codeItem);
      }
  
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt, rs, log);
    }
    
    request.setAttribute("codeList", codeList);
    return "/raw/ljf/html/listchild.jsp";
  }
}
