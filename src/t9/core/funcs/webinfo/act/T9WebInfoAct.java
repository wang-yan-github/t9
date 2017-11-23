package t9.core.funcs.webinfo.act;


import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.funcs.webinfo.dto.T9WebInfo;
import t9.core.funcs.webinfo.dto.T9WebInfoAttachment;
import t9.core.funcs.webinfo.file.T9WebInfoFileOperate;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;

public class T9WebInfoAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.webinfo.act.T9WebInfoAct");
  public String doSave(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9WebInfo webInfo = new T9WebInfo();
      
      webInfo.setWebInfoDate(request.getParameter("webInfoDate"));
      webInfo.setWebInfoKeyWord(request.getParameter("webInfoKeyWord"));
      webInfo.setWebInfoTitle(request.getParameter("webInfoTitle"));
      webInfo.setContent(request.getParameter("content"));
      webInfo.setWebInfoUser(request.getParameter("webInfoUser"));
      String countStr = request.getParameter("fileCount");
      
      if(countStr == null|| "".equals(countStr)){
        countStr = "0";
      }
      int count = Integer.parseInt(countStr);
      if(count != 0){
        for(int i = 1 ;i <= count ; i++){
          T9WebInfoAttachment a = new T9WebInfoAttachment();
          String fileName = request.getParameter("fileName-"+i);
          String filePath = request.getParameter("filePath-"+i);
          a.setName(fileName);
          a.setFilePath(filePath);
          webInfo.addAttachment(a);
        }
      }
      String baseFilePath = request.getParameter("baseFilePath");
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext()) ;
      String basePath = contextRealPath + baseFilePath ;
      T9WebInfoFileOperate.addWebInfoFile(webInfo, basePath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getWebInfoList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String listLength = request.getParameter("listLength");
      String indexStr = request.getParameter("pageIndex");
      String searchStr = request.getParameter("searchStr");
     
      if(searchStr==null){
        searchStr = "";
      }
      int pageIndex;
      if(indexStr == null || "".equals(indexStr)){
        pageIndex = 0;
      }else{
        pageIndex = Integer.parseInt(indexStr);
      }
      if(pageIndex<0){
        pageIndex = 0;
      }
     
      int listLengthInt;
      if(listLength == null || "".equals(listLength)){
        listLengthInt = 10;
      }else{
        listLengthInt = Integer.parseInt(listLength);
      }
      String baseFilePath = request.getParameter("baseFilePath");
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext()) ;
      String basePath = contextRealPath + baseFilePath ;
      List<T9WebInfo> list = T9WebInfoFileOperate.getWebInfoList(basePath,searchStr);
      int webInfoCount = list.size();
      int remainder = webInfoCount%listLengthInt;
      int pageCount = (remainder==0)?webInfoCount/listLengthInt:webInfoCount/listLengthInt+1;
     
      if(pageIndex > pageCount){
        pageIndex = pageCount ;
      }
     
      int index = pageIndex*listLengthInt;
      int max;
     
      if(pageCount-1 == pageIndex&&remainder != 0){
        max = webInfoCount;
      }else{
        max = index+listLengthInt;
      }
      if(max > webInfoCount ){
        max = webInfoCount;
      }
    
      List<T9WebInfo> list2 = list.subList(index, max);
      StringBuffer sb = new StringBuffer("{\"pageCount\":" +pageCount +
              ",\"pageIndex\":" + pageIndex +
                   ",\"searchStr\":\"" +  searchStr +
                  "\",\"webInfoCount\":" + webInfoCount +
                  ",\"listLength\":" + listLengthInt );
      if(list2 != null ){
        sb.append(",\"list\":[");
        for(T9WebInfo webInfo : list2){
          sb.append(webInfo.toJSON(true));
          sb.append(",");
        }
        if(list2.size() > 0)
          sb.deleteCharAt(sb.length()-1);
        sb.append("]");
      }
      sb.append("}");
  
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      response.setHeader("Cache-Control", "no-cache");  
      PrintWriter out = response.getWriter();
      out.print(sb.toString());
      out.flush();
      out.close();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getWebInfoByName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try{
      String baseFilePath = request.getParameter("baseFilePath");
      String isAll = request.getParameter("isAllContent");
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
      String name = request.getParameter("name");
      String basePath = contextRealPath + baseFilePath ;
      T9WebInfo webInfo = T9WebInfoFileOperate.getWebInfoByName(basePath,name);
     
      response.setCharacterEncoding("UTF-8");
      response.setContentType("application/json");
      response.setHeader("Cache-Control", "no-cache");  
      PrintWriter out = response.getWriter();
     
        out.print(webInfo.toJSON(true).toString());
     
      out.flush();
      out.close();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String doUpdate(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String baseFilePath = request.getParameter("baseFilePath");
      T9WebInfo webInfo = new  T9WebInfo();
      
      webInfo.setWebInfoDate(request.getParameter("webInfoDate"));
      webInfo.setWebInfoKeyWord(request.getParameter("webInfoKeyWord"));
      webInfo.setWebInfoTitle(request.getParameter("webInfoTitle"));
      webInfo.setContent(request.getParameter("content"));
      webInfo.setWebInfoUser(request.getParameter("webInfoUser"));
      webInfo.setFileName(request.getParameter("fileName"));
      String countStr = request.getParameter("fileCount");
      
      if(countStr == null|| "".equals(countStr)){
        countStr = "0";
      }
      int count = Integer.parseInt(countStr);
      if(count != 0){
        for(int i = 1 ;i <= count ; i++){
          T9WebInfoAttachment a = new T9WebInfoAttachment();
          String fileName = request.getParameter("fileName-"+i);
          String filePath = request.getParameter("filePath-"+i);
          a.setName(fileName);
          a.setFilePath(filePath);
          webInfo.addAttachment(a);
        }
      }
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
      String basePath = contextRealPath + baseFilePath ;
      T9WebInfoFileOperate.updateWebInfoFile(webInfo, basePath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doDelete(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String baseFilePath = request.getParameter("baseFilePath");
      String name = request.getParameter("name");
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
      String basePath = contextRealPath + baseFilePath ;
      T9WebInfoFileOperate.deleteWebInfoFile(basePath,name);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
 public String doUploadImage(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   try{
     String baseFilePath = request.getParameter("baseFilePath");
     T9FileUploadForm fileForm = new T9FileUploadForm();
     fileForm.parseUploadRequest(request);
     Date date = new Date();
     SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
     String fileName = sf.format(date) + fileForm.getFileName();
     String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
     String basePath = contextRealPath + baseFilePath ;
     
     String filePath = basePath + T9WebInfoFileOperate.IMAGEPATH + "/" + fileName;
     fileForm.saveFile(filePath);
     String requestPath = request.getContextPath() + "/" + baseFilePath + T9WebInfoFileOperate.IMAGEPATH + "/" + fileName;
     
    
     response.setCharacterEncoding("UTF-8");
     response.setContentType("text/html");
     response.setHeader("Cache-Control", "no-cache");  
     PrintWriter out = response.getWriter();
     out.print("<body onload=\"alert('ddd');window.parent.OnUploadCompleted(0, '" + requestPath + "', '" + fileName + "', 'success' )\"/>");
     out.flush();
     out.close();
     
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return null;
 }
 public String doUploadFile(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   try{
   
     T9FileUploadForm fileForm = new T9FileUploadForm();
     
     fileForm.parseUploadRequest(request);
     String baseFilePath = fileForm.getParameter("baseFilePath");
     Date date = new Date();
     SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
     //System.out.print(fileForm.getFileName());
     String fileName = sf.format(date) + fileForm.getFileName();;
     String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
     String filePath = contextRealPath  + baseFilePath + T9WebInfoFileOperate.ATTACHMENTPATH + "/" + fileName;

     fileForm.saveFile(filePath);
     String basePath = contextRealPath + baseFilePath ;
     String requestPath = request.getContextPath() + "/" + basePath + T9WebInfoFileOperate.ATTACHMENTPATH + "/" + fileName;
     T9WebInfoAttachment attachment = new T9WebInfoAttachment();
     attachment.setFilePath(fileName);
     attachment.setName(fileForm.getFileName());
     
     response.setCharacterEncoding("UTF-8");
     response.setContentType("text/html");
     response.setHeader("Cache-Control", "no-cache");  
     PrintWriter out = response.getWriter();
     out.print("<body onload=\"window.parent.addAttachment('" + fileForm.getFileName() + "','" + fileName + "')\"/>");
     out.flush();
     out.close();
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return null;
   //return "/raw/lh/return.jsp";
 }
 public String doUpdateDoc(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   try{
     //String baseFilePath = request.getParameter("baseFilePath");
     T9FileUploadForm fileForm = new T9FileUploadForm();
     fileForm.parseUploadRequest(request);
     String baseFilePath = fileForm.getParameter("baseFilePath");
     String fileName = fileForm.getFileName();
     //fileName = T9Utility.transferCode(fileName, "ISO-8859-1", "UTF-8");
     String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
     //System.out.println(baseFilePath);
     String filePath = contextRealPath  + baseFilePath + T9WebInfoFileOperate.ATTACHMENTPATH + "/" + fileName;
     
     fileForm.saveFile(filePath);
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return null;
 }
 public String deleteAttachment(HttpServletRequest request,
     HttpServletResponse response) throws Exception{
   try{
     String baseFilePath = request.getParameter("baseFilePath");
     String filePath = request.getParameter("attachmentPath");
     if(filePath == null||"".equals(filePath)){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, "filePath 没有值");
       return "/core/inc/rtjson.jsp";
     }
     String fileName = request.getParameter("fileName");
     String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext());
     String basePath = contextRealPath + baseFilePath ;
     if(fileName != null && !"".equals(fileName)){
       T9WebInfoFileOperate.deleteFileAttachment(basePath, filePath , fileName);
     }
     T9WebInfoFileOperate.deleteAttachment(basePath, filePath );
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/inc/rtjson.jsp";
 }

}
