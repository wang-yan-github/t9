package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9SecureKey;
import t9.core.funcs.person.logic.T9SecureCardLogic;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.common.T9ValidatorHelper;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.funcs.system.security.data.T9Security;
import t9.core.funcs.system.security.logic.T9SecurityLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9LogConst;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9ORM;
import seamoonotp.seamoonapi;

public class T9VerificationCodeValidator implements T9LoginValidator {
  String isSecureCard = "0";
  String returnValue = "0";

  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    String verificationCode = request.getParameter("verificationCode");
    
    Map<String,String> map = logic.getSysPara(conn);
    
    String vc = map.get("VERIFICATION_CODE");
    
    if (!"1".equals(vc)){
      return true;
    }
    HttpSession session = request.getSession(false);
    String value = T9Utility.null2Empty((String)session.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY));
    return value.equalsIgnoreCase(verificationCode);
  }

  
  public int getValidatorCode() {
    return T9LoginErrorConst.VERIFICATION_CODE_CODE;
  }

  
  public String getValidatorType() {
    return T9LoginErrorConst.VERIFICATION_CODE_ERROR;
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    //系统日志-登陆密码错误
    T9SysLogLogic.addSysLog(conn, T9LogConst.LOGIN_PASSWORD_ERROR, "验证码错误",
      person.getSeqId(), request.getRemoteAddr());
  }


  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  }

}
