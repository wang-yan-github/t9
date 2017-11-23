package test.core.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.exps.T9InvalidParamException;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.db.T9DBUtility;

public class T9TestLoginAct {
  private static Logger log = Logger.getLogger("yzq.test.core.act.T9TestLoginAct");
  /**
   * 处理登录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String doLogin(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    
    int seqId = 0;
    
    Connection dbConn = null;
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;  
    ResultSet rs2 = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      stmt = dbConn.createStatement();
      
      rs = stmt.executeQuery("");
      if (rs.next()) {
        //todo
        rs = stmt.executeQuery("");
      }
      
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    
//    Thread.sleep(5000);
//    T9Out.print(request.getParameter("name") + ">>" + request.getParameter("password"));
//    log.debug("doLogin");
//    PrintWriter pw = response.getWriter();
//    pw.println("{rtState:'3'}");
//    pw.flush();
    
//    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//    request.setAttribute(T9ActionKeys.RET_MSRG, "无效的用户名");
//    request.setAttribute(T9ActionKeys.RET_DATA, "{name:'yzq', pass: '1234'}");
    
    return "/core/inc/rtjson.jsp";
  }
}
