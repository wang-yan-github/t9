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
import t9.core.funcs.allreport.logic.T9ReportPrivLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;

public class T9ReportPrivAct {

	T9ReportPrivLogic logic = new T9ReportPrivLogic();
	
	
	/**
	  * 报表权限设置
	  */
	 public String getReportPrivByRidAct(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try{
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      String rId = request.getParameter("rId");
		      String data="";
		      data=logic.getReportPrivByRidLogic(dbConn,rId);
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
	 /**
	  * 删除权限
	  */
	 public String delReportPrivByIdAct(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try{
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      String seqId = request.getParameter("seqId");    
		      logic.delReportPrivByIdLogic(dbConn,seqId);
		      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);

		    } catch (Exception ex){
		       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	 /**
	  * 添加权限
	  */
	 public String addReportPrivAct(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		      String rid="";
		      String rId=request.getParameter("rid");
		      Connection dbConn = null;
		    try{
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      T9FileUploadForm fileForm = new T9FileUploadForm();
		      fileForm.parseUploadRequest(request);
		      dbConn = requestDbConn.getSysDbConn();
		       //rid=request.getParameter("rid");
		      logic.addReportPrivLogic(dbConn,fileForm,person);
		      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);     
		   
		    } catch (Exception ex){
		       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }     
		    return "/core/funcs/allreport/set_priv/newRemind.jsp";
		  }
	 /**
	  * 编辑权限
	  */
	 public String updateReportPrivAct(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
	       
		      Connection dbConn = null;
		    try{
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      dbConn = requestDbConn.getSysDbConn();
		      String seqId = request.getParameter("seqId");
          String userstr = request.getParameter("userId");
          //String deptstr = request.getParameter("deptvalue");
    
		      logic.updateReportPrivLogic(dbConn,seqId,userstr,person);
		      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);     
		   
		    } catch (Exception ex){
		       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/funcs/allreport/set_priv/editRemind.jsp";
		  }
	 
	 public String getReportPrivByPidAct(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try{
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      String seqId = request.getParameter("seqId");
	      String data="";
	      data=logic.getReportPrivByPidLogic(dbConn,seqId);
        //System.out.println(seqId);
	      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
	      
	      request.setAttribute(T9ActionKeys.RET_DATA,data);
	    } catch (Exception ex){
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
	 
}
