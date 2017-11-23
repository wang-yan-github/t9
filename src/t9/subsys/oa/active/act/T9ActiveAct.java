package t9.subsys.oa.active.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.subsys.oa.active.data.T9Active;
import t9.subsys.oa.active.logic.T9ActiveLogic;
import t9.core.funcs.calendar.act.T9CalendarAct;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9ActiveAct {
  
  /*
  * 新建周活动安排
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String addActive(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     Date curDate = new Date();
     String activeUser = request.getParameter("activeUser");
     String activeContent = request.getParameter("activeContent");
     String date = request.getParameter("activeTime");
     String hour = request.getParameter("hour");
     String min = request.getParameter("min");
     T9Active active = new T9Active();
     active.setActiveUser(activeUser);
     active.setActiveTimeRang("0");
     active.setOverStatus("0");
     active.setActiveContent(activeContent);
     active.setOpUserId(String.valueOf(userId));
     active.setOpDatetime(curDate);
     String activeTimeStr = date + " " + hour + ":" + min + ":00";
     Date activeTime = dateFormat.parse(activeTimeStr);
     
     Date remindTime = new Date(activeTime.getTime() + 60 * 60 * 1000);
     
     active.setActiveTime(activeTime);
     
     String calendarstr = request.getParameter("calendar");
     String calendarIds = "";
     if (!T9Utility.isNullorEmpty(calendarstr)) {
       String cont = "活动安排: " + activeContent;
       T9Calendar calendar = new T9Calendar();
       T9CalendarLogic calendarLogic = new T9CalendarLogic();
       String mAttend = activeUser;
       String[] mAttendStr = mAttend.split(",");
       for(int x = 0; x < mAttendStr.length; x++){
         calendar.setEndTime(activeTime);
         calendar.setCalTime(activeTime);
         calendar.setUserId(mAttendStr[x]);
         calendar.setCalType("1");
         calendar.setCalLevel("1");
         calendar.setContent(cont);
         calendar.setManagerId(String.valueOf(user.getSeqId()));
         calendar.setOverStatus("0");
         int newCalendar = calendarLogic.addCalendar(dbConn, calendar);
         calendarIds += newCalendar + ",";
       }
     }
     active.setCalendars(calendarIds);
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     activeLogic.addActive(dbConn, active);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
     //request.setAttribute(T9ActionKeys.RET_DATA, "data");
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }
 /*
  * 更新周活动安排
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String updateActive(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     String seqId = request.getParameter("seqId");
     Date curDate = new Date();
     String activeUser = request.getParameter("activeUser");
     String activeContent = request.getParameter("activeContent");
     String date = request.getParameter("activeTime");
     String hour = request.getParameter("hour");
     String min = request.getParameter("min");
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     if(seqId!=null&&!seqId.equals("")){
       T9Active active = new T9Active();
       active.setSeqId(Integer.parseInt(seqId));
       active.setActiveUser(activeUser);
       active.setActiveTimeRang("0");
       active.setOverStatus("0");
       active.setActiveContent(activeContent);
       active.setOpUserId(String.valueOf(userId));
       active.setOpDatetime(curDate);
       String activeTimeStr = date + " " + hour + ":" + min + ":00";
       Date activeTime = dateFormat.parse(activeTimeStr);
       active.setActiveTime(activeTime);
      
       
       String calendarstr = request.getParameter("calendar");
       String calendarIds = "";
       if (!T9Utility.isNullorEmpty(calendarstr)) {
         String cont = "活动安排: " + activeContent;
         T9Calendar calendar = new T9Calendar();
         T9CalendarLogic calendarLogic = new T9CalendarLogic();
         String mAttend = activeUser;
         String[] mAttendStr = mAttend.split(",");
         for(int x = 0; x < mAttendStr.length; x++){
           calendar.setEndTime(activeTime);
           calendar.setCalTime(activeTime);
           calendar.setUserId(mAttendStr[x]);
           calendar.setCalType("1");
           calendar.setCalLevel("1");
           calendar.setContent(cont);
           calendar.setManagerId(String.valueOf(user.getSeqId()));
           calendar.setOverStatus("0");
           int newCalendar =  calendarLogic.addCalendar(dbConn, calendar);
           calendarIds += newCalendar + ",";
         }
       }
       active.setCalendars(calendarIds);
       activeLogic.delActiveCadenarById(dbConn, active.getSeqId());
       activeLogic.updateActive(dbConn, active);
     }
   /*    //短信smsType, content, remindUrl, toId, fromId
       T9SmsBack sb = new T9SmsBack();
       sb.setSmsType("5");
       sb.setContent("请查看日程安排！内容："+content);
       sb.setRemindUrl("/t9/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
       sb.setToId(String.valueOf(userId));
       sb.setFromId(userId);
       T9SmsUtil.smsBack(dbConn, sb);*/
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
     //request.setAttribute(T9ActionKeys.RET_DATA, "data");
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }
 /*
  * 查询周活动安排（在指定一周之内）
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String selectActiveByWeek(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     T9ActiveLogic logic = new T9ActiveLogic();
     String userIds = logic.getUserIds(dbConn);
     boolean hasPriv = T9WorkFlowUtility.findId(userIds, String.valueOf(userId));
     
     String beginDate = request.getParameter("beginDate");
     String endDate = request.getParameter("endDate");
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     List<T9Active> activeList = activeLogic.selectActiveByWeek(dbConn, beginDate, endDate,"");
     T9PersonLogic personLogic = new T9PersonLogic();
     String data = "[";
     for (int i = 0; i < activeList.size(); i++) {
      T9Active active = activeList.get(i);
      String opUserIdName = "";
      String activeUserName = "";
      if(active.getOpUserId()!=null&&!active.getOpUserId().trim().equals("")){
        opUserIdName = personLogic.getNameBySeqIdStr(active.getOpUserId(), dbConn);
      }
      if(active.getActiveUser()!=null&&!active.getActiveUser().trim().equals("")){
        activeUserName = personLogic.getNameBySeqIdStr(active.getActiveUser(), dbConn);
      }
      data = data + T9FOM.toJson(active).toString().substring(0, T9FOM.toJson(active).toString().length()-1)+",opUserIdName:\""+opUserIdName+"\",activeUserName:\""+ activeUserName+ "\"},";
     }
     if(activeList.size()>0){
       data = data.substring(0, data.length()-1);
     }
     data = data + "]";
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
     request.setAttribute(T9ActionKeys.RET_DATA, data + ",hasPriv:" + hasPriv);
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }
 /*
  * 查询周活动安排（ById）
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String selectActiveById(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     String seqId = request.getParameter("seqId");
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     T9PersonLogic personLogic = new T9PersonLogic();
     String data = "";
     if(seqId!=null&&!seqId.equals("")){
       T9Active active = activeLogic.selectActiveById(dbConn, Integer.parseInt(seqId));
       if(active!=null){
         String opUserIdName = "";
         String activeUserName = "";
         if(active.getOpUserId()!=null&&!active.getOpUserId().trim().equals("")){
           opUserIdName = personLogic.getNameBySeqIdStr(active.getOpUserId(), dbConn);
         }
         if(active.getActiveUser()!=null&&!active.getActiveUser().trim().equals("")){
           activeUserName = personLogic.getNameBySeqIdStr(active.getActiveUser(), dbConn);
         }
         data = data + T9FOM.toJson(active).toString().substring(0, T9FOM.toJson(active).toString().length()-1) +",opUserIdName:\""+opUserIdName+"\",activeUserName:\""+ activeUserName+ "\"}";
       }
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
 /*
  * 删除周活动安排（ById）
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String delActiveById(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn =
       (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     String seqId = request.getParameter("seqId");
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     String data = "";
     if(seqId!=null&&!seqId.equals("")){
       activeLogic.delActiveCadenarById(dbConn, Integer.parseInt(seqId));
         activeLogic.delActiveById(dbConn, Integer.parseInt(seqId));
     }
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
     //request.setAttribute(T9ActionKeys.RET_DATA, data);
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }
 /*
  * 查询周活动安排（在今日||本周之内的）
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String selectActive(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     Date curDate = new Date();
     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
     T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     int userId = user.getSeqId();
     String index = request.getParameter("index");
     String data = "[";
     List<T9Active> activeList = new ArrayList<T9Active>();
     T9ActiveLogic activeLogic = new T9ActiveLogic();
     if (!T9Utility.isNullorEmpty(index)){
       if(index.equals("1")){
         String beginDate = dateFormat.format(curDate);
         activeList = activeLogic.selectActiveByWeek(dbConn, beginDate, beginDate,String.valueOf(userId));
       }
       if(index.equals("2")){
         T9CalendarAct  calendarAct = new T9CalendarAct();
         Calendar[] darr = calendarAct.getStartEnd();
         String beginDate = calendarAct.getFullTimeStr(darr[0]);
         String endDate = calendarAct.getFullTimeStr(darr[1]) ;
         activeList = activeLogic.selectActiveByWeek(dbConn, beginDate, endDate,String.valueOf(userId));
       }
     }
     T9PersonLogic personLogic = new T9PersonLogic();
     for (int i = 0; i < activeList.size(); i++) {
      T9Active active = activeList.get(i);
      String opUserIdName = "";
      String activeUserName = "";
      if(active.getOpUserId()!=null&&!active.getOpUserId().trim().equals("")){
        opUserIdName = personLogic.getNameBySeqIdStr(active.getOpUserId(), dbConn);
      }
      if(active.getActiveUser()!=null&&!active.getActiveUser().trim().equals("")){
        activeUserName = personLogic.getNameBySeqIdStr(active.getActiveUser(), dbConn);
      }
      data = data + T9FOM.toJson(active).toString().substring(0, T9FOM.toJson(active).toString().length()-1)+",opUserIdName:\""+opUserIdName+"\",activeUserName:\""+ activeUserName+ "\"},";
     }
     if(activeList.size()>0){
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
}
