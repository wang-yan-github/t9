package t9.subsys.oa.meeting.act;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.meeting.logic.T9MeetingCommentLogic;

public class T9MeetingCommentAct {
	private T9MeetingCommentLogic logic = new T9MeetingCommentLogic();

	/**
	 * 新建会议纪要评论请信息--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addMeetingCommentInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		T9FileUploadForm fileForm = new T9FileUploadForm();
		fileForm.parseUploadRequest(request);
		
		String meetingIdStr = fileForm.getParameter("meetingId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(meetingIdStr)) {
			seqId = Integer.parseInt(meetingIdStr);
		}
		
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			// 保存从文件柜、网络硬盘选择附件
			T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, T9MeetingAct.attachmentFolder);
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
			String filePath = T9SysProps.getAttachPath() + separator + T9MeetingAct.attachmentFolder + separator + currDate;

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
				uploadFlag = true;

				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath +File.separator +fileName);
			}

			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("fromFolderFlag", fromFolderFlag);
			map.put("newAttchId", newAttchId);
			map.put("newAttchName", newAttchName);
			map.put("uploadFlag", uploadFlag);
			map.put("attachmentId", attachmentId);
			map.put("attachmentName", attachmentName);
			this.logic.setCommentValueLogic(dbConn, fileForm, person, map);

			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		String contexPath = request.getContextPath();
		response.sendRedirect(contexPath + "/subsys/oa/meeting/apply/review.jsp?seqId=" + seqId);
		return null;
	}
	
	
	/**
	 * 获取最新评论--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getNewCommentInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String meetingIdStr = request.getParameter("seqId");
		int meetingId = 0;
		if(!T9Utility.isNullorEmpty(meetingIdStr)){
			meetingId = Integer.parseInt(meetingIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			 T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
			String data =  this.logic.getCommentInfoLogic(dbConn,person,meetingId);
		
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
			request.setAttribute(T9ActionKeys.RET_DATA, data);
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	/**
	 * 删除会议信息--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String delCommentInfo(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String seqIdStr = request.getParameter("commentId");
		int seqId=0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId=Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String filePath = T9SysProps.getAttachPath() + File.separator + T9MeetingAct.attachmentFolder + File.separator;
			this.logic.delCommentLogic(dbConn, seqId,filePath);
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
	
	
	
	
	
	

}
