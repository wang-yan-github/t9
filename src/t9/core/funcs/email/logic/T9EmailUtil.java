package t9.core.funcs.email.logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import t9.core.funcs.email.data.T9Email;
import t9.core.funcs.email.data.T9EmailBody;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.util.db.T9ORM;

/**
 * 邮件外部接口
 * @author Think
 *
 */
public class T9EmailUtil {
  /**
   * 邮件提醒接口
   * @param conn 数据库连接
   * @param fromId 邮件发送人
   * @param toId  邮件接收人
   * @param subject 邮件标题
   * @param content 邮件正文
   * @param sentTime 邮件发送时间
   * @param important 邮件重要度[1,2,3]
   * @throws Exception
   */
  public static void emailNotifier(Connection conn , int fromId 
      , String toId , String subject  , String content ,Date sentTime,String important,String contextPath) throws Exception{
    emailNotifier(conn, fromId, toId, subject, content, sentTime, important, contextPath,false);
  }
  /**
   * 邮件提醒接口
   * @param conn 数据库连接

   * @param fromId 邮件发送人
   * @param toId  邮件接收人

   * @param subject 邮件标题
   * @param content 邮件正文
   * @param sentTime 邮件发送时间

   * @param important 邮件重要度[1,2,3]
   * @param contextPath 系统上下文地址
   * @throws Exception
   */
  public static void emailNotifier(Connection conn , int fromId 
      , String toId , String subject  , String content ,Date sentTime,String important,String contextPath,boolean isSmsRemind) throws Exception{
    T9ORM orm = new T9ORM();
    T9EmailBody emailBody = new T9EmailBody();
    T9Email email = new T9Email();
    long size = 0l;
    content = content.replaceAll("[\r\n]", "<br>");
    emailBody.setEnsize(size);
    emailBody.setFromId(fromId);
    emailBody.setToId(toId);
    emailBody.setContent(content);
    if(subject == null || "".equals(subject)){
      subject = "[无主题]";
    }
    emailBody.setSubject(subject);
    if(sentTime == null){
      sentTime = new Date();
    }
    emailBody.setSendTime(sentTime);
    if(important == null || "".equals(important)){
      important = "1";
    }
    emailBody.setImportant(important);
    emailBody.setSendFlag("1");
    orm.saveSingle(conn, emailBody);
    T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
    int bId = emul.getBodyId(conn);
    ArrayList<String> ids = new ArrayList<String>();
    if(toId != null && !"".equals(toId)){
      ids = emul.addArray(ids, toId.split(","));
    }
    if(isSmsRemind){
      subject = " 请查收我的邮件！主题：" + subject;
      String remindUrl = "/core/funcs/email/inbox/read_email/index.jsp?seqId=" + bId ;
      T9SmsBack sb = new T9SmsBack();
      sb.setFromId(fromId);
      sb.setContent(subject);
      sb.setSmsType("2");
      sb.setRemindUrl(remindUrl);
      sb.setToId(toId);
      T9SmsUtil.smsBack(conn, sb);
    }
    for(int i = 0 ; ids != null && i < ids.size(); i++){
      String id = ids.get(i);
      if("".equals(id)){
        continue;
      }
      email.setBodyId(bId);
      email.setToId(id);
      email.setReadFlag("0");
      email.setDeleteFlag("0");
      email.setBoxId(0);
      orm.saveSingle(conn, email);
    }   
  }
}

