package t9.core.funcs.setdescktop.syspara.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.picture.act.T9ImageUtil;
import t9.core.funcs.portal.logic.T9PortalLogic;
import t9.core.funcs.setdescktop.syspara.logic.T9SysparaLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.interfaces.data.T9InterFaceCont;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.servlet.T9SessionListener;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.auth.T9RegistUtility;
import t9.core.util.form.T9FOM;

public class T9SysparaAct {
  
  public String queryLogoutText(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysparaLogic logic = new T9SysparaLogic();
      String data = logic.queryLogoutText(dbConn);
      data = "\"" + data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "") + "\"";
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String queryStatusText(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysparaLogic logic = new T9SysparaLogic();
      String text = logic.queryStatusText(dbConn);
      String marquee = logic.queryStatusMarquee(dbConn);
      
      if (text == null) {
        text = "";
      }
      
      if (marquee == null) {
        marquee = "";
      }
      
      StringBuffer sb = new StringBuffer("{\"TEXT\":\"");
      sb.append(text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "<br>"));
      sb.append("\",\"MARQUEE\":\"");
      sb.append(marquee);
      sb.append("\"}");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String queryHeaderImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    
    int styleIndex = 1;
    Integer styleInSession = (Integer)request.getSession().getAttribute("STYLE_INDEX");
    if (styleInSession != null) {
      styleIndex = styleInSession;
    }
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysparaLogic logic = new T9SysparaLogic();
      Map<String,String> map = logic.queryHeaderImg(dbConn);
      
      String path = T9InterFaceCont.ATTA_PATH + File.separator + "system" + File.separator  + map.get("id") + File.separator + map.get("name");
      String newPath = path.replaceAll("\\..*", map.get("width") + "-" + map.get("height") + ".jpg");
      //map.put("path", path.replace("\\", "\\\\"));
      //String data = this.toJson(map);
      
      T9ImageUtil iu = new T9ImageUtil();
      
      if (!new File(path).exists()) {
        return "/core/styles/style" + styleIndex + "/img/banner/logo_bg.jpg";
      }
      if (!new File(newPath).exists()) {
        try {
          iu.saveImageAsUser(path, newPath, Integer.parseInt(map.get("width")), Integer.parseInt(map.get("height")));
        } catch (NumberFormatException e) {
          iu.saveImageAsUser(path, newPath, 300, 50);
        }
      }
      
      FileInputStream fis = new FileInputStream(newPath);
      response.setContentType("image/" + map.get("name").replaceAll(".*\\.", ""));
      OutputStream out = response.getOutputStream();
      
      byte[] b = new byte[1024];  
      int i = 0;  
      
      while((i = fis.read(b)) > 0) {  
      out.write(b, 0, i);
      }
      
      out.flush();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    } catch (FileNotFoundException ex) {
      return "/core/styles/style" + styleIndex + "/img/banner/logo_bg.jpg";
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "";
  }
  public String queryUserCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysparaLogic logic = new T9SysparaLogic();
      int count = logic.queryUserCount(dbConn);
      
      
      
      T9SystemLogic logic2 = new T9SystemLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      long start = System.currentTimeMillis();
      Map userState = T9SessionListener.getUserStateMap();
      Set<Integer> keys = userState.keySet();
      StringBuffer users = new StringBuffer("users:{");
      for (Integer k  : keys) {
        users.append("\"u-").append(k).append("\":\"").append(userState.get(k)).append("\",");
      }
      if (keys.size() > 0) {
        users.deleteCharAt(users.length() - 1);
      }
      users.append("}");
      logic2.updateOnlineTime(dbConn, user.getSeqId());
      long end = System.currentTimeMillis();
      T9Out.debug("查询在线用户数耗时:" + (end - start) );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, count + "," + users.toString());
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询天气的方法,解决ajax跨域的问题

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryWeather(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String id = request.getParameter("cityId");
      if (T9Utility.isNullorEmpty(id)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "ID错误");
        return "/core/inc/rtjson.jsp";
      }
      String weatherPath = "http://m.weather.com.cn/data/" + id + ".html";
      HttpClient client = new HttpClient();
      GetMethod getMethod = new GetMethod(weatherPath);
      //设置成了默认的恢复策略，在发生异常时候将自动重试3次，在这里你也可以设置成自定义的恢复策略
      getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
              new DefaultHttpMethodRetryHandler()); 
      //执行getMethod
      int statusCode = client.executeMethod(getMethod);
      if (statusCode != HttpStatus.SC_OK) {
        //System.err.println("Method failed: " + getMethod.getStatusLine());
      }
      byte[] responseBody = getMethod.getResponseBody();
      getMethod.releaseConnection();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, new String(responseBody, "utf-8"));
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  private Map<String, String> registInfo() {
    Map<String, String> map = new HashMap<String, String>();
    String hasReg = "1";
    if (!T9RegistUtility.hasRegisted()) {
      hasReg = "0";
      int remainDays = T9RegistUtility.remainDays();
      map.put("remainDays", String.valueOf(remainDays));
    }
    map.put("hasRegisted", hasReg);
    return map;
  }
  
  public String queryInitInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      //门户的id
      String idStr = request.getParameter("id");
      int id = -1;
      try {
        id = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        
      }
      
      if ("personal".equals(idStr)) {
        id = -2;
      }
      
      T9SysparaLogic sysParaLogic = new T9SysparaLogic();
      T9UserinfoLogic userInfoLogic = new T9UserinfoLogic();
      T9SystemLogic systemLogic = new T9SystemLogic();
      T9PortalLogic portalLogic = new T9PortalLogic();
      
      String portal = portalLogic.listPorts(dbConn, user, id);
      String title = systemLogic.getIETitle(dbConn);
      if (T9Utility.isNullorEmpty(title)) {
        title = T9SysProps.getString("productName");
      }
      Map<String, String> map = userInfoLogic.queryInfo(dbConn, user);
      Map<String, String> otherPara = getOtherPara(request);
      Map<String, String> smsPara = getSmsPara();
      
      trimStringMap(map);
      trimStringMap(otherPara);
      trimStringMap(smsPara);
      
      int count = sysParaLogic.queryUserCount(dbConn);
      String onlineRefStr = T9SysProps.getString("$ONLINE_REF_SEC");
      if (onlineRefStr == null || "".equals(onlineRefStr.trim())) {
        onlineRefStr = "3600";
      }
      
      String funcId = sysParaLogic.queryFuncId(dbConn, "控制面板");
      otherPara.put("controlId", funcId);
      StringBuffer sb = new StringBuffer("{");
      sb.append("\"userInfo\":");
      sb.append(T9FOM.toJson(map).toString());
      sb.append(",\"background\":");
      sb.append(map.get("desktopBg"));
      sb.append(",\"portal\":");
      sb.append(portal);
      sb.append(",\"browserTitle\":\"");
      sb.append(T9Utility.encodeSpecial(title));
      sb.append("\",\"onlineAmount\": {\"amount\":");
      sb.append(count);
      sb.append(",\"onlineRefStr\":");
      sb.append(onlineRefStr);
      sb.append("},\"smsPara\":");
      sb.append(T9FOM.toJson(smsPara));
      sb.append(",\"otherPara\":");
      sb.append(T9FOM.toJson(otherPara));
      sb.append(",\"logoutMsg\":");
      
      String logoutMsg = sysParaLogic.queryLogoutText(dbConn);
      sb.append("\"" + T9Utility.encodeSpecial(logoutMsg) + "\"");
      sb.append(",\"regist\":");
      sb.append(T9FOM.toJson(registInfo()));
      sb.append("}");
      
      
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public Map<String, String> getSmsPara() {
    Map<String, String> map = new HashMap<String, String>();
    String smsRef = T9SysProps.getString("$SMS_REF_SEC");
    if (smsRef == null || "".equals(smsRef.trim())) {
      smsRef = "30";
    }
    String smsCallCount = T9SysProps.getString("$SMS_REF_MAX");
    if (smsCallCount == null || "".equals(smsCallCount.trim())) {
      smsCallCount = "3";
    }
    String smsInterval = T9SysProps.getString("$SMS_CALLSOUND_INTERVAL");
    if (smsInterval == null || "".equals(smsInterval.trim())) {
      smsInterval = "3";
    }
    map.put("smsRef", smsRef);
    map.put("smsCallCount", smsCallCount);
    map.put("smsInterval", smsInterval);
    return map;
  }
  
  public Map<String, String> getOtherPara(HttpServletRequest request) {
    Map<String, String> map = new HashMap<String, String>();
    String sessionToken = (String)request.getSession().getAttribute("sessionToken");
    String statusRefStr = T9SysProps.getString("$STATUS_REF_SEC");
    if (statusRefStr == null || "".equals(statusRefStr.trim())) {
      statusRefStr = "3600";
    }
    T9SysparaLogic logic = new T9SysparaLogic();
    int remainDays = T9RegistUtility.remainDays();
    map.put("sesstionToken", sessionToken);
    map.put("statusRefStr", statusRefStr);
    map.put("remainDays", String.valueOf(remainDays));
    return map;
  }
  
  private String toJson(Map<String,String> m) throws Exception {
    StringBuffer sb = new StringBuffer("{");
    for (Iterator<Entry<String,String>> it = m.entrySet().iterator(); it.hasNext();){
      Entry<String,String> e = it.next();
      sb.append(e.getKey());
      sb.append(":\"");
      sb.append(e.getValue());
      sb.append("\",");
    }
    if (sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("}");
    return sb.toString();
  }
  
  private void trimStringMap(Map<String, String> map) {
    for (String s : map.keySet()) {
      String value = map.get(s);
      if (value != null) {
        map.put(s, map.get(s).trim());
      }
    }
  }
}