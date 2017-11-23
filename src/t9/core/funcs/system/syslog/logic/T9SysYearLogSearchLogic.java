package t9.core.funcs.system.syslog.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9SysYearLogSearchLogic {
  public static List getMySysYearLog(Connection conn,int userId) throws Exception{
    StringBuffer sb = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    String time = "";
    String countAll = "";
    try{
        List list = new ArrayList();
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);
        String countYear ="select distinct TIME from sys_log where TYPE='1' order by TIME";
        rs = stmt.executeQuery(countYear);
        while(rs.next()){
            time = rs.getString("TIME");
            time = time.substring(0,4);
            if(!list.contains(time))
            list.add(time);
        }
        if(time.equals("")||time==null){
           Date date = new Date();
           String times = String.valueOf(date);
           times = times.substring(times.indexOf("CST")+4,times.length());
           list.add(times);
        }
         /* 
          Map mapTmp = new HashMap();
          mapTmp.put("time", time);
          list.add(mapTmp);
          for(int i = 0; i < list.size(); i++){
          Map tmpMap = list.get(i);
          sb.append("{");
          sb.append("time:" + tmpMap.get("time"));
          if(i <  list.size()-1){
            sb.append("},");
          }else{
            sb.append("}");
          }
          }
          sb.append("]");
          sb.append("}");
          */
        return list;
      }catch(Exception ex){
          throw ex;
    } finally {
          T9DBUtility.close(stmt, rs, null);
    }
  }

  /**
   * 本年访问量  以及每月访问量
   * @param request
   * @return
   */
  public static List getMyymSys(Connection conn,int userId , String year, String month) throws Exception{
    List list = new ArrayList();
    List list1 = new ArrayList();
    int sum = 0;
     StringBuffer sb = new StringBuffer();
     Statement stmt = null;
     ResultSet rs = null;
     String ymd = "";
     try{
         stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_READ_ONLY);
         for(int i = 1; i <= 12; i++){
             String moth = String.valueOf(i);
             if(year == null){
                 ymd = year+"-"+moth+"-"+"07";
              }else{
                 ymd = year+"-"+moth+"-"+"07";
              }
             //T9DBUtility.getMonthFilter()写好的函数 输入2个参数就可以求出，一个月的有效天数 
             String curyear = "select count(*) as amount from SYS_LOG where TYPE='1' and "+ T9DBUtility.getMonthFilter("TIME", T9Utility.parseDate(ymd));
             rs = stmt.executeQuery(curyear);
             if(rs.next()){
                list.add(rs.getString("amount"));
              }
          }
     }catch(Exception ex){
       throw ex;
     }finally {
       T9DBUtility.close(stmt, rs, null);
     }     
  return list;
  }
  /**
   * 本月访问量  以及每天访问量-
   * @param request
   * @return
   */
  public static List getMyydSys(Connection conn,int userId , String year, String month) throws Exception{
    List list = new ArrayList();
    int sum = 0;
    StringBuffer sb = new StringBuffer();
    Statement stmt = null;
    Statement stmt1 = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String ymd = "";
    Calendar c = Calendar.getInstance();
    ymd = year+"-"+month+"-"+"07";
    Date date = T9Utility.parseDate(year+"-"+month+"-01");
    c.setTime(date);
    int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
    try{
         stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_READ_ONLY);
         stmt1 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_READ_ONLY);
        /* 
         String curyue = "select count(*) as amount from SYS_LOG where TYPE='1' and "+ T9DBUtility.getMonthFilter("TIME",T9Utility.parseDate(ymd));
         rs = stmt.executeQuery(curyue);
         String yuemount ="";
         while(rs.next()){
         yuemount = rs.getString("amount");
         //System.out.println(yuemount);
         }
          String daymout=""; //下列方法用到
          daymout = rs1.getString("amount");
          double yueble =  Double.parseDouble(yuemount);
          //System.out.println(daymout);
          double dayble =  Double.parseDouble(daymout);
          double dayYue =  dayble/yueble;
          //System.out.println(dayYue);
        */
       
         for (int i = 1; i <= maxDay; i++) {
             Date dateTmp = T9Utility.parseDate(year+"-"+month+"-"+i);
             // 一下函数是 访问月份的各个天数             String curday = "select  count(*) as amount from SYS_LOG where TYPE='1' and "+ T9DBUtility.getDayFilter("TIME", dateTmp);
             rs1 = stmt1.executeQuery(curday);
             while(rs1.next()){
                   list.add(rs1.getString("amount"));
             }
         }
     }catch(Exception ex){
         throw ex;
     }finally {
         T9DBUtility.close(stmt, rs, null);
     }     
     return list;
  }
 
  
  /**
   * 每个时间段  / 每年小时量 的百分比   getMyhourSysLog
   * @param request
   * @return
   */
  public static Map getMyhourSysLog(Connection conn,int userId , String year, String month) throws Exception{
     List list = new ArrayList();
     Map map = new HashMap();
     int sum = 0;
     StringBuffer sb = new StringBuffer();
     Statement stmt = null;
     ResultSet rs = null;
     try{
         stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
         ResultSet.CONCUR_READ_ONLY);
         //String sql = "select count(*) as amount,to_char(time, 'hh24') as hour from sys_log group by to_char(time, 'hh24')";
         String sql = null;
         sql = "select count(*) as amount, " + T9DBUtility.formatDateHOnly("time") + " as t from sys_log group by " + T9DBUtility.formatDateHOnly("time");
         rs = stmt.executeQuery(sql);
         while(rs.next()){
           map.put("h" + rs.getString(2), rs.getString(1)); //
         }
    }catch(Exception ex){
       throw ex;
    }finally{
       T9DBUtility.close(stmt, rs, null);
    }
     return map;
  }
   public static String getMySysLog(Connection conn,int userId) throws Exception{
      StringBuffer sb = new StringBuffer();
      Statement stmt = null;
      Statement stmt1 = null;
      ResultSet rs = null;
      ResultSet rs1 = null;
      String time = "";
      String time1 = ""; 
      long countDay = 0;
      long countDay1 = 0;
      String count = "";
    try{
      stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      stmt1 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      String SysLogSqldesc = "select TIME from SYS_LOG order by TIME desc";
      String SysLogSqlasc = "select TIME from SYS_LOG order by TIME asc";
      //此语句也可以
      //String sql = "select (select max(TIME) from sys_log) - (select min(time) from sys_log) from dual";
      rs = stmt.executeQuery(SysLogSqldesc);
      rs1 = stmt1.executeQuery(SysLogSqlasc);
      if(rs.next()){
       time = rs.getString("TIME");
      }
      if(rs1.next()){
       time1 = rs1.getString("TIME");
      } 
      DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
      Date Timedesc = sdf.parse(time);
      Date Timeasc = sdf.parse(time1);
      countDay = Timedesc.getTime();
      countDay1 = Timeasc.getTime();
      long countDays = ((countDay - countDay1)/24/3600)+1;
      count = String.valueOf(countDays);
    }catch(Exception ex){
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return count;
  }
   /**
    *  总访问量
    * @param request
    * @return
    */
  public static String getMySysLogAll(Connection conn,int userId) throws Exception{
    StringBuffer sb = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;
    String countAll = "";
    try{
        stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY);
        String SysLogSqlAll = "SELECT count(*) from SYS_LOG where TYPE='1'";
        rs = stmt.executeQuery(SysLogSqlAll);
        if(rs.next()){
           countAll = rs.getString(1);
        }
    }catch(Exception ex){
       throw ex;
    } finally {
       T9DBUtility.close(stmt, rs, null);
    }
    return countAll;
  }
  /**
   * 平均每日访问量
   * @param request
   * @return
   */
  public static String getMySysAveLog(Connection conn,int userId) throws Exception{
    String count = T9SysLogSearchLogic.getMySysLog(conn, userId);
    String countAll =  T9SysLogSearchLogic.getMySysLogAll(conn, userId);
    if(count.length() > 4){
      count=count.substring(0,2);
    }else{
      count=count.substring(0,1);
    }
     Double dayAve=0.0;
     Double aveAll = Double.valueOf(countAll);
     Double aveDay = Double.valueOf(count);
     if(aveAll%aveDay ==0){
        dayAve = aveAll/aveDay;
     }else{
        dayAve = aveAll/aveDay;
        dayAve = dayAve+1;
     }
     String aveCount =   String.valueOf(dayAve);
     if(aveCount.contains(".")){
        aveCount = aveCount.substring(0,aveCount.lastIndexOf("."));
      }   
   return aveCount;
  }
  public static String toDate(int i){
    String dat = "";
    if(i==24){
      dat += " 23:59:59";
    }else{
      dat += " "+i+":00:00";
    }
    return dat;
  }
  public static void main(String[] args){
    // Date d = new Date();
    // String test = toDate(d, 2);
    //T9SysYearLogSearchLogic lgo = new T9SysYearLogSearchLogic();
    for(int i=0; i<24; i++){
        String time = toDate(i);
        //System.out.println(time);
    }
  }
}
