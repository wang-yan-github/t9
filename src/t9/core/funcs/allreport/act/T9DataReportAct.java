package t9.core.funcs.allreport.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.allreport.logic.T9DataReportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9DataReportAct {

	T9DataReportLogic logic=new T9DataReportLogic();
	
	/**
	 * 数据报表初始页面时查出的数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	 public String getReportListAct(HttpServletRequest request,
		      HttpServletResponse response) throws Exception{
		    
		    Connection dbConn = null;
		    try{
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      dbConn = requestDbConn.getSysDbConn();
		      
		      String data=logic.getReportListLogic(dbConn,person);
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
	 
	 public String getReportByRidAct(HttpServletRequest request,
	      HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    try{
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);      
	      String rId = request.getParameter("rId");
	      String data="";
	      data=this.logic.getReportByRidLogic(dbConn,rId,person);

	      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
	      
	      request.setAttribute(T9ActionKeys.RET_DATA,data);
	    } catch (Exception ex){
	       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
	 
	 public String getTableListAct(HttpServletRequest request,
	      HttpServletResponse response) throws Exception{
	    
	    Connection dbConn = null;
	    try{
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();

	      String data=this.logic.getTableListLogic(dbConn,person,request.getParameterMap());

	    
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
	   * 导出excel表
	   */
	  public String toExcel(HttpServletRequest request,
	      HttpServletResponse response) throws Exception{
	    Connection dbConn = null;
	    OutputStream ops = null;
	    try{
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
	      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      List<List<String>> data=logic.getExcelListLogic(dbConn,person,request.getParameterMap());
	      String fileName = URLEncoder.encode("数据报表.xls","UTF-8");
	      fileName = fileName.replaceAll("\\+", "%20");
	      response.setHeader("Cache-control","private");
	      response.setContentType("application/vnd.ms-excel");
	      response.setHeader("Accept-Ranges","bytes");
	      response.setHeader("Cache-Control","maxage=3600");
	      response.setHeader("Pragma","public");
	      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
	      ops = response.getOutputStream(); 
	      ArrayList<T9DbRecord > dbL = logic.convertList(data);
	      T9JExcelUtil.writeExc(ops, dbL);
	    } catch (Exception e){
	      e.printStackTrace();
	      throw e;
	    }finally{
	      ops.close();
	    }
	    return null;
	  }
}
