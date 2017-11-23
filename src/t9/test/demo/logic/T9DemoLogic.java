package t9.test.demo.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.manage.logic.T9HrStaffIncentiveLogic;
import t9.subsys.oa.hr.setting.act.T9HrSetOtherAct;
import t9.test.demo.data.T9HrStaffCare;

public class T9DemoLogic {

  /**
   * 员工关怀 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getStaffCareJsonLogic(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " select c1.SEQ_ID, c1.CARE_TYPE, c1.BY_CARE_STAFFS,  c1.CARE_DATE "
                 + " from HR_STAFF_CARE c1 ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  public String getUserName(Connection dbConn, String seqId) throws Exception{
    
    String userName = "";
    String sql = " select user_name from person where seq_id="+seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        userName = rs.getString("user_name");
      }
    }catch(Exception ex){
      throw ex;
    }
    return userName;
  }
  
  /**
   * 处理上传附件，返回附件id，附件名称--wyw
   * 
   * @param fileForm
   * @return
   * @throws Exception
   */
  public Map<Object, Object> fileUploadLogic(T9FileUploadForm fileForm, String attachmentFolder) throws Exception {
    Map<Object, Object> result = new HashMap<Object, Object>();
    try {
      // 保存从文件柜、网络硬盘选择附件
      T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, attachmentFolder);
      String attIdStr = sel.getAttachIdToString(",");
      String attNameStr = sel.getAttachNameToString("*");
      boolean fromFolderFlag = false;
      String forlderAttchId = "";
      String forlderAttchName = "";
      if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
        forlderAttchId = attIdStr + ",";
        forlderAttchName = attNameStr + "*";
        fromFolderFlag = true;
      }
      Iterator<String> iKeys = fileForm.iterateFileFields();
      boolean uploadFlag = false;
      String uploadAttchId = "";
      String uploadAttchName = "";
      Date date = new Date();
      SimpleDateFormat format = new SimpleDateFormat("yyMM");
      String currDate = format.format(date);
      String separator = File.separator;
      String filePath = T9SysProps.getAttachPath() + separator + attachmentFolder + separator + currDate;

      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
        String rand = emul.getRandom();
        uploadAttchId += currDate + "_" + rand + ",";
        uploadAttchName += fileName + "*";
        uploadFlag = true;

        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName, filePath + File.separator + fileName);
      }
      boolean attachFlag = false;
      String attachmentIds = "";
      String attachmentNames = "";
      if (fromFolderFlag && uploadFlag) {
        attachmentIds = forlderAttchId + uploadAttchId;
        attachmentNames = forlderAttchName + uploadAttchName;
        attachFlag = true;
      } else if (fromFolderFlag) {
        attachmentIds = forlderAttchId;
        attachmentNames = forlderAttchName;
        attachFlag = true;
      } else if (uploadFlag) {
        attachmentIds = uploadAttchId;
        attachmentNames = uploadAttchName;
        attachFlag = true;
      }
      result.put("attachFlag", attachFlag);
      result.put("attachmentIds", attachmentIds);
      result.put("attachmentNames", attachmentNames);
    } catch (Exception e) {
      throw e;
    }
    return result;
  }

  /**
   * 新建员工关怀
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void setNewStaffCareValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
    T9ORM orm = new T9ORM();
    String careType = fileForm.getParameter("careType");
    String careFees = fileForm.getParameter("careFees");
    String byCareStaffsStr = fileForm.getParameter("byCareStaffs");
    String careDateStr = fileForm.getParameter("careDate");
    String careEffects = fileForm.getParameter("careEffects");
    String participants = fileForm.getParameter("participants");
    String careContent = fileForm.getParameter("careContent");
    String smsRemind = fileForm.getParameter("smsRemind");
    String sms2Remind = fileForm.getParameter("sms2Remind");

    Map<Object, Object> map = this.fileUploadLogic(fileForm, T9HrSetOtherAct.attachmentFolder);
    boolean attachFlag = (Boolean) map.get("attachFlag");
    String attachmentIds = (String) map.get("attachmentIds");
    String attachmentNames = (String) map.get("attachmentNames");

    try{
      String[] staffNameArry = byCareStaffsStr.split(",");
      if (staffNameArry != null && staffNameArry.length > 0) {
        for (String staffName : staffNameArry) {
          T9HrStaffCare staffCare = new T9HrStaffCare();
          staffCare.setCareType(careType);
          if(!T9Utility.isNullorEmpty(careFees)){
            staffCare.setCareFees(Double.valueOf(careFees));
          }
          staffCare.setByCareStaffs(staffName);
          if(!T9Utility.isNullorEmpty(careDateStr)){
            Date careDate = T9Utility.parseDate("yyyy-MM-dd", careDateStr);
            staffCare.setCareDate(careDate);
          }
          staffCare.setCareEffects(careEffects);
          staffCare.setParticipants(participants);
          staffCare.setCareContent(careContent);
          staffCare.setAddTime(T9Utility.parseTimeStamp());
          staffCare.setCreateUserId(String.valueOf(person.getSeqId()));
          staffCare.setCreateDeptId(person.getDeptId());
          if(attachFlag){
            staffCare.setAttachmentId(attachmentIds);
            staffCare.setAttachmentName(attachmentNames);
          }
          orm.saveSingle(dbConn, staffCare);
        }
      }
      int maxSeqId = this.getMaxSeqId(dbConn);
      T9MobileSms2Logic sbl = new T9MobileSms2Logic();
      String remindUrl = "/subsys/oa/hr/manage/staffCare/detail.jsp?seqId=" + maxSeqId + "&openFlag=1&openWidth=860&openHeight=650";
      String smsContent = "请查看员工关怀信息！";
      // 短信提醒
      if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
        this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), byCareStaffsStr+","+participants, "57", remindUrl, new Date());
      }
      // 手机提醒
      if (!T9Utility.isNullorEmpty(sms2Remind) && "1".equals(sms2Remind.trim())) {
        smsContent = "员工关怀管理: 关怀 " + getUserNameLogic(dbConn, byCareStaffsStr) + "等员工。 ";
        sbl.remindByMobileSms(dbConn, byCareStaffsStr+","+participants, person.getSeqId(), smsContent, new Date());
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 获取最大的SeqId值


   * 
   * @param dbConn
   * @return
   */
  public int getMaxSeqId(Connection dbConn) {
    String sql = "select SEQ_ID from HR_STAFF_CARE where SEQ_ID=(select MAX(SEQ_ID) from HR_STAFF_CARE )";
    int seqId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        seqId = rs.getInt("SEQ_ID");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return seqId;
  }
  
  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
    if (T9Utility.isNullorEmpty(userIdStr)) {
      userIdStr = "-1";
    }
    if (userIdStr.endsWith(",")) {
      userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
    }
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
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  /**
   * 获取详情
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9HrStaffCare getCareDetailLogic(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      return (T9HrStaffCare) orm.loadObjSingle(conn, T9HrStaffCare.class, seqId);
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * 员工关怀信息
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void updateCareInfoLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
    T9ORM orm = new T9ORM();
    String seqIdStr = fileForm.getParameter("seqId");
    String careType = fileForm.getParameter("careType");
    String careFees = fileForm.getParameter("careFees");
    String byCareStaffs = fileForm.getParameter("byCareStaffs");
    String careDateStr = fileForm.getParameter("careDate");
    String careEffects = fileForm.getParameter("careEffects");
    String participants = fileForm.getParameter("participants");
    String careContent = fileForm.getParameter("careContent");
    String smsRemind = fileForm.getParameter("smsRemind");
    String sms2Remind = fileForm.getParameter("sms2Remind");

    int seqId = 0;
    if (!T9Utility.isNullorEmpty(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    
    Map<Object, Object> map = this.fileUploadLogic(fileForm, T9HrSetOtherAct.attachmentFolder);
    boolean attachFlag = (Boolean) map.get("attachFlag");
    String attachmentIds = (String) map.get("attachmentIds");
    String attachmentNames = (String) map.get("attachmentNames");
    
    try {
      T9HrStaffCare staffCare = (T9HrStaffCare) orm.loadObjSingle(dbConn, T9HrStaffCare.class, seqId);
      if (staffCare != null) {
        String dbAttachId = T9Utility.null2Empty(staffCare.getAttachmentId());
        String dbAttachName = T9Utility.null2Empty(staffCare.getAttachmentName());
      
        staffCare.setCareType(careType);
        staffCare.setCareFees(Double.valueOf(careFees));
        staffCare.setByCareStaffs(byCareStaffs);
        if(!T9Utility.isNullorEmpty(careDateStr)){
          Date careDate = T9Utility.parseDate("yyyy-MM-dd", careDateStr);
          staffCare.setCareDate(careDate);
        }
        staffCare.setCareEffects(careEffects);
        staffCare.setParticipants(participants);
        staffCare.setCareContent(careContent);
        staffCare.setAddTime(T9Utility.parseTimeStamp());
        staffCare.setCreateUserId(String.valueOf(person.getSeqId()));
        if(attachFlag){
          staffCare.setAttachmentId(dbAttachId.trim() + attachmentIds.trim());
          staffCare.setAttachmentName(dbAttachName.trim() + attachmentNames.trim());
        }
        orm.updateSingle(dbConn, staffCare);
        T9MobileSms2Logic sbl = new T9MobileSms2Logic();
        String remindUrl = "/subsys/oa/hr/manage/staffCare/detail.jsp?seqId=" + seqId + "&openFlag=1&openWidth=860&openHeight=650";
        String smsContent = "请查看员工关怀信息！";
        // 短信提醒
        if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
          this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), byCareStaffs+","+participants, "57", remindUrl, new Date());
        }
        // 手机提醒
        if (!T9Utility.isNullorEmpty(sms2Remind) && "1".equals(sms2Remind.trim())) {
          smsContent = "员工关怀管理: 关怀 " + getUserNameLogic(dbConn, byCareStaffs) + "等员工。 ";
          sbl.remindByMobileSms(dbConn, byCareStaffs+","+participants, person.getSeqId(), smsContent, new Date());
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 删除文件--wyw
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteFileLogic(Connection dbConn, String seqIdStr, String filePath) throws Exception {
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          StringBuffer attIdBuffer = new StringBuffer();
          StringBuffer attNameBuffer = new StringBuffer();
          T9HrStaffCare staffCare = (T9HrStaffCare) orm.loadObjSingle(dbConn, T9HrStaffCare.class, Integer.parseInt(seqId));
          String attachmentId = T9Utility.null2Empty(staffCare.getAttachmentId());
          String attachmentName = T9Utility.null2Empty(staffCare.getAttachmentName());
          attIdBuffer.append(attachmentId.trim());
          attNameBuffer.append(attachmentName.trim());
          String[] attIdArray = {};
          String[] attNameArray = {};
          if (!T9Utility.isNullorEmpty(attIdBuffer.toString()) && !T9Utility.isNullorEmpty(attNameBuffer.toString()) && attIdBuffer.length() > 0) {
            attIdArray = attIdBuffer.toString().trim().split(",");
            attNameArray = attNameBuffer.toString().trim().split("\\*");
          }
          if (attIdArray != null && attIdArray.length > 0) {
            for (int i = 0; i < attIdArray.length; i++) {
              Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
              if (map.size() != 0) {
                Set<String> set = map.keySet();
                // 遍历Set集合
                for (String keySet : set) {
                  String key = keySet;
                  String keyValue = map.get(keySet);
                  String attaIdStr = this.getAttaId(keySet);
                  String fileNameValue = attaIdStr + "_" + keyValue;
                  String fileFolder = this.getFilePathFolder(key);
                  String oldFileNameValue = attaIdStr + "." + keyValue;
                  File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
                  File oldFile = new File(filePath + File.separator + fileFolder + File.separator + oldFileNameValue);
                  if (file.exists()) {
                    T9FileUtility.deleteAll(file.getAbsoluteFile());
                  } else if (oldFile.exists()) {
                    T9FileUtility.deleteAll(oldFile.getAbsoluteFile());
                  }
                }
              }
            }
          }
          // 删除数据库信息


          orm.deleteSingle(dbConn, staffCare);
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 拼接附件Id与附件名--wyw
   * 
   * @param attachmentId
   * @param attachmentName
   * @return
   */
  public Map<String, String> getFileName(String attachmentId, String attachmentName) {
    Map<String, String> map = new HashMap<String, String>();
    if (T9Utility.isNullorEmpty(attachmentId) || T9Utility.isNullorEmpty(attachmentName)) {
      return map;
    }
    if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {
      String attachmentIds[] = attachmentId.split(",");
      String attachmentNames[] = attachmentName.split("\\*");
      if (attachmentIds.length != 0 && attachmentNames.length != 0) {
        for (int i = 0; i < attachmentIds.length; i++) {
          map.put(attachmentIds[i], attachmentNames[i]);
        }
      }
    }
    return map;
  }
  
  /**
   * 得到附件的Id 兼老数据--wyw
   * 
   * @param keyId
   * @return
   */
  public String getAttaId(String keyId) {
    String attaId = "";
    if (keyId != null && !"".equals(keyId)) {
      if (keyId.indexOf('_') != -1) {
        String[] ids = keyId.split("_");
        if (ids.length > 0) {
          attaId = ids[1];
        }

      } else {
        attaId = keyId;
      }
    }
    return attaId;
  }
  
  /**
   * 得到该文件的文件夹名--wyw
   * 
   * @param key
   * @return
   */
  public String getFilePathFolder(String key) {
    String folder = "";
    if (key != null && !"".equals(key)) {
      if (key.indexOf('_') != -1) {
        String[] str = key.split("_");
        for (int i = 0; i < str.length; i++) {
          folder = str[0];
        }
      } else {
        folder = "all";
      }
    }
    return folder;
  }
  
  /**
   * 员工关怀查询
   * 
   * @param dbConn
   * @param request
   * @param map
   * @param person
   * @return
   * @throws Exception
   */
  public String queryCareListJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
    T9HrStaffIncentiveLogic logic = new T9HrStaffIncentiveLogic();
    String deptIdStr = logic.getHrManagerPriv(dbConn, person);
    
    String careType = (String) map.get("careType");
    String byCareStaffs = (String) map.get("byCareStaffs");
    String careDate1 = (String) map.get("careDate1");
    String careDate2 = (String) map.get("careDate2");
    String careFees1 = (String) map.get("careFees1");
    String careFees2 = (String) map.get("careFees2");
    String participants = (String) map.get("participants");
    String careContent = (String) map.get("careContent");
    String conditionStr = "";
    String sql = "";
    try {
      if (!T9Utility.isNullorEmpty(careType)) {
        conditionStr = " and c1.CARE_TYPE ='" + T9DBUtility.escapeLike(careType) + "'";
      }
      if (!T9Utility.isNullorEmpty(byCareStaffs)) {
        
        conditionStr += " and " + T9DBUtility.findInSet(byCareStaffs, "c1.BY_CARE_STAFFS");
      }
      if (!T9Utility.isNullorEmpty(careDate1)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("CARE_DATE", careDate1, ">=");
      }
      if (!T9Utility.isNullorEmpty(careDate2)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("CARE_DATE", careDate2, "<=");
      }
      if (!T9Utility.isNullorEmpty(careFees1)) {
        conditionStr += " and CARE_FEES >= " + careFees1;
      }
      if (!T9Utility.isNullorEmpty(careFees2)) {
        conditionStr += " and CARE_FEES <= " + careFees2;
      }
      if (!T9Utility.isNullorEmpty(participants)) {
        conditionStr += " and " + T9DBUtility.findInSet(participants, "c1.PARTICIPANTS");
      }
      if (!T9Utility.isNullorEmpty(careContent)) {
        conditionStr += " and c1.CARE_CONTENT like '%" + T9DBUtility.escapeLike(careContent) + "%'";
      }
      sql = " select c1.SEQ_ID, c1.CARE_TYPE, c1.BY_CARE_STAFFS, c1.CARE_FEES, c1.PARTICIPANTS, c1.CARE_DATE "
          + " from HR_STAFF_CARE c1 "
          + " where CREATE_USER_ID = "+ person.getSeqId()
          + conditionStr 
          + " ORDER BY c1.ADD_TIME desc";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  
  /**
   * CMS 获取站点树
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getTree(Connection dbConn, T9Person person) throws Exception {
    
    String data = "[{nodeId:\"11\",name:\"站点八\",isHaveChild:1,extData:{title:\"此处为站点八\"}},"
                + "{nodeId:\"12\",name:\"集团公司\",isHaveChild:1,extData:{}}]";
    return data;
  }
  
  
  /**
   * 短信提醒(带时间)
   * 
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @param sendDate
   * @throws Exception
   */
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
}
