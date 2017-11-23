package t9.cms.content.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import t9.cms.column.data.T9CmsColumn;
import t9.cms.column.logic.T9ColumnLogic;
import t9.cms.common.logic.T9CmsCommonLogic;
import t9.cms.content.data.T9CmsContent;
import t9.cms.permissions.logic.T9PermissionsLogic;
import t9.cms.setting.data.T9SysPara;
import t9.cms.setting.logic.T9FileUploadFormCms;
import t9.cms.setting.logic.T9JhSysParaLogic;
import t9.cms.station.data.T9CmsStation;
import t9.cms.station.logic.T9StationLogic;
import t9.cms.template.data.T9CmsTemplate;
import t9.cms.velocity.T9velocityUtil;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9ContentLogic {
  public static final String attachmentFolder = "cms";
  
  public int getContentIndexByColumnId(Connection dbConn, int columnId) throws Exception{
    int contentIndex = 1;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "  SELECT max(CONTENT_INDEX) CONTENT_INDEX FROM cms_content c WHERE c.COLUMN_ID ="+columnId;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        contentIndex = rs.getInt("CONTENT_INDEX") + 1;
      }
      return contentIndex;
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
  }

  /**
   * CMS栏目 添加
   * 
   */
  public void addContent(Connection dbConn, T9CmsContent content, T9Person person) throws Exception{
    
    int contentIndex = 1;
    contentIndex = getContentIndexByColumnId(dbConn, content.getColumnId());
    
    T9ORM orm = new T9ORM();
    content.setContentTop(0);
    content.setContentType(1);
    content.setContentStatus(0);//新建
    content.setCreateId(person.getSeqId());
    content.setCreateTime(T9Utility.parseTimeStamp());
    content.setContentIndex(contentIndex);
    orm.saveSingle(dbConn,content);
  }
  
  /**
   * CMS文章 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getContentList(Connection dbConn, Map request, T9Person person, int stationId, int columnId) throws Exception {
    String whereStr = "";
    if(columnId != 0){
      whereStr = " and c.COLUMN_ID ="+columnId;
    }
    try {
      String sql = " SELECT c.SEQ_ID, c.CONTENT_NAME, c.CREATE_ID, p.USER_NAME, c2.COLUMN_NAME, c.CONTENT_DATE, c.CONTENT_STATUS, c.CONTENT_INDEX, c.CONTENT_TOP "
                 + " , c2.VISIT_USER, c2.EDIT_USER_CONTENT, c2.APPROVAL_USER_CONTENT, c2.RELEASE_USER_CONTENT, c2.RECEVIE_USER_CONTENT, c2.ORDER_CONTENT "
                 + " FROM cms_content c "
                 + " left join person p on c.CREATE_ID = p.SEQ_ID "
                 + " left join cms_column c2 on c.COLUMN_ID = c2.SEQ_ID "
                 + " where c.STATION_ID ="+stationId
                 + whereStr
                 + " ORDER BY c.CONTENT_TOP desc, c.CONTENT_INDEX desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      
      //判断列表访问权限
      T9PermissionsLogic pLogic = new T9PermissionsLogic();
      pageDataList = pLogic.visitControl(pageDataList, person);
      
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
  public StringBuffer getContentDetailLogic(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
      T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, content.getColumnId());
      StringBuffer data = T9FOM.toJson(content);
      data = data.deleteCharAt(data.length()-1);
      data.append(",columnName:\""+column.getColumnName()+"\"}");
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * CMS站点 修改
   * 
   */
  public void updateContent(Connection dbConn, T9CmsContent content) throws Exception{
    
    T9ORM orm = new T9ORM();
    content.setContentType(1);
    content.setContentStatus(1);//已编
    orm.updateSingle(dbConn, content);
  }
  
  /**
   * 删除内容
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteContent(Connection dbConn, String seqIdStr) throws Exception {
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9CmsContent content = (T9CmsContent) orm.loadObjSingle(dbConn, T9CmsContent.class, Integer.parseInt(seqId));
          orm.deleteSingle(dbConn, content);
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 签发
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toIssued(Connection conn, int seqId) throws Exception {
    PreparedStatement ps = null;
    String sql = " update cms_content set CONTENT_STATUS = 3 where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 否定
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toNo(Connection conn, int seqId) throws Exception {
    PreparedStatement ps = null;
    String sql = " update cms_content set CONTENT_STATUS = 2 where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  public int toReleaseStart(Connection conn, int seqId, boolean fullRelease) throws Exception{
  T9ORM orm = new T9ORM();
  PreparedStatement ps = null;
  try{
//    if(releaseTotal > -1){
//      return 2;
//    }
    
//    //此次发布的总文章数
//    String filters[] = {" STATION_ID =" + seqId + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
//    List<T9CmsContent> contentListTotal = orm.loadListSingle(conn, T9CmsContent.class, filters);
//    nowReleaseTotal = 0;
//    releaseTotal = contentListTotal.size();
    
    //修改发布文章状态
    String sql = " update cms_content set CONTENT_STATUS = 5 where SEQ_ID ="+seqId;
    ps = conn.prepareStatement(sql);
    ps.executeUpdate();
    
    
    //初始化全局变量
    T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
    T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
    T9StationLogic.stationPublic = commonLogic.getStationInfo(conn, content.getStationId());
    
    int returnInt = toRelease(conn, seqId, fullRelease);
    
//    //全部发送完毕后，发布文章总数归-1（初始值）
//    releaseTotal = -1;
//    nowReleaseTotal = -1;
    return returnInt;
  }
  catch(Exception ex){
//    //全部发送完毕后，发布文章总数归-1（初始值）
//    releaseTotal = -1;
//    nowReleaseTotal = -1;
    throw ex;
  }
}
  
  /**
   * 发布
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int toRelease(Connection conn, int seqId, boolean fullRelease) throws Exception {
    T9ORM orm = new T9ORM();
    T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, content.getColumnId());
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, content.getStationId());
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, column.getTemplateArticleId());
    T9CmsTemplate templateColumn = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, column.getTemplateIndexId());
    if(template != null){
      try {
        
        //读取模板文件
        T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
        
        //获取栏目树形结构目录
        T9ColumnLogic logic = new T9ColumnLogic();
        String parent = logic.getParentPath(conn, column);
        
        //获取生成文章的文件名&扩展名，如果为空则用seqId代替
        String fileName = "";
        if(T9Utility.isNullorEmpty(content.getContentFileName())){
          fileName = content.getSeqId()+"";
        }
        else{
          fileName = content.getContentFileName().trim();
        }
        
        
        //velocity拼map
        Map<String,Object> request = new HashMap<String,Object>();
        //文件输出路径的文件名
        request.put("fileName", fileName + "." + station.getArticleExtendName());
        //当前位置
        request.put("location", commonLogic.getLocation(conn, column, "../") + " > <a href=\""+templateColumn.getTemplateFileName()+"."+station.getExtendName()+"\">" + column.getColumnName() + "</a>");
        
        //获取站点所有信息
//        station = commonLogic.getStationInfo(conn, station.getSeqId());
        request.put("station", T9StationLogic.stationPublic);
        
        //当前文章内容
        request.put("content", content);
        
        
        //文件输出路径、模板名、模板路径
        String pageOutPath = T9SysProps.getWebPath() + File.separator + T9StationLogic.stationPublic.getStationPath() + File.separator + parent + column.getColumnPath() ;
        String indexTemplateName = template.getAttachmentName();
        String pageTemlateUrl = T9SysProps.getAttachPath() + File.separator + attachmentFolder+ File.separator+T9StationLogic.stationPublic.getStationName();
        
        T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
        

        
        //发布内容所在的栏目
        if(fullRelease){
          T9ColumnLogic logicColumn = new T9ColumnLogic();
          logicColumn.toRelease(conn, content.getColumnId(), false);
        }
        
        //已发布文章数量++
        T9StationLogic.nowReleaseTotal ++;
        return 1;
      } catch (Exception ex) {
        throw ex;
      }
    }
    return 0;
  }
  
  /**
   * 撤回
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toReceive(Connection conn, int seqId) throws Exception {
    
    T9ORM orm = new T9ORM();
    T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, content.getColumnId());
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, content.getStationId());
    
    //获取栏目树形结构目录
    T9ColumnLogic logic = new T9ColumnLogic();
    String parent = logic.getParentPath(conn, column);
    
    //获取生成文章的文件名&扩展名，如个为空则用seqId代替
    String fileName = "";
    if(T9Utility.isNullorEmpty(content.getContentFileName())){
      fileName = content.getSeqId()+"";
    }
    else{
      fileName = content.getContentFileName().trim();
    }
    File file = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + column.getColumnPath() + File.separator + fileName + "." + station.getArticleExtendName());
    if(file.exists()){
      file.delete();
    }
    
    
    PreparedStatement ps = null;
    String sql = " update cms_content set CONTENT_STATUS = 4 where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
      
      //初始化全局变量
      T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
      T9StationLogic.stationPublic = commonLogic.getStationInfo(conn, content.getStationId());
      
      //重新发布内容所在的栏目
      T9ColumnLogic logicColumn = new T9ColumnLogic();
      logicColumn.toRelease(conn, content.getColumnId(), false);
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 置顶 取消置顶
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toTop(Connection conn, int seqId, int contentTop) throws Exception {
    String flag = "";
    if(contentTop == 0){
      flag = "1";
    }
    else{
      flag = "0";
    }
    PreparedStatement ps = null;
    String sql = " update cms_content set CONTENT_TOP = " + flag + " where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 调序、排序
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public void toSort(Connection conn, int seqId, int toSeqId, int flag, int columnId) throws Exception {
    if((flag == 1 || flag == 2) && toSeqId != 0){
      int toContentIndex = getContentIndexByColumnId(conn,toSeqId);
      int contentIndex = getContentIndexByColumnId(conn,seqId);
      updateContentIndex(conn, seqId,toContentIndex);
      updateContentIndex(conn, toSeqId,contentIndex);
    }
    if(flag == 3 || flag == 4){
      PreparedStatement ps = null;
      String sql1 = "";
      int contentIndex = getMaxOrMinContentIndex(conn, columnId, flag);
      if(flag == 3){
        contentIndex = contentIndex + 1;
        sql1 = " update CMS_CONTENT set CONTENT_INDEX ="+contentIndex+" where SEQ_ID ="+seqId;
      }
      else if(flag == 4){
        contentIndex = contentIndex - 1;
        sql1 = " update CMS_CONTENT set CONTENT_INDEX ="+contentIndex+" where SEQ_ID ="+seqId;
      }
      try {
        ps = conn.prepareStatement(sql1);
        ps.executeUpdate();
      } catch (Exception ex) {
        throw ex;
      }
      finally{
        T9DBUtility.close(ps, null, null);
      }
    }
  }
  
  /**
   * 获取排序索引 contentIndex
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int getContentIndex(Connection conn,int seqId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql1 = " select CONTENT_INDEX from CMS_CONTENT where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql1);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt("CONTENT_INDEX");
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 更新排序索引
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int updateContentIndex(Connection conn,int seqId,int contentIndex) throws Exception{
    PreparedStatement ps = null;
    String sql1 = " update CMS_CONTENT set CONTENT_INDEX ="+contentIndex+" where SEQ_ID ="+seqId;
    try {
      ps = conn.prepareStatement(sql1);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, null, null);
    }
    return 0;
  }
  
  /**
   * 更新排序索引
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public int getMaxOrMinContentIndex(Connection conn,int columnId,int flag) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "";
    if(flag ==3){
      sql = " select MAX(CONTENT_INDEX) CONTENT_INDEX from CMS_CONTENT where COLUMN_ID ="+columnId;
    }
    else if(flag == 4){
      sql = " select MIN(CONTENT_INDEX) CONTENT_INDEX from CMS_CONTENT where COLUMN_ID ="+columnId;
    }
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getInt("CONTENT_INDEX");
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  /**
   * 根据T9CmsContent id 获取文章路径
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getUrls(Connection conn, int seqId) throws Exception {
    String url="error.jsp";
    T9ORM orm = new T9ORM();
    T9CmsContent content = (T9CmsContent) orm.loadObjSingle(conn, T9CmsContent.class, seqId);
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, content.getColumnId());
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, content.getStationId());
    //获取栏目树形结构目录
    T9ColumnLogic logic = new T9ColumnLogic();
    String parent = logic.getParentPath(conn, column);
    parent=parent.replace("\\", "/");
    //获取生成文章的文件名&扩展名，如个为空则用seqId代替
    String fileName = "";
    if(T9Utility.isNullorEmpty(content.getContentFileName().trim())){
      fileName = content.getSeqId()+"";
    }
    else{
      fileName = content.getContentFileName().trim();
    }
    File file = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath() + File.separator + parent + column.getColumnPath() + File.separator + fileName + "." + station.getArticleExtendName());
    if(file.exists()){
      url="/t9/" + station.getStationPath() + "/" + parent + column.getColumnPath() + "/" + fileName + "." + station.getArticleExtendName();
    }
    return url;
  }
  
  
  //点击单文件上传时调用的方法
  /**
   * 处理上传附件，返回附件id，附件名称,附件地址
  * @param request  HttpServletRequest
  * @param 
  * @return Map<String, String> ==> {id = 文件名}
  * @throws Exception 
  */
 public Map<String, String> fileUploadLogic(Connection dbConn,T9FileUploadFormCms fileForm,String contextPath, String stationId) throws Exception {
   Map<String, String> result = new HashMap<String, String>();
   T9ORM orm=new T9ORM();
   T9CmsStation station =(T9CmsStation)orm.loadObjSingle(dbConn, T9CmsStation.class, Integer.parseInt(stationId));
   String filePath = T9SysProps.getWebPath() + File.separator + station.getStationPath();
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
       fileForm.saveFile(fieldName, filePath + File.separator + "attach" + File.separator + hard + File.separator + fileName);
     }
   } catch (Exception e) {
     throw e;
   }
   return result;
 }
 
 /**
  * 浮动菜单文件删除
  * 
  * @param dbConn
  * @param attId
  * @param attName
  * @param contentId
  * @throws Exception
  */
 public boolean delFloatFile(Connection dbConn, String attId, String attName, int seqId,String stationId) throws Exception {
   boolean updateFlag = false;
   T9ORM orm=new T9ORM();
   T9CmsStation station =(T9CmsStation)orm.loadObjSingle(dbConn, T9CmsStation.class, Integer.parseInt(stationId));
   String filePath = T9SysProps.getWebPath() + File.separator + station.getStationPath();
   if (seqId != 0) {
     T9CmsContent content = (T9CmsContent)orm.loadObjSingle(dbConn, T9CmsContent.class, seqId);
     String[] attIdArray = {};
     String[] attNameArray = {};
     String attachmentId = content.getAttachmentId();
     String attachmentName = content.getAttachmentName();
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
     content.setAttachmentId(attaId.trim());
     content.setAttachmentName(attaName.trim());
     orm.updateSingle(dbConn, content);
   }
   //处理文件
   String[] tmp = attId.split("_");
   String path = filePath + File.separator + "attach" + File.separator  + tmp[0] + File.separator + tmp[1] + "_" + attName;
   File file = new File(path);
   if(file.exists()){
     file.delete();
   } else {
     //兼容老的数据
     String path2 = filePath + File.separator + "attach" + File.separator  + tmp[0] + File.separator + tmp[1] + "." + attName;
     File file2 = new File(path2);
     if(file2.exists()){
       file2.delete();
     }
   }
   updateFlag=true;
   return updateFlag;
 }
 
 /**暂时没用处理多文件上传

  * 附件批量上传页面处理
  * @return
 * @throws Exception 
  */
  public StringBuffer uploadMsrg2Json( Connection dbConn,T9FileUploadFormCms fileForm,String contextPath,String stationId) throws Exception{
    StringBuffer sb = new StringBuffer();
    Map<String, String> attr = null;
    String attachmentId = "";
    String attachmentName = "";
    String attachUrl = "";
    try{    
	  T9ORM orm=new T9ORM();
	  T9CmsStation station =(T9CmsStation)orm.loadObjSingle(dbConn, T9CmsStation.class, Integer.parseInt(stationId));
	  String filePath = contextPath+ File.separator + station.getStationPath()+ File.separator +"attach"+ File.separator ;
      attr = fileUploadLogic(dbConn,fileForm,contextPath, stationId);
      Set<String> attrKeys = attr.keySet();
      for (String key : attrKeys){
        String fileName = attr.get(key);
        attachmentId += key + ",";
        String hard=attachmentId.substring(0, 4)+File.separator ;
        attachmentName += fileName + "*";
        String realFileName=attachmentId.substring(5, attachmentId.length()-1)+"_"+fileName;
        attachUrl+=filePath+hard +realFileName+",";
      }
      attachUrl = attachUrl.replace("\\", "/").replaceAll("\"", "\\\\\"");
      long size = getSize(fileForm);
      sb.append("{");
      sb.append("'attachmentId':").append("\"").append(attachmentId).append("\",");
      sb.append("'attachmentName':").append("\"").append(attachmentName).append("\",");
      sb.append("'attachUrl':").append("\"").append(attachUrl).append("\",");
      sb.append("'size':").append("").append(size);
      sb.append("}");
    } catch (Exception e){
      e.printStackTrace();
      throw e;
    }
    return sb;
  }
 
  public long getSize( T9FileUploadFormCms fileForm) throws Exception{
    long result = 0l;
    Iterator<String> iKeys = fileForm.iterateFileFields();
    while (iKeys.hasNext()) {
      String fieldName = iKeys.next();
      String fileName = fileForm.getFileName(fieldName);
      if (T9Utility.isNullorEmpty(fileName)) {
        continue;
      }
      result += fileForm.getFileSize(fieldName);
    }
    return result;
  }
  
  /**
   * 是否有盖章辅助角色
   * @param request
   * @throws Exception 
   */
  public static String isIssued(HttpServletRequest request) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //判断参数
      T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "IS_ISSUED_CMS");
      if(sysPara == null){
        return "0";
      }
      
      return sysPara.getParaValue();
    }catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * 根据条件获取文章数量（对各个站点或者栏目的文章进行统计)
   * @param conn
   * @param stationId
   * @param columnId
   * @return
   */
  public String contentStatistics(Connection conn, String stationId,String columnId){
	  String data="<graph caption='文章统计（单位：条）' showNames='1'>";
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  PreparedStatement ps1=null;
	  ResultSet rs1=null;
	  String sql="";
	  try{
		  if(T9Utility.isNullorEmpty(stationId)||stationId.equals("0")){
			  sql="select station_id,count(*) from cms_content group by station_id";
			  ps=conn.prepareStatement(sql);
			  rs=ps.executeQuery();
			  while(rs.next()){
				  String sqlStr="select station_name from cms_station where seq_id="+rs.getInt(1);
				  ps1=conn.prepareStatement(sqlStr);
				  rs1=ps1.executeQuery();
				  while(rs1.next()	){
					  data+="<set name='"+rs1.getString(1)+"' value='"+rs.getInt(2)+"'/>";
				  }
			  }
			  data+="</graph>";
		  }
		  else
		  {
			  if(T9Utility.isNullorEmpty(columnId)){
			  sql="select seq_id,column_name from cms_column where parent_id=0 and station_id= "+stationId;
			  ps=conn.prepareStatement(sql);
			  rs=ps.executeQuery();
			  while(rs.next()){
				  String sqlStr="select count(*) from cms_content where column_id="+rs.getInt(1);
				  ps1=conn.prepareStatement(sqlStr);
				  rs1=ps1.executeQuery();
				  while(rs1.next()	){
					  int sum=0;
					  if(isHasChild(conn,rs.getInt(1))){
						  sum=rs1.getInt(1)+this.getChildContents(conn, rs.getInt(1));
					  }
					  else{
						  sum=rs1.getInt(1);
					  }
					  data+="<set name='"+rs.getString(2)+"' value='"+sum+"'/>";
				 // }
			  }
			  }
			  data+="</graph>";
			  }else{
				  String columnIds[]=columnId.split(",");
				  for(int i=0;i<columnIds.length;i++){
					  String columnName=this.getColumnName(conn,Integer.parseInt( columnIds[i]));
					  int n=this.getCurrentColumnContents(conn, Integer.parseInt( columnIds[i]));
					  int columnSum=this.getChildContents(conn, Integer.parseInt(columnIds[i]))+n;
					  data+="<set name='"+columnName+"' value='"+columnSum+"'/>";
				  }
				  data+="</graph>";
			  }
		  }
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return data;
  }
  /**
   * 获取子栏目的文章总数
   * @param conn
   * @param seqId
   * @return
   */
  public int getChildContents(Connection conn, int seqId){
	  String sql="";
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  PreparedStatement ps1=null;
	  ResultSet rs1=null;
	  int sum=0;
	  try{
		  sql="select seq_id from cms_column where parent_id="+seqId;
		  ps=conn.prepareStatement(sql);
		  rs=ps.executeQuery();
		  while(rs.next()){
			  String sqlStr="select count(*) from cms_content where column_id="+rs.getInt(1);
			  ps1=conn.prepareStatement(sqlStr);
			  rs1=ps1.executeQuery();
			  while(rs1.next()){
			  if(this.isHasChild(conn, rs.getInt(1))){
				 int n=getChildContents(conn,rs.getInt(1))+rs1.getInt(1);
				 sum+=n;
			  }else{
				  sum=rs1.getInt(1);
			  }
			  }
		  }
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return sum;
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
   * 根据Id获取栏目名称
   * @param conn
   * @param seqId
   * @return
   */
  public String getColumnName(Connection conn,int seqId){
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   String sql="";
	   String columnName="";
	   try{
		   sql="select column_name from cms_column where seq_id ="+seqId;
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   columnName=rs.getString(1);
		   }
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return columnName;
  }
  /**
   * 获取当前栏目的文章数
   * @param conn
   * @param columnId
   * @return
   */
  public int getCurrentColumnContents(Connection conn, int columnId){
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   String sql="";
	   int contents=0;
	   try{
		   sql="select count(*) from cms_content where column_id ="+columnId;
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   contents=rs.getInt(1);
		   }
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	   return contents;
  }
  /**
   * 添加收藏
   * @param dbConn
   * @param person
   * @param contentId
   * @param contentUrl
   * @return
   */
public String addCollection(Connection dbConn,T9Person person, String contentId,String contentUrl) {
	String flag="0";
	T9ORM orm = new T9ORM();
	PreparedStatement ps=null;
	boolean f=false;
	String sql="";
	String curDate=T9Utility.getCurDateTimeStr();
	String isExist=this.isExist(dbConn, Integer.parseInt(contentId));
	try{
			T9CmsContent cmsContent=(T9CmsContent)orm.loadObjSingle(dbConn, T9CmsContent.class, Integer.parseInt(contentId));
		   sql="insert into H_PORTAL_FAVORITE(site_id,column_id,article_id,article_url,user_id,create_time,module_code,article_name)values(?,?,?,?,?,?,?,?)";
		   ps=dbConn.prepareStatement(sql);
		   ps.setInt(1, cmsContent.getStationId());
		   ps.setInt(2, cmsContent.getColumnId());
		   ps.setInt(3, Integer.parseInt(contentId));
		   ps.setString(4, contentUrl);
		   ps.setInt(5,person.getSeqId());
		   ps.setDate(6,T9Utility.parseSqlDate(curDate));
		   ps.setString(7, "14");
		   ps.setString(8, cmsContent.getContentName());
		   if("0".equals(isExist)){
			   f=ps.execute();
			   if(f==true){
				   flag="0";
			   }else{
				   flag="1";
			   }
		   }else{
			   flag="2";
		   }
	   }catch(Exception ex){
		   ex.printStackTrace();
	   }
	return flag;
}

/**
 * 判断当前文章是否已经收藏 0代表没有收藏，1代表已经收藏。
 * @param dbConn
 * @param contentId
 * @return
 */
public String isExist(Connection dbConn,int contentId){
	String flag="0";
	PreparedStatement ps=null;
	ResultSet rs=null;
	String sql="";
	try{
		sql="select * from h_portal_favorite where article_id="+contentId;
		ps=dbConn.prepareStatement(sql);
		rs=ps.executeQuery();
		if(rs.next()){
			flag="1";
		}
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return flag;
}
}
