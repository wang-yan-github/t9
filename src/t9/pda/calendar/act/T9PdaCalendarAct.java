package t9.pda.calendar.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.pda.calendar.data.T9PdaAffair;
import t9.pda.calendar.data.T9PdaCalendar;

public class T9PdaCalendarAct {

  public void doint(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String dateStr1 = T9Utility.getDateTimeStr(null).substring(0, 10) + " 00:00:00";
      String dateStr2 = T9Utility.getDateTimeStr(null).substring(0, 10) + " 23:59:59";
      String calTime2 = T9DBUtility.getDateFilter("CAL_TIME", dateStr2, "<=");
      String endTime2 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      
      List<T9PdaCalendar> list1 = new ArrayList<T9PdaCalendar>();
      List<T9PdaAffair> list2 = new ArrayList<T9PdaAffair>();
      
      //查询日程
      String sql = " SELECT SEQ_ID, CAL_TIME, END_TIME, CONTENT " 
      		       + " from CALENDAR where USER_ID='"+person.getSeqId()+"'" 
      		       + " and "+calTime2+ " and " + endTime2 
      		       + " order by CAL_TIME ";
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      rs = ps.executeQuery();
      while(rs.next()){
        T9PdaCalendar calendar = new T9PdaCalendar();
        calendar.setSeqId(rs.getInt("SEQ_ID"));
        calendar.setCalTime(rs.getTimestamp("CAL_TIME"));
        calendar.setEndTime(rs.getTimestamp("END_TIME"));
        calendar.setContent(rs.getString("CONTENT"));
        list1.add(calendar);
      }
      
      String dateEnd = T9Utility.getDateTimeStr(null).substring(0, 10)+" 23:59:59";
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateEnd, "<=");
      //查询事务
      sql = " SELECT SEQ_ID, USER_ID, TYPE, REMIND_DATE, REMIND_TIME, CONTENT from AFFAIR where USER_ID='"+person.getSeqId()+"' "
          + " and " + beginDate +" order by REMIND_TIME ";
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      
      while(rs.next()){
        T9PdaAffair affair = new T9PdaAffair();
        affair.setSeqId(rs.getInt("SEQ_ID"));
        affair.setUserId(rs.getString("USER_ID"));
        affair.setType(rs.getInt("TYPE"));
        affair.setRemindDate(rs.getString("REMIND_DATE"));
        affair.setRemindTime(rs.getString("REMIND_TIME"));
        affair.setContent(rs.getString("CONTENT"));
        list2.add(affair);
      }
      request.setAttribute("calendars", list1);
      request.setAttribute("affairs", list2);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    request.getRequestDispatcher("/pda/calendar/index.jsp").forward(request, response);
    return;
  }
  
  public void newCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PreparedStatement ps = null;
    try{
      String calTime = (String)request.getParameter("calTime");
      String endTime = (String)request.getParameter("endTime");
      String calType = (String)request.getParameter("calType");
      String content = (String)request.getParameter("content");
      int flag = 0;
      String date = T9Utility.getDateTimeStr(null).substring(0, 11);
      
      if(T9Utility.isDayTime(date+calTime+":00") || T9Utility.isDayTime(date+endTime+":59")){
        T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        
        String sql = " insert into CALENDAR(USER_ID,CAL_TIME,END_TIME,CAL_TYPE,CAL_LEVEL,CONTENT,OVER_STATUS) " 
        	         + " values("+person.getSeqId()+",?,?,'"+calType+"','','"+content+"','0')";
        ps = dbConn.prepareStatement(sql);
        
        ps.setTimestamp(1, T9Utility.parseTimeStamp(T9Utility.parseDate(date+calTime+":00").getTime()));
        ps.setTimestamp(2, T9Utility.parseTimeStamp(T9Utility.parseDate(date+endTime+":59").getTime()));
        flag = ps.executeUpdate();
      }
      else{
        flag = 3;
      }
      request.setAttribute("flag", flag);
    }
    catch(Exception ex){
      ex.printStackTrace();
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    request.getRequestDispatcher("/pda/calendar/send.jsp").forward(request, response);
    return;
  }
  
}
