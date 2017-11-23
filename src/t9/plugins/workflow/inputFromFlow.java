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
public class inputFromFlow implements T9IWFPlugin{

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
      if(!T9Utility.isNullorEmpty(seqId)) {
        T9BudgetApplyLogic budget = new T9BudgetApplyLogic();
        budget.updateBudgetToSettle(dbConn, seqId);
      }
      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId,"报销ID串");
      String seqIdStr = rd2.getItemData();
      //System.out.println(seqIdStr);
      //      String applyDate = rd.getItemData();
      //
      //      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId,"编号");
      //
      //      T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId,"团组名称");    
      //
      //      T9FlowRunData cheque = wf.getFlowRunData(dbConn, flowId, runId,"部门");
      //     
      //      T9FlowRunData rd4 =  wf.getFlowRunData(dbConn, flowId, runId,"申请人姓名");
      //  
      //      T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId,"预收款实际到帐金额");
      //
      //      T9FlowRunData rd6 =  wf.getFlowRunData(dbConn, flowId, runId,"到帐方式");
      //     
      //      T9FlowRunData rd7 =  wf.getFlowRunData(dbConn, flowId, runId,"余款到帐金额");
      //  
      //      T9FlowRunData rd8 =  wf.getFlowRunData(dbConn, flowId, runId,"余款到帐方式");
      //     
      //      T9FlowRunData rd9 =  wf.getFlowRunData(dbConn, flowId, runId,"发票金额开据数");
      //
      //      T9FlowRunData rd10 =  wf.getFlowRunData(dbConn, flowId, runId,"追加");
      //      
      //      T9FlowRunData rd11 =  wf.getFlowRunData(dbConn, flowId, runId,"实际收入金额");
      //      
      //      T9FlowRunData rd12 =  wf.getFlowRunData(dbConn, flowId, runId,"实收金额(大写)");
      //      
      //      T9FlowRunData rd13 =  wf.getFlowRunData(dbConn, flowId, runId,"实际支出金额");
      //      
      //      T9FlowRunData rd14 =  wf.getFlowRunData(dbConn, flowId, runId,"实支金额(大写)");
      //
      //      T9FlowRunData rd15 =  wf.getFlowRunData(dbConn, flowId, runId,"备注");
      //      
      //      T9FlowRunData rd16 =  wf.getFlowRunData(dbConn, flowId, runId,"主管业务会领导");
      //      
      //      T9FlowRunData rd17 =  wf.getFlowRunData(dbConn, flowId, runId,"财务主管");
      //      
      //      T9FlowRunData rd18 =  wf.getFlowRunData(dbConn, flowId, runId,"财务人员");
      //      
      //      T9FlowRunData rd19 =  wf.getFlowRunData(dbConn, flowId, runId,"部门领导");
      //      
      //      T9FlowRunData rd20 =  wf.getFlowRunData(dbConn, flowId, runId,"经办人");
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
