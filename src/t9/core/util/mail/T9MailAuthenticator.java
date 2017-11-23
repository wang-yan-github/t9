package t9.core.util.mail;

import javax.mail.*;

/**
 * 发送邮件身份验证信息类
 * @author yzq
 *
 */
public class T9MailAuthenticator extends Authenticator {
  String userName = null;
  String password = null;

  public T9MailAuthenticator() {
  }

  public T9MailAuthenticator(String username, String password) {
    this.userName = username;
    this.password = password;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(userName, password);
  }
}
