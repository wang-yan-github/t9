package t9.cms.area.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;

import t9.cms.area.data.T9CmsArea;
import t9.cms.column.data.T9CmsColumn;
import t9.cms.column.logic.T9ColumnLogic;
import t9.cms.common.logic.T9CmsCommonLogic;
import t9.cms.content.data.T9CmsContent;
import t9.cms.setting.data.T9SysPara;
import t9.cms.setting.logic.T9FileUploadFormCms;
import t9.cms.setting.logic.T9JhSysParaLogic;
import t9.cms.station.data.T9CmsStation;
import t9.cms.station.logic.T9StationLogic;
import t9.cms.template.data.T9CmsTemplate;
import t9.cms.velocity.T9velocityUtil;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.office.ntko.data.T9NtkoCont;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.user.api.core.db.T9DbconnWrap;


public class T9AreaLogic {
  
	  public static final String attachmentFolder = "cms";
	  public static String filePath = T9SysProps.getAttachPath() + File.separator + "cms";
	  public static String sysPara="IS_CTRLATTACH_CMS";
	  
  /**
   * CMS动态浏览次数获取
   * 
   * @param String
   * @return String
   * @throws Exception
   */
  public static String getBrowseCount(String contentSeqId) throws Exception{
    Connection dbConn = null;
    String count = "0";
    int seqId = 0;
    if(T9Utility.isInteger(contentSeqId)){
      seqId = Integer.parseInt(contentSeqId);
    }
    try{
      T9DbconnWrap dbUtil = new T9DbconnWrap();
      dbConn = dbUtil.getSysDbConn();
      
      T9ORM orm = new T9ORM();
      T9CmsContent cmsContent = (T9CmsContent)orm.loadObjSingle(dbConn, T9CmsContent.class, seqId);
      count = cmsContent.getBrowseCount()+"";
      
    }catch(Exception e){
      e.printStackTrace();
      throw(e);
    }
    return count;
  }
  
  /**
   * CMS动态浏览次数统计
   * 
   * @param String
   * @return String
   * @throws Exception
   */
  public static String setBrowseCount(String contentSeqId) throws Exception{
    
    Connection dbConn = null;
    String count = "0";
    int seqId = 0;
    if(T9Utility.isInteger(contentSeqId)){
      seqId = Integer.parseInt(contentSeqId);
    }
    try{
      T9DbconnWrap dbUtil = new T9DbconnWrap();
      dbConn = dbUtil.getSysDbConn();
      
      T9ORM orm = new T9ORM();
      T9CmsContent cmsContent = (T9CmsContent)orm.loadObjSingle(dbConn, T9CmsContent.class, seqId);
      cmsContent.setBrowseCount(cmsContent.getBrowseCount()+1);
      count = cmsContent.getBrowseCount()+"";
      orm.updateSingle(dbConn, cmsContent);
    }catch(Exception e){
      e.printStackTrace();
      throw(e);
    }
    return count;
  }
  /**
   * 附件批量上传页面处理
   * 
   * @return
   * @throws Exception
   */
  public StringBuffer uploadMsrg2Json(Connection dbConn,T9FileUploadForm fileForm)
      throws Exception {
    StringBuffer sb = new StringBuffer();
    Map<String, String> attr = null;
    String attachmentId = "";
    String attachmentName = "";
    try {
      attr = this.fileUploadLogic(fileForm);
      Set<String> attrKeys = attr.keySet();
      for (String key : attrKeys) {
        String fileName = attr.get(key);
        attachmentId += key + ",";
        attachmentName += fileName + "*";
      }
      long size = this.getSize(attr, attachmentFolder);
      sb.append("{");
      sb.append("'attachmentId':").append("\"").append(attachmentId).append("\",");
      sb.append("'attachmentName':").append("\"").append(attachmentName).append("\",");
      sb.append("'size':").append("").append(size);
      sb.append("}");
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return sb;
  }
  /**
   * 获取附件大小
   * @param attr
   * @param module
   * @return
   * @throws Exception
   */
  public long getSize(Map<String, String> attr, String module) throws Exception {
	    long result = 0l;
	    Set<String> attrKeys = attr.keySet();
	    String fileName = "";
	    String path = "";
	    for (String attachmentId : attrKeys) {
	      String attachmentName = attr.get(attachmentId);
	      if(attachmentId != null && !"".equals(attachmentId)){
	        if(attachmentId.indexOf("_") > 0){
	          String attIds[] = attachmentId.split("_");
	          fileName = attIds[1] + "." + attachmentName;
	          path = T9SysProps.getAttachPath()+ File.separator + module + File.separator + attIds[0] + File.separator  + fileName;
	        }else{
	          fileName = attachmentId + "." + attachmentName;
	          path = T9SysProps.getAttachPath() + File.separator + module + File.separator  + fileName;
	        }
	        
	        File file = new File(path);
	        if(!file.exists()){
	          if(attachmentId.indexOf("_") > 0){
	            String attIds[] = attachmentId.split("_");
	            fileName = attIds[1] + "_" + attachmentName;
	            path = T9NtkoCont.ATTA_PATH + File.separator + module + File.separator + attIds[0] + File.separator  + fileName;
	          }else{
	            fileName = attachmentId + "_" + attachmentName;
	            path = T9NtkoCont.ATTA_PATH + File.separator + module + File.separator  + fileName;
	          }
	          file = new File(path);
	        }
	        if(!file.exists()){
	          continue;
	        }
	        //this.fileName = fileName;
	        result += file.length();
	      }
	    }
	    return result;
	  }
  /**
   * 文件上传处理
   * @param fileForm
   * @return
   * @throws Exception
   */
  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm) throws Exception {
	    Map<String, String> result = new HashMap<String, String>();
	    try {
	      Calendar cld = Calendar.getInstance();
	      int year = cld.get(Calendar.YEAR) % 100;
	      int month = cld.get(Calendar.MONTH) + 1;
	      String mon = month >= 10 ? month + "" : "0" + month;
	      String hard = year + mon;
	      Iterator<String> iKeys = fileForm.iterateFileFields();
	      while (iKeys.hasNext()) {
	        String fieldName = iKeys.next();
	        String fileName = fileForm.getFileName(fieldName);
	        String fileNameV = fileName;
	        if (T9Utility.isNullorEmpty(fileName)) {
	          continue;
	        }
	        String rand = T9DiaryUtil.getRondom();
	        fileName = rand + "_" + fileName;
	        
	        while (T9DiaryUtil.getExist(T9SysProps.getAttachPath() + File.separator + hard, fileName)) {
	          rand = T9DiaryUtil.getRondom();
	          fileName = rand + "_" + fileName;
	        }
	        result.put(hard + "_" + rand, fileNameV);
	        fileForm.saveFile(fieldName, T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator + hard + File.separator + fileName);
	        T9SelAttachUtil selA = new T9SelAttachUtil(fileForm, attachmentFolder);
	        result.putAll(selA.getAttachInFo());
	      }
	    } catch (Exception e) {
	      throw e;
	    }
	    return result;
	  }
  
 
  /**
   * 获取区域模板
   * @param dbConn
   * @param stationId
   * @return
   * @throws Exception
   */
  public String getAreaTemplate(Connection dbConn, String stationId) throws Exception {
	    try {
	      StringBuffer data = new StringBuffer("[");
	      String sql = " select c1.SEQ_ID, c1.TEMPLATE_NAME "
	                 + " from cms_template c1 "
	                 + " where c1.template_type = 4 "
	                 + " and station_id ="+ stationId
	                 + " ORDER BY c1.SEQ_ID asc ";
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      boolean flag = false;
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	          data.append("{seqId:"+rs.getInt("SEQ_ID")+","+"templateName:\""+rs.getString("TEMPLATE_NAME")+"\"},");
	          flag = true;
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      if(flag){
	        data = data.deleteCharAt(data.length() - 1);
	      }
	      data.append("]");
	      return data.toString();
	    } catch (Exception e) {
	      throw e;
	    }
	  }
  /**
   * 添加区域
   * @param conn
   * @param area
   * @param person
   * @param fileForm
   * @throws Exception
   */
  public void addArea(Connection conn,T9CmsArea area,T9Person person,T9FileUploadForm fileForm) throws Exception{
	  try{
	    String attachmentId = fileForm.getParameter("attachmentId");
	    String attachmentName =fileForm.getParameter("attachmentName");
		  String columnIdStr=fileForm.getParameter("columnId");
		  if(T9Utility.isNullorEmpty(columnIdStr)){
			  columnIdStr="";
		  }else{
			  columnIdStr=","+columnIdStr+",";
		  }
	    T9ORM orm = new T9ORM();
	    area.setCreateId(person.getSeqId());
	    area.setCreateTime(T9Utility.parseTimeStamp());
	    area.setAttachmentId(attachmentId);
	    area.setAttachmentName(attachmentName);
	    area.setColumnIdStr(columnIdStr);
	    area.setAreaStatus(0);
	    orm.saveSingle(conn,area); 
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  
  }
  
  //多文件上传时用到的----
  /**
   * 浮动菜单文件删除
   * 
   * @param dbConn
   * @param attId
   * @param attName
   * @param contentId
   * @throws Exception
   */
  public boolean delFloatFile(Connection dbConn, String attId, String attName, int seqId) throws Exception {
    boolean updateFlag = false;
    if (seqId != 0) {
      T9ORM orm = new T9ORM();
      T9CmsArea area = (T9CmsArea)orm.loadObjSingle(dbConn, T9CmsArea.class, seqId);
      String[] attIdArray = {};
      String[] attNameArray = {};
      String attachmentId = area.getAttachmentId();
      String attachmentName = area.getAttachmentName();
      //T9Out.println("attachmentId"+attachmentId+"--------attachmentName"+attachmentName);
      if (!"".equals(attachmentId.trim()) && attachmentId != null && attachmentName != null) {
        attIdArray = attachmentId.trim().split(",");
        attNameArray = attachmentName.trim().split("\\*");
      }
      String attaId = "";
      String attaName = "";
  
      for (int i = 0; i < attIdArray.length; i++) {
        if (attId.equals(attIdArray[i])) {
          continue;
        }
        attaId += attIdArray[i] + ",";
        attaName += attNameArray[i] + "*";
      }
      //T9Out.println("attaId=="+attaId+"--------attaName=="+attaName);
      area.setAttachmentId(attaId.trim());
      area.setAttachmentName(attaName.trim());
      orm.updateSingle(dbConn, area);
    }
  //处理文件
    String[] tmp = attId.split("_");
    String path = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "_" + attName;
    File file = new File(path);
    if(file.exists()){
      file.delete();
    } else {
      //兼容老的数据
      String path2 = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "." + attName;
      File file2 = new File(path2);
      if(file2.exists()){
        file2.delete();
      }
    }
    updateFlag=true;
    return updateFlag;
  }
  /**
   * 获取区域详情列表
   * @param dbConn
   * @param request
   * @param person
   * @param stationId
   * @return
   * @throws Exception
   */
  public String getAreaList(Connection dbConn, Map request, T9Person person, String stationId) throws Exception {
	    try {
	      String sql = " select c1.SEQ_ID, c1.area_name, c1.area_file_name,c1.station_id "
	                 + " from cms_area c1  "
	                 + " where c1.station_id ="+stationId
	                 + " ORDER BY c1.SEQ_ID asc ";
	      if("0".equals(stationId)){
	        sql = " select c1.SEQ_ID, c1.area_name, c1.area_file_name,c1.station_id "
	            + " from cms_area c1 "
	            + " ORDER BY c1.SEQ_ID asc ";
	      }
	      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
	      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
	      return pageDataList.toJson();
	    } catch (Exception e) {
	      throw e;
	    }
	  }
  /**
   * 删除区域
   * @param dbConn
   * @param seqIdStr
   * @param stationId
   * @throws Exception
   */
  public void deleteAreaLogic(Connection dbConn, String seqIdStr,String stationId) throws Exception {
	    T9ORM orm = new T9ORM();
	    if (T9Utility.isNullorEmpty(seqIdStr)) {
	      seqIdStr = "";
	    }
	    try {
	      String seqIdArry[] = seqIdStr.split(",");
	      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
	        for (String seqId : seqIdArry) {
	          T9CmsArea area = (T9CmsArea) orm.loadObjSingle(dbConn, T9CmsArea.class, Integer.parseInt(seqId));
	          deleteFile(dbConn, Integer.parseInt(seqId),stationId);
	          orm.deleteSingle(dbConn, area);
	        }
	      }
	    } catch (Exception e) {
	      throw e;
	    }
	  }
  /**
   * 删除附件文件
   * @param dbConn
   * @param seqId
   * @param stationId
   * @return
   * @throws Exception
   */
  public boolean deleteFile(Connection dbConn, int seqId,String stationId) throws Exception{
	    
	    T9ORM orm = new T9ORM();
	    T9CmsArea area = (T9CmsArea) orm.loadObjSingle(dbConn, T9CmsArea.class, seqId);
	    String attachmentIds=area.getAttachmentId();
	    String attachmentNames=area.getAttachmentName();
	    String attachmentId[]=attachmentIds.split(",");
	    String attachmentName[]=attachmentNames .split("\\*");
	    if(attachmentId.length<2){
	    	return true;
	    }else{
	    for(int len=0;len<attachmentId.length;len++){
	    String forder=attachmentId[len].substring(0,4);
	    String rand=attachmentId[len].substring(5, attachmentId[len].length());
	    File file = new File(T9SysProps.getAttachPath() + File.separator + attachmentFolder +File.separator+forder+File.separator +rand+"_"+ attachmentName[len]);
	     file.delete();
	    }
	    }
	    return true;
	  }
  
  /**
   * 获取详情
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer getAreaDetailLogic(Connection conn, int seqId, int flag,String stationId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9CmsArea area = (T9CmsArea) orm.loadObjSingle(conn, T9CmsArea.class, seqId);
      String columnIds=area.getColumnIdStr();
      if(columnIds!=""){
    	  columnIds=columnIds.substring(1,columnIds.length()-1);
      }
      area.setColumnIdStr(columnIds);
      StringBuffer data = T9FOM.toJson(area);  
      int templateId=area.getTemplateId();
      String templateName=this.getTemplateName(conn, templateId);
      String columnNames=this.getColumnNames(conn, columnIds);
      data.deleteCharAt(data.length()-1);
      data.append(",\"columnDesc\":\""+columnNames+"\"");
      data.append(",\"templateName\":\""+templateName+"\"}");
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  /**
   * 根据模板Id获取模板名称
   * @param conn
   * @param templateId
   * @return
   */
  public String getTemplateName(Connection conn,int templateId){
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  String sql="select template_name from cms_template where seq_id ="+templateId;
	  String templateName="";
	  try{
		  ps=conn.prepareStatement(sql);
		  rs=ps.executeQuery();
		  while(rs.next()){
			  templateName=rs.getString(1);
		  }
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return templateName;
  }
  /**
   * 根据栏目Id 获取栏目名称
   * @param conn
   * @param columnIds
   * @return
   * @throws SQLException
   */
  public String getColumnNames(Connection conn,String columnIds) throws SQLException{
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  if(T9Utility.isNullorEmpty(columnIds)){
		  columnIds="";
		  return "";
	  }
	  String columnId[]=columnIds.split(",");
	  String columnNames="";
	  int i;
	  String sql;
	  try{
		  for(i=0;i<columnId.length;i++){
				 sql ="select column_name from cms_column where seq_id="+columnId[i];
		          ps=conn.prepareStatement(sql);
		          rs=ps.executeQuery();
		          while(rs.next()){
		        	  columnNames+=rs.getString(1);
		          }
		          columnNames+=",";
		  }
		  columnNames=columnNames.substring(0,columnNames.length()-1);
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return columnNames;
  }
  
  /**
   * 更新区域
   * @param dbConn
   * @param area
   * @param fileForm
   * @throws Exception
   */
  public void updateArea(Connection dbConn ,T9CmsArea area,T9FileUploadForm fileForm)throws Exception{
	  try{
		  T9ORM orm=new T9ORM();
		  String attachmentId=fileForm.getParameter("attachmentId");
		  String attachmentName=fileForm.getParameter("attachmentName");
		  String columnIdStr=fileForm.getParameter("columnId");
		  if(T9Utility.isNullorEmpty(columnIdStr)){
			  columnIdStr="";
		  }else{
			  columnIdStr=","+columnIdStr+",";
		  }
		  area.setAttachmentId(attachmentId);
		  area.setAttachmentName(attachmentName);
		  area.setColumnIdStr(columnIdStr);
		  orm.updateSingle(dbConn, area);
		 
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
  }
  /**
   * 获取栏目
   * @param conn
   * @return
   * @throws Exception
   */
  public List getColumnList(Connection conn,String stationId) throws Exception{
	    T9ORM orm = new T9ORM();
	    Map filters = new HashMap();
	    filters.put("STATION_ID", stationId);
	    filters.put("PARENT_ID","0");
	   // String[] sql = { "STATION_ID = "+stationId,"parent_id=0","1 = 1 order by seq_id asc"};
	    List list  = orm.loadListSingle(conn ,T9CmsColumn.class , filters);
	   // List list  =  orm.loadListSingle(conn ,T9CmsColumn.class , filters);
	    return list;
	  }

   public String getColumns(Connection conn,String stationId){
	   StringBuffer sb=new StringBuffer("[{");
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   PreparedStatement ps1=null;
	   ResultSet rs1=null;
	   String nbsp = "├";
	   String sql="";
	   try{
		   sql="select seq_id,column_name,parent_id from cms_column where station_id="+stationId;
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   sb.append("\"columnId\":"+rs.getInt(1)+",");
			   sb.append("\"columnName\":\""+nbsp+rs.getString(2)+"\",");
			   if(rs.getInt(3)==0){
				   if(sb.length()>3){
					   sb.deleteCharAt(sb.length()-1);
				   }
				   sb.append("},");
				   continue;
			   }else
			   {
				   String sqlStr="select seq_id, column_name from cms_column where station_id="+stationId+" and parent_id="+rs.getInt(3);
				   ps1=conn.prepareStatement(sqlStr);
				   rs1=ps1.executeQuery();
				   sb.append("\"childNode\":{");
				   while(rs1.next()){
					   sb.append("\"columnId\":"+rs1.getInt(1)+",");
					   sb.append("\"columnName\":\""+rs1.getString(2)+"\",");
				   }
				   if(sb.length()>3){
					   sb.deleteCharAt(sb.length()-1);
				   }
				   sb.append("},");
			   }
		   }
		   if(sb.length()>3){
			   sb.deleteCharAt(sb.length()-1);
		   }
		   sb.append("]");
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return sb.toString();
}
   /**
    * 获取子栏目
    * @param conn
    * @param parentId
    * @param space
    * @return
    */
   public String getChildColumn(Connection conn , int parentId,int space){
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   String sql="";
	   String nbsp = "├";
	   StringBuffer sb=new StringBuffer("[");
	   String sp="";
	   for(int i=0;i<space;i++){
		   sp+="&nbsp;&nbsp;&nbsp;";
	   }
	   boolean hasChild=false;
	   try{
		   sql="select seq_id, column_name from cms_column where parent_id="+parentId;
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   hasChild=this.isHasChild(conn, rs.getInt(1));
			   sb.append("{\"columnId\":"+rs.getInt(1));
			   sb.append(",\"hasChild\":"+hasChild);
			   sb.append(",\"columnName\":\""+sp+nbsp+rs.getString(2)+"\"},");
		   }
		   if(sb.length()>3){
			   sb.deleteCharAt(sb.length()-1);
		   }
		   sb.append("]");
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return sb.toString();
   }
   
   /**
    * 判断是否有子栏目     false 表示无子栏目，true 反之
    * @param conn
    * @param seqId
    * @return
    */
   public boolean isHasChild(Connection conn , int seqId){
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   String sql="";
	   boolean hasChild=false;
	   try{
		   sql="select * from cms_column where parent_id ="+seqId;
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   hasChild=true;
		   }
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return hasChild;
   }
   /**
    * 处理上传附件，返回附件id，附件名称
   * @param request  HttpServletRequest
   * @param 
   * @return Map<String, String> ==> {id = 文件名}
   * @throws Exception 
   */
  public Map<String, String> fileUploadLogic(T9FileUploadFormCms fileForm, String pathPx) throws Exception {
    Map<String, String> result = new HashMap<String, String>();
    String filePath = pathPx;
    try {
      Calendar cld = Calendar.getInstance();
      int year = cld.get(Calendar.YEAR) % 100;
      int month = cld.get(Calendar.MONTH) + 1;
      String mon = month >= 10 ? month + "" : "0" + month;
      String hard = year + mon;
      Iterator<String> iKeys = fileForm.iterateFileFields();
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName).replaceAll("\\'", "");
        String fileNameV = fileName;
        //T9Out.println(fileName+"*************"+fileNameV);
        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        String rand = T9DiaryUtil.getRondom();
        fileName = rand + "_" + fileName;
        
        while (T9DiaryUtil.getExist(filePath + File.separator + hard, fileName)) {
          rand = T9DiaryUtil.getRondom();
          fileName = rand + "_" + fileName;
        }
        result.put(hard + "_" + rand, fileNameV);
        fileForm.saveFile(fieldName, filePath + File.separator + attachmentFolder + File.separator + hard + File.separator + fileName);
      }
    } catch (Exception e) {
      throw e;
    }
    return result;
  }
  
/**
 * 处理表单内容
 * @param request
 * @param dbConn
 * @param fileForm
 * @throws Exception
 */
  
  
  public void parseUploadRequest(HttpServletRequest request,Connection dbConn,T9FileUploadFormCms fileForm) throws Exception{
	  fileForm.request = request;
	    File tmpFile = new File(T9SysProps.getUploadCatchPath());
	    if (!tmpFile.exists()) {
	      tmpFile.mkdirs();
	    }
	   this.parseUploadRequest(request,dbConn,10 * T9Const.K,T9SysProps.getString("fileUploadTempDir"),T9Const.DEFAULT_CODE,fileForm);
	  
  }
  /**
   * 单附件上传时判断附件类型及附件大小是否超过系统设置最大值
   * @param request
   * @param dbConn
   * @param buffSize
   * @param tempPath
   * @param charSet
   * @param fileForm
   * @throws Exception
   */
  
  public void parseUploadRequest(HttpServletRequest request, Connection dbConn, int buffSize,String tempPath,String charSet,T9FileUploadFormCms fileForm) throws Exception {
	    DiskFileUpload fu = new DiskFileUpload();
	    fu.setHeaderEncoding(charSet);
	    // 设置允许用户上传文件大小,单位:字节
	    fu.setSizeMax((T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) )* T9Const.M);
	    // maximum size that will be stored in memory?
	    // 设置最多只允许在内存中存储的数据,单位:字节
	    fu.setSizeThreshold(buffSize);
	    // 设置一旦文件大小超过getSizeThreshold()的值时数据存放在硬盘的目录
	    if (!T9Utility.isNullorEmpty(tempPath)) {
	      fu.setRepositoryPath(tempPath);
	    }
	    //开始读取上传信息
	    List fieldList = fu.parseRequest(request);
	    Iterator iter = fieldList.iterator();
	    while (iter.hasNext()) {
	      FileItem item = (FileItem) iter.next();
	      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "IS_CTRLATTACH_CMS");
	      double[] maxSize=new double[4];
	      if(sysPara.getParaValue().equals("1")){
	    	  T9SysPara sysPara1 = T9JhSysParaLogic.hallObj(dbConn, "IMAGE_FILE_SIZE");
	    	  T9SysPara sysPara2 = T9JhSysParaLogic.hallObj(dbConn, "AUDIO_FILE_SIZE");
	    	  T9SysPara sysPara3 = T9JhSysParaLogic.hallObj(dbConn, "VIDEO_FILE_SIZE");
	    	  T9SysPara sysPara4 = T9JhSysParaLogic.hallObj(dbConn, "OTHER_FILE_SIZE");
	    	  double imageSize=0;
	    	  double audioSize=0;
	    	  double videoSize=0;
	    	  double  otherSize=0;
	    	  
	    	  if(T9Utility.isNullorEmpty(sysPara1.getParaValue())){
	    		  imageSize=0;
	    	  }else{
	    	  imageSize=Double.parseDouble(sysPara1.getParaValue());
	    	  }
	    	  if(T9Utility.isNullorEmpty(sysPara2.getParaValue())){
	    		  audioSize=0;
	    	  }else{
	    	  audioSize=Double.parseDouble(sysPara2.getParaValue());
	    	  }
	    	  if(T9Utility.isNullorEmpty(sysPara3.getParaValue())){
	    		  videoSize=0;
	    	  }else{
	    	  videoSize=Double.parseDouble(sysPara3.getParaValue());
	    	  }
	    	  if(T9Utility.isNullorEmpty(sysPara4.getParaValue())){
	    		  otherSize=0;
	    	  }else{
	    	  otherSize=Double.parseDouble(sysPara4.getParaValue());
	    	  }
	    	  maxSize[0]=imageSize;
	    	  maxSize[1]=audioSize;
	    	  maxSize[2]=videoSize;
	    	  maxSize[3]=otherSize;
		      String fileExt=T9FileUtility.getFileExtName(item.getName());
		      if(fileExt.equals("jpg")||fileExt.equals("png")||fileExt.equals("bmp")||fileExt.equals("gif")||fileExt.equals("jpeg")){
		    	  if(item.getSize()>(maxSize[0])*T9Const.M){
		    		  throw new SizeLimitExceededException("cms_文件上传失败：超出图片允许大小"+maxSize[0]+"兆");
		    	  }
		      }else if(fileExt.equals("mp3")||fileExt.equals("wav")){
		    	  if(item.getSize()>(maxSize[1])*T9Const.M){
		    		  throw new SizeLimitExceededException("cms_文件上传失败：超出音频允许大小"+maxSize[1]+"兆");
		    	  }
		      }else if(fileExt.equals("rmvb")||fileExt.equals("mp4")||fileExt.equals("avi")||fileExt.equals("mov")){
		    	  if(item.getSize()>(maxSize[2])*T9Const.M){
		    		  throw new SizeLimitExceededException("cms_文件上传失败：超出视频允许大小"+maxSize[2]+"兆");
		    	  }
		      }else{
		    	  if(item.getSize()>(maxSize[3])*T9Const.M){
		    		  throw new SizeLimitExceededException("cms_文件上传失败：超出附件文件允许大小"+maxSize[3]+"兆");
		    	  }
		      }
	      }
	      //文件字段
	      if (!item.isFormField()) {
	    	  fileForm.fileList.add(item);
	    	  fileForm.fileMap.put(item.getFieldName(), item);
	      //普通表单字段

	      }else {
	        if (charSet != null) {
	        	fileForm.paramMap.put(item.getFieldName(), item.getString(charSet));
	        }else {
	        	fileForm.paramMap.put(item.getFieldName(), item.getString());
	        }
	      }
	    }
	  }
  /**
   * 栏目是否关联区域
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public static boolean isHaveArea(Connection conn, int stationId, int columnId){
    PreparedStatement ps = null;
    ResultSet rs = null;
    boolean isHave = false;
    
    try{
      String sql = " select 1 from cms_area where STATION_ID = " + stationId + " and " + T9DBUtility.findInSet(columnId+"", "COLUMN_ID_STR");
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        isHave = true;
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return isHave;
  }
  
  /**
   * 获取栏目关联的区域
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public static List<T9CmsArea> getListArea(Connection conn, int stationId, int columnId) throws Exception{
    T9ORM orm = new T9ORM();
    String filtersArea[] = {"STATION_ID = " + stationId + " and " +T9DBUtility.findInSet(columnId+"", "COLUMN_ID_STR")};
    List<T9CmsArea> areaList = orm.loadListSingle(conn, T9CmsArea.class, filtersArea);
    return areaList;
  }
  
  /**
   * 发布
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int toRelease(Connection conn, int areaSeqId, int columnSeqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9CmsArea area = (T9CmsArea) orm.loadObjSingle(conn, T9CmsArea.class, areaSeqId);
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, columnSeqId);
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, area.getTemplateId());
    if(template != null){
      try {
        
        //读取模板文件
        T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
        
        //获取栏目树形结构目录
        T9ColumnLogic logic = new T9ColumnLogic();
        String parent = logic.getParentPath(conn, column);
        
        //获取生成文章的文件名&扩展名，如果为空则用seqId代替
        String fileName = area.getAreaFileName().trim();
        
        
        //velocity拼map
        Map<String,Object> request = new HashMap<String,Object>();
        //文件输出路径的文件名
        request.put("fileName", fileName);
        //当前位置
        request.put("location", commonLogic.getLocation(conn, column, "../") + " >" + column.getColumnName());
        
        //获取站点所有信息
//        station = commonLogic.getStationInfo(conn, station.getSeqId());
        request.put("station", T9StationLogic.stationPublic);
        
        
        //当前站点下的最新文章
        //获取出来的list 是有排序的
        T9CmsColumn columnNew = new T9CmsColumn();
        columnNew.setColumnName("最新更新");
        String filtersContentNew[] = {" STATION_ID =" + T9StationLogic.stationPublic.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_DATE desc "};
        List<T9CmsContent> contentListNew = orm.loadListSingle(conn, T9CmsContent.class, filtersContentNew);
        for(T9CmsContent content : contentListNew){
          String contentFileName = content.getContentFileName();
          if(T9Utility.isNullorEmpty(content.getContentFileName())){
            contentFileName = content.getSeqId()+"";
          }
          String path = commonLogic.getColumnPath(conn, content.getColumnId());
          contentFileName = "/t9/" + T9StationLogic.stationPublic.getStationPath() + "/" + path + "/" + contentFileName + "." + T9StationLogic.stationPublic.getExtendName();
          content.setUrl(contentFileName);
        }
        columnNew.setContentList(contentListNew);   
        request.put("columnNew", columnNew);
        
        
        //文件输出路径、模板名、模板路径
        String pageOutPath = T9SysProps.getWebPath() + File.separator + T9StationLogic.stationPublic.getStationPath() + File.separator + "script" ;
        String indexTemplateName = template.getAttachmentName();
        String pageTemlateUrl = T9SysProps.getAttachPath() + File.separator + attachmentFolder+ File.separator+T9StationLogic.stationPublic.getStationName();
        
        T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
        
        return 1;
      } catch (Exception ex) {
        throw ex;
      }
    }
    return 0;
  }
}