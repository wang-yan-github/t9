package t9.core.funcs.attendance.personal.act;

import java.net.InetAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.funcs.attendance.personal.logic.T9AttendLeaveLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
public class T9AttendLeaveAct {
  
  private T9AttendLeaveLogic logic = new T9AttendLeaveLogic();
  /**
   * 
   * 添加请假记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendLeave leave = new T9AttendLeave();
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      Date curDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String leaveDays = request.getParameter("leaveDays");
      String smsRemind = request.getParameter("smsRemind");
      String userSeqId = request.getParameter("user");
      if(userSeqId!=null&&!userSeqId.equals("")){
        userId = Integer.parseInt(userSeqId);
      }
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
      T9FOM fom = new T9FOM();
      leave = (T9AttendLeave) fom.build(request.getParameterMap());
      String leaveType = request.getParameter("leaveType");
      leaveType = leaveType.replaceAll("\\\n","");
      leaveType = leaveType.replaceAll("\\\r","");
   
      leave.setRegisterIp(registerIp);
      leave.setLeaveDays(Double.parseDouble(leaveDays));
      leave.setAllow("0");
      //leave.setStatus("1");
      leave.setUserId(String.valueOf(userId));
      leave.setLeaveDate1(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate1));
      leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
      double hour = this.logic.getHourDiff(leaveDate1, leaveDate2, "yyyy-MM-dd HH:mm:ss");
      if(leave.getLeaveDate2().compareTo(curDate)<=0){
        leaveType = "补假: " + leaveType;
      }
      leave.setLeaveType(leaveType);
      leave.setHour(hour);
      t9all.addLeave(dbConn, leave);
      T9FlowHookUtility ut = new T9FlowHookUtility();
      int attendLeaveId = ut.getMax(dbConn, "select max(SEQ_ID) FROM ATTEND_LEAVE");
      Map dataArray = new HashMap();
      dataArray.put("KEY", attendLeaveId + "");
      dataArray.put("FIELD", "LEAVE_ID");
      dataArray.put("USER_ID", leave.getUserId());
      T9PersonLogic p = new T9PersonLogic();
      String userName = p.getUserNameLogic(dbConn, Integer.parseInt(leave.getUserId()));
      dataArray.put("USER_NAME", userName);
      dataArray.put("LEAVE_TYPE", leaveType);
      dataArray.put("LEAVE_DATE1", leaveDate1);
      dataArray.put("LEAVE_DATE2", leaveDate2);
      dataArray.put("ANNUAL_LEAVE", leave.getAnnualLeave() + "");
      dataArray.put("LEADER_ID", leave.getLeaderId());
      String url = ut.runHook(dbConn, user, dataArray, "attend_leave");
      if (!"".equals(url)) {
        String path = request.getContextPath();
        response.sendRedirect(path+ url);
        return null;
      }
      
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交请假申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leave.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交请假申请，请批示:" + leaveType, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/leave.jsp");
    return null;
  }
  /**
   * 
   * 查询所有请假记录根据自己的ID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendLeave leave = new T9AttendLeave();
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      List<T9AttendLeave> leaveList = t9all.selectLeave(dbConn, userId);
      String data = "[";
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        leave = leaveList.get(i);
        //System.out.println(leave.getDestroyTime());
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        int runId = fu.isRunHook(dbConn, "LEAVE_ID", leave.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+ flowId +"\",leaderName:\"" + leaderName + "\"},";
      }
      if(leaveList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      //得到今年已请假多少天
      long leaveDaysTotal = t9all.selectLeaveDaysByUserId(dbConn, userId + "");
      String leaveDaysTotalStr = getDateTimeStr(leaveDaysTotal);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,leaveDaysTotalStr );
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据时长后转换为天 时
   * @return
   */
  public String getDateTimeStr(long dateTime){
    String dateStr = "";
    long day = 0;
    day = dateTime/(24*3600);
    if(day>0){
      dateStr = dateStr + day + "天";
    }
    long hour = 0;
    hour = dateTime - (day * 24*3600);
    if(hour>0){
      dateStr = dateStr + hour + "时";
    }
    return dateStr ;
  }
  /**
   * 
   * 查询所有历史请假记录根据自己的ID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHistroyLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendLeave leave = new T9AttendLeave();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String whereStr = "";
      String ymd = year + "-" + month + "-" + "01";
      if(!T9Utility.isNullorEmpty(year) || !T9Utility.isNullorEmpty(month)){
        whereStr += " and " + T9DBUtility.getMonthFilter("LEAVE_DATE1", T9Utility.parseDate(ymd));
      }
      String data = "[";
      Map map = new HashMap();
      map.put("USER_ID", userId);
      //map.put("STATUS", "2");
      String[] str = {"USER_ID='"+userId + "' and ALLOW = '1'" + whereStr + " order by LEAVE_DATE1 desc"};
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      List<T9AttendLeave> leaveList = t9all.selectHistroyLeave(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        leave = leaveList.get(i);
        //System.out.println(leave.getDestroyTime());
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        int runId = fu.isRunHook(dbConn, "LEAVE_ID", leave.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+ flowId +"\",leaderName:\"" + leaderName + "\"},";
      }
      if(leaveList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
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
   * 删除请假记录根据ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AttendLeave leave = new T9AttendLeave();
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      t9all.deleteLeaveById(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 查询所有请假记录根据自己的ID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendLeave leave = new T9AttendLeave();
      String seqId = request.getParameter("seqId");
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      leave = t9all.selectLeaveById(dbConn, seqId);
      T9PersonLogic tpl = new T9PersonLogic();
      //System.out.println(leave.getDestroyTime());
      String leaderName = "";
      String data = "";
      if(!T9Utility.isNullorEmpty(leave.getLeaderId())){
        leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
      } 
      if(leaderName!=null&&!leaderName.equals("")){
        leaderName = leaderName.substring(0, leaderName.length()-1);
        leaderName = T9Utility.encodeSpecial(leaderName);
      }
      data = data + T9FOM.toJson(leave).toString().substring(0, T9FOM.toJson(leave).toString().length()-1 ) + ",leaderName:\"" + leaderName + "\"}";
      //System.out.println(data);
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
   * 更新请假记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendLeave leave = new T9AttendLeave();
      Date curDate = new Date();
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDays = request.getParameter("leaveDays");
      //System.out.println(leaveDate1);
      String leaveDate2 = request.getParameter("leaveDate2");
      String smsRemind = request.getParameter("smsRemind");
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
      T9FOM fom = new T9FOM();
      leave = (T9AttendLeave) fom.build(request.getParameterMap());
      String leaveType = request.getParameter("leaveType");
      leaveType = leaveType.replaceAll("\\\n","");
      leaveType = leaveType.replaceAll("\\\r",""); 
      leave.setRegisterIp(registerIp);
      leave.setLeaveDays(Double.parseDouble(leaveDays));
      leave.setAllow("0");
      leave.setStatus("1");
      leave.setUserId(String.valueOf(userId));
      leave.setReason("");
      leave.setLeaveDate1(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate1));
      leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
      double hour = this.logic.getHourDiff(leaveDate1, leaveDate2, "yyyy-MM-dd HH:mm:ss");
      leave.setHour(hour);
      if(leave.getLeaveDate2().compareTo(curDate)<=0){
        if(!leaveType.trim().startsWith("补假")){
          leaveType = "补假: " + leaveType;
        }
      }
      leave.setLeaveType(leaveType);
      t9all.updateLeave(dbConn, leave);
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交请假申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leave.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
 
      
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交请假申请，请批示:" + leaveType, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/leave.jsp");
    return "";
  }
  /**
   * 
   * 更新请假记录status
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateLeaveStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String checkLeave = request.getParameter("checkLeave");
      String dateStr = T9Utility.getDateTimeStr(new Date());
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      T9AttendLeave leave = new T9AttendLeave();
      leave = t9all.selectLeaveById(dbConn, seqId);
      leave.setAllow(allow);
      leave.setDestroyTime(T9Utility.parseDate(dateStr));
      t9all.updateLeave(dbConn, leave);
   
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("提交销假申请，请批示！");
      sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
      sb.setToId(leave.getLeaderId());
      sb.setFromId(userId);
      T9SmsUtil.smsBack(dbConn, sb);
      if(checkLeave!=null&&checkLeave.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交请假销假，请批示！", new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/attendance/personal/leave.jsp";
  }
  
  /**
   * 展示自动补登记、不需要审核日期--cc 20101126
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String data = "";
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      data = t9all.showTimeStr(dbConn, person, beginDate, endDate);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 请假总时长
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendLeaveHour(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = userIdStr;
      }
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      T9AttendLeaveLogic all = new T9AttendLeaveLogic();
      double data = this.logic.getAttendLeaveHourLogic(dbConn, year, month, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getOverTimeHour(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = userIdStr;
      }
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      T9AttendLeaveLogic all = new T9AttendLeaveLogic();
      double data = this.logic.getOverTimeHourLogic(dbConn, year, month, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
