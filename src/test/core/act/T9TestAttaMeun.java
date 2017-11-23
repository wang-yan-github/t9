package test.core.act;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9TestAttaMeun {
  public String uploadFile2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    
    String data = "";
    try{
      String comment = fileForm.getParameter("comment");
      fileForm.saveFile("C:\\Users\\yzq\\Desktop\\1.docx");
      
      System.out.println("comment>>" + comment);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String moudel = (fileForm.getParameter("moduel")== null )? "":fileForm.getParameter("moduel");

    String data = "";
    try{
      attr = fileUploadLogic(fileForm, T9SysProps.getAttachPath(),moudel);
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  /**
   * 
   * @param fileForm
   * @param pathPx
   * @return
   * @throws Exception
   */
  private Map<String, String> fileUploadLogic(T9FileUploadForm fileForm,
      String pathPx,String moudel) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    String filePath = pathPx;
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
        String attaName = fileName;
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9Guid.getRawGuid();
        fileName = rand + "_" + fileName;
        while (getExist(filePath + "\\" + hard, fileName)) {
          rand = T9Guid.getRawGuid();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, attaName);
        fileForm.saveFile(fieldName, filePath + "\\" + moudel + "\\" + hard + "\\" + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  /**
   * 
   * @param savePath
   * @param fileExtName
   * @return
   * @throws IOException
   */
  private  static boolean getExist(String savePath,String fileExtName) throws IOException {
    String filePath = savePath + "\\" + fileExtName;
    if (new File(filePath).exists()) {
      return true;
    }
   return false;
  }
}
