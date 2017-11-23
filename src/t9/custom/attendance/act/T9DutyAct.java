package t9.custom.attendance.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import t9.custom.attendance.data.T9Duty;
import t9.custom.attendance.logic.T9DutyLogic;
import t9.custom.attendance.logic.T9OvertimeRecordLogic;

public class T9DutyAct {

  public static final String attachmentFolder = "duty";
  private T9DutyLogic logic = new T9DutyLogic();
  
  /**
   * 添加值班申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addDuty (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String dutyDesc = request.getParameter("dutyDesc");
      String dutyTime = request.getParameter("dutyTime");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String endTime = request.getParameter("endTime");
      String leaderId = request.getParameter("leaderId");
      String dutyType = request.getParameter("dutyType");
      String hour = request.getParameter("hour");
      String dutyMoney = request.getParameter("dutyMoney");
      String normalAdd = request.getParameter("normalAdd");
      String festivalAdd = request.getParameter("festivalAdd");
      String weekAdd = request.getParameter("weekAdd");
      T9Duty duty = new T9Duty();
      duty.setLeaderId(leaderId);
      duty.setDutyDesc(dutyDesc);
      duty.setDutyTime(new Date());
      if(!T9Utility.isNullorEmpty("beginTime")){
        duty.setDutyTime(format.parse(dutyTime));
      }
      duty.setUserId(String.valueOf(userId));
      duty.setStatus("0");
      duty.setBeginDate(beginDate);
      duty.setEndDate(endDate);
      duty.setHour(hour);
      if(T9Utility.isNullorEmpty(normalAdd)){
        normalAdd = "0";
      }
      if(T9Utility.isNullorEmpty(weekAdd)){
        weekAdd = "0";
      }
      if(T9Utility.isNullorEmpty(dutyMoney)){
        dutyMoney = "0";
      }
      if(T9Utility.isNullorEmpty(festivalAdd)){
        festivalAdd = "0";
      }
      duty.setNormalAdd(Double.valueOf(normalAdd));
      duty.setWeekAdd(Double.valueOf(weekAdd));
      duty.setDutyMoney(Double.valueOf(dutyMoney));
      duty.setDutyType(dutyType);
      duty.setFestivalAdd(Double.valueOf(festivalAdd));
      this.logic.addDuty(dbConn, duty);
      String smsRemind = request.getParameter("smsRemind");
      //短信smsType, content, remindUrl, toId, fromId
      if(!T9Utility.isNullorEmpty(smsRemind)){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交值班申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leaderId);
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      //手机短信 提醒 
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(!T9Utility.isNullorEmpty(moblieSmsRemind)){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leaderId, userId, "提交值班申请，请批示:" + dutyDesc, new Date());
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
    response.sendRedirect(path+ "/custom/attendance/personal/duty/index.jsp");
    return "";
  }
  
  /**
   * 查询加班申请（待批和未批的）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDutyJsonList (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      List<T9Duty> overList = new ArrayList<T9Duty>();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "(STATUS = '0' or STATUS is null or STATUS = '2') order by DUTY_TIME desc" };
      overList = this.logic.getDutyList(dbConn, str);
      String data = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9Duty overtime = overList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(overtime.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        data = data + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",leaderName:\"" + leaderName+ "\"},";
      }
      if(overList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
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
  
  public String deleteSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.deleteSingle(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询加班申请ById
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDuty (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9PersonLogic tpl = new T9PersonLogic();
      String data = "";
      if(!T9Utility.isNullorEmpty(seqId)){
        T9Duty overtime  = this.logic.getDutyDetail(dbConn, Integer.parseInt(seqId));
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(overtime.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        data = data + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",leaderName:\"" + leaderName+ "\"}";
      }
      if(data.equals("")){
        data = "{}";
      }
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
  
  public String updateDuty(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String dutyDesc = request.getParameter("dutyDesc");
      Map<String,String[]> map = request.getParameterMap();
      String leaderId = request.getParameter("leaderId");
      T9Duty record = (T9Duty) T9FOM.build(map, T9Duty.class, "");
      record.setStatus("0");
      this.logic.updateDuty(dbConn, record);
      String smsRemind = request.getParameter("smsRemind");
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交值班申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leaderId);
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leaderId, userId, "提交值班申请，请批示:" + dutyDesc, new Date());
      }
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
   * 查询加班申请（已批的）

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getHistoryDuty (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
//      String year = request.getParameter("year");
//      String month = request.getParameter("month");
//      String ymd = year + "-" +month + "-01";
      List<T9Duty> dutyList = new ArrayList<T9Duty>();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = {"USER_ID = '" + userId + "' and STATUS = '1'"};
      dutyList = this.logic.getDutyList(dbConn, str);
      String data = "[";
      for (int i = 0; i < dutyList.size(); i++) {
        T9Duty overtime = dutyList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(overtime.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        data = data + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",leaderName:\"" + leaderName+ "\"},";
      }
      if(dutyList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
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
  
  public String updateDutyStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String status = request.getParameter("allow");
      String userId = request.getParameter("userId");
      String checkOut = request.getParameter("checkOut");
      String reason = request.getParameter("reason");
      String content = "";
      T9Duty duty = new T9Duty();
      duty.setSeqId(Integer.parseInt(seqId));
      duty.setStatus(status);
      duty.setReason(reason);
//          Map map = new HashMap();
//          map.put("seqId", seqId);
//          map.put("status", status);
//          this.logic.updateStatus(dbConn, map);
      this.logic.updateDuty(dbConn, duty);
      //短信smsType, content, remindUrl, toId, fromId
      if("1".equals(status)){
        content = "您的值班申请已被批准！";
      }else{
        content = "您的值班申请未被批准！";
      }
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent(content);
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      if(checkOut!=null&&checkOut.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, content, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
