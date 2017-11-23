package t9.core.funcs.workflow.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.workflow.logic.T9AttachmentLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9AttachmentAct {
  
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9AttachmentAct");
  /**
   * 上传图片处理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadImg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    try{
           
      T9FileUploadForm fileForm = new T9FileUploadForm();
      //注意这里的
      fileForm.parseUploadRequest(request);
      String imgFiles = fileForm.getParameter("imgFiles");
      String imgs = "[]";
      if (!T9Utility.isNullorEmpty(imgFiles)) {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
         imgs = logic.uploadImg(fileForm, imgFiles);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
      request.setAttribute(T9ActionKeys.RET_DATA, imgs);
    } catch (SizeLimitExceededException ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "上传文件的大小超出了系统允许上传文件的大小! ");
      return "/core/funcs/workflow/flowrun/list/inputform/rtimgfile.jsp";
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/workflow/flowrun/list/inputform/rtimgfile.jsp";
  }
  /**
   * 上传附件处理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    boolean isFeedAttach = false;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       
      T9FileUploadForm fileForm = new T9FileUploadForm();
      //注意这里的      fileForm.parseUploadRequest(request);
      String sIsFeedAttach = fileForm.getParameter("isFeedAttach");
      if (sIsFeedAttach != null && !"".equals(sIsFeedAttach)) {
        isFeedAttach = Boolean.valueOf(sIsFeedAttach);
      }
      int runId = Integer.parseInt(fileForm.getParameter("runId"));
      int flowId = Integer.parseInt(fileForm.getParameter("flowId"));
      
      if (isFeedAttach) {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        Map attach = logic.uploadAttachment(fileForm, dbConn);
        String data = "obj = {id:'"+(String)attach.get("id") +"' , name:'"+(String)attach.get("name")+"'};";
        request.setAttribute("fileForm", "ATTACHMENT1_fileForm");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      } else {
        boolean isEdit = false;
        String sIsEdit = fileForm.getParameter("isEdit");
        if (sIsEdit != null && !"".equals(sIsEdit)) {
          isEdit = Boolean.valueOf(sIsEdit);
        }
        if (!isEdit) {
          String sPrcsId = fileForm.getParameter("prcsId");
          int prcsId = Integer.parseInt(sPrcsId);
          T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
          //验证是否有权限,并取出权限字符串
          String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
          if ( "".equals(roleStr) && !isEdit) {//没有权限
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程办理权限，请与OA管理员联系");
            return "/core/inc/rtjson.jsp";
          }
        } 
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        logic.addAttachment(runId , fileForm, dbConn);
        request.setAttribute("fileForm", "fileForm");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
      }
    } catch (SizeLimitExceededException ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "上传文件的大小超出了系统允许上传文件的大小! ");
      return "/core/inc/rtuploadfile.jsp";
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  public String getAttachments(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser ,dbConn);
      if ( "".equals(roleStr) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"没有该流程办理权限，请与OA管理员联系");
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        String data = "[" + logic.getAttachments(loginUser, runId , flowId , dbConn) + "]";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      boolean isEdit = false;
      String sIsEdit = request.getParameter("isEdit");
      if (sIsEdit != null && !"".equals(sIsEdit)) {
        isEdit = Boolean.valueOf(sIsEdit);
      }
      if (!isEdit) {
        int prcsId = Integer.parseInt(request.getParameter("prcsId"));
        T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
        //验证是否有权限,并取出权限字符串
        String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
        if ( "".equals(roleStr) && !isEdit) {//没有权限
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程办理权限，请与OA管理员联系");
          return "/core/inc/rtjson.jsp";
        }
      } 
      T9AttachmentLogic logic = new  T9AttachmentLogic();
      logic.delAttachment(runId, attachmentId, attachmentName , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功 ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delFeedbackAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int feedId = Integer.parseInt(request.getParameter("feedId"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      T9AttachmentLogic logic = new  T9AttachmentLogic();
      logic.delFeedAttachment(feedId, attachmentId, attachmentName , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功 ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String createAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String newType = request.getParameter("newType");
      String newName = request.getParameter("newName");
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if ( "".equals(roleStr) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程办理权限，请与OA管理员联系");
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        String realPath = request.getRealPath("/");
        String attachment = logic.createAttachment(runId, newType, newName , dbConn, realPath);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功 ");
        request.setAttribute(T9ActionKeys.RET_DATA, attachment);
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String downAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String filePath = request.getParameter("filePath");
      String fileName = request.getParameter("fileName");
     // filePath = URLEncoder.encode(filePath, "UTF-8");
      filePath =  T9AttachmentLogic.filePath + File.separator + filePath;
      fileName = URLEncoder.encode(fileName, "UTF-8");
      
      response.setContentType("application/octet-stream");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
      InputStream in = null;
      OutputStream out = null;
      try {      
        in = new FileInputStream(filePath);
        out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = in.read(buff)) > 0) {        
           out.write(buff, 0, readLength);
        }
        out.flush();
      }catch(Exception ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (in != null) {
            in.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String restoreFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String attachName = request.getParameter("attachmentName");
      String attachDir = request.getParameter("attachmentDir");
      String diskId = request.getParameter("diskId");
      
      Map map = new HashMap();
      map.put(T9SelAttachUtil.ATTACH_DIR, attachDir);
      map.put(T9SelAttachUtil.ATTACH_NAME, attachName);
      map.put(T9SelAttachUtil.DISK_ID, diskId);
      boolean isFeedAttach = false;
      String sIsFeedAttach = request.getParameter("isFeedAttach");
      if (sIsFeedAttach != null && !"".equals(sIsFeedAttach)) {
        isFeedAttach = Boolean.valueOf(sIsFeedAttach);
      }
      if (isFeedAttach) {
        T9SelAttachUtil su = new T9SelAttachUtil(map , "workflow");
        String ids = su.getAttachIdToString("");
        String names = su.getAttachNameToString("");
        if (!ids.endsWith(","))  {
          ids += ",";
        }
        if (!names.endsWith("*")) {
          names += "*";
        }
        String data = "{id:'"+ ids +"' , name:'"+ T9Utility.encodeSpecial(names) +"'}";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      } else {
        int runId = Integer.parseInt(request.getParameter("runId"));
        int flowId = Integer.parseInt(request.getParameter("flowId"));
        boolean isEdit = false;
        String sIsEdit = request.getParameter("isEdit");
        if (sIsEdit != null && !"".equals(sIsEdit)) {
          isEdit = Boolean.valueOf(sIsEdit);
        }
        if (!isEdit) {
          int prcsId = Integer.parseInt(request.getParameter("prcsId"));
          T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
          //验证是否有权限,并取出权限字符串
          String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
          if ( "".equals(roleStr) && !isEdit) {//没有权限
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程办理权限，请与OA管理员联系");
            return "/core/inc/rtjson.jsp";
          }
        } 
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        
        T9SelAttachUtil su = new T9SelAttachUtil(map , "workflow");
        String ids = su.getAttachIdToString("");
        String names = su.getAttachNameToString("");
        logic.restoreFile(names, ids , runId, dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
