package t9.core.funcs.filefolder.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.filefolder.data.T9FileSort;
import t9.core.funcs.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9FileSortAct {

	/**
	 * 新建文件夹
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addFileSortInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9FileSortLogic fileSortLogic = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			fileSortLogic = new T9FileSortLogic();
			T9FileSort fileSort = (T9FileSort) T9FOM.build(request.getParameterMap());
			fileSortLogic.saveFileSortInfo(dbConn, fileSort);
			dbConn.close();
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 取得文件夹所有信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileSortInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9FileSortLogic fileSortLogic = new T9FileSortLogic();
		StringBuffer sb = new StringBuffer("[");
		List<T9FileSort> fileSorts = new ArrayList<T9FileSort>();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			fileSorts = fileSortLogic.getFileSortsInfo(dbConn);
			for (int i = 0; i < fileSorts.size(); i++) {
				T9FileSort fileSort = fileSorts.get(i);
				String sortNo = fileSort.getSortNo() == null ? "" : fileSort.getSortNo();
				sb.append("{");
				sb.append("sqlId:\"" + fileSort.getSeqId() + "\"");
				sb.append(",sortParent:\"" + fileSort.getSortParent() + "\"");
				sb.append(",sortNo:\"" + sortNo + "\"");
				String fileSortNameStr = fileSort.getSortName() == null ? "" : fileSort.getSortName();
				fileSortNameStr = fileSortNameStr.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
				sb.append(",sortName:\"" + fileSortNameStr + "\"");
				sb.append(",sortType:\"" + fileSort.getSortType() + "\"");
				sb.append(",deptId:\"" + fileSort.getDeptId() + "\"");
				sb.append(",userId:\"" + fileSort.getUserId() + "\"");
				sb.append(",newUser:\"" + fileSort.getNewUser() + "\"");
				sb.append(",manageUser:\"" + fileSort.getManageUser() + "\"");
				sb.append(",DownUser:\"" + fileSort.getDownUser() + "\"");
				sb.append(",shareUser:\"" + fileSort.getShareUser() + "\"");
				sb.append(",owner:\"" + fileSort.getOwner() + "\"");
				sb.append("},");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 通过id获取信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9FileSortLogic fileSortLogic = new T9FileSortLogic();
		StringBuffer sb = new StringBuffer("[");
		String seqId = request.getParameter("seqId");
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9FileSort fileSort = fileSortLogic.getFileSortInfoById(dbConn, seqId);
			String sortNo = fileSort.getSortNo() == null ? "" : fileSort.getSortNo();
			String sortName = fileSort.getSortName();
			sortName = sortName.replaceAll("[\n-\r]", "<br>");
			sortName = sortName.replaceAll("[\\\\/:*?\"<>|]", "");
			sortName = sortName.replace("\"", "\\\"");

			sb.append("{");
			sb.append("sqlId:\"" + fileSort.getSeqId() + "\"");
			sb.append(",sortParent:\"" + fileSort.getSortParent() + "\"");
			sb.append(",sortNo:\"" + sortNo + "\"");
			sb.append(",sortName:\"" + sortName + "\"");
			sb.append(",sortType:\"" + fileSort.getSortType() + "\"");
			sb.append(",deptId:\"" + fileSort.getDeptId() + "\"");
			sb.append(",userId:\"" + fileSort.getUserId() + "\"");
			sb.append(",newUser:\"" + fileSort.getNewUser() + "\"");
			sb.append(",manageUser:\"" + fileSort.getManageUser() + "\"");
			sb.append(",DownUser:\"" + fileSort.getDownUser() + "\"");
			sb.append(",shareUser:\"" + fileSort.getShareUser() + "\"");
			sb.append(",owner:\"" + fileSort.getOwner() + "\"");
			sb.append("},");
			sb.append("]");
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
			dbConn.close();
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	public String updateFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9FileSortLogic fileSortLogicc = new T9FileSortLogic();
		int seqId = Integer.parseInt(request.getParameter("seqId"));
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9FileSort fileSort = (T9FileSort) T9FOM.build(request.getParameterMap());
			fileSort.setSeqId(seqId);
			fileSortLogicc.updateFileSortInfoById(dbConn, fileSort);
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
	 * 递归删除文件夹及下的所有文件信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFileSortInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int seqId = Integer.parseInt(request.getParameter("seqId"));
		T9FileSort fileSort = null;
		T9FileSortLogic fileSortLogic = new T9FileSortLogic();
		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		// 获取ip
		String ipStr = request.getRemoteAddr();
		T9ORM orm = new T9ORM();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9FileSort addLogFileSort = (T9FileSort) orm.loadObjSingle(dbConn, T9FileSort.class, seqId);
		  String nameStr = "";
      if (addLogFileSort!=null) {
        nameStr = T9Utility.null2Empty(addLogFileSort.getSortName());
      }
		      
			fileSort = new T9FileSort();
			fileSort.setSeqId(seqId);
			fileSortLogic.delFileSortInfoById(dbConn, fileSort, loginUserSeqId, ipStr);
		// 写入系统日志
      String remark = "删除目录，名称： " + nameStr ;
      T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, request.getRemoteAddr());
			
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
	 * 签阅情况
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String showReader(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String sortIdStr = request.getParameter("sortId");
		String contentIdStr = request.getParameter("contentId");
		int sortId = 0;
		int contentId = 0;
		if (sortIdStr != null) {
			sortId = Integer.parseInt(sortIdStr);
		}
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}
		T9FileSortLogic logic = new T9FileSortLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
			String userIdStrs = "";
			if (fileSort != null) {
				userIdStrs = fileSort.getUserId();
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "数据取出成功！");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 取得文件夹名
	 * @return
	 * @throws Exception
	 */
	public String getFolderName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		T9FileSortLogic logic = new T9FileSortLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String sortName = "";
			if ("0".equals(seqId)) {
				sortName = "根目录";
			} else {
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, seqId);
				if (fileSort != null) {
					sortName = T9Utility.null2Empty(fileSort.getSortName());
				}
			}
			String data = "{folderName:\"" + T9Utility.encodeSpecial(sortName) + "\"}";
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
