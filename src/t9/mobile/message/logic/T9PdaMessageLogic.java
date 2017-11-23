package t9.mobile.message.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.message.data.T9MessageBack;
import t9.core.funcs.message.logic.T9MessageUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.interfaces.data.T9SysPara;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.servlet.T9SessionListener;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaMessageLogic {
  public void sendVoiceMsg(Connection conn , int formId , String toId  , String content , Date sendTime) throws Exception {
    T9MessageBack mb = new T9MessageBack();
    
    content = content.replace("\n", "<br />\n");
    content = content.replace("\n", "<br />\r");
    
    mb.setContent(content);
    mb.setFromId(formId);
    mb.setRemindUrl("");
    mb.setSendDate(sendTime);
    mb.setSmsType("0");
    mb.setToId(toId);
    T9MessageUtil.smsBack(conn, mb);
    T9MsgPusher.mobilePushNotification(toId, content, "message");
  }
  
  public String getSingleNewMsg(Connection dbConn , int person , int fromId , String p_VER , String type) throws Exception {
    String sql = "";
    SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    String ss = dateFormat.format(date);
    String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME", ss, "<=");
    String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", ss, " <= ");
    
    if ("ME".equals(type)) {
      sql = "SELECT T1.SEQ_ID,FROM_ID,SEND_TIME,CONTENT,MESSAGE_TYPE FROM message T1 ,message_BODY T0 WHERE TO_ID=" + fromId + " AND FROM_ID='"+ person +"'  AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
          "and DELETE_FLAG in (0, 2) " +
          "AND " + dateFiler +
          " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ") order by T1.SEQ_ID desc";
    }else{
       sql = "SELECT T1.SEQ_ID,FROM_ID,SEND_TIME,CONTENT,MESSAGE_TYPE FROM message T1 ,message_BODY T0 WHERE TO_ID=" + person + " AND FROM_ID='"+ fromId +"' AND REMIND_FLAG = '1' AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
          "and DELETE_FLAG in (0, 2) " +
          "AND " + dateFiler +
          " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ") order by T1.SEQ_ID desc";
    }
    
    StringBuffer sb = new StringBuffer("[");
    int rc = T9QuickQuery.getCount(dbConn, sql);
    String ids = "";
    if (rc > 0) {
    Statement stmt = null;
    ResultSet rs = null;
    boolean flag = true;
    int count = 0;
    Map<Integer , Map<String , String>> map = new HashMap();
    Map<String , String> getMessageType = this.getMessageType(dbConn);
    try {
     
     // String queryStr = "SELECT  from message where (TO_ID='"+person+"' and FROM_UID='"+ fromId +"') and DELETE_FLAG!='1' and REMIND_FLAG = 1 and "+T9DBUtility.getDateFilter("SEND_TIME", T9Utility.getCurDateTimeStr(), "<=")+" order by MSG_ID desc ";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      
      
      while (rs.next()) {
        if (flag) {
          map = this.getUserInfoByUID(dbConn, fromId + "," + person , "SEQ_ID,USER_NAME,AUATAR,SEX");
          flag = false;
        }
        int fromId2 = rs.getInt("FROM_ID");
        int msgId = rs.getInt("SEQ_ID");
        
        
        String sendTime = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
        String content = T9Utility.null2Empty(rs.getString("CONTENT"));
        String msgType = rs.getString("MESSAGE_TYPE");
        
        String isvoicemsg = "0";
        String aid = "";
        String aname = "";
        String duration = "";
        
        if (T9MobileUtility.isVoiceMsg(content)) {
          isvoicemsg = "1";
          
          String[] a = T9MobileUtility.getVoiceMsgAttach(content);
          aid = a[0];
          aname = a[1];
          duration = a[2];
          
          String  VOICE_PF = "5".equals(p_VER)? "ios_client" : "android_client";
          content = T9MobileUtility.getVoiceMsgOutputForMobile(content , VOICE_PF);
          content = "[语音]";
        }
        
        Map m =new HashMap();
        m.put("q_id", msgId + "");
        m.put("q_uid", fromId2 + "");
        Map u = map.get(fromId2);
        m.put("q_name", (String)u.get("USER_NAME"));
        String avatar = (String)u.get("AUATAR");
        String sex = (String)u.get("SEX");
        m.put("avatar", T9Utility.encodeSpecial(T9MobileUtility.showAvatar(avatar, sex)));
        m.put("send_time", sendTime);
        m.put("content", T9Utility.encodeSpecial(content));
        m.put("msg_from", fromId2 == person ? "1" : "0");
        m.put("msg_type_name",(fromId2 != person && !"0".equals(msgType)) ? "来自" + (String)getMessageType.get(msgType) : "" );
        m.put("isvoicemsg", isvoicemsg);
        m.put("attachment_id", aid);
        m.put("attachment_name", aname);
        m.put("duration", duration);
        
        count++;
        sb.append(T9MobileUtility.mapToJson(m)).append(",");
        ids += msgId + ",";
        if ("ME".equals(type)) {
          break;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
      ids = ids.substring(0 , ids.length()-1);
    } else {
      return "NONEWDATA";
    }
    sb.append("]");
    }
    if (!"ME".equals(type) 
        && !T9Utility.isNullorEmpty(ids)) {
      String sql2 = "UPDATE message SET REMIND_FLAG = 2 WHERE SEQ_ID IN (" + ids +")";
      T9MobileUtility.updateSql(dbConn, sql2);
    }
    //System.out.println(sb.toString());
    return sb.toString();
  }
  public Map<String , String> getMessageType(Connection dbConn ) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    Map<String , String> m = new HashMap();
    try {
      String queryStr = "SELECT CLASS_CODE, CLASS_DESC from CODE_ITEM where CLASS_NO = 'SMS_REMIND'";
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        m.put(T9Utility.null2Empty(rs.getString("CLASS_CODE")), rs.getString("CLASS_DESC"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return m;
  }
  public Map<Integer , Map<String , String>> getUserInfoByUID(Connection dbConn , String uid , String fields) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    Map<Integer , Map<String , String>> map = new HashMap();
    String[] field = fields.split(",");
    try {
      String queryStr = "SELECT "+fields+" from person where SEQ_ID IN (" + uid + ")";
      stmt = dbConn.createStatement();
     // System.out.println(queryStr);
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        //String userName = rs.getString("USER_NAME");
        //String avatar = rs.getString("AUATAR");
        //String sex = rs.getString("SEX");
        
        Map<String , String> m = new HashMap();
        //m.put("USER_NAME", userName);
       // m.put("AVATAR", avatar);
       // m.put("SEX", sex);
        for (String f : field) {
          if ("SEQ_ID".equals(f)) {
            continue;
          }
          String o = rs.getString(f);
          m.put(f,o);
        }
        map.put(seqId, m);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return map;
  }
  public int getNewListSmsNum(Connection dbConn , int person) throws Exception {
    int result = 0;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "SELECT count(*) from message where TO_ID='"+person+"' and DELETE_FLAG!='1' and REMIND_FLAG ='1' and " + T9DBUtility.getDateFilter("SEND_TIME", T9Utility.getCurDateTimeStr(), "<=");
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        result = rs.getInt(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    
    return result;
  }
  public String getDialogList(Connection dbConn, T9Person person, int id, String p_VER) throws Exception {
    // TODO Auto-generated method stub
    String sql = "UPDATE message SET REMIND_FLAG = 2 WHERE TO_ID='"+person.getSeqId()+"' and BODY_SEQ_ID IN (SELECT MESSAGE_BODY.SEQ_ID FROM MESSAGE_BODY WHERE FROM_ID = '"+id+"' and "+T9DBUtility.getDateFilter("SEND_TIME", T9Utility.getCurDateTimeStr(), "<=")+" )   and DELETE_FLAG!='1' and REMIND_FLAG = 1 " ;
    T9MobileUtility.updateSql(dbConn, sql);
    
    Map<Integer , Map<String , String>> map  = this.getUserInfoByUID(dbConn, id + "," + person.getSeqId() , "SEQ_ID,USER_ID,USER_NAME,AUATAR,SEX,DEPT_ID");

    StringBuffer sb = new StringBuffer("[");
    Statement stmt = null;
    ResultSet rs = null;
    int count = 0;
    Map<String , String> getMessageType = this.getMessageType(dbConn);
    try {
      String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME", T9Utility.getDateTimeStr(new Date()), "<=");
      String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", T9Utility.getCurDateTimeStr(), " <= ");
      String sql2 = "SELECT T1.SEQ_ID,FROM_ID,SEND_TIME,CONTENT,MESSAGE_TYPE FROM message T1 ,message_BODY T0 WHERE ((TO_ID=" + person.getSeqId() + " AND FROM_ID='"+ id +"') or (TO_ID=" + id + " AND FROM_ID='"+ person.getSeqId() +"'))   AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
          "and DELETE_FLAG in (0, 2) " +
          "AND " + dateFiler +
          " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ") order by T0.SEND_TIME desc";
      
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql2);
      
      while (rs.next()) {
        int fromId2 = rs.getInt("FROM_ID");
        int msgId = rs.getInt("SEQ_ID");
        
        String sendTime = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
        String content = T9Utility.null2Empty(rs.getString("CONTENT"));
        
        String isvoicemsg = "0";
        String aid = "";
        String aname = "";
        String duration = "";
        
        if (T9MobileUtility.isVoiceMsg(content)) {
          isvoicemsg = "1";
          
          
          String[] a = T9MobileUtility.getVoiceMsgAttach(content);
          aid = a[0];
          aname = a[1];
          duration = a[2];
          
          String  VOICE_PF = "5".equals(p_VER)? "ios_client" : "android_client";
          content = T9MobileUtility.getVoiceMsgOutputForMobile(content , VOICE_PF);
          content = "[语音]";
        }
        
        String msgType = rs.getString("MESSAGE_TYPE");
       // $SEND_TIME = timeintval($SEND_TIME);
        Map m =new HashMap();
        m.put("q_id", msgId + "");
        m.put("q_uid", fromId2 + "");
        Map u = map.get(fromId2);
        m.put("q_name", (String)u.get("USER_NAME"));
        String avatar = (String)u.get("AUATAR");
        String sex = (String)u.get("SEX");
        m.put("avatar", T9Utility.encodeSpecial(T9MobileUtility.showAvatar(avatar, sex)));
        m.put("send_time", sendTime);
        m.put("content", T9Utility.encodeSpecial(content));
        m.put("msg_from", fromId2 == person.getSeqId() ? "1" : "0");
        m.put("msg_type_name",(fromId2 != person.getSeqId() && !"0".equals(msgType)) ? "来自" + (String)getMessageType.get(msgType) : "" );
        m.put("isvoicemsg", isvoicemsg);
        m.put("attachment_id", aid);
        m.put("attachment_name", aname);
        m.put("duration", duration);
        
        count++;
        sb.append(T9MobileUtility.mapToJson(m)).append(",");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      return "[]";
    }
    sb.append("]");
    return sb.toString();
  }
  public String refreshList(Connection dbConn, T9Person person) throws Exception {
    // TODO Auto-generated method stub
    String uidStrs = "";
    Map<String , Integer> msgCount = new HashMap();
    Map<String , Integer> msgUserList = new HashMap();
    Map<String , Map<String , String>> msgList = new HashMap();
    String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME", T9Utility.getDateTimeStr(new Date()), "<=");
    String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", T9Utility.getCurDateTimeStr(), " <= ");
    String query = "SELECT FROM_ID,TO_ID,REMIND_FLAG,SEND_TIME,CONTENT FROM message T1 ,message_BODY T0 WHERE (TO_ID=" + person.getSeqId() + " or FROM_ID='"+ person.getSeqId() +"') AND REMIND_FLAG = '1' AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
        "and DELETE_FLAG in (0, 2) " +
        "AND " + dateFiler +
        " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ")";
    StringBuffer sb = new StringBuffer("[");
    Statement stmt = null;
    ResultSet rs = null;
    int count = 0;
    try {
      stmt = dbConn.createStatement();
      //System.out.println(query);
      rs = stmt.executeQuery(query);
      
      while (rs.next()) {
        int fromId = rs.getInt("FROM_ID");
        int toId = rs.getInt("TO_ID");
        String sendTime = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
        String remindFlag = rs.getString("REMIND_FLAG");
        String content = rs.getString("CONTENT");
        
        String fromkey = "USER_" + fromId;
        String tokey = "USER_" + toId;
        
        if (toId == person.getSeqId() && "1".equals(remindFlag)) {
          if (!msgCount.containsKey(fromkey)) {
            msgCount.put(fromkey, 0);
          }
          int b = msgCount.get(fromkey) + 1;
          msgCount.put(fromkey, b);
        }
        if (toId == person.getSeqId() 
            && !msgUserList.containsKey(fromkey) ) {
          msgUserList.put(fromkey, 1);
          uidStrs += fromId + ",";
          Map map = new HashMap();
          map.put("id", fromId  + "");
          map.put("sendTime", sendTime);
          map.put("remindFlag", "1".equals(remindFlag) ? "1" : "0");
          map.put("content", content);
          map.put("type", "0");
          
          msgList.put(fromkey, map);
        }
        if (fromId == person.getSeqId() 
            && !msgUserList.containsKey(tokey)) {
          msgUserList.put(tokey, 1);
          if (!T9WorkFlowUtility.findId(uidStrs, toId + "")) {
            uidStrs += toId + ",";
          }
          Map map = new HashMap();
          map.put("id", toId  + "");
          map.put("sendTime", sendTime);
          map.put("remindFlag",  "0");
          map.put("content", content);
          map.put("type", "1");
          msgList.put(tokey, map);
        }
        if (msgList.keySet().size() >= T9MobileConfig.PAGE_SIZE)
          break;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    
    if (uidStrs.endsWith(",")) {
      uidStrs = uidStrs.substring(0, uidStrs.length() - 1);
    }
    if (T9Utility.isNullorEmpty(uidStrs)) {
      return "[]";
    }
    Map<Integer , Map<String , String>> userInfo = this.getUserInfoByUID(dbConn, uidStrs, "SEQ_ID,USER_ID,USER_NAME,AUATAR,SEX,USER_NAME,DEPT_ID");
    
    Map userStates = T9SessionListener.getUserStateMap();
    
    if (msgList.size() > 0) {
      Set<String> ks = msgList.keySet();
      for (String key : ks) {
        Map<String , String> map = msgList.get(key);
        String remindFlag = map.get("remindFlag");
        String readFlag = "1" ;
        if ("1".equals(remindFlag)) {
          readFlag = "0";
        }
        int id = Integer.parseInt((String)map.get("id"));
        Map<String , String> user = userInfo.get(id);
        if ("".equals(user.get("USER_NAME"))) {
          continue;
        }
        String content = map.get("content");
        String isvoicemsg = "0";
        if (T9MobileUtility.isVoiceMsg(content)) {
          isvoicemsg = "1";
          content = "[语音]";
        }
        
        
        String unreadNum = "";
        if (msgCount.containsKey(key)) {
          unreadNum = msgCount.get(key) + "";
        }
        Map o = new HashMap();
        o.put("q_id", id + "");
        o.put("q_name", user.get("USER_NAME"));
        String avatar = (String)user.get("AUATAR");
        String sex = (String)user.get("SEX");
        o.put("avatar", T9Utility.encodeSpecial(T9MobileUtility.showAvatar(avatar, sex)));
        o.put("send_time", map.get("sendTime"));
        o.put("read_flag", readFlag);
        o.put("content", content);
        o.put("unread_num", unreadNum);
        
        String loginType = "";
        if (userStates.containsKey(id)) {
          loginType = (String)userStates.get(id);
        }
        o.put("online", loginType);
        o.put("isfromme", map.get("type"));
        o.put("isvoicemsg", isvoicemsg);
        String out = T9MobileUtility.mapToJson(o);
        sb.append(out).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
    } 
    sb.append("]");
    
    return sb.toString();
  }
}
