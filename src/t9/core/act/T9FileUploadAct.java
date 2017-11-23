package t9.core.act;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.file.T9FileUploadForm;

public class T9FileUploadAct {
  /**
   * log                                               
   */
  private static Logger log = Logger.getLogger("yzq.t9.core.act.action.T9FileUploadAct");
  
  public String doFileUpload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String rtUrl = "/core/inc/rtuploadfile.jsp";
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String uploadDir = fileForm.getParameter("uploadPath");
      T9Guid guidBuilder = new T9Guid();
      String relaPath = uploadDir + File.separator + guidBuilder.getRawGuid() + "." + fileForm.getFileExt();
      String filePath = (String)request.getAttribute(T9ActionKeys.ACT_CTX_PATH) + relaPath;
      fileForm.saveFile(filePath);
      
      request.setAttribute(T9ActionKeys.RET_DATA,
          "{actionFrom: \"upload\", fileNameServer: \"" + relaPath + "\"}");

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.FORWARD_PATH, rtUrl);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败" + ex.getMessage());
      throw ex;
    }
    return rtUrl;
  }
}
