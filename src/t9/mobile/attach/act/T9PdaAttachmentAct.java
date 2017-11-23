package t9.mobile.attach.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.office.ntko.data.T9NtkoStream;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.mobile.attach.logic.T9PdaAttachmentLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaAttachmentAct {


/**
   * 上传文件
   * @param request
       <form method="post" action="http://localhost:8080/t9/t9/mobile/attach/act/T9PdaAttachmentAct/upload.act" enctype="multipart/form-data">  
         <input type="file" name="file"/>  
         <input type="hidden" name="moudle" value="notify"/>
         <input type="submit"/>  
       </form>  
   * @param response
       attachment_id      附件ID
       attachment_Name    附件名
   * @return
       JSON
   * @throws Exception
   */
  public String upload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    Connection conn = null;
    String pathPx = "";
    
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      pathPx = T9SysProps.getAttachPath();
      String module = fileForm.getParameter("moudle");
      
      T9PdaAttachmentLogic logic = new T9PdaAttachmentLogic();
      Map result = logic.fileUploadLogic(request,fileForm, pathPx,module);
      
      T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "", T9MobileUtility.mapToJson(result)));
    } catch (Exception ex) {
      throw ex;
    } finally {
      
    }
    return null;
  }

  /**
   * 下载文件
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
public String downFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String directView = request.getParameter("directView");
      T9NtkoLogic nl = new T9NtkoLogic();
      T9NtkoStream ns = (T9NtkoStream) T9FOM.build(request.getParameterMap(),T9NtkoStream.class, null);
      is = ns.getFileStream();
      HashMap<String, String> contentTypeMap = (HashMap<String, String>) nl.getAttachHeard(ns.getAttachmentName(), directView);
      String contentType = contentTypeMap.get("contentType");
      String contentTypeDesc = contentTypeMap.get("contentTypeDesc");
      //设置html 头信息
      String fileName = URLEncoder.encode(ns.getAttachmentName(),"UTF-8");
      if (fileName.length() > 150) {
        fileName =new String(ns.getAttachmentName().getBytes("GB2312") , "ISO-8859-1");
      }
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      if(contentTypeDesc != null){
        response.setContentType(contentTypeDesc);
      }else {
        response.setContentType("application/octet-stream");
      }
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Length",String.valueOf(ns.getFileSize()));
      response.setHeader("Content-Length",String.valueOf(ns.getFileSize()));
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      if(is != null){
        byte[] buff = new byte[8192];
        int byteread = 0;
        while( (byteread = is.read(buff)) != -1){
          ops.write(buff,0,byteread);
          ops.flush();
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      if (is != null) {
        is.close();
      }
    }
    return null;
  }
  
}
