package t9.core.funcs.workplan.logic;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workplan.data.T9WorkPlanCont;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
public class T9WorkPlanManageLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");
  //private T9NotifyManageUtilLogic notifyManageUtil = new T9NotifyManageUtilLogic();
  /**
 
  
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
        
        while (T9DiaryUtil.getExist(T9SysProps.getAttachPath() +File.separator+ hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, T9SysProps.getAttachPath()  +File.separator+ T9WorkPlanCont.MODULE  +File.separator+ hard  +File.separator+fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  /**
   * 附件批量上传页面处理
   * @return
  * @throws Exception 
   */
  public StringBuffer uploadMsrg2Json( T9FileUploadForm fileForm) throws Exception{
    StringBuffer sb = new StringBuffer();
    Map<String, String> attr = null;
    String attachmentId = "";
    String attachmentName = "";
    try{
      attr = fileUploadLogic(fileForm);
      Set<String> attrKeys = attr.keySet();
      for (String key : attrKeys){
        String fileName = attr.get(key);
        attachmentId += key + ",";
        attachmentName += fileName + "*";
      }
      long size = getSize(fileForm);
      sb.append("{");
      sb.append("'attachmentId':").append("\"").append(attachmentId).append("\",");
      sb.append("'attachmentName':").append("\"").append(attachmentName).append("\",");
      sb.append("'size':").append("").append(size);
      sb.append("}");
   } catch (Exception e){
     e.printStackTrace();
     throw e;
   }
    return sb;
  }
  
  
  /**
   * 附件批量上传页面处理
   * @return
  * @throws Exception 
   */
  public Map uploadMsrg2Map( T9FileUploadForm fileForm,String pathP) throws Exception{
    Map<String, String> map = new HashMap();
    Map<String, String> attr = null;
    String attachmentId = "";
    String attachmentName = "";
    try{
      attr = fileUploadLogic(fileForm);
      Set<String> attrKeys = attr.keySet();
      for (String key : attrKeys){
        String fileName = attr.get(key);
        String file = fileName.split("_")[1];
        attachmentId += key + ",";
        attachmentName += fileName + "*";
      }
      long size = getSize(fileForm);
      map.put("attachmentId", attachmentId);
      map.put("attachmentName", attachmentName);
   } catch (Exception e){
     e.printStackTrace();
     throw e;
   }
    return map;
  }
  
  public long getSize( T9FileUploadForm fileForm) throws Exception{
    long result = 0l;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      result += fileForm.getFileSize(fieldName);
    }
    return result;
  }
}
