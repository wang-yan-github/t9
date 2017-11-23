package t9.core.frame.act;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.frame.logic.T9ClassicInterfaceLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.picture.act.T9ImageUtil;
import t9.core.funcs.setdescktop.shortcut.logic.T9ShortcutLogic;
import t9.core.funcs.setdescktop.syspara.logic.T9SysparaLogic;
import t9.core.funcs.setdescktop.userinfo.logic.T9UserinfoLogic;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.data.T9Menu;
import t9.core.funcs.system.interfaces.data.T9InterFaceCont;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9ClassicInterfaceAct {
  /**
   * 获取ietitle
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getInterfaceInfo(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    Connection dbConn = null;
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9ClassicInterfaceLogic logic = new T9ClassicInterfaceLogic();
      Map<String, String> map = logic.getInterfaceInfo(dbConn);
      
      if (map == null) {
        map = new HashMap<String, String>();
      }
      
      if (T9Utility.isNullorEmpty(map.get("title"))) {
        map.put("title", T9SysProps.getString("productName"));
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA, T9FOM.toJson(map).toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
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
      
      T9ClassicInterfaceLogic logic = new T9ClassicInterfaceLogic();
      Map<String, String> map = logic.queryInfo(dbConn, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询桌面属性");
      request.setAttribute(T9ActionKeys.RET_DATA, String.valueOf(T9FOM.toJson(map)));
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 查询菜单快捷项


   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listShortCut(HttpServletRequest request, HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      String contextPath = request.getContextPath();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      PrintWriter pw = response.getWriter();
      String shortcut = user.getShortcut();
      List<String> list = T9SystemAct.listUserMenu(dbConn, user);
      if (null == shortcut || "".equals(shortcut) || !shortcut.contains(",")){
        pw.println("[]");
        pw.flush();
      }
      else{
        StringBuffer sb = new StringBuffer("[");
        for (String s : user.getShortcut().split(",")){
          try {
            if (!list.contains(s)) {
              continue;
            }
            T9ShortcutLogic logic = new T9ShortcutLogic();
            T9Menu menu = logic.queryShortcut(dbConn, s);
            
            if (menu != null && menu.getUrl() != null){
              T9SystemAct.parseMenuIcon(menu);
              menu.setUrl(T9SystemAct.parseMenuUrl(menu.getUrl(), contextPath, request));
              menu.setLeaf(1);
              sb.append(T9FOM.toJson(menu));
              sb.append(",");
            }
          } catch (NumberFormatException e) {
            continue;
          }
        }
        
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        
        pw.println(sb.toString().trim());
        pw.flush();
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
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
      String w = map.get("width");
      String h = map.get("height");
      
      int width = Integer.parseInt(w);
      int height = Integer.parseInt(h);
      
      if (width <= 10) {
        width = 10;
      }
      
      if (height <=10 ) {
        height = 10;
      }
      
      String path = T9InterFaceCont.ATTA_PATH + File.separator+ "system" + File.separator+  map.get("id")+ File.separator +  map.get("name");
      String newPath = path.replaceAll("\\..*", width + "-" + height + ".jpg");
      //map.put("path", path.replace("\\", "\\\\"));
      //String data = this.toJson(map);
      
      T9ImageUtil iu = new T9ImageUtil();
      
      if (!new File(path).exists()) {
        return "/core/styles/style" + styleIndex + "/img/banner/logo_bg.jpg";
      }
      if (!new File(newPath).exists()) {
        try {
          iu.saveImageAsUser(path, newPath, width, height);
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
      return "/core/styles/style" + styleIndex + "/img/banner/logo_bg.jpg";
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
      logic2.updateOnlineTime(dbConn, user.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, count + "");
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}