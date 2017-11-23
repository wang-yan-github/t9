package t9.plugins.workflow;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.finance.data.T9FinanceApplyRecord;
import t9.subsys.oa.finance.logic.T9FinanceApplyRecordLogic;

public class T9FinanceApplyRecordFlow implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
    return null;
  }
  public String before(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String mage = "";
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      T9FlowRunUtility wf = new T9FlowRunUtility();
      int flowId = Integer.parseInt(flowIdStr);

      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");//系统时间
      String time = sf.format(new java.util.Date());
      String operator = String.valueOf(person.getSeqId());//创建人
      Date operateDate = Date.valueOf(time);//创建时间
      int runId = Integer.parseInt(runIdStr);//工作流ID

      T9FlowRunData det =  wf.getFlowRunData(dbConn, flowId, runId,"领用部门ID");
      String deptId = det.getItemData() ;//登录人的部门ID，领用部门ID

      T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId,"领用人ID");
      String applyClaimer = rd.getItemData();

      T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId,"领用项目");
      String applyProject = rd2.getItemData();

      T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId,"领用日期");
      Date applyDate = Date.valueOf(rd3.getItemData());

      int applyYear = Integer.parseInt(time.substring(0,4));//年份

      //T9FlowRunData rd4 =  wf.getFlowRunData(dbConn, flowId, runId,"领用金额");
      //T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId,"备注");

      T9FlowRunData rd6 =  wf.getFlowRunData(dbConn, flowId, runId,"部门主管ID");
      String deptDirector = rd6.getItemData();

      T9FlowRunData rd7 =  wf.getFlowRunData(dbConn, flowId, runId,"部门主管审批时间");
      Date deptDirectorDate = Date.valueOf(rd7.getItemData());

      T9FlowRunData rd8 =  wf.getFlowRunData(dbConn, flowId, runId,"部门主管审批内容");
      String deptDirectorContent = rd8.getItemData();

      T9FlowRunData rd9 =  wf.getFlowRunData(dbConn, flowId, runId,"财务签发人ID");
      String financeSignatory = rd9.getItemData();

      T9FlowRunData rd10 =  wf.getFlowRunData(dbConn, flowId, runId,"签发时间");
      Date signDate = Date.valueOf(rd10.getItemData());

      T9FlowRunData getId =  wf.getFlowRunData(dbConn, flowId, runId,"预算ID");
      String budgetId = getId.getItemData();

      T9FlowRunData cheque = wf.getFlowRunData(dbConn, flowId, runId,"支票领用详细");
      String chequeAccount = "";
      String applyItem = "";  
      String applyMemo = "";
      String money = "";
      if(!T9Utility.isNullorEmpty(cheque.getItemData())){
        String detailContent = cheque.getItemData();
        String dcs[] = detailContent.split("\n");
        for(int i = 0; i< dcs.length;i++){
          String dc= dcs[i];
          if (!T9Utility.isNullorEmpty(dc)&&dc.endsWith("`")) {
            dc = dc.substring(0, dc.length()-1);
          }
          String[] temp = dc.split("`");
          chequeAccount = "";
          applyItem = "";
          applyMemo = "";
          money = "";
          for (int j = 0; j< temp.length;j++) {
            if(j == 0){
              chequeAccount = temp[0];
            }
            if(j == 1){
              applyMemo = temp[1];
            }
            if(j == 2){
              applyItem = temp[2];
            }
            if(j == 3){ 
              money = temp[3];
            }
          }
          boolean flage = true;
          double applyMoney =0;
          try {
            applyMoney = Double.parseDouble(money);
          } catch (Exception ex) {
            flage = false;
          }
          if (flage) {
            T9FinanceApplyRecord record = new T9FinanceApplyRecord();
            record.setApplyClaimer(applyClaimer);
            record.setApplyDate(applyDate);
            record.setRunId(runId);
            record.setApplyMemo(applyMemo);
            record.setApplyItem(applyItem);
            record.setApplyMoney(applyMoney);
            record.setApplyProject(applyProject);
            record.setBudgetId(budgetId);
            record.setApplyYear(applyYear);
            record.setChequeAccount(chequeAccount);
            record.setDeptDirector(deptDirector);
            record.setSignDate(signDate);
            record.setOperator(operator);
            record.setOperateDate(operateDate);
            record.setDeptDirector(deptDirector);
            record.setDeptDirectorContent(deptDirectorContent);
            record.setDeptDirectorDate(deptDirectorDate);
            record.setDeptId(deptId);
            record.setFinanceDirector(financeSignatory);  

            T9FinanceApplyRecordLogic rLogic = new T9FinanceApplyRecordLogic();
            rLogic.addFlow(dbConn, record);
            mage = null;
          }else {
            mage = "金额只能为数字!";
          }

        }
      }else {
        mage = null;
      }
    } catch(Exception ex) {
      throw ex;
    }
    return mage;
  }

}
