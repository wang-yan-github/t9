package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaWorkFlowLogic;

/**
 * 工作流
 * 
 * @author zrh 20170223
 * 
 */
public class T9PdaNewFlowAct {
    // 加载流程分类及分类下流程列表
    public String newList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            String SORT_ID = request.getParameter("SORT_ID");
            Map map = logic.getNewListMap(conn, person, SORT_ID);
            request.setAttribute("r", map);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        // return "/mobile/workflow/newlist.jsp?sessionid=" + sid;
        return "/mobile/workflow/flowTypeList.jsp?sessionid=" + sid;
    }

    // 新建流程-生成流程名称或文号接口
    public String newEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            String FLOW_ID = request.getParameter("FLOW_ID");
            Map map = logic.newEditMap(conn, person, FLOW_ID);
            // request.setAttribute("r", map);

            T9MobileUtility.output(response, "[" + T9MobileUtility.mapToJson(map) + "]");
            // String result = "<script>AUTO_NEW = '1';q_flow_id= '" + FLOW_ID +
            // "';runName = '"
            // + map.get("runName") + "';</script>";
            // T9MobileUtility.output(response, result);
        } catch (Exception ex) {
            throw ex;
        }
        // String sid = request.getSession().getId();
        // return "/mobile/workflow/newedit.jsp?sessionid=" + sid;
        return null;
    }

    // 新建流程-创建工作流实例及第一个步骤接口
    public String newSubmit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            String FLOW_ID = request.getParameter("FLOW_ID");
            String flowName = T9Utility.null2Empty(request.getParameter("RUN_NAME"));
            String RUN_NAME_LEFT = T9Utility.null2Empty(request.getParameter("RUN_NAME_LEFT"));
            String RUN_NAME_RIGHT = T9Utility.null2Empty(request.getParameter("RUN_NAME_RIGHT"));
            flowName = RUN_NAME_LEFT + flowName + RUN_NAME_RIGHT;
            if (T9Utility.isNullorEmpty(flowName)) {
                T9MobileUtility.output(response, "NORUNNAME");
                return null;
            }
            T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
            int flowId = Integer.parseInt(FLOW_ID);
            T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId, conn);
            int runId = logic.newSubmitMap(conn, person, FLOW_ID, flowName, flowType);
            // String result = "<script>q_run_id = '" + runId + "';q_flow_id= '"
            // + flowId
            // +
            // "';q_prcs_id     = '1';q_flow_prcs   = '1';q_op_flag     = '1';</script>";
            // T9MobileUtility.output(response, result);
            Map map = new HashMap();
            map.put("q_run_id", runId);
            map.put("q_flow_id", flowId);
            map.put("q_prcs_id", "1");
            map.put("q_flow_prcs", "1");
            map.put("q_op_flag", "1");
            T9MobileUtility.output(response, "[" + T9MobileUtility.mapToJson(map) + "]");
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }
}
