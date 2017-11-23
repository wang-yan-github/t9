package t9.subsys.oa.rollmanage.act;

import java.io.File;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.jexcel.util.T9CSVUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsFile;
import t9.subsys.oa.rollmanage.logic.T9RmsFileLogic;

public class T9RmsFileAct {
	private T9RmsFileLogic logic = new T9RmsFileLogic();

	/**
	 * 获取下拉列表值
	 * 
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
	 * 新建文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addFileInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

			// 保存从文件柜、网络硬盘选择附件
			T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "roll_manage");
			String attIdStr = sel.getAttachIdToString(",");
			String attNameStr = sel.getAttachNameToString("*");
			boolean fromFolderFlag = false;
			String newAttchId = "";
			String newAttchName = "";
			if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
				newAttchId = attIdStr + ",";
				newAttchName = attNameStr + "*";
				fromFolderFlag = true;
			}
			Iterator<String> iKeys = fileForm.iterateFileFields();
			boolean uploadFlag = false;
			boolean docAttachmentFlag = false;
			String attachmentId = "";
			String attachmentName = "";
			String docAttachmentId = "";
			String docAttachmentName = "";
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			String separator = File.separator;
			String filePath = T9SysProps.getAttachPath() + separator + "roll_manage" + separator + currDate;

			while (iKeys.hasNext()) {
				String fieldName = iKeys.next();
				String fileName = fileForm.getFileName(fieldName);

				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}
				T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
				String rand = emul.getRandom();

				if (!T9Utility.isNullorEmpty(fieldName) && "ATTACHMENT_DOC".equals(fieldName.trim())) {
					docAttachmentId += currDate + "_" + rand + ",";
					docAttachmentName += fileName + "*";
					docAttachmentFlag = true;
				} else {
					attachmentId += currDate + "_" + rand + ",";
					attachmentName += fileName + "*";
					uploadFlag = true;
				}

				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath + File.separator + fileName);
			}

			Map<Object, Object> map = new HashMap<Object, Object>();
			
			map.put("fromFolderFlag", fromFolderFlag);
			map.put("uploadFlag", uploadFlag);
			map.put("docAttachmentFlag", docAttachmentFlag);
			
			map.put("newAttchId", newAttchId);
			map.put("attachmentId", attachmentId);
			map.put("docAttachmentId", docAttachmentId);
			
			map.put("newAttchName", newAttchName);
			map.put("attachmentName", attachmentName);			
			map.put("docAttachmentName", docAttachmentName);			

			
			this.logic.setRmsFileValue(dbConn, fileForm, person, map);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/subsys/oa/rollmanage/rollfile/newFileWarn.jsp";
	}

	/**
	 * 取得文件列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getRmsFileJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data = this.logic.getRmsFileJosn(dbConn, request.getParameterMap(), person);
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
	 * 根据seqId取出RmsFile信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getRmsFileDetailById(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String seqIdStr = request.getParameter("seqId");

		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			// T9RmsFileLogic logic = new T9RmsFileLogic();

			T9RmsFile rmsFile = this.logic.getRmsFileDetailById(dbConn, seqId);

			if (rmsFile == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "文件不存在");
				return "/core/inc/rtjson.jsp";
			}

			StringBuffer data = T9FOM.toJson(rmsFile);
			
			T9PersonLogic logic = new T9PersonLogic();
			String userName = T9Utility.encodeSpecial(logic.getNameBySeqIdStr(T9Utility.null2Empty(rmsFile.getPrivUser()), dbConn));
			T9DeptLogic logic2 =  new T9DeptLogic();
			String deptName = T9Utility.encodeSpecial(logic2.getNameByIdStr(T9Utility.null2Empty(rmsFile.getPrivDept()), dbConn));
			T9UserPrivLogic logic3 =  new T9UserPrivLogic();
      String roleName = T9Utility.encodeSpecial(logic3.getNameByIdStr(T9Utility.null2Empty(rmsFile.getPrivRole()), dbConn));
      
			data.append(",").append("privUserName:\"").append(userName).append("\"");
			data.append(",").append("privDeptName:\"").append(deptName).append("\"");
			data.append(",").append("privRoleName:\"").append(roleName).append("\"");
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
   * 获取案卷下拉列表值

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollSelectOption2(HttpServletRequest request, HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getRmsRollSelectOption2(dbConn , loginUser.getDeptId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
	/**
	 * 获取案卷下拉列表值
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getRmsRollSelectOption(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			String data = this.logic.getRmsRollSelectOption(dbConn);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data);

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 修改文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateRmsFileById(HttpServletRequest request, HttpServletResponse response) throws Exception {

		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		String seqIdStr = (String) fileForm.getParameter("seqId");
		String fileCode = (String) fileForm.getParameter("fileCode");
		String fileSubject = (String) fileForm.getParameter("fileSubject");
		String fileTitle = (String) fileForm.getParameter("fileTitle");

		String fileTitleo = (String) fileForm.getParameter("fileTitleo");
		String sendUnit = (String) fileForm.getParameter("sendUnit");
		String sendDate = (String) fileForm.getParameter("sendDate");
		String secret = (String) fileForm.getParameter("secret");
		String urgency = (String) fileForm.getParameter("urgency");
		String fileType = (String) fileForm.getParameter("fileType");
		String fileKind = (String) fileForm.getParameter("fileKind");
		String filePage = (String) fileForm.getParameter("filePage");
		String printPage = (String) fileForm.getParameter("printPage");
		String remark = (String) fileForm.getParameter("remark");
		String rollIdStr = (String) fileForm.getParameter("rollId");
		String downloadYnStr = (String) fileForm.getParameter("downloadYn");

		String fileYear = (String) fileForm.getParameter("fileYear");
    String fileWord = (String) fileForm.getParameter("fileWord");
    String issueNum = (String) fileForm.getParameter("issueNum");
    
    String privUser = T9Utility.null2Empty((String) fileForm.getParameter("privUser"));
    String privDept =T9Utility.null2Empty((String) fileForm.getParameter("privDept"));
    String privRole =T9Utility.null2Empty((String) fileForm.getParameter("role"));
    
    
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}

		int rollId = 0;
		int downloadYn = 0;
		if (!T9Utility.isNullorEmpty(rollIdStr)) {
			rollId = Integer.parseInt(rollIdStr);
		}
		if (!T9Utility.isNullorEmpty(downloadYnStr)) {
			downloadYn = Integer.parseInt(downloadYnStr);
		}

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9RmsFile rmsFile = this.logic.getRmsFileDetailById(dbConn, seqId);
			String dbAttchId = T9Utility.null2Empty(rmsFile.getAttachmentId());
			String dbAttchName = T9Utility.null2Empty(rmsFile.getAttachmentName());

			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

			// 保存从文件柜、网络硬盘选择附件
			T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "roll_manage");
			String attIdStr = sel.getAttachIdToString(",");
			String attNameStr = sel.getAttachNameToString("*");

			boolean fromFolderFlag = false;
			String newAttchId = "";
			String newAttchName = "";
			if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
				newAttchId = attIdStr + ",";
				newAttchName = attNameStr + "*";
				fromFolderFlag = true;

			}

			Iterator<String> iKeys = fileForm.iterateFileFields();

			boolean uploadFlag = false;
			String attachmentId = "";
			String attachmentName = "";

			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			String separator = File.separator;
			String filePath = T9SysProps.getAttachPath() + separator + "roll_manage" + separator + currDate;

			while (iKeys.hasNext()) {
				String fieldName = iKeys.next();
				String fileName = fileForm.getFileName(fieldName);

				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}

				T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
				String rand = emul.getRandom();

				attachmentId += currDate + "_" + rand + ",";
				attachmentName += fileName + "*";

				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath + File.separator +fileName);

				uploadFlag = true;
			}

			if (fromFolderFlag && uploadFlag) {

				rmsFile.setAttachmentId(dbAttchId + newAttchId + attachmentId);
				rmsFile.setAttachmentName(dbAttchName + newAttchName + attachmentName);

			} else if (fromFolderFlag) {
				rmsFile.setAttachmentId(dbAttchId + newAttchId);
				rmsFile.setAttachmentName(dbAttchName + newAttchName);
			} else if (uploadFlag) {
				rmsFile.setAttachmentId(dbAttchId + attachmentId);
				rmsFile.setAttachmentName(dbAttchName + attachmentName);

			}

			rmsFile.setModUser(String.valueOf(person.getSeqId()));
			rmsFile.setModTime(new Date());
			rmsFile.setFileCode(fileCode);
			rmsFile.setFileTitle(fileTitle);
			rmsFile.setFileTitleo(fileTitleo);
			rmsFile.setFileSubject(fileSubject);
			rmsFile.setSendUnit(sendUnit);
			rmsFile.setSendDate(T9Utility.parseDate(sendDate));
			rmsFile.setSecret(secret);
			rmsFile.setUrgency(urgency);
			rmsFile.setFileKind(fileKind);
			rmsFile.setFileType(fileType);
			rmsFile.setFilePage(filePage);
			rmsFile.setPrintPage(printPage);
			rmsFile.setRemark(remark);
			rmsFile.setDownloadYn(downloadYn);
			rmsFile.setRollId(rollId);

			rmsFile.setFileWord(fileWord);
			rmsFile.setFileYear(fileYear);
			rmsFile.setIssueNum(issueNum);
			
			rmsFile.setPrivRole(privRole);
      rmsFile.setPrivUser(privUser);
      rmsFile.setPrivDept(privDept);
      
			this.logic.updateRmsFileByObj(dbConn, rmsFile);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath + "/subsys/oa/rollmanage/rollfile/fileManage.jsp");

		return null;
	}

	/**
	 * 文件销毁人
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String destroySingleFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			// seqId=Integer.parseInt(seqIdStr);
			seqIdStr = "0";
		}

		T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			this.logic.updateRmsFileById(dbConn, String.valueOf(person.getSeqId()), seqIdStr);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 浮动菜单文件删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String seqIdStr = request.getParameter("seqId");
		String attachId = request.getParameter("attachId");
		String attachName = request.getParameter("attachName");

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			boolean updateFlag = this.logic.updateFloadFile(dbConn, seqIdStr, T9Utility.null2Empty(attachId), T9Utility.null2Empty(attachName));

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
	 * 查询文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryRmsFileJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("fileCode", request.getParameter("fileCode"));
		map.put("fileSubject", request.getParameter("fileSubject"));
		map.put("fileTitle", request.getParameter("fileTitle"));
		map.put("fileTitleo", request.getParameter("fileTitleo"));
		map.put("sendUnit", request.getParameter("sendUnit"));

		map.put("sendTimeMin", request.getParameter("sendTimeMin"));
		map.put("sendTimeMax", request.getParameter("sendTimeMax"));
		map.put("secret", request.getParameter("secret"));
		map.put("urgency", request.getParameter("urgency"));
		map.put("fileType", request.getParameter("fileType"));
		map.put("fileKind", request.getParameter("fileKind"));

		map.put("filePage1", request.getParameter("filePage1"));
		map.put("filePage2", request.getParameter("filePage2"));
		map.put("printPage1", request.getParameter("printPage1"));
		map.put("printPage2", request.getParameter("printPage2"));
		map.put("remark", request.getParameter("remark"));
		map.put("handlerTime", request.getParameter("handlerTime"));
		map.put("fileWord", request.getParameter("fileWord"));
    map.put("fileYear", request.getParameter("fileYear"));
    map.put("issueNum", request.getParameter("issueNum"));
		Connection dbConn;
		try {

			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

			String data = this.logic.queryRmsFileLogic(dbConn, request.getParameterMap(), person, map);
			PrintWriter pw = response.getWriter();
			pw.println(data);
			pw.flush();

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return null;
	}

	/**
	 * 组卷至
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String changeRoll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqIdStr");
		String rollIdStr = request.getParameter("rollId");
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			seqIdStr = "0";
		}
		int rollId = 0;
		if (!T9Utility.isNullorEmpty(rollIdStr)) {
			rollId = Integer.parseInt(rollIdStr);
		}

		Connection dbConn;
		try {

			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			this.logic.changeRollLogic(dbConn, seqIdStr, rollId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 取得已销毁文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getDestroyRmsFileJosn(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

			String data = this.logic.getDestroyFileLogic(dbConn, request.getParameterMap(), person);

			PrintWriter pw = response.getWriter();
			pw.println(data);
			pw.flush();
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return null;
	}

	/**
	 * 还原文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String recoverFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			seqIdStr = "0";
		}

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			this.logic.updateDestroyFileById(dbConn, seqIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 删除文件
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

			String filePath = T9SysProps.getAttachPath() + File.separator + "roll_manage" + File.separator;

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
	 * 导出到csv
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String exportFileToCsv(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  response.setCharacterEncoding(T9Const.CSV_FILE_CODE);
	  Connection conn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			conn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute("LOGIN_USER");
			String seqIdStr = request.getParameter("seqIdStr");
			String fileName = URLEncoder.encode("文件档案.csv", "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
			response.setHeader("Cache-control", "private");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			ArrayList<T9DbRecord> dbL = this.logic.toExportRmsFileData(conn, seqIdStr);
			T9CSVUtil.CVSWrite(response.getWriter(), dbL);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return null;
	}

}
