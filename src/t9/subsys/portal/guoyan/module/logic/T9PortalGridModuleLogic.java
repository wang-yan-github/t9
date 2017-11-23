package t9.subsys.portal.guoyan.module.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Map;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
/**
 * 
 * @author Think
 *
 */
public class T9PortalGridModuleLogic {
  /**
   * 加载数据
   * @param conn
   * @param params
   * @return
   * @throws Exception
   */
  public StringBuffer loadGridDataLogic(Connection conn,Map params) throws Exception{
    StringBuffer result = new StringBuffer();
    /**
     * 这些是从页面传过来的参数
     */
    Map<String ,String > paramsMap = T9PortalUtil.praserParams(params);
    String orderByField = paramsMap.get("orderBy");
    String orderBySort = paramsMap.get("sort");
    String newsType = paramsMap.get("type");
    String limit = paramsMap.get("limit");
    String start = paramsMap.get("start");
    String mappingName = paramsMap.get("paramName");
    String[] orderByFields = orderByField.split(",");
    String[] orderBySorts = orderBySort.split(",");
    String orderByStr = "";
    for (int i = 0; i < orderByFields.length; i++) {
      String field = orderByFields[i];
      String sort = orderBySorts[i];
      if(!"".equals(orderByStr)){
        orderByStr += ",";
      }
      orderByStr += field + " "  + sort;
    }
    if(!"".equals(orderByStr.trim())){
      orderByStr = " order by " + orderByStr;
    }
    /*****下面是SQL语句* 如果需要做查询可以组织where子句*****/
    String sql = "SELECT " +
    " NEWS_ID" +
    ",SUBJECT" +
    ",NEWS_TIME" +
    " from " +
    " drc_news_leader  " +
    " where " +
    " PUBLISH='1' and TYPE_ID='" + newsType + "' ";
    
    sql += orderByStr;
    
    T9PageQueryParam queryParam = new T9PageQueryParam();
    /*****此段返回的是json数据{newsId:"",subject:"",newsTime:""}
     * mappingName 的值为 "newsId,subject,newsTime"  ，可以自己定义，这个地方主要是为了页面js调用JSON
     * 1.这个地方是JSON中的name值,需要和SQL语句中的字段顺序对应
     * mappingName值: newsId,subject,newsTime
     * sql字段: NEWS_ID,SUBJECT,NEWS_TIME
     * 返回的结果：{newsId:"数据库中NEWS_ID的值",subject:"数据库中SUBJECT的值",newsTime:"数据库中NEWS_TIME的值"}
     * ******/
    queryParam.setNameStr(mappingName);
    queryParam.setPageIndex(Integer.valueOf(start));
    queryParam.setPageSize(Integer.valueOf(limit));
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    result = T9PortalUtil.toJson(pageDataList);
    /**********/
    return result;
  }

  /**
   * 加载单条数据JSON格式
   * @param conn
   * @param newId
   * @return
   * @throws Exception
   */
  public StringBuffer loadOneData(Connection conn,int newId) throws Exception{
    StringBuffer result = new StringBuffer();
    String sql = "SELECT " +
        " NEWS_ID" +
        ",SUBJECT" +
        ",CONTENT" +
        ",NEWS_TIME" +
        ",PROVIDER " +
        " from " +
        " drc_news_leader " +
        " where " +
        " NEWS_ID = " + newId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String subject = rs.getString(2);
        String content = rs.getString(3);
        Date newsTime = rs.getDate(4);
        String userId = rs.getString(5);
        String userName = T9PortalUtil.getUserNameById(conn, userId);
        /***此处组织JSON数据*字段的多少你可以根据需求添加***/
        result.append("{")
          .append("subject:\"").append(T9Utility.encodeSpecial(subject)).append("\",")
          .append("content:\"").append(T9Utility.encodeSpecial(content)).append("\",")
          .append("publish:\"").append(T9Utility.encodeSpecial(userName)).append("\",")
          .append("newsTime:\"").append(T9Utility.getDateTimeStr(newsTime)).append("\"")
          .append("}");
        /*******/
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  /**
   * 这是带有分页的，和无分页的很类似
   * @param conn
   * @param pageSize
   * @param pageIndex
   * @return
   * @throws Exception
   */
  public StringBuffer loadDataPage(Connection conn,int pageSize,int pageIndex,String newsType) throws Exception{
    StringBuffer result = new StringBuffer();
    String orderByStr = "";
    orderByStr = " order by NEWS_TIME desc " ;
    String sql = "SELECT " +
    " NEWS_ID" +
    ",SUBJECT" +
    ",NEWS_TIME" +
    " from " +
    " drc_news_leader  " +
    " where " +
    " PUBLISH='1' and TYPE_ID='" + newsType + "' ";
    
    sql += orderByStr;
    T9PageQueryParam queryParam = new T9PageQueryParam();
    String nameStr = "newId,subject,newsTime";
    queryParam.setNameStr(nameStr);
    queryParam.setPageIndex(pageIndex);
    queryParam.setPageSize(pageSize);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    result.append(pageDataList.toJson());
    return result;
  }
  /**
   * 这是一个工具方法，根据userId取得用户名称
   * @return
   * @throws Exception 
   */
  public String getUserNameById(Connection conn,String userId) throws Exception{
    String result = "";
    String sql = "select user_name from user where user_Id='" + userId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  /**
   * 这是一个工具方法，根据用户名称取得userId
   * @return
   * @throws Exception 
   */
  public String getUserIdByName(Connection conn,String userName) throws Exception{
    String result = "";
    String sql = "select user_id from user where user_name='" + userName + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  
  /**
   * 这是一个工具方法，根据部门ID取得部门名称
   * @return
   * @throws Exception 
   */
  public String getDeptIdByName(Connection conn,String deptName) throws Exception{
    String result = "";
    String sql = "select DEPT_ID from department where DEPT_NAME='" + deptName + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
}
