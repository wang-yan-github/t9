package t9.core.esb.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.entity.NByteArrayEntity;
import org.apache.http.protocol.HTTP;

import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.common.util.T9HttpClientUtil;
import t9.core.esb.frontend.T9EsbFrontend;
import t9.core.esb.frontend.logic.T9EsbFrontendLogic;
import t9.core.esb.frontend.oa.T9ESBMessageServiceCaller;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9DigestUtility;

public class T9UploadTask {
  public static Integer maxSeqId = 0;
  public int taskSeqId = 0;
  public int count = 0 ;
  public static String LOGIN_URL = "/t9/t9/core/esb/server/system/act/SystemLoginAct/doLogin.act";
  public static final String UPLOAD_URL = "/t9/t9/core/esb/server/act/T9RangeUploadAct/transfer.act";
  public static final String UPLOAD_INITIALIZE_URL = "/t9/t9/core/esb/server/act/T9RangeUploadAct/initialize.act";
  public static final String UPLOAD_COMPLETE_URL = "/t9/t9/core/esb/server/act/T9RangeUploadAct/complete.act";
  private File file;
  private HttpHost host;
  private String toId;
  private String guid;
  private int completeResumeCnt;
  public String hasDone = "" ;
  public String message = "";
  public String optGuid = "";
  
  public T9UploadTask(HttpHost host, File file, String toId, String guid , String optGuid , String message   ) {
    this.host = host;
    this.toId = toId;
    this.file = file;
    this.guid = guid;
    this.message = message ;
    this.optGuid = optGuid;
    
    synchronized(maxSeqId) {
      taskSeqId = ++maxSeqId;
    }
  }
  public HttpResponse initializeQuest(DefaultHttpClient hc) throws Exception {
    HttpPost post = new HttpPost(UPLOAD_INITIALIZE_URL);
    
    
    String md5 = T9DigestUtility.md5File(file.getAbsolutePath());
    
    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    long contentLength = file.length();
    nvps.add(new BasicNameValuePair("Content-length", String.valueOf(contentLength)));
    nvps.add(new BasicNameValuePair("MD5", md5));
    nvps.add(new BasicNameValuePair("Content-name", file.getName()));
    nvps.add(new BasicNameValuePair("TO_ID", toId));
    nvps.add(new BasicNameValuePair("GUID", guid));
    nvps.add(new BasicNameValuePair("FileSeqId", String.valueOf(taskSeqId)));
    nvps.add(new BasicNameValuePair("optGuid", optGuid));
    nvps.add(new BasicNameValuePair("message", message));
    
    post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
    HttpResponse response = null;
    try {
      T9EsbUtil.debug("u-initializeQuest-任务:"+ this.guid  +"发送初始化请求");
      long start = System.currentTimeMillis();
     response = hc.execute(host, post);
     long end = System.currentTimeMillis();
     T9EsbUtil.debug("u-initializeQuest-任务:"+ this.guid  +"完成发送初始化请求，请求耗时：" + (end - start) );
     return response;
    } catch(Exception ex) {
      count++;
      ex.printStackTrace();
      T9EsbUtil.debug("u-initializeQuest-任务:"+ this.guid  +"执行次数："+ count +"，发送初始化请求，报错：" + ex.getMessage() );
      if (count < 10) {
        return this.initializeQuest(hc);
      } else {
        ex.printStackTrace();
        T9EsbUtil.debug("u-initializeQuest-任务:"+ this.guid  +"执行次数："+ count +"，发送初始化请求，报错：" + ex.getMessage() );
        return null;
      }
    }
  }
  /**
   * 初史化，并把md5值发送过去
   * @param hc
   * @param conn 
   * @param pwd 
   * @param userName 
   * @return
   * @throws Exception
   */
  public boolean initialize(DefaultHttpClient hc) throws Exception {
    if (T9Utility.isNullorEmpty(guid)) {
      this.guid = UUID.randomUUID().toString();
    }
    T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传初始化");
    T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
    
    synchronized(T9EsbFrontend.loc2) {
      if (logic.hasEsbUploadFinish(guid)) {
        T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"任务已上传成功");
        return false;
      } else {
        logic.updateStatus( guid, "1");
      }
    }
    long begin = System.currentTimeMillis();
    HttpResponse response = this.initializeQuest(hc);
    long end = System.currentTimeMillis();
    T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传初始化，耗时：" + (end - begin));
    
    boolean flag = false;
    if (response == null) {
      //异常处理
      T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传初始化请求为空");
      flag = false;
    } else {
      //正常,返回guid
      T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传初始化请求返回正常");
      Header[] headers = response.getAllHeaders();
      
      for (Header h : headers) {
        if ("Content-GUID".equals(h.getName()) ) {
          this.guid = h.getValue();
          flag = true;
        }
        if ("SYS-FIELD".equals(h.getName()) ) {
          String sysFlag = h.getValue();
          if ("1".equals(sysFlag)) {
            T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传失败");
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            try{
              caller.updateState(guid, -2, "");
              T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传失败后，完成相应的业务处理");
            } catch (Exception ex) {
              ex.printStackTrace();
              T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传失败后，完成相应的业务处理时报错："+ ex.getMessage());
            }
            synchronized(T9EsbFrontend.loc2) {
              logic.updateStatus(guid, "-3");
            }
            T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传失败后，更新上传表的状态为失败");
            return false;
            //上传已经成功
          } else if ("3".equals(sysFlag)) {
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            try {
              caller.updateState(guid, 1, "");
              T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传成功后，完成相应的业务处理");
            } catch (Exception ex) {
              T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传成功后，完成相应的业务处理时报错："+ ex.getMessage());
            }
            synchronized(T9EsbFrontend.loc2) {
              T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"上传成功后，更新上传表的状态");
              logic.updateStatus(guid, "0");
            }
            return false;
          }
          flag = true;
        }
      }
      HttpEntity et = response.getEntity();
      if (et != null) {
        InputStream is = et.getContent();
        String res = "";
        byte[] b = new byte[1024];
        for (int i = 0; (i = is.read(b)) > 0;) {
          res += new String(b);
        }
        if (!"".equals(res.trim())) {
          this.hasDone = res.trim();
          T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"读取已下载的分片成功");
        }
      }
    }
    if (!flag) {
      //传输失败
      T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"初始化失败");
        logic.updateStatus(guid, "-1");
    } else {
      T9EsbUtil.debug("u-initialize-任务:"+ this.guid  +"初始化成功");
    }
    return flag;
  }
  /**
   * 传文件
   * @param hc
   * @throws Exception
   */
  public void transfer(HttpClient hc) throws Exception {
    long size = PropertiesUtil.getUploadPartSize();
    long length = this.file.length();
    Collection<Runnable> tasks = new ArrayList<Runnable>();
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(100000);
    ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);
    pool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
      public void rejectedExecution(Runnable r, ThreadPoolExecutor pool) {
        T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
        T9EsbUtil.debug("u-transfer-任务传输时，传输线程失败");
        logic.updateStatus(guid, "-1");
      }
    });
    
    int partCnt = (length % size == 0) ? (int)(length / size) :  (int)(length / size + 1);
    int partIndex = 0;
    Set<Integer> set = new HashSet();
    T9EsbUtil.debug("u-transfer-任务"+ this.guid +"传输初始化(去掉已完成的分片)，");
    String[] done = this.hasDone.split(",");
    for (String s : done) {
      if (!T9Utility.isNullorEmpty(s) && T9Utility.isInteger(s))
        set.add(Integer.parseInt(s));
    }
    T9EsbUtil.debug("u-transfer-任务"+ this.guid +"完成传输初始化开始传输");
    for (int start = 0, i = 0; start < length;  i++) {
      if (start + size > length) {
        size = length - start;
      }
      if (set.contains(i)) {
        start += size;
        continue;
      }
      partIndex++;
      MultiThreadUploadRequest multiThreadRequest = new MultiThreadUploadRequest(pool, hc, host, i, guid, start, size, file, taskSeqId, partCnt, partIndex );
      tasks.add(multiThreadRequest);
      pool.execute(multiThreadRequest);
      start += size;
    }
    while (pool.getQueue().size() > 0) {
      Thread.sleep(1000);
    }
    pool.shutdown();
    while (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
      //等待所有线程结束
    }
  }
  /**
   * 校验
   * @param hc
   * @return
   * @throws Exception
   */
  public int complete(HttpClient hc) throws Exception {
    long begin = System.currentTimeMillis();
    try {
      HttpPost post = new HttpPost(UPLOAD_COMPLETE_URL);
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("GUID", guid));
      nvps.add(new BasicNameValuePair("FileSeqId", String.valueOf(taskSeqId)));
      post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      
      long begin2 = System.currentTimeMillis();
      T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "发送上传完成请求");
      HttpResponse response = hc.execute(host, post);
      long end = System.currentTimeMillis();
      T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "完成发送上传完成请求，耗时：" + (end - begin2));
      int returnValue = -3;
      if (response == null) {
        returnValue = -3;
        T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "发送上传完成请求，返回为空");
      }
      else {
        int code = response.getStatusLine().getStatusCode();
        response.getEntity().getContent().close();
        
        if (code == 200) {
          T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
          try {
            caller.updateState(guid, 1, "");
            T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "完成上传，处理相应的业务");
          } catch (Exception ex) {
            ex.printStackTrace();
            T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "完成上传，处理相应的业务时报错" + ex.getMessage());
          }
          returnValue = 0;
        } else if (code == 300) {
          returnValue = -1;
        } else if (code == 400) {
          T9EsbUtil.debug("complete-6");
          T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "发送失败!");
          try {
            T9ESBMessageServiceCaller caller = new T9ESBMessageServiceCaller();
            caller.updateState(guid, -2, "");
            T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "上传失败，处理相应的业务");
          } catch (Exception ex) {
            ex.printStackTrace();
            T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "上传失败，处理相应的业务时报错" + ex.getMessage());
          }
          returnValue = -4;
        }else  {
          returnValue = -2;
        }
      }
      T9EsbUtil.debug("complete-8:" + returnValue);
      if (returnValue != 0) {
        if (returnValue == -4) {
          synchronized(T9EsbFrontend.loc2) {
            T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "上传失败，更新客户端状态");
            T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
            logic.updateStatus(guid, "-3");
          }
          
        } else {
          T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "本次上传失败，更新客户端状态");
          T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
          logic.updateStatus(guid, "-1");
        }
      } else  {
        synchronized(T9EsbFrontend.loc2) {
          T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "上传完毕，更新客户端状态");
          T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
          logic.updateStatus(guid, "0");
        }
      }
      return returnValue;
    } catch (Exception e) {
      e.printStackTrace();
      T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "发送上传任务时报错：" + e.getMessage() + ",次数：" + completeResumeCnt);
      if (completeResumeCnt++ < 10) {
        return complete(hc);
      } else {
        T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
        logic.updateStatus(guid, "-1");
        T9EsbUtil.debug("u-complete-任务:"+ this.guid  + "本次上传失败，更新客户端状态");
        return -3;
      }
    }
  }
}

class MultiThreadUploadRequest implements Runnable {
  private final int taskSeqId;
  private final int totalParts;
  private final int seqId;
  private final HttpClient hc;
  private final HttpHost host;
  private final int no;
  private final String guid;
  private final File file;
  private long start;
  private long limit;
  private int resumeCnt;
  private final ExecutorService es;
  public static final int E_NO_RESPONSE = -1;
  public static final int E_VALIDATION = -2;
  public static final int E_SERVER_TIMEOUT = -3;
  public static final int E_UNKNOW = -100;
  
  public String getName() {
    return "[task " + taskSeqId + " the part " + seqId + "/" + totalParts + "]";
  }
  public MultiThreadUploadRequest(ExecutorService es, HttpClient hc, HttpHost host, int no, String guid, long start, long limit, File file, int taskSeqId, int totalParts, int seqId ) {
    this.hc = hc;
    this.host = host;
    this.no = no;
    this.guid = guid;
    this.file = file;
    this.start = start;
    this.limit = limit;
    this.es = es;
    this.taskSeqId = taskSeqId;
    this.totalParts = totalParts;
    this.seqId = seqId;
  }
  
  public void run() {
    long begin = System.currentTimeMillis();
    HttpResponse response = null;
    try {
      response = this.request();
    } catch (Exception e) {
//      T9EsbUtil.println("MultiThreadRequest: 线程" + getName() + " - 请求出现异常,取消请求");
      e.printStackTrace();
      T9EsbUtil.debug("u-MultiThreadRequest-任务:"+ this.guid  + "线程" + getName() + "报错：" + e.getMessage() );
      this.resume();
      return;
    } finally {
      
    }
    
    try {
      int code = this.parseResponse(response);
      //T9EsbUtil.println("MultiThreadRequest: Thread " + no + " - code: " + code);
      
      if (code == 0) {
        if (resumeCnt > 0) {
//          T9EsbUtil.println(getName() + "重传成功");
        }
      }
      else if (code == E_NO_RESPONSE) {
        this.resume();
      }
      else if (code == E_SERVER_TIMEOUT) {
        synchronized(T9EsbFrontend.loc2) {
          T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
          logic.updateStatus(guid, "-3");
        }
        T9EsbUtil.debug("u-MultiThreadRequest-任务:"+ this.guid  + "，上传超时" );
        this.stopAll();
      }
      else if (code == E_VALIDATION) {
        this.resume();
      }
      else if (code == E_UNKNOW) {
        this.resume();
      }
      long end = System.currentTimeMillis();
      long lastTime = end - begin;
    } catch (Exception e) {
      this.resume();
    } finally {
      T9HttpClientUtil.releaseConnection(response);
    }
  }
  
  public int parseResponse(HttpResponse response) {
    int code = response.getStatusLine().getStatusCode();
    if (code == 300) {
      return E_VALIDATION;
    }
    else if (code == 301) {
      return E_SERVER_TIMEOUT;
    }
    else {
      return 0;
    }
  }
  
  public HttpResponse request() throws IOException {
    HttpResponse response = null;
    HttpPost request = new HttpPost(T9UploadTask.UPLOAD_URL);
    if (start >= 0 && limit > 0 && start < file.length()) {
      request.addHeader("GUID", this.guid);
      if (start + limit > file.length()) {
        limit = file.length() - start;
      }
      RandomAccessFile raf = new RandomAccessFile(file, "r");
      raf.seek(start);
      byte[] bytes = new byte[(int) limit];
      raf.readFully(bytes);
      String md5 = T9DigestUtility.md5Hex(bytes);
      request.addHeader("MD5", md5);
      request.addHeader("START", String.valueOf(start));
      request.addHeader("Con-length", String.valueOf(limit));
      request.addHeader("FileInfo", getName());
      request.addHeader("NO", String.valueOf(this.no));
      raf.close();
      NByteArrayEntity entity = new NByteArrayEntity(bytes);
      request.setEntity(entity);
      boolean flag = false;
      for (int i = 0; i < 10; i++) {
        try {
          response = hc.execute(host, request);
          flag = true;
          break;
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      if (!flag) {
        T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
        logic.updateStatus( guid, "-1");
      }
    }
    return response;
  }
  
  /**
   * 重传,超过重传限制则终止整个下载任务

   */
  public void resume() {
    if (resumeCnt++ < 10) {
      T9EsbUtil.debug("u-MultiThreadRequest-任务:"+ this.guid  + "线程" + getName() + "报错，正在重传,重传次数：  " + resumeCnt  );
      es.execute(this);
    }
    else {
      T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
      logic.updateStatus( guid, "-1");
      T9EsbUtil.debug("u-MultiThreadRequest-任务:"+ this.guid  + "线程" + getName() + "失败，正在重传,重传次数：  " + resumeCnt + "整个上传任务失败" );
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
