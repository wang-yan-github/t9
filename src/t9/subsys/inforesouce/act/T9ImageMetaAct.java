package t9.subsys.inforesouce.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.news.util.T9ImageUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;
import t9.subsys.inforesouce.data.T9SignFile;
import t9.subsys.inforesouce.db.T9MetaDbHelper;
import t9.subsys.inforesouce.logic.T9ImageMetaLogic;
import t9.subsys.inforesouce.logic.T9MateTypeLogic;
import t9.subsys.inforesouce.util.T9AjaxUtil;
import t9.subsys.inforesouce.util.T9FileMateConstUtil;
import t9.subsys.inforesouce.util.T9StringUtil;

/**
 * 图片浏览<br>
 * 调用的类有T9MetaDbHelper, T9MateTypeLogic, T9ImageMetaLogic
 * @see t9.subsys.inforesouce.db.T9MetaDbHelper
 * @see t9.subsys.inforesouce.logic.T9MateTypeLogic
 * @see t9.subsys.inforesouce.logic.T9ImageMetaLogic
 * @author lh
 *
 */
public class T9ImageMetaAct{
  public static String profix = System.getProperty("file.separator");
  /**
   * <fieldset>
   * <legend>查找图片列表</legend>
   * <p>
   * 先点左边得先看右边的树有没有选中的，如果有选中的则把值代过来<br>
   * 先点右边的树先看有没有点击左边的列表，如果点击了，则把值带过来,调用T9MateTypeLogic.findNumberId<br>
   * 返回人名，地名，组织机构名，主题词等元数据的编号, 调用T9MetaDbHelper.searchImageList返回与元数据相关的图片列表</p>
   * </fieldset>
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#findNumberId(Connection, String)
   * @see t9.subsys.inforesouce.db.T9MetaDbHelper#searchImageList(Connection, List, Map)
   * @param request
   * @param response
   * @return null
   * @throws Exception
   */
  public String findImageList(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      T9MetaDbHelper helper = new T9MetaDbHelper();
      List<String> moduleList = new ArrayList<String>();
      moduleList.add("news");
      //String modules = request.getParameter("modules");             //从左侧带过来的类型，如notify,news,mails等的标志
//      if(!T9Utility.isNullorEmpty(modules)){
//        String[] module = modules.split(",");
//        for(int i=0; i<module.length; i++){
//          if(!T9Utility.isNullorEmpty(module[i])){
//            moduleList.add(module[i]);
//          }
//        }
//      }
      String type = request.getParameter("type");
      T9MateTypeLogic mateLogic = new T9MateTypeLogic();
      String number = "";
      if ("address".equals(type)) {
        number = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.areaName);
      } else if ("org".equals(type)) {
        number =  mateLogic.findNumberId(dbConn, T9FileMateConstUtil.Org);
      } else if ("meta".equals(type)){
        number = request.getParameter("prop");
      } else {
        number = mateLogic.findNumberId(dbConn, T9FileMateConstUtil.userName);
      }
      String value = request.getParameter("value");                //从右侧带过来的值串，如：M12-M23-123,M23-asdfd,M43-,等的值
      String metas = number + "-" + value;
      metas = T9Utility.decodeURL(metas);
      Map<String, String> metaFilters = null;
      if(!T9Utility.isNullorEmpty(metas)){
        metaFilters = T9StringUtil.toMap(metas.trim());
      }
      
      List<T9SignFile> fileList = helper.searchImageList(dbConn, moduleList, metaFilters);   //图片列表       
      StringBuffer sb =  new StringBuffer();
      sb.append("{\"images\":[");
      int count = 0 ;
      for (T9SignFile signFile : fileList) {
        String bigPath = signFile.getFilePath();
        File file = new File(bigPath);
        if (file.exists()) {
          String url = getSmallPicPath(bigPath);
          File smallImageFile = new File(url);
          if (!smallImageFile.exists()) {
            T9ImageUtility.saveImageAsJpg(bigPath,url, 200,160);
          }
          sb.append("{\"path\":\"" + T9Utility.encodeURL(bigPath) + "\"");
          sb.append(",\"id\":\"" + signFile.getFileId() + "\"");
          sb.append(",\"smallPath\":\"" + T9Utility.encodeURL(url)+ "\"},");
          count++;
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      sb.append("}");
      T9AjaxUtil.ajax(sb.toString(), response);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 通过文件id返回图片的元数据
   * 调用T9ImageMetaLogic的getImageMeta方法返回图片的元数据
   * @see t9.subsys.inforesouce.logic.T9ImageMetaLogic#getImageMeta(Connection, String)
   * @param request
   * @param response
   * @return null
   * @throws Exception
   */
  public String getImageMeta(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      Connection dbConn = null;
      dbConn = requestDbConn.getSysDbConn();
      String fileId = request.getParameter("fileId");
      
      T9ImageMetaLogic logic = new T9ImageMetaLogic();
      String xml = logic.getImageMeta(dbConn, fileId);
      response.setContentType("text/xml");
      PrintWriter pw = response.getWriter();  
      pw.println(xml);    
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getSmallPicPath(String bigPath) {
    String path = T9FileUtility.getFilePath(bigPath);
    String fileName = T9FileUtility.getFileName(bigPath);
    path +=  profix + "smallPic" ;
    File file = new File(path);
    if (!file.exists()) {
      file.mkdir();
    }
    String smallPath = path  + profix + fileName; 
    return smallPath ; 
  }
  public static void main(String[] args) {
    try {
//      Connection conn = TestDbUtil.getConnection(false, "TD_OA2");
//      T9MetaDbHelper helper = new T9MetaDbHelper();
//      List<String> moduleList = new ArrayList();
//      moduleList.add("news");
//      Map<String , String> metaFilters = new HashMap();
//      metaFilters.put("MEX150", "广角");
//      List<T9SignFile> fileList = helper.searchImageList(conn, moduleList, metaFilters);   //图片列表  
//      System.out.println(fileList.size());
    } catch(Exception ex) {
      ex.printStackTrace();
    }
  }
}
