package t9.custom.attendance.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.custom.attendance.data.T9OvertimeRecord;
import t9.custom.attendance.logic.T9OvertimeRecordLogic;
import t9.subsys.oa.addworkfee.logic.T9AddWorkFeeLogic;

public class T9OvertimeRecordAct {
  private T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
  /**
   * 添加加班申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addOvertime (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String overtimeDesc = request.getParameter("overtimeDesc");
      String beginTime = request.getParameter("beginTime");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String endTime = request.getParameter("endTime");
      String leaderId = request.getParameter("leaderId");
      String overtimeType = request.getParameter("overtimeType");
      String hour = request.getParameter("hour");
      String overtimeMoney = request.getParameter("overtimeMoney");
      String normalAdd = request.getParameter("normalAdd");
      String festivalAdd = request.getParameter("festivalAdd");
      String weekAdd = request.getParameter("weekAdd");
      T9OvertimeRecord t9or = new T9OvertimeRecord();
      t9or.setLeaderId(leaderId);
      t9or.setOvertimeDesc(overtimeDesc);
      t9or.setOvertimeTime(new Date());
      if(!T9Utility.isNullorEmpty("beginTime")){
        t9or.setBeginTime(format.parse(beginTime));
      }
      t9or.setUserId(String.valueOf(userId));
      t9or.setStatus("0");
      t9or.setBeginDate(beginDate);
      t9or.setEndDate(endDate);
      t9or.setHour(hour);
      if(T9Utility.isNullorEmpty(normalAdd)){
        normalAdd = "0";
      }
      if(T9Utility.isNullorEmpty(weekAdd)){
        weekAdd = "0";
      }
      if(T9Utility.isNullorEmpty(overtimeMoney)){
        overtimeMoney = "0";
      }
      if(T9Utility.isNullorEmpty(festivalAdd)){
        festivalAdd = "0";
      }
      t9or.setNormalAdd(Double.valueOf(normalAdd));
      t9or.setWeekAdd(Double.valueOf(weekAdd));
      t9or.setOvertimeMoney(Double.valueOf(overtimeMoney));
      t9or.setOvertimeType(overtimeType);
      t9or.setFestivalAdd(Double.valueOf(festivalAdd));
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      logic.addOvertime(dbConn, t9or);
      
      T9FlowHookUtility ut = new T9FlowHookUtility();
      int overTimeId = ut.getMax(dbConn, "select max(SEQ_ID) FROM overtime_record");
      Map dataArray = new HashMap();
      dataArray.put("KEY", overTimeId + "");
      dataArray.put("FIELD", "OVERTIME_ID");
      dataArray.put("USER_ID", t9or.getUserId()+"");
      T9PersonLogic p = new T9PersonLogic();
      String userName = p.getUserNameLogic(dbConn, Integer.parseInt(t9or.getUserId()));
      dataArray.put("USER_NAME", userName);
      String leaderName= p.getUserNameLogic(dbConn, Integer.parseInt(t9or.getLeaderId()));
      dataArray.put("APPROVE_ID",leaderName);
      dataArray.put("RECORD_TIME",beginTime);
      dataArray.put("OVERTIME_CONTENT",overtimeDesc);
      dataArray.put("START_TIME",beginTime+" "+beginDate);
      dataArray.put("END_TIME",beginTime+" "+beginDate);
      String url = ut.runHook(dbConn, user, dataArray, "attendance_overtime");
      if (!"".equals(url)) {
        String path = request.getContextPath();
        response.sendRedirect(path+ url);
      }
      
      
      String smsRemind = request.getParameter("smsRemind");
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交加班申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(leaderId);
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      //手机短信 提醒 
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,leaderId, userId, "提交加班申请，请批示:" + overtimeDesc, new Date());
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
    response.sendRedirect(path+ "/custom/attendance/personal/overtime/index.jsp");
    return "";
  }
  /**
   * 更新加班申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateOvertime (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String overtimeDesc = request.getParameter("overtimeDesc");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String leaderId = request.getParameter("leaderId");
      String overtimeType = request.getParameter("overtimeType");
      String hour = request.getParameter("hour");
      String overtimeMoney = request.getParameter("overtimeMoney");
      String normalAdd = request.getParameter("normalAdd");
      String festivalAdd = request.getParameter("festivalAdd");
      String weekAdd = request.getParameter("weekAdd");
      if(T9Utility.isInteger(seqId)){
        T9OvertimeRecord t9or = new T9OvertimeRecord();
        t9or.setLeaderId(leaderId);
        t9or.setOvertimeDesc(overtimeDesc);
        t9or.setOvertimeTime(new Date());
        if(!T9Utility.isNullorEmpty("beginTime")){
          t9or.setBeginTime(format.parse(beginTime));
        }
        t9or.setStatus("0");
        t9or.setBeginDate(beginDate);
        t9or.setEndDate(endDate);
        t9or.setHour(hour);
        if(T9Utility.isNullorEmpty(normalAdd)){
          normalAdd = "0";
        }
        if(T9Utility.isNullorEmpty(weekAdd)){
          weekAdd = "0";
        }
        if(T9Utility.isNullorEmpty(overtimeMoney)){
          overtimeMoney = "0";
        }
        if(T9Utility.isNullorEmpty(festivalAdd)){
          festivalAdd = "0";
        }
        t9or.setNormalAdd(Double.valueOf(normalAdd));
        t9or.setWeekAdd(Double.valueOf(weekAdd));
        t9or.setOvertimeMoney(Double.valueOf(overtimeMoney));
        t9or.setOvertimeType(overtimeType);
        t9or.setFestivalAdd(Double.valueOf(festivalAdd));
        t9or.setSeqId(Integer.parseInt(seqId));
        T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
        logic.updateOvertimeById(dbConn, t9or);
        String smsRemind = request.getParameter("smsRemind");
        //短信smsType, content, remindUrl, toId, fromId
        if(smsRemind!=null){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("6");
          sb.setContent("提交加班申请，请批示！");
          sb.setRemindUrl("/custom/attendance/attendmanage/index.jsp");
          sb.setToId(leaderId);
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }

        String moblieSmsRemind = request.getParameter("moblieSmsRemind");
        if(moblieSmsRemind!=null){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,leaderId, userId, "提交加班申请，请批示:" + overtimeDesc, new Date());
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
    response.sendRedirect(path+ "/custom/attendance/personal/overtime/index.jsp");
    return "";
  }
  /**
   * 更新加班申请的状态
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateOvertimeStatus (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String reason = request.getParameter("reason");
      String status = request.getParameter("status");
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      if(!T9Utility.isInteger(seqId)){
        T9OvertimeRecord t9or = logic.selectOvertimeById(dbConn, seqId);
        if(t9or!=null){
          logic.updateOvertimeById(dbConn, seqId, status, reason);
          String leaderId = t9or.getLeaderId();
          String smsRemind = request.getParameter("smsRemind");
          //短信smsType, content, remindUrl, toId, fromId
          if(smsRemind!=null){
            T9SmsBack sb = new T9SmsBack();
            sb.setSmsType("6");
            sb.setContent("提交加班申请，请批示！");
            sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
            sb.setToId(leaderId);
            sb.setFromId(userId);
            T9SmsUtil.smsBack(dbConn, sb);
          }

          String moblieSmsRemind = request.getParameter("moblieSmsRemind");
          if(moblieSmsRemind!=null){
            T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
            sms2Logic.remindByMobileSms(dbConn,leaderId, userId, "提交加班申请，请批示:" , new Date());
          }
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
    response.sendRedirect(path+ "/core/funcs/attendance/personal/evection.jsp");
    return "";
  }
  /**
   * 删除加班申请ById
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delOvertimeById (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      if(T9Utility.isInteger(seqId)){
        logic.delOvertimeById(dbConn, seqId);
      }
      String data = "{}";
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
  /**
   * 查询加班申请ById
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectOvertimeById (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      T9PersonLogic tpl = new T9PersonLogic();
      String data = "";
      if(!T9Utility.isNullorEmpty(seqId)){
        T9OvertimeRecord overtime  = logic.selectOvertimeById(dbConn, seqId);
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
  /**
   * 查询加班申请（待批和未批的）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectOvertime (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "(STATUS = '0' or STATUS is null)" };
      overList = logic.selectOvertime(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      String data = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(overtime.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        
        int runId = fu.isRunHook(dbConn, "OVERTIME_ID", overtime.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        
        data = data + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",leaderName:\"" + leaderName+ "\"},";
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
  
  /**
   * 查询加班申请（已批的）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHistoryOvertime (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      T9PersonLogic tpl = new T9PersonLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "STATUS = '1'" };
      overList = logic.selectOvertime(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      String data = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(overtime.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        
        int runId = fu.isRunHook(dbConn, "OVERTIME_ID", overtime.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        data = data + T9FOM.toJson(overtime).toString().substring(0, T9FOM.toJson(overtime).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",leaderName:\"" + leaderName+ "\"},";
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
  /**
   * 更改状态同意
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String status = request.getParameter("allow");
      String userId = request.getParameter("userId");
      String checkOut = request.getParameter("checkOut");
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      if (!T9Utility.isNullorEmpty(seqId)) {
        logic.updateOvertimeById(dbConn, seqId, status, "");
        // 短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的加班申请已被批准！");
        sb.setRemindUrl("/custom/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        // 发送手机短信
        if (checkOut != null && checkOut.equals("1")) {
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userId, userSeqId, "您的加班申请已被批准！",
              new Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更改状态不同意
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatusReason(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String status = request.getParameter("allow");
      String userId = request.getParameter("userId");
      String checkOut = request.getParameter("checkOut");
      String reason = request.getParameter("reason");
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      if (!T9Utility.isNullorEmpty(seqId)) {
        logic.updateOvertimeById(dbConn, seqId, status, reason);
        // 短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的加班申请未被批准！");
        sb.setRemindUrl("/custom/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        // 发送手机短信
        if (checkOut != null && checkOut.equals("1")) {
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userId, userSeqId, "您的加班申请未被批准！",new Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取个人平时加班时长--cc 20101130
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonalOverTime (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String overtimeType = request.getParameter("overtimeType");
      String curDateStr = T9Utility.getCurDateTimeStr().substring(0, 10);
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      T9PersonLogic tpl = new T9PersonLogic();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String whereStr = "";
      String ymd = year + "-" + month + "-" + "01";
      if(!T9Utility.isNullorEmpty(year) || !T9Utility.isNullorEmpty(month)){
        whereStr += " and " + T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(ymd));
      }
      if(!T9Utility.isNullorEmpty(beginDate)){ 
        whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">=");
      } 
      if(!T9Utility.isNullorEmpty(endDate)){ 
       whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=");
      }
//      if(T9Utility.isNullorEmpty(beginDate) && T9Utility.isNullorEmpty(endDate)){
//        whereStr += " and "+ T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(curDateStr));
//      }
      String[] str = {"USER_ID = '" + userId + "' and STATUS = '1' and OVERTIME_TYPE = '"+overtimeType+"'"+ whereStr+"" };
      overList = logic.selectOvertime(dbConn, str);
      String data = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
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
  
  /**
   * 获取加班时长--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOverTimeStat (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String overtimeType = request.getParameter("overtimeType");
      String userId = request.getParameter("userId");
      String curDateStr = T9Utility.getCurDateTimeStr().substring(0, 10);
      T9OvertimeRecordLogic logic = new T9OvertimeRecordLogic();
      List<T9OvertimeRecord> overList = new ArrayList<T9OvertimeRecord>();
      T9PersonLogic tpl = new T9PersonLogic();
      String whereStr = "";
      if(!T9Utility.isNullorEmpty(beginDate)){ 
        whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", beginDate, ">=");
      } 
      if(!T9Utility.isNullorEmpty(endDate)){ 
       whereStr += " and "+ T9DBUtility.getDateFilter("BEGIN_TIME", endDate, "<=");
      }
      if(T9Utility.isNullorEmpty(beginDate) && T9Utility.isNullorEmpty(endDate)){
        whereStr += " and "+ T9DBUtility.getMonthFilter("BEGIN_TIME", T9Utility.parseDate(curDateStr));
      }
      String[] str = {"USER_ID = '" + userId + "' and STATUS = '1' and OVERTIME_TYPE = '"+overtimeType+"'"+ whereStr+"" };
      overList = logic.selectOvertime(dbConn, str);
      String data = "[";
      for (int i = 0; i < overList.size(); i++) {
        T9OvertimeRecord overtime = overList.get(i);
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
  
  /**
   * 个人加班总时长  1-平时,2-周末,3-节假日
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTotalAdd (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(user.getSeqId());
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String overtimeType = request.getParameter("overtimeType");
      String curDateStr = T9Utility.getCurDateTimeStr().substring(0, 10);
      String data = "";
      if("1".equals(overtimeType)){
        data = String.valueOf(this.logic.getNormalAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      if("2".equals(overtimeType)){
        data = String.valueOf(this.logic.getWeekAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      if("3".equals(overtimeType)){
        data = String.valueOf(this.logic.getFestivalAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 加班总时长统计  1-平时,2-周末,3-节假日
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTotalAddStat (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String overtimeType = request.getParameter("overtimeType");
      String userId = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String curDateStr = T9Utility.getCurDateTimeStr().substring(0, 10);
      String data = "";
      if("1".equals(overtimeType)){
        data = String.valueOf(this.logic.getNormalAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      if("2".equals(overtimeType)){
        data = String.valueOf(this.logic.getWeekAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      if("3".equals(overtimeType)){
        data = String.valueOf(this.logic.getFestivalAddLogic(dbConn, beginDate, endDate, userId, overtimeType, curDateStr, year, month));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取加班基本信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOverTimeInfo (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String beginTime = request.getParameter("beginTime");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9AddWorkFeeLogic awfl = new T9AddWorkFeeLogic();
      String begin = beginTime + " " + beginDate;
      String end = beginTime + " " + endDate;
      String data = awfl.accountAddWorkFee(dbConn, beginTime, begin, end, Integer.parseInt(person.getUserPriv()));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data );
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取值班基本信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDutyInfo (HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String beginTime = request.getParameter("beginTime");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      T9AddWorkFeeLogic awfl = new T9AddWorkFeeLogic();
      String begin = beginTime + " " + beginDate;
      String end = beginTime + " " + endDate;
      String data = awfl.accountAddDutyFee(dbConn, beginTime, begin, end, Integer.parseInt(person.getUserPriv()));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data );
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String data = this.logic.getUserNameLogic(dbConn, Integer.parseInt(userId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 加班总时长
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
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
  
  /**
   * 加班工资数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOverTimeMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
      double data = this.logic.getOverTimeMoneyLogic(dbConn, year, month, userId);
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
