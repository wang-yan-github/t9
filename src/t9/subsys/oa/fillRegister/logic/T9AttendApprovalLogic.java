package t9.subsys.oa.fillRegister.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.data.T9AttendFill;

public class T9AttendApprovalLogic {

  private static Logger log = Logger.getLogger("t9.subsys.oa.fillRigister.logic.T9AttendApprovalLogic.java");
  
  /**
   * 获取补登记审批列表
   * @param dbConn
   * @param request
   * @param assessingStatus
   * @param person
   * @return
   * @throws Exception
   */
  public String getRegisterApprovalListJson(Connection dbConn, Map request, String assessingStatus, T9Person person) throws Exception {
    String sql = "";
//    if(person.isAdminRole()){
      sql = "select " 
        + "  SEQ_ID" 
        + ", ASSESSING_STATUS" 
        + ", PROPOSER" 
        + ", REGISTER_TYPE" 
        + ", FILL_TIME"
        + ", ASSESSING_OFFICER"
        + " from ATTEND_FILL where  ASSESSING_STATUS = '" + assessingStatus + "' ORDER BY FILL_TIME, REGISTER_TYPE asc";
//    }else{
//      sql = "select " 
//        + "  SEQ_ID" 
//        + ", ASSESSING_STATUS" 
//        + ", PROPOSER" 
//        + ", REGISTER_TYPE" 
//        + ", FILL_TIME"
//        + ", ASSESSING_OFFICER"
//        + " from ATTEND_FILL where (PROPOSER = '" + person.getSeqId() + "' or ASSESSING_OFFICER = '" + person.getSeqId() + "') and ASSESSING_STATUS = '" + assessingStatus + "' ORDER BY FILL_TIME, REGISTER_TYPE asc";
//    }
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
  
  public String getRegisterApprovalPassJson(Connection dbConn, Map request, String assessingStatus, T9Person person, String begin, String end) throws Exception {
    String sql = "";
      sql = "select " 
        + "  SEQ_ID" 
        + ", ASSESSING_STATUS" 
        + ", PROPOSER" 
        + ", REGISTER_TYPE" 
        + ", FILL_TIME"
        + ", ASSESSING_OFFICER"
        + " from ATTEND_FILL where  ASSESSING_STATUS = '" + assessingStatus + "' and " + T9DBUtility.getDateFilter("FILL_TIME", end+ " 23:59:59", "<=") + " and "+T9DBUtility.getDateFilter("FILL_TIME", begin+ " 00:00:00", ">=") +"ORDER BY FILL_TIME, REGISTER_TYPE asc";

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
  /**
   * 取得用户名称--cc
   * 
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */

  public String getUserNameLogic(Connection conn, int userId) throws Exception {
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID = " + userId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  public static void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
  throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    sb.setSendDate(sendDate);
    T9SmsUtil.smsBack(conn, sb);
  }
  
  public T9AttendFill getPlanDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9AttendFill) orm.loadObjSingle(conn, T9AttendFill.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 处长主观分
   * @param conn
   * @param year
   * @param month
   * @param userId
   * @return
   * @throws Exception
   */
  public String getDirectorScoreStr(Connection conn, String year, String month, String userId) throws Exception {
    String result = "";
    String ymd = "";
    if(year == null){
      ymd = year+"-"+month+"-"+"07";
   }else{
      ymd = year+"-"+month+"-"+"07";
   }
    String sql = " select SCORE from SCORE_DATA where PARTICIPANT='"+userId+"' and "+ T9DBUtility.getMonthFilter("RANK_DATE", T9Utility.parseDate(ymd));
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result += toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  public int getDirectorScore(Connection conn, String year, String month, String userId) throws Exception {
    String data = getDirectorScoreStr(conn, year, month, userId);
    int result = 0;
 
    try {
      String dataStr[] = data.split(",");
      for(int i = 0; i < dataStr.length; i++){
        if(!T9Utility.isNullorEmpty(dataStr[i])){
          int val = Integer.parseInt(dataStr[i]);
          result = result + val;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      //T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 某月份对映的考核分数

   * @param conn
   * @param year
   * @param month
   * @param userId
   * @return
   * @throws Exception
   */
  public String getAttendScore(Connection conn, String year, String month, String userId) throws Exception {
    String result = "";
    String ymd = year + "-" + month + "-" + "01";
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance(); 
    calendar.setTime(dateFormat1.parse(ymd)); 
    int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
    double totalScore = 0;
    for(int i = 0;i < maxDay; i++){
      calendar.setTime(dateFormat1.parse(ymd));
      calendar.add(Calendar.DATE,+i) ;
      Date dateTemp = calendar.getTime();
      String dateTempStr = dateFormat1.format(dateTemp);
      String beginDate = dateTempStr + " 00:00:00";
      String endDate = dateTempStr + " 23:59:59";
      double reduceScore1 = getAttendScoreStr(conn, beginDate,endDate, userId, "1");  //扣分
      double reduceScore2 = getAttendScoreStr(conn, beginDate,endDate, userId, "2");
      double reduceScore3 = getAttendScoreStr(conn, beginDate,endDate, userId, "3");
      double reduceScore4 = getAttendScoreStr(conn, beginDate,endDate, userId, "4");
      double reduceScore5 = getAttendScoreStr(conn, beginDate,endDate, userId, "5");
      double reduceScore6 = getAttendScoreStr(conn, beginDate,endDate, userId, "6");
      //System.out.println(reduceScore);
      double type1 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "1"));     //回冲分
      double type2 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "2"));
      double type3 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "3"));
      double type4 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "4"));
      double type5 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "5"));
      double type6 = Double.parseDouble(getAttendFillStr(conn, beginDate,endDate, userId, "6"));
      double num = reduceScore1*type1 + reduceScore2*type2 + reduceScore3*type3 + reduceScore4*type4 + reduceScore5*type5 + reduceScore6*type6;
      if(num > Double.parseDouble(getMaxScore(conn))){
        num = Double.parseDouble(getMaxScore(conn));
      }      
      
      
      totalScore += num;
    }
    return result;
  }
  
  /**
   * 出差统计天数－－进行自动补登记
   * @param conn
   * @param year
   * @param month
   * @param userId
   * @return
   * @throws Exception
   */
  public String getAttendEvection(Connection conn, String year, String month, String userId) throws Exception {
    String result = "";
    String ymd = "";
    if(year == null){
      ymd = year+"-"+month+"-"+"07";
   }else{
      ymd = year+"-"+month+"-"+"07";
   }
    String sql = " select EVECTION_DATE1, EVECTION_DATE2 from ATTEND_EVECTION where ALLOW ='1' and STATUS = '1' and PARTICIPANT='"+userId+"' and "+ T9DBUtility.getMonthFilter("EVECTION_DATE1", T9Utility.parseDate(ymd));
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        Date beginDate = rs.getDate(1);
        Date endDate = rs.getDate(2);
        String toId = "";
        if (toId != null) {
          result += toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 获取扣的分数
   * @param conn
   * @param year
   * @param userId
   * @return
   * @throws Exception
   */
  public double getAttendScoreStr(Connection conn, String beginDate, String endDate, String userId, String registerType) throws Exception {
     double result = 0;
     double num = 0;
     String sql = " select SCORE from ATTEND_SCORE where USER_ID = '" + userId 
                 + "' and " + T9DBUtility.getDateFilter("CREATE_TIME", beginDate, ">=") 
                 + " and " +  T9DBUtility.getDateFilter("CREATE_TIME", endDate, "<=") 
                 + " and REGISTER_TYPE = '" + registerType + "'";
     PreparedStatement ps = null;
     ResultSet rs = null;
     try {
       ps = conn.prepareStatement(sql);
       rs = ps.executeQuery();
       while (rs.next()) {
         String toId = rs.getString(1);
         if (!T9Utility.isNullorEmpty(toId)) {
           result += Double.parseDouble(toId);
           if(result > Double.parseDouble(getMaxScore(conn))){
             result = Double.parseDouble(getMaxScore(conn));
             break;
           }
         }else{
           result += Double.parseDouble(getMaxScore(conn));
         }
       }
     } catch (Exception e) {
       throw e;
     } finally {
       T9DBUtility.close(ps, rs, log);
     }
     return result;
   }
 
 /**
  * 获取补登记状态
  * @param conn
  * @param year
  * @param userId
  * @return
  * @throws Exception
  */
   public String getAttendFillStr(Connection conn, String beginDate, String endDate, String userId, String registerType) throws Exception {
     String result = "1";
     String sql = " select ATTEND_FLAG from ATTEND_FILL where PROPOSER ='" + userId 
                 + "' and " + T9DBUtility.getDateFilter("FILL_TIME", beginDate, ">=") 
                 + " and " +  T9DBUtility.getDateFilter("FILL_TIME", endDate, "<=") 
                 + " and REGISTER_TYPE = '" + registerType + "'";
     PreparedStatement ps = null;
     ResultSet rs = null;
     try {
       ps = conn.prepareStatement(sql);
       rs = ps.executeQuery();
       while (rs.next()) {
         String toId = rs.getString(1);
         if(toId != null){
           result = toId;
         }
       }
     } catch (Exception e) {
       throw e;
     } finally {
       T9DBUtility.close(ps, rs, log);
     }
     return result;
   }
 
 /**
  * 获取最大扣除的分数
  * @param conn
  * @return
  * @throws Exception
  */
   public String getMaxScore(Connection conn) throws Exception {
     String result = "0";
     String sql = " select MAX(SCORE) from ATTEND_TIME";
     PreparedStatement ps = null;
     ResultSet rs = null;
     try {
       ps = conn.prepareStatement(sql);
       rs = ps.executeQuery();
       if (rs.next()) {
         String toId = rs.getString(1);
         if (toId != null) {
           result = toId;
         }
       }
     } catch (Exception e) {
       throw e;
     } finally {
       T9DBUtility.close(ps, rs, log);
     }
     return result;
   }
   /**
 	 * 补登记(审批)查询
 	 * @param dbConn
 	 * @param request
 	 * @param map
 	 * @param person
 	 * @return
 	 * @throws Exception
 	 */
 	public String queryApprovalListJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
 		String assessingOfficer = (String) map.get("assessingOfficer");
 		String assessingStatus = (String) map.get("assessingStatus");
 		String beginDate = (String) map.get("beginDate");
 		String endDate = (String) map.get("endDate");
 		String conditionStr = "";
 		String sql = "";
 		try {
 			if (!T9Utility.isNullorEmpty(assessingOfficer)) {
 				conditionStr = " and ASSESSING_OFFICER ='" + T9DBUtility.escapeLike(assessingOfficer) + "'";
 			}
 			if (!T9Utility.isNullorEmpty(assessingStatus)) {
 				conditionStr += " and ASSESSING_STATUS ='" + T9DBUtility.escapeLike(assessingStatus) + "'";
 			}
 			if (!T9Utility.isNullorEmpty(beginDate)) {
 				conditionStr += " and " + T9DBUtility.getDateFilter("ASSESSING_TIME", beginDate, ">=");
 			}
 			if (!T9Utility.isNullorEmpty(endDate)) {
 				conditionStr += " and " + T9DBUtility.getDateFilter("ASSESSING_TIME", endDate, "<=");
 			}
 			if(person.isAdminRole()){
 				sql = "select " 
 					+ "  SEQ_ID" 
 					+ ", PROPOSER" 
 					+ ", REGISTER_TYPE" 
 					+ ", FILL_TIME"
 					+ ", ASSESSING_OFFICER"
 					+ ", ASSESSING_TIME"
 					+ ", ASSESSING_STATUS" 
 					+ " from ATTEND_FILL where 1=1" + conditionStr + " ORDER BY SEQ_ID desc";
 			}else{
 				sql = "select " 
 					+ "  SEQ_ID" 
 					+ ", PROPOSER" 
 					+ ", REGISTER_TYPE" 
 					+ ", FILL_TIME"
 					+ ", ASSESSING_OFFICER"
 					+ ", ASSESSING_TIME"
 					+ ", ASSESSING_STATUS" 
 					+ " from ATTEND_FILL where PROPOSER = '" + person.getSeqId() + "'" + conditionStr + " ORDER BY SEQ_ID desc";
 			}
 			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
 			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
 			return pageDataList.toJson();
 		} catch (Exception e) {
 			throw e;
 		}
 	}
 	
 	public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9AttendFill.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
 	
 	public void deleteAll(Connection conn, String seqIdStr) throws Exception {
    String sql = "DELETE FROM ATTEND_FILL WHERE SEQ_ID IN(" + seqIdStr + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
}
