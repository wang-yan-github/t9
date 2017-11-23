package t9.core.funcs.calendar.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Affair;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.info.logic.T9InfoLogic;
import t9.core.funcs.calendar.logic.T9AffairLogic;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.jexcel.logic.T9ExportLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9AffairAct {
  /***
   * 添加周期性事务
   */
  public String addAffair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Affair affair = new T9Affair();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date curDate = new Date();
      Date curDate1 = T9Utility.parseDate(T9Utility.getCurDateTimeStr("yyyy-MM-dd")+" 23:59:59");
      String curDateStr = dateFormat.format(curDate);
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String content = request.getParameter("content");
      String isWeekend = request.getParameter("isWeekend");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      String type = request.getParameter("type");
      //System.out.println(type);
      //判断今天是否可以为提醒时间
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.DAY_OF_WEEK);
      int day = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      if(week==0){
        week = 7;
      }else{
        week = week-1;
      }
      month = month+1;
      if(isWeekend!=null){
        affair.setIsWeekend("1");
      }
      String remindTime = request.getParameter("remindTime");
      String remindDate = "";
      if(type.equals("3")){
        remindDate = request.getParameter("remindDate3");
        remindTime = request.getParameter("remindTime3");
      }
      if(type.equals("4")){
        remindDate = request.getParameter("remindDate4");
        remindTime = request.getParameter("remindTime4");
      }
      if(type.equals("5")){
        remindDate = request.getParameter("remindDate5Mon")+"-"+request.getParameter("remindDate5Day");
        remindTime = request.getParameter("remindTime5");
      }
      affair.setUserId(String.valueOf(userId));
      if(beginTime.equals("")){
        affair.setBeginTime(curDate);
      }else{
        affair.setBeginTime(dateFormat.parse(beginTime));
      }
      if(!endTime.equals("")){
        affair.setEndTime(dateFormat.parse(endTime));
      }
      //System.out.println(remindTime);
      if(remindTime.equals("")){
        affair.setRemindTime(curDateStr.substring(11, 19));
      }else{
        affair.setRemindTime(remindTime);
      }
      //判断同时要不要加最后一次提醒时间
/*      if(affair.getBeginTime().compareTo(curDate1)<=0){
        if(type.equals("2")){
          affair.setLastRemind(curDate);
        }else if(type.equals("3")){
          if(remindDate.equals(String.valueOf(week))){
            affair.setLastRemind(curDate);
          }
        }else if(type.equals("4")&&remindDate.equals(String.valueOf(day))){
          affair.setLastRemind(curDate);
        }else if(type.equals("5")&&remindDate.equals(String.valueOf(month)+"-"+String.valueOf(day))){
          affair.setLastRemind(curDate);
        }
      }*/
      
      affair.setRemindDate(remindDate);
      affair.setContent(content);
      affair.setType(type);
      T9AffairLogic tal = new T9AffairLogic();
      int maxSeqId = tal.addAffair(dbConn, affair);
      /*//短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("5");
      sb.setContent("日常事务提醒："+content);
      sb.setRemindUrl("/core/funcs/calendar/affairnote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
      sb.setToId(String.valueOf(userId));
      sb.setFromId(userId);
      T9SmsUtil.smsBack(dbConn, sb);*/
      selectAffairRemindByToday(request, response); //判断时候需要提醒
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn, String.valueOf(userId), userId, "日常事务："+content, new Date());
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
    response.sendRedirect(path+"/core/funcs/calendar/Cycaffair.jsp");
    //return "/core/funcs/calendar/Cycaffair.jsp";
    return null;
  }
  /**
   * 更新事务
   */
  public String updateAffair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Affair affair = new T9Affair();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date curDate = new Date();
      Date curDate1 = T9Utility.parseDate(T9Utility.getCurDateTimeStr("yyyy-MM-dd")+" 23:59:59");
      String isWeekend = request.getParameter("isWeekend");
      //判断今天是否可以为提醒时间
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.DAY_OF_WEEK);
      int day = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      if(week==0){
        week = 7;
      }else{
        week = week-1;
      }
      month = month+1;
      
      String curDateStr = dateFormat.format(curDate);
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String content = request.getParameter("content");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      String type = request.getParameter("type");
      //System.out.println(type);
      String remindTime = request.getParameter("remindTime");
      String remindDate = "";
      if(isWeekend!=null){
        affair.setIsWeekend("1");
      }else{
        affair.setIsWeekend("");
      }
      if(type.equals("3")){
        remindDate = request.getParameter("remindDate3");
        remindTime = request.getParameter("remindTime3");
      }
      if(type.equals("4")){
        remindDate = request.getParameter("remindDate4");
        remindTime = request.getParameter("remindTime4");
      }
      if(type.equals("5")){
        remindDate = request.getParameter("remindDate5Mon")+"-"+request.getParameter("remindDate5Day");
        remindTime = request.getParameter("remindTime5");
      }
      affair.setSeqId(Integer.parseInt(seqId));
      affair.setUserId(String.valueOf(userId));
      if(beginTime.equals("")){
        affair.setBeginTime(curDate);
      }else{
        affair.setBeginTime(dateFormat.parse(beginTime));
      }
      if(!endTime.equals("")){
        affair.setEndTime(dateFormat.parse(endTime));
      }else{
        affair.setEndTime(null);
      }
      //System.out.println(remindTime);
      if(remindTime.equals("")){
        affair.setRemindTime(curDateStr.substring(11, 19));
      }else{
        affair.setRemindTime(remindTime);
      }
      //判断同时要不要加最后一次提醒时间
  /*    if(affair.getBeginTime().compareTo(curDate1)<=0){
        if(type.equals("2")){
          affair.setLastRemind(curDate);
        }else if(type.equals("3")){
          if(remindDate.equals(String.valueOf(week))){
            affair.setLastRemind(curDate);
          }
        }else if(type.equals("4")&&remindDate.equals(String.valueOf(day))){
          affair.setLastRemind(curDate);
        }else if(type.equals("5")&&remindDate.equals(String.valueOf(month)+"-"+String.valueOf(day))){
          affair.setLastRemind(curDate);
        }
      }*/
      affair.setRemindDate(remindDate);
      affair.setContent(content);
      affair.setType(type);
      T9AffairLogic tal = new T9AffairLogic();
      tal.updateAffair(dbConn, affair);
      /*//短信smsType, content, remindUrl, toId, fromId
      T9SmsBack sb = new T9SmsBack();
      sb.setSmsType("5");
      sb.setContent("日常事务提醒："+content);
      sb.setRemindUrl("/core/funcs/calendar/affairnote.jsp?seqId="+seqId+"&openFlag=1&openWidth=300&openHeight=250");
      sb.setToId(String.valueOf(userId));
      sb.setFromId(userId);
      T9SmsUtil.smsBack(dbConn, sb);*/
      selectAffairRemindByToday(request, response); //判断时候需要提醒
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn, String.valueOf(userId), userId, "日常事务："+content, new Date());
      }
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
  /**
   * 更新事务 :查询
   */
  public String updateAffairByUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Affair affair = new T9Affair();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date curDate = new Date();
      Date curDate1 = T9Utility.parseDate(T9Utility.getCurDateTimeStr("yyyy-MM-dd")+" 23:59:59");
      //判断今天是否可以为提醒时间
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.DAY_OF_WEEK);
      int day = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      if(week==1){
        week = 7;
      }else{
        week = week-1;
      }
      month = month+1;
      
      String curDateStr = dateFormat.format(curDate);
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String userId = request.getParameter("userId");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String content = request.getParameter("content");
      String isWeekend = request.getParameter("isWeekend");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      String type = request.getParameter("type");
      //System.out.println(type);
      String remindTime = request.getParameter("remindTime");
      String remindDate = "";
      if(type.equals("3")){
        remindDate = request.getParameter("remindDate3");
        remindTime = request.getParameter("remindTime3");
      }
      if(type.equals("4")){
        remindDate = request.getParameter("remindDate4");
        remindTime = request.getParameter("remindTime4");
      }
      if(type.equals("5")){
        remindDate = request.getParameter("remindDate5Mon")+"-"+request.getParameter("remindDate5Day");
        remindTime = request.getParameter("remindTime5");
      }
      T9AffairLogic tal = new T9AffairLogic();
      if(seqId!=null&&!seqId.equals("")){
        affair = tal.selectAffairById(dbConn, Integer.parseInt(seqId));
        if(affair!=null){
          affair.setUserId(userId);
          if(beginTime.equals("")){
            affair.setBeginTime(curDate);
          }else{
            affair.setBeginTime(dateFormat.parse(beginTime));
          }
          if(!endTime.equals("")){
            affair.setEndTime(dateFormat.parse(endTime));
          }else{
            affair.setEndTime(null);
          }
          //System.out.println(remindTime);
          if(remindTime.equals("")){
            affair.setRemindTime(curDateStr.substring(11, 19));
          }else{
            affair.setRemindTime(remindTime);
          }
          //判断同时要不要加最后一次提醒时间
  /*        if(affair.getBeginTime().compareTo(curDate1)<=0){
            if(type.equals("2")){
              affair.setLastRemind(curDate);
            }else if(type.equals("3")){
              if(remindDate.equals(String.valueOf(week))){
                affair.setLastRemind(curDate);
              }
            }else if(type.equals("4")&&remindDate.equals(String.valueOf(day))){
              affair.setLastRemind(curDate);
            }else if(type.equals("5")&&remindDate.equals(String.valueOf(month)+"-"+String.valueOf(day))){
              affair.setLastRemind(curDate);
            }
          }*/
          affair.setManagerId(String.valueOf(userSeqId));
          affair.setRemindDate(remindDate);
          affair.setContent(content);
          affair.setType(type);
          if(isWeekend!=null){
            affair.setIsWeekend("1");
          }else{
            affair.setIsWeekend("");
          }
          tal.updateAffair(dbConn, affair);
        /*  //短信smsType, content, remindUrl, toId, fromId
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          sb.setContent("日常事务提醒："+content);
          sb.setRemindUrl("/core/funcs/calendar/affairnote.jsp?seqId="+seqId+"&openFlag=1&openWidth=300&openHeight=250");
          sb.setToId(affair.getUserId());
          sb.setFromId(userSeqId);
          T9SmsUtil.smsBack(dbConn, sb);*/
          selectAffairRemindByToday(request, response); //判断时候需要提醒
          String moblieSmsRemind = request.getParameter("moblieSmsRemind");
          if(moblieSmsRemind!=null){
            T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
            sms2Logic.remindByMobileSms(dbConn,affair.getUserId(), userSeqId, "日常事务："+content, new Date());
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
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 查询所有 事务
   */
  public String selectAffair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String str[] = {"USER_ID="+userId + " order by BEGIN_TIME desc "};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = tal.selectAffair(dbConn, str);
      String data = "[";
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        data = data + T9FOM.toJson(affair)+",";
      }
      if(affairList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data+"]";
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
   * 事务分页查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAffairByPagin(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AffairLogic dl = new T9AffairLogic();
      String data = dl.toSearchData(dbConn,request.getParameterMap(),userId);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   *  按条件查询事务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAffairByTerm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String minTime = request.getParameter("minTime");
      String maxTime = request.getParameter("maxTime");
      String content = request.getParameter("content");
      T9AffairLogic  tal = new T9AffairLogic();
      List<Map<String,String>> affairList = tal.selectAffairByTerm(dbConn, userId,minTime,maxTime,content);
      request.setAttribute("affairList", affairList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/calendar/queryaffair.jsp";
  }
  /**
   *  
   * 查询单个事务ById
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAffairById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AffairLogic  tal = new T9AffairLogic();
      String data = ""; 
      if(seqId!=null&&!seqId.equals("")){
        T9Affair affair = tal.selectAffairById(dbConn, Integer.parseInt(seqId));
        if(affair!=null){
          String userName = T9InfoLogic.getUserName(affair.getUserId(), dbConn);
          if(userName!=null){
            userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          //System.out.println(affair.getManagerId());
          String managerName  = T9InfoLogic.getUserName(affair.getManagerId(), dbConn);
          if(managerName!=null){
            managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          data = data + T9FOM.toJson(affair).toString().substring(0, T9FOM.toJson(affair).toString().length()-1)+",managerName:\""+managerName+"\",userName:\""+userName+"\"}";        
        }else{
          data = "{}";
        }
      }
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
   *  
   * 删除多条事务
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAffair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIds = request.getParameter("seqIds");
      T9AffairLogic  tal = new T9AffairLogic();
      tal.deleteAffair(dbConn, seqIds);  
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/calendar/Cycaffair.jsp";
  }
  /**
   *   
   * 删除多条事务
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAffairById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AffairLogic  tal = new T9AffairLogic();
      tal.deleteAffairById(dbConn, Integer.parseInt(seqId));
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return  "/core/inc/rtjson.jsp";
  }
  /**
   *  把事务显示到日程安排中  按日查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
   
  public String selectAffairByDay(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String day = request.getParameter("day");
      String date = year+"-"+month+"-"+day;
      String dateEnd = date+" 23:59:59";
      //System.out.println(date);
      Date newDate = T9Utility.parseDate(date);//转换为DATE类型
      Calendar cld = Calendar.getInstance();
      cld.setTime(newDate);
      int weekInt = cld.get(Calendar.DAY_OF_WEEK);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateEnd, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", date, ">=");
      String str[]= {"USER_ID='" + userId + "'",beginDate};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = tal.selectAffair(dbConn, str);
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          String isWeekend  = affair.getIsWeekend(); 
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(isWeekend!=null&&isWeekend.equals("1")){
              if(weekInt!=1&weekInt!=7){
                if(affairListTemp.size()>0){
                  affair = affairListTemp.get(0);
                  affairList2.add(affair);
                }
              }
            }else{
              if(affairListTemp.size()>0){
                affair = affairListTemp.get(0);
                affairList2.add(affair);
              }
            }
          
          }else{
            if(isWeekend!=null&&isWeekend.equals("1")){
              if(weekInt!=1&weekInt!=7){
                affairList2.add(affair);
              }
            }else{
              affairList2.add(affair);
            }
          
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String week = affair.getRemindDate();
              GregorianCalendar d = new GregorianCalendar(); 
              Date mydate= dateFormat.parse(date); 
              d.setTime(mydate);
              int today = d.get(Calendar.DAY_OF_WEEK);
              if(today==1){
                today = 7;  
              }else{
                today = today - 1;
              }
              if(String.valueOf(today).equals(week)){
                affairList2.add(affair);
              }
            }
          }else{
            String week = affair.getRemindDate();
            GregorianCalendar d = new GregorianCalendar(); 
            Date mydate= dateFormat.parse(date); 
            d.setTime(mydate);
            int today = d.get(Calendar.DAY_OF_WEEK);
            if(today==1){
              today = 7;  
            }else{
              today = today - 1;
            }
            if(String.valueOf(today).equals(week)){
              affairList2.add(affair);
            }
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String d = affair.getRemindDate();
              if(d.length()==1){
                d = "0"+ d;
              }
              if(d.equals(day)){
                affairList2.add(affair);
              }
            }
          } else{
            String d = affair.getRemindDate();
            //System.out.println(d+"dddd");
            if(d.length()==1){
              d = "0"+ d;
            }
            if(day.equals(d)){
              affairList2.add(affair);
            }    
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              if(m.length()==1){
                m = "0"+ m;
              }
              if(d.length()==1){
                d = "0"+ d;
              }
              if((month+day).equals(m+d)){
                affairList2.add(affair);
              }
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            if(m.length()==1){
              m = "0"+ m;
            }
            if(d.length()==1){
              d = "0"+ d;
            }
            if((month+day).equals(m+d)){
              affairList2.add(affair);
            }
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        data = data+ T9FOM.toJson(affair)+",";
      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
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
   *  把事务显示到日程安排中  按周查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
   
  public String selectAffairByWeek(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year =  request.getParameter("year");
      String weekth = request.getParameter("week");
      Calendar[] darr = T9CalendarAct.getStartEnd(Integer.parseInt(year),Integer.parseInt(weekth));
      String dateStr1 = T9CalendarAct.getFullTimeStr(darr[0]) + " 00:00:00";
      String dateStr2 = T9CalendarAct.getFullTimeStr(darr[1]) + " 23:59:59";
      //System.out.println(dateStr1);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateStr2, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      String str[]= {"USER_ID='" + userId + "'",beginDate};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = tal.selectAffair(dbConn, str);
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String d = affair.getRemindDate();
              Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
              Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
              String monthStr1 = dateFormatMonth.format(date1);
              String monthStr2 = dateFormatMonth.format(date2);
              //System.out.println(monthStr2);
              Date date3 = dateFormat.parse(year+"-"+monthStr1+"-"+d);
              Date date4 = dateFormat.parse(year+"-"+monthStr2+"-"+d);                 
              if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
                affairList2.add(affair);
              }
            }
          } else{
            String d = affair.getRemindDate();
            Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
            Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
            String monthStr1 = dateFormatMonth.format(date1);
            String monthStr2 = dateFormatMonth.format(date2);
            //System.out.println(monthStr2);
            Date date3 = dateFormat.parse(year+"-"+monthStr1+"-"+d);
            Date date4 = dateFormat.parse(year+"-"+monthStr2+"-"+d);                 
            if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
              affairList2.add(affair);
            }  
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
              Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
              String monthStr1 = dateFormatMonth.format(date1);
              String monthStr2 = dateFormatMonth.format(date2);
              //System.out.println(monthStr2);
              Date date3 = dateFormat.parse(year+"-"+m+"-"+d);
              if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
                affairList2.add(affair);
              }  
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
            Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
            String monthStr1 = dateFormatMonth.format(date1);
            String monthStr2 = dateFormatMonth.format(date2);
            //System.out.println(monthStr2);
            Date date3 = dateFormat.parse(year+"-"+m+"-"+d);
            if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
              affairList2.add(affair);
            }  
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        data = data+ T9FOM.toJson(affair)+",";
      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
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
   *   
   * 把事务显示到日程安排中  按月查询
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectAffairByMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year =  request.getParameter("year");
      String month = request.getParameter("month");
      Calendar time=Calendar.getInstance(); 
      time.clear(); 
      time.set(Calendar.YEAR,Integer.parseInt(year)); //year 为 int 
      time.set(Calendar.MONTH,Integer.parseInt(month)-1);//注意,Calendar对象默认一月为0           
      int maxDay=time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
      if(String.valueOf(month).length()==1){
        month = "0"+month;
      }
      String dateStr1 = year+"-"+month + "-01 00:00:00";
      String dateStr2 = year+"-"+month+ "-"+maxDay + " 23:59:59";
      //System.out.println(dateStr1);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateStr2, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      String str[]= {"USER_ID='" + userId + "'",beginDate};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = tal.selectAffair(dbConn, str);
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          } else{
            affairList2.add(affair);  
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              if(m.length()==1){
                m = "0" + m;
              }
              if(m.equals(month)){
                affairList2.add(affair);
              }  
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            if(m.length()==1){
              m = "0" + m;
            }
            if(m.equals(month)){
              affairList2.add(affair);
            }  
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        data = data+ T9FOM.toJson(affair)+",";
      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
      //System.out.println(data);      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(maxDay));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新周期性事物最后一次提醒时间 以及查询当天需要短信提醒的
   */
  public  static void selectAffairRemindByToday(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      int userId = user.getSeqId();
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.DAY_OF_WEEK);
      int day = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      if(week==1){
        week = 7;
      }else{
        week = week-1;
      }
      month = month+1;
      Date dateCur = new Date();  
      String curDate = T9Utility.getCurDateTimeStr("yyyy-MM-dd");
      String beginTime = T9DBUtility.getDateFilter("BEGIN_TIME", curDate + " 23:59:59", "<=");
      String endTime = T9DBUtility.getDateFilter("END_TIME", curDate , ">=");
      String str[]= {"USER_ID = " + userId,"("+beginTime,endTime + " or END_TIME is null)", "( " + T9DBUtility.getDateFilter("LAST_REMIND", curDate , "<=")+" or LAST_REMIND is null)"};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = tal.selectAffair(dbConn, str);
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        String isWeekEnd = affair.getIsWeekend();
        if(affair.getType().equals("2")){
          if(isWeekEnd!=null&&isWeekEnd.equals("1")){
            if(week!=6&&week!=7){
              T9SmsBack sb = new T9SmsBack();
              sb.setSmsType("5");
              sb.setContent("日常事务提醒："+affair.getContent());
              sb.setRemindUrl("/core/funcs/calendar/index.jsp");
              sb.setToId(String.valueOf(userId));
              String remindTime = affair.getRemindTime();
              Date newDate = new Date();
              if(remindTime!=null&&!remindTime.equals("")){
                if(T9Utility.isDayTime(dateFormat.format(newDate)+ " " + remindTime.trim())){
                  newDate = T9Utility.parseDate(dateFormat.format(newDate)+ " " + remindTime.trim());
                }
              }
              sb.setSendDate(newDate);
              int toId = userId;
              if(T9Utility.isInteger(affair.getManagerId())){
                toId = Integer.parseInt(affair.getManagerId());
              }
              sb.setFromId(toId);
              T9SmsUtil.smsBack(dbConn, sb);
             // String moblieSmsRemind = request.getParameter("moblieSmsRemind");
             /* if(moblieSmsRemind!=null){
                T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
                sms2Logic.remindByMobileSms(dbConn, String.valueOf(userId), userId, "日常事务："+content, new Date());
              }*/
              affair.setLastRemind(newDate);
              tal.updateAffair(dbConn, affair);
            }
          }else{
            T9SmsBack sb = new T9SmsBack();
            sb.setSmsType("5");
            sb.setContent("日常事务提醒："+affair.getContent());
            sb.setRemindUrl("/core/funcs/calendar/index.jsp");
            sb.setToId(String.valueOf(userId));
            String remindTime = affair.getRemindTime();
            Date newDate = new Date();
            if(remindTime!=null&&!remindTime.equals("")){
              if(T9Utility.isDayTime(dateFormat.format(newDate)+ " " + remindTime.trim())){
                newDate = T9Utility.parseDate(dateFormat.format(newDate)+ " " + remindTime.trim());
              }
            }
            sb.setSendDate(newDate);
            int toId = userId;
            if(T9Utility.isInteger(affair.getManagerId())){
              toId = Integer.parseInt(affair.getManagerId());
            }
            sb.setFromId(toId);
            T9SmsUtil.smsBack(dbConn, sb);

            affair.setLastRemind(newDate);
            tal.updateAffair(dbConn, affair);
          }
       
        }else if(affair.getType().equals("3")){
          if(affair.getRemindDate().equals(String.valueOf(week))){
            T9SmsBack sb = new T9SmsBack();
            sb.setSmsType("5");
            sb.setContent("日常事务提醒："+affair.getContent());
            sb.setRemindUrl("/core/funcs/calendar/index.jsp");
            sb.setToId(String.valueOf(userId));
            String remindTime = affair.getRemindTime();
            Date newDate = new Date();
            if(remindTime!=null&&!remindTime.equals("")){
              if(T9Utility.isDayTime(dateFormat.format(newDate)+ " " + remindTime.trim())){
                newDate = T9Utility.parseDate(dateFormat.format(newDate)+ " " + remindTime.trim());
              }
            }
            sb.setSendDate(newDate);
            int toId = userId;
            if(T9Utility.isInteger(affair.getManagerId())){
              toId = Integer.parseInt(affair.getManagerId());
            }
            sb.setFromId(toId);
            T9SmsUtil.smsBack(dbConn, sb);
            affair.setLastRemind(newDate);
            tal.updateAffair(dbConn, affair);
          }
        }else if(affair.getType().equals("4")&&affair.getRemindDate().equals(String.valueOf(day))){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          sb.setContent("日常事务提醒："+affair.getContent());
          sb.setRemindUrl("/core/funcs/calendar/index.jsp");
          sb.setToId(String.valueOf(userId));
          String remindTime = affair.getRemindTime();
          Date newDate = new Date();
          if(remindTime!=null&&!remindTime.equals("")){
            if(T9Utility.isDayTime(dateFormat.format(newDate)+ " " + remindTime.trim())){
              newDate = T9Utility.parseDate(dateFormat.format(newDate)+ " " + remindTime.trim());
            }
          }
          sb.setSendDate(newDate);
          int toId = userId;
          if(T9Utility.isInteger(affair.getManagerId())){
            toId = Integer.parseInt(affair.getManagerId());
          }
          sb.setFromId(toId);
          T9SmsUtil.smsBack(dbConn, sb);
          affair.setLastRemind(newDate);
          tal.updateAffair(dbConn, affair);
        }else if(affair.getType().equals("5")&&affair.getRemindDate().equals(String.valueOf(month)+"-"+String.valueOf(day))){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          sb.setContent("日常事务提醒："+affair.getContent());
          sb.setRemindUrl("/core/funcs/calendar/index.jsp");
          sb.setToId(String.valueOf(userId));
          String remindTime = affair.getRemindTime();
          Date newDate = new Date();
          if(remindTime!=null&&!remindTime.equals("")){
            if(T9Utility.isDayTime(dateFormat.format(newDate)+ " " + remindTime.trim())){
              newDate = T9Utility.parseDate(dateFormat.format(newDate)+ " " + remindTime.trim());
            }
          }
          sb.setSendDate(newDate);
          int toId = userId;
          if(T9Utility.isInteger(affair.getManagerId())){
            toId = Integer.parseInt(affair.getManagerId());
          }
          sb.setFromId(toId);
          T9SmsUtil.smsBack(dbConn, sb);
          affair.setLastRemind(newDate);
          tal.updateAffair(dbConn, affair);
        }
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
  }
  /**
   *  把事务显示到桌面上
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
   
  public String selectAffairToDisk(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String day = request.getParameter("day");
      String date = year+"-"+month+"-"+day;
      String index = request.getParameter("index");
      String calendarRecord = request.getParameter("calendarRecord");
      int recordInt = 0;
      if(!calendarRecord.equals("")){
        recordInt = Integer.parseInt(calendarRecord);
      }
      String dateEnd = date+" 23:59:59";
      //System.out.println(date);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateEnd, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", date, ">=");
      String str[]= {"USER_ID="+userId,beginDate};
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = new ArrayList<T9Affair>();
      affairList = tal.selectAffair(dbConn, str);
      if(index.equals("1")){
        String[] strTemp = {"USER_ID="+userId,beginDate};
        affairList = tal.selectAffair(dbConn, strTemp); 
      }
      if(index.equals("2")){
        T9CalendarAct calendar = new T9CalendarAct();
        String endTimeTemp = dateFormat.format(calendar.getDateBefore(new Date(), 10));
        beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", endTimeTemp + " 23:59:59", "<=");
        endDate = T9DBUtility.getDateFilter("END_TIME", date, ">=");
        String[] strTemp = {"USER_ID="+userId,beginDate};
        affairList = tal.selectAffair(dbConn, strTemp); 
      }
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String week = affair.getRemindDate();
              GregorianCalendar d = new GregorianCalendar(); 
              Date mydate= dateFormat.parse(date); 
              d.setTime(mydate);
              int today = d.get(Calendar.DAY_OF_WEEK);
              if(today==1){
                today = 7;  
              }else{
                today = today - 1;
              }
              if(String.valueOf(today).equals(week)){
                affairList2.add(affair);
              }
            }
          }else{
            String week = affair.getRemindDate();
            GregorianCalendar d = new GregorianCalendar(); 
            Date mydate= dateFormat.parse(date); 
            d.setTime(mydate);
            int today = d.get(Calendar.DAY_OF_WEEK);
            if(today==1){
              today = 7;  
            }else{
              today = today - 1;
            }
            if(String.valueOf(today).equals(week)){
              affairList2.add(affair);
            }
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String d = affair.getRemindDate();
              if(d.length()==1){
                d = "0"+ d;
              }
              if(d.equals(day)){
                affairList2.add(affair);
              }
            }
          } else{
            String d = affair.getRemindDate();
            //System.out.println(d+"dddd");
            if(d.length()==1){
              d = "0"+ d;
            }
            if(day.equals(d)){
              affairList2.add(affair);
            }    
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID="+userId,beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              if(m.length()==1){
                m = "0"+ m;
              }
              if(d.length()==1){
                d = "0"+ d;
              }
              if((month+day).equals(m+d)){
                affairList2.add(affair);
              }
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            if(m.length()==1){
              m = "0"+ m;
            }
            if(d.length()==1){
              d = "0"+ d;
            }
            if((month+day).equals(m+d)){
              affairList2.add(affair);
            }
          }
        }  
      }
      String data = "[";
      
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        data = data+ T9FOM.toJson(affair)+",";
        if((i+recordInt)>=9){
          break;
        }

      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
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
public static void main(String[] args) {

}
}
