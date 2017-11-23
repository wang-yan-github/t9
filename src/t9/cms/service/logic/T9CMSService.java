package t9.cms.service.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import t9.cms.content.data.T9CmsContent;
import t9.cms.content.logic.T9ContentLogic;
import t9.cms.setting.data.T9SysPara;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.cms.setting.logic.T9JhSysParaLogic;
import t9.user.api.core.db.T9DbconnWrap;


public class T9CMSService {

  
  public String testService(String contentName, String json)throws Exception{
  
    //文件处理？？
    
    Connection dbConn = null;
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    dbConn = dbUtil.getSysDbConn();
    
    Map map = T9FOM.json2Map(json);
    String contentTitle = (String)map.get("contentTitle");
    String contentAbstract = (String)map.get("contentAbstract");
    String keyword = (String)map.get("keyword");
    String contentSource = (String)map.get("contentSource");
    String contentFileName = (String)map.get("contentFileName");
    String contentAuthor = (String)map.get("contentAuthor");
    Date contentDate = null;
    if(T9Utility.isDayTime((String)map.get("contentDate"))){
      contentDate = T9Utility.parseDate((String)map.get("contentDate"));
    }
    else{
      contentDate = T9Utility.parseTimeStamp();
    }
    String stationStr = (String)map.get("stationStr");
    int stationId = getStationId(dbConn, stationStr);
    String columnStr = (String)map.get("columnStr");
    int columnId = getColumnId(dbConn, columnStr);;
    String content = (String)map.get("content");
    int createId = 0;
    Date createTime = T9Utility.parseTimeStamp();
    int contentType = 1;
    int contentStatus = 5; //已发
    String contentTopStr = (String)map.get("contentTop"); //是否置顶 1为置顶
    
    //系统自动计算索引排序号
    int contentIndex = 1;
    T9ContentLogic logic = new T9ContentLogic();
    contentIndex = logic.getContentIndexByColumnId(dbConn, columnId);    
    
    T9CmsContent CMSContent = new T9CmsContent();
    CMSContent.setContentName(contentName);
    if(!T9Utility.isNullorEmpty(contentTitle)){
      CMSContent.setContentTitle(contentTitle);
    }
    if(!T9Utility.isNullorEmpty(contentAbstract)){
      CMSContent.setContentTitle(contentAbstract);
    }
    if(!T9Utility.isNullorEmpty(keyword)){
      CMSContent.setKeyword(keyword);
    }
    if(!T9Utility.isNullorEmpty(contentSource)){
      CMSContent.setContentSource(contentSource);
    }
    if(!T9Utility.isNullorEmpty(contentFileName)){
      CMSContent.setContentFileName(contentFileName);
    }
    if(!T9Utility.isNullorEmpty(contentAuthor)){
      CMSContent.setContentAuthor(contentAuthor);
    }
    if(contentDate != null){
      CMSContent.setContentDate(contentDate);
    }
    else{
      CMSContent.setContentDate(T9Utility.parseTimeStamp());
    }
    if(!T9Utility.isNullorEmpty(content)){
      CMSContent.setContent(content);
    }
    
    CMSContent.setStationId(stationId);
    CMSContent.setColumnId(columnId);
    CMSContent.setCreateId(createId);
    CMSContent.setCreateTime(createTime);
    CMSContent.setContentType(contentType);
    CMSContent.setContentStatus(contentStatus);
    
    if(!T9Utility.isNullorEmpty(contentTopStr) && T9Utility.isInteger(contentTopStr)){
      CMSContent.setContentTop(Integer.parseInt(contentTopStr));
    }
    else{
      CMSContent.setContentTop(0);
    }
    CMSContent.setContentIndex(contentIndex);
    
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, CMSContent);
    
    //判断参数
    T9SysPara sysPara = T9JhSysParaLogic.hallObj(dbConn, "IS_RELEASE_CMS");
    if("1".equals(sysPara.getParaValue())){
//      logic.toRelease(dbConn, getMaxContentId(dbConn), true);
    }
    T9DbconnWrap.closeDbConn(dbConn, null);
    
    //return "{\"code\": \"-7\", \"msg\": \"令牌不对！\"}";
    return json;
  }
  
  
  
  public static void main(String args[]) {
    try {
      String serviceUrl = "http://localhost:88/t9/services/T9CMSService?wsdl";
      Service service = new Service(); 
      Call call = (Call) service.createCall(); 
      call.setTargetEndpointAddress(new java.net.URL(serviceUrl)); 
      call.setOperationName("testService");
      call.addParameter("contentName", XMLType.XSD_STRING, ParameterMode.IN); 
      call.addParameter("json", XMLType.XSD_STRING, ParameterMode.IN); 
      call.setReturnType(XMLType.XSD_STRING); 
      String ret = (String) call.invoke(new Object[] {"22222222222","{\"code\":\"2\",\"name\":\"哈哈\"}"});
//      System.out.println(ret);
    } catch (Exception e) {
      System.out.println("config - 调用web服务异常,异常信息:" + e.getMessage());
    }
  }
  
  public int getStationId(Connection dbConn, String stationStr) throws Exception{
    int stationId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "  SELECT SEQ_ID FROM cms_station c WHERE c.STATION_NAME ='"+stationStr+"'";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        stationId = rs.getInt("SEQ_ID");
      }
      return stationId;
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  public int getColumnId(Connection dbConn, String columnStr) throws Exception{
    int columnId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "  SELECT SEQ_ID FROM cms_column c WHERE c.COLUMN_NAME ='"+columnStr+"'";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        columnId = rs.getInt("SEQ_ID");
      }
      return columnId;
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
  }
  
  public int getMaxContentId(Connection dbConn) throws Exception{
    int contentId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "  SELECT SEQ_ID FROM cms_content c WHERE c.seq_id = (select max(seq_id) from cms_content)";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        contentId = rs.getInt("SEQ_ID");
      }
      return contentId;
    } catch (Exception ex) {
      throw ex;
    }
    finally{
      T9DBUtility.close(ps, rs, null);
    }
  }
}
