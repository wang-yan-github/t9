package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.common.T9ValidatorHelper;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9LogConst;

public class T9IpRuleValidator implements T9LoginValidator{

  private boolean isIpRuleLimit(Connection conn, T9Person person,String ip) throws Exception{
    
    Map<String,String> map = logic.getSysPara(conn);
    String unlimted = map.get("IP_UNLIMITED_USER");
    unlimted = unlimted == null ? "" : "," + unlimted + ",";
    
    String seqId = "," + person.getSeqId() + ",";
    
    if (unlimted.contains(seqId)){
      return false;
    }
    else{
      List<T9IpRule> list  = logic.getIpRule(conn);
      if (list.size() == 0){
        return false;
      }
      else{
        for(T9IpRule ir : list){
          if (T9ValidatorHelper.betweenIP(ip, ir.getBeginIp(), ir.getEndIp())){
            return false;
          }
        }
        return true;
      }
    }
  }

  
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    
    return !this.isIpRuleLimit(conn, person, request.getRemoteAddr());
  }

 
  public int getValidatorCode() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_IP_RULE_LIMIT_CODE;
  }

 
  public String getValidatorType() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_IP_RULE_LIMIT;
  }

  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    //系统日志-非法ip登陆
    T9SysLogLogic.addSysLog(conn, T9LogConst.ILLEGAL_IP_LOGIN, "非法ip登录", 
        person.getSeqId(),request.getRemoteAddr());
  }


  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  } 
}
