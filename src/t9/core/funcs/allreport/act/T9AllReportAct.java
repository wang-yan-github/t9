package t9.core.funcs.allreport.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.allreport.logic.T9AllReportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.logic.T9FlowSortLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;

public class T9AllReportAct {

  T9AllReportLogic logic =new T9AllReportLogic();
  /**
   * 主要是流程管理时，右边的滑动菜单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMenuList(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
  
    try{
      String actionUrl = request.getContextPath() + "/t9/core/funcs/allreport/act/T9AllReportAct/getReportsById.act?sortId=";
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9FlowSortLogic fs = new T9FlowSortLogic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      data=this.logic.getMenuList(dbConn,actionUrl);
      data="["+data+"]";
      
      
     // System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 根据id获取报表
   * 
   * 
   * */

  public String getReportsById(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sortId = request.getParameter("sortId");
   
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
     
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      data=this.logic.getReportsById(dbConn,sortId);
      data="["+data+"]";
      
      
     // System.out.println(sb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getListItemAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String rId = request.getParameter("rId");
      String data="";
      data=this.logic.getSelectOptionLogic(dbConn,rId);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getCalListItemAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String rId = request.getParameter("rId");
      String data="";
      data=this.logic.getSelectCalOptionLogic(dbConn,rId);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String addReportAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   
      Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.addReportLogic(dbConn,fileForm,person);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);     
   
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/allreport/newRemind.jsp";
  }
  
  public String editReportAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String rId = request.getParameter("rId");
      String data="";
      data=this.logic.editReportLogic(dbConn,rId);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
      
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateReportAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
      Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.updateReportLogic(dbConn,fileForm,person);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);     
   
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/allreport/editRemind.jsp";
  }
  
  public String delReportByIdAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String rId = request.getParameter("rId");    
     this.logic.delReportByIdLogic(dbConn,rId);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);

    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
 
}
