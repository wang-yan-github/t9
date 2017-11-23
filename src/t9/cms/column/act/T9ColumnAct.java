package t9.cms.column.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import t9.cms.column.data.T9CmsColumn;
import t9.cms.column.logic.T9ColumnLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9ColumnAct {

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
    String stationId = request.getParameter("stationId");
    if (T9Utility.isNullorEmpty(stationId)) {
      stationId = "0";
    }
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      String data = logic.getTemplateArticle(dbConn, stationId);
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
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getInfomation(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String stationId = request.getParameter("stationId");
    String parentId = request.getParameter("parentId");
    if (T9Utility.isNullorEmpty(stationId) || "null".equals(stationId)) {
      stationId = "0";
    }
    if (T9Utility.isNullorEmpty(parentId) || "null".equals(parentId)) {
      parentId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      StringBuffer data = logic.getInfomation(dbConn, Integer.parseInt(stationId), Integer.parseInt(parentId));
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
  * CMS栏目 添加
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsColumn column = (T9CmsColumn)T9FOM.build(request.getParameterMap());
      T9ColumnLogic logic = new T9ColumnLogic();
      int seqId = logic.addColumn(dbConn, column, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "{maxSeqId:\"" + seqId + "\",extData:{visitUser:\"0||\",editUser:\"0||\",newUser:\"0||\",delUser:\"0||\",relUser:\"0||\",editUserContent:\"0||\",approvalUserContent:\"0||\",releaseUserContent:\"0||\",recevieUserContent:\"0||\",orderContent:\"0||\"}}");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 得到模板的所有类型

   * 根据seqId（codeClass） 得到所有的codeItem
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getColumnTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String id = request.getParameter("id");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      String data = "";
      if (!T9Utility.isNullorEmpty(id) && !id.equals("0")) {
        String idArry[] = id.split(",");
        if (idArry != null && idArry.length > 0) {
          data = logic.getColumnTree(dbConn, idArry[0], idArry[1], person);
        }
      } else {
        data = logic.getStationTree(dbConn, person);
      }
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
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getColumnDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      StringBuffer data = logic.getColumnDetailLogic(dbConn, Integer.parseInt(seqId));
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
   * CMS栏目 修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsColumn column  = (T9CmsColumn) T9FOM.build(request.getParameterMap()); 
      T9ColumnLogic logic = new T9ColumnLogic();
      logic.updateColumn(dbConn, column);
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
   * CMS下级栏目 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getColumnList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int seqId = 0;
    String seqIdStr = (String)request.getParameter("seqId");
    if(!T9Utility.isNullorEmpty(seqIdStr)){
      seqId = Integer.parseInt(seqIdStr);
    }
    String flag = (String)request.getParameter("flag");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ColumnLogic logic = new T9ColumnLogic();
      String data = logic.getColumnList(dbConn, request.getParameterMap(), person, seqId, flag);
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
   * 删除栏目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteColumn(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      int data = logic.deleteColumn(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+data+"\"");
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
      T9ColumnLogic logic = new T9ColumnLogic();
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
   * 调序
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toSort(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String toSeqId = request.getParameter("toSeqId");
    String flag = request.getParameter("flag");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    if (T9Utility.isNullorEmpty(toSeqId)) {
      toSeqId = "0";
    }
    if (T9Utility.isNullorEmpty(flag)) {
      flag = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      logic.toSort(dbConn, Integer.parseInt(seqId), Integer.parseInt(toSeqId), Integer.parseInt(flag));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 验证路径是否存在
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkPath(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String stationId = request.getParameter("stationId");
    String parentId = request.getParameter("parentId");
    String seqId = request.getParameter("seqId");
    String columnPath = request.getParameter("columnPath");
    if (T9Utility.isNullorEmpty(stationId) || "null".equals(stationId)) {
      stationId = "0";
    }
    if (T9Utility.isNullorEmpty(parentId) || "null".equals(parentId)) {
      parentId = "0";
    }
    if (T9Utility.isNullorEmpty(seqId) || "null".equals(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ColumnLogic logic = new T9ColumnLogic();
      int data = logic.checkPath(dbConn, Integer.parseInt(stationId), Integer.parseInt(parentId), Integer.parseInt(seqId), columnPath);
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
      T9ColumnLogic logic = new T9ColumnLogic();
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
  
  
  /**
   * 获取栏目列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getColumns(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Connection dbConn = null;
	    int stationId = Integer.parseInt(request.getParameter("stationId"));
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      dbConn = requestDbConn.getSysDbConn();
	      T9ColumnLogic logic = new T9ColumnLogic();
	      String data = logic.getColumnList(dbConn, stationId);
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功！");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp";
	  }
}
