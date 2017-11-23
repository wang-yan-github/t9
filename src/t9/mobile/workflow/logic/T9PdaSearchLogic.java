package t9.mobile.workflow.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9PdaSearchLogic {

    public Map search(Connection conn, T9Person person, String searchName, String lastedId) throws Exception {
        String queryStr = "";
        if (!T9Utility.isNullorEmpty(searchName)) {
            queryStr += " and (FLOW_RUN.RUN_NAME like '%" + searchName + "%' OR FLOW_RUN.RUN_ID='"
                    + searchName + "') ";
        }
        if (!T9Utility.isNullorEmpty(lastedId)) {
            queryStr += " and FLOW_RUN_PRCS.SEQ_ID < " + lastedId;
        }
        Map r = new HashMap();
        List<Map> list = new ArrayList();
        // String q = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE "
        // + "WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID "
        // + "and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and USER_ID='" +
        // person.getSeqId()
        // + "' and PRCS_FLAG < '5' " + "and FLOW_RUN.DEL_FLAG=0 " + queryStr;
        // int totalItems = T9QuickQuery.getCount(conn, q);
        // if (totalItems > 0) {
        String q2 = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE p.seq_id = FLOW_RUN.begin_user and FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID "
                + " and FLOW_RUN_PRCS.PRCS_ID = (SELECT MAX(f.prcs_id) from FLOW_RUN_PRCS f where f.USER_ID = FLOW_RUN_PRCS.USER_ID AND f.RUN_ID = FLOW_RUN.RUN_ID)  "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'5' " + queryStr
                + " order by FLOW_RUN_PRCS.PRCS_FLAG asc, FLOW_RUN_PRCS.SEQ_ID desc limit 7 ";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(q2);
            while (rs.next()) {
                Map m = new HashMap();
                int prcsId = rs.getInt("PRCS_ID");
                int runId = rs.getInt("RUN_ID");
                int flowId = rs.getInt("FLOW_ID");
                int flowPrcs = rs.getInt("FLOW_PRCS");
                m.put("SEQ_ID", rs.getInt("FLOW_RUN_PRCS.SEQ_ID"));
                m.put("prcsId", prcsId);
                m.put("runId", runId);
                m.put("flowId", flowId);
                m.put("flowPrcs", flowPrcs);

                String prcsFlag = rs.getString("PRCS_FLAG");
                String opFlag = rs.getString("OP_FLAG");
                m.put("opFlag", opFlag);
                String runName = T9Utility.null2Empty(rs.getString("RUN_NAME")).replace(searchName,
                        "<font color='red'>" + searchName + "</font>");
                String flowName = rs.getString("FLOW_NAME");
                String flowType = rs.getString("FLOW_TYPE");
                String createTime = T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME"));

                String opFlagDesc = "会签";
                if ("1".equals(opFlag))
                    opFlagDesc = "主办";

                String status = "";
                String COLOR = "";
                String Class = "";
                if ("1".equals(prcsFlag)) {
                    status = "未接收";
                    COLOR = "#FFBC18";
                    Class = " active";
                } else if ("2".equals(prcsFlag)) {
                    status = "已接收";
                    COLOR = "#50C625";
                    Class = " received";
                } else if ("3".equals(prcsFlag)) {
                    status = "已转交";
                    COLOR = "#F4A8BD";
                    Class = " referred";
                } else if ("4".equals(prcsFlag)) {
                    status = "已办结";
                    COLOR = "#F4A8BD";
                    Class = " gone";
                }
                m.put("userName", rs.getString("USER_NAME"));
                Date date = rs.getDate("END_TIME");
                String state = "办理中";
                if (date != null) {
                    state = "已结束";
                }
                m.put("state", state);
                status = "<span style='color:" + COLOR + "'>" + status + "</span>";
                String feedback = "0";
                String prcsName = "第" + prcsId + "步";
                if ("1".equals(flowType)) {
                    Statement stmt1 = null;
                    ResultSet rs1 = null;
                    try {
                        stmt1 = conn.createStatement();
                        rs1 = stmt1
                                .executeQuery("SELECT PRCS_NAME,FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID='"
                                        + flowId + "' AND PRCS_ID='" + prcsId + "'");
                        if (rs1.next()) {
                            prcsName += rs1.getString(1);
                            feedback = rs.getString(2);
                        }
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        T9DBUtility.close(stmt1, rs1, null);
                    }
                }
                m.put("CLASS", Class);
                m.put("createTime", createTime);
                m.put("flowName", flowName);
                m.put("runName", runName);
                m.put("prcsName", prcsName);
                m.put("status", status);
                m.put("opFlagDesc", opFlagDesc);
                list.add(m);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        // }
        // r.put("totalItems", totalItems);
        r.put("list", list);
        return r;
    }
}
