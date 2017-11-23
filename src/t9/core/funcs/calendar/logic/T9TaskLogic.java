package t9.core.funcs.calendar.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.calendar.data.T9Task;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9TaskLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public int addTask(Connection dbConn,T9Task task) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, task);
    return T9CalendarLogic.getMaSeqId(dbConn, "TASK");
  }
  public void updateTask(Connection dbConn,T9Task task) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, task);
  }
  public List<Map<String,String>> selectTask(Connection dbConn,int userId) throws Exception{
    List<Map<String,String>> taskList = new ArrayList<Map<String,String>>();
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select *from TASK where USER_ID = '" + userId + "' order by TASK_NO";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        map.put("seqId",rs.getString("SEQ_ID"));
        map.put("userId",rs.getString("USER_ID"));
        map.put("content",rs.getString("CONTENT"));
        map.put("beginDate",rs.getString("BEGIN_DATE"));
        map.put("endDate",rs.getString("END_DATE"));
        map.put("managerId",rs.getString("MANAGER_ID"));
        map.put("taskType",rs.getString("TASK_TYPE"));
        map.put("taskNo",rs.getString("TASK_NO"));
        map.put("color", rs.getString("COLOR"));
        map.put("subject",rs.getString("SUBJECT"));
        map.put("rate", rs.getString("RATE"));
        map.put("finishTime", rs.getString("FINISH_TIME"));
        map.put("totalTime",rs.getString("TOTAL_TIME"));
        map.put("useTime", rs.getString("USE_TIME"));
        map.put("calId", rs.getString("CAL_ID"));
        map.put("important",rs.getString("IMPORTANT"));
        map.put("taskStatus", rs.getString("TASK_STATUS"));
        map.put("editTime", rs.getString("EDIT_TIME"));
        taskList.add(map);
      }  
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return taskList;
  }
  /*
   * 分页
   */
  public String toSearchData(Connection conn,Map request,int userId) throws Exception{
    String sql = "select SEQ_ID,IMPORTANT,TASK_NO,SUBJECT,TASK_STATUS,RATE,TASK_TYPE,COLOR,BEGIN_DATE,END_DATE,MANAGER_ID,USER_ID from TASK where USER_ID  ='" + userId +"' order by TASK_NO";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  public List<Map<String,String>> selectTaskByTerm(Connection dbConn,int userId,String minDate,String maxDate,String taskType,String taskStatus,String content,String important) throws Exception{
    List<Map<String,String>> taskList = new ArrayList<Map<String,String>>();
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select *from TASK where USER_ID='"+userId + "'";
    if(!minDate.equals("")){
      String temp = T9DBUtility.getDateFilter("BEGIN_DATE", minDate, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxDate.equals("")){
      maxDate = maxDate + " 23:59:59";
      String temp = T9DBUtility.getDateFilter("END_DATE", maxDate, "<=");
      sql = sql + " and " + temp;
    }
    if(!taskType.equals("")){
      sql = sql + " and TASK_TYPE='" + taskType + "'";
    }
    if(!taskStatus.equals("")){
      sql = sql + " and TASK_STATUS='" + taskStatus+"'";
    }
    if(!content.equals("")){
      content = T9DBUtility.escapeLike(content);
      sql = sql + " and CONTENT like '%" + content + "%' "+ T9DBUtility.escapeLike();
    }
    if(!important.equals("")){
      if(important.equals("0")){
        sql = sql + " and (IMPORTANT='" + important+"' or IMPORTANT=' ')";
      }else{
        sql = sql + " and IMPORTANT='" + important+"'";
      }
    }
    sql = sql + " order by BEGIN_DATE";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        map.put("seqId",rs.getString("SEQ_ID"));
        map.put("userId",rs.getString("USER_ID"));
        map.put("content",rs.getString("CONTENT"));
        map.put("beginDate",rs.getString("BEGIN_DATE"));
        map.put("endDate",rs.getString("END_DATE"));
        map.put("managerId",rs.getString("MANAGER_ID"));
        map.put("taskType",rs.getString("TASK_TYPE"));
        map.put("taskNo",rs.getString("TASK_NO"));
        map.put("color", rs.getString("COLOR"));
        map.put("subject",rs.getString("SUBJECT"));
        map.put("rate", rs.getString("RATE"));
        map.put("finishTime", rs.getString("FINISH_TIME"));
        map.put("totalTime",rs.getString("TOTAL_TIME"));
        map.put("useTime", rs.getString("USE_TIME"));
        map.put("calId", rs.getString("CAL_ID"));
        map.put("important",rs.getString("IMPORTANT"));
        map.put("taskStatus", rs.getString("TASK_STATUS"));
        map.put("editTime", rs.getString("EDIT_TIME"));
        taskList.add(map);
      }  
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return taskList;
  }
  public void deleteTaskById(Connection dbConn,String seqId) throws Exception{
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9Task.class, Integer.parseInt(seqId));
  }
  public void deleteTask(Connection dbConn,String seqIds) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from TASK where SEQ_ID in(" + seqIds + ")";
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, log);
   }
  }
  public Map<String,String> selectTaskById(Connection dbConn,String seqId) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select *from TASK where SEQ_ID=" + seqId;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      Map<String,String> map = null;
      if(rs.next()){
        map = new HashMap<String,String>();
        map.put("seqId",rs.getString("SEQ_ID"));
        map.put("userId",rs.getString("USER_ID"));
        map.put("content",rs.getString("CONTENT"));
        map.put("beginDate",rs.getString("BEGIN_DATE"));
        map.put("endDate",rs.getString("END_DATE"));
        map.put("managerId",rs.getString("MANAGER_ID"));
        map.put("taskType",rs.getString("TASK_TYPE"));
        map.put("taskNo",rs.getString("TASK_NO"));
        map.put("color", rs.getString("COLOR"));
        map.put("subject",rs.getString("SUBJECT"));
        map.put("rate", rs.getString("RATE"));
        map.put("finishTime", rs.getString("FINISH_TIME"));
        //System.out.println(rs.getString("FINISH_TIME"));
        map.put("totalTime",rs.getString("TOTAL_TIME"));
        //System.out.println((rs.getString("TOTAL_TIME")));
        map.put("useTime", rs.getString("USE_TIME"));
        map.put("calId", rs.getString("CAL_ID"));
        map.put("important",rs.getString("IMPORTANT"));
        map.put("taskStatus", rs.getString("TASK_STATUS"));
        map.put("editTime", rs.getString("EDIT_TIME"));
      } 
      return map;
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
}
