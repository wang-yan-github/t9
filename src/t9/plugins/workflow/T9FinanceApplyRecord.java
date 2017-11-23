package t9.plugins.workflow;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunData;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;

public class T9FinanceApplyRecord  implements T9IWFPlugin{

  public String after(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    // TODO Auto-generated method stub
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
    String prcsIdStr = request.getParameter("prcsId");
    String flowPrcsStr = request.getParameter("flowPrcs");
    
    int runId = Integer.parseInt(runIdStr);
    int prcsId = Integer.parseInt(prcsIdStr);
    int flowId = Integer.parseInt(flowIdStr);
    int flowPrcs = Integer.parseInt(flowPrcsStr);

    T9FlowRunUtility wf = new T9FlowRunUtility();
    
    T9FlowRunData rd =  wf.getFlowRunData(dbConn, flowId, runId, "领用人姓名");
    String applyClaimer = rd.getItemData();

    T9FlowRunData rd2 =  wf.getFlowRunData(dbConn, flowId, runId, "领用项目");
    String applyProject = rd2.getItemData();
    
    T9FlowRunData rd3 =  wf.getFlowRunData(dbConn, flowId, runId, "领用日期");
    String applyDate = rd3.getItemData();
    
    T9FlowRunData rd4 =  wf.getFlowRunData(dbConn, flowId, runId, "领用金额");
    double applyMoney = Double.parseDouble(rd4.getItemData().toString());
    
    T9FlowRunData rd5 =  wf.getFlowRunData(dbConn, flowId, runId, "支票张数");
    String applyDate5 = rd5.getItemData();
    
    
    T9ORM orm = new T9ORM();
    T9Person perName = (T9Person)orm.loadObjComplex(dbConn,T9Person.class,person.getSeqId());
    T9Department dep = (T9Department)orm.loadObjComplex(dbConn, T9Department.class,perName.getDeptId());
    String useDept = dep.getDeptName();
   
  } catch(Exception ex) {
    throw ex;
  }
  return mage;
  }

}
