package t9.core.funcs.mysqldb.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipOutputStream;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.mysqldb.data.T9MySqlTabInfo;
import t9.core.funcs.mysqldb.logic.T9MySqlDBLogic;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;

public class T9MySqldbAct {
/**
 * 
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String listTableInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      List<T9MySqlTabInfo> data = mydbl.getTableInfo(dbConn);
      request.setAttribute("tabInfo", data);
    } catch (Exception ex) {
      throw ex;
    }
    return "/core/funcs/mysqldb/list.jsp";
  }
 /**
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String importSql(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      ArrayList<String> data = mydbl.importSql(dbConn, fileForm.getInputStream("sql_file"));
      request.setAttribute("warring", data);
    } catch (Exception ex) {
      throw ex;
    }
    return "/core/funcs/mysqldb/importInfo.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String doaction(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tables = request.getParameter("tables");
      String action = request.getParameter("action");
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      StringBuffer data = mydbl.getActionMrsg(dbConn, action, tables);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"取出数据失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDbNames(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      StringBuffer data = mydbl.getDataBaseNames(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"取出数据失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String backUp(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String[] dbName = request.getParameterValues("dbName");
      String backUpPath = request.getParameter("backUpDir");
      String dbNames = "";
      if(dbName != null){
        for (int i = 0; i < dbName.length; i++) {
          if(!"".equals(dbNames)){
            dbNames += ",";
          }
          dbNames += dbName[i];
        }
      }
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      StringBuffer data = mydbl.backup(dbConn, dbNames, backUpPath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"取出数据失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBackUpTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      StringBuffer data = mydbl.getBackUpTask(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"取出数据失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateOfficeTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      mydbl.updateOfficeTask(dbConn, Integer.valueOf(seqId), request.getParameterMap());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"热备份定时设置成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"热备份定时设置失败：" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String export(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tables = request.getParameter("tables");
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      String fileName = URLEncoder.encode( "1.zip","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      ZipOutputStream zos = new ZipOutputStream(ops);
      zos.setEncoding("GBK");
      mydbl.exportMysqlTable(dbConn, tables,zos);
      zos.flush();
      zos.close();
      ops.flush();
    } catch (Exception ex) {
      throw ex;
    }
    return null;
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String userRepair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      synchronized(T9SystemService.onlineSync) {
        mydbl.clearOnLineUser(dbConn);
        dbConn.commit();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"在线人数已修正！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"在线人数修正失败：" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String userRepairById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIds = request.getParameter("userIds");
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      if(userIds.trim().endsWith(",")){
        userIds = userIds.trim().substring(0, userIds.trim().length() - 1);
      }
      synchronized(T9SystemService.onlineSync) {
        mydbl.clearOnLineUser(dbConn,userIds);
        dbConn.commit();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"强制离线操作完成！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"强制离线操作失败：" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String checkDb(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      T9MySqlDBLogic mydbl = new T9MySqlDBLogic();
      boolean result = mydbl.checkDb();
      String data = "{isMysql:" + result + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG,"数据库检查失败：" + ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
