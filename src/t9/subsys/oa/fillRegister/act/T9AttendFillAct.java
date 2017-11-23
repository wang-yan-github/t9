package t9.subsys.oa.fillRegister.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.data.T9AttendFill;
import t9.subsys.oa.fillRegister.logic.T9AttendFillLogic;


public class T9AttendFillAct {
  public static final String attachmentFolder = "attendFill";
  private T9AttendFillLogic logic = new T9AttendFillLogic();
  
  public String add(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      T9AttendFill fill = (T9AttendFill) T9FOM.build(map, T9AttendFill.class, "");
      String registerStr = request.getParameter("idStrs");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
//      if (scoreFlow.getBeginDate() == null) {
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//        scoreFlow.setBeginDate(T9Utility.parseSqlDate(sf.format(new Date())));
//      }
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日");
      long daySpace = T9Utility.getDaySpan(dateFormat1.parse(beginDate),dateFormat1.parse(endDate))+1;
      //得到之间的天数数组
      List daysList = new ArrayList();
      String days = "";
      Calendar calendar = new GregorianCalendar();
      String[] register = registerStr.split(",");
      for(int i = 0;i < daySpace; i++){
        if(this.logic.isWeekend(beginDate)){
          continue;
        }
        calendar.setTime(dateFormat1.parse(beginDate));
        calendar.add(Calendar.DATE,+i) ;
        Date dateTemp = calendar.getTime();
        String dateTempStr = dateFormat1.format(dateTemp);
        for(int x = 0; x < register.length; x++){
          fill.setFillTime(T9Utility.parseSqlDate(dateTempStr));
          fill.setRegisterType(register[x]);
          fill.setAssessingStatus("0");
          fill.setAttendFlag("0");
          SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
          fill.setAssessingTime(T9Utility.parseSqlDate(sf.format(new Date())));
          this.logic.addAttendFill(dbConn, fill);//添加数据
        }
      }
      String smsSJ = request.getParameter("smsSJ");//手机短信
      String smsflag = request.getParameter("smsflag");//内部短信
      if (smsflag.equals("1")) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("62");
        sb.setContent("请查看补登记审批！");
        sb.setSendDate(new java.util.Date());
        sb.setFromId(person.getSeqId());
        sb.setToId(fill.getAssessingOfficer());
        sb.setRemindUrl("/subsys/oa/fillRegister/approval/index.jsp?openFlag=1&openWidth=820&openHeight=600");
        T9SmsUtil.smsBack(dbConn,sb);
      }
      //手机消息提醒
      if (smsSJ.equals("1")) {
        T9MobileSms2Logic sb2 = new T9MobileSms2Logic();
        sb2.remindByMobileSms(dbConn,fill.getAssessingOfficer(),person.getSeqId(),"请查看考核任务！" ,new java.util.Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addVolume(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      T9AttendFill fill = (T9AttendFill) T9FOM.build(map, T9AttendFill.class, "");
      String registerStr = request.getParameter("idStrs");
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String proposerStr = request.getParameter("proposer");
      String[] proposer = proposerStr.split(",");
      String[] register = registerStr.split(",");
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy年MM月dd日");
      long daySpace = T9Utility.getDaySpan(dateFormat1.parse(beginDate),dateFormat1.parse(endDate))+1;
      //得到之间的天数数组
      List daysList = new ArrayList();
      String days = "";
      Calendar calendar = new GregorianCalendar();
      for(int i = 0; i < daySpace; i++){
        calendar.setTime(dateFormat1.parse(beginDate));
        calendar.add(Calendar.DATE,+i) ;
        Date dateTemp = calendar.getTime();
        String dateTempStr = dateFormat1.format(dateTemp);
        for(int x = 0; x < register.length; x++){
          for(int y = 0; y < proposer.length; y++){
            fill.setProposer(proposer[y]);
            fill.setFillTime(T9Utility.parseSqlDate(dateTempStr));
            fill.setRegisterType(register[x]);
            fill.setAssessingStatus("1");
            fill.setAttendFlag("0");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            fill.setAssessingTime(T9Utility.parseSqlDate(sf.format(new Date())));
            this.logic.addAttendFill(dbConn, fill);//添加数据
          }
        }
      }
//      String smsSJ = request.getParameter("smsSJ");//手机短信
//      String smsflag = request.getParameter("smsflag");//内部短信
//      if (smsflag.equals("1")) {
//        T9SmsBack sb = new T9SmsBack();
//        sb.setSmsType("15");
//        sb.setContent("请查看补登记审批！");
//        sb.setSendDate(new java.util.Date());
//        sb.setFromId(person.getSeqId());
//        sb.setToId(fill.getAssessingOfficer());
//        sb.setRemindUrl("/subsys/oa/hr/score/flow/index1.jsp&openFlag=1&openWidth=820&openHeight=600");
//        //T9SmsUtil.smsBack(dbConn,sb);
//      }
//      //手机消息提醒
//      if (smsSJ.equals("1")) {
//        T9MobileSms2Logic sb2 = new T9MobileSms2Logic();
//        //sb2.remindByMobileSms(dbConn,fill.getAssessingOfficer(),person.getSeqId(),"请查看考核任务！" ,new java.util.Date());
//      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 出国培训补登记
   * @param request
   * @param response
   * @param configId
   * @param dutyType
   * @param registerType
   * @return
   * @throws Exception
   */
  public String addAttendScore(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String startDateStr = request.getParameter("beginDate");
      String endDateStr = request.getParameter("endDate");
      String proposer = request.getParameter("proposer");
      this.logic.addAttendScore(dbConn, person, startDateStr, endDateStr, proposer);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
}
