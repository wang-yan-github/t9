package t9.core.module.oa.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.util.T9Utility;

public class T9UnitAct {
  //http://localhost:88/t9/t9/core/module/oa/act/T9UnitAct/createUnit.act
  public String createUnit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9OaSyncLogic reportSysc = new T9OaSyncLogic();
      Connection conn = T9OaSyncLogic.getOAConn();
      reportSysc.createUnit(conn);
      if (conn!=null) {
        conn.close();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
//http://localhost:88/t9/t9/core/module/oa/act/T9UnitAct/syncUnit.act
  public String syncUnit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String privNoFlagStr = request.getParameter("privNoFlag");
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9OaSyncLogic reportSysc = new T9OaSyncLogic();
      Connection conn = T9OaSyncLogic.getOAConn();
      reportSysc.unitSync(dbConn, conn);
      //reportSysc.createOaMenu(dbConn);
      if (conn!=null) {
        conn.close();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String createOaMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String privNoFlagStr = request.getParameter("privNoFlag");
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9OaSyncLogic reportSysc = new T9OaSyncLogic();
      Connection conn = T9OaSyncLogic.getOAConn();
      reportSysc.createOaMenu(dbConn , request.getRealPath("/"));
      if (conn!=null) {
        conn.close();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
