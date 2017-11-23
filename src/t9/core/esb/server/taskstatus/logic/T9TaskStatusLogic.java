package t9.core.esb.server.taskstatus.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
public class T9TaskStatusLogic {
  public String taskInfoToJson(Connection conn , Map<String, T9TaskInfo> map , String queryUserCode, String queryGuid ) throws Exception {
    StringBuffer sb = new StringBuffer();
    Set<String> guids = map.keySet();
    sb.append("[");
    int count = 0 ;
    for (String guid : guids) {
      T9TaskInfo info = map.get(guid);
      if (info == null)
        continue;
      String userCode = this.getUserCode(conn, info.getFromId());
      if (!T9Utility.isNullorEmpty(queryUserCode) 
          && !queryUserCode.equals(userCode)) {
        continue;
      }
      if (!T9Utility.isNullorEmpty(queryGuid) 
          && !queryGuid.equals(guid)) {
        continue;
      }
      count++;
      sb.append("{guid:\"").append(guid).append("\"");
      sb.append(",userCode:\"").append(T9Utility.encodeSpecial(userCode)).append("\"");
      sb.append(",filePath:\"").append(T9Utility.encodeSpecial(info.getFile().getAbsolutePath())).append("\"");
      long fileLength = info.getFileLength();
      sb.append(",uploadScale:\"").append(this.getScale(info.hasDone(), fileLength , true)).append("\"},");
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String downTaskInfoToJson(Connection conn , Map<String , Map<Integer, T9TaskInfo>> map, String userCode2, String sendUserCode, String queryGuid  ) throws Exception {
    StringBuffer sb = new StringBuffer();
    Set<String> guids = map.keySet();
    sb.append("[");
    int count = 0 ;
    for (String guid : guids) {
      Map<Integer, T9TaskInfo> infoMap = map.get(guid);
      if (infoMap == null)
        continue;
      
      if (!T9Utility.isNullorEmpty(queryGuid) 
          && !queryGuid.equals(guid)) {
        continue;
      }
      Set<Integer> userIds = infoMap.keySet();
      String fromUser = "";
      for (Integer toId : userIds) {
        T9TaskInfo info = infoMap.get(toId);
        if (info == null)
          continue;
        fromUser = info.getFromCode();
      }
      if (!T9Utility.isNullorEmpty(sendUserCode) 
          && !sendUserCode.equals(fromUser)) {
        continue;
      }
      
      count++;
      sb.append("{guid:\"").append(guid).append("\"");
      
      String path = "";
      
      sb.append(",users:[");
      int count2  = 0;
      for (Integer toId : userIds) {
        T9TaskInfo info = infoMap.get(toId);
        if (info == null)
          continue;
        
        if (info.getFile() != null) {
          path = info.getFile().getAbsolutePath();
        }
        long fileLength = info.getFileLength();
        String userCode = this.getUserCode(conn,  toId);
        
        String c = info.getContent();
        long cInt  = 0 ;
        if (!T9Utility.isNullorEmpty(c)
            && T9Utility.isInteger(c)) {
          cInt = Long.parseLong(c);
        }
        count2++;
        sb.append("{downScale:\"").append(this.getScale(cInt, fileLength) ).append("\"");
        sb.append(",userCode:\"").append(T9Utility.encodeSpecial(userCode)).append("\"},");
      }
      if (count2 > 0 ) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      sb.append(",fromUserCode:\"").append(T9Utility.encodeSpecial(fromUser)).append("\"");
      sb.append(",filePath:\"").append(T9Utility.encodeSpecial(path)).append("\"},");
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
public String getScale (long hasUpload , long fileLength ) {
    if (fileLength != 0 && hasUpload > fileLength) {
      hasUpload = fileLength;
    }
    String hasDownStr = "";
    String fileSizeStr = "";
    
    String p = "";
    if (fileLength > 1024) {
      fileSizeStr = (fileLength / 1024) + "KB";
      hasDownStr = (hasUpload / 1024) + "KB";
    } else {
      fileSizeStr = fileLength  + "b";
      hasDownStr = hasUpload + "b";
    }
    if (fileLength == 0 ) {
      if (hasUpload > 1024) {
        hasDownStr = (hasUpload / 1024) + "KB";
      }else {
        hasDownStr = hasUpload + "b";
      }
      String tip = "已下载：";
      return "已上传：" + hasDownStr; 
    }
    p = hasDownStr + "/" + fileSizeStr;
    return p;
  }
  public String getScale (String hasUploads , long fileLength , boolean isUpload) {
    
    long size = PropertiesUtil.getUploadPartSize();
    if (!isUpload) {
      size = PropertiesUtil.getDownloadPartSize();
    }
    int has = hasUploads.split(",").length;
    long hasUpload = has * size;
    if (fileLength != 0 && hasUpload > fileLength) {
      hasUpload = fileLength;
    }
    String hasDownStr = "";
    String fileSizeStr = "";
    
    String p = "";
    if (fileLength > 1024) {
      fileSizeStr = (fileLength / 1024) + "KB";
      hasDownStr = (hasUpload / 1024) + "KB";
    } else {
      fileSizeStr = fileLength  + "b";
      hasDownStr = hasUpload + "b";
    }
    if (fileLength == 0 ) {
      if (hasUpload > 1024) {
        hasDownStr = (hasUpload / 1024) + "KB";
      }else {
        hasDownStr = hasUpload + "b";
      }
      String tip = "已上传：";
      if (!isUpload) {
        tip = "已下载：";
      }
      return "已上传：" + hasDownStr; 
    }
    p = hasDownStr + "/" + fileSizeStr;
    return p;
  }
  public String getSql(HttpServletRequest request , int fromId) throws Exception {
    String queryType = request.getParameter("queryType");
    String startTime =  T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime =  T9Utility.null2Empty(request.getParameter("endTime"));
    String uplodType = T9Utility.null2Empty(request.getParameter("uplodType"));
    String userCode = T9Utility.null2Empty(request.getParameter("userCode"));
    String sql = "select" +
        " count(*)," +
        " sum(file_length) as count2," +
        " t.STATUS" +
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
    sql += " and t.FROM_ID = '" + fromId + "'" ;
    if (!T9Utility.isNullorEmpty(uplodType)) {
      sql += " and t.STATUS = '" + uplodType + "'" ;
    } else {
      sql += " group by t.STATUS";
    }
    return sql;
  }
  public String getSql2(HttpServletRequest request , int fromId , String sizeType) throws Exception {
    String queryType = request.getParameter("queryType");
    String startTime =  T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime =  T9Utility.null2Empty(request.getParameter("endTime"));
    String sql = "select" +
        " count(*)," +
        " sum(file_length) as count2" +
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
    sql += " and t.FROM_ID = '" + fromId + "'" ;
    int s = 1024 * 1024 ;
    int s2 = 2 * s ;
    int s3 = 3 * s ;
    int s4 = 4 * s ;
    int s5 = 5 * s ;
    if ("1".equals(sizeType)) {
      sql += " and FILE_LENGTH <= " + s2;
    } else if ("2".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s2 ;
    }
    /*
    if ("1".equals(sizeType)) {
      sql += " and FILE_LENGTH <= " + s2;
    } else if ("2".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s2 + " and FILE_LENGTH <= " + s3;
    }else if ("3".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s3 + " and FILE_LENGTH <= " + s4;
    }else if ("4".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s4 + " and FILE_LENGTH <= " + s5;
    }else if ("5".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s5;
    }*/
    return sql;
  }
  public String getSqlDown(HttpServletRequest request , int toId) throws Exception {
    String queryType = request.getParameter("queryType");
    String startTime = T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime = T9Utility.null2Empty(request.getParameter("endTime"));
    String downType = T9Utility.null2Empty(request.getParameter("downType"));
    
    String sql = "select" +
        " count(*)," +
        " sum(file_length) as count2," +
        " s.STATUS" +
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
    
    if (!T9Utility.isNullorEmpty(downType)) {
      sql += " and s.STATUS = '" + downType + "'" ;
    }
    sql += " and s.TO_ID = '" + toId + "'" ;
    
    if (!T9Utility.isNullorEmpty(downType)) {
      sql += " and s.STATUS = '" + downType + "'" ;
    } else {
      sql += " group by s.STATUS";
    }
    return sql;
  }
  public String getSqlDown2(HttpServletRequest request , int toId , String sizeType) throws Exception {
    String queryType = request.getParameter("queryType");
    String startTime = T9Utility.null2Empty(request.getParameter("startTime"));
    String endTime = T9Utility.null2Empty(request.getParameter("endTime"));
    String downType = T9Utility.null2Empty(request.getParameter("downType"));
    
    String sql = "select" +
        " count(*)," +
        " sum(file_length) as count2" +
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
    int s = 1024 * 1024 ;
    int s2 = 2 * s ;
    int s3 = 3 * s ;
    int s4 = 4 * s ;
    int s5 = 5 * s ;
    if ("1".equals(sizeType)) {
      sql += " and FILE_LENGTH <= " + s2;
    } else if ("2".equals(sizeType)) {
      sql += " and FILE_LENGTH >= " + s2 ;
    }
    
    /*
    if ("1".equals(sizeType)) {
      sql += " and FILE_LENGTH <= " + s2;
    } else if ("2".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s2 + " and FILE_LENGTH <= " + s3;
    }else if ("3".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s3 + " and FILE_LENGTH <= " + s4;
    }else if ("4".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s4 + " and FILE_LENGTH <= " + s5;
    }else if ("5".equals(sizeType)) {
      sql += " and FILE_LENGTH > " + s5;
    }*/
    sql += " and s.TO_ID = '" + toId + "'" ;
    return sql;
  }
  public  String getInStr(String str) {
    if (str == null || "".equals(str)) {
      return "";
    }
    String[] strs = str.split(",");
    String newStr = "";
    for (String tmp : strs) {
      
      if (tmp != null && !"".equals(tmp)) {
        if (tmp.startsWith("'") && tmp.endsWith("'")) {
          newStr += "" + tmp + ",";
        } else {
          newStr += "'" + tmp + "',";
        }
      } 
    }
    newStr = getOutOfTail(newStr , ",");
    return newStr;
  }
  
  /**
   * 去掉最后一个逗号
   * @param str
   * @return
   */
  public  String getOutOfTail(String str , String split) {
    if (str == null) {
      return str ;
    }
    if (str.endsWith(split) ) {
      str = str.substring(0, str.length() - split.length());
    }
    return str;
  }
  public List<Map> getTdUser(Connection conn  , String userCode) throws Exception {
    String sql = "select * from td_user ";
    if(!T9Utility.isNullorEmpty(userCode) ) {
      userCode = this.getInStr(userCode);
      sql += " where USER_CODE IN (" + userCode + ")";
    }
    List<Map> list = new ArrayList();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        String userName = rs.getString("USER_NAME");
        String userCode1 = rs.getString("USER_CODE");
        Map map = new HashMap();
        map.put("SEQ_ID", seqId);
        map.put("USER_NAME", userName);
        map.put("USER_CODE", userCode1);
        list.add(map);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return list;
  }
  public String getUserCode(Connection conn  , int seqId) throws Exception {
    String sql = "select USER_CODE from td_user where SEQ_ID =" + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String userCode1 = rs.getString("USER_CODE");
        return userCode1;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return "";
  }
  public void statistics(Map map  , Connection conn , String sql) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    int count = 0;
    int count2 = 0 ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        int c = rs.getInt(1);
        int c2 = rs.getInt(2);
        count2 += c2;
        count += c;
        String status = rs.getString("STATUS");
        map.put(status, c);
        map.put(status + "-size", c2);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    map.put("count", count);
    map.put("count2", count2);
  }
  public void statistics2(Map map  , Connection conn , String sql , String type) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    int count = 0;
    if (map.get("count") != null) {
      count = (Integer) map.get("count") ;
    }
    int count2 = 0 ;
    if (map.get("count2") != null) {
      count2 = (Integer) map.get("count2") ;
    }
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        int c = rs.getInt(1);
        int c2 = rs.getInt(2);
        count2 += c2;
        count += c;
        map.put(type, c);
        map.put(type + "-size", c2);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    map.put("count", count);
    map.put("count2", count2);
  }
  public String statisticsUpload(Connection conn , HttpServletRequest request) throws Exception {
    String userCode = request.getParameter("userCode");
    String type = request.getParameter("type");
    
    List<Map> list = this.getTdUser(conn, userCode);
    String sizeType  = request.getParameter("sizeType");
    for (Map map : list) {
      int fromId = (Integer)map.get("SEQ_ID");
      String sql =  "";
      if ("1".equals(type)) {
         sql = this.getSql(request,fromId );
         this.statistics(map, conn, sql );
      } else {
        if ("".equals(sizeType)) {
          String sql1= this.getSql2(request,fromId ,"1");
          this.statistics2(map, conn, sql1 , "1");
          
          String sql2 = this.getSql2(request,fromId ,"2");
          this.statistics2(map, conn, sql2 ,"2" );
          
          /*
          String sql3 = this.getSql2(request,fromId ,"3");
          this.statistics2(map, conn, sql3 , "3");
          
          String sql4 = this.getSql2(request,fromId ,"4");
          this.statistics2(map, conn, sql4 , "4"  );
          
          String sql5 = this.getSql2(request,fromId ,"5");
          this.statistics2(map, conn, sql5 , "5"  );
          */
        } else {
          sql = this.getSql2(request,fromId , sizeType );
          this.statistics2(map, conn, sql , sizeType);
        }
      }
    }
    return this.mapToJson(conn, list); 
  }
  public String mapToJson(Connection conn  , List<Map> list) {
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (Map map : list) {
      sb.append("{");
      sb.append("seqId:").append(map.get("SEQ_ID"));
      sb.append(",userName:\"").append(T9Utility.encodeSpecial((String)map.get("USER_NAME"))).append("\"");
      sb.append(",userCode:\"").append(T9Utility.encodeSpecial((String)map.get("USER_CODE"))).append("\"");
      Integer status1 = (Integer)map.get("1");
      if (status1 == null) {
        status1 = 0 ;
      }
      Integer status3 = (Integer)map.get("3");
      if (status3 == null) {
        status3 = 0 ;
      }
      Integer status0 = (Integer)map.get("0");
      if (status0 == null) {
        status0 = 0 ;
      }
      Integer status4 = (Integer)map.get("4");
      if (status4 == null) {
        status4 = 0 ;
      }
      Integer status2= (Integer)map.get("2");
      if (status2 == null) {
        status2 = 0 ;
      }
      Integer status5= (Integer)map.get("5");
      if (status5 == null) {
        status5 = 0 ;
      }
      
      Integer status21 = (Integer)map.get("1-size");
      if (status21 == null) {
        status21 = 0 ;
      }
      Integer status23 = (Integer)map.get("3-size");
      if (status23 == null) {
        status23 = 0 ;
      }
      Integer status20 = (Integer)map.get("0-size");
      if (status20 == null) {
        status20 = 0 ;
      }
      Integer status24 = (Integer)map.get("4-size");
      if (status24 == null) {
        status24 = 0 ;
      }
      Integer status22= (Integer)map.get("2-size");
      if (status22 == null) {
        status22 = 0 ;
      }
      Integer status25= (Integer)map.get("5-size");
      if (status25 == null) {
        status25 = 0 ;
      }
      
      sb.append(",STATUS0:\"").append(status0).append("\"");
      sb.append(",STATUS1:\"").append(status1).append("\"");
      sb.append(",STATUS2:\"").append(status2).append("\"");
      sb.append(",STATUS3:\"").append(status3).append("\"");
      sb.append(",STATUS4:\"").append(status4).append("\"");
      sb.append(",STATUS5:\"").append(status5).append("\"");
      
      sb.append(",STATUS20:\"").append(status20).append("\"");
      sb.append(",STATUS21:\"").append(status21).append("\"");
      sb.append(",STATUS22:\"").append(status22).append("\"");
      sb.append(",STATUS23:\"").append(status23).append("\"");
      sb.append(",STATUS24:\"").append(status24).append("\"");
      sb.append(",STATUS25:\"").append(status25).append("\"");
      sb.append(",count:").append(map.get("count"));
      sb.append(",count2:").append(map.get("count2"));
      sb.append("},");
    }
    if (list.size() > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String statisticsDown(Connection conn, HttpServletRequest request) throws Exception {
    // TODO Auto-generated method stub
    String userCode = request.getParameter("recUserCode");
    String type = request.getParameter("type");
    String sizeType  = request.getParameter("sizeType");
    
    List<Map> list = this.getTdUser(conn, userCode);
    for (Map map : list) {
      int toId = (Integer)map.get("SEQ_ID");
      
      String sql =  "";
      if ("1".equals(type)) {
         sql = this.getSqlDown(request,toId );
        this.statistics(map, conn, sql);
      } else {
        if ("".equals(sizeType)) {
          String sql1= this.getSqlDown2(request,toId ,"1");
          this.statistics2(map, conn, sql1 , "1");
          
          String sql2 = this.getSqlDown2(request,toId ,"2");
          this.statistics2(map, conn, sql2 ,"2" );
          
          /*
          String sql3 = this.getSqlDown2(request,toId ,"3");
          this.statistics2(map, conn, sql3 , "3");
          
          String sql4 = this.getSqlDown2(request,toId ,"4");
          this.statistics2(map, conn, sql4 , "4"  );
          
          String sql5 = this.getSqlDown2(request,toId ,"5");
          this.statistics2(map, conn, sql5 , "5"  );*/
        } else {
          sql = this.getSqlDown2(request,toId , sizeType );
          this.statistics2(map, conn, sql , sizeType);
        }
      }
    }
    return this.mapToJson(conn, list);
  }
}
