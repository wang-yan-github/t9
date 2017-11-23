package t9.core.funcs.message.logic;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import t9.core.funcs.message.data.T9Message;
import t9.core.funcs.message.data.T9MessageBack;
import t9.core.funcs.message.data.T9MessageBody;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.system.ispirit.n12.org.logic.T9IsPiritLogic;
import t9.core.global.T9SysProps;
import t9.core.util.db.T9ORM;

public class T9MessageUtil{

  public static boolean smsBack(Connection conn ,T9MessageBack sb) throws Exception{
    if (T9SysProps.getString("closeAllSms").equals("1")) {
      return true;
    }
    try{
      T9MessageBody smsBody = new T9MessageBody();
      smsBody.setFromId(sb.getFromId());
      if(sb.getContent() == null){
        throw new Exception("内容为空!");
      }
      Date sentTime = null;
      smsBody.setContent(sb.getContent());
      if(sb.getSendDate() != null){
        sentTime = sb.getSendDate();
      } else {
        Calendar cal = Calendar.getInstance();        
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
        String cdate = sdf.format(cal.getTime());                
        sentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cdate);//HH:mm
      }
      smsBody.setSendTime(sentTime);
      ArrayList<T9Message> smsList = new ArrayList<T9Message>();
      T9Message sms = null;
      if(sb.getToId() == null || "".equals(sb.getToId())){
        return false;
      }
      String[] userIds = sb.getToId().split(",");
      String flag = "1";  //标记为2  表示没有阅读的
      String delFlag = "0";
      String extendTimeStr = T9SysProps.getProp("$SMS_DELAY_PER_ROWS");
      String extendFlagStr = T9SysProps.getProp("$SMS_DELAY_SECONDS");
      long curTimeL = sentTime.getTime();
      int extendTime = 0;
      int extendFlag = 0;
      Date remindDate = sentTime;
      try {
        extendTime = Integer.valueOf(extendTimeStr);
      } catch (Exception e) {
        extendTime = 0;
      }
      try {
        extendFlag = Integer.valueOf(extendFlagStr);
      } catch (Exception e) {
        extendFlag = 0;
      }
      
      for(int i = 0; i < userIds.length; i++) {
        sms = new T9Message();
        sms.setToId(Integer.parseInt(userIds[i]));
        sms.setRemindFlag(flag);
        sms.setDeleteFlag(delFlag);
        if(i>0 && extendFlag != 0 && extendTime != 0 && (i % extendFlag) ==0 ){
          long remindTime = curTimeL + (i / extendFlag) * extendTime*1000;
          remindDate = new Date(remindTime);
        }
        sms.setRemindTime(remindDate);
        smsList.add(sms);
        
        
        //设置提醒
        T9IsPiritLogic.setUserMessageRemind(sb.getToId());
      }
      smsBody.setMessagelist(smsList);
      smsBody.setMessageType("0");
      smsBody.setRemindUrl(sb.getRemindUrl());
      T9ORM orm = new T9ORM();
      orm.saveComplex(conn, smsBody);
      T9MsgPusher.pushSms(sb.getToId());
      return true;
    }catch(Exception e){
      e.printStackTrace();
      throw e;
    }
  }
}
