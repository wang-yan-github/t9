package t9.mobile.user.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.mobile.user.logic.T9PdaPersonLogic;
import t9.mobile.util.T9MobileUtility;

public class T9PdaPersonAct {

	public String data(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;

		try {
			T9RequestDbConn dbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			conn = dbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(
					T9Const.LOGIN_USER);

			String query = "";
			T9PdaPersonLogic logic = new T9PdaPersonLogic();
			String ATYPE = request.getParameter("ATYPE");
			String A = request.getParameter("A");
			String Q_ID = request.getParameter("Q_ID");
			boolean flag = false;

			if ("refreshList".equals(ATYPE)) {
				if ("loadList".equals(A)) {
					query = "SELECT person.SEQ_ID,person.USER_NAME,person.BYNAME,person.DEPT_ID,person.SEX,person.BIRTHDAY,person.TEL_NO_DEPT,person.FAX_NO_DEPT,person.ADD_HOME,person.POST_NO_HOME,person.TEL_NO_HOME,person.MOBIL_NO,person.EMAIL,person.OICQ,person.ICQ,person.MSN,person.NICK_NAME FROM `person`";
					//query = "SELECT * FROM `person`";
				}
			} else if ("getDetail".equals(ATYPE)) {
				if (!"".equals(Q_ID) && null != Q_ID) {
					flag = true;
					query = "SELECT person.SEQ_ID,person.USER_NAME,person.BYNAME,person.DEPT_ID,person.SEX,person.BIRTHDAY,person.TEL_NO_DEPT,person.FAX_NO_DEPT,person.ADD_HOME,person.POST_NO_HOME,person.TEL_NO_HOME,person.MOBIL_NO,person.EMAIL,person.OICQ,person.ICQ,person.MSN,person.NICK_NAME FROM `person`"
							+ "where SEQ_ID='" + Q_ID + "'";

				}
			}
			List list = logic.getPerson(conn, query, flag);
			T9MobileUtility.output(
					response,
					T9MobileUtility.getResultJson(1, null,
							T9MobileUtility.list2Json(list)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
