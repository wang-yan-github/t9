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
import t9.core.funcs.workflow.act.T9FlowRunAct;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowSortLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaWorkFlowLogic {
    /**
     * 用户所有的待办数据
     * 
     * @param conn
     * @param person
     * @param whereSql
     * @return
     * @throws Exception
     */
    public int listFlowDBTotal(Connection conn, T9Person person, String whereSql) throws Exception {
        String query_total = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE p.seq_id = FLOW_RUN.begin_user and FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID   "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' " + whereSql;
        int total_items = T9QuickQuery.getCount(conn, query_total);
        return total_items;
    }

    /**
     * 用户所有的待办数据
     * 
     * @param conn
     * @param person
     * @param whereSql
     * @return
     * @throws Exception
     */
    public Map listDB(Connection conn, T9Person person, String whereSql) throws Exception {
        Map m = new HashMap();
        List<Map> list = new ArrayList();
        String query_total = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE p.seq_id = FLOW_RUN.begin_user and FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' " + whereSql;
        int total_items = T9QuickQuery.getCount(conn, query_total);
        m.put("total_items", total_items);

        String query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE p.seq_id = FLOW_RUN.begin_user and FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID   "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' " + whereSql
                + " order by FLOW_RUN_PRCS.SEQ_ID desc limit 7";
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map mapTmp = new HashMap();
                mapTmp.put("SEQ_ID", rs.getInt("FLOW_RUN_PRCS.SEQ_ID"));
                mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                mapTmp.put("CREATE_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME")));
                mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));

                String OP_FLAG_DESC = "会签";
                if (rs.getInt("OP_FLAG") == 1) {
                    OP_FLAG_DESC = "主办";
                }
                mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                String divClass = "";
                String status = "";
                if (rs.getInt("PRCS_FLAG") == 1) {
                    divClass = " active";
                    status = "未接收";
                } else if (rs.getInt("PRCS_FLAG") == 2) {
                    status = "已接收";
                }
                mapTmp.put("STATUS", status);
                Date date = rs.getDate("END_TIME");
                String state = "办理中";
                if (date != null) {
                    state = "已结束";
                }
                mapTmp.put("userName", rs.getString("USER_NAME"));
                mapTmp.put("state", state);
                mapTmp.put("CLASS", divClass);
                String feedBack = "0";
                if ("1".equals(rs.getString("FLOW_TYPE"))) {
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsName = "流程步骤已删除";
                    try {
                        String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(queryStr);
                        if (rs4.next()) {
                            prcsName = rs4.getString("PRCS_NAME");
                            feedBack = rs4.getString("FEEDBACK");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    mapTmp.put("PRCS_NAME", prcsName);
                } else {
                    mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                }
                mapTmp.put("FEEDBACK", feedBack);
                list.add(mapTmp);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        // list.addAll(list);
        // list.addAll(list);
        m.put("list", list);
        return m;
    }

    /**
     * 用户所有的已办数据
     * 
     * @param conn
     * @param person
     * @param whereSql
     * @return
     * @throws Exception
     */
    public Map listYB(Connection conn, T9Person person, String whereSql) throws Exception {
        Map m = new HashMap();
        List<Map> list = new ArrayList();
        String query_total = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID   "
                + " and FLOW_RUN_PRCS.PRCS_ID = (SELECT MAX(f.prcs_id) from FLOW_RUN_PRCS f where f.USER_ID = FLOW_RUN_PRCS.USER_ID AND f.RUN_ID = FLOW_RUN.RUN_ID)  "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG>='3' " + whereSql;
        int total_items = T9QuickQuery.getCount(conn, query_total);
        m.put("total_items", total_items);

        String query = "SELECT *,p.user_name from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE,person p "
                + " WHERE p.seq_id = FLOW_RUN.begin_user and FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID   "
                + " and FLOW_RUN_PRCS.PRCS_ID = (SELECT MAX(f.prcs_id) from FLOW_RUN_PRCS f where f.USER_ID = FLOW_RUN_PRCS.USER_ID AND f.RUN_ID = FLOW_RUN.RUN_ID)  "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and FLOW_RUN_PRCS.USER_ID='" + person.getSeqId()
                + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG>='3' " + whereSql
                + " order by FLOW_RUN_PRCS.SEQ_ID desc limit 7";
        System.out.println(query);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map mapTmp = new HashMap();
                mapTmp.put("SEQ_ID", rs.getInt("FLOW_RUN_PRCS.SEQ_ID"));
                mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                mapTmp.put("CREATE_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME")));
                mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));
                Date date = rs.getDate("END_TIME");
                String state = "办理中";
                if (date != null) {
                    state = "已结束";
                }
                mapTmp.put("state", state);
                String OP_FLAG_DESC = "会签";
                if (rs.getInt("OP_FLAG") == 1) {
                    OP_FLAG_DESC = "主办";
                }
                mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                String divClass = "";
                String status = "";
                if (rs.getInt("PRCS_FLAG") == 1) {
                    divClass = " active";
                    status = "未接收";
                } else if (rs.getInt("PRCS_FLAG") == 2) {
                    status = "已接收";
                }
                mapTmp.put("userName", rs.getString("USER_NAME"));
                mapTmp.put("STATUS", status);
                mapTmp.put("CLASS", divClass);
                String feedBack = "0";
                if ("1".equals(rs.getString("FLOW_TYPE"))) {
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsName = "流程步骤已删除";
                    try {
                        String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(queryStr);
                        if (rs4.next()) {
                            prcsName = rs4.getString("PRCS_NAME");
                            feedBack = rs4.getString("FEEDBACK");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    mapTmp.put("PRCS_NAME", prcsName);
                } else {
                    mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                }
                mapTmp.put("FEEDBACK", feedBack);
                list.add(mapTmp);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        m.put("list", list);
        return m;
    }

    /**
     * 用户所有的已办数据
     * 
     * @param conn
     * @param person
     * @param whereSql
     * @return
     * @throws Exception
     */
    public Map listApply(Connection conn, T9Person person, String lastId, String type) throws Exception {
        Map m = new HashMap();
        List<Map> list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        StringBuffer sb_total = new StringBuffer();
        StringBuffer sb_sql = new StringBuffer();
        sb_total.append("SELECT count(*) ");
        sb_sql.append("SELECT a.SEQ_ID,");
        sb_sql.append(" a.RUN_ID,");
        sb_sql.append(" a.RUN_NAME,");
        sb_sql.append(" a.FLOW_ID,");
        sb_sql.append(" a.BEGIN_USER,");
        sb_sql.append(" a.BEGIN_TIME,");
        sb_sql.append(" a.END_TIME,");
        sb_sql.append(" b.PRCS_ID,");
        sb_sql.append(" b.PRCS_FLAG,");
        sb_sql.append(" b.FLOW_PRCS,");
        sb_sql.append(" b.OP_FLAG,");
        sb_sql.append(" b.TOP_FLAG,");
        sb_sql.append(" c.FLOW_NAME,");
        sb_sql.append(" c.flow_type,");
        sb_sql.append(" d.USER_NAME as BEGIN_USER_NAME,");
        sb_sql.append(" e.USER_NAME");
        sb.append(" FROM");
        sb.append(" flow_run a");
        sb.append(" LEFT JOIN flow_run_prcs b ON a.RUN_ID = b.RUN_ID");
        sb.append(" LEFT JOIN flow_type c ON a.FLOW_ID = c.SEQ_ID");
        sb.append(" LEFT JOIN person d ON a.BEGIN_USER = d.SEQ_ID");
        sb.append(" LEFT JOIN person e ON b.USER_ID = e.SEQ_ID");
        sb.append(" WHERE");
        sb.append(" a.DEL_FLAG = 0");
        if (type.equals("1")) {// 审批中
            sb.append(" AND a.END_TIME IS NULL");
        } else if (type.equals("2")) {// 已完成
            sb.append(" AND a.END_TIME IS NOT NULL");
        } else {
            m.put("total_items", 0);
            m.put("list", null);
            return m;
        }
        if (lastId != null && !lastId.equals("")) {
            sb.append(" AND a.SEQ_ID > ").append(lastId);
        }
        sb.append(" AND PRCS_ID = 1");// 只取本人的数据
        // sb.append(" AND PRCS_ID = (");
        // sb.append(" SELECT");
        // sb.append("     MAX(f.prcs_id)");
        // sb.append(" FROM");
        // sb.append("     FLOW_RUN_PRCS f");
        // sb.append(" WHERE");
        // sb.append("     f.RUN_ID = a.RUN_ID AND f.OP_FLAG = 1");
        // sb.append(" )");
        sb.append(" AND OP_FLAG = 1 AND a.BEGIN_USER='").append(person.getSeqId()).append("'");
        sb.append(" ORDER BY");
        sb.append(" a.SEQ_ID DESC");
        sb.append(" LIMIT 7");
        sb_total.append(sb.toString());
        sb_sql.append(sb.toString());
        int total_items = T9QuickQuery.getCount(conn, sb_total.toString());
        m.put("total_items", total_items);
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sb_sql.toString());
            while (rs.next()) {
                Map mapTmp = new HashMap();
                mapTmp.put("SEQ_ID", rs.getInt("SEQ_ID"));
                mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));
                mapTmp.put("END_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("END_TIME")));
                mapTmp.put("BEGIN_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("BEGIN_TIME")));

                String OP_FLAG_DESC = "会签";
                if (rs.getInt("OP_FLAG") == 1) {
                    OP_FLAG_DESC = "主办";
                }
                mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                mapTmp.put("USER_NAME", rs.getString("USER_NAME"));
                mapTmp.put("BEGIN_USER_NAME", rs.getString("BEGIN_USER_NAME"));
                String feedBack = "0";
                if ("1".equals(rs.getString("FLOW_TYPE"))) {
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsName = "流程步骤已删除";
                    try {
                        String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(queryStr);
                        if (rs4.next()) {
                            prcsName = rs4.getString("PRCS_NAME");
                            feedBack = rs4.getString("FEEDBACK");
                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    mapTmp.put("PRCS_NAME", prcsName);
                } else {
                    mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                }
                mapTmp.put("FEEDBACK", feedBack);
                list.add(mapTmp);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        m.put("list", list);
        return m;
    }

    public Map getIndexMap(Connection conn, T9Person person) throws Exception {
        String query = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE "
                + " WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID "
                + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and USER_ID='" + person.getSeqId()
                + "' and PRCS_FLAG < '3' and FLOW_RUN.DEL_FLAG=0";
        int total_items = T9QuickQuery.getCount(conn, query);
        Map m = new HashMap();
        m.put("total_items", total_items);
        if (total_items > 0) {
            List<Map> list = new ArrayList();
            m.put("list", list);
            int c = 0;
            int j = 0;
            query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE "
                    + " WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID   "
                    + "  and FLOW_RUN_PRCS.CHILD_RUN = '0' "
                    + " and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and USER_ID='" + person.getSeqId()
                    + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' "
                    + " order by FLOW_RUN_PRCS.PRCS_FLAG , FLOW_RUN_PRCS.CREATE_TIME desc ";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    if (j < c) {
                        j++;
                        continue;
                    }
                    if (j >= T9MobileConfig.PAGE_SIZE + c)
                        break;
                    Map mapTmp = new HashMap();
                    mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                    mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                    mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                    mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                    mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                    mapTmp.put("CREATE_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME")));
                    mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                    mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                    mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                    mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));

                    String OP_FLAG_DESC = "会签";
                    if (rs.getInt("OP_FLAG") == 1) {
                        OP_FLAG_DESC = "主办";
                    }
                    mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                    String divClass = "";
                    String status = "";
                    if (rs.getInt("PRCS_FLAG") == 1) {
                        divClass = " active";
                        status = "未接收";
                    } else if (rs.getInt("PRCS_FLAG") == 2) {
                        status = "已接收";
                    }
                    mapTmp.put("STATUS", status);
                    mapTmp.put("CLASS", divClass);
                    String feedBack = "0";
                    if ("1".equals(rs.getString("FLOW_TYPE"))) {
                        Statement stm4 = null;
                        ResultSet rs4 = null;
                        String prcsName = "流程步骤已删除";
                        try {
                            String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                    + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                            stm4 = conn.createStatement();
                            rs4 = stm4.executeQuery(queryStr);
                            if (rs4.next()) {
                                prcsName = rs4.getString("PRCS_NAME");
                                feedBack = rs4.getString("FEEDBACK");

                            }
                        } catch (Exception ex) {
                            throw ex;
                        } finally {
                            T9DBUtility.close(stm4, rs4, null);
                        }
                        mapTmp.put("PRCS_NAME", prcsName);
                    } else {
                        mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                    }
                    mapTmp.put("FEEDBACK", feedBack);
                    list.add(mapTmp);
                    j++;
                }
            } catch (Exception e) {
                throw e;
            } finally {
                T9DBUtility.close(stmt, rs, null);
            }
        }
        return m;
    }

    public List<Map<String, String>> getIndexStr(Connection conn, T9Person person, String query,
            String CURRITERMS) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        // StringBuffer sb = new StringBuffer();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        int c = T9MobileUtility.getCURRITERMS(CURRITERMS);
        int j = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                if (j < c) {
                    j++;
                    continue;
                }
                if (j >= T9MobileConfig.PAGE_SIZE + c)
                    break;
                Map mapTmp = new HashMap();
                mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                mapTmp.put("CREATE_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME")));
                mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));

                String OP_FLAG_DESC = "会签";
                if (rs.getInt("OP_FLAG") == 1) {
                    OP_FLAG_DESC = "主办";
                }
                mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                String divClass = "";
                String status = "";
                if (rs.getInt("PRCS_FLAG") == 1) {
                    divClass = " active";
                    status = "未接收";
                } else if (rs.getInt("PRCS_FLAG") == 2) {
                    status = "已接收";
                }
                mapTmp.put("STATUS", status);
                mapTmp.put("CLASS", divClass);
                String feedBack = "0";
                if ("1".equals(rs.getString("FLOW_TYPE"))) {
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsName = "流程步骤已删除";
                    try {
                        String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(queryStr);
                        if (rs4.next()) {
                            prcsName = rs4.getString("PRCS_NAME");
                            feedBack = rs4.getString("FEEDBACK");

                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    mapTmp.put("PRCS_NAME", prcsName);
                } else {
                    mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                }
                mapTmp.put("FEEDBACK", feedBack);
                list.add(mapTmp);
                String ss = "<li class=\"" + (String) mapTmp.get("CLASS") + "\" q_id=\""
                        + mapTmp.get("CREATE_TIME") + "\" q_run_id=\"" + (Integer) mapTmp.get("RUN_ID")
                        + "\"" + " q_flow_id=\"" + (Integer) mapTmp.get("FLOW_ID") + "\" q_prcs_id=\""
                        + (Integer) mapTmp.get("PRCS_ID") + "\"" + " q_flow_prcs=\""
                        + (Integer) mapTmp.get("FLOW_PRCS") + "\" q_op_flag=\""
                        + (Integer) mapTmp.get("OP_FLAG") + "\">" + "<h3>[" + (Integer) mapTmp.get("RUN_ID")
                        + "] - " + (String) mapTmp.get("FLOW_NAME") + " - " + (String) mapTmp.get("RUN_NAME")
                        + "</h3>" + "<p class=\"grapc\">" + (String) mapTmp.get("PRCS_NAME")
                        + (String) mapTmp.get("OP_FLAG_DESC") + "</p> <span"
                        + "class=\"ui-icon-rarrow\"></span>" + "</li>";
                // sb.append(ss);
                j++;
            }
        } catch (Exception e) {
            return null;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        return list;
    }

    public List<Map<String, String>> getIndexStr(Connection conn, T9Person person, String query)
            throws Exception {
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                Map mapTmp = new HashMap();
                mapTmp.put("PRCS_ID", rs.getInt("PRCS_ID"));
                mapTmp.put("RUN_ID", rs.getInt("RUN_ID"));
                mapTmp.put("FLOW_ID", rs.getInt("FLOW_ID"));
                mapTmp.put("OP_FLAG", rs.getInt("OP_FLAG"));
                mapTmp.put("PRCS_FLAG", rs.getInt("PRCS_FLAG"));
                mapTmp.put("CREATE_TIME", T9Utility.getDateTimeStr(rs.getTimestamp("CREATE_TIME")));
                mapTmp.put("FLOW_NAME", rs.getString("FLOW_NAME"));
                mapTmp.put("FLOW_TYPE", rs.getString("FLOW_TYPE"));
                mapTmp.put("RUN_NAME", rs.getString("RUN_NAME"));
                mapTmp.put("FLOW_PRCS", rs.getInt("FLOW_PRCS"));

                String OP_FLAG_DESC = "会签";
                if (rs.getInt("OP_FLAG") == 1) {
                    OP_FLAG_DESC = "主办";
                }
                mapTmp.put("OP_FLAG_DESC", OP_FLAG_DESC);
                String divClass = "";
                String status = "";
                if (rs.getInt("PRCS_FLAG") == 1) {
                    divClass = " active";
                    status = "未接收";
                } else if (rs.getInt("PRCS_FLAG") == 2) {
                    status = "已接收";
                }
                mapTmp.put("STATUS", status);
                mapTmp.put("CLASS", divClass);
                String feedBack = "0";
                if ("1".equals(rs.getString("FLOW_TYPE"))) {
                    Statement stm4 = null;
                    ResultSet rs4 = null;
                    String prcsName = "流程步骤已删除";
                    try {
                        String queryStr = "SELECT PRCS_NAME , FEEDBACK from FLOW_PROCESS WHERE FLOW_SEQ_ID="
                                + rs.getInt("FLOW_ID") + " AND PRCS_ID=" + rs.getInt("FLOW_PRCS");
                        stm4 = conn.createStatement();
                        rs4 = stm4.executeQuery(queryStr);
                        if (rs4.next()) {
                            prcsName = rs4.getString("PRCS_NAME");
                            feedBack = rs4.getString("FEEDBACK");

                        }
                    } catch (Exception ex) {
                        throw ex;
                    } finally {
                        T9DBUtility.close(stm4, rs4, null);
                    }
                    mapTmp.put("PRCS_NAME", prcsName);
                } else {
                    mapTmp.put("PRCS_NAME", "第" + rs.getInt("PRCS_ID") + "步");
                }
                mapTmp.put("FEEDBACK", feedBack);
                list.add(mapTmp);
                String ss = "<li class=\"" + (String) mapTmp.get("CLASS") + "\" q_id=\""
                        + mapTmp.get("CREATE_TIME") + "\" q_run_id=\"" + (Integer) mapTmp.get("RUN_ID")
                        + "\"" + " q_flow_id=\"" + (Integer) mapTmp.get("FLOW_ID") + "\" q_prcs_id=\""
                        + (Integer) mapTmp.get("PRCS_ID") + "\"" + " q_flow_prcs=\""
                        + (Integer) mapTmp.get("FLOW_PRCS") + "\" q_op_flag=\""
                        + (Integer) mapTmp.get("OP_FLAG") + "\">" + "<h3>[" + (Integer) mapTmp.get("RUN_ID")
                        + "] - " + (String) mapTmp.get("FLOW_NAME") + " - " + (String) mapTmp.get("RUN_NAME")
                        + "</h3>" + "<p class=\"grapc\">" + (String) mapTmp.get("PRCS_NAME")
                        + (String) mapTmp.get("OP_FLAG_DESC") + "</p> <span"
                        + "class=\"ui-icon-rarrow\"></span>" + "</li>";
                // sb.append(ss);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            T9DBUtility.close(stmt, rs, null);
        }
        return list;// sb.toString();
    }

    public Map getNewListMap(Connection conn, T9Person user, String sortId) throws Exception {
        T9FlowTypeLogic logic = new T9FlowTypeLogic();
        List<T9FlowType> typeList = logic.getFlowTypeList(conn);
        T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
        int count = 0;
        Map result = new HashMap();

        Map<Integer, List<T9FlowType>> flowTypes = new HashMap();
        for (T9FlowType ftTmp : typeList) {
            // 如果是自由流程，只判断新建权限，如果是固定流程需判断第一步的经办权限， 如果第一步经办权限没设或没有第一步，只不会显示
            boolean flag = false;
            flag = tru.prcsRole(ftTmp, 0, user, conn);
            if (flag) {
                int flowSort = ftTmp.getFlowSort();
                List fss = flowTypes.get(flowSort);
                if (fss == null)
                    fss = new ArrayList();
                fss.add(ftTmp);
                flowTypes.put(flowSort, fss);
                count++;
            }
        }
        List<Map> list_sort = new ArrayList();
        if (T9Utility.isNullorEmpty(sortId)) {
            sortId = "0";
        }
        if (count > 0) {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                String queryStr = "select SEQ_ID ,SORT_NAME,HAVE_CHILD,DEPT_ID  from FLOW_SORT ";
                queryStr += " order by SORT_NO";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStr);
                while (rs.next()) {
                    int nodeId = rs.getInt("SEQ_ID");
                    String name = rs.getString("SORT_NAME");
                    String haveChild1 = rs.getString("HAVE_CHILD");
                    if (this.checkChild(conn, nodeId, haveChild1, flowTypes) > 0) {
                        String url = "tree.php?SORT_ID=" + nodeId + "&rand=";
                        Map m = new HashMap();
                        m.put("title", name);
                        m.put("isFolder", true);
                        m.put("isLazy", true);
                        m.put("key", nodeId);
                        m.put("json", url);
                        List<Map> list = new ArrayList();
                        // list.add(m);
                        if (flowTypes.containsKey(nodeId)) {
                            List<T9FlowType> fss = flowTypes.get(nodeId);
                            if (fss != null) {
                                for (T9FlowType e : fss) {
                                    String url_ = "edit.php?FLOW_ID=" + e.getSeqId() + "&rand=";
                                    Map m_ = new HashMap();
                                    m_.put("title", e.getFlowName());
                                    m_.put("isFolder", false);
                                    m_.put("isLazy", false);
                                    m_.put("key", e.getSeqId());
                                    m_.put("nodeId", nodeId);
                                    m_.put("json", url_);
                                    m_.put("attachmentId", e.getAttachmentId());
                                    m_.put("attachmentName", e.getAttachmentName());
                                    list.add(m_);
                                }
                            }
                        }
                        m.put("list_type", list);
                        if (list.size() > 0) {
                            list_sort.add(m);
                        }
                    }
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stmt, rs, null);
            }
        }
        result.put("list_sort", list_sort);
        // result.put("list", list);
        String sortNameTmp = "";
        int parentId = 0;
        if (Integer.parseInt(sortId) != 0) {
            T9FlowSortLogic lg = new T9FlowSortLogic();
            T9FlowSort fs = lg.getFlowSortById(conn, Integer.parseInt(sortId));
            sortNameTmp = fs.getSortName();
            parentId = fs.getSortParent();
        }
        result.put("sortNameTmp", sortNameTmp);
        result.put("parentId", parentId);
        result.put("sortId", sortId);
        return result;
    }

    public int checkChild(Connection conn, int sortId, String haveChild, Map flowMap) throws Exception {
        int result = 0;
        if (flowMap.containsKey(sortId)) {
            result = 1;
        } else {
            String queryStr = "select SEQ_ID, HAVE_CHILD  from FLOW_SORT where SORT_PARENT =" + sortId;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStr);
                while (rs.next()) {
                    int seqId = rs.getInt("SEQ_ID");
                    String haveChild1 = rs.getString("HAVE_CHILD");
                    if (this.checkChild(conn, seqId, haveChild1, flowMap) > 0) {
                        result = 2;
                        break;
                    }
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                T9DBUtility.close(stmt, rs, null);
            }
        }
        return result;

    }

    public Map newEditMap(Connection conn, T9Person person, String fLOW_ID) throws Exception {
        int flowId = Integer.parseInt(fLOW_ID);
        Map m = new HashMap();
        T9FlowProcessLogic fpl = new T9FlowProcessLogic();
        T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
        List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId, conn);
        T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId, conn);
        boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, person, conn);
        // 如果第一步为空，以及检查出没有权限则提示
        if (flag) {
            m.put("isPriv", false);
        } else {
            m.put("isPriv", true);
            // 查询是否为重名的
            T9FlowRunLogic frl = new T9FlowRunLogic();
            synchronized (T9FlowRunAct.loc) {
                String runName = frl.getRunName(flowType, person, conn, false);
                conn.commit();
                m.put("runName", runName);
                m.put("flowId", flowType.getSeqId());
                m.put("flowName", flowType.getFlowName());
                m.put("flowType", flowType.getFlowType());
                m.put("autoEdit", flowType.getAutoEdit());//
            }
        }
        return m;
    }

    public int newSubmitMap(Connection conn, T9Person person, String fLOW_ID, String runName,
            T9FlowType flowType) throws Exception {
        T9FlowRunLogic logic = new T9FlowRunLogic();
        int runId = logic.createNewWork(person, flowType, runName, conn);
        return runId;
    }
}
