package t9.core.funcs.doc.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowRunFeedback;
import t9.core.funcs.doc.logic.T9FeedbackLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9RuleLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9RuleAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9RuleAct");
  public String addRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String sUserId = request.getParameter("userId");
      int userId = loginUser.getSeqId();
      if (sUserId != null && !"".equals(sUserId)) {
        userId = Integer.parseInt(sUserId);
      }
      String sortId = request.getParameter("sortId");
      String checkAll = request.getParameter("checkAll");
      String alwaysOn = request.getParameter("alwaysOn");
      String beginDate = request.getParameter("beginDate");
      if ("".equals(beginDate)) {
        beginDate = null;
      }
      String endDate = request.getParameter("endDate");
      if ("".equals(endDate)) {
        endDate = null;
      }
      int toId = Integer.parseInt(request.getParameter("toId"));
      if ("on".equals(alwaysOn)) {
        beginDate = null;
        endDate = null;
      } 
      T9RuleLogic logic = new T9RuleLogic();
      if ("on".equals(checkAll)) {
        String flowIdStr = request.getParameter("flowIdStr");
        String[] flowIds = flowIdStr.split(",");
        for (String sFlowId : flowIds) {
          int flowId = 0 ;
          if (sFlowId != null && !"".equals(sFlowId)) {
            flowId = Integer.parseInt(sFlowId);
            //添加规则
            logic.addRule(userId, toId, flowId, beginDate, endDate, dbConn);
          }
        }
      } else {
        int flowId = Integer.parseInt(request.getParameter("flowId"));
        logic.addRule(userId, toId, flowId, beginDate, endDate, dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String loadRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String sUserId = request.getParameter("queryUserId");
      int userId = loginUser.getSeqId();
      if (sUserId != null && !"".equals(sUserId)) {
        userId = Integer.parseInt(sUserId);
      }
      String sortId = request.getParameter("sortId");
      if (sortId == null) {
        sortId = "";
      }
      String ruleState = request.getParameter("ruleState");
      T9RuleLogic logic = new T9RuleLogic();
      String result = logic.loadRule(userId, Integer.parseInt(ruleState), dbConn , sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, result);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String openOrClose(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int ruleId = Integer.parseInt(request.getParameter("ruleId"));
      String sIsOpened = request.getParameter("isOpened");
      boolean isOpened = Boolean.valueOf(sIsOpened);
      
      T9RuleLogic logic = new T9RuleLogic();
      logic.openOrClose(ruleId, isOpened, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String ruleId = request.getParameter("ruleId");
      T9RuleLogic logic = new T9RuleLogic();
      logic.delRule(ruleId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String closeOrOpenAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String ruleIds = request.getParameter("ruleIds");
      String sIsOpened = request.getParameter("isOpen");
      boolean isOpen = Boolean.valueOf(sIsOpened);
      T9RuleLogic logic = new T9RuleLogic();
      if (ruleIds != null) {
        logic.closeOrOpenAll(ruleIds , isOpen , dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String ruleIds = request.getParameter("ruleIds");
      T9RuleLogic logic = new T9RuleLogic();
      logic.delAll(ruleIds , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getRuleById(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int ruleId = Integer.parseInt(request.getParameter("ruleId"));
      T9RuleLogic logic = new T9RuleLogic();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, logic.getRuleById(ruleId, dbConn));
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int ruleId = Integer.parseInt(request.getParameter("ruleId"));
      int userId = Integer.parseInt(request.getParameter("userId"));
      if (loginUser.getSeqId() == userId || loginUser.isAdminRole()) {
        int toId = Integer.parseInt(request.getParameter("toId"));
        int flowId = Integer.parseInt(request.getParameter("flowId"));
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        T9RuleLogic logic = new T9RuleLogic();
        logic.updateRule(ruleId , userId, toId, flowId, beginDate, endDate, dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "操作成功!");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getList(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String queryUserId = request.getParameter("queryUserId");
      String sHasDelegate = request.getParameter("type");
      String sortId = request.getParameter("sortId");
      if (sortId == null) {
        sortId = "";
      }
      boolean hasDelegate = true;
      if (sHasDelegate != null 
          && !"".equals(sHasDelegate)) {
        hasDelegate = Boolean.valueOf(sHasDelegate);
      }
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int otherUser = loginUser.getSeqId();
      if (queryUserId != null && !"".equals(queryUserId)) {
        otherUser =  Integer.parseInt(queryUserId);
      }
      T9RuleLogic logic = new T9RuleLogic();
      Map map =  request.getParameterMap();
      String data = logic.getList(dbConn , otherUser , map , hasDelegate , sortId);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
