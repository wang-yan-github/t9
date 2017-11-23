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
import t9.subsys.oa.finance.logic.T9BudgetApplyLogic;
public class inputFrom implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  public String before(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    String mage = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      T9FlowRunUtility wf = new T9FlowRunUtility();
      int flowId = Integer.parseInt(flowIdStr);
      int runId = Integer.parseInt(runIdStr);

      T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId,"预算ID");
      String seqId = rd.getItemData();
      if (T9Utility.isNullorEmpty(seqId)) {
        mage = "预算ID为空,不能结算!";
      }else {
        mage = null;
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }
}
