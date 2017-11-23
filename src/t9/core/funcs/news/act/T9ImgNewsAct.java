package t9.core.funcs.news.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.news.data.T9ImgNews;
import t9.core.funcs.news.logic.T9FindNewaImageLogic;
import t9.core.funcs.news.util.T9ImageUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;


/**
 * 查找图片新闻用的类
 * @author qwx110
 *
 */
public class T9ImgNewsAct{
  private static String[] imageType = {"gif","JPG","jpg","jpeg","png","bmp","iff","jp2","jpx","jb2","jpc","xbm","wbmp"};
  public String getNews(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String newsId = request.getParameter("newsId");
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9FindNewaImageLogic logic = new T9FindNewaImageLogic();
      T9Person user =  (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ImgNews news = logic.getImageNews(newsId, dbConn , user);
      String attachmentId = news.getAttachmentId();
      String attachmentName= news.getAttachmentName();
      String[] attrName = null;
      String[] attrId = null;
      StringBuffer sb =  new StringBuffer();
      sb.append("{\"images\":[");
      int count = 0 ;
      
      if(!T9Utility.isNullorEmpty(attachmentName) 
          && !T9Utility.isNullorEmpty(attachmentId)){
        attrName = attachmentName.split("[*]");
        attrId = attachmentId.split(",");
        
        for(int i=0; i<attrId.length; i++){
          if(!T9Utility.isNullorEmpty(attrName[i]) 
              && isImageType(T9FileUtility.getFileExtName(attrName[i]))
              && !T9Utility.isNullorEmpty(attrId[i])){
            String bigPath = news.getPicPath(attrName[i], attrId[i]);
            File file = new File(bigPath);
            if (file.exists()) {
              String url = news.getSmallPicPath(attrName[i], attrId[i]);
              File smallImageFile = new File(url);
              if (!smallImageFile.exists()) {
                T9ImageUtility.saveImageAsJpg(bigPath,url, 200,160);
              }
              sb.append("{\"path\":\"" + T9Utility.encodeURL(bigPath) + "\"");
              sb.append(",\"id\":\"" + attrId[i] + "\"");
              sb.append(",\"smallPath\":\"" + T9Utility.encodeURL(url)+ "\"},");
              count++;
            }
          }
        }      
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      sb.append(",\"subject\":\"" + T9Utility.encodeSpecial(news.getSubject())+ "\"");
      String content = news.getContent();
      if (content == null) {
        content = "";
      }
      content = content.replaceAll("<P>", "");
      content = content.replaceAll("</P>", "");
      
      sb.append(",\"content\":\"" +T9Utility.encodeSpecial(content) + "\"");
      Timestamp newsTime = news.getNewsTime();
      SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
      String time = "";
      if (newsTime !=null) {
         time = sd.format(newsTime);
      }
      
      sb.append(",\"time\":\""+ time +"\"");
      sb.append(",\"address\":[\"\"]");
      sb.append(",\"org\":[\"\"]");
      sb.append(",\"names\":[\"\"]");
      sb.append(",\"nextTitle\":\""+ T9Utility.encodeSpecial(news.getNextTitle()) +"\"");
      sb.append(",\"nextId\":\""+ (news.getNextId() == 0 ? "" :news.getNextId()) +"\"");
      sb.append("}");
      
      ajax(sb.toString(), response);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getNewsList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user =  (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9FindNewaImageLogic logic = new T9FindNewaImageLogic();
      List<T9ImgNews> list = logic.findNewsAndPictures(dbConn, user);
      StringBuffer sb =  new StringBuffer();
      sb.append("{\"list\":[");
      int count = 0;
      for (T9ImgNews news : list) {
        String attId = news.getAttachmentId();
        String attName = news.getAttachmentName();
        if (attId == null || attName == null) {
          continue;
        }
        String subject = T9Utility.encodeSpecial(news.getSubject());
        int seqId = news.getSeqId();
        String[] attIds = attId.split(",");
        String[] attNames = attName.split("[*]");
        
        if (attIds.length > 0 && attNames.length > 0 ) {
          String aId = attIds[0];
          String aName = attNames[0];
          if(!T9Utility.isNullorEmpty(aName) 
              && isImageType(T9FileUtility.getFileExtName(aName))
              && !T9Utility.isNullorEmpty(aId)){
            String bigPath = news.getPicPath(aName, aId);
            File file = new File(bigPath);
            if (file.exists()) {
              String url = news.getSmallPicPath(aName, aId);
              File smallImageFile = new File(url);
              if (!smallImageFile.exists()) {
                T9ImageUtility.saveImageAsJpg(bigPath,url, 200,160);
              }
              sb.append("{\"id\":\"" + seqId + "\"");
              sb.append(",\"title\":\"" + subject + "\"");
              sb.append(",\"smallpath\":\"" + T9Utility.encodeURL(url)+ "\"},");
              count++;
            }
          }
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      ajax(sb.toString(), response);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public static boolean isImageType(String ext) {
    for (int i = 0;i < imageType.length ;i++ ) {
      String imageExt = imageType[i];
      if (imageExt.equals(ext)) {
        return true;
      }
    }
    return false;
  }
  /**
   * 桌面模块图片新闻
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String findNewsAndPicturesAjax(HttpServletRequest request, HttpServletResponse response) throws Exception, SQLException{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9FindNewaImageLogic imgLogic = new T9FindNewaImageLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER"); 
      List<T9ImgNews> imgNews = imgLogic.findNewsAndPictures(dbConn, user);
      String newsJsons = toJson(imgNews);
      //T9Out.println(newsJsons);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功");       
      request.setAttribute(T9ActionKeys.RET_DATA, newsJsons);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;   
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查找图片的绝对路径 
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String findNewsAndPicturesPathAjax(HttpServletRequest request, HttpServletResponse response)throws Exception, SQLException{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9FindNewaImageLogic imgLogic = new T9FindNewaImageLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER"); 
      List<T9ImgNews> imgNews = imgLogic.findNewsAndPictures(dbConn, user);
      String newsJsons = toJson2(imgNews);
      //T9Out.println(newsJsons);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功");       
      request.setAttribute(T9ActionKeys.RET_DATA, newsJsons);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;   
    }
    return "/core/inc/rtjson.jsp";
  }
  
/**
 * 把一个list转化为字符串 
 * @param imgNews
 * @return
 * @throws Exception 
 */
  public String toJson( List<T9ImgNews> imgNews) throws Exception {
    StringBuffer sb = new StringBuffer();
     sb.append("[");
       if(imgNews !=null && imgNews.size() >0){
         for(int i=0; i<imgNews.size(); i++){
          sb.append(imgNews.get(i).toJson());
          if(i < imgNews.size()-1){
            sb.append(",");
          }
         }
       }
     sb.append("]");
    
    return sb.toString();
  }
  
  /**
   * 把一个list转化为字符串 
   * @param imgNews
   * @return
   * @throws Exception 
   */
    public String toJson2( List<T9ImgNews> imgNews) throws Exception{
      StringBuffer sb = new StringBuffer();
       sb.append("[");
         if(imgNews !=null && imgNews.size() >0){
           for(int i=0; i<imgNews.size(); i++){
            sb.append(imgNews.get(i).toJson2());
            if(i < imgNews.size()-1){
              sb.append(",");
            }
           }
         }
       sb.append("]");
      
      return sb.toString();
    }
    
    /**
     * 最新的图片新闻模块
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public String leatImagNews(HttpServletRequest request, HttpServletResponse response)throws Exception, SQLException{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
      Connection dbConn = null;
      try{
        dbConn = requestDbConn.getSysDbConn();
        T9FindNewaImageLogic imgLogic = new T9FindNewaImageLogic();
        T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");         
        String imgJsons = imgLogic.findImgToJsonDesk(dbConn, user, request);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功");       
        request.setAttribute(T9ActionKeys.RET_DATA, imgJsons);
      } catch (Exception ex){
        String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
        throw ex;   
      }
      return "/core/inc/rtjson.jsp";
    }
    
    public static void ajax(String str, HttpServletResponse response) throws Exception{
      PrintWriter pw = response.getWriter();    
      String rtData = str;
      pw.println(rtData);    
      pw.flush();
    }
}
