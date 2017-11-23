package t9.core.esb.server.sysConfig.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import t9.core.esb.common.util.ClientPropertiesUtil;
import t9.core.esb.common.util.PropertiesUtil;
import t9.core.esb.server.act.T9RangeUploadAct;
import t9.core.esb.server.sysConfig.data.ClientConfig;
import t9.core.esb.server.sysConfig.data.SysConfig;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;


public class SysConfigLogic {

  public SysConfig getSysConfigLogic(Connection dbConn , HttpServletRequest request){
    SysConfig sysconfig = new SysConfig();
    try{
//      PropertiesUtil.getClientProperties();
      sysconfig.setUploadPartSize(PropertiesUtil.getProp("UPLOAD_PART_SIZE"));
      sysconfig.setDownloadPartSize(PropertiesUtil.getProp("DOWNLOAD_PART_SIZE"));
      sysconfig.setMaxUploadTime(PropertiesUtil.getProp("MAX_UPLOAD_TIME"));
      sysconfig.setMaxDownloadTime(PropertiesUtil.getProp("MAX_DOWNLOAD_TIME"));
      sysconfig.setUploadCacheDir(PropertiesUtil.getProp("UPLOAD_CACHE_DIR"));
      
    }catch(Exception e){
      e.printStackTrace();
    }
    return sysconfig;
  }
  
  public void updateSysConfigLogic(Connection dbConn , HttpServletRequest request , Map map){
    String uploadCacheDir = (String)map.get("uploadCacheDir");
    String maxUploadTime = (String)map.get("maxUploadTime");
    String maxDownloadTime = (String)map.get("maxDownloadTime");
    String uploadPartSize = (String)map.get("uploadPartSize");
    String downloadPartSize = (String)map.get("downloadPartSize");
    try{
      
//      PropertiesUtil.getClientProperties();
      PropertiesUtil.updateProp("UPLOAD_PART_SIZE", uploadPartSize);
      PropertiesUtil.updateProp("DOWNLOAD_PART_SIZE", downloadPartSize);
      PropertiesUtil.updateProp("MAX_UPLOAD_TIME", maxUploadTime);
      PropertiesUtil.updateProp("MAX_DOWNLOAD_TIME", maxDownloadTime);
      PropertiesUtil.updateProp("UPLOAD_CACHE_DIR", uploadCacheDir);
      if (!T9Utility.isNullorEmpty(uploadCacheDir)) {
        T9RangeUploadAct.UPLOAD_PATH = uploadCacheDir;
      }
      PropertiesUtil.updateProp("token", PropertiesUtil.getProp("token"));
      PropertiesUtil.store();
      //PropertiesUtil.
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public ClientConfig getClientConfigLogic(Connection dbConn , HttpServletRequest request){
    ClientConfig clientConfig = new ClientConfig();
    try{
//      PropertiesUtil.getClientProperties();
      clientConfig.setHost(ClientPropertiesUtil.getProp("host"));
      clientConfig.setUsername(ClientPropertiesUtil.getProp("usercode"));
      clientConfig.setPort(ClientPropertiesUtil.getProp("port"));
      clientConfig.setPassword(ClientPropertiesUtil.getProp("password"));
      clientConfig.setCacheDir(ClientPropertiesUtil.getProp("cacheDir"));
    }catch(Exception e){
      e.printStackTrace();
    }
    return clientConfig;
  }
  
  public void updateClientConfigLogic(Connection dbConn , HttpServletRequest request , Map map){
    String host = (String)map.get("host");
    String username = (String)map.get("username");
    String port = (String)map.get("port");
    String password = (String)map.get("password");
    String cacheDir = (String)map.get("cacheDir");
    try{
      
//      PropertiesUtil.getClientProperties();
      ClientPropertiesUtil.updateProp("host", host);
      ClientPropertiesUtil.updateProp("usercode", username);
      ClientPropertiesUtil.updateProp("port", port);
      ClientPropertiesUtil.updateProp("password", password);
      ClientPropertiesUtil.updateProp("cacheDir", cacheDir);
      ClientPropertiesUtil.store();
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
