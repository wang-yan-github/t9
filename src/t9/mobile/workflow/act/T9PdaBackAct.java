package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunFeedback;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaBackLogic;

public class T9PdaBackAct {
    public String backPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            // T9PdaTurnLogic logic = new T9PdaTurnLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            // T9PdaBackLogic flowRunLogic = new T9PdaBackLogic();
            // 取转交相关数据
            List<Map> m = T9PdaBackLogic.getBackData(conn, person, runIdStr, prcsIdStr, flowIdStr,
                    flowPrcsStr);
            request.setAttribute("r", m);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/selback.jsp?sessionid=" + sid;
    }

    public String goback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);
            int flowPrcs = Integer.parseInt(flowPrcsStr);

            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, person, conn);
            if (!T9WorkFlowUtility.findId(roleStr, "1") && !T9WorkFlowUtility.findId(roleStr, "2")
                    && !T9WorkFlowUtility.findId(roleStr, "3")) {
                // T9MobileUtility.output(response, "NOSUBEDITPRIV");
                return null;
            }

            String content = T9Utility.null2Empty(request.getParameter("CONTENT"));
            if (!T9Utility.isNullorEmpty(content)) {
                T9FeedbackLogic fbLogic = new T9FeedbackLogic();
                T9FlowRunFeedback fb = new T9FlowRunFeedback();
                if (content.startsWith("<p>")) {
                    content = content.replaceFirst("<p>", "");
                }
                if (content.endsWith("</p>")) {
                    content = content.substring(0, content.lastIndexOf("</p>"));
                }
                fb.setContent(content);
                Date date = new Date();
                fb.setEditTime(date);
                fb.setPrcsId(prcsId);
                fb.setRunId(runId);
                fb.setUserId(person.getSeqId());
                fb.setAttachmentId("");
                fb.setAttachmentName("");
                fb.setFlowPrcs(flowPrcs);
                fb.setSignData("");
                fbLogic.saveFeedback(fb, conn);
            }
            String prcsIdPre = T9Utility.null2Empty(request.getParameter("FLOW_PRCS_LAST"));

            String allow = T9PdaBackLogic.getAllowBack(conn, flowId, flowPrcs);
            if (allow != null && !"0".equals(allow) && prcsId != 1) {
                if ("1".equals(allow)) {
                    prcsIdPre = String.valueOf(prcsId - 1);
                }
                T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
                flowRunLogic.backTo(person, runId, flowId, prcsId, flowPrcs, prcsIdPre,
                        request.getRemoteAddr(), request.getContextPath(), "", "", conn);
                T9MobileUtility.output(response, "WORKHASGOBACK");
                return null;
            } else {
                T9MobileUtility.output(response, "WORKHASNOTGOBACK");
                return null;
            }

        } catch (Exception ex) {
            throw ex;
        }
    }
}
