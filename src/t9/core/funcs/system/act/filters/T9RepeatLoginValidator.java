package t9.core.funcs.system.act.filters;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9Props;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.global.T9SysProps;

public class T9RepeatLoginValidator implements T9LoginValidator {

  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    // TODO Auto-generated method stub

  }

  public int getValidatorCode() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.REPEAT_LOGIN_ERROR_CODE;
  }

  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getValidatorType() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.REPEAT_LOGIN_ERROR;
  }

  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    // TODO Auto-generated method stub
    String mulLogin = T9SysProps.getString("$ONE_USER_MUL_LOGIN");
    //Cookies cks = request.getCookies();
    if (mulLogin == null) {
      mulLogin = "1";
    }
    
    if ("0".equals(mulLogin)) {
      return !this.logic.isLogin(conn, person.getSeqId() , request );
    }
    return true;
  }

}
