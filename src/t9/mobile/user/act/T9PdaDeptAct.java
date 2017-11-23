package t9.mobile.user.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.mobile.user.logic.T9PdaDeptLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaDeptAct {
    /**
     * 查看公司组织架构
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String companyStructure(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Connection dbConn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            dbConn = requestDbConn.getSysDbConn();
            T9Person user = (T9Person) request.getSession().getAttribute(
                    T9Const.LOGIN_USER);

            int userDeptId = user.getDeptId();
            T9PdaDeptLogic deptLogic = new T9PdaDeptLogic();
            String[] postDeptArray = { String.valueOf(userDeptId) };
            /*String data = "";
            data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray)
                    + "]";*/

            List data = deptLogic.getDeptTreeJson(0, dbConn, postDeptArray);
            T9MobileUtility.output(response,
                    T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }
}
