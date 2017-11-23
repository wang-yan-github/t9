package t9.core.esb.server.sysConfig.act;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.sysConfig.data.ClientConfig;
import t9.core.esb.server.sysConfig.data.SysConfig;
import t9.core.esb.server.sysConfig.logic.SysConfigLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

public class SysConfigAct {
  private SysConfigLogic logic = new SysConfigLogic();
  /**
   * 读取系统配置
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSysConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SysConfig sysConfig = (SysConfig)this.logic.getSysConfigLogic(dbConn , request);
      StringBuffer data = T9FOM.toJson(sysConfig);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSysConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = T9FOM.buildMap(request.getParameterMap());
    String contexPath = request.getContextPath();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.updateSysConfigLogic(dbConn , request , map);
      
      //向状态表插入配置文件下载数据
      //T9EsbServerLogic serlogic = new T9EsbServerLogic();
      //serlogic.broadcastConfig(dbConn, request.getSession().getServletContext().getRealPath(File.separator) + "WEB-INF\\config\\esbconfig.properties" , "" , "");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    response.sendRedirect(contexPath + "/core/esb/server/user/success.jsp");
    return null;
  }
  
  public String getClientConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      ClientConfig clientConfig = (ClientConfig)this.logic.getClientConfigLogic(dbConn , request);
      StringBuffer data = T9FOM.toJson(clientConfig);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateClientConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = T9FOM.buildMap(request.getParameterMap());
    String contexPath = request.getContextPath();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.updateClientConfigLogic(dbConn , request , map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    response.sendRedirect(contexPath + "/core/esb/server/user/success.jsp");
    return null;
  }
}
