package t9.subsys.oa.guest.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.guest.data.T9Guest;

public class T9GuestLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.guest.act.T9Guest");
  public static int addGuest(Connection dbConn,T9Guest guest) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, guest);
    return 0;
  }
  public static void updateGuest(Connection dbConn,T9Guest guest) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, guest);
  }
  public static void delGuest(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9Guest.class, Integer.parseInt(seqId));
  }
  public static T9Guest selectGuestById(Connection dbConn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9Guest guest = (T9Guest) orm.loadObjSingle(dbConn, T9Guest.class, Integer.parseInt(seqId));
    return guest;
  }

  /***
   * 查询所有数据
   * @return
   * @throws Exception 
   */
  public static String queryGuest(Connection dbConn,Map request) throws Exception {
    String sql = "select g.seq_id,g.GUEST_NUM,ci.class_desc,g.GUEST_NAME,g.GUEST_UNIT,"
      +"g.GUEST_PHONE,g.GUEST_DEPT,g.GUEST_ATTEND_TIME,g.GUEST_LEAVE_TIME,g.GUEST_DINER"
      + " from GUEST g left outer join CODE_ITEM ci on g.GUEST_TYPE = ci.SEQ_ID where 1=1";
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /***
   * 根据条件查询数据
   * @return
   * @throws Exception 
   */
  public static String queryGuestTrem(Connection dbConn,Map request,String guestNum,String guestType,String guestName
      ,String guestDiner,String guestUnit,String guestPhone,String guestAttendTime,String guestAttendTime1
      ,String guestLeaveTime,String guestLeaveTime1,String guestCreator,String guestDept,String guestNote) throws Exception {
    String sql = "select g.seq_id,g.GUEST_NUM,ci.class_desc,g.GUEST_NAME,g.GUEST_UNIT,"
      +"g.GUEST_PHONE,g.GUEST_DEPT,g.GUEST_ATTEND_TIME,g.GUEST_LEAVE_TIME,g.GUEST_DINER"
      + " from GUEST g left outer join CODE_ITEM ci on g.GUEST_TYPE = ci.SEQ_ID where 1=1";
    if(!T9Utility.isNullorEmpty(guestNum)){
      sql += " and g.guest_num like '%" + T9Utility.encodeLike(guestNum) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(guestType)) {
      sql += " and guest_type = '" + guestType + "'";
    }
    if (!T9Utility.isNullorEmpty(guestName)) {
      sql += " and guest_name like '%" + T9Utility.encodeLike(guestName) + "%' " + T9DBUtility.escapeLike();
    }

    if (!T9Utility.isNullorEmpty(guestDiner)) {
      sql += " and guest_diner like '%" + T9Utility.encodeLike(guestDiner) + "%' " + T9DBUtility.escapeLike();
    }
    
    
    if (!T9Utility.isNullorEmpty(guestUnit)) {
      sql += " and guest_unit like '%" + T9Utility.encodeLike(guestUnit) + "%' " + T9DBUtility.escapeLike();
    }

    if (!T9Utility.isNullorEmpty(guestPhone)) {
      sql += " and guest_phone like '%" + T9Utility.encodeLike(guestPhone) + "%' " + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(guestAttendTime)) {
      String str =  T9DBUtility.getDateFilter("guest_attend_time", guestAttendTime, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(guestAttendTime1)) {
      String str =  T9DBUtility.getDateFilter("guest_attend_time", guestAttendTime1, "<=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(guestAttendTime)) {
      String str =  T9DBUtility.getDateFilter("guest_leave_time", guestLeaveTime, ">=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(guestAttendTime)) {
      String str =  T9DBUtility.getDateFilter("guest_leave_time", guestLeaveTime1, "<=");
      sql += " and " + str;
    }
    if (!T9Utility.isNullorEmpty(guestCreator)) {
      sql += " and guest_creator = '" + guestCreator + "'";
    }
    if (!T9Utility.isNullorEmpty(guestDept)) {
      sql += " and " + T9DBUtility.findInSet(guestDept, "guest_dept");
    }
    if (!T9Utility.isNullorEmpty(guestNote)) {
      sql += " and guest_note like '%" + T9Utility.encodeLike(guestNote) + "%' " + T9DBUtility.escapeLike();
    }

    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
    return pageDataList.toJson();
  }
  /**
   * 处理上传附件，返回附件id，附件名称



   * 
   * @param request
   *          HttpServletRequest
   * @param
   * @return Map<String, String> ==> {id = 文件名}
   * @throws Exception
   */
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    try {
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String fileNameV = fileName;
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9DiaryUtil.getRondom();
        fileName = rand + "_" + fileName;
        while (T9DiaryUtil.getExist(T9SysProps.getAttachPath() + File.separator + hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, T9SysProps.getAttachPath() + File.separator + "guest" + File.separator + hard + File.separator + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  /**
   * 更新数据库中的文件

   * @param dbConn
   * @param attachmentId
   * @param attachmentName
   * @param seqId
   * @throws Exception
   */
  public void updateFile(Connection dbConn,String tableName,String attachmentId,String attachmentName,String seqId) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    try {
      String sql = "update " + tableName + " set ATTACHMENT_ID = ? ,ATTACHMENT_NAME = ? where SEQ_ID=?"   ;
      pstmt = dbConn.prepareStatement(sql);
      pstmt.setString(1, attachmentId);
      pstmt.setString(2,attachmentName);
      pstmt.setString(3, seqId);
      pstmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(pstmt, rs, log);
    }
  }
}
