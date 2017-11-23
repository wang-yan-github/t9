package t9.core.esb.client.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;

import t9.core.util.T9Utility;

public class T9EsbClientConfig {
  
  public String getESBHOST() {
    return ESBHOST;
  } 
  public void setESBHOST(String esbhost) {
    ESBHOST = esbhost;
  }
  public String getESBPORT() {
    return ESBPORT;
  }
  public void setESBPORT(String esbport) {
    ESBPORT = esbport;
  }
  public String getOAHOST() {
    return OAHOST;
  }
  public void setOAHOST(String oahost) {
    OAHOST = oahost;
  }
  public String getOAPORT() {
    return OAPORT;
  }
  public void setOAPORT(String oaport) {
    OAPORT = oaport;
  }
  public String getToken() {
    return token;
  }
  public void setToken(String token) {
    this.token = token;
  }
  public String getESBSERVER() {
    return ESBSERVER;
  }
  public void setESBSERVER(String esbserver) {
    ESBSERVER = esbserver;
  }
  public String getESBSERVERPORT() {
    return ESBSERVERPORT;
  }
  public void setESBSERVERPORT(String esbserverport) {
    ESBSERVERPORT = esbserverport;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getCachePath() {
    return cachePath;
  }
  public void setCachePath(String cachePath) {
    this.cachePath = cachePath;
  }
  public String getWS_PATH() {
    WS_PATH = "http://" + this.ESBHOST + ":" + this.ESBPORT + "/t9/services/T9EsbService";
    return WS_PATH;
  }
  public void setWS_PATH(String ws_path) {
    WS_PATH = ws_path;
  }
  public static T9EsbClientConfig getConfig() {
    return config;
  }
  public static void setConfig(T9EsbClientConfig config) {
    T9EsbClientConfig.config = config;
  }
  
  private String local;
  public String getLocal() {
    return local;
  }
  public void setLocal(String string) {
    local = string;
  }

  private String ESBHOST;
  private String ESBPORT;
  private String OAHOST;
  private String OAPORT;
  private String token;
  
  private String ESBSERVER;
  private String ESBSERVERPORT;
  private String password;
  private String userId;
  private String cachePath;
  private String WS_PATH;
  private String webserviceUri;
  
  public String getWebserviceUri() {
    webserviceUri = "http://" + this.OAHOST + ":" + this.OAPORT + "/t9/services/OAWebservice";
    return webserviceUri;
  }
  public void setWebserviceUri(String webserviceUri) {
    this.webserviceUri = webserviceUri;
  }
  public void load(String path) throws FileNotFoundException, IOException {
    Properties p = new Properties();
    Reader rd = new InputStreamReader(new FileInputStream(new File(path)), "UTF-8");  
    p.load(rd);
    this.ESBHOST = p.getProperty(T9EsbConst.ESB_HOST);
    this.ESBPORT = p.getProperty(T9EsbConst.ESB_PORT);
    if (T9Utility.isNullorEmpty(ESBPORT)){
      this.ESBPORT = "8089";
    }
    
    this.OAHOST = p.getProperty(T9EsbConst.OA_HOST);
    this.OAPORT = p.getProperty(T9EsbConst.OA_PORT);
    if (T9Utility.isNullorEmpty(OAPORT)){
      this.OAPORT = "80";
    }
    this.token = p.getProperty(T9EsbConst.TOKEN);
    
    this.ESBSERVER =  p.getProperty(T9EsbConst.ESB_SERVER_HOST);
    this.ESBSERVERPORT =  p.getProperty(T9EsbConst.ESB_SERVER_PORT);
    if (T9Utility.isNullorEmpty(ESBSERVERPORT)){
      this.ESBSERVERPORT = "8088";
    }
    this.password =  p.getProperty(T9EsbConst.PASSWORD);
    this.userId =  p.getProperty(T9EsbConst.USER_ID);
    this.cachePath =  p.getProperty(T9EsbConst.CACHE_PATH);
    this.local = p.getProperty(T9EsbConst.IS_LOCAL);
    if (T9Utility.isNullorEmpty(this.local)  ) {
      this.local = "0";
    }
    p.clear();
    rd.close();
  }
  public void store(String path)throws Exception {
    Properties p = new Properties();
    p.put(T9EsbConst.ESB_HOST, this.ESBHOST);
    p.put(T9EsbConst.ESB_PORT, this.ESBPORT);
    p.put(T9EsbConst.ESB_SERVER_HOST, this.ESBSERVER);
    p.put(T9EsbConst.ESB_SERVER_PORT, this.ESBSERVERPORT);
    p.put(T9EsbConst.USER_ID, this.userId);
    p.put(T9EsbConst.OA_HOST, this.OAHOST);
    p.put(T9EsbConst.OA_PORT, this.OAPORT);
    p.put(T9EsbConst.PASSWORD, this.password);
    p.put(T9EsbConst.TOKEN, this.token);
    p.put(T9EsbConst.CACHE_PATH, this.cachePath);
    p.put(T9EsbConst.IS_LOCAL, this.local);
    FileOutputStream fo =  new FileOutputStream(new File(path));
    Writer wr = new OutputStreamWriter(fo , "UTF-8");
    p.store(wr,new Date().toString());
    wr.flush();
    wr.close();
    p.clear();
  }
  public static T9EsbClientConfig config = null; 
  public static T9EsbClientConfig builder(String path) throws Exception {
    if (config == null ) {
      config = new T9EsbClientConfig();
      config.load(path);
    }
    return config;
  }
}
