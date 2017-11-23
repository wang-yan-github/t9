package t9.core.funcs.filefolder.act;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.filefolder.data.T9FileContent;
import t9.core.funcs.filefolder.data.T9Page;
import t9.core.funcs.filefolder.logic.T9FileContentLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.office.ntko.logic.T9NtkoLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.filefolder.data.T9FileSort;
import t9.core.funcs.system.filefolder.logic.T9FileSortLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9FileContentAct {
	private static Logger log = Logger.getLogger("t9.core.funcs.filefolder.T9FileContentAct");

	/**
	 * 获得文件夹下的所有内容信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileContentInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		String pageNoStr = request.getParameter("pageNo");
		String pageSizeStr = request.getParameter("pageSize");
		String orderBy = request.getParameter("field");
		String ascDesc = request.getParameter("ascDescFlag");
		if (orderBy == null) {
			orderBy = "";
		}
		if (ascDesc == null) {
			ascDesc = "";
		}
		int sortId = 0;
		if (seqId != null) {
			sortId = Integer.parseInt(seqId);
		}
		int currNo = 1;
		if (T9Utility.isNullorEmpty(pageNoStr)) {
			currNo = 1;
		} else {
			currNo = Integer.parseInt(pageNoStr);
		}
		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		Map map = new HashMap();
		String condition = "SORT_ID=" + sortId;
		if ("NAME".equals(orderBy.trim())) {
			if ("1".equals(ascDesc.trim())) {
				condition = "SORT_ID=" + sortId + " order by SUBJECT desc ";
			} else {
				condition = "SORT_ID=" + sortId + " order by SUBJECT asc ";
			}
		} else if ("SENDTIME".equals(orderBy.trim())) {
			if ("1".equals(ascDesc.trim())) {
				condition = "SORT_ID=" + sortId + " order by SEND_TIME desc ";
			} else {
				condition = "SORT_ID=" + sortId + " order by SEND_TIME asc ";
			}
		} else if ("CONTENTNO".equals(orderBy.trim())) {
			if ("1".equals(ascDesc.trim())) {
				condition = "SORT_ID=" + sortId + " order by CONTENT_NO desc ";
			} else {
				condition = "SORT_ID=" + sortId + " order by CONTENT_NO asc ";
			}
		}

		String[] filters = { condition };

		Connection dbConn = null;
		T9FileContentLogic contentLogic = new T9FileContentLogic();
		StringBuffer sb = new StringBuffer();
		List<T9FileContent> fileContents = new ArrayList<T9FileContent>();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			fileContents = contentLogic.getFileContentsByFilters(dbConn, filters);
			long count = fileContents.size();
			int pageSize = 20;// 一个页面显示的数目
			if (pageSizeStr != null) {
				pageSize = Integer.parseInt(pageSizeStr);
			}

			T9Page page = new T9Page(pageSize, count, (currNo + 1));
			long first = page.getFirstResult();
			long last = page.getLastResult();
			String RET_MSRG="成功取出数据";
			if (fileContents.size() > 0) {
				sb.append("[");
				for (int i = (int) first; i < last; i++) {
					T9FileContent content = fileContents.get(i);
					String subject = content.getSubject() == null ? "" : content.getSubject();
					String attachmentName = content.getAttachmentName() == null ? "" : content.getAttachmentName();
					String contentNo = content.getContentNo() == null ? "" : content.getContentNo();
					String attachmentDesc = content.getAttachmentDesc() == null ? "" : content.getAttachmentDesc();
					String date = "";
					if (content.getSendTime() != null) {
						date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(content.getSendTime());
					}
					String sendTime = date == null ? "" : date;
					String YM = contentLogic.getFilePathFolder(T9Utility.null2Empty(content.getAttachmentId()));
					boolean isRead = contentLogic.isHaveReaderId(loginUserSeqId, content.getReaders());
					int reader = 0;
					if (isRead) {
						reader = 1;
					}
					sb.append("{");
					sb.append("contentId:\"" + content.getSeqId() + "\"");
					sb.append(",sortId:\"" + content.getSortId() + "\"");

//					subject = subject.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");

//					contentNo = contentNo.replaceAll("[\n-\r]", "<br>");
//					contentNo = contentNo.replaceAll("[\\\\/:*?\"<>|]", "");
//					contentNo = contentNo.replace("\"", "\\\"");

//					attachmentDesc = attachmentDesc.replaceAll("[\n-\r]", "<br>");
//					attachmentDesc = attachmentDesc.replaceAll("[\\\\/:*?\"<>|]", "");
//					attachmentDesc = attachmentDesc.replace("\"", "\\\"");

					sb.append(",subject:\"" + T9Utility.encodeSpecial(subject) + "\"");
					String contentStr = T9Utility.null2Empty(content.getContent());
//					contentStr = contentStr.replaceAll("[\n-\r]", "<br>");
//					contentStr = contentStr.replace("\"", "\\\"");

					sb.append(",content:\"" + T9Utility.encodeSpecial(contentStr) + "\"");
					sb.append(",sendTime:\"" + sendTime + "\"");
					sb.append(",attachmentId:\"" + T9Utility.null2Empty(content.getAttachmentId()) + "\"");
					sb.append(",attachmentName:\"" + attachmentName + "\"");
					sb.append(",attachmentDesc:\"" + T9Utility.encodeSpecial(attachmentDesc) + "\"");
					sb.append(",userId:\"" + content.getUserId() + "\"");
					sb.append(",contentNo:\"" + T9Utility.encodeSpecial(contentNo) + "\"");
					sb.append(",newPerson:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(content.getNewPerson())) + "\"");
					sb.append(",readers:\"" + reader + "\"");
					sb.append(",creater:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(content.getCreater())) + "\"");
					sb.append(",YM:\"" + YM + "\"");
					sb.append(",ascDesc:\"" + ascDesc + "\"");
					sb.append(",orderBy:\"" + orderBy + "\"");

					sb.append(",totalRecord:\"" + count + "\"");
					sb.append(",pageNo:\"" + currNo + "\"");
					sb.append(",pageSize:\"" + pageSize + "\"");
					sb.append("},");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("]");
			} else {
				sb.append("[]");
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, RET_MSRG);
			request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 从公共文件柜选择附件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileContentsById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		int sortId = 0;
		if (seqId != null) {
			sortId = Integer.parseInt(seqId);
		}

		String ext_filter = request.getParameter("EXT_FILTER");
		if (ext_filter == null) {
			ext_filter = "";
		}

		String div_id = request.getParameter("DIV_ID");
		if (div_id == null) {
			div_id = "";
		}
		String dir_field = request.getParameter("DIR_FIELD");
		if (dir_field == null) {
			dir_field = "";
		}
		String name_field = request.getParameter("NAME_FIELD");
		if (name_field == null) {
			name_field = "";
		}
		String type_field = request.getParameter("TYPE_FIELD");
		if (type_field == null) {
			type_field = "";
		}
		String multi_select = request.getParameter("MULTI_SELECT");
		if (multi_select == null) {
			multi_select = "";
		}

		T9FileContentLogic contentLogic = new T9FileContentLogic();
		StringBuffer sb = new StringBuffer();

		Map map = new HashMap();
		map.put("SORT_ID", sortId);
		boolean isHave = false;

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			List<T9FileContent> fileContents = contentLogic.getFileContentsInfo(dbConn, map);

			if (fileContents.size() > 0) {
				sb.append("[");
				for (int i = 0; i < fileContents.size(); i++) {
					T9FileContent content = fileContents.get(i);
					String subject = content.getSubject() == null ? "" : content.getSubject();
					String attachmentId = content.getAttachmentName() == null ? "" : content.getAttachmentName();
					String attachmentName = content.getAttachmentName() == null ? "" : content.getAttachmentName();
					if (attachmentId != null && !"".equals(attachmentId)) {

						sb.append("{");
						sb.append("contentId:\"" + content.getSeqId() + "\"");
						sb.append(",sortId:\"" + content.getSortId() + "\"");
						sb.append(",subject:\"" + subject + "\"");
						sb.append(",attachmentId:\"" + content.getAttachmentId() + "\"");
						sb.append(",attachmentName:\"" + attachmentName + "\"");

						sb.append(",ext_filter:\"" + ext_filter + "\"");
						sb.append(",div_id:\"" + div_id + "\"");
						sb.append(",dir_field:\"" + dir_field + "\"");
						sb.append(",name_field:\"" + name_field + "\"");
						sb.append(",type_field:\"" + type_field + "\"");
						sb.append(",multi_select:\"" + multi_select + "\"");

						sb.append("},");

						isHave = true;

					}

				}
				if (isHave) {

					sb.deleteCharAt(sb.length() - 1);

				}
				sb.append("]");
			} else {
				sb.append("[]");
			}
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
	 * 新建文件信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addNewFileInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;

		// 保存从文件柜、网络硬盘选择附件
		T9SelAttachUtil sel = new T9SelAttachUtil(request, "file_folder");
		String attIdStr = sel.getAttachIdToString(",");
		String attNameStr = sel.getAttachNameToString("*");

		T9FileContentLogic contentLogic = new T9FileContentLogic();
		String seqIdStr = request.getParameter("seqId");
		String type = request.getParameter("fileType");
		String subject = request.getParameter("subject");
		String contentNo = request.getParameter("contentNo");
		String content = request.getParameter("content");
		String attachmentName = request.getParameter("attachmentName");
		String attachmentDesc = request.getParameter("attachmentDesc");
		String contentIdStr = request.getParameter("contentId");
		String smsPerson = request.getParameter("smsPerson");
		String mobileSmsPerson = request.getParameter("mobileSmsPerson");

		String folderPath = request.getParameter("folderPath");
		if (folderPath == null) {
			folderPath = "";
		}

		int contentId = 0;
		int sortId = 0;
		if (seqIdStr != null) {
			sortId = Integer.parseInt(seqIdStr);
		}

		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}
		if (smsPerson == null) {
			smsPerson = "";
		}
		if (mobileSmsPerson == null) {
			mobileSmsPerson = "";
		}
		if (attachmentName == null) {
			attachmentName = "";
		}else {
			attachmentName = attachmentName.trim();
		}

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			boolean fromFolderFlag = false;
			String newAttchId = "";
			String newAttchName = "";
			if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
				newAttchId = attIdStr + ",";
				newAttchName = attNameStr + "*";
				fromFolderFlag = true;

			}

			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(new Date());

			T9FileContent fileContent = new T9FileContent();

			int newContentId = 0;
			int newSortId = 0;

			if (contentId != 0) {
			  newContentId = contentId;
				T9FileContent fileContentStr = contentLogic.getFileContentInfoById(dbConn, contentId);
				String attIdString = fileContentStr.getAttachmentId();
				String attNameString = fileContentStr.getAttachmentName();
				fileContentStr.setAttachmentId(attIdString + newAttchId.trim());
				fileContentStr.setAttachmentName(attNameString + newAttchName.trim());
				fileContentStr.setContentNo(contentNo);
				fileContentStr.setSubject(subject);
				fileContentStr.setContent(content);
				fileContentStr.setAttachmentDesc(attachmentDesc);
				fileContentStr.setSendTime(T9Utility.parseTimeStamp());
				contentLogic.updateSingleObj(dbConn, fileContentStr);

				// 系统日志
				String remark = "编辑文件,名称:" + subject;
				T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, request.getRemoteAddr());

			} else {
				boolean newTypeFlag = false;

				String attIdString = "";
				String attNameString = "";
				String realPath = request.getRealPath("/");

				if (!T9Utility.isNullorEmpty(type.trim()) && !T9Utility.isNullorEmpty(attachmentName)) {
					String rand = contentLogic.createFile(type, attachmentName, realPath);
					if (!rand.equals("0")) {
						attIdString = currDate + "_" + String.valueOf(rand) + ",";
						attNameString = attachmentName + "." + type.trim() + "*";
						newTypeFlag = true;
					}
				}

				if (newTypeFlag && fromFolderFlag) {
					fileContent.setAttachmentId(newAttchId.trim() + attIdString.trim());
					fileContent.setAttachmentName(newAttchName.trim() + attachmentName.trim());

				} else if (newTypeFlag) {
					fileContent.setAttachmentId(attIdString.trim());
					fileContent.setAttachmentName(attNameString.trim());
				} else if (fromFolderFlag) {
					fileContent.setAttachmentId(newAttchId.trim());
					fileContent.setAttachmentName(newAttchName.trim());
				}

				fileContent.setSortId(sortId);
				fileContent.setContentNo(contentNo);
				fileContent.setSubject(subject);
				fileContent.setContent(content);
				fileContent.setAttachmentDesc(attachmentDesc);
				// fileContent.setUserId(String.valueOf(loginUserSeqId));
				fileContent.setSendTime(T9Utility.parseTimeStamp());
				fileContent.setCreater(String.valueOf(loginUserSeqId));
				contentLogic.saveSingleObj(dbConn, fileContent);
				T9FileContent maxContent = contentLogic.getMaxSeqId(dbConn);
				newContentId = maxContent.getSeqId();
				newSortId = maxContent.getSortId();
			}

			// 系统日志
			String remark = "新建文件,名称:" + subject;
			T9SysLogLogic.addSysLog(dbConn, T9LogConst.FILE_FOLDER, remark, loginUserSeqId, request.getRemoteAddr());

			// 短信提醒
			// T9SmsUtil sms=new T9SmsUtil();
			T9SmsBack sms = new T9SmsBack();
			String loginName = contentLogic.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
			String smsContent = loginName + " 在公共文件柜 " + folderPath + " 下建立新文件:" + subject;
			String remindUrl = "/core/funcs/filefolder/read.jsp?sortId=" + sortId + "&contentId=" + newContentId + "&newFileFlag=1&openFlag=1";
			if ("allPrivPerson".equals(smsPerson)) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");

				if (!T9Utility.isNullorEmpty(personIdStr)) {
					personIdStr += ",";
				}
				// 判断这个部门是否有权限//				String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
//				String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
				// 如有权限，获取该部门下的所有人员id串//				String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
//				String rolePrivIdStrs = logic.getRolePersonIdStr(loginUserDeptId, roleIdStr, dbConn);
				String deptPersonIdStr ="";
				String rolePersonIdStr ="";
				if (!T9Utility.isNullorEmpty(deptIdStr)) {
					deptPersonIdStr = logic.getDeptPersonIds(deptIdStr, dbConn);
				}
				if (!T9Utility.isNullorEmpty(roleIdStr)) {
					rolePersonIdStr = logic.getRolePersonIds(roleIdStr, dbConn);
				}
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					sms.setFromId(loginUserSeqId);
					sms.setToId(allpersonStr.trim());
					sms.setContent(smsContent);
					sms.setSendDate(T9Utility.parseTimeStamp());
					sms.setSmsType(T9LogConst.FILE_FOLDER);
					sms.setRemindUrl(remindUrl);
					T9SmsUtil.smsBack(dbConn, sms);
				}
			} else if (!"".equals(smsPerson)) {
				sms.setFromId(loginUserSeqId);
				sms.setToId(smsPerson);
				sms.setContent(smsContent);
				sms.setSendDate(T9Utility.parseTimeStamp());
				sms.setSmsType(T9LogConst.FILE_FOLDER);
				sms.setRemindUrl(remindUrl);
				T9SmsUtil.smsBack(dbConn, sms);
			}
			// 手机短信提醒mobileSmsPerson
			String mobileSmsContent = loginName + " 在公共文件柜 " + folderPath + " 下建立新文件:" + subject;
			T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
			if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");
				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				// 判断这个部门是否有权限				String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
				String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
				// 如有权限，获取该部门下的所有人员id串				String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
				String rolePersonIdStr = logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;

				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}

				if (allpersonStr != null && !"".equals(allpersonStr)) {
					mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId, mobileSmsContent, null);
				}
			} else if (!"".equals(mobileSmsPerson.trim())) {
				mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId, mobileSmsContent, null);
			}
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
	 * 批量文件上传
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		T9FileContentLogic contentLogic = new T9FileContentLogic();
		T9FileContent content = new T9FileContent();
		String seqId = request.getParameter("seqId");
		String remoteAddr = request.getRemoteAddr();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			
			T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			String separator = File.separator;
			String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator + currDate;
			
			T9FileUploadForm fileForm = new T9FileUploadForm();
			fileForm.parseUploadRequest(request);
			String fileExists = fileForm.getExists(filePath);
			if (fileExists != null) {
				response.setCharacterEncoding(T9Const.DEFAULT_CODE);
				response.setContentType("text/html;charset=UTF-8");
				PrintWriter pw = response.getWriter();
				pw.println("-ERR 文件\"" + fileExists + "\"已经存在！");
				pw.flush();
				return null;
			}
			
			contentLogic.uploadFileLogic(dbConn, content, fileForm,loginUser,seqId,remoteAddr,filePath);

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
	 * 根据contentId获得文件夹下的所有内容信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getFileContentInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contentIdStr = request.getParameter("contentId");
		int contentId = 0;
		if (contentIdStr != null && !"".equals(contentIdStr)) {
			contentId = Integer.parseInt(contentIdStr);
		}

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		T9FileContentLogic contentLogic = new T9FileContentLogic();

		StringBuffer sb = new StringBuffer("[");
		T9FileContent fileContent = new T9FileContent();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			fileContent = contentLogic.getFileContentInfoById(dbConn, contentId);
			if (fileContent != null) {

				String subject = fileContent.getSubject() == null ? "" : fileContent.getSubject();
				String content = fileContent.getContent() == null ? "" : fileContent.getContent();
				String attachmentDesc = fileContent.getAttachmentDesc() == null ? "" : fileContent.getAttachmentDesc();

				String attachmentName = fileContent.getAttachmentName() == null ? "" : fileContent.getAttachmentName();
				String contentNo = fileContent.getContentNo() == null ? "" : fileContent.getContentNo();
				String date = "";
				if (fileContent.getSendTime() != null) {
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fileContent.getSendTime());
				}
				String sendTime = date == null ? "" : date;

				boolean isRead = contentLogic.isHaveReaderId(loginUserSeqId, fileContent.getReaders());
				int reader = 0;
				if (isRead) {
					reader = 1;
				}
				String createName = contentLogic.getPersonNamesByIds(dbConn, fileContent.getCreater());

				sb.append("{");
				sb.append("contentId:\"" + fileContent.getSeqId() + "\"");
				sb.append(",sortId:\"" + fileContent.getSortId() + "\"");

				sb.append(",subject:\"" + T9Utility.encodeSpecial(subject) + "\"");

//				String contentStr = content.replaceAll("[\n-\r]", "<br>");
//				contentStr = contentStr.replace("\"", "\\\"");

//				contentNo = contentNo.replaceAll("[\n-\r]", "<br>");
//				contentNo = contentNo.replaceAll("[\\\\/:*?\"<>|]", "");
//				contentNo = contentNo.replace("\"", "\\\"");

//				attachmentDesc = attachmentDesc.replaceAll("[\n-\r]", "<br>");
//				attachmentDesc = attachmentDesc.replaceAll("[\\\\/:*?\"<>|]", "");
//				attachmentDesc = attachmentDesc.replace("\"", "\\\"");

				sb.append(",content:\"" + T9Utility.encodeSpecial(content) + "\"");
				sb.append(",sendTime:\"" + sendTime + "\"");
				sb.append(",attachmentId:\"" + T9Utility.null2Empty(fileContent.getAttachmentId()).trim() + "\"");
				sb.append(",attachmentName:\"" + T9Utility.encodeSpecial(attachmentName.trim()) + "\"");
				sb.append(",attachmentDesc:\"" + T9Utility.encodeSpecial(attachmentDesc) + "\"");
				sb.append(",userId:\"" + T9Utility.null2Empty(fileContent.getUserId()) + "\"");
				sb.append(",contentNo:\"" + T9Utility.encodeSpecial(contentNo) + "\"");
				sb.append(",newPerson:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(fileContent.getNewPerson())) + "\"");
				sb.append(",readers:\"" + reader + "\"");
				sb.append(",creater:\"" + T9Utility.encodeSpecial(createName) + "\"");
				String logStr = T9Utility.null2Empty(fileContent.getLogs());
				logStr.replaceAll("[\n-\r]", "<br>");
				logStr = logStr.replace("\r\n", "<br>");
				logStr = logStr.replace("\n", "<br>");

				sb.append(",logs:\"" + logStr + "\"");
				sb.append("}");
				sb.append("]");

			} else {
				sb.append("]");
			}
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
	 * 根据id更新文件数据
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateFileInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileContentLogic fileContentLogic = new T9FileContentLogic();
		String contentIdStr = request.getParameter("contentId");
		String type = request.getParameter("fileType");
		String sortIdStr = request.getParameter("sortId");

		String smsPerson = request.getParameter("smsPerson");
		String mobileSmsPerson = request.getParameter("mobileSmsPerson");
		String folderPath = request.getParameter("folderPath");
		if (folderPath == null) {
			folderPath = "";
		}
		if (mobileSmsPerson == null) {
			mobileSmsPerson = "";
		}
		int sortId = 0;
		int contentId = 0;
		if (sortIdStr != null) {
			sortId = Integer.parseInt(sortIdStr);
		}
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}
		if (smsPerson == null) {
			smsPerson = "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(new Date());

		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		int loginUserDeptId = loginUser.getDeptId();
		String loginUserRoleId = loginUser.getUserPriv();

		// 保存从文件柜、网络硬盘选择附件
		T9SelAttachUtil sel = new T9SelAttachUtil(request, "file_folder");
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
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9FileContent content = fileContentLogic.getFileContentInfoById(dbConn, contentId);
			String idStr = T9Utility.null2Empty(content.getAttachmentId());
			String nameStr = T9Utility.null2Empty(content.getAttachmentName());
			T9FileContent fileContent = (T9FileContent) T9FOM.build(request.getParameterMap());
			String attachmentName = T9Utility.null2Empty(fileContent.getAttachmentName()).trim();
			T9FileContent content2 = new T9FileContent();
			boolean newTypeFlag = false;
			String attIdString = "";
			String realPath = request.getRealPath("/");
			if (!T9Utility.isNullorEmpty(type) && !T9Utility.isNullorEmpty(attachmentName)) {
				String rand = fileContentLogic.createFile(type, attachmentName, realPath);
				if (!rand.equals("0")) {
					attIdString = currDate + "_" + String.valueOf(rand) + ",";
					attachmentName = attachmentName + "." + type + "*";
					newTypeFlag = true;
				}
			}
			if (newTypeFlag && fromFolderFlag) {
				content2.setAttachmentId(idStr.trim() + newAttchId.trim() + attIdString.trim());
				content2.setAttachmentName(nameStr.trim() + newAttchName.trim() + attachmentName.trim());
			} else if (newTypeFlag) {
				content2.setAttachmentId(idStr.trim() + attIdString.trim());
				content2.setAttachmentName(nameStr.trim() + attachmentName.trim());
			} else if (fromFolderFlag) {
				content2.setAttachmentId(idStr.trim() + newAttchId.trim());
				content2.setAttachmentName(nameStr.trim() + newAttchName.trim());
			} else {
				content2.setAttachmentId(idStr.trim());
				content2.setAttachmentName(nameStr.trim());
			}
			content2.setSeqId(contentId);
			content2.setSortId(content.getSortId());
			content2.setContentNo(fileContent.getContentNo());
			content2.setSubject(fileContent.getSubject());
			content2.setContent(fileContent.getContent());
			content2.setAttachmentDesc(fileContent.getAttachmentDesc());
			content2.setSendTime(T9Utility.parseTimeStamp());

			String createName = fileContentLogic.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
			String logStr = content.getLogs();
			if (logStr == null) {
				logStr = "";
			}
			String brStr = "";
			if (!"".equals(logStr)) {
				brStr = "\r\n";
			}
			Date date = new Date();
			String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
			logStr = logStr.trim();
			String logString = "";
			if (newTypeFlag || fromFolderFlag) {
				logString = logStr + brStr + timeStr + " " + createName + " " + "增加附件";
			} else {
				logString = logStr + brStr + timeStr + " " + createName + " " + "修改文件";
			}
			content2.setLogs(logString.trim());
			fileContentLogic.updataFileInfoByObj(dbConn, content2);

			// 短信提醒
			// T9SmsUtil sms=new T9SmsUtil();
			T9SmsBack sms = new T9SmsBack();
			String loginName = fileContentLogic.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
			String smsContent = loginName + " 在公共文件柜 " + folderPath + " 下更新文件:" + T9Utility.null2Empty(fileContent.getSubject());
			String remindUrl = "/core/funcs/filefolder/read.jsp?sortId=" + sortId + "&contentId=" + contentId + "&newFileFlag=1&openFlag=1";
			if ("allPrivPerson".equals(smsPerson)) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");
				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				// 获取该部门的Id串
				String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
				String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
				// 如有权限，获取该部门下的所有人员id串
				String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
				String rolePersonIdStr = logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					sms.setFromId(loginUserSeqId);
					sms.setToId(allpersonStr.trim());
					sms.setContent(smsContent);
					sms.setSendDate(T9Utility.parseTimeStamp());
					sms.setSmsType(T9LogConst.FILE_FOLDER);
					sms.setRemindUrl(remindUrl);
					T9SmsUtil.smsBack(dbConn, sms);
				}
			} else if (!"".equals(smsPerson)) {
				sms.setFromId(loginUserSeqId);
				sms.setToId(smsPerson);
				sms.setContent(smsContent);
				sms.setSendDate(T9Utility.parseTimeStamp());
				sms.setSmsType(T9LogConst.FILE_FOLDER);
				sms.setRemindUrl(remindUrl);
				T9SmsUtil.smsBack(dbConn, sms);
			}
			// 手机短信提醒mobileSmsPerson
			String mobileSmsContent = loginName + " 在公共文件柜 " + folderPath + " 下更新文件:" + T9Utility.null2Empty(fileContent.getSubject());
			T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
			if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
				T9FileSortLogic logic = new T9FileSortLogic();
				T9FileSort fileSort = logic.getFileSortInfoById(dbConn, String.valueOf(sortId));
				String deptIdStr = logic.getDeptIds(dbConn, fileSort, "USER_ID");
				String roleIdStr = logic.getRoleIds(dbConn, fileSort, "USER_ID");
				String personIdStr = logic.selectManagerIds(dbConn, fileSort, "USER_ID");

				if (!"".equals(personIdStr)) {
					personIdStr += ",";
				}
				// 获取该部门的Id串
				String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
				String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
				// 如有权限，获取该部门下的所有人员id串
				String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
				String rolePersonIdStr = logic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
				String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
				String allpersonStr = "";
				ArrayList al = new ArrayList();
				String[] arr = allPersonIdStr.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (al.contains(arr[i]) == false) {
						al.add(arr[i]);
						allpersonStr += arr[i] + ",";
					}
				}
				if (allpersonStr != null && !"".equals(allpersonStr)) {
					mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId, mobileSmsContent, null);
				}
			} else if (!"".equals(mobileSmsPerson.trim())) {
				mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId, mobileSmsContent, null);
			}
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
	 * 查询文件信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryFileContentInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		String subject = URLDecoder.decode(request.getParameter("subject"), "UTF-8");// 标题包含文字

		// subject=URLDecoder.decode(subject,"UTF-8");
		String contentNo = URLDecoder.decode(request.getParameter("contentNo"), "UTF-8");// 排序号
		String key1 = URLDecoder.decode(request.getParameter("key1"), "UTF-8");// 排序号		String key2 = URLDecoder.decode(request.getParameter("key2"), "UTF-8");// 排序号		String key3 = URLDecoder.decode(request.getParameter("key3"), "UTF-8");// 排序号
		String attachmentDesc = URLDecoder.decode(request.getParameter("attachmentDesc"), "UTF-8");// 附件说明包含文字
		String attachmentName = URLDecoder.decode(request.getParameter("attachmentName"), "UTF-8");// 附件文件名包含文字
		String attachmentData = URLDecoder.decode(request.getParameter("attachmentData"), "UTF-8");// 附件内容包含文字

		String sendTimeMin = URLDecoder.decode(request.getParameter("sendTimeMin"), "UTF-8");// 最小日期
		String sendTimeMax = URLDecoder.decode(request.getParameter("sendTimeMax"), "UTF-8");// 最大日期
		String pageNoStr = request.getParameter("currNo");
		String pageSizeStr = request.getParameter("pageSizeStr");

		if (T9Utility.isNullorEmpty(pageSizeStr)) {
			pageSizeStr = "20";
		}

		int currNo = 1;
		if (T9Utility.isNullorEmpty(pageNoStr)) {
			currNo = 1;
		} else {
			currNo = Integer.parseInt(pageNoStr);
		}

		int pageSize = 0;
		if (!T9Utility.isNullorEmpty(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}

		if (T9Utility.isNullorEmpty(seqId)) {
			seqId = "0";
		}

		T9FileContentLogic logic = new T9FileContentLogic();
		Map<String, String> map = new HashMap<String, String>();
		map.put("seqId", seqId);
		map.put("subject", subject);
		map.put("contentNo", contentNo);
		map.put("key1", key1);
		map.put("key2", key2);
		map.put("key3", key3);
		map.put("attachmentDesc", attachmentDesc);
		map.put("attachmentName", attachmentName);
		map.put("attachmentData", attachmentData);
		map.put("sendTimeMin", sendTimeMin);
		map.put("sendTimeMax", sendTimeMax);
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			boolean isHave = false;

			StringBuffer buffer = new StringBuffer("[");
			List<Map<String, String>> fileContentsList = logic.queryFileContentInfoById(dbConn, map, loginUser, currNo, pageSize);

			for (Map<String, String> fileContentsMap : fileContentsList) {
				buffer.append("{");

				buffer.append("contentId:\"" + fileContentsMap.get("contentId") + "\"");
				buffer.append(",sortId:\"" + fileContentsMap.get("sortId") + "\"");
				buffer.append(",subject:\"" + T9Utility.encodeSpecial(fileContentsMap.get("subject")) + "\"");
				buffer.append(",sendTime:\"" + fileContentsMap.get("sendTime") + "\"");
				buffer.append(",attachmentId:\"" + fileContentsMap.get("attachmentId") + "\"");
				buffer.append(",attachmentName:\"" + T9Utility.encodeSpecial(fileContentsMap.get("attachmentName")) + "\"");
				buffer.append(",attachmentDesc:\"" + T9Utility.encodeSpecial(fileContentsMap.get("attachmentDesc")) + "\"");

				buffer.append(",totalRecord:\"" + Integer.parseInt(fileContentsMap.get("maxRow")) + "\"");
				buffer.append(",pageNo:\"" + currNo + "\"");
				buffer.append(",pageSize:\"" + pageSize + "\"");

				buffer.append(",returnSortId:\"" + seqId + "\"");
				buffer.append(",returnSubject:\"" + T9Utility.encodeSpecial(subject) + "\"");
				buffer.append(",returnContentNo:\"" + T9Utility.encodeSpecial(contentNo) + "\"");
				buffer.append(",returnKey1:\"" + T9Utility.encodeSpecial(key1) + "\"");
				buffer.append(",returnKey2:\"" + T9Utility.encodeSpecial(key2) + "\"");
				buffer.append(",returnKey3:\"" + T9Utility.encodeSpecial(key3) + "\"");
				buffer.append(",returnSendTimeMin:\"" + sendTimeMin + "\"");
				buffer.append(",returnSendTimeMax:\"" + sendTimeMax + "\"");
				buffer.append(",returnAttachmentName:\"" + T9Utility.encodeSpecial(attachmentName) + "\"");
				buffer.append(",returnAttachmentData:\"" + T9Utility.encodeSpecial(attachmentData) + "\"");
				buffer.append(",returnAttachmentDesc:\"" + T9Utility.encodeSpecial(attachmentDesc) + "\"");

				buffer.append("},");
				isHave = true;
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");

			// System.out.println("sb.toString()>>>" + buffer.toString());
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, buffer.toString());

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 全局搜索文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getGlobalFileContents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqId = request.getParameter("seqId");
		String subject = URLDecoder.decode(request.getParameter("subject"), "UTF-8");// 标题包含文字
		String contentNo = URLDecoder.decode(request.getParameter("contentNo"), "UTF-8");// 排序号
		String key1 = URLDecoder.decode(request.getParameter("key1"), "UTF-8");// 排序号
		String key2 = URLDecoder.decode(request.getParameter("key2"), "UTF-8");// 排序号
		String key3 = URLDecoder.decode(request.getParameter("key3"), "UTF-8");// 排序号
		String attachmentDesc = URLDecoder.decode(request.getParameter("attachmentDesc"), "UTF-8");// 附件说明包含文字
		String attachmentName = URLDecoder.decode(request.getParameter("attachmentName"), "UTF-8");// 附件文件名包含文字
		String attachmentData = URLDecoder.decode(request.getParameter("attAchmentData"), "UTF-8");// 附件内容包含文字

		String sendTimeMin = URLDecoder.decode(request.getParameter("sendTimeMin"), "UTF-8");// 最小日期
		String sendTimeMax = URLDecoder.decode(request.getParameter("sendTimeMax"), "UTF-8");// 最大日期
		T9FileContentLogic logic = new T9FileContentLogic();

		T9FileSortLogic fileSortLogic = new T9FileSortLogic();

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("subject", subject);
		map.put("contentNo", contentNo);
		map.put("key1", key1);
		map.put("key2", key2);
		map.put("key3", key3);
		map.put("attachmentDesc", attachmentDesc);
		map.put("attachmentName", attachmentName);
		map.put("attachmentData", attachmentData);
		map.put("sendTimeMin", sendTimeMin);
		map.put("sendTimeMax", sendTimeMax);
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			boolean isHave = false;
			StringBuffer buffer = new StringBuffer("[");

			List<Map<Object, Object>> fileContentsList = logic.getGlobalFileContentsByList(dbConn, map, loginUser);

			if (fileContentsList != null && fileContentsList.size() > 0) {
				for (Map<Object, Object> fileContentsMap : fileContentsList) {

					int dbContentId = (Integer) fileContentsMap.get("dbContentId");
					int dbSortId = (Integer) fileContentsMap.get("dbSortId");
					String dbSubject = (String) fileContentsMap.get("dbSubject");
					String dbSendTime = (String) fileContentsMap.get("dbSendTime");
					String dbAttachmentId = (String) fileContentsMap.get("dbAttachmentId") == null ? "" : (String) fileContentsMap.get("dbAttachmentId");
					String dbAttachmentName = (String) fileContentsMap.get("dbAttachmentName") == null ? "" : (String) fileContentsMap.get("dbAttachmentName");
					String dbAttachmentDesc = (String) fileContentsMap.get("dbAttachmentDesc") == null ? "" : (String) fileContentsMap.get("dbAttachmentDesc");
					// String dbUserId = (String) fileContentsMap.get("dbUserId") == null
					// ? "" : (String) fileContentsMap.get("dbUserId");

					String treePath = (String) fileContentsMap.get("treePath");
					int managePriv = (Integer) fileContentsMap.get("managePriv");
					int downPriv = (Integer) fileContentsMap.get("downPriv");
					int newPriv = (Integer) fileContentsMap.get("newPriv");
					int fileSortCur = (Integer) fileContentsMap.get("fileSortCur");

					buffer.append("{");

					buffer.append("contentId:\"" + dbContentId + "\"");
					buffer.append(",sortId:\"" + dbSortId + "\"");
					buffer.append(",subject:\"" + T9Utility.encodeSpecial(dbSubject) + "\"");
					buffer.append(",sendTime:\"" + dbSendTime + "\"");
					buffer.append(",attachmentId:\"" + dbAttachmentId + "\"");
					buffer.append(",attachmentName:\"" + T9Utility.encodeSpecial(dbAttachmentName) + "\"");
					buffer.append(",attachmentDesc:\"" + T9Utility.encodeSpecial(dbAttachmentDesc) + "\"");
					buffer.append(",treePath:\"" + T9Utility.encodeSpecial(treePath) + "\"");

					buffer.append(",managePriv:\"" + managePriv + "\"");
					buffer.append(",downPriv:\"" + downPriv + "\"");
					buffer.append(",newPriv:\"" + newPriv + "\"");
					buffer.append(",fileSortCur:\"" + fileSortCur + "\"");

					buffer.append("},");
					isHave = true;
				}

				if (isHave) {
					buffer.deleteCharAt(buffer.length() - 1);
				}
				buffer.append("]");

			} else {
				buffer.append("]");
			}

			// System.out.println(buffer.toString());

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, buffer.toString());
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 编辑文件中的"新建附件"
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @return
	 * @throws Exception
	 */
	public String newFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String seqIdStr = request.getParameter("seqId");
		String contentIdStr = request.getParameter("contentId");
		String attachmentName = request.getParameter("newAttachmentName");
		String fileType = request.getParameter("newFileType");
		String subject = request.getParameter("newSubject");
		String contentNo = request.getParameter("newContentNo");
		String content = request.getParameter("newContent");
		String atttDesc = request.getParameter("newAtttDesc");

		int contentId = 0;
		// System.out.println(contentIdStr != null);
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}
		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		if (attachmentName==null) {
			attachmentName = "";
		}else {
			attachmentName = attachmentName.trim();
		}

		T9FileContentLogic log = new T9FileContentLogic();
		StringBuffer buffer = new StringBuffer();
		Map map = new HashMap();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(new Date());
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9FileContent fileContent = log.getFileContentInfoById(dbConn, contentId);
			String idsString = fileContent.getAttachmentId();
			String nameString = fileContent.getAttachmentName();
			String attachmentDesc = fileContent.getAttachmentDesc();
			// String subjectStr=fileContent.getSubject();
			String attachId = idsString == null ? "" : idsString;
			String attachName = nameString == null ? "" : nameString;

			String attachmentIds = attachId;
			String attachmentNames = attachName;
			String newAttachIdStr = "";
			String newAttachNameStr = "";
			String realPath = request.getRealPath("/");
			String rand = log.createFile(fileType, attachmentName, realPath);
			if (!rand.equals("0")) {
				// String randStr=String.valueOf(rand);
				attachmentIds = attachId + currDate + "_" + String.valueOf(rand) + ",";
				attachmentNames = attachName + attachmentName + "." + fileType + "*";

				newAttachIdStr = currDate + "_" + String.valueOf(rand);
				newAttachNameStr = attachmentName + "." + fileType;
			}

			fileContent.setSubject(subject);
			fileContent.setAttachmentId(attachmentIds);
			fileContent.setAttachmentName(attachmentNames);
			fileContent.setContentNo(contentNo);
			fileContent.setContent(content);
			fileContent.setAttachmentDesc(atttDesc);
			log.updateSingleObj(dbConn, fileContent);

			request.setAttribute("newAttachIdStr", newAttachIdStr);
			request.setAttribute("newAttachNameStr", newAttachNameStr);

		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/funcs/filefolder/edit.jsp?seqId=" + seqId + "&contentId=" + contentId;

	}

	/**
	 * 新建文件中的"新建附件"
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String createFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String actionFlag = "";
		String seqIdStr = request.getParameter("seqId");
		String contentIdStr = request.getParameter("contentId");
		String fileType = request.getParameter("newFileType");
		String attachmentName = request.getParameter("newAttachmentName");
		String subject = request.getParameter("newSubject");
		String contentNo = request.getParameter("newContentNo");
		String content = request.getParameter("newContent");
		String attDesc = request.getParameter("newAtttDesc");
		int seqId = 0;
		int contentId = 0;
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		T9FileContentLogic logic = new T9FileContentLogic();
		T9FileContent fileContent = new T9FileContent();
		SimpleDateFormat format = new SimpleDateFormat("yyMM");
		String currDate = format.format(new Date());
		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String attachmentIds = "";
			String attachmentNames = "";

			String newAttachIdStr = "";
			String newAttachNameStr = "";
			String realPath = request.getRealPath("/");
			if (!"".equals(fileType) && fileType != null && !"".equals(attachmentName) && attachmentName != null) {
				String rand = logic.createFile(fileType, attachmentName, realPath);
				if (!rand.equals("0")) {
					attachmentIds = currDate + "_" + String.valueOf(rand) + ",";
					attachmentNames = attachmentName + "." + fileType + "*";
					newAttachIdStr = currDate + "_" + String.valueOf(rand);
					newAttachNameStr = attachmentName + "." + fileType;
				}
				if (contentId != 0) {
					T9FileContent eitContent = logic.getFileContentInfoById(dbConn, contentId);
					String attIdString = eitContent.getAttachmentId();
					String attNameString = eitContent.getAttachmentName();
					eitContent.setAttachmentId(attIdString + attachmentIds);
					eitContent.setAttachmentName(attNameString + attachmentNames);
					eitContent.setSendTime(T9Utility.parseTimeStamp());
					eitContent.setSubject(subject);
					eitContent.setContent(content);
					eitContent.setAttachmentDesc(attDesc);
					eitContent.setContentNo(contentNo);
					logic.updateSingleObj(dbConn, eitContent);
				} else {
					fileContent.setSortId(seqId);
					fileContent.setAttachmentId(attachmentIds); // 1006_2a4c3044f00b76b721a3374c587c4146,
					fileContent.setAttachmentName(attachmentNames); // 新建gggg.doc*
					fileContent.setAttachmentDesc(attDesc);
					fileContent.setContent(content);
					fileContent.setContentNo(contentNo);
					fileContent.setSubject(subject);
					fileContent.setSendTime(T9Utility.parseTimeStamp());
					fileContent.setCreater(String.valueOf(loginUserSeqId));
					logic.saveSingleObj(dbConn, fileContent);
					// 得到最大id
					fileContent = logic.getMaxSeqId(dbConn);
					contentId = fileContent.getSeqId();
				}
			}
			actionFlag = "edit";
			request.setAttribute("newAttachIdStr", newAttachIdStr);
			request.setAttribute("newAttachNameStr", newAttachNameStr);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		String contextPath = request.getContextPath();
		// response.sendRedirect(contextPath
		// +"/core/funcs/filefolder/new/newFile.jsp?actionFlag=" + actionFlag +
		// "&contentId=" + contentId);
		// return "";

		return "/core/funcs/filefolder/new/newFile.jsp?actionFlag=" + actionFlag + "&contentId=" + contentId;
	}

	/**
	 * 删除所选择的seqId信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delCheckedFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqIdStr");

		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator; // T9SysProps.getAttachPath()得到
		String recyclePath = T9SysProps.getAttachPath() + separator + "recycle" + separator + "file_folder"; // 文件回收站的路径

		String recycle = T9SysProps.getString("$MYOA_IS_RECYCLE");
		if (recycle == null) {
			recycle = "";
		}

		// // 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();

		// 获取ip
		String ipStr = request.getRemoteAddr();

		if (seqIdStr.trim().endsWith(",")) {
			seqIdStr = seqIdStr.trim().substring(0, seqIdStr.trim().length() - 1);
		}

		T9FileContentLogic contentLogic = new T9FileContentLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();
			contentLogic.delFile(dbConn, seqIdStr, filePath, loginUserSeqId, ipStr, recycle, recyclePath);

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
	 * 浮动菜单文件删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String attachId = request.getParameter("attachId");
		String attachName = request.getParameter("attachName");
		String contentIdStr = request.getParameter("contentId");
		int contentId = 0;
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}

		if (attachId == null) {
			attachId = "";
		}
		if (attachName == null) {
			attachName = "";
		}

		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();

			T9FileContentLogic logic = new T9FileContentLogic();

			boolean updateFlag = logic.delFloatFile(dbConn, attachId, attachName, contentId);

			String isDel = "";
			if (updateFlag) {
				isDel = "isDel";

			}
			String data = "{updateFlag:\"" + isDel + "\"}";

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 *复制文件信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String copyFileByIds(HttpServletRequest request, HttpServletResponse response) {
		String seqIdStrs = request.getParameter("seqIdStrs");
		String action = request.getParameter("action");
		String folderSeqId = request.getParameter("folderSeqId");
		if (seqIdStrs.endsWith(",")) {
			seqIdStrs = seqIdStrs.substring(0, seqIdStrs.length() - 1);
		}
		try {
			request.getSession().setAttribute("folderContentId", seqIdStrs);
			request.getSession().setAttribute("folderSeqId", folderSeqId);
			request.getSession().setAttribute("folderActionStr", action);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "完成复制!");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 粘贴文件
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String pasteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStrs = (String) request.getSession().getAttribute("folderContentId");
		String action = (String) request.getSession().getAttribute("folderActionStr");
		String sortIdStr = (String) request.getParameter("sortId");
		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator; // T9SysProps.getAttachPath()得到
		T9FileContentLogic contentLogic = new T9FileContentLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();
			if ("copyFile".equals(action)) {
				contentLogic.copyFile(dbConn, seqIdStrs, sortIdStr, filePath);
			} else if ("cutFile".equals(action)) {
				contentLogic.cutFile(dbConn, seqIdStrs, sortIdStr, filePath);
			}
			request.getSession().setAttribute("folderContentId", null);
			request.getSession().setAttribute("folderSeqId", null);
			request.getSession().setAttribute("folderActionStr", null);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功粘贴数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 公共文件柜转存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String transferFolder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("checkId");
		String attachId = request.getParameter("attachId");
		String attachName = request.getParameter("attachName");
		String subject = request.getParameter("subject");
		String module = request.getParameter("module");

		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		if (subject == null) {
			subject = "";
		}
		if (module == null) {
			module = "";
		}

		String separator = File.separator;
		String filePath = T9SysProps.getAttachPath() + separator + module + separator; // T9SysProps.getAttachPath()得到
		String folderPath = T9SysProps.getAttachPath() + separator + "file_folder";
		T9FileContentLogic logic = new T9FileContentLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();
			boolean flag = logic.transferFolder(dbConn, seqId, attachId, attachName, subject, filePath, folderPath);
			if (flag) {
				request.setAttribute(T9ActionKeys.RET_MSRG, "文件转存成功！");
			} else {
				request.setAttribute(T9ActionKeys.RET_MSRG, "文件转存失败！");
			}

			String data = "{flag:\"" + flag + "\"}";
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
	 * 新建文件单个文件上传
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String newFileSingleUpload(HttpServletRequest request, HttpServletResponse response) throws Exception {

	  String contextPath = request.getContextPath();
    T9FileUploadForm fileForm = new T9FileUploadForm();
    String seqIdStr = request.getParameter("seqId"); // 文件夹的seqId
    String actionFlag = (String) request.getParameter("actionFlag");
    int contentId = 0;
    String contentStr = request.getParameter("contentId");
    if (!T9Utility.isNullorEmpty(contentStr)) {
      contentId = Integer.parseInt(contentStr);
    }
    int sortId = 0;
    if (seqIdStr != null) {
      sortId = Integer.parseInt(seqIdStr);
    }
    if (T9Utility.isNullorEmpty(actionFlag)) {
      actionFlag = "new";
    }
    try {
      fileForm.parseUploadRequest(request);
    } catch (Exception e) {
      response.sendRedirect(contextPath + "/core/funcs/filefolder/uploadFailse.jsp?actionFlag=" + actionFlag + "&seqId="+ sortId + "&contentId=" + contentId);
    }
		
		// 保存从文件柜、网络硬盘选择附件
		T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "file_folder");
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

		// String attachAction = fileForm.getParameter("attachAction");
		String retrunFlag = fileForm.getParameter("retrunFlag"); // 返回页面returnFolderFlag
		String returnFolderFlag = fileForm.getParameter("returnFolderFlag");

		String subject1 = fileForm.getParameter("subject1");
		String contentNo1 = fileForm.getParameter("contentNo1");
		String content1Str = fileForm.getParameter("content1");
		String attachmentName1 = fileForm.getParameter("attachmentName1");
		String fileType1 = fileForm.getParameter("fileType1");
		String attachmentDesc1 = fileForm.getParameter("attachmentDesc1");
		
    String folderPath = request.getParameter("folderPath");
    if (folderPath == null) {
      folderPath = "";
    }
    String smsPerson = fileForm.getParameter("smsPerson1");
    if (smsPerson == null) {
      smsPerson = "";
    }
    String mobileSmsPerson = fileForm.getParameter("mobileSmsPerson1");
    if (mobileSmsPerson == null) {
      mobileSmsPerson = "";
    }

		if (actionFlag == null || "".equals(actionFlag)) {
			actionFlag = "new";
		}

		if (returnFolderFlag == null) {
			returnFolderFlag = "";
		}
		if (retrunFlag == null) {
			retrunFlag = "";
		}

		if (subject1 == null) {
			subject1 = "";
		}
		if (contentNo1 == null) {
			contentNo1 = "";
		}
		if (content1Str == null) {
			content1Str = "";
		}
		if (attachmentName1 == null) {
			attachmentName1 = "";
		}
		if (fileType1 == null) {
			fileType1 = "";
		}
		if (attachmentDesc1 == null) {
			attachmentDesc1 = "";
		}

    T9FileContentLogic logic = new T9FileContentLogic();

		T9FileContent fileContent = new T9FileContent();

		if (!"".equals(returnFolderFlag.trim())) {
			retrunFlag = returnFolderFlag;

		}

		// // 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
    int loginUserDeptId = loginUser.getDeptId();
    String loginUserRoleId = loginUser.getUserPriv();

		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();

			// String
			// regex="/\ssrc=\\\"file:\/\/?[^"\']+\/'.preg_quote($NAME_ARRAY[$I]).'\\\"\s/i";

			// String content=contentString.replaceAll(regex, replacement);

			boolean flag = false;
			String attachmentId = "";
			String attachmentName = "";

			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			String separator = File.separator;
			String filePath = T9SysProps.getAttachPath() + separator + "file_folder" + separator + currDate; // T9SysProps.getAttachPath()得到

			Iterator<String> iKeys = fileForm.iterateFileFields();
			while (iKeys.hasNext()) {
				String fieldName = iKeys.next();
				String fileName = fileForm.getFileName(fieldName);
				String regName = fileName;

				if (T9Utility.isNullorEmpty(fileName)) {
					continue;
				}
				T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
				String rand = emul.getRandom();

				attachmentId += currDate + "_" + rand + ",";
				attachmentName += fileName + "*";

				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath + File.separator + fileName);

				// String contentFilePath= request.getContextPath() +
				// "/attach/file_folder/" + currDate +"/" + fileName;

				String regAttId = currDate + "_" + rand;
				// String regAttName=fileName;
				String contentFilePath = request.getContextPath() + "/t9/core/funcs/office/ntko/act/T9NtkoAct/upload.act?attachmentName=" + regName
						+ "&amp;attachmentId=" + regAttId + "&amp;module=file_folder&amp;directView=1";

				// String name= "J0309585.JPG";
				// String address = "/ssss/J0309585ssss.JPG";
				// String str =
				// "<p>aaadfasdfafdasfa<img alt=\"\" src=\"file://C:/Documents and Settings/wyw/桌面/J0309585.JPG\" />aasdfdsafda</p>";
				// String regex = "\\ssrc=\"file://?[^\"']+/" + name + "\"\\s";
				// str = str.replaceAll(regex, " src=\""+ address +"\" ");

				String isImage = "^.*?(\\.(png|gif|jpg|bmp|PNG|GIF|JPG|BMP))$";

				if (fileName.matches(isImage)) {
					String regex = "\\ssrc=\"file://?[^\"']+/" + regName + "\"\\s";
					content1Str = content1Str.replaceAll(regex, " src=\"" + contentFilePath + "\" ");

				}

				flag = true;
			}
			if (flag) {

				if ("edit".equals(actionFlag.trim())) {
					T9FileContent content = logic.getFileContentInfoById(dbConn, contentId);
					String attIdString = content.getAttachmentId();
					String attNameString = content.getAttachmentName();
					// String contentString=content.getContent();

					content.setAttachmentId(T9Utility.null2Empty(attIdString).trim() + attachmentId + T9Utility.null2Empty(newAttchId).trim());
					content.setAttachmentName(T9Utility.null2Empty(attNameString).trim() + T9Utility.null2Empty(attachmentName).trim() + T9Utility.null2Empty(newAttchName).trim());
					content.setSendTime(T9Utility.parseTimeStamp());
					content.setSubject(subject1);
					content.setContent(content1Str);
					content.setAttachmentDesc(attachmentDesc1);
					content.setContentNo(contentNo1);
					logic.updateSingleObj(dbConn, content);

				} else if ("new".equals(actionFlag.trim())) {
					fileContent.setSortId(sortId);
					fileContent.setSubject(subject1);
					fileContent.setContent(content1Str);
					fileContent.setSendTime(T9Utility.parseTimeStamp());
					fileContent.setAttachmentId(T9Utility.null2Empty(attachmentId).trim() + T9Utility.null2Empty(newAttchId).trim());
					fileContent.setAttachmentName(T9Utility.null2Empty(attachmentName).trim() + T9Utility.null2Empty(newAttchName).trim());
					fileContent.setAttachmentDesc(attachmentDesc1);
					fileContent.setContentNo(contentNo1);
					fileContent.setCreater(String.valueOf(loginUserSeqId));
					logic.saveSingleObj(dbConn, fileContent);

					// 得到最大id
					fileContent = logic.getMaxSeqId(dbConn);
					contentId = fileContent.getSeqId();

					request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
					request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
				}

			} else {
				request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
			}
			actionFlag = "edit";
			
      // 短信提醒
      // T9SmsUtil sms=new T9SmsUtil();
      T9SmsBack sms = new T9SmsBack();
      T9FileContentLogic contentLogic = new T9FileContentLogic();
      String loginName = contentLogic.getPersonNamesByIds(dbConn, String.valueOf(loginUserSeqId));
      String smsContent = loginName + " 在公共文件柜 " + folderPath + " 下建立新文件:" + subject1;
      String remindUrl = "/core/funcs/filefolder/read.jsp?sortId=" + sortId + "&contentId=" + contentId + "&newFileFlag=1&openFlag=1";
      if ("allPrivPerson".equals(smsPerson)) {
        T9FileSortLogic sortLogic = new T9FileSortLogic();
        T9FileSort fileSort = sortLogic.getFileSortInfoById(dbConn, String.valueOf(sortId));
        String deptIdStr = sortLogic.getDeptIds(dbConn, fileSort, "USER_ID");
        String roleIdStr = sortLogic.getRoleIds(dbConn, fileSort, "USER_ID");
        String personIdStr = sortLogic.selectManagerIds(dbConn, fileSort, "USER_ID");

        if (!T9Utility.isNullorEmpty(personIdStr)) {
          personIdStr += ",";
        }
        // 判断这个部门是否有权限
//        String deptPrivIdStrs = logic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
//        String rolePrivIdStrs = logic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
        // 如有权限，获取该部门下的所有人员id串
//        String deptPersonIdStr = logic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
//        String rolePrivIdStrs = logic.getRolePersonIdStr(loginUserDeptId, roleIdStr, dbConn);
        String deptPersonIdStr ="";
        String rolePersonIdStr ="";
        if (!T9Utility.isNullorEmpty(deptIdStr)) {
          deptPersonIdStr = sortLogic.getDeptPersonIds(deptIdStr, dbConn);
        }
        if (!T9Utility.isNullorEmpty(roleIdStr)) {
          rolePersonIdStr = sortLogic.getRolePersonIds(roleIdStr, dbConn);
        }
        String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;
        String allpersonStr = "";
        ArrayList al = new ArrayList();
        String[] arr = allPersonIdStr.split(",");
        for (int i = 0; i < arr.length; i++) {
          if (al.contains(arr[i]) == false) {
            al.add(arr[i]);
            allpersonStr += arr[i] + ",";
          }
        }
        if (allpersonStr != null && !"".equals(allpersonStr)) {
          sms.setFromId(loginUserSeqId);
          sms.setToId(allpersonStr.trim());
          sms.setContent(smsContent);
          sms.setSendDate(T9Utility.parseTimeStamp());
          sms.setSmsType(T9LogConst.FILE_FOLDER);
          sms.setRemindUrl(remindUrl);
          T9SmsUtil.smsBack(dbConn, sms);
        }
      } else if (!"".equals(smsPerson)) {
        sms.setFromId(loginUserSeqId);
        sms.setToId(smsPerson);
        sms.setContent(smsContent);
        sms.setSendDate(T9Utility.parseTimeStamp());
        sms.setSmsType(T9LogConst.FILE_FOLDER);
        sms.setRemindUrl(remindUrl);
        T9SmsUtil.smsBack(dbConn, sms);
      }
      // 手机短信提醒mobileSmsPerson
      String mobileSmsContent = loginName + " 在公共文件柜 " + folderPath + " 下建立新文件:" + subject1;
      T9MobileSms2Logic mobileSms = new T9MobileSms2Logic();
      if ("allPrivPerson".equals(mobileSmsPerson.trim())) {
        T9FileSortLogic sortLogic = new T9FileSortLogic();
        T9FileSort fileSort = sortLogic.getFileSortInfoById(dbConn, String.valueOf(sortId));
        String deptIdStr = sortLogic.getDeptIds(dbConn, fileSort, "USER_ID");
        String roleIdStr = sortLogic.getRoleIds(dbConn, fileSort, "USER_ID");
        String personIdStr = sortLogic.selectManagerIds(dbConn, fileSort, "USER_ID");
        if (!"".equals(personIdStr)) {
          personIdStr += ",";
        }
        // 判断这个部门是否有权限
        String deptPrivIdStrs = sortLogic.getPrivDeptIdStr(dbConn, loginUserDeptId, deptIdStr);
        String rolePrivIdStrs = sortLogic.getPrivRoleIdStr(dbConn, Integer.parseInt(loginUserRoleId), roleIdStr);
        // 如有权限，获取该部门下的所有人员id串
        String deptPersonIdStr = sortLogic.getDeptPersonIdStr(loginUserDeptId, deptPrivIdStrs, dbConn);
        String rolePersonIdStr = sortLogic.getRolePersonIdStr(Integer.parseInt(loginUserRoleId), rolePrivIdStrs, dbConn);
        String allPersonIdStr = personIdStr + deptPersonIdStr + rolePersonIdStr;

        String allpersonStr = "";
        ArrayList al = new ArrayList();
        String[] arr = allPersonIdStr.split(",");
        for (int i = 0; i < arr.length; i++) {
          if (al.contains(arr[i]) == false) {
            al.add(arr[i]);
            allpersonStr += arr[i] + ",";
          }
        }

        if (allpersonStr != null && !"".equals(allpersonStr)) {
          mobileSms.remindByMobileSms(dbConn, allpersonStr, loginUserSeqId, mobileSmsContent, null);
        }
      } else if (!"".equals(mobileSmsPerson.trim())) {
        mobileSms.remindByMobileSms(dbConn, mobileSmsPerson, loginUserSeqId, mobileSmsContent, null);
      }
      
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		// return "/core/inc/rtuploadfile.jsp";

		if ("returnNew".equals(retrunFlag.trim())) {

			response.sendRedirect(contextPath + "/core/funcs/filefolder/new/newFile.jsp?actionFlag=" + actionFlag + "&contentId=" + contentId + "&seqId="
					+ sortId);

		} else if ("returnEdit".equals(retrunFlag.trim())) {
			response.sendRedirect(contextPath + "/core/funcs/filefolder/edit.jsp?seqId=" + sortId + "&contentId=" + contentId);

		} else if ("returnFolder".equals(retrunFlag.trim())) {

			response.sendRedirect(contextPath + "/core/funcs/filefolder/folder.jsp?seqId=" + sortId);
		}
		return "";

	}

	/**
	 * 文件签阅
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String fileSign(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contentIdStr = request.getParameter("contentId");
		if (contentIdStr == null) {
			contentIdStr = "";
		}
		// 获取登录用户信息
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		int loginUserSeqId = loginUser.getSeqId();
		T9FileContentLogic logic = new T9FileContentLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();

			if (contentIdStr.endsWith(",")) {
				contentIdStr = contentIdStr.substring(0, contentIdStr.length() - 1);
			}
			logic.updateReader(dbConn, contentIdStr, loginUserSeqId);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "文件签阅成功");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 判断附件是否存在本地硬盘上
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String isHaveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String attIdStr = request.getParameter("attIdStr");
		String attNameStr = request.getParameter("attNameStr");

		if (attIdStr == null) {
			attIdStr = "";
		}
		if (attNameStr == null) {
			attNameStr = "";
		}

		T9FileContentLogic logic = new T9FileContentLogic();

		try {

			String data = logic.isHaveFileInDisk(attIdStr, attNameStr);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "文件签阅成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data);

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 判断新建文件名是否已存在
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String checkSubjectName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		String subjectName = request.getParameter("subject");
		String contentIdStr = request.getParameter("contentId");

		int seqId = 0; // 文件夹的id
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}
		int contentId = 0;
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}

		if (subjectName == null) {
			subjectName = "";
		}

		T9FileContentLogic logic = new T9FileContentLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();
			String data = logic.checkSubjectName(dbConn, seqId, contentId, subjectName);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 判断编辑文件名是否已存在
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String checkEditSubjectName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		String subjectName = request.getParameter("subject");
		String contentIdStr = request.getParameter("contentId");

		int seqId = 0; // 文件夹的id
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		int contentId = 0;
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}

		if (subjectName == null) {
			subjectName = "";
		}

		T9FileContentLogic logic = new T9FileContentLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requesttDbConn.getSysDbConn();
			String data = logic.checkEditSubjectName(dbConn, seqId, contentId, subjectName);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 显示在桌面上
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String selectFolderInfoToDisk(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String index = request.getParameter("index");
		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
		T9FileContentLogic logic = new T9FileContentLogic();
		T9FileSortLogic fileSortLogic = new T9FileSortLogic();
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			StringBuffer sb = new StringBuffer();
			boolean isHave = false;
			List<Map<Object, Object>> contentList = new ArrayList<Map<Object, Object>>();
			if (index != null && !"".equals(index)) {
				if ("1".equals(index)) {
					contentList = logic.selectNewContent(dbConn);
					if (contentList != null && contentList.size() != 0) {
						sb.append("[");
						for (Map<Object, Object> contnetMap : contentList) {
							T9FileSort fileSort = new T9FileSort();
							int contentId = (Integer) contnetMap.get("contentId");
							String subject = (String) contnetMap.get("subject");
							String readersIdStr = (String) contnetMap.get("readers");
							Date sendTime = (Date) contnetMap.get("sendTime");
							int sortId = (Integer) contnetMap.get("sortId");
							String sortName = (String) contnetMap.get("sortName");
							String userIdStr = (String) contnetMap.get("userId");
							String ownerIdStr = (String) contnetMap.get("owner");

							fileSort.setSeqId(sortId);
							fileSort.setUserId(userIdStr);
							fileSort.setOwner(ownerIdStr);
							String date = "";
							// date.substring(beginIndex, endIndex)
							if (sendTime != null) {
								date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sendTime);
							}
							boolean isReadFlag = logic.isReaders(readersIdStr, loginUser);
							if (isReadFlag) {
								continue;
							}
							boolean userPrivFlag = logic.getVisiPriv(dbConn, fileSort, loginUser);
							if (userPrivFlag) {
								subject = subject.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
								String fileSortNameStr = sortName;
								fileSortNameStr = fileSortNameStr.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
								sb.append("{");
								sb.append("contentId:\"" + contentId + "\"");
								sb.append(",subject:\"" + subject + "\"");
								sb.append(",sendTime:\"" + date + "\"");
								sb.append(",sortId:\"" + sortId + "\"");
								sb.append(",sortName:\"" + fileSortNameStr + "\"");
								sb.append("},");
								isHave = true;
							}
						}
						if (isHave) {
							sb.deleteCharAt(sb.length() - 1);
						}
						sb.append("]");
					} else {
						sb.append("[]");
					}
				}
				if ("2".equals(index)) {
					String[] condition = { " SORT_PARENT=" + 0 + " AND (SORT_TYPE !='4' or SORT_TYPE is null)  order by SORT_NO,SORT_NAME" };
					List<T9FileSort> list = fileSortLogic.getFileFilderInfo(dbConn, condition);
					if (list != null && list.size() != 0) {
						sb.append("[");
						for (T9FileSort fileSort : list) {
							boolean userPrivFlag = logic.getVisiPriv(dbConn, fileSort, loginUser);
							if (userPrivFlag) {
								String sortName = fileSort.getSortName() == null ? "" : fileSort.getSortName();
								String fileSortNameStr = sortName;
								fileSortNameStr = fileSortNameStr.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
								sb.append("{");
								sb.append("contentId:\"" + "" + "\"");
								sb.append(",subject:\"" + "" + "\"");
								sb.append(",sendTime:\"" + "" + "\"");
								sb.append(",sortId:\"" + fileSort.getSeqId() + "\"");
								sb.append(",sortName:\"" + fileSortNameStr + "\"");
								sb.append("},");
								isHave = true;
							}
						}
						if (isHave) {
							sb.deleteCharAt(sb.length() - 1);
						}
						sb.append("]");
					} else {
						sb.append("[]");
					}
				}
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
			request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 返回下载文件所需要的值
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String downFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String contentIdStr = request.getParameter("contentId");
		if (contentIdStr == null) {
			contentIdStr = "";
		}

		T9FileContentLogic logic = new T9FileContentLogic();

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			if (contentIdStr.endsWith(",")) {
				contentIdStr = contentIdStr.substring(0, contentIdStr.length() - 1);
			}

			String data = logic.getDownFileInfo(dbConn, contentIdStr);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 重命名附件
	 * 
	 * 
	 * 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String renameFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String contentIdStr = request.getParameter("contentId");
		int contentId = 0;
		if (contentIdStr != null) {
			contentId = Integer.parseInt(contentIdStr);
		}

		String attachName = request.getParameter("attachName");
		if (attachName == null) {
			attachName = "";
		}
		String attachId = request.getParameter("attachId");
		if (attachId == null) {
			attachId = "";
		}
		String module = request.getParameter("module");
		if (module == null) {
			module = "";
		}

		String renameStr = request.getParameter("newName");
		if (renameStr == null) {
			renameStr = "";
		}

		T9FileContentLogic logic = new T9FileContentLogic();
		String filePath = T9SysProps.getAttachPath() + "/" + module;

		int successFlag = 0;

		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			boolean success = logic.reNameOndisk(attachName, attachId, renameStr, filePath);

			// 对数据库操作
			if (success) {
				successFlag = 1;
				logic.updateAttachName(dbConn, contentId, attachName, attachId, renameStr, filePath);
			}

			String data = "{successFlag:\"" + successFlag + "\"}";

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功更新数据");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}

		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 批量下载
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String batchDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String contentIdStr = request.getParameter("contentIdStr");
		String seqIdStr = request.getParameter("sortId");

		T9Person loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

		if (contentIdStr == null) {
			contentIdStr = "";
		}
		int seqId = 0;
		if (seqIdStr != null) {
			seqId = Integer.parseInt(seqIdStr);
		}

		OutputStream ops = null;
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			// String attachmentName = request.getParameter("attachmentName");
			// String attachmentId = request.getParameter("attachmentId");
			String module = request.getParameter("module") == null ? "file_folder" : request.getParameter("module");
			String name = request.getParameter("name");
			if (name == null || "".equals(name)) {
				name = "附件打包下载";
			}
			String fileName = URLEncoder.encode(name + ".zip", "UTF-8");
			fileName = fileName.replaceAll("\\+", "%20");
			response.setHeader("Cache-control", "private");
			 response.setHeader("Cache-Control","maxage=3600");
	      response.setHeader("Pragma","public");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\"");
			ops = response.getOutputStream();
			T9NtkoLogic nl = new T9NtkoLogic();
			// Map<String, FileInputStream> map = nl.toZipInfoMap(attachmentName,
			// attachmentId, module);
			T9FileContentLogic logic = new T9FileContentLogic();
			Map<String, InputStream> map = logic.toZipInfoMapFile(dbConn, seqId, contentIdStr, module, loginUser);
			nl.zip(map, ops);
			ops.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			ops.close();
		}
		return null;
	}

	public static void main(String args[]) {
		String s = "ssss\n\rsssss";
		// System.out.print(s.replaceAll("[\n-\r]", "<br>"));
	}

}
