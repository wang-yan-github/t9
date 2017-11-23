package t9.core.funcs.doc.receive.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.data.T9DocFlowProcess;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.receive.logic.T9DocRegisterLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9DocReceiveHandlerAct{
  public final static byte[] loc = new byte[1];
  /**
   * 老的创建工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createWork(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   String flowIdStr = request.getParameter("flowId");
   
   Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int flowId = Integer.parseInt(flowIdStr);
      String seqId = request.getParameter("seqId");
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9DocFlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9DocFlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
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
          int runId = fru.createNewWork(dbConn, flowId, loginUser, request.getParameterMap() , "", "");
          String sql = "update doc_receive set RUN_ID = "+ runId +",STATUS=1  where SEQ_ID=" + seqId;
          PreparedStatement ps = null;
          try{
            ps = dbConn.prepareStatement(sql);
            ps.executeUpdate();
          } catch (SQLException e){      
            throw e;
          }finally{
            T9DBUtility.close(ps, null, null);
          }
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
   * 新的创建工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createWorkNew(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   String flowIdStr = request.getParameter("flowId");
   
   Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int flowId = Integer.parseInt(flowIdStr);
      String seqId = request.getParameter("seqId");
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9DocFlowProcess> list = fpl.getFlowProcessByFlowId(flowId , dbConn);
      T9DocFlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);
      //取得第一步
      
      String attach = request.getParameter("attid");
      
      boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, loginUser , dbConn);
      //如果第一步为空，以及检查出没有权限则提示
      synchronized(loc) {
        if ( flag ) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程新建权限，请与OA管理员联系");
        }else{
          T9DocRegisterLogic logic1 = new T9DocRegisterLogic();
          String att[] = logic1.getAttach(dbConn, seqId);
          String ids = att[0];
          String[] idss = ids.split(",");
          String[] names = att[1].split("\\*");
          String news = "";
          String newsId = "";
          for (int i = 0 ;i < idss.length ; i++) {
            String ss = idss[i];
            if (!T9Utility.isNullorEmpty(ss) 
                && T9WorkFlowUtility.findId(attach, ss) ) {
              news += names[i] + "*";
              newsId += ss + ",";
            }
          }
          if (news.endsWith("*")) {
            news = news.substring(0, news.length() - 1);
          }
          if (newsId.endsWith(",")) {
            newsId = newsId.substring(0, newsId.length() - 1);
          }
          int runId = fru.createNewWork(dbConn, flowId, loginUser, request.getParameterMap() , newsId , news);
          if(runId == 0){ 
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "输入的工作名称/文号与之前的工作重复，请重新设置.");
          }else{
            String sql = "update doc_rec_register set RUN_ID = "+ runId +" where SEQ_ID=" + seqId;
            PreparedStatement ps = null;
            try{
              ps = dbConn.prepareStatement(sql);
              ps.executeUpdate();
            } catch (SQLException e){      
              throw e;
            }finally{
              T9DBUtility.close(ps, null, null);
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
            request.setAttribute(T9ActionKeys.RET_DATA, "{runId:" + runId + ",flowId:" + flowId + "}");
          }
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
}
