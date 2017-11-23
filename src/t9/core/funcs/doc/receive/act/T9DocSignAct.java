package t9.core.funcs.doc.receive.act;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.doc.receive.data.T9DocConst;
import t9.core.funcs.doc.receive.data.T9DocReceive;
import t9.core.funcs.doc.receive.logic.T9DocReceiveLogic;
import t9.core.funcs.doc.receive.logic.T9DocReceiveRegLogic;
import t9.core.funcs.doc.receive.logic.T9DocSignLogic;
import t9.core.funcs.doc.receive.logic.T9DocSmsLogic;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
public class T9DocSignAct{
  /**
   * 取得有权限的收文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRegList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      PrintWriter pw = response.getWriter();
      T9DocSignLogic doc = new T9DocSignLogic();
      String isSign = request.getParameter("isSign");
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = doc.getSendMesage(user, dbConn, request.getParameterMap(), request.getRealPath("/") , isSign);
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 取得有权限的收文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRegListDesktop(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocSignLogic doc = new T9DocSignLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = doc.getSendMesageDesktop(user, dbConn, request.getParameterMap(), request.getRealPath("/") );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String sign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9DocSignLogic doc = new T9DocSignLogic();
      doc.sign(dbConn ,seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String comeback(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9DocSignLogic doc = new T9DocSignLogic();
      doc.comeback(dbConn ,seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
