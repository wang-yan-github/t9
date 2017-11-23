package t9.mobile.inc.act;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.message.data.T9MessageBack;
import t9.core.funcs.message.logic.T9MessageLogic;
import t9.core.funcs.message.logic.T9MessageUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.logic.T9SmsLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.inc.logic.T9PdaUserSelectLogic;
import t9.mobile.message.logic.T9PdaMessageLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaUserSelectAct {
  public String select(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String ATYPE = request.getParameter("A");
      T9PdaUserSelectLogic logic = new T9PdaUserSelectLogic();
      if ("get_Dept".equals(ATYPE)) {
        StringBuffer sb = new StringBuffer("[");
        logic.getDept(dbConn  , "0" , 0 , sb);
        if (sb.toString().endsWith(",")) {
          sb.deleteCharAt(sb.length() -1);
        } else {
          T9MobileUtility.output(response,"");
          return null;
        }
        sb.append("]");
        T9MobileUtility.output(response,sb.toString());
      } else if ("get_USER".equals(ATYPE)){
        String Q_ID = T9Utility.null2Empty(request.getParameter("Q_ID"));
        T9MobileUtility.output(response, logic.getUser(dbConn , person , Q_ID));
      } else  {
        String KWORD = T9Utility.null2Empty(request.getParameter("KWORD"));
         if (T9Utility.isNullorEmpty(KWORD)){
           T9MobileUtility.output(response,"");
           return null;
         }
         String DATA_TYPE = request.getParameter("DATA_TYPE");
         String sb = logic.select(dbConn , KWORD , DATA_TYPE);
         T9MobileUtility.output(response, sb);
      } 
      return null;
    } catch (Exception ex) {
      throw ex;
    }
  }
  
}
