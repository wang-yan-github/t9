package t9.core.funcs.portal.act;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.portal.data.T9Port;
import t9.core.funcs.portal.data.T9PortStyle;
import t9.core.funcs.portal.data.T9Portal;
import t9.core.funcs.portal.data.T9PortalPort;
import t9.core.funcs.portal.logic.T9PortalLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;


public class T9PortalAct {
  private String sp = System.getProperty("file.separator");
  private String webPath = "core" + sp 
    + "funcs" + sp 
    + "portal" + sp 
    + "modules" + sp;
  
  /**
   * 新建模块的时候   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String newPort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String script = request.getParameter("script");
      String name = request.getParameter("title");
      String sql = request.getParameter("sql");
      String ruleList = request.getParameter("ruleList");
      String deptId = request.getParameter("deptId");
      String userId = request.getParameter("userId");
      String roleId =request.getParameter("roleId");
      deptId = (deptId == null) ? "" : deptId;
      userId = (userId == null) ? "" : userId;
      roleId = (roleId == null) ? "" : roleId;
      
      if (T9Utility.isNullorEmpty(script) || T9Utility.isNullorEmpty(name)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递数据或者信息不全");
        return "/core/inc/rtjson.jsp";
      }
      
      String sp = System.getProperty("file.separator");
      String path = request.getSession().getServletContext().getRealPath(sp) 
        + webPath 
        + name + ".js"; 
      File file = new File(path);
      if (file.exists()) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "该名称的模块已经存在,请重命名!");
        return "/core/inc/rtjson.jsp";
      }
      
      if (!T9Utility.isNullorEmpty(sql)) {
        String dataSql = "{\"sql\":\"" + sql + "\" , \"ruleList\":" + ruleList +"}";
        String dataPath = request.getSession().getServletContext().getRealPath(sp) 
          + webPath
          + "data" + sp 
          + "data.properties"; 
        try {
          File dataFile = new File(dataPath);
          if (!dataFile.exists()) {
            dataFile.createNewFile();
          }
          Map<String , String> dataMap = new HashMap();
          T9FileUtility.load2Map(dataPath, dataMap);
          dataMap.put(name, dataSql);
          Set<String> set = dataMap.keySet();
          String str = "";
          for (String key : set) {
            String value = dataMap.get(key);
            str += key + " = " + value + "\r\n";
          }
          T9FileUtility.storeString2File(dataPath, str);
        } catch (Exception ex) {
          throw ex;
        }
      }
      
      T9FileUtility.storeString2File(path, script);
      T9Port port = new T9Port();
      port.setFileName(name + ".js");
      port.setStatus(1);
      port.setDeptId(deptId);
      port.setPrivId(roleId);
      port.setUserId(userId);
      
      T9PortalLogic logic = new T9PortalLogic();
      logic.newPort(dbConn, port);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新建门户布局
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateUserPortal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String portalStr = request.getParameter("portal");
      String portalName = request.getParameter("name");
      String idStr = request.getParameter("id");
      
      int id = -1;
      try {
        id = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        
      }
      
      if (T9Utility.isNullorEmpty(portalStr)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未传递字符串");
        return "/core/inc/rtjson.jsp";
      }
      
      T9PortalLogic logic = new T9PortalLogic();
      if (id >= 0) {
        T9Portal portal = logic.queryPortal(dbConn, id);
        
        if (portal == null) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "门户id错误, 无此门户");
          return "/core/inc/rtjson.jsp";
        }
        
        String path = request.getSession().getServletContext().getRealPath(sp) 
        + webPath + "portals" + sp
        + portal.getFileName();
        
        T9FileUtility.storeString2File(path, portalStr);
      }
      else {
        /*
        if (!logic.checkPortalName(dbConn, portalName)) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "门户名称重复或者未定义");
          return "/core/inc/rtjson.jsp";
        }*/
        T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9Portal portal = logic.queryPersonalPortal(dbConn, user.getSeqId());
        if (portal != null) {
          String path = request.getSession().getServletContext().getRealPath(sp) 
          + webPath + "portals" + sp
          + portal.getFileName();
          id = portal.getSeqId();
          T9FileUtility.storeString2File(path, portalStr);
        }
        else {
          portal = new T9Portal();
          portal.setUserId(user.getSeqId() + "");
          portalName = user.getUserName() + "的个人门户";
          String guid = T9Guid.getGuid();
          String fileName = guid + ".js";
          String path = request.getSession().getServletContext().getRealPath(sp) 
          + webPath + "portals" + sp + fileName;
          
          T9FileUtility.storeString2File(path, portalStr);
          
          portal.setFileName(fileName);
          portal.setPortalName(portalName);
          id = logic.newPortal(dbConn, portal);
        }
      }
      
      String data = "{id: " + id + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加门户布局");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 检查重名
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkPortalName(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String name = request.getParameter("name");
      if ( T9Utility.isNullorEmpty(name)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        return "/core/inc/rtjson.jsp";
      }
      String query = "select * from portal where portal_name =  '" + name + "'";
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        } else  {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得门户备注
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPortalRemark(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String id = request.getParameter("id");
      String query = "select remark from portal where seq_id =  " + id ;
      Statement stm = null;
      ResultSet rs = null;
      String remark = "";
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()) {
          remark = rs.getString("remark");
          if (remark == null) {
            remark = "";
          }
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
      request.setAttribute(T9ActionKeys.RET_DATA, "'"+remark+"'");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存门户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String savePortal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String name = request.getParameter("portalName");
      String id = request.getParameter("id");
      String remark = request.getParameter("remark");
      String query = "";
      String guId = T9Guid.getGuid() + ".js";
      if (T9Utility.isNullorEmpty(id)) {
        query = "insert into" +
        		" Portal (FILE_NAME, REMARK, STATUS, USER_ID, DEPT_ID, PRIV_ID, PORTAL_NAME)" +
        		" values ('"+ guId +"' , '" + remark +"' , 1 , null , null , null , '" + name + "')"; 
      } else {
        query = "update PORTAL set PORTAL_NAME='" + name + "' , REMARK = '"+ remark +"' where seq_id=" + id;
      }
      Statement stm = null;
      try {
        stm = dbConn.createStatement();
        stm.executeUpdate(query);
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, null, null);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建模块的时候
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String script = request.getParameter("script");
      String name = request.getParameter("title");
      String sql = request.getParameter("sql");
      String ruleList = request.getParameter("ruleList");
      String deptId = request.getParameter("deptId");
      String userId = request.getParameter("userId");
      String roleId =request.getParameter("roleId");
      String type = request.getParameter("type");
      
      deptId = (deptId == null) ? "" : deptId;
      userId = (userId == null) ? "" : userId;
      roleId = (roleId == null) ? "" : roleId;
      
      String oldName = request.getParameter("oldName");
      if (T9Utility.isNullorEmpty(script) || T9Utility.isNullorEmpty(name)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递数据或者信息不全");
        return "/core/inc/rtjson.jsp";
      }
      String sp = System.getProperty("file.separator");
      String path = request.getSession().getServletContext().getRealPath(sp) 
        + webPath 
        + name + ".js"; 
      if (!name.equals(oldName)) {
        File file = new File(path);
        if (file.exists()) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "该名称的模块已经存在,请重命名!");
          return "/core/inc/rtjson.jsp";
        }
        String oldPath = request.getSession().getServletContext().getRealPath(sp) 
          + webPath 
          + oldName + ".js"; 
        File oldFile = new File(oldPath);
        if (oldFile.exists()) {
          oldFile.delete();
        }
      }
      String dataPath = request.getSession().getServletContext().getRealPath(sp) 
      + webPath
      + "data" + sp 
      + "data.properties"; 
      File dataFile = new File(dataPath);
      if (!dataFile.exists()) {
        dataFile.createNewFile();
      }
      Map<String , String> dataMap = new HashMap();
      T9FileUtility.load2Map(dataPath, dataMap);
      if ("sql".equals(type)) {
        String dataSql = "{\"sql\":\"" + sql + "\" , \"ruleList\":" + ruleList +"}";
        try {
          dataMap.put(name, dataSql);
          if (!name.equals(oldName)) {
            dataMap.remove(oldName);
          }
        } catch (Exception ex) {
          throw ex;
        }
      } else {
        dataMap.remove(oldName);
      }
      Set<String> set = dataMap.keySet();
      String str = "";
      for (String key : set) {
        String value = dataMap.get(key);
        str += key + " = " + value + "\r\n";
      }
      T9FileUtility.storeString2File(dataPath, str);
      
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      T9FileUtility.storeString2File(path, script);
      T9Port port = new T9Port();
      port.setSeqId(id);
      port.setFileName(name + ".js");
      port.setDeptId(deptId);
      port.setPrivId(roleId);
      port.setUserId(userId);
      T9PortalLogic logic = new T9PortalLogic();
      logic.updatePort(dbConn, port);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改模块权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePortPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String id = request.getParameter("portId");
      String role = request.getParameter("role");
      String dept = request.getParameter("dept");
      String user = request.getParameter("user");
      
      T9PortalLogic logic = new T9PortalLogic();
      T9Port port = new T9Port();
      port.setSeqId(Integer.parseInt(id));
      port.setUserId(user);
      port.setDeptId(dept);
      port.setPrivId(role);
      logic.updatePortPriv(dbConn, port);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除模块权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delPort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      String rootPath = request.getSession().getServletContext().getRealPath(sp) +  webPath;
      T9PortalLogic logic = new T9PortalLogic();
      logic.deletePort(dbConn, id, rootPath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除门户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delPortal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      T9PortalLogic logic = new T9PortalLogic();
      logic.deletePortal(dbConn, id);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 设置门户权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setPortalPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      T9PortalLogic logic = new T9PortalLogic();
      String role = request.getParameter("role");
      String user = request.getParameter("user");
      String dept = request.getParameter("dept");
      if (role == null) {
        role = "";
      }
      if (user == null) {
        user = "";
      }
      if (dept == null) {
        dept = "";
      }
      logic.setPortalPriv(dbConn, id , role , user ,dept);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得门户权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPortalPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      T9PortalLogic logic = new T9PortalLogic();
      String data = logic.getPortalPriv(dbConn, id );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取得权限");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 列出属于用户的所有模块,考虑了权限问题   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listPorts(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int id = -1;
      try {
        id = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        
      }
      
      if ("personal".equals(idStr)) {
        id = -2;
      }
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PortalLogic logic = new T9PortalLogic();
      String data = logic.listPorts(dbConn, user, id);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 列出门户，没有考虑权限问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listPortal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9PortalLogic logic = new T9PortalLogic();
      String data = logic.listPortal(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得模块数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getModData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String idStr = request.getParameter("id");
      int id = 0 ;
      if (T9Utility.isInteger(idStr)) {
        id = Integer.parseInt(idStr);
      }
      T9PortalLogic logic = new T9PortalLogic();
      String wPath =  request.getSession().getServletContext().getRealPath(sp) +  webPath;
      String data = logic.getModData(dbConn, id, wPath);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 列出用户拥有权限的所有模块   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listAllPorts(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String priv = user.getUserPriv();
      String privOther = user.getUserPrivOther();
      int dept = user.getDeptId();
      String deptOther = user.getDeptIdOther();
      
      Set<String> deptSet = T9PortalAct.string2Set(deptOther, String.valueOf(dept));
      Set<String> privSet = T9PortalAct.string2Set(privOther, String.valueOf(priv));
      
      T9PortalLogic logic = new T9PortalLogic();
      List<T9Port> portList = logic.listPort(dbConn, userId, deptSet, privSet);
      List<Map<String, String>> list = new ArrayList<Map<String, String>>();
      for (T9Port p : portList) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(p.getSeqId()));
        map.put("file", p.getFileName());
        list.add(map);
      }
      
      String data = "{records:"
      + logic.toJson(list) + "}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 列出用户拥有权限的所有模块   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listAllPortals(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String priv = user.getUserPriv();
      String privOther = user.getUserPrivOther();
      int dept = user.getDeptId();
      String deptOther = user.getDeptIdOther();
      
      Set<String> deptSet = T9PortalAct.string2Set(deptOther, String.valueOf(dept));
      Set<String> privSet = T9PortalAct.string2Set(privOther, String.valueOf(priv));
      
      T9PortalLogic logic = new T9PortalLogic();
      List<T9Portal> portalList = logic.listPortal(dbConn, userId, deptSet, privSet);
      List<Map<String, String>> list = new ArrayList<Map<String, String>>();
      for (T9Portal p : portalList) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(p.getSeqId()));
        map.put("file", p.getFileName());
        map.put("remark", p.getRemark());
        map.put("name", p.getPortalName());
        list.add(map);
      }
      
      String data = "{records:"
        + logic.toJson(list) + "}";
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 设置默认门户(带权限判断)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setDefaultPortal(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      
      String idStr = request.getParameter("id");
      int id = -1;
      try {
        id = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "门户参数传递错误");
        return "/core/inc/rtjson.jsp";
      }
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String priv = user.getUserPriv();
      String privOther = user.getUserPrivOther();
      int dept = user.getDeptId();
      String deptOther = user.getDeptIdOther();
      
      Set<String> deptSet = T9PortalAct.string2Set(deptOther, String.valueOf(dept));
      Set<String> privSet = T9PortalAct.string2Set(privOther, String.valueOf(priv));
      
      T9PortalLogic logic = new T9PortalLogic();
      List<T9Portal> portalList = logic.listPortal(dbConn, userId, deptSet, privSet);
      List<Map<String, String>> list = new ArrayList<Map<String, String>>();
      for (T9Portal p : portalList) {
        if (id == p.getSeqId()) {
          logic.setDefaultPortal(dbConn, user, id);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
          return "/core/inc/rtjson.jsp";
        }
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "无权限或者id传递有误!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 检查重名   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkName(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    try {
      String name = request.getParameter("title");
      if ( T9Utility.isNullorEmpty(name)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        return "/core/inc/rtjson.jsp";
      }
      String sp = System.getProperty("file.separator");
      String path = request.getSession().getServletContext().getRealPath(sp) 
        + "core" + sp 
        + "funcs" + sp 
        + "portal" + sp 
        + "modules" + sp 
        + name + ".js"; 
      File file = new File(path);
      if (file.exists()) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        return "/core/inc/rtjson.jsp";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 更新门户的模块   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePortalPorts(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
     
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String idStr = request.getParameter("id");
      int portalId = -1;
      try {
        portalId = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未传递ID或者传递有误");
        return "/core/inc/rtjson.jsp";
      }
      
      T9PortalLogic logic = new T9PortalLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9PortalPort> list = logic.listPortalPort(dbConn, portalId);

      List<Integer> idList = new ArrayList<Integer>();
      for (T9PortalPort up : list) {
        idList.add(up.getPortId());
      }
      
      Map<String, String[]> params = request.getParameterMap();
      for (T9PortalPort up : list) {
        if (params.get(String.valueOf(up.getPortId()) + "[]") == null) {
          logic.deletePortStyle(dbConn, up.getStyleId());
          logic.deletePortalPort(dbConn, up.getSeqId());
          continue;
        }
      }
      
      for (Iterator<Entry<String,String[]>> it = params.entrySet().iterator(); it.hasNext();){
        Entry<String,String[]> e = it.next();
        try {
          int id = Integer.parseInt(e.getKey().trim().replace("[]", ""));
          String[] style = e.getValue();
          if (style.length <  6) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "ID为" + e.getKey() + "的模块样式传递错误");
            return "/core/inc/rtjson.jsp";
          }
          
          List<String> temp = new ArrayList<String>(Arrays.asList(style)).subList(2, 6);
          
          if (!idList.contains(id)) {
            String guid = T9Guid.getGuid();
            T9PortStyle ps = strings2PortStyle(temp, guid);
            T9PortalPort pp = new T9PortalPort();
            pp.setContainer(style[0]);
            pp.setPortId(id);
            pp.setSortNo(Integer.parseInt(style[1]));
            pp.setStyleId(guid);
            pp.setPortalId(portalId);
            logic.addPortStyle(dbConn, ps);
            logic.addPortalPort(dbConn, pp);
          }
          else {
            T9PortalPort up = list.get(idList.indexOf(id));
            String styleId = up.getStyleId();
            up.setSortNo(Integer.parseInt(style[1]));
            T9PortStyle portStyle = strings2PortStyle(temp, styleId);
            logic.updatePortStyle(dbConn, portStyle);
            logic.updatePortalPort(dbConn, style[0], up.getSeqId(), up.getSortNo());
          }
        } catch (NumberFormatException ex) {
          continue;
        }
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 用逗号分隔的String转化为set(不重复的id串)
   * @param strs 多个String
   * @return
   */
  public static Set<String> string2Set(String ... strs) {
    Set<String> set = new HashSet<String>();
    for (String s : strs) {
      if (!T9Utility.isNullorEmpty(s)) {
        set.addAll(Arrays.asList(s.split(",")));
      }
    }
    return set;
  }
  
  /**
   * 获取模块的路径,为了避免路径上出现中文)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String viewPort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String file = request.getParameter("file");
    String idStr = request.getParameter("id");
    Connection dbConn;
    int id = -1;
    try {
      try {
        id = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
        
      }
      if (id >= 0) {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9PortalLogic logic = new T9PortalLogic();
        T9Port port = logic.queryPort(dbConn, id);
        if (port != null) {
          file = port.getFileName();
        }
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/portal/modules/" + file;
  }
  
  /**
   * 获取模块的路径,为了避免路径上出现中文)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String viewLayout(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String file = request.getParameter("file");
    
    return "/core/funcs/portal/modules/layouts/" + file;
  }
  
  private T9PortStyle strings2PortStyle(List<String> style, String guid) {
    T9PortStyle ps = new T9PortStyle();
    ps.setGuid(guid);
    ps.setWidth(style.get(0));
    ps.setHeight(style.get(1));
    try {
      ps.setPosX(Integer.parseInt(style.get(2)));
      ps.setPosY(Integer.parseInt(style.get(3)));
    } catch (NumberFormatException ex) {
      ps.setPosX(0);
      ps.setPosY(0);
    }
    return ps;
  }
  
}