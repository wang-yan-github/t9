package t9.subsys.oa.training.act;

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
import t9.subsys.oa.training.data.T9HrTrainingPlan;
import t9.subsys.oa.training.logic.T9HrTrainingPlanLogic;

public class T9HrTrainingPlanAct {
	public static final String attachmentFolder = "training";
	private T9HrTrainingPlanLogic logic = new T9HrTrainingPlanLogic();

	/**
	 * 获取下拉列表值--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSelectOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parentNo = request.getParameter("parentNo");
		String optionType = request.getParameter("optionType");
		if (T9Utility.isNullorEmpty(parentNo)) {
			parentNo = "";
		}
		if (T9Utility.isNullorEmpty(optionType)) {
			optionType = "";
		}
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getSelectOption(dbConn, parentNo);
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
	 * 获取小编码表内容--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getCodeName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String classCode = request.getParameter("classCode");
			String classNo = request.getParameter("classNo");
			String data = this.logic.getCodeNameLogic(dbConn, classCode, classNo);
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
	 * 新建培训计划--wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addTrainingPlanInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = this.logic.fileUploadLogic(fileForm, attachmentFolder);
			this.logic.setTrainingPlanInfoLogic(dbConn, fileForm, person, map);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		response.sendRedirect(contexPath + "/subsys/oa/training/plan/newPlanWarn.jsp");
		return "";
	}

	/**
	 * 管理培训计划列表 -wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTrainingPlanListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getTrainingPlanListLogic(dbConn, request.getParameterMap(), person);
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
	 * 获取培训计划详情 -wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTrainingPlanDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9HrTrainingPlan trainingPlan = (T9HrTrainingPlan) this.logic.getTriningPlanDetailLogic(dbConn, Integer.parseInt(seqId));
			if (trainingPlan == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "培训计划信息详情不存在");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(trainingPlan);
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
	 * 更新培训计划信息--wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateTrainingPlanInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String contexPath = request.getContextPath();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = this.logic.fileUploadLogic(fileForm, attachmentFolder);
			this.logic.updateTrainingPlanInfoLogic(dbConn, fileForm, person, map);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		response.sendRedirect(contexPath + "/subsys/oa/training/plan/planManage.jsp");
		return null;
	}

	/**
	 * 浮动菜单文件删除--wyw
	 * 
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
	 * 培训计划查询 --wyw
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryTrainingPlanListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("tPlanName", T9DBUtility.escapeLike(request.getParameter("tPlanName")));
			map.put("tChannel", T9DBUtility.escapeLike(request.getParameter("tChannel")));
			map.put("tCourseTypes", T9DBUtility.escapeLike(request.getParameter("tCourseTypes")));
			map.put("tAddress", T9DBUtility.escapeLike(request.getParameter("tAddress")));
			map.put("tInstitutionName", T9DBUtility.escapeLike(request.getParameter("tInstitutionName")));
			map.put("courseStartDate1", T9DBUtility.escapeLike(request.getParameter("courseStartDate1")));
			map.put("courseStartDate2", T9DBUtility.escapeLike(request.getParameter("courseStartDate2")));
			String data = "";
			data = this.logic.queryTrainingPlanListJsonLogic(dbConn, request.getParameterMap(), map, person);
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
	 * 检查数据库是否已经有该值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String checkPlanNo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String checkPlanNo = request.getParameter("tPlanNo");
		
		String seqIdStr = request.getParameter("seqId");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			int isHave =this.logic.checkPlanNoLogic(dbConn,checkPlanNo,seqIdStr);
			
			String data = "{isHave:" + isHave +"}";
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
