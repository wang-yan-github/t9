package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.common.T9ValidatorHelper;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;
import t9.core.util.auth.T9PassEncrypt;

public class T9RetryLoginValidator implements T9LoginValidator {

  private int times;
  private int minutes;
  
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    
    Map<String,String> map = logic.getSysPara(conn);
    
    String retry = map.get("SEC_RETRY_BAN");
    
    //当不限制错误登陆次数时,返回验证成功
    if (!"1".equals(retry)){
      return true;
    }
    
    try {
      this.times = Integer.parseInt(map.get("SEC_RETRY_TIMES"));
      this.minutes = Integer.parseInt(map.get("SEC_BAN_TIME"));
    } catch (NumberFormatException e) {
      //默认设置10分钟3次登陆错误      this.times = 3;
      this.minutes = 10;
    }
    
    if (logic.retryLogin(conn, this.times, this.minutes, person.getSeqId(), request.getRemoteAddr())){
      return true;
    }
    else{
      return false;
    }
  }
  
  public int getValidatorCode() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_RETRY_ERROR_CODE;
  }

  
  public String getValidatorType() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_RETRY_ERROR;
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    
  }

/**
 * 返回验证的具体信息
 */
  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return "{\"times\":" + times + ",\"minutes\":" + minutes + "}";
  }

}
