package t9.core.funcs.setdescktop.userinfo.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;  
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.ContainerServlet;

import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.setdescktop.setports.data.T9Port;
import t9.core.funcs.setdescktop.shortcut.logic.T9ShortcutLogic;
import t9.core.funcs.setdescktop.theme.logic.T9ThemeLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.ispirit.communication.T9MsgPusher;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.menu.data.T9SysMenu;

public class T9UserinfoAct {
  
  public String getOnStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9UserinfoLogic logic = new T9UserinfoLogic();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String onStatus = user.getOnStatus();
      String sex = user.getSex();
      if (T9Utility.isNullorEmpty(sex)) {
        sex = "1";
      }
      
      if (T9Utility.isNullorEmpty(onStatus)){
        onStatus = "1";
      }
      
      PrintWriter pw = response.getWriter();
      String data = "{status:\"" + onStatus + "\",sex:\"" + sex + "\"}";
      pw.println(data);
      pw.flush();
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String updateUserParam(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9UserinfoLogic logic = new T9UserinfoLogic();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String key = request.getParameter("name");
      String value = request.getParameter("value");
      
      if (T9Utility.isNullorEmpty(key) || T9Utility.isNullorEmpty(value)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "传递参数不正确");
        return "/core/inc/rtjson.jsp";
      }
      
      Map<String, String> map = new HashMap<String, String>();
      map.put(key, value);
      logic.addUserParam(dbConn, map, user);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getMyStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String myStatus = user.getMyStatus();
      
      if (myStatus == null){
        myStatus = "";
      }
      
      PrintWriter pw = response.getWriter();
      pw.println(myStatus);
      pw.flush();
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String modifyOnStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String onStatus = request.getParameter("onStatus");
    Connection dbConn = null;
    
    try {
      T9UserinfoLogic logic = new T9UserinfoLogic();
      if (onStatus == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递状态值!");
        return "/core/inc/rtjson.jsp";
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      if ("1".equals(logic.getSecOnStatus(dbConn))) {
        logic.modifyOnStatus(dbConn, user.getSeqId(), onStatus);
      }
      synchronized(T9SystemService.onlineSync) {
        logic.modifyStatusUserOnline(dbConn, user.getSeqId(), onStatus);
        user.setOnStatus(onStatus);
        dbConn.commit();
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String modifyMyStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String myStatus = request.getParameter("myStatus");
    Connection dbConn = null;
    try {
      T9UserinfoLogic logic = new T9UserinfoLogic();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      if (myStatus != null) {
        myStatus = T9Utility.encodeSpecial(myStatus);
      }
      
      logic.modifyMyStatus(dbConn, user.getSeqId(), myStatus);
      user.setMyStatus(myStatus);
      
      T9MsgPusher.pushMyStatus(String.valueOf(user.getSeqId()), myStatus);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
      //精灵更新组织机构使用
      T9MsgPusher.updateOrg(dbConn);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String queryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      T9UserinfoLogic logic = new T9UserinfoLogic();
      Map<String, String> map = logic.queryInfo(dbConn, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      request.setAttribute(T9ActionKeys.RET_DATA, toJson(map));
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String queryCardInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String userId = request.getParameter("userId");
    if (!T9Utility.isNumber(userId)) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "id错误");
      return "/core/inc/rtjson.jsp";
    }
      
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
        T9PersonLogic pLogic = new T9PersonLogic();
        T9Person user = pLogic.getPersonById(Integer.parseInt(userId), dbConn);
        
        T9UserinfoLogic uLogic = new T9UserinfoLogic();
      
      Map<String, String> map = uLogic.queryCardInfo(dbConn, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      request.setAttribute(T9ActionKeys.RET_DATA, toJson(map));
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateBackground(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9UserinfoLogic logic = new T9UserinfoLogic();
      String background = request.getParameter("background");
      if (background == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "参数传递错误!");
        return "/core/inc/rtjson.jsp";
      }
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map<String, String> map = T9FOM.json2Map(user.getParamSet());
      map.put("desktopBg", background);
      
      logic.addUserParam(dbConn, map, user);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public static String toJson(Map<String,String> m) throws Exception{
    StringBuffer sb = new StringBuffer("{");
    for (Iterator<Entry<String,String>> it = m.entrySet().iterator(); it.hasNext();){
      Entry<String,String> e = it.next();
      sb.append("\"");
      sb.append(e.getKey());
      sb.append("\":\"");
      String value = e.getValue();
      if (value == null) {
        value = "";
      }
      sb.append(value.trim());
      sb.append("\",");
    }
    if (sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("}");
    return sb.toString();
  }
}