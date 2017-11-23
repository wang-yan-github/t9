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
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.funcs.attendance.personal.logic.T9AttendOutLogic;
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

public class T9ManageOutAct {
  /**
   * 
   * 查询所有外出记录根据自己的ID审批人
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String selectOutManage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendOut out = new T9AttendOut();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
      SimpleDateFormat formatter2 = new SimpleDateFormat("E"); 
      int userId = user.getSeqId();
      Map map = new HashMap();
      //System.out.println(userId);
      map.put("LEADER_ID", String.valueOf(userId));//审批人员
      map.put("STATUS", "0");
      map.put("ALLOW", "0");
      String[] str = {"LEADER_ID = '" + userId + "'","STATUS='0'","ALLOW='0' order by CREATE_DATE"};
      List<T9AttendOut> outList = t9aol.selectOutManage(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();     
      String data = "[";
      for (int i = 0; i < outList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        out = outList.get(i);
        String submitTime = formatter1.format(out.getSubmitTime());
        String applyName = tpl.getNameBySeqIdStr(out.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        String week = formatter2.format(out.getSubmitTime());
        //System.out.println(week);
        //String mydate = formatter2.format(formatter1.parse(rs.getString("REGISTER_TIME")));
        String deptName = t9aol.selectByUserIdDept(dbConn, out.getUserId());
        
        int runId = fu.isRunHook(dbConn, "OUT_ID", out.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        data = data + T9FOM.toJson(outList.get(i)).toString().substring(0, T9FOM.toJson(outList.get(i)).toString().length()-1 ) + ",week:\"" + week+"\",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",applyName:\"" +applyName +"\",deptName:\"" + deptName +"\"},";
      }
      if(outList.size()>0){
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
      String checkOut = request.getParameter("checkOut");
      //System.out.println(checkOut);
      //System.out.println(seqId+allow);
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("allow", allow);
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      t9aol.updateStatus(dbConn, map);
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("您的外出申请已被批准！");
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      if(checkOut!=null&&checkOut.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的外出申请已被批准！", new Date());
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
      String seqId = request.getParameter("seqId");
      String allow = request.getParameter("allow");
      String reason = request.getParameter("reason");
      String userId = request.getParameter("userId");
      String checkOut = request.getParameter("checkOut");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      reason = reason.replaceAll("\\\n", "");
      reason = reason.replaceAll("\\\r", "");
      //System.out.println(seqId+allow);
      T9AttendOut out = new T9AttendOut();
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("allow", allow);
      map.put("reason", reason);
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      t9aol.updateStatus(dbConn, map);
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("您的外出申请未被批准！");
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      
      if(checkOut!=null&&checkOut.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的外出申请未被批准！原因："+reason, new Date());
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
   * 得到开始日期和结束日期
   * 得到UserId
   */
  public String selectOut(HttpServletRequest request,
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
      List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      if(!userId.equals("")){
        String newUserId = "";
        String[] userIdArray = userId.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
          newUserId = newUserId + "'" + userIdArray[i] + "',";
        }
        newUserId = newUserId.substring(0, newUserId.length()-1);
        String str[] = {"USER_ID in(" + newUserId + ")",T9DBUtility.getDateFilter("SUBMIT_TIME", beginTime, ">="),T9DBUtility.getDateFilter("SUBMIT_TIME", endTime, "<=")+" order by SUBMIT_TIME"};
        outList = t9aol.selectOutManage(dbConn, str);
      }
      String data = "[";
      T9PersonLogic tpl = new T9PersonLogic();
      for (int i = 0; i < outList.size(); i++) {
        T9AttendOut out = outList.get(i);
        String userName = tpl.getNameBySeqIdStr(out.getUserId(), dbConn);
        if(userName!=null&&!userName.equals("")){
          userName = T9Utility.encodeSpecial(userName);
        }
        String leaderName = tpl.getNameBySeqIdStr(out.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        String deptName = t9aol.selectByUserIdDept(dbConn, out.getUserId());
        data = data + T9FOM.toJson(outList.get(i)).toString().substring(0, T9FOM.toJson(outList.get(i)).toString().length()-1 ) + ",userName:\""+userName+"\",leaderName:\"" +leaderName +"\",deptName:\"" + deptName +"\"},";
      }
      if(outList.size()>0){
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
   * 根据ID更改外出的时间
   * 
   */
  public String updateOutById(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String outDate = request.getParameter("outDate");
      String outTime1 = request.getParameter("outTime1");
      String outTime2 = request.getParameter("outTime2");
      String outType = request.getParameter("outType");
      outType = outType.replaceAll("\\\n", "");
      outType = outType.replaceAll("\\\r", "");
      T9AttendOut  out = new T9AttendOut();
      T9AttendOutLogic outLogic = new T9AttendOutLogic();
      out = outLogic.selectOutById(dbConn, seqId);
      out.setOutTime1(outTime1);
      out.setOutTime2(outTime2);
      out.setOutType(outType);
      out.setSubmitTime(T9Utility.parseDate(outDate + " " + outTime1 + ":00"));
      outLogic.updateOut(dbConn, out);
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
