package t9.mobile.chat.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.message.BasicNameValuePair;

import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.servlet.T9SessionListener;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9ChatGroupLogic {

  public String getGroupList(Connection dbConn, int seqId) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt2= null;
    ResultSet rs = null;
    String userIds = "";
    Map<String, String> map = new HashMap();
    StringBuffer sb = new StringBuffer("[");
    int count = 0 ;
    try{
      stmt2 = dbConn.createStatement();
      rs = stmt2.executeQuery("select * FROM CHAT_GROUP WHERE USER_ID =" + seqId);
      while (rs.next()){
        String groupName = rs.getString("GROUP_NAME");
        String groupId = rs.getString("SEQ_ID");
        userIds = T9Utility.null2Empty(rs.getString("USERS"));
        String user = this.getUsers(dbConn, userIds);
        
        count++;
        sb.append("{\"groupName\":\"").append(T9Utility.encodeSpecial(groupName)).append("\",\"groupId\":\"").append(groupId).append("\",\"users\":").append(user).append("},");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt2, rs, null);
    }
    if (count > 0 ){
      sb.deleteCharAt(sb.length() -1);
    }
    sb.append("]");
    return sb.toString();
  }
  public String getUsers(Connection conn, String users) throws Exception {
    Statement stmt2= null;
    ResultSet rs = null;
    Map<String, String> map = new HashMap();
    if (users.endsWith(",")) {
      users = users.substring(0, users.length() -1);
    }
    if (T9Utility.isNullorEmpty(users)) {
      return "[]";
    }
    Map userStates = T9SessionListener.getUserStateMap();
    StringBuffer sb = new StringBuffer("[");
    try{
      stmt2 = conn.createStatement();
      int count = 0;
      rs = stmt2.executeQuery("select PERSON.SEQ_ID , DEPT_ID, USER_ID,DEPT_NAME,PRIV_NAME,USER_NAME,MY_STATUS,AUATAR,SEX FROM PERSON,DEPARTMENT,USER_PRIV WHERE PERSON.SEQ_ID IN (" + users + ") AND PERSON.DEPT_ID = DEPARTMENT.SEQ_ID AND PERSON.USER_PRIV = USER_PRIV.SEQ_ID");
      while (rs.next()){
        String userName = rs.getString("USER_NAME");
        String userId = rs.getString("SEQ_ID");
        String avatar = T9Utility.null2Empty(rs.getString("AUATAR"));
        String sex = rs.getString("SEX");
        String myStatus = rs.getString("MY_STATUS");
        String uid = rs.getString("USER_ID");
        int deptId = rs.getInt("DEPT_ID");
        String longName = T9MobileUtility.getLongDept(conn, deptId);
        if (longName.startsWith("//")) {
          longName = longName.substring(1 , longName.length());
        }
        String deptName =T9Utility.encodeSpecial(longName);
        String privName =T9Utility.encodeSpecial(rs.getString("PRIV_NAME"));
        
        
        map.put("userId", userId);
        map.put("uid", uid);
        map.put("userName", T9Utility.encodeSpecial(userName));
        map.put("deptName", deptName);
        map.put("privName", privName);
        map.put("message", T9Utility.encodeSpecial(myStatus));
        map.put("avatar", T9Utility.encodeSpecial(T9MobileUtility.showAvatar(avatar, sex)));
        String loginType = "";
        if (userStates.containsKey(Integer.parseInt(userId))) {
          loginType = (String)userStates.get(Integer.parseInt(userId));
        }
        map.put("online", loginType);
        sb.append(T9MobileUtility.mapToJson(map)).append(",");
        count++;
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length()-1);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt2, rs, null);
    }
    sb.append("]");
    //System.out.println(sb.toString());
    return sb.toString();
  }
  public String addGroup(Connection conn, int seqId , String groupName , String userIds) throws Exception {
    // TODO Auto-generated method stub

    if (T9Utility.isNullorEmpty(userIds)) {
      userIds = "";
    }
    
    if (userIds.endsWith(",")) {
      userIds = userIds.substring(0, userIds.length() -1);
    }
    String sql = "insert into  CHAT_GROUP ( GROUP_NAME, USER_ID , USERS) VALUES (?,?,?)";
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(sql);
      stmt.setString(1, groupName);
      stmt.setInt(2, seqId);
      stmt.setString(3, userIds);
      stmt.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
    int c = 0;
    Statement stmt2= null;
    ResultSet rs = null;
    try{
      stmt2 = conn.createStatement();
      rs = stmt2.executeQuery("select max(SEQ_ID) FROM CHAT_GROUP WHERE USER_ID =" + seqId);
      if(rs.next()){
        c = rs.getInt(1);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt2, rs, null);
    }
    String user = this.getUsers(conn, userIds);
    StringBuffer sb = new StringBuffer();
    sb.append("{\"groupName\":\"").append(T9Utility.encodeSpecial(groupName)).append("\",\"groupId\":\"").append(c).append("\",\"users\":").append(user).append("}");
    return sb.toString();
  }

  public String updateGroup(Connection dbConn,String groupId , String groupName , String userIds) throws Exception {
    // TODO Auto-generated method stub
    if (T9Utility.isNullorEmpty(userIds)) {
      userIds = "";
    }
    
    if (userIds.endsWith(",")) {
      userIds = userIds.substring(0, userIds.length() -1);
    }
    
    String sql = "update CHAT_GROUP set GROUP_NAME = ? , USERS = ? WHERE SEQ_ID = " + groupId;
    PreparedStatement stmt = null;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1, groupName);
      stmt.setString(2, userIds);
      stmt.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
    
    String user = this.getUsers(dbConn, userIds);
    StringBuffer sb = new StringBuffer();
    sb.append("{\"groupName\":\"").append(T9Utility.encodeSpecial(groupName)).append("\",\"groupId\":\"").append(groupId).append("\",\"users\":").append(user).append("}");
    return sb.toString();
  }

  public String getGroupById(Connection dbConn, String groupId) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt2= null;
    ResultSet rs = null;
    String userIds = "";
    Map<String, String> map = new HashMap();
    try{
      stmt2 = dbConn.createStatement();
      rs = stmt2.executeQuery("select * FROM CHAT_GROUP WHERE SEQ_ID =" + groupId);
      if(rs.next()){
        String groupName = rs.getString("GROUP_NAME");
        map.put("groupName", T9Utility.encodeSpecial(groupName));
         userIds = T9Utility.null2Empty(rs.getString("USERS"));
         map.put("userIds", userIds);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt2, rs, null);
    }
    T9PersonLogic logic = new T9PersonLogic();
    String userNames = T9Utility.encodeSpecial(logic.getNameBySeqIdStr(userIds, dbConn));
    map.put(userNames, userIds);
    return T9MobileUtility.mapToJson(map);
  }

  public void deleteGroupById(Connection dbConn, String groupId) throws Exception {
    // TODO Auto-generated method stub
    String sql = "delete from CHAT_GROUP WHERE SEQ_ID = " + groupId;
    T9MobileUtility.updateSql(dbConn, sql);
  }
}
