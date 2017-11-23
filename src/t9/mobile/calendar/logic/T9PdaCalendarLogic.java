package t9.mobile.calendar.logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletResponse;

import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaCalendarLogic {

	/**
	 * 获取日程
	 * @param dbConn
	 * @param sql
	 * @param sql2 
	 * @return
	 * @throws Exception
	 */
	public String getCalendar(Connection dbConn,String sql, String sql2) throws Exception{
	    try {
	      StringBuffer data = new StringBuffer("[");
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      boolean flag = false;
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	          data.append("{\"q_id\":"+rs.getInt("SEQ_ID")+","
	                    + "\"cal_time\":\""+T9MobileString.formateDateTimeToTime(T9Utility.getDateTimeStr(rs.getTimestamp("cal_time")))+"\","//T9Utility.getDateTimeStr(rs.getTimestamp("cal_time"))
	                    + "\"end_time\":\""+T9MobileString.formateDateTimeToTime(T9Utility.getDateTimeStr(rs.getTimestamp("end_time")))+"\","//T9Utility.getDateTimeStr(rs.getTimestamp("end_time"))+"\","
	                    + "\"cal_level_desc\":\""+changeLevel(rs.getString("cal_level"))+"\","//changeLevel(rs.getString("cal_level"))
	                    + "\"cal_level_color\":\""+""+"\","
	                    + "\"cal_type\":\""+rs.getString("cal_type")+"\","//那个是写死了的T9MobileUtility.get_code_name(dbConn, rs.getString("cal_type"), "CAL_TYPE")+"\","
	                    + "\"content\":\""+rs.getString("content")+"\"},");
	          flag = true;
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      
	      java.util.Date d = new java.util.Date();
        try {
          ps = dbConn.prepareStatement(sql2);
          rs = ps.executeQuery();
          while (rs.next()) {
              String type = T9Utility.null2Empty(rs.getString("TYPE"));
              Timestamp date = rs.getTimestamp("REMIND_DATE");
              int f =0 ;
              if ("2".equals(type)) {
                f = 1;
              } else if ("3".equals(type) && date != null && d.getDay() == date.getDay() ) {
                f = 1;
              }else if ("4".equals(type) && date != null && d.getDate() == date.getDate()) {
                f = 1;
              }else if ("5".equals(type) && date != null && d.getMonth() == date.getMonth() &&  d.getDate() == date.getDate()) {
                f = 1;
              }
              if (f !=1) continue;
                
            data.append("{\"q_id\":\"\","
                      + "\"cal_time\":\""+T9Utility.getDateTimeStr(rs.getTimestamp("REMIND_TIME"))+"\","//T9Utility.getDateTimeStr(rs.getTimestamp("cal_time"))
                      + "\"end_time\":\"\","//T9Utility.getDateTimeStr(rs.getTimestamp("end_time"))+"\","
                      + "\"cal_level_desc\":\"\","//changeLevel(rs.getString("cal_level"))
                      + "\"cal_level_color\":\""+""+"\","
                      + "\"cal_type\":\"\","//那个是写死了的T9MobileUtility.get_code_name(dbConn, rs.getString("cal_type"), "CAL_TYPE")+"\","
                      + "\"content\":\""+rs.getString("CONTENT")+"\"},");
            flag = true;
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          T9DBUtility.close(ps, rs, null);
        }
        if(flag){
          data = data.deleteCharAt(data.length() - 1);
        }
	      data.append("]");
	      return data.toString();
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	
	public void addCalendar(HttpServletResponse response,Connection dbConn,T9Calendar tc,Timestamp t2,Timestamp t1) throws SQLException{
	      StringBuffer sql = new StringBuffer();
	      PreparedStatement ps = null;
	      sql.append("insert into Calendar (USER_ID,CAL_TIME,END_TIME,CAL_TYPE,CAL_LEVEL,CONTENT,OVER_STATUS)values(?,?,?,?,?,?,?)");
	      try {
	        ps = dbConn.prepareStatement(sql.toString());
	        ps.setString(1,tc.getUserId() );
	        ps.setTimestamp(2,t2);
	        ps.setTimestamp(3,t1);
	        ps.setString(4, tc.getCalType());
	        ps.setString(5, tc.getCalLevel());
	        ps.setString(6, tc.getContent());
	        ps.setString(7, tc.getOverStatus());
	        ps.execute();
	        dbConn.commit();
	        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "添加成功", null));
	      } catch (Exception e) {
	    	  dbConn.rollback();
	    	  e.printStackTrace();
	      } finally {
	    	  
	        T9DBUtility.close(ps, null, null);
	      }
	 }
	/**
	 * 更新 操作
	 * @param response 
	 * @param dbConn
	 * @param tc
	 * @param s_time
	 * @param e_time
	 * @throws SQLException
	 */
	public void updateCalendar(HttpServletResponse response, Connection dbConn,T9Calendar tc,Timestamp t2,Timestamp t1) throws SQLException{
	      StringBuffer sql = new StringBuffer();
	      PreparedStatement ps = null;
	      sql.append("update Calendar set CAL_TIME = ?,END_TIME =?,CAL_TYPE = ?,CAL_LEVEL = ?,CONTENT = ? where SEQ_ID = ?");
	      try {
	        ps = dbConn.prepareStatement(sql.toString());
	        ps.setTimestamp(1,t2);
	        ps.setTimestamp(2,t1);
	        ps.setString(3, tc.getCalType());
	        ps.setString(4, tc.getCalLevel());
	        ps.setString(5, tc.getContent());
	        ps.setInt(6, tc.getSeqId());
	        ps.executeUpdate();
	        dbConn.commit();
	        T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "修改成功", null));
	      } catch (Exception e) {
	    	  dbConn.rollback();
	    	  e.printStackTrace();
	      } finally {
	    	  
	        T9DBUtility.close(ps, null, null);
	      }
	 }
	
	private String changeLevel(String cal_level){
		if(T9MobileString.isEmpty(cal_level)){
			return "未指定";
		}
		if("1".equals(cal_level)){
			return "重要/紧急";
		}
		if("2".equals(cal_level)){
			return "重要/不紧急";
		}
		if("3".equals(cal_level)){
			return "不重要/紧急";
		}
		if("4".equals(cal_level)){
			return "不重要/不紧急";
		}
		return "未指定";
	}
}


