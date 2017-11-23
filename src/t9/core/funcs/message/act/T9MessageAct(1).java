package t9.core.funcs.message.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.message.data.T9Message;
import t9.core.funcs.message.data.T9MessageBody;
import t9.core.funcs.message.logic.T9MessageLogic;
import t9.core.funcs.sms.data.T9Sms;
import t9.core.funcs.sms.logic.T9SmsLogic;
import t9.core.funcs.sms.logic.T9SmsTestLogic;
import t9.core.funcs.system.censorcheck.logic.T9CensorCheckLogic;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.system.ispirit.n12.org.logic.T9IsPiritLogic;
import t9.core.funcs.system.ispirit.sms.logic.T9SmsBoxLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9MessageAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.Message.act.T9MessageAct");
  T9MessageLogic messageLogic = null;
  
  public String addMessageBody(HttpServletRequest request,
      HttpServletResponse response) throws Exception  {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    int userId = person.getSeqId();
    String toId = request.getParameter("user");
    String content = request.getParameter("content");
    String sendTime = request.getParameter("sendTime");
    String MessageType = request.getParameter("messageType");
    String remindUrl = request.getParameter("remindUrl");

    T9ORM orm = new T9ORM();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      int fromId = userId;//"liuhan";
      T9MessageBody messageBody = new T9MessageBody();
      messageBody.setFromId(fromId);
      messageBody.setContent(content);
      Date time = null;
      if(sendTime == null || "".equals(sendTime)){
        time = new Date();
      }else {
        try{
          time = T9Utility.parseDate(sendTime);
        } catch (Exception e){
          time = new Date();
        }
      }
      if(remindUrl != null && !"".equals(remindUrl)){
        messageBody.setRemindUrl(remindUrl);
      }else{
        messageBody.setRemindUrl("");
      }
      messageBody.setSendTime(time);
      ArrayList<T9Message> messageList = new ArrayList<T9Message>();
      T9Message message = null;
      
      String flag = "1";  //标记为2  表示没有阅读的
      String delFlag = "0";
      if("0".equals(toId) || "ALL_DEPT".equals(toId)){
        toId = T9OrgSelectLogic.getAlldept(dbConn);
      }
      String[] userIds = toId.split(",");
      String extendFlagStr = T9SysProps.getProp("$Message_DELAY_PER_ROWS");
      String extendTimeStr = T9SysProps.getProp("$Message_DELAY_SECONDS");
      long curTimeL = time.getTime();
      int extendTime = 0;
      int extendFlag = 0;
      Date remindDate = time;
      if (T9Utility.isInteger(extendTimeStr)) {
        extendTime = Integer.valueOf(extendTimeStr);
      }
      if (T9Utility.isInteger(extendFlagStr)) {
        extendFlag = Integer.valueOf(extendFlagStr);
      }
      for(int i = 0; i < userIds.length; i++) {
        message = new T9Message();
        message.setToId(Integer.parseInt(userIds[i]));
        message.setRemindFlag(flag);
        message.setDeleteFlag(delFlag);
        if(i>0 && extendFlag != 0 && extendTime != 0 && (i % extendFlag) ==0 ){
          long remindTime = curTimeL + (i / extendFlag) * extendTime*1000;
          remindDate = new Date(remindTime);
        }
        message.setRemindTime(remindDate);
        messageList.add(message);
        
        //设置提醒
        if(T9Utility.isNullorEmpty(sendTime))
        T9IsPiritLogic.setUserMessageRemind(userIds[i]);
        
      }
      messageBody.setMessagelist(messageList);
//      if(){
//        
//      }
      messageBody.setMessageType(MessageType);
    
      orm.saveComplex(dbConn, messageBody);
    
      T9MsgPusher.pushSms(toId);
      T9MsgPusher.mobilePushNotification(toId, content, "message");
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加微讯");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String addMessageBody1(HttpServletRequest request,
      HttpServletResponse response) throws Exception  {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    int userId = person.getSeqId();
    String toId = request.getParameter("user");
    String content = request.getParameter("content");
    String sendTime = request.getParameter("sendTime");
    String MessageType = request.getParameter("messageType");
    String remindUrl = request.getParameter("remindUrl");

    T9ORM orm = new T9ORM();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      int fromId = userId;//"liuhan";
      T9MessageBody messageBody = new T9MessageBody();
      messageBody.setFromId(fromId);
      messageBody.setContent(content);
      Date time = null;
      if(sendTime == null || "".equals(sendTime)){
        time = new Date();
      }else {
        try{
          time = T9Utility.parseDate(sendTime);
        } catch (Exception e){
          time = new Date();
        }
      }
      if(remindUrl != null && !"".equals(remindUrl)){
        messageBody.setRemindUrl(remindUrl);
      }else{
        messageBody.setRemindUrl("");
      }
      messageBody.setSendTime(time);
      ArrayList<T9Message> messageList = new ArrayList<T9Message>();
      T9Message message = null;
      
      String flag = "0";  //标记为2  表示没有阅读的

      String delFlag = "0";
      if("0".equals(toId) || "ALL_DEPT".equals(toId)){
        toId = T9OrgSelectLogic.getAlldept(dbConn);
      }
      String[] userIds = toId.split(",");
      String extendFlagStr = T9SysProps.getProp("$Message_DELAY_PER_ROWS");
      String extendTimeStr = T9SysProps.getProp("$Message_DELAY_SECONDS");
      long curTimeL = time.getTime();
      int extendTime = 0;
      int extendFlag = 0;
      Date remindDate = time;
      if (T9Utility.isInteger(extendTimeStr)) {
        extendTime = Integer.valueOf(extendTimeStr);
      }
      if (T9Utility.isInteger(extendFlagStr)) {
        extendFlag = Integer.valueOf(extendFlagStr);
      }
      for(int i = 0; i < userIds.length; i++) {
        message = new T9Message();
        message.setToId(Integer.parseInt(userIds[i]));
        message.setRemindFlag(flag);
        message.setDeleteFlag(delFlag);
        if(i>0 && extendFlag != 0 && extendTime != 0 && (i % extendFlag) ==0 ){
          long remindTime = curTimeL + (i / extendFlag) * extendTime*1000;
          remindDate = new Date(remindTime);
        }
        message.setRemindTime(remindDate);
        messageList.add(message);
        
        //设置提醒
        if(T9Utility.isNullorEmpty(sendTime))
        T9IsPiritLogic.setUserMessageRemind(userIds[i]);
        
      }
      messageBody.setMessagelist(messageList);
//      if(){
//        
//      }
      messageBody.setMessageType(MessageType);
    
      orm.saveComplex(dbConn, messageBody);
    
      T9MsgPusher.pushSms(toId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加微讯");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  
  
  public String showMessageBody(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("utf-8");
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      messageLogic = new T9MessageLogic();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9MessageBody messageBody = null;
      T9ORM orm = new T9ORM();
      messageBody = (T9MessageBody)orm.loadObjSingle(dbConn, T9MessageBody.class, Integer.parseInt(seqId));
      messageLogic.updateFalg(dbConn, person.getSeqId(), Integer.parseInt(seqId));
      StringBuffer data = T9FOM.toJson(messageBody);
      //System.out.println(data.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出微讯内容！");
      request.setAttribute(T9ActionKeys.RET_DATA,data.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");  
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
     // Map map = new HashMap();
     // map.put("seqId", seqId);
      T9ORM orm = new T9ORM();
      T9Message message = (T9Message)orm.loadObjSingle(dbConn, T9Message.class, Integer.parseInt(seqId));
      String bodyId=message.getBodySeqId()+"";
      T9MessageBody mb = (T9MessageBody)orm.loadObjSingle(dbConn, T9MessageBody.class, Integer.parseInt(bodyId));
      
      orm.deleteSingle(dbConn, message);
     // orm.deleteSingle(dbConn, mb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除内部短信！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 短信删除逻辑
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deType = request.getParameter("deType");
      String bodyId = request.getParameter("bodyId");
      T9MessageLogic Logic = new T9MessageLogic();
      if(bodyId != null){
        String[] bIds = bodyId.split(",");
        for (String bIdstr : bIds){
          if("".equals(bIdstr.trim())){
            continue;
          }
          int bId = Integer.parseInt(bIdstr.trim());
          Logic.doDelSms(dbConn, bId, deType, person.getSeqId());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除！");
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除失败！可能原因：" + e.getMessage());
       e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getMessageTypeDesc(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
    String smsType = request.getParameter("messageType");
    String code = request.getParameter("code");
    Connection dbConn = null;
    try {
      
        
  

      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      dbConn = requestDbConn.getSysDbConn();
      T9MessageLogic sl = new T9MessageLogic();
      String data="";
      if(T9Utility.isNullorEmpty(smsType) || "null".equals(smsType)){    
        data="微讯";
      }else{
        
        if(T9Utility.isNullorEmpty(smsType) || "null".equals(smsType)){    
          smsType="0";
        }
        
        data= sl.getMessageTypeDesc(dbConn, Integer.parseInt(smsType.trim()), code);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 标记为读
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String resetFlag(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      String bodySeqIds = request.getParameter("seqId");
      T9ORM orm = new T9ORM();
 
      T9MessageLogic sl = new T9MessageLogic();
    
      String[] bodyIds = bodySeqIds.split(",");
      for (String bodyId : bodyIds) {
        if("".equals(bodyId.trim())){
          continue;
        }
        
        //解决sms表 body_seq_id和to_id索引出数据不唯一的情况

        List<Integer> list = sl.getMessageSeqIds(dbConn, toId, Integer.parseInt(bodyId.trim()));
        for (int id : list) {
          T9Message Message = (T9Message) orm.loadObjSingle(dbConn, T9Message.class, id);
          Message.setSeqId(id);
          Message.setToId(toId);
          Message.setRemindFlag("0");
          orm.updateSingle(dbConn, Message);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 已收微讯
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String messageInbox(HttpServletRequest request,
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    int toId = person.getSeqId();  //用户seqId
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try {
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);  
      dbConn = requestDbConn.getSysDbConn();
      messageLogic = new T9MessageLogic();
      String data = messageLogic.getPanelInBox1(dbConn,request.getParameterMap(), toId, pageNo, pageSize);
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 标记为读
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String resetFlagAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    ArrayList<T9Message> messagelist = null;
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9ORM orm = new T9ORM();
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{" REMIND_FLAG IN(1,2) AND TO_ID=" + toId };
      List funcList = new ArrayList();
      T9MessageLogic sl = new T9MessageLogic();
      funcList.add("message");
      
      messagelist = (ArrayList<T9Message>) orm.loadListSingle(dbConn,  T9Message.class, filters);
      for (T9Message message : messagelist){
        message.setRemindFlag("0");
        orm.updateSingle(dbConn, message);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
  public String queryMessageList(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    String queryType = request.getParameter("queryType");
    boolean isQuery = false;
    String url = "";
      isQuery = true;
      url =  "/core/funcs/message/searchForOut.jsp?";
   
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9MessageLogic messageLogic = new T9MessageLogic();
      data = messageLogic.toSendBoxJson2(dbConn, request.getParameterMap(), toId,pageNo,pageSize,isQuery);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return url + "sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
  public String queryMessageList1(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9PageDataList data = null;
    String pageNoStr = request.getParameter("pageNo");
    String pageSizeStr = request.getParameter("pageSize");
    String queryType = request.getParameter("queryType");
    boolean isQuery = false;
    String url = "";
      isQuery = true;
      url =  "/core/funcs/message/searchForOut.jsp?";
   
    int sizeNo = 0;
    int pageNo = 0;
    int pageSize = 0;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      pageNo = Integer.parseInt(pageNoStr);
      pageSize = Integer.parseInt(pageSizeStr);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int toId = person.getSeqId();
      T9MessageLogic messageLogic = new T9MessageLogic();
      data = messageLogic.toSendBoxJson1(dbConn, request.getParameterMap(), toId,pageNo,pageSize,isQuery);
      sizeNo = data.getTotalRecord();
      request.setAttribute("contentList", data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return url + "sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
  }
  
  
  
  public String getMessageToId(HttpServletRequest request,
      HttpServletResponse response) throws Exception { 
    String bodyId = request.getParameter("bodyId");
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      dbConn = requestDbConn.getSysDbConn();
      T9MessageLogic sl = new T9MessageLogic();
      if(bodyId.indexOf(".")!=-1)
        bodyId=bodyId.substring(0, bodyId.indexOf("."));
      String data = sl.getToIdByBodyId(dbConn, Integer.parseInt(bodyId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getMessageBodyContent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    T9MessageLogic messageLogic = null;
    String contentDetail = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      StringBuffer sb = new StringBuffer();
      messageLogic = new T9MessageLogic();
      contentDetail = messageLogic.getContent(dbConn, seqId);
      contentDetail = T9Utility.encodeSpecial(contentDetail);
      sb.append("{");
      sb.append("content:\"" + T9Utility.encodeSpecial(contentDetail) + "\"");
      sb.append("}");
      data = sb.toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出短信内容！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);   
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getStauts(HttpServletRequest request,
      HttpServletResponse response)throws Exception{
    String bodyId = request.getParameter("bodyId");
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      dbConn = requestDbConn.getSysDbConn();
      T9MessageLogic sl = new T9MessageLogic();
      if(bodyId.indexOf(".")!=-1)
      bodyId=bodyId.substring(0, bodyId.indexOf("."));
      String data = sl.getStatusByBodyId(dbConn, Integer.parseInt(bodyId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getStatusByBodyId(HttpServletRequest request,
      HttpServletResponse response) throws Exception { 
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    String bodyIdStr = request.getParameter("bodyId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      ArrayList<T9Message> contentList = (ArrayList<T9Message>) orm.loadListSingle(dbConn, T9Message.class, new String[]{" BODY_SEQ_ID=" + bodyIdStr + " and (DELETE_FLAG = '0' or DELETE_FLAG = '1') "});
      request.setAttribute("contentList", contentList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/message/test.jsp";
  }
  
  
  /**
   * 数据结构：Map<T9SmsBody,List<T9sms>>
   * @param request
   * @param response
   * @return
   * @throws Exception
  */
   public String messageSentbox(HttpServletRequest request,
       HttpServletResponse response) throws Exception { 
     String pageNoStr = request.getParameter("pageNo");
     String pageSizeStr = request.getParameter("pageSize");
     int sizeNo = 0;
     int pageNo = 0;
     int pageSize = 0;
     Connection dbConn = null;
     try {
       pageNo = Integer.parseInt(pageNoStr);
       pageSize = Integer.parseInt(pageSizeStr);
       T9ORM orm = new T9ORM();
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
       dbConn = requestDbConn.getSysDbConn();
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       int userId = person.getSeqId();
       T9MessageLogic sl = new T9MessageLogic();
       StringBuffer data = sl.getPanelSentBox(dbConn, request.getParameterMap(),userId, pageNo, pageSize);
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_DATA,data.toString());
     }catch(Exception ex) {
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
       ex.printStackTrace();
     }
     return "/core/inc/rtjson.jsp";
   }
   
   /**
    * 
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String viewDetails(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
     Connection dbConn = null;
     String smsIds = request.getParameter("smsIds");
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       int toId = person.getSeqId();
       T9MessageLogic sl = new T9MessageLogic();
       List<Map<String, String>> l = sl.viewDetailsLogic(dbConn, smsIds,toId);
       request.setAttribute("pageData", l);
     } catch (Exception e) {
       throw e;
     }
     return "/core/frame/ispirit/nav.jsp";
   }
   
   public String acceptedSms(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
     Connection dbConn = null;
     T9PageDataList data = null;
     String pageNoStr = request.getParameter("pageNo");
     String pageSizeStr = request.getParameter("pageSize");
     String queryType = request.getParameter("queryType");
     boolean isQuery = false;
     String url = "";
     int sizeNo = 0;
     int pageNo = 0;
     int pageSize = 0;
     if("1".equals(queryType)){
       isQuery = true;
       url =  "/core/funcs/message/searchForIn.jsp?";
     }else{
       url =  "/core/funcs/message/accepte.jsp?";
     }
     try{
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       pageNo = Integer.parseInt(pageNoStr);
       pageSize = Integer.parseInt(pageSizeStr);
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       int toId = person.getSeqId();
       T9MessageLogic smsLogic = new T9MessageLogic();
       data = smsLogic.toInBoxJson(dbConn, request.getParameterMap(), toId,pageNo,pageSize,isQuery);
       sizeNo = data.getTotalRecord();
       request.setAttribute("contentList", data);
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
     }catch(Exception ex) {
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
       throw ex;
     }
     return url + "sizeNo="+sizeNo + "&pageNo=" + pageNo + "&pageSize=" + pageSize ;
   }
   
   /**
    * 精灵检查是否有新微讯
    **/
   public String get_msg(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
      Connection dbConn = null;
      try{
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String IM_FLAG=request.getParameter("IM_FLAG");
      String MSG_ID_STR ="";
      String FROM_UID_STR = "";
      Map<String,String> output =new HashMap();
      List<Map<String,String>> list = new ArrayList(); 
      T9MessageLogic  logic= new T9MessageLogic();
      if("1".equals(IM_FLAG)){
        String dateFiler = T9DBUtility.getDateFilter("T0.SEND_TIME",T9Utility.getCurDateTimeStr(), "<=");
        String dbDateFremind = T9DBUtility.getDateFilter("T1.REMIND_TIME", T9Utility.getCurDateTimeStr(), " <= ");
        String sql = "SELECT * FROM MESSAGE T1 ,MESSAGE_BODY T0 WHERE TO_ID='"+person.getSeqId()+"' AND REMIND_FLAG = '1' AND  T1.BODY_SEQ_ID= T0.SEQ_ID  " +
            "and DELETE_FLAG in (0, 2) " +  "AND " + dateFiler +
            " AND (T1.REMIND_TIME IS NULL OR " + dbDateFremind + ")";
         Statement stmt=null; 
         ResultSet rs = null;
         try {
           stmt = dbConn.createStatement();
           long start = System.currentTimeMillis();
           rs = stmt.executeQuery(sql);
           long end = System.currentTimeMillis();
           T9Out.debug("查询微讯耗时:" + (end - start) );
           while(rs.next()) {
              String MSG_ID=rs.getString("SEQ_ID");
              String FROM_UID=rs.getString("FROM_ID");
              String MSG_TYPE=rs.getString("MESSAGE_TYPE");
              String SEND_TIME=rs.getString("SEND_TIME");
              String CONTENT=rs.getString("CONTENT");
              output = new HashMap();
              MSG_ID_STR += MSG_ID+",";
              FROM_UID_STR += FROM_UID+",";
              output.put("from_uid", FROM_UID);
              SEND_TIME=SEND_TIME.substring(0, 19);
              output.put("time", SEND_TIME);
              if(T9Utility.isNullorEmpty(MSG_TYPE)){
                MSG_TYPE="0";
              }
              output.put("type",  "发自 网页微讯") ;
              output.put("content", T9Utility.encodeSpecial(CONTENT));
              output.put("from_name", logic.getUserName(dbConn, FROM_UID));
              list.add(output);
           }
         }catch(Exception ex) {
           throw ex;
         }finally{
           T9DBUtility.close(stmt, rs, null);
         }
       }
        // 提醒  new_sms_remind($LOGIN_UID, 0, 1);
         
         //已读
       MSG_ID_STR=MSG_ID_STR.trim();
       if(MSG_ID_STR.endsWith(",")){
         MSG_ID_STR=MSG_ID_STR.substring(0, MSG_ID_STR.length()-1);
       }
        String REMIND_FLAG ="0";
       if(!T9Utility.isNullorEmpty(MSG_ID_STR)){
         Statement stmt2 = null;
         try {
           String update = "update message set REMIND_FLAG='"+REMIND_FLAG+"' where SEQ_ID in ("+MSG_ID_STR+")"; 
           stmt2=dbConn.createStatement();
           long start = System.currentTimeMillis();
           stmt2.executeUpdate(update);
           long end = System.currentTimeMillis();
           T9Out.debug("更新微讯为已读耗时:" + (end - start) );
         }catch(Exception ex) {
           throw ex;
         }finally{
           T9DBUtility.close(stmt2, null, null);
         }
       }

      String returnStr="";
      for(int i=0;i<list.size();i++){
        Map<String,String> map = list.get(i);
        returnStr+=T9FOM.toJson(map).toString();
        returnStr+=",";
      }
    
      if(returnStr.endsWith(",")){
        returnStr=returnStr.substring(0, returnStr.length()-1);  
      }
        
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{data:["+returnStr+"]}");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      e.printStackTrace();
    }
    return "/core/inc/rtjson.jsp";
   }
   
   /**
    * 
    * @param request
    * @param response
    * @return
    * @throws Exception
    */
   public String remindCheck(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
     Connection conn = null;
     int data = 0;
     try{
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       conn = requestDbConn.getSysDbConn();
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       if(person != null){
         int personId = person.getSeqId();
         messageLogic = new T9MessageLogic();
         data = messageLogic.isRemind(conn, personId);
       }
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
       request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
       request.setAttribute(T9ActionKeys.RET_DATA, data+"");
     } catch (Exception e){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
       return "/core/inc/rtjson.jsp";
     }
     return "/core/inc/rtjson.jsp";
   }
   
}
