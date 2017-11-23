package t9.core.funcs.system.attendance.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import t9.core.util.db.T9DBUtility;

public class T9ManageDataLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public void deleteDutyDate(Connection dbConn,String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_DUTY where ";
    if(minTime.equals("") && maxTime.equals("")){
      sql = "delete from ATTEND_DUTY" ;
    }else if(!minTime.equals("") && !maxTime.equals("")){
      String temp1 = T9DBUtility.getDateFilter("REGISTER_TIME", minTime, ">=");
      String temp2 = T9DBUtility.getDateFilter("REGISTER_TIME", maxTime, "<=");
      sql = sql + temp1 + " and " + temp2;
    }else{
      if(!minTime.equals("")){
        String temp = T9DBUtility.getDateFilter("REGISTER_TIME", minTime, ">=");
        sql = sql  + temp;
      }
      if(!maxTime.equals("")){
        String temp = T9DBUtility.getDateFilter("REGISTER_TIME", maxTime, "<=");
        sql = sql + temp;
      }
    }
    //System.out.println(sql);
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteDutyDate(Connection dbConn,String userIds, String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String newUserId = "";
    String[] userIdArray = userIds.split(",");
    for (int i = 0; i < userIdArray.length; i++) {
      newUserId = newUserId + "'" + userIdArray[i] + "'," ; 
    }
    if(userIdArray.length>0){
      newUserId = newUserId.substring(0, newUserId.length()-1);
    }
    String sql = "delete from ATTEND_DUTY where USER_ID in(" +newUserId + ")" ;
    if(!minTime.equals("")){
      String temp = T9DBUtility.getDateFilter("REGISTER_TIME", minTime, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxTime.equals("")){
      String temp = T9DBUtility.getDateFilter("REGISTER_TIME", maxTime, "<=");
      sql = sql + " and " + temp;
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteOutDate(Connection dbConn,String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_OUT where ";
    if(minTime.equals("") && maxTime.equals("")){
      sql = "delete from ATTEND_OUT" ;
    }else if(!minTime.equals("") && !maxTime.equals("")){
      String temp1 = T9DBUtility.getDateFilter("SUBMIT_TIME", minTime, ">=");
      String temp2 = T9DBUtility.getDateFilter("SUBMIT_TIME", maxTime, "<=");
      sql = sql + temp1 + " and " + temp2;
    }else{
      if(!minTime.equals("")){
        String temp = T9DBUtility.getDateFilter("SUBMIT_TIME", minTime, ">=");
        sql = sql + temp;
      }
      if(!maxTime.equals("")){
        String temp = T9DBUtility.getDateFilter("SUBMIT_TIME", maxTime, "<=");
        sql = sql + temp;
      }
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteOutDate(Connection dbConn,String userIds, String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String newUserId = "";
    String[] userIdArray = userIds.split(",");
    for (int i = 0; i < userIdArray.length; i++) {
      newUserId = newUserId + "'" + userIdArray[i] + "'," ; 
    }
    if(userIdArray.length>0){
      newUserId = newUserId.substring(0, newUserId.length()-1);
    }
    String sql = "delete from ATTEND_OUT where USER_ID in(" +newUserId + ")" ;
    if(!minTime.equals("")){
      String temp = T9DBUtility.getDateFilter("SUBMIT_TIME", minTime, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxTime.equals("")){
      String temp = T9DBUtility.getDateFilter("SUBMIT_TIME", maxTime, "<=");
      sql = sql + " and " + temp;
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteLeaveDate(Connection dbConn,String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_LEAVE where ";
    if(minTime.equals("") && maxTime.equals("")){
      sql = "delete from ATTEND_LEAVE" ;
    }else if(!minTime.equals("") && !maxTime.equals("")){
      String temp1 = T9DBUtility.getDateFilter("LEAVE_DATE1", minTime, ">=");
      String temp2 = T9DBUtility.getDateFilter("LEAVE_DATE2", maxTime, "<=");
      sql = sql + temp1 + " and " + temp2;
    }else{
      if(!minTime.equals("")){
        String temp = T9DBUtility.getDateFilter("LEAVE_DATE1", minTime, ">=");
        sql = sql + temp;
      }
      if(!maxTime.equals("")){
        String temp = T9DBUtility.getDateFilter("LEAVE_DATE2", maxTime, "<=");
        sql = sql + temp;
      }
    }   
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteLeaveDate(Connection dbConn,String userIds, String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String newUserId = "";
    String[] userIdArray = userIds.split(",");
    for (int i = 0; i < userIdArray.length; i++) {
      newUserId = newUserId + "'" + userIdArray[i] + "'," ; 
    }
    if(userIdArray.length>0){
      newUserId = newUserId.substring(0, newUserId.length()-1);
    }
    String sql = "delete from ATTEND_LEAVE where USER_ID in(" +newUserId + ")" ;
    if(!minTime.equals("")){
      String temp = T9DBUtility.getDateFilter("LEAVE_DATE1", minTime, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxTime.equals("")){
      String temp = T9DBUtility.getDateFilter("LEAVE_DATE2", maxTime, "<=");
      sql = sql + " and " + temp;
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteEvectionDate(Connection dbConn,String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from ATTEND_EVECTION where ";
    if(minTime.equals("") && maxTime.equals("")){
      sql = "delete from ATTEND_EVECTION" ;
    }else if(!minTime.equals("") && !maxTime.equals("")){
      String temp1 = T9DBUtility.getDateFilter("EVECTION_DATE1", minTime, ">=");
      String temp2 = T9DBUtility.getDateFilter("EVECTION_DATE2", maxTime, "<=");
      sql = sql + temp1 + " and " + temp2;
    }else{
      if(!minTime.equals("")){
        String temp = T9DBUtility.getDateFilter("EVECTION_DATE1", minTime, ">=");
        sql = sql + temp;
      }
      if(!maxTime.equals("")){
        String temp = T9DBUtility.getDateFilter("EVECTION_DATE2", maxTime, "<=");
        sql = sql + temp;
      }
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  public void deleteEvectionDate(Connection dbConn,String userIds, String minTime,String maxTime)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String newUserId = "";
    String[] userIdArray = userIds.split(",");
    for (int i = 0; i < userIdArray.length; i++) {
      newUserId = newUserId + "'" + userIdArray[i] + "'," ; 
    }
    if(userIdArray.length>0){
      newUserId = newUserId.substring(0, newUserId.length()-1);
    }
    String sql = "delete from ATTEND_EVECTION where USER_ID in(" +newUserId + ")" ;
    if(!minTime.equals("")){
      String temp = T9DBUtility.getDateFilter("EVECTION_DATE1", minTime, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxTime.equals("")){
      String temp = T9DBUtility.getDateFilter("EVECTION_DATE2", maxTime, "<=");
      sql = sql + " and " + temp;
    }
    try {
     stmt = dbConn.createStatement();
     stmt.executeUpdate(sql);
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
}
