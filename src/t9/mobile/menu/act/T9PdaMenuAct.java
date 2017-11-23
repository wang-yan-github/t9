package t9.mobile.menu.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.mobile.menu.logic.T9PdaMenuLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaMenuAct {

    public String data(HttpServletRequest request, HttpServletResponse response) {
        Connection conn = null;

        try {
            T9RequestDbConn dbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            conn = dbConn.getSysDbConn();
            T9Person user = (T9Person) request.getSession().getAttribute(
                    T9Const.LOGIN_USER);

            String query = "";
            T9PdaMenuLogic logic = new T9PdaMenuLogic();
            String ATYPE = request.getParameter("ATYPE");
            String A = request.getParameter("A");
            String Q_ID = request.getParameter("Q_ID");
            boolean flag = false;

            if ("refreshList".equals(ATYPE)) {
                if ("loadList".equals(A)) {
                    query = "SELECT MENU_ID,MENU_NAME,MENU_LOCATION,IMAGE FROM `mobile_menu`";
                }
            } else if ("getDetail".equals(ATYPE)) {
                if (!"".equals(Q_ID) && null != Q_ID) {
                    flag = true;
                    query = "SELECT MENU_ID,MENU_NAME,MENU_LOCATION,IMAGE FROM `mobile_menu`"
                            + "WHERE MENU_ID='" + Q_ID + "'";

                }
            }
            List list = logic.getMenu(conn, query, flag);
            T9MobileUtility.output(
                    response,
                    T9MobileUtility.getResultJson(1, null,
                            T9MobileUtility.list2Json(list)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String add(HttpServletRequest request,
            HttpServletResponse response) {
        Connection conn = null;

        try {
            T9RequestDbConn dbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            conn = dbConn.getSysDbConn();
            T9Person user = (T9Person) request.getSession().getAttribute(
                    T9Const.LOGIN_USER);

            String query = "";
            T9PdaMenuLogic logic = new T9PdaMenuLogic();
            String name = request.getParameter("name");
            String location = request.getParameter("location");
            String image = request.getParameter("image");
            logic.addMenu(request, response, conn, name, location, image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String delete(HttpServletRequest request,
            HttpServletResponse response) {
        Connection conn = null;
        try {
            T9RequestDbConn requestDbConn = (T9RequestDbConn) request
                    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
            conn = requestDbConn.getSysDbConn();

            String q_id = request.getParameter("Q_ID");

            T9PdaMenuLogic logic = new T9PdaMenuLogic();
            logic.deleteMenu(response, conn, q_id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return null;
    }

}
