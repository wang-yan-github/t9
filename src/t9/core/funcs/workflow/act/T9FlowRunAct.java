package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunAssistLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9FlowRunAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.workflow.act.T9FlowRunAct");
  /**
   * 取得工作流新建信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getNewMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
      String sFlowId = request.getParameter("flowId");
      int flowId = Integer.parseInt(sFlowId);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
      //如果第一步为空，以及检查出没有权限则提示          
      boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, loginUser  , dbConn);
      if ( flag) {
        String message = T9WorkFlowUtility.Message("没有该流程新建权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        //取得文号,步骤列表
        //返加的json数据格式为rtData:{formId:23,flowName:'ddd',runName:'请假申请(2010-01-20 14:45:45)',prcsList:[{prcsNo:'1',prcsName:'请假申请',prcsTo:'2,'},{prcsNo:'1',prcsName:'请假申请',prcsTo:'2,'}]}
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
          synchronized(loc) {
            request.setAttribute(T9ActionKeys.RET_DATA, flowRunLogic.getNewMsg(flowType, loginUser, list , dbConn));
            dbConn.commit();
          }
        }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  /**
   * 取得最近的工作列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRunList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      if(loginUser == null){
        String message = T9WorkFlowUtility.Message("用户未登录，请<a href='" + request.getContextPath() +"'>重新登录!</a>",2);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        String sSortId = request.getParameter("sortId");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
        request.setAttribute(T9ActionKeys.RET_DATA, "[" + flowRunLogic.getRecentlyFlowRun(loginUser , dbConn , sSortId) + "]");
      }
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public final static byte[] loc = new byte[1];
  /**
   * 新建一个工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createNewWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String runName = null;
    if(request.getParameter("runName") != null){
      runName =  request.getParameter("runName");
    }
    String leftName = request.getParameter("runNameLeft");
    if (leftName != null && !"".equals(leftName)) {
      runName = leftName + runName;
    }
    String rightName = request.getParameter("runNameRight");
    if (rightName != null && !"".equals(rightName)) {
      runName = runName + rightName;
    }  
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      String sFlowId = request.getParameter("flowId");
      int flowId = Integer.parseInt(sFlowId);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
      //取得第一步      synchronized(loc) {
        boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, loginUser , dbConn);
      //如果第一步为空，以及检查出没有权限则提示        if (flag) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程新建权限，请与OA管理员联系");
        }else{
        //查询是否为重名的
          T9FlowRunLogic frl = new T9FlowRunLogic();
          //如果没有指定runName
          if(runName == null){
            runName = frl.getRunName(flowType, loginUser , dbConn , false) ;
          }
          //重名
          if(frl.isExist(runName, flowId , dbConn)){ 
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "输入的工作名称/文号与之前的工作重复，请重新设置.");
          }else{
            int runId  = frl.createNewWork(loginUser, flowType, runName , dbConn);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
            request.setAttribute(T9ActionKeys.RET_DATA, runId + "");
          }
        }
        dbConn.commit();
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建一个工作

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createNewDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String runName = null;
    if(request.getParameter("runName") != null){
      runName =  request.getParameter("runName");
    }
    String leftName = request.getParameter("runNameLeft");
    if (leftName != null && !"".equals(leftName)) {
      runName = leftName + runName;
    }
    String rightName = request.getParameter("runNameRight");
    if (rightName != null && !"".equals(rightName)) {
      runName = runName + rightName;
    }  
   Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      String sFlowId = request.getParameter("flowId");
      int flowId = Integer.parseInt(sFlowId);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
      //取得第一步

      boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, loginUser , dbConn);
      //如果第一步为空，以及检查出没有权限则提示

      if ( flag ) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程新建权限，请与OA管理员联系");
      }else{
        //查询是否为重名的
        T9FlowRunLogic frl = new T9FlowRunLogic();
        //如果没有指定runName
        if(runName == null){
          runName = frl.getRunName(flowType, loginUser , dbConn , false) ;
        }
        //重名
        if(frl.isExist(runName, flowId , dbConn)){ 
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "输入的工作名称/文号与之前的工作重复，请重新设置.");
        }else{
          T9FlowRunUtility util = new T9FlowRunUtility();
          int runId = util.createNewWork(dbConn, flowId, loginUser, request.getParameterMap());
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
          request.setAttribute(T9ActionKeys.RET_DATA, runId + "");
        }
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建一个工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   String flowName = request.getParameter("flowName");
   Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int flowId = fru.getFlowId(dbConn, flowName);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
      //取得第一步
      boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, loginUser , dbConn);
      //如果第一步为空，以及检查出没有权限则提示
      if ( flag ) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程新建权限，请与OA管理员联系");
      }else{
        //查询是否为重名的
        T9FlowRunLogic frl = new T9FlowRunLogic();
        String runName = frl.getRunName(flowType, loginUser , dbConn , false) ;
        //重名
        if(frl.isExist(runName, flowId , dbConn)){ 
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "输入的工作名称/文号与之前的工作重复，请重新设置.");
        }else{
          int runId = fru.createNewWork(dbConn, flowId, loginUser, request.getParameterMap());
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
          request.setAttribute(T9ActionKeys.RET_DATA, "{runId:" + runId + ",flowId:" + flowId + "}");
        }
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得流水号
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   String flowName = request.getParameter("flowName");
   String runId = request.getParameter("runId");
   Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int flowId  = 0 ;
      if (!T9Utility.isNullorEmpty(flowName)) {
         flowId = fru.getFlowId(dbConn, flowName);
      } else {
        flowId = fru.getFlowId(dbConn, Integer.parseInt(runId));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, flowId + "");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改流程名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateRunName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runName =  request.getParameter("runName");
    String sRunId = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(sRunId);
      T9FlowRunLogic logic = new T9FlowRunLogic();
      logic.updateRunName(runName, runId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功!");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 添加经办人
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    String toIdStr = request.getParameter("user");
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
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = 0;
      if (T9Utility.isInteger(flowPrcsStr)) {
        flowPrcs = Integer.parseInt(flowPrcsStr);
      }
      int toId = 0 ;
      if (T9Utility.isInteger(toIdStr)) {
        toId = Integer.parseInt(toIdStr);
      }
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser, dbConn);
      
      if (!T9WorkFlowUtility.findId(roleStr, "2")
          && !T9WorkFlowUtility.findId(roleStr, "3")
          && !loginUser.isAdminRole()) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "此用户没有此工作的经办权限");
        return "/core/inc/rtjson.jsp";
      }
      T9FlowRunUtility fru = new T9FlowRunUtility();
      boolean isExistUser = fru.isExistUser(dbConn, runId, prcsId, flowPrcs, toId);
      if (isExistUser) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "此用户已经为本步骤经办人！");
        return "/core/inc/rtjson.jsp";
      }
      T9FlowRunAssistLogic logic = new T9FlowRunAssistLogic();
      logic.addPrcsUser(dbConn, prcsId, runId, flowId, flowPrcs, toId, sortId , skin, loginUser.getSeqId(), request.getContextPath());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功!");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
