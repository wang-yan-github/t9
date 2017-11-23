package t9.core.esb.server.user.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.esb.server.user.data.TdUser;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class TdUserLogic {
  private static Logger log = Logger.getLogger("esb");
  /**
   * 新建用户
   * 
   * @param dbConn
   * @param fileForm
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public int addUserLogic(Connection dbConn, Map map) throws Exception {
    T9ORM orm = new T9ORM();
    String userCode = (String)map.get("userCode");
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = " select 1 from td_user where user_code = '" + userCode + "'";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return -1;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    String userName = (String)map.get("userName");
    String password = (String)map.get("password");
    int appId = Integer.parseInt((String)map.get("appId"));
    int userType = Integer.parseInt((String)map.get("userType"));
    int status = Integer.parseInt((String)map.get("status"));
    String description = (String)map.get("description");

    TdUser tdUser = new TdUser();
    tdUser.setUserCode(userCode);
    tdUser.setUserName(userName);
    tdUser.setPassword(T9DigestUtility.md5Hex(password.getBytes()));
    tdUser.setAppId(appId);
    tdUser.setUserType(userType);
    tdUser.setStatus(status);
    tdUser.setDescription(description);
    
    orm.saveSingle(dbConn, tdUser);
    return 1;
  }
  
  /**
   * 用户列表
   * 
   * @param dbConn
   * @param request
   * @return @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String getUserListLogic(Connection dbConn, Map request  , String userCode, String  userCode1 , String userName) throws Exception {
    try {
      String sql = " SELECT seq_id , user_code , user_name , user_type , status , description ,1 FROM td_user t where 1=1 ";
      if (!T9Utility.isNullorEmpty(userCode1)) {
        sql += " and user_code = '" + userCode1 + "'"; 
      }
      if (!T9Utility.isNullorEmpty(userName)) {
        sql += " and user_name like '%" + userName + "%'";
      }
      
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      
      for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
        T9DbRecord record = pageDataList.getRecord(i);
        String userCode2 = (String)record.getValueByName("userCode");
        if (userCode.endsWith(userCode2)) {
          record.updateField("moniter", "1");
        } else {
          record.updateField("moniter", "0");
        }
      }
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
  public TdUser getUserDetailLogic(Connection conn, int seqId) throws Exception {
    String sql = " SELECT * FROM td_user where seq_id =" + seqId;
    TdUser user = new TdUser();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        user.setSeqId(rs.getInt("seq_id"));
        user.setUserCode(rs.getString("user_code"));
        user.setUserName(rs.getString("user_name"));
        user.setDescription(rs.getString("description"));
        user.setAppId(rs.getInt("app_id"));
        user.setUserType(rs.getInt("user_type"));
        user.setStatus(rs.getInt("status"));
        user.setIsOnline(rs.getInt("is_online"));
        user.setOnlineIp(rs.getString("online_Ip"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return user;
  }
  public String getEsbClientMessage(Connection conn, int seqId) throws Exception {
    String sql = " SELECT * FROM ESB_CLIENT_MESSAGE where USER_ID =" + seqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    int count = 0 ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        count++;
        sb.append("{time:\"").append(T9Utility.getDateTimeStr(rs.getTimestamp("time"))).append("\"");
        sb.append(",message:\"").append(T9Utility.encodeSpecial(rs.getString("message"))).append("\"},");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  
  /**
   * 修改用户
   * 
   * @param dbConn
   * @param fileForm
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void updateUserLogic(Connection dbConn, Map map) throws Exception {
    int seqId = Integer.parseInt((String)map.get("seqId"));
    String userName = (String)map.get("userName");
    int appId = Integer.parseInt((String)map.get("appId"));
    int userType = Integer.parseInt((String)map.get("userType"));
    int status = Integer.parseInt((String)map.get("status"));
    String description = (String)map.get("description");

    PreparedStatement ps = null;
    String sql = " update td_user set "
               + " user_name='" + userName +"'"
               + " ,app_id=" + appId 
               + " ,user_type=" + userType 
               + " ,status=" + status 
               + " ,description='" + description +"'"
               + " where seq_id=" + seqId; 
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
  
  /**
   * 删除用户
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteUserLogic(Connection dbConn, String seqIdStr) throws Exception {

    PreparedStatement ps = null;
    String sql = " delete from td_user where seq_id =" + seqIdStr;
    try {
      ps = dbConn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
  /**
   * 
   * @param dbConn
   * @param seqIdStr
   * @throws Exception
   */
  public void deleteUserMessage(Connection conn, int userId) throws Exception {
    String ss = this.getEsbClientMessage(conn, userId);
    log.debug(ss);
    PreparedStatement ps = null;
    String sql = " delete FROM ESB_CLIENT_MESSAGE where USER_ID =" + userId;
    try {
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, null);
    }
  }
  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
    if (T9Utility.isNullorEmpty(userIdStr)) {
      userIdStr = "-1";
    }
    if (userIdStr.endsWith(",")) {
      userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
    }
    String result = "";
    String sql = " select USER_NAME from td_user where SEQ_ID IN (" + userIdStr + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String toId = rs.getString(1);
        if (!"".equals(result)) {
          result += ",";
        }
        result += toId;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
}
