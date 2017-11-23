package t9.custom.attendance.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.custom.attendance.data.T9AnnualLeave;
import t9.custom.attendance.data.T9PersonAnnualPara;
import t9.custom.attendance.logic.T9AnnualLeaveLogic;
import t9.custom.attendance.logic.T9PersonAnnualParaLogic;
import t9.subsys.oa.fillRegister.logic.T9AttendFillLogic;

public class T9AnnualLeaveAct {
  /**
   * 
   * 添加年休假记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAnnualLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      System.out.println("");
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AnnualLeave leave = new T9AnnualLeave();
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      Date curDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String smsRemind = request.getParameter("smsRemind");
      String leaveDays = request.getParameter("leaveDays");
      T9FOM fom = new T9FOM();
      leave = (T9AnnualLeave) fom.build(request.getParameterMap());
      leave.setAllow("0");
      leave.setStatus("1");
      leave.setUserId(String.valueOf(userId));
      if(!T9Utility.isNullorEmpty(leaveDate1)){
        leave.setLeaveDate1(dateFormat.parse(leaveDate1));
      }
      if(!T9Utility.isNullorEmpty(leaveDate2)){
        leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
      }
      leave.setApplyTime(curDate);
      if(T9Utility.isInteger(leaveDays)){
        leave.setLeaveDays(Integer.parseInt(leaveDays)); 
      }
     
      logic.addLeave(dbConn, leave);
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交年休假申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leave.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交年休假申请，请批示:", new Date());
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
    response.sendRedirect(path+ "/custom/attendance/personal/annualleave/index.jsp");
    return "";
  }
  /**
   * 
   * 更新年休假记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAnnualLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AnnualLeave leave = new T9AnnualLeave();
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      Date curDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String smsRemind = request.getParameter("smsRemind");
      String leaveDays = request.getParameter("leaveDays");
      String seqId =  request.getParameter("seqId");
      if(!T9Utility.isNullorEmpty(seqId)){
        T9FOM fom = new T9FOM();
        leave = (T9AnnualLeave) fom.build(request.getParameterMap());
        leave.setAllow("0");
        leave.setStatus("1");
        leave.setSeqId(Integer.parseInt(seqId));
        leave.setUserId(String.valueOf(userId));
        if(!T9Utility.isNullorEmpty(leaveDate1)){
          leave.setLeaveDate1(dateFormat.parse(leaveDate1));
        }
        if(!T9Utility.isNullorEmpty(leaveDate2)){
          leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
        }
        leave.setApplyTime(curDate);
        if(T9Utility.isInteger(leaveDays)){
          leave.setLeaveDays(Integer.parseInt(leaveDays)); 
        }
       
        logic.updateLeave(dbConn, leave);
        //短信smsType, content, remindUrl, toId, fromId
        if(smsRemind!=null){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("6");
          sb.setContent("提交年休假申请，请批示！");
          sb.setRemindUrl("/custom/attendance/attendmanage/index.jsp");
          sb.setToId(leave.getLeaderId());
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
        String moblieSmsRemind = request.getParameter("moblieSmsRemind");
        if(moblieSmsRemind!=null){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交年休假申请，请批示:" , new Date());
        }
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
    response.sendRedirect(path+ "/custom/attendance/personal/annualleave/index.jsp");
    return "";
  }
  /**
   * 
   * 查询所有请假记录根据自己的UserID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAnnualLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "(STATUS = '1' and (ALLOW = '0' or ALLOW = '2') )"};
      List<T9AnnualLeave> leaveList = logic.selectLeave(dbConn, str);
      String data = "[";
      T9PersonLogic tpl = new T9PersonLogic();
      for (int i = 0; i < leaveList.size(); i++) {
        T9AnnualLeave leave = leaveList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",leaderName:\"" + leaderName + "\"},";
      }
      if(leaveList.size()>0){
        data = data.substring(0, data.length()-1);
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
   * 
   * 查询所有请假记录根据自己的ID(已销假的/历史 记录)
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHistroyAnnualLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String whereStr = "";
      String ymd = year + "-" + month + "-" + "01";
      if(!T9Utility.isNullorEmpty(year) || !T9Utility.isNullorEmpty(month)){
        whereStr += " and " + T9DBUtility.getMonthFilter("LEAVE_DATE1", T9Utility.parseDate(ymd));
      }
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      String[] str = {"USER_ID = '" + userId + "' and ALLOW = '1'" + whereStr};
      List<T9AnnualLeave> leaveList = logic.selectLeave(dbConn, str);
      String data = "[";
      T9PersonLogic tpl = new T9PersonLogic();
      for (int i = 0; i < leaveList.size(); i++) {
        T9AnnualLeave leave = leaveList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",leaderName:\"" + leaderName + "\"},";
      }
      if(leaveList.size()>0){
        data = data.substring(0, data.length()-1);
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
   * 
   * 删除请假记录根据ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAnnualLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      if(!T9Utility.isNullorEmpty(seqId)){
        logic.deleteLeaveById(dbConn, seqId);
      }
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
  public String selectAnnualLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      String seqId = request.getParameter("seqId");
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      String data = "";
      if(!T9Utility.isNullorEmpty(seqId)){
        T9AnnualLeave leave = logic.selectLeaveById(dbConn, seqId);
        if(leave!=null){
          T9PersonLogic tpl = new T9PersonLogic();
          String leaderName = "";
          if(!T9Utility.isNullorEmpty(leave.getLeaderId())){
            leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
          } 
          if(leaderName!=null&&!leaderName.equals("")){
            leaderName = leaderName.substring(0, leaderName.length()-1);
            leaderName = T9Utility.encodeSpecial(leaderName);
          }
          data = data + T9FOM.toJson(leave).toString().substring(0, T9FOM.toJson(leave).toString().length()-1 ) + ",leaderName:\"" + leaderName + "\"}";
        }
      }
      if(data.equals("")){
        data = "{}";
      }
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
   * 更改出差状态(批准)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAllow(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
      Connection dbConn = null; 
      try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
      dbConn = requestDbConn.getSysDbConn(); 
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER); 
      int userSeqId = user.getSeqId(); 
      String seqId = request.getParameter("seqId"); 
      String allow = request.getParameter("allow"); 
      String userId = request.getParameter("userId"); 
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic(); 
      if(!T9Utility.isNullorEmpty(seqId)){ 
      logic.updateLeaveAllow(dbConn, seqId, allow, ""); 
      //配合陈晨年休假自动补登记 
      T9AnnualLeave leave = logic.selectLeaveById(dbConn, seqId); 
      T9AttendFillLogic fillLogic = new T9AttendFillLogic(); 
      fillLogic.addAttendScoreYear(dbConn, user, T9Utility.getDateTimeStr(leave.getLeaveDate1()), T9Utility.getDateTimeStr(leave.getLeaveDate2()),userId ,user.getSeqId()+""); 

      //短信smsType, content, remindUrl, toId, fromId 
      T9SmsBack sb = new T9SmsBack(); 
      sb.setSmsType("6"); 
      sb.setContent("您的年休假申请已被批准！"); 
      sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp"); 
      sb.setToId(userId); 
      sb.setFromId(userSeqId); 
      T9SmsUtil.smsBack(dbConn, sb); 
      String checkEvection = request.getParameter("checkEvection"); 
      if(checkEvection!=null&&checkEvection.equals("1")){ 
      T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic(); 
      sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的年休假申请已被批准", new Date()); 
      } 
      } 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！"); 
      }catch(Exception ex) { 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage()); 
      throw ex; 
      } 
      return "/core/funcs/attendance/manage/manage.jsp"; 
    }
  /**
   * 外出不批准(说明理由)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAllowReason(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String notReason = request.getParameter("notReason");
      String userId = request.getParameter("userId");
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      if(!T9Utility.isNullorEmpty(seqId)){
        logic.updateLeaveAllow(dbConn, seqId, allow, notReason);
        //短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的年休假申请未被批准！");
        sb.setRemindUrl("/custom/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        
        String checkEvection = request.getParameter("checkEvection");
        if(checkEvection!=null&&checkEvection.equals("1")){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的年休假申请未被批准！内容："+notReason, new Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{}");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取年休假剩余天数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAnnualOverplus(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId(); 
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = Integer.parseInt(userIdStr);
      }
      String yearStr = request.getParameter("year");
      //得到用户的年休假
      T9PersonAnnualParaLogic logic = new T9PersonAnnualParaLogic();
      String[] str = {"USER_ID = '" + userId + "'"};
      List< T9PersonAnnualPara> annualList = logic.selectAnnualLeavePara(dbConn, str);
      int annualDays = 0;
      if(annualList.size()>0){
        T9PersonAnnualPara annualPara = annualList.get(0);
        annualDays = annualPara.getAnnualDays();
      }
      
      //得到今年已请年休假的总天数
      Calendar cal = Calendar.getInstance();
      int year = cal.get(Calendar.YEAR);
      if(T9Utility.isNullorEmpty(yearStr)){
        yearStr = String.valueOf(year);
      }
      T9AnnualLeaveLogic leaveLogic = new T9AnnualLeaveLogic();
      
      //已请年休假
      String leaveTotal = leaveLogic.selectPersonAnnualDays(dbConn, userId +"", yearStr);
      
      //已请假
//      String leaveSum = leaveLogic.selectPersonLeaveDays(dbConn, String.valueOf(userId), yearStr);
      
      //得到年休假剩余//      double leaveCount = 0;
//      if(!T9Utility.isNullorEmpty(leaveSum)){
//        leaveCount = Double.parseDouble(leaveSum);
//      }
      double OverplusDays = annualDays - Integer.parseInt(leaveTotal) ;//- leaveCount;
      double leaveDays = Integer.parseInt(leaveTotal) ;//+ leaveCount;
      
      String data = "{overplusDays:" + OverplusDays + ",leaveDays:"+leaveDays+"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
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
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String data = "";
      T9AnnualLeaveLogic all = new T9AnnualLeaveLogic();
      data = all.showTimeStr(dbConn, beginDate, endDate);
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
  
  
  public String getAnnualLeaveDay(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      T9AnnualLeaveLogic logic = new T9AnnualLeaveLogic();
      double data = logic.getAnnualLeaveDayLogic(dbConn, year, month, userId);
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
