package t9.core.funcs.sms.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.logic.T9ExportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.logic.T9SmsTestLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SmsTestAct {
  public String notConfirm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9SmsTestLogic smsLogic = new T9SmsTestLogic();
      data = smsLogic.toNewBoxJson(dbConn, request.getParameterMap(), toId,pageNo,pageSize);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/sms/notConSms2.jsp?sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
  public String acceptedSms(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    String queryType = request.getParameter("queryType");
    boolean isQuery = false;
    String url = "";
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    if("1".equals(queryType)){
      isQuery = true;
      url =  "/core/funcs/sms/searchForIn.jsp?";
    }else{
      url =  "/core/funcs/sms/accepte.jsp?";
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9SmsTestLogic smsLogic = new T9SmsTestLogic();
      data = smsLogic.toInBoxJson(dbConn, request.getParameterMap(), toId,pageNo,pageSize,isQuery);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return url + "sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
  public String sentSmsList(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    String queryType = request.getParameter("queryType");
    boolean isQuery = false;
    String url = "";
    if("1".equals(queryType)){
      isQuery = true;
      url =  "/core/funcs/sms/searchForOut.jsp?";
    }else{
      url =  "/core/funcs/sms/sentsms1.jsp?";
    }
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9SmsTestLogic smsLogic = new T9SmsTestLogic();
      data = smsLogic.toSendBoxJson(dbConn, request.getParameterMap(), toId,pageNo,pageSize,isQuery);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return url + "sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
  public String exportExcel(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    OutputStream ops = null;
    ArrayList<T9DbRecord > dbL = null;
    try {
      String userType = request.getParameter("userType");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String fileName = URLEncoder.encode("内部短消息.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
     

      T9SmsTestLogic stl = new T9SmsTestLogic();
      if("1".equals(userType)){
        dbL = stl.toInBoxExportData(dbConn, request.getParameterMap(), person.getSeqId());
      }else if("2".equals(userType)){
        dbL = stl.toSendBoxExportData(dbConn, request.getParameterMap(),  person.getSeqId());
      }
      //dbL = stl.toInBoxExportData(dbConn, request.getParameterMap(), person.getSeqId());
      //System.out.println(dbL);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
}
