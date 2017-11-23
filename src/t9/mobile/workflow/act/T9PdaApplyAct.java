package t9.mobile.workflow.act;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.workflow.logic.T9PdaWorkFlowLogic;

/**
 * 我的申请
 * 
 * @author Administrator
 * 
 */
public class T9PdaApplyAct {
    /**
     * 我的申请首页
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
            Map m = logic.listApply(conn, person, null, "1");
            request.setAttribute("n", m);

            Map m2 = logic.listApply(conn, person, null, "2");
            request.setAttribute("n2", m2);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getParameter("sessionid");
        return "/mobile/workflow/index_apply.jsp?sessionid=" + sid;
    }

    /**
     * 我的申请查询
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
            T9PdaWorkFlowLogic logic = new T9PdaWorkFlowLogic();
            String spType = request.getParameter("spType");// 1:审批中 2：已完成
            String LASTEDID = T9Utility.null2Empty(request.getParameter("LASTEDID"));// 区分上拉和下拉，上拉则有值，否则无值
            Map m = logic.listApply(conn, person, LASTEDID, spType);
            List<Map> list = (List<Map>) m.get("list");
            if (list != null && list.size() > 0) {
                m.put("list", T9MobileUtility.list2Json(list));
                System.out.println(T9MobileUtility.obj2Json(m));
                T9MobileUtility.output(response, T9MobileUtility.obj2Json(m));
            } else {
                T9MobileUtility.output(response, "NONEWDATA");
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }
}
