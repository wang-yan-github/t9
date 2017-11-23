package t9.subsys.oa.smsInterface.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9InterfaceAct {

  /**
   * 集成oa内部短信接口
   * 
   * @param content
   * @param fromId
   * @param toId
   * @param smsType
   * @param remindUrl
   * @return
   * @throws Exception
   */
  public String getSmsInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String smsContent = request.getParameter("content");
      String fromIdStr = request.getParameter("fromId");
      int fromId = Integer.parseInt(fromIdStr.substring(0, fromIdStr.length()-1));
      String toId = request.getParameter("toId");
      String smsType = request.getParameter("smsType");
      String remindUrl = request.getParameter("remindUrl").replaceAll("@", "&");
      
      request.getHeader("Referer");
      if(remindUrl.startsWith("1:")){
        String temp[] = request.getHeader("Referer").split("/");
        remindUrl = temp[0] + "//" + temp[1] + temp[2] + "/" + temp[3] + "/" + remindUrl.substring(2, remindUrl.length());
      }
      
      doSmsBackTime(dbConn, smsContent, fromId, toId, smsType, remindUrl, new Date());
      
      PrintWriter pw = response.getWriter();
      pw.println("var xxx = true;");
      pw.flush();
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  
  public String getSmsPhoneInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String smsContent = request.getParameter("content");
      String fromIdStr = request.getParameter("fromId");
      int fromId = Integer.parseInt(fromIdStr.substring(0, fromIdStr.length()-1));
      String toId = request.getParameter("toId");
      
      T9MobileSms2Logic sbl = new T9MobileSms2Logic();
      sbl.remindByMobileSms(dbConn, toId, fromId, smsContent, new Date());
      
      PrintWriter pw = response.getWriter();
      pw.println("var xxx = true;");
      pw.flush();
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 短信提醒(带时间)
   * 
   * @param conn
   * @param content
   * @param fromId
   * @param toId
   * @param type
   * @param remindUrl
   * @param sendDate
   * @throws Exception
   */
  public static void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
      throws Exception {
    T9SmsBack sb = new T9SmsBack();
    sb.setContent(content);
    sb.setFromId(fromId);
    sb.setToId(toId);
    sb.setSmsType(type);
    sb.setRemindUrl(remindUrl);
    sb.setSendDate(sendDate);
    T9SmsUtil.smsBack(conn, sb);
  }
}
