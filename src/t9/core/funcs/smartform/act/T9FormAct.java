package t9.core.funcs.smartform.act;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9ServletUtility;
import t9.core.util.file.T9FileUtility;

public class T9FormAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.smartform.act.T9FormAct");
  public String getForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String formHtmlPage = "";
    try {
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext()) ;
      
      String formTemplt = request.getParameter("formTemplt");
      String formDataPathStr = request.getParameter("formDataPath");
      
      
      int i = formTemplt.lastIndexOf("/");
      String fileName = formTemplt.substring(i + 1);
      String dataPath = contextRealPath + formDataPathStr + "/" + fileName;
      
      
      if(new File(dataPath).exists()){
        formHtmlPage = "/" + formDataPathStr + "/" + fileName;
      }else{
        formHtmlPage = "/" + formTemplt;
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }
    return formHtmlPage;
  }
  public String saveFormData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String formHtml = request.getParameter("html"); 
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext()) ;
      
      String formTemplt = request.getParameter("formTemplt");
      String formDataPathStr = request.getParameter("formDataPath");
      
      int i = formTemplt.lastIndexOf("/");
      String fileName = formTemplt.substring( i + 1);
      
      String dataPath = contextRealPath + formDataPathStr + "/" + fileName;
      T9FileUtility.storeString2File(dataPath, formHtml);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }finally {
      
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doSaveForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String path = request.getParameter("path");
      String formHtml = request.getParameter("content"); 
      String contextRealPath = T9ServletUtility.getWebAppDir(request.getSession().getServletContext()) ;
      Date date = new Date();
      SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
      String fileName = sf.format(date);
      
      String dataPath = contextRealPath + path + "/" + fileName + ".html";
      T9FileUtility.storeString2File(dataPath, formHtml);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }finally {
      
    }
    return "/core/inc/rtjson.jsp";
   
  }
}