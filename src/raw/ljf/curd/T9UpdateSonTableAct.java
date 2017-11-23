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

public class T9UpdateSonTableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9UpdateSonTableAct");
  
  public String updateSonTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String sqlId = request.getParameter("sqlId");
    Connection dbConn = null;
    PreparedStatement pstmt = null;
    
    String classNo = request.getParameter("classNo");
    String classCode = request.getParameter("classCode");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
       
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryStr = "update CODE_ITEM set CLASS_NO = ?, CLASS_CODE = ?, CLASS_DESC = ? ,SORT_NO = ? where SEQ_ID = ?";
      pstmt = dbConn.prepareStatement(queryStr);
 
      pstmt.setString(1, classNo);
      pstmt.setString(2, classCode);
      pstmt.setString(3, classDesc);
      pstmt.setString(4, sortNo);
      pstmt.setInt(5, Integer.parseInt(sqlId));
      
      pstmt.executeUpdate();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
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
