package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.Date;
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
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaHandlerLogic;

public class T9PdaHandlerAct {

    /**
     * 验证编辑流程权限
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String checkPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
            if (!flowRunLogic.canHandlerWrok(runId, prcsId, flowPrcs, person.getSeqId(), conn)) {
                T9MobileUtility.output(response, "NOEDITPRIV");
                return null;
            }
            if (flowRunLogic.hasDelete(runId, conn)) {
                T9MobileUtility.output(response, "NOEDITPRIV");
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * zrh 获取流程编辑内容
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaHandlerLogic logic = new T9PdaHandlerLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");

            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);

            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            // T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
            // if (!flowRunLogic.canHandlerWrok(runId, prcsId, flowPrcs,
            // person.getSeqId(), conn)) {
            // T9MobileUtility.output(response, "NOEDITPRIV");
            // return null;
            // }
            // if (flowRunLogic.hasDelete(runId, conn)) {
            // T9MobileUtility.output(response, "NOEDITPRIV");
            // return null;
            // }
            String imgPath = T9WorkFlowUtility.getImgPath(request);
            String ip = request.getRemoteAddr();
            Map map = logic.getEditMap(conn, person, flowId, runId, prcsId, flowPrcs, ip, imgPath);
            request.setAttribute("r", map);
            request.setAttribute("imgPath", imgPath);
            request.setAttribute("refreshFlag", request.getParameter("refreshFlag"));
            // request.setAttribute("FLOW_ID", flowIdStr);
            // request.setAttribute("RUN_ID", runIdStr);
            // request.setAttribute("PRCS_ID", prcsIdStr);
            // request.setAttribute("FLOW_PRCS", flowPrcsStr);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/flowFormEdit.jsp?sessionid=" + sid;
    }

    /**
     * zrh 保存会签意见
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String signSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String flowIdStr = request.getParameter("FLOW_ID");
        String runIdStr = request.getParameter("RUN_ID");
        String prcsIdStr = request.getParameter("PRCS_ID");
        String flowPrcsStr = request.getParameter("FLOW_PRCS");
        String hiddenStr = request.getParameter("hiddenStr");
        String readOnlyStr = request.getParameter("readOnlyStr");
        String content = T9Utility.null2Empty(request.getParameter("CONTENT"));
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);
            int flowPrcs = Integer.parseInt(flowPrcsStr);
            if (hiddenStr == null) {
                hiddenStr = "";
            }
            if (readOnlyStr == null) {
                readOnlyStr = "";
            }
            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser, dbConn);
            if ("".equals(roleStr)) {// 没有权限
                T9MobileUtility.output(response, "NOSIGNFLOWPRIV");
                return null;
            } else {
                // 取表单相关信息
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
                    fb.setUserId(loginUser.getSeqId());
                    fb.setAttachmentId("");
                    fb.setAttachmentName("");
                    fb.setFlowPrcs(flowPrcs);
                    fb.setSignData("");
                    fbLogic.saveFeedback(fb, dbConn);
                    T9MobileUtility.output(response, "SIGNSUCCESS");
                } else {
                    T9MobileUtility.output(response, "SIGNISNOTEMPTY");
                }
            }
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        return null;
    }

    /**
     * zrh 获取会签的意见列表
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String sign(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String flowIdStr = request.getParameter("FLOW_ID");
        String runIdStr = request.getParameter("RUN_ID");
        String prcsIdStr = request.getParameter("PRCS_ID");
        String flowPrcsStr = request.getParameter("FLOW_PRCS");
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);
            int flowPrcs = Integer.parseInt(flowPrcsStr);
            T9PdaHandlerLogic logic = new T9PdaHandlerLogic();

            String imgPath = T9WorkFlowUtility.getImgPath(request);
            // String ip = request.getRemoteAddr();
            Map map = logic.getSignMap(dbConn, person, flowId, runId, prcsId, flowPrcs);
            request.setAttribute("r", map);
            request.setAttribute("imgPath", imgPath);
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/sign.jsp?sessionid=" + sid;
    }

    /**
     * zrh 编辑提交
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String editSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        String flowIdStr = request.getParameter("FLOW_ID");
        String runIdStr = request.getParameter("RUN_ID");
        String prcsIdStr = request.getParameter("PRCS_ID");
        String flowPrcsStr = request.getParameter("FLOW_PRCS");
        String hiddenStr = request.getParameter("hiddenStr");
        String readOnlyStr = request.getParameter("readOnlyStr");
        String content = T9Utility.null2Empty(request.getParameter("CONTENT"));
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);
            int flowPrcs = Integer.parseInt(flowPrcsStr);
            if (hiddenStr == null) {
                hiddenStr = "";
            }
            if (readOnlyStr == null) {
                readOnlyStr = "";
            }
            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser, dbConn);
            if ("".equals(roleStr)) {// 没有权限
                T9MobileUtility.output(response, "NOEDITPRIV");
                return null;
            } else {
                T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
                // 取表单相关信息
                flowRunLogic.saveFormData(loginUser, flowId, runId, prcsId, flowPrcs, request, hiddenStr,
                        readOnlyStr, dbConn);
                // if (!T9Utility.isNullorEmpty(content)) {
                // T9FeedbackLogic fbLogic = new T9FeedbackLogic();
                // T9FlowRunFeedback fb = new T9FlowRunFeedback();
                // if (content.startsWith("<p>")) {
                // content = content.replaceFirst("<p>", "");
                // }
                // if (content.endsWith("</p>")) {
                // content = content.substring(0, content.lastIndexOf("</p>"));
                // }
                // fb.setContent(content);
                // Date date = new Date();
                // fb.setEditTime(date);
                // fb.setPrcsId(prcsId);
                // fb.setRunId(runId);
                // fb.setUserId(loginUser.getSeqId());
                // fb.setAttachmentId("");
                // fb.setAttachmentName("");
                // fb.setFlowPrcs(flowPrcs);
                // fb.setSignData("");
                // // fbLogic.saveFeedback(fb, dbConn);
                // // 与上面保存会签内容接口重复，去除 zrh 20170227
                // }

                // String title = "<div class=\"no_msg\">保存成功</div>"
                // +
                // "<div id=\"save_opts\" class=\"save_opts\" style=\"display:none;\">"
                // + "<span class=\"continueEdit_flow\">继续编辑</span>"
                // + "<span class=\"turn_flow\">转交</span>" + "</div>";
                T9MobileUtility.output(response, "保存成功");
            }
        } catch (Exception ex) {
            String message = T9WorkFlowUtility.Message(ex.getMessage(), 1);
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, message);
            throw ex;
        }
        return null;
    }
}
