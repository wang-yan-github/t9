package t9.core.esb.server.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Encoder;
import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.server.data.T9EsbSysMsg;
import t9.core.esb.server.data.T9EsbTransfer;
import t9.core.esb.server.data.T9EsbTransferStatus;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.task.T9EsbServerTasksMgr;
import t9.core.esb.server.user.data.TdUser;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.form.T9FOM;

public class T9EsbServerAct {
  private static final char[] HEX_ARRAY = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
  public static final String SESSION_DOWNLOAD_MAP = "Download-map";
  public static final int DOWNLOADS_LIMIT = 20;
  public  boolean findId(String str, String id) {
    if(str == null || id == null || "".equals(str) || "".equals(id)){
      return false;
    }
    String[] aStr = str.split(",");
    for(String tmp : aStr){
      if(tmp.equals(id)){
        return true;
      }
    }
    return false;
  }
  /**
   * 查询有无该应用的文件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String query(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user == null) {
        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      int userId = user.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbServerLogic logic = new T9EsbServerLogic();
      List<T9EsbTransfer> status = logic.queryDownloadTask(dbConn, userId);
      PrintWriter pw = response.getWriter();
      
      Map<String, String> result = new HashMap<String, String>();
      
      String tasks = "";
      
      String fileLength ="";
      String contentMd5 ="";
      String fileName ="";
      String fileType="";
      String fromId = "";
      String optGuid = "";
      String message = "";
      String message2 = "";
      
      for (T9EsbTransfer s : status) {
        if (s == null) {
          continue;
        }
        if (this.findId(tasks, s.getGuid())) {
          continue;
        }
        File file = new File(s.getFilePath());
        String fileName2 = java.net.URLEncoder.encode(file.getName(),"UTF-8") ;
        fileLength += String.valueOf(file.length()) + ",";
        contentMd5 += T9DigestUtility.md5File(file.getAbsolutePath()) + ",";
        fileName += T9Utility.null2Empty(fileName2) + ",";
        fromId += logic.seqId2UserCode(dbConn, s.getFromId()) + ",";
        optGuid += T9Utility.null2Empty(s.getOptGuid()) + ",";
        message2 += toHexStr(T9Utility.null2Empty(s.getMessage())) + ",";
        BASE64Encoder base64 = new BASE64Encoder();
        message += base64.encode(T9Utility.null2Empty(s.getMessage()).getBytes("UTF-8")) + ",";
        tasks += s.getGuid();
        tasks += ",";
      }
      List<T9EsbSysMsg> list = logic.querySysMsg(dbConn, userId);
      for (T9EsbSysMsg m : list) {
        response.setHeader("SYS-MSG",m.getContent());
        response.setHeader("SYS-MSG2",toHexStr(T9Utility.null2Empty(m.getContent())));
        logic.setSysMsgStatus(dbConn, m.getSeqId(), "1");
        break;
        //暂时一次执行一个命令
      }
      result.put("tasks", tasks);
      result.put("fileLength", fileLength);
      result.put("contentMd5", contentMd5);
      result.put("fileName", fileName);
      result.put("fileType", fileType);
      result.put("fromId", fromId);
      result.put("optGuid", optGuid);
      result.put("message", message);
      result.put("message2", message2);
      pw.write(T9FOM.toJson(result).toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      throw e;
    }
    return "";
  }
  /**
   * 查询 有无下载信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryDownTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user == null) {
        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      int userId = user.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbServerLogic logic = new T9EsbServerLogic();
      List<T9EsbTransfer> status = logic.queryDownloadTask(dbConn, userId);
      PrintWriter pw = response.getWriter();
      
      Map<String, String> result = new HashMap<String, String>();
      
      String tasks = "";
      
      String fileLength ="";
      String contentMd5 ="";
      String fileName ="";
      String fileType="";
      String fromId = "";
      String optGuid = "";
      String message = "";
      String message2 = "";
      
      for (T9EsbTransfer s : status) {
        if (s == null) {
          continue;
        }
        if (this.findId(tasks, s.getGuid())) {
          continue;
        }
        File file = new File(s.getFilePath());
        String fileName2 = java.net.URLEncoder.encode(file.getName(),"UTF-8") ;
        fileLength += String.valueOf(file.length()) + ",";
        contentMd5 += T9DigestUtility.md5File(file.getAbsolutePath()) + ",";
        fileName += T9Utility.null2Empty(fileName2) + ",";
        fromId += logic.seqId2UserCode(dbConn, s.getFromId()) + ",";
        optGuid += T9Utility.null2Empty(s.getOptGuid()) + ",";
        message2 += toHexStr(T9Utility.null2Empty(s.getMessage())) + ",";
        BASE64Encoder base64 = new BASE64Encoder();
        message += base64.encode(T9Utility.null2Empty(s.getMessage()).getBytes("UTF-8")) + ",";
        tasks += s.getGuid();
        tasks += ",";
      }
      result.put("tasks", tasks);
      result.put("fileLength", fileLength);
      result.put("contentMd5", contentMd5);
      result.put("fileName", fileName);
      result.put("fileType", fileType);
      result.put("fromId", fromId);
      result.put("optGuid", optGuid);
      result.put("message", message);
      result.put("message2", message2);
      pw.write(T9FOM.toJson(result).toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      throw e;
    }
    return "";
  }
  /**
   * 查询有无任务
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryMsg(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user == null) {
        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      int userId = user.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbServerLogic logic = new T9EsbServerLogic();
      List<T9EsbSysMsg> list = logic.querySysMsg(dbConn, userId);
      for (T9EsbSysMsg m : list) {
        response.setHeader("SYS-MSG", m.getContent());
        logic.setSysMsgStatus(dbConn, m.getSeqId(), "1");
        break;
        //暂时一次执行一个命令
      }
      PrintWriter pw = response.getWriter();
      pw.write("");
      pw.flush();
      pw.close();
    } catch (Exception e) {
      throw e;
    }
    return "";
  }
  
  public String isOnline(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      String res = "";
      if (user != null) {
        res = "{\"user\": \" " + user.getSeqId() + " \"}";
      }
     // System.out.println(res);
      response.getWriter().write(res);
    } catch (Exception e) {
      throw e;
    }
    return "";
  }
  public String reSend (HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      T9EsbServerLogic logic = new T9EsbServerLogic();
      if (user == null) {
//        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String guid = request.getParameter("guid");
      String toId = request.getParameter("toId");
      
      if (logic.isTransferTaskField(dbConn, guid)) {
        logic.setTransStatus(dbConn, guid, "1");
      } else {
        logic.setStatus(dbConn, guid, toId, T9EsbServerLogic.TRANSFER_STATUS_READY);
      }
      PrintWriter pw = response.getWriter();
      Map<String , String> result = new HashMap();
      result.put("rtState", "0");
      pw.write(T9FOM.toJson(result).toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
  public String reDown (HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      T9EsbServerLogic logic = new T9EsbServerLogic();
      if (user == null) {
//        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String guid = request.getParameter("guid");
      int toId = user.getSeqId();
          
      logic.setStatus2(dbConn, guid, toId, T9EsbServerLogic.TRANSFER_STATUS_READY);
      
      PrintWriter pw = response.getWriter();
      Map<String , String> result = new HashMap();
      result.put("rtState", "0");
      pw.write(T9FOM.toJson(result).toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
  public String pause(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      
      if (user == null) {
        T9EsbUtil.println("T9EsbServerAct: 登陆失败 客户端地址 - " + request.getRemoteAddr());
        return "";
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String guid = request.getParameter("guid");
      if (T9Utility.isNullorEmpty(guid)) {
        return "";
      }
      int toId = user.getSeqId();
      T9EsbServerTasksMgr.removeDownloadTask(guid, toId);
      String msg = "文件" + guid + "的下载任务已被挂起";
      T9EsbUtil.println(msg);
      T9EsbServerLogic.setDownloadFailedMessage(dbConn, guid, msg, String.valueOf(toId) , 0 , false);
      
      PrintWriter pw = response.getWriter();
      Map<String , String> result = new HashMap();
      result.put("rtState", "0");
      pw.write(T9FOM.toJson(result).toString());
      pw.flush();
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
  public String sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user != null) {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String toId = T9Utility.null2Empty(request.getParameter("toId"));
        String message =  T9Utility.null2Empty(request.getParameter("message"));
        
        T9EsbServerLogic logic = new T9EsbServerLogic();
        String[] toIds = toId.split(",");
        for (String id : toIds) {
          logic.addSysmsg(dbConn, "", message, id);
        }
        PrintWriter pw = response.getWriter();
        Map<String , String> result = new HashMap();
        result.put("rtState", "0");
        pw.write(T9FOM.toJson(result).toString());
        pw.flush();
        pw.close();
        return "";
      } else {
        PrintWriter pw = response.getWriter();
        Map<String , String> result = new HashMap();
        result.put("rtState", "1");
        pw.write(T9FOM.toJson(result).toString());
        pw.flush();
        pw.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
  public String reportMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    HttpSession session = request.getSession();
    Connection dbConn = null;
    try {
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      
      if (user == null) {
        PrintWriter pw = response.getWriter();
        Map<String , String> result = new HashMap();
        result.put("rtState", "1");
        pw.write(T9FOM.toJson(result).toString());
        pw.flush();
        pw.close();
        return "";
      } else {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String message = T9Utility.null2Empty(request.getParameter("message"));
        T9EsbServerLogic logic = new T9EsbServerLogic();

        logic.addClientMsg(dbConn,  user.getSeqId() , message);
        PrintWriter pw = response.getWriter();
        Map<String , String> result = new HashMap();
        result.put("rtState", "0");
        pw.write(T9FOM.toJson(result).toString());
        pw.flush();
        pw.close();
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return "";
  }
  /**
   * 转化成16进制字符串
   * @param srcStr
   * @return
   * @throws Exception
   */
  private static String toHexStr(String srcStr) throws Exception {
    return bytes2HexStr0(srcStr.getBytes("UTF-8"));
  }
  
  /**
   * 二进制数组转化成16进制字符串
   * @param buf
   * @return
   * @throws Exception
   */
  private static String bytes2HexStr0(byte[] buf) throws Exception {
    if (buf == null || buf.length == 0) {
      return "";
    }
    StringBuffer rtBuf = new StringBuffer("");
    for (byte b : buf) {
      byte high = (byte)((b >> 4) & 0x0F);
      byte low = (byte)(b & 0x0F);
      rtBuf.append(HEX_ARRAY[high]);
      rtBuf.append(HEX_ARRAY[low]);
    }
    return rtBuf.toString().toLowerCase();
  }
}
