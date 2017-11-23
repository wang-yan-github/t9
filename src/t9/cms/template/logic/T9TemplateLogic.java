package t9.cms.template.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import t9.cms.template.data.T9CmsTemplate;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.cms.station.data.T9CmsStation;

public class T9TemplateLogic {
  public static final String attachmentFolder = "cms";

  
  public Map<Object, Object> fileUploadLogic(Connection dbConn,T9FileUploadForm fileForm) throws Exception {
    
    Map<Object, Object> result = new HashMap<Object, Object>();
    Iterator<String> iKeys = fileForm.iterateFileFields();
    String stationId=fileForm.getParameter("stationId");
    String stationName=getCurStationName(dbConn,stationId);
    String attachmentId = "";
    String attachmentName = "";
    boolean attachFlag = false;
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      attachmentName = fileName;
      T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
      String rand = emul.getRandom();
      attachmentId = rand;
      fileForm.saveFile(fieldName, T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator +stationName+File.separator+ fileName);
      attachFlag = true;
    }
    result.put("attachFlag", attachFlag);
    result.put("attachmentId", attachmentId);
    result.put("attachmentName", attachmentName);
    return result;
  }
  
  /**
   * CMS模板 添加
   * 
   */
  public void addTemplate(Connection dbConn, T9CmsTemplate template, T9Person person, T9FileUploadForm fileForm) throws Exception{
    
    Map<Object, Object> map = fileUploadLogic(dbConn,fileForm);
    boolean attachFlag = (Boolean) map.get("attachFlag");
    String attachmentId = (String) map.get("attachmentId");
    String attachmentName = (String) map.get("attachmentName");
    
    T9ORM orm = new T9ORM();
    template.setCreateId(person.getSeqId());
    template.setCreateTime(T9Utility.parseTimeStamp());
    if (attachFlag) {
      template.setAttachmentId(attachmentId);
      template.setAttachmentName(attachmentName);
    }
    orm.saveSingle(dbConn,template);
  }
  
  /**
   * CMS模板 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getTemplateList(Connection dbConn, Map request, T9Person person, String stationId) throws Exception {
    try {
      String sql = " select c1.SEQ_ID, c1.template_name, c1.template_file_name, c1.template_type, c1.template_type,c1.station_id "
                 + " from cms_template c1 "
                 + " where c1.station_id ="+stationId
                 + " ORDER BY c1.SEQ_ID asc ";
      if("0".equals(stationId)){
        sql = " select c1.SEQ_ID, c1.template_name, c1.template_file_name, c1.template_type, c1.template_type,c1.station_id "
            + " from cms_template c1 "
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
   * 获取详情
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer getTemplateDetailLogic(Connection conn, int seqId, int flag,String stationId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, seqId);
      StringBuffer data = T9FOM.toJson(template);
      String stationName = getCurStationName(conn,template.getStationId()+"");
      File templateFile = new File(T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator +stationName+File.separator+ template.getAttachmentName());
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile),"utf-8"));
      StringBuffer sb = new StringBuffer();
      String tempStr = "";
      while((tempStr = reader.readLine()) != null){
        sb.append(tempStr+"\n");
      }
      if(flag == 1){
        data.deleteCharAt(data.length()-1);
        data.append(",templateContent:\""+sb.toString().replace(" ", "&nbsp;&nbsp;").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;").replace("\\", "&#92;").replace("\"", "&#34;").replace("\'", "&#39;").replaceAll( "<", "&lt").replaceAll( ">", "&gt").replace("\n", "<br>")+"\"}");
      }
      else{
        data.deleteCharAt(data.length()-1);
        data.append(",templateContent:\""+sb.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\'", "\\\'").replace("\n", "\\n")+"\"}");
      }
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * CMS模板 修改
   * 
   */
  public void updateTemplate(Connection dbConn, T9CmsTemplate template, T9FileUploadForm fileForm) throws Exception{
    
    Map<Object, Object> map = fileUploadLogic(dbConn,fileForm);
    boolean attachFlag = (Boolean) map.get("attachFlag");
    String attachmentId = (String) map.get("attachmentId");
    String attachmentName = (String) map.get("attachmentName");
    String stationId = fileForm.getParameter("stationId");
    String stationName = getCurStationName(dbConn,stationId);
    T9ORM orm = new T9ORM();
    if (attachFlag) {
      template.setAttachmentId(attachmentId);
      template.setAttachmentName(attachmentName);
      deleteFile(dbConn, template.getSeqId(),stationId);
      fileUploadLogic(dbConn,fileForm);
    }
    else{
      T9CmsTemplate templateTemp = (T9CmsTemplate) orm.loadObjSingle(dbConn, T9CmsTemplate.class, template.getSeqId());
      File templateFile = new File(T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator +stationName+File.separator+ templateTemp.getAttachmentName());
      FileOutputStream out = new FileOutputStream(templateFile, false);
      out.write(fileForm.getParameter("templateContent").getBytes("UTF-8"));
      out.close();
    }
    orm.updateSingle(dbConn, template);
  }
  
  /**
   * 删除模板文件
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public boolean deleteFile(Connection dbConn, int seqId,String stationId) throws Exception{
    
    T9ORM orm = new T9ORM();
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(dbConn, T9CmsTemplate.class, seqId);
    template.getAttachmentId();
    template.getAttachmentName();
    String stationName=getCurStationName(dbConn,stationId);
    File file = new File(T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator +stationName+File.separator + template.getAttachmentName());
    return file.delete();
  }
  
  /**
   * 删除模板信息
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteTemplateLogic(Connection dbConn, String seqIdStr,String stationId) throws Exception {
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(dbConn, T9CmsTemplate.class, Integer.parseInt(seqId));
          deleteFile(dbConn, Integer.parseInt(seqId),stationId);
          orm.deleteSingle(dbConn, template);
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 员工关怀查询
   * 
   * @param dbConn
   * @param request
   * @param map
   * @param person
   * @return
   * @throws Exception
   */
  public String queryTemplateLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
    String templateName = (String) map.get("templateName");
    String templateFileName = (String) map.get("templateFileName");
    String templateType = (String) map.get("templateType");
    String conditionStr = "";
    String sql = "";
    try {
      if (!T9Utility.isNullorEmpty(templateType)) {
        conditionStr = " and c1.TEMPLATE_TYPE ='" + T9DBUtility.escapeLike(templateType) + "'";
      }
      if (!T9Utility.isNullorEmpty(templateName)) {
        conditionStr += " and c1.TEMPLATE_NAME like '%" + T9DBUtility.escapeLike(templateName) + "%'";
      }
      if (!T9Utility.isNullorEmpty(templateFileName)) {
        conditionStr += " and c1.TEMPLATE_FILE_NAME like '%" + T9DBUtility.escapeLike(templateFileName) + "%'";
      }
      sql = " select c1.SEQ_ID, c1.template_name, c1.template_file_name, c2.CLASS_DESC, c1.template_type "
          + " from cms_template c1 "
          + " join code_item c2 on c1.template_type = c2.seq_id "
          + " where 1=1" + conditionStr
          + " ORDER BY c1.SEQ_ID desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 查询下一级
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getStationName(Connection dbConn) throws Exception {
    StringBuffer sb = new StringBuffer("[");
    Statement stmt = null;
    ResultSet rs = null;
    String sql = " SELECT SEQ_ID, STATION_NAME FROM cms_station order by SEQ_ID asc";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sb.append("{\"seqId\":" + rs.getInt("SEQ_ID"));
        sb.append(",\"stationName\":\"" + rs.getString("STATION_NAME") + "\"},");
      }
      if(sb.length() > 3){
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return sb.toString();
  }

  public String getCurStationName(Connection dbConn,String stationId){
	    Statement stmt = null;
	    ResultSet rs = null;
	    String stationName="";
	    String sql="select station_name from cms_station  where seq_id ="+ stationId;
	    try {
	        stmt = dbConn.createStatement();
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	stationName=rs.getString(1);
	        }
	    }catch(Exception ex){
	    	ex.printStackTrace();
	    }
	return stationName;
	  
  }
}
