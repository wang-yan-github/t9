package t9.core.funcs.email.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import t9.core.data.T9DataSources;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.email.data.T9Email;
import t9.core.funcs.email.data.T9EmailBody;
import t9.core.funcs.email.data.T9EmailCont;
import t9.core.funcs.email.data.T9Webmail;
import t9.core.funcs.email.data.T9WebmailBody;
import t9.core.funcs.email.util.T9MailPop3Util;
import t9.core.funcs.email.util.T9MailSmtpUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9SysProps;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

/**
 * 
 * @author Think
 * 
 */
public class T9WebmailLogic {
  private static Logger log = Logger.getLogger("yzq.t9.core.funcs.email.logic.T9WebmailLogic");
  /**
   * 根据用户名取得用户的外部邮件设置信息
   * 
   * @param personId
   * @return
   * @throws Exception
   */
  public List<T9Webmail> getWebMailByUserId(Connection conn, int personId)
      throws Exception {
    List<T9Webmail> webList = null;
    T9ORM orm = new T9ORM();
    webList = orm.loadListSingle(conn, T9Webmail.class,
        new String[] { "USER_ID=" + personId });
    return webList;
  }

  /**
   * 取得外部邮件
   * 
   * @param conn
   * @throws Exception
   */
  public void loadWebMail() throws Exception {
    loadWebMail(T9EmailCont.UPLOAD_HOME);
  }

  /**
   * 根据单个用户取得外部邮件
   * 
   * @param conn
   * @throws Exception
   */
  private void loadWebMail(Connection conn, int personId) throws Exception {
    loadWebMail(conn, T9EmailCont.UPLOAD_HOME, personId);
  }

  /**
   * 取得外部邮件
   * 
   * @param conn
   * @param attachPath
   * @throws Exception
   * @throws Exception
   */
  private void loadWebMail(String attachPath) throws Exception {
    List<T9Webmail> wms = null;
    T9ORM orm = new T9ORM();
    Map hash = null;
    Connection dbConn = null;
    try {
      dbConn = T9DataSources.getDataSource(T9SysProps.getSysDbDsName()).getConnection();
      wms = (List<T9Webmail>) orm.loadListSingle(dbConn, T9Webmail.class, hash);
    }catch(Exception ex) {
    }finally {
      T9DBUtility.closeDbConn(dbConn, log);
    }
    for (T9Webmail wm : wms) {
      if(!"1".equals(wm.getCheckFlag())){
        continue;
      }
      try {
        loadWebMail(wm, attachPath, orm);
      } catch (Exception e) {
        e.printStackTrace();
        log.debug(e.getMessage(), e);
      }
    }
  }

  /**
   * 根据单个用户取得外部邮件
   * 
   * @param conn
   * @param attachPath
   * @param personId
   * @throws Exception
   */
  private void loadWebMail(Connection conn, String attachPath, int personId)
      throws Exception {
    List<T9Webmail> wms = null;
    T9ORM orm = new T9ORM();
    Map hash = null;
    wms = getWebMailByUserId(conn, personId);
    for (T9Webmail wm : wms) {
      try {
        loadWebMail(wm, attachPath, orm);
      } catch (Exception e) {
        log.debug(e.getMessage(), e);
      }
    }
  }

  /**
   * 取得外部邮件
   * 
   * @param conn
   * @param wm
   * @param attachPath
   * @throws Exception
   */
  public void loadWebMail(T9Webmail wm, String attachPath,
      T9ORM orm) throws Exception {
    ArrayList<T9WebmailBody> webmailbodys = null;
    boolean flag = true;
    int loopCnt = 0;
    int maxLoopCnt = 5;
    while (flag && loopCnt < maxLoopCnt) {
      webmailbodys = null;
      try {
        webmailbodys = T9MailPop3Util.getWebMailBody(wm, attachPath, 10);
      }catch(Exception ex) {
        try {
          Thread.sleep(1000);
        }catch(Exception ex2) {
        }
        loopCnt++;
        continue;
      }
      Connection dbConn = null;
      try {
        dbConn = T9DataSources.getDataSource(T9SysProps.getSysDbDsName()).getConnection();
        for (int i = 0; i < webmailbodys.size(); i++) {
          try {
            T9WebmailBody wmb = webmailbodys.get(i);
            if (wmb == null) {
              continue;
            }
            saveWebMail2Local(dbConn, wmb, wm, orm);
            dbConn.commit();
          } catch (Exception e) {
            try {
              dbConn.rollback();
            }catch(Exception ex2) {          
            }
            log.debug(e.getMessage(), e);
          }
        }
      }catch(Exception ex) {
      }finally {
        T9DBUtility.closeDbConn(dbConn, log);
      }
      
      if (webmailbodys.size() < 1) {
        flag = false;
      } else {
        flag = true;
      }
      if (flag) {
        try {
          Thread.sleep(1000);
        }catch(Exception ex) {
        }
      }
      loopCnt++;
    }
  }

  /**
   * 
   * @param conn
   * @param subject
   * @param emailBodyId
   * @param userId
   * @param emailId
   * @param ids
   * @return
   * @throws Exception
   */
  public boolean smsRmind(Connection conn, String subject, int emailBodyId,
      int userId, int emailId, String ids) throws Exception {
    subject = " 您收到一份外部邮件！主题：" + subject;
    String remindUrl = "/core/funcs/email/webbox/read_email/index.jsp?mailId="
        + emailId + "&seqId=" + emailBodyId;
    T9SmsBack sb = new T9SmsBack();
    sb.setFromId(userId);
    sb.setContent(subject);
    sb.setSmsType("2");
    sb.setRemindUrl(remindUrl);
    sb.setToId(ids);
    T9SmsUtil.smsBack(conn, sb);
    return false;
  }

  /**
   * 删除已经保存的内部邮件
   * @param conn
   * @return
   * @throws SQLException
   */
  private void delInnerEmail(Connection conn, int bodyId) throws Exception {
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      String sql = "delete form EMAIL where BODY_ID=" + bodyId;
      stmt.executeUpdate(sql);
      sql = "delete form EMAIL_BODY where SEQ_ID=" + bodyId;
      stmt.executeUpdate(sql);
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  /**
   * 保存外部邮件到本地
   * 
   * @param conn
   * @param wmb
   * @throws Exception
   */
  public void saveWebMail2Local(Connection conn, T9WebmailBody wmb,
      T9Webmail wm, T9ORM orm) throws Exception {
    int bodyId = 0;
    //保存内部邮件到 EMAIL_BODY
    bodyId = saveWemailBodyByWebmail(conn, wmb, wm, orm);// 保存邮件正文
    wmb.setBodyId(bodyId);
    //保存内部邮件到 EMAIL
    T9Email em = new T9Email();
    em.setBodyId(bodyId);
    em.setBoxId(0);
    em.setToId(String.valueOf(wm.getUserId()));
    orm.saveSingle(conn, em);
    T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
    int emailId = emul.getBodyId(conn, "EMAIL");
    //保存到WEBMAIL_BODY
    try {
      orm.saveSingle(conn, wmb);
      smsRmind(conn, wmb.getSubject(), bodyId, 1, emailId, String.valueOf(wm.getUserId()));
    }catch(Exception ex) {
      delInnerEmail(conn, bodyId);
    }
  }

  /**
   * 保存外部邮件到本地
   * 
   * @param conn
   * @param wmb
   * @throws Exception
   */
  public void updateWebMail2Local(Connection conn, T9WebmailBody wmb,
      T9Webmail wm, T9ORM orm, T9EmailBody eb) throws Exception {
    int bodyId = 0;
    bodyId = updateWemailBodyByWebmail(conn, wmb, wm, orm, eb);// 保存邮件正文
    wmb.setBodyId(bodyId);
    orm.updateSingle(conn, wmb);
  }

  /**
   * @throws Exception
   * 
   */
  public int saveWemailBodyByWebmail(Connection conn, T9WebmailBody wmb,
      T9Webmail wm, T9ORM orm) throws Exception {
    T9EmailBody eb = new T9EmailBody();
    String attachmentId = wmb.getAttachmentId();
    String attachmentName = wmb.getAttachmentName();
    String webmailContent = wmb.getContentHtml();
    String compressContent = T9DiaryUtil.cutHtml(webmailContent);
    String fromWebmail = wmb.getFromMail();
    
    eb.setAttachmentId(attachmentId);
    eb.setAttachmentName(attachmentName);
    eb.setCompressContent(compressContent);
    eb.setWebmailContent(webmailContent);
    eb.setSubject(wmb.getSubject());
    eb.setSendTime(wmb.getSendDate());
    eb.setSendFlag("1");
    eb.setFromId(-2);// 收件人为外部邮箱，所以不在t9系统中存在用户
    eb.setToId(String.valueOf(wm.getUserId()));
    eb.setFromWebmail(fromWebmail);
    eb.setFromWebmailId(String.valueOf(wm.getSeqId()));
    eb.setToWebmail(wmb.getToMail());
    eb.setIsWebmail("1");
    eb.setEnsize(0);

    orm.saveSingle(conn, eb);
    T9InnerEMailUtilLogic iem = new T9InnerEMailUtilLogic();
    int bodyId = iem.getBodyId(conn);
    return bodyId;
  }

  /**
   * update
   * 
   * @param conn
   * @param wmb
   * @param wm
   * @param orm
   * @return
   * @throws Exception
   */
  public int updateWemailBodyByWebmail(Connection conn, T9WebmailBody wmb,
      T9Webmail wm, T9ORM orm, T9EmailBody eb) throws Exception {
    String attachmentId = wmb.getAttachmentId();
    String attachmentName = wmb.getAttachmentName();
    String webmailContent = wmb.getContentHtml();
    String compressContent = T9DiaryUtil.cutHtml(webmailContent);
    String fromWebmail = wmb.getFromMail();
    eb.setAttachmentId(attachmentId);
    eb.setAttachmentName(attachmentName);
    eb.setCompressContent(compressContent);
    eb.setWebmailContent(webmailContent);
    eb.setSubject(wmb.getSubject());
    eb.setSendTime(wmb.getSendDate());
    eb.setSendFlag("1");
    eb.setFromId(-2);// 收件人为外部邮箱，所以不在t9系统中存在用户
    eb.setToId(String.valueOf(wm.getUserId()));
    eb.setFromWebmail(fromWebmail);
    eb.setFromWebmailId(String.valueOf(wm.getSeqId()));
    eb.setToWebmail(wmb.getToMail());
    eb.setIsWebmail("1");
    eb.setEnsize(0);
    orm.updateSingle(conn, eb);
    return eb.getSeqId();
  }

  /**
   * 
   * @param conn
   * @param wmb
   * @param wm
   * @param orm
   * @param eb
   * @return
   * @throws Exception
   */
  public int updateWemailBodyByWebmailForAtt(Connection conn,
      T9WebmailBody wmb, T9Webmail wm, T9ORM orm, T9EmailBody eb)
      throws Exception {
    String attachmentId = wmb.getAttachmentId();
    String attachmentName = wmb.getAttachmentName();
    eb.setAttachmentId(attachmentId);
    eb.setAttachmentName(attachmentName);
    orm.updateSingle(conn, eb);
    return eb.getSeqId();
  }

  /**
   * 
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  public ArrayList<T9Webmail> getWebmailInfo(Connection conn, int userId)
      throws Exception {
    T9ORM orm = new T9ORM();
    ArrayList<T9Webmail> wms = (ArrayList<T9Webmail>) orm.loadListSingle(conn,
        T9Webmail.class, new String[] { "USER_ID=" + userId });
    return wms;
  }

  /**
   * 设置网络邮箱的信息
   * 
   * @param conn
   * @param request
   * @param person
   * @throws Exception
   */
  public String setWebmail(Connection conn, Map request, T9Person person)
      throws Exception {
    T9Webmail wm = (T9Webmail) T9FOM.build(request, T9Webmail.class, null);
    String[] pop3Ssls = (String[])request.get("pop3Ssl");
    String pop3Ssl = "0" ;
    if (pop3Ssls != null && pop3Ssls.length > 0) {
      pop3Ssl = pop3Ssls[0];
    } 
    String[] smtpSsls = (String[])request.get("smtpSsl");
    String smtpSsl = "0" ;
    if (smtpSsls != null && smtpSsls.length > 0) {
      smtpSsl = smtpSsls[0];
    } 
    wm.setPop3Ssl(pop3Ssl);
    wm.setSmtpSsl(smtpSsl);
    wm.setUserId(person.getSeqId());
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, wm);
    
    
    Statement st = null;
    ResultSet rs = null;
    String seqId = "";
    try {
      String sql = " select max(SEQ_ID) SEQ_ID from webmail ";
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        seqId = rs.getString("SEQ_ID");
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      T9DBUtility.close(st, rs, null);
    }
    return seqId;
  }

  /**
   * 
   * @param conn
   * @param request
   * @param person
   * @throws Exception
   */
  public void updateWebmail(Connection conn, Map request, int userId)
      throws Exception {
    T9Webmail wm = (T9Webmail) T9FOM.build(request, T9Webmail.class, null);
    String[] pop3Ssls = (String[])request.get("pop3Ssl");
    String pop3Ssl = "0" ;
    if (pop3Ssls != null && pop3Ssls.length > 0) {
      pop3Ssl = pop3Ssls[0];
    } 
    String[] smtpSsls = (String[])request.get("smtpSsl");
    String smtpSsl = "0" ;
    if (smtpSsls != null && smtpSsls.length > 0) {
      smtpSsl = smtpSsls[0];
    } 
    wm.setPop3Ssl(pop3Ssl);
    wm.setSmtpSsl(smtpSsl);
    wm.setUserId(userId);
    T9ORM orm = new T9ORM();
    orm.updateSingle(conn, wm);
  }

  /**
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String listWebmail(Connection conn, int userId) throws Exception {
    String sql = "select seq_id,email, is_default from webmail where USER_ID=" + userId;
    StringBuffer sb = new StringBuffer("[");
    StringBuffer temp = new StringBuffer();
    Statement st = null;
    ResultSet rs = null;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      while (rs.next()) {
        int seqId = rs.getInt(1);
        String email = rs.getString(2);
        String isDefault = rs.getString(3) == null ? "0" : rs.getString(3)  ;
        if (!"".equals(temp.toString())) {
          temp.append(",");
        }
        temp.append("{seqId:").append(seqId).append(",")
         .append("isDefault:\"").append(isDefault).append("\",")
         .append("email:\"").append(email).append("\"}");
      }
    } catch (Exception e) {
    } finally {
      T9DBUtility.close(st, rs, null);
    }
    sb.append(temp).append("]");
    return sb.toString();
  }

  /**
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public String getWebmail(Connection conn, int seqId) throws Exception {
    T9Webmail wm = null;
    T9ORM orm = new T9ORM();
    wm = (T9Webmail) orm.loadObjSingle(conn, T9Webmail.class, seqId);
    StringBuffer sb = T9FOM.toJson(wm);
    return sb.toString();
  }

  /**
   * 
   * @param conn
   * @param seqId
   * @return
   * @throws Exception
   */
  public T9Webmail getWebmailById(Connection conn, int seqId) throws Exception {
    T9Webmail wm = null;
    T9ORM orm = new T9ORM();
    wm = (T9Webmail) orm.loadObjSingle(conn, T9Webmail.class, seqId);
    return wm;
  }

  /**
   * 删除邮件信息
   * 
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deletWebmail(Connection conn, int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(conn, T9Webmail.class, seqId);
  }

  /**
   * 发送外部邮件
   * 
   * @param conn
   * @param eb
   * @param bodyId
   * @throws Exception
   * @throws NumberFormatException
   */
  public void sendWebMail(Connection conn, T9EmailBody eb, int bodyId)
      throws NumberFormatException, Exception {
    T9WebmailBody wmb = new T9WebmailBody();
    String fromWebmailId = eb.getFromWebmailId();
    T9ORM orm = new T9ORM();
    T9Webmail wm = (T9Webmail) orm.loadObjSingle(conn, T9Webmail.class, Integer
        .valueOf(fromWebmailId));
    wmb.setAttachmentId(eb.getAttachmentId());
    wmb.setAttachmentName(eb.getAttachmentName());
    wmb.setBodyId(bodyId);
    wmb.setContentHtml(eb.getContent());
    wmb.setFromMail(wm.getEmail());
    wmb.setIsHtml("1");
    wmb.setSendDate(new Date());
    wmb.setSubject(eb.getSubject());
    wmb.setToMail(eb.getToWebmail());
    String copy = eb.getToWebmailCopy();
    wmb.setToMailCopy(copy);
    wmb.setToMailSecret(eb.getToWebmailSecret());
    
    int seqId = getWebSeqId(conn, bodyId);
    if (seqId > 0) {
      wmb.setSeqId(seqId);
      orm.updateSingle(conn, wmb);
    } else {
      orm.saveSingle(conn, wmb);
    }

    T9MailSmtpUtil.sendWebMail(wm, wmb, T9EmailCont.UPLOAD_HOME);
  }

  /**
   * 
   * @param conn
   * @param eb
   * @param bodyId
   * @throws NumberFormatException
   * @throws Exception
   */
  public void saveWebMailBodyByEb(Connection conn, T9EmailBody eb, int bodyId)
      throws NumberFormatException, Exception {
    T9WebmailBody wmb = new T9WebmailBody();
    String fromWebmailId = eb.getFromWebmailId();
    T9ORM orm = new T9ORM();
    T9Webmail wm = (T9Webmail) orm.loadObjSingle(conn, T9Webmail.class, Integer
        .valueOf(fromWebmailId));
    wmb.setAttachmentId(eb.getAttachmentId());
    wmb.setAttachmentName(eb.getAttachmentName());
    wmb.setBodyId(bodyId);
    wmb.setContentHtml(eb.getContent());
    wmb.setFromMail(wm.getEmail());
    wmb.setIsHtml("1");
    wmb.setSendDate(new Date());
    wmb.setSubject(eb.getSubject());
    wmb.setToMail(eb.getToWebmail());
    int seqId = getWebSeqId(conn, bodyId);
    if (seqId > 0) {
      wmb.setSeqId(seqId);
      orm.updateSingle(conn, wmb);
    } else {
      orm.saveSingle(conn, wmb);
    }
  }

  /**
   * 
   * @param conn
   * @param bodyId
   * @return
   * @throws Exception
   */
  public int getWebSeqId(Connection conn, int bodyId) throws Exception {
    String sql = "select SEQ_ID FROM WEBMAIL_BODY WHERE BODY_ID=" + bodyId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int seqId = -1;
    try {
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while (rs.next()) {
        seqId = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    return seqId;
  }

  /**
   * 
   * @param conn
   * @param bodyIds
   * @throws Exception
   */
  public void deleteWebmail(Connection conn, String bodyIds) throws Exception {
    if(bodyIds == null || "".equals(bodyIds.trim())){
      return;
    }
    String sql = "DELETE FROM webmail_body WHERE BODY_ID IN (" + bodyIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }

  /**
   * 
   * @param conn
   * @param bodyIds
   * @throws Exception
   */
  public void deleteMailByBodyId(Connection conn, String bodyIds)
      throws Exception {
    if (bodyIds == null || "".equals(bodyIds) || ",".equals(bodyIds.trim())) {
      return;
    }
    if (bodyIds.endsWith(",")) {
      bodyIds = bodyIds.substring(0, bodyIds.length() - 1);
    }
    String sql = "DELETE FROM EMAIL WHERE BODY_ID IN(" + bodyIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }
  /**
   * 
   * @param conn
   * @param bodyIds
   * @throws Exception
   */
  public void deleteMailByBodyId(Connection conn, String bodyIds,String userId)
      throws Exception {
    if (bodyIds == null || "".equals(bodyIds) || ",".equals(bodyIds.trim())) {
      return;
    }
    if (bodyIds.endsWith(",")) {
      bodyIds = bodyIds.substring(0, bodyIds.length() - 1);
    }
    String sql = "DELETE FROM EMAIL WHERE BODY_ID IN(" + bodyIds + ") and TO_ID = " + userId;
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }

  /**
   * 
   * @param conn
   * @param bodyIds
   * @throws Exception
   */
  public void deleteMailBodyByBodyId(Connection conn, String bodyIds)
      throws Exception {
    String sql = "DELETE FROM EMAIL_BODY WHERE SEQ_ID IN(" + bodyIds + ")";
    PreparedStatement pstmt = null;
    try {
      pstmt = conn.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (Exception e) {
      throw e;

    } finally {
      T9DBUtility.close(pstmt, null, null);
    }
  }

  /**
   * 
   * @param conn
   * @param bodyIds
   * @throws Exception
   */
  public String deleteWebmailAll(Connection conn, String bodyIds)
      throws Exception {
    String[] bodyArrays = bodyIds.split(",");
    String result = "";
    String temp = "";
    for (String bodyId : bodyArrays) {
      if (isWebmail(conn, bodyId)) {
        if (!"".equals(temp)) {
          temp += ",";
        }
        temp += bodyId;
      } else {
        if (!"".equals(result)) {
          result += ",";
        }
        result += bodyId;
      }
    }
    if (temp != null && !"".equals(temp)) {
      deleteMailByBodyId(conn, temp);
      deleteMailBodyByBodyId(conn, temp);
//      deleteWebmail(conn, temp);
    }
    return result;
  }

  /**
   * 
   * @param conn
   * @param bodyId
   * @return
   * @throws Exception
   */
  public boolean isWebmail(Connection conn, String bodyId) throws Exception {
    boolean result = false;
    String sql = "select count(SEQ_ID) from email_body where SEQ_ID=" + bodyId
        + " and is_webmail='1'";

    Statement st = null;
    ResultSet rs = null;

    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if (rs.next()) {

        int count = rs.getInt(1);
        if (count > 0) {
          result = true;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(st, rs, log);
    }
    return result;
  }

  /**
   * 
   * @param con
   * @param bodyId
   * @throws Exception
   */
  public T9WebmailBody refreshLagerAttachmentMail(Connection con, int bodyId)
      throws Exception {
    T9ORM orm = new T9ORM();
    Map filters = new HashMap();
    filters.put("BODY_ID", bodyId);
    T9EmailBody eb = (T9EmailBody) orm.loadObjSingle(con, T9EmailBody.class,
        bodyId);
    T9WebmailBody wmb = (T9WebmailBody) orm.loadObjSingle(con,
        T9WebmailBody.class, filters);
    T9Webmail wm = (T9Webmail) orm.loadObjSingle(con, T9Webmail.class, Integer
        .valueOf(eb.getFromWebmailId()));
    int wmseqId = wmb.getSeqId();
    wmb = T9MailPop3Util.getWebMailBodyByMesId(con, wm, T9EmailCont.UPLOAD_HOME, wmb.getWebmailUid());
    wmb.setSeqId(wmseqId);
    updateWebMail2Local(con, wmb, wm, orm, eb);
    return wmb;
  }

  /**
   * 
   * @return
   * @throws Exception
   */
  public String hasLagerAttachment(Connection con, int bodyId) throws Exception {
    String result = "0";
    String sql = "select LARGE_ATTACHMENT from webmail_body where body_id="
        + bodyId;
    Statement st = null;
    ResultSet rs = null;
    try {
      st = con.createStatement();
      rs = st.executeQuery(sql);
      if (rs.next()) {
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(st, rs, log);
    }
    return result;
  }
  
  /**
   * 取得外部邮件
   * 
   * @param conn
   * @param attachPath
   * @throws Exception
   * @throws Exception
   */
  public void deleteNoUsedAttaches() throws Exception {
    List<T9Webmail> wms = null;

    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    try {
      dbConn = T9DataSources.getDataSource(T9SysProps.getSysDbDsName()).getConnection();
      
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery("select ATTACHMENT_ID, ATTACHMENT_NAME from EMAIL_BODY order by SEQ_ID");
      List<String> fileNameList = new ArrayList<String>();
      while (rs.next()) {
        String attachId = rs.getString(1);
        String attachName = rs.getString(2);
        if (attachId == null || attachName == null) {
          continue;
        }
        if ("".equals(attachId.trim()) || "".equals(attachName.trim())) {
          continue;
        }
      }
    }catch(Exception ex) {
    }finally {
      T9DBUtility.closeDbConn(dbConn, log);
    }
  }
}
