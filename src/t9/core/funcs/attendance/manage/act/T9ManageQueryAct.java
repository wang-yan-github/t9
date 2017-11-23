package t9.core.funcs.attendance.manage.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.manage.logic.T9ManageEvectionLogic;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.attendance.manage.logic.T9ManageQueryLogic;
import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.calendar.info.logic.T9InfoLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9ManageQueryAct {
  T9ManageQueryLogic logic = new  T9ManageQueryLogic();
  
  public String getDutyType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String data="";
      data=logic.getDutyType(dbConn);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getDeptName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String data="";
      data=logic.getDutyType(dbConn);
      data="{data:["+data+"]}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getDeptDuty(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String data="";
      data=logic.getDeptDuty(dbConn,request.getParameterMap());

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String expDeptDuty(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      List<Map<String, String>> dataList = logic.expDeptDuty(dbConn,request.getParameterMap());
      String name = "上下班登记数据.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  public String getEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data="";
      data=logic.getEvection(dbConn,request.getParameterMap());
      data="{data:["+data+"],userPriv:'"+user.getPostPriv()+"'}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data="";
      data=logic.getOut(dbConn,request.getParameterMap());
      data="{data:["+data+"],userPriv:'"+user.getPostPriv()+"'}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data="";
      data=logic.getLeave(dbConn,request.getParameterMap());
      data="{data:["+data+"],userPriv:'"+user.getPostPriv()+"'}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getOvertime(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data="";
      data=logic.getOvertime(dbConn,request.getParameterMap());
      data="{data:["+data+"],userPriv:'"+user.getPostPriv()+"'}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  public String getExeclEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("dept");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");

      List<Map<String, String>> dataList = this.logic.getExeclEvectionLogic(
          dbConn, deptId, startDate, endDate);

      String name = "考勤出差数据（" + startDate + " 至   " + endDate + "）.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertEvectionList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  
  public String getExeclLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("dept");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");

      List<Map<String, String>> dataList = this.logic.getExeclLeaveLogic(
          dbConn, deptId, startDate, endDate);

      String name = "考勤请假数据（" + startDate + " 至   " + endDate + "）.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertLeaveList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  
  public String getExeclOvertime(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("dept");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");

      List<Map<String, String>> dataList = this.logic.getExeclOvertimeLogic(
          dbConn, deptId, startDate, endDate);

      String name = "考勤加班数据（" + startDate + " 至   " + endDate + "）.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertOvertimeList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  
  public String getExeclOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String deptId = request.getParameter("dept");
      String startDate = request.getParameter("startDate");
      String endDate = request.getParameter("endDate");

      List<Map<String, String>> dataList = this.logic.getExeclOutLogic(
          dbConn, deptId, startDate, endDate);

      String name = "考勤外出数据（" + startDate + " 至   " + endDate + "）.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertOutList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  
  public String delEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId=request.getParameter("seqId");
      logic.delEvection(dbConn,seqId);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String delOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId=request.getParameter("seqId");
      logic.delOut(dbConn,seqId);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String delLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId=request.getParameter("seqId");
      logic.delLeave(dbConn,seqId);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String delOvertime(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId=request.getParameter("seqId");
      logic.delOvertime(dbConn,seqId);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserDutyInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      String userId = request.getParameter("userId");//得到指定用户的ID
      String userName = T9InfoLogic.getUserName(userId, dbConn);
      String days = request.getParameter("days");//得到指定的所有日期


      String data=this.logic.getUserDutyInfoLogic(dbConn,request,response,userId,days);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, userName);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getExeclDutyInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String userId = request.getParameter("userId");//得到指定用户的ID
      String startDate = request.getParameter("startTime");//得到指定的所有日期
      String endDate = request.getParameter("endTime");
         //得到到之间的天数数组
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      long daySpace = T9Utility.getDaySpan(dateFormat1.parse(startDate),dateFormat1.parse(endDate))+1;
      String days = "";
      Calendar calendar = new GregorianCalendar();
      for(int i = 0;i<daySpace;i++){
        calendar.setTime(dateFormat1.parse(startDate));
        calendar.add(Calendar.DATE,+i) ;
        Date dateTemp = calendar.getTime();
        String dateTempStr = dateFormat1.format(dateTemp);
        days = days + dateTempStr + ",";
      }
      if(daySpace>0){
        days = days.substring(0,days.length()-1);
      }
      
      
      
      
      
      List<LinkedList<String>> dataList = this.logic.getExeclDutyInfo(dbConn,request,response,userId,days);

      String name = "考勤记录数据.xls";
      String fileName = URLEncoder.encode(name, "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      ArrayList<T9DbRecord> dbL = this.logic.convertExelDutyList(dataList);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e) {
      throw e;
    } finally {
      ops.close();
    }
    return null;
  }
  
}
