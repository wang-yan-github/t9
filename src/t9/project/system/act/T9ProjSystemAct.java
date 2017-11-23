package t9.project.system.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.permissions.logic.T9PermissionsLogic;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.project.system.logic.T9ProjSystemLogic;

public class T9ProjSystemAct {

	/**
	 * 获取类型列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getStyleList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String classNo = request.getParameter("classNo");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			String data = logic.getStyleList(dbConn, classNo);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 设置新建权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setNewPriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String user = request.getParameter("user");
		String role = request.getParameter("role");
		String dept = request.getParameter("dept");
		String privCode = request.getParameter("privCode");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			logic.setNewPriv(dbConn, privCode, user, role, dept);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "新建权限设置成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取新建项目权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getNewPriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String privCode = request.getParameter("privCode");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			String data = logic.getNewPriv(dbConn, privCode);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 设置审批权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setApprovePriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String user = request.getParameter("user");
		String dept = request.getParameter("dept");
		String privCode = request.getParameter("privCode");
		String seqId = request.getParameter("seqId");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			logic.setApprovePriv(dbConn, privCode, user, dept, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "权限设置成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除审批人权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	public String delApprovePriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqId = request.getParameter("seqId");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			logic.delApprovePriv(dbConn, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "权限删除成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取审批人权限规则列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getApproveList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String privCode = request.getParameter("privCode");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			String data = logic.getApproveList(dbConn, privCode);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 *获取审批权限
	 * 2013-3-21
	 * @author ny
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getApprovePriv(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqId = request.getParameter("seqId");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ProjSystemLogic logic = new T9ProjSystemLogic();
			String data = logic.getApprovePriv(dbConn, Integer.parseInt(seqId));

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
}