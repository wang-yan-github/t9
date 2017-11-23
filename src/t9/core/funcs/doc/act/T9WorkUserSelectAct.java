package t9.core.funcs.doc.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.logic.T9FlowUserSelectLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9WorkUserSelectAct {
  private static Logger log = Logger
  .getLogger("t9.core.funcs.doc.act.T9WorkUserSelectAct");
  public String getOpUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String prcsChooseStr = request.getParameter("prcsChoose");
    String userFilterStr = request.getParameter("userFilter");
    String deptIdStr = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int flowId = Integer.parseInt(flowIdStr);
      int userFilter = 0 ;
      if(userFilterStr != null && !"".equals(userFilterStr)){
        userFilter = Integer.parseInt(userFilterStr);
      }
      int deptId = 0 ;
      String msrg = "全部经办人";
      if(deptIdStr != null  && !"".equals(deptIdStr)){
        deptId = Integer.parseInt(deptIdStr);
        T9DeptLogic deptLogic = new T9DeptLogic();
        msrg = deptLogic.getNameById(deptId , dbConn);
      }
      int prcsChoose = Integer.parseInt(prcsChooseStr);
      T9FlowUserSelectLogic flowRunLogic = new T9FlowUserSelectLogic(userFilter);
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + flowRunLogic.getOpUserJson(deptId , prcsChoose , flowId , loginUser , dbConn) + "]");
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doSearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String prcsChooseStr = request.getParameter("prcsChoose");
    String userFilterStr = request.getParameter("userFilter");
    String userName = request.getParameter("userName");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int flowId = Integer.parseInt(flowIdStr);
      int userFilter = 0 ;
      if(userFilterStr != null && !"".equals(userFilterStr)){
        userFilter = Integer.parseInt(userFilterStr);
      }
      String msrg = "查询";
      int prcsChoose = Integer.parseInt(prcsChooseStr);
      T9FlowUserSelectLogic flowRunLogic = new T9FlowUserSelectLogic(userFilter);
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + flowRunLogic.doSearch(userName , prcsChoose , flowId , loginUser , dbConn) + "]");
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getUserByRole(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String prcsChooseStr = request.getParameter("prcsChoose");
    String userFilterStr = request.getParameter("userFilter");
    String sRoleId = request.getParameter("roleId");
    int roleId = Integer.parseInt(sRoleId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int flowId = Integer.parseInt(flowIdStr);
      int userFilter = 0 ;
      if(userFilterStr != null && !"".equals(userFilterStr)){
        userFilter = Integer.parseInt(userFilterStr);
      }
      String msrg = "查询";
      int prcsChoose = Integer.parseInt(prcsChooseStr);
      T9FlowUserSelectLogic flowRunLogic = new T9FlowUserSelectLogic(userFilter);
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
      request.setAttribute(T9ActionKeys.RET_DATA, "{principalRole:[" + flowRunLogic.getUserByRole(roleId, prcsChoose, flowId, user, dbConn) 
          + "],supplementRole:["
          + flowRunLogic.getUserBySupplementRole(roleId, prcsChoose, flowId, user, dbConn) + "]}");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getGroupUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String prcsChooseStr = request.getParameter("prcsChoose");
    String userFilterStr = request.getParameter("userFilter");
    String sGroupId = request.getParameter("groupId");
    int groupId = Integer.parseInt(sGroupId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int flowId = Integer.parseInt(flowIdStr);
      int userFilter = 0 ;
      if(userFilterStr != null && !"".equals(userFilterStr)){
        userFilter = Integer.parseInt(userFilterStr);
      }
      String msrg = "查询";
      int prcsChoose = Integer.parseInt(prcsChooseStr);
      T9FlowUserSelectLogic flowRunLogic = new T9FlowUserSelectLogic(userFilter);
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + flowRunLogic.getGroupUser(groupId, prcsChoose, flowId, user, dbConn) + "]");
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 自由流程取得所有人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUsers(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String flowIdStr = request.getParameter("flowId");
    String deptIdStr = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int flowId = Integer.parseInt(flowIdStr);
      
      int deptId = 0 ;
      String msrg = "全部经办人";
      if(deptIdStr != null  && !"".equals(deptIdStr)){
        deptId = Integer.parseInt(deptIdStr);
        T9DeptLogic deptLogic = new T9DeptLogic();
        msrg = deptLogic.getNameById(deptId , dbConn);
      }
      T9FlowUserSelectLogic flowRunLogic = new T9FlowUserSelectLogic();
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, msrg);
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + flowRunLogic.getUserJson(deptId , flowId, loginUser , dbConn) + "]");
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
