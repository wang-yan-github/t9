package t9.subsys.oa.officeProduct.report.act;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.subsys.oa.officeProduct.report.logic.T9ReportAnalysisLogic;

public class T9ReportAnalysisAct {
  private T9ReportAnalysisLogic logic = new T9ReportAnalysisLogic();
  public static final String attachmentFolder = "hr";


  /**
   *办公用品报表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String getAnalysis(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String careDate1 = request.getParameter("careDate1");
      String careDate2 = request.getParameter("careDate2");
      String mapType = request.getParameter("mapType");
      String officeDepository = request.getParameter("officeDepository");
      String officeProtype = request.getParameter("officeProtype");
      String product = request.getParameter("product");
      String module = request.getParameter("module");
      String deptId = request.getParameter("deptId");
      
      Map map = new HashMap();
      map.put("careDate1", careDate1);
      map.put("careDate2", careDate2);
      map.put("officeDepository", officeDepository);
      map.put("officeProtype", officeProtype);
      map.put("product", product);
      map.put("module", module);
      map.put("mapType", mapType);
      map.put("deptId", deptId);
      
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getAnalysis(dbConn, map, request.getParameterMap(), person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询");
      
      if(T9Utility.isNullorEmpty(mapType)){
        if(module.equals("OFFICE_LYWP")){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询");
          request.setAttribute(T9ActionKeys.RET_DATA, data);
          return "/core/inc/rtjson.jsp";
        }
        PrintWriter pw = response.getWriter();
        pw.println(data);
        pw.flush();
        return null;
      }
      else{
        data = "{\"data\":\""+data+"\"}";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
        return "/core/inc/rtjson.jsp";
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  
  
  /**
   *导出cvs
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String printExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String careDate1 = request.getParameter("careDate1");
      String careDate2 = request.getParameter("careDate2");
      String mapType = request.getParameter("mapType");
      String officeDepository = request.getParameter("officeDepository");
      String officeProtype = request.getParameter("officeProtype");
      String product = request.getParameter("product");
      String module = request.getParameter("module");
      String deptId = request.getParameter("deptId");
      String title = request.getParameter("title");
      
      Map map = new HashMap();
      map.put("careDate1", careDate1);
      map.put("careDate2", careDate2);
      map.put("officeDepository", officeDepository);
      map.put("officeProtype", officeProtype);
      map.put("product", product);
      map.put("module", module);
      map.put("mapType", mapType);
      map.put("deptId", deptId);
      map.put("title", title);
      
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      ArrayList<T9DbRecord> dbl  = this.logic.printExcel(dbConn, map, request.getParameterMap(), person);
      
      String fileName = URLEncoder.encode("报表.xls", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
      
      ops = response.getOutputStream();
      T9JExcelUtil.writeExc(ops, dbl);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally{
      ops.close();
    }
    return null;
  }

  
  /**
   * 所属部门下拉框
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOfficeDepository(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

      String data = "";
      data = this.logic.getOfficeDepository(dbConn);
      if(T9Utility.isNullorEmpty(data)){
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

}
