package t9.subsys.inforesouce.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.util.T9Utility;
import t9.subsys.inforesouce.data.T9ImageManage;
import t9.subsys.inforesouce.data.T9Kengine;
import t9.subsys.inforesouce.logic.T9ManageImgLogic;
import t9.subsys.inforesouce.logic.T9MateTypeLogic;
import t9.subsys.inforesouce.util.T9FileMateConstUtil;
import t9.subsys.inforesouce.util.T9PageUtil;

/**
 * 图片管理中心<br>
 * 调用的类：T9ManageImgLogic, T9MateTypeLogic
 * @see t9.subsys.inforesouce.logic.T9ManageImgLogic
 * @see t9.subsys.inforesouce.logic.T9MateTypeLogic
 * @author qwx110
 * 
 */
public class T9ManageImgAct{
  
  /**
   * 通过分页查找所有的图片新闻<br>
   * 调用T9ManageImgLogic的findAllImage方法进行查询，返回当前页的图片新闻<br>
   * 通过ajax调用返回结果
   * @see t9.subsys.inforesouce.logic.T9ManageImgLogic#findAllImage(Connection, T9PageUtil, HttpServletRequest)
   * @see t9.subsys.inforesouce.util.T9PageUtil
   * @see t9.core.oaknow.util.T9AjaxUtil#ajax(String, HttpServletResponse)
   * @param request
   * @param response
   * @return null
   * @throws Exception
   */
  public String findAllImages(HttpServletRequest request, HttpServletResponse response)throws Exception{
    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9ManageImgLogic imgLogic = new T9ManageImgLogic();
      T9PageUtil page = new T9PageUtil();
      String crrNo = request.getParameter("currNo").trim();
      int imgAmount = imgLogic.findImageAmount(dbConn);
      if(T9Utility.isNullorEmpty(crrNo)){
        crrNo = "1";
      }
      page.setCurrentPage(Integer.parseInt(crrNo));
      page.setPageSize(3);
      page.setElementsCount(imgAmount); 
      List<T9ImageManage> imges = imgLogic.findAllImage(dbConn, page, request);
      String images = toJson(imges, page);     
      T9AjaxUtil.ajax(images, response);      
      request.setAttribute(T9ActionKeys.RET_DATA,  images);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return null;
  }
  
  /**
   * 把一个List转化为json格式
   * @param imges
   * @return
   * @throws Exception 
   */
  public String toJson( List<T9ImageManage> imges, T9PageUtil pu) throws Exception{
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    sb.append("data:[");
    if(imges != null && imges.size() >0){
      int size = imges.size();
      for(int i=0; i<size; i++){
        sb.append(imges.get(i).toJson());
        if(i < size-1){
          sb.append(",");
        }
      }
    }
    sb.append("]");
    if(pu != null){
      sb.append(",");
      sb.append("\"currNo\":").append(pu.getCurrentPage()).append(",");
      sb.append("\"totalNo\":").append(pu.getPagesCount()).append(",");
      sb.append("\"pageSize\":").append(pu.getPageSize());
    }    
    sb.append("}");
    //T9Out.println(sb.toString());
    return sb.toString();
  }
  
  /**
   * 查找文件的主题词，组织机构名，人名，地名等<br>
   * 调用T9MateTypeLogic的findName返回文件的主题词，组织机构名，人名，地名等的编号<br>
   * 调用T9ManageImgLogic的findMate返回主题词，组织机构名，人名，地名
   * @see t9.subsys.inforesouce.logic.T9MateTypeLogic#findName(Connection, String)
   * @see t9.subsys.inforesouce.logic.T9ManageImgLogic#findMate(Connection, String, String, String, String)
   * @param request
   * @param response
   * @return "/core/inc/rtjson.jsp"
   * @throws Exception
   */
  public String findAmate(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    
    Connection dbConn = null;
    String userName = null ;
    String areaName = null;
    String org = null;
    
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9MateTypeLogic logic = new T9MateTypeLogic();
      T9ManageImgLogic alogic = new T9ManageImgLogic();
      String newSeqId = request.getParameter("newSeqId");  
      userName = logic.findName(dbConn, T9FileMateConstUtil.userName);
      areaName = logic.findName(dbConn, T9FileMateConstUtil.areaName);
      org = logic.findName(dbConn, T9FileMateConstUtil.Org);
      T9Kengine ki = alogic.findMate(dbConn, newSeqId+"",userName,areaName,org);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回内容");
      request.setAttribute(T9ActionKeys.RET_DATA,  mate2Json(ki) );
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    } 
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 把T9Kengine 转化为json
   * @param engine
   * @return
   */
  public String mate2Json(T9Kengine engine){
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    if(engine != null){
       if(!T9Utility.isNullorEmpty(engine.getUserName())){
         sb.append("name:").append("\"").append(T9Utility.encodeSpecial(strSplit(engine.getUserName()))).append("\"").append(",");
       }else{
         sb.append("name:").append("''").append(",");
       }
       if(!T9Utility.isNullorEmpty(engine.getAreaName())){
         sb.append("area:").append("\"").append(T9Utility.encodeSpecial(strSplit(engine.getAreaName()))).append("\"").append(",");
       }else{
         sb.append("area:").append("''").append(",");
       }
       if(!T9Utility.isNullorEmpty(engine.getOrgName())){
         sb.append("orge:").append("\"").append(T9Utility.encodeSpecial(strSplit(engine.getOrgName()))).append("\"");
       }else{
         sb.append("orge:").append("''");
       }
    }
    sb.append("}");
    return sb.toString();
  }
  
  public String strSplit(String str){
    String[] fix = str.split(" ");
    int len = fix.length >2 ? 2 :fix.length;
    String nStr = "";
    for(int i=0; i<len; i++){
      nStr += fix[i] + " ";
    }
    return nStr;
  }
  
  /**
   * 返回最多10张图片（热点图片）<br>
   * 调用T9ManageImgLogic的findTenImage返回10篇图片新闻，生成json数据<br>
   * 调用ajax返回到前台
   * @see t9.subsys.inforesouce.logic.T9ManageImgLogic#findTenImage(Connection, HttpServletRequest)
   * @param request
   * @param response
   * @return null
   * @throws Exception
   */
  public String findTenImages(HttpServletRequest request, HttpServletResponse response)throws Exception{
    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9ManageImgLogic imgLogic = new T9ManageImgLogic();
    
      List<T9ImageManage> imges = imgLogic.findTenImage(dbConn, request);
      String images = toJson(imges, null);     
      T9AjaxUtil.ajax(images, response);      
      request.setAttribute(T9ActionKeys.RET_DATA,  images);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return null;
  }
}
