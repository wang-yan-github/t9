package t9.core.funcs.sms.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9Sms;
import t9.core.funcs.sms.data.T9SmsBody;
import t9.core.funcs.sms.logic.T9SmsLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

/**
 * 组织各个短信箱的数据
 * @author Think
 *
 */
public class T9SmsBoxDataAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.sms.act.T9SmsBoxDataAct");
  /**
   * 列出所有已发送的短信
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listAllSms(HttpServletRequest request,
      HttpServletResponse response) throws Exception { 
    
    String toIdStr = request.getParameter("toId");
    StringBuffer data = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      T9SmsLogic sl = new T9SmsLogic();
      
      PrintWriter pw = response.getWriter();
      pw.println(data.toString());
      pw.flush();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;//sentsms
  }
  
}
