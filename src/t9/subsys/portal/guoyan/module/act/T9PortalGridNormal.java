package t9.subsys.portal.guoyan.module.act;

import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.portal.guoyan.module.logic.T9PortalGridNormalLogic;

public class T9PortalGridNormal {
  /**
   * 加载列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadGridData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PortalGridNormalLogic lal = new T9PortalGridNormalLogic();
      String data = lal.loadGridDataLogic(dbConn, request.getParameterMap()).toString();
      PrintWriter pw = response.getWriter();
      pw.write(data);
      pw.flush();
    }catch(Exception ex) {
      throw ex;
    }
    return null;
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadOneData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String Id = request.getParameter("Id");
      String roleName = request.getParameter("ruleName");
      T9PortalGridNormalLogic lal = new T9PortalGridNormalLogic();
      String data = lal.loadOneData(dbConn,Integer.valueOf(Id),roleName).toString();
      if("".equals(data)){
        data = "\"\"";
      }
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
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String loadDataPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String pageIndex = request.getParameter("pageIndex");
      String roleName = request.getParameter("ruleName");
      T9PortalGridNormalLogic lal = new T9PortalGridNormalLogic();
      String data = lal.loadDataPage(dbConn, Integer.valueOf(pageIndex),roleName,request.getParameterMap()).toString();
      if("".equals(data)){
        data = "\"\"";
      }
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
