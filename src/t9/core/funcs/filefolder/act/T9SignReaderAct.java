package t9.core.funcs.filefolder.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.logic.T9SignReaderLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SignReaderAct {
	private static Logger log = Logger.getLogger("t9.core.funcs.filefolder.act.T9SignReaderAct");

	public String getSignReader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortIdStr = request.getParameter("sortId");
		String contentIdStr = request.getParameter("contentId");
		int sortId = 0;
		if (sortIdStr != null && !"".equals(sortIdStr)) {
			sortId = Integer.parseInt(sortIdStr);
		}
		int contentId = 0;
		if (contentIdStr != null && !"".equals(contentIdStr)) {
			contentId = Integer.parseInt(contentIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9SignReaderLogic logic = new T9SignReaderLogic();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = logic.getSignReader(sortId, contentId,person, dbConn);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	public String delSignReader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contentIdStr = request.getParameter("contentId");
		int contentId = 0;
		if (contentIdStr != null && !"".equals(contentIdStr)) {
			contentId = Integer.parseInt(contentIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			T9SignReaderLogic logic = new T9SignReaderLogic();
			logic.delSignReader(dbConn, contentId, user);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "清空成功!");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
}
