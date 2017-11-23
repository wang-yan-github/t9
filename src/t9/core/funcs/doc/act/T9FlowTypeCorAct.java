package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

/**
 * 与流程管理相关的一些功能
 * @author 刘涵
 *
 */
public class T9FlowTypeCorAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.doc.act.T9FlowTypeCorAct");
  /**
   * 校验
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sFlowId = request.getParameter("flowId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      String data = logic.checkFlowType(Integer.parseInt(sFlowId) , dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getCloneMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sFlowId = request.getParameter("flowId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      String data = logic.getCloneMsg(Integer.parseInt(sFlowId), dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String clone(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sFlowId = request.getParameter("flowId");
    String flowName = request.getParameter("flowName");
    String sFlowNo = request.getParameter("flowNo");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      logic.clone(Integer.parseInt(sFlowId), flowName, sFlowNo, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String trans(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String flowIdStr = request.getParameter("flowIdStr");
    String toId = request.getParameter("toId");
    String userId = request.getParameter("userId");
    String beginRun = request.getParameter("beginRun");
    String endRun = request.getParameter("endRun");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      logic.trans(dbConn, flowIdStr, toId, userId , beginRun , endRun);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "移交成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String search(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String search = request.getParameter("searchKey");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      StringBuffer sb = new StringBuffer("[");
      String data = "";
      StringBuffer workCounts = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      List<T9DocFlowType> typeList = flowTypeLogic.getFlowTypeList(dbConn);
      int count = 0 ;
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      for (T9DocFlowType flowType  : typeList) {
        if (flowType.getFlowName().contains(search)){
          if (!w.isHaveRight(flowType.getDeptId(), u, dbConn)) {
            continue;
          }
          sb.append(flowType.toJson() + ",");
          //取得工作数量
          int workCount = flowTypeLogic.getWorkCountByFlowId(flowType.getSeqId() , dbConn);
          int delCount = flowTypeLogic.getDelWorkCountByFlowId(flowType.getSeqId() , dbConn);
          workCounts.append("{workCount:" + workCount);
          workCounts.append(",delCount:" + delCount + "},");
          count ++ ;
        }
      }
      if (count >  0) {
        sb.deleteCharAt(sb.length() - 1);
        workCounts.deleteCharAt(workCounts.length() - 1);
      }
      workCounts.append("]");
      sb.append("]");
      data = "{flowList:" + sb.toString() + ",workCounts:" + workCounts.toString() + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
