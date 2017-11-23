package t9.core.funcs.system.act.filters;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;

public class T9ForbidLoginValidator implements T9LoginValidator{

 
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    return forbidLogin(person);
  }

  /**
   * 判断是否禁止登录
   * @param person
   * @return
   */
  private boolean forbidLogin(T9Person person){
    String notLogin = person.getNotLogin();
    if (notLogin == null) {
      notLogin = "0";
    }
    return !"1".equals(notLogin.trim());
  }
 
  public int getValidatorCode() {
    return T9LoginErrorConst.LOGIN_FORBID_LOGIN_CODE;
  }

  
  public String getValidatorType() {
    return T9LoginErrorConst.LOGIN_FORBID_LOGIN;
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    
  }

  
  public String getValidatorMsg() {
    return null;
  }
}
