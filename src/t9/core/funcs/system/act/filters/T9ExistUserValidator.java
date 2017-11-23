package t9.core.funcs.system.act.filters;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;

public class T9ExistUserValidator  implements T9LoginValidator {


  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    return person != null;
  }



 
  public String getValidatorType() {
    return T9LoginErrorConst.LOGIN_NOTEXIST_USER;
  }

 
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    //系统日志-用户名错误
    T9SysLogLogic.addSysLog(conn, T9LogConst.INVALID_USER, "用户名错误",
        0,request.getRemoteAddr());
    
  }



  public int getValidatorCode() {
    return T9LoginErrorConst.LOGIN_NOTEXIST_USER_CODE;
  }




  public String getValidatorMsg() {
    return null;
  }

}
