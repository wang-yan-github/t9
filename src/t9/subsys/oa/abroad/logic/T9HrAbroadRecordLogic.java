package t9.subsys.oa.abroad.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.abroad.data.T9HrAbroadRecord;

public class T9HrAbroadRecordLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.abroad.logic.T9HrAbroadRecordLogic.java");
  
  public void add(Connection dbConn, T9HrAbroadRecord record) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, record);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  public String getAbroadRecordListJson(Connection dbConn, Map request, T9Person person) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select " 
        + "  SEQ_ID" 
        + ", ABROAD_USER_ID" 
        + ", ABROAD_NAME" 
        + ", BEGIN_DATE" 
        + ", END_DATE" 
        + " from HR_ABROAD_RECORD where 1=1 ORDER BY SEQ_ID desc";
    }else{
      sql = "select " 
        + "  SEQ_ID" 
        + ", ABROAD_USER_ID" 
        + ", ABROAD_NAME" 
        + ", BEGIN_DATE" 
        + ", END_DATE" 
        + " from HR_ABROAD_RECORD where CREATE_USER_ID = '"+person.getSeqId()+"' ORDER BY SEQ_ID desc";
    }

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }

  public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID IN (" + userIdStr + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String toId = rs.getString(1);
        if (!"".equals(result)) {
          result += ",";
        }
        result += toId;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 删除一条记录--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9HrAbroadRecord.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
  
  /**
   * 批量删除--cc
   * @param conn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteAll(Connection conn, String seqIdStr) throws Exception {
    String sql = "DELETE FROM HR_ABROAD_RECORD WHERE SEQ_ID IN (" + seqIdStr + ")";
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
  
  public T9HrAbroadRecord getRecordDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9HrAbroadRecord) orm.loadObjSingle(conn, T9HrAbroadRecord.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  public void updateRecord(Connection conn, T9HrAbroadRecord record) throws Exception {
  try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, record);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }
  
  /**
   * 出国记录查询  --wyw
   * @param dbConn
   * @param request
   * @param map
   * @param person
   * @return
   * @throws Exception
   */
	public String queryAbroadRecordListJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
		String abroadUserId = (String) map.get("abroadUserId");
		String abroadName = (String) map.get("abroadName");
		String beginDate = (String) map.get("beginDate");
		String beginDate1 = (String) map.get("beginDate1");
		String endDate = (String) map.get("endDate");
		String endDate1 = (String) map.get("endDate1");
		String remark = (String) map.get("remark");
		String conditionStr = "";
		String sql = "";
		try {
			if (!T9Utility.isNullorEmpty(abroadUserId)) {
				conditionStr = " and ABROAD_USER_ID ='" + T9DBUtility.escapeLike(abroadUserId) + "'";
			}
			if (!T9Utility.isNullorEmpty(abroadName)) {
				conditionStr += " and ABROAD_NAME ='" + T9DBUtility.escapeLike(abroadName) + "'";
			}
			if(!T9Utility.isNullorEmpty(beginDate)){ 
				 conditionStr += " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", beginDate, ">=");
			} 
			if(!T9Utility.isNullorEmpty(beginDate1)){ 
				 conditionStr += " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", beginDate1, "<=");
			}
			if(!T9Utility.isNullorEmpty(endDate)){ 
				conditionStr += " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", endDate, ">=");
			} 
			if(!T9Utility.isNullorEmpty(endDate1)){ 
				conditionStr += " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", endDate1, "<=");
			}
			if (!T9Utility.isNullorEmpty(remark)) {
				conditionStr += " and REMARK like '%" + T9DBUtility.escapeLike(remark) + "%'" + T9DBUtility.escapeLike();
			}
			
		  if(person.isAdminRole()){
	      sql = "select " 
	        + "  SEQ_ID" 
	        + ", ABROAD_USER_ID" 
	        + ", ABROAD_NAME" 
	        + ", BEGIN_DATE" 
	        + ", END_DATE" 
	        + " from HR_ABROAD_RECORD where 1=1 " + conditionStr + " ORDER BY SEQ_ID desc";
	    }else{
	      sql = "select " 
	        + "  SEQ_ID" 
	        + ", ABROAD_USER_ID" 
	        + ", ABROAD_NAME" 
	        + ", BEGIN_DATE" 
	        + ", END_DATE" 
	        + " from HR_ABROAD_RECORD where CREATE_USER_ID = '" + person.getSeqId()+ "'" + conditionStr + " ORDER BY SEQ_ID desc";
	    }
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
   * 返回两个日期的相隔月份
   * @param startDate
   * @param endDate
   * @return
   * @throws Exception
   */
  public List<String> getDateValue(String startDateStr, String endDateStr) throws Exception {
    List<String> list = new ArrayList<String>();
    if (T9Utility.isNullorEmpty(endDateStr) && !T9Utility.isNullorEmpty(startDateStr)) {
      endDateStr = startDateStr;
      list.add(startDateStr);
      list.add(endDateStr);
      return list;
    } else if (T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
      startDateStr = endDateStr;
      list.add(startDateStr);
      list.add(endDateStr);
      return list;
    }
    try {
      if (!T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
        String startDateArry[] = startDateStr.split("-");
        String endDateArry[] = endDateStr.split("-");
        int startYear = Integer.parseInt(startDateArry[0]);
        int startMonth = Integer.parseInt(startDateArry[1]);
        int endMonth = Integer.parseInt(endDateArry[1]);
        String result = "";
        if (startMonth < endMonth) {
          list.add(startDateStr);
          int tmp = endMonth - startMonth;
          if (tmp <= 11) {
            for (int i = 1; i < tmp; i++) {
              int tmpMonth = startMonth + i;
              String str = "";
              if (tmpMonth < 10) {
                str = "0";
              }
              result = startYear + "-" + str + tmpMonth;
              list.add(result);
            }
          }
          list.add(endDateStr);
        } else if (startMonth == endMonth) {
          list.add(startDateStr);
          list.add(endDateStr);

        } else if (startMonth > endMonth) {
        }
      }
    } catch (Exception e) {
      throw e;
    }
    return list;
  }
  
  /**
   * 获取开始日期和结束日期之间的所有日期串
   * @param dbConn
   * @param beginTime
   * @param endTime
   * @return
   * @throws Exception
   */
  public String getDayList(Connection dbConn, String beginTime,String endTime) throws Exception {
    //相隔多少天
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
    long daySpace = T9Utility.getDaySpan(dateFormat1.parse(beginTime),dateFormat1.parse(endTime))+1;
    //得到到之间的天数数组
    List daysList = new ArrayList();
    String days = "";
    Calendar calendar = new GregorianCalendar();
    for(int i = 0;i<daySpace;i++){
      calendar.setTime(dateFormat1.parse(beginTime));
      calendar.add(Calendar.DATE,+i) ;
      Date dateTemp = calendar.getTime();
      String dateTempStr = dateFormat1.format(dateTemp);
      daysList.add(dateTempStr);
      days = days + dateTempStr + ",";
    }
    if(daySpace>0){
      days = days.substring(0,days.length()-1);
    }
    return days;
  }
}
