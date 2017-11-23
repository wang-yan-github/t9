package t9.subsys.inforesouce.docmgr.logic;

import java.sql.Connection;
import java.util.Date;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;

public class T9DocSmsLogic{
  public static void sendSms(T9Person user, Connection dbConn,String content, 
      String url, String toId, Date date) throws Exception{
    T9SmsBack smsBack = new T9SmsBack();    
    smsBack.setContent(content);
    smsBack.setFromId(user.getSeqId());
    smsBack.setRemindUrl(url);
    smsBack.setSmsType("0");
    smsBack.setToId(toId);    
    if(date != null){
      smsBack.setSendDate(date);
    }
    T9SmsUtil.smsBack(dbConn, smsBack);
  }
}
