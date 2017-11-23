package t9.mobile.act;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserOnline;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.funcs.workflow.logic.T9MoreOperateLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.mobile.logic.T9PdaLoginLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9PdaLogin {
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            String pVer = request.getParameter("P_VER");
            String client = "1";
            if (T9Utility.isInteger(pVer)) {
                client = pVer;
            }
            T9SystemLogic logic = new T9SystemLogic();
            String userName = request.getParameter("USERNAME");
            String pwd = request.getParameter("PASSWORD");
            boolean flag = false;
            T9Person person = null;
            if (!T9Utility.isNullorEmpty(userName)) {
                person = logic.queryPerson(dbConn, userName);
                if (person != null) {
                    T9LoginAdapter loginAdapter = new T9LoginAdapter(request, person);
                    if (loginAdapter.validate(new T9PasswordValidator(pwd))) {
                        flag = true;
                    }
                }
            }
            if (!flag) {
                T9MobileUtility.output(response, T9MobileUtility.getResultJson(0, "用户名或密码错误", null));
            } else {
                T9SystemAct act = new T9SystemAct();
                act.loginSuccess(dbConn, person, request, response, client);

                T9MoreOperateLogic l2 = new T9MoreOperateLogic();
                List<String> list = l2.getUserPriv(dbConn, person.getUserPriv(), person.getUserPrivOther());

                String title = logic.getIETitle(dbConn);
                HttpSession session = request.getSession();
                T9PdaLoginLogic l = new T9PdaLoginLogic();

                if (T9Utility.isNullorEmpty(title)) {
                    title = T9SysProps.getString("productName");
                }
                Map map = new HashMap();
                map.put("status", "YES");
                map.put("uid", person.getUserId());
                map.put("q_id", person.getSeqId());
                map.put("login_user_name", T9Utility.encodeSpecial(person.getUserName()));
                map.put("session_id", session.getId());
                map.put("login_func_str", T9MobileConfig.getFuncStr(list));
                map.put("td_myoa_version", l.getT9Version(dbConn));
                map.put("myoa_tdim_port", T9MobileConfig.MYOA_TDIM_PORT);
                map.put("app_title", T9Utility.encodeSpecial(title));
                // sb.append(T9MobileUtility.mapToJson(map));
                // resMap.put("data",T9MobileUtility.obj2Json(map));
                // T9MobileUtility.output(response,
                // T9MobileUtility.obj2Json(resMap));
                T9MobileUtility.output(response,
                        T9MobileUtility.getResultJson(1, null, T9MobileUtility.obj2Json(map)));
            }

            return null;
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
    }

    public String updateOnlineStatus(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9UserOnline online = new T9UserOnline();
            online.setSessionToken("");
            online.setLoginTime(new Date());
            T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
            online.setUserId(person.getSeqId());
            String pVer = request.getParameter("CLIENT");
            online.setUserState(pVer);

            T9SystemLogic logic = new T9SystemLogic();
            logic.addOnline(dbConn, online);
            return null;
        } catch (Exception ex) {
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
            throw ex;
        }
    }
}
