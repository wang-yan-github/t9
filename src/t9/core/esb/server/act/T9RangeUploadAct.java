package t9.core.esb.server.act;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.common.T9EsbTaskInfo;
import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.common.util.T9Serializer;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.task.T9EsbServerTasksMgr;
import t9.core.esb.server.user.data.TdUser;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9RangeUploadAct {
  private static Logger log = Logger.getLogger("esb.t9.core.esb.server.act.T9RangeUploadAct");
  public static String UPLOAD_PATH = PropertiesUtil.getUploadPath();
  public final static Set<Integer> loc = new HashSet();
  public final static Set<Integer> loc2 = new HashSet();
  public final static Set<Integer> loc3 = new HashSet();
  
  public String initialize(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long begin = System.currentTimeMillis();
    String contentName = "";
    String fileSeqId = "";
    try {
      HttpSession session = request.getSession();
      String contentLength = request.getParameter("Content-length");
      long len = Long.parseLong(contentLength);
      contentName = request.getParameter("Content-name");
      String md5 = request.getParameter("MD5");
      String toId = request.getParameter("TO_ID");
      fileSeqId = request.getParameter("FileSeqId");
      String optGuid = T9Utility.null2Empty(request.getParameter("optGuid"));
      String message = T9Utility.null2Empty(request.getParameter("message"));
      
      String guid = request.getParameter("GUID");
      if (guid == null) {
        guid = UUID.randomUUID().toString();
      }
      
      TdUser user = (TdUser)session.getAttribute("ESB_LOGIN_USER");
      if (user == null) {
        log.error(request.getAttribute(T9ActionKeys.RET_MSRG));
        log.error("initialize - 服务器接收文件异常,异常信息:用户未登陆");
        return "";
      }
      
      int userId = user.getSeqId();
      synchronized(loc) {
        //开始上传的时候或者重新上传
        T9TaskInfo info = T9EsbServerTasksMgr.getUploadTask(guid);
        //续传
        String ss = "";
        if ( info != null  ) {
          ss = info.hasDone();
        } 
        Connection dbConn = null;
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9EsbServerLogic logic = new  T9EsbServerLogic();
        
        boolean flag = logic.hasTransferTask(dbConn, guid);
        if ( info == null ) {
          if (flag && logic.isTransferTaskField(dbConn, guid)) {
            response.setHeader("SYS-FIELD", "1");
            return "";
          } else {
            response.setHeader("SYS-FIELD", "0");
          }
          
          if (this.hasTransferSucess(dbConn, guid)) {
            response.setHeader("SYS-FIELD", "3");
            return "";
          }
          
          info = new T9TaskInfo();
          
          boolean isNewFile = true;
          if (flag) {
            String path = this.getFilePath(dbConn, guid);
            if (!T9Utility.isNullorEmpty(path)) {
              info.setFile(new File(path));
              isNewFile = false;
            } 
          } 
          if (isNewFile) {
            String dirParent = this.getDir();
            File dir = new File(dirParent + File.separator + guid);
            if (!dir.exists()) {
             dir.mkdirs();
            }
            File file = new File(dirParent + File.separator + guid + File.separator + contentName);
            info.setFile(file);
          }
          
          info.setFileLength(len);
          info.setGuid(guid);
          info.setMd5(md5);
          info.setFromId(userId);
          
          ByteBuffer bab = ByteBuffer.allocate((int)len);
          info.setBytes(bab);
          T9EsbServerTasksMgr.addUploadTask(guid, info);
        } 
        toId = logic.codeStr2IdStr(dbConn, toId);
        info.setToId(toId);
        if (!flag){
          logic.addTransferTask(dbConn, guid, userId, info.getFile().getAbsolutePath(), info.getContent(), "0", info.getToId() , optGuid , message , len);
        } 
        
        response.addHeader("Content-GUID", guid);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        PrintWriter pw = response.getWriter();
        pw.write(ss);
        pw.flush();
        pw.close();
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      log.error("initialize - 服务器开始接收文件" + contentName + "异常,异常信息:" + ex.getMessage());
      throw ex;
    }finally {
      long end = System.currentTimeMillis();
      int lastTime = (int)(end - begin);
      //System.out.println("File " + fileSeqId + " init use " + lastTime + " ms");
    }
    return "";
  }
  public String getDir() {
    Date date = new Date();
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    String[] time2 = sdf2.format(date).split("-");
    String path = UPLOAD_PATH + File.separator + time2[0]  + File.separator+ time2[1] + File.separator + time2[2];
    File file1 = new File(path);
    if (!file1.exists()) {
      file1.mkdirs();
    }
    return path;
  }
  public String getFilePath(Connection conn , String guid) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select FILE_PATH  " +
          " from ESB_TRANSFER" +
          " where GUID =? ";
      ps = conn.prepareStatement(sql);
      ps.setString(1, guid);
      rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getString("FILE_PATH");
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, log);
    }
    return "";
  }
  public String transfer(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long begin = System.currentTimeMillis();
    String fileName = "";
    String fileInfo = "";
    Connection dbConn = null;
    try {
      int contentLength = Integer.parseInt(request.getHeader("Con-length"));
      String guid = request.getHeader("GUID");
      T9TaskInfo info = T9EsbServerTasksMgr.getUploadTask(guid);
      String md5 = request.getHeader("MD5");
      String start = request.getHeader("START");
      fileInfo = request.getHeader("FileInfo");
      String no = request.getHeader("NO");
      
      if (info != null) {
        InputStream is = request.getInputStream();
        ByteArrayBuffer bab = new ByteArrayBuffer(contentLength);
        byte[] tmp = new byte[1024];
        for (int i = 0; (i = is.read(tmp)) > 0;) {
          bab.append(tmp, 0, i);
        }
        
        if (T9DigestUtility.isMatch(bab.toByteArray(), md5)) {
          synchronized(loc3) {
            info.getDateCache().put(start, bab.toByteArray());
            if (T9Utility.isInteger(no)) {
              //完成一个线程
              info.done(Integer.parseInt(no));
            }
          }
        } else {
          response.setStatus(300);
        }
        bab.clear();
      }
      else {
        //文件上传超时
        T9EsbServerLogic logic = new  T9EsbServerLogic();
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        boolean flag = logic.isTransferTaskField(dbConn, guid);
        if (flag) {
          response.setStatus(301);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
    }catch(Exception ex) {
      ex.printStackTrace();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      log.error("transfer - 服务器接收文件" + fileName + "异常,异常信息:" + ex.getMessage());
      throw ex;
    }finally {
      long end = System.currentTimeMillis();
      int lastTime = (int)(end - begin);
      //System.out.println(fileInfo + " use " + lastTime + " ms");
    }
    return "";
  }
  public String complete(HttpServletRequest request, HttpServletResponse response) throws Exception {
    long begin = System.currentTimeMillis();
    String fileSeqId = "";
    Connection dbConn = null;
    String fileName= "";
    try {
      String guid = request.getParameter("GUID");
      fileSeqId = request.getParameter("FileSeqId");
      String md5 = "";
      synchronized(loc2) {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        
        T9TaskInfo info = T9EsbServerTasksMgr.getUploadTask(guid);
        
        if (info != null) {
            File file = info.getFile();
            md5 = info.getMd5();
            String status = this.getTransferStatus(dbConn, guid);
            writeByte(info.getDateCache(), info.getBytes());
            if (info != null 
                && info.getBytes() != null 
                && info.getBytes().array() != null
                && info.getBytes().array().length > 0) {
              //未完成时，写文件
              if (!"2".equals(status)
                  && !"3".equals(status)) {
                writeFile(file, info.getBytes().array());
              }
            }
           // T9EsbServerLogic logic2 = new  T9EsbServerLogic();
            //boolean flag = status != -10;
                //logic2.hasTransferTask(dbConn, guid);
            //logic2.isTransferTaskField(dbConn, guid);
            //存在，且失败
            if (!"-10".equals(status)
                && "4".equals(status)) {
              response.setHeader("SYS-FIELD", "1");
              response.setStatus(400);
            } else {
              if ((info.getBytes() != null 
                  && T9DigestUtility.isMatch(info.getBytes().array(), md5))
                  || T9DigestUtility.isFileMatch(file.getAbsolutePath(), md5)) {
                response.setStatus(200);
                T9EsbServerLogic logic = new T9EsbServerLogic();
                logic.uploadComplete(dbConn, info);
                info.getBytes().clear();
                info.setBytes(null);
                T9EsbServerTasksMgr.removeUploadTask(guid);
              } else {
                response.setStatus(300);
              }
            }
        } else {
          response.setStatus(301);
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      log.error("complete - 文件" + fileName + "完整性校验异常,异常信息:" + ex.getMessage());
      throw ex;
    }finally {
      long end = System.currentTimeMillis();
      int lastTime = (int)(end - begin);
      //System.out.println("File " + fileSeqId + " terminate use " + lastTime + " ms");
    }
    return "";
  }
  public boolean hasTransferSucess(Connection conn , String guid) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select 1 " +
          " from ESB_TRANSFER" +
          " where GUID =? and (status = '2' or status = '3')";
      ps = conn.prepareStatement(sql);
      ps.setString(1, guid);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, log);
    }
    return false;
  }
  public String getTransferStatus(Connection conn , String guid) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select status " +
          " from ESB_TRANSFER" +
          " where GUID =? ";
      ps = conn.prepareStatement(sql);
      ps.setString(1, guid);
      rs = ps.executeQuery();
      if (rs.next()) {
        return T9Utility.null2Empty(rs.getString("status"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, null, log);
    }
    return "-10";
  }
  /**
   * 写byte，加上同步锁很重要
   * @param start
   * @param bytes
   * @throws IOException
   * @throws InterruptedException
   */
 static private void writeByte(Map<String , byte[]> dataCache ,  ByteBuffer bb) throws IOException, InterruptedException {
    Set<String> keys = dataCache.keySet();
    Set<Integer> treeKey = new TreeSet();
    for (String str : keys) {
      Integer it = Integer.parseInt(str);
      treeKey.add(it);
    }
    for (Integer it : treeKey) {
      byte[] data = dataCache.get(it + "");
      bb.put(data);
    }
   //dataCache.clear();
  }
  
  /**
   * 写文件，加上同步锁很重要
   * @param start
   * @param bytes
   * @throws IOException
   * @throws InterruptedException
   */
  synchronized static private void writeFile(File file ,  byte[] bts) throws IOException, InterruptedException {
    FileOutputStream out  = null;
    try {
      out = new FileOutputStream(file);
      out.write(bts);
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (out != null)  {
        out.flush();
        out.close();
      }
    }
  }
  public T9EsbTaskInfo getTaskInfo(File file) {
    File dir = file.getParentFile();
    T9EsbTaskInfo taskInfo = null;
    for (File f: dir.listFiles()) {
      String name = f.getName();
      if (!T9Utility.isNullorEmpty(name)) {
        if (name.endsWith(".esb")) {
          T9Serializer<T9EsbTaskInfo> s = new T9Serializer<T9EsbTaskInfo>();
          try {
            taskInfo = s.deserialize(f);
          } catch (Exception e) {
            e.printStackTrace();
          }
          break;
        }
      }
    }
    if (taskInfo == null) {
      taskInfo = new T9EsbTaskInfo();
    }
    return taskInfo;
  }
  
  private void saveTaskInfo(File file ,T9EsbTaskInfo taskInfo) throws IOException {
    File dir = file.getParentFile();
    T9Serializer<T9EsbTaskInfo> s = new T9Serializer<T9EsbTaskInfo>();
    String path = dir.getAbsolutePath() + File.separator + "taskinfo.esb";
    s.serialize(new File(path), taskInfo);
  }
}