package t9.cms.station.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.station.data.T9CmsStation;
import t9.cms.station.logic.T9StationLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9StationAct {

  /**
   * 得到模板的所有类型

   * 根据seqId（codeClass） 得到所有的codeItem
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int stationId = 0;
    String stationIdStr = request.getParameter("stationId");
    if (T9Utility.isInteger(stationIdStr)) {
      stationId = Integer.parseInt(stationIdStr);
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      String data = logic.getTemplate(dbConn, stationId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
  * CMS站点 添加
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addStation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsStation station = (T9CmsStation)T9FOM.build(request.getParameterMap());
      T9StationLogic logic = new T9StationLogic();
      logic.addStation(dbConn, station, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * CMS站点 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getStationList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9StationLogic logic = new T9StationLogic();
      String data = logic.getStationList(dbConn, request.getParameterMap(), person);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getStationDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      StringBuffer data = logic.getStationDetailLogic(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * CMS站点 修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsStation station  = (T9CmsStation) T9FOM.build(request.getParameterMap()); 
      T9StationLogic logic = new T9StationLogic();
      logic.updateStation(dbConn, station);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除站点
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteStation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      logic.deleteStation(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 发布
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toRelease(HttpServletRequest request, HttpServletResponse response) throws Exception {
    
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      int data = logic.toReleaseStart(dbConn, Integer.parseInt(seqId), true);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "发布失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 发布
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String stationPath = request.getParameter("stationPath");
    if (T9Utility.isNullorEmpty(seqId) || "null".equals(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      int data = logic.checkPath(dbConn, Integer.parseInt(seqId), stationPath);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "发布失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 站点预览
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toSee(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId) || "null".equals(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      String data = logic.getPath(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "预览失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSchedule(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      T9StationLogic logic = new T9StationLogic();
      String data = logic.getSchedule();

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取失败！");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 更新用户访问数
   * 2013-5-13
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAndUpdateVisitedCount(HttpServletRequest request, HttpServletResponse response) throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      int visitedCount = logic.getAndUpdateVisitedCount(dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "{visitedCount:"+visitedCount+"}");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取失败！");
      throw e;
    }
    return "/cms/inc/rtrootjson.jsp";
  }
  
  /**
   * 获取访问数量
   * 2013-5-15
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getVisitedCount(HttpServletRequest request, HttpServletResponse response) throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9StationLogic logic = new T9StationLogic();
      int visitedCount = logic.getVisitedCount(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, "{visitedCount:"+visitedCount+"}");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取失败！");
      throw e;
    }
    return "/cms/inc/rtrootjson.jsp";
  }
}
