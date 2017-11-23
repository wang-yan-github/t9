package t9.cms.content.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;

import t9.cms.area.logic.T9AreaLogic;
import t9.cms.bbs.documentinfo.logic.T9BbsDocumentLogic;
import t9.cms.content.data.T9CmsContent;
import t9.cms.content.logic.T9ContentLogic;
import t9.cms.setting.logic.T9FileUploadFormCms;
import t9.cms.station.data.T9CmsStation;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9ContentAct {

  /**
  * CMS栏目 添加
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsContent Content = (T9CmsContent)T9FOM.build(request.getParameterMap());
      T9ContentLogic logic = new T9ContentLogic();
      logic.addContent(dbConn, Content, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * CMS站点 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getContentList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int stationId = 0;
    int columnId = 0;
    String stationIdStr = (String)request.getParameter("stationId");
    if(!T9Utility.isNullorEmpty(stationIdStr)){
      stationId = Integer.parseInt(stationIdStr);
    }
    String columnIdStr = (String)request.getParameter("columnId");
    if(!T9Utility.isNullorEmpty(columnIdStr)){
      columnId = Integer.parseInt(columnIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ContentLogic logic = new T9ContentLogic();
      String data = logic.getContentList(dbConn, request.getParameterMap(), person, stationId, columnId);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getContentDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      StringBuffer data = logic.getContentDetailLogic(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * CMS文章 修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsContent column  = (T9CmsContent) T9FOM.build(request.getParameterMap()); 
      T9ContentLogic logic = new T9ContentLogic();
      logic.updateContent(dbConn, column);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除站点
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.deleteContent(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 签发
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toIssued(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.toIssued(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 签发
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.toNo(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 发布
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toRelease(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      int data = logic.toReleaseStart(dbConn, Integer.parseInt(seqId), true);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "发布失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 撤回
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toReceive(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.toReceive(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 置顶/取消置顶
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toTop(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String contentTop = request.getParameter("contentTop");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    if (T9Utility.isNullorEmpty(contentTop)) {
      contentTop = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.toTop(dbConn, Integer.parseInt(seqId), Integer.parseInt(contentTop));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 调序
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toSort(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String toSeqId = request.getParameter("toSeqId");
    String flag = request.getParameter("flag");
    String columnId = request.getParameter("columnId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    if (T9Utility.isNullorEmpty(toSeqId)) {
      toSeqId = "0";
    }
    if (T9Utility.isNullorEmpty(flag)) {
      flag = "0";
    }
    if (T9Utility.isNullorEmpty(columnId)) {
      columnId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ContentLogic logic = new T9ContentLogic();
      logic.toSort(dbConn, Integer.parseInt(seqId), Integer.parseInt(toSeqId), Integer.parseInt(flag), Integer.parseInt(columnId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String fileLoad(HttpServletRequest request, HttpServletResponse response) throws Exception{
	T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	Connection dbConn = requestDbConn.getSysDbConn();
    PrintWriter pw = null;
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    try {
      T9FileUploadFormCms fileForm = new T9FileUploadFormCms();
      fileForm.parseUploadRequest(request);
      T9ContentLogic logic = new T9ContentLogic();
      String stationId=request.getParameter("stationId");
      String contextPath = request.getContextPath();
      StringBuffer sb = logic.uploadMsrg2Json(dbConn,fileForm, contextPath,stationId);
      String data = "{'state':'0','data':" + sb.toString() + "}";
      pw = response.getWriter();
      pw.println(data.trim());
      pw.flush();
    }catch(Exception e){
      pw = response.getWriter();
      pw.println("{'state':'1'}".trim());
      pw.flush();
    } finally {
      pw.close();
    }
    return null;
  }
  
  /**
   * 单文件附件上传

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try{
	    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	    Connection dbConn = requestDbConn.getSysDbConn();
        T9FileUploadFormCms fileForm = new T9FileUploadFormCms();
//      fileForm.parseUploadRequest(request);
      T9AreaLogic logic2 = new T9AreaLogic();
      logic2.parseUploadRequest(request,dbConn,fileForm);
      Map<String, String> attr = null;
      String attrId = (fileForm.getParameter("attachmentId") == null ) ? "" : fileForm.getParameter("attachmentId");
      String attrName = (fileForm.getParameter("attachmentName") == null ) ? "" : fileForm.getParameter("attachmentName");
      String attachUrl = (fileForm.getParameter("attachUrl") == null ) ? "" : fileForm.getParameter("attachUrl");
      String stationId = (request.getParameter("stationId") == null ) ? "" : request.getParameter("stationId");
      String contextPath=request.getContextPath();
      String data = "";
      T9ContentLogic logic = new T9ContentLogic();
      attr = logic.fileUploadLogic(dbConn,fileForm, contextPath,stationId);
      T9ORM orm=new T9ORM();
	  T9CmsStation station =(T9CmsStation)orm.loadObjSingle(dbConn, T9CmsStation.class, Integer.parseInt(stationId));
	  String filePath = contextPath+ File.separator + station.getStationPath()+ File.separator +"attach"+ File.separator ;
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attachUrl.trim()).endsWith(",")){
        	  attachUrl += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        String hard=attrId.substring(0,4)+File.separator ;
        String realFileName=attrId.substring(5, attrId.length()-1)+"_"+value;
        attrName += value + "*";
        attachUrl +=filePath+hard+realFileName+",";
      }
      attachUrl = attachUrl.replace("\\", "/").replaceAll("\"", "\\\\\"");
      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\",attachUrl:\""+attachUrl+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch (SizeLimitExceededException ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      if((ex.getMessage()).startsWith("cms_")){
    	  request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      }else
      {
    	  request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败：文件需要小于" + T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) + "兆");
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  
  /**
   * 浮动菜单文件删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String stationId=request.getParameter("stationId");
    String sSeqId = request.getParameter("seqId");
    if (attachId == null) {
      attachId = "";
    }
    if (attachName == null) {
      attachName = "";
    }
    int seqId = 0 ;
    if (sSeqId != null && !"".equals(sSeqId)) {
      seqId = Integer.parseInt(sSeqId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();

      T9ContentLogic logic = new T9ContentLogic();
      boolean updateFlag = logic.delFloatFile(dbConn, attachId, attachName , seqId,stationId);
     
      String isDel="";
      if (updateFlag) {
        isDel ="isDel"; 
      }
      String data = "{updateFlag:\"" + isDel + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }
  
  public String statistics(HttpServletRequest request, HttpServletResponse response) throws Exception{
	  Connection dbConn=null;	  
	  String stationId=request.getParameter("stationId");
	  String columnId=request.getParameter("columnId");
	  try{
		   T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		  dbConn = requesttDbConn.getSysDbConn();
		  T9ContentLogic logic=new T9ContentLogic();
		  String data=logic.contentStatistics(dbConn,stationId,columnId);
		  
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success!!!!!");
	      request.setAttribute(T9ActionKeys.RET_DATA, "{\"data\":\""+data+"\"}");
	  }catch(Exception e){
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	  }
	  return "/core/inc/rtjson.jsp";
  }
  
  public String getStatistics(HttpServletRequest request, HttpServletResponse response) throws Exception{
	  Connection dbConn=null;	 
	  String stationId=request.getParameter("stationId");
	  String columnId=request.getParameter("columnId");
	  try{
		   T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		  dbConn = requesttDbConn.getSysDbConn();
		  T9ContentLogic logic=new T9ContentLogic();
		  String data=logic.contentStatistics(dbConn,stationId,columnId);
		  
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success!!!!!");
	      request.setAttribute(T9ActionKeys.RET_DATA, "{\"data\":\""+data+"\"}");
	  }catch(Exception e){
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	  }
	  return "/core/inc/rtjson.jsp";
  }
  /**
   * 添加收藏
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
	 public String addCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		    Connection dbConn = null;
		    String contentId = request.getParameter("contentId");
		    String contentUrl = request.getParameter("contentUrl");
		    try {
		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
		      dbConn = requestDbConn.getSysDbConn();
		      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		      T9ContentLogic logic = new T9ContentLogic();
		      String flag = logic.addCollection(dbConn,person, contentId,contentUrl);
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		      request.setAttribute(T9ActionKeys.RET_MSRG, "获取帖子列表成功");
		      request.setAttribute(T9ActionKeys.RET_DATA, "{flag:"+flag+"}");
		    } catch (Exception ex) {
		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
		      throw ex;
		    }
		    return "/cms/inc/rtrootjson.jsp";
	}
}
