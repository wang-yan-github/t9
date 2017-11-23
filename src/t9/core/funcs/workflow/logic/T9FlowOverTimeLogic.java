package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9FlowOverTimeLogic {
  public String getSql(int flowId, String flowStatus, String starttime, String endtime ,Map request, T9Person user, String sortId) throws Exception {
    String query="select A.RUN_ID,B.RUN_NAME,C.PRCS_NAME,A.PRCS_FLAG,E.USER_NAME,C.TIME_OUT,C.TIME_EXCEPT,A.OP_FLAG,A.PRCS_TIME,A.PRCS_FLAG,"
      + " A.DELIVER_TIME,A.CREATE_TIME,B.FLOW_ID , C.TIME_OUT_TYPE,A.PRCS_ID"
      +" from  FLOW_RUN_PRCS A "
      +" inner join FLOW_RUN B on A.RUN_ID=B.RUN_ID"
      +" inner join FLOW_PROCESS C on B.FLOW_ID=C.FLOW_SEQ_ID and C.PRCS_ID = A.FLOW_PRCS"
      +" inner join FLOW_TYPE D on B.FLOW_ID=D.seq_id"
      +" inner join person E on A.USER_ID=E.seq_id where";

    if (!"".equals(flowId)&& flowId != 0) {
      query += " B.FLOW_ID='"
        + flowId + "' and";
    }
    if(flowStatus.equals("doing")){
      query +=" A.PRCS_FLAG in ('1','2') and ";
    }else if(flowStatus.equals("end")){
      query +=" A.PRCS_FLAG in ('3','4') and ";
    }      
    boolean isHaveStartTime = false;
    boolean isHaveEndTime = false;
    if (!"".equals(starttime) && starttime != null) {
      String value = T9DBUtility.getDateFilter("A.PRCS_TIME",starttime , ">=");
      query += " "+ value +" and ";
    }
    if (!"".equals(endtime) && endtime != null) {
      String value = T9DBUtility.getDateFilter("A.PRCS_TIME",endtime , "<=");
      query += " "+ value +" and ";
    }
    if (!"".equals(sortId)) {
      sortId = T9WorkFlowUtility.getOutOfTail(sortId);
      query += " D.flow_sort IN (" + sortId + ") and ";
    }
    query = query + " A.TOP_FLAG = '0' and A.OP_FLAG='1' and  A.TIME_OUT_FLAG='1' order by A.RUN_ID desc";
    return query;
  }
  public ArrayList<T9DbRecord>  getOverTimeList( int flowId, String flowStatus, String starttime, String endtime, Connection conn,Map request, T9Person user , String sortId) throws Exception {
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    String query = this.getSql(flowId, flowStatus, starttime, endtime, request, user , sortId);
    Statement stm = null;
    ResultSet rs = null;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        T9DbRecord dr = new T9DbRecord();
        int runId = rs.getInt("RUN_ID");
        int prcsId = rs.getInt("PRCS_ID");
        
        dr.addField("流水号", runId);
        String runName =rs.getString("RUN_NAME");
        dr.addField("工作名称/文号", runName);
        String prcsName = rs.getString("PRCS_NAME");
        dr.addField("步骤名称", prcsName);
        String prcsFlag = rs.getString("PRCS_FLAG");
        String state = "";
        int nowState =  0; 
        if (T9Utility.isInteger(prcsFlag)) {
          nowState = Integer.parseInt(prcsFlag);
        }
        switch(nowState) {
          case 1:
            state = "未接收";
            break;
          case 2:
            state = "办理中";
            break;
          case 3:
            state = "已转交";
            break;
          case 4:
            state = "已办结";
            break;
        }
        dr.addField("主办人状态", state);
        String userName = rs.getString("USER_NAME");
        dr.addField("办理人", userName);
        String timeOut = rs.getString("TIME_OUT");
        dr.addField("办理时限", timeOut);
        String timeStr="";
        long timeUsed = 0 ;
        Timestamp deliverTime = rs.getTimestamp("DELIVER_TIME");
        Timestamp createTime = rs.getTimestamp("CREATE_TIME");
        String opFlag = rs.getString("OP_FLAG");
        Timestamp prcsTime = rs.getTimestamp("PRCS_TIME");
        String except = rs.getString("TIME_EXCEPT");
        String timeOutType = rs.getString("TIME_OUT_TYPE");
        //计算用时 时间  
        if("null".equals(timeOut)){
          timeOut = "1";
        }
        if(!"null".equals(timeOut) && !"".equals(timeOut) && opFlag.equals("1")){
          long start = 0 ;
          if ("1".equals(timeOutType) 
              && prcsId != 1) {
            start = T9FlowRunUtility.getPrcsDeliverTime(conn, runId, prcsId).getTime();
          } else {
            if (prcsTime!=null) {
              start = prcsTime.getTime();
            }
          }
          
          Date  date = new Date();
          if(prcsFlag.equals("1")){// prcsFlag 是判断状态1-未接收  2-办理
            timeUsed = date.getTime() - start; 
          }else if(prcsFlag.equals("2")){ 
            timeUsed = date.getTime() - start; // 状态为2 当前时间-工作接收时间=用时时间prcsBeginTime.getTime()
          }else{
           if(deliverTime!=null){
             timeUsed = deliverTime.getTime() - start;//用时prcsBeginTime.getTime()
           }
          }
          //-- 超时信息 --       
          // 如果用时 大于 time_out 时间 说明超时 设置time_out_flag为1
           Double timeOut1 = Double.parseDouble(timeOut);
           
            if(timeUsed > timeOut1*3600000){  // 如果用时 大于 time_out 时间 说明超时 设置time_out_flag为1
             String timeDesc = T9WorkFlowUtility.getTimeOut(timeOut, new Date(start), new Date(), "dhms", except);
             if(!"".equals(timeDesc)){
               timeStr = timeDesc;
            }
         }
        }
        dr.addField("超时时间", timeStr);
        result.add(dr);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, null);
    }
    
    return result;
  }
  public String getWorkOverTimeList(
      int flowId, String flowStatus, String starttime, String endtime, Connection conn,Map request, T9Person user, String sortId)
      throws Exception {
    String result="";
    Statement stmt = null;
    ResultSet rs = null;// 结果集
    String query= this.getSql(flowId, flowStatus, starttime, endtime, request, user , sortId);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request,T9PageQueryParam.class,null);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,query);
    for (int i = 0 ; i < pageDataList.getRecordCnt() ; i ++) {
      
      T9DbRecord record = pageDataList.getRecord(i);
      String timeStr="";
      long timeUsed = 0 ;
      Timestamp deliverTime = null;
      Timestamp createTime = null;
      int prcsId = T9Utility.cast2Long(record.getValueByName("prcsId")).intValue();
      int runId = T9Utility.cast2Long(record.getValueByName("runId")).intValue();
      Object timeOu = record.getValueByName("timeOut");
      String timeOut = String.valueOf(timeOu);
      Object opFla = record.getValueByName("opFlag");
      String opFlag = String.valueOf(opFla);
      Object oTimeOutType = record.getValueByName("timeOutType");
      String timeOutType = String.valueOf(oTimeOutType);
      Timestamp prcsTime = (Timestamp)record.getValueByName("prcsTime"); //???
      Object prcsFla = record.getValueByName("prcsFlag");
      String prcsFlag = String.valueOf(prcsFla);
      deliverTime = (Timestamp) record.getValueByName("deliverTime");
      createTime = (Timestamp) record.getValueByName("createTime");
     // String createtim= String.valueOf(createTime);
      Object timeExcep = record.getValueByName("timeExcept"); //00-都不排除11-都排除 01-排除周日10-排除周六
      String except = String.valueOf(timeExcep);
      //计算用时 时间  
      if("null".equals(timeOut)){
        timeOut = "1";
      }
      if(!"null".equals(timeOut)&& !"".equals(timeOut) && opFlag.equals("1")){
        long start = 0 ;
        if ("1".equals(timeOutType) 
            && prcsId != 1) {
          start = T9FlowRunUtility.getPrcsDeliverTime(conn, runId, prcsId).getTime();
        } else {
          if (prcsTime!=null) {
            start = prcsTime.getTime();
          }
        }
        
        Date  date = new Date();
        if(prcsFlag.equals("1")){// prcsFlag 是判断状态1-未接收  2-办理
          timeUsed = date.getTime() - start; 
        }else if(prcsFlag.equals("2")){ 
          timeUsed = date.getTime() - start; // 状态为2 当前时间-工作接收时间=用时时间prcsBeginTime.getTime()
        }else{
         if(deliverTime!=null){
           timeUsed = deliverTime.getTime() - start;//用时prcsBeginTime.getTime()
         }
        }
        
        //-- 超时信息 --       
       Double timeOut1 = Double.parseDouble(timeOut);
       if(timeUsed > timeOut1*3600000){  
         String timeDesc = T9WorkFlowUtility.getTimeOut(timeOut, new Date(start), new Date(), "dhms", except);
         if(!"".equals(timeDesc)){
           timeStr = timeDesc;
        }
       }
      }
      record.updateField("timeExcept", timeStr);
      Object oNowState = record.getValueByName("nowState");
      String state = "";
      int nowState =  Integer.parseInt((String)oNowState);
      switch(nowState) {
        case 1:
          state = "未接收";
          break;
        case 2:
          state = "办理中";
          break;
        case 3:
          state = "已转交";
          break;
        case 4:
          state = "已办结";
          break;
      }
      record.updateField("nowState", state);
    }
    result = pageDataList.toJson();
    return result;
  }
 // 获得集合中所有的数据  
  //根据传过来的统计分类方式（人员、部门、角色）获取$USER_ID，形如：('admin','dq') 用于WHERE子句
  public static String getqueryBumen(String personBm, String bumenquery,Connection conn) throws Exception{
 
    Statement stmt = null;
    ResultSet rs = null;
    Statement stmt1 = null;
    ResultSet rs1 = null;
    Statement stmt2 = null;
    ResultSet rs2 = null;   
    try {
      stmt = conn.createStatement();
      stmt1 = conn.createStatement();stmt2 = conn.createStatement();
      String Bmperson="";
      String personBms = "";
      List list =new ArrayList();
      String querybm = "";
      if(bumenquery.equals("user")){
        if(!"".equals(personBm) &&personBm!=null){
         personBm = personBm.trim();
         personBm = personBm.replaceAll(",", "','");
         personBm = "'"+personBm+"'";
         }  return personBm;
      }else if(bumenquery.equals("dept")){
        if(!"".equals(personBm) && personBm!=null){
          personBm = personBm.trim();
          personBm = personBm.replaceAll(",", "','");
          personBm = "'"+personBm+"'";
          querybm="select seq_id from person where dept_id in("+personBm +")";
          rs = stmt.executeQuery(querybm);
          while(rs.next()){
            personBms = rs.getInt("seq_id")+",";
            Bmperson += personBms;
             }
          if(Bmperson!=null && !"".equals(Bmperson)){
            Bmperson=Bmperson.substring(0, Bmperson.length()-1);
          }
         
        }
      }else if(bumenquery.equals("role")){
        
        if(!"".equals(personBm) && personBm !=null){
          personBm = personBm.trim();
          personBm = personBm.replaceAll(",", "','");
          personBm = "'"+personBm+"'";
          querybm="select seq_id from person where USER_PRIV in("+personBm +")";
          rs1 = stmt1.executeQuery(querybm);
          
          while(rs1.next()){
            personBms = rs1.getInt("seq_id")+",";
            Bmperson += personBms;
          }
          if(Bmperson!=null && !"".equals(Bmperson)){
            Bmperson=Bmperson.substring(0, Bmperson.length()-1);
          }
        }
       }
      return Bmperson;
     }catch(Exception ex){
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  public String WorkFlow(String flowquery,Connection conn, T9Person user)throws Exception {
   StringBuffer sb = new StringBuffer();
    Statement stmt = null;
    ResultSet rs = null;// 结果集
    String personBm="";
    int timeoutAll=0;
    try {
      stmt = conn.createStatement();
      if(!"".equals(flowquery)&& flowquery!=null){
        String[] queryStrs = flowquery.split(",");
        flowquery = "";
        for(int i = 0 ;i < queryStrs.length ; i++){
          flowquery +=  "'" + queryStrs[i] + "'" + ",";
        }
        flowquery = flowquery.substring(0, flowquery.length() - 1);

      }
      String flowquerys = "select d.flow_name from flow_type d, flow_run b where d.seq_id=b.flow_id and  flow_id = "+flowquery;
      rs = stmt.executeQuery(flowquerys);
      String flowName="";
      if(rs.next()){
        flowName = rs.getString("flow_name");
      }
       
      return flowName;
    }catch(Exception ex){
      throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, null);
      }
    }
  public String getNameId(String username,Connection conn, T9Person user)throws Exception {
    StringBuffer sb = new StringBuffer();
     Statement stmt = null;
     ResultSet rs = null;// 结果集

     String personBm="";
     int timeoutAll=0;
     try {
       stmt = conn.createStatement();
      
       String flowquerys = "select seq_id from person where user_name = "+username;
       rs = stmt.executeQuery(flowquerys);
       String userId="";
       if(rs.next()){
         userId = rs.getString("seq_id");
       }
        
       return userId;
     }catch(Exception ex){
       throw ex;
       }finally {
         T9DBUtility.close(stmt, rs, null);
       }
     }
 public String viewDetail(Connection conn , Map request  , int flowId , int userId , String prcsDate1Query , String prcsDate2Query) throws Exception {
   String result = "";
   String sql = "select "
           + "  A.RUN_ID, "
           + "  D.FLOW_NAME, "
           + "  B.RUN_NAME, "
           + "  C.PRCS_NAME, "
           + "  A.PRCS_FLAG, "
           + "  E.USER_NAME, "
           + "  A.PRCS_TIME, "
           + "  A.DELIVER_TIME, "
           + "  B.FLOW_ID, "
           
           + "  C.TIME_OUT , "
           + "  C.TIME_EXCEPT, "
           + "  A.OP_FlAG,"
           + " A.CREATE_TIME"
           
           + "  from  "
           + "   FLOW_RUN_PRCS A  "
           + "    inner join FLOW_RUN B on A.RUN_ID=B.RUN_ID "
           + "    inner join FLOW_PROCESS C on B.FLOW_ID=C.FLOW_SEQ_ID and A.FLOW_PRCS=C.PRCS_ID "
           + "    inner join FLOW_TYPE D on B.FLOW_ID=D.SEQ_ID "
           + "   inner join PERSON E on A.USER_ID=E.SEQ_ID ";
   String where = "";
   if (flowId != 0) {
     where += " B.FLOW_ID=" + flowId + " and";
   }
   if (userId != 0) {
     where += " A.USER_ID=" + userId + " and";
   }
   if (prcsDate1Query != null && !"".equals(prcsDate1Query)) {
     String value = T9DBUtility.getDateFilter("A.PRCS_TIME",prcsDate1Query , ">=");
     where += " "+ value +" and ";
   }
   if (prcsDate2Query != null && !"".equals(prcsDate2Query)) {
     String value = T9DBUtility.getDateFilter("A.PRCS_TIME",prcsDate2Query , "<=");
     where += " "+ value +" and ";
   }
   where += " A.TOP_FLAG = '0' and A.OP_FLAG='1' order by A.RUN_ID ";
   sql = sql + " where "  + where;
   T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
   T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
   for (int i = 0 ; i < pageDataList.getRecordCnt() ; i ++) {
     T9DbRecord record = pageDataList.getRecord(i);
     String timeStr="";
     long timeUsed = 0 ;
     Timestamp deliverTime = null;
     Timestamp createTime = null;
     Object timeOu = record.getValueByName("timeOut");
     String timeOut = String.valueOf(timeOu);
     Object opFla = record.getValueByName("opFlag");
     String opFlag = String.valueOf(opFla);
     Timestamp prcsTime = (Timestamp)record.getValueByName("prcsTime"); //???
     Object prcsFla = record.getValueByName("prcsFlag");
     String prcsFlag = String.valueOf(prcsFla);
     deliverTime = (Timestamp) record.getValueByName("deliverTime");
     createTime = (Timestamp) record.getValueByName("createTime");
     Object timeExcep = record.getValueByName("timeExcept"); //00-都不排除11-都排除 01-排除周日10-排除周六
     String except = String.valueOf(timeExcep);
     //计算用时 时间  
     
     if(T9WorkFlowUtility.isFloat(timeOut)  && opFlag.equals("1")){
       if(prcsTime!=null && !"".equals(prcsTime)){
       if(prcsFlag.equals("1")){
       }else if(prcsFlag.equals("2")){ 
         Date  date = new Date();
         timeUsed = date.getTime() - prcsTime.getTime(); // 状态为2 当前时间-工作接收时间=用时时间prcsBeginTime.getTime()
       }else{
         if(deliverTime!=null){
           timeUsed = deliverTime.getTime() - prcsTime.getTime();//用时prcsBeginTime.getTime()
         }
       }
     }
      Double timeOut1 = Double.parseDouble(timeOut);
      Date beginTime = new Date();
      int timeOutFlag = 0;
      if((prcsFlag.equals("2") || prcsFlag.equals("1")) 
          && !"".equals(timeOut) 
          && timeOut != null){
        if(timeOutFlag == 0){
          if(!"".equals(prcsTime)&&prcsTime != null){
            beginTime = new Date(prcsTime.getTime());
          }else{
            if (createTime != null) {
              beginTime = new Date(createTime.getTime());
            } else {
              beginTime = null;
            }
          }
        }else{
          if (createTime != null) {
            beginTime = new Date(createTime.getTime());
          } else {
            beginTime = null;
          }
        }
      } 
       if(timeUsed > timeOut1*3600000){  // 如果用时 大于 time_out 时间 说明超时 设置time_out_flag为1
        String timeDesc = T9WorkFlowUtility.getTimeOut(timeOut, beginTime, new Date(), "dhms", except);
        if(!"".equals(timeDesc)){
          timeStr = timeDesc;
          timeOutFlag = 1;
       }
      }
     }
     record.updateField("timeExcept", timeStr);
     Object oNowState = record.getValueByName("prcsFlag");
     String state = "";
     int nowState =  Integer.parseInt((String)oNowState);
     switch(nowState) {
       case 1:
         state = "未接收";
         break;
       case 2:
         state = "办理中";
         break;
       case 3:
         state = "已转交";
         break;
       case 4:
         state = "已办结";
         break;
     }
     record.updateField("prcsFlag", state);
   }
   result = pageDataList.toJson();
   return result;
 }

 public Map getOverTimeTotal(String flowquery, String bumenquery,
     String starttime, String endtime, String user, String dept, String role,
     Connection conn, T9Person loginUser) throws Exception {
   // TODO Auto-generated method stub
   // 时间段 过滤
   if (flowquery == null) {
     flowquery = "";
   }
   String[] flowIds = flowquery.split(",");
   Map result = new HashMap();
   Map data = new HashMap();
   Map flowNames = new HashMap();
   for (String tmp : flowIds) {
     if (T9Utility.isInteger(tmp)) {
       String overtimequery = this.getOverTimeTotalSql(tmp, bumenquery, user, dept, role, conn, loginUser);
       Timestamp startTime =  null;
       Timestamp endTime =  null;
       if(!"".equals(starttime)&& starttime!=null){
         overtimequery += " and A.PRCS_TIME >=? ";
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date date  = sdf.parse(starttime + " 00:00:00");
         startTime = new  Timestamp(date.getTime());
       }
       if(!"".equals(endtime)&& endtime!=null){
         overtimequery += " and A.PRCS_TIME <=? ";
         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date date  = sdf.parse(endtime + " 23:59:59");
         endTime = new  Timestamp(date.getTime());
       }
       overtimequery += " group by A.del_flag, B.flow_id, C.prcs_id,D.flow_name, D.seq_id, E.SEQ_ID,E.user_name, A.prcs_time ";
       overtimequery += " order by E.USER_NAME";
       PreparedStatement stmt = null;
       ResultSet rs = null;// 结果集
       try {
         stmt = conn.prepareStatement(overtimequery);
         if (startTime != null) {
           stmt.setTimestamp(1, startTime);
           if (endTime != null) {
             stmt.setTimestamp(2, endTime);
           }
         } else {
           if (endTime != null) {
             stmt.setTimestamp(1, endTime);
           }
         }
         rs = stmt.executeQuery();
         while (rs.next()) {
           int userId = rs.getInt("USER_ID");
           String flowName = rs.getString("flow_Name");
           flowNames.put(tmp, flowName);
           String sUserId = String.valueOf(userId);
           if (data.containsKey(sUserId)) {
             //更新
             Map map2 = (Map)data.get(sUserId);
             //取得当前流程所占的数量
             Map map3 = this.getCount(conn, tmp, userId);
             map2.put(tmp, map3);
           } else {
             //添加
             Map map2 = new HashMap();
             String userName = rs.getString("USER_NAME");
             map2.put("userName", userName);
             //取得当前流程的数量
             Map map3 = this.getCount(conn, tmp, userId);
             map2.put(tmp, map3);
             data.put(sUserId, map2);
           }
         }
       } catch (Exception ex ) {
         throw ex;
       } finally {
         T9DBUtility.close(stmt, rs, null);
       }
     }
   }
   Set<String> set = data.keySet();
   for (String userId : set) {
     Map map = (Map)data.get(userId);
     int workCount = 0;
     int timeOutCount = 0;
     for (String tmp : flowIds) {
       if (T9Utility.isInteger(tmp)) {
         Map map2 = (Map)map.get(tmp);
         if (map2 != null) {
           int workCountTmp = (Integer)map2.get("workCount");
           int timeOutCountTmp = (Integer)map2.get("timeOutCount");
           workCount += workCountTmp ;
           timeOutCount  += timeOutCountTmp;
         }
       }
     }
     Map map3 = new HashMap();
     map3.put("workCount", workCount);
     map3.put("timeOutCount", timeOutCount);
     map.put("count", map3);
     
     flowNames.put("count", "小计");
   }
   result.put("data", data);
   result.put("flowName", flowNames);
   return result;
 }
 public Map getCount(Connection conn , String flowId ,int userId ) throws Exception {
   Map map = new HashMap();
   int workCount = this.getCount(conn, flowId, userId, false);
   int timeOutCount = this.getCount(conn, flowId, userId, true);
   map.put("workCount", workCount);
   map.put("timeOutCount", timeOutCount);
   return map;
 }
 public int getCount (Connection conn , String flowId , int userId , boolean isTimeOut) throws Exception {
   int result = 0 ;
   String query = "select count(distinct(flow_run.run_ID)) as count  from   flow_run , flow_run_prcs where flow_run.RUN_ID = flow_run_prcs.RUN_ID "  
      + " and flow_run.flow_id = " + flowId
      + " and flow_run_prcs.user_id = " + userId; 
   if (isTimeOut) {
     query += " and flow_run_prcs.time_out_flag='1'"; 
   }
   Statement stmt = null;
   ResultSet rs = null;// 结果集
   try {
     stmt = conn.createStatement();
     rs = stmt.executeQuery(query);
     if(rs.next()){
       result = rs.getInt("count");
     }
   }catch(Exception ex){
     throw ex;
   }finally {
     T9DBUtility.close(stmt, rs, null);
   }
   return result ;
 }
 public String getOverTimeTotalSql(String flowquery, String bumenquery, String users,String dept,String role,Connection conn, T9Person user) throws Exception {
   String personBm="";
 //属于那个部门   获得各个部门的名称
   if(!"".equals(bumenquery)&& bumenquery.equals("user")){
     personBm=  users;
   }else if(!"".equals(bumenquery)&& bumenquery.equals("dept")){
     personBm = dept;
   }else if(!"".equals(bumenquery)&& bumenquery.equals("role")){
     personBm = role;
   }
   // 获得查询语句    取得查询每个人每个流程，工作总个数的SQL // D表中的flow_id 现在已改成 seq_id and A.RUN_ID=B.RUN_ID
  // A.RUN_ID=B.RUN_ID and  与OA 相差一个条件   完成后在加上这个条件， 现在加上没有数据，所以先去了这个条件
   String sqlquery =  "select D.flow_name, A.prcs_time, A.del_flag,C.prcs_id ,B.flow_id, D.seq_id, E.SEQ_ID as USER_ID,E.user_name,"
      + "COUNT(*) JOB_NUMBER_TOTAL"
      + " from FLOW_RUN_PRCS A ,FLOW_PROCESS C ,FLOW_RUN B,FLOW_TYPE D,PERSON E"
      + " where A.FLOW_PRCS=C.PRCS_ID and B.FLOW_ID=C.FLOW_seq_id and A.RUN_ID=B.RUN_ID"
      + " and B.FLOW_ID=D.seq_id and A.USER_ID=E.seq_id";
   sqlquery = sqlquery + " and A.TOP_FLAG = '0' and A.OP_FLAG='1' ";
   //流程类型      
   if(!"".equals(flowquery)&& flowquery!=null){
     sqlquery += " and B.FLOW_ID ='"+ flowquery +"'"; // 传的与数据库不一致 数据库是int类型  flowquery 是String的
   }
   //从页面中  获取逗号分开的人名 (就是按部门或角色，人员分类)即：user_id 用于where条件，形如：('admin','dq') 
   String getFenlei = getqueryBumen(personBm,bumenquery,conn);
   if(!"".equals(getFenlei)&& getFenlei !=null){   //测试由于 加上 各个部门，角色，指定人员 测试数据 很难有 所有先关掉，正是用在加上
     sqlquery +=" and A.USER_ID in("+ getFenlei +")"; 
   }
   return sqlquery ;
 }
 public ArrayList<T9DbRecord> covertToExportCsvData(Map map) {
   ArrayList<T9DbRecord> db  = new ArrayList<T9DbRecord>();
   Map flowName = (Map)map.get("flowName");
   Map data = (Map)map.get("data");
   Set<String> flow = flowName.keySet();
   Set<String> userIds = data.keySet();
   for (String userId : userIds) {
     T9DbRecord rd = new T9DbRecord();
     Map userData = (Map)data.get(userId);
     String userName =(String) userData.get("userName");
     rd.addField("姓名", userName);
     for (String flowId : flow) { 
       if (!"count".equals(flowId)) {
         Map flowDataTmp =(Map) userData.get(flowId);
         String flowNameStr = (String)flowName.get(flowId);
         String value = "0(0)";
         if (flowDataTmp != null) {
           int workCount = (Integer)flowDataTmp.get("workCount");
           int timeOutCount = (Integer)flowDataTmp.get("timeOutCount");
           value = timeOutCount  + "(" + workCount + ")";
         }
         rd.addField(flowNameStr, value);
       }
     }
     Map countTmp = (Map) userData.get("count");
     String value = "0(0)";
     if (countTmp != null) {
       int workCount = (Integer)countTmp.get("workCount");
       int timeOutCount = (Integer)countTmp.get("timeOutCount");
       value = timeOutCount  + "(" + workCount + ")";
     }
     rd.addField("小计", value);
     db.add(rd);
   }
   return db;
 }
}