package t9.core.module.report.act;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9AuthKeys;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.exps.T9InvalidParamException;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.setdescktop.mypriv.logic.T9MyprivLogic;
import t9.core.funcs.system.ispirit.n12.org.act.T9IsPiritOrgAct;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9RegistProps;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.module.report.logic.T9PersonSyncLogic;
import t9.core.module.report.logic.T9ReportSyncLogic;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.auth.T9RegistUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9UnitAct {
  //http://localhost:88/t9/t9/core/module/report/act/T9UnitAct/createUnit.act
  public String createUnit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9ReportSyncLogic reportSysc = new T9ReportSyncLogic();
      Connection conn = T9ReportSyncLogic.getReportConn();
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
//http://localhost:88/t9/t9/core/module/report/act/T9UnitAct/syncUnit.act
  public String syncUnit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String privNoFlagStr = request.getParameter("privNoFlag");
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ReportSyncLogic reportSysc = new T9ReportSyncLogic();
      Connection conn = T9ReportSyncLogic.getReportConn();
      reportSysc.unitSync(dbConn, conn);
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
  public String createReportMenu(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ReportSyncLogic reportSysc = new T9ReportSyncLogic();
      reportSysc.createReportMenu(dbConn);
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
