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
import t9.subsys.oa.hr.setting.logic.T9HrSetOtherLogic;

public class T9HrSetOtherAct {
	public static final String attachmentFolder = "hr";
	private T9HrSetOtherLogic logic = new T9HrSetOtherLogic();

	/**
	 * 获取是否允许人力资源管理员设置OA登录权限值(新建)

	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getHrSetUserLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getHrSetUserLogin(dbConn);
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
	 * 获取休年龄默认值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getHrRetireAge(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getHrRetireAge(dbConn);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 设置值
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setOtherValue(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String yesOther = request.getParameter("yesOther");
		String manAge = request.getParameter("manAge");
		String womenAge = request.getParameter("womenAge");
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			 this.logic.setOtherValueLogic(dbConn,T9Utility.null2Empty(yesOther),manAge,womenAge);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	/**
	 * 获取是否允许人力资源管理员设置OA登录权限值(编辑)
	 * 2011-4-14
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getHrSetUserLogin2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = T9Utility.null2Empty(request.getParameter("seqId"));
		String treeFlag = T9Utility.null2Empty(request.getParameter("treeFlag"));
		int seqId = 0;
		
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			int counter = 0;
			if (T9Utility.isNumber(seqIdStr) && !"1".equals(treeFlag)) { //人事档案编辑
				seqId = Integer.parseInt(seqIdStr);
				counter = this.logic.getPersongCountLogic1(dbConn,seqId);
			} else {//如是左侧人员树传来值
				counter = this.logic.getPersongCountLogic2(dbConn,seqIdStr);
			}
			String isLogin = this.logic.getHrSetUserLogin(dbConn);
			String data = "{isLogin:\"" + isLogin + "\",counter:\"" + counter + "\" }";
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_DATA, data );
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}
	

}
