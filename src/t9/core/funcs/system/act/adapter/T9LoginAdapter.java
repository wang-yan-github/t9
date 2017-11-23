package t9.core.funcs.system.act.adapter;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9LoginAdapter {

    private HttpServletRequest request;
    private Connection dbConn;
    private T9Person person;

    public T9LoginAdapter(HttpServletRequest request, T9Person person) throws Exception {
        this.request = request;

        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);

        this.dbConn = requestDbConn.getSysDbConn();

        this.person = person;
    }

    public boolean isValid(T9LoginValidator lv) throws Exception {
        return lv.isValid(this.request, this.person, this.dbConn);
    }

    public boolean validate(T9LoginValidator lv) throws Exception {
        if (lv.isValid(this.request, this.person, this.dbConn)) {
            return true;
        } else {
            // 写系统日志
            lv.addSysLog(this.request, this.person, this.dbConn);
            String msg = lv.getValidatorMsg();
            if (msg == null || "".equals(msg.trim())) {
                msg = "{}";
            }
            // 返回到页面上的信息
            String retData = "{\"code\":" + lv.getValidatorCode() + ",\"msg\":" + msg + "}";
            if (lv.getValidatorCode() == T9LoginErrorConst.LOGIN_PW_EXPIRED_CODE
                    || lv.getValidatorCode() == T9LoginErrorConst.LOGIN_INITIAL_PW_CODE) {
                String sessionToken = (String) request.getSession().getAttribute("sessionToken");
                retData = "{\"code\":" + lv.getValidatorCode() + ",\"msg\":" + msg + ",\"seqId\":\""
                        + person.getSeqId() + "\",\"sessionToken\":\"" + sessionToken + "\"}";
            }
            request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
            request.setAttribute(T9ActionKeys.RET_MSRG, lv.getValidatorType());
            request.setAttribute(T9ActionKeys.RET_DATA, retData);

            return false;
        }
    }
}
