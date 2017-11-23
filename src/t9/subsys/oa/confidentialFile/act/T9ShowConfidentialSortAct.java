package t9.subsys.oa.confidentialFile.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.confidentialFile.data.T9ConfidentialSort;
import t9.subsys.oa.confidentialFile.logic.T9ConfidentialContentLogic;
import t9.subsys.oa.confidentialFile.logic.T9ShowConfidentialSortLogic;

public class T9ShowConfidentialSortAct {
	private static Logger log = Logger.getLogger("t9.subsys.oa.confidentialFile.act.T9ShowConfidentialSortAct");
	private T9ShowConfidentialSortLogic logic = new T9ShowConfidentialSortLogic();

	/**
	 * 获取文件夹树形结构信息，考虑是否有权限，有权限才能显示
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPrivTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		String sortIdStr = request.getParameter("seqId");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getPrivTreeLogic(dbConn, loginUser, idStr, sortIdStr);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 通过id递归获取文件夹名
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSortNameById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getFolderPathByIdLogic(dbConn, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "返回数据成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 添加子文件夹
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addSubFolderInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortParent = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			Map<String, String[]> map = request.getParameterMap();
			T9ConfidentialSort fileSort = (T9ConfidentialSort) T9FOM.build(map, T9ConfidentialSort.class, "");
			String folderName = T9Utility.null2Empty(fileSort.getSortName());
			String data = this.logic.addSubFolderLogic(dbConn, fileSort, sortParent, folderName);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 通过id获取该文件夹的“所有者权限”
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getPrivteById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		if (T9Utility.isNullorEmpty(seqId)) {
			seqId = "0";
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getOwnerPrivLogic(dbConn, person, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	/**
	 * 通过id获取该文件夹的“所有者权限”与“访问权限”
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getVisitOrOwnerPrivteById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		if (T9Utility.isNullorEmpty(seqId)) {
			seqId = "0";
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getVisitOrOwnerPrivLogic(dbConn, person, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
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
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
	 * 更新编辑子文件夹信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortParent = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			Map<String, String[]> map = request.getParameterMap();
			T9ConfidentialSort fileSort = (T9ConfidentialSort) T9FOM.build(map, T9ConfidentialSort.class, "");
			String folderName = T9Utility.null2Empty(fileSort.getSortName());
			String data = this.logic.updateSubFolderLogic(dbConn, fileSort, sortParent, folderName);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 复制文件夹
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String copyFolderById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("folderId");
		String action = request.getParameter("action");
		try {
			Cookie sortSeqIdCookie = new Cookie("folderSeqIdCookie", seqId);
			Cookie sortActionCookie = new Cookie("confidentialAction", action);
			response.addCookie(sortSeqIdCookie);
			response.addCookie(sortActionCookie);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功设置数据");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 粘贴文件夹
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String pasteFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortParentStr = request.getParameter("sortParent"); // 点击粘贴时的文件夹seqId作为父级id
		int sortParent = 0;
		if (!T9Utility.isNullorEmpty(sortParentStr)) {
			sortParent = Integer.parseInt(sortParentStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9ConfidentialContentLogic contentLogic = new T9ConfidentialContentLogic();

			String seqIdStr = contentLogic.getCookieValue(request, "folderSeqIdCookie");
			String action = contentLogic.getCookieValue(request, "confidentialAction");
			int seqId = 0;
			if (!T9Utility.isNullorEmpty(seqIdStr)) {
				seqId = Integer.parseInt(seqIdStr);
			}
			T9ConfidentialSort fileSort3 = this.logic.getfileSortById(dbConn, seqId); // 获取补复制文件夹信息
			String data = "";
			int nodeId = 0;
			int isHaveChild = 0;
			String sortName = "";
			if (fileSort3 != null) {
				sortName = T9Utility.null2Empty(fileSort3.getSortName());
				if ("cut".equals(action)) {
					this.logic.updateFolderInfoById(dbConn, sortParent, seqId);
					isHaveChild = this.logic.isHaveChild(dbConn, seqId);
					nodeId = fileSort3.getSeqId();
				} else if ("copy".equals(action)) {
					// 级联查询本文件夹及其所有的子文件夹信息
					List listTemp = new ArrayList();
					T9ConfidentialSort maxFileSort = this.logic.getSortMaxSeqId(dbConn);
					int maxSeqId = maxFileSort.getSeqId();
					List folderList = this.logic.getAllFolderList(dbConn, seqId, sortParent, listTemp, maxSeqId);
					isHaveChild = this.logic.isHaveChild(dbConn, seqId);
					nodeId = (Integer) folderList.get(0);
				}
				Cookie sortCookie = contentLogic.getCookie(request, "folderSeqIdCookie");
				Cookie actionCookie = contentLogic.getCookie(request, "confidentialAction");
				if (sortCookie != null) {
					sortCookie.setMaxAge(0);
					response.addCookie(sortCookie);
				}
				if (actionCookie != null) {
					actionCookie.setMaxAge(0);
					response.addCookie(actionCookie);
				}
			}
			data = "[{nodeId:\"" + nodeId + "\",isHaveChild:\"" + isHaveChild + "\",sortName:\"" + T9Utility.encodeSpecial(sortName) + "\",seqId:\""
					+ seqId + "\",action:\"" + action + "\" }]";
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功粘贴数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 通过id获取该文件夹的所有权限信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getAllPrivteById(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getAllPrivteByIdLogic(dbConn,person,seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 取得文件夹名
	 * @return
	 * @throws Exception
	 */
	public String getFolderName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getFolderNameLogic(dbConn, seqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	

}
