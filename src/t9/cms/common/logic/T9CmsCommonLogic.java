package t9.cms.common.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.cms.column.data.T9CmsColumn;
import t9.cms.content.data.T9CmsContent;
import t9.cms.station.data.T9CmsStation;
import t9.cms.template.data.T9CmsTemplate;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9CmsCommonLogic {
  public static final String attachmentFolder = "cms";
  /**
   * 读取文章模板文件，转换模板内容--文章
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer readFile(Connection conn, File templateFile, T9CmsContent content) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, content.getStationId());
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, content.getColumnId());
    List<T9CmsContent> contentList = new ArrayList<T9CmsContent>();
    contentList.add(content);
    StringBuffer sb = resolve(conn, templateFile, station, column, contentList);
    return sb;
  }
  
  /**
   * 读取索引模板文件，转换模板内容--栏目
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer readFile(Connection conn, File templateFile, T9CmsStation station, T9CmsColumn column, List<T9CmsContent> contentList, int... i) throws Exception{
    StringBuffer sb = resolve(conn, templateFile, station, column, contentList, i);
    return sb;
  }
  
  /**
   * 读取索引模板文件，转换模板内容--站点
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public StringBuffer readFile(Connection conn, File templateFile, T9CmsStation station) throws Exception{
    StringBuffer sb = resolve(conn, templateFile, station, null, null);
    return sb;
  }
  
  public StringBuffer resolve(Connection conn, File templateFile, T9CmsStation station, T9CmsColumn column, List<T9CmsContent> contentList, int... i) throws Exception{
    StringBuffer sb = new StringBuffer("");
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(templateFile),"utf-8"));
      String tempStr = "";
      String foreachStr = "";
      String columnParameters = "";
      boolean foreachBoolean = false;
      int line = 0;
      while((tempStr = reader.readLine()) != null){
        
        //计数器--记录行数
        line++;
        
        //解决读文件首字节出现特殊编码字符的问题--导致模板换行
        if(line == 1){
          String ttt = new String(tempStr.getBytes(),"utf-8");
          if(ttt.startsWith("?")){
            tempStr = ttt.substring(1, ttt.length());
          }
        }
        
        //包含模板
        if(tempStr.contains("#parse(")){
          T9ORM orm = new T9ORM();
          columnParameters = tempStr.substring(tempStr.indexOf("(")+1, tempStr.indexOf(")"));
          Map<String,String> map = new HashMap<String,String>();
          map.put("TEMPLATE_NAME", columnParameters);
          T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, map);
          if(template != null){
            File templateFileContain = new File(T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator + template.getAttachmentId() + "_" + template.getAttachmentName());
            tempStr = tempStr.replace("#parse("+columnParameters+")", readFile(conn, templateFileContain, station, column, contentList).toString());
            sb.append(tempStr);
          }
          columnParameters = "";
          continue;
        }
        
        //对循环处理--文章foreach循环
        if(tempStr.contains("#foreachContent")){
          if(tempStr.contains("#foreachContent(")){
            columnParameters = tempStr.substring(tempStr.indexOf("(")+1, tempStr.indexOf(")"));
            foreachStr = foreachStr + tempStr.substring(tempStr.indexOf("#foreachContent("), tempStr.length()).replace("#foreachContent("+columnParameters+")", "") + "\n";
          }
          else{
            foreachStr = foreachStr + tempStr.substring(tempStr.indexOf("#foreachContent"), tempStr.length()).replaceAll("#foreachContent", "") + "\n";
          }
          tempStr = tempStr.substring(0,tempStr.indexOf("#foreachContent"));
          foreachBoolean = true;
        }
         
        if(tempStr.contains("#endContent") && foreachBoolean){
          
          //判断是否有参数
          if(!T9Utility.isNullorEmpty(columnParameters)){
            foreachStr = foreachStr + tempStr.substring(0, tempStr.indexOf("#endContent")) + "\n";
            
            //获取出来的list 是有排序的
            T9ORM orm = new T9ORM();
            String[] parameter = columnParameters.split(",");
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("COLUMN_NAME", parameter[0]);   //parameter[0]栏目名称
            T9CmsColumn columnInfo = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, map);
            String filters[] = {" COLUMN_ID =" + columnInfo.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
            List<T9CmsContent> contentListTemp = orm.loadListSingle(conn, T9CmsContent.class, filters);
            List<T9CmsContent> contentListInfo = new ArrayList<T9CmsContent>();
            int count = 0;
            if(contentListTemp.size() > Integer.parseInt(parameter[1])){    //parameter[1]显示条数
              for(T9CmsContent content : contentListTemp){
                contentListInfo.add(content);
                count++;
                if(count == Integer.parseInt(parameter[1])){
                  break;
                }
              }
            }
            else{
              contentListInfo = contentListTemp;
            }
            
            tempStr = getForeachContent(conn, foreachStr, station, contentListInfo) + tempStr.substring(tempStr.indexOf("#endContent"), tempStr.length()).replaceAll("#endContent", "");
          }
          else{
            
            T9ORM orm = new T9ORM();
            foreachStr = foreachStr + tempStr.substring(0, tempStr.indexOf("#endContent")) + "\n";
            tempStr = getForeachContent(conn, foreachStr, station, contentList) + tempStr.substring(tempStr.indexOf("#endContent"), tempStr.length()).replaceAll("#endContent", "");
            
            //是否分页，如果分页则显示分页信息
            if(column.getPaging() == 1){
              int maxIndexPage = column.getMaxIndexPage();
              int pagingNumber = column.getPagingNumber(); 
              
              String filters[] = {" COLUMN_ID =" + column.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
              List<T9CmsContent> contentListAll = orm.loadListSingle(conn, T9CmsContent.class, filters);
              int total = contentListAll.size();
              
              int page = total/pagingNumber + (total%pagingNumber > 0 ? 1 : 0);
              if(page > maxIndexPage){
                total = maxIndexPage * pagingNumber;
              }
              page = page > maxIndexPage ? maxIndexPage : page;
              
              T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, column.getTemplateIndexId());
              String fileName = template.getTemplateFileName();
              String prv = "";
              String next = "";
              if(i[0] - 1 <= 0){
                prv = fileName+"."+station.getExtendName();
              }
              else{
                prv = fileName+(i[0] - 1)+"."+station.getExtendName();
              }
              if(i[0] + 1 >= page){
                next = fileName+(page-1)+"."+station.getExtendName();
              }
              else{
                next = fileName+(i[0]+1)+"."+station.getExtendName();
              }
              
              tempStr = tempStr +" <table width=\"100%\" border=\"0\" cellspacing=\"5\" cellpadding=\"0\">\n " 
                                + "<tr>\n " 
                                + "<td align=\"center\" class=\"dahei\"><table> " 
                                + "共"+ total +"条新闻，分"+page+"页，当前第<font color=red>" + (i[0]+1) + "</font>页&nbsp;&nbsp;"
                                +	"<a href=\""+(fileName+"."+station.getExtendName())+"\">最前页</a> " 
                                + "<a href=\""+prv+"\">上一页</a> " 
                                + "<a href=\""+next+"\">下一页</a> " 
                                + "<a href=\""+(fileName+(page-1)+"."+station.getExtendName())+"\">最后页</a> " 
                                + "</table> ";
            }
          }
          foreachStr = "";
          foreachBoolean = false;
        }

        //对循环处理
        if(tempStr.contains("#foreachColumn")){
          foreachStr = foreachStr + tempStr.substring(tempStr.indexOf("#foreachColumn"), tempStr.length()).replaceAll("#foreachColumn", "") + "\n";
          tempStr = tempStr.substring(0,tempStr.indexOf("#foreachColumn"));
          foreachBoolean = true;
        }
        if(tempStr.contains("#endColumn") && foreachBoolean){
          foreachStr = foreachStr + tempStr.substring(0, tempStr.indexOf("#endColumn")) + "\n";
          tempStr = getForeachColumn(conn, foreachStr, station) + tempStr.substring(tempStr.indexOf("#endColumn"), tempStr.length()).replaceAll("#endColumn", "");
          foreachStr = "";
          foreachBoolean = false;
        }
        if(foreachBoolean){
          foreachStr = foreachStr + tempStr + "\n";
          continue;
        }
        
        //替换站点名称
        if(tempStr.contains("$CMSstation.getStationName")){
          tempStr = tempStr.replaceAll("\\$CMSstation\\.getStationName", station.getStationName());
        }
        //替换站点首页url
        if(tempStr.contains("$CMSstation.getUrl")){
          T9ORM orm = new T9ORM();
          T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
          String fileName = template.getTemplateFileName();
          tempStr = tempStr.replaceAll("\\$CMSstation\\.getUrl", "/t9/" + station.getStationPath() + "/" + fileName + "." + station.getExtendName());
        }
        //替换栏目名称
        if(tempStr.contains("$CMScolumn.getColumnName")){
          tempStr = tempStr.replaceAll("\\$CMScolumn\\.getColumnName", column.getColumnName());
        }
        //替换栏目url
        if(tempStr.contains("$CMScolumn.getUrl")){
          //替换指定栏目url 带参数
          if(tempStr.contains("$CMScolumn.getUrl(")){
            String columnName = tempStr.substring(tempStr.indexOf("(")+1, tempStr.indexOf(")"));
            T9ORM orm = new T9ORM();
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("COLUMN_NAME", columnName);
            T9CmsColumn columnInfo = (T9CmsColumn) orm.loadObjSingle(conn, T9CmsColumn.class, map);
            if(columnInfo != null){
              T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
              String fileName = template.getTemplateFileName();
              String path = getColumnPath(conn, columnInfo.getSeqId());
              fileName = "/t9/" + station.getStationPath() + "/" + path + "/" + fileName + "." + station.getExtendName();
              tempStr = tempStr.replace("$CMScolumn.getUrl("+ columnName +")", fileName);
            }
            else{
              tempStr = tempStr.replace("$CMScolumn.getUrl("+ columnName +")", "");
            }
          }
        }
        //替换文章名称
        if(tempStr.contains("$CMScontent.getContentName")){
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentName", contentList.get(0).getContentName());
        }
        //替换文章作者
        if(tempStr.contains("$CMScontent.getContentAuthor")){
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentAuthor", contentList.get(0).getContentAuthor());
        }
        //替换文章来源
        if(tempStr.contains("$CMScontent.getContentSource")){
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentSource", contentList.get(0).getContentSource());
        }
        //替换文章发布日期
        if(tempStr.contains("$CMScontent.getContentDate")){
          String date = "";
          if(contentList.get(0).getContentDate() != null){
            date = contentList.get(0).getContentDate().toString().substring(0,19);
          }
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentDate", date);
        }
        //替换文章内容
        if(tempStr.contains("$CMScontent.getContent")){
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContent", contentList.get(0).getContent());
        }
        //替换所在位置
        if(tempStr.contains("#getLocation")){
          tempStr = tempStr.replaceAll("#getLocation", getLocation(conn, column, "../") + " >" + column.getColumnName());
//          System.out.println(getLocation(conn, column, "../") + " >> " + column.getColumnName());
        }
        sb.append(tempStr+"\n");
      }
    }catch(Exception e){
      throw e;
    }
    finally{
      reader.close();
    }
    return sb;
  }
  
  
  
  
  
  
  /**
   * 查询循环中的信息
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getForeachContent(Connection conn, String foreachStr, T9CmsStation station, List<T9CmsContent> contentList) throws Exception{
    String data = "";
    try{
      for(T9CmsContent content : contentList){
        String tempStr = foreachStr;
        if(tempStr.contains("$CMScontent.getContentName")){
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentName", content.getContentName());
        }
        if(tempStr.contains("$CMScontent.getContentDate")){
          String date = "";
          if(content.getContentDate() != null){
            date = content.getContentDate().toString().substring(0,19);
          }
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getContentDate", date);
        }
        if(tempStr.contains("$CMScontent.getUrl")){
          String fileName = content.getContentFileName();
          if(T9Utility.isNullorEmpty(content.getContentFileName())){
            fileName = content.getSeqId()+"";
          }
          String path = getColumnPath(conn, content.getColumnId());
          fileName = "/t9/" + station.getStationPath() + "/" + path + "/" + fileName + "." + station.getExtendName();
          tempStr = tempStr.replaceAll("\\$CMScontent\\.getUrl", fileName);
        }
        data = data + tempStr;
      }
    }catch(Exception e){
      throw e;
    }
    return data;
  }
  
  /**
   * 查询循环中的信息
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getForeachColumn(Connection conn, String foreachStr, T9CmsStation station) throws Exception{
    String data = "";
    T9ORM orm = new T9ORM();
    
    //获取出来的list 是有排序的
    String filters[] = {" STATION_ID =" + station.getSeqId() + " and PARENT_ID =" + 0 + " order by COLUMN_INDEX desc "};
    List<T9CmsColumn> columnList = orm.loadListSingle(conn, T9CmsColumn.class, filters);
    try{
      for(T9CmsColumn column : columnList){
        String tempStr = foreachStr;
        if(tempStr.contains("$CMScolumn.getColumnName")){
          tempStr = tempStr.replaceAll("\\$CMScolumn\\.getColumnName", column.getColumnName());
        }
        if(tempStr.contains("CMScolumn.getUrl")){
          T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
          String fileName = template.getTemplateFileName();
          String path = getColumnPath(conn, column.getSeqId());
          fileName = "/t9/" + station.getStationPath() + "/" + path + "/" + fileName + "." + station.getExtendName();
          tempStr = tempStr.replaceAll("\\$CMScolumn\\.getUrl", fileName);
        }
        data = data + tempStr;
      }
    }catch(Exception e){
      throw e;
    }
    return data;
  }
  
  /**
   * 递归获取目录结构
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getColumnPath(Connection dbConn, int columnId) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsColumn column = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, columnId);
    String parentPath = column.getColumnPath();
    if(column.getParentId() > 0){
      parentPath = getColumnPath(dbConn, column.getParentId()) + "/" + parentPath;
    }
    return parentPath;
  }
  
  /**
   * 递归获取栏目地址
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getLocation(Connection dbConn, T9CmsColumn column, String temp) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, column.getStationId());
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(dbConn, T9CmsTemplate.class, column.getTemplateIndexId());
    String location = "";
    if(column.getParentId() == 0){
      location = "<a href=\""+temp+template.getTemplateFileName()+"."+station.getExtendName()+"\">首页</a>";
    }
    else{
      T9CmsColumn columnParent = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, column.getParentId());
      String abc = getLocation(dbConn, columnParent, temp+"../");
      location = abc + " ><a href=\""+temp+template.getTemplateFileName()+"."+station.getExtendName()+"\">" + columnParent.getColumnName() + "</a>";
    }
    return location;
  }
  
  public T9CmsStation getStationInfo(Connection conn, int stationId) throws Exception{
    T9ORM orm = new T9ORM();
    T9CmsStation station = (T9CmsStation) orm.loadObjSingle(conn, T9CmsStation.class, stationId);
    T9CmsTemplate template = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
    if(template != null){
      //一级栏目，并设置栏目url
      //获取出来的list 是有排序的
      String filters[] = {" STATION_ID =" + stationId + " order by COLUMN_INDEX asc "};
      List<T9CmsColumn> columnList = orm.loadListSingle(conn, T9CmsColumn.class, filters);
      for(T9CmsColumn columnTemp : columnList){
        if(columnTemp != null){
          T9CmsTemplate columnTemplate = (T9CmsTemplate) orm.loadObjSingle(conn, T9CmsTemplate.class, station.getTemplateId());
          String columnFileName = columnTemplate.getTemplateFileName();
          String pathColumn = getColumnPath(conn, columnTemp.getSeqId());
          columnFileName = "/t9/" + station.getStationPath() + "/" + pathColumn + "/" + columnFileName + "." + station.getExtendName();
          columnTemp.setUrl(columnFileName);
          
          //当前栏目下的文章，并设置文章url
          //获取出来的list 是有排序的
          String filtersContent[] = {" COLUMN_ID =" + columnTemp.getSeqId() + " and CONTENT_STATUS = 5 order by CONTENT_TOP desc, CONTENT_INDEX desc "};
          List<T9CmsContent> contentList = orm.loadListSingle(conn, T9CmsContent.class, filtersContent);
          for(T9CmsContent content : contentList){
            String contentFileName = content.getContentFileName();
            if(T9Utility.isNullorEmpty(content.getContentFileName())){
              contentFileName = content.getSeqId()+"";
            }
            String pathContent = getColumnPath(conn, content.getColumnId());
            contentFileName = "/t9/" + station.getStationPath() + "/" + pathContent + "/" + contentFileName + "." + station.getExtendName();
            content.setUrl(contentFileName);
          }
          columnTemp.setContentList(contentList);
        }
      }
      
      station.setColumnList(columnList);
      //站点及站点url
      station.setUrl("/t9/" + station.getStationPath() + "/" + template.getTemplateFileName() + "." + station.getExtendName());
    }
    return station;
  }
}
