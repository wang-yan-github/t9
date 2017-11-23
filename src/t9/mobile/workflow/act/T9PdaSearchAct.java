package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaHandlerLogic;
import t9.mobile.workflow.logic.T9PdaSearchLogic;

public class T9PdaSearchAct {
    public String searchList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            conn = requestDbConn.getSysDbConn();

            String searchName = request.getParameter("SEARCH_NAME");
            // String searchRunId = request.getParameter("SEARCH_RUN_ID");
            // String CURRITERMS = request.getParameter("CURRITERMS");
            String lastedId = request.getParameter("LASTEDID");
            T9PdaSearchLogic logic = new T9PdaSearchLogic();
            Map m = logic.search(conn, person, searchName, lastedId);
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
        // String sid = request.getSession().getId();
        // return "/mobile/workflow/searchlist.jsp?sessionid=" + sid;
    }

    public String detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            String imgPath = T9WorkFlowUtility.getImgPath(request);
            String ip = request.getRemoteAddr();
            Map map = logic.getEditMap(conn, person, flowId, runId, prcsId, flowPrcs, ip, imgPath);
            request.setAttribute("r", map);
            request.setAttribute("imgPath", imgPath);
            request.setAttribute("refreshFlag", request.getParameter("refreshFlag"));
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/flowDetail.jsp?sessionid=" + sid;
    }
}
