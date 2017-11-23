package t9.core.funcs.demo.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.demo.logic.T9DeviceLogic;
import t9.core.funcs.demo.logic.T9SealLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.sealmanage.logic.T9SealLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.mobilseal.util.Base64Util;
import t9.pda.mobilseal.util.XXTEA;

public class T9DeviceAct {

	public String getDeviceLogList(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
		          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
	        
		      String DEVICE_TYPE = request.getParameter("DEVICE_TYPE");
	        String UID = request.getParameter("UID");
	        String beginTime = request.getParameter("beginTime");
	        String endTime = request.getParameter("endTime");
	          
		      String data = "";
		      T9DeviceLogic deviceLogic = new T9DeviceLogic();
	        data = deviceLogic.getDevicelList(dbConn,request.getParameterMap() ,DEVICE_TYPE,UID,beginTime, endTime );
		      PrintWriter pw = response.getWriter();
		      pw.println(data);
		      pw.flush();
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return null;
		  }
	public String deleteDevice(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
		      int seqId = person.getSeqId();
		      String sumStrs = request.getParameter("sumStrs");
		      T9DeviceLogic pl = new T9DeviceLogic();
		      pl.deleteDevice(dbConn, sumStrs);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	public String changType(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
		      String seqId = request.getParameter("seqId");
		      String opt = request.getParameter("opt");
		      T9DeviceLogic tDeviceLogic = new T9DeviceLogic();
		      String sql = "UPDATE mobile_device SET DEVICE_TYPE = '"+opt+"' WHERE SEQ_ID = "+seqId;
		     //System.out.println(sql);
		      tDeviceLogic.updateSql(dbConn, sql);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
	}
	
	public String getDeviceInfo(HttpServletRequest request,
		      HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    String userName = "";
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
		      String seqIds = request.getParameter("seqId");
		      String para = request.getParameter("para");
		      int seqId = Integer.parseInt(seqIds);
		      //System.out.println("para:"+para);
		     /**
		      * 
		      */
		      T9DeviceLogic tDeviceLogic = new T9DeviceLogic();
		      String rSData = tDeviceLogic.getDeviceInfo(dbConn, seqId);
		      String data = parseJson(rSData,"'"+para+"'");
		      
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
		      request.setAttribute(T9ActionKeys.RET_DATA, "{'data':'"+data+"'}");
		    }catch(Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/core/inc/rtjson.jsp";
		  }
	public String parseJson(String str,String str2){
		 String key = "UDkvzXkK0zgDC5OaGZUWFywlBuXnlWVNkAN98Qx4CEPiv9yukIr8nI2apleMTnNX";
	      byte[] bytes = Base64Util.decode(str);
	      String result = new String(bytes);
	      
	      byte[] ret = XXTEA.decrypt(bytes, key.getBytes());
	      String result1 = new String(ret);
	      int pindex = result1.indexOf(str2);
	      result1 = result1.substring(pindex);
	      result1 = result1.replaceAll(str2+":'", "");
	    
	      int endindex = result1.indexOf("'");
	      result1 = result1.substring(0, endindex);
	      
	   //   System.out.println("result1"+result1);
		return result1;
	}
	
}
