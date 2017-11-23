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

public class T9AddTableAct {
  private static Logger log = Logger.getLogger("ljf.raw.ljf.T9AddTableAct");
  
  public String addTable(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    //classNo不能重复，对classNo进行判断
    String classNo = request.getParameter("classNo");
    String sortNo = request.getParameter("sortNo");
    String classDesc = request.getParameter("classDesc");
    String classLevel = request.getParameter("classLevel");

    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null; 

    PreparedStatement pstmt = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      String sql = "select count(*) from CODE_CLASS where CLASS_NO = '" + classNo + "'";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if(rs.next()){      
        count = rs.getLong(1);
      }
      if(count == 1) {
        System.out.println("classNo重复, classNo不能重复");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "classNo重复, classNo不能重复");
        return "/core/inc/rtjson.jsp";
      }
      
      String queryStr = "insert into CODE_CLASS (CLASS_NO, SORT_NO, CLASS_DESC, CLASS_LEVEL) values(?, ?, ?, ?)";
      pstmt = dbConn.prepareStatement(queryStr);
      pstmt.setString(1, classNo);
      pstmt.setString(2, sortNo);
      pstmt.setString(3, classDesc);
      pstmt.setString(4, classLevel);
      pstmt.executeUpdate();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "主分类添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        T9DBUtility.close(stmt,null,log);
    }    
    return "/core/inc/rtjson.jsp";
  }
}
