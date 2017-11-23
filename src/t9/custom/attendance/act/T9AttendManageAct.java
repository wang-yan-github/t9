package t9.custom.attendance.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.attendance.personal.data.T9AttendDuty;
import t9.core.funcs.attendance.personal.logic.T9AttendDutyLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.attendance.data.T9AttendConfig;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.custom.attendance.data.T9AnnualLeave;
import t9.custom.attendance.data.T9Duty;
import t9.custom.attendance.data.T9OvertimeRecord;
import t9.custom.attendance.data.T9PersonAnnualPara;
import t9.custom.attendance.data.T9PersonalLeave;
import t9.custom.attendance.logic.T9AnnualLeaveLogic;
import t9.custom.attendance.logic.T9DutyLogic;
import t9.custom.attendance.logic.T9OvertimeRecordLogic;
import t9.custom.attendance.logic.T9PersonAnnualParaLogic;
import t9.custom.attendance.logic.T9PersonalLeaveLogic;

public class T9AttendManageAct {
  /**
   * 
   * 查询所有是自己审批的加班审批 + 请假审批 + 年休假审批
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAttendLeader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat formatter2 = new SimpleDateFormat("E"); 
      int userId = user.getSeqId();
      T9PersonLogic tpl = new T9PersonLogic();
      //加班审批
      String[] overtimeStr = {"LEADER_ID = '" + userId + "'" , "STATUS = '0'"};
      T9OvertimeRecordLogic overtimeLogic = new T9OvertimeRecordLogic();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      overList = overtimeLogic.selectOvertime(dbConn, overtimeStr);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      String overtimeJson = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
        String applyName = "";
        applyName = tpl.getNameBySeqIdStr(overtime.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        long overtimeChar = 0;
        if(overtime.getBeginTime()!= null){
          String beginDate = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " + overtime.getBeginDate();
          String endDate = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " + overtime.getEndDate();
          overtimeChar = getDateTime(T9Utility.parseDate(beginDate),T9Utility.parseDate(endDate));
        }
        String deptName = T9Utility.encodeSpecial(t9aol.selectByUserIdDept(dbConn, overtime.getUserId()));
        int runId = fu.isRunHook(dbConn, "OVERTIME_ID", overtime.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        overtimeJson = overtimeJson + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",applyName:\"" + applyName+ "\",deptName:\"" + deptName + "\",overtimeChar:\"" + overtimeChar + "\"},";
      }
      if(overList.size()>0){
        overtimeJson = overtimeJson.substring(0, overtimeJson.length()-1);
      }
      overtimeJson = overtimeJson + "]" ;
      
    //值班审批
      String[] dutyStr = {"LEADER_ID = '" + userId + "'" , "STATUS = '0'"};
      T9DutyLogic dutyLogic = new T9DutyLogic();
      List<T9Duty> dutyList = new ArrayList<T9Duty>();
      dutyList = dutyLogic.getDutyList(dbConn, dutyStr);
      String dutyJson = "[";
      for (int i = 0; i < dutyList.size(); i++) {
        T9Duty duty = dutyList.get(i);
        String applyName = "";
        applyName = tpl.getNameBySeqIdStr(duty.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        long overtimeChar = 0;
        if(duty.getDutyTime()!= null){
          String beginDate = String.valueOf(duty.getDutyTime()).substring(0, 10) + " " + duty.getBeginDate();
          String endDate = String.valueOf(duty.getDutyTime()).substring(0, 10) + " " + duty.getEndDate();
          overtimeChar = getDateTime(T9Utility.parseDate(beginDate),T9Utility.parseDate(endDate));
        }
        String deptName = T9Utility.encodeSpecial(t9aol.selectByUserIdDept(dbConn, duty.getUserId()));
        dutyJson = dutyJson + T9FOM.toJson(duty).toString().substring(0, T9FOM.toJson(duty).toString().length()-1 ) + ",applyName:\"" + applyName+ "\",deptName:\"" + deptName + "\",overtimeChar:\"" + overtimeChar + "\"},";
      }
      if(dutyList.size()>0){
        dutyJson = dutyJson.substring(0, dutyJson.length()-1);
      }
      dutyJson = dutyJson + "]" ;
      
      //请假审批
      T9PersonalLeaveLogic leaveLogic = new T9PersonalLeaveLogic();
      String[] str = {"LEADER_ID = '" + userId + "'" , "(STATUS = '1' and (ALLOW = '0' or ALLOW = '3'))"};
      List<T9PersonalLeave> leaveList = leaveLogic.selectLeave(dbConn, str);
      String leaveJson = "[";
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonalLeave leave = leaveList.get(i);
        String applyName = "";
        applyName = tpl.getNameBySeqIdStr(leave.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        long leaveChar = 0;
        if(leave.getLeaveDate1()!=null&&leave.getLeaveDate2()!=null){
          leaveChar = getDateTime(leave.getLeaveDate1(),leave.getLeaveDate2());
        }
        String deptName = T9Utility.encodeSpecial(t9aol.selectByUserIdDept(dbConn, leave.getUserId()));
        leaveJson = leaveJson + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",applyName:\"" + applyName + "\",deptName:\"" + deptName + "\",leaveChar:\"" + leaveChar +"\"},";
      }
      if(leaveList.size()>0){
        leaveJson = leaveJson.substring(0, leaveJson.length()-1);
      }
      leaveJson = leaveJson + "]";
      //年休假审批
      T9AnnualLeaveLogic annuallogic = new T9AnnualLeaveLogic();
      String[] annualStr = {"LEADER_ID = '" + userId + "'" , "(STATUS = '1' and (ALLOW = '0' or ALLOW = '3'))"};
      List<T9AnnualLeave> annualList = annuallogic.selectLeave(dbConn, annualStr);
      String annualJson = "[";
      for (int i = 0; i < annualList.size(); i++) {
        T9AnnualLeave leave = annualList.get(i);
        String applyName = "";
        applyName = tpl.getNameBySeqIdStr(leave.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        String deptName = T9Utility.encodeSpecial(t9aol.selectByUserIdDept(dbConn, leave.getUserId()));
        //得到用户的年休假
        T9PersonAnnualParaLogic logic = new T9PersonAnnualParaLogic();
        String[] annualParaStr = {"USER_ID = '" + leave.getUserId() + "'"};
        List< T9PersonAnnualPara> annualParaList = logic.selectAnnualLeavePara(dbConn, annualParaStr);
        int annualDays = 0;
        if(annualParaList.size()>0){
          T9PersonAnnualPara annualPara = annualParaList.get(0);
          annualDays = annualPara.getAnnualDays();
        }
        //得到今年已请年休假的总天数
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String leaveTotal = annuallogic.selectPersonAnnualDays(dbConn, leave.getUserId(), year+"");
        //得到年休假剩余
        int overPlusDays = annualDays - Integer.parseInt(leaveTotal);
        annualJson = annualJson + T9FOM.toJson(leave).toString().substring(0, T9FOM.toJson(leave).toString().length()-1 ) + ",applyName:\"" + applyName +  "\",deptName:\"" + deptName +  "\",annualDays:" + annualDays + ",leaveTotal:"+leaveTotal + ",overPlusDays:" + overPlusDays+ "},";
      }
      if(annualList.size()>0){
        annualJson = annualJson.substring(0, annualJson.length()-1);
      }
      annualJson = annualJson + "]";
      String data = "{overtimeJson:" + overtimeJson + ",leaveJson:" + leaveJson + ",annualJson:" + annualJson + ",dutyJson:" + dutyJson + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 
   * 得到本用户在本月 的加班记录和请假记录，进行统计时长
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectPersonAttend(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat formatter2 = new SimpleDateFormat("E"); 
      int userId = user.getSeqId();
      T9PersonLogic tpl = new T9PersonLogic();
      //本月加班记录
      String[] overtimeStr = {"USER_ID = '" + userId + "'" , "STATUS = '1'",T9DBUtility.getMonthFilter("BEGIN_TIME", new Date())};
      T9OvertimeRecordLogic overtimeLogic = new T9OvertimeRecordLogic();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      overList = overtimeLogic.selectOvertime(dbConn, overtimeStr);
      long overtimeTotal = 0; 
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
        if(overtime.getBeginTime() != null){
          String beginDate = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " +overtime.getBeginDate();
          String endDate = String.valueOf(overtime.getBeginTime() ).substring(0, 10) + " " +overtime.getEndDate();
          overtimeTotal = overtimeTotal + getDateTime(T9Utility.parseDate(beginDate) ,T9Utility.parseDate(endDate));
        }
      }
      
      //请假审批
      T9PersonalLeaveLogic leaveLogic = new T9PersonalLeaveLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "(STATUS = '1' and (ALLOW = '1'))",T9DBUtility.getMonthFilter("LEAVE_DATE2", new Date())};
      List<T9PersonalLeave> leaveList = leaveLogic.selectLeave(dbConn, str);
      long leaveTotal = 0;;
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonalLeave leave = leaveList.get(i);
        if(leave.getLeaveDate1()!=null&&leave.getLeaveDate2()!=null){
          leaveTotal = leaveTotal + getDateTime(leave.getLeaveDate1(), leave.getLeaveDate2());
        }
      }
      //本月加班时长-本月请假时长
      long overtime_leave = overtimeTotal - leaveTotal;
      String data = "{overtimeTotal:" + overtimeTotal + ",leaveTotal:" + leaveTotal + ",overtime_leave:\"" + overtime_leave + "\",userName:\"" + T9Utility.encodeSpecial(user.getUserName()) + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据两个日期得到相隔多长时长精确到分
   * @param date1
   * @param date2
   * @return
   */
  public long getDateTime(Date date1, Date date2){
    long  dateTime1 = date1.getTime(); 
    long  dateTime2 = date2.getTime(); 
    long  dateTime = (dateTime2 - dateTime1)/(1000*60);//精确到分
    return dateTime;
  }
  /**
   * 根据时长后转换为时分
   * @return
   */
  public String getDateTimeStr(long dateTime){
    String dateStr = "";
    long hour = 0;
    hour = dateTime/60;
    if(hour>0){
      dateStr = dateStr + hour + "时";
    }
    long minute = 0;
    minute = dateTime - (hour * 60);
    if(minute>0){
      dateStr = dateStr + minute + "分";
    }
    return dateStr ;
  }
  /**
   * 根据两个日期得到相隔多长时长精确到 dd->天 HH->时 mm->分
   * @param date1
   * @param date2
   * @return
   */
  public long getDateTime(Date date1, Date date2,String type){
    long  dateTime1 = date1.getTime(); 
    long  dateTime2 = date2.getTime(); 
    long  dateTime = 0 ;
    if(type.equals("mm")){
      dateTime = (dateTime2 - dateTime1)/(1000*60);//精确到分
    }else if(type.equals("HH")){
      dateTime = (dateTime2 - dateTime1)/(1000*3600);//精确到时
    }else if(type.equals("dd")){
      dateTime = (dateTime2 - dateTime1)/(1000*3600*24);//精确到天
    }else{
      dateTime = dateTime2 - dateTime1;//精确到秒
    }
      

    return dateTime;
  }
  /**
   * 根据时长后转换为 dd->天 HH->时 mm->分
   * @return
   */
  public String getDateTimeStr(long dateTime,String type){
    String dateStr = "";
    long hour = 0;
    hour = dateTime/60;
    if(hour>0){
      dateStr = dateStr + hour + "时";
    }
    long minute = 0;
    minute = dateTime - (hour * 60);
    if(minute>0){
      dateStr = dateStr + minute + "分";
    }
    return dateStr ;
  } 
  /**
   * 
   * 根据日期，用户得到加班记录和请假记录，进行统计时长
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAttendByUserDate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
      String userIds = request.getParameter("userIds");//得到指定部门的所有的ID
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      endDate = endDate + " 23:59:59";
      String[] userIdArray = {};
      if(!userIds.equals("")){
        userIdArray = userIds.split(",");
      }
      
      T9PersonLogic tpl = new T9PersonLogic();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      T9OvertimeRecordLogic overtimeLogic = new T9OvertimeRecordLogic();
      T9PersonalLeaveLogic leaveLogic = new T9PersonalLeaveLogic();
      String data = "[";
      //对所有用户循环
      for (int i = 0; i < userIdArray.length; i++) {
        String deptName = t9aol.selectByUserIdDept(dbConn, userIdArray[i]);//得到用户的 部门 
        String userName = tpl.getNameBySeqIdStr(userIdArray[i],dbConn);
        //得到 加班记录时长
        String[] overtimeStr = {"USER_ID = '" +  userIdArray[i] + "'" , "STATUS = '1'",T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">="),T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=")};

        List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
        overList = overtimeLogic.selectOvertime(dbConn, overtimeStr);
        long overtimeTotal = 0; 
        for (int j = 0; j < overList.size(); j++) {
          T9OvertimeRecord overtime = overList.get(j);
          if(overtime.getBeginTime() != null){
            String beginDates = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " +overtime.getBeginDate();
            String endDates = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " +overtime.getEndDate();
            overtimeTotal = overtimeTotal + getDateTime(T9Utility.parseDate(beginDates), T9Utility.parseDate(endDates));
          }
        }
        

        //得到 请假时长 

        String[] str = {"USER_ID = '" + userIdArray[i] + "'" , "(STATUS = '1' and (ALLOW = '1'))",T9DBUtility.getDateFilter("LEAVE_DATE1", beginDate, ">="),T9DBUtility.getDateFilter("LEAVE_DATE2", endDate, "<=")};
        List<T9PersonalLeave> leaveList = leaveLogic.selectLeave(dbConn, str);
        long leaveTotal = 0;;
        for (int j  = 0; j < leaveList.size(); j++) {
          T9PersonalLeave leave = leaveList.get(j);
          if(leave.getLeaveDate1()!=null&&leave.getLeaveDate2()!=null){
            leaveTotal = leaveTotal + getDateTime(leave.getLeaveDate1(), leave.getLeaveDate2());
          }
        }
        
        //本月加班时长-本月请假时长
        long overtime_leave = overtimeTotal - leaveTotal;
        data =  data + "{overtimeTotal:" + overtimeTotal + ",leaveTotal:" + leaveTotal + ",overtime_leave:\"" + overtime_leave + "\",userName:\"" + T9Utility.encodeSpecial(userName) + "\",deptName:\"" + T9Utility.encodeSpecial(deptName)+ "\"},";
        
      }
      if(userIdArray.length>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data + "]";
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * exl导出
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exprotAttendExl(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
      String userId = request.getParameter("userId");
      String beginDate = request.getParameter("beginTime");
      String endDate = request.getParameter("endTime");
      String days = request.getParameter("days");
      endDate = endDate + " 23:59:59";
      String fileName = "考勤统计数据.xls";

      fileName = URLEncoder.encode(fileName, "UTF-8");//fileName.getBytes("GBK"), "iso8859-1")
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"" );
      ops = response.getOutputStream();

      
      String[] userIdArray = {};
      if(!userId.equals("")){
        userIdArray = userId.split(",");
      }
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      T9PersonLogic personLogic  = new T9PersonLogic();
      T9OvertimeRecordLogic overtimeLogic = new T9OvertimeRecordLogic();
      T9PersonalLeaveLogic leaveLogic = new T9PersonalLeaveLogic();
      for (int i = 0; i < userIdArray.length; i++) {
        String deptName = t9aol.selectByUserIdDept(dbConn, userIdArray[i]);
        T9Person person = personLogic.getPerson(dbConn, userIdArray[i]);
        T9DbRecord rc = new T9DbRecord();
        //得到 加班记录时长
        String[] overtimeStr = {"USER_ID = '" +  userIdArray[i] + "'" , "STATUS = '1'",T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">="),T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=")};

        List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
        overList = overtimeLogic.selectOvertime(dbConn, overtimeStr);
        long overtimeTotal = 0; 
        for (int j = 0; j < overList.size(); j++) {
          T9OvertimeRecord overtime = overList.get(j);
          if(overtime.getBeginTime() != null){
            String beginDates = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " +overtime.getBeginDate();
            String endDates = String.valueOf(overtime.getBeginTime()).substring(0, 10) + " " +overtime.getEndDate();
            overtimeTotal = overtimeTotal + getDateTime(T9Utility.parseDate(beginDates), T9Utility.parseDate(endDates));
          }
        }
        //得到 请假时长 

        String[] str = {"USER_ID = '" + userIdArray[i] + "'" , "(STATUS = '1' and (ALLOW = '1'))",T9DBUtility.getDateFilter("LEAVE_DATE1", beginDate, ">="),T9DBUtility.getDateFilter("LEAVE_DATE2", endDate, "<=")};
        List<T9PersonalLeave> leaveList = leaveLogic.selectLeave(dbConn, str);
        long leaveTotal = 0;;
        for (int j  = 0; j < leaveList.size(); j++) {
          T9PersonalLeave leave = leaveList.get(j);
          if(leave.getLeaveDate1()!=null&&leave.getLeaveDate2()!=null){
            leaveTotal = leaveTotal + getDateTime(leave.getLeaveDate1(), leave.getLeaveDate2());
          }
        }     
        //本月加班时长-本月请假时长
        long overtime_leave = overtimeTotal - leaveTotal;  
        String overtimeTotalStr = getDateTimeStr(overtimeTotal);
        String leaveTotalStr = getDateTimeStr(leaveTotal);
        String overtime_leave_opt = "";
        if(overtime_leave < 0){
          overtime_leave_opt = "-";
        }
        overtime_leave = Math.abs(overtime_leave);
        String overtime_leave_str = overtime_leave_opt + getDateTimeStr(overtime_leave);
        rc.addField("部门", deptName);
        rc.addField("姓名", person.getUserName());
        rc.addField("加班时长 ", overtimeTotalStr);
        rc.addField("请假时长", leaveTotalStr);
        rc.addField("加班时长-请假时长", overtime_leave_str);
        dbL.add(rc);
      }
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
