package t9.mobile.message.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.message.data.T9MessageBack;
import t9.core.funcs.message.logic.T9MessageLogic;
import t9.core.funcs.message.logic.T9MessageUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.logic.T9SmsLogic;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.mobile.message.logic.T9PdaMessageLogic;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaMessageAct {
  public String message(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String ATYPE = request.getParameter("ATYPE");
      String P_VER = request.getParameter("P_VER");
      if ("mutiSend".equals(ATYPE)) {
        this.mutiSend(request, response, dbConn, person);
      } else if ("sendSignleMsg".equals(ATYPE)){
        this.sendSignleMsg(request, response, dbConn, person , P_VER);
      } else if ("getNewListSmsNum".equals(ATYPE)) {
        this.getNewListSmsNum(request, response, dbConn, person);
      } else if ("getSingleNewMsg".equals(ATYPE)) {
        this.getSingleNewMsg(request, response, dbConn, person , P_VER);
      } else if ("getDialogList".equals(ATYPE)) {
        this.getDialogList(request, response, dbConn, person , P_VER);
      } else if ("refreshList".equals(ATYPE)) {
        if ("loadList".equals(request.getParameter("A"))) {
          this.refreshList(request, response, dbConn, person);
        }
      } 
      
      return null;
    } catch (Exception ex) {
      throw ex;
    }
  }
  public String upload(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      
      Map<String , String> ATTACHMENTS = T9MobileUtility.uploadAttachment(fileForm, "voicemsg" , "","");
      String ATTACHMENT_ID =  T9Utility.null2Empty(ATTACHMENTS.get("id"));
      String ATTACHMENT_NAME = T9Utility.encodeSpecial(T9Utility.null2Empty(ATTACHMENTS.get("name")));
      
      if (ATTACHMENT_ID.endsWith(",")) {
        ATTACHMENT_ID = ATTACHMENT_ID.substring(0 , ATTACHMENT_ID.length() -1);
      }
      if (ATTACHMENT_NAME.endsWith("*")) {
        ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0 , ATTACHMENT_NAME.length() -1);
      }
      
      String P_VER = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("P_VER") , "UTF-8"));
      String DURATION = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("DURATION") , "UTF-8")); 
      String DEST_UID = T9Utility.null2Empty(URLDecoder.decode(fileForm.getParameter("DEST_UID") , "UTF-8"));
      
      int iDURATION = T9Utility.isInteger(DURATION) ? Integer.parseInt(DURATION) : 0;
      String content = "[vm]" +ATTACHMENT_ID+ "|" +ATTACHMENT_NAME+ "|" + iDURATION +"[/vm]";
      
      T9PdaMessageLogic l = new T9PdaMessageLogic();
      l.sendVoiceMsg(dbConn, person.getSeqId(), DEST_UID, content, new Date());
      
      T9MobileUtility.output(response, "+OK " + content);
      return null;
    }  catch (Exception ex) {
       throw ex;
    }
  }
  public String down(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    try {
      String aid = request.getParameter("ATTACHMENT_ID");
      String aname = request.getParameter("ATTACHMENT_NAME");
      
      T9WorkFlowUtility u = new T9WorkFlowUtility();
      String path = u.getAttachPath(aid, aname, "voicemsg");
      
      String fileName = URLEncoder.encode(aname, "UTF-8");
     
      response.setContentType("application/octet-stream");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
      InputStream in = null;
      OutputStream out = null;
      try {      
        in = new FileInputStream(path);
        out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = in.read(buff)) > 0) {        
           out.write(buff, 0, readLength);
        }
        out.flush();
      }catch(Exception ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (in != null) {
            in.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      
      return null;
    }  catch (Exception ex) {
       throw ex;
    }
  }
  
  public void refreshList(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  ) throws Exception {
    
    T9PdaMessageLogic logic = new T9PdaMessageLogic();
    T9MobileUtility.output(response,logic.refreshList(dbConn, person) );
  }
  
  public void getDialogList(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person, String p_VER  ) throws Exception {
    
    int id = 0;
    String ss = request.getParameter("Q_ID");
    if (T9Utility.isInteger(ss)) {
      id = Integer.parseInt(ss);
    }
    T9PdaMessageLogic logic = new T9PdaMessageLogic();
    T9MobileUtility.output(response,logic.getDialogList(dbConn, person, id , p_VER) );
  }
  
  public void getSingleNewMsg(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person, String p_VER   ) throws Exception {
    
    int _FROM_UID = 0;
    String ss = request.getParameter("Q_UID");
    if (T9Utility.isInteger(ss)) {
      _FROM_UID = Integer.parseInt(ss);
    }
    
    String getType =  request.getParameter("GET_TYPE");
    
    
    T9PdaMessageLogic logic = new T9PdaMessageLogic();
    T9MobileUtility.output(response,logic.getSingleNewMsg(dbConn, person.getSeqId(), _FROM_UID , p_VER , getType) );
  }
  
  public void getNewListSmsNum(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  ) throws Exception {
    String str = "NONEWDATA";
    
    T9SmsLogic smsLogic = new T9SmsLogic();
    int f = smsLogic.isRemindNew1(person.getSeqId());
    if (f == 1) {
      //T9PdaMessageLogic logic = new T9PdaMessageLogic();
      T9MessageLogic logic2 = new T9MessageLogic();
      int result = logic2.isRemind(dbConn, person.getSeqId());
          //logic.getNewListSmsNum(dbConn, person.getSeqId());
      if (result != 0) {
        str = String.valueOf(result);
      }
    }
    
    T9MobileUtility.output(response,str );
  }
  public void sendSignleMsg(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  , String P_VER) throws Exception {
    
    String content = T9Utility.null2Empty(request.getParameter("CONTENT"));
    String isVoceMsg = "0";
    String aid = "";
    String aname = "";
    String duration = "";
    
    
    if (T9MobileUtility.isVoiceMsg(content)) {
      isVoceMsg= "1";
      String[] a = T9MobileUtility.getVoiceMsgAttach(content);
      aid = a[0];
      aname = a[1];
      duration = a[2];
      
      String  VOICE_PF = "5".equals(P_VER)? "ios_client" : "android_client";
      content = T9MobileUtility.getVoiceMsgOutputForMobile(content , VOICE_PF);
    } else {
      content = content.replace("\n", "<br />\n");
      content = content.replace("\n", "<br />\r");
    }
    
    
    String toId = T9Utility.null2Empty(request.getParameter("Q_UID"));
    
    Date sendTime = new Date();
    if (!T9Utility.isNullorEmpty(toId)) {
      T9MessageBack mb = new T9MessageBack();
      mb.setContent(content);
      mb.setFromId(person.getSeqId());
      mb.setRemindUrl("");
      mb.setSendDate(sendTime);
      mb.setSmsType("0");
      mb.setToId(toId);
      T9MessageUtil.smsBack(dbConn, mb);
    }
    Map<String , String> map = new HashMap();
    map.put("q_id", "");
    map.put("q_uid", String.valueOf(person.getSeqId()));
    map.put("q_name", T9Utility.encodeSpecial(person.getUserName()));
    map.put("avatar", T9MobileUtility.showAvatar(person.getAuatar(), person.getSex()));
    map.put("send_time", T9Utility.getDateTimeStr(sendTime));
    map.put("content", content);
    map.put("msg_from", "1");
    map.put("msg_type_name", "");
    map.put("isvoicemsg", isVoceMsg);
    map.put("attachment_id", aid);
    map.put("attachment_name", aname);
    map.put("duration", duration);
    
    T9MobileUtility.output(response,"[" + T9MobileUtility.mapToJson(map) + "]" );
  }
   
  public void mutiSend(HttpServletRequest request,
      HttpServletResponse response ,Connection dbConn , T9Person person  ) throws Exception {
    
    String content =  T9Utility.null2Empty(request.getParameter("CONTENT"));
    String toId = T9Utility.null2Empty(request.getParameter("TO_UID"));
    
    if (!T9Utility.isNullorEmpty(toId)) {
      Date sendTime = new Date();
      T9MessageBack mb = new T9MessageBack();
      
      content = content.replace("\n", "<br />\n");
      content = content.replace("\n", "<br />\r");
      
      mb.setContent(content);
      mb.setFromId(person.getSeqId());
      mb.setRemindUrl("");
      mb.setSendDate(sendTime);
      mb.setSmsType("0");
      mb.setToId(toId);
      T9MessageUtil.smsBack(dbConn, mb);
      T9MsgPusher.mobilePushNotification(toId, content, "message");
    }
    T9MobileUtility.output(response, "OK");
  }
}
