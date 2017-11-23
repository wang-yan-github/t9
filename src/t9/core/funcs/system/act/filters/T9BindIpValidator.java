package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.common.T9ValidatorHelper;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;

public class T9BindIpValidator implements T9LoginValidator {

  /**
   * 绑定ip
   * @param person
   * @param ip
   * @return
   */
  private boolean bindIp(T9Person person, String ip){
    String bindIp = person.getBindIp();
    if (bindIp != null && !"".equals(bindIp.trim())){
      
      boolean result = false;
      
      for (String s: bindIp.split(",")){
        
        if (s.contains("-")){
          String[] segment = s.split("-");
          if (segment.length == 2) {
            result = T9ValidatorHelper.betweenIP(ip, s.split("-")[0], s.split("-")[1]);
          }
        }
        
        else{
          result = s.equals(ip);
        }
        
        if (result){
          break;
        }
      }
      
      return !result;
    }
    else{
      return false;
    }
  }


  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) {
    return !this.bindIp(person, request.getRemoteAddr());
  }


  public String getValidatorType() {
    return T9LoginErrorConst.LOGIN_BIND_IP;
  }


  public int getValidatorCode() {
    return T9LoginErrorConst.LOGIN_BIND_IP_CODE;
  }

 
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    //系统日志-非法ip登陆
    T9SysLogLogic.addSysLog(conn, T9LogConst.ILLEGAL_IP_LOGIN, "非法ip登录", 
        person.getSeqId(),request.getRemoteAddr());
  }


  public String getValidatorMsg() {
    return null;
  }
}
