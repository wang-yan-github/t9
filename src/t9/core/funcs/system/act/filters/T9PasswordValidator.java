package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9ORM;
import seamoonotp.seamoonapi;

public class T9PasswordValidator implements T9LoginValidator {
  String isSecureCard = "0";
  String returnValue = "0";
  String password  =  "";
  public T9PasswordValidator(String password) {
    this.password = password;
  }
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn ) throws Exception  {
    String pwd = "";
    
    
    //是否开启动态密码卡验证
    T9SecurityLogic orgLogic = new T9SecurityLogic();
    T9Security security  = orgLogic.getSecritySecureKey(conn);
    
    //该用户是否绑定动态密码卡
    T9SecureCardLogic secureCardLogic = new T9SecureCardLogic();
    T9SecureKey secureKey = secureCardLogic.getKeyInfo(conn , person);
    
    if("1".equals(security.getParaValue()) && secureKey != null){
      isSecureCard = security.getParaValue();
      if(password.length() < 6){
        return false;
      }
      pwd = password.substring(0, password.length() - 6);
    }
    else{
      pwd = password;
    }
    
    if (pwd == null) {
      pwd = null;
    }
     
    if (person.getPassword() == null){
      person.setPassword("");
    }
    
    if (person != null && T9PassEncrypt.isValidPas(pwd, person.getPassword().trim())){
      if("1".equals(security.getParaValue()) && secureKey != null){
        
        seamoonapi sc = new seamoonapi();
        String newSninfo = sc.checkpassword(secureKey.getKeyInfo(), password.substring(password.length()-6 , password.length()));
        returnValue = newSninfo;
        if(newSninfo.length() > 3){
          secureKey.setKeyInfo(newSninfo);
          T9ORM orm = new T9ORM();
          orm.updateSingle(conn, secureKey);
          return true;
        }
        else
          return false;
      }
      else
        return true;
    }
    else {
      return false;
    }
  }
  

  
  public int getValidatorCode() {
    // TODO Auto-generated method stub
    if("1".equals(isSecureCard)){
      if("-1".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_CODE_SECURE_CARD_1;
      }
      else if("-2".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_CODE_SECURE_CARD_2;
      }
      else if("0".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_CODE_SECURE_CARD_3;
      }
    }
    return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_CODE;
  }

  
  public String getValidatorType() {
    // TODO Auto-generated method stub
    if("1".equals(isSecureCard)){
      if("-1".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_SECURE_CARD_1;
      }
      else if("-2".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_SECURE_CARD_2;
      }
      else if("0".equals(returnValue)){
        return T9LoginErrorConst.LOGIN_PASSWORD_ERROR_SECURE_CARD_3;
      }
    }
    return T9LoginErrorConst.LOGIN_PASSWORD_ERROR;
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    //系统日志-登陆密码错误
    T9SysLogLogic.addSysLog(conn, T9LogConst.LOGIN_PASSWORD_ERROR, "登录密码错误",
      person.getSeqId(), request.getRemoteAddr());
  }


  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  }

}
