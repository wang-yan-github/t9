package t9.rad.docs.common.file;

import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9FileUpLoadDemo {
  /**
   * 批量附件上传的处理
   * 附件的命名规则 附件随机Id_附件名称
   * 附件的存储命名规则  “系统设定的附件存储路径 " + \\ + "当前模块的模块名称" + \\ + “当前年的后两位+月份（共四位）” + \\ + “上传后的附件名”
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);

      StringBuffer attachInfo = new StringBuffer();
      String filePath = T9SysProps.getAttachPath();
      String moudle = "test";
       filePath = filePath + "\\" + moudle;
      System.out.println(fileForm.getParamMap());
      String fileExists = fileForm.getExists(filePath);
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      if (fileExists != null) {
        response.setCharacterEncoding(T9Const.DEFAULT_CODE);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.println("-ERR 文件\"" + fileExists + "\"已经存在！");
        pw.flush();
        return null;
      }
      //fileForm.saveFileAll(filePath);
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = "123"; 
        String attachmentId = hard + "_" + rand;
        String attachmentName = fileName;
        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName,  filePath + "\\" + hard + "\\" + attachmentName);
        if(!"".equals(attachInfo.toString())){
          attachInfo.append(",");
        }
        attachInfo.append("{attachmentId:\"" + attachmentId + "\",attachmentName:\"" + attachmentName + "\"}");
      }
      String data = "[" + attachInfo.toString() + "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "上传失败:" + ex.getMessage());
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 单个附件上传的处理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFileSign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    String filePath = T9SysProps.getAttachPath();
    String moudle = "test";
     filePath = filePath + "\\" + moudle;
    System.out.println(fileForm.getParamMap());
    String fileExists = fileForm.getExists(filePath);
    Calendar cld = Calendar.getInstance();
    int year = cld.get(Calendar.YEAR) % 100;
    int month = cld.get(Calendar.MONTH) + 1;
    String mon = month >= 10 ? month + "" : "0" + month;
    String hard = year + mon;
    try{
        Iterator<String> iKeys = fileForm.iterateFileFields();
        while (iKeys.hasNext()) {
          String fieldName = iKeys.next();
          String fileName = fileForm.getFileName(fieldName);
          if (T9Utility.isNullorEmpty(fileName)) {
            continue;
          }
          String rand = "123"; 
          String attachmentId = hard + "_" + rand;
          String attachmentName = fileName;
          fileName = rand + "_" + fileName;
          fileForm.saveFile(fieldName,  filePath + "\\" + hard + "\\" + attachmentName);
          attrId += attachmentId + ",";
          attrName += attachmentName + "*";
        }
      data = "{attachmentId:\"" + attrId + "\",attachmentName:\"" + attrName + "\"}";
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
}
