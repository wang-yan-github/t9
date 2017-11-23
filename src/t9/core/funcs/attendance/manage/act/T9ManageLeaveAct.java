package t9.core.funcs.attendance.manage.act;

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
import t9.core.funcs.attendance.manage.logic.T9ManageLeaveLogic;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
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
import t9.subsys.oa.fillRegister.logic.T9AttendFillLogic;

public class T9ManageLeaveAct {
  /**
   * 
   * 查询所有放假记录根据自己的ID审批人
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String selectLeaveManage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendLeave leave = new T9AttendLeave();
      T9ManageLeaveLogic t9all = new T9ManageLeaveLogic();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
      SimpleDateFormat formatter2 = new SimpleDateFormat("E"); 
      int userId = user.getSeqId();
      List<T9AttendLeave> leaveList = t9all.selectLeaveManage(dbConn,  userId);
      String data = "[";
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        leave = leaveList.get(i);
        String applyName = tpl.getNameBySeqIdStr(leave.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        String week1 = formatter2.format(leave.getLeaveDate1());
        String week2 = formatter2.format(leave.getLeaveDate2());
        String deptName = t9aol.selectByUserIdDept(dbConn, leave.getUserId());
        int runId = fu.isRunHook(dbConn, "LEAVE_ID", leave.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+ flowId +"\",week1:\"" + week1+ "\",week2:\"" + week2+ "\",applyName:\"" +applyName +"\",deptName:\"" + deptName +"\"},";
      }
      if(leaveList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
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
  /*
   * 
   * updateStatus ById
   */
  public String updateStatus(HttpServletRequest request,
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
      String checkLeave = request.getParameter("checkLeave");
      if(T9Utility.isInteger(seqId)){
        Map map = new HashMap();
        map.put("seqId", seqId);
        map.put("allow", allow);
        T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
        T9AttendLeave leave = t9all.selectLeaveById(dbConn, seqId);
        T9AttendFillLogic fillLogic = new T9AttendFillLogic();
        if(leave != null){
          fillLogic.addAttendScoreLeave(dbConn, user, T9Utility.getDateTimeStr(leave.getLeaveDate1()),  T9Utility.getDateTimeStr(leave.getLeaveDate2()), leave.getUserId(), leave.getLeaderId());    
        }
        t9all.updateStatus(dbConn, map);
        //短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的请假申请已被批准！");
        sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        
        if(checkLeave!=null&&checkLeave.equals("1")){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的请假申请已被批准！", new Date());
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
  /*
   * 
   * updateStatus ById
   */
  public String updateReason(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String reason = request.getParameter("reason");
      String userId = request.getParameter("userId");
      reason = reason.replaceAll("\\\n","");
      reason = reason.replaceAll("\\\r","");
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("allow", allow);
      map.put("reason", reason);
      //System.out.println(seqId+allow);
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      t9all.updateStatus(dbConn, map);
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("您的请假申请未被批准！");
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      String checkLeave = request.getParameter("checkLeave");
      if(checkLeave!=null&&checkLeave.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的请假申请未被批准！原因："+reason, new Date());
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
  /*
   * 
   * updateStatus ById
   */
  public String updateDestroyStatus(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String status = request.getParameter("status");
      String userId = request.getParameter("userId");
      String checkLeave = request.getParameter("checkLeave");
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("status", status);
      T9AttendLeaveLogic t9all = new T9AttendLeaveLogic();
      t9all.updateStatus(dbConn, map);
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("您的销假申请已被批准，已销假！");
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      
      if(checkLeave!=null&&checkLeave.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的销假申请已被批准，已销假！", new Date());
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
  /*
   * 得到开始日期和结束日期
   * 得到UserId
   */
  public String selectLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
          T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
          String userId = request.getParameter("userId");
          String beginTime = request.getParameter("beginTime");
          String endTime = request.getParameter("endTime");
          beginTime = beginTime + " 00:00:00";
          endTime = endTime + " 23:59:59";
          //System.out.println(beginTime+endTime);
          T9ManageLeaveLogic leaveLogic = new T9ManageLeaveLogic();
          T9ManageOutLogic t9aol = new T9ManageOutLogic();
          List<T9AttendLeave> leaveList = new ArrayList<T9AttendLeave>();
          if(!userId.equals("")){
            String newUserId = "";
            String[] userIdArray = userId.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              newUserId = newUserId + "'" + userIdArray[i] + "',";
            }
            newUserId = newUserId.substring(0, newUserId.length()-1);
            String str[] = {"USER_ID in(" + userId + ")",T9DBUtility.getDateFilter("LEAVE_DATE1", beginTime, ">="),T9DBUtility.getDateFilter("LEAVE_DATE2", endTime, "<="),"ALLOW in('1','3') order by LEAVE_DATE1"};
            leaveList =  leaveLogic.selectLeave(dbConn, str);
          }
       
          String data = "[";
          T9PersonLogic tpl = new T9PersonLogic();
          for (int i = 0; i < leaveList.size(); i++) {
            T9AttendLeave leave = leaveList.get(i);
            String userName = tpl.getNameBySeqIdStr(leave.getUserId(), dbConn);
            if(userName!=null&&!userName.equals("")){
              userName = T9Utility.encodeSpecial(userName);
            }
            String leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
            if(leaderName!=null&&!leaderName.equals("")){
              leaderName = leaderName.substring(0, leaderName.length()-1);
              leaderName = T9Utility.encodeSpecial(leaderName);
            }
            String deptName = t9aol.selectByUserIdDept(dbConn, leave.getUserId());
            data = data + T9FOM.toJson(leaveList.get(i)).toString().substring(0, T9FOM.toJson(leaveList.get(i)).toString().length()-1 ) + ",userName:\""+userName+"\",leaderName:\"" +leaderName +"\",deptName:\"" + deptName +"\"},";
          }
          if(leaveList.size()>0){
            data = data.substring(0, data.length()-1);
          }
          data = data + "]";
          //System.out.println(data);
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
  /*
   * 更新ById
   */
  public String updateLeaveById(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String leaveType = request.getParameter("leaveType");
      String annualLeave = request.getParameter("annualLeave");
      String destroyTime = request.getParameter("destroyTime");
      leaveType = leaveType.replaceAll("\\\n","");
      leaveType = leaveType.replaceAll("\\\r","");
      T9AttendLeave leave = new T9AttendLeave();
      T9AttendLeaveLogic leaveLogic = new T9AttendLeaveLogic();
      leave = leaveLogic.selectLeaveById(dbConn, seqId);
      leave.setLeaveType(leaveType);
      if(!leaveDate1.equals("")){
        leave.setLeaveDate1(T9Utility.parseDate(leaveDate1));
      }else{
        leave.setLeaveDate1(null);
      }
      if(!leaveDate2.equals("")){
        leave.setLeaveDate2(T9Utility.parseDate(leaveDate2));
      }else{
        leave.setLeaveDate2(null);
      }
      if(!destroyTime.equals("")){
        leave.setDestroyTime(T9Utility.parseDate(destroyTime));
      }else{
        leave.setDestroyTime(null);
      }
      leaveLogic.updateLeave(dbConn, leave);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{}");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

}
