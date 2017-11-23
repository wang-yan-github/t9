package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaTurnLogic;

public class T9SealDelAct {
	public String data(HttpServletRequest request, 
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      
      String flowIdStr = request.getParameter("FLOW_ID");
      String runIdStr = request.getParameter("RUN_ID");
      String DATA_ID = T9Utility.null2Empty(request.getParameter("DATA_ID"));
      String id = DATA_ID.replace("DATA_", "");
      
      int runId = Integer.parseInt(runIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      if (T9Utility.isInteger(id)) {
        T9WorkFlowUtility.updateFormData(conn, runId, flowId, Integer.parseInt(id), "");
      }
      T9MobileUtility.output(response, "OK+");
      return null;
    }  catch (Exception ex) {
       throw ex;
    }
  }
	
}
