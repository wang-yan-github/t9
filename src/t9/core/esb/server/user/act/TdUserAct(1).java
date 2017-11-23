package t9.core.esb.server.user.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.server.logic.T9EsbServerLogic;
import t9.core.esb.server.user.data.TdUser;
import t9.core.esb.server.user.logic.TdUserLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
public class TdUserAct {
  private TdUserLogic logic = new TdUserLogic();
  
  /**
   * 新建用户
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String addUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map map = T9FOM.buildMap(request.getParameterMap());
    String contexPath = request.getContextPath();
    Connection dbConn = null;
    int status = 0;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      status = this.logic.addUserLogic(dbConn, map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    if(status == 1)
      response.sendRedirect(contexPath + "/core/esb/server/user/success.jsp");
    else if(status == -1)
      response.sendRedirect(contexPath + "/core/esb/server/user/failed.jsp");
    return null;
  }
  
  /**
   * 用户列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      ServletContext sc = request.getSession().getServletContext();
      String listenUser = T9Utility.null2Empty((String)sc.getAttribute("listenUser"));
      String data = this.logic.getUserListLogic(dbConn, request.getParameterMap() , listenUser , request.getParameter("userCode") , request.getParameter("userName"));
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
  public String stopMoniter(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String userCode = request.getParameter("userCode");
      ServletContext sc = request.getSession().getServletContext();
      String listenUser = T9Utility.null2Empty((String)sc.getAttribute("listenUser"));
      
      if (!T9Utility.isNullorEmpty(userCode)) {
        if (userCode.equals(listenUser)) {
          sc.removeAttribute("listenUser");
        }
      } else {
        sc.removeAttribute("listenUser");
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "对用户：" +  userCode + "请求的监控已移出");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String moniter(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      String userCode = request.getParameter("userCode");
      ServletContext sc = request.getSession().getServletContext();
      if (!T9Utility.isNullorEmpty(userCode)) {
        sc.setAttribute("listenUser", userCode);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "用户：" +  userCode + "请求已在监控");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbServerLogic logic = new T9EsbServerLogic();
      String message = request.getParameter("message");
      logic.addSysmsg(dbConn, "", message, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String getUserDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      TdUser user = (TdUser) this.logic.getUserDetailLogic(dbConn, Integer.parseInt(seqId));
      String str = logic.getEsbClientMessage(dbConn, Integer.parseInt(seqId));
      StringBuffer data = T9FOM.toJson(user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString() + ",list:" + str);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改用户
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public String updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Map map = T9FOM.buildMap(request.getParameterMap());
    String contexPath = request.getContextPath();
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.updateUserLogic(dbConn, map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    response.sendRedirect(contexPath + "/core/esb/server/user/success.jsp");
    return null;
  }
  
  /**
   * 删除用户
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.deleteUserLogic(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取单位员工用户名称
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userIdStr");
      String data = this.logic.getUserNameLogic(dbConn, userIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 清空日志
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int userId = Integer.parseInt(request.getParameter("userId"));
      this.logic.deleteUserMessage(dbConn, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
