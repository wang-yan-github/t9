package t9.core.funcs.setdescktop.shortcut.act;

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

import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;

import t9.core.funcs.setdescktop.shortcut.logic.T9ShortcutLogic;
import t9.core.funcs.setdescktop.theme.logic.T9ThemeLogic;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.data.T9Menu;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.menu.data.T9SysMenu;

public class T9ShortcutAct {
  
  //桌面模块-菜单快捷组的最大记录条数
  public final static int MAX_MODULE_COUNT = 18;
  private T9ShortcutLogic logic = new T9ShortcutLogic();
  
  /**
   * 获取所有菜单,其中:
   * 1.selected数组:已经设置为菜单快捷项的菜单信息
   * 2.disselected数组:尚未设置为菜单快捷项的菜单信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFuncs(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      List<String> menuList = T9SystemAct.listUserMenu(dbConn, user);
      
      List<Map<String,String>> select = new ArrayList<Map<String,String>>();
      List<Map<String,String>> disSelect = new ArrayList<Map<String,String>>();
      String shortcut = user.getShortcut();
      if (shortcut == null){
        shortcut = "";
      }
      
      List<String> shortcutList = Arrays.asList(shortcut.split(","));
      
      String menuStr = menuList.toString();
      
      for (String s : menuList){
        try {
          int id = Integer.parseInt(s);
          if (s.trim().length() == 2){
            Map<String,String> menu = this.logic.queryMenu(dbConn, id);
            if (menu == null){
              continue;
            }
            menu.put("isParent", "1");
            disSelect.add(menu);
          }
          else if (!menuStr.matches(".*[ ,\\[]" + s + "\\d+.*")){
            Map<String,String> menu = this.logic.queryFunc(dbConn, id);
            if (menu != null){
              menu.put("parentId", s.substring(0, 2));
              if (shortcutList.contains(s)){
                select.add(menu);
                //按照快捷菜单项排序的标志量
                menu.put("sort", String.valueOf(shortcutList.indexOf(s)));
              }
              else{
                disSelect.add(menu);
              }
            }
          }
        } catch (NumberFormatException e) {
          continue;
        }
      }
      
      Collections.sort(select, new Comparator<Map<String, String>>() {
        public int compare(Map<String, String> arg0, Map<String, String> arg1) {
          int a0 = -1;
          int a1 = -1;
          try {
            a0 = Integer.parseInt(arg0.get("sort"));
            a1 = Integer.parseInt(arg1.get("sort"));
          } catch (NumberFormatException e) {
            
          }
          return a0 - a1;
        }
      });
      
      StringBuffer selectedSb = new StringBuffer("selected:[");
      StringBuffer disselectedSb = new StringBuffer("disselected:[");
      
      for (Map<String,String> m : select){
        selectedSb.append(this.toJson(m));
        selectedSb.append(",");
      }
      if (selectedSb.charAt(selectedSb.length() - 1) == ','){
        selectedSb.deleteCharAt(selectedSb.length() - 1);
      }
      selectedSb.append("]");
      
      for (Map<String,String> m : disSelect){
        disselectedSb.append(this.toJson(m));
        disselectedSb.append(",");
      }
      if (disselectedSb.charAt(disselectedSb.length() - 1) == ','){
        disselectedSb.deleteCharAt(disselectedSb.length() - 1);
      }
      disselectedSb.append("]");
      
      StringBuffer sb = new StringBuffer("{");
      sb.append(selectedSb);
      sb.append(",");
      sb.append(disselectedSb);
      sb.append("}");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 获取所有菜单,其中:
   * 1.selected数组:已经设置为菜单快捷项的菜单信息
   * 2.disselected数组:尚未设置为菜单快捷项的菜单信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFuncsAll(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      List<String> menuList = T9SystemAct.listUserMenu(dbConn, user);
      
      List<Map<String,String>> select = new ArrayList<Map<String,String>>();
      List<Map<String,String>> disSelect = new ArrayList<Map<String,String>>();
      String shortcut = user.getShortcut();
      if (shortcut == null){
        shortcut = "";
      }
      
      List<String> shortcutList = Arrays.asList(shortcut.split(","));
      
      String menuStr = menuList.toString();
      
      for (String s : menuList){
        try {
          int id = Integer.parseInt(s);
          if (s.trim().length() == 2){
            Map<String,String> menu = this.logic.queryMenu(dbConn, id);
            if (menu == null){
              continue;
            }
            menu.put("isParent", "1");
            disSelect.add(menu);
          }
          else if (!menuStr.matches(".*[ ,\\[]" + s + "\\d+.*")){
            Map<String,String> menu = this.logic.queryFunc(dbConn, id);
            if (menu != null){
              menu.put("parentId", s.substring(0, 2));
              disSelect.add(menu);
            }
          }
        } catch (NumberFormatException e) {
          continue;
        }
      }
      
      StringBuffer selectedSb = new StringBuffer("selected:[");
      StringBuffer disselectedSb = new StringBuffer("disselected:[");
      
      for (Map<String,String> m : select){
        selectedSb.append(this.toJson(m));
        selectedSb.append(",");
      }
      if (selectedSb.charAt(selectedSb.length() - 1) == ','){
        selectedSb.deleteCharAt(selectedSb.length() - 1);
      }
      selectedSb.append("]");
      
      for (Map<String,String> m : disSelect){
        disselectedSb.append(this.toJson(m));
        disselectedSb.append(",");
      }
      if (disselectedSb.charAt(disselectedSb.length() - 1) == ','){
        disselectedSb.deleteCharAt(disselectedSb.length() - 1);
      }
      disselectedSb.append("]");
      
      StringBuffer sb = new StringBuffer("{");
      sb.append(selectedSb);
      sb.append(",");
      sb.append(disselectedSb);
      sb.append("}");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 修改菜单快捷项
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String modifyShortCut(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String shortCut = request.getParameter("sortCut");
    Connection dbConn = null;
    try {
      if ("cancel".equals(shortCut)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
        return "/core/inc/rtjson.jsp";
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      this.logic.modifyShortCut(dbConn, user.getSeqId(), shortCut);
      user.setShortcut(shortCut);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
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
      if (null == shortcut || "".equals(shortcut) || !shortcut.contains(",")){
        pw.println("[]");
        pw.flush();
      }
      else{
        StringBuffer sb = new StringBuffer("[");
        for (String s : user.getShortcut().split(",")){
          try {
            T9Menu menu = this.logic.queryShortcut(dbConn, s);
            
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
  
  /**
   * 查询菜单快捷项(Ext.tree.treeloader使用)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listShortCut4Loader(HttpServletRequest request, HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      PrintWriter pw = response.getWriter();
      String shortcut = user.getShortcut();
      if (null == shortcut || "".equals(shortcut) || !shortcut.contains(",")){
        pw.println("{records:[]}");
        pw.flush();
      }
      else{
        StringBuffer sb = new StringBuffer("{records:[");
        for (String s : user.getShortcut().split(",")){
          try {
            Map<String,String> map = this.logic.queryFunc(dbConn, Integer.parseInt(s));
            if (map == null){
              continue;
            }
            map.put("icon", request.getContextPath() + map.get("icon"));
            sb.append(this.toJson(map));
            sb.append(",");
          } catch (NumberFormatException e) {
            continue;
          }
        }
        
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]}");
        
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
  
  /**
   * 查询菜单快捷项(桌面模块-菜单快捷组使用)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getModule(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      String contextPath = request.getContextPath();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String shortcut = user.getShortcut();
      
      StringBuffer sb = new StringBuffer("[");
      
      if (null == shortcut || "".equals(shortcut) || !shortcut.contains(",")){
        sb.append("]");
      }
      else{
        String[] shortCutList = null;
        
        //截取前最大记录数的数据
        if (user.getShortcut().split(",").length > T9ShortcutAct.MAX_MODULE_COUNT){
          shortCutList = Arrays.copyOfRange(user.getShortcut().split(","), 0, T9ShortcutAct.MAX_MODULE_COUNT);
        }
        else{
          shortCutList = user.getShortcut().split(",");
        }
        
        for (String s : shortCutList){
          try {
            Map<String,String> map = this.logic.queryFunc4Module(dbConn, Integer.parseInt(s));
            if (map == null){
              continue;
            }
            String icon = map.get("icon");
            if (T9Utility.isNullorEmpty(icon)) {
              icon = "edit.gif";
            }
            map.put("url", T9SystemAct.parseMenuUrl(map.get("url"), contextPath, request));
            String url = map.get("url");
            map.put("icon", request.getContextPath() + T9SystemAct.IMAGE_PATH + map.get("icon"));
            sb.append(this.toJson(map));
            sb.append(",");
          } catch (NumberFormatException e) {
            continue;
          }
        }
        
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
        
      }
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询桌面属性");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  private String toJson(Map<String,String> m) throws Exception{
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
}