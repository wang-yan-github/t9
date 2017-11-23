package t9.subsys.oa.hr.manage.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.manage.data.T9HrStaffIncentive;
import t9.subsys.oa.hr.manage.logic.T9HrStaffIncentiveLogic;

public class T9HrStaffIncentiveAct {
	private T9HrStaffIncentiveLogic logic = new T9HrStaffIncentiveLogic();
	public static final String attachmentFolder = "hr";

	/**
	 * 新建奖惩信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addStaffincentiveInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			dbConn = requestDbConn.getSysDbConn();
			this.logic.setNewStaffincentiveValueLogic(dbConn, fileForm, person);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		response.sendRedirect(contexPath + "/subsys/oa/hr/manage/staffIncentive/newRemind.jsp");
		return null;
	}

	/**
	 *奖惩信息 通用列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getStaffincentiveListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getStaffincentiveJsonLogic(dbConn, request.getParameterMap(), person);
			PrintWriter pw = response.getWriter();
			pw.println(data);
			pw.flush();
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return null;
	}

	/**
	 * 获取单位员工用户名称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String userIdStr = request.getParameter("userIdStr");
			String data = this.logic.getUserNameLogic(dbConn, userIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除文件--wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String filePath = T9SysProps.getAttachPath() + File.separator + this.attachmentFolder + File.separator;
			this.logic.deleteFileLogic(dbConn, seqIdStr, filePath);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 获取详情
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getIncentiveDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		if (T9Utility.isNullorEmpty(seqId)) {
			seqId = "0";
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9HrStaffIncentive incentive = (T9HrStaffIncentive) this.logic.getIncentiveDetailLogic(dbConn, Integer.parseInt(seqId));
			if (incentive == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "未找到相应记录");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(incentive);
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
	 * 奖惩信息查询
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryIncentiveListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("staffName", T9DBUtility.escapeLike(request.getParameter("staffName")));
			map.put("incentiveTime1", T9DBUtility.escapeLike(request.getParameter("incentiveTime1")));
			map.put("incentiveTime2", T9DBUtility.escapeLike(request.getParameter("incentiveTime2")));
			map.put("incentiveItem", T9DBUtility.escapeLike(request.getParameter("incentiveItem")));
			map.put("incentiveType", T9DBUtility.escapeLike(request.getParameter("incentiveType")));
			String data = "";
			data = this.logic.queryIncentiveListJsonLogic(dbConn, request.getParameterMap(), map, person);
			PrintWriter pw = response.getWriter();
			pw.println(data);
			pw.flush();
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return null;
	}
	/**
	 * 编辑奖惩信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateIncentiveInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			this.logic.updateIncentiveInfoLogic(dbConn,fileForm,person);
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		response.sendRedirect(contexPath + "/subsys/oa/hr/manage/staffIncentive/manage.jsp");
		return null;
	}
	/**
	 * 浮动菜单文件删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		String attachId = request.getParameter("delAttachId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean updateFlag = this.logic.updateFloadFile(dbConn, seqIdStr, T9Utility.null2Empty(attachId));
			int returnFlag = 0;
			if (updateFlag) {
				returnFlag = 1;
			}
			String data = "{updateFlag:\"" + returnFlag + "\"}";
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
