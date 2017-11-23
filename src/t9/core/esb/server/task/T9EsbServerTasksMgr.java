package t9.core.esb.server.task;

import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import t9.core.autorun.T9AutoRun;
import t9.core.esb.common.data.T9TaskInfo;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.server.act.T9RangeDownloadAct;
import t9.core.esb.server.act.T9RangeUploadAct;
import t9.core.esb.server.data.T9RandomFileWrap;
import t9.core.esb.server.logic.T9EsbServerLogic;

public class T9EsbServerTasksMgr extends T9AutoRun {
  public static final int MAX_UPLOAD_TASKS = 1000;
  public static final int MAX_DOWNLOAD_TASKS = 1000;
  private static Map<String, T9TaskInfo> uploadTasks = new ConcurrentHashMap<String, T9TaskInfo>();
  private static Map<String, T9RandomFileWrap> downFiles = new ConcurrentHashMap<String, T9RandomFileWrap>();
  private static Map<String, Map<Integer, T9TaskInfo>> downloadTasks = new ConcurrentHashMap<String, Map<Integer, T9TaskInfo>>();
  
  public static boolean addUploadTask(String guid, T9TaskInfo task) {
    if (uploadTasks.entrySet().size() < T9EsbServerTasksMgr.MAX_UPLOAD_TASKS) {
      uploadTasks.put(guid, task);
      return true;
    }
    return false;
  }
  public static boolean isDownloading(String guid) {
    return downloadTasks.get(guid) != null;
  }
  /**
   * 添加下载任务(暂时未做数量限制)
   * @param guid
   * @param toId
   * @param task
   * @return
   */
  public static boolean addDownloadTask(String guid, int toId, T9TaskInfo task) {
    Map<Integer, T9TaskInfo> m = downloadTasks.get(guid);
    if (m == null) {
      m = new ConcurrentHashMap<Integer, T9TaskInfo>();
      downloadTasks.put(guid, m);
    }
    m.put(toId, task);
    task.setStartTime(new Date());
    
    return true;
  }
  
  public static void removeUploadTask(String guid) {
    uploadTasks.remove(guid);
  }
  public static void removeDownFile(String guid)  {
    synchronized(T9RangeDownloadAct.loc3) {
      downFiles.remove(guid);
    }
  }
  public static void removeDownloadTask(String guid) {
    downloadTasks.remove(guid);
  }
  public static void removeDownloadTask(String guid , int toId) {
    Map<Integer, T9TaskInfo> m  = downloadTasks.get(guid);
    if (m != null) {
      synchronized(T9RangeDownloadAct.loc2) {
        m.remove(toId);
      }
    }
    if (m.keySet().size() == 0) {
      removeDownloadTask(guid);
      removeDownFile(guid);
    }
  }
  
  public static T9RandomFileWrap getDownloadFile(String guid) {
    T9RandomFileWrap file   = downFiles.get(guid);
    return file;
  }
  public static void addDownloadFile(String guid , T9RandomFileWrap file) {
    downFiles.put(guid, file);
  }
  public static T9TaskInfo getDownloadTask(String guid, int toId) {
    Map<Integer, T9TaskInfo> m  = downloadTasks.get(guid);
    if (m == null) {
      return null;
    }
    return m.get(toId);
  }
  
  public static T9TaskInfo getUploadTask(String guid) {
    return uploadTasks.get(guid);
  }

  public static Map<String, T9TaskInfo> getUploadTasks() {
    return uploadTasks;
  }
  public static void setUploadTasks(Map<String, T9TaskInfo> uploadTasks) {
    T9EsbServerTasksMgr.uploadTasks = uploadTasks;
  }
  public static Map<String, Map<Integer, T9TaskInfo>> getDownloadTasks() {
    return downloadTasks;
  }
  public static void setDownloadTasks(
      Map<String, Map<Integer, T9TaskInfo>> downloadTasks) {
    T9EsbServerTasksMgr.downloadTasks = downloadTasks;
  }
  public void doTask() throws Exception {
    T9EsbUtil.println("服务器端超时检查");
    checkUpload(uploadTasks, PropertiesUtil.getMaxUploadTime());
    checkDownload(downloadTasks, PropertiesUtil.getMaxDownloadTime());
    T9EsbUtil.println("上传任务数" + uploadTasks.entrySet().size());
    T9EsbUtil.println("下载任务数" + uploadTasks.entrySet().size());
  }
  
  private void checkUpload(Map<String, T9TaskInfo> tasks, long time) throws Exception {
    for (Entry<String, T9TaskInfo> e : tasks.entrySet()) {
      if (e.getValue() != null) {
        if (new Date().getTime() - e.getValue().getStartTime().getTime() > time) {
          long l = new Date().getTime() - e.getValue().getStartTime().getTime();
          String msg = "文件传送超时:文件<"+ e.getValue().getGuid() +">上传已用时：" + l / 1000 + "秒,发送超时";
          T9EsbUtil.println(msg);
          synchronized(T9RangeUploadAct.loc2) {
            tasks.remove(e.getKey());
          }
          Connection dbConn = requestDbConn.getSysDbConn();
          T9EsbServerLogic.setUploadFailedMessage(dbConn, e.getValue().getGuid(), msg);
          String msg2 = "文件传送超时:文件<"+  e.getValue().getGuid() +">上传任务已移出";
          T9EsbUtil.println(msg2);
        }
      }
      else {
        synchronized(T9RangeUploadAct.loc2) {
          tasks.remove(e.getKey());
        }
      }
    }
  }
  
  private void checkDownload(Map<String, Map<Integer, T9TaskInfo>> tasks, long time) throws Exception {
    for (Entry<String, Map<Integer, T9TaskInfo>> en : tasks.entrySet()) {
      if (en.getValue() != null) {
        for (Entry<Integer, T9TaskInfo> e : en.getValue().entrySet()) {
          if (new Date().getTime() - e.getValue().getStartTime().getTime() > time) {
            long l = new Date().getTime() - e.getValue().getStartTime().getTime();
            T9TaskInfo ti = e.getValue();
            String msg = "文件传送超时:文件<"+ ti.getGuid() +">下载已用时" + l / 1000 + "秒,下载超时,下载方：" + ti.getToId() + ",发送方："+  ti.getFromId();
            T9EsbUtil.println(msg);
            synchronized(T9RangeDownloadAct.loc2) {
              en.getValue().remove(e.getKey());
              if (en.getValue().entrySet().size() == 0) {
                tasks.remove(en.getKey());
                removeDownFile(en.getKey());
              } 
            }
            Connection dbConn = requestDbConn.getSysDbConn();
            T9EsbServerLogic.setDownloadFailedMessage(dbConn, e.getValue().getGuid(), msg, e.getValue().getToId() , e.getValue().getFromId() , true);
            String msg2 = "文件传送超时:文件<"+ ti.getGuid() +">下载任务已移出";
            T9EsbUtil.println(msg2);
          }
        }
      }
      else {
        synchronized(T9RangeDownloadAct.loc2) {
          tasks.remove(en.getKey());
        }
        removeDownFile(en.getKey());
      }
    }
  }
  public static void main(String[] argc) {
  }
}
