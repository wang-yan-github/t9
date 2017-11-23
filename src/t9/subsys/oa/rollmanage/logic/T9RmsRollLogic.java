package t9.subsys.oa.rollmanage.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsRoll;

public class T9RmsRollLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.rollmanage.logic.T9RmsRollLogic.java");

  public void add(Connection conn, T9RmsRoll rmsRollRoom) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, rmsRollRoom);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
  /**
   * 是否需要审批
   * 
   * @param conn
   * @param secret
   * @return
   * @throws Exception
   */
  public boolean isNeedApprove(Connection conn, int rollId , T9Person user) throws Exception {
    String sql = "select PRIV_USER,PRIV_DEPT,PRIV_ROLE from rms_roll where SEQ_ID=" + rollId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String privUser = T9Utility.null2Empty(rs.getString(1));
        String privDept = T9Utility.null2Empty( rs.getString(2));
        String privRole = T9Utility.null2Empty( rs.getString(3));
        
        if( "0".equals(privDept)
            || "ALL_DEPT".equals(privDept)
            || T9WorkFlowUtility.findId(privUser,String.valueOf(user.getSeqId())) 
            || T9WorkFlowUtility.findId(privDept,String.valueOf(user.getDeptId())) 
            || T9WorkFlowUtility.findId(privRole,user.getUserPriv())
            || !T9WorkFlowUtility.checkId(privRole , user.getUserPrivOther() ,true).equals("")
            || !T9WorkFlowUtility.checkId(privDept , user.getDeptIdOther() ,true).equals("")){
          return true;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return false;
  }
  public String getRmsRollJson(Connection dbConn, Map request, T9Person person, String seqId, String flag) throws Exception {
    String sql = "";
    if(person.isAdminRole() || "1".equals(flag)){
     sql = "select "
       + "  RMS_ROLL.SEQ_ID"
       + ", RMS_ROLL.ROLL_CODE"
       + ", RMS_ROLL.ROLL_NAME"
       + ", RMS_ROLL.ROOM_ID"
       + ", RMS_ROLL.CATEGORY_NO"
       + ", RMS_ROLL.CERTIFICATE_KIND"
       + ", RMS_ROLL.SECRET"
       + ", RMS_ROLL.STATUS"
       + " from RMS_ROLL left join RMS_ROLL_ROOM on RMS_ROLL.ROOM_ID = RMS_ROLL_ROOM.SEQ_ID where 1 = 1";
    }else{
     sql = "select "
       + " RMS_ROLL.SEQ_ID"
       + ", RMS_ROLL.ROLL_CODE"
       + ", RMS_ROLL.ROLL_NAME"
       + ", RMS_ROLL.ROOM_ID"
       + ", RMS_ROLL.CATEGORY_NO"
       + ", RMS_ROLL.CERTIFICATE_KIND"
       + ", RMS_ROLL.SECRET"
       + ", RMS_ROLL.STATUS"
       + " from RMS_ROLL left join RMS_ROLL_ROOM on RMS_ROLL.ROOM_ID = RMS_ROLL_ROOM.SEQ_ID where (RMS_ROLL.ADD_USER = '" + String.valueOf(person.getSeqId()) + "' or RMS_ROLL.MANAGER = '"  + String.valueOf(person.getSeqId()) + "')" ;
    }
    if(!T9Utility.isNullorEmpty(seqId)){
      sql = sql + " and RMS_ROLL_ROOM.SEQ_ID=" + Integer.parseInt(seqId);
    }
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }

  public T9RmsRoll getRmsRollDetail(Connection conn, int seqId)
      throws Exception {

    try {
      T9ORM orm = new T9ORM();
      return (T9RmsRoll) orm.loadObjSingle(conn, T9RmsRoll.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {

    }
  }
 
 /**
  * 编辑案卷
  * @param conn
  * @param rmsRoll
  * @throws Exception
  */
  public void updateRmsRoll(Connection conn, T9RmsRoll rmsRoll)
      throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, rmsRoll);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }

  /**
   * 删除案卷的同时编辑文件的rollId=0
   * 
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void updateRmsFile(Connection conn, int seqId) throws Exception {
    try {
      Map m = new HashMap();
      m.put("seqId", seqId);
      m.put("rollId", 0);
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, "rmsFile", m);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }
 
 /**
  * 删除一条卷库
  * @param conn
  * @param seqId
  * @throws Exception
  */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9RmsRoll.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }

  public void deleteAll(Connection conn, String loginUserId, T9Person person)
      throws Exception {
    String sql = "";
    if (person.isAdminRole()) {
      sql = "DELETE FROM RMS_ROLL_ROOM";
    } else {
      sql = "DELETE FROM RMS_ROLL_ROOM WHERE ADD_USER = '" + loginUserId + "'";
    }
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
 
  public void deleteAllRoll(Connection conn, String seqIdStr) throws Exception {
    String sql = "DELETE FROM RMS_ROLL WHERE SEQ_ID IN(" + seqIdStr + ")";
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
 
  public void updateAllRoll(Connection dbConn, String seqIdStr)
      throws Exception {
    String sql = "update RMS_FILE set ROLL_ID=0 WHERE ROLL_ID IN (" + seqIdStr + ")";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
 
  public String getRmsRollRoomNameLogic(Connection conn, int seqId)
      throws Exception {
    String result = "";
    String sql = " select ROOM_NAME from RMS_ROLL_ROOM where SEQ_ID = " + seqId;
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
  * 获取系统代码表中对映的字段描述
  * @param conn
  * @param classCode
  * @param classNo
  * @return
  * @throws Exception
  */
  public String getCodeNameLogic(Connection conn, String classCode,
      String classNo) throws Exception {
    String result = "";
    String sql = " select CLASS_DESC from CODE_ITEM where CLASS_CODE = '" + classCode + "' and CLASS_NO = '" + classNo + "'";
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
 
  public void deleteAll(Connection conn, String seqIds) throws Exception {
    String sql = "DELETE FROM ADDRESS WHERE SEQ_ID IN(" + seqIds + ")";
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
  * 查看文件
  * @param dbConn
  * @param request
  * @param person
  * @return
  * @throws Exception
  */
  public String getRmsFileJosn(Connection dbConn, Map request, T9Person person, int roomId) throws Exception {
    String sql="SELECT SEQ_ID," +
                    "ROLL_ID," +
                 		"FILE_CODE," +
                 		"FILE_TITLE," +
                 		"SECRET," +
                 		"SEND_UNIT," +
                 		"SEND_DATE," +
                 		"URGENCY from RMS_FILE where ROLL_ID=" + roomId + " and ( DEL_USER = '' or DEL_USER is null )";

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
 
  public void changeRmsRollSelect(Connection conn, String seqStr, int rollId)
      throws Exception {
    String sql = "update RMS_FILE SET ROLL_ID = " + rollId + " WHERE SEQ_ID IN(" + seqStr + ")";
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
 
 /**
  * 文件档案导出
  * @param conn
  * @return
  * @throws Exception
  */
 public ArrayList<T9DbRecord> toExportRmsFileData(Connection conn, String seqIdStr) throws Exception{
   ArrayList<T9DbRecord> result = new ArrayList<T9DbRecord>();
   String sql = "SELECT "
         + " FILE_CODE "
         + ",FILE_SUBJECT "
         + ",FILE_TITLE "
         + ",FILE_TITLEO "
         + ",SEND_UNIT"
         + ",SEND_DATE"
         + ",SECRET"
         + ",URGENCY"
         + ",FILE_TYPE"
         + ",FILE_KIND"
         + ",FILE_PAGE"
         + ",PRINT_PAGE"
         + ",REMARK"
         + " from RMS_FILE where SEQ_ID IN (" + seqIdStr + ") and (DEL_USER = '' or DEL_USER is null)";
   PreparedStatement ps = null;
   ResultSet rs = null;
   try {
     ps = conn.prepareStatement(sql);
     rs = ps.executeQuery() ;
     while (rs.next()) {
       T9DbRecord record = new T9DbRecord();
       String fileCode = rs.getString(1);
       String fileSubject = rs.getString(2);
       String fileTitle = rs.getString(3);
       String fileTitleo = rs.getString(4);
       String sendUnit = rs.getString(5);
       Date sendDate = rs.getTimestamp(6);
       String secret = rs.getString(7);
       String urgency = rs.getString(8);
       String fileType = rs.getString(9);
       String fileKind = rs.getString(10);
       String filePage = rs.getString(11);
       String printPage = rs.getString(12);
       String remark = rs.getString(13);
       
       record.addField("文件号", fileCode);
       record.addField("文件主题词", fileSubject);
       record.addField("文件标题",  fileTitle);
       record.addField("文件副标题", fileTitleo);
       record.addField("发文单位", sendUnit);
       record.addField("发文日期",T9Utility.getDateTimeStrCn(sendDate));
       record.addField("密级",getRmsSecret(conn, secret).toString());
       record.addField("紧急等级",getUrgency(conn, urgency).toString());
       record.addField("文件分类",getFileType(conn, fileType).toString());
       record.addField("公文类别",getFileKind(conn, fileKind).toString());
       record.addField("文件页数",filePage);
       record.addField("打印页数",printPage);
       record.addField("备注",remark);
       result.add(record);
     }
   } catch (Exception e) {
     throw e;
   } finally {
     T9DBUtility.close(ps, rs, null);
   }
   return result;
 }
 
 /**
  * 从系统代码表中获取密级
  * @param conn
  * @param secret
  * @return
  * @throws Exception
  */
  public String getRmsSecret(Connection conn, String secret) throws Exception {
    String result = "";
    String sql = "select CLASS_DESC from CODE_ITEM where CLASS_CODE='" + secret + "' and CLASS_NO = 'RMS_SECRET'";
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
 
  public String getFileKind(Connection conn, String fileKind) throws Exception {
    String result = "";
    String sql = "select CLASS_DESC from CODE_ITEM where CLASS_CODE='" + fileKind + "' and CLASS_NO = 'RMS_FILE_KIND'";
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
 
  public String getFileType(Connection conn, String fileType) throws Exception {
    String result = "";
    String sql = "select CLASS_DESC from CODE_ITEM where CLASS_CODE='" + fileType + "' and CLASS_NO = 'RMS_FILE_TYPE'";
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }

  public String getUrgency(Connection conn, String urgency) throws Exception {
    String result = "";
    String sql = "select CLASS_DESC from CODE_ITEM where CLASS_CODE='" + urgency + "' and CLASS_NO = 'RMS_URGENCY'";
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  public String getRmsRollSearchJson(Connection dbConn, Map request, T9Person person,
      String rollCode, String rollName, String roomId, String years, String beginDate0, String beginDate1, String endDate0, String endDate1, String secret,
      String deadline0, String deadline1, String categoryNo, String catalogNo, String archiveNo, String boxNo, String microNo, String certificateKind,
      String certificateStart0, String certificateStart1, String rollPage0, String rollPage1, String deptId, String remark, String certificateEnd0, String certificateEnd1) throws Exception {
    String sql = "";
    if(person.isAdminRole()){
      sql = "select "
        + "SEQ_ID"
        + ", ROLL_CODE"
        + ", ROLL_NAME"
        + ", ROOM_ID"
        + ", CATEGORY_NO"
        + ", CERTIFICATE_KIND"
        + ", SECRET"
        + ", STATUS"
        + " from RMS_ROLL where 1=1 ";
    }else{
      sql = "select "
        + "SEQ_ID"
        + ", ROLL_CODE"
        + ", ROLL_NAME"
        + ", ROOM_ID"
        + ", CATEGORY_NO"
        + ", CERTIFICATE_KIND"
        + ", SECRET"
        + ", STATUS"
        + " from RMS_ROLL where (ADD_USER = '" + person.getSeqId()+ "' or MANAGER = '" + person.getSeqId() + "')";
    }

    if(!T9Utility.isNullorEmpty(rollCode)){ 
      sql = sql + " and ROLL_CODE like '%" + rollCode + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(rollName)){ 
      sql = sql + " and ROLL_NAME like '%" + rollName + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(roomId)){ 
      sql = sql + " and ROOM_ID like '%" + roomId + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(years)){ 
      sql = sql + " and YEARS like '%" + years + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(beginDate0)){ 
      sql = sql + " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", beginDate0, ">=");
    } 
    if(!T9Utility.isNullorEmpty(beginDate1)){ 
      sql = sql + " and "+ T9DBUtility.getDateFilter("BEGIN_DATE", beginDate1, "<=");
    } 
    if(!T9Utility.isNullorEmpty(endDate0)){ 
      sql = sql + " and "+ T9DBUtility.getDateFilter("END_DATE", endDate0, ">=");
    } 
    if(!T9Utility.isNullorEmpty(endDate1)){ 
      sql = sql + " and "+ T9DBUtility.getDateFilter("END_DATE", endDate1, "<=");
    } 
    if(!T9Utility.isNullorEmpty(secret)){ 
      sql = sql + " and SECRET like '%" + secret + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(deadline0)){ 
      sql = sql + " and DEADLINE >= '" + deadline0 + "'";
    } 
    if(!T9Utility.isNullorEmpty(deadline1)){ 
      sql = sql + " and DEADLINE <= '" + deadline1 + "'";
    } 
    if(!T9Utility.isNullorEmpty(categoryNo)){ 
      sql = sql + " and CATEGORY_NO like '%" + categoryNo + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(catalogNo)){ 
      sql = sql + " and CATALOG_NO like '%" + catalogNo + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(archiveNo)){ 
      sql = sql + " and ARCHIVE_NO like '%" + archiveNo + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(boxNo)){ 
      sql = sql + " and BOX_NO like '%" + boxNo + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(microNo)){ 
      sql = sql + " and MICRO_NO like '%" + microNo + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(certificateKind)){ 
      sql = sql + " and CERTIFICATE_KIND like '%" + certificateKind + "%'" + T9DBUtility.escapeLike(); 
    } 
    if(!T9Utility.isNullorEmpty(certificateStart0)){ 
      sql = sql + " and CERTIFICATE_START >= '" + certificateStart0 + "'";
    } 
    if(!T9Utility.isNullorEmpty(certificateStart1)){ 
      sql = sql + " and CERTIFICATE_START <= '" + certificateStart1 + "'"; 
    } 
    if(!T9Utility.isNullorEmpty(certificateEnd0)){ 
      sql = sql + " and CERTIFICATE_END >= '" + certificateEnd0 + "'";
    } 
    if(!T9Utility.isNullorEmpty(certificateEnd1)){ 
      sql = sql + " and CERTIFICATE_END <= '" + certificateEnd1 + "'"; 
    } 
    if(!T9Utility.isNullorEmpty(rollPage0)){ 
      sql = sql + " and ROLL_PAGE >= '" + rollPage0 + "'";
    } 
    if(!T9Utility.isNullorEmpty(rollPage1)){ 
      sql = sql + " and ROLL_PAGE <= '" + rollPage1 + "'"; 
    } 
    if(!T9Utility.isNullorEmpty(deptId)){ 
      sql = sql + " and DEPT_ID = " + deptId + ""; 
    } 
    if(!T9Utility.isNullorEmpty(remark)){ 
      sql = sql + " and REMARK like '%" + remark + "%'" + T9DBUtility.escapeLike(); 
    } 
    sql = sql + " order by ROLL_CODE desc";
    
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
     
    return pageDataList.toJson();
  }
}
