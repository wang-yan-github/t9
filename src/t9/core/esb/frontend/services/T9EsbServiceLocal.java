package t9.core.esb.frontend.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.esb.common.T9DownloadTask;
import t9.core.esb.common.T9EsbTaskInfo;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.common.util.T9Serializer;
import t9.core.esb.frontend.T9EsbFrontend;
import t9.core.esb.frontend.T9EsbPoller;
import t9.core.esb.frontend.logic.T9EsbPollerLogic;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;

public class T9EsbServiceLocal {
  /**
   * 配置用户名密码主机地址
   * @param host
   * @param username
   * @param password
   * @param oaWebserviceUri
   * @return
   */
  public String config(String host, int port, String username, String password, String webserviceUri, String cacheDir , String isLocal) {
      ClientPropertiesUtil.updateProp("usercode", username);
      ClientPropertiesUtil.updateProp("port", String.valueOf(port));
      ClientPropertiesUtil.updateProp("password", password);
      ClientPropertiesUtil.updateProp("host", host);
      ClientPropertiesUtil.updateProp("webserviceUri", webserviceUri);
      ClientPropertiesUtil.updateProp("cacheDir", cacheDir);
      ClientPropertiesUtil.updateProp("isLocal", isLocal);
      ClientPropertiesUtil.store();
      
      ClientPropertiesUtil.refresh();
      return "{\"code\": \"0\", \"msg\": \"Configuration has been modified!\"}";
  }
  /**
   * 读配置文件登陆   * @return
   */
  public static String login() {
      ClientPropertiesUtil.refresh();
      String username = ClientPropertiesUtil.getProp("usercode");
      String host = ClientPropertiesUtil.getHost();
      int port = ClientPropertiesUtil.getHostPort();
      String password = ClientPropertiesUtil.getProp("password");
      
      if (T9Utility.isNullorEmpty(username) ||
          T9Utility.isNullorEmpty(host)) {
        return "{\"code\": \"-6\", \"msg\": \"Configuration information is not completely!\"}";
      }
      return T9EsbFrontend.login(host, port, username, password);
  }
  
  /**
   * 发送函数
   * @param filepath      待发送文件的绝对路径
   * @param toId          发送到的OA服务器id串，多个用逗号隔开
   * @return              返回发送的状态

   */
  public String send(final String filepath, final String toId , String optGuid , String message) {
      String str = T9EsbFrontend.send(filepath, toId , optGuid ,  message);
      return str;
  }
  public String resend(final String guid, final String toId) {
      String str = T9EsbFrontend.resend(guid, toId);
      return str;
  }
  public String redown(final String guid) {
    String str = T9EsbFrontend.redown(guid);
    T9EsbPollerLogic logic = new T9EsbPollerLogic();
    logic.updateStatus(guid, "1");
    return str;
}
  /**
   * 发送给所有用户
   * @param filepath
   * @return
   */
  public String broadcast(String filepath   , String optGuid , String message) {
      return T9EsbFrontend.broadcast(filepath , optGuid , message);
  }
  public String down(String guid){
      return T9EsbFrontend.down(guid);
  }
  public static String getDownloadScale(T9EsbTaskInfo info , long fileSize ) {
    String p = "等待下载";
    long size = PropertiesUtil.getDownloadPartSize();
    if (info == null) {
      return p;
    }
    
    String ss  = info.hasDone();

    int has = ss.split(",").length;
    long hasDown = has * size;
    if (fileSize != 0 && hasDown > fileSize) {
      hasDown = fileSize;
    }
    String hasDownStr = "";
    String fileSizeStr = "";
    
    if (fileSize > 1024) {
      fileSizeStr = (fileSize / 1024) + "KB";
      hasDownStr = (hasDown / 1024) + "KB";
    } else {
      fileSizeStr = fileSize  + "b";
      hasDownStr = hasDown + "b";
    }
    if (fileSize == 0 ) {
      if (hasDown > 1024) {
        hasDownStr = (hasDown / 1024) + "KB";
      }else {
        hasDownStr = hasDown + "b";
      }
      return "已下载：" + hasDownStr; 
    }
    p = hasDownStr + "/" + fileSizeStr;
    return p;
  }
  public String pause(String guid) {
    String str = T9EsbFrontend.pause(guid);
    return str;
  }
  public String sendMessage(String message , String toId) {
    String str = T9EsbFrontend.sendMessage(message, toId);
    return str;
  }
  public String reportMessage(String message ) {
    String str = T9EsbFrontend.reportMessage(message);
    return str;
  }
  public static String getDownloadScale(String guid) {
    String dirPath = ClientPropertiesUtil.getCacheDir();
    if (!dirPath.endsWith(File.separator)) {
      dirPath += File.separator;
    }
    T9DownloadTask task = T9EsbPoller.task;
    if (task != null && task.getGuid().equals(guid)) {
      T9EsbTaskInfo info =  task.getTaskInfo();
      return getDownloadScale(info , task.getLength() );
    } else {
      String path2 = dirPath + guid + File.separator + "taskinfo.esbx";
      File file = new File(path2);
      if (file.exists()) {
        List<String> rtList = new ArrayList();
        try {
          T9FileUtility.loadLine2Array(path2, rtList);
          T9EsbTaskInfo taskInfo = new T9EsbTaskInfo();
          long fileSize = 0 ;
          if (rtList.size() > 0 ) {
            String ss = rtList.get(0);
            String[] sss = ss.split(",");
            for (String s : sss) {
              if (T9Utility.isInteger(s)) {
                taskInfo.done(Integer.parseInt(s));
              }
            }
          }
          if (rtList.size() > 1) {
            String ss = rtList.get(1);
            if (T9Utility.isInteger(ss)) {
              fileSize = Long.parseLong(ss);
            }
          }
          return getDownloadScale(taskInfo , fileSize);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return "等待下载";
      } else {
        String path = dirPath + guid + File.separator + "taskinfo.esb";
        File file1 = new File(path);
        if (file1.exists()) {
          T9Serializer<T9EsbTaskInfo> s = new T9Serializer<T9EsbTaskInfo>();
          try {
            T9EsbTaskInfo taskInfo = s.deserialize(file1);
            return getDownloadScale(taskInfo , 0);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        return "等待下载";
      }
    }
  }
  public static boolean isDownloading(String guid) {
    T9DownloadTask task = T9EsbPoller.task; 
    if (guid != null && guid.equals(T9EsbPoller.nowDownTaskGuid)) {
      return true;
    }
    if (task != null && task.getGuid().equals(guid)) {
      return true;
    } else {
      return false;
    }
  }
  /**
   * 是否在线
   * @return
   */
  public boolean isOnline() {
    return T9EsbFrontend.isOnline();
  }
}
