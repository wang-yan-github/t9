package t9.core.esb.frontend;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import sun.misc.BASE64Decoder;
import t9.core.autorun.T9AutoRun;
import t9.core.esb.common.T9DownloadTask;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.frontend.data.T9EsbDownTask;
import t9.core.esb.frontend.logic.T9EsbPollerLogic;
import t9.core.esb.frontend.oa.T9ESBMessageServiceCaller;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9EsbDownQuery extends T9AutoRun {
  public static final String PULL_URL = "/t9/t9/core/esb/server/act/T9EsbServerAct/query.act";
  
  public void doTask() {
    try {
      if (!T9EsbFrontend.isOnline()) {
        int code = T9EsbFrontend.login();
        if (code != 0) {
          return;
        }
      }
      T9EsbPollerLogic logic = new T9EsbPollerLogic();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      T9EsbUtil.debug("d-1");
      
      String tasks = "";
      String fileLength ="";
      String contentMd5 ="";
      String fileName ="";
      String fileType="";
      String fromId = "";
      String optGuid = "";
      String message = "";
      HttpGet request = new HttpGet(PULL_URL);
      long t1 = System.currentTimeMillis();
      HttpResponse response = T9EsbFrontend.getHc().execute(host, request);
      long t2 = System.currentTimeMillis();
      T9EsbUtil.debug("d-3" + ":耗时:" + (t2 - t1) / 1000 + "秒");
      InputStream is = response.getEntity().getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(is , "UTF-8"));
      String res = "";
      int readLen;
      char[] charArray = new char[50];
      long t3 = System.currentTimeMillis();
      long t5 = System.currentTimeMillis();
      T9EsbUtil.debug("d-4");
      while((readLen = reader.read(charArray)) > 0){ 
        String s = new String(charArray, 0, readLen);
        T9EsbUtil.debug(s);
        long t4 = System.currentTimeMillis();
        T9EsbUtil.debug("d-耗时：" + ((t4 - t3) / 1000) + "秒");
        res += s;
        t3 = System.currentTimeMillis();
      }
      long t6 = System.currentTimeMillis();
      T9EsbUtil.debug(((t6 - t5) / 1000) + "秒");
      T9EsbUtil.debug(res);
      
      T9EsbUtil.debug("d-5");
      if (!"".equals(res.trim())) {
        Map<String, String> map = T9FOM.json2Map(res.trim());
        T9EsbUtil.debug("d-6");
        if (map != null) {
           tasks = map.get("tasks");
           fileLength = map.get("fileLength");
           contentMd5 = map.get("contentMd5");
           fileName = map.get("fileName"); 
           fileType = map.get("fileType");
           fromId = map.get("fromId");
           optGuid = map.get("optGuid");
           message = map.get("message2");
           
          String msg = null;
          
          T9EsbUtil.debug("d-7");
          for (Header h : response.getAllHeaders()) {
            if ("SYS-MSG".equals(h.getName())) {
              msg = h.getValue();
              T9EsbUtil.debug(msg);
              break;
            }
          }
          T9EsbUtil.debug("d-8");
          if (!T9Utility.isNullorEmpty(msg)) {
            //更新状态
            Map<String, String> m = T9FOM.json2Map(msg);
            T9EsbUtil.debug("d-9:" + msg + ":"+System.currentTimeMillis());
            String type = m.get("type");
            
            if (T9Utility.isNullorEmpty(type)) {
              try{
                T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
                caller.updateState(m.get("guid"), Integer.parseInt(m.get("code")), m.get("to"));
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            } else {
              if ("1".equals(type)) {
                //执行sql
                String sql =  m.get("sql");
              } else if ("2".equals(type)) {
                //上传情况
                T9EsbFrontend.reportMessage("");
              }
            }
          }
          T9EsbUtil.debug("d-10");
        }
      }
      else {
        //T9EsbUtil.println("没有下载任务");
      }
      try {
        T9EsbUtil.debug("d-11");
        this.saveTasks(tasks, fileLength, contentMd5, fileName, fileType, fromId, optGuid, message);
        T9EsbUtil.debug("d-12");
      }catch (Exception ex) {
        ex.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  public void saveTasks(String guids , String fileLength ,
     String contentMd5 
     , String fileName
     , String fileType 
     , String fromId 
     , String optGuid 
     , String message) {
    String[] fileNames = fileName.split(",");
    String[] fromIds = fromId.split(",");
    String[] optGuids = optGuid.split(",");
    String[] gs = guids.split(",");
    String[] messages = message.split(",");
    File dir = new File(ClientPropertiesUtil.getCacheDir());
    if (!dir.exists()) {
      dir.mkdirs();
    }
    for (int i  = 0;i < gs.length ; i++) {
      String s = gs[i];
      if (s != null && !"".equals(s.trim())) {
        T9EsbPollerLogic logic = new T9EsbPollerLogic();
        if (!logic.hasEsbDownTask(s)) {
          File dir2 = new File(dir.getAbsolutePath() + File.separator + s);
          if (!dir2.exists()) {
            dir2.mkdirs();
          }
          String n = "";
          if (fileNames.length > i) {
            n = fileNames[i];
          }
          try {
            n = java.net.URLDecoder.decode(n, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          String fId = "";
          if (fromIds.length > i) {
            fId = fromIds[i];
          }
          String opt = "";
          if (optGuids.length > i) {
            opt = optGuids[i];
          }
          String mes = "";
          if (messages.length > i) {
            try {
              mes = messages[i];
              mes = toSrcStr(mes);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          String name = dir.getAbsolutePath() + File.separator + n;
          try{
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            caller.doMessage(fId, mes);
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          
          try {
            logic.addEsbDownTask(name, s, 1, fId , opt , mes);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        } else {
          logic.updateStatus(s, "1");
        }
      }
    }
  }
  public String getTasks(List<T9EsbDownTask> list  , String tasks) {
    for (T9EsbDownTask d : list) {
      if (!this.findId(tasks, d.getGuid())){
        if (T9Utility.isNullorEmpty(tasks) || tasks.endsWith(",")) {
          tasks += d.getGuid() + ",";
        } else {
          tasks += "," + d.getGuid();
        }
      }
    }
    return tasks;
  }
  public  T9EsbDownTask  getDownTask(List<T9EsbDownTask> list,String guid)  {
    for (T9EsbDownTask d : list) {
      if (d.getGuid() != null && d.getGuid().equals(guid)){
        return d;
      }
    }
    return null;
  }
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
   * 16进制字符串转成原来的字符串
   * @param srcStr
   * @return
   * @throws Exception
   */
  private static String toSrcStr(String srcStr) throws Exception {
    return new String(hexStr2Bytes0(srcStr), "UTF-8");
  }
  /**
   * 16进制字符串转化成二进制数组
   * @param srcStr
   * @return
   * @throws Exception
   */
  private static byte[] hexStr2Bytes0(String srcStr) throws Exception {
    int len = srcStr.length();
    int bufLen = len / 2;
    byte[] rtBuf = new byte[bufLen];
    for (int i = 0; i < bufLen; i++) {
      byte heigh = (byte)Integer.parseInt(srcStr.substring(i * 2, i * 2 + 1), 16);
      byte low = (byte)Integer.parseInt(srcStr.substring(i * 2 + 1, i * 2 + 2), 16);
      heigh = (byte)((byte)heigh << 4);
      rtBuf[i] = (byte)((byte)(heigh & 0xF0) + (byte)(low & 0x0F));
    }
    return rtBuf;
  }
  public static  void stopDownlondRun() {
    T9DownloadTask downLoadTask = T9EsbPoller.task; 
    if (downLoadTask != null) {
      try {
        downLoadTask.setStopDownloadFlag();
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
  public static  void runDownlond() {
    T9DownloadTask downLoadTask = T9EsbPoller.task; 
    if (downLoadTask != null && downLoadTask.getEsbPoller() != null) {
      try {
        downLoadTask.getEsbPoller().menuStartRun();
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
