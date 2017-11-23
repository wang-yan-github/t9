package t9.cms.area.act;


import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;

import t9.cms.area.data.T9CmsArea;
import t9.cms.area.logic.T9AreaLogic;
import t9.cms.column.data.T9CmsColumn;
import t9.cms.content.logic.T9ContentLogic;
import t9.cms.setting.logic.T9FileUploadFormCms;
import t9.cms.template.data.T9CmsTemplate;
import t9.cms.template.logic.T9TemplateLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;





public class T9AreaAct{
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
		      T9FileUploadForm fileForm = new T9FileUploadForm();
		      fileForm.parseUploadRequest(request);
		      T9AreaLogic logic = new T9AreaLogic();
		      StringBuffer sb = logic.uploadMsrg2Json(dbConn,fileForm);
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
   * 获取区域模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getAreaTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String stationId = request.getParameter("stationId");
	    if (T9Utility.isNullorEmpty(stationId)) {
	      stationId = "0";
	    }
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9AreaLogic logic = new T9AreaLogic();
	      String data = logic.getAreaTemplate(dbConn, stationId);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
  
  /**
   *增加区域
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    String contexPath = request.getContextPath();
	    try {
		  T9FileUploadForm fileForm = new T9FileUploadForm();
		  fileForm.parseUploadRequest(request);
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	      T9CmsArea area = (T9CmsArea)T9FOM.build(fileForm.getParamMap(), T9CmsArea.class, null);
	      T9AreaLogic logic = new T9AreaLogic();
	      logic.addArea(dbConn, area, person,fileForm);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    response.sendRedirect(contexPath + "/cms/area/manage.jsp");
	    return null;
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
    String sSeqId = request.getParameter("seqId");
    //T9Out.println(sSeqId);
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

      T9AreaLogic Logic = new T9AreaLogic();

      boolean updateFlag = Logic.delFloatFile(dbConn, attachId, attachName , seqId);
     
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
  /**
   * 获取区域列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAreaList(HttpServletRequest request, HttpServletResponse response) throws Exception{
	  Connection dbConn = null;
	  try {
	      String stationId = request.getParameter("stationId");
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      T9AreaLogic logic = new T9AreaLogic();
	      String data = logic.getAreaList(dbConn, request.getParameterMap(), person, stationId);
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
   * 
   * 删除区域
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String seqIdStr = request.getParameter("seqId");
	    String stationId=request.getParameter("stationId");
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9AreaLogic logic = new T9AreaLogic();
	      logic.deleteAreaLogic(dbConn, seqIdStr,stationId);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	    } catch (Exception e) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
	      throw e;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
	  
  /**
   * 获取区域详情
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAreaDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String seqId = request.getParameter("seqId");
	    String stationId=request.getParameter("stationId");
	    if (T9Utility.isNullorEmpty(seqId)) {
	      seqId = "0";
	    }
	    String flag = request.getParameter("flag");
	    if (T9Utility.isNullorEmpty(flag)) {
	      flag = "0";
	    }
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9AreaLogic logic = new T9AreaLogic();
	      StringBuffer data = logic.getAreaDetailLogic(dbConn, Integer.parseInt(seqId), Integer.parseInt(flag),stationId);
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
   * 更新区域信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    T9FileUploadForm fileForm = new T9FileUploadForm();
	    fileForm.parseUploadRequest(request);
	    Connection dbConn = null;
	    String contexPath = request.getContextPath();
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9CmsArea area  = (T9CmsArea) T9FOM.build(fileForm.getParamMap(), T9CmsArea.class, null); 
	      T9AreaLogic logic = new T9AreaLogic();
	      logic.updateArea(dbConn, area, fileForm);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    response.sendRedirect(contexPath + "/cms/area/manage.jsp");
	    return null;
	  }
	  /**
	   * 获取当前站点下的所有栏目
	   * @param request
	   * @param response
	   * @return
	   * @throws Exception
	   */
  public String getColumns(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    String stationId = request.getParameter("stationId");
	    String parentId=request.getParameter("columnId");
	    Connection dbConn = null;
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9AreaLogic logic = new T9AreaLogic();
	      List<T9CmsColumn> data= logic.getColumnList(dbConn,stationId);
	      StringBuffer sb = new StringBuffer();
	      String nbsp = "├";
	      boolean hasChild=false;
	      for (T9CmsColumn ca : data) {
	    	 hasChild=logic.isHasChild(dbConn,ca.getSeqId());
	        String str = "{";
	        str += "columnId:" + ca.getSeqId() + ",";  
	        str += "columnName:\"" + nbsp+T9Utility.encodeSpecial(ca.getColumnName()) + "\","; 
	        str += "hasChild:" + hasChild;  
	        str += "},";
	        sb.append(str);
	      }
	      if (data.size() > 0) {
	        sb.deleteCharAt(sb.length() - 1);
	      }
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
	      request.setAttribute(T9ActionKeys.RET_DATA, "[" + sb.toString() + "]");
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
  
  /**
   * 获取子栏目
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
       public String getChildColumn(HttpServletRequest request, HttpServletResponse response) throws Exception{
    	   int parentId=Integer.parseInt(request.getParameter("columnId"));
    	   int space=Integer.parseInt(request.getParameter("space"));
    	   T9AreaLogic logic = new T9AreaLogic();
    	   Connection dbConn = null;
    	   try{
    		      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    		      dbConn = requestDbConn.getSysDbConn();
    		      String data=logic.getChildColumn(dbConn, parentId,space);
    		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    		      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
    		      request.setAttribute(T9ActionKeys.RET_DATA, data);
    	   }catch (Exception ex) {
    		      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    		      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
    		      throw ex;
    		    }
    		    return "/core/inc/rtjson.jsp";
    	 
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
//           fileForm.parseUploadRequest(request);
           T9AreaLogic logic = new T9AreaLogic();
           logic.parseUploadRequest(request,dbConn,fileForm);
           Map<String, String> attr = null;
           String attrId = (fileForm.getParameter("attachmentId") == null ) ? "" : fileForm.getParameter("attachmentId");
           String attrName = (fileForm.getParameter("attachmentName") == null ) ? "" : fileForm.getParameter("attachmentName");
           String data = "";
           attr = logic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
           Set<String> keys = attr.keySet();
           for (String key : keys){
             String value = attr.get(key);
             if(attrId != null && !"".equals(attrId)){
               if(!(attrId.trim()).endsWith(",")){
                 attrId += ",";
               }
               if(!(attrName.trim()).endsWith("*")){
                 attrName += "*";
               }
             }
             attrId += key + ",";
             attrName += value + "*";
           }
           data = "{attachmentId:\"" + attrId + "\",attachmentName:\"" + attrName + "\"}";
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
}