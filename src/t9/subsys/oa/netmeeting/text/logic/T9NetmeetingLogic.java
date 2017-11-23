package t9.subsys.oa.netmeeting.text.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.netmeeting.text.data.T9Netmeeting;

public class T9NetmeetingLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.netmeeting.text.logic.T9NetmeetingLogic.java");


  /**
   * 新建文本会议
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void setNewNetmeetingValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person,String contexPathAll) throws Exception {
    T9ORM orm = new T9ORM();
    String toIdStr = fileForm.getParameter("toId");
    String subject = fileForm.getParameter("subject");
    String beginTimeStr = fileForm.getParameter("beginTime");
    String smsRemind = fileForm.getParameter("smsRemind");

    try{
      T9Netmeeting netmeeting = new T9Netmeeting();
      netmeeting.setToId(toIdStr);
      netmeeting.setSubject(subject);
      Date beginTime = T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", beginTimeStr);
      netmeeting.setBeginTime(beginTime);
      Date date = new Date();
      if(beginTime.before(date)){
        netmeeting.setStop("2");
      }
      else{
        netmeeting.setStop("0");
      }
      netmeeting.setCreateUserId(String.valueOf(person.getSeqId()));
      netmeeting.setAddTime(T9Utility.parseTimeStamp());
      
      orm.saveSingle(dbConn, netmeeting);
      
      File msg = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator   + getMaxSeqId(dbConn) + ".msg");
      msg.createNewFile();
      File user = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + getMaxSeqId(dbConn) + ".user");
      user.createNewFile();
      
      String remindUrl = "/subsys/oa/netmeeting/text/index.jsp";
      String smsContent = "请出席文本会议！议题：" + subject;
      // 短信提醒
      if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
        this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), toIdStr, "3", remindUrl, new Date());
      }
    } catch (Exception e) {
      throw e;
    }
  }
  

  /**
   * 管理已创建的文本会议 通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getNetmeetingJsonLogic(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " select n1.SEQ_ID, p1.USER_NAME, n1.TO_ID, n1.TO_ID, n1.SUBJECT, n1.BEGIN_TIME, n1.STOP "
                 + " from NETMEETING n1 "
                 + " join person p1 on n1.CREATE_USER_ID = p1.SEQ_ID "
                 + " ORDER BY n1.BEGIN_TIME desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
    if (T9Utility.isNullorEmpty(userIdStr)) {
      userIdStr = "-1";
    }
    if (userIdStr.endsWith(",")) {
      userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
    }
    String result = "";
    String sql = " select USER_NAME from PERSON where SEQ_ID IN (" + userIdStr + ")";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String toId = rs.getString(1);
        if (!"".equals(result)) {
          result += ",";
        }
        result += toId;
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }

  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public void doStop(Connection conn, String seqId, String typeStr, T9Person person, String contexPathAll) throws Exception {
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "-1";
    }
    int type = Integer.parseInt(typeStr);
    try {
      T9ORM orm = new T9ORM();
      T9Netmeeting netmeeting = (T9Netmeeting) orm.loadObjSingle(conn, T9Netmeeting.class, Integer.parseInt(seqId));
      netmeeting.setCreateUserId(String.valueOf(person.getSeqId()));
      switch(type){
        case 0 : netmeeting.setStop("2");break;
        case 2 : netmeeting.setStop("3");    
                 byte[] msg = ("\n&nbsp;[系统消息] - 会议召集人已经结束会议 ").getBytes();
                 storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg", msg, 0, msg.length, true);    
                 File stp = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".stp");
                 stp.createNewFile();
                 break;
        case 3 : netmeeting.setStop("2");
                 byte[] msg1 = ("\n&nbsp;[系统消息] - 会议继续进行").getBytes();
                 storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg", msg1, 0, msg1.length, true);    
                 File stp1 = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".stp");
                 stp1.delete();
                 break;
      }
      netmeeting.setAddTime(T9Utility.parseTimeStamp());
      orm.updateSingle(conn, netmeeting);
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 修改文本会议
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void updateNetmeetingValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
    T9ORM orm = new T9ORM();
    String seqId = fileForm.getParameter("seqId");
    String toIdStr = fileForm.getParameter("toId");
    String smsRemind = fileForm.getParameter("smsRemind");

    try{
      T9Netmeeting netmeeting = (T9Netmeeting) orm.loadObjSingle(dbConn, T9Netmeeting.class, Integer.parseInt(seqId));
      netmeeting.setToId(toIdStr);
      netmeeting.setCreateUserId(String.valueOf(person.getSeqId()));
      netmeeting.setAddTime(T9Utility.parseTimeStamp());
      
      orm.updateSingle(dbConn, netmeeting);
      
      String remindUrl = "/subsys/oa/netmeeting/text/index.jsp";
      String smsContent = "请出席文本会议！议题：" + netmeeting.getSubject();
      // 短信提醒
      if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
        this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), toIdStr, "3", remindUrl, new Date());
      }
    } catch (Exception e) {
      throw e;
    }
  }
  
  /**
   * 删除文本会议
   * 
   * @param dbConn
   * @param fileForm
   * @param person
   * @throws Exception
   */
  public void deleteNetmeetingLogic(Connection dbConn, String seqId, String contexPathAll) throws Exception {
    try{
      T9ORM orm = new T9ORM();
      T9Netmeeting netmeeting = (T9Netmeeting) orm.loadObjSingle(dbConn, T9Netmeeting.class, Integer.parseInt(seqId));
      orm.deleteSingle(dbConn, netmeeting);
      File msg = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg");
      msg.delete();
      File user = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user");
      user.delete();
    } catch (Exception e) {
      throw e;
    }
  } 
  
  /**
   * 获取单位员工用户名称
   * 
   * @param conn
   * @param userIdStr
   * @return
   * @throws Exception
   */
  public void getNetmeetingState(Connection conn) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      String[] filters = {" STOP = 0 and " + T9DBUtility.getDateFilter("BEGIN_TIME", T9Utility.parseTimeStamp().toString().substring(0, 19), "<=")};
      List<T9Netmeeting> list = orm.loadListSingle(conn, T9Netmeeting.class, filters);
    
      for(T9Netmeeting netmeeting : list){
        netmeeting.setStop("2");
        orm.updateSingle(conn, netmeeting);
      }
    } catch (Exception e) {
      throw e;
    } 
  }
  
  /**
   * 文本会议列表    通用列表
   * 
   * @param dbConn
   * @param request
   * @param person
   * @return
   * @throws Exception
   */
  public String getNetmeetingLogic(Connection dbConn, Map request, T9Person person) throws Exception {
    try {
      String sql = " select n1.SEQ_ID, n1.CREATE_USER_ID, n1.SUBJECT, n1.TO_ID, n1.BEGIN_TIME "
                 + " from NETMEETING n1 "
                 + " where (" + T9DBUtility.findInSet(person.getSeqId()+"", "n1.TO_ID")
                 + " or n1.CREATE_USER_ID =" + person.getSeqId() + ")"
                 + " and n1.stop = 2 "
                 + " ORDER BY n1.BEGIN_TIME desc ";
      T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
      return pageDataList.toJson();
    } catch (Exception e) {
      throw e;
    }
  }
  
  public String getMsg(String seqId, String contexPathAll, int count, T9Person person) throws Exception{
    
    //每秒更新状态
    BufferedReader readerSet = new BufferedReader(new FileReader(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user"));
    String dataSet = "";
    String tempSet = "";
    int readCountSet = 0;
    while((tempSet = readerSet.readLine()) != null){
      if(readCountSet == 0)
        dataSet = dataSet + tempSet;
      else
        dataSet = dataSet + "\n" + tempSet;
      if(tempSet.equals(person.getUserName())){
        readerSet.readLine();
        dataSet = dataSet + "\n" + T9Utility.getCurDateTimeStr();
      }
      readCountSet++;
    }
    readerSet.close();
    byte[] user = dataSet.getBytes();
    storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user", user, 0, user.length, false);  
    
    //每秒获取聊天室聊天信息
    BufferedReader reader = new BufferedReader(new FileReader(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg"));
    String data = "";
    String temp = "";
    int readCount = 1;
    while((temp = reader.readLine()) != null){
      if(readCount > count){
        if(temp.indexOf("@+#") > 0){
          String usernameAll = temp.substring(0, temp.indexOf("@+#"));
          String[] username = usernameAll.split(",");
          String nowUsername = person.getUserName();
          if(username[0].equals(nowUsername) || username[1].equals(nowUsername))
            data += temp.substring(temp.indexOf("@+#") + 3, temp.length()) + "<BR>";
        }
        else
          data += temp + "<BR>";
      }
      readCount++;
    }
    reader.close();
    
    //获取聊天室在线人数 60秒没有跟新状态的视为不在线
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    BufferedReader reader1 = new BufferedReader(new FileReader(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user"));
    String data1 = "";
    String temp1 = "";
    String temp2 = "";
    int readCount1 = 0;
    reader1.readLine();
    while((temp1 = reader1.readLine()) != null && (temp2 = reader1.readLine()) != null ){
      Date d1 = new Date();
      Date d2 = df.parse(temp2);
      long diff = d1.getTime() - d2.getTime();
      if(diff/1000 <= 60){
        if(readCount1 == 0)
          data1 = data1 + "\"" + temp1 + "\"";
        else
          data1 = data1 + ",\"" + temp1 + "\"";
        readCount1++;
      }
    }
    reader1.close();    
    
    File stp = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".stp");
    if(stp.isFile()){
      return "{data:\""+ data +"\",count:\"" + (readCount - 1) + "\",user:[" + data1 + "],stop:\"stop\"}";
    }
    return "{data:\""+ data +"\",count:\"" + (readCount - 1) + "\",user:[" + data1 + "]}";
  }
  
  //发送信息
  public void setMsg(Map map, String contexPathAll, T9Person person) throws Exception{
    String seqId = ((String[])map.get("seqId"))[0];
    File stp = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".stp");
    if(!stp.isFile()){
    
      String message = ((String[])map.get("message"))[0];
      String color = ((String[])map.get("color"))[0];
      String quiet = ((String[])map.get("quiet"))[0];
      String usernameToSay = "";
      String toSay = "";
      String quietString = "";
      String showUsername = "";
      if(map.get("usernameToSay") != null && !T9Utility.isNullorEmpty(((String[])map.get("usernameToSay"))[0])){
        usernameToSay = ((String[])map.get("usernameToSay"))[0];
        toSay = "&nbsp;对&nbsp;<b><a href=javascript:sayTo('" + usernameToSay + "');>" + usernameToSay + "</a></b>";
        
        if(quiet.equals("true")){
          showUsername = usernameToSay + "," + person.getUserName() + "@+#";
          quietString = "悄悄地";
        }
      }
      byte[] srcBuf = ("\n" + showUsername + "&nbsp;<b><a href=javascript:sayTo('" + person.getUserName() + "');>" + person.getUserName() + "</a></b>" + toSay + "&nbsp;" + quietString + "说：&nbsp;<font color=" + color + ">" + message + "</font>&nbsp;<font color=#888888>[" + T9Utility.getCurDateTimeStr() + "]</font>  ").getBytes();
      storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg", srcBuf, 0, srcBuf.length, true);
    }
  }
  
  //加入聊天室
  public void joinNeeting(String seqId, String contexPathAll, T9Person person) throws Exception{
    boolean isHave = false;
    BufferedReader reader = new BufferedReader(new FileReader(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user"));
    String data = "";
    String temp = "";
    int readCount = 0;
    while((temp = reader.readLine()) != null){
      if(readCount == 0)
        data = data + temp;
      else
        data = data + "\n" + temp;
      if(temp.equals(person.getUserName())){
        reader.readLine();
        data = data + "\n" + T9Utility.getCurDateTimeStr();
        isHave = true;
      }
      readCount++;
    }
    reader.close();
    if(isHave){
      byte[] user = data.getBytes();
      storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user", user, 0, user.length, false);  
    }
    else{
      byte[] user = ("\n" + person.getUserName() + "\n" + T9Utility.getCurDateTimeStr()).getBytes();
      storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user", user, 0, user.length, true);     
    }
    byte[] msg = ("\n&nbsp;[系统消息] - " + person.getUserName() + "进入会议室  ").getBytes();
    storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg", msg, 0, msg.length, true);
  }
  
  //停止会议
  public void stopMeeting(String seqId, String contexPathAll, T9Person person, Connection dbConn) throws Exception{
    byte[] msg = ("\n&nbsp;[系统消息] - 会议召集人已经结束会议 ").getBytes();
    storBytes2File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".msg", msg, 0, msg.length, true);    
    
    File stp = new File(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".stp");
    stp.createNewFile();
    
    try {
      T9ORM orm = new T9ORM();
      T9Netmeeting netmeeting = (T9Netmeeting) orm.loadObjSingle(dbConn, T9Netmeeting.class, Integer.parseInt(seqId));
      netmeeting.setCreateUserId(String.valueOf(person.getSeqId()));
      netmeeting.setStop("3");
      netmeeting.setAddTime(T9Utility.parseTimeStamp());
      orm.updateSingle(dbConn, netmeeting);
    } catch (Exception e) {
      throw e;
    }
  }
  
  
  public String joinNeetingCount(String seqId, String contexPathAll) throws Exception{
    
    //获取聊天室在线人数 60秒没有跟新状态的视为不在线
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    BufferedReader reader1 = new BufferedReader(new FileReader(contexPathAll + "subsys"+ File.separator  +"oa"+ File.separator  +"netmeeting"+ File.separator  +"text"+ File.separator  +"msg"+ File.separator  +"" + seqId + ".user"));
    int count = 0;
    String temp1 = "";
    String temp2 = "";
    reader1.readLine();
    while((temp1 = reader1.readLine()) != null && (temp2 = reader1.readLine()) != null ){
      Date d1 = new Date();
      Date d2 = df.parse(temp2);
      long diff = d1.getTime() - d2.getTime();
      if(diff/1000 <= 60){
        count++;
      }
    }
    reader1.close();    
    
    return "{count:" + count + "}";
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
  
  /**
   *  把字节数组保存到文件
   * @param fileName
   * @param srcBuf
   * @throws Exception
   */
  public static void storBytes2File(String fileName,
      byte[] srcBuf, int offset, int length, boolean endWrite) throws Exception {
    OutputStream outs = null;
    try {
      File outFile = new File(fileName);
      File outDir = outFile.getParentFile();
      if (!outDir.exists()) {
        outDir.mkdirs();
      }
      if (!outFile.exists()) {
        outFile.createNewFile();
      }
      if (!outFile.canWrite()) {
        outFile.setWritable(true);
      }
      outs = new FileOutputStream(outFile,endWrite);
      
      outs.write(srcBuf, offset, length);
    }catch(Exception ex) {
      throw ex;
    }finally {
      try {
        if (outs != null) {
          outs.close();
        }
      }catch(Exception ex) {
        //System.out.println(ex.getMessage());
      }      
    }
  }
  
  /**
   * 获取最大的SeqId值

   * 
   * @param dbConn
   * @return
   */
  public int getMaxSeqId(Connection dbConn) {
    String sql = "select SEQ_ID from NETMEETING where SEQ_ID=(select MAX(SEQ_ID) from NETMEETING )";
    int seqId = 0;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        seqId = rs.getInt("SEQ_ID");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return seqId;
  }
}
