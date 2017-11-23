package t9.subsys.portal.guoyan.module.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import t9.core.data.T9Database;
import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.portal.guoyan.module.data.T9PortalDataRule;

public class T9PortalUtil {

  /**
   * 组装json数据
   * @return
   */
  public static StringBuffer toJson(T9PageDataList pageDataList){
    StringBuffer result = new StringBuffer();
    StringBuffer field = new StringBuffer();
    int recordCnt = pageDataList.getRecordCnt();
    for (int i = 0; i < recordCnt; i++) {
      T9DbRecord record = pageDataList.getRecord(i);
      if (!"".equals(field.toString())) {
        field.append(",");
      }
      field.append(record.toJson());
    }
    result.append("[").append(field).append("]");
    return result;
  }
  
  /**
   * 组装json数据
   * @return
   */
  public static StringBuffer oneDatatoJson(T9PageDataList pageDataList){
    StringBuffer result = new StringBuffer();
    T9DbRecord record = null;
    if(pageDataList.getRecordCnt() > 0){
      record = pageDataList.getRecord(0);
      result.append(record.toJson());
    }else{
      result.append("{}");
    }
    return result;
  }
  /**
   * 解析页面穿过来的参数
   * @param params
   * @return
   */
  public static Map<String, String> praserParams(Map params){
    Map<String, String> resultParams = new HashMap<String, String>();
    Set<String> keys = params.keySet();
    for (String key : keys) {
      String value = "";
      Object paramValue = params.get(key);
      if (paramValue instanceof String) {
        value = (String)paramValue;
      }else {
        value = ((String[])paramValue)[0];
      }
      resultParams.put(key, value);
    }
    return resultParams;
  }
  
  public static String getUserNameById(Connection conn,String userId) throws Exception{
    String sql = "select USER_NAME from user where USER_ID='" + userId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    String result = "";
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  /**
   * 
   * @param fileName
   * @return
   * @throws Exception
   */
  public static Map<String,T9PortalDataRule> loadDataRules(String fileName) throws Exception {
    Map<String,T9PortalDataRule> rtList = new  HashMap<String,T9PortalDataRule>();
    
    if (new File(fileName).exists()) {
      Map<String, String> ruleMap = new HashMap<String, String>();
      T9FileUtility.load2Map(fileName, ruleMap);
      Iterator<String> iKeys = ruleMap.keySet().iterator();
      while (iKeys.hasNext()) {
        String key = iKeys.next();
        String dbConfJson = ruleMap.get(key).trim();
        if (T9Utility.isNullorEmpty(dbConfJson)) {
          continue;
        }
        try {
          T9PortalDataRule pdr = (T9PortalDataRule) T9FOM.json2Obj(dbConfJson, T9PortalDataRule.class);
          rtList.put(key,pdr);
        }catch(Exception ex) {
          throw ex;
        }
      }
    }
    return rtList;
  } 
  /**
   * 取得配置文件的位置
   * @return
   */
  public static String getConfigPath(){
    String configPath = "";
    configPath = T9SysProps.getRootPath() 
      + "\\" + T9SysProps.getString(T9SysPropKeys.WEB_ROOT_DIR)
      + "\\" + T9SysProps.getString(T9SysPropKeys.JSP_ROOT_DIR)
      + "\\subsys\\portal\\guoyan\\config";
    return configPath;
  }
  /**
   * 取得配置文件的位置
   * @return
   */
  public static String getConfigFileName(){
    String configFileName = "";
    configFileName = getConfigPath() + "\\" + "datarule.properties";
    return configFileName;
  }
}
