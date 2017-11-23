package test.core.act;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import test.core.dto.T9InnerBean11;

public class T9TestFileUploadAct {
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);

    String filePath = "D:\\tmp\\upload";
    System.out.println(fileForm.getParamMap());
    String fileExists = fileForm.getExists(filePath);
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
      T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
      String rand = "123"; 
      fileName = rand + "_" + fileName;
      fileForm.saveFile(fieldName, filePath + "\\" + fileName);
    }
    PrintWriter pw = response.getWriter();
    pw.println("-OK");
    pw.flush();
    return null;
  }
  
  /**
   * 构造单个对象
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);

    String filePath = "D:\\tmp\\upload";
    System.out.println(fileForm.getParamMap());

    //fileForm.saveFileAll(filePath);
    StringBuffer fileStr = new StringBuffer();
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      System.out.println("fieldName>>" + fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      System.out.println("fieldName>>" + fieldName);
      fileStr.append("\'");
      fileStr.append(fileName);
      fileStr.append("\',");
      T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
      String rand = emul.getRandom(); 
      fileName = rand + "_" + fileName;
      fileForm.saveFile(fieldName, filePath + "\\" + fileName);
    }
    if (fileStr.length() > 0) {
      fileStr.deleteCharAt(fileStr.length() - 1);
    }
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
    request.setAttribute(T9ActionKeys.RET_DATA, fileStr.toString());

    return "/core/inc/rtuploadfile.jsp";
  }
}
