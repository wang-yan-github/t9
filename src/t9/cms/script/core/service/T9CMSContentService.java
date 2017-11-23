package t9.cms.script.core.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.cms.content.data.T9CmsContent;
import t9.cms.script.core.CMSException.CException;
import t9.cms.script.core.bo.T9Attach;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

/**
 * 
 * @author zhaopeng
 * 提供对 CMS_content 的各种服务
 * 主要有 获取栏目下所有的文章
 */
public class T9CMSContentService {
	
  /**
   * 带分页的 获取所有文章
   * @param dbConn
   * @param sWhere
   * @param nPageSize
   * @param nCurrentPage
   * @param nTotalSize
   * @return
   * @throws Exception
   */
  public static List getContentListByWher1(Connection dbConn,String sWhere, int nPageSize, int nCurrentPage, int nTotalSize) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    ArrayList list;
        list = new ArrayList();
        T9CmsContent content = null;
	    StringBuffer sb = new StringBuffer();
        sb.append("select * from cms_content");
        if(sWhere != null && !sWhere.equals(""))
        {
            sb.append(" where ");
            sb.append(sWhere);
        }
        sb.append(" order by SEQ_ID desc");
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sb.toString());
	      /**
	       * 蛋疼的分页 不过靠谱 不知道t9怎么分的
	       */
	      int nPageCount = ((nTotalSize + nPageSize) - 1) / nPageSize;
          if(nCurrentPage > nPageCount)
              nCurrentPage = nPageCount;
          int i = (nCurrentPage - 1) * nPageSize;
          for(int j = 0; j < i; j++)
              rs.next();
          for(i = 0; i < nPageSize && rs.next(); i++){
	    	  content = new T9CmsContent();
	    	  content.setSeqId(rs.getInt("SEQ_ID"));
	    	  content.setContentName(rs.getString("CONTENT_NAME"));
	    	  list.add(content);
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return list;
  }
  /**
   * 通过id获取 content
   * @param dbConn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9CmsContent getContentById(Connection dbConn, int seqId) throws Exception {
	   
	  T9ORM orm = new T9ORM();
	   T9CmsContent content = (T9CmsContent) orm.loadObjSingle(dbConn, T9CmsContent.class, seqId);
	    return content;
  }
  /**
   * 
   * @param cmsContent
   * @param type
   * @return
   */
  public List getAllT9Attach(T9CmsContent cmsContent){
	  List list = null;
	       list = new ArrayList();
	  String attIds ;
	  String attName;
	  T9Attach attch = null;
	  if(cmsContent.getAttachmentId() == null || "".equals(cmsContent.getAttachmentId())){
		  return list;
	  }
	  attIds = cmsContent.getAttachmentId();
	  attName = cmsContent.getAttachmentName();
	  String[] _aAttIds = attIds.split(",");
	  String[] _aAttName = attName.split("\\*");
	  for(int i=0 ;i<_aAttIds.length ;i++){
		  attch = new T9Attach();
		  attch.setAttId(_aAttIds[i]);
		  attch.setAttName(_aAttName[i]);
		  attch.setCrTime(cmsContent.getCreateTime());
		  list.add(attch);
	  }
	  return list;
  }
  /**
   * 获取指定类型的附件
   * @param cmsContent
   * @param type
   * @return
   */
  public List getT9AttachByType(T9CmsContent cmsContent,String type){
	  List list = null;
	  list = getAllT9Attach(cmsContent);
	  return filterT9Attach(list,type);
  }
  /**
   * 过滤附件类型
   * @param list
   * @param type
   * @return
   */
  public  List filterT9Attach(List list,String type ){
	  List typeList = new ArrayList();
	  if(list == null ){
		  return list;
	  }
	  for(int i=0;i<list.size();i++){
		  T9Attach att = (T9Attach)list.get(i);
		  if(type.equals(parseFileNameType(att.getAttName()))){
			  att.setAttType(type);
			  typeList.add(att);
		  }
	  }
	  return typeList;
  }
  /**
   * 之后把格式 该在配置文件中 解析附件文件类型
   * @param fileName
   * @return
   */
  public String parseFileNameType(String fileName){
	  int extNameIndex = fileName.lastIndexOf(".");
	  String endName = "";
	  String[] imageArray = {".jpg",".jpeg",".bmp"
		        ,".gif",".png",".pcx",".tiff",".tga",".svg",".pod",".tif",".hdr"};
	  String[] audioArray = {".mp3",".mid",".wma",".vqf"};
	  String[] videoArray = {".wmv",".wav",".swf",".flv",".avi",".rmvb",".mov",".asf",".navi",".3gp",".mkv",".mpeg",".mp4"};//rm,flv不能播放
	    if(extNameIndex > 0){
	      endName = fileName.substring(extNameIndex).toLowerCase();
	    }
	    if(isExitsArray(imageArray,endName)){
	    	return "image";
	    }else if(isExitsArray(audioArray,endName)){
	    	return "audio";
	    }else if(isExitsArray(videoArray,endName)){
	    	return "vedio";
	    }else{
	    	return "other";
	    }
  }
  /**
   * 是否存在在数组中
   * @param array
   * @param indexStr
   * @return
   */
  private boolean isExitsArray(String[] array,String indexStr){
	    boolean isExits = false;
	    if(array != null){
	      for (int i = 0 ; i < array.length ; i++) {
	        if(array[i].equals(indexStr.trim())){
	        	isExits = true;
	        }
	      }
	    }
	    return isExits;
 }
 /**
  * 通过条件 获取content 不带分页的
  * @param dbConn
  * @param sWhere
  * @param nPageSize
  * @param nCurrentPage
  * @param nTotalSize
  * @return
  * @throws Exception
  */
  public static List getContentListByWher(Connection dbConn,String sWhere) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    ArrayList list;
        list = new ArrayList();
        T9CmsContent content = null;
	    StringBuffer sb = new StringBuffer();
        sb.append("select * from cms_content");
        if(sWhere != null && !sWhere.equals(""))
        {
            sb.append(" where ");
            sb.append(sWhere);
        }
        sb.append(" order by SEQ_ID desc");
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sb.toString());
	      while (rs.next()) {
	    	  content = new T9CmsContent();
	    	  content.setSeqId(rs.getInt("SEQ_ID"));
	    	  content.setContentName(rs.getString("CONTENT_NAME"));
	    	  list.add(content);
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return list;
  }
  /**
   * 获取指定条数的 记录数量
   * @param dbConn
   * @param sWhere
   * @param maxCount
   * @return
   * @throws Exception
   */
  public static List getContentListByWher(Connection dbConn,String sWhere,int maxCount) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    ArrayList list;
      list = new ArrayList();
      T9CmsContent content = null;
	    StringBuffer sb = new StringBuffer();
      sb.append("select * from cms_content");
      if(sWhere != null && !sWhere.equals(""))
      {
          sb.append(" where ");
          sb.append(sWhere);
      }
      sb.append(" order by SEQ_ID desc");
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sb.toString());
	      for(int i = 0; i < maxCount && rs.next(); i++){
	    	  content = new T9CmsContent();
	    	  content.setSeqId(rs.getInt("SEQ_ID"));
	    	  content.setContentName(rs.getString("CONTENT_NAME"));
	    	  list.add(content);
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return list;
}
  /**
   * 获取最大sql_ID
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public int getMaSeqId(Connection dbConn, String tableName) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    int maxSeqId = 0;
	    String sql = "select max(SEQ_ID) as SEQ_ID from " + tableName;
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sql);
	      if (rs.next()) {
	        maxSeqId = rs.getInt("SEQ_ID");
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return maxSeqId;
  }
  /**
   * 通过条件 获取最大的 
   * @param dbConn
   * @param tableName
   * @param sWhere
   * @return
   * @throws CException
   */
  public int getMaxSeqIdBySwhere(Connection dbConn,String tableName,String sWhere) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    int maxSeqId = 0;
	    StringBuffer sb = new StringBuffer();
        sb.append("select max(SEQ_ID) as SEQ_ID from");
        sb.append(tableName);
        if(sWhere != null && !sWhere.equals(""))
        {
            sb.append(" where ");
            sb.append(sWhere);
        }
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sb.toString());
	      if (rs.next()) {
	        maxSeqId = rs.getInt(1);
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return maxSeqId;
  }
  /**
   * 通过wher条件 获取表中记录数量
   * @param dbConn
   * @param tableName
   * @param sWhere
   * @return
   * @throws Exception
   */
  public int getMaxCountdBySwhere(Connection dbConn,String tableName,String sWhere) throws Exception {
	    Statement stmt = null;
	    ResultSet rs = null;
	    int count = 0;
	    StringBuffer sb = new StringBuffer();
      sb.append("select count(*) as num from");
      sb.append(tableName);
      if(sWhere != null && !sWhere.equals(""))
      {
          sb.append(" where ");
          sb.append(sWhere);
      }
	    try {
	      stmt = dbConn.createStatement();
	      rs = stmt.executeQuery(sb.toString());
	      if (rs.next()) {
	    	  count = rs.getInt("num");
	      }
	    } catch (Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    return count;
  }
  /**
   * 重构方法 获取最大记录数
   * @param dbConn
   * @param tableName
   * @return
   * @throws Exception
   */
  public int getMaxCountdBySwhere(Connection dbConn,String tableName) throws Exception {
	  return getMaxCountdBySwhere(dbConn,tableName,"");
  }
	
}
