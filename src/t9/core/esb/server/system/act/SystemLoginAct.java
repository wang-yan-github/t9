package t9.core.esb.server.system.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.server.system.logic.SystemLoginLogic;
import t9.core.esb.server.user.data.TdUser;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class SystemLoginAct {

    public String doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String userCode = new String(request.getParameter("userCode").getBytes("iso8859-1"), "utf-8");
            String pwd = request.getParameter("pwd");

            SystemLoginLogic logic = new SystemLoginLogic();

            // 验证用户否存在

            if (!logic.validateUser(dbConn, userCode)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "用户不存在");
                request.setAttribute(T9ActionKeys.RET_DATA, "{\"code\":-1}");
                return "/core/inc/rtjson.jsp";
            }
            // 验证密码
            if (!logic.checkPwd(dbConn, userCode, pwd)) {
                request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
                request.setAttribute(T9ActionKeys.RET_MSRG, "密码错误");
                request.setAttribute(T9ActionKeys.RET_DATA, "{\"code\":-2}");
                return "/core/inc/rtjson.jsp";
            }
            TdUser tdUser = logic.queryPerson(dbConn, userCode);
            /*
             * if (!T9RegistUtility.hasRegisted() &&
             * T9RegistUtility.isExpired()) {
             * request.setAttribute(T9ActionKeys.RET_STATE,
             * T9Const.RETURN_ERROR);
             * request.setAttribute(T9ActionKeys.RET_MSRG, "软件已经过期");
             * request.setAttribute(T9ActionKeys.RET_DATA, "{\"code\":-3}");
             * return "/core/inc/rtjson.jsp"; }
             */
            this.loginSuccess(dbConn, tdUser, request, response);

            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
            request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
        return "/core/inc/rtjson.jsp";
    }

    /**
     * 登录成功的处理
     * 
     * @param conn
     * @param person
     * @param request
     * @throws Exception
     */
    private void loginSuccess(Connection conn, TdUser tdUser, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // 获取用户当前的session,如果不存在就生成一个新的session
        HttpSession session = request.getSession(true);
        // 判断用户是否已经登录
        if (session.getAttribute("ESB_LOGIN_USER") == null) {
            session.setAttribute("ESB_LOGIN_USER", tdUser);
        } else {
            TdUser loginPerson = (TdUser) session.getAttribute("ESB_LOGIN_USER");

            // 如果是新用户登录时,销毁原有的session
            if (loginPerson.getSeqId() != tdUser.getSeqId()) {

                // 销毁session
                session.invalidate();

                // 重新调用登录成功的处理
                loginSuccess(conn, tdUser, request, response);
            }
        }
    }
}
