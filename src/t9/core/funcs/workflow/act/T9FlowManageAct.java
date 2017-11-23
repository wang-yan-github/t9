package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowManageLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9FlowManageAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9FlowManageAct");


  public String setPriv(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String act = request.getParameter("action");
    int flowId = Integer.parseInt(request.getParameter("flowId"));
    Connection dbConn = null;
    try {
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowManageLogic flowManage = new T9FlowManageLogic();
      if ("COMMENT".equals(act)) {
        String commentPriv1 = request.getParameter("commentPriv1");
        String commentPriv2 = request.getParameter("commentPriv2");
        int priv = 0;
        if (commentPriv1 == null && commentPriv2 != null) {
          priv = 2;
        } else if (commentPriv1 != null && commentPriv2 == null) {
          priv = 1;
        } else if (commentPriv2 != null && commentPriv1 != null) {
          priv = 3;
        }
        flowManage.setCommentPriv(flowId, priv, dbConn);
      } else {
        String privUser = request.getParameter("privUser");
        if (privUser == null) {
          privUser = "";
        }
        String privDept = request.getParameter("privDept");
        if (privDept == null) {
          privDept = "";
        }
        String role = request.getParameter("role");
        if (role == null) {
          role = "";
        }
        String privStr = privUser + "|" + privDept + "|" + role;

        flowManage.setPriv(flowId, act, privStr, dbConn);
      }
      this.setRequestSuccess(request, "设置成功 ");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getPriv(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    int flowId = Integer.parseInt(request.getParameter("flowId"));
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowManageLogic flowManage = new T9FlowManageLogic();
      String data = flowManage.getPriv(flowId, dbConn);
      this.setRequestSuccess(request, "取得成功 ", data);
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 取得超时催办提醒页面的相关信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRemindInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String toId = request.getParameter("toId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PersonLogic logic = new T9PersonLogic();
      String data = "{user:[" + logic.getPersonSimpleJson(toId, dbConn) + "]";
      T9FlowManageLogic mLogic = new T9FlowManageLogic();
      String query = "select TYPE_PRIV from SMS2_PRIV";
      String typePriv = "";
      String sms2RemindPriv = "";
      Statement stm1 = null;
      ResultSet rs1 = null;
      try {
        stm1 = dbConn.createStatement();
        rs1 = stm1.executeQuery(query);
        if (rs1.next()) {
          typePriv = rs1.getString("TYPE_PRIV");
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm1, rs1, null);
      }
      // 检查该模块是否允许手机提醒
      boolean sms2Priv = false;
      if (T9WorkFlowUtility.findId(typePriv, "7")) {
        sms2Priv = true;
      }

      data += ",sms2Priv:" + sms2Priv;
      data += "}";
      this.setRequestSuccess(request, "取得成功 ", data);
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String remindUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String toId = request.getParameter("toId");
    String sms2Remind = request.getParameter("sms2Remind");
    String content = request.getParameter("content");
    String sortId = request.getParameter("sortId");
    if (sortId == null) {
      sortId = "";
    }
    String skin = request.getParameter("skin");
    if (skin == null) {
      skin = "";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic mLogic = new T9FlowManageLogic();
      mLogic.remindUser(dbConn, toId, content, request.getContextPath(),
          loginUser.getSeqId(), sortId, skin);
      if ("on".equals(sms2Remind)) {
        T9MobileSms2Logic ms2l = new T9MobileSms2Logic();
        ms2l.remindByMobileSms(dbConn, toId, loginUser.getSeqId(), content,
            null);
      }
      this.setRequestSuccess(request, "催办超时流程短信已发送 ");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 强制结束工作流
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String endWorkFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runIdStr = request.getParameter("runIdStr");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String s = "";
      String sIsManage = request.getParameter("isManage");
      boolean isManage = false;
      if (sIsManage != null || "".equals(sIsManage)) {
        isManage = Boolean.valueOf(sIsManage);
      }
      if (runIdStr != null) {
        T9PrcsRoleUtility ru = new T9PrcsRoleUtility();
        T9FlowManageLogic manage = new T9FlowManageLogic();
        String[] runIds = runIdStr.split(",");
        for (int i = 0; i < runIds.length; i++) {
          String tmp = runIds[i];

          if (!"".equals(tmp)) {
            int runId = Integer.parseInt(tmp);
            String runRole = ru.runRole(runId, loginUser, dbConn);
            if (!loginUser.isAdminRole()
                && !T9WorkFlowUtility.findId(runRole, "3")) {
              continue;
            } else {
              manage.endWorkFlow(runId, loginUser, dbConn);
              s += runId + ",";
            }
          }
        }
      }
      if (s.endsWith(",")) {
        s = s.substring(0, s.length() - 1);
      }
      this.setRequestSuccess(request, "结束流水号为[" + s + "]的工作,操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 删除工作流
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delWorkFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runIdStr = request.getParameter("runIdStr");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String s = "";
      if (runIdStr != null) {
        T9PrcsRoleUtility ru = new T9PrcsRoleUtility();
        T9FlowManageLogic manage = new T9FlowManageLogic();
        String[] runIds = runIdStr.split(",");
        for (String tmp : runIds) {
          if (!"".equals(tmp)) {
            int runId = Integer.parseInt(tmp);
            String runRole = ru.runRole(runId, 1, loginUser, dbConn);
            boolean flag = manage.getFlag(runId, dbConn);
            if (!(T9WorkFlowUtility.findId(runRole, "2") && flag)// 不是发起人（第一步的主办人）或者已经开始流转（不只有第一步）
                && !T9WorkFlowUtility.findId(runRole, "1") // 当前用户不是系统管理员
                && !T9WorkFlowUtility.findId(runRole, "3")) { // 当前用户不是管理与监控人员
              continue;
            } else {
              boolean result = manage.delWorkFlow(runId, loginUser.getSeqId(),
                  dbConn);
              if (result) {
                s += runId + ",";
              }
            }
          }
        }
      }
      if (s.endsWith(",")) {
        s = s.substring(0, s.length() - 1);
      }
      this.setRequestSuccess(request, "删除流水号为[" + s + "]的工作,操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 恢复执行
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String restore(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String runId = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      if (!loginUser.isAdminRole()) {
        this.setRequestSuccess(request, "没有此操作权限！");
        return "/core/inc/rtjson.jsp";
      }
      boolean reslut = manage.restore(Integer.parseInt(runId), loginUser
          .getSeqId(), dbConn);
      if (!reslut) {
        this.setRequestSuccess(request, "您的恢复执行操作没有成功!");
      } else {
        this.setRequestSuccess(request, "流水号为[" + runId + "]的工作已经恢复到执行状态!");
      }
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 取得评论信息
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCommentMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sRunId = request.getParameter("runId");
    String sFlowId = request.getParameter("flowId");
    int flowId = Integer.parseInt(sFlowId);
    int runId = Integer.parseInt(sRunId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      StringBuffer sb = new StringBuffer();
      sb.append("{");
      sb.append(manage.getCommentPriv(flowId, loginUser, dbConn));
      sb.append(",prcsId:" + manage.getMaxPrcsId(runId, dbConn) + ",");
      sb.append(manage.getSmsRemind(loginUser.getSeqId(), dbConn));
      sb.append("}");
      this.setRequestSuccess(request, "取得成功！", sb.toString());
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String saveComment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sRunId = request.getParameter("runId");
    String sFlowId = request.getParameter("flowId");
    String sPrcsId = request.getParameter("prcsId");
    String content = request.getParameter("comment");
    String smsRemind = request.getParameter("smsRemind");
    int prcsId = Integer.parseInt(sPrcsId);
    int runId = Integer.parseInt(sRunId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person u = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      manage.saveComment(runId, prcsId, u.getSeqId(), u.getUserName(), content,
          smsRemind, request.getContextPath(), dbConn);
      this.setRequestSuccess(request, "操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String focus(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String sRunId = request.getParameter("runId");
    int runId = Integer.parseInt(sRunId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person u = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      String focusUser = manage.getFocusUser(runId, dbConn);
      if (T9WorkFlowUtility.findId(focusUser, String.valueOf(u.getSeqId()))) {
        this.setRequestSuccess(request, "您已经关注了此工作！");
        return "/core/inc/rtjson.jsp";
      } else {
        manage.focus(u, focusUser, runId, request.getContextPath(), dbConn);
      }
      this.setRequestSuccess(request, "操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String calFocus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sRunId = request.getParameter("runId");
    int runId = Integer.parseInt(sRunId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person u = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      String focusUser = manage.getFocusUser(runId, dbConn);
      if (!T9WorkFlowUtility.findId(focusUser, String.valueOf(u.getSeqId()))) {
        this.setRequestSuccess(request, "您没有关注此工作！");
        return "/core/inc/rtjson.jsp";
      } else {
        manage.calFocus(focusUser, u.getSeqId(), runId, dbConn);
      }
      this.setRequestSuccess(request, "操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getFlowTypeJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    List<T9FlowType> typeList = new ArrayList();
    T9FlowType flowType = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sSortId = request.getParameter("sortId");
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (!T9Utility.isNullorEmpty(sSortId)) {
        typeList = flowTypeLogic.getFlowTypeList(sSortId, dbConn);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(dbConn);
      }

      int count = 0;
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      for (int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        boolean canShow = true;

        T9PrcsRoleUtility pu = new T9PrcsRoleUtility();
        String manageUser = flowType.getManageUser();
        if (manageUser == null) {
          manageUser = "";
        }
        String manageUserDept = flowType.getManageUserDept();
        if (manageUserDept == null) {
          manageUserDept = "";
        }
        boolean mUserPriv = pu.checkPriv(loginUser, flowType.getManageUser());
        boolean mUserDeptPriv = pu.checkPriv(loginUser, manageUserDept);
        if (!(mUserPriv || mUserDeptPriv)) {
          canShow = false;
        }
        if (canShow) {
          sb.append("{");
          sb.append("seqId:\"" + flowType.getSeqId() + "\"");
          sb.append(",flowName:\"" + flowType.getFlowName() + "\"");
          sb.append("},");
          count++;
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getQueryItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      String str = flowTypeLogic.getQueryItem(flowId, dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String setQueryItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowId = request.getParameter("flowId");
    String queryItem = request.getParameter("fldStr");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      flowTypeLogic.setQueryItem(flowId, queryItem, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 设置错误信息
   * 
   * @param request
   * @param message
   */
  public void setRequestError(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   */
  public void setRequestSuccess(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   * @param data
   */
  public void setRequestSuccess(HttpServletRequest request, String message,
      String data) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }

}