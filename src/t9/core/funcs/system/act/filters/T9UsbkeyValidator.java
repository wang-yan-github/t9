package t9.core.funcs.system.act.filters;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9UsbKey;

public class T9UsbkeyValidator implements T9LoginValidator {

  private String msg;
  
  public void addSysLog(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    // TODO Auto-generated method stub

  }

  
  public int getValidatorCode() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_USBKEY_ERROR_CODE;
  }
  
  public String getValidatorType() {
    // TODO Auto-generated method stub
    return T9LoginErrorConst.LOGIN_USBKEY_ERROR;
  }
  
  public boolean isValid(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception {
    // TODO Auto-generated method stub
    return isUsbkey(request, person, conn);
  }
  
  private boolean isUsbkey(HttpServletRequest request, T9Person person,
      Connection conn) throws Exception{
    
    //Map<String,String> map = logic.getSysPara(conn);
    //String secUserName = map.get("SEC_KEY_USER");
    
    String keyUser = T9Utility.null2Empty(request.getParameter("KEY_USER"));
    int randomNum = 123456; 
    Integer randomInt = (Integer)request.getSession().getAttribute("RANDOM_NUMBER");
    if (randomInt != null) {
      randomNum = randomInt;
    }
    //tVHbkPWW57Hw.
    //使用UsbKey 登录
    String useingKey = person.getUseingKey();
    if (useingKey == null) {
      useingKey = "";
    }
    else {
      useingKey = useingKey.trim();
    }
    String keySn = T9Utility.null2Empty(request.getParameter("KEY_SN"));
    String keyDigest = T9Utility.null2Empty(request.getParameter("KEY_DIGEST"));
    String userKey = "";
    String userKeyStr = "";
    
    if (!"".equals(keySn) && !"".equals(keyUser) && !"".equals(keyDigest) && "1".equals(useingKey)) {
      userKey = keySn;
      userKeyStr = userKey.substring(0, 8).toUpperCase();
      boolean isValid = T9UsbKey.digestComp(keyDigest, String.valueOf(randomNum), T9UsbKey.md5Hex(person.getPassword()));
      
      if (!userKeyStr.equals(T9Const.KEY_TD_SIGN) || !keySn.equals(userKey) || !isValid) {
        return false;
      }
      else{
        return true;
      }
    }
    
    return false;
  }


  
  public String getValidatorMsg() {
    // TODO Auto-generated method stub
    return null;
  }

}
