package t9.mobile.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowRunFeedback;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9MyWorkLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.pda.workflow.logic.T9PdaWorkflowLogic;

public class T9PdaHandlerLogic {

    public Map getSignMap(Connection conn, T9Person user, int flowId, int runId, int prcsId, int flowPrcs)
            throws Exception {
        // TODO Auto-generated method stub
        T9PdaWorkflowLogic logic = new T9PdaWorkflowLogic();
        Map map = new HashMap();
        T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
        T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId, conn);
        Map runPrcsQuery = new HashMap();
        runPrcsQuery.put("RUN_ID", runId);
        runPrcsQuery.put("PRCS_ID", prcsId);
        runPrcsQuery.put("USER_ID", user.getSeqId());
        T9ORM orm = new T9ORM();
        T9FlowRunPrcs runProcess = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class, runPrcsQuery);

        T9FlowProcess flowProcess = null;
        if ("1".equals(flowType.getFlowType())) {
            T9FlowProcessLogic flowPrcsLogic = new T9FlowProcessLogic();
            // 查出相关步骤
            flowProcess = flowPrcsLogic.getFlowProcessById(flowId, flowPrcs + "", conn);
        }
        String feedback = "0";
        if (flowProcess != null && flowProcess.getFeedback() != null) {
            feedback = flowProcess.getFeedback();
        }

        map.put("runId", runId);
        map.put("opFlag", runProcess.getOpFlag());

        map.put("flowId", flowId);
        map.put("prcsId", prcsId);
        map.put("flowPrcs", flowPrcs);
        map.put("feedbackFlag", feedback);
        map.put("feedbacks", this.getFeedbacks(user, runId, prcsId, flowId, conn));
        return map;
    }

    public Map getEditMap(Connection conn, T9Person user, int flowId, int runId, int prcsId, int flowPrcs,
            String ip, String imgPath) throws Exception {
        T9PdaWorkflowLogic logic = new T9PdaWorkflowLogic();
        Map map = logic.getHandlerMsg(user, runId, prcsId, String.valueOf(flowPrcs), ip, conn, imgPath, "1");
        T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
        T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId, conn);
        String sql = " select RUN_NAME,ATTACHMENT_ID,ATTACHMENT_NAME,BEGIN_TIME from FLOW_RUN where RUN_ID="
                + runId;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                map.put("runName", rs.getString("RUN_NAME"));
                map.put("attachmentId", rs.getString("ATTACHMENT_ID"));
                map.put("attachmentName", rs.getString("ATTACHMENT_NAME"));
                map.put("beginTime", T9Utility.getDateTimeStr(rs.getTimestamp("BEGIN_TIME")));
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(ps, rs, null);
        }

        map.put("flowId", flowId);
        map.put("prcsId", prcsId);
        map.put("flowPrcs", flowPrcs);
        map.put("feedbacks", this.getFeedbacks(user, runId, prcsId, flowId, conn));
        map.put("prcs", this.getPrcsList(runId, flowType, conn));
        return map;
    }

    /**
     * 取得json形式流程列表
     * 
     * @param runId
     * @param flowId
     * @return
     * @throws Exception
     */
    public List<Map> getPrcsList(int runId, T9FlowType flowType, Connection conn) throws Exception {
        T9ORM orm = new T9ORM();
        String timeToId = "";
        String queryMax = "SELECT MAX(PRCS_ID) as max " + " from FLOW_RUN_PRCS where  " + " RUN_ID=" + runId;
        T9MyWorkLogic l = new T9MyWorkLogic();
        int prcsMax = l.getMax(conn, queryMax);
        List<Map> result = new ArrayList();

        for (int i = 1; i <= prcsMax; i++) {
            Map m = new HashMap();
            m.put("prcsId", i);
            List<Map> list1 = new ArrayList();
            String query = "SELECT * from FLOW_RUN_PRCS where " + "  RUN_ID=" + runId + "  "
                    + " and PRCS_ID=" + i + " order by FLOW_PRCS";
            int flowPrcs = 0;
            T9FlowProcess fp = null;
            Statement stm2 = null;
            ResultSet rs2 = null;
            String ss = "";
            int count = 0;
            try {
                stm2 = conn.createStatement();
                rs2 = stm2.executeQuery(query);
                // 获取步骤信息
                while (rs2.next()) {
                    flowPrcs = rs2.getInt("FLOW_PRCS");
                    if (T9WorkFlowUtility.findId(ss, flowPrcs + "")) {
                        continue;
                    }
                    count++;
                    ss += flowPrcs + ",";
                    String prcsName = "";
                    String timeOut = "";
                    String except = "00";
                    if (flowType != null && "1".equals(flowType.getFlowType())) {
                        T9FlowProcessLogic logic = new T9FlowProcessLogic();
                        fp = logic.getFlowProcessById(flowType.getSeqId(), String.valueOf(flowPrcs), conn);

                        if (fp != null) {
                            prcsName = fp.getPrcsName();
                            timeOut = fp.getTimeOut();
                            except = fp.getTimeExcept();
                        } else {
                            prcsName = "<font color=red>流程步骤已删除</font>";
                        }

                    }
                    String parent = rs2.getString("PARENT");
                    int childRun = rs2.getInt("CHILD_RUN");

                    int childFlowId = 0;
                    if (childRun != 0) {
                        T9FlowRunUtility u = new T9FlowRunUtility();
                        childFlowId = u.getFlowId(conn, childRun);
                    }
                    Map m1 = new HashMap();
                    m1.put("prcsName", prcsName);
                    m1.put("flowPrcs", flowPrcs);
                    m1.put("childRun", childRun);
                    m1.put("childFlowId", childFlowId);

                    // ---------- while2 获得此步骤、此序号的办理信息 ---------------
                    int prcsState = 0;
                    String opUserName = "";
                    // $query1 =
                    // "SELECT * from FLOW_RUN_PRCS where RUN_ID=$RUN_ID and PRCS_ID=$PRCS_ID_I and FLOW_PRCS='$FLOW_PRCS' AND PARENT='$PARENT' order by OP_FLAG,PRCS_FLAG DESC,PRCS_TIME";
                    Map map = new HashMap();
                    map.put("RUN_ID", runId);
                    map.put("PRCS_ID", i);
                    map.put("FLOW_PRCS", flowPrcs);
                    // map.put("PARENT", parent);//暂不考虑父流程

                    List<T9FlowRunPrcs> list = orm.loadListSingle(conn, T9FlowRunPrcs.class, map);
                    List<Map> users = new ArrayList();
                    for (T9FlowRunPrcs flowRunPrcs : list) {
                        Map u = new HashMap();
                        // 处理办理人信息

                        String queryPerson = "SELECT u.USER_NAME " + " ,d.DEPT_NAME  "
                                + " from PERSON u,DEPARTMENT d where  " + " u.SEQ_ID="
                                + flowRunPrcs.getUserId() + " AND d.SEQ_ID = u.DEPT_ID";
                        String userName = "";
                        Statement stm4 = null;
                        ResultSet rs4 = null;

                        try {
                            stm4 = conn.createStatement();
                            rs4 = stm4.executeQuery(queryPerson);
                            if (rs4.next()) {
                                userName = rs4.getString("USER_NAME");
                                u.put("userName", userName);
                                u.put("deptName", rs4.getString("DEPT_NAME"));
                            } else {
                                u.put("userName", "此人员已删除");
                                u.put("deptName", "此人员已删除");
                            }
                        } catch (Exception ex) {
                            throw ex;
                        } finally {
                            T9DBUtility.close(stm4, rs4, null);
                        }
                        // true-我主办的，false-我会签

                        boolean isOp = false;
                        // 未接收中
                        int prcsFlag = 0;
                        if (flowRunPrcs.getPrcsFlag() != null) {
                            prcsFlag = Integer.parseInt(flowRunPrcs.getPrcsFlag());
                            prcsState = prcsFlag;
                        }
                        // 主办的

                        if ("1".equals(flowRunPrcs.getOpFlag())) {
                            isOp = true;
                            opUserName = userName;
                            prcsState = prcsFlag;
                        }
                        if ("2".equals(flowRunPrcs.getTopFlag())) {
                            opUserName = "无主办人会签";
                            if (prcsState == 0) {
                                prcsState = prcsFlag;
                            } else if (prcsFlag < prcsState) {
                                prcsState = prcsFlag;
                            }
                        }
                        u.put("isOp", isOp);
                        // 计算用户用时
                        long timeUsed = 0;
                        if (flowRunPrcs.getPrcsTime() != null) {
                            if (prcsFlag == 1) {
                            } else if (prcsFlag == 2) {
                                Date date = new Date();
                                timeUsed = date.getTime() - flowRunPrcs.getPrcsTime().getTime();
                            } else {
                                if (flowRunPrcs.getDeliverTime() != null) {
                                    timeUsed = flowRunPrcs.getDeliverTime().getTime()
                                            - flowRunPrcs.getPrcsTime().getTime();
                                }
                            }
                        }
                        String timeStr = "";
                        long day = timeUsed / (24 * 60 * 60 * 1000);
                        long hour = (timeUsed / (60 * 60 * 1000) - day * 24);
                        long min = ((timeUsed / (60 * 1000)) - day * 24 * 60 - hour * 60);
                        long s = (timeUsed / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

                        if (day > 0) {
                            timeStr = day + "天";
                        }
                        if (hour > 0) {
                            timeStr += hour + "时";
                        }
                        if (min > 0) {
                            timeStr += min + "分";
                        }
                        if (s > 0) {
                            timeStr += s + "秒";
                        }
                        // -- 超时信息 --
                        int timeOutFlag = 0;
                        Date beginTime = flowRunPrcs.getPrcsTime();
                        if ((prcsFlag == 2 || prcsFlag == 1) && !"".equals(timeOut) && timeOut != null) {
                            if (timeOutFlag == 0) {
                                if (flowRunPrcs.getPrcsTime() != null) {
                                    beginTime = flowRunPrcs.getPrcsTime();
                                } else {
                                    beginTime = flowRunPrcs.getCreateTime();
                                }
                            } else {
                                beginTime = flowRunPrcs.getCreateTime();
                            }
                            // 如果不是自由流程
                            if ("1".equals(flowType.getFlowType())) {
                                String timeDesc = T9WorkFlowUtility.getTimeOut(timeOut, beginTime,
                                        new Date(), "dhms", except);
                                // 超时
                                if (!"".equals(timeDesc)) {
                                    timeStr = timeDesc;
                                    timeOutFlag = 1;
                                }
                            }
                        }
                        if (!"".equals(timeStr)) {
                            u.put("timeUsed", timeStr);
                        } else {
                            u.put("timeUsed", "0");
                        }
                        // 记录未接收或者超时用户

                        if (prcsFlag == 1 || timeOutFlag == 1) {
                            timeToId += flowRunPrcs.getUserId() + ",";
                        }
                        if (beginTime != null) {
                            u.put("beginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beginTime));
                        } else {
                            u.put("beginTime", "");
                        }
                        if (flowRunPrcs.getDeliverTime() != null) {
                            u.put("deliverTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(flowRunPrcs.getDeliverTime()));
                        } else {
                            u.put("deliverTime", "");
                        }
                        u.put("state", flowRunPrcs.getPrcsFlag());
                        if (flowRunPrcs.getCreateTime() != null) {
                            u.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .format(flowRunPrcs.getCreateTime()));
                        } else {
                            u.put("createTime", "");
                        }
                        users.add(u);
                    }
                    m1.put("state", prcsState);
                    String prcsTitle = "第" + i + "步." + prcsName + "(" + opUserName + ")";
                    m1.put("prcsTitle", prcsTitle);
                    m1.put("user", users);
                    list1.add(m1);
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm2, rs2, null);
            }
            m.put("list", list1);
            result.add(m);
        }
        return result;
    }

    public List<Map> getFeedbacks(T9Person loginUser, int runId, int prcsId, int flowId, Connection conn)
            throws Exception {
        T9FeedbackLogic l = new T9FeedbackLogic();
        Map prcsNameMap = new HashMap();
        Map signLookMap = new HashMap();
        int type = l.getFlowType(conn, flowId);
        l.setPrcsMap(prcsNameMap, signLookMap, runId, flowId, type, conn);
        List<T9FlowRunFeedback> list = l.getSignLookFeedback(signLookMap, type, runId, loginUser.getSeqId(),
                conn);
        StringBuffer sb = new StringBuffer();
        List<Map> list2 = new ArrayList();
        for (T9FlowRunFeedback tmp : list) {
            Map map = new HashMap();
            map.put("prcsId", tmp.getPrcsId());
            String prcsName = (String) prcsNameMap.get(String.valueOf(tmp.getPrcsId()) + ":"
                    + tmp.getFlowPrcs());
            prcsName = prcsName == null ? "" : prcsName;
            map.put("prcsName", prcsName);
            Map nameMap = l.getNames(tmp.getUserId(), conn);
            String userName = (String) nameMap.get("userName");
            map.put("userName", userName);
            String deptName = (String) nameMap.get("deptName");
            map.put("deptName", deptName);
            String attachmentId = tmp.getAttachmentId() == null ? "" : tmp.getAttachmentId();
            String attachmentName = tmp.getAttachmentName() == null ? "" : tmp.getAttachmentName();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("aId", attachmentId);
            map.put("aName", attachmentName);
            map.put("time", sdf.format(tmp.getEditTime()));
            if (tmp.getContent() != null) {
                String con = tmp.getContent();
                con = con.replaceAll("\'", "\\\\'");
                con = con.replaceAll("\r", "");
                con = con.replaceAll("\n", "<br>");
                map.put("content", con);
            } else {
                map.put("content", "");
            }
            list2.add(map);
        }
        return list2;
    }
}
