package raw.lh;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.funcs.webinfo.file.T9WebInfoFileOperate;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;

public class T9ImageUploadAct {
  private static Logger log = Logger.getLogger("lh.raw.lh.T9ImageUploadAct");
  
  public String upload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String baseFilePath = request.getParameter("saveFileTo");
    //  baseFilePath = baseFilePath.replace("/", "\\");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      Date date = new Date();
      SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
      String fileName = sf.format(date) + fileForm.getFileName();
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
      String basePath = contextRealPath + baseFilePath ;
      
      String filePath = basePath  + "/" + fileName;
      fileForm.saveFile(filePath);
      String requestPath = request.getContextPath() + "/" + baseFilePath + "/" + fileName;
      
      request.setAttribute("address", requestPath);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }finally {
        
    }
    return "/raw/lh/fckeditor/editor/plugins/uploadimage/return.jsp";
  }
}
