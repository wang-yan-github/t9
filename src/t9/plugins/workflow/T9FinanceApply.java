package t9.plugins.workflow;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;

public class T9FinanceApply implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
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
      int runId = Integer.parseInt(runIdStr);//工作流ID

      T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId,"领用金额");
      String money = rd.getItemData();

      boolean flage = true;
      double applyMoney =0;
      try {
        applyMoney = Double.parseDouble(money);
        mage = null;
      } catch (Exception ex) {
        flage = false;
      }
      if (!flage) {
        mage = "金额只能为数字!";
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }
}
