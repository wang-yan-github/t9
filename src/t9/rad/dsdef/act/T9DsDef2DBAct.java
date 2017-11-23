package t9.rad.dsdef.act;


import java.io.PrintWriter;
import java.sql.Connection;

import javax.print.DocFlavor.STRING;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.rad.dsdef.logic.T9DsDefLogic;
import t9.rad.dsdef.logic.T9DsDefLogic2Db;

/**
 * 用于生成数据字典的物理结构
 * @author Think
 *
 */
public class T9DsDef2DBAct {

  /**
   *生成物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toPhysicsDb(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableNo = request.getParameter("tableNo");
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      ddl.createPhyics(dbConn, tableNo);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构创建成功!");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构创建失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   *生成物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String tabIsExist(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableName = request.getParameter("tableName");
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      boolean isExist = ddl.isExist(dbConn, tableName);
      String data = "0";
      if(isExist){
        data = "1";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构创建失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   *生成物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String dropTab(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String tableName = request.getParameter("tableName");
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      ddl.dropTabLogic(dbConn, tableName);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构删除成功!");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构删除失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 抽取表的物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPhysicsDbInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String tableName = request.getParameter("tableName");
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      String data = ddl.getPhysicsDbInfo(dbConn,tableName).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构删除失败!");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 抽取表的物理结构
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPhysicsDbInfo2(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String tableName = request.getParameter("tableName");
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      String data = ddl.getPhysicsDbInfo2(dbConn,tableName);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
      pw.close();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "物理结构删除失败!");
      throw ex;
    }
    return null;
  }
  
  public String isExistForTab(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try {
      String tableName = request.getParameter("tableName");
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DsDefLogic2Db ddl = new T9DsDefLogic2Db();
      String isExist  = ddl.isExistForTab(dbConn, tableName);
      String data = "{\"isExist\":\"" + isExist + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
