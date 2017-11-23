package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.util.auth.T9PassEncrypt;
public class T9InitialPwValidator implements T9LoginValidator {

 
  public int getValidatorCode() {
    return T9LoginErrorConst.LOGIN_INITIAL_PW_CODE;
  }

  
  public String getValidatorType() {
    return T9LoginErrorConst.LOGIN_INITIAL_PW;
  }
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    return !isInitialPw(conn, person);
  }
  
  /**
   * 判断是否为初始密码
   * @param person
   * @return
   * @throws Exception 
   */
  private boolean isInitialPw(Connection conn, T9Person person) throws Exception{
    
    Map<String,String> map = logic.getSysPara(conn);
    String flag = map.get("SEC_INIT_PASS");
    
    if ("1".equals(flag)){
      return person.getLastPassTime() == null;
     // return (person.getPassword() == null || T9PassEncrypt.isValidPas("", person.getPassword().trim()));
    }
    else{
      return false;
    }
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    // TODO Auto-generated method stub
    
  }


  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  }
}
