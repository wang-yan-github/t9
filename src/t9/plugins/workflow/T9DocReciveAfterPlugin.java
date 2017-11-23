package t9.plugins.workflow;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.subsys.inforesouce.docmgr.logic.T9DocReceiveLogic;

public class T9DocReciveAfterPlugin implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
      throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection conn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String prcsIdStr = request.getParameter("prcsId");
      String flowPrcsStr = request.getParameter("flowPrcs");
      int runId = Integer.parseInt(runIdStr);
      int prcsId = Integer.parseInt(prcsIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      T9FlowRunUtility wf = new T9FlowRunUtility();
      T9FlowRunData rd6 =  wf.getFlowRunData(conn, flowId, runId, "收文ID");
      T9FlowRunData rd7 =  wf.getFlowRunData(conn, flowId, runId, "行文类型ID");
      String id= rd6.getItemData();
      String docId = rd7.getItemData();
      T9DocReceiveLogic logic = new T9DocReceiveLogic();
      if (!T9Utility.isNullorEmpty(id) && !T9Utility.isNullorEmpty(docId)) {
        logic.updateDocReceive(conn, Integer.parseInt(id), String.valueOf(runId), Integer.parseInt(docId));
      }
    
    } catch(Exception ex) {
      throw ex;
    }
    return null;
  }

  public String before(HttpServletRequest request, HttpServletResponse response)
      throws Exception{
    
    return null;
  }

}
