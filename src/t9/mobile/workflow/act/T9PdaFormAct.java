package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9MyWorkLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaTurnLogic;

public class T9PdaFormAct {
    /**
     * 原始表单查看
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String originalForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            dbConn = requestDbConn.getSysDbConn();

            T9PdaTurnLogic logic = new T9PdaTurnLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);
            int flowPrcs = Integer.parseInt(flowPrcsStr);

            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser, dbConn);
            if (T9Utility.isNullorEmpty(roleStr)) {
                T9MobileUtility.output(response, "NOREADFLOWPRIV");
                return null;
            }
            T9FlowRunLogic frl = new T9FlowRunLogic();
            T9FlowRun flowRun = frl.getFlowRunByRunId(runId, dbConn);
            T9FlowTypeLogic ftl = new T9FlowTypeLogic();
            T9FlowType ft = ftl.getFlowTypeById(flowId, dbConn);
            String imgPath = T9WorkFlowUtility.getImgPath(request);
            Map result = frl.getPrintForm(loginUser, flowRun, ft, true, dbConn, imgPath, "");
            String form = (String) result.get("form");

            T9FeedbackLogic feedbackLogic = new T9FeedbackLogic();
            String feedbacks = feedbackLogic.getFeedbacksHtml(loginUser, flowRun.getFlowId(),
                    flowRun.getRunId(), dbConn);
            T9MyWorkLogic workLogic = new T9MyWorkLogic();
            String prcs = workLogic.getPrcsHtml(flowRun.getRunId(), ft, dbConn);

            String content = "<div class=\"container\"><form name=\"form1\" method=\"post\" action=\"\">"
                    + form + feedbacks + prcs + "</form></div>";
            T9MobileUtility.output(response, content);
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
