package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.logic.T9MyWorkLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9MyWorkAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9MyWorkAct");
  public String getPrcsList1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      Map map =  myWorkLogic.getPrcsListMsg1(Integer.parseInt(flowIdStr), Integer.parseInt(runIdStr) , dbConn);
      request.setAttribute("map" , map);
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        String message = T9WorkFlowUtility.Message("用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }
      throw ex;
    }
    return "/core/funcs/doc/flowrun/list/flowview/viewgraph2.jsp?flowId=" + flowIdStr + "&runId=" + runIdStr;
  }
  public String getMyWorkList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String showLenStr = request.getParameter("showLength");
    String flowIdStr = request.getParameter("flowId");
    String pageIndexStr = request.getParameter("pageIndex");
    String typeStr = request.getParameter("typeStr");
    String sSortId = request.getParameter("sortId");
    
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String str =  "";
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      
      String filedList = "";
      if (!T9Utility.isNullorEmpty(flowIdStr)) {
        filedList = T9WorkFlowUtility.getOutOfTail(myWorkLogic.getFildList(dbConn, flowIdStr));
      }
      String realPath = request.getRealPath("/");
      boolean isDesk = false;
      if (!T9Utility.isNullorEmpty(request.getParameter("isDesk"))) {
        isDesk = true;
      }
      if("3".equals(typeStr)){
        String opFlag = request.getParameter("opFlag");
        str = myWorkLogic.getEndWorkList1( loginUser, Integer.parseInt(pageIndexStr),
        flowIdStr, Integer.parseInt(showLenStr),opFlag  , sSortId ,dbConn , filedList,realPath , isDesk);
      }else{
        str = myWorkLogic.getMyWorkList( loginUser, Integer.parseInt(pageIndexStr),
        flowIdStr, Integer.parseInt(showLenStr),typeStr , sSortId , dbConn , filedList,realPath , isDesk);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,filedList);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        String message = T9WorkFlowUtility.Message("用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getMyWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      str = myWorkLogic.getMyWork(dbConn, user, 10  , sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSign(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      str = myWorkLogic.getSign(dbConn, user, 10 , sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getFocusWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person user = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String str =  "";
      String sortId = request.getParameter("sortId");
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      str = myWorkLogic.getFocusWork(dbConn, user, 10, sortId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getWorkMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      String str =  myWorkLogic.getWorkMsg(Integer.parseInt(flowIdStr), Integer.parseInt(runIdStr), dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        String message = T9WorkFlowUtility.Message("用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getPrcsList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      String str =  myWorkLogic.getPrcsListMsg(Integer.parseInt(flowIdStr), Integer.parseInt(runIdStr) , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        String message = T9WorkFlowUtility.Message("用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getLogList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      String str =  myWorkLogic.getLogList(Integer.parseInt(flowIdStr), Integer.parseInt(runIdStr) , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!");
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      }
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delRun(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runIdStr = request.getParameter("runId");
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      myWorkLogic.delRun(Integer.parseInt(runIdStr), loginUser, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据!");
    } catch (Exception ex) {
      if(loginUser == null){
        String contextPath = request.getContextPath();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "用户未登录，请重新<a href='"+contextPath+"/login.jsp'>登录</a>!");
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      }
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String callBack(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic myWorkLogic = new T9MyWorkLogic();
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      String msg = myWorkLogic.callBack(runId, prcsId, flowPrcs, loginUser, dbConn);
      if (msg == null ) {
        msg = "收回操作成功";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,msg);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
  /**
   * 是否有未接收的工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String hasWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String fFlowId = request.getParameter("flowId");  
    String sSortId = request.getParameter("sortId");
    int flowId = 0 ;
    
    if (T9Utility.isInteger(fFlowId)) {
      flowId = Integer.parseInt(fFlowId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyWorkLogic logic = new T9MyWorkLogic();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"");
      request.setAttribute(T9ActionKeys.RET_DATA, String.valueOf(logic.hasWork(dbConn, loginUser.getSeqId() , sSortId , flowId)));
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getRecvInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String runId = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MyWorkLogic logic = new T9MyWorkLogic();
      String data = logic.getRecvInfo(dbConn, runId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
