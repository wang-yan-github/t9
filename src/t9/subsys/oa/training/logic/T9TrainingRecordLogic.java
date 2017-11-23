package t9.subsys.oa.training.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import t9.subsys.oa.training.data.T9HrTrainingRecord;

public class T9TrainingRecordLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.training.logic.T9TrainingRecordLogic.java");

	/**
	 * 新建培训记录--cc
	 * @param dbConn
	 * @param record
	 * @throws Exception
	 */
  public void add(Connection dbConn, T9HrTrainingRecord record) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, record);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 是否存在记录--cc
   * @param dbConn
   * @param staffUserId
   * @param tPlanNo
   * @return
   * @throws Exception
   */
  public boolean existsTrainingRecord(Connection dbConn, String staffUserId, String tPlanNo)
  throws Exception {
    long count = 0;
    String sql = "SELECT count(*) FROM HR_TRAINING_RECORD WHERE STAFF_USER_ID = '" + staffUserId + "' and T_PLAN_NO= '" + tPlanNo + "'";
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count >= 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  /**
   * 培训记录信息 列表--cc
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getTrainingRecordListJson(Connection dbConn, Map request, T9Person person) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select " 
        + "  SEQ_ID" 
        + ", T_PLAN_NAME" 
        + ", STAFF_USER_ID" 
        + ", TRAINNING_COST" 
        + ", T_INSTITUTION_NAME" 
        + " from HR_TRAINING_RECORD where 1=1 ORDER BY SEQ_ID desc";
    }else{
      sql = "select " 
        + "  SEQ_ID" 
        + ", T_PLAN_NAME" 
        + ", STAFF_USER_ID" 
        + ", TRAINNING_COST" 
        + ", T_INSTITUTION_NAME" 
        + " from HR_TRAINING_RECORD where CREATE_USER_ID = '"+person.getSeqId()+"' ORDER BY SEQ_ID desc";
    }

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
  
  /**
   * 删除一条记录--cc
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9HrTrainingRecord.class, seqId);
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
    String sql = "DELETE FROM HR_TRAINING_RECORD WHERE SEQ_ID IN (" + seqIdStr + ")";
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
  
  /**
   * 获取培训记录详细信息--cc
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9HrTrainingRecord getRecordDetail(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9HrTrainingRecord) orm.loadObjSingle(conn, T9HrTrainingRecord.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  
  /**
   * 获取培训记录管理列表--cc
   * @param dbConn
   * @param request
   * @param person
   * @param userId
   * @param tPlanNo
   * @param tInstitutionName
   * @param trainingCost
   * @return
   * @throws Exception
   */
  public String getTrainingRecordSearchList(Connection dbConn, Map request, T9Person person, String userId, String tPlanNo, String tInstitutionName,
      String trainningCost) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select " 
        + "  SEQ_ID" 
        + ", T_PLAN_NAME" 
        + ", STAFF_USER_ID" 
        + ", TRAINNING_COST" 
        + ", T_INSTITUTION_NAME" 
        + " from HR_TRAINING_RECORD where 1=1";
    }else{
      sql = "select " 
        + "  SEQ_ID" 
        + ", T_PLAN_NAME" 
        + ", STAFF_USER_ID" 
        + ", TRAINNING_COST" 
        + ", T_INSTITUTION_NAME" 
        + " from HR_TRAINING_RECORD where CREATE_USER_ID = '" + person.getSeqId() + "'";
    }
   
    if (!T9Utility.isNullorEmpty(tPlanNo)) {
      sql = sql + " and T_PLAN_NO = '" + tPlanNo + "'";
    }
    if (!T9Utility.isNullorEmpty(userId)) {
      sql = sql + " and STAFF_USER_ID = '" + userId + "'";
    }

    if (!T9Utility.isNullorEmpty(tInstitutionName)) {
      sql = sql + " and T_INSTITUTION_NAME = '" + tInstitutionName + "'";
    }

    if (!T9Utility.isNullorEmpty(trainningCost)) {
      sql = sql + " and TRAINNING_COST = '" + Double.parseDouble(trainningCost) + "'";
    }
    sql = sql + " ORDER BY SEQ_ID desc";

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    

    return pageDataList.toJson();
  }
  
  /**
   * 编辑培训记录--cc
   * @param conn
   * @param record
   * @throws Exception
   */
  public void updateRecord(Connection conn, T9HrTrainingRecord record)
    throws Exception {
  try {
        T9ORM orm = new T9ORM();
        orm.updateSingle(conn, record);
      } catch (Exception ex) {
        throw ex;
      } finally {
    }
  }

}
