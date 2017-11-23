package t9.subsys.oa.hr.setting.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.setting.data.T9HrManager;
import t9.subsys.oa.hr.setting.logic.T9HrManagerLogic;

public class T9HrManagerAct {
	private T9HrManagerLogic logic = new T9HrManagerLogic();

	public String setBatchValue(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		String userStr = request.getParameter("user");
		String deptStr = request.getParameter("deptStr");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setBatchValueLogic(dbConn, T9Utility.null2Empty(operation), T9Utility.null2Empty(userStr), deptStr);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据用户的管理权限得到所有部门
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String selectDeptToAttendance(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String userPriv = user.getUserPriv();// 角色
			String postpriv = user.getPostPriv();// 管理范围
			String postDept = user.getPostDept();// 管理范围指定部门
			int userDeptId = user.getDeptId();
			T9DeptLogic deptLogic = new T9DeptLogic();
			String data = "";
			if (userPriv != null && userPriv.equals("1") && user.getUserId().trim().equals("admin")) {// 假如是系统管理员的都快要看得到.而且是ADMIN用户
				data = deptLogic.getDeptTreeJson(0, dbConn);
			} else {
				if (postpriv.equals("0")) {
					String[] postDeptArray = { String.valueOf(userDeptId) };
					data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";
				}
				if (postpriv.equals("1")) {
					data = deptLogic.getDeptTreeJson(0, dbConn);
				}
				if (postpriv.equals("2")) {
					if (postDept == null || postDept.equals("")) {
						data = "[]";
					} else {
						String[] postDeptArray = postDept.split(",");
						data = "[" + deptLogic.getDeptTreeJson(0, dbConn, postDeptArray) + "]";
					}
				}
			}
			if (data.equals("")) {
				data = "[]";
			}
			data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replace("\r", "");
			// System.out.println(data);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId) + "," + postpriv);
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 获取人力资源管理员名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getHrManager(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("deptId");
		
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getHrManagerLogic(dbConn,deptId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	/**
	 * 获取人力资源管理员Id串
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getHrManagerIdStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("deptId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getHrManagerIdStrLogic(dbConn,deptId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	/**
	 * 编辑人力资源管理员Id串
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String editHrManager(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("deptId");
		String deptHrManager = request.getParameter("deptHrManager");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.editHrManagerLogic(dbConn,deptId,T9Utility.null2Empty(deptHrManager));
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	
	
	
	

}
