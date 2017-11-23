package t9.core.funcs.autorunmgr.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.autorun.T9AutoRunConfig;
import t9.core.autorun.T9AutoRunThread;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

/**
 * 
 * @author tulaike
 *
 */
public class T9AutoRunManager {
  private static Logger log = Logger.getLogger("yzq.t9.core.funcs.autorunmgr.logic.T9AutoRunManager");

  private final String confFile = T9SysProps.getWebInfPath() + File.separator + "config" + File.separator + "autoruntasksconfig.properties";

  /**
   * 根据名称取得后台服务的定义
   * @param name
   * @return
   * @throws Exception 
   */
  public T9AutoRunConfig getAutoRunCfgByName(String name) throws Exception{
    if(!isDefCfg()){
      return null;
    }
    Map<String,T9AutoRunConfig>  arcs = getAutoRunCfgMap();
    T9AutoRunConfig arc = arcs.get(name);
    return arc;
  }
  /**
   * 取得所有的后台服务定义
   * @return
   * @throws Exception 
   */
  public ArrayList<T9AutoRunConfig> getAutoRunCfgList() throws Exception{
    if(!isDefCfg()){
      return null;
    }
    ArrayList<T9AutoRunConfig> arcs = new ArrayList<T9AutoRunConfig>();
    Map<String, String> rawConfMap = new HashMap<String, String>();
    try {
      T9FileUtility.load2Map(confFile, rawConfMap);
      Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "autoRunTask");
      Iterator<String> iKeys = confMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String confJson = confMap.get(key);
        if (T9Utility.isNullorEmpty(confJson)) {
          continue;
        }
        try {
          T9AutoRunConfig config = (T9AutoRunConfig)T9FOM.json2Obj(confJson, T9AutoRunConfig.class);
          arcs.add(config);
        }catch(Exception ex) {
          throw ex;
        }
      }
    }catch(Exception ex) {
      throw ex;
    }
    return arcs;
  }
  
  /**
   * 取得所有的后台服务定义
   * @return
   * @throws Exception 
   */
  public Map<String,T9AutoRunConfig> getAutoRunCfgMap() throws Exception{
    if(!isDefCfg()){
      return null;
    }
    Map<String,T9AutoRunConfig>  arcs = new HashMap<String,T9AutoRunConfig> ();
    Map<String, String> rawConfMap = new HashMap<String, String>();
    try {
      T9FileUtility.load2Map(confFile, rawConfMap);
      Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "autoRunTask");
      Iterator<String> iKeys = confMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String confJson = confMap.get(key);
        if (T9Utility.isNullorEmpty(confJson)) {
          continue;
        }
        try {
          T9AutoRunConfig config = (T9AutoRunConfig)T9FOM.json2Obj(confJson, T9AutoRunConfig.class);
          arcs.put(key, config);
        }catch(Exception ex) {
          throw ex;
        }
      }
    }catch(Exception ex) {
      throw ex;
    }
    return arcs;
  }
  /**
   * 修改单个配置
   * @param name
   * @param arc
   * @throws Exception
   */
  public void updatePropertiesByName(String name,T9AutoRunConfig arc) throws Exception{
    /*if(!isDefCfg()){
      return ;
    }*/
    Map<String, String> rawConfMap = new HashMap<String, String>();
    T9FileUtility.load2Map(confFile, rawConfMap);
    StringBuffer value = T9FOM.toJson(arc);
    rawConfMap.put(name,value.toString());
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(confFile);
      List<String> temp = Map2PopersString(rawConfMap);
      T9FileUtility.storeArray2Line(fos, temp, "UTF-8");
      if("1".equals(arc.getIsUsed())){
        registAutoRun(name, value.toString());
      }else{
        removeAutoRun(name);
      }
     // deBugMainThread();
    } catch (Exception e) {
      throw e;
    } finally {
      if(fos != null)fos.close();
    }
  }
 /* private void deBugMainThread(){
    T9AutoRunThread art = T9AutoRunThread.currInstance();
    System.out.println(art);
  }*/
  /**
   * 注册服务
   * @param name
   * @param json
   */
  public void registAutoRun(String name,String json){
    T9AutoRunThread art = T9AutoRunThread.currInstance();

    if(art == null){
      return;
    }
    art.registAutoRun(name, json);
  }
  
  /**
   * 取消服务
   * @param name
   * @param json
   */
  public void removeAutoRun(String name){
    T9AutoRunThread art = T9AutoRunThread.currInstance();
    if(art == null){
      return;
    }
    art.removeAutoRun(name);
  }
  /**
   * 
   * @param rawConfMap
   * @return
   */
  public List<String> Map2PopersString( Map<String, String> rawConfMap){
    Set<String> keys = rawConfMap.keySet();
    ArrayList<String> result = new ArrayList<String>();
    for (String key : keys) {
      String value = rawConfMap.get(key);
      String temp = key + "=" + value;
      result.add(temp);
    }
    return result;
  }
  /**
   * 注销某条配置
   * @param name
   * @throws Exception
   */
  public void deletePropertiesByName(String name) throws Exception{
    if(!isDefCfg()){
      return ;
    }
    
    Map<String, String> rawConfMap = new HashMap<String, String>();
    T9FileUtility.load2Map(confFile, rawConfMap);
    rawConfMap.remove(name);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(confFile);
      List<String> temp = Map2PopersString(rawConfMap);
      T9FileUtility.storeArray2Line(fos, temp, "UTF-8");
      removeAutoRun(name);
    } catch (Exception e) {
      throw e;
    } finally {
      if(fos != null)fos.close();
    }
//    StringBuffer value = null;//T9FOM.toJson(arc);
//    p.setProperty(name, value.toString());
  }
  /**
   * 判断当前的配置项是否存在
   * @param name
   * @return
   * @throws Exception
   */
  public boolean exists(String name) throws Exception{
    if(!isDefCfg()){
      return false ;
    }
    if(name == null){
      return false;
    }
    FileInputStream fis = new FileInputStream(confFile);
    Properties p = new Properties();
    p.load(fis);
    return p.containsKey(name);
  }
  /**
   * 取得最大的Id值
   * @return
   * @throws Exception
   */
  public int getAutoRunName() throws Exception{
    if(!isDefCfg()){
      return 0;
    }
    Map<String, String> rawConfMap = new HashMap<String, String>();
    T9FileUtility.load2Map(confFile, rawConfMap);
    Map<String, String> confMap = T9Utility.startsWithMap(rawConfMap, "autoRunTask");

    Set<String>  keys = confMap.keySet();
    int max = 0;
    for (String key: keys) {
      if(key.startsWith("autoRunTask")){
        int temp = 0;
        try {
          String s = key.substring("autoRunTask".length());
          temp = Integer.valueOf(s);
        } catch (Exception e) {
          temp = 0;
        }
        if(max < temp){
          max = temp;
        }
      }
    }
    return max + 1;
  }
  /**
   * 判断配置文件是否存在
   * @return
   */
  private boolean isDefCfg(){
    File cfgFile = new File(confFile);
    return cfgFile.exists();
  }
  /**
   * 得到所有配置文件，转换成json数据
   * @return
   * @throws Exception
   */
  public StringBuffer getAutoRunCfgList2Json() throws Exception{
    StringBuffer sb = new StringBuffer();
    StringBuffer arcJsons = new StringBuffer();
    Map<String,T9AutoRunConfig> arcs = getAutoRunCfgMap();
    if(arcs != null){
      Iterator<String> iKeys = arcs.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        T9AutoRunConfig arc = arcs.get(key);
        if(!"".equals(arcJsons.toString())){
          arcJsons.append(",");
        }
        arcJsons.append("{id:\"").append(key).append("\",")
          .append("data:").append(T9FOM.toJson(arc)).append("}");
      }
    }
    sb.append("[").append(arcJsons).append("]");
    return sb;
  }
  /**
   * 判断主服务是否状态
   * @return
   */
  public String getMainThreadStatus(){
    StringBuffer sb = new StringBuffer();
    T9AutoRunThread art = T9AutoRunThread.currInstance();
    String isRunning = "1";
    if(art == null){
      isRunning = "0";
    }
    sb.append("{isRunning:\"" + isRunning + "\"}");
    return sb.toString();
  }
  
  /**
   * 执行主服务操作
   * @param type
   * @return
   */
  public void execMainThreadStatus(int type) throws Exception{
    if(type == 1){
      T9AutoRunThread.stopRun();
    }else{
      int sleepTime = T9SysProps.getInt(
          T9SysPropKeys.BACK_THREAD_SLEEP_TIME);
      if (sleepTime < 1) {
        sleepTime = 100;
      }
      sleepTime = 10000;
      T9AutoRunThread.startAutoRun(sleepTime);
    }
  }
  
  /**
   * 执行子服务操作
   * @param type
   * @return
   */
  public int execSubThreadStatus(String id) throws Exception{
      T9AutoRunThread art = T9AutoRunThread.currInstance();
      if(art == null){
        return 4;
      }
      int result =  art.manuStartAutoRun(id);
      return result;
  }
  
  /**
   * 检查类的有效性
   * @param type
   * @return
   */
  public int checkClassIsInvalidity(String clsName){
     int result = 0;
     try {
      Class cls = Class.forName(clsName);
      result = 1;
    } catch (ClassNotFoundException e) {
      result = 0;
    }
     return result;
  }
}
