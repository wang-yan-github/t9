package t9.core.esb.server.act;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



import org.apache.http.util.ByteArrayBuffer;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.common.util.T9EsbServerUtil;
import t9.core.esb.server.data.T9EsbTransfer;
import t9.core.esb.server.data.T9RandomFileWrap;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.task.T9EsbServerTasksMgr;
import t9.core.esb.server.user.data.TdUser;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;

public class T9RangeDownloadAct {
  private static Logger log = Logger.getLogger("esb.t9.core.esb.server.act.T9RangeDownloadAct");
  public final static byte[] loc = new byte[1];
  public final static byte[] loc2 = new byte[1];
  public final static byte[] loc3 = new byte[1];
  public String initialize(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String fileName = "";
    try {
      synchronized(loc) {
        HttpSession session = request.getSession();
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String guid = request.getParameter("GUID");
        if (T9Utility.isNullorEmpty(guid)) {
          return "";
        }
        TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
        if (user == null) {
          log.error("initialize - 服务器发送文件异常,异常信息:用户未登陆");
          return "";
        }
        
        T9TaskInfo info = T9EsbServerTasksMgr.getDownloadTask(guid, user.getSeqId());
        T9EsbServerLogic logic = new T9EsbServerLogic();
        boolean hasField = logic.hasStatus(dbConn, guid, user.getSeqId(), T9EsbServerLogic.TRANSFER_STATUS_FAILED);
        
        String message = "";
        if (info == null) {
          if (hasField) {
            response.setHeader("SYS-FIELD", "1");
            return "";
          } else {
            response.setHeader("SYS-FIELD", "0");
          } 
          T9EsbTransfer t = logic.queryDownloadInfo(dbConn, guid);
          if (t == null) {
            response.setHeader("SYS-FIELD", "1");
            return "";
          }
          
          //更改状态-下载中          logic.setStatus(dbConn, guid, user.getSeqId(), T9EsbServerLogic.TRANSFER_STATUS_DOWNLOADING);
          //更新下载开始时间          logic.setDownloadCreate(dbConn, guid, user.getSeqId());
          File f = new File(t.getFilePath());
          if (!f.exists()) {
            return "";
          }
          
          info = new T9TaskInfo();
          info.setFile(f);
          info.setType(t.getType());
          info.setGuid(guid);
          info.setFileLength(f.length());
          info.setFromId(t.getFromId());
          info.setFromCode(logic.seqId2UserCode(dbConn, t.getFromId()));
          info.setToId(String.valueOf(user.getSeqId()));
          //info.setMd5(T9DigestUtility.md5File(t.getFilePath()));
          T9EsbServerTasksMgr.addDownloadTask(guid, user.getSeqId(), info);
          
          response.addHeader("OptGuid", t.getOptGuid());
          message = T9Utility.null2Empty(t.getMessage());
        }
        else {
          response.addHeader("OptGuid", "");
        }
        
        T9RandomFileWrap raf = T9EsbServerTasksMgr.getDownloadFile(guid);
        if (raf == null) {
           raf = new T9RandomFileWrap(info.getFile().getAbsolutePath());
          T9EsbServerTasksMgr.addDownloadFile(guid, raf);
        }
        String fileName2 = java.net.URLEncoder.encode(info.getFile().getName(),"UTF-8") ;
        response.addHeader("File-Length", String.valueOf(info.getFile().length()));
        response.addHeader("Content-MD5", raf.getMd5());
        response.addHeader("File-Name", fileName2);
        response.addHeader("File-Type", info.getType());
        response.addHeader("From-ID", info.getFromCode());
        PrintWriter pw = response.getWriter();
        pw.write(message);
        pw.flush();
        pw.close();
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      log.error("initialize - 服务器开始发送文件" + fileName + "异常,异常信息:" + ex.getMessage());
      throw ex;
    }
    return "";
  }
  
  public String transfer(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String fileName = "";
    Connection dbConn = null;
    try {
      HttpSession session =request.getSession();
      String url = request.getParameter("URL");
      String guid = request.getParameter("GUID");
      
      //int userId = Integer.parseInt(request.getParameter("userId"));
       TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      String range = request.getParameter("RANGE");
      if (range == null || (url == null && guid == null)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "参数错误");
        request.setAttribute(T9ActionKeys.RET_DATA, "");
        return "";
      }
      
      T9EsbServerLogic logic = new T9EsbServerLogic();
      File f = null;
      if (guid == null) {
        String webrootDir = request.getSession().getServletContext().getRealPath(File.separator);
        f = new File(webrootDir + url.replaceFirst("/t9", ""));
        if (!f.exists()) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "请求文件不存在");
          request.setAttribute(T9ActionKeys.RET_DATA, "");
          return "";
        }
      }
      else {
        T9TaskInfo info = T9EsbServerTasksMgr.getDownloadTask(guid, user.getSeqId());
        if (info == null) {
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          boolean hasField = logic.hasStatus(dbConn, guid, user.getSeqId(), T9EsbServerLogic.TRANSFER_STATUS_FAILED);
          if (hasField) {
            response.setHeader("SYS-FIELD", "1");
          }
          return "";
        }
        else {
          f = info.getFile();
        }
      }
      
      
      long start = 0;
      long end = 0;
      String[] ranges = range.split("-");
      try {
        if (ranges.length > 1) {
          start = Long.parseLong(ranges[0]);
          end = Long.parseLong(ranges[1]);
        }
      } catch (NumberFormatException e) {
        start = 0;
        end = 0;
      }
      
      try {
        T9TaskInfo info = T9EsbServerTasksMgr.getDownloadTask(guid, user.getSeqId());
        if (info != null) {
           if (ranges.length > 1) {
             String c = info.getContent();
             long cInt = 0 ;
             long cInt2 = 0 ;
             if (!T9Utility.isNullorEmpty(c) && T9Utility.isInteger(c))  {
               cInt = Long.parseLong(c);
             }
             if (!T9Utility.isNullorEmpty(ranges[0]) && T9Utility.isInteger(ranges[0]))  {
               cInt2 = Long.parseLong(ranges[0]);
               if (cInt2 > cInt) {
                 info.setContent(ranges[0]);
               }
             }
           }
        }
      }catch(Exception ex) {
        ex.printStackTrace();
      }
      
      if (end > 0 && start < end) {
        if (end > f.length()) {
          end = f.length();
        }
        OutputStream out = response.getOutputStream();
        int len = (int) (end - start);
        byte[] bytes = new byte[len];
        
        synchronized(loc3) {
          T9RandomFileWrap raf = T9EsbServerTasksMgr.getDownloadFile(guid);
          if (raf == null) {
             raf = new T9RandomFileWrap(f.getAbsolutePath());
            T9EsbServerTasksMgr.addDownloadFile(guid, raf);
          }
          raf.readFull(bytes, (int)start);
          String md5 = T9DigestUtility.md5Hex(bytes);
          response.setHeader("Data-Length", String.valueOf(bytes.length));
          response.setHeader("Content-MD5", md5);
        }
        out.write(bytes);
        out.flush();
        out.close();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      String msg = "transfer - 服务器发送文件" + fileName + "异常,异常信息:" + ex.getMessage();
      log.error(msg);
      ex.printStackTrace();
      return "";
    }
    return "";
  }
  
  public String complete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String fileName = "";
    try {
      synchronized(loc2) {
        T9EsbServerLogic logic = new T9EsbServerLogic();
        HttpSession session =request.getSession();
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        String guid = request.getParameter("GUID");
        TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
        String result = request.getParameter("RESULT");
        
        T9TaskInfo info = T9EsbServerTasksMgr.getDownloadTask(guid, user.getSeqId());
        String fromId = "";
        File file = null;
        int fromIdInt = 0 ;
        
        //此任务是否被超时停止,如果已经超时停止，则阻止其“下载成功”保证此任务此前发送出去的信息是正确的。
        boolean hasField = logic.hasStatus(dbConn, guid, user.getSeqId(), T9EsbServerLogic.TRANSFER_STATUS_FAILED);
        if (info != null) {
          file = info.getFile();
          fromId = String.valueOf(info.getFromId());
          fromIdInt = info.getFromId() ;
        } else {
          if (hasField) {
            response.setHeader("SYS-FIELD", "1");
          }
        }
        if (!hasField) {
          if ("ok".equalsIgnoreCase(result)) {
            logic.recvCompleted(dbConn, guid,  user.getSeqId(), fromId,  file);
            T9EsbServerTasksMgr.removeDownloadTask(guid);
          } else {
            //T9EsbServerLogic.setDownloadFailedMessage(dbConn, guid, "MD5校验失败", String.valueOf(user.getSeqId()), fromIdInt , false);
          }
        } 
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      String msg = "complete - 文件" + fileName + "完整性校验异常,异常信息:" + ex.getMessage();
      log.error(msg);
      throw ex;
    }
    return "";
  }
}
