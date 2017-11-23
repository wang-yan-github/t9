package t9.plugins.workflow;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.util.T9Utility;
import t9.subsys.oa.finance.data.T9ChargeExpense;
import t9.subsys.oa.finance.logic.T9ChargeExpenseLogic;
import t9.subsys.oa.finance.logic.T9FinanceApplyRecordLogic;

public class T9ChargeExpenseFlow implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String mage = "";
    try {
      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");//系统时间
      String time = sf.format(new java.util.Date());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");

      int runId = Integer.parseInt(runIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      T9FlowRunUtility wf = new T9FlowRunUtility();
      T9FlowRunData seq =  wf.getFlowRunData(dbConn, flowId, runId, "支票ID串");
      String seqIdStr = "" ;
      if (seq != null) {
        seq.getItemData();
      }
      String expenseId = "0";
      if(!T9Utility.isNullorEmpty(seqIdStr)) {
        T9FinanceApplyRecordLogic record = new T9FinanceApplyRecordLogic();
        record.updateExpense2(dbConn, seqIdStr);
        //expenseId = "1";
      }

      T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId, "报销人的部门ID");
      String deptId = rd.getItemData();

      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId, "报销申请人ID");
      String chargeUser = rd2.getItemData();

      T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId, "报销日期");
      Date chargeDate = Date.valueOf(rd3.getItemData());

      T9FlowRunData rd4 =  wf.getFlowRunData(dbConn, flowId, runId, "报销金额");
      String money = rd4.getItemData();

      T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId, "备注");
      String chargeMemo = rd5.getItemData();

      T9FlowRunData rd6 =  wf.getFlowRunData(dbConn, flowId, runId, "部门审批人ID");
      String deptAuditUser = rd6.getItemData();

      T9FlowRunData rd7 =  wf.getFlowRunData(dbConn, flowId, runId, "部门审批时间");
      Date deptAuditDate = null;
      if (!T9Utility.isNullorEmpty(rd7.getItemData())) {
        deptAuditDate = Date.valueOf(rd7.getItemData());
      } else {
        deptAuditDate = Date.valueOf(time);
      }
      T9FlowRunData rd8 =  wf.getFlowRunData(dbConn, flowId, runId, "部门审批内容");
      String deptAuditContent = rd8.getItemData();

      T9FlowRunData rd9 =  wf.getFlowRunData(dbConn, flowId, runId, "费用报销信息");
      String chargeItem = rd9.getItemData();

      T9FlowRunData rd10 =  wf.getFlowRunData(dbConn, flowId, runId, "财务审批人ID");
      String financeAuditUser = rd10.getItemData();

      T9FlowRunData rd12 =  wf.getFlowRunData(dbConn, flowId, runId, "预算ID");
      String budgetId = rd12.getItemData();

      int chargeYear = Integer.parseInt(time.substring(0,4));//年份
      boolean flage = true;
      double chargeMoney = 0;
      try {
        chargeMoney = Double.parseDouble(money);
      } catch (Exception ex) {
        flage = false;
      }
      if (flage) {
        T9ChargeExpense expense = new T9ChargeExpense();
        expense.setBudgetId(budgetId);
        expense.setChargeDate(chargeDate);
        expense.setChargeItem(chargeItem);
        expense.setChargeMemo(chargeMemo);
        expense.setChargeMoney(chargeMoney);
        expense.setChargeUser(chargeUser);
        expense.setChargeYear(chargeYear);
        expense.setCostId(0);
        expense.setDeptAuditContent(deptAuditContent);
        expense.setDeptAuditDate(deptAuditDate);
        expense.setDeptAuditUser(deptAuditUser);
        expense.setRunId(runId);
        expense.setExpense(expenseId);
        expense.setDeptId(deptId);
        expense.setFinanceAuditUser(financeAuditUser);
        T9ChargeExpenseLogic exLogic = new T9ChargeExpenseLogic();
        exLogic.addFlow(dbConn,expense);
        mage = null;
      }else {
        mage = "金额只能为数字!";
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }

}
