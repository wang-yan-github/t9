package t9.subsys.portal.guoyan.leaderact.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.sun.jmx.snmp.Timestamp;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9LeaderactLogic {
 /**
  * 加载 显示
  * @param conn
  * @param newsType
  * @param limit 显示条数
  * @return
 * @throws Exception 
 */
  public StringBuffer loadNew(Connection conn,int limit) throws Exception{
    StringBuffer result = new StringBuffer();
    result = loadNew(conn, limit, 0);
    return result;
  }
  /**
   * 分页加载
   * @param conn
   * @param newsType
   * @param pageSize
   * @param pageNum
   * @return
   * @throws Exception 
   */
  public StringBuffer loadNew(Connection conn,int pageSize,int pageIndex) throws Exception{
    StringBuffer result = new StringBuffer();
    String sql = "SELECT " +
    		" NEWS_ID" +
    		",SUBJECT" +
    		",NEWS_TIME" +
    		" from " +
    		" NEWS " +
    		" where " +
    		" PUBLISH='1' and TO_ID='ALL_DEPT' order by NEWS_TIME desc";
    T9PageQueryParam queryParam = new T9PageQueryParam();
    String nameStr = "newId,subject,newsTime";
    queryParam.setNameStr(nameStr);
    queryParam.setPageIndex(pageIndex);
    queryParam.setPageSize(pageSize);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    result.append(pageDataList.toJson());
    return result;
  }
  public StringBuffer loadNew2(Connection conn,int newId) throws Exception{
    StringBuffer result = new StringBuffer();
    StringBuffer field = new StringBuffer();
    String sql = "SELECT " +
    " NEWS_ID" +
    ",SUBJECT" +
    ",NEWS_TIME" +
    " from " +
    " NEWS " +
    " where " +
    " PUBLISH='1' and TO_ID='ALL_DEPT' order by NEWS_TIME desc";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      
      while(rs.next()){
        int newsId = rs.getInt(1);
        String subject = rs.getString(2);
        Date newsTime = rs.getDate(3);
        if(!"".equals(field.toString())){
          field.append(",");
        }
        field.append("{")
          .append("newsId:").append(newsId).append(",")
          .append("subject:\"").append(T9Utility.encodeSpecial(subject)).append("\",")
          .append("newsTime:\"").append(T9Utility.getDateTimeStr(newsTime)).append("\"")
          .append("}");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    result.append("[").append(field).append("]");
    return result;
  }
  
  /**
   * 分页加载
   * @param conn
   * @param newsType
   * @param pageSize
   * @param pageNum
   * @return
   * @throws Exception 
   */
  public StringBuffer loadOneNew(Connection conn,int newId) throws Exception{
    StringBuffer result = new StringBuffer();
    String sql = "SELECT " +
        " NEWS_ID" +
        ",SUBJECT" +
        ",CONTENT" +
        ",NEWS_TIME" +
        " from " +
        " NEWS " +
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
        result.append("{")
          .append("subject:\"").append(T9Utility.encodeSpecial(subject)).append("\",")
          .append("content:\"").append(T9Utility.encodeSpecial(content)).append("\",")
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
}
