package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;
import t9.mobile.workflow.logic.T9PdaWorkFlowLogic;

/**
 * 待办列表数据
 * 
 * @author Administrator
 * 
 */
public class T9PdaWorkflowIndexAct {
    // 我的工作流程列表（首页）
    /**
     * 待办首页
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            // Map map = logic.getIndexMap(conn, person);
            Map m = logic.listDB(conn, person, "");
            request.setAttribute("n", m);
            String source = request.getParameter("source");
            if (source == null || source.equals("")) {
                source = "0";
            }
            request.setAttribute("source", source);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getParameter("sessionid");
        return "/mobile/workflow/index_flow.jsp?sessionid=" + sid;
    }

    /**
     * 待办上拉下拉刷新
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String data(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            String A = request.getParameter("A");
            String whereSql = "";
            if ("down".equals(A)) {// 获取新数据-下拉刷新，下拉刷新获取上拉所有数据---修改(下拉刷新则重新加载)
                // whereSql = " and " +
                // T9DBUtility.getDateFilter("FLOW_RUN_PRCS.CREATE_TIME",
                // LASTEDID, ">");
            } else if ("up".equals(A)) {// 获取更多数据（上拉刷新）
                String LASTEDID = T9Utility.null2Empty(request.getParameter("LASTEDID"));
                whereSql = " and FLOW_RUN_PRCS.SEQ_ID < " + LASTEDID;
            }
            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            // List<Map> list = logic.list(conn, person, whereSql);
            Map m = logic.listDB(conn, person, whereSql);
            List<Map> list = (List<Map>) m.get("list");
            if (list != null && list.size() > 0) {
                m.put("list", T9MobileUtility.list2Json(list));
                T9MobileUtility.output(response, T9MobileUtility.obj2Json(m));
            } else {
                T9MobileUtility.output(response, "NONEWDATA");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 已办流程上拉下拉刷新
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String dataYB(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            String A = request.getParameter("A");
            String whereSql = "";
            if ("down".equals(A)) {// 获取新数据-下拉刷新，下拉刷新获取上拉所有数据---修改(下拉刷新则重新加载)

            } else if ("up".equals(A)) {// 获取更多数据（上拉刷新）
                String LASTEDID = T9Utility.null2Empty(request.getParameter("LASTEDID"));
                whereSql = " and FLOW_RUN_PRCS.SEQ_ID < " + LASTEDID;
            }
            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            Map m = logic.listYB(conn, person, whereSql);
            List<Map> list = (List<Map>) m.get("list");
            if (list != null && list.size() > 0) {
                m.put("list", T9MobileUtility.list2Json(list));
                T9MobileUtility.output(response, T9MobileUtility.obj2Json(m));
            } else {
                T9MobileUtility.output(response, "NONEWDATA");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    /**
     * 弃用
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String data2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();
            String A = request.getParameter("A");
            String CURRITERMS = request.getParameter("CURRITERMS");
            if (T9Utility.isNullorEmpty(CURRITERMS)) {
                CURRITERMS = "0";
            }
            String whereStr = " WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID and USER_ID='"
                    + person.getSeqId()
                    + "' and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' and not (TOP_FLAG='1' and PRCS_FLAG=1) ";
            String order = " order by FLOW_RUN_PRCS.CREATE_TIME desc ";
            String query = "";
            String LASTEDID = T9Utility.null2Empty(request.getParameter("LASTEDID"));
            boolean flag = true;
            if ("loadList".equals(A)) {// 装载数据
                query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE "
                        + "WHERE FLOW_RUN_PRCS.RUN_ID=FLOW_RUN.RUN_ID "
                        + "and FLOW_RUN_PRCS.CHILD_RUN = '0' " + "and FLOW_RUN.FLOW_ID=FLOW_TYPE.SEQ_ID "
                        + "and USER_ID='" + person.getSeqId() + "' " + ""
                        + "and FLOW_RUN.DEL_FLAG=0 and PRCS_FLAG<'3' "
                        + "order by FLOW_RUN_PRCS.CREATE_TIME desc ";
            } else if ("GetNew".equals(A)) {// 获取新数据-下拉刷新，下拉刷新获取上拉所有数据
                String countStr = "SELECT count(*) from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE " + whereStr
                        + " and " + T9DBUtility.getDateFilter("FLOW_RUN_PRCS.CREATE_TIME", LASTEDID, ">")
                        + " " + order;

                int count = T9QuickQuery.getCount(conn, countStr);
                if (count == 0) {
                    T9MobileUtility.output(response, "NONEWDATA");
                    return null;
                } else {
                    query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE  " + whereStr + " and  "
                            + T9DBUtility.getDateFilter("FLOW_RUN_PRCS.CREATE_TIME", LASTEDID, ">") + " "
                            + order;
                    flag = false;
                }
            } else {// 获取更多数据（上拉刷新）
                query = "SELECT * from FLOW_RUN_PRCS,FLOW_RUN,FLOW_TYPE " + whereStr + " and  "
                        + T9DBUtility.getDateFilter("FLOW_RUN_PRCS.CREATE_TIME", LASTEDID, "<") + " " + order;
                int count = T9QuickQuery.getCountByCursor(conn, query, CURRITERMS);
                if (count == 0) {
                    T9MobileUtility.output(response, "NOMOREDATA");
                    return null;
                }
            }
            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();

            // String map = "";
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            if (flag) {
                list = logic.getIndexStr(conn, person, query, CURRITERMS);
            } else {
                list = logic.getIndexStr(conn, person, query);
            }
            T9MobileUtility.output(response, T9MobileUtility.list2Json(list));
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }
}
