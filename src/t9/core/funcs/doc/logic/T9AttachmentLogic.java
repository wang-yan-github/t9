package t9.core.funcs.doc.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.email.data.T9EmailCont;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.doc.data.T9DocRun;
import t9.core.funcs.doc.data.T9DocFlowRunFeedback;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
public class T9AttachmentLogic {
  public static String filePath = T9SysProps.getAttachPath() + File.separator + T9WorkFlowConst.MODULE;
  public static String COPYPATH = File.separator+"core"+File.separator+"funcs"+File.separator+T9WorkFlowConst.MODULE+File.separator+"workflowUtility";
  public Map uploadAttachment( T9FileUploadForm fileForm , Connection conn ) throws Exception{
    File f1 = new File(filePath);
    if (!f1.exists()) {
      f1.mkdir();
    }
    T9ORM orm = new T9ORM();
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard = year + mon ;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    String attachmentNameStr = "";
    String attachmentIdStr = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      if (fieldName.startsWith("ATTACHMENT_")) {
        continue;
      }
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String attachmentId = T9Guid.getRawGuid(); 
      String fileName2 = attachmentId + "_" + fileName;
      File f2 = new File(filePath + File.separator + hard);
      if (!f2.exists()) {
        f2.mkdir();
      }
      String tmp = filePath+ File.separator + hard + File.separator +fileName2;
      fileForm.saveFile(fieldName , tmp);
      
      attachmentNameStr += fileName + "*";
      attachmentIdStr += hard + "_" + attachmentId + ",";
    }
    Map map = new HashMap();
    map.put("id", attachmentIdStr);
    map.put("name", attachmentNameStr);
    return map;
  }
  public void  restoreFile(String attachmentNameStr , String attachmentIdStr , int runId , Connection conn ) throws Exception{
    T9ORM orm = new T9ORM();
    T9FlowRunLogic runLogic = new T9FlowRunLogic();
    T9DocRun flowRun = runLogic.getFlowRunByRunId(runId , conn);
    String attachmentId = flowRun.getAttachmentId() != null ? flowRun.getAttachmentId() : "";
    String attachmentName = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
    if (!attachmentIdStr.endsWith(","))  {
      attachmentIdStr += ",";
    }
    if (!attachmentNameStr.endsWith("*")) {
      attachmentNameStr += "*";
    }
    attachmentIdStr += attachmentId;
    attachmentNameStr += attachmentName;
    flowRun.setAttachmentId(attachmentIdStr);
    flowRun.setAttachmentName(attachmentNameStr);
    orm.updateSingle(conn, flowRun);
  }
  public void addAttachment(int runId , T9FileUploadForm fileForm , Connection conn ) throws Exception{
    File f1 = new File(filePath);
    if (!f1.exists()) {
      f1.mkdir();
    }
    T9ORM orm = new T9ORM();
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard = year + mon ;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    String attachmentNameStr = "";
    String attachmentIdStr = "";
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      if (fieldName.startsWith("ATTACHMENT1_")) {
        continue;
      }
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String attachmentId = T9Guid.getRawGuid(); 
      String fileName2 = attachmentId + "_" + fileName;
      File f2 = new File(filePath + File.separator + hard);
      if (!f2.exists()) {
        f2.mkdir();
      }
      String tmp = filePath + File.separator +hard + File.separator + fileName2;
      fileForm.saveFile(fieldName , tmp);
      
      attachmentNameStr += fileName + "*";
      attachmentIdStr += hard + "_" + attachmentId + ",";
    }
    T9FlowRunLogic runLogic = new T9FlowRunLogic();
    T9DocRun flowRun = runLogic.getFlowRunByRunId(runId , conn);
    String attachmentId = flowRun.getAttachmentId() != null ? flowRun.getAttachmentId() : "";
    String attachmentName = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
    attachmentIdStr += attachmentId;
    attachmentNameStr += attachmentName;
    flowRun.setAttachmentId(attachmentIdStr);
    flowRun.setAttachmentName(attachmentNameStr);
    orm.updateSingle(conn, flowRun);
  }
  /**
   * 取得所有的附件
   * @param user
   * @param runId
   * @param prcsId
   * @param flowPrcs
   * @return
   * @throws Exception
   */
  public String getAttachments(T9Person user ,int runId , int flowId , Connection conn) throws Exception{
    
    int flowType = 1;
    String query = "select FLOW_TYPE from "+ T9WorkFlowConst.FLOW_TYPE +" where SEQ_ID = " + flowId;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if (rs.next()) {
        String tmp = rs.getString("FLOW_TYPE");
        if (tmp != null) {
          flowType = Integer.parseInt(tmp);
        }
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    String priv = "";
    if (flowType == 1) {
      priv = this.getDownPrintPriv(runId, flowId, user.getSeqId(), conn);
    } else {
      priv = "1,1";
    }
    StringBuffer sb = new StringBuffer();
    Map map = new HashMap();
    map.put("RUN_ID", runId);
    T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
    T9DocRun flowRun = flowRunLogic.getFlowRunByRunId(runId, conn);
    if(flowRun.getAttachmentId() == null || "".equals(flowRun.getAttachmentId())){
      return "";
    }
    String attachmentName = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
    String attachmentId = flowRun.getAttachmentId() != null ? flowRun.getAttachmentId() : "";
    String[] attachsName = attachmentName.split("\\*");
    String[] attachsId = attachmentId.split(",");
    for ( int i = 0 ;i < attachsId.length ;i ++ ) {
      String tmp = attachsId[i];
      String name = attachsName[i];    
      sb.append("{attachmentName:'" + name + "'");
      sb.append(",attachmentId:'" + tmp + "'" +
      		",ext:'" +  T9FileUtility.getFileExtName(name) + "',priv:'"+ priv +"'},");
    }
    if ( attachsId.length > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  /**
   * 取得所有的附件
   * @param user
   * @param runId
   * @param prcsId
   * @param flowPrcs
   * @return
   * @throws Exception
   */
  public String getAttachmentsHtml(T9Person user ,int runId , Connection conn) throws Exception{
    StringBuffer sb = new StringBuffer();
    Map map = new HashMap();
    map.put("RUN_ID", runId);
    T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
    T9DocRun flowRun = flowRunLogic.getFlowRunByRunId(runId, conn);
    if(flowRun.getAttachmentId() == null || "".equals(flowRun.getAttachmentId())){
      return null;
    }
    String attachmentName = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
    String[] attachsName = attachmentName.split("\\*");
    for ( int i = 0 ;i < attachsName.length ;i ++ ) {
      String name = attachsName[i];    
      sb.append("<tr class='TableData'>");
      sb.append("<td align=left width='100%'>" + name + "<td>");
      sb.append("</tr>");
    }
    return sb.toString();
  }
  
  
  /**
   * 新建文档
   * @param runId
   * @param newType
   * @param newName
   * @throws Exception 
   */
  public String createAttachment(int runId, String newType, String newName , Connection conn , String webrootPath) throws Exception {
    // TODO Auto-generated method stub
    String fileName = newName + "." + newType;
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard = year + mon ;
    String attachmentId = T9Guid.getRawGuid(); 
    T9FlowRunLogic runLogic = new T9FlowRunLogic();
    T9DocRun flowRun = runLogic.getFlowRunByRunId(runId ,conn);
    String attachmentIdStr = flowRun.getAttachmentId() != null ? flowRun.getAttachmentId() : "";
    String attachmentNameStr = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
    
    String fileName2 = attachmentId + "_" + fileName;
    String tmp = filePath + File.separator + hard+ File.separator + fileName2;
    
    File catalog = new File(filePath+ File.separator +hard);
    if(!catalog.exists()){
      catalog.mkdirs();
    }
    boolean success = false;
    if ("xls".equals(newType)) {
      String srcFile = webrootPath + this.COPYPATH  + File.separator + "copy.xls";
      T9FileUtility.copyFile(srcFile, tmp);
      success = true;
    } else if ("ppt".equals(newType)) {
      String srcFile = webrootPath + this.COPYPATH   + File.separator + "copy.ppt";
      T9FileUtility.copyFile(srcFile, tmp);
      success = true;
    } else if ("doc".equals(newType)) {
      String srcFile = webrootPath + this.COPYPATH  + File.separator + "copy.doc";
      T9FileUtility.copyFile(srcFile, tmp);
      success = true;
    } else {
      File file = new File(tmp);
      success = file.createNewFile();
    }
    if (success) {
      attachmentNameStr += fileName + "*";
      attachmentIdStr += hard + "_" + attachmentId + ",";
      
      flowRun.setAttachmentId(attachmentIdStr);
      flowRun.setAttachmentName(attachmentNameStr);
      T9ORM orm = new T9ORM();
      orm.updateSingle(conn, flowRun);
      return "{attachmentName:'"+ fileName +"', attachmentId:'"+ hard + "_" + attachmentId +"'}";
    } else {
      return "''";
    }
  }
  /**
   * 删除指定的公共附件
   * @param attachmentId
   * @param attachmentName
   */
  public void deleteAttachments (String attachmentId , String attachmentName) {
    if(attachmentId == null 
        || "".equals(attachmentId)
        || attachmentName == null 
        || "".equals(attachmentName)){
      return ;
    }
    String[] attachmentIdArray = attachmentId.split(",");
    String[] attachmentNameArray = attachmentName.split("\\*");
    for (int i = 0 ; i< attachmentIdArray.length ; i++) {
      //处理文件
      String tmpId = attachmentIdArray[i];
      String tmpName = attachmentNameArray[i];
      
      this.deleteAttachement(tmpId, tmpName);
    }
  }
  /**
   * 删除附件
   * @param runId
   * @param attachmentId
   * @param attachmentName
   * @throws Exception
   */
  public void delFeedAttachment(int feedId , String attachmentId , String attachmentName , Connection conn) throws Exception{
    //处理数据库
    T9ORM orm = new T9ORM();
    T9DocFlowRunFeedback fb = (T9DocFlowRunFeedback) orm.loadObjSingle(conn, T9DocFlowRunFeedback.class, feedId);
    String attachmentIds = fb.getAttachmentId();
    String attachmentNames = fb.getAttachmentName();
    if(attachmentIds == null 
        || "".equals(attachmentIds)
        || attachmentNames == null 
        || "".equals(attachmentNames)
        || attachmentId == null 
        || "".equals(attachmentId)
        || attachmentName == null 
        || "".equals(attachmentName)){
      return ;
    }
    String[] attachmentIdArray = attachmentIds.split(",");
    String[] attachmentNameArray = attachmentNames.split("\\*");
    String newAttachId = "";
    String newAttachName = "";
    for(int i = 0 ;i < attachmentIdArray.length ;i ++){
      String tmp = attachmentIdArray[i];
      if(!tmp.equals(attachmentId)){
        newAttachId += tmp + ",";
        newAttachName += attachmentNameArray[i] + "*";
      }
    }
    fb.setAttachmentId(newAttachId);
    fb.setAttachmentName(newAttachName);
    orm.updateSingle(conn, fb);
    this.deleteAttachement(attachmentId, attachmentName);
  }
  public static void deleteAttachement(String aId , String aName) {
  //处理文件
    if (T9Utility.isNullorEmpty(aId)
        || T9Utility.isNullorEmpty(aName)) {
      return ;
    }
    int index = aId.indexOf("_");
    String hard = "";
    String str = "";
    if (index > 0) {
      hard = aId.substring(0, index);
      str = aId.substring(index + 1);
    } else {
      hard = "all";
      str = aId;
    }
    String path = filePath  + File.separator +  hard + File.separator + str + "_" + aName;
    File file = new File(path);
    if(file.exists()){
      file.delete();
    } else {
      //兼容老的数据
      String path2 = filePath + File.separator +  hard + File.separator +  str + "." + aName;
      File file2 = new File(path2);
      if(file2.exists()){
        file2.delete();
      }
    }
  }
  /**
   * 删除附件
   * @param runId
   * @param attachmentId
   * @param attachmentName
   * @throws Exception
   */
  public void delAttachment(int runId , String attachmentId , String attachmentName , Connection conn) throws Exception{
    //处理数据库
    T9ORM orm = new T9ORM();
    Map map = new HashMap();
    map.put("RUN_ID", runId);
    T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
    T9DocRun flowRun = flowRunLogic.getFlowRunByRunId(runId , conn);
    String attachmentIds = flowRun.getAttachmentId();
    String attachmentNames = flowRun.getAttachmentName();
    if(attachmentIds == null 
        || "".equals(attachmentIds)
        || attachmentNames == null 
        || "".equals(attachmentNames)
        || attachmentId == null 
        || "".equals(attachmentId)
        || attachmentName == null 
        || "".equals(attachmentName)){
      return ;
    }
    String[] attachmentIdArray = attachmentIds.split(",");
    String[] attachmentNameArray = attachmentNames.split("\\*");
    String newAttachId = "";
    String newAttachName = "";
    for(int i = 0 ;i < attachmentIdArray.length ;i ++){
      String tmp = attachmentIdArray[i];
      if(!tmp.equals(attachmentId)){
        newAttachId += tmp + ",";
        newAttachName += attachmentNameArray[i] + "*";
      }
    }
    flowRun.setAttachmentId(newAttachId);
    flowRun.setAttachmentName(newAttachName);
    orm.updateSingle(conn, flowRun);
    //处理文件
    this.deleteAttachement(attachmentId, attachmentName);
  }
  public String getDownPrintPriv(int runId , int flowId , int userId , Connection conn ) throws Exception {
    String downPrivOffic = "";
    String query = "select ATTACH_PRIV from "+ T9WorkFlowConst.FLOW_RUN_PRCS +","+ T9WorkFlowConst.FLOW_PROCESS +" WHERE "+ T9WorkFlowConst.FLOW_RUN_PRCS +".RUN_ID="+ runId +" AND "+ T9WorkFlowConst.FLOW_PROCESS +".FLOW_SEQ_ID="+ flowId +" AND "+ T9WorkFlowConst.FLOW_RUN_PRCS +".FLOW_PRCS="+ T9WorkFlowConst.FLOW_PROCESS +".PRCS_ID AND "+ T9WorkFlowConst.FLOW_RUN_PRCS +".USER_ID="+ userId ;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      int downFlag = 0 ;
      int printFlag = 0 ;
      if (rs.next()) {
        String tmp = rs.getString("ATTACH_PRIV");
        if (T9WorkFlowUtility.findId(tmp, "4")) {
          downFlag = 1;
        }
        if (T9WorkFlowUtility.findId(tmp, "5")) {
          printFlag = 1 ;
        }
      }
      if (downFlag == 0) {
        downPrivOffic = "0";
      } else {
        //有权限
        downPrivOffic = "1";
      }
      if (printFlag == 0) {
        downPrivOffic += ",0";
      } else {
        //有权限
        downPrivOffic += ",1";
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    return downPrivOffic;
  }
  public String copyAttachSingle(String attId , String attName) throws Exception {
    int index = attId.indexOf("_");
    String hard = "";
    String str = "";
    if (index > 0) {
      hard = attId.substring(0, index);
      str = attId.substring(index + 1);
    } else {
      hard = "all";
      str = attId;
    }
    String path = filePath+ File.separator +  hard+ File.separator +  str + "_" + attName;
    String attachmentId = T9Guid.getRawGuid();
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard2 = year + mon ;
    String fileName2 = attachmentId + "_" + attName;
    File catalog = new File(filePath + File.separator + hard2);
    if(!catalog.exists()){
      catalog.mkdirs();
    }
    String tmp = filePath + File.separator +  hard2 + File.separator +  fileName2;
    T9FileUtility.copyFile(path, tmp);
    return hard2 + "_" + attachmentId;
  }
  public String copyAttach(String attId , String attName) throws Exception {
    String newAttId = "";
    String[] attIds = attId.split(",");
    String[] attNames = attName.split("\\*");
    for(int i = 0 ;i < attIds.length ;i ++){
      String tmp = attIds[i];
      if ("".equals(tmp)) {
        continue;
      }
      String attN = attNames[i];
      String newId = this.copyAttachSingle(tmp, attN);
      newAttId += newId + ",";
    }
    return newAttId;
  }
  public Map restoreFeedAttach(String attachName, String attachDir) throws Exception {
    // TODO Auto-generated method stub
    File f1 = new File(filePath);
    if (!f1.exists()) {
      f1.mkdir();
    }
    T9ORM orm = new T9ORM();
    Calendar cld = Calendar.getInstance();
    int year =  cld.get(Calendar.YEAR)%100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10?month+"":"0"+month;
    String hard = year + mon ;
    String[] aAttachName = attachName.split("\\*");
    String[] aAttachDir = attachDir.split("\\*");
    
    
    String attachmentNameStr = "";
    String attachmentIdStr = "";
    for (int i = 0 ;i < aAttachName.length ;i ++) {
      String fileName = aAttachName[i];
      String filePath2 = aAttachDir[i] + fileName;
      
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      String attachmentId = T9Guid.getRawGuid(); 
      String fileName2 = attachmentId + "_" + fileName;
      File f2 = new File(filePath+ File.separator +  hard);
      if (!f2.exists()) {
        f2.mkdir();
      }
      String tmp = filePath + File.separator +  hard + File.separator + fileName2;
      
      T9FileUtility.copyFile(filePath2, tmp);
      
      attachmentNameStr += fileName + "*";
      attachmentIdStr += hard + "_" + attachmentId + ",";
    }
    Map map = new HashMap();
    map.put("id", attachmentIdStr);
    map.put("name", attachmentNameStr);
    return map;
  }
}
