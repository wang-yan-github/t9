package t9.core.funcs.doc.flowrunRec.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowSort;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.flowrunRec.logic.T9FlowWorkSearchLogic;
import t9.core.funcs.doc.logic.T9FlowSortLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.logic.T9FlowWorkAdSearchLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9WorkQueryAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.doc.flowrunRec.act.T9WorkQueryAct");
  public String getWorkList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String sSortId =  request.getParameter("sortId");
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      StringBuffer result = myWorkLogic.getWorkList(dbConn,request.getParameterMap(), loginUser , sSortId , request.getRealPath("/"));
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
