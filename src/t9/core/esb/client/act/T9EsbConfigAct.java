package t9.core.esb.client.act;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.service.T9WSCaller;
import t9.core.esb.frontend.services.T9EsbServiceLocal;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9EsbConfigAct {
  public T9WSCaller caller = new T9WSCaller();
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
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      //request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
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
      //this.logic.updateSysConfigLogic(dbConn , request , map);
      
      //向状态表插入配置文件下载数据
      T9EsbServerLogic serlogic = new T9EsbServerLogic();
      serlogic.broadcastConfig(dbConn, request.getSession().getServletContext().getRealPath(File.separator) + "WEB-INF\\config\\esbconfig.properties" , "" , "" );
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
      T9EsbClientConfig config = T9EsbClientConfig.builder(request.getRealPath("/") + T9EsbConst.CONFIG_PATH) ;
      StringBuffer data = T9FOM.toJson(config);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String isOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      T9EsbClientConfig config = T9EsbClientConfig.builder(request.getRealPath("/") + T9EsbConst.CONFIG_PATH) ;
      
      boolean flag = false;
      if ("1".equals(config.getLocal())) {
        T9EsbServiceLocal local = new T9EsbServiceLocal();
        flag =  local.isOnline();
      } else {
        caller.setWS_PATH(config.getWS_PATH());
        flag = caller.isOnline(config.getToken());
      }      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, flag + "");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateClientConfig(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      T9EsbClientConfig config = T9EsbClientConfig.builder(request.getRealPath("/") + T9EsbConst.CONFIG_PATH) ;
      config.setCachePath(T9Utility.null2Empty(request.getParameter("cachePath")));
      config.setESBHOST(T9Utility.null2Empty(request.getParameter("ESBHOST")));
      config.setESBPORT(T9Utility.null2Empty(request.getParameter("ESBPORT")));
      config.setESBSERVER(T9Utility.null2Empty(request.getParameter("ESBSERVER")));
      String s = request.getParameter("ESBSERVERPORT");
      config.setESBSERVERPORT(T9Utility.null2Empty(s));
      
      config.setLocal(T9Utility.null2Empty(request.getParameter("isLocal")));
      config.setUserId(T9Utility.null2Empty(request.getParameter("userId")));
      config.setPassword(T9Utility.null2Empty(request.getParameter("password")));
      config.setOAHOST(T9Utility.null2Empty(request.getParameter("OAHOST")));
      config.setOAPORT(T9Utility.null2Empty(request.getParameter("OAPORT")));
      config.setToken(T9Utility.null2Empty(request.getParameter("token")));
      config.store(request.getRealPath("/") + T9EsbConst.CONFIG_PATH);
      caller.setWS_PATH(config.getWS_PATH());
      if ("1".equals(config.getLocal())) {
        T9EsbServiceLocal local = new T9EsbServiceLocal();
        local.config(config.getESBSERVER(), Integer.parseInt(config.getESBSERVERPORT()), config.getUserId(), config.getPassword(), config.getWebserviceUri(), config.getCachePath() , config.getLocal());
      } else {
        caller.config(config.getESBSERVER(), Integer.parseInt(config.getESBSERVERPORT()), config.getUserId(), config.getPassword(), config.getWebserviceUri(), config.getCachePath() , config.getToken());
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
