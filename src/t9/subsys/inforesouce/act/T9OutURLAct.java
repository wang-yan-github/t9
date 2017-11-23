package t9.subsys.inforesouce.act;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.subsys.inforesouce.util.T9AjaxUtil;
import t9.subsys.inforesouce.util.T9OutURLUtil;
import t9.subsys.inforesouce.util.T9TempFileUtil;

public class T9OutURLAct{
  T9TempFileUtil fu =  T9TempFileUtil.getInstance();
  /**
   * 返回第一层tag图
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String firstLevelTag(HttpServletRequest request, HttpServletResponse response) throws Exception{   
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/GetFirstLevelTagList";
    String content = null; 
      try{
        content = T9OutURLUtil.getContent(url);       
        T9AjaxUtil.ajax(content, response);        
      } catch (Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e;      
      }  
    
    return null;
  }
  /**
   * 返回指定主题词的文档列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String articleListByKeyID(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String KeyID = request.getParameter("KeyID"); 
    int Page=0;
    int PageSize=8;
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/GetArticleListByKeyID?KeyID="+KeyID+"&nStartPage="+Page+"&nPageSize="+PageSize;
    String keyContent = null;  
    
      try{
        keyContent = T9OutURLUtil.getContent(url); 
        T9AjaxUtil.ajax(keyContent, response);
      }catch(Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e;
      }
    
    return null;
  }
  /**
   * 显示tag 云图的层次关系
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String Keyword(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String keyID = request.getParameter("keyID");     
    String basePath = T9SysProps.getString("signFileServiceUrl");
    if(!T9Utility.isNullorEmpty(keyID)){
      keyID = keyID.substring(0, keyID.lastIndexOf(",")==-1?keyID.length():keyID.lastIndexOf(","));
    }
    String url = basePath + "/TitleSign/GetKeyword?KeyIDs="+keyID;
    String keyContent = null;  
      try{
        keyContent = T9OutURLUtil.getContent(url); 
        T9AjaxUtil.ajax(keyContent, response);
      }catch(Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e;
      }
    
    return null;
  }
 
  
  /**
   * 回与指定主题词相关的文档列表(多个主题词KeyID之间用逗号隔开) 
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String  getArticleListByKeyIDs(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String keyID = request.getParameter("KeyIDs"); 
    String useRealData = T9SysProps.getString("useSignFileService");
    String startPage = request.getParameter("nStartPage");
    String pageSize =  request.getParameter("nPageSize");
    if(T9Utility.isNullorEmpty(pageSize)){
      pageSize = "8";
    }
    if(T9Utility.isNullorEmpty(startPage)){
      startPage = "0";
    }
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/GetArticleListByKeyIDs?KeyIDs="+keyID+"&nStartPage="+startPage + "&nPageSize="+pageSize;
    String files = null;
  
        try{
         // String files = null;     
          files = T9OutURLUtil.getContent(url);       
          T9AjaxUtil.ajax(files, response);
        } catch (Exception e){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
          throw e;
        }
    
    return null;
  }
  
  /**
   * 返回指定文档的相关文档列表 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRelationArticleList(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String fileId = request.getParameter("fileId");
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/GetRelationArticleList?FILE_ID=" + fileId;
    String files = null;
 
        try{
          files = T9OutURLUtil.getContent(url);
          T9AjaxUtil.ajax(files, response);
        } catch (Exception e){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
          throw e;
        }
    
    return null;
  }
  
  /**
   * 返回指定文档的主题词列表 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  getArticleTags(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String fileId = request.getParameter("fileId");
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/GetArticleTags?FILE_ID=" + fileId;
    try{
      String titles = null;
        titles = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(titles, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;   
  }
  
  /**
   * 返回全文检索结果列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFullTextDocList(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String startPage = request.getParameter("nStartPage");
    String pageSize =  request.getParameter("nPageSize");
    String q =  request.getParameter("q");
     q=T9Utility.decodeURL(q);
    if(T9Utility.isNullorEmpty(pageSize)){
      pageSize = "8";
    }
    if(T9Utility.isNullorEmpty(startPage)){
      startPage = "0";
    }
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/FullText/GetFullTextDocList?q="+ URLEncoder.encode(q, "UTF-8") + "&nStartPage="+startPage+"&nPageSize="+pageSize;
    try{
      long start = System.currentTimeMillis();
      String files = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(files, response);
      long end = System.currentTimeMillis();
      //T9Out.println("调用getFullTextDocList全文检索用时："+(end-start)+" ms");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }  
    return null;
  }
  public String getRelationWords(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String q =  request.getParameter("q");
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    String type = request.getParameter("type");
    if (T9Utility.isNullorEmpty(type)) {
      type = "0";
    }
    q=T9Utility.decodeURL(q);
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/FullText/GetRelationWords?q="+ URLEncoder.encode(q, "UTF-8") +"&type=" + type + "&start=" + start + "&end=" + end;
    try{
      String files = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(files, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }  
    return null;
  }
  public String getFullText(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String q = request.getParameter("q");
    String limit =  request.getParameter("limit");
    q=T9Utility.decodeURL(q);
    String basePath = T9SysProps.getString("signFileServiceUrl");
    
    String url = basePath + "/FullText/GetFullTextSuggest?q="+ URLEncoder.encode(q, "UTF-8") + "&limit="+limit;
    try{
      String files = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(files, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }  
    return null;
  }
  public String getColChartData(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String q = request.getParameter("q");
    String type =  request.getParameter("type");
    q=T9Utility.decodeURL(q);
    String basePath = T9SysProps.getString("signFileServiceUrl");
    
    String url = basePath + "/FullText/GetFullTextSuggest?q="+ URLEncoder.encode(q, "UTF-8") ;
    try{
      String files = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(files, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }  
    return null;
  }
  public String getLineChartData(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String q = request.getParameter("q");
    q=T9Utility.decodeURL(q);
    String basePath = T9SysProps.getString("signFileServiceUrl");
    
    String url = basePath + "/FullText/GetFullTextSuggest?q="+ URLEncoder.encode(q, "UTF-8") ;
    try{
      String files = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(files, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }  
    return null;
  }
  public String getDoc(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String fileId = request.getParameter("fileId");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String query = "select FILE_PATH from sign_files WHERE FILE_ID = '"+fileId+"'";
      Statement stm = null;
      ResultSet rs = null;
      String filePath = "";
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()) {
          filePath = rs.getString("FILE_PATH");
          filePath = filePath.replace("\\", "/");
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
      String name = T9FileUtility.getFileNameNoExt(filePath);
      request.setAttribute(T9ActionKeys.RET_DATA, "'"+filePath+"'");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, name);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 返回人名，地名，组织机构名, 关键词
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String titleSignFile(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String fileId = request.getParameter("attachmentId");
    String basePath = T9SysProps.getString("signFileServiceUrl");
    String url = basePath + "/TitleSign/TitleSignFile?FILE_ID=" + fileId;
    try{
      long start = System.currentTimeMillis();
      String title = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(title, response);
      long end = System.currentTimeMillis();
      //T9Out.println("返回人名，地名，组织机构名, 关键词 titleSignFile用时：" + (end-start) +" ms");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 返回热点人物
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("deprecation")
  public String  getHotPersonOfMonth(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String yearMonth = request.getParameter("yearmonth");
    String nmax = request.getParameter("nMax");
    if(T9Utility.isNullorEmpty(yearMonth)){
      Date dat = new Date();
      int year = dat.getYear() + 1900;
      int month = dat.getMonth() + 1;
      if(month < 10){
        yearMonth = year+"0"+month;
      }else{
        yearMonth = year+""+month;
      }
    }
    if(T9Utility.isNullorEmpty(nmax)){
      nmax = "6";
    }
    String baseUrl = T9SysProps.getString("signFileServiceUrl");
    String url = baseUrl + "/TagIt/GetHotPersonOfMonth?YearMonth=" + yearMonth + "&nMax=" + nmax;
    try{
      String title = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(title, response);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 返回热点地区
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  getHotAddressOfMonth(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String yearMonth = request.getParameter("yearmonth");
    String nmax = request.getParameter("nMax");
    if(T9Utility.isNullorEmpty(yearMonth)){
      Date dat = new Date();
      int year = dat.getYear() + 1900;
      int month = dat.getMonth() + 1;
      if(month < 10){
        yearMonth = year+"0"+month;
      }else{
        yearMonth = year+""+month;
      }
    }
    if(T9Utility.isNullorEmpty(nmax)){
      nmax = "6";
    }
    String baseUrl = T9SysProps.getString("signFileServiceUrl");
    String url = baseUrl + "/TagIt/GetHotAddressOfMonth?YearMonth=" + yearMonth + "&nMax=" + nmax;
    try{
      String title = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(title, response);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 返回热点组织机构
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  getHotOrganizationOfMonth(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String yearMonth = request.getParameter("yearmonth");
    String nmax = request.getParameter("nMax");
    if(T9Utility.isNullorEmpty(yearMonth)){
      Date dat = new Date();
      int year = dat.getYear() + 1900;
      int month = dat.getMonth() + 1;
      if(month < 10){
        yearMonth = year+"0"+month;
      }else{
        yearMonth = year+""+month;
      }
    }
    if(T9Utility.isNullorEmpty(nmax)){
      nmax = "6";
    }
    String baseUrl = T9SysProps.getString("signFileServiceUrl");
    String url = baseUrl + "/TagIt/GetHotOrganizationOfMonth?YearMonth=" + yearMonth + "&nMax=" + nmax;
    try{
      String title = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(title, response);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 返回热点组织主题词
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  getHotKeywordOfMonth(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String yearMonth = request.getParameter("yearmonth");
    String nmax = request.getParameter("nMax");
    if(T9Utility.isNullorEmpty(yearMonth)){
      Date dat = new Date();
      int year = dat.getYear() + 1900;
      int month = dat.getMonth() + 1;
      if(month < 10){
        yearMonth = year+"0"+month;
      }else{
        yearMonth = year+""+month;
      }
    }
    if(T9Utility.isNullorEmpty(nmax)){
      nmax = "6";
    }
    String baseUrl = T9SysProps.getString("signFileServiceUrl");
    String url = baseUrl + "/TagIt/GetHotKeywordOfMonth?YearMonth=" + yearMonth + "&nMax=" + nmax;
    try{
      String title = T9OutURLUtil.getContent(url);
      T9AjaxUtil.ajax(title, response);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String personTag(HttpServletRequest request, HttpServletResponse response)throws Exception{
    String dataStr = "{'name':['张三', '李四'], 'address':['北京','上海'], 'org':[]}";
    T9AjaxUtil.ajax(dataStr, response);
    return null;
  }
}
