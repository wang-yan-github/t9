package t9.core.esb.server.taskstatus.act;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.server.task.T9EsbServerTasksMgr;
import t9.core.esb.server.taskstatus.logic.T9TaskStatusLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
public class T9TaskStatusAct {
  public String getTask(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9EsbServerTasksMgr mgr = new T9EsbServerTasksMgr();
      Map<String, T9TaskInfo> map = mgr.getUploadTasks();
      int uploadCount = map.size();
      
      T9TaskStatusLogic logic = new T9TaskStatusLogic();
      String userCode = request.getParameter("userCode");
      String guid = request.getParameter("guid");
      String ss = logic.taskInfoToJson(dbConn, map, userCode , guid);
      //String result = logic.statisticsUpload(dbConn, request);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, uploadCount  + "");
      request.setAttribute(T9ActionKeys.RET_DATA, ss);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getTask2(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9EsbServerTasksMgr mgr = new T9EsbServerTasksMgr();
      Map<String, Map<Integer, T9TaskInfo>> down = mgr.getDownloadTasks();
      int downCount = 0 ;
      for (String ss : down.keySet()) {
        if (down.get(ss) != null) {
          downCount += down.get(ss).size();
        }
      }
      
      T9TaskStatusLogic logic = new T9TaskStatusLogic();
      String userCode = request.getParameter("userCode");
      String sendUserCode = request.getParameter("sendUserCode");
      String guid = request.getParameter("guid");
      
      String ss = logic.downTaskInfoToJson(dbConn, down , userCode , sendUserCode , guid);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, downCount  + "");
      request.setAttribute(T9ActionKeys.RET_DATA, ss);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String statisticsUpload(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9TaskStatusLogic logic =new T9TaskStatusLogic();
      String result = logic.statisticsUpload(dbConn, request);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, result);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String statisticsDown(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9TaskStatusLogic logic =new T9TaskStatusLogic();
      String result = logic.statisticsDown(dbConn, request);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, result);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getUploadPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    String queryType = request.getParameter("queryType");
    String startTime =  T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime =  T9Utility.null2Empty(request.getParameter("endTime"));
    String startNo = T9Utility.null2Empty(request.getParameter("startNo"));
    String endNo = T9Utility.null2Empty(request.getParameter("endNo"));
    String guid = T9Utility.null2Empty(request.getParameter("guid"));
    String uplodType = T9Utility.null2Empty(request.getParameter("uplodType"));
    String userCode = T9Utility.null2Empty(request.getParameter("userCode"));
    String message = T9Utility.null2Empty(request.getParameter("message"));
    
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      String sql = "select" +
          " t.SEQ_ID," +
          " t.GUID," +
          " t.FROM_ID," +
          " t.FILE_PATH," +
          " t.TO_ID," +
          " t.STATUS," +
          " t.FAILED_MESSAGE," +
          " t.CREATE_TIME," +
          " t.COMPLETE_TIME" +
          ", t.MESSAGE " + 
          //" , 1 " + 
          " from ESB_TRANSFER t" +
          " where TYPE = '0' ";
      Date date  = new Date();
      if (T9Utility.isNullorEmpty(queryType) || "0".equals(queryType)) {
         String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
         startTime =   dateStr +   " 00:00:00";
         endTime =  dateStr + " 23:59:59";
         //确定日期时间
      } else if ("1".equals(queryType))  {
        //三天
      } else if ("2".equals(queryType)) {
        long time = date.getTime();
        long time2 = time - 24 * 60 * 60 * 1000 * 3;
        Date d = new Date(time2);
        String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
        startTime = dateStr  +   " 00:00:00";
        //一周时间
      } else if ("3".equals(queryType)) {
        long time = date.getTime();
        int day = date.getDay();
        if (day == 0 ) {
          day = 7;
        }
        long time2 = time - 24 * 60 * 60 * 1000 * (day - 1);
        Date d = new Date(time2);
        String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
        startTime = dateStr  +   " 00:00:00";
        //一月
      } else if ("4".equals(queryType)) {
        String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
        int last = dateStr.lastIndexOf("-");
        dateStr = dateStr.substring(0 , last);
        startTime = dateStr  +   "-01 00:00:00";
      }
      if (!T9Utility.isNullorEmpty(startTime)) {
        String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
        sql += " and " + dbDateF1;
      }
      if (!T9Utility.isNullorEmpty(endTime)) {
        String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
        sql += " and " + dbDateF2;
      }
      
      if (!T9Utility.isNullorEmpty(startNo) && T9Utility.isInteger(startNo)) {
        sql += " and t.SEQ_ID >= " + startNo ;
      }
      if (!T9Utility.isNullorEmpty(endNo) && T9Utility.isInteger(endNo)) {
        sql += " and t.SEQ_ID <= " + endNo ;
      }
      if (!T9Utility.isNullorEmpty(guid)) {
        sql += " and t.GUID = '" + guid + "'" ;
      }
      if (!T9Utility.isNullorEmpty(uplodType)) {
        sql += " and t.STATUS = '" + uplodType + "'" ;
      }
      if (!T9Utility.isNullorEmpty(userCode)) {
        int fromId = this.getSeqIdByUserCode(dbConn, userCode);
        sql += " and t.FROM_ID = '" + fromId + "'" ;
      }
      if (!T9Utility.isNullorEmpty(message)) {
        sql += " and t.MESSAGE like '%" + T9DBUtility.escapeLike(message) + "%' "+T9DBUtility.escapeLike();
      }
      sql += " order by t.SEQ_ID desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      sql);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        /*
        Date d1 = (Date)record.getValueByName("createTime");
        Date d2 = (Date)record.getValueByName("completetime");
        long dtime1 = 0 ;
        long dtime2 = 0 ;
        long timeUsed = 0;
        String ss = "";
        if (d1 != null) {
          dtime1 = d1.getTime();
          if (d2 != null) {
            dtime2 = d2.getTime();
            timeUsed = (dtime2 - dtime1);
            
            long day = timeUsed/(24*60*60*1000); 
            long hour = (timeUsed/(60*60*1000)-day*24); 
            long min = ((timeUsed/(60*1000))-day*24*60-hour*60); 
            long s = (timeUsed/1000-day*24*60*60-hour*60*60-min*60);
            if(day > 0){
              ss = day + "天";
            }
            if(hour>0){
              ss +=hour + "时";
            }
            if(min>0){
              ss +=min + "分";
            }
            if(s>0){
              ss +=s + "秒";
            }
            if (T9Utility.isNullorEmpty(ss)) {
              ss =  "0秒";
            }
          }
          
        } 
        record.updateField("time", ss);*/
      }
      PrintWriter pw = response.getWriter(); 
      pw.println(pageDataList.toJson()); 
      pw.flush(); 
  
      return null; 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    } 
  }
  
  public String getDownloadPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    String queryType = request.getParameter("queryType");
    String startTime = T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime = T9Utility.null2Empty(request.getParameter("endTime"));
    String startNo = T9Utility.null2Empty(request.getParameter("startNo"));
    String endNo = T9Utility.null2Empty(request.getParameter("endNo"));
    String guid = T9Utility.null2Empty(request.getParameter("guid"));
    String downType = T9Utility.null2Empty(request.getParameter("downType"));
    String userCode = T9Utility.null2Empty(request.getParameter("userCode"));
    String recUserCode = T9Utility.null2Empty(request.getParameter("recUserCode"));
    String message = T9Utility.null2Empty(request.getParameter("message"));
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      String sql = "select" +
          " t.SEQ_ID," +
          " t.GUID," +
          " t.FROM_ID," +
      		" t.FILE_PATH," +
      		" s.TO_ID," +
      		" s.STATUS," +
      		" s.FAILED_MESSAGE," +
      		" s.CREATE_TIME," +
      		" s.COMPLETE_TIME" +
      		 ", t.MESSAGE " + 
      	//	" , 1 " + 
      		" from ESB_TRANSFER_STATUS s" +
      		" join ESB_TRANSFER t on s.TRANS_ID = t.GUID" +
      		" where TYPE = '0'" ;
      
      Date date  = new Date();
      if (T9Utility.isNullorEmpty(queryType) || "0".equals(queryType)) {
         
         String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
         startTime =   dateStr +   " 00:00:00";
         endTime =  dateStr + " 23:59:59";
         
         String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
         sql += " and (" + dbDateF1 + " or t.CREATE_TIME IS NULL) ";
         String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
         sql += " and (" + dbDateF2 + "  or t.CREATE_TIME IS NULL) ";
         //确定日期时间
      } else {
        if ("1".equals(queryType))  {
          
          //三天
        } else if ("2".equals(queryType)) {
          long time = date.getTime();
          long time2 = time - 24 * 60 * 60 * 1000 * 3;
          Date d = new Date(time2);
          String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
          startTime = dateStr  +   " 00:00:00";
          //一周时间
        } else if ("3".equals(queryType)) {
          long time = date.getTime();
          int day = date.getDay();
          if (day == 0 ) {
            day = 7;
          }
          long time2 = time - 24 * 60 * 60 * 1000 * (day - 1);
          Date d = new Date(time2);
          String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
          startTime = dateStr  +   " 00:00:00";
          //一月
        } else if ("4".equals(queryType)) {
          String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
          int last = dateStr.lastIndexOf("-");
          dateStr = dateStr.substring(0 , last);
          startTime = dateStr  +   "-01 00:00:00";
        }
        if (!T9Utility.isNullorEmpty(startTime)) {
          String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
          sql += " and " + dbDateF1;
        }
        if (!T9Utility.isNullorEmpty(endTime)) {
          String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
          sql += " and " + dbDateF2;
        }
      }
      
      if (!T9Utility.isNullorEmpty(startNo) && T9Utility.isInteger(startNo)) {
        sql += " and t.SEQ_ID >= " + startNo ;
      }
      if (!T9Utility.isNullorEmpty(endNo) && T9Utility.isInteger(endNo)) {
        sql += " and t.SEQ_ID <= " + endNo ;
      }
      if (!T9Utility.isNullorEmpty(guid)) {
        sql += " and t.GUID = '" + guid + "'" ;
      }
      if (!T9Utility.isNullorEmpty(downType)) {
        sql += " and s.STATUS = '" + downType + "'" ;
      }
      if (!T9Utility.isNullorEmpty(userCode)) {
        int fromId = this.getSeqIdByUserCode(dbConn, userCode);
        sql += " and t.FROM_ID = '" + fromId + "'" ;
      }
      if (!T9Utility.isNullorEmpty(recUserCode)) {
        int toId = this.getSeqIdByUserCode(dbConn, recUserCode);
        sql += " and s.TO_ID = '" + toId + "'" ;
      }
      if (!T9Utility.isNullorEmpty(message)) {
        sql += " and t.MESSAGE like '%" + T9DBUtility.escapeLike(message) + "%' "+T9DBUtility.escapeLike();
      }
      
      sql += " order by t.SEQ_ID desc ";
      
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      sql);
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        /*
        T9DbRecord record = pageDataList.getRecord(i);
        Date d1 = (Date)record.getValueByName("createTime");
        Date d2 = (Date)record.getValueByName("completetime");
        long dtime1 = 0 ;
        long dtime2 = 0 ;
        long timeUsed = 0;
        String ss = "";
        if (d1 != null) {
          dtime1 = d1.getTime();
          if (d2 != null) {
            dtime2 = d2.getTime();
            timeUsed = (dtime2 - dtime1);
            
            long day = timeUsed/(24*60*60*1000); 
            long hour = (timeUsed/(60*60*1000)-day*24); 
            long min = ((timeUsed/(60*1000))-day*24*60-hour*60); 
            long s = (timeUsed/1000-day*24*60*60-hour*60*60-min*60);
            if(day > 0){
              ss = day + "天";
            }
            if(hour>0){
              ss +=hour + "时";
            }
            if(min>0){
              ss +=min + "分";
            }
            if(s>0){
              ss +=s + "秒";
            }
            if (T9Utility.isNullorEmpty(ss)) {
              ss =  "0秒";
            }
          }
          
        } 
        record.updateField("time", ss);*/
      }
      PrintWriter pw = response.getWriter(); 
      pw.println(pageDataList.toJson()); 
      pw.flush(); 
  
      return null; 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    } 
  }
  public String getUserNameById(Connection conn , String userIds) throws Exception {
    if (T9Utility.isNullorEmpty(userIds)) {
      return "";
    }
    if (userIds.endsWith(",")) {
      userIds = userIds.substring(0 , userIds.length() - 1);
    }
    String sql = "select USER_NAME from td_user where SEQ_ID IN  (" + userIds + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String userName = "";
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        userName += rs.getString("USER_NAME") + ",";
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    if (userName.endsWith(",")) {
      userName = userName.substring(0 , userName.length() - 1);
    }
    return userName ;
  }
  public int getSeqIdByUserCode(Connection conn , String userCode) throws Exception {
    String sql = " select SEQ_ID from td_user where USER_CODE  = '" + userCode + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getInt("SEQ_ID");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return 0 ;
  }
  public String expDown(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    String queryType = request.getParameter("queryType");
    String startTime = T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime = T9Utility.null2Empty(request.getParameter("endTime"));
    String startNo = T9Utility.null2Empty(request.getParameter("startNo"));
    String endNo = T9Utility.null2Empty(request.getParameter("endNo"));
    String guid = T9Utility.null2Empty(request.getParameter("guid"));
    String downType = T9Utility.null2Empty(request.getParameter("downType"));
    String userCode = T9Utility.null2Empty(request.getParameter("userCode"));
    String recUserCode = T9Utility.null2Empty(request.getParameter("recUserCode"));
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      String sql = "select" +
          " t.SEQ_ID," +
          " t.GUID," +
          " t.FROM_ID," +
          " t.FILE_PATH," +
          " s.TO_ID," +
          " s.STATUS," +
          " s.FAILED_MESSAGE," +
          " s.CREATE_TIME," +
          " s.COMPLETE_TIME" +
          " , 1 " + 
          " from ESB_TRANSFER_STATUS s" +
          " join ESB_TRANSFER t on s.TRANS_ID = t.GUID" +
          " where TYPE = '0'" ;
      
      Date date  = new Date();
      if (T9Utility.isNullorEmpty(queryType) || "0".equals(queryType)) {
         
         String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
         startTime =   dateStr +   " 00:00:00";
         endTime =  dateStr + " 23:59:59";
         
         String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
         sql += " and (" + dbDateF1 + " or t.CREATE_TIME IS NULL) ";
         String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
         sql += " and (" + dbDateF2 + "  or t.CREATE_TIME IS NULL) ";
         //确定日期时间
      } else {
        if ("1".equals(queryType))  {
          
          //三天
        } else if ("2".equals(queryType)) {
          long time = date.getTime();
          long time2 = time - 24 * 60 * 60 * 1000 * 3;
          Date d = new Date(time2);
          String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
          startTime = dateStr  +   " 00:00:00";
          //一周时间
        } else if ("3".equals(queryType)) {
          long time = date.getTime();
          int day = date.getDay();
          if (day == 0 ) {
            day = 7;
          }
          long time2 = time - 24 * 60 * 60 * 1000 * (day - 1);
          Date d = new Date(time2);
          String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
          startTime = dateStr  +   " 00:00:00";
          //一月
        } else if ("4".equals(queryType)) {
          String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
          int last = dateStr.lastIndexOf("-");
          dateStr = dateStr.substring(0 , last);
          startTime = dateStr  +   "-01 00:00:00";
        }
        if (!T9Utility.isNullorEmpty(startTime)) {
          String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
          sql += " and " + dbDateF1;
        }
        if (!T9Utility.isNullorEmpty(endTime)) {
          String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
          sql += " and " + dbDateF2;
        }
      }
      
      if (!T9Utility.isNullorEmpty(startNo) && T9Utility.isInteger(startNo)) {
        sql += " and t.SEQ_ID >= " + startNo ;
      }
      if (!T9Utility.isNullorEmpty(endNo) && T9Utility.isInteger(endNo)) {
        sql += " and t.SEQ_ID <= " + endNo ;
      }
      if (!T9Utility.isNullorEmpty(guid)) {
        sql += " and t.GUID = '" + guid + "'" ;
      }
      if (!T9Utility.isNullorEmpty(downType)) {
        sql += " and s.STATUS = '" + downType + "'" ;
      }
      if (!T9Utility.isNullorEmpty(userCode)) {
        int fromId = this.getSeqIdByUserCode(dbConn, userCode);
        sql += " and t.FROM_ID = '" + fromId + "'" ;
      }
      if (!T9Utility.isNullorEmpty(recUserCode)) {
        int toId = this.getSeqIdByUserCode(dbConn, recUserCode);
        sql += " and s.TO_ID = '" + toId + "'" ;
      }
      sql += " order by t.SEQ_ID desc ";
      
      ArrayList<T9DbRecord> dbL = this.toExportData(dbConn, sql , false);
      String fileName = URLEncoder.encode("下载.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9CSVUtil.CVSWrite(response.getWriter(), dbL); 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return null; 
  }
  public String expUpload(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 
    response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
    String queryType = request.getParameter("queryType");
    String startTime =  T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime =  T9Utility.null2Empty(request.getParameter("endTime"));
    String startNo = T9Utility.null2Empty(request.getParameter("startNo"));
    String endNo = T9Utility.null2Empty(request.getParameter("endNo"));
    String guid = T9Utility.null2Empty(request.getParameter("guid"));
    String uplodType = T9Utility.null2Empty(request.getParameter("uplodType"));
    String userCode = T9Utility.null2Empty(request.getParameter("userCode"));
    
    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      String sql = "select" +
          " t.SEQ_ID," +
          " t.GUID," +
          " t.FROM_ID," +
          " t.FILE_PATH," +
          " t.TO_ID," +
          " t.STATUS," +
          " t.FAILED_MESSAGE," +
          " t.CREATE_TIME," +
          " t.COMPLETE_TIME" +
          " , 1 " + 
          " from ESB_TRANSFER t" +
          " where TYPE = '0' ";
      Date date  = new Date();
      if (T9Utility.isNullorEmpty(queryType) || "0".equals(queryType)) {
         
         String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
         startTime =   dateStr +   " 00:00:00";
         endTime =  dateStr + " 23:59:59";
         //确定日期时间
      } else if ("1".equals(queryType))  {
         
        //三天
      } else if ("2".equals(queryType)) {
        long time = date.getTime();
        long time2 = time - 24 * 60 * 60 * 1000 * 3;
        Date d = new Date(time2);
        String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
        startTime = dateStr  +   " 00:00:00";
        //一周时间
      } else if ("3".equals(queryType)) {
        long time = date.getTime();
        int day = date.getDay();
        if (day == 0 ) {
          day = 7;
        }
        long time2 = time - 24 * 60 * 60 * 1000 * (day - 1);
        Date d = new Date(time2);
        String dateStr = T9Utility.getDateTimeStr(d).split(" ")[0];
        startTime = dateStr  +   " 00:00:00";
        //一月
      } else if ("4".equals(queryType)) {
        String dateStr = T9Utility.getDateTimeStr(date).split(" ")[0];
        int last = dateStr.lastIndexOf("-");
        dateStr = dateStr.substring(0 , last);
        startTime = dateStr  +   "-01 00:00:00";
      }
      if (!T9Utility.isNullorEmpty(startTime)) {
        String dbDateF1 = T9DBUtility.getDateFilter("t.CREATE_TIME", startTime, " >= ");
        sql += " and " + dbDateF1;
      }
      if (!T9Utility.isNullorEmpty(endTime)) {
        String dbDateF2 = T9DBUtility.getDateFilter("t.CREATE_TIME", endTime, " <= ");
        sql += " and " + dbDateF2;
      }
      
      if (!T9Utility.isNullorEmpty(startNo) && T9Utility.isInteger(startNo)) {
        sql += " and t.SEQ_ID >= " + startNo ;
      }
      if (!T9Utility.isNullorEmpty(endNo) && T9Utility.isInteger(endNo)) {
        sql += " and t.SEQ_ID <= " + endNo ;
      }
      if (!T9Utility.isNullorEmpty(guid)) {
        sql += " and t.GUID = '" + guid + "'" ;
      }
      if (!T9Utility.isNullorEmpty(uplodType)) {
        sql += " and t.STATUS = '" + uplodType + "'" ;
      }
      if (!T9Utility.isNullorEmpty(userCode)) {
        int fromId = this.getSeqIdByUserCode(dbConn, userCode);
        sql += " and t.FROM_ID = '" + fromId + "'" ;
      }
      
      sql += " order by t.SEQ_ID desc ";
      ArrayList<T9DbRecord> dbL = this.toExportData(dbConn, sql , true);
      String fileName = URLEncoder.encode("上传.csv","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      T9CSVUtil.CVSWrite(response.getWriter(), dbL); 
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return null; 
  }
  public ArrayList<T9DbRecord> toExportData(Connection conn,String sql , boolean isUpload) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
    try {
      stmt = conn.createStatement();
    rs = stmt.executeQuery(sql);
    while (rs.next()) {
      T9DbRecord record = new T9DbRecord();
      int seqId = rs.getInt("SEQ_ID");
      Date d1 = rs.getTimestamp("CREATE_TIME");
      Date d2 = rs.getTimestamp("COMPLETE_TIME");
      
      long dtime1 = 0 ;
      long dtime2 = 0 ;
      String ss = "";
      if (d1 != null) {
        dtime1 = d1.getTime();
        if (d2 != null) {
          dtime2 = d2.getTime();
          ss = ((dtime2 - dtime1) / 1000) + "";
        }
      } 
      int fromIds = rs.getInt("FROM_ID");
      String filePath = rs.getString("FILE_PATH");
      String status = rs.getString("STATUS");
      String failedMessage = rs.getString("FAILED_MESSAGE");
      
      record.addField("SEQ_ID", seqId + "");
      record.addField("guid", rs.getString("GUID"));
      record.addField("发送方", this.getUserNameById(conn, String.valueOf(fromIds)));
      record.addField("文件",filePath );
      
      if (isUpload) {
        String toIds = rs.getString("TO_ID");
        String statusString = "";
        if ("4".equals(status)) {
          statusString = "上传失败";
        }else if ("3".equals(status)) {
          statusString = "接收完毕";
        }else if ("2".equals(status)) {
          statusString = "上传完毕";
        }else if ("1".equals(status)) {
          statusString = "上传中";
        }
        
        record.addField("接收方", this.getUserNameById(conn, String.valueOf(toIds)));
        record.addField("状态", statusString);
      } else {
        int toIds = rs.getInt("TO_ID");
        
        String statusString = "";
        if ("0".equals(status)) {
          statusString = "等待接收";
        }else if ("4".equals(status)) {
          statusString = "接收失败";
        }else if ("2".equals(status)) {
          statusString = "接收完毕";
        }else if ("1".equals(status)) {
          statusString = "接收中";
        }
        
        record.addField("接收方", this.getUserNameById(conn, String.valueOf(toIds)));
        record.addField("状态", statusString);
      }
      
      if (isUpload) {
        record.addField("发送时间",T9Utility.getDateTimeStr(d1));
      } else {
        record.addField("接收时间",T9Utility.getDateTimeStr(d1));
      }
      record.addField("完成时间",T9Utility.getDateTimeStr(d2));
      record.addField("用时",ss);
      record.addField("失败原因", failedMessage);
      result.add(record);
    }
  } catch (Exception ex) {
    throw ex;
  } finally {
    T9DBUtility.close(stmt, rs,null);
  }
  return result;
}
}
