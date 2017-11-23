package t9.mobile.workflow.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRunPrcs;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;

public class T9PdaBackLogic {
    public static List<Map> getBackData(Connection conn, T9Person person, String runIdStr, String prcsStr,
            String flowIdStr, String flowPrcsStr) throws Exception {
        int runId = Integer.parseInt(runIdStr);
        int prcsId = Integer.parseInt(prcsStr);
        int flowId = Integer.parseInt(flowIdStr);
        int flowPrcsTemp = Integer.parseInt(flowPrcsStr);
        List<Map> list2 = new ArrayList();
        List<T9FlowRunPrcs> list = new ArrayList();
        String query = "select " + "COUNT(distinct PRCS_ID)" + ",PRCS_ID" + ",FLOW_PRCS "
                + ",count(distinct PRCS_ID) " + " from FLOW_RUN_PRCS where " + " RUN_ID=" + runId
                + " and PRCS_ID!=" + prcsId + " and FLOW_PRCS<" + flowPrcsTemp
                + " GROUP BY prcs_id , flow_prcs";
        Statement stm1 = null;
        ResultSet rs1 = null;
        String prcsIdStr = "";
        try {
            stm1 = conn.createStatement();
            rs1 = stm1.executeQuery(query);
            while (rs1.next()) {
                int flowPrcs = rs1.getInt("FLOW_PRCS");
                if (!T9WorkFlowUtility.findId(prcsIdStr, String.valueOf(flowPrcs))) {
                    T9FlowRunPrcs rp = new T9FlowRunPrcs();
                    rp.setPrcsId(rs1.getInt("PRCS_ID"));
                    rp.setFlowPrcs(flowPrcs);
                    prcsIdStr += flowPrcs + ",";
                    list.add(rp);
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            T9DBUtility.close(stm1, rs1, null);
        }
        for (T9FlowRunPrcs runPrcs : list) {
            if (runPrcs.getPrcsId() != prcsId) {
                String prcsName = "";
                query = "select " + " PRCS_NAME " + " from FLOW_PROCESS where " + " FLOW_SEQ_ID =" + flowId
                        + " and PRCS_ID =" + runPrcs.getFlowPrcs();
                Statement stm2 = null;
                ResultSet rs2 = null;
                try {
                    stm2 = conn.createStatement();
                    rs2 = stm2.executeQuery(query);
                    if (rs2.next()) {
                        prcsName = rs2.getString("PRCS_NAME");
                    }
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    T9DBUtility.close(stm2, rs2, null);
                }
                // sb.append(",prcsName:'" + prcsName + "',flowPrcsId:" +
                // runPrcs.getFlowPrcs());
                Map m = new HashMap();
                m.put("FLOW_ID", flowIdStr);
                m.put("FLOW_PRCS", String.valueOf(runPrcs.getPrcsId()));
                m.put("PRCS_NAME", prcsName);
                list2.add(m);
            }
        }
        return list2;
    }

    public static String getAllowBack(Connection conn, int flowId, int flowPrcs) throws Exception {
        String query = "SELECT ALLOW_BACK from FLOW_PROCESS WHERE FLOW_SEQ_ID='" + flowId + "' AND PRCS_ID='"
                + flowPrcs + "'";
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
        return "0";
    }
}
