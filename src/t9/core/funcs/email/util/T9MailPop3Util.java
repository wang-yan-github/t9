package t9.core.funcs.email.util;

import java.security.Security;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import t9.core.data.T9DataSources;
import t9.core.funcs.email.data.T9EmailCont;
import t9.core.funcs.email.data.T9Webmail;
import t9.core.funcs.email.data.T9WebmailBody;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9MailPop3Util {
  private static final int MAX_MAILCNT_ONSESSION = 10;
  
  private static Logger log = Logger.getLogger("yzq.t9.core.funcs.email.util.T9MailPop3Util");

  /**
   * 
   * @param popHost
   * @param userName
   * @param password
   * @param oldIds
   * @param attachPath
   * @return
   * @throws Exception
   */
  public static ArrayList<T9WebmailBody> getWebMailBody(T9Webmail webmail,String attachPath , int limit) throws Exception {
    Properties p = new Properties();
    if(webmail.getPop3Ssl() != null && ("1".equals(webmail.getPop3Ssl().trim()) || "yes".equalsIgnoreCase(webmail.getPop3Ssl().trim()))){
      Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
      final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
      p.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
      p.setProperty("mail.smtp.socketFactory.fallback", "false");
    }
    p.setProperty("mail.pop3.host", webmail.getPopServer());
    p.setProperty("mail.pop3.userName", webmail.getEmail());
    p.setProperty("mail.pop3.password", webmail.getEmailPass());
    p.setProperty("mail.pop3.port", webmail.getPop3Port());
    p.setProperty("mail.pop3.recvDel", webmail.getRecvDel());
    p.setProperty("mail.pop3.connectiontimeout", "20000");
    p.setProperty("mail.pop3.timeout", "40000");
    return getWebMailBody(p, attachPath ,limit);
  }
  
  /**
   * 接受外部邮件
   * 
   * @param p
   * @return
   * @throws Exception
   */
  private static ArrayList<T9WebmailBody> getWebMailBody(Properties p, String attachPath, int limit)
      throws Exception {
    // 取得POP3.properties文件中的pop3相关设定
    // popHost = "pop3.163.com";
    // userName = "chenyimdj";
    // password = "chyi1986";
    // 建立与pop3 服务器的session连接
    // p = new Properties();
    // p.put("mail.pop3.host", popHost);
    String popHost = p.getProperty("mail.pop3.host");
    String userName = p.getProperty("mail.pop3.userName");
    String password = p.getProperty("mail.pop3.password");
    String portstr = p.getProperty("mail.pop3.port");
    String recvDel = p.getProperty("mail.pop3.recvDel");
    Session session = Session.getInstance(p, null);
    ArrayList<T9WebmailBody> webmails = new ArrayList<T9WebmailBody>();
    
    // 获取邮件服务器上的邮件夹，将数值传给MailListPanel显示
    Store store = null;
    int port = -1;
    if(portstr != null && !"".equals(portstr)){
      port = Integer.valueOf(portstr);
    }
    try {
      store = session.getStore("pop3");
      store.connect(popHost, port, userName, password);
      Folder folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); // 取得store的Foder邮件夹
      
      FetchProfile profile = new FetchProfile(); 
      profile.add(UIDFolder.FetchProfileItem.UID); 
      profile.add(FetchProfile.Item.ENVELOPE);

      folder.getNewMessageCount();
      if (folder instanceof POP3Folder) { // pop3的形式        POP3Folder inbox = (POP3Folder) folder;
        Message[] messages = inbox.getMessages();
        inbox.fetch(messages, profile);
        for (int i = 0; i < messages.length; i++) {
          if(webmails.size() >= limit){
            break;
          }
          MimeMessage mimeMessage = (MimeMessage) messages[i];
          String uid = inbox.getUID(mimeMessage);
          try {
            T9WebmailBody webmailBody = null;
            try {
              webmailBody = toWebMailByMessage(mimeMessage, uid, userName,attachPath,true);
            }catch(Exception ex) {
              ex.printStackTrace();
            }
            if(webmailBody == null){
              continue;
            }
//            System.out.println(webmailBody.getSubject());
            webmails.add(webmailBody);
            if("1".equals(recvDel)){
              if(webmailBody.getLargeAttachment() != null && !"".equals(webmailBody.getLargeAttachment().trim())){
                mimeMessage = setMessageDeleteFlag(mimeMessage, false);
              }else{
                mimeMessage =  setMessageDeleteFlag(mimeMessage, true);
              }
            }
          } catch (Exception e) {
            log.debug(e.getMessage() ,e);
          }
        }
      } else if (folder instanceof IMAPFolder) { // IMAP的形式        IMAPFolder inbox = (IMAPFolder) folder;
        Message[] messages = inbox.getMessages();
        inbox.fetch(messages, profile);
        for (int i = 0; i < messages.length; i++) {
          if(webmails.size() >= limit){
            break;
          }
          MimeMessage mimeMessage = (MimeMessage) messages[i];
          String uid = Long.toString(inbox.getUID(mimeMessage));
          try {
            T9WebmailBody webmailBody = null;
            try {
              webmailBody = toWebMailByMessage(mimeMessage, uid, userName,attachPath,true);
            }catch(Exception ex) {
            }
            if(webmailBody == null){
              continue;
            }
            webmails.add(webmailBody);
            if("1".equals(recvDel)){
              if(webmailBody.getLargeAttachment() != null && !"".equals(webmailBody.getLargeAttachment().trim())){
                mimeMessage =  setMessageDeleteFlag(mimeMessage, false);
              }else{
                mimeMessage =  setMessageDeleteFlag(mimeMessage, true);
              }
            }
          } catch (Exception e) {
            log.debug(e.getMessage(),e);
          }
        }
      } else {
        throw new Exception("不支持此类邮箱的协议类型!");
      }
      try {
        folder.close(true);
      }catch(Exception ex) {        
      }
    } catch (Exception ex) {
//      System.out.println(userName);
      throw ex;
    } finally {
      try {
        store.close();
      }catch(Exception ex) {        
      }
    }
    return webmails;
  }
  /**
   * 
   * @param webmail
   * @param attachPath
   * @param messageId
   * @return
   * @throws Exception
   */
  public static T9WebmailBody getWebMailBodyByMesId(Connection dbConn, T9Webmail webmail,String attachPath,String messageId) throws Exception {
    Properties p = new Properties();
    if(webmail.getPop3Ssl() != null && ("1".equals(webmail.getPop3Ssl().trim()) || "yes".equalsIgnoreCase(webmail.getPop3Ssl().trim()))){
      Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
      final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
      p.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
      p.setProperty("mail.smtp.socketFactory.fallback", "false");
    }
    p.setProperty("mail.pop3.host", webmail.getPopServer());
    p.setProperty("mail.pop3.userName", webmail.getEmail());
    p.setProperty("mail.pop3.password", webmail.getEmailPass());
    p.setProperty("mail.pop3.port", webmail.getPop3Port());
    p.setProperty("mail.pop3.recvDel", webmail.getRecvDel());
    return getWebMimeMessageById(dbConn, p, webmail.getEmailUid(), attachPath,messageId);
  }
  /**
   * 取得单个邮件信息
   * @param p
   * @param oldIdsStr
   * @param attachPath
   * @param messageId
   * @return
   * @throws Exception
   */
  public static T9WebmailBody getWebMimeMessageById(Connection dbConn, Properties p,String oldIdsStr,String attachPath,String messageId)
  throws Exception {
    // 取得POP3.properties文件中的pop3相关设定
    // popHost = "pop3.163.com";
    // userName = "chenyimdj";
    // password = "chyi1986";
    // 建立与pop3 服务器的session连接
    // p = new Properties();
    // p.put("mail.pop3.host", popHost);
    String popHost = p.getProperty("mail.pop3.host");
    String userName = p.getProperty("mail.pop3.userName");
    String password = p.getProperty("mail.pop3.password");
    String portstr = p.getProperty("mail.pop3.port");
    String recvDel = p.getProperty("mail.pop3.recvDel");
    Session session = Session.getInstance(p, null);
    String[] oldIds = oldIdsStr == null ? null : oldIdsStr.split(",");
    // session.setDebug(true);
    // 获取邮件服务器上的邮件夹，将数值传给MailListPanel显示
    Store store = null;
    int port = -1;
    if(portstr != null && !"".equals(portstr)){
      port = Integer.valueOf(portstr);
    }
    T9WebmailBody webmailBody = null;
    try {
      store = session.getStore("pop3");
      store.connect(popHost, port, userName, password);
      Folder folder = store.getFolder("INBOX");
      folder.open(Folder.READ_WRITE); // 取得store的Foder邮件夹
      FetchProfile profile = new FetchProfile(); 
      profile.add(UIDFolder.FetchProfileItem.UID); 
      profile.add(FetchProfile.Item.ENVELOPE);

      folder.getNewMessageCount();
      if (folder instanceof POP3Folder) { // pop3的形式        POP3Folder inbox = (POP3Folder) folder;
        Message[] messages = inbox.getMessages();
        inbox.fetch(messages, profile);
        for (int i = 0; i < messages.length; i++) {
          MimeMessage mimeMessage = (MimeMessage) messages[i];
          String uid = inbox.getUID(mimeMessage);
          if(!uid.equals(messageId)){
            continue;
          }
          webmailBody = toWebMailByMessage(mimeMessage, uid, userName,attachPath,false);
          if("1".equals(recvDel)){
            if(webmailBody.getLargeAttachment() != null && "1".equals(webmailBody.getLargeAttachment().trim())){
              mimeMessage = setMessageDeleteFlag(mimeMessage, false);
            }else{
              mimeMessage =  setMessageDeleteFlag(mimeMessage, true);
            }
          }
        }
      } else if (folder instanceof IMAPFolder) { // IMAP的形式
        IMAPFolder inbox = (IMAPFolder) folder;
        MimeMessage mimeMessage = (MimeMessage) inbox.getMessageByUID(Long.parseLong(messageId));
        webmailBody = toWebMailByMessage(mimeMessage, messageId, userName,attachPath,false);
        if("1".equals(recvDel)){
          if(webmailBody.getLargeAttachment() != null && "1".equals(webmailBody.getLargeAttachment().trim())){
            mimeMessage =  setMessageDeleteFlag(mimeMessage, false);
          }else{
            mimeMessage =  setMessageDeleteFlag(mimeMessage, true);
          }
        }
      } else {
        throw new Exception("不支持此类邮箱的协议类型!");
      }
      folder.close(true);
    } catch (Exception ex) {
      throw ex;
    } finally {
      store.close();
    }
    return webmailBody;
 }
/**
 * 
 * @param mimeMessage
 * @param isRecvDel
 * @return
 * @throws Exception
 */
  public static MimeMessage  setMessageDeleteFlag(MimeMessage mimeMessage,boolean isRecvDel) throws Exception{
    mimeMessage.setFlag(Flags.Flag.DELETED, isRecvDel);
    return mimeMessage;
  }
  /**
   * 转换Message到webmailBody
   * 
   * @param message
   * @return
   * @throws Exception
   */
  private static T9WebmailBody toWebMailByMessage(Message mimeMessage,
      String messageUid, String userName,String attachPath,boolean isLimitlarger) throws Exception {

    String attachmentId = "";
    String attachmentName = "";
    String largerAttachmentName = "0";
    int webLimit = T9EmailCont.webLimit*1024*1024;
    
    T9WebmailBody webmailBody = new T9WebmailBody();
    String fromMail = T9MimeMessageUtil.getFrom(mimeMessage);
    webmailBody.setWebmailUid(messageUid);
    webmailBody.setSendDate(T9MimeMessageUtil.getSentDate(mimeMessage));
    webmailBody.setSubject(T9MimeMessageUtil.getSubject(mimeMessage));
    webmailBody.setFromMail(fromMail);
    webmailBody.setToMail(T9MimeMessageUtil.getMailAddress(mimeMessage, "TO"));
    webmailBody.setCcMail(T9MimeMessageUtil.getMailAddress(mimeMessage, "CC"));
    webmailBody.setReplyMail(fromMail);
    
    Connection dbConn = null;
    try {
      dbConn = T9DataSources.getDataSource(T9SysProps.getSysDbDsName()).getConnection();
      if (!isNewMail(dbConn, webmailBody)) {
        return null;
      }
    }catch(Exception ex) {
    }finally {
      T9DBUtility.closeDbConn(dbConn, log);
    }
    
    webmailBody.setContentHtml(T9MimeMessageUtil.getBody(mimeMessage, userName));
    String isHtml = T9MimeMessageUtil.isHtml(mimeMessage)? "0" : "1";
    webmailBody.setIsHtml(isHtml);
    
    String body = webmailBody.getContentHtml();
    if(T9MimeMessageUtil.hasAttachment(mimeMessage)){
      if(isLimitlarger && (webLimit) < mimeMessage.getSize() ){
        largerAttachmentName = "1";
      }else{
        Map<String, String[]> attachMap = T9MimeMessageUtil.saveAttachMent(mimeMessage,attachPath);
        Set<String> cids = attachMap.keySet();
        for (String key : cids) {
          String[] value = attachMap.get(key);
          if(key == null || value == null || "null".equals(key)){
            continue;
          }
          if (value.length < 2) {
            continue;
          }
          if (T9Utility.isNullorEmpty(value[0]) || "null".equals(value[0])) {
            continue;
          }
          if (value[0].equals(key)) {
            if(!"".equals(attachmentId)){
              attachmentId += ",";
              attachmentName += "*" ;
            }
            attachmentId += value[0];
            attachmentName += value[1] ;
          } else {
            String path = "/t9/t9/core/funcs/office/ntko/act/T9NtkoAct/upload.act?attachmentName="+ value[1]+ "&attachmentId="+ value[0] +"&module=email";
            body = body.replace("cid:" + key, path);
          }
        }
      }
    }
    webmailBody.setContentHtml(body);
    webmailBody.setLargeAttachment(largerAttachmentName);
    webmailBody.setAttachmentId(attachmentId);
    webmailBody.setAttachmentName(attachmentName);

    return webmailBody;
  }

  /**
   * 判断当前邮件是否为新邮件
   * 2011-08-31，双重判断机制，解决邮件重复收取问题
   * @param messageId
   * @param oldIds
   * @return
   */
  private static boolean isNewMail(Connection dbConn, T9WebmailBody webmailBody) {
    String messageId = T9Utility.null2Empty(webmailBody.getWebmailUid()).trim();
    
    boolean result = true;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      //一种情况，UID有效，UID的邮件已经存在，说明不是新邮件
      if (!"".equals(messageId)) {
        rs = stmt.executeQuery("select count(SEQ_ID) from WEBMAIL_BODY where WEBMAIL_UID='" + messageId + "'");
        if (rs.next()) {
          int mailCnt = rs.getInt(1);
          if (mailCnt > 0) {
            return false;
          }
        }
      }
      //两种情况，UID无效 || (UID有效 && UID的邮件不存在)
      StringBuffer sqlBuf = new StringBuffer("select count(SEQ_ID) from WEBMAIL_BODY where 1=1");
      if (!T9Utility.isNullorEmpty(webmailBody.getReplyMail())) {
        sqlBuf.append(" and REPLY_MAIL='" + webmailBody.getReplyMail() + "'");
      }
      sqlBuf.append(" and FROM_MAIL='" + webmailBody.getFromMail() + "'");
      sqlBuf.append(" and TO_MAIL='" + webmailBody.getToMail() + "'");
      sqlBuf.append(" and SUBJECT='" + webmailBody.getSubject() + "'");
      sqlBuf.append(" and " + T9DBUtility.getDateFilter("SEND_DATE", T9Utility.getDateTimeStr(webmailBody.getSendDate()), "="));
      
      rs = stmt.executeQuery(sqlBuf.toString());
      if (rs.next()) {
        int mailCnt = rs.getInt(1);
        if (mailCnt > 0) {
          return false;
        }
        return true;
      }else {
        return true;
      }
    } catch (Exception e) {
      // TODO: handle exception
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  /**
   * 加载大附件
   * @return
   */
  public static boolean loadLargerAttachment(Connection conn,int bodyId,String messageId){
    boolean result = true;
    
    return result;
  }
}
