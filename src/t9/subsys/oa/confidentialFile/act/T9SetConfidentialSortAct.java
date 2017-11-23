package t9.subsys.oa.confidentialFile.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.confidentialFile.data.T9ConfidentialSort;
import t9.subsys.oa.confidentialFile.logic.T9SetConfidentialSortLogic;

public class T9SetConfidentialSortAct {
	private T9SetConfidentialSortLogic logic = new T9SetConfidentialSortLogic();

	/**
	 * 新建文件夹
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addFileSortInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int sortParent = 0;
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			Map<String, String[]> map = request.getParameterMap();
			T9ConfidentialSort confidentialSort = (T9ConfidentialSort) T9FOM.build(map, T9ConfidentialSort.class, "");
			confidentialSort.setSortParent(sortParent);

			int isHaveFlag = 0;
			int counter = this.logic.checkSortNameLogic(dbConn, T9Utility.null2Empty(confidentialSort.getSortName()), "");
			if (counter > 0) {
				isHaveFlag = 1;
			} else {
				this.logic.addConfidentialSortLogic(dbConn, confidentialSort);
			}
			String date = "{isHaveFlag:\"" + isHaveFlag + "\" }";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, date);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据seqId获取目录列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileSortList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getFileSortListLogic(dbConn);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据seqId获取T9ConfidentialSort对象
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileSortById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ConfidentialSort confidentialSort = this.logic.getfileSortById(dbConn, seqId);
			if (confidentialSort == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "会议设备信息不存在");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(confidentialSort);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 更新T9ConfidentialSort对象
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateFileSortById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			Map<String, String[]> map = request.getParameterMap();
			T9ConfidentialSort confidentialSort = (T9ConfidentialSort) T9FOM.build(map, T9ConfidentialSort.class, "");

			int isHaveFlag = 0;
			int counter = this.logic.checkSortNameLogic(dbConn, T9Utility.null2Empty(confidentialSort.getSortName()), String.valueOf(confidentialSort
					.getSeqId()));
			if (counter > 0) {
				isHaveFlag = 1;
			} else {
				this.logic.updateFileSortByIdLogic(dbConn, confidentialSort);
			}
			String date = "{isHaveFlag:\"" + isHaveFlag + "\" }";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功更新数据");
			request.setAttribute(T9ActionKeys.RET_DATA, date);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 递归删除文件夹及下的所有文件信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		T9ConfidentialSort fileSort = new T9ConfidentialSort();
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		// 获取ip
		String ipStr = request.getRemoteAddr();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			fileSort.setSeqId(seqId);
			this.logic.delFileSortInfoById(dbConn, fileSort, loginUserSeqId, ipStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "数据删除成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取树形结构信息,用于权限设置用，不考虑权限。
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String sortIdStr = request.getParameter("seqId");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getSetTreeLogic(dbConn, idStr, sortIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据id设置访问权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setVisitById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortId = request.getParameter("seqId");
		String override = request.getParameter("override");
		String userId = request.getParameter("userId");
		String action = "USER_ID";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setPrivLogic(dbConn, T9Utility.null2Empty(sortId), T9Utility.null2Empty(override), T9Utility.null2Empty(userId), action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据id设置管理权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setManageUserById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortId = request.getParameter("seqId");
		String override = request.getParameter("override");
		String manageUser = request.getParameter("manageUser");
		String action = "MANAGE_USER";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setPrivLogic(dbConn, T9Utility.null2Empty(sortId), T9Utility.null2Empty(override), T9Utility.null2Empty(manageUser), action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据id设置新建权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setNewUserById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortId = request.getParameter("seqId");
		String override = request.getParameter("override");
		String createId = request.getParameter("createId");
		String action = "NEW_USER";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setPrivLogic(dbConn, T9Utility.null2Empty(sortId), T9Utility.null2Empty(override), T9Utility.null2Empty(createId), action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据id设置下载权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setDownLoadById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortId = request.getParameter("seqId");
		String override = request.getParameter("override");
		String setidStr = request.getParameter("downLoadId");
		String action = "DOWN_USER";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setPrivLogic(dbConn, T9Utility.null2Empty(sortId), T9Utility.null2Empty(override), T9Utility.null2Empty(setidStr), action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 根据id设置所有者权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setOwnerById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortId = request.getParameter("seqId");
		String override = request.getParameter("override");
		String setidStr = request.getParameter("ownerId");
		String action = "OWNER";
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setPrivLogic(dbConn, T9Utility.null2Empty(sortId), T9Utility.null2Empty(override), T9Utility.null2Empty(setidStr), action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 批量设置权限
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setBatchPriv(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqString = request.getParameter("seqId"); // 文件夹seqId 12
		String setIdStr = request.getParameter("idStr"); // 设置的id串 16||
		String check = request.getParameter("check"); // 要设置的选项 OWNER,
		String opt = request.getParameter("opt"); // 添加或删除操作 addPriv
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setBatchPrivLogic(dbConn, seqString, setIdStr, check, opt);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取人员id名字串
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqIdStr = request.getParameter("seqId");
		String action = request.getParameter("action");

		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getPersonNameStrLogic(dbConn, seqIdStr, action);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取部门名字串
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getDeptNameStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqIdStr = request.getParameter("seqId");
		String action = request.getParameter("action");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getDeptNameStrLogic(dbConn, seqIdStr, action);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 角色名字串
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getRoleNameStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqIdStr = request.getParameter("seqId");
		String action = request.getParameter("action");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getRoleNameStrLogic(dbConn, seqIdStr, action);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 获取所有人员的id名字串
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getAllPersonIdStr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn;
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getSortName(dbConn, seqId);
			//System.out.println(data);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	

}
