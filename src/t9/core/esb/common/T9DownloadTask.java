package t9.core.esb.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.common.util.T9HttpClientUtil;
import t9.core.esb.common.util.T9Serializer;
import t9.core.esb.frontend.T9EsbPoller;
import t9.core.esb.frontend.logic.T9EsbPollerLogic;
import t9.core.esb.frontend.oa.T9ESBMessageServiceCaller;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;
import t9.core.util.file.T9FileUtility;

public class T9DownloadTask {
  public static final Integer loc1 = new Integer(1);
  
  public static String LOGIN_URL = "/t9/t9/core/esb/server/system/act/SystemLoginAct/doLogin.act";
  public static final String DOWNLOAD_TRANSFER_URL = "/t9/t9/core/esb/server/act/T9RangeDownloadAct/transfer.act";
  public static final String DOWNLOAD_INITIALIZE_URL = "/t9/t9/core/esb/server/act/T9RangeDownloadAct/initialize.act";
  public static final String DOWNLOAD_COMPLETE_URL = "/t9/t9/core/esb/server/act/T9RangeDownloadAct/complete.act";
  private File file;
  private File dir;
  private HttpHost host;
  private String guid;
  private String type;
  private String md5;
  private String fromId;
  private long length;
  public String getGuid() {
    return guid;
  }
  public void setGuid(String guid) {
    this.guid = guid;
  }

  private String optGuid;
  private String message;
  private int completeResumeCnt;
  private T9EsbTaskInfo taskInfo;
  private int count = 0;
  private T9EsbPoller esbPoller;
  
  public T9DownloadTask(HttpHost host, File dir, String guid ,String optGuid, String message, T9EsbPoller t9EsbPoller) {
    this.host = host;
    this.dir = dir;
    this.guid = guid;
    this.optGuid = optGuid;
    this.message = message;
    this.esbPoller = t9EsbPoller;
  }
  public T9EsbPoller getEsbPoller() {
    return esbPoller;
  }
  public T9EsbTaskInfo getTaskInfo() {
    return taskInfo;
  }

  public void setTaskInfo(T9EsbTaskInfo taskInfo) {
    this.taskInfo = taskInfo;
  }

  public long getLength() {
    return length;
  }
  public void setLength(long length) {
    this.length = length;
  }
  public void initTaskInfo()  {
    T9EsbUtil.debug("d-initTaskInfo-根据文件初始化下载任务信息" );
    if (file != null) {
      File dir = file.getParentFile();
      boolean flag = false;
      for (File f: dir.listFiles()) {
        String name = f.getName();
        if (!T9Utility.isNullorEmpty(name)) {
          if (name.endsWith(".esbx")) {
            flag = true;
            taskInfo = new T9EsbTaskInfo();
            List<String> rtList = new ArrayList();
            try {
              T9FileUtility.loadLine2Array(f.getAbsolutePath(), rtList);
              if (rtList.size() > 0) {
                String ss = rtList.get(0);
                String[] sss = ss.split(",");
                for (String s : sss) {
                  if (T9Utility.isInteger(s)) {
                    taskInfo.done(Integer.parseInt(s));
                  }
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
              T9EsbUtil.debug("d-initTaskInfo-根据"+name+"文件，初始化时报错：" + e.getMessage());
            }
            T9EsbUtil.debug("d-initTaskInfo-根据"+name+"文件，完成下载任务信息的初始化" );
            break;
          }
        }
      }
      if (!flag) {
        for (File fi: dir.listFiles()) {
          String name1 = fi.getName();
          if (!T9Utility.isNullorEmpty(name1)) {
            if (name1.endsWith(".esb")) {
              T9Serializer<T9EsbTaskInfo> s = new T9Serializer<T9EsbTaskInfo>();
              try {
                taskInfo = s.deserialize(fi);
              } catch (Exception e) {
                T9EsbUtil.debug("d-initTaskInfo-根据"+name1+"文件，初始化时报错：" + e.getMessage());
              }
              T9EsbUtil.debug("d-initTaskInfo-根据"+name1+"文件，完成下载任务信息的初始化" );
              break;
            }
          }
        }
      }
    }
    if (taskInfo == null) {
      T9EsbUtil.debug("d-initTaskInfo-新建任务信息" );
      taskInfo = new T9EsbTaskInfo();
    }
  }

  public synchronized void saveTaskInfo() throws Exception {
    T9EsbUtil.debug("d-saveTaskInfo-任务："+ this.guid +"保存下载状态到文件");
    File dir = file.getParentFile();
    String path = dir.getAbsolutePath() + File.separator + "taskinfo.esbx";
    String ss = taskInfo.hasDone();
    String size = String.valueOf(this.length);
    List<String> rtList = new ArrayList();
    rtList.add(ss);
    rtList.add(size);
    T9FileUtility.storeArray2Line(path,rtList);
  }
  
  public boolean initialize(DefaultHttpClient hc) throws Exception {
    HttpResponse response = null;
    HttpPost post = null;
    try {
      post = new HttpPost(DOWNLOAD_INITIALIZE_URL);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("GUID", guid));
      post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      T9EsbUtil.debug("d-initialize-发送初始化请求");
      long start = System.currentTimeMillis();
      response = hc.execute(host, post);
      long end = System.currentTimeMillis();
      T9EsbUtil.debug("d-initialize-发送初始化请求成功，耗时：" + (end - start));
      String optGuid = "";
      String message = "";
      boolean flag = false;
      if (response == null) {
        T9EsbUtil.debug("d-initialize-发送初始化请求返回为空");
      } else {
        Header h0 = response.getFirstHeader("File-Length");
        Header h1 = response.getFirstHeader("Content-MD5");
        Header h2 = response.getFirstHeader("File-Name");
        Header h3 = response.getFirstHeader("File-Type");
        Header h4 = response.getFirstHeader("From-ID");
        Header h5 = response.getFirstHeader("SYS-FIELD");
        Header h6 = response.getFirstHeader("OptGuid");
        
        if (response.getEntity() != null && response.getEntity().getContent() != null) {
          HttpEntity et = response.getEntity();
          if (et != null) {
            InputStream is = et.getContent();
            String res = "";
            byte[] b = new byte[1024];
            for (int i = 0; (i = is.read(b)) > 0;) {
              res += new String(b,"UTF-8");
            }
            if (!"".equals(res.trim())) {
              message = res.trim();
            }
            T9EsbUtil.debug("d-initialize-读取到下载文件的message:" + message);
            response.getEntity().getContent().close();
          }
        }
        
        if (h5 != null) {
          String value = h5.getValue();
          if ("1".equals(value)) {
            //超时
            T9EsbPollerLogic logic = new T9EsbPollerLogic();
            logic.updateStatus(guid, "-3");
            T9EsbUtil.debug("d-initialize-读取到下载任务已经超时");
            return false;
          }
        }
        if (h0 != null && h1 != null && h2 != null && h3 != null && h4 != null ) {
          flag = true;
        }
        if (h0 != null) {
          length = Long.parseLong(h0.getValue());
        }
        if (h1 != null) {
          this.md5 = h1.getValue();
        }
        String name = "";
        if (h2 != null) {
          if (!this.dir.exists()) {
            this.dir.mkdirs();
          }
          T9EsbUtil.debug("d-initialize-下载文件的保存路径：" + this.dir.getAbsolutePath() + File.separator + this.guid);
          File dir = new File(this.dir.getAbsolutePath() + File.separator + this.guid);
          dir.mkdirs();
          String n = h2.getValue();
          n = java.net.URLDecoder.decode(n, "UTF-8");
          
          name = dir.getAbsolutePath() + File.separator + n;
          this.file = new File(name);
        }
        if (h3 != null) {
          this.type = h3.getValue();
        }
        if (h4 != null) {
          this.fromId = h4.getValue();
        }
        if (h6 != null) {
          optGuid = h6.getValue();
        }
        if (h2 != null  && h4 != null ) {
          T9EsbPollerLogic logic = new T9EsbPollerLogic();
          T9EsbUtil.debug("d-initialize-务理取得的下载任务信息" );
          if (!logic.hasEsbDownTask(guid)) {
            T9EsbUtil.debug("d-initialize-下载任务信息还未保存" );
            this.optGuid = optGuid;
            this.message = message;
            logic.addEsbDownTask(name, guid, 1, fromId , optGuid , message);
            T9EsbUtil.debug("d-initialize-下载任务信息已保存" );
          } else {
            T9EsbUtil.debug("d-initialize-下载任务信息已存在" );
            logic.updateStatus(guid, "1");
          }
        }
      }
      initTaskInfo();
      return flag;
    } catch (Exception e) {
      e.printStackTrace();
      T9EsbUtil.debug("d-initialize-任务初始化时报错：" + e.getMessage());
      count++;
      return false;
    } finally {
      T9HttpClientUtil.releaseConnection(response);
    }
  }
  public boolean transfer(HttpClient hc) throws Exception {
    long size = PropertiesUtil.getDownloadPartSize();
    Collection<Runnable> tasks = new ArrayList<Runnable>();
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(100000);
    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);
    long fileSize = this.length;
    int i = 0;
    long s = 0;
    while (fileSize >  s) {
      if (s + size > fileSize) {
        size = fileSize - s;
      }
      if (taskInfo.isDone(i)) {
        s += size;
        i++;
        continue;
      }
      //T9EsbUtil.debug("transfer-" + s);
      MultiThreadDownloadRequest multiThreadRequest = new MultiThreadDownloadRequest(pool, hc, host, taskInfo, i++, guid, s, size, file , this );
      tasks.add(multiThreadRequest);
      pool.execute(multiThreadRequest);
      s += size;
    }
    long lastTime = System.currentTimeMillis();
    String hasDown = taskInfo.hasDone();
    T9EsbUtil.debug("d-transfer-任务开始");
    while (pool.getQueue().size() > 0) {
      if (this.stopDownloadFlag) {
        T9EsbUtil.debug("d-transfer-任务被手工停止");
        stopDownload(((ExecutorService)pool));
        return false;
      }
      long currTime = System.currentTimeMillis();
      long costTime = currTime - lastTime;
      if (costTime > 2 * T9Const.DT_MINIT) {
        lastTime = System.currentTimeMillis();
        String hasDown2 = taskInfo.hasDone();
        if (hasDown.equals(hasDown2)) {
          T9EsbUtil.debug("d-transfer-任务未增长被停止:");
          stopDownload(((ExecutorService)pool));
          return false;
        } else {
          hasDown = hasDown2;
        }
      }
      Thread.sleep(1000);
    }
    pool.shutdown();
    while (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
      //等待所有线程结束    }
    return true;
  }
  
  public int complete(HttpClient hc) {
    HttpResponse response = null;
    try {
      String result = null;
      if (T9DigestUtility.isFileMatch(this.file.getAbsolutePath(), this.md5)) {
        T9EsbUtil.debug("d-complete-任务："+ this.guid +"通过md5校验:md5:" + this.md5);
        result = "ok";
        if ("1".equals(this.type)) {
          String dirPath = T9SysProps.getWebInfPath() + File.separator + "config" + File.separator;
          File dir = new File(dirPath);
          if (!dir.exists()) {
            dir.mkdirs();
          }
          
          File old = new File(dirPath + "esbconfig.properties");
          if (old.delete()) {
            this.file.renameTo(old);
            ClientPropertiesUtil.refresh();
          }
        }
        HttpPost post = new HttpPost(DOWNLOAD_COMPLETE_URL);
        
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("RESULT", result));
        nvps.add(new BasicNameValuePair("GUID", this.guid));
        post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        T9EsbUtil.debug("d-complete-发送完成命令");
        long start = System.currentTimeMillis();
        response = hc.execute(host, post);
        long end = System.currentTimeMillis();
        T9EsbUtil.debug("d-complete-发送完成命令:耗时:" + (end - start));
        if (response == null) {
        }else {
          Header h5 = response.getFirstHeader("SYS-FIELD");
          if (h5 != null) {
            String value = h5.getValue();
            if ("1".equals(value)) {
              T9EsbUtil.debug("d-complete-已经超时了");
              //超时
              T9EsbPollerLogic logic = new T9EsbPollerLogic();
              logic.updateStatus(guid, "-3");
              saveTaskInfo();
              return -3;
            }
          }
        }
        if ("0".equals(this.type)) {
          try {
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            T9EsbUtil.debug("d-complete-调用完成接口");
            if (this.file.exists()) {
              T9EsbUtil.debug("d-complete-文件存在");
            } else {
              T9EsbUtil.debug("d-complete-文件不存在");
            }
            caller.recvMessage(this.file.getAbsolutePath(), this.guid, this.fromId , this.optGuid , this.message );
            if (this.file.exists()) {
              T9EsbUtil.debug("d-complete-文件存在");
            } else {
              T9EsbUtil.debug("d-complete-文件不存在");
            }
          }catch(Exception ex) {
            ex.printStackTrace();
          }
        }
        response.getEntity().getContent().close();
        T9EsbPollerLogic logic = new T9EsbPollerLogic();
        T9EsbUtil.debug("d-complete-完成写入");
        logic.updateStatus(guid, "0");
        return 0;
      } else {
        T9EsbUtil.debug("d-complete-任务："+ this.guid +"，未通过md5效验");
        T9EsbPollerLogic logic = new T9EsbPollerLogic();
        if (!logic.hasEsbDownTaskField(guid)) {
          T9EsbUtil.debug("d-complete-将下载失败状态写入数据库");
          logic.updateStatus(guid, "-1");
        } 
      }
      HttpPost post = new HttpPost(DOWNLOAD_COMPLETE_URL);
      
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("RESULT", result));
      nvps.add(new BasicNameValuePair("GUID", this.guid));
      post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      T9EsbUtil.debug("d-complete-任务："+ this.guid +"md5未通过时,告诉服务器");
      long start = System.currentTimeMillis();
      response = hc.execute(host, post);
      long end = System.currentTimeMillis();
      T9EsbUtil.debug("d-complete-任务："+ this.guid +"md5未通过时,告诉服务器,耗时：" + (end - start) );
      if (response == null) {
        T9EsbUtil.debug("d-complete-任务："+ this.guid +"md5未通过时,告诉服务器 ,返回为空");
      } else {
        Header h5 = response.getFirstHeader("SYS-FIELD");
        if (h5 != null) {
          String value = h5.getValue();
          if ("1".equals(value)) {
            T9EsbUtil.debug("d-complete-任务："+ this.guid +"md5未通过时,告诉服务器 ,已经超时了");
            T9EsbPollerLogic logic = new T9EsbPollerLogic();
            logic.updateStatus(guid, "-3");
          }
        }
      }
      response.getEntity().getContent().close();
      saveTaskInfo();
      return -1;
    } catch (Exception e) {
      e.printStackTrace();
      T9HttpClientUtil.releaseConnection(response);
      if (completeResumeCnt++ < 10) {
        return complete(hc);
      }
      else {
        try {
          saveTaskInfo();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        T9EsbPollerLogic logic = new T9EsbPollerLogic();
        logic.updateStatus( guid, "-1");
        return -3;
      }
    }
    finally {
      T9HttpClientUtil.releaseConnection(response);
    }
  }
  public void stopDownload( ExecutorService pool) {
    // TODO Auto-generated method stub
    String status = "-1";
    if (this.stopDownloadFlag) {
      status = "-3";
    } 
    T9EsbUtil.debug("stopDownload-" + status + ":" +  System.currentTimeMillis());
    T9EsbPollerLogic logic = new T9EsbPollerLogic();
    logic.updateStatus(this.guid, status);
    try {
      this.saveTaskInfo();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    pool.shutdownNow();
  }
  private boolean stopDownloadFlag = false;
  public void setStopDownloadFlag() {
    // TODO Auto-generated method stub
    T9EsbUtil.debug("setStopDownloadFlag-" + System.currentTimeMillis());
    stopDownloadFlag = true;
  }
}

class MultiThreadDownloadRequest implements Runnable {
  public static final int E_NO_RESPONSE = -1;
  public static final int E_VALIDATION = -2;
  public static final int E_SERVER_TIMEOUT = -3;
  public static final int E_UNKNOW = -100;
  public static final int E_DOWNLOAD_TIMEOUT = 400;
  
  
  private final HttpClient hc;
  private final HttpHost host;
  private final int no;
  private final String guid;
  private final File file;
  private long start;
  private long limit;
  private int resumeCnt;
  private final ExecutorService es;
  private final  T9EsbTaskInfo taskInfo;
  private T9DownloadTask task = null;
  
  public MultiThreadDownloadRequest(ExecutorService es, HttpClient hc, HttpHost host, T9EsbTaskInfo taskInfo, int no, String guid, long start, long limit, File file, T9DownloadTask task ) {
    this.es = es;
    this.hc = hc;
    this.host = host;
    this.no = no;
    this.start = start;
    this.limit = limit;
    this.guid = guid;
    this.file = file;
    this.taskInfo = taskInfo;
    this.task = task;
  }
  
  public void run() {
    HttpResponse response = null;
    try {
      //T9EsbUtil.debug("MultiThreadDownloadRequest-" + 1);
      response = this.request(); 
      //T9EsbUtil.debug("MultiThreadDownloadRequest-" + 2);
    } catch (Exception e) {
//      T9EsbUtil.println("MultiThreadRequest: 线程" + no + " - 请求出现异常,取消请求");
      e.printStackTrace();
      this.resume();
      //return;
    } finally {
      
    }
    
    try {
      int code = this.parseResponse(response);
      
      if (code == 0) {
//        taskInfo.done(no);
        //T9EsbUtil.println("分片" + no + "传输成功");
      }
      else if (code == E_NO_RESPONSE) {
        this.resume();
      }
      else if (code == E_SERVER_TIMEOUT) {
        this.stopAll();
      }
      else if (code == E_VALIDATION) {
        this.resume();
      }
      else if (code == E_UNKNOW) {
        this.resume();
      } else if (code == E_DOWNLOAD_TIMEOUT) {
        this.stopAll();
      }
    } catch (Exception e) {
      e.printStackTrace();
      this.resume();
    } finally {
      T9HttpClientUtil.releaseConnection(response);
    }
  }
  
  /**
   * 处理返回数据
   * @param response
   * @return
   * @throws IllegalStateException
   * @throws IOException
   */
  private int parseResponse(HttpResponse response) throws IllegalStateException, IOException {
    if (response == null) {
      return E_NO_RESPONSE;
    }
    else {
      Header h5 = response.getFirstHeader("SYS-FIELD");
      if (h5 != null) {
        String value = h5.getValue();
        if ("1".equals(value)) {
          //超时
          T9EsbPollerLogic logic = new T9EsbPollerLogic();
          logic.updateStatus(guid, "-3");
          response.getEntity().getContent().close();
          if (this.task != null) {
            try {
              this.task.saveTaskInfo();
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
          return E_DOWNLOAD_TIMEOUT;
        }
      }
      Header mdHeader = response.getFirstHeader("Content-MD5");
      if (mdHeader == null || T9Utility.isNullorEmpty(mdHeader.getValue())) {
        if (this.task != null) {
          try {
            this.task.saveTaskInfo();
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        return E_SERVER_TIMEOUT;
      }
      InputStream is = response.getEntity().getContent();
      String md5 = mdHeader.getValue();
      ByteBuffer bb = ByteBuffer.allocate(Integer.parseInt(response.getFirstHeader("Data-Length").getValue()));
      byte[] tmp = new byte[1024];
      int i = 0;
      try {
        for (i = 0; (i = is.read(tmp)) > 0;) {
          bb.put(tmp, 0, i);
          //System.out.println("第 " + i + "次");
        }
      }catch(Exception ex) {
        ex.printStackTrace();
      }
      byte[] b = bb.array();
      bb.clear();
      synchronized (T9DownloadTask.loc1) {
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        rf.seek(start);
        if (T9DigestUtility.isMatch(b, md5)) {
          rf.write(b);
          rf.close();
          taskInfo.done(no);
          return 0;
        }
        else {
          rf.close();
          return E_VALIDATION;
        }
      }
    }
  }
  
  /**
   * 请求数据
   * @return
   * @throws ClientProtocolException
   * @throws IOException
   */
  public HttpResponse request() throws ClientProtocolException, IOException {
    HttpPost request = new HttpPost(T9DownloadTask.DOWNLOAD_TRANSFER_URL);
    //System.out.println("RANGE" + start + "-" + (start + limit));
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    nvps.add(new BasicNameValuePair("RANGE", start + "-" + (start + limit)));
    nvps.add(new BasicNameValuePair("GUID", guid));
    request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    return hc.execute(host, request);
  }
  
  /**
   * 重传,超过重传限制则终止整个下载任务   */
  public void resume() {
//    T9EsbUtil.println("MultiThreadRequest: 线程" + no + "异常,进行重传");
    if (resumeCnt++ < 10) {
      try {
        es.execute(this);
      } catch (Exception e) {
//        T9EsbPollerLogic logic = new T9EsbPollerLogic();
//        logic.updateStatus( guid, "-1");
//        T9EsbUtil.println("MultiThreadRequest: 线程" + no + "加入线程池失败");
      }
    }
    else {
      T9EsbPollerLogic logic = new T9EsbPollerLogic();
      logic.updateStatus( guid, "-1");
      if (this.task != null) {
        try {
          this.task.saveTaskInfo();
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
//      T9EsbUtil.println("MultiThreadRequest: 线程" + no + "失败" + resumeCnt + "次,下载任务失败");
      this.stopAll();
    }
  }
  
  /**
   * 终止整个下载任务
   */
  public void stopAll() {
    es.shutdownNow();
  }
  
}
