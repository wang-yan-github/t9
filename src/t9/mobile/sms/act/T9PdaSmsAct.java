package t9.mobile.sms.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.message.logic.T9PdaMessageLogic;
import t9.mobile.sms.logic.T9PdaSmsLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9PdaSmsAct {
  public String sms(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String ATYPE = request.getParameter("ATYPE");
      if ("readsms".equals(ATYPE)) {
        this.readsms(request, response, dbConn, person);
      }  else if ("refreshList".equals(ATYPE)) {
          this.loadList(request, response, dbConn, person);
      } 
      
      
      return null;
    } catch (Exception ex) {
      throw ex;
    }
  }
  public void loadList(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  ) throws Exception {
    String query = "";
    T9PdaSmsLogic logic = new T9PdaSmsLogic();
    SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    String ss = dateFormat.format(date);
    String CURRITERMS = T9Utility.null2Empty(request.getParameter("CURRITERMS"));
    if ("loadList".equals(request.getParameter("A"))) {
      query = "SELECT SMS.SEQ_ID AS SMS_ID,FROM_ID, SEND_TIME,SMS_TYPE,CONTENT,REMIND_FLAG,REMIND_URL,PERSON.SEQ_ID AS USER_ID,USER_NAME,AUATAR, SEX from SMS,SMS_BODY,PERSON where SMS.BODY_SEQ_ID=SMS_BODY.SEQ_ID and PERSON.SEQ_ID=SMS_BODY.FROM_ID and TO_ID='"+person.getSeqId()+"' and "+T9DBUtility.getDateFilter("SEND_TIME",ss, "<=")+" and DELETE_FLAG<>'1' and REMIND_FLAG<>'0' order by SEND_TIME desc " ;
    } else {
      query = "SELECT SMS.SEQ_ID AS SMS_ID,FROM_ID, SEND_TIME,SMS_TYPE,CONTENT,REMIND_FLAG,REMIND_URL,PERSON.SEQ_ID AS USER_ID,USER_NAME,AUATAR , SEX from SMS,SMS_BODY,PERSON where SMS.BODY_SEQ_ID=SMS_BODY.SEQ_ID and PERSON.SEQ_ID=SMS_BODY.FROM_ID and TO_ID='"+person.getSeqId()+"' and "+T9DBUtility.getDateFilter("SEND_TIME", ss, "<=")+"  and DELETE_FLAG<>'1' and REMIND_FLAG<>'0' order by SEND_TIME desc ";
      if (!logic.hasRow(dbConn , query)) {
        T9MobileUtility.output(response,"NOMOREDATA" );
        return ;
      }
    }
    String sb = logic.sms(dbConn , person , query ,CURRITERMS);
    T9MobileUtility.output(response,sb);
  }
  
  public void readsms(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  ) throws Exception {
    
    String SMS_ID = T9Utility.null2Empty(request.getParameter("SMS_ID"));
    String query = "update SMS set REMIND_FLAG=0 where SEQ_ID='"+ SMS_ID +"'";
    T9MobileUtility.updateSql(dbConn, query);
    T9MobileUtility.output(response, "");
  }
  
  
}
