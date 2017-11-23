package t9.core.funcs.office.ntko.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.logic.T9DiaryLogic;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.office.ntko.data.T9NtkoCont;
import t9.core.funcs.office.ntko.data.T9NtkoStream;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.system.wordmoudel.logic.T9WordModelLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9NtkoAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.office.ntko.act.T9NtkoAct");
  /**
   * NTKO文件下载
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String upload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
//    request.setCharacterEncoding("UTF-8");
//    response.setCharacterEncoding("UTF-8");
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      //T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String directView = request.getParameter("directView");
      T9NtkoLogic nl = new T9NtkoLogic();
      T9NtkoStream ns = (T9NtkoStream) T9FOM.build(request.getParameterMap(),T9NtkoStream.class, null);
      is = ns.getFileStream();
      //nl.saveOcLog(conn, person.getSeqId(), request.getRemoteAddr(), ns.getAttachmentId(), ns.getAttachmentName(), 1);
      HashMap<String, String> contentTypeMap = (HashMap<String, String>) nl.getAttachHeard(ns.getAttachmentName(), directView);
      String contentType = contentTypeMap.get("contentType");
      String contentTypeDesc = contentTypeMap.get("contentTypeDesc");
      //设置html 头信息      String fileName = URLEncoder.encode(ns.getAttachmentName(),"UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      if(contentTypeDesc != null){
        response.setContentType(contentTypeDesc);
      }else {
        response.setContentType("application/octet-stream");
      }
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Accept-Length",String.valueOf(ns.getFileSize()));
      response.setHeader("Content-Length",String.valueOf(ns.getFileSize()));
      if("1".equals(contentType)){
        response.setHeader("Content-disposition","filename=" + fileName);
      } else {
        response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      }
      ops = response.getOutputStream();
      if(is != null){
        byte[] buff = new byte[8192];
        int byteread = 0;
        while( (byteread = is.read(buff)) != -1){
          ops.write(buff,0,byteread);
          ops.flush();
        }
      }
      //System.out.println(ns.toString());
      //System.out.println(response.getContentType());
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
  /**
   * 更新/保存文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9NtkoLogic nl = new T9NtkoLogic();
      String attachmentId = fileForm.getParameter("attachmentId");
      String attachmentName = fileForm.getParameter("attachmentName");
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String path = "";
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String module = fileForm.getParameter("moudle");
        if(attachmentName.trim().endsWith("*")){
          attachmentName = attachmentName.trim().substring(0,attachmentName.trim().length() - 1);
        }
        if(attachmentId.trim().endsWith(",")){
          attachmentId = attachmentId.trim().substring(0,attachmentId.trim().length() - 1);
        }
        if(attachmentId != null && !"".equals(attachmentId)){
          if(attachmentId.indexOf("_") > 0){
            String attIds[] = attachmentId.split("_");
            fileName = attIds[1] + "." + attachmentName;
            path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  attIds[0]  +File.separator+ fileName;
          }else{
            fileName = attachmentId + "." + attachmentName;
            path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  fileName;
          }
          File file = new File(path);
          if(!file.exists()){
            if(attachmentId.indexOf("_") > 0){
              String attIds[] = attachmentId.split("_");
              fileName = attIds[1] + "_" + attachmentName;
              path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  attIds[0]  +File.separator+  fileName;
            }else{
              fileName = attachmentId + "_" + attachmentName;
              path = T9NtkoCont.ATTA_PATH  +File.separator+ module +File.separator+ fileName;
            }
          }
        }
        fileForm.saveFile(fieldName, path);
        nl.saveOcLog(conn, person.getSeqId(),request.getRemoteAddr(), attachmentId, fileName, 2);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      
    }
    return null;
  }
  /**
   * 更新/保存文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    try {
      String path = "";
      String fileName = "";
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      String module = request.getParameter("moudle");
      if(attachmentName.trim().endsWith("*")){
        attachmentName = attachmentName.trim().substring(0,attachmentName.trim().length() - 1);
      }
      if(attachmentId.trim().endsWith(",")){
        attachmentId = attachmentId.trim().substring(0,attachmentId.trim().length() - 1);
      }
      if(attachmentId != null && !"".equals(attachmentId)){
        if(attachmentId.indexOf("_") > 0){
          String attIds[] = attachmentId.split("_");
          fileName = attIds[1] + "." + attachmentName;
          path = T9NtkoCont.ATTA_PATH  +File.separator+ module  +File.separator+  attIds[0]  +File.separator+ fileName;
        }else{
          fileName = attachmentId + "." + attachmentName;
          path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  fileName;
        }
        File file = new File(path);
        if(!file.exists()){
          if(attachmentId.indexOf("_") > 0){
            String attIds[] = attachmentId.split("_");
            fileName = attIds[1] + "_" + attachmentName;
            path = T9NtkoCont.ATTA_PATH  +File.separator+ module  +File.separator+  attIds[0] +File.separator+ fileName;
          }else{
            fileName = attachmentId + "_" + attachmentName;
            path = T9NtkoCont.ATTA_PATH +File.separator+  module  +File.separator+  fileName;
          }
        }
      }
      File file = new File(path);
      if(file.exists()){
        file.delete();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"附件删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得保留痕迹的系统设置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOcMarkSet(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NtkoLogic ntl = new T9NtkoLogic();
      String secOcMark = ntl.getLock(dbConn, "SEC_OC_MARK");
      String secOcMarkDefault = ntl.getLock(dbConn, "SEC_OC_MARK_DEFAULT");
      String secOcRevision = ntl.getLock(dbConn, "SEC_OC_REVISION");
      String data = "{secOcMark:\"" + secOcMark + "\""
        + ",secOcMarkDefault:\"" + secOcMarkDefault + "\""
        + ",secOcRevision:\"" + secOcRevision + "\""
        + "}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      //System.out.println(data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得保留痕迹的系统设置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String fileExists(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    try {
      int isExist = 0;
      String type = request.getParameter("type");
      File file = null;
      if ("2".equals(type)) {
        String attrPath = request.getParameter("path");
        file = new File(attrPath);
      } else {
        String fileName = "";
        String path = "";
        String attachmentId = request.getParameter("attachmentId");
        String attachmentName = request.getParameter("attachmentName");
        String module = request.getParameter("module");
        if (attachmentName.trim().endsWith("*")) {
          attachmentName = attachmentName.trim().substring(0,
              attachmentName.trim().length() - 1);
        }
        if (attachmentId.trim().endsWith(",")) {
          attachmentId = attachmentId.trim().substring(0,
              attachmentId.trim().length() - 1);
        }
        if (attachmentId != null && !"".equals(attachmentId)) {
          if (attachmentId.indexOf("_") > 0) {
            String attIds[] = attachmentId.split("_");
            fileName = attIds[1] + "." + attachmentName;
            path = T9NtkoCont.ATTA_PATH +File.separator+ module  +File.separator+  attIds[0]
                                                                                          +File.separator+ fileName;
          } else {
            fileName = attachmentId + "." + attachmentName;
            path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  fileName;
          }
          file = new File(path);
          if (!file.exists()) {
            if (attachmentId.indexOf("_") > 0) {
              String attIds[] = attachmentId.split("_");
              fileName = attIds[1] + "_" + attachmentName;
              path = T9NtkoCont.ATTA_PATH  +File.separator+  module  +File.separator+  attIds[0]
                                                                                              +File.separator+ fileName;
            } else {
              fileName = attachmentId + "_" + attachmentName;
              path = T9NtkoCont.ATTA_PATH  +File.separator+ module  +File.separator+  fileName;
            }
            file = new File(path);
          }
        }
      }
      if (file.exists()) {
        isExist = 1;
      }
      //System.out.println(isExist);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + isExist + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得套红模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getWordModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      conn = requestDbConn.getSysDbConn();
      T9NtkoLogic ntl = new T9NtkoLogic();
      String data = ntl.getWordModel(conn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 判断附件是否可编辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String isCanEdit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9NtkoLogic ntl = new T9NtkoLogic();
      String attachmentId = request.getParameter("attachmentId");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");

      String data = ntl.isCanEditLogic(conn, person.getSeqId(), attachmentId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 判断附件是否可编辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String lockRef(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9NtkoLogic ntl = new T9NtkoLogic();
      String attachmentId = request.getParameter("attachmentId");
      String op = request.getParameter("lockOp");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = ntl.lockRefLogic(conn, person.getSeqId(), attachmentId, op);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 得到附件日志信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOcLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9NtkoLogic ntl = new T9NtkoLogic();
      String data =  ntl.getLogList(conn, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String readPdf(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      //T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String directView = request.getParameter("directView");
      T9NtkoLogic nl = new T9NtkoLogic();
      T9NtkoStream ns = (T9NtkoStream) T9FOM.build(request.getParameterMap(),T9NtkoStream.class, null);
      is = ns.getFileStream();
      //nl.saveOcLog(conn, person.getSeqId(), request.getRemoteAddr(), ns.getAttachmentId(), ns.getAttachmentName(), 1);
      HashMap<String, String> contentTypeMap = (HashMap<String, String>) nl.getAttachHeard(ns.getAttachmentName(), directView);
      String contentType = contentTypeMap.get("contentType");
      String contentTypeDesc = contentTypeMap.get("contentTypeDesc");
      //设置html 头信息

      String fileName = URLEncoder.encode(ns.getAttachmentName(),"UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/pdf");
      response.setHeader("Content-type", "application/pdf");
      ops = response.getOutputStream();
      if(is != null){
        byte[] buff = new byte[8192];
        int byteread = 0;
        while( (byteread = is.read(buff)) != -1){
          ops.write(buff,0,byteread);
          ops.flush();
        }
      }
      //System.out.println(ns.toString());
      //System.out.println(response.getContentType());
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
  
  public String downFileFromPda(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
//    request.setCharacterEncoding("UTF-8");
//    response.setCharacterEncoding("UTF-8");
    String sessionid = request.getParameter("sessionid");
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      //T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
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
  public String downFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
//    request.setCharacterEncoding("UTF-8");
//    response.setCharacterEncoding("UTF-8");
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      //T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String directView = request.getParameter("directView");
      T9NtkoLogic nl = new T9NtkoLogic();
      T9NtkoStream ns = (T9NtkoStream) T9FOM.build(request.getParameterMap(),T9NtkoStream.class, null);
      is = ns.getFileStream();
      //nl.saveOcLog(conn, person.getSeqId(), request.getRemoteAddr(), ns.getAttachmentId(), ns.getAttachmentName(), 1);
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
      //System.out.println(ns.toString());
      //System.out.println(response.getContentType());
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
  public String downWord2Picture(HttpServletRequest request,
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
      is = ns.getFileStreamWord();
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
      throw ex;
    } finally {
      if (is != null) {
        is.close();
      }
    }
    return null;
  }
  
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String downFileByLocal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    try {
      String directView = request.getParameter("directView");
      String attrPath = request.getParameter("path");
      String fileName = request.getParameter("fileName");
      T9NtkoLogic nl = new T9NtkoLogic();
      HashMap<String, String> contentTypeMap = (HashMap<String, String>) nl.getAttachHeard(fileName, directView);
      String contentType = contentTypeMap.get("contentType");
      String contentTypeDesc = contentTypeMap.get("contentTypeDesc");
      fileName = URLEncoder.encode(fileName,"UTF-8");
      File file = new File(attrPath);
      long size = file.length();
      is =  new FileInputStream(file);
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
      response.setHeader("Accept-Length",String.valueOf(size));
      response.setHeader("Content-Length",String.valueOf(size));
      if("1".equals(contentType)){
        response.setHeader("Content-disposition","filename=" + fileName);
      } else {
        response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      }
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
      throw ex;
    } finally {
      if (is != null) {
        is.close();
      }
    }
    return null;
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String downFileByLocal2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      //T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String directView = request.getParameter("directView");
      String attrPath = request.getParameter("path");
      String fileName = request.getParameter("fileName");
      T9NtkoLogic nl = new T9NtkoLogic();
//      System.out.println(new String(fileName.getBytes(),"UTF-8"));
      fileName = URLEncoder.encode(fileName,"UTF-8");
      File file = new File(attrPath);
      long size = file.length();
      is =  new FileInputStream(file);
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/octet-stream");
      response.setHeader("Accept-Ranges","bytes");
      
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Accept-Length",String.valueOf(size));
      response.setHeader("Content-Length",String.valueOf(size));
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
  /**
   * 更新/保存文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateFileByLocal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    Connection conn = null;
    try {
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9NtkoLogic nl = new T9NtkoLogic();
      String attrPath = fileForm.getParameter("path");
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        fileForm.saveFile(attrPath);
      }
    } catch (Exception ex) {
      throw ex;
    }
    return null;
  }
  public String batchDownload(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    try {
      String attachmentName = request.getParameter("attachmentName");
      String attachmentId = request.getParameter("attachmentId");
      String module = request.getParameter("module");
      String name = request.getParameter("name");
      if(name == null || "".equals(name)){
        name = "附件打包下载";
      }
      String fileName = URLEncoder.encode( name + ".zip","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9NtkoLogic nl = new T9NtkoLogic();
      Map<String, String> map = nl.toZipInfoMap(attachmentName, attachmentId, module);
      nl.zip(map, ops);
      ops.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
   }
  public String batchDownloadByLocal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    try {
      String attrPath = request.getParameter("paths");
      String fileNames = request.getParameter("fileNames");
      String name = request.getParameter("name");
      if(name == null || "".equals(name)){
        name = "附件打包下载";
      }
      String fileName = URLEncoder.encode( name + ".zip","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9NtkoLogic nl = new T9NtkoLogic();
      Map<String, String> map = nl.toZipInfoMap(fileNames, attrPath);
      nl.zip(map, ops);
      ops.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
   }
  }
