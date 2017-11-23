package t9.project.task.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.project.project.data.T9ProjProject;
import t9.project.task.data.T9ProjTask;

public class T9TaskLogic {
  /**
   * 根据项目id 获取任务列表
   * 
   * @param dbConn
   * @param projId
   * @return
   */
  public String getTaskList(Connection dbConn, String projId) {
    T9ORM orm = new T9ORM();
    StringBuffer data = new StringBuffer("[");
    try {
      if (projId == "null" || "".equals(projId)) {
        projId = "0";
      }
      String[] filters = { "PROJ_ID=" + Integer.parseInt(projId) };
      List<T9ProjTask> taskList = orm.loadListSingle(dbConn, T9ProjTask.class,
          filters);
      if (taskList != null && taskList.size() > 0) {
        for (T9ProjTask task : taskList) {
          data.append("{\"taskName\":\"" + task.getTaskName() + "\",");
          data.append("\"taskId\":\"" + task.getSeqId() + "\",");
          data.append("\"taskUser\":\""
              + queryUsername(dbConn, task.getTaskUser()) + "\",");
          data.append("\"beginDate\":\"" + task.getTaskStartTime() + "\",");
          data.append("\"endDate\":\"" + task.getTaskEndTime() + "\",");
          data.append("\"taskMilestone\":\"" + task.getTaskMilestone() + "\",");
          data.append("\"taskPercentComplete\":\""
              + task.getTaskPercentComplete() + "\",");
          data.append("\"flowIdStr\":\""
                  + task.getFlowIdStr() + "\",");
          data.append("\"taskTime\":\"" + task.getTaskTime() + "\"},");
        }
      }
      if (data.length() > 3) {
        data = data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return data.toString();
  }

  /**
   * 添加任务信息
   * 
   * @param dbConn
   * @param request
   * @param person
   * @param taskInfo
   */
  public void addTaskInfo(Connection dbConn, HttpServletRequest request,
      T9Person person) {
    T9ORM orm = new T9ORM();
    String taskId = request.getParameter("taskId");
    String projId = request.getParameter("projId");
    String taskNo = request.getParameter("taskNo");
    String taskName = request.getParameter("taskName");
    String taskUser = request.getParameter("taskUser");
    String taskStartTime = request.getParameter("taskStartTime");
    String taskEndTime = request.getParameter("taskEndTime");
    String taskDescription = request.getParameter("taskDescription");
    String taskConstrain = request.getParameter("constrain");
    String taskMilestone = request.getParameter("flag");
    String remark = request.getParameter("remark");
    String taskLevel = request.getParameter("taskLevel");
    String preTask = request.getParameter("preTask");
    String parentTask = request.getParameter("parentTask");
    String FLOW_ID_STR = request.getParameter("FLOW_ID_STR");
   // System.out.println(FLOW_ID_STR);
    int taskTime = new Long(this.getWorktime(taskStartTime, taskEndTime))
        .intValue();
    T9ProjTask taskInfo = new T9ProjTask();
    try {
      if (taskId != null && !"".equals(taskId)) {
        taskInfo = (T9ProjTask) orm.loadObjSingle(dbConn, T9ProjTask.class,
            Integer.parseInt(taskId));
      }
      taskInfo.setTaskNo(taskNo);
      taskInfo.setTaskName(taskName);
      if (parentTask != null && !"".equals(parentTask)) {

        taskInfo.setParentTask(Integer.parseInt(parentTask));
      }
      if (preTask != null && !"".equals(preTask)) {
        taskInfo.setPreTask(Integer.parseInt(preTask));
      }
      if (projId != null && !"".equals(projId)) {
        taskInfo.setProjId(Integer.parseInt(projId));
      }
      taskInfo.setRemark(remark);
      taskInfo.setTaskDescription(taskDescription);
      taskInfo.setTaskEndTime(T9Utility.parseDate(taskEndTime));
      taskInfo.setTaskStartTime(T9Utility.parseDate(taskStartTime));
      taskInfo.setTaskLevel(taskLevel);
      taskInfo.setTaskStatus(0);
      taskInfo.setTaskUser(taskUser);
      taskInfo.setTaskTime(taskTime + 1);
      taskInfo.setFlowIdStr(FLOW_ID_STR);
      if (taskConstrain != null && !"".equals(taskConstrain)) {
        taskInfo.setTaskConstrain(Integer.parseInt(taskConstrain));
      }
      if (taskMilestone != null && !"".equals(taskMilestone)) {
        taskInfo.setTaskMilestone(Integer.parseInt(taskMilestone));
      }
      if (taskId == null || "".equals(taskId)) {
        orm.saveSingle(dbConn, taskInfo);
      } else {
        orm.updateSingle(dbConn, taskInfo);
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    }

  }

  /**
   * 获取两个日期之间的天数
   * 
   * @param starttime
   * @param endtime
   * @return
   */

  public long getWorktime(String starttime, String endtime) {
    // 设置时间格式
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // 开始日期
    Date dateFrom = null;
    Date dateTo = null;
    try {
      dateFrom = dateFormat.parse(starttime);
      dateTo = dateFormat.parse(endtime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    long days = 0;
    long quot = dateTo.getTime() - dateFrom.getTime();
    days = quot / 1000 / 60 / 60 / 24;
    return days;
  }

  /**
   * 获取任务信息
   * 
   * @param dbConn
   * @param taskId
   * @return
   */
  public String getTaskInfo(Connection dbConn, String taskId) {
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer();
    try {
      T9ProjTask tk = (T9ProjTask) orm.loadObjSingle(dbConn, T9ProjTask.class,
          Integer.parseInt(taskId));
      sb = T9FOM.toJson(tk);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return sb.toString();
  }

  /**
   * 删除任务
   * 
   * @param dbConn
   * @param taskId
   */
  public void delTaskInfo(Connection dbConn, String taskId) {
    T9ORM orm = new T9ORM();
    try {
      orm.deleteSingle(dbConn, T9ProjTask.class, Integer.parseInt(taskId));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * 获取项目任务人员
   * 
   * @param dbConn
   * @param projId
   * @return
   */
  public String getTaskUser(Connection dbConn, String projId) {
    T9ORM orm = new T9ORM();
    StringBuffer data = new StringBuffer("[");
    try {
      String projUser = "";
      T9ProjProject project = (T9ProjProject) orm.loadObjSingle(dbConn,
          T9ProjProject.class, Integer.parseInt(projId));
      if (project != null) {
        projUser = project.getProjUser();
        String[] projUsers = {};
        if (projUser != null) {
          projUsers = projUser.split("\\|");
        }
        for (int i = 0; i < projUsers.length; i++) {
          String[] users = projUsers[i].split(",");
          for (int j = 0; j < users.length; j++) {
            data.append("{\"userId\":\"" + users[j] + "\",");
            data.append("\"userName\":\"" + queryUsername(dbConn, users[j])
                + "\"},");
          }
        }
        if (data.length() > 3) {
          data = data.deleteCharAt(data.length() - 1);
        }
        data.append("]");
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return data.toString();
  }
  /**
   * 获取项目表单名称
   * 
   * @param dbConn
   * @param 
   * @return
 * @throws Exception 
   */
  public String getFlowFormName(Connection dbConn) throws Exception {
    //T9ORM orm = new T9ORM();
    String sql = "select seq_id,flow_name from flow_type";
    PreparedStatement ps = dbConn.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    StringBuffer data = new StringBuffer("[");
    while(rs.next()){
    	  data.append("{\"flowId\":\"" +rs.getInt("seq_id") + "\",");
      data.append("\"flowName\":\"" + rs.getString("flow_name")+ "\"},");
    }
    /*
     * ny 2013-5-10 修改bug
     */
    if(data.length()>3){
      data=data.deleteCharAt(data.length()-1);
    }
    data.append("]");
 //   System.out.println( data.toString());
    return data.toString();
	  
  }
  /* 获取项目表单名称
  * 
  * @param dbConn
  * @param 
  * @return
  * @throws Exception 
  */
 public String getflowNameByflowId(Connection dbConn,String flowId) throws Exception {
   //T9ORM orm = new T9ORM();
	if(T9Utility.isNullorEmpty(flowId)){
		flowId="0";
	}
	else{
		flowId=flowId.substring(0,flowId.length()-1);  
	}
	//System.out.println(flowId);
   String sql = "select seq_id,flow_name from flow_type where seq_id in ("+flowId+")";
   PreparedStatement ps = dbConn.prepareStatement(sql);
   ResultSet rs = ps.executeQuery();
   StringBuffer data = new StringBuffer("[");
   while(rs.next()){
   	  data.append("{\"flowId\":\"" +rs.getInt("seq_id") + "\",");
     data.append("\"flowName\":\"" + rs.getString("flow_name")+ "\"},");
   }

   data.append("]");
//   System.out.println( data.toString());
   return data.toString();
	  
 }
  /**
   * 获取人员名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryUsername(Connection dbConn, String seqIds)
      throws Exception {
    if (T9Utility.isNullorEmpty(seqIds)) {
      return "";
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select user_name from person where seq_id in (" + seqIds
        + ")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String userName = rs.getString("user_name");
        sb.append(userName + ",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (sb.length() > 1) {
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  public String getTaskName(Connection dbConn, String projId) {
    T9ORM orm = new T9ORM();
    StringBuffer data = new StringBuffer("[");
    try {
      String[] filters = { "1=1 and proj_id=" + Integer.parseInt(projId) };
      List<T9ProjTask> taskList = orm.loadListSingle(dbConn, T9ProjTask.class,
          filters);
      if (taskList != null && taskList.size() > 0) {
        for (T9ProjTask task : taskList) {
          data.append("{\"taskId\":" + task.getSeqId() + ",");
          data.append("\"taskName\":\"" + task.getTaskName() + "\"},");
        }
      }
      if (data.length() > 3) {
        data = data.deleteCharAt(data.length() - 1);
      }
      data.append("]");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return data.toString();
  }

  /**
   * 获取任务(进行中/已结束)
   * 
   * @author zq 2013-3-26
   * @param dbConn
   * @param map
   * @param person
   * @param request
   * @return
   * @throws Exception
   */
  public String getProjTaskTree(Connection dbConn, Map map, T9Person person,
      HttpServletRequest request) throws Exception {
    String sql = "";
    String conditionStr = "";
    String projId = request.getParameter("projlist");
    String flag = request.getParameter("flag");
    int range = Integer.parseInt(request.getParameter("range"));

    try {
      if (!T9Utility.isNullorEmpty(projId)) {
        conditionStr = " and proj_id = " + T9DBUtility.escapeLike(projId);
      }
        Calendar calendar  =   new  GregorianCalendar();
        switch (range)
        {
          case 0:
            conditionStr +=" AND task_status=0";
          break;
          //本周任务
          case 1:
            int minimum = calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
            int current = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.add(Calendar.DAY_OF_WEEK, minimum-current);
            String wStart = T9Utility.getDateTimeStr(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            String wEnd = T9Utility.getDateTimeStr(calendar.getTime());
            conditionStr +=" AND TASK_END_TIME<='"+wEnd+"' && TASK_END_TIME>='"+wStart+"'";
            break;
          //本月任务
          case 2:
            calendar.set( Calendar.DATE,  1 );
            String mStart = T9Utility.getDateTimeStr(calendar.getTime());
            calendar.roll(Calendar.DATE,  - 1 );
            String mEnd = T9Utility.getDateTimeStr(calendar.getTime());
            conditionStr +=" AND TASK_END_TIME <= '"+mEnd+"' && TASK_END_TIME >= '"+mStart+"'";
            break;
          //未来任务
          case 3:
            conditionStr +=" AND TASK_START_TIME >= '"+T9Utility.getDateTimeStr(new Date())+"'";
            break;
          case 4:
            conditionStr +="  AND task_status=1";
            break;
          default:
            break;
        }
      sql = "select proj_task.seq_id,proj_id,task_no,proj_project.proj_name,task_name,task_level,task_start_time,task_time,task_end_time,task_status from proj_task,proj_project where 1=1 and proj_task.proj_id = proj_project.seq_id and proj_project.proj_status=2 and proj_task.task_user = "
          + person.getSeqId()
          + conditionStr
          + " and task_status="
          + flag
          + " order by task_start_time desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,
          queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  /**
   * 获取参与任务列表
   * 
   * @author yc 2013-4-11
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
   * @throws Exception
   */
  
  public String getFlowName(Connection conn,Map map,int projId,String userId,int taskId) throws Exception{
	  String seqIds = this.getFlowIdByUserId(conn,projId ,userId,taskId);
	//  System.out.println(seqIds);
	  String   flowId = this.getflowIdbyRunId(conn, projId, userId, taskId);
	  String seqIdsFinsh="";
	//  System.out.println(flowId);
	  if(T9Utility.isNullorEmpty(seqIds)){
		  seqIdsFinsh="0";
	  }
	  else if(!T9Utility.isNullorEmpty(seqIds)&&!T9Utility.isNullorEmpty(flowId)){
		 String a[] = seqIds.split(",");
		 String b[] = flowId.split(",");
		 List a1 = new ArrayList();
		 List b1 = new ArrayList();
		 for(int i=0;i<a.length;i++){
			 a1.add(a[i]);
		 }
		 for(int i=0;i<b.length;i++){
			 b1.add(b[i]);
		 }
		 
		 for(int j=0;j<b1.size();j++){
			 if(a1.contains(b1.get(j))){
				a1.remove(b1.get(j));
			 }
		 }
		seqIdsFinsh = a1.toString();
		// System.out.println(seqIdsFinsh);
		 if(T9Utility.isNullorEmpty(seqIdsFinsh)||seqIdsFinsh.indexOf(",")==-1){
//			  if(!T9Utility.isNullorEmpty(seqIds)){
//				  seqIdsFinsh = seqIds;
//			  }
//			  else{
				  seqIdsFinsh="0";  
			//  }
			 
		  }else{
			  seqIdsFinsh=  seqIdsFinsh.substring(1,seqIdsFinsh.lastIndexOf("]"));
			 // System.out.println(seqIdsFinsh);
		  }
		  
	  }else{
		  seqIdsFinsh=seqIds;
	  }
	 
	  String sql = "select 	seq_id,flow_type,flow_name  FROM flow_type where seq_id in ("+seqIdsFinsh+")";
	  T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,
          queryParam, sql);  
      return pageDataList.toJson();
  }
  /**
   * runId
   * 
   * @author yc 2013-4-12
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
   * @throws Exception
   */
  
  public void updaterunId(Connection conn,int projId,String runId,int taskId,String userId) throws Exception{
	  String runId1 = this.getrunIdByUserId(conn, projId, userId, taskId);
	  String sql = "update proj_task set RUN_ID_STR = ? where proj_id = ? and seq_id = ?";
	  PreparedStatement ps = conn.prepareStatement(sql);
	  if(T9Utility.isNullorEmpty(runId1)){
		  ps.setString(1, runId+",");  
	  }
	  else{
		  ps.setString(1, runId1+","+runId+",");  
	  }
	  ps.setInt(2,projId );
	  ps.setInt(3,taskId);
	  ps.executeUpdate();
	  ps.close();
	 
  }
 
  /**
   * 获取正在执行的项目列表
   * 
   * @author yc 2013-4-12
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
   * @throws Exception
   */
  
  public String getFlowNowName(Connection conn,Map map,String userId,int projId,int taskId) throws Exception{
	  StringBuffer data=new StringBuffer("[");
  String  runIds = this.getrunIdByUserId(conn, projId, userId, taskId);
  if(T9Utility.isNullorEmpty(runIds)){
	  runIds="0";
  }
	 String sql="SELECT seq_id,run_id,run_name,flow_id FROM flow_run where  end_time is null and run_id in("+runIds+") ";
	 PreparedStatement ps = conn.prepareStatement(sql);
	 ResultSet rs = ps.executeQuery();
	 while(rs.next()){
		 data.append("{\"seqId\":"+rs.getInt("seq_id")+",");
         data.append("\"runId\":"+rs.getInt("run_id")+",");
         data.append("\"flowId\":"+rs.getInt("flow_id")+",");
        // System.out.println(rs.getInt("flow_id"));
         data.append("\"runName\":\""+rs.getString("run_name")+"\"},");
	 }
	 if(data.length()>3){
	        data=data.deleteCharAt(data.length()-1);
	      }
     data.append("]");
//	  T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
//      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,
//          queryParam, sql);  
//      return pageDataList.toJson();
	 return data.toString();
  }
    
  /**
   * 获取结束项目列表
   * 
   * @author yc 2013-4-12
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
   * @throws Exception
   */
  
  public String getFinshflow(Connection conn,Map map,String userId,int projId,int taskId) throws Exception{
	  StringBuffer data=new StringBuffer("[");
  String  runIds = this.getrunIdByUserId(conn, projId, userId, taskId);
  if(T9Utility.isNullorEmpty(runIds)){
	  runIds="0";
  }
  
	 String sql="SELECT seq_id,run_id,run_name,flow_id FROM flow_run where  end_time is not null and run_id in("+runIds+") ";
	 PreparedStatement ps = conn.prepareStatement(sql);
	 ResultSet rs = ps.executeQuery();
	 while(rs.next()){
		 data.append("{\"seqId\":"+rs.getInt("seq_id")+",");
         data.append("\"runId\":"+rs.getInt("run_id")+",");
         data.append("\"flowId\":"+rs.getInt("flow_id")+",");
        // System.out.println(rs.getInt("flow_id"));
         data.append("\"runName\":\""+rs.getString("run_name")+"\"},");
	 }
	 if(data.length()>3){
	        data=data.deleteCharAt(data.length()-1);
	      }
     data.append("]");
//	  T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(map);
//      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,
//          queryParam, sql);  
//      return pageDataList.toJson();
	 return data.toString();
  }
  /**
   * 获取正在执行的项目列表
   * 
   * @author yc 2013-4-12
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
   * @throws Exception
   */
  
  public String getNameByRunId(Connection conn,String runIds) throws Exception{
	  StringBuffer data=new StringBuffer("[");
  if(T9Utility.isNullorEmpty(runIds)){
	  runIds="0";
  }
	 String sql="SELECT run_name FROM flow_run where  end_time is null and run_id=?";
	 PreparedStatement ps = conn.prepareStatement(sql);
	 ps.setInt(1, Integer.parseInt("runIds"));
	 ResultSet rs = ps.executeQuery();
	 while(rs.next()){
         data.append("\"runName\":\""+rs.getString("run_name")+"\"},");
	 }
	 if(data.length()>3){
	        data=data.deleteCharAt(data.length()-1);
	      }
     data.append("]");
	 return data.toString();
  }
      /**
       * 通过UserId获取FlowId
       * 
       * @author yc 2013-4-11
       * @param dbConn
       * @param classNo
       * @return
     * @throws Exception 
     * @throws Exception 
       * @throws Exception
       */
  public String getFlowIdByUserId(Connection conn,int projId,String seqId,int taskId) throws Exception{
	  if(T9Utility.isNullorEmpty(seqId)){
		  seqId="0";
	  }
	 // if(T9Utility.is)
	  String flowId="";
	String sql = "select FLOW_ID_STR from proj_task where TASK_USER =? and  proj_id =? and seq_id =?";
	PreparedStatement ps = conn.prepareStatement(sql);
	ps.setString(1, seqId);
	ps.setInt(2, projId);
	ps.setInt(3, taskId);
	ResultSet rs = ps.executeQuery();
	while(rs.next()){
		if(!T9Utility.isNullorEmpty( rs.getString("FLOW_ID_STR"))){
			 flowId+= rs.getString("FLOW_ID_STR");
		}
		
	}
	//System.out.println(flowId);
	rs.close();
	ps.close();
	if(!T9Utility.isNullorEmpty(flowId)&&flowId.split(",").length>1){
		 return  flowId.substring(0,flowId.length()-1);	
	}else{
		return "";
	}
	
  }
  
  /**
   * 通过UserId获取FlowId
   * 
   * @author yc 2013-4-11
   * @param dbConn
   * @param classNo
   * @return
 * @throws Exception 
 * @throws Exception 
   * @throws Exception
   */
public String getrunIdByUserId(Connection conn,int projId,String seqId,int taskId) throws Exception{
  if(T9Utility.isNullorEmpty(seqId)){
	  seqId="0";
  }
 // if(T9Utility.is)
  String flowId="";
String sql = "select RUN_ID_STR from proj_task where TASK_USER =? and  proj_id =? and seq_id =?";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, seqId);
ps.setInt(2, projId);
ps.setInt(3, taskId);
ResultSet rs = ps.executeQuery();
while(rs.next()){
	if(!T9Utility.isNullorEmpty( rs.getString("RUN_ID_STR"))){
		 flowId+= rs.getString("RUN_ID_STR");
		// System.out.println(flowId.split(",").length);
	}
	
}
//System.out.println(flowId);
rs.close();
ps.close();
//System.out.println(flowId);
if(!T9Utility.isNullorEmpty(flowId)&&flowId.split(",").length>=1){
	
	 return  flowId.substring(0,flowId.lastIndexOf(","));	
}else{
	return "";
}

}
/**
 * 获取flowIdbyrunId
 * 
 * @author zq 2013-4-16
 * @param dbConn
 * @param classNo
 * @return
 * @throws Exception 
 */
public String getflowIdbyRunId(Connection conn ,int projId,String userId,int taskId) throws Exception{
	 String seqIds = this.getrunIdByUserId(conn, projId, userId, taskId);
	 
	 if(T9Utility.isNullorEmpty(seqIds)){
		 seqIds="0";
	 }
	
	 String sql="SELECT flow_id FROM flow_run where run_id in ("+seqIds+")";
	 String flowId="";
	 PreparedStatement ps = conn.prepareStatement(sql);
	 ResultSet rs = ps.executeQuery();
	 while(rs.next()){
			if(!T9Utility.isNullorEmpty( rs.getString("flow_id"))){
				flowId+= rs.getString("flow_id")+",";
				// System.out.println(flowId.split(",").length);
			}
			
		}
		//System.out.println(flowId);
		rs.close();
		ps.close();
		//System.out.println(flowId);
		if(!T9Utility.isNullorEmpty(flowId)&&flowId.split(",").length>=1){
			
			 return  flowId.substring(0,flowId.lastIndexOf(","));	
		}else{
			return "";
		}

	//return null;
}
  /**
   * 获取参与任务列表
   * 
   * @author zq 2013-3-28
   * @param dbConn
   * @param classNo
   * @return
   * @throws Exception
   */
  public String getProjListByUser(Connection dbConn, T9Person person)
      throws Exception {
    StringBuffer sb = new StringBuffer("[");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID,PROJ_NAME from proj_project where PROJ_USER LIKE '%|"
        + person.getSeqId()
        + ",%' OR PROJ_USER LIKE '%,"
        + person.getSeqId()
        + ",%'";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        sb.append("{");
        sb.append("\"seqId\":" + rs.getInt("seq_id") + ",");
        sb.append("\"projName\":\"" + rs.getString("PROJ_NAME") + "\"},");
      }
      if (sb.length() > 3) {
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return sb.toString();
  }
  public void updateTask(Connection con,T9ProjTask projTask) throws Exception {
    T9ORM t9orm = new T9ORM();
    t9orm.updateSingle(con, projTask);
  }
  public T9ProjTask getTask(Connection con,int seqId) throws Exception {
    T9ORM t9orm = new T9ORM();
//    System.out.println(seqId);
    return (T9ProjTask) t9orm.loadObjSingle(con, T9ProjTask.class, seqId);
  }
}