package t9.custom.attendance.act;

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
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.custom.attendance.data.T9PersonalLeave;
import t9.custom.attendance.logic.T9PersonalLeaveLogic;

public class T9PersonalLeaveAct {
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
      T9PersonalLeave leave = new T9PersonalLeave();
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      Date curDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String smsRemind = request.getParameter("smsRemind");
      String leaveType = request.getParameter("leaveType");
      String userSeqId = request.getParameter("user");
      T9FOM fom = new T9FOM();
      leave = (T9PersonalLeave) fom.build(request.getParameterMap());
      leave.setAllow("0");
      leave.setStatus("1");
      leave.setUserId(String.valueOf(userId));
      if(!T9Utility.isNullorEmpty(leaveDate1)){
        leave.setLeaveDate1(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate1));
      }
      if(!T9Utility.isNullorEmpty(leaveDate2)){
        leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
      }
      leave.setApplyTime(new Date());
      logic.addLeave(dbConn, leave);
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
    response.sendRedirect(path+ "/custom/attendance/personal/leave/index.jsp");
    return "";
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
      T9PersonalLeave leave = new T9PersonalLeave();
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      Date curDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String leaveDate1 = request.getParameter("leaveDate1");
      String leaveDate2 = request.getParameter("leaveDate2");
      String smsRemind = request.getParameter("smsRemind");
      String leaveType = request.getParameter("leaveType");
      String userSeqId = request.getParameter("user");
      String seqId = request.getParameter("seqId");
      T9FOM fom = new T9FOM();
      leave = (T9PersonalLeave) fom.build(request.getParameterMap());
      if(!T9Utility.isNullorEmpty(seqId)){
        leave.setAllow("0");
        leave.setStatus("1");
        leave.setSeqId(Integer.parseInt(seqId));
        leave.setUserId(String.valueOf(userId));
        if(!T9Utility.isNullorEmpty(leaveDate1)){
          leave.setLeaveDate1(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate1));
        }
        if(!T9Utility.isNullorEmpty(leaveDate2)){
          leave.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",leaveDate2));
        }
        leave.setApplyTime(new Date());
        logic.updateLeave(dbConn, leave);
        //短信smsType, content, remindUrl, toId, fromId
        if(smsRemind!=null){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("6");
          sb.setContent("提交请假申请，请批示！");
          sb.setRemindUrl("/custom/attendance/attendmanage/index.jsp");
          sb.setToId(leave.getLeaderId());
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
        String moblieSmsRemind = request.getParameter("moblieSmsRemind");
        if(moblieSmsRemind!=null){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,leave.getLeaderId(), userId, "提交请假申请，请批示:" + leaveType, new Date());
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
    response.sendRedirect(path+ "/custom/attendance/personal/leave/index.jsp");
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
  public String selectLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "(STATUS = '1' and (ALLOW = '0' or ALLOW = '2') )"};
      List<T9PersonalLeave> leaveList = logic.selectLeave(dbConn, str);
      String data = "[";
      T9PersonLogic tpl = new T9PersonLogic();
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonalLeave leave = leaveList.get(i);
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
  public String selectHistroyLeave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      String[] str = {"USER_ID = '" + userId + "'" , "ALLOW = '1'"};
      List<T9PersonalLeave> leaveList = logic.selectLeave(dbConn, str);
      String data = "[";
      T9PersonLogic tpl = new T9PersonLogic();
      for (int i = 0; i < leaveList.size(); i++) {
        T9PersonalLeave leave = leaveList.get(i);
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
  public String deleteLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
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
  public String selectLeaveById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      String seqId = request.getParameter("seqId");
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      String data = "";
      if(!T9Utility.isNullorEmpty(seqId)){
        T9PersonalLeave leave = logic.selectLeaveById(dbConn, seqId);
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
   * 更改状态不同意
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAllowReason(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String userId = request.getParameter("userId");
      String checkLeave = request.getParameter("checkLeave");
      String reason = request.getParameter("reason");
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      if (!T9Utility.isNullorEmpty(seqId)) {
        logic.updateLeaveAllow(dbConn, seqId, allow, reason);
        // 短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的请假申请未被批准！");
        sb.setRemindUrl("/custom/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        // 发送手机短信
        if (checkLeave != null && checkLeave.equals("1")) {
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userId, userSeqId, "您的请假申请未被批准！",new Date());
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
   * 更改状态同意
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAllowReasonOn(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String userId = request.getParameter("userId");
      String checkLeave = request.getParameter("checkLeave");
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      if (!T9Utility.isNullorEmpty(seqId)) {
        logic.updateLeaveAllow(dbConn, seqId, allow, "");
        // 短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的请假申请已被批准！");
        sb.setRemindUrl("/custom/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        // 发送手机短信
        if (checkLeave != null && checkLeave.equals("1")) {
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userId, userSeqId, "您的请假申请已被批准！",new Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/custom/attendance/attendmanage/leadermanage/index.jsp";
  }
  
  /**
   * 
   * 更新请假记录allow,申请销假
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
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String checkLeave = request.getParameter("checkLeave");
      String dateStr = T9Utility.getDateTimeStr(new Date());
      T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9PersonalLeave personLeave = logic.selectLeaveById(dbConn, seqId);
        if(personLeave!=null){
          personLeave.setAllow(allow);
          personLeave.setDestroyTime(new Date());
          logic.updateLeave(dbConn, personLeave);
        //短信smsType, content, remindUrl, toId, fromId
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("6");
          sb.setContent("提交销假申请，请批示！");
          sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
          sb.setToId(personLeave.getLeaderId());
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
          if(checkLeave!=null&&checkLeave.equals("1")){
            T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
            sms2Logic.remindByMobileSms(dbConn,personLeave.getLeaderId(), userId, "提交请假销假，请批示！", new Date());
          }
        }
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
   * 
   * 更新请假记录status,审批人员销假
   * @param request
   * @param response
   * @return
   * @throws Exception
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
          T9PersonalLeaveLogic logic = new T9PersonalLeaveLogic();
          if (!T9Utility.isNullorEmpty(seqId)) {
            logic.updateLeaveStatus(dbConn, seqId, status);
            //短信smsType, content, remindUrl, toId, fromId
            T9SmsBack sb = new T9SmsBack();
            sb.setSmsType("6");
            sb.setContent("您的销假申请已被批准，已销假！");
            sb.setRemindUrl("/custom/attendance/personal/index.jsp");
            sb.setToId(userId);
            sb.setFromId(userSeqId);
            T9SmsUtil.smsBack(dbConn, sb);
            
            if(checkLeave!=null&&checkLeave.equals("1")){
              T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
              sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的销假申请已被批准，已销假！", new Date());
            }
          }
         
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
        }catch(Exception ex) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
          throw ex;
        }
        return "/custom/attendance/attendmanage/leadermanage/index.jsp"; 
      }
}
