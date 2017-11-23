package raw.ljf.curd;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class T9UpdateTableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9UpdateTableAct");
  
  public String updateTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String classNofirst = request.getParameter("classNofirst"); 
    String classNo = request.getParameter("classNo");
    String classDesc = request.getParameter("classDesc");
    String sortNo = request.getParameter("sortNo");
    String classLevel = request.getParameter("classLevel");
    
    Connection dbConn = null;
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null; 

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(classNo.equals(classNofirst)) {
        String updateStr = "update CODE_CLASS set SORT_NO = ?, CLASS_DESC = ? , CLASS_LEVEL = ? where CLASS_NO = ?";
        pstmt = dbConn.prepareStatement(updateStr);
        pstmt.setString(1, sortNo);
        pstmt.setString(2, classDesc);
        pstmt.setString(3, classLevel);
        pstmt.setString(4, classNo);
        pstmt.executeUpdate();
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改主表成功！");  
        return "/core/inc/rtjson.jsp";
      }
        String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo + "'";
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        long count = 0;
        if(rs.next()){
          count = rs.getLong(1);
        }       
        if(count > 0) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "classNo重复, classNo不能重复");
          return "/core/inc/rtjson.jsp";
        }
      String sqlStr = "insert into CODE_CLASS (CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(sqlStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classDesc);
      pstmt.setString(3, sortNo);
      pstmt.setString(4, classLevel);
      pstmt.executeUpdate();
      System.out.println(sqlStr);
      
      String updateStr = "update CODE_ITEM set CLASS_NO = ? where CLASS_NO = ?";
      pstmt = dbConn.prepareStatement(updateStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, classNofirst);
      pstmt.executeUpdate();
          
      //原来的classNo   
      String deleteStr = "delete from CODE_CLASS where CLASS_NO = '" + classNofirst + "'";
      System.out.println(deleteStr);
      stmt.executeUpdate(deleteStr);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改主子表成功！");  
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
