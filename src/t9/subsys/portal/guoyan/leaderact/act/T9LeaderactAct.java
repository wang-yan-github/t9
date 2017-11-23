package t9.subsys.portal.guoyan.leaderact.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.portal.guoyan.leaderact.logic.T9LeaderactLogic;
/**
 * 领导活动
 * @author Think
 *
 */
public class T9LeaderactAct {
  /**
   * 取的领导活动数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listDataLimit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9LeaderactLogic lal = new T9LeaderactLogic();
      String limitstr = request.getParameter("pageSize");
      String data = lal.loadNew2(dbConn, Integer.valueOf(limitstr)).toString();
      PrintWriter pw = response.getWriter();
      pw.write(data);
      pw.flush();
    }catch(Exception ex) {
      
      throw ex;
    }
    return null;
  }
  
  /**
   * 取的领导活动数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listDataPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9LeaderactLogic lal = new T9LeaderactLogic();
      String pageSize = request.getParameter("pageSize");
      String pageIndex = request.getParameter("pageIndex");
      System.out.println(request.getParameterMap());
      String data = lal.loadNew(dbConn, Integer.valueOf(pageSize),Integer.valueOf(pageIndex)).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
  /**
   * 取的领导活动数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOneNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9LeaderactLogic lal = new T9LeaderactLogic();
      String newId = request.getParameter("newId");
      String data = lal.loadOneNew(dbConn, Integer.valueOf(newId)).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
