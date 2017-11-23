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
import t9.core.funcs.attendance.manage.logic.T9ManageEvectionLogic;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.attendance.personal.logic.T9AttendEvectionLogic;
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

public class T9ManageEvectionAct {
  /**
   * 
   * 查询所有出差记录根据自己的ID审批人
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String selectEvectionManage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendEvection evection = new T9AttendEvection();
      T9ManageEvectionLogic t9ael = new T9ManageEvectionLogic();
      T9ManageOutLogic t9aol = new T9ManageOutLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
      SimpleDateFormat formatter2 = new SimpleDateFormat("E"); 
      int userId = user.getSeqId();
      Map map = new HashMap();
      map.put("STATUS", "1");
      map.put("ALLOW", "0");
      List<T9AttendEvection> evectionList = t9ael.selectEvectionManage(dbConn, map);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      String data = "[";
      for (int i = 0; i < evectionList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        evection = evectionList.get(i);
        String applyName = tpl.getNameBySeqIdStr(evection.getUserId() , dbConn);
        if(applyName!=null&&!applyName.equals("")){
          applyName = T9Utility.encodeSpecial(applyName);
        }
        String week1 = formatter2.format(evection.getEvectionDate1());
        String week2 = formatter2.format(evection.getEvectionDate2());
        int runId = fu.isRunHook(dbConn, "EVECTION_ID", evection.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        String deptName = t9aol.selectByUserIdDept(dbConn, evection.getUserId());
        data = data + T9FOM.toJson(evectionList.get(i)).toString().substring(0, T9FOM.toJson(evectionList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+ flowId +"\",week1:\"" + week1+ "\",week2:\"" + week2+ "\",applyName:\"" +applyName +"\",deptName:\"" + deptName +"\"},";
      }
      if(evectionList.size()>0){
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
  /**
   * 更改出差状态(批准)
   * @param request
   * @param response
   * @return
   * @throws Exception
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
      if(T9Utility.isInteger(seqId)){
        Map map = new HashMap();
        map.put("seqId", seqId);
        map.put("allow", allow);
        T9AttendEvectionLogic t9all = new T9AttendEvectionLogic();
        t9all.updateEvectionStatus(dbConn, map);
        //短信smsType, content, remindUrl, toId, fromId
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("您的出差申请已被批准！");
        sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
        sb.setToId(userId);
        sb.setFromId(userSeqId);
        T9SmsUtil.smsBack(dbConn, sb);
        String checkEvection = request.getParameter("checkEvection");
        if(checkEvection!=null&&checkEvection.equals("1")){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的出差申请已被批准", new Date());
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
      String notReason = request.getParameter("notReason");
      String userId = request.getParameter("userId");
      notReason = notReason.replaceAll("\\\n","");
      notReason = notReason.replaceAll("\\\r","");
      Map map = new HashMap();
      map.put("seqId", seqId);
      map.put("allow", allow);
      map.put("notReason", notReason);
      T9AttendEvectionLogic t9all = new T9AttendEvectionLogic();
      t9all.updateEvectionStatus(dbConn, map);
      //短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("6");
      sb.setContent("您的出差申请未被批准！");
      sb.setRemindUrl("/core/funcs/attendance/personal/index.jsp");
      sb.setToId(userId);
      sb.setFromId(userSeqId);
      T9SmsUtil.smsBack(dbConn, sb);
      
      String checkEvection = request.getParameter("checkEvection");
      if(checkEvection!=null&&checkEvection.equals("1")){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,userId, userSeqId, "您的出差申请未被批准！内容："+notReason, new Date());
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
  public String selectEvection(HttpServletRequest request,
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
          T9ManageEvectionLogic evectionLogic = new T9ManageEvectionLogic();
          T9ManageOutLogic t9aol = new T9ManageOutLogic();
          List<T9AttendEvection> evectionList = new ArrayList<T9AttendEvection>();
          if(!userId.equals("")){
            String newUserId = "";
            String[] userIdArray = userId.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              newUserId = newUserId + "'" + userIdArray[i] + "',";
            }
            newUserId = newUserId.substring(0, newUserId.length()-1);
            String str[] = {"USER_ID in(" + userId + ")",T9DBUtility.getDateFilter("EVECTION_DATE1", beginTime, ">="),T9DBUtility.getDateFilter("EVECTION_DATE2", endTime, "<="),"ALLOW='1' order by EVECTION_DATE1"};
            evectionList = evectionLogic.selectEvectionManage(dbConn, str);
          }
          String data = "[";
          T9PersonLogic tpl = new T9PersonLogic();
          for (int i = 0; i < evectionList.size(); i++) {
            T9AttendEvection evection = evectionList.get(i);
            String userName = tpl.getNameBySeqIdStr(evection.getUserId(), dbConn);
            if(userName!=null&&!userName.equals("")){
              userName = T9Utility.encodeSpecial(userName);
            }
            String leaderName = tpl.getNameBySeqIdStr(evection.getLeaderId() , dbConn);
            if(leaderName!=null&&!leaderName.equals("")){
              leaderName = T9Utility.encodeSpecial(leaderName);
            }
            String deptName = t9aol.selectByUserIdDept(dbConn, evection.getUserId());
            data = data + T9FOM.toJson(evectionList.get(i)).toString().substring(0, T9FOM.toJson(evectionList.get(i)).toString().length()-1 ) + ",userName:\"" + userName + "\",leaderName:\"" +leaderName +"\",deptName:\"" + deptName +"\"},";
          }
          if(evectionList.size()>0){
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
  public String updateEvectionById(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String evectionDate1 = request.getParameter("evectionDate1");
      String evectionDate2 = request.getParameter("evectionDate2");
      String reason = request.getParameter("reason");
      reason = reason.replaceAll("\\\n","");
      reason = reason.replaceAll("\\\r","");
      String evectionDest = request.getParameter("evectionDest");
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic evectionLogic = new T9AttendEvectionLogic();
      evection = evectionLogic.selectEvectionById(dbConn, seqId);
      evection.setReason(reason);
      if(!evectionDate1.equals("")){
        evection.setEvectionDate1(T9Utility.parseDate(evectionDate1));
      }else{
        evection.setEvectionDate1(null);
      }
      if(!evectionDate2.equals("")){
        evection.setEvectionDate2(T9Utility.parseDate(evectionDate2));
      }else{
        evection.setEvectionDate2(null);
      }
      evection.setEvectionDest(evectionDest);
      evectionLogic.updateEvection(dbConn, evection);
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
