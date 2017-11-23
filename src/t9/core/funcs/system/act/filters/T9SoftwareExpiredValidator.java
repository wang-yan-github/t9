package t9.core.funcs.system.act.filters;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.common.T9LoginErrorConst;
import t9.core.funcs.system.act.imp.T9LoginValidator;
import t9.core.util.auth.T9RegistUtility;

public class T9SoftwareExpiredValidator implements T9LoginValidator {

    public void addSysLog(HttpServletRequest request, T9Person person, Connection conn) throws Exception {
        // TODO Auto-generated method stub

    }

    public int getValidatorCode() {
        // TODO Auto-generated method stub
        return T9LoginErrorConst.SOFTWARE_EXPIRED_CODE;
    }

    public String getValidatorMsg() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getValidatorType() {
        // TODO Auto-generated method stub
        return T9LoginErrorConst.SOFTWARE_EXPIRED;
    }

    public boolean isValid(HttpServletRequest request, T9Person person, Connection conn) throws Exception {
        // try {
        // if (T9RegistProps.isEmpty()) {
        // List<String> registInfo = T9SystemLogic.loadRegistRequires(conn);
        // if (registInfo.size() > 1) {
        // String webInfoPath = T9SysProps.getWebInfPath();
        // Map registMap = T9RegistUtility.loadRegistFromPath(webInfoPath +
        // File.separator
        // + "config" + File.separator + "regist", webInfoPath.substring(0, 3),
        // registInfo.get(0), registInfo.get(1));
        // if (registMap != null && registMap.size() > 0) {
        // T9RegistProps.setProps(registMap);
        // }
        // }
        // }
        // } catch (Exception ex) {
        // log.debug(ex);
        // }
        // 如果软件过期提示用户注册
        if (!T9RegistUtility.hasRegisted() && T9RegistUtility.isExpired()) {
            return false;
        } else {
            return true;
        }
    }

}
