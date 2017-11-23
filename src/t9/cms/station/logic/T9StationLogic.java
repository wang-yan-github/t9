package t9.cms.station.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.cms.column.data.T9CmsColumn;
import t9.cms.column.logic.T9ColumnLogic;
import t9.cms.common.logic.T9CmsCommonLogic;
import t9.cms.content.data.T9CmsContent;
import t9.cms.permissions.logic.T9PermissionsLogic;
import t9.cms.station.data.T9CmsStation;
import t9.cms.template.data.T9CmsTemplate;
import t9.cms.velocity.T9velocityUtil;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9StationLogic {
  public static final String attachmentFolder = "cms";

  /**
   * CMS索引模板获取
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getTemplate(Connection dbConn, int stationId) throws Exception {
    try {
      StringBuffer data = new StringBuffer("[");
      String sql = " select c1.SEQ_ID, c1.TEMPLATE_NAME "
                 + " from cms_template c1 "
                 + " where c1.template_type = 1 "
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
   * CMS站点 添加
   * 
   */
  public void addStation(Connection dbConn, T9CmsStation station, T9Person person) throws Exception{
    
    T9ORM orm = new T9ORM();
    station.setCreateId(person.getSeqId());
    station.setCreateTime(T9Utility.parseTimeStamp());
    
    //初始化权限
    station.setVisitUser("0||");
    station.setEditUser("0||");
    station.setNewUser("0||");
    station.setDelUser("0||");
    station.setRelUser("0||");
    
    orm.saveSingle(dbConn,station);
    
    File file = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath());
    file.mkdir();
  }
  
  /**
   * CMS站点 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getStationList(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " SELECT s.SEQ_ID, s.STATION_NAME, s.STATION_DOMAIN_NAME, s.TEMPLATE_ID, t.TEMPLATE_NAME, s.STATION_PATH, s.EXTEND_NAME, s.ARTICLE_EXTEND_NAME "
                 + " , s.VISIT_USER, s.EDIT_USER, s.NEW_USER, s.DEL_USER, s.REL_USER "
                 + " FROM cms_station s "
                 + " LEFT JOIN cms_template t on s.TEMPLATE_ID = t.SEQ_ID "
                 + " ORDER BY s.SEQ_ID asc ";
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
  public StringBuffer getStationDetailLogic(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, seqId);
      StringBuffer data = T9FOM.toJson(station);
      return data;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
  /**
   * CMS站点 修改
   * 
   */
  public void updateStation(Connection dbConn, T9CmsStation station) throws Exception{
    
    T9ORM orm = new T9ORM();
    T9CmsStation stationOld = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, station.getSeqId());
    orm.updateSingle(dbConn, station);
    
    File fileOld = new File(T9SysProps.getWebPath() + File.separator + stationOld.getStationPath());
    File fileNew = new File(T9SysProps.getWebPath() + File.separator + station.getStationPath());
    fileOld.renameTo(fileNew);
  }
  
  /**
   * 删除模板信息
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteStation(Connection dbConn, String seqIdStr) throws Exception {
    T9ORM orm = new T9ORM();
    if (T9Utility.isNullorEmpty(seqIdStr)) {
      seqIdStr = "";
    }
    try {
      String seqIdArry[] = seqIdStr.split(",");
      if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
        for (String seqId : seqIdArry) {
          T9CmsStation station = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, Integer.parseInt(seqId));
          orm.deleteSingle(dbConn, station);
        }
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  public int toReleaseStart(Connection conn, int seqId, boolean fullRelease) throws Exception{
    T9ORM orm = new T9ORM();
    
    try{
      if(releaseTotal > -1){
        return 2;
      }
      
      //此次发布的总文章数
      String filters[] = {" STATION_ID =" + seqId + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
      List<T9CmsContent> contentListTotal = orm.loadListSingle(conn, T9CmsContent.class, filters);
      nowReleaseTotal = 0;
      releaseTotal = contentListTotal.size();
      
      //初始化全局变量
      T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
      stationPublic = commonLogic.getStationInfo(conn, seqId);
      
      int returnInt = toRelease(conn, seqId, fullRelease);
      
      //全部发送完毕后，发布文章总数归-1（初始值）
      releaseTotal = -1;
      nowReleaseTotal = -1;
      return returnInt;
    }
    catch(Exception ex){
      //全部发送完毕后，发布文章总数归-1（初始值）
      releaseTotal = -1;
      nowReleaseTotal = -1;
      throw ex;
    }
  }
  
  
  public static T9CmsStation stationPublic = null;
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
    
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, seqId);
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
    try {
      if(template != null){
        
        //读取模板文件
        T9CmsCommonLogic commonLogic = new T9CmsCommonLogic();
        
        //获取生成索引的文件名&扩展名，站点文件名为模板文件名，此字段不能为空
        String fileName = template.getTemplateFileName();
        
        //velocity拼map
        Map<String,Object> request = new HashMap<String,Object>();
        //文件输出路径的文件名
        request.put("fileName", fileName + "." + station.getExtendName());
        
        //获取站点所有信息
//      station = commonLogic.getStationInfo(conn, station.getSeqId());
        request.put("station", stationPublic);
        
        //获取图片新闻
        String filtersContentImg[] = {" STATION_ID = "+ seqId +" and CONTENT_STATUS = 5 and  ("+T9DBUtility.isFieldNotNull("ATTACHMENT_ID")+") order by CONTENT_DATE desc "};
        List<T9CmsContent> contentImgList = orm.loadListSingle(conn, T9CmsContent.class, filtersContentImg);
        List<T9CmsContent> contentImgListReturn = new ArrayList<T9CmsContent>();
        int countImg = 0;
        for(T9CmsContent content : contentImgList){
          if(countImg >= 5){
            break;
          }
          String contentFileName = content.getContentFileName();
          if(T9Utility.isNullorEmpty(content.getContentFileName())){
            contentFileName = content.getSeqId()+"";
          }
          String pathContent = commonLogic.getColumnPath(conn, content.getColumnId());
          contentFileName = "/t9/" + stationPublic.getStationPath() + "/" + pathContent + "/" + contentFileName + "." + stationPublic.getExtendName();
          content.setUrl(contentFileName);
          
          String str[] = content.getAttachmentId().split(",")[0].split("_");
          String srcFile = T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator + str[0] + File.separator + str[1] + "_" + content.getAttachmentName().split("\\*")[0];
          String destFile = T9SysProps.getWebPath() + File.separator + stationPublic.getStationPath() + File.separator + "images" + File.separator + "contentImages" + File.separator + str[1] + "." + content.getAttachmentName().split("\\*")[0].split("\\.")[1];
          String fileNameEnd = content.getAttachmentName().split("\\*")[0];
          if(fileNameEnd.endsWith(".jpg") || fileNameEnd.endsWith(".JPG")){
            T9FileUtility fileUtility = new T9FileUtility();
            fileUtility.deleteAll(destFile);
            fileUtility.copyFile(srcFile, destFile);
            content.setImageUrl(File.separator + "t9" + File.separator + stationPublic.getStationPath() + File.separator + "images" + File.separator + "contentImages" + File.separator + str[1] + "." + content.getAttachmentName().split("\\*")[0].split("\\.")[1]);
            countImg++;
            contentImgListReturn.add(content);
          }
        }
        request.put("contentImgList", contentImgListReturn);
        
        //文件输出路径、模板名、模板路径
        String pageOutPath = T9SysProps.getWebPath() + File.separator + stationPublic.getStationPath();
        String indexTemplateName = template.getAttachmentName();
        
        String pageTemlateUrl = T9SysProps.getAttachPath() + File.separator + attachmentFolder+File.separator+stationPublic.getStationName();
        
        T9velocityUtil.velocity(request, pageOutPath, indexTemplateName, pageTemlateUrl);
        
        //发布站点下的所有栏目
        if(fullRelease){
          String filtersAll[] = {" STATION_ID =" + seqId + " order by COLUMN_INDEX asc "};
          List<T9CmsColumn> columnListAll = orm.loadListSingle(conn, T9CmsColumn.class, filtersAll);
          T9ColumnLogic logicColumn = new T9ColumnLogic();
          for(T9CmsColumn column : columnListAll){
            logicColumn.toRelease(conn, column.getSeqId(), true);
          }
        }
        
        return 1;
      } 
    } catch (Exception ex) {
      throw ex;
    }
    return 0;
  }
  
  public int checkPath(Connection conn, int seqId, String stationPath) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = " SELECT 1 FROM cms_station c where c.station_path = '" + stationPath + "' and seq_id !="+seqId;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return 1;
      }
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
    return 0;
  }
  
  public String getPath(Connection conn, int seqId) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, seqId);
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
    if(template == null){
      return "1";
    }
    return T9Utility.encodeSpecial("/t9/" + station.getStationPath() + "/" + template.getTemplateFileName() + "." + station.getExtendName());
  }
  
  public static int releaseTotal = -1;
  public static int nowReleaseTotal =-1;
  
  public String getSchedule(){
    
    float a = Float.parseFloat(nowReleaseTotal+"");
    float b = Float.parseFloat(releaseTotal+"");
    
    if(b == 0){
      return "0";
    }
    
    String c = (a/b)*100 + "";
    if(c.length() > 3){
      c = c.substring(0, 2);
      if(c.endsWith(".")){
        c = c.substring(0, c.length() - 1);
      }
    }
    
    if(a == b){
      return "100";
    }
    return c;
  }

  /**
   * 获取/更新用户访问数
   * 2013-5-13
   * @author ny
   * @param dbConn
   * @return
   */
  public int getAndUpdateVisitedCount(Connection dbConn) {
    int visitedCount=0;
    PreparedStatement ps = null;
    PreparedStatement ps1 = null;
    ResultSet rs = null;
    String sql="";
    String sql1="";
    try{
      sql="select visited_count from cms_extend";
      ps=dbConn.prepareStatement(sql);
      rs=ps.executeQuery();
      while(rs.next()){
        visitedCount=rs.getInt(1)+1;
      }
      sql1="update cms_extend set visited_count="+visitedCount;
      ps1=dbConn.prepareStatement(sql1);
      ps1.execute();
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return visitedCount;
  }
  
  /**
   * 获取用户访问数
   * 2013-5-13
   * @author ny
   * @param dbConn
   * @return
   */
  public int getVisitedCount(Connection dbConn) {
    int visitedCount=0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql="";
    try{
      sql="select visited_count from cms_extend";
      ps=dbConn.prepareStatement(sql);
      rs=ps.executeQuery();
      while(rs.next()){
        visitedCount=rs.getInt(1);
      }
    }catch(Exception ex){
      ex.printStackTrace();
    }
    return visitedCount;
  }
}
