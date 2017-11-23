package t9.mobile.workflow.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.mobile.workflow.logic.T9PdaWorkFlowLogic;

/**
 * 待办列表数据
 * 
 * @author Administrator
 * 
 */
public class T9PdaDBWorkAct {
    /**
     * 待办中心首页
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
            int total_flow = logic.listFlowDBTotal(conn, person, "");
            request.setAttribute("total_flow", total_flow);
        } catch (Exception ex) {
            throw ex;
        }
        String sid = request.getSession().getId();
        return "/mobile/workflow/index.jsp?sessionid=" + sid;
    }
}
