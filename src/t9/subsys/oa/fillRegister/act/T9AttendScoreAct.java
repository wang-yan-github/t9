package t9.subsys.oa.fillRegister.act;

import java.sql.Connection;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.logic.T9AttendDutyLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.attendance.data.T9AttendConfig;
import t9.core.funcs.system.attendance.logic.T9AttendConfigLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.data.T9AttendScore;
import t9.subsys.oa.fillRegister.logic.T9AttendScoreLogic;


public class T9AttendScoreAct {
  public static final String attachmentFolder = "attendScore";
  private T9AttendScoreLogic logic = new T9AttendScoreLogic();
  
  
  public  String addRegister(HttpServletRequest request,
      HttpServletResponse response,String configId, String dutyType,String registerType) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      if(T9Utility.isInteger(configId)){
        T9AttendConfigLogic configLogic = new T9AttendConfigLogic();
        T9AttendConfig config = configLogic.selectConfigById(dbConn, configId);
        if(T9Utility.isNullorEmpty(dutyType)){
          dutyType = "";
        }
        if(dutyType.equals("2")){
          dutyType = "2";
        }else{
          dutyType = "1";
        }
        if(config != null){
          T9AttendDutyLogic t9adl = new T9AttendDutyLogic();
          String dutyIntervalBefore1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE1");
          String dutyIntervalAfter1 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER1");
          String dutyIntervalBefore2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_BEFORE2");
          String dutyIntervalAfter2 = t9adl.getParaValue(dbConn, "DUTY_INTERVAL_AFTER2");
          int before1 = 0;
          int after1 = 0;
          int before2 = 0;
          int after2 = 0;
          if(dutyIntervalBefore1 != null && !dutyIntervalBefore1.trim().equals("")){
            //System.out.println(dutyIntervalBefore1.length());
            before1 = Integer.parseInt(dutyIntervalBefore1);
          }
          if(dutyIntervalAfter1 != null && !dutyIntervalAfter1.trim().equals("")){
            after1 = Integer.parseInt(dutyIntervalAfter1);
          }
          if(dutyIntervalBefore2!=null&&!dutyIntervalBefore2.trim().equals("")){
            before2 = Integer.parseInt(dutyIntervalBefore2);
          }
          if(dutyIntervalAfter2!=null&&!dutyIntervalAfter2.trim().equals("")){
            after2 = Integer.parseInt(dutyIntervalAfter2);
          } 
          String dutyTime1 = config.getDutyTime1();
          String dutyTime2 = config.getDutyTime2();
          String dutyTime3 = config.getDutyTime3();
          String dutyTime4 = config.getDutyTime4();
          String dutyTime5 = config.getDutyTime5();
          String dutyTime6 = config.getDutyTime6();
          String dutyType1 = config.getDutyType1();
          String dutyType2 = config.getDutyType2();
          String dutyType3 = config.getDutyType3();
          String dutyType4 = config.getDutyType4();
          String dutyType5 = config.getDutyType5();
          String dutyType6 = config.getDutyType6();
          String selfType = "";
          Map<String,String[]> map = request.getParameterMap();
          T9AttendScore record = new T9AttendScore();
          Date time = new Date();
          if(!T9Utility.isNullorEmpty(dutyTime1)){
            Date date = getDateInt(dutyTime1,dutyType1 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType1, "1",registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "1", time);
            selfType = "1";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType1, scores, selfType, registerType);
            }else{
              //record.setDutyTime(T9Utility.parseDate(dutyTime1));
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType1, scores, "1", "0", selfType);
//              record.setCreateTime(time);
//              record.setUserId(String.valueOf(person.getSeqId()));
//              record.setDutyType(dutyType1);
//              record.setScore(scores);
//              record.setAssessingStatus("1");
//              record.setAttendFlag("0");
//              record.setRegisterType(selfType);
//              this.logic.addRegister(dbConn, record);
            }
          }
          if(!T9Utility.isNullorEmpty(dutyTime2)){
            Date date = getDateInt(dutyTime2,dutyType2 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType2, "2", registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "2", time);
            selfType = "2";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType2, scores, selfType, registerType);
            }else{
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType2, scores, "1", "0", selfType);
//              record.setCreateTime(T9Utility.parseDate(T9Utility.getCurDateTimeStr().substring(0, 10)));
//              record.setDutyType(dutyType2);
//              record.setUserId(String.valueOf(person.getSeqId()));
//              record.setScore(scores);
//              record.setAssessingStatus("1");
//              record.setAttendFlag("0");
//              record.setRegisterType("2");
//              this.logic.addRegister(dbConn, record);
            }
          }
          if(!T9Utility.isNullorEmpty(dutyTime3)){
            Date date = getDateInt(dutyTime3,dutyType3 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType3, "3", registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "3", time);
            selfType = "3";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType3, scores, selfType, registerType);
            }else{
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType3, scores, "1", "0", selfType);
            }
          }
          if(!T9Utility.isNullorEmpty(dutyTime4)){
            Date date = getDateInt(dutyTime4,dutyType4 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType4, "4", registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "4", time);
            selfType = "4";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType4, scores, selfType, registerType);
            }else{
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType4, scores, "1", "0", selfType);
            }
          }
          if(!T9Utility.isNullorEmpty(dutyTime5)){
            Date date = getDateInt(dutyTime5,dutyType5 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType5, "5", registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "5", time);
            selfType = "5";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType5, scores, selfType, registerType);
            }else{
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType5, scores, "1", "0", selfType);
            }
          }
          if(!T9Utility.isNullorEmpty(dutyTime6)){
            Date date = getDateInt(dutyTime6,dutyType6 ,after1 ,before2);
            double scores = this.logic.getUpScore(dbConn, time, date, dutyType6, "6", registerType, person.getDutyType());
            boolean bool = this.logic.getAttendScoreFlag(dbConn, "6", time);
            selfType = "6";
            if(bool){
              this.logic.getUpdateRegister(dbConn, record, person, time, dutyType6, scores, selfType, registerType);
            }else{
              this.logic.getAddRegisterFunc(dbConn, record, person, time, dutyType6, scores, "1", "0", selfType);
            }
          }
        }
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
   * 获取分值

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
//  public String getAttendTimeScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
//    String lateTimeStr = request.getParameter("lateTime");
//    int lateTime = 0;
//    if (T9Utility.isNumber(lateTimeStr)) {
//      lateTime = Integer.parseInt(lateTimeStr);
//    }
//    Connection dbConn = null;
//    try {
//      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
//      dbConn = requestDbConn.getSysDbConn();
//      String data = this.logic.getAttendTimeScoreLogic(dbConn, lateTime);
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//      request.setAttribute(T9ActionKeys.RET_MSRG, "成功获取数据");
//      request.setAttribute(T9ActionKeys.RET_DATA, data);
//    } catch (Exception e) {
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
//      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
//      throw e;
//    }
//    return "/core/inc/rtjson.jsp";
//  }
  
  
  public static Date getDateInt(String dutyTime,String dutyType,int before1, int after2) throws ParseException{
    Date currDate = new Date();
    String currDateStr =  T9Utility.getCurDateTimeStr();
    String dateStr = currDateStr.substring(0, 11) + dutyTime;
    Calendar calendar = Calendar.getInstance();
    Date date = T9Utility.parseDate(dateStr);
    calendar.setTime(date);
    int tempInt = 0;
    int dutyTimeInt = getLongByDutyTime(dutyTime);
    if(dutyType.equals("1")){//上班
      calendar.add(Calendar.MINUTE, before1);
    }else{//下班
      calendar.add(Calendar.MINUTE, -after2);
    }
    return calendar.getTime();
  } 
  public static int getLongByDutyTime(String dutyTime){
    int time = 0;
    String times[] = dutyTime.split(":");
    int length = times.length;
    for (int i = 0; i < times.length -1; i++) {
      time = time + Integer.parseInt(times[i])* (int)(Math.pow(60,times.length -2- i)) ;
    }
    return time;
  }
  
  public String addRecord(HttpServletRequest request,
      HttpServletResponse response,String configId, String dutyType,String registerType) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      T9AttendScore record = (T9AttendScore) T9FOM.build(map, T9AttendScore.class, "");
      String stUserId = record.getAssessingOfficer();
      String[] staffUserIdStr = stUserId.split(",");
      for(int i = 0; i < staffUserIdStr.length; i++){
//        record.setCreateUserId(String.valueOf(person.getSeqId()));
//        record.setCreateDeptId(person.getDeptId());
//        record.setAbroadUserId(staffUserIdStr[i]);
        this.logic.add(dbConn, record);
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
  
  public String getAttendDutyJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      StringBuffer sb = new StringBuffer();
      //List<T9AttendDuty> attendList = this.logic.getAttendDutyJson(dbConn, request.getParameterMap(), person, beginDate, endDate);
      T9AttendConfig attend = this.logic.selectConfigById(dbConn, String.valueOf(person.getDutyType()));
        sb.append("{");
        sb.append("seqId:\"" +  attend.getSeqId() + "\"");
        sb.append(",dutyTime1:\"" + T9Utility.null2Empty(attend.getDutyTime1())+ "\"");
        sb.append(",dutyTime2:\"" + (attend.getDutyTime2() == null ? "" : attend.getDutyTime2())+ "\"");
        sb.append(",dutyTime3:\"" + (attend.getDutyTime3() == null ? "" : attend.getDutyTime3())+ "\"");
        sb.append(",dutyTime4:\"" + (attend.getDutyTime4() == null ? "" : attend.getDutyTime4())+ "\"");
        sb.append(",dutyTime5:\"" + (attend.getDutyTime5() == null ? "" : attend.getDutyTime5())+ "\"");
        sb.append(",dutyTime6:\"" + (attend.getDutyTime6() == null ? "" : attend.getDutyTime6())+ "\"");
        sb.append(",dutyType1:\"" + (attend.getDutyType1() == null ? "" : attend.getDutyType1())+ "\"");
        sb.append(",dutyType2:\"" + (attend.getDutyType2() == null ? "" : attend.getDutyType2())+ "\"");
        sb.append(",dutyType3:\"" + (attend.getDutyType3() == null ? "" : attend.getDutyType3())+ "\"");
        sb.append(",dutyType4:\"" + (attend.getDutyType4() == null ? "" : attend.getDutyType4())+ "\"");
        sb.append(",dutyType5:\"" + (attend.getDutyType5() == null ? "" : attend.getDutyType5())+ "\"");
        sb.append(",dutyType6:\"" + (attend.getDutyType6() == null ? "" : attend.getDutyType6())+ "\"");
        sb.append("},");
      sb.deleteCharAt(sb.length() - 1);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public static void main(String[] args) {
    //System.out.println(getLongByDutyTime("9:54:00"));
  }
}
