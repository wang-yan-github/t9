package t9.core.esb.frontend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import t9.core.esb.common.T9DownloadTask;
import t9.core.esb.common.T9UploadTask;
import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.T9EsbUtil;
import t9.core.esb.frontend.logic.T9EsbFrontendLogic;
import t9.core.esb.frontend.services.T9EsbServiceLocal;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9EsbFrontend {
  
  private static String cookie;
  public static final int MAX_UPLOAD_QUEUE_SIZE = 10;
  public static final String PAUSE_URL = "/t9/t9/core/esb/server/act/T9EsbServerAct/pause.act";
  public static final String LOGIN_URL = "/t9/t9/core/esb/server/system/act/SystemLoginAct/doLogin.act";
  public static final String IS_ONLINE_URL = "/t9/t9/core/esb/server/act/T9EsbServerAct/isOnline.act";
  public static final String RE_SEND = "/t9/t9/core/esb/server/act/T9EsbServerAct/reSend.act";
  public static final String RE_DOWN = "/t9/t9/core/esb/server/act/T9EsbServerAct/reDown.act";
  public static final String SEND_MESSAGE_URL = "/t9/t9/core/esb/server/act/T9EsbServerAct/sendMessage.act";
  public static final String REPORT_MESSAGE_TO_SERVER= "/t9/t9/core/esb/server/act/T9EsbServerAct/reportMessage.act";
  
  private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(1000);
  private static ThreadPoolExecutor pool = null;
  public static final ThreadSafeClientConnManager tcm = new ThreadSafeClientConnManager();
  public static final DefaultHttpClient hc = new DefaultHttpClient(tcm);
  static {
    HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {
      public boolean retryMethod(
          final HttpMethod method, 
          final IOException exception, 
          int executionCount) {
          if (executionCount >= 3) {
              return false;
          }
          if (exception instanceof NoHttpResponseException) {
              return true;
          }
          if (!method.isRequestSent()) {
              return true;
          }
          return false;
      }
    };
    pool = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, workQueue);
    tcm.setDefaultMaxPerRoute(10);
    tcm.setMaxTotal(20);
    HttpParams params = hc.getParams();
    params
        .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1000 * 30)
        .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 30)
        .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 10 * 1024)
        .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
        .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
        .setParameter(CoreProtocolPNames.USER_AGENT, "HttpComponents/1.1")
        .setParameter(HttpMethodParams.RETRY_HANDLER, myretryhandler);
    HttpClientParams.setCookiePolicy(params,CookiePolicy.BROWSER_COMPATIBILITY);
  }
  public static int getWorkQueueSize() {
    return pool.getQueue().size();
  }
  
  public static boolean isCompleted() {
    return pool.getQueue().size() == 0 && pool.getActiveCount() == 0;
  }
  
  
  public static DefaultHttpClient getHc() {
    return hc;
  }
  /**
   * 配置用户名密码主机地址
   * @param host
   * @param username
   * @param password
   * @param oaWebserviceUri
   * @return
   */
  public static String config(String host, int port, String username, String password, String webserviceUri, String cacheDir) {
    ClientPropertiesUtil.updateProp("usercode", username);
    ClientPropertiesUtil.updateProp("password", password);
    ClientPropertiesUtil.updateProp("host", host);
    ClientPropertiesUtil.updateProp("port", port);
    ClientPropertiesUtil.updateProp("webserviceUri", webserviceUri);
    ClientPropertiesUtil.updateProp("cacheDir", cacheDir);
    ClientPropertiesUtil.store();
    return "{\"code\": \"0\", \"msg\": \"Configuration has been modified!\"}";
  }
  
  
  /**
   * 读配置文件登陆   * @return
   */
  public static int login() {
    ClientPropertiesUtil.refresh();
    String username = ClientPropertiesUtil.getProp("usercode");
    HttpHost host = ClientPropertiesUtil.getHttpHost();
    String password = ClientPropertiesUtil.getProp("password");
    
    if (T9Utility.isNullorEmpty(username) ||
        host == null) {
      return -6;
    }
    
    return login(host, username, password);
  }
  public static final Set<String> nowTask = new HashSet();
  public final static byte[] loc = new byte[1];
  public final static byte[] loc2 = new byte[1];
  public static final Set<String> nowDownTask = new HashSet();
  public final static byte[] loc1 = new byte[1];
  /**
   * 发送
   * @param file
   * @param toId
   * @return  [0: 发送成功, -2: 获取主机配置不正确, -100: 程序出现异常]
   */
  public static int send(final File file, final String toId , final String guid ,final String optGuid ,final String message ) {
    final HttpHost host = ClientPropertiesUtil.getHttpHost();
    if (host == null) {
      T9EsbUtil.debug("u-获取主机配置不正确");
      return -2;
    }
    try {
      T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
      if (!logic.hasEsbUploadTask( guid)) {
        T9EsbUtil.debug("u-任务"+guid+"不存在，添加任务");
        logic.addEsbUploadTask(file.getAbsolutePath(), guid, 1, toId , T9Utility.null2Empty(optGuid) , T9Utility.null2Empty(message));
      } else {
        T9EsbUtil.debug("u-任务"+guid+"已存在");
      }
      Runnable task = new Runnable() {
        public void run() {
          DefaultHttpClient hc = T9EsbFrontend.getHc();
        //是否已经在上传中
          synchronized(T9EsbFrontend.loc) {
            boolean uploading  =  T9EsbFrontend.nowTask.contains(guid);
            if (uploading) {
              T9EsbUtil.debug("u-任务"+guid+"正在上传");
              return ;
            }
            T9EsbFrontend.nowTask.add(guid);
          }
          if (!file.exists()) {
            T9EsbUtil.debug("u-任务"+guid+"上传文件不存在");
            synchronized(T9EsbFrontend.loc) {
              T9EsbFrontend.nowTask.remove(guid);
            }
            return ;
          }
          
          try {
          
            T9UploadTask upload = new T9UploadTask(host, file, toId, guid , optGuid , message );
            T9EsbUtil.debug("u-任务"+guid+"初始化任务");
            boolean flag = upload.initialize(hc );
            
            if (flag) {
              T9EsbUtil.debug("u-任务"+guid+"传输开始");
              upload.transfer(hc);
              T9EsbUtil.debug("u-任务"+guid+"传输完成");
              upload.complete(hc);
              T9EsbUtil.debug("u-任务"+guid+"完成单次上传");
            } else {
              T9EsbUtil.debug("u-任务"+guid+"初始化任务失败");
            }
          } catch (Exception e) {
            e.printStackTrace();
            T9EsbUtil.debug("u-发送报错：" + e.getMessage() + ",写入数据库状态");
            T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
            logic.updateStatus( guid, "-1");
          }finally {
            synchronized(loc) {
              T9EsbFrontend.nowTask.remove(guid);
            }
          }
        }
      };
      pool.execute(task);
      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
      logic.updateStatus( guid, "-1");
      return 0;
    } 
  }
  /**
   * 发送函数   * @param filepath      待发送文件的绝对路径
   * @param toId          发送到的OA服务器id串，多个用逗号隔开
   * @return              返回发送的状态 
   * {code:'-3',msg:'发送的文件不存在！'} 发送文件不存在
   * {code:'0',msg:'文件正在发送中......'} 文件正在发送中...
   * {code:'-2',msg:'获取主机配置不正确 ！'} 获取主机配置不正确
   * {code: "-7", msg: "令牌不对！"} 令牌配置不对
   *  {msg:'未知错误!'}   */
  public static String send(final String filepath, final String toId , String optGuid , String message ) {
    try {
      File file = new File(filepath);
      Map<String, String> map = new HashMap<String, String>();
      if (!file.exists()) {
        map.put("code", "-3");
        map.put("msg", "发送的文件不存在！");
          return T9FOM.toJson(map).toString();
      }
      final String guid = UUID.randomUUID().toString();
      int code = T9EsbFrontend.send(file, toId ,guid , optGuid , message);
      String msg = "";
      switch (code) {
        case 0: msg = "文件正在发送中......";break;
        case -2: msg = " 获取主机配置不正确 ！";break;
        default: {
          msg = "Unknown error!";
        }
      }
      map.put("code", String.valueOf(code));
      map.put("guid", String.valueOf(guid));
      map.put("msg",  msg);
      return T9FOM.toJson(map).toString();
    } catch (Exception e) {
      return "";
    }
  }
  /**
   * 发送信息
   * @param message      待发送信息 
   * @param toId          发送到的OA服务器id串，多个用逗号隔开
   * @return              返回发送的状态 
   * {code:'1',msg:'用户未登陆！'} 
   * {code:'-1',msg:'信息发送失败！'} 
   * {code:'0',msg:'信息发送成功！'} 
   * {code:'-2',msg:'发送出错！'} 
   * {code: "-5", msg: "本地配置不对！"} 
   *  {msg:'未知错误!'}
   */
  public static String sendMessage(String message , String toId)  {
    Map<String, String> map = new HashMap<String, String>();
    try {
      ClientPropertiesUtil.refresh();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      if (host == null) {
        map.put("code", "-5");
        map.put("msg", "本地配置不对");
        return T9FOM.toJson(map).toString();
      }
      HttpPost post = new HttpPost(SEND_MESSAGE_URL);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("toId", String.valueOf(toId)));
      nvps.add(new BasicNameValuePair("message", message));
      post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      
      HttpResponse response = hc.execute(host, post);
      InputStream is = response.getEntity().getContent();
      String res = "";
      byte[] b = new byte[1024];
      int i = 0;
      while((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map m = T9FOM.json2Map(res);
      if ("0".equals(m.get("rtState"))) {
        map.put("code", "0");
        map.put("msg", "信息发送成功！");
        return T9FOM.toJson(map).toString();
      } else if ("1".equals(m.get("rtState"))) {
        map.put("code", "1");
        map.put("msg", "用户未登陆！");
        return T9FOM.toJson(map).toString();
      } else {
        map.put("code", "-1");
        map.put("msg", "信息发送失败！");
        return T9FOM.toJson(map).toString();
      }
    } catch (Exception e) {
        map.put("code", "-2");
        map.put("msg", "发送出错！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    } 
    return null;
  }
  /**
   * 发送信息到服务器
   * @param message      待发送信息 
   * @param toId          发送到的OA服务器id串，多个用逗号隔开
   * @return              返回发送的状态 
   * {code:'1',msg:'用户未登陆！'} 
   * {code:'-1',msg:'信息发送失败！'} 
   * {code:'0',msg:'信息发送成功！'} 
   * {code:'-2',msg:'发送出错！'} 
   * {code: "-5", msg: "本地配置不对！"} 
   *  {msg:'未知错误!'}
   */
  public static String reportMessage(String message)  {
    Map<String, String> map = new HashMap<String, String>();
    try {
      ClientPropertiesUtil.refresh();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      if (host == null) {
        map.put("code", "-5");
        map.put("msg", "本地配置不对");
        return T9FOM.toJson(map).toString();
      }
      HttpPost post = new HttpPost(REPORT_MESSAGE_TO_SERVER);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      List<NameValuePair> nvps = new ArrayList<NameValuePair>();
      nvps.add(new BasicNameValuePair("message", message));
      post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
      
      HttpResponse response = hc.execute(host, post);
      InputStream is = response.getEntity().getContent();
      String res = "";
      byte[] b = new byte[1024];
      int i = 0;
      while((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map m = T9FOM.json2Map(res);
      if ("0".equals(m.get("rtState"))) {
        map.put("code", "0");
        map.put("msg", "信息发送成功！");
        return T9FOM.toJson(map).toString();
      } else if ("1".equals(m.get("rtState"))) {
        map.put("code", "1");
        map.put("msg", "用户未登陆！");
        return T9FOM.toJson(map).toString();
      } else {
        map.put("code", "-1");
        map.put("msg", "信息发送失败！");
        return T9FOM.toJson(map).toString();
      }
    } catch (Exception e) {
        map.put("code", "-2");
        map.put("msg", "发送出错！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    } 
    return null;
  }
  /**
   * 登陆
   * @param username
   * @param password
   * @return [0: 成功登陆, -1: 用户不存在, -2: 用户密码错误, -3: 软件已过期, -4: 连接超时, -5: 其他原因, -6: 配置信息不完全]
   */
  public static String login(String host, int port, String username, String password) {
    Map<String, String> map = new HashMap<String, String>();
    try {
      int code = T9EsbFrontend.login(new HttpHost(host, port), username, password);
      map.put("code", String.valueOf(code));
      if (code == 0) {
        map.put("msg", "Login Successful!");
      }
      else if (code == -1) {
        map.put("msg", "User does not exist!");
      }
      else if (code == -2) {
        map.put("msg", "Password error!");
      }
      else if (code == -3) {
        map.put("msg", "Software has expired!");
      }
      else if (code == -4) {
        map.put("msg", "Connection Timeout!");
      }
      else if (code == -5) {
        map.put("msg", "For some reason!");
      }
      else if (code == -6) {
        map.put("msg", "Configuration information is not completely!");
      }
      return T9FOM.toJson(map).toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "{code: \"-100\", \"Exception:" + e.getMessage() + "\"}";
    }
  }
  
  /**
   * 发送给所有用户
   * @param filepath
   * @return
   */
  public static String broadcast(String filepath , String optGuid , String message) {
    return send(filepath, "OTHER_USERS" , optGuid , message);
  }
  
  /**
   * 是否在线
   * @return
   */
  public static boolean isOnline() {
    try {
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      HttpResponse response = hc.execute(host, new HttpGet(IS_ONLINE_URL));
      if (response == null) {
        return false;
      }
      InputStream is = response.getEntity().getContent();
      int i = 0;
      byte[] b = new byte[1024];
      String res = "";
      while ((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map map = T9FOM.json2Map(res);
      if (map != null && map.get("user") != null) {
        return true;
      }
    } catch (ConnectionPoolTimeoutException e) {
//      T9EsbUtil.println("T9EsbFrontend: isOnline - 连接超时");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  public static String getCookie() {
    return T9EsbFrontend.cookie;
  }
  
  public static void setCookie(String cookie) {
    T9EsbFrontend.cookie = cookie;
  }
  
  /**
   * 登陆函数
   * @param host
   * @param username
   * @param password
   * @return [0: 成功登陆, -1: 用户不存在, -2: 用户密码错误, -3: 软件已过期, -4: 连接超时, -5: 其他原因, -6: 配置信息不完全]
   */
  public static int login(HttpHost host,String username, String password) {
    try {
      HttpGet request = new HttpGet(LOGIN_URL + "?userCode=" + username + "&pwd=" + password);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      HttpResponse response = hc.execute(host, request);
      InputStream is = response.getEntity().getContent();
      String res = "";
      byte[] b = new byte[1024];
      int i = 0;
      while((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map m = T9FOM.json2Map(res);
      if (m == null) {
        return -4;
      }
      if ("0".equals(m.get("rtState"))) {
//        T9EsbUtil.println("T9EsbFrontend: login - 登陆成功!");
        return 0;
      }
      
      if ("-1".equals(m.get("rtData"))) {
//        T9EsbUtil.println("T9EsbFrontend: login - 用户不存在!");
        return -1;
      }
      if ("-2".equals(m.get("rtData"))) {
//        T9EsbUtil.println("T9EsbFrontend: login - 密码错误!");
        return -1;
      }
      if ("-3".equals(m.get("rtData"))) {
//        T9EsbUtil.println("T9EsbFrontend: login - 软件已经过期!");
        return -1;
      }
      else {
        return -5;
      }
    } catch (ConnectionPoolTimeoutException e) {
//      T9EsbUtil.println("T9EsbFrontend: login - 连接超时");
      return -4;
    } catch (Exception e) {
//      T9EsbUtil.println("T9EsbFrontend: login - 程序出错");
      return -5;
    }
  }
  
  public static void shutdown() {
    if (pool == null || pool.isShutdown()) {
      return;
    }
    while (!pool.isTerminated()) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    pool.shutdown();
  }
  public static String resend(String guid, String toId)  {
    // TODO Auto-generated method stub
    
    Map<String, String> map = new HashMap<String, String>();
    try {
      ClientPropertiesUtil.refresh();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      if (host == null) {
        map.put("code", "-5");
        map.put("msg", "本地配置不对");
        return T9FOM.toJson(map).toString();
      }
      T9EsbFrontendLogic logic = new T9EsbFrontendLogic();
      if (logic.hasSendField(guid)){
        logic.updateStatus(guid, "1");
      }
      HttpGet request = new HttpGet(RE_SEND + "?guid=" + guid + "&toId=" + toId);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      HttpResponse response = hc.execute(host, request);
      InputStream is = response.getEntity().getContent();
      String res = "";
      byte[] b = new byte[1024];
      int i = 0;
      while((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map m = T9FOM.json2Map(res);
      if (m == null) {
        map.put("code", "-4");
        map.put("msg", "未返回消息！");
        return T9FOM.toJson(map).toString();
      }
      if ("0".equals(m.get("rtState"))) {
        map.put("code", "0");
        map.put("msg", "重发成功！");
        return T9FOM.toJson(map).toString();
      } else {
        map.put("code", "-1");
        map.put("msg", "重发失败！");
        return T9FOM.toJson(map).toString();
      }
    } catch (ConnectionPoolTimeoutException e) {
//      T9EsbUtil.println("T9EsbFrontend: login - 连接超时");
        map.put("code", "-2");
        map.put("msg", "连接超时！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    } catch (Exception e) {
        map.put("code", "-3");
        map.put("msg", "程序出错！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    }
    return null;
  }
  public static String redown(String guid)  {
    // TODO Auto-generated method stub
    
    Map<String, String> map = new HashMap<String, String>();
    try {
      ClientPropertiesUtil.refresh();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      
      if (host == null) {
        map.put("code", "-5");
        map.put("msg", "本地配置不对");
        return T9FOM.toJson(map).toString();
      }
      HttpGet request = new HttpGet(RE_DOWN + "?guid=" + guid);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      HttpResponse response = hc.execute(host, request);
      InputStream is = response.getEntity().getContent();
      String res = "";
      byte[] b = new byte[1024];
      int i = 0;
      while((i = is.read(b)) > 0) {
        res += new String(b).trim();
      }
      is.close();
      Map m = T9FOM.json2Map(res);
      if (m == null) {
        map.put("code", "-4");
        map.put("msg", "未返回消息！");
        return T9FOM.toJson(map).toString();
      }
      if ("0".equals(m.get("rtState"))) {
        map.put("code", "0");
        map.put("msg", "接收成功！");
        return T9FOM.toJson(map).toString();
      } else {
        map.put("code", "-1");
        map.put("msg", "接收失败！");
        return T9FOM.toJson(map).toString();
      }
    } catch (ConnectionPoolTimeoutException e) {
//      T9EsbUtil.println("T9EsbFrontend: login - 连接超时");
        map.put("code", "-2");
        map.put("msg", "连接超时！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    } catch (Exception e) {
        map.put("code", "-3");
        map.put("msg", "程序出错！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    }
    return null;
  }
  public static String down(String tasks) {
    // TODO Auto-generated method stub
    try {
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      DefaultHttpClient hc = T9EsbFrontend.getHc();
     
     
      for (String s : tasks.split(",")) {
        if (s != null && !"".equals(s.trim())) {
          synchronized(loc1) {
            //nowDownTask.add(s);
          }
          T9DownloadTask task = new T9DownloadTask(host, new File(ClientPropertiesUtil.getCacheDir()), s, "", "" , null);
          boolean flag = task.initialize(hc);
          if (flag) {
            task.transfer(hc);
            task.complete(hc);
          }
          synchronized(loc1) {
            //nowDownTask.remove(s);
          }
        }
      }
    } catch (Exception e) {
        e.printStackTrace();
        return "-100";
     } finally {
       for (String s : tasks.split(",")) {
         if (s != null && !"".equals(s.trim())) {
           synchronized(loc1) {
             //nowDownTask.remove(s);
           }
         }
       }
     }
    return null;
  }
  /**
   * 中止
   * @param guid      
   * @return              返回发送的状态 
   * {code:'-6',msg:''}
   */
  public static String pause(String guid) {
    // TODO Auto-generated method stub
    Map<String, String> map = new HashMap<String, String>();
    try {
      ClientPropertiesUtil.refresh();
      HttpHost host = ClientPropertiesUtil.getHttpHost();
      if (!T9EsbServiceLocal.isDownloading(guid)) {
        map.put("code", "-6");
        map.put("msg", "不是下载中的任务！");
        return T9FOM.toJson(map).toString();
      }
      T9EsbPoller.stopDownlondRun();
    } catch (Exception e) {
        map.put("code", "-3");
        map.put("msg", "程序出错！");
        try {
          return T9FOM.toJson(map).toString();
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
    }
    return null;
  }
  public static void main(String[] args) {
    try {
      HttpHost host = new HttpHost("192.168.0.154", 11);
      DefaultHttpClient hc = T9EsbFrontend.getHc();
      hc.execute(host, new HttpGet(IS_ONLINE_URL));
    }catch (org.apache.http.conn.HttpHostConnectException ex) {
    }catch ( java.net.ConnectException ex) {
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
