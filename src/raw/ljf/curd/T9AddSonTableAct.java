package raw.ljf.curd;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;

public class T9AddSonTableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9AddSonTableAct");
  
  public String addSonTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String classNo = request.getParameter("classNo");
    String classCode = request.getParameter("classCode");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
    
    Connection dbConn = null;
    PreparedStatement pstmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryStr = "insert into CODE_ITEM(CLASS_NO, CLASS_CODE, SORT_NO, CLASS_DESC) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(queryStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classCode);
      pstmt.setString(3, sortNo);
      pstmt.setString(4, classDesc);
      pstmt.executeUpdate();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "codeitem添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
      T9DBUtility.close(pstmt,null,log);
    }
    return "/core/inc/rtjson.jsp";
  }
}
