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
public class T9PortalGridModuleNotifyLogic {
  /**
   * 加载数据
   * @param conn
   * @param params
   * @return
   * @throws Exception
   */
  public StringBuffer loadGridDataLogic(Connection conn,Map params) throws Exception{
    StringBuffer result = new StringBuffer();
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
    String sql = "SELECT " +
    " NOTIFY_ID" +
    ",SUBJECT" +
    ",SEND_TIME" +
    " from " +
    " drc_notify  " +
    " where " +
    " PUBLISH='1' and TYPE_ID='" + newsType + "' ";
    
    sql += orderByStr;
    
    T9PageQueryParam queryParam = new T9PageQueryParam();
    queryParam.setNameStr(mappingName);
    queryParam.setPageIndex(Integer.valueOf(start));
    queryParam.setPageSize(Integer.valueOf(limit));
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    result = T9PortalUtil.toJson(pageDataList);
    return result;
  }

  public StringBuffer loadOneData(Connection conn,int newId) throws Exception{
    StringBuffer result = new StringBuffer();
    String sql = "SELECT " +
        " NOTIFY_ID" +
        ",SUBJECT" +
        ",COMPRESS_CONTENT" +
        ",SEND_TIME" +
        ",FROM_ID " +
        " from " +
        " drc_notify " +
        " where " +
        " NOTIFY_ID = " + newId;
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
        result.append("{")
          .append("subject:\"").append(T9Utility.encodeSpecial(subject)).append("\",")
          .append("content:\"").append(T9Utility.encodeSpecial(content)).append("\",")
          .append("publish:\"").append(T9Utility.encodeSpecial(userId)).append("\",")
          .append("newsTime:\"").append(T9Utility.getDateTimeStr(newsTime)).append("\"")
          .append("}");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  /**
   * 
   * @param conn
   * @param pageSize
   * @param pageIndex
   * @return
   * @throws Exception
   */
  public StringBuffer loadDataPage(Connection conn,int pageSize,int pageIndex,String newsType) throws Exception{
    StringBuffer result = new StringBuffer();
    String orderByStr = "";
    orderByStr = " order by SEND_TIME desc " ;
    String sql = "SELECT " +
    " NOTIFY_ID" +
    ",SUBJECT" +
    ",SEND_TIME" +
    " from " +
    " drc_notify  " +
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
}
