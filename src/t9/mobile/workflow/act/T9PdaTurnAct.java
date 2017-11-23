package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.act.T9WorkTurnAct;
import t9.core.funcs.workflow.data.T9FlowRunFeedback;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FreeFlowLogic;
import t9.core.funcs.workflow.util.T9IWFPlugin;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaTurnLogic;

public class T9PdaTurnAct {
    /**
     * 流程保存完流程下一步（获取下一步流程内容）
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String turn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaTurnLogic logic = new T9PdaTurnLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            String sIsManage = request.getParameter("isManage");
            boolean isManage = false;
            if (sIsManage != null || "".equals(sIsManage)) {
                isManage = Boolean.valueOf(sIsManage);
            }

            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);

            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            Map runPrcsQuery = new HashMap();
            runPrcsQuery.put("RUN_ID", runId);
            runPrcsQuery.put("PRCS_ID", prcsId);
            runPrcsQuery.put("USER_ID", person.getSeqId());
            T9ORM orm = new T9ORM();
            T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class,
                    runPrcsQuery);

            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限,并取出权限字符串
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, person, conn);
            if (!"2".equals(runProcess.getTopFlag())) {
                if (!T9WorkFlowUtility.findId(roleStr, "2")) {// 没有权限
                    T9MobileUtility.output(response, "NOEDITPRIV");
                    return null;
                }
            } else {
                if (!T9WorkFlowUtility.findId(roleStr, "4")) {// 没有权限
                    T9MobileUtility.output(response, "NOSIGNFLOWPRIV");
                    return null;
                }
            }
            // T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
            // 取转交相关数据
            Map map = logic.getTurnData(person, runId, prcsId, flowPrcsStr, conn, isManage);
            String notPass = (String) map.get("notPass");
            if (notPass != null) {
                notPass = notPass.replace("\n", "<br>");
                T9MobileUtility.output(response, "<div class=\"no_msg\">" + notPass + "</div>");
                return null;
            }
            map.put("topFlag", runProcess.getTopFlag());
            request.setAttribute("r", map);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/turn.jsp?sessionid=" + sid;
    }

    /**
     * 办理流程
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String turnSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaTurnLogic logic = new T9PdaTurnLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            String sIsManage = request.getParameter("isManage");
            String FLOW_TYPE = request.getParameter("FLOW_TYPE");
            String PRCS_ID_NEXT = request.getParameter("PRCS_ID_NEXT");

            boolean isManage = false;
            if (sIsManage != null || "".equals(sIsManage)) {
                isManage = Boolean.valueOf(sIsManage);
            }

            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);

            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            Map runPrcsQuery = new HashMap();
            runPrcsQuery.put("RUN_ID", runId);
            runPrcsQuery.put("PRCS_ID", prcsId);
            runPrcsQuery.put("USER_ID", person.getSeqId());
            T9ORM orm = new T9ORM();
            T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class,
                    runPrcsQuery);

            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限,并取出权限字符串
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, person, conn);
            if (!"2".equals(runProcess.getTopFlag())) {
                if (!T9WorkFlowUtility.findId(roleStr, "2")) {// 没有权限
                    T9MobileUtility.output(response, "NOEDITPRIV");
                    return null;
                }
            } else {
                if (!T9WorkFlowUtility.findId(roleStr, "4")) {// 没有权限
                    T9MobileUtility.output(response, "NOSIGNFLOWPRIV");
                    return null;
                }
            }
            if (!"2".equals(FLOW_TYPE)) {
                T9FlowProcessLogic fp = new T9FlowProcessLogic();
                String pluginName = fp.getPluginStr(flowPrcs, flowId, conn);
                T9IWFPlugin pluginObj = null;
                if (pluginName != null && !"".equals(pluginName)) {
                    String className = T9WorkTurnAct.PLUGINPACKAGE + "." + pluginName;
                    try {
                        pluginObj = (T9IWFPlugin) Class.forName(className).newInstance();
                    } catch (ClassNotFoundException ex) {
                    }
                }
                if (pluginObj != null) {
                    String str = pluginObj.before(request, response);
                    if (str != null) {
                        T9MobileUtility.output(response, str);
                        return null;
                    }
                }
                T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
                if (PRCS_ID_NEXT == null || "".equals(PRCS_ID_NEXT) || "0".equals(PRCS_ID_NEXT)
                        || "0,".equals(PRCS_ID_NEXT)) {
                    String prcsUser = request.getParameter("prcsUser_0");
                    String prcsOpUser = request.getParameter("prcsOpUser_0");
                    String topFlag = request.getParameter("topFlag_0");
                    String prcsBack = request.getParameter("prcsBack");
                    String viewUser = "";
                    if (prcsBack == null) {
                        prcsBack = "";
                    }
                    flowRunLogic.turnEnd(person, runId, flowId, prcsId, flowPrcs, prcsUser, prcsOpUser,
                            topFlag, request.getRemoteAddr(), conn, prcsBack, viewUser);

                    T9MobileUtility.output(response, "WORKCOMPLETE");
                    return null;

                } else {
                    Map opUserMap = new HashMap();
                    String[] aStr = PRCS_ID_NEXT.split(",");
                    for (int i = 0; i < aStr.length; i++) {
                        String prcsUser = request.getParameter("PRCS_USER_" + aStr[i]);
                        String prcsOpUser = request.getParameter("PRCS_USER_OP_" + aStr[i]);
                        String topFlag = request.getParameter("TOP_DEFAULT_" + aStr[i]);
                        opUserMap.put("prcsOpUser_" + aStr[i], prcsOpUser);
                        opUserMap.put("prcsUser_" + aStr[i], prcsUser);
                        opUserMap.put("topFlag_" + aStr[i], topFlag);
                    }
                    flowRunLogic.turnNext(person, runId, flowId, prcsId, flowPrcs, PRCS_ID_NEXT, opUserMap,
                            request.getRemoteAddr(), conn);
                    String[] ss = PRCS_ID_NEXT.split(",");
                    for (int i = 0; i < ss.length; i++) {
                        String s = ss[i];
                        if (!"".equals(s) && T9Utility.isInteger(s)) {
                            int nextFlowPrcs = Integer.parseInt(s);
                            // 短信提醒下一步经办人
                            // 为什么不用上面的prcsUser?因为下面这个有可能是委托之后的用户．．．
                            String prcsUser2 = (String) opUserMap.get("prcsUser_" + s);
                            String childFlow = (String) opUserMap.get("nextFlow_" + s);
                            if (childFlow == null) {
                                flowRunLogic.remindNext(conn, runId, flowId, prcsId + 1, nextFlowPrcs,
                                        "请办理工作", request.getContextPath(), prcsUser2, person.getSeqId(), "",
                                        "");
                            } else {
                                int runIdNew = (Integer) opUserMap.get("nextRun_" + s);
                                flowRunLogic.remindNext(conn, runIdNew, Integer.parseInt(childFlow), 1, 1,
                                        "请办理工作", request.getContextPath(), prcsUser2, person.getSeqId(), "",
                                        "");
                            }
                        }
                    }
                }

                if (pluginObj != null) {
                    pluginObj.after(request, response);
                }
                T9MobileUtility.output(response, "WORKHASTURNNEXT");
                return null;
            } else {
                if (PRCS_ID_NEXT == null || "".equals(PRCS_ID_NEXT) || "0".equals(PRCS_ID_NEXT)
                        || "0,".equals(PRCS_ID_NEXT)) {
                    T9FreeFlowLogic freeFlowLogic = new T9FreeFlowLogic();
                    String msg = freeFlowLogic.stop(runId, flowId, prcsId, person, conn);

                    T9MobileUtility.output(response, "WORKCOMPLETE");
                    return null;
                } else {
                    String sPreSet = request.getParameter("PRESET");
                    String sMaxPrcs = request.getParameter("maxPrcs");

                    boolean preSet = false;
                    if (!T9Utility.isNullorEmpty(sPreSet)) {
                        preSet = true;
                    }
                    int maxPrcs = prcsId + 1;
                    if (sMaxPrcs != null) {
                        maxPrcs = Integer.parseInt(sMaxPrcs);
                    }
                    List<Map> preList = new ArrayList();
                    for (int i = prcsId + 1; i <= maxPrcs; i++) {
                        Map map = new HashMap();
                        map.put("prcsId", i);
                        String tmp = "";
                        if (i != prcsId + 1) {
                            tmp = String.valueOf(i);
                        }
                        // PRCS_ID_NEXT =
                        // T9WorkFlowUtility.getOutOfTail(PRCS_ID_NEXT);
                        // System.out.println(request.getParameter("PRCS_USER_"
                        // + PRCS_ID_NEXT));
                        map.put("prcsUser", request.getParameter("PRCS_USER_" + PRCS_ID_NEXT));
                        map.put("prcsOpUser", request.getParameter("PRCS_USER_OP_" + PRCS_ID_NEXT));
                        map.put("freeItem", request.getParameter("freeItem" + PRCS_ID_NEXT));
                        map.put("topFlag", request.getParameter("TOP_DEFAULT_" + PRCS_ID_NEXT));
                        preList.add(map);
                    }
                    T9FreeFlowLogic freeFlowLogic = new T9FreeFlowLogic();
                    String remindUser = freeFlowLogic.turnNext(person, flowId, runId, prcsId, conn, preSet,
                            preList, "");
                    T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
                    String sortId = request.getParameter("sortId");
                    if (sortId == null) {
                        sortId = "";
                    }
                    String skin = request.getParameter("skin");
                    if (skin == null) {
                        skin = "";
                    }
                    flowRunLogic.remindNext(conn, runId, flowId, prcsId + 1, 0, "请办理工作",
                            request.getContextPath(), remindUser, person.getSeqId(), sortId, skin);
                    T9MobileUtility.output(response, "WORKHASTURNNEXT");
                    return null;
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 结束流程（自由流程使用）
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String stop(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            // String sIsManage = request.getParameter("isManage");

            // boolean isManage = false;
            // if (sIsManage != null || "".equals(sIsManage)) {
            // isManage = Boolean.valueOf(sIsManage);
            // }

            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);

            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            Map runPrcsQuery = new HashMap();
            runPrcsQuery.put("RUN_ID", runId);
            runPrcsQuery.put("PRCS_ID", prcsId);
            runPrcsQuery.put("USER_ID", person.getSeqId());
            runPrcsQuery.put("FLOW_PRCS", flowPrcs);

            T9ORM orm = new T9ORM();
            T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class,
                    runPrcsQuery);
            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限,并取出权限字符串
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, person, conn);

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
                T9MobileUtility.output(response, "SIGNSUCCESS");
            }

            boolean turnNext = false;
            if ("2".equals(runProcess.getTopFlag())) {
                turnNext = T9PdaTurnLogic.turnNext(conn, runId, prcsId, flowPrcs, person.getSeqId());
            }
            if (!T9WorkFlowUtility.findId(roleStr, "1") && !T9WorkFlowUtility.findId(roleStr, "4")) {
                T9MobileUtility.output(response, "NOSUBEDITPRIV");
                return null;
            } else if (!turnNext) {
                String query = "update FLOW_RUN_PRCS set PRCS_FLAG='4',DELIVER_TIME=? WHERE RUN_ID='" + runId
                        + "' and PRCS_ID='" + prcsId + "' and USER_ID='" + person.getSeqId() + "'";
                T9PdaTurnLogic.updateFlowPrcs(conn, query);
                if ("2".equals(T9PdaTurnLogic.getFlowType(conn, flowId))) {
                    // 自由流程
                    T9FreeFlowLogic freeFlowLogic = new T9FreeFlowLogic();
                    String msg = freeFlowLogic.stop(runId, flowId, prcsId, person, conn);
                }
                T9MobileUtility.output(response, "WORKDONECOMPLETE");
                return null;
            } else {
                T9MobileUtility.output(response, "TURNNEXT");
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * zrh 转交下一步(获取转交下一步信息)
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String turnUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaTurnLogic logic = new T9PdaTurnLogic();
            String flowIdStr = request.getParameter("FLOW_ID");
            String runIdStr = request.getParameter("RUN_ID");
            String prcsIdStr = request.getParameter("PRCS_ID");
            String flowPrcsStr = request.getParameter("FLOW_PRCS");
            String sIsManage = request.getParameter("isManage");
            String FLOW_TYPE = request.getParameter("FLOW_TYPE");
            String PRCS_ID_NEXT = T9Utility.null2Empty(request.getParameter("PRCS_ID_NEXT"));
            boolean isManage = false;
            if (sIsManage != null || "".equals(sIsManage)) {
                isManage = Boolean.valueOf(sIsManage);
            }

            if (T9Utility.isNullorEmpty(PRCS_ID_NEXT) && !"2".equals(FLOW_TYPE)) {
                T9MobileUtility.output(response, "NONEXTPRCS");
                return null;
            }

            int runId = Integer.parseInt(runIdStr);
            int prcsId = Integer.parseInt(prcsIdStr);
            int flowId = Integer.parseInt(flowIdStr);

            int flowPrcs = 0;
            if (flowPrcsStr != null && !"".equals(flowPrcsStr) && !"null".equals(flowPrcsStr)) {
                flowPrcs = Integer.parseInt(flowPrcsStr);
            }
            Map runPrcsQuery = new HashMap();
            runPrcsQuery.put("RUN_ID", runId);
            runPrcsQuery.put("PRCS_ID", prcsId);
            runPrcsQuery.put("USER_ID", person.getSeqId());
            T9ORM orm = new T9ORM();
            T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class,
                    runPrcsQuery);

            T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
            // 验证是否有权限,并取出权限字符串
            String roleStr = roleUtility.runRole(runId, flowId, prcsId, person, conn);
            if (!"2".equals(runProcess.getTopFlag())) {
                if (!T9WorkFlowUtility.findId(roleStr, "2")) {// 没有权限
                    T9MobileUtility.output(response, "NOEDITPRIV");
                    return null;
                }
            } else {
                if (!T9WorkFlowUtility.findId(roleStr, "4")) {// 没有权限
                    T9MobileUtility.output(response, "NOSIGNFLOWPRIV");
                    return null;
                }
            }
            // 取转交相关数据
            request.setAttribute("flowType", FLOW_TYPE);
            request.setAttribute("prcsId", prcsId);

            if ("2".equals(FLOW_TYPE)) {
                Map map = logic.getFreeTurnData(person, flowId, runId, prcsId, conn, isManage);
                request.setAttribute("r", map);
                String sid = request.getSession().getId();
                return "/mobile/workflow/turnUser.jsp?sessionid=" + sid;
            } else {
                Map map = logic.getTurnUser(person, runId, prcsId, flowPrcsStr, conn, isManage, PRCS_ID_NEXT,
                        flowId);
                request.setAttribute("r", map);
                String sid = request.getSession().getId();
                return "/mobile/workflow/turnUser.jsp?sessionid=" + sid;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
