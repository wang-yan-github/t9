package t9.mobile.sms.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.message.data.T9MessageBack;
import t9.core.funcs.message.logic.T9MessageLogic;
import t9.core.funcs.message.logic.T9MessageUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.logic.T9SmsLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.message.logic.T9PdaMessageLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9PdaSmsLogic {
  
  public boolean  hasRow(Connection dbConn, String query) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      //System.out.println(query);
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }

  public String sms(Connection dbConn, T9Person person , String query, String cURRITERMS) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    int count = 0 ;
    int c = T9MobileUtility.getCURRITERMS(cURRITERMS);
    int j = 0;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        if (j < c ) {
          j++;
          continue;
        }
        if (j >= T9MobileConfig.PAGE_SIZE + c)
          break;
        
        int smsId = rs.getInt("SMS_ID");
        int fromId = rs.getInt("FROM_ID");
        String sendTime = T9Utility.getDateTimeStr(rs.getTimestamp("SEND_TIME"));
        String smsType = rs.getString("SMS_TYPE");
        String content =  T9Utility.null2Empty(rs.getString("CONTENT"));
        String url = T9Utility.null2Empty(rs.getString("REMIND_URL"));
        String remindFlag = rs.getString("REMIND_FLAG");
        int userId = rs.getInt("USER_ID");
        String fromName =  T9Utility.null2Empty(rs.getString("USER_NAME"));
        String avatar = rs.getString("AUATAR");
        String sex = rs.getString("SEX");
        
        if (T9Utility.isNullorEmpty(fromName)) {
          fromName = fromId + "";
          avatar = "0";
        }
        Map m = new HashMap();
        m.put("q_id", smsId+"");
        m.put("read_flag", remindFlag);
        m.put("avatar", T9Utility.encodeSpecial(T9MobileUtility.showAvatar(avatar , sex)));
        m.put("time", sendTime);
        m.put("content", T9Utility.encodeSpecial(content));
        m.put("from_name", fromName);
        m.put("sms_url",  T9Utility.encodeSpecial(url));
        m.put("sms_type", smsType);
        
       
        sb.append(T9MobileUtility.mapToJson(m)).append(",");
        count++;
        j++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (count > 0 ) {
      sb.deleteCharAt(sb.length() - 1);
    } else {
      return "NONEWDATA";
    }
    sb.append("]");
    return sb.toString();
  }
}
