package t9.mobile.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9TurnConditionUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.mobile.util.T9MobileUtility;

public class T9PdaTurnLogic {
    /**
     * 获取流转数据
     * 
     * @param loginUser
     * @param runId
     * @param prcsId
     * @param flowPrcsStr
     * @param conn
     * @param isManage
     * @return
     * @throws Exception
     */
    public Map getTurnData(T9Person loginUser, int runId, int prcsId, String flowPrcsStr, Connection conn,
            boolean isManage) throws Exception {
        Map r = new HashMap();
        T9ORM orm = new T9ORM();
        Map queryMap = new HashMap();
        queryMap.put("RUN_ID", runId);
        T9FlowRun flowRun = (T9FlowRun) orm.loadObjSingle(conn, T9FlowRun.class, queryMap);
        Map queryPrcs = new HashMap();
        queryPrcs.put("RUN_ID", runId);
        queryPrcs.put("PRCS_ID", prcsId);
        queryPrcs.put("FLOW_PRCS", Integer.parseInt(flowPrcsStr));
        List<T9FlowRunPrcs> flowRunPrcList = orm.loadListSingle(conn, T9FlowRunPrcs.class, queryPrcs);
        String parentStr = "";
        for (T9FlowRunPrcs p : flowRunPrcList) {
            if (!T9Utility.isNullorEmpty(p.getParent())) {
                parentStr += p.getParent() + ",";
            }
        }
        // 注意这儿
        T9FlowProcessLogic flowProcessLogic = new T9FlowProcessLogic();
        T9FlowProcess flowProcess = flowProcessLogic.getFlowProcessById(flowRun.getFlowId(), flowPrcsStr,
                conn);
        T9FlowType flowType = (T9FlowType) orm.loadObjSingle(conn, T9FlowType.class, flowRun.getFlowId());
        // ------------------------------------------- 转出条件检查
        // ----------------------------------
        T9TurnConditionUtility turnUtility = new T9TurnConditionUtility();
        Map formData = turnUtility.getForm(flowType.getFormSeqId(), runId, flowRun.getFlowId(), conn);

        if (!isManage) {
            String notPass = turnUtility.checkCondition(loginUser, formData, flowProcess, false, runId,
                    prcsId, Integer.parseInt(flowPrcsStr), conn);
            if (!"setOk".equals(notPass)) {
                r.put("notPass", notPass);
                return r;
            }
        }
        r.put("flowName", flowType.getFlowName());
        String syncDeal = flowProcess.getSyncDeal();
        if (T9Utility.isNullorEmpty(syncDeal)) {
            syncDeal = "0";
        }
        String gatherNode = flowProcess.getGatherNode();
        if (T9Utility.isNullorEmpty(gatherNode)) {
            gatherNode = "0";
        }
        r.put("prcsName", flowProcess.getPrcsName());
        r.put("syncDeal", syncDeal);

        String runName = flowRun.getRunName();
        Map frm = new HashMap();
        r.put("flowRun", frm);
        frm.put("runName", runName);
        frm.put("flowType", flowType.getFlowType());
        T9PersonLogic logic = new T9PersonLogic();
        String beginUser = logic.getNameBySeqIdStr(flowRun.getBeginUser() + "", conn);
        frm.put("beginUser", beginUser);
        frm.put("isAllowTurn", flowProcess.getTurnPriv() != null ? flowProcess.getTurnPriv() : "0");

        // 取得所有经办人的办理信息

        if ("1".equals(gatherNode)) {
            int iflowPrc = Integer.parseInt(flowPrcsStr);
            String query1 = "select " + " PRCS_ID  " + "  from  " + "  FLOW_PROCESS  " + " where  "
                    + " FLOW_SEQ_ID='" + flowRun.getFlowId() + "' " + "  and  ("
                    + T9DBUtility.findInSet(flowPrcsStr, "PRCS_TO") + " or (PRCS_ID = " + (iflowPrc - 1)
                    + " AND PRCS_TO=''))";
            Statement stm3 = null;
            ResultSet rs3 = null;
            try {
                stm3 = conn.createStatement();
                rs3 = stm3.executeQuery(query1);
                while (rs3.next()) {
                    int prcsId1 = rs3.getInt("PRCS_ID");
                    String query2 = "select PRCS_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='" + runId
                            + "' AND FLOW_PRCS='" + prcsId1 + "' and OP_FLAG='1'";
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsFlag1 = "1";
                    boolean cannotTurn = false;
                    try {
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(query2);
                        if (rs4.next()) {
                            prcsFlag1 = rs4.getString("PRCS_FLAG");
                            if (T9Utility.isInteger(prcsFlag1) && Integer.parseInt(prcsFlag1) <= 2) {
                                cannotTurn = true;
                            }
                        } else {
                            cannotTurn = false;
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    if (!T9WorkFlowUtility.findId(parentStr, prcsId1 + "") && cannotTurn) {
                        r.put("gatherNode", "1");
                        return r;
                    }
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm3, rs3, null);
            }
        }
        boolean flag = this.getHandlerState(flowRunPrcList, loginUser, Integer.parseInt(flowPrcsStr), conn,
                isManage, frm);
        if (!flag) {
            return r;
        }
        // 取得下一步的信息
        List<T9FlowProcess> nextPrcs = flowProcessLogic.getNextProcess(flowProcess, conn);
        List<Map> nextPrcsList = new ArrayList();
        for (T9FlowProcess tmp : nextPrcs) {
            Map p = new HashMap();
            p.put("seqId", tmp.getSeqId());
            p.put("prcsId", tmp.getPrcsId());
            p.put("childFlow", tmp.getChildFlow());

            int parentFlowId = 0;
            String prcsBack = "";
            String backUserOp = "";
            String backUser = "";
            p.put("parentRun", flowRun.getParentRun());

            if (tmp.getPrcsId() == 0 && flowRun.getParentRun() != 0) {
                T9FlowRunUtility u = new T9FlowRunUtility();
                parentFlowId = u.getFlowId(conn, flowRun.getParentRun());
                String query3 = "select FLOW_PRCS FROM FLOW_RUN_PRCS WHERE RUN_ID='" + flowRun.getParentRun()
                        + "' AND CHILD_RUN='" + runId + "'";
                Statement stm = null;
                ResultSet rs = null;
                int flowPrcs = 0;
                try {
                    stm = conn.createStatement();
                    rs = stm.executeQuery(query3);
                    if (rs.next()) {
                        flowPrcs = rs.getInt("FLOW_PRCS");
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm, rs, null);
                }
                p.put("parentFlowPrcs", flowPrcs);
                p.put("parentFlowId", parentFlowId);
                p.put("prcsName", "结束子流程");

                String query5 = "select PRCS_TO,AUTO_USER_OP,AUTO_USER FROM FLOW_PROCESS WHERE FLOW_SEQ_ID='"
                        + parentFlowId + "' AND PRCS_ID='" + flowPrcs + "'";
                Statement stm6 = null;
                ResultSet rs6 = null;
                try {
                    stm6 = conn.createStatement();
                    rs6 = stm6.executeQuery(query5);
                    if (rs6.next()) {
                        prcsBack = rs6.getString("PRCS_TO");
                        backUserOp = T9Utility.null2Empty(rs6.getString("AUTO_USER_OP"));
                        backUser = T9Utility.null2Empty(rs6.getString("AUTO_USER"));
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm6, rs6, null);
                }
                if (T9Utility.isNullorEmpty(prcsBack)) {
                    prcsBack = "";
                }
                p.put("prcsBack", prcsBack);
            } else {
                if (tmp.getChildFlow() != 0) {
                    p.put("prcsName", tmp.getPrcsName() + "(子流程)");
                } else {
                    p.put("prcsName", tmp.getPrcsName());
                }
            }

            String topFlag = (tmp.getTopDefault() == null ? "0" : tmp.getTopDefault());
            String userFilter = (tmp.getUserFilter() == null ? "0" : tmp.getUserFilter());
            // 检查转入条件
            String notInPass = turnUtility.checkCondition(loginUser, formData, tmp, true, runId, prcsId,
                    Integer.parseInt(flowPrcsStr), conn);
            boolean userLock = false;
            if ("0".equals(tmp.getUserLock())) {
                userLock = true;
            }
            if (!"setOk".equals(notInPass)) {
                p.put("notInPass", notInPass);
            }
            // 取得下一步中的默认人员
            if (tmp.getPrcsId() == 0 && flowRun.getParentRun() != 0) {
                turnUtility.userSelect2(conn, loginUser, prcsBack, parentFlowId, backUserOp, backUser, p);
            } else {
                turnUtility.userSelect(loginUser, tmp, flowRun, conn, p);
            }

            p.put("userFilter", userFilter);
            p.put("userLock", userLock);
            p.put("topFlag", topFlag);
            nextPrcsList.add(p);
        }
        r.put("nextPrcs", nextPrcsList);
        return r;
    }

    /**
     * 取得办理情况的字符串
     * 
     * @return
     * @throws Exception
     */
    public boolean getHandlerState(List<T9FlowRunPrcs> flowRunPrcList, T9Person user, int flowPrcs,
            Connection conn, boolean isManage, Map u) throws Exception {
        String userNameStr = "";
        String parentStr = "";
        String notAllFinish = "";
        int turnForbidden = 0;
        String flowPrcsUp = "";
        T9PersonLogic personLogic = new T9PersonLogic();
        for (T9FlowRunPrcs tmp : flowRunPrcList) {
            if (tmp.getFlowPrcs() == flowPrcs) {
                int userId = tmp.getUserId();
                String parent = tmp.getParent();
                String prcsFlag = tmp.getPrcsFlag();
                parentStr += parent + ",";
                T9Person person = personLogic.getPersonById(userId, conn);
                if (!prcsFlag.equals("4") && userId != user.getSeqId()) {
                    if (person != null) {
                        notAllFinish += person.getUserName() + ",";
                    }
                }
                if ((prcsFlag.equals("3") || prcsFlag.equals("4")) && userId == user.getSeqId() && !isManage) {
                    turnForbidden = 1;
                } else {
                    turnForbidden = 0;
                }
            } else {
                flowPrcsUp += tmp.getFlowPrcs();
            }
        }
        if (!"".equals(notAllFinish)) {
            notAllFinish = notAllFinish.substring(0, notAllFinish.length() - 1);
        }
        if (turnForbidden != 1) {
            u.put("notAllFinish", notAllFinish);
            return true;
        } else {
            return false;
        }
    }

    public Map getTurnUserData(T9Person loginUser, int runId, int prcsId, String flowPrcsStr,
            Connection conn, boolean isManage) throws Exception {
        // TODO Auto-generated method stub
        Map r = new HashMap();
        T9ORM orm = new T9ORM();
        Map queryMap = new HashMap();
        queryMap.put("RUN_ID", runId);
        T9FlowRun flowRun = (T9FlowRun) orm.loadObjSingle(conn, T9FlowRun.class, queryMap);
        Map queryPrcs = new HashMap();
        queryPrcs.put("RUN_ID", runId);
        queryPrcs.put("PRCS_ID", prcsId);
        queryPrcs.put("FLOW_PRCS", Integer.parseInt(flowPrcsStr));
        List<T9FlowRunPrcs> flowRunPrcList = orm.loadListSingle(conn, T9FlowRunPrcs.class, queryPrcs);
        String parentStr = "";
        for (T9FlowRunPrcs p : flowRunPrcList) {
            if (!T9Utility.isNullorEmpty(p.getParent())) {
                parentStr += p.getParent() + ",";
            }
        }
        // 注意这儿
        T9FlowProcessLogic flowProcessLogic = new T9FlowProcessLogic();
        T9FlowProcess flowProcess = flowProcessLogic.getFlowProcessById(flowRun.getFlowId(), flowPrcsStr,
                conn);
        T9FlowType flowType = (T9FlowType) orm.loadObjSingle(conn, T9FlowType.class, flowRun.getFlowId());
        // ------------------------------------------- 转出条件检查
        // ----------------------------------
        T9TurnConditionUtility turnUtility = new T9TurnConditionUtility();
        Map formData = turnUtility.getForm(flowType.getFormSeqId(), runId, flowRun.getFlowId(), conn);

        if (!isManage) {
            String notPass = turnUtility.checkCondition(loginUser, formData, flowProcess, false, runId,
                    prcsId, Integer.parseInt(flowPrcsStr), conn);
            if (!"setOk".equals(notPass)) {
                r.put("notPass", notPass);
                return r;
            }
        }
        r.put("flowName", flowType.getFlowName());
        String syncDeal = flowProcess.getSyncDeal();
        if (T9Utility.isNullorEmpty(syncDeal)) {
            syncDeal = "0";
        }
        String gatherNode = flowProcess.getGatherNode();
        if (T9Utility.isNullorEmpty(gatherNode)) {
            gatherNode = "0";
        }
        r.put("prcsName", flowProcess.getPrcsName());
        r.put("syncDeal", syncDeal);

        String runName = flowRun.getRunName();
        Map frm = new HashMap();
        r.put("flowRun", frm);
        frm.put("runName", runName);
        frm.put("flowType", flowType.getFlowType());
        T9PersonLogic logic = new T9PersonLogic();
        String beginUser = logic.getNameBySeqIdStr(flowRun.getBeginUser() + "", conn);
        frm.put("beginUser", beginUser);
        frm.put("isAllowTurn", flowProcess.getTurnPriv() != null ? flowProcess.getTurnPriv() : "0");

        // 取得所有经办人的办理信息

        if ("1".equals(gatherNode)) {
            int iflowPrc = Integer.parseInt(flowPrcsStr);
            String query1 = "select " + " PRCS_ID  " + "  from  " + "  FLOW_PROCESS  " + " where  "
                    + " FLOW_SEQ_ID='" + flowRun.getFlowId() + "' " + "  and  ("
                    + T9DBUtility.findInSet(flowPrcsStr, "PRCS_TO") + " or (PRCS_ID = " + (iflowPrc - 1)
                    + " AND PRCS_TO=''))";
            Statement stm3 = null;
            ResultSet rs3 = null;
            try {
                stm3 = conn.createStatement();
                rs3 = stm3.executeQuery(query1);
                while (rs3.next()) {
                    int prcsId1 = rs3.getInt("PRCS_ID");
                    String query2 = "select PRCS_FLAG from FLOW_RUN_PRCS WHERE RUN_ID='" + runId
                            + "' AND FLOW_PRCS='" + prcsId1 + "' and OP_FLAG='1'";
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsFlag1 = "1";
                    boolean cannotTurn = false;
                    try {
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(query2);
                        if (rs4.next()) {
                            prcsFlag1 = rs4.getString("PRCS_FLAG");
                            if (T9Utility.isInteger(prcsFlag1) && Integer.parseInt(prcsFlag1) <= 2) {
                                cannotTurn = true;
                            }
                        } else {
                            cannotTurn = false;
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    if (!T9WorkFlowUtility.findId(parentStr, prcsId1 + "") && cannotTurn) {
                        r.put("gatherNode", "1");
                        return r;
                    }
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stm3, rs3, null);
            }
        }
        boolean flag = this.getHandlerState(flowRunPrcList, loginUser, Integer.parseInt(flowPrcsStr), conn,
                isManage, frm);
        if (!flag) {
            return r;
        }
        // 取得下一步的信息
        List<T9FlowProcess> nextPrcs = flowProcessLogic.getNextProcess(flowProcess, conn);
        List<Map> nextPrcsList = new ArrayList();
        for (T9FlowProcess tmp : nextPrcs) {
            Map p = new HashMap();
            p.put("seqId", tmp.getSeqId());
            p.put("prcsId", tmp.getPrcsId());
            p.put("childFlow", tmp.getChildFlow());

            int parentFlowId = 0;
            String prcsBack = "";
            String backUserOp = "";
            String backUser = "";
            p.put("parentRun", flowRun.getParentRun());

            if (tmp.getPrcsId() == 0 && flowRun.getParentRun() != 0) {
                T9FlowRunUtility u = new T9FlowRunUtility();
                parentFlowId = u.getFlowId(conn, flowRun.getParentRun());
                String query3 = "select FLOW_PRCS FROM FLOW_RUN_PRCS WHERE RUN_ID='" + flowRun.getParentRun()
                        + "' AND CHILD_RUN='" + runId + "'";
                Statement stm = null;
                ResultSet rs = null;
                int flowPrcs = 0;
                try {
                    stm = conn.createStatement();
                    rs = stm.executeQuery(query3);
                    if (rs.next()) {
                        flowPrcs = rs.getInt("FLOW_PRCS");
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm, rs, null);
                }
                p.put("parentFlowPrcs", flowPrcs);
                p.put("parentFlowId", parentFlowId);
                p.put("prcsName", "结束子流程");

                String query5 = "select PRCS_TO,AUTO_USER_OP,AUTO_USER FROM FLOW_PROCESS WHERE FLOW_SEQ_ID='"
                        + parentFlowId + "' AND PRCS_ID='" + flowPrcs + "'";
                Statement stm6 = null;
                ResultSet rs6 = null;
                try {
                    stm6 = conn.createStatement();
                    rs6 = stm6.executeQuery(query5);
                    if (rs6.next()) {
                        prcsBack = rs6.getString("PRCS_TO");
                        backUserOp = T9Utility.null2Empty(rs6.getString("AUTO_USER_OP"));
                        backUser = T9Utility.null2Empty(rs6.getString("AUTO_USER"));
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm6, rs6, null);
                }
                if (T9Utility.isNullorEmpty(prcsBack)) {
                    prcsBack = "";
                }
                p.put("prcsBack", prcsBack);
            } else {
                if (tmp.getChildFlow() != 0) {
                    p.put("prcsName", tmp.getPrcsName() + "(子流程)");
                } else {
                    p.put("prcsName", tmp.getPrcsName());
                }
            }

            String topFlag = (tmp.getTopDefault() == null ? "0" : tmp.getTopDefault());
            String userFilter = (tmp.getUserFilter() == null ? "0" : tmp.getUserFilter());
            // 检查转入条件
            String notInPass = turnUtility.checkCondition(loginUser, formData, tmp, true, runId, prcsId,
                    Integer.parseInt(flowPrcsStr), conn);
            boolean userLock = false;
            if ("0".equals(tmp.getUserLock())) {
                userLock = true;
            }
            if (!"setOk".equals(notInPass)) {
                p.put("notInPass", notInPass);
            }
            // 取得下一步中的默认人员
            if (tmp.getPrcsId() == 0 && flowRun.getParentRun() != 0) {
                turnUtility.userSelect2(conn, loginUser, prcsBack, parentFlowId, backUserOp, backUser, p);
            } else {
                turnUtility.userSelect(loginUser, tmp, flowRun, conn, p);
            }

            p.put("userFilter", userFilter);
            p.put("userLock", userLock);
            p.put("topFlag", topFlag);
            nextPrcsList.add(p);
        }
        r.put("nextPrcs", nextPrcsList);
        return r;
    }

    /**
     * 根据userId取得用户名
     * 
     * @param userId
     * @param conn
     * @return
     * @throws Exception
     */
    public String getUserName(int userId, Connection conn) throws Exception {
        String query4 = "select  " + " USER_NAME  " + " from PERSON where  " + " SEQ_ID = " + userId;
        Statement stm4 = null;
        ResultSet rs4 = null;
        try {
            stm4 = conn.createStatement();
            rs4 = stm4.executeQuery(query4);
            if (rs4.next()) {
                String name = rs4.getString("USER_NAME");
                return name;
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm4, rs4, null);
        }
    }

    public Map getFreeTurnData(T9Person loginUser, int flowId, int runId, int prcsId, Connection conn,
            boolean isManage) throws Exception {
        // TODO Auto-generated method stub
        Map r = new HashMap();
        T9ORM orm = new T9ORM();
        String formItem = "";
        T9FlowTypeLogic flowTypelogic = new T9FlowTypeLogic();
        T9FlowType ft = flowTypelogic.getFlowTypeById(flowId, conn);
        if (prcsId == 1) {
            T9FlowFormLogic ffLogic = new T9FlowFormLogic();
            String flowDoc = ft.getFlowDoc();
            int formId = ft.getFormSeqId();
            formItem = ffLogic.getTitle(conn, formId);

            if (flowDoc.equals("1")) {
                formItem += ",[A@],";
            } else {
                formItem += ",[B@],";
            }
        } else {
            Map map = new HashMap();
            map.put("RUN_ID", runId);
            map.put("PRCS_ID", prcsId);
            map.put("OP_FLAG", 1);
            T9FlowRunPrcs runPrcs = (T9FlowRunPrcs) orm.loadObjSingle(conn, T9FlowRunPrcs.class, map);
            if (runPrcs != null) {
                formItem = runPrcs.getFreeItem();
            }
        }
        // 取出流程名以及流程中的预设自段

        String flowName = "";
        boolean freePreSet = false;
        if (ft != null) {
            flowName = ft.getFlowName();
            if (ft.getFreePreset() != null && "1".equals(ft.getFreePreset())) {
                freePreSet = true;
            }
        }
        // 取出实例 名

        String query2 = "SELECT  " + " RUN_NAME,USER_NAME  " + " from FLOW_RUN,PERSON WHERE  "
                + " PERSON.SEQ_ID = FLOW_RUN.BEGIN_USER AND RUN_ID=" + runId;
        String runName = "";
        String userName = "";
        Statement stm1 = null;
        ResultSet rs1 = null;
        try {
            stm1 = conn.createStatement();
            rs1 = stm1.executeQuery(query2);
            if (rs1.next()) {
                runName = rs1.getString("RUN_NAME");
                userName = rs1.getString("USER_NAME");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm1, rs1, null);
        }
        r.put("runName", runName);
        r.put("beginUser", userName);
        r.put("isPreview", freePreSet);
        r.put("formItem", formItem);

        r.put("nextPrcs", prcsId + 1);

        String PRCS_USER_OP_ZB = "";
        String PRCS_USER_OP_CB = "";

        String preSet = "";
        String queryPre = "SELECT * from FLOW_RUN_PRCS where " + "  RUN_ID= " + runId + "  "
                + " and PRCS_ID=" + (prcsId + 1) + " and PRCS_FLAG='5'  " + " order by OP_FLAG desc";
        Statement stm5 = null;
        ResultSet rs5 = null;
        try {
            stm5 = conn.createStatement();
            rs5 = stm5.executeQuery(queryPre);
            while (rs5.next()) {
                Map m = new HashMap();

                int userId = rs5.getInt("USER_ID");
                int opFlag = Integer.parseInt(rs5.getString("OP_FLAG"));
                String userName2 = this.getUserName(userId, conn);
                if (userName2 != null) {
                    if (opFlag == 1) {
                        PRCS_USER_OP_ZB = PRCS_USER_OP_CB = "<em  userid='" + userId + "'>" + userName2
                                + "<span>—</span></em>";
                    } else {
                        PRCS_USER_OP_CB += "<em userid='" + userId + "'>" + userName2 + "<span>—</span></em>";
                    }
                    preSet += userName2 + ",";
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm5, rs5, null);
        }
        List<Map> users = new ArrayList();
        String query = "SELECT SEQ_ID , USER_ID, USER_NAME, DEPT_ID from PERSON WHERE DEPT_ID != '0'";
        try {
            stm5 = conn.createStatement();
            rs5 = stm5.executeQuery(query);
            while (rs5.next()) {
                Map u = new HashMap();

                int seqId = rs5.getInt("SEQ_ID");
                String userName2 = rs5.getString("USER_NAME");
                String userId = rs5.getString("USER_ID");
                int deptId = rs5.getInt("DEPT_ID");
                String deptName = T9MobileUtility.getLongDept(conn, deptId);

                u.put("userId", userId);
                u.put("userName", userName2);
                u.put("deptName", deptName);
                u.put("seqId", seqId);
                users.add(u);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm5, rs5, null);
        }
        r.put("users", users);
        r.put("PRCS_USER_OP_ZB", PRCS_USER_OP_ZB);
        r.put("PRCS_USER_OP_CB", PRCS_USER_OP_CB);
        r.put("preSet", preSet);

        return r;
    }

    public Map getTurnUser(T9Person person, int runId, int prcsId, String flowPrcsStr, Connection conn,
            boolean isManage, String pRCS_ID_NEXT, int flowId) throws Exception {
        // TODO Auto-generated method stub
        List<Map> list = new ArrayList();
        String[] prcsNext = pRCS_ID_NEXT.split(",");
        if (pRCS_ID_NEXT.endsWith(",")) {
            pRCS_ID_NEXT = pRCS_ID_NEXT.substring(0, pRCS_ID_NEXT.length() - 1);
        }
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        T9FlowRun flowRun = flowRunLogic.getFlowRunByRunId(runId, conn);

        T9FlowProcessLogic flowProcessLogic = new T9FlowProcessLogic();
        T9FlowProcess flowProcess = flowProcessLogic.getFlowProcessById(flowRun.getFlowId(), flowPrcsStr,
                conn);

        int childFlow = 0;
        if (T9Utility.isNullorEmpty(pRCS_ID_NEXT)) {
            return null;
        }

        String query5 = "select CHILD_FLOW FROM FLOW_PROCESS WHERE (CHILD_FLOW is not null and CHILD_FLOW != '0') and FLOW_SEQ_ID='"
                + flowId + "' AND PRCS_ID in (" + pRCS_ID_NEXT + ")";
        Statement stm6 = null;
        ResultSet rs6 = null;

        try {
            stm6 = conn.createStatement();
            rs6 = stm6.executeQuery(query5);
            if (rs6.next()) {
                flowId = rs6.getInt("CHILD_FLOW");
                childFlow = flowId;
                pRCS_ID_NEXT = "1";
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm6, rs6, null);
        }
        T9ORM orm = new T9ORM();
        int count = 0;
        query5 = "select * FROM FLOW_PROCESS WHERE FLOW_SEQ_ID='" + flowId + "' AND PRCS_ID in ("
                + pRCS_ID_NEXT + ")";
        try {
            stm6 = conn.createStatement();
            rs6 = stm6.executeQuery(query5);
            while (rs6.next()) {
                Map m = new HashMap();

                String topDefault = rs6.getString("TOP_DEFAULT");
                m.put("topDefault", topDefault);
                String userLock = rs6.getString("USER_LOCK");
                m.put("userLock", userLock);

                Map filters = new HashMap();
                String PRCS_NAME = "";
                filters.put("FLOW_SEQ_ID", flowId);
                if (childFlow != 0) {
                    filters.put("PRCS_ID", 1);
                    String query3 = "select PRCS_NAME from FLOW_PROCESS where FLOW_SEQ_ID = '" + flowId
                            + "' and PRCS_ID='" + prcsNext[count] + "'";
                    Statement stm7 = null;
                    ResultSet rs7 = null;
                    String prcsName = "";
                    try {
                        stm7 = conn.createStatement();
                        rs7 = stm7.executeQuery(query3);
                        if (rs7.next()) {
                            prcsName = rs7.getString("PRCS_NAME");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm7, rs7, null);
                    }
                } else {
                    filters.put("PRCS_ID", prcsNext[count]);
                }
                T9FlowProcess prcs = (T9FlowProcess) orm.loadObjSingle(conn, T9FlowProcess.class, filters);
                T9TurnConditionUtility tc = new T9TurnConditionUtility();
                Map u = new HashMap();
                tc.userSelect(person, prcs, flowRun, conn, u);
                this.mapToUserList(conn, m, u);

                // 如果是自动选人
                m.put("prcsName", prcs.getPrcsName());
                m.put("prcsNext", prcsNext[count]);
                m.put("users", this.getPrcsUser(conn, prcs.getPrcsUser(), prcs.getPrcsPriv(),
                        prcs.getPrcsDept(), person, prcs.getUserFilter()));
                count++;
                list.add(m);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm6, rs6, null);
        }
        Map r = new HashMap();
        r.put("list", list);
        Map queryPrcs = new HashMap();
        queryPrcs.put("RUN_ID", runId);
        queryPrcs.put("PRCS_ID", prcsId);
        queryPrcs.put("FLOW_PRCS", Integer.parseInt(flowPrcsStr));
        List<T9FlowRunPrcs> flowRunPrcList = orm.loadListSingle(conn, T9FlowRunPrcs.class, queryPrcs);
        this.getHandlerState(flowRunPrcList, person, Integer.parseInt(flowPrcsStr), conn, isManage, r);
        r.put("isAllowTurn", flowProcess.getTurnPriv() != null ? flowProcess.getTurnPriv() : "0");
        r.put("runName", flowRun.getRunName());
        String query2 = "SELECT  " + " RUN_NAME,USER_NAME  " + " from FLOW_RUN,PERSON WHERE  "
                + " PERSON.SEQ_ID = FLOW_RUN.BEGIN_USER AND RUN_ID=" + runId;
        String userName = "";
        Statement stm1 = null;
        ResultSet rs1 = null;
        try {
            stm1 = conn.createStatement();
            rs1 = stm1.executeQuery(query2);
            if (rs1.next()) {
                userName = rs1.getString("USER_NAME");
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm1, rs1, null);
        }

        r.put("beginUser", userName);
        return r;
    }

    public List<String[]> getPrcsUser(Connection conn, String privUser, String privRole, String privDept,
            T9Person person, String userFilter) throws Exception {
        String query = "SELECT SEQ_ID,USER_ID,USER_NAME,DEPT_ID FROM PERSON WHERE 1=1 and DEPT_ID!=0";
        privUser = T9WorkFlowUtility.getOutOfTail(privUser);
        privRole = T9WorkFlowUtility.getOutOfTail(privRole);
        privDept = T9WorkFlowUtility.getOutOfTail(privDept);

        if (!"ALL_DEPT".equals(privDept) && !"0".equals(privDept)) {
            String where = "";
            if (!T9Utility.isNullorEmpty(privUser)) {
                where += " SEQ_ID IN (" + privUser + ") OR";
            }
            if (!T9Utility.isNullorEmpty(privDept)) {
                where += " DEPT_ID IN (" + privDept + ") OR";
            }
            if (!T9Utility.isNullorEmpty(privRole)) {
                String s = T9WorkFlowUtility.getInStr(privRole);
                where += " USER_PRIV IN (" + s + ") OR";
            }
            if (where.endsWith("OR")) {
                where = T9WorkFlowUtility.getOutOfTail(where, "OR");
            }

            if (T9Utility.isNullorEmpty(where)) {
                where += " 1!=1 ";
            } else {
                where += " or 1!=1 ";
            }
            where += T9MobileUtility.privOtherSql("USER_PRIV_OTHER", privRole);
            where += T9MobileUtility.privOtherSql("DEPT_ID_OTHER", privDept);
            if (!T9Utility.isNullorEmpty(where))
                query += " and (" + where + ")";
        }
        // System.out.println(query);
        Statement stm3 = null;
        ResultSet rs3 = null;
        List<String[]> list = new ArrayList();
        try {
            stm3 = conn.createStatement();
            rs3 = stm3.executeQuery(query);

            while (rs3.next()) {
                String[] str = new String[4];
                String userId = rs3.getString("USER_ID");
                String userName = rs3.getString("USER_NAME");
                String seqId = rs3.getString("SEQ_ID");

                int deptId = rs3.getInt("DEPT_ID");
                String deptName = T9MobileUtility.getLongDept(conn, deptId);

                str[0] = seqId;
                str[1] = userId;
                str[2] = userName;
                str[3] = deptName;
                // System.out.println(userFilter);
                if ("1".equals(userFilter)) {// 只允许选择本部门经办人
                    if (deptId != person.getDeptId()) {
                        continue;
                    }
                }
                list.add(str);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm3, rs3, null);
        }
        return list;
    }

    public List<String[]> getUserList(Connection conn, String prcsUser) throws Exception {
        if (T9Utility.isNullorEmpty(prcsUser)) {
            return new ArrayList<String[]>();
        }
        if (prcsUser.endsWith(","))
            prcsUser = prcsUser.substring(0, prcsUser.length() - 1);

        Statement stm3 = null;
        ResultSet rs3 = null;
        List<String[]> list = new ArrayList();
        try {
            stm3 = conn.createStatement();
            rs3 = stm3.executeQuery("select SEQ_ID , USER_ID , USER_NAME FROM PERSON WHERE SEQ_ID IN ("
                    + prcsUser + ")");

            while (rs3.next()) {
                String[] str = new String[3];
                String userId = String.valueOf(rs3.getInt("USER_ID"));
                String userName = rs3.getString("USER_NAME");
                String seqId = rs3.getString("SEQ_ID");
                str[0] = seqId;
                str[1] = userId;
                str[2] = userName;
                list.add(str);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm3, rs3, null);
        }
        return list;
    }

    public void mapToUserList(Connection conn, Map m, Map u) throws Exception {
        if (!m.containsKey("isAutoSelect")) {
            u.put("prcsOpUser", this.getUserList(conn, ""));
            u.put("prcsUser", this.getUserList(conn, ""));
            return;
        }
        boolean isAutoSelect = (Boolean) m.get("isAutoSelect");
        String prcsOpUser = (String) m.get("prcsOpUser");
        String prcsUser = (String) m.get("prcsUser");

        if (isAutoSelect) {
            u.put("prcsOpUser", this.getUserList(conn, prcsOpUser));
            u.put("prcsUser", this.getUserList(conn, prcsUser));
        } else {
            u.put("prcsOpUser", this.getUserList(conn, ""));
            u.put("prcsUser", this.getUserList(conn, ""));
        }
    }

    public static boolean turnNext(Connection conn, int runId, int prcsId, int flowPrcs, int userId)
            throws Exception {
        String query = "select 1 FROM FLOW_RUN_PRCS WHERE RUN_ID='" + runId + "' AND PRCS_ID='" + prcsId
                + "' AND FLOW_PRCS='" + flowPrcs + "' AND USER_ID<>'" + userId + "' AND PRCS_FLAG IN(1,2)";
        Statement stm3 = null;
        ResultSet rs3 = null;
        try {
            stm3 = conn.createStatement();
            rs3 = stm3.executeQuery(query);
            if (!rs3.next()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm3, rs3, null);
        }
    }

    public static String getFlowType(Connection conn, int flowId) throws Exception {
        String query = "select FLOW_TYPE FROM FLOW_TYPE WHERE SEQ_ID = '" + flowId + "'";
        Statement stm3 = null;
        ResultSet rs3 = null;
        try {
            stm3 = conn.createStatement();
            rs3 = stm3.executeQuery(query);
            if (rs3.next()) {
                return rs3.getString(1);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm3, rs3, null);
        }
        return "1";
    }

    public static void updateFlowPrcs(Connection conn, String sql) throws Exception {
        PreparedStatement stm3 = null;
        try {
            stm3 = conn.prepareStatement(sql);
            stm3.setTimestamp(1, new Timestamp(new Date().getTime()));
            stm3.executeUpdate();
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm3, null, null);
        }
    }
}
