package t9.subsys.oa.meeting.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.meeting.data.T9Meeting;
import t9.subsys.oa.meeting.data.T9MeetingRoom;

public class T9MeetingLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.meeting.logic.T9MeetingLogic.java");

	/**
	 * 新建会议申请信息
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public String setMeetingValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {

		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String newAttchId = (String) map.get("newAttchId");
			String newAttchName = (String) map.get("newAttchName");
			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String attachmentId = (String) map.get("attachmentId");
			String attachmentName = (String) map.get("attachmentName");
			boolean pageAttIdFlag = (Boolean) map.get("pageAttIdFlag");
			String pageAttachmentId = (String) map.get("pageAttachmentId");
			String pageattachmentName = (String) map.get("pageattachmentName");
			T9Meeting meeting = new T9Meeting();
			if (fromFolderFlag && uploadFlag && pageAttIdFlag) {
				meeting.setAttachmentId(newAttchId.trim() + attachmentId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + attachmentName.trim() + pageattachmentName.trim());
			} else if (fromFolderFlag && uploadFlag) {
				meeting.setAttachmentId(newAttchId.trim() + attachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + attachmentName.trim());
			} else if (fromFolderFlag && pageAttIdFlag) {
				meeting.setAttachmentId(newAttchId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + pageattachmentName.trim());
			} else if (uploadFlag && pageAttIdFlag) {
				meeting.setAttachmentId(attachmentId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(attachmentName.trim() + pageattachmentName.trim());
			} else if (fromFolderFlag) {
				meeting.setAttachmentId(newAttchId.trim());
				meeting.setAttachmentName(newAttchName.trim());
			} else if (uploadFlag) {
				meeting.setAttachmentId(attachmentId.trim());
				meeting.setAttachmentName(attachmentName.trim());
			} else if (pageAttIdFlag) {
				meeting.setAttachmentId(pageAttachmentId.trim());
				meeting.setAttachmentName(pageattachmentName.trim());
			}
			int mRoom = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("mRoom"))) {
				mRoom = Integer.parseInt(fileForm.getParameter("mRoom"));
			}
			int resendLong = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("resendLong"))) {
				resendLong = Integer.parseInt(fileForm.getParameter("resendLong"));
			}
			int resendSeveral = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("resendSeveral"))) {
				resendSeveral = Integer.parseInt(fileForm.getParameter("resendSeveral"));
			}
			String smsRemind = fileForm.getParameter("smsRemind");
			if (T9Utility.isNullorEmpty(smsRemind)) {
				smsRemind = "0";
			}
			String sms2Remind = fileForm.getParameter("sms2Remind");
			if (T9Utility.isNullorEmpty(sms2Remind)) {
				sms2Remind = "0";
			}
			String calendar = fileForm.getParameter("calendar");
			if (T9Utility.isNullorEmpty(calendar)) {
				calendar = "0";
			}
			if ("on".equals(calendar)) {
				calendar = "1";
			}
			String recorder = fileForm.getParameter("recorder");
			if (T9Utility.isNullorEmpty(recorder)) {
				recorder = "";
			}
			String mStatus = fileForm.getParameter("mStatus");
			if (T9Utility.isNullorEmpty(mStatus)) {
				mStatus = "0";
			}
			String cycle = fileForm.getParameter("cycle");
			if (T9Utility.isNullorEmpty(cycle)) {
				cycle = "0";
			}
			String smsReminde1 = fileForm.getParameter("smsReminde1");
			String smsReminde2 = fileForm.getParameter("smsReminde2");
			if (T9Utility.isNullorEmpty(smsReminde1)) {
				smsReminde1 = "0";
			}
			if (T9Utility.isNullorEmpty(smsReminde2)) {
				smsReminde2 = "0";
			}
			String mName = fileForm.getParameter("mName");
			String mTopic = fileForm.getParameter("mTopic");
			String mDesc = fileForm.getParameter("mDesc");
			String mAttendee = fileForm.getParameter("mAttendee");
			String mStart = fileForm.getParameter("mStart");
			String mEnd = fileForm.getParameter("mEnd");
			String mManager = fileForm.getParameter("mManager");
			String mAttendeeOut = fileForm.getParameter("mAttendeeOut");
			String toId = fileForm.getParameter("toId");
			String privId = fileForm.getParameter("privId");
			String secretToId = fileForm.getParameter("secretToId");
			String equipmentIdStr = fileForm.getParameter("checkEquipmentes");

			meeting.setMName(mName);
			meeting.setMTopic(mTopic);
			meeting.setMDesc(mDesc);
			meeting.setMProposer(String.valueOf(person.getSeqId()));
			meeting.setMRequestTime(T9Utility.parseTimeStamp());
			meeting.setMAttendee(mAttendee);
			meeting.setMStart(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", mStart));
			meeting.setMEnd((T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", mEnd)));
			meeting.setMRoom(mRoom);
			meeting.setMManager(mManager);
			meeting.setMAttendeeOut(mAttendeeOut);
			meeting.setSmsRemind(smsRemind);
			meeting.setSms2Remind(sms2Remind);
			meeting.setToId(toId);
			meeting.setPrivId(privId);
			meeting.setSecretToId(secretToId);
			meeting.setResendLong(resendLong);
			meeting.setResendSeveral(resendSeveral);
			meeting.setEquipmentIdStr(equipmentIdStr);
			meeting.setCalendar(calendar);
			meeting.setRecorder(recorder);
			meeting.setMStatus(mStatus);
			meeting.setCycle(cycle);
			this.addMeetingInfo(dbConn, meeting);

			int maxSeqId = this.getMaxMeetingId(dbConn);
			String mManagerStr = mManager;
			String userName = person.getUserName();
			
			Map dataMap = new HashMap();
			dataMap.put("KEY", maxSeqId + "");
      dataMap.put("FIELD", "M_ID");
      dataMap.put("M_ATTENDEE_OUT", mAttendeeOut);
      dataMap.put("M_ATTENDEE", mAttendee);
      dataMap.put("M_START", mStart );
      dataMap.put("M_END", mEnd );
      dataMap.put("M_NAME", mName);
      dataMap.put("M_TOPIC", mTopic);
      dataMap.put("M_DESC", mDesc);
      dataMap.put("USER_ID", person.getSeqId() + "");

      dataMap.put("ATTACHMENT_ID", pageAttachmentId);
      dataMap.put("ATTACHMENT_NAME", pageattachmentName);
      dataMap.put("MODULE_SRC", "meeting");
      dataMap.put("MODULE_DESC", "workflow");
      
      T9FlowHookUtility ut = new T9FlowHookUtility();
      String url = ut.runHook(dbConn, person, dataMap, "meeting_apply");
      if (!"".equals(url)) {
        return url;
      }
			String content = userName + " 向您提交会议申请，请批示！";
			int fromId = person.getSeqId();
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde1)) {
				String remindUrl = "/subsys/oa/meeting/query/meetingdetail.jsp?seqId=" + maxSeqId + "&openFlag=1&openWidth=860&openHeight=650";
				this.doSmsBack2(dbConn, content, fromId, mManagerStr, "8", remindUrl, T9Utility.parseTimeStamp());
			}
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde2)) {
				T9MobileSms2Logic sbl = new T9MobileSms2Logic();
				sbl.remindByMobileSms(dbConn, mManagerStr, fromId, content, T9Utility.parseTimeStamp());
			}

		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * 获取最大的SeqId值
	 * 
	 * @param dbConn
	 * @return
	 */
	public int getMaxMeetingId(Connection dbConn) {
		String sql = "select SEQ_ID from MEETING where SEQ_ID=(select MAX(SEQ_ID) from MEETING )";
		int seqId = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				seqId = rs.getInt("SEQ_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return seqId;
	}

	/**
	 * 添加信息
	 * 
	 * @param dbConn
	 * @param rmsFile
	 * @throws Exception
	 */
	public void addMeetingInfo(Connection dbConn, T9Meeting meeting) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.saveSingle(dbConn, meeting);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 会议查询--cc
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param mName
	 * @param mProposer
	 * @param beginDate
	 * @param endDate
	 * @param mRoom
	 * @param mStatus
	 * @return
	 * @throws Exception
	 */
	public String getMeetingSearchJson(Connection dbConn, Map request, T9Person person, String mName, String mProposer, String beginDate,
			String endDate, String mRoom, String mStatus) throws Exception {
		String sql = "";
		if (person.isAdminRole()) {
			sql = "select " +
					"" + "SEQ_ID" 
					+ ", M_NAME" 
					+ ", M_PROPOSER"
					+ ", M_ATTENDEE" 
					+ ", M_START" 
					+ ", M_STATUS" 
					+ ", M_ATTENDEE_OUT"
					+ ", M_ROOM"
					 + " , RECORDER"
			      + " , M_MANAGER "
					+ " from MEETING where 1=1 ";
		} else {
			sql = "select " + "SEQ_ID" 
			+ ", M_NAME" 
			+ ", M_PROPOSER"
			+ ", M_ATTENDEE"
			+ ", M_START" 
			+ ", M_STATUS"
			+ ", M_ATTENDEE_OUT" 
			+ ", M_ROOM"
			+ " , RECORDER"
			+ " , M_MANAGER "
					+ " from MEETING where (TO_ID='ALL_DEPT' or TO_ID='0' or " + T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "TO_ID") + " or "
					+ T9DBUtility.findInSet(person.getUserPriv(), "PRIV_ID") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "SECRET_TO_ID") + " or "
					+ T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "M_ATTENDEE")
					+ " or M_PROPOSER='" + String.valueOf(person.getSeqId())
					+ "' or M_MANAGER='" + String.valueOf(person.getSeqId()) + "')";
		}
		if (!T9Utility.isNullorEmpty(mName)) {
			sql = sql + " and M_NAME like '%" + mName + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(mProposer)) {
			sql = sql + " and M_PROPOSER like '%" + mProposer + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(beginDate)) {
			sql = sql + " and " + T9DBUtility.getDateFilter("M_REQUEST_TIME", beginDate, ">=");
		}
		if (!T9Utility.isNullorEmpty(endDate)) {
			sql = sql + " and " + T9DBUtility.getDateFilter("M_REQUEST_TIME", endDate, ">=");
		}
		if (!T9Utility.isNullorEmpty(mRoom)) {
			sql = sql + " and M_ROOM like '%" + mRoom + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(mStatus)) {
			sql = sql + " and M_STATUS like '%" + mStatus + "%'" + T9DBUtility.escapeLike();
		}
		sql = sql + " order by M_START desc, M_ROOM desc";

		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);

		return pageDataList.toJson();
	}

	/**
	 * 会议纪要列表
	 * 
	 * @param dbConn
	 * @param request
	 * @param mName
	 * @param mProposer
	 * @param beginDate
	 * @param endDate
	 * @param keyWord1
	 * @param keyWord2
	 * @param keyWord3
	 * @param mRoom
	 * @return
	 * @throws Exception
	 */
	public String getMeetingSummarySearchJson(Connection dbConn, Map request, String mName, String mProposer, String beginDate, String endDate,
			String keyWord1, String keyWord2, String keyWord3, String mRoom) throws Exception {
		String sql = "select " + "SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_END" + " from MEETING where 1=1 ";

		if (!T9Utility.isNullorEmpty(mName)) {
			sql = sql + " and M_NAME like '%" + mName + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(mProposer)) {
			sql = sql + " and M_PROPOSER like '%" + mProposer + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(beginDate)) {
			sql = sql + " and " + T9DBUtility.getDateFilter("M_START", beginDate, ">=");
		}
		if (!T9Utility.isNullorEmpty(endDate)) {
			sql = sql + " and " + T9DBUtility.getDateFilter("M_START", endDate, ">=");
		}
		if (!T9Utility.isNullorEmpty(mRoom)) {
			sql = sql + " and M_ROOM like '%" + mRoom + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(keyWord1)) {
			sql = sql + " and SUMMARY like '%" + keyWord1 + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(keyWord2)) {
			sql = sql + " and SUMMARY like '%" + keyWord2 + "%'" + T9DBUtility.escapeLike();
		}
		if (!T9Utility.isNullorEmpty(keyWord3)) {
			sql = sql + " and SUMMARY like '%" + keyWord3 + "%'" + T9DBUtility.escapeLike();
		}
		sql = sql + " order by M_START desc";

		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);

		return pageDataList.toJson();
	}

	/**
	 * 取得用户名称--cc
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */

	public String getUserNameLogic(Connection conn, int userId) throws Exception {
		String result = "";
		String sql = " select USER_NAME from PERSON where SEQ_ID = " + userId;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result = toId;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	/**
	 * 获取会议室名称--cc
	 * 
	 * 
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getMeetingRoomNameLogic(Connection conn, int seqId) throws Exception {
		String result = "";
		String sql = " select MR_NAME from MEETING_ROOM where SEQ_ID = " + seqId;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result = toId;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	/**
	 * 获取会议室详细信息--cc
	 * 
	 * @param conn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9Meeting getMeetingRoomDetail(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9Meeting) orm.loadObjSingle(conn, T9Meeting.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	/**
	 * 会议管理通用列表--cc
	 * 
	 * @param dbConn
	 * @param request
	 * @param mStatus
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMeetingListJson(Connection dbConn, Map request, String mStatus, T9Person person) throws Exception {
		String sql = "";
		if (person.isAdminRole()) {
			if ("0".equals(mStatus)) {
				sql = "select " 
				  + "  SEQ_ID"
				  + ", M_NAME" 
				  + ", M_PROPOSER"
				  + ", M_START"
				  + ", M_ROOM" 
				  + ", M_STATUS"
				  + ",1,1 "
				  + "  from MEETING where M_STATUS='"
						+ mStatus + "' and not CYCLE = '1'";
			} else {
				sql = "select " 
				  + "  SEQ_ID"
				  + ", M_NAME"
				  + ", M_PROPOSER" 
				  + ", M_START" 
				  + ", M_ROOM" 
				  + ", M_STATUS" 
				  + ",1,1 "
				  + " from MEETING where M_STATUS='"
						+ mStatus + "'";
			}

		} else {
			if ("0".equals(mStatus)) {
				sql = "select " 
				  + "  SEQ_ID" 
				  + ", M_NAME"
				  + ", M_PROPOSER" 
				  + ", M_START" 
				  + ", M_ROOM" 
				  + ", M_STATUS" 
				  + ",1,1 "
				  + " from MEETING where M_STATUS='"
						+ mStatus + "' and not CYCLE = '1' and M_MANAGER = '" + String.valueOf(person.getSeqId()) + "'";
			} else {
				sql = "select "
				  + "  SEQ_ID"
				  + ", M_NAME"
				  + ", M_PROPOSER" 
				  + ", M_START" 
				  + ", M_ROOM"
				  + ", M_STATUS" 
				  + ",1,1 "
				  + " from MEETING where M_STATUS='"
						+ mStatus + "' and M_MANAGER = '" + String.valueOf(person.getSeqId()) + "'";
			}
		}
		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		T9FlowHookUtility fu = new T9FlowHookUtility();
    T9FlowRunUtility ru = new T9FlowRunUtility();
		for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
      T9DbRecord r = pageDataList.getRecord(i);
      int seqId = T9Utility.cast2Long(r.getValueByName("seqId")).intValue();
      int runId = fu.isRunHook(dbConn, "M_ID", seqId + "");
      int flowId = 0;
      if (runId != 0) {
        flowId = ru.getFlowId(dbConn, runId);
      }
      r.addField("flowId", flowId);
      r.addField("runId", runId);
    }
		return pageDataList.toJson();
	}

	/**
	 * 会议申请通用列表--wyw
	 * 
	 * @param dbConn
	 * @param request
	 * @param mStatus
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMeetingListJson2(Connection dbConn, Map request, String mStatus, T9Person person) throws Exception {
		String sql = "select " + " MEETING.SEQ_ID" + ", M_NAME"
				// + ", M_TOPIC"
				+ ", M_PROPOSER"
				// + ", M_REQUEST_TIME"
				+ ", M_ATTENDEE" + ", M_START" + ", M_END" + ", M_ROOM" + ", M_STATUS "
				// + ", M_MANAGER"
				+ ", M_ATTENDEE_OUT , 1 , 1 " + " from MEETING, PERSON where M_STATUS='" + mStatus + "' and M_PROPOSER = '" + String.valueOf(person.getSeqId())
				+ "' and PERSON.SEQ_ID=" + person.getSeqId() + " order by M_STATUS";
		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9FlowHookUtility fu = new T9FlowHookUtility();
    T9FlowRunUtility ru = new T9FlowRunUtility();
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		for (int i = 0 ;i < pageDataList.getRecordCnt() ; i++) {
      T9DbRecord r = pageDataList.getRecord(i);
      int seqId = T9Utility.cast2Long(r.getValueByName("seqId")).intValue();
      int runId = fu.isRunHook(dbConn, "M_ID", seqId + "");
      int flowId = 0;
      if (runId != 0) {
        flowId = ru.getFlowId(dbConn, runId);
      }
      r.addField("flowId", flowId);
      r.addField("runId", runId);
    }
		return pageDataList.toJson();
	}

	/**
	 * 待批周期性列表--cc
	 * 
	 * @param dbConn
	 * @param request
	 * @param mStatus
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMeetingCycleListJson(Connection dbConn, Map request, T9Person person) throws Exception {
		String sql = "";
		if (person.isAdminRole()) {
			sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_ROOM" + ", CYCLE_NO" + ", M_STATUS"
					+ " from MEETING where SEQ_ID IN (select min(SEQ_ID) from MEETING where M_STATUS='0' and CYCLE='1' group by CYCLE_NO) ";
		} else {
			sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_ROOM" + ", CYCLE_NO" + ", M_STATUS"
					+ " from MEETING where SEQ_ID IN (select min(SEQ_ID) from MEETING where M_STATUS='0' and M_MANAGER = " + person.getSeqId()
					+ " and CYCLE='1' group by CYCLE_NO) ";
		}

		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	public String getMStartLogic(Connection conn, String cyleNo) throws Exception {
		String result = "";
		String sql = " select M_START from MEETING where CYCLE_NO = " + cyleNo;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result = toId;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	public String getMeetingManageCycleSeqIdStr(Connection dbConn, Map request, HttpServletRequest fileForm, String cycleNo, T9Person person,
			String flag) throws Exception, Exception {
		String result = "";
		String sql = "";
		if (person.isAdminRole()) {
			sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_ROOM" + ", CYCLE_NO" + ", M_STATUS"
					+ " from MEETING where M_STATUS='0' and CYCLE='1' and CYCLE_NO = '" + cycleNo + "'";
		} else {
			sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_ROOM" + ", CYCLE_NO" + ", M_STATUS"
					+ " from MEETING where M_STATUS='0' and CYCLE='1' and CYCLE_NO = '" + cycleNo + "' and M_MANAGER = '" + String.valueOf(person.getSeqId())
					+ "'";
		}
		Statement stm = null;
		ResultSet rs = null;
		try {
			stm = dbConn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String seqIdStr = String.valueOf(rs.getInt("SEQ_ID"));
				String mStart = String.valueOf(rs.getDate("M_START"));
				int curDay = this.getDateWeek(mStart);
				if (!"0".equals(flag)) {
					if (curDay == 1 && T9Utility.isNullorEmpty(fileForm.getParameter("W11"))) {
						continue;
					}
					if (curDay == 2 && T9Utility.isNullorEmpty(fileForm.getParameter("W12"))) {
						continue;
					}
					if (curDay == 3 && T9Utility.isNullorEmpty(fileForm.getParameter("W13"))) {
						continue;
					}
					if (curDay == 4 && T9Utility.isNullorEmpty(fileForm.getParameter("W14"))) {
						continue;
					}
					if (curDay == 5 && T9Utility.isNullorEmpty(fileForm.getParameter("W15"))) {
						continue;
					}
					if (curDay == 6 && T9Utility.isNullorEmpty(fileForm.getParameter("W16"))) {
						continue;
					}
					if (curDay == 7 && T9Utility.isNullorEmpty(fileForm.getParameter("W17"))) {
						continue;
					}
				}
				if (!"".equals(result)) {
					result += ",";
				}
				result += seqIdStr;
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stm, rs, null);
		}
		return result;
	}

	public String getMeetingManageCycleList(Connection dbConn, Map request, HttpServletRequest fileForm, String cycleNo, T9Person person, String flag)
			throws Exception {
		String idStr = getMeetingManageCycleSeqIdStr(dbConn, request, fileForm, cycleNo, person, flag);
		String sql = "";
		if (T9Utility.isNullorEmpty(idStr)) {
			idStr = "-1";
		}
		sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_PROPOSER" + ", M_START" + ", M_ROOM" + ", CYCLE_NO" + ", M_STATUS"
				+ " from MEETING where SEQ_ID IN (" + idStr + ")";
		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	/**
	 * 删除单个会议记录--cc
	 * 
	 * @param conn
	 * @param seqId
	 * @throws Exception
	 */
	public void deleteSingle(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.deleteSingle(conn, T9Meeting.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} finally {
		}
	}

	/**
	 * 删除会议记录（含附件）--wyw
	 * 
	 * @param dbConn
	 * @param seqId
	 * @throws Exception
	 */
	public void delMeetingLogic(Connection dbConn, int seqId, String filePath) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			StringBuffer attIdBuffer = new StringBuffer();
			StringBuffer attNameBuffer = new StringBuffer();
			T9Meeting meeting = (T9Meeting) orm.loadObjSingle(dbConn, T9Meeting.class, seqId);
			String dbAttachmentId = "";
			String dbAttachmentName = "";
			String dbAttachmentId1 = "";
			String dbAttachmentName1 = "";
			if (meeting != null) {
				dbAttachmentId = T9Utility.null2Empty(meeting.getAttachmentId());
				dbAttachmentName = T9Utility.null2Empty(meeting.getAttachmentName());

				// 会议纪要附件
				dbAttachmentId1 = T9Utility.null2Empty(meeting.getAttachmentId1());
				dbAttachmentName1 = T9Utility.null2Empty(meeting.getAttachmentName1());

				attIdBuffer.append(dbAttachmentId.trim() + dbAttachmentId1.trim());
				attNameBuffer.append(dbAttachmentName.trim() + dbAttachmentName1.trim());
			}
			String[] attIdArray = {};
			String[] attNameArray = {};
			if (!T9Utility.isNullorEmpty(attIdBuffer.toString()) && !T9Utility.isNullorEmpty(attNameBuffer.toString()) && attIdBuffer.length() > 0) {
				attIdArray = attIdBuffer.toString().trim().split(",");
				attNameArray = attNameBuffer.toString().trim().split("\\*");
			}

			if (attIdArray != null && attIdArray.length > 0) {
				for (int i = 0; i < attIdArray.length; i++) {
					Map<String, String> map = this.getFileName(attIdArray[i], attNameArray[i]);
					if (map.size() != 0) {
						Set<String> set = map.keySet();
						// 遍历Set集合
						for (String keySet : set) {
							String key = keySet;
							String keyValue = map.get(keySet);
							String attaIdStr = this.getAttaId(keySet);
							String fileNameValue = attaIdStr + "_" + keyValue;
							String fileFolder = this.getFilePathFolder(key);
							String oldFileNameValue = attaIdStr + "." + keyValue;
							File file = new File(filePath + File.separator + fileFolder + File.separator + fileNameValue);
							File oldFile = new File(filePath + File.separator + fileFolder + File.separator + oldFileNameValue);
							if (file.exists()) {
								T9FileUtility.deleteAll(file.getAbsoluteFile());
							} else if (oldFile.exists()) {
								T9FileUtility.deleteAll(oldFile.getAbsoluteFile());
							}
						}
					}
				}
			}
			// 删除数据库信息
			orm.deleteSingle(dbConn, meeting);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 拼接附件Id与附件名--wyw
	 * 
	 * @param attachmentId
	 * @param attachmentName
	 * @return
	 */
	public Map<String, String> getFileName(String attachmentId, String attachmentName) {
		Map<String, String> map = new HashMap<String, String>();
		if (attachmentId == null || attachmentName == null) {
			return map;
		}
		if (!"".equals(attachmentId.trim()) && !"".equals(attachmentName.trim())) {
			String attachmentIds[] = attachmentId.split(",");
			String attachmentNames[] = attachmentName.split("\\*");
			if (attachmentIds.length != 0 && attachmentNames.length != 0) {
				for (int i = 0; i < attachmentIds.length; i++) {
					map.put(attachmentIds[i], attachmentNames[i]);
				}
			}
		}
		return map;
	}

	/**
	 * 得到附件的Id 兼老数据 --wyw
	 * 
	 * @param keyId
	 * @return
	 */
	public String getAttaId(String keyId) {
		String attaId = "";
		if (keyId != null && !"".equals(keyId)) {
			if (keyId.indexOf('_') != -1) {
				String[] ids = keyId.split("_");
				if (ids.length > 0) {
					attaId = ids[1];
				}

			} else {
				attaId = keyId;
			}
		}
		return attaId;
	}

	/**
	 * 得到该文件的文件夹名--wyw
	 * 
	 * @param key
	 * @return
	 */
	public String getFilePathFolder(String key) {
		String folder = "";
		if (key != null && !"".equals(key)) {
			if (key.indexOf('_') != -1) {
				String[] str = key.split("_");
				for (int i = 0; i < str.length; i++) {
					folder = str[0];
				}
			} else {
				folder = "all";
			}
		}
		return folder;
	}

	/**
	 * 修改 m_status --cc
	 * 
	 * @param dbConn
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static void updateStatus(Connection dbConn, int seqId, String mStatus) throws Exception {
		String sql = "update MEETING set M_STATUS = ? where SEQ_ID = ?";
		PreparedStatement ps = null;
		try {
			ps = dbConn.prepareStatement(sql);
			ps.setString(1, mStatus);
			ps.setInt(2, seqId);
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, null, log);
		}
	}

	/**
	 * 短信提醒 --cc
	 * 
	 * @param conn
	 * @param content
	 * @param fromId
	 * @param toId
	 * @param type
	 * @param remindUrl
	 * @throws Exception
	 */
	public static void doSmsBack(Connection conn, String content, int fromId, String toId, String type, String remindUrl) throws Exception {
		T9SmsBack sb = new T9SmsBack();
		sb.setContent(content);
		sb.setFromId(fromId);
		sb.setToId(toId);
		sb.setSmsType(type);
		sb.setRemindUrl(remindUrl);
		T9SmsUtil.smsBack(conn, sb);
	}

	/**
	 * 短信提醒(带时间)--cc
	 * 
	 * @param conn
	 * @param content
	 * @param fromId
	 * @param toId
	 * @param type
	 * @param remindUrl
	 * @param sendDate
	 * @throws Exception
	 */
	public static void doSmsBack2(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
			throws Exception {
		T9SmsBack sb = new T9SmsBack();
		sb.setContent(content);
		sb.setFromId(fromId);
		sb.setToId(toId);
		sb.setSmsType(type);
		sb.setRemindUrl(remindUrl);
		sb.setSendDate(sendDate);
		T9SmsUtil.smsBack(conn, sb);
	}

	/**
	 * 邮件提醒 --cc
	 * 
	 * @param conn
	 * @param content
	 * @param fromId
	 * @param toId
	 * @param type
	 * @param remindUrl
	 * @throws Exception
	 */
	public static void doEmailBack(Connection conn, String content, int fromId, String toId, String type, String remindUrl) throws Exception {
		T9SmsBack sb = new T9SmsBack();
		sb.setContent(content);
		sb.setFromId(fromId);
		sb.setToId(toId);
		sb.setSmsType(type);
		sb.setRemindUrl(remindUrl);
		T9SmsUtil.smsBack(conn, sb);
	}

	/**
	 * 自动开始--cc
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public void getAutoBegin(Connection conn) throws Exception {
		String curDateStr = T9Utility.getCurDateTimeStr();
		String sql = " SELECT SEQ_ID, M_START from MEETING where M_STATUS = 1 ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				int seqId = rs.getInt("SEQ_ID");
				Date mStart = rs.getTimestamp("M_START");
				if (mStart.before(new Date()) || curDateStr.equals(mStart)) {
					updateStatus(conn, seqId, "2");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
	}

	/**
	 * 自动结束--cc
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public void getAutoEnd(Connection conn) throws Exception {
		String curDateStr = T9Utility.getCurDateTimeStr();
		String sql = " SELECT SEQ_ID, M_END from MEETING where M_STATUS = 2 ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				int seqId = rs.getInt("SEQ_ID");
				Date mStart = rs.getTimestamp("M_END");
				if (mStart.before(new Date()) || mStart.equals(new Date())) {
					updateStatus(conn, seqId, "4");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
	}

	/**
	 * 预约会议室是否冲突(返回true或者false)--cc
	 * 
	 * 
	 * @param conn
	 * @param seqId
	 * @param mRoom
	 * @param mStarts
	 * @param mEnds
	 * @return
	 * @throws Exception
	 */
	public boolean checkRoom(Connection conn, int seqId, int mRoom, Date mStart, Date mEnd) throws Exception {
		String result = "";
		int count = 0;
		String sql = "select SEQ_ID, M_START, M_END from MEETING where not SEQ_ID = " + seqId + " and M_ROOM = " + mRoom
				+ " and (M_STATUS = '1' or M_STATUS = '2')";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String seqIdStr = String.valueOf(rs.getInt("SEQ_ID"));
				Date mStart1 = rs.getTimestamp("M_START");
				Date mEnd1 = rs.getTimestamp("M_END");
				if (((mStart1.after(mStart) || mStart1.equals(mStart)) && mEnd1.before(mEnd)) || (mStart1.before(mStart) && mEnd1.after(mStart))
						|| (mStart1.before(mEnd) && mEnd1.after(mEnd)) || (mStart1.before(mStart) && mEnd1.after(mEnd))) {
					count++;
					if (!"".equals(result)) {
						result += ",";
					}
					result += seqIdStr;
				}
			}
			if (count >= 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
	}

	/**
	 * 预约会议室是否冲突(返回冲突会议的ID串)--cc
	 * 
	 * 
	 * @param conn
	 * @param seqId
	 * @param mRoom
	 * @param mStarts
	 * @param mEnds
	 * @return
	 * @throws Exception
	 */
	public String checkRoomData(Connection conn, int seqId, int mRoom, Date mStart, Date mEnd) throws Exception {
		String result = "";
		int count = 0;
		String sql = "select SEQ_ID, M_START, M_END from MEETING where not SEQ_ID = " + seqId + " and M_ROOM = " + mRoom
				+ " and (M_STATUS = '1' or M_STATUS = '2')";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String seqIdStr = String.valueOf(rs.getInt("SEQ_ID"));
				Date mStart1 = rs.getTimestamp("M_START");
				Date mEnd1 = rs.getTimestamp("M_END");
				if (((mStart1.after(mStart) || mStart1.equals(mStart)) && mEnd1.before(mEnd)) || (mStart1.before(mStart) && mEnd1.after(mStart))
						|| (mStart1.before(mEnd) && mEnd1.after(mEnd)) || (mStart1.before(mStart) && mEnd1.after(mEnd))) {
					count++;
					if (!"".equals(result)) {
						result += ",";
					}
					result += seqIdStr;
				}
			}
			if (count >= 1) {
				return result;
			} else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
	}

	/**
	 * 与下列会议冲突列表--cc
	 * 
	 * 
	 * @param dbConn
	 * @param request
	 * @param cycleNo
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMeetingConflict(Connection dbConn, Map request, String seqId) throws Exception {
		String sql = "";
		sql = "select " + "  SEQ_ID" + ", M_NAME" + ", M_START" + ", M_END" + " from MEETING where SEQ_ID IN ('" + seqId + "')";

		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	/**
	 * 预约情况－获取会议室信息--cc
	 * 
	 * @param dbConn
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9MeetingRoom> selectMeetingRoom(Connection dbConn, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		String[] str = { T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "SECRET_TO_ID") + " or "
				+ T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "OPERATOR") + " or "
				+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "TO_ID")
				+ " or (TO_ID like 'ALL_DEPT' or TO_ID like '0') or (TO_ID is null and SECRET_TO_ID is null)" };

		ArrayList<T9MeetingRoom> meetingRoomList = (ArrayList<T9MeetingRoom>) orm.loadListSingle(dbConn, T9MeetingRoom.class, str);
		return meetingRoomList;
	}

	/**
	 * 预约情况－获取会议信息--cc
	 * 
	 * @param dbConn
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public List<T9Meeting> selectMeetingStatus(Connection dbConn, int roomId, String date) throws Exception {
		T9ORM orm = new T9ORM();
		String[] str2 = { "M_STATUS <> '3' and M_STATUS <> '4' and M_ROOM='" + roomId + "' and ((" + T9DBUtility.getDateFilter("M_START", date, ">")
				+ " and " + T9DBUtility.getDateFilter("M_START", date + " 23:59:59", "<") + ")" + " or (" + T9DBUtility.getDateFilter("M_END", date, ">")
				+ " and " + T9DBUtility.getDateFilter("M_END", date + " 23:59:59", "<") + ")" + " or (" + T9DBUtility.getDateFilter("M_START", date, "<")
				+ " and " + T9DBUtility.getDateFilter("M_END", date + " 23:59:59", ">") + ")) order by M_START" };
		List<T9Meeting> usageList = new ArrayList<T9Meeting>();
		usageList = orm.loadListSingle(dbConn, T9Meeting.class, str2);
		return usageList;
	}

	/**
	 * 获取会议信息--cc
	 * 
	 * @param dbConn
	 * @param idStr
	 * @return
	 * @throws Exception
	 */
	public ArrayList<T9Meeting> getMeetingInfo(Connection dbConn, String idStrs) throws Exception {
		T9ORM orm = new T9ORM();
		String[] str = { "SEQ_ID IN (" + idStrs + ")" };
		ArrayList<T9Meeting> meetingList = (ArrayList<T9Meeting>) orm.loadListSingle(dbConn, T9Meeting.class, str);
		return meetingList;
	}

	// cc end
	/**
	 * 获取会议室管理员名称--wyw
	 */
	public String getMManagerLogic(Connection dbConn) throws Exception {
		String data = "";
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			StringBuffer buffer = new StringBuffer("[");
			String paraValue = this.getParaValue(dbConn);
			if (T9Utility.isNullorEmpty(paraValue)) {
				paraValue = "-1";
			}
			String sql = "SELECT SEQ_ID,USER_NAME from person where SEQ_ID in(" + paraValue + ")  order by USER_NO,USER_NAME";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			boolean isHave = false;
			while (rs.next()) {
				int dbSeqId = rs.getInt("SEQ_ID");
				String userName = T9Utility.null2Empty(rs.getString("USER_NAME"));
				buffer.append("{");
				buffer.append("value:" + dbSeqId);
				buffer.append(",text:\"" + T9Utility.encodeSpecial(userName) + "\"");
				buffer.append("},");
				isHave = true;
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
			data = buffer.toString();
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return data;
	}

	public String getParaValue(Connection dbConn) throws Exception {
		String sql = "SELECT PARA_VALUE from SYS_PARA where PARA_NAME='MEETING_OPERATOR'";
		String paraValue = "";

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();

			while (rs.next()) {
				paraValue = T9Utility.null2Empty(rs.getString("PARA_VALUE"));
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stmt, null, log);
		}
		return paraValue;
	}

	/**
	 * 获取有权限的会议室名称(暂留)
	 * 
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMRoomNameLogic1(Connection dbConn, T9Person person) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		try {
			List<T9MeetingRoom> meetingRooms = this.getMRoomList(dbConn);
			boolean isHave = false;
			if (meetingRooms != null && meetingRooms.size() > 0) {
				for (T9MeetingRoom meetingRoom : meetingRooms) {
					String secretToId = T9Utility.null2Empty(meetingRoom.getSecretToId());
					String operator = T9Utility.null2Empty(meetingRoom.getOperator());
					boolean secretToIdFlag = this.isLonginUser(secretToId, String.valueOf(person.getSeqId()));
					boolean operatorFlag = this.isLonginUser(operator, String.valueOf(person.getSeqId()));
					if (secretToIdFlag || operatorFlag) {
						int dbSeqId = meetingRoom.getSeqId();
						String mrName = T9Utility.null2Empty(meetingRoom.getMrName());
						String mrDesc = T9Utility.null2Empty(meetingRoom.getMrDesc());
						buffer.append("{");
						buffer.append("value:" + dbSeqId);
						buffer.append(",text:\"" + T9Utility.encodeSpecial(mrName) + "\"");
						buffer.append(",mrDesc:\"" + T9Utility.encodeSpecial(mrDesc) + "\"");
						buffer.append("},");
						isHave = true;
					}
				}
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
		} catch (Exception e) {
			throw e;
		}
		return buffer.toString();
	}

	/**
	 * 获取有权限的会议室名称
	 * 2011-4-13
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getMRoomNameLogic(Connection dbConn, T9Person person) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		// String sql =
		// "SELECT SEQ_ID,MR_NAME,MR_DESC from MEETING_ROOM where  find_in_set('$LOGIN_USER_ID',SECRET_TO_ID) or find_in_set('$LOGIN_USER_ID',OPERATOR) or TO_ID='ALL_DEPT' or find_in_set('$LOGIN_DEPT_ID',TO_ID) or (TO_ID='' and SECRET_TO_ID='')";
		String sql = "SELECT SEQ_ID,MR_NAME,MR_DESC from MEETING_ROOM where " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "SECRET_TO_ID")
				+ " or " + T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "OPERATOR") + " or TO_ID like 'ALL_DEPT' or TO_ID like '0' or "
				+ T9DBUtility.findInSet(String.valueOf(person.getDeptId()), "TO_ID") + " or (TO_ID  like '' and SECRET_TO_ID like '')";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {

			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			boolean isHave = false;
			while (rs.next()) {
				int dbSeqId = rs.getInt("SEQ_ID");
				String mrName = T9Utility.null2Empty(rs.getString("MR_NAME"));
				String mrDesc = T9Utility.null2Empty(rs.getString("MR_NAME"));
				buffer.append("{");
				buffer.append("value:" + dbSeqId);
				buffer.append(",text:\"" + T9Utility.encodeSpecial(mrName) + "\"");
				buffer.append(",mrDesc:\"" + T9Utility.encodeSpecial(mrDesc) + "\"");
				buffer.append("},");
				isHave = true;
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return buffer.toString();

	}

	public List<T9MeetingRoom> getMRoomList(Connection dbConn) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			Map map = new HashMap();
			return orm.loadListSingle(dbConn, T9MeetingRoom.class, map);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 比较idStrs串是否与loginUserSeqId匹配
	 * 
	 * @param idStrs
	 * @param loginUserSeqId
	 * @return
	 * @throws Exception
	 */
	public boolean isLonginUser(String idStrs, String loginUserSeqId) throws Exception {
		boolean flag = false;
		try {
			if (T9Utility.isNullorEmpty(idStrs)) {
				idStrs = "";
			}
			String[] idstrArry = idStrs.split(",");
			if (idstrArry != null && idstrArry.length != 0) {
				for (String id : idstrArry) {
					if (loginUserSeqId.equals(id)) {
						flag = true;
						return flag;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

	/**
	 * 获取会议规则
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public String getMeetingRuleLogic(Connection dbConn) throws Exception {
		String sql = "select PARA_VALUE from SYS_PARA where PARA_NAME = 'MEETING_ROOM_RULE'";
		String paraVale = "";
		String data = "";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				paraVale = T9Utility.null2Empty(rs.getString("PARA_VALUE"));
			}
			data = "{meetingRule:\"" + T9Utility.encodeSpecial(paraVale) + "\"}";
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}

		return data;
	}

	/**
	 * 新建周期性会议设置--wyw
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void setCycleMeetingValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String newAttchId = (String) map.get("newAttchId");
			String newAttchName = (String) map.get("newAttchName");
			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String attachmentId = (String) map.get("attachmentId");
			String attachmentName = (String) map.get("attachmentName");

			T9Meeting meeting = new T9Meeting();
			if (fromFolderFlag && uploadFlag) {
				meeting.setAttachmentId(newAttchId.trim() + attachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + attachmentName.trim());
			} else if (fromFolderFlag) {
				meeting.setAttachmentId(newAttchId.trim());
				meeting.setAttachmentName(newAttchName.trim());
			} else if (uploadFlag) {
				meeting.setAttachmentId(attachmentId.trim());
				meeting.setAttachmentName(attachmentName.trim());
			}
			int mRoom = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("mRoom"))) {
				mRoom = Integer.parseInt(fileForm.getParameter("mRoom"));
			}
			int resendLong = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("resendLong"))) {
				resendLong = Integer.parseInt(fileForm.getParameter("resendLong"));
			}
			int resendSeveral = 0;
			if (!T9Utility.isNullorEmpty(fileForm.getParameter("resendSeveral"))) {
				resendSeveral = Integer.parseInt(fileForm.getParameter("resendSeveral"));
			}
			String smsRemind = fileForm.getParameter("smsRemind");
			if (T9Utility.isNullorEmpty(smsRemind)) {
				smsRemind = "0";
			}
			String sms2Remind = fileForm.getParameter("sms2Remind");
			if (T9Utility.isNullorEmpty(sms2Remind)) {
				sms2Remind = "0";
			}
			String calendar = fileForm.getParameter("calendar");
			if (T9Utility.isNullorEmpty(calendar)) {
				calendar = "0";
			}
			if ("on".equals(calendar)) {
				calendar = "1";
			}
			String recorder = fileForm.getParameter("recorder");
			if (T9Utility.isNullorEmpty(recorder)) {
				recorder = "";
			}
			String mStatus = fileForm.getParameter("mStatus");
			if (T9Utility.isNullorEmpty(mStatus)) {
				mStatus = "0";
			}
			String cycle = fileForm.getParameter("cycle");
			if (T9Utility.isNullorEmpty(cycle)) {
				cycle = "0";
			}
			String rd = fileForm.getParameter("RD");
			if ("1".equals(rd)) {
				cycle = "1";
			}
			int cycleNo = 0;
			if ("1".equals(cycle)) {
				cycleNo = this.getMaxCycleNo(dbConn);
				cycleNo++;
			}
			String mStartDateStr = fileForm.getParameter("M_START_DATE");
			String mEndDateStr = fileForm.getParameter("M_END_DATE");
			Date mStartDate = null;
			if (!T9Utility.isNullorEmpty(mStartDateStr)) {
				mStartDate = T9Utility.parseDate("yyyy-MM-dd", mStartDateStr);
			}
			Date mEndDate = null;
			if (!T9Utility.isNullorEmpty(mEndDateStr)) {
				mEndDate = T9Utility.parseDate("yyyy-MM-dd", mEndDateStr);
			}
			int temDate = T9Utility.getDaySpan(mStartDate, mEndDate) + 1;

			String smsReminde1 = fileForm.getParameter("smsReminde1");
			String smsReminde2 = fileForm.getParameter("smsReminde2");
			if (T9Utility.isNullorEmpty(smsReminde1)) {
				smsReminde1 = "0";
			}
			if (T9Utility.isNullorEmpty(smsReminde2)) {
				smsReminde2 = "0";
			}

			String mName = fileForm.getParameter("mName");
			String mTopic = fileForm.getParameter("mTopic");
			String mDesc = fileForm.getParameter("mDesc");
			String mAttendee = fileForm.getParameter("mAttendee");
			String mManager = fileForm.getParameter("mManager");
			String mAttendeeOut = fileForm.getParameter("mAttendeeOut");
			String toId = fileForm.getParameter("toId");
			String privId = fileForm.getParameter("privId");
			String secretToId = fileForm.getParameter("secretToId");
			String equipmentIdStr = fileForm.getParameter("equipmentIdStr");
			
			for (int i = 0; i < temDate; i++) {
				String mStarTimeStr = T9Utility.null2Empty(fileForm.getParameter("M_START_TIME"));
				String mEndTimeStr = T9Utility.null2Empty(fileForm.getParameter("M_END_TIME"));
				Date afterDate = T9Utility.getDayAfter(mStartDate, i);
				String formatDateStr = new SimpleDateFormat("yyyy-MM-dd").format(afterDate);
				String startAfterDateStr = formatDateStr + " " + mStarTimeStr;
				String endAfterDateStr = formatDateStr + " " + mEndTimeStr;
				int curDay = this.getDateWeek(formatDateStr);

				String w1 = fileForm.getParameter("W1");
				String w2 = fileForm.getParameter("W2");
				String w3 = fileForm.getParameter("W3");
				String w4 = fileForm.getParameter("W4");
				String w5 = fileForm.getParameter("W5");
				String w6 = fileForm.getParameter("W6");
				String w7 = fileForm.getParameter("W7");

				if (curDay == 1 && T9Utility.isNullorEmpty(w1)) {
					continue;
				}
				if (curDay == 2 && T9Utility.isNullorEmpty(w2)) {
					continue;
				}
				if (curDay == 3 && T9Utility.isNullorEmpty(w3)) {
					continue;
				}
				if (curDay == 4 && T9Utility.isNullorEmpty(w4)) {
					continue;
				}
				if (curDay == 5 && T9Utility.isNullorEmpty(w5)) {
					continue;
				}
				if (curDay == 6 && T9Utility.isNullorEmpty(w6)) {
					continue;
				}
				if (curDay == 7 && T9Utility.isNullorEmpty(w7)) {
					continue;
				}

				meeting.setMName(mName);
				meeting.setMTopic(mTopic);
				meeting.setMDesc(mDesc);
				meeting.setMProposer(String.valueOf(person.getSeqId()));
				meeting.setMRequestTime(T9Utility.parseTimeStamp());
				meeting.setMAttendee(mAttendee);
				meeting.setMStart(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", startAfterDateStr));
				meeting.setMEnd(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", endAfterDateStr));
				meeting.setMRoom(mRoom);
				meeting.setMManager(mManager);
				meeting.setMAttendeeOut(mAttendeeOut);
				meeting.setSmsRemind(smsRemind);
				meeting.setSms2Remind(sms2Remind);
				meeting.setToId(toId);
				meeting.setPrivId(privId);
				meeting.setSecretToId(secretToId);
				meeting.setResendLong(resendLong);
				meeting.setResendSeveral(resendSeveral);
				meeting.setEquipmentIdStr(equipmentIdStr);
				meeting.setCalendar(calendar);
				meeting.setRecorder(recorder);
				meeting.setMStatus(mStatus);
				meeting.setCycle(cycle);
				meeting.setCycleNo(cycleNo);
				this.addMeetingInfo(dbConn, meeting);
			}

			int maxSeqId = this.getMaxMeetingId(dbConn);
			String mManagerStr = mManager;
			String userName = person.getUserName();
      
			String content = userName + " 向您提交周期性会议申请，请批示！";
			int fromId = person.getSeqId();
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde1)) {
				String remindUrl = "/subsys/oa/meeting/manage/index.jsp";
				this.doSmsBack2(dbConn, content, fromId, mManagerStr, "8", remindUrl, T9Utility.parseTimeStamp());
			}
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde2)) {
				T9MobileSms2Logic sbl = new T9MobileSms2Logic();
				sbl.remindByMobileSms(dbConn, mManagerStr, fromId, content, T9Utility.parseTimeStamp());
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 返回某个日期为星期几 --wyw
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static int getDateWeek(String date) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		GregorianCalendar d = new GregorianCalendar();
		Date mydate = dateFormat.parse(date);
		d.setTime(mydate);
		int today = d.get(Calendar.DAY_OF_WEEK);
		if (today == 1) {
			today = 7;
		} else {
			today = today - 1;
		}
		return today;
	}

	/**
	 * 获取最大的周期性会议编号加
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public int getMaxCycleNo(Connection dbConn) throws Exception {
		int reSult = 0;
		String sql = "select max(CYCLE_NO) as COUNT from MEETING";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				reSult = rs.getInt("COUNT");
			}
		} catch (Exception e) {
			throw e;
		}
		return reSult;
	}

	/**
	 * 获取出席人员名称 --wyw
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public String getAttendeeNameLogic(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			T9Meeting meeting = (T9Meeting) orm.loadObjSingle(dbConn, T9Meeting.class, seqId);
			String mAttendee = "";
			String mAttendeeOut = "";
			String mAttendeeNames = "";
			if (meeting != null) {
				mAttendee = T9Utility.null2Empty(meeting.getMAttendee());
				mAttendeeOut = T9Utility.null2Empty(meeting.getMAttendeeOut());
			}
			mAttendeeNames = this.getUserNameLogic2(dbConn, mAttendee);
			String data = "\"内部:" + T9Utility.encodeSpecial(mAttendeeNames) + "<br>外部:" + T9Utility.encodeSpecial(mAttendeeOut) + "\" ";
			return data;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 取得出席人员名称--wyw
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getUserNameLogic2(Connection dbConn, String seqIdStr) throws Exception {
		String result = "";
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			return result;
		}
		if (seqIdStr.endsWith(",")) {
			seqIdStr = seqIdStr.substring(0, seqIdStr.length() - 1);
		}
		String sql = " select USER_NAME from PERSON where SEQ_ID in(" + seqIdStr + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result += toId + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

	/**
	 * 更新会议纪要信息--wyw
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public String updateMeetingSummaryLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		T9ORM orm = new T9ORM();
		String seqIdStr = fileForm.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String newAttchId = (String) map.get("newAttchId");
			String newAttchName = (String) map.get("newAttchName");

			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String attachmentId = (String) map.get("attachmentId");
			String attachmentName = (String) map.get("attachmentName");
			String summary = fileForm.getParameter("summary");
			String readPeopleId = fileForm.getParameter("readPeopleId");

			T9Meeting meeting = (T9Meeting) orm.loadObjSingle(dbConn, T9Meeting.class, seqId);

			if (meeting == null) {
				return null;
			}

			String dbAttachmentId1 = "";
			String dbAttachmentName = "";
			dbAttachmentId1 = T9Utility.null2Empty(meeting.getAttachmentId1());
			dbAttachmentName = T9Utility.null2Empty(meeting.getAttachmentName1());
			if (fromFolderFlag && uploadFlag) {
				meeting.setAttachmentId1(dbAttachmentId1.trim() + newAttchId.trim() + attachmentId.trim());
				meeting.setAttachmentName1(dbAttachmentName.trim() + newAttchName.trim() + attachmentName.trim());
			} else if (fromFolderFlag) {
				meeting.setAttachmentId1(dbAttachmentId1.trim() + newAttchId.trim());
				meeting.setAttachmentName1(dbAttachmentName.trim() + newAttchName.trim());
			} else if (uploadFlag) {
				meeting.setAttachmentId1(dbAttachmentId1.trim() + attachmentId.trim());
				meeting.setAttachmentName1(dbAttachmentName.trim() + attachmentName.trim());
			}
			meeting.setSummary(summary);
			meeting.setReadPeopleId(readPeopleId);
			orm.updateSingle(dbConn, meeting);
			;
		} catch (Exception e) {
			throw e;
		}
		return null;
	}

	/**
	 * 会议纪要查询--wyw
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param mName
	 * @param mProposer
	 * @param beginDate
	 * @param endDate
	 * @param mRoom
	 * @param mStatus
	 * @return
	 * @throws Exception
	 */
	public String queryMeetingSummaryLogic(Connection dbConn, Map request, T9Person person, Map map) throws Exception {

		String mName = (String) map.get("mName");
		String mProposer = (String) map.get("mProposer");
		String beginDate = (String) map.get("beginDate");
		String endDate = (String) map.get("endDate");
		String mRoom = (String) map.get("mRoom");
		String keyWord1 = (String) map.get("keyWord1");
		String keyWord2 = (String) map.get("keyWord2");
		String keyWord3 = (String) map.get("keyWord3");
		String conditionStr = "";
		try {
			if (!T9Utility.isNullorEmpty(mName)) {
				conditionStr = " and M_NAME like '%" + T9DBUtility.escapeLike(mName) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(mProposer)) {
				conditionStr += " and M_PROPOSER ='" + T9DBUtility.escapeLike(mProposer) + "'";
			}
			if (!T9Utility.isNullorEmpty(beginDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("M_START", beginDate, ">=");
			}
			if (!T9Utility.isNullorEmpty(endDate)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("M_START", endDate, "<=");
			}
			if (!T9Utility.isNullorEmpty(mRoom)) {
				conditionStr += " and M_ROOM ='" + T9DBUtility.escapeLike(mRoom) + "'";
			}
			if (!T9Utility.isNullorEmpty(keyWord1)) {
				conditionStr += " and SUMMARY like '%" + T9DBUtility.escapeLike(keyWord1) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(keyWord2)) {
				conditionStr += " and SUMMARY like '%" + T9DBUtility.escapeLike(keyWord2) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(keyWord3)) {
				conditionStr += " and SUMMARY like '%" + T9DBUtility.escapeLike(keyWord3) + "%'" + T9DBUtility.escapeLike();
			}
			String sql = "SELECT SEQ_ID,M_NAME,M_PROPOSER,M_START,M_END,SUMMARY,READ_PEOPLE_ID,M_ATTENDEE,TO_ID,PRIV_ID , SECRET_TO_ID from MEETING where 1=1 " + conditionStr;
			String privSeqIdStr = this.getSeqIdStr(dbConn, sql, person);
			String query = "SELECT SEQ_ID,M_NAME,M_PROPOSER,M_START,M_END,SEQ_ID from MEETING where SEQ_ID in(" + privSeqIdStr
					+ " )  order by M_START desc";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, query);
			return pageDataList.toJson();

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取满足条件的seqId串
	 * 
	 * @param dbConn
	 * @param sqlStr
	 * @return
	 * @throws Exception
	 */
	public String getSeqIdStr(Connection dbConn, String sqlStr, T9Person person) throws Exception {
		String seqIdStr = "-1,";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConn.prepareStatement(sqlStr);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String readPeopleId = T9Utility.null2Empty(rs.getString("READ_PEOPLE_ID"));
				String mProposer = T9Utility.null2Empty(rs.getString("M_PROPOSER"));
				String mAttendee = T9Utility.null2Empty(rs.getString("M_ATTENDEE"));
				String summary = T9Utility.null2Empty(rs.getString("SUMMARY"));
				String toId = T9Utility.null2Empty(rs.getString("TO_ID"));
				String privId = T9Utility.null2Empty(rs.getString("PRIV_ID"));
				String secretToId = T9Utility.null2Empty(rs.getString("SECRET_TO_ID"));
			      
				int meetingId = rs.getInt("SEQ_ID");
				if (!T9Utility.isNullorEmpty(readPeopleId)) {
					readPeopleId += ",";
				}
				if (!T9Utility.isNullorEmpty(mProposer)) {
					mProposer += ",";
				}
				if (!T9Utility.isNullorEmpty(mAttendee)) {
					mAttendee += ",";
				}
				String idStrs = readPeopleId + mProposer + mAttendee;
				if (idStrs.endsWith(",")) {
					idStrs = idStrs.substring(0, idStrs.length() - 1);
				}
				boolean havePrivFlag = this.isLonginUser(idStrs, String.valueOf(person.getSeqId()));
				boolean havePrivFlag2 = !"ALL_DEPT".equals(toId) 
        && !"0".equals(toId) 
        && !T9WorkFlowUtility.findId(toId, String.valueOf(person.getDeptId()))
        && !T9WorkFlowUtility.findId(privId, person.getUserPriv())
        && !T9WorkFlowUtility.findId(secretToId, String.valueOf(person.getSeqId()));
				if (havePrivFlag2 && !havePrivFlag && !person.isAdminRole()) {
					continue;
				}
        
				if (!T9Utility.isNullorEmpty(summary)) {
					seqIdStr += String.valueOf(meetingId) + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		if (seqIdStr.endsWith(",")) {
			seqIdStr = seqIdStr.substring(0, seqIdStr.length() - 1);
		}
		return seqIdStr;
	}

	/**
	 * 取得在线调度人名称--wyw
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public String getOnlineUsersLogic(Connection dbConn) throws Exception {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String data = "";
		String returnStr = "";
		synchronized(T9SystemService.onlineSync) {
		try {
			String paraValue = this.getParaValue(dbConn);
			if (T9Utility.isNullorEmpty(paraValue)) {
				paraValue = "-1";
			}
			String sql = "SELECT distinct p.SEQ_ID,  p.USER_NAME from PERSON p,USER_ONLINE where p.SEQ_ID=USER_ONLINE.USER_ID and P.SEQ_ID in(" + paraValue
					+ ") order by USER_NAME";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String userName = T9Utility.null2Empty(rs.getString("USER_NAME"));
				if (!T9Utility.isNullorEmpty(userName)) {
					data += userName + ",";
				}
			}
			if (data.endsWith(",")) {
				data = data.substring(0, data.length() - 1);
			}
			returnStr = "{userName:\"" + T9Utility.encodeSpecial(data) + "\"}";
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		dbConn.commit();
		}
		return returnStr;
	}

	/**
	 * 更新会议申请信息
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void updateMeetingValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map, int meetingId)
			throws Exception {
		T9ORM orm = new T9ORM();
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String newAttchId = (String) map.get("newAttchId");
			String newAttchName = (String) map.get("newAttchName");
			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String attachmentId = (String) map.get("attachmentId");
			String attachmentName = (String) map.get("attachmentName");
			boolean pageAttIdFlag = (Boolean) map.get("pageAttIdFlag");
			String pageAttachmentId = (String) map.get("pageAttachmentId");
			String pageattachmentName = (String) map.get("pageattachmentName");
			T9Meeting meeting = (T9Meeting) orm.loadObjSingle(dbConn, T9Meeting.class, meetingId);
			if (fromFolderFlag && uploadFlag && pageAttIdFlag) {
				meeting.setAttachmentId(newAttchId.trim() + attachmentId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + attachmentName.trim() + pageattachmentName.trim());
			} else if (fromFolderFlag && uploadFlag) {
				meeting.setAttachmentId(newAttchId.trim() + attachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + attachmentName.trim());
			} else if (fromFolderFlag && pageAttIdFlag) {
				meeting.setAttachmentId(newAttchId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(newAttchName.trim() + pageattachmentName.trim());
			} else if (uploadFlag && pageAttIdFlag) {
				meeting.setAttachmentId(attachmentId.trim() + pageAttachmentId.trim());
				meeting.setAttachmentName(attachmentName.trim() + pageattachmentName.trim());
			} else if (fromFolderFlag) {
				meeting.setAttachmentId(newAttchId.trim());
				meeting.setAttachmentName(newAttchName.trim());
			} else if (uploadFlag) {
				meeting.setAttachmentId(attachmentId.trim());
				meeting.setAttachmentName(attachmentName.trim());
			} else if (pageAttIdFlag) {
				meeting.setAttachmentId(pageAttachmentId.trim());
				meeting.setAttachmentName(pageattachmentName.trim());
			}

			String privId = fileForm.getParameter("privId");
			String toId = fileForm.getParameter("toId");
			String mName = fileForm.getParameter("mName");
			String mTopic = fileForm.getParameter("mTopic");
			String mDesc = fileForm.getParameter("mDesc");
			String secretToId = fileForm.getParameter("secretToId");
			String mAttendee = fileForm.getParameter("mAttendee");
			String mStart = fileForm.getParameter("mStart");
			String mEnd = fileForm.getParameter("mEnd");
			String mRoomStr = fileForm.getParameter("mRoom");
			String mManager = fileForm.getParameter("mManager");
			String mAttendeeOut = fileForm.getParameter("mAttendeeOut");
			String smsRemindStr = fileForm.getParameter("smsRemind");
			String sms2RemindStr = fileForm.getParameter("sms2Remind");
			String resendSeveralStr = fileForm.getParameter("resendSeveral");
			String resendLongStr = fileForm.getParameter("resendLong");
			String equipmentIdStr = fileForm.getParameter("checkEquipmentes");
			String calendarStr = fileForm.getParameter("calendar");
			String recorderStr = fileForm.getParameter("recorder");
			String mStatus = fileForm.getParameter("mStatus");
			String cycle = fileForm.getParameter("cycle");
			String smsReminde1 = fileForm.getParameter("smsReminde1");
			String smsReminde2 = fileForm.getParameter("smsReminde2");

			String attachmentIdStr = fileForm.getParameter("attachmentId");
			String attachmentNameStr = fileForm.getParameter("attachmentName");
			if (T9Utility.isNullorEmpty(smsReminde1)) {
				smsReminde1 = "0";
			}
			if (T9Utility.isNullorEmpty(smsReminde2)) {
				smsReminde2 = "0";
			}

			meeting.setPrivId(privId);
			meeting.setSecretToId(secretToId);
			meeting.setToId(toId);
			meeting.setMName(mName);
			meeting.setMTopic(mTopic);
			meeting.setMDesc(mDesc);
			meeting.setMRequestTime(T9Utility.parseTimeStamp());
			meeting.setMAttendee(mAttendee);
			meeting.setMStart(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", mStart));
			meeting.setMEnd(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", mEnd));
			int mRoom = 0;
			if (!T9Utility.isNullorEmpty(mRoomStr)) {
				mRoom = Integer.parseInt(mRoomStr);
			}
			meeting.setMRoom(mRoom);
			meeting.setMManager(mManager);
			meeting.setMAttendeeOut(mAttendeeOut);
			if (T9Utility.isNullorEmpty(smsRemindStr)) {
				smsRemindStr = "0";
			}
			meeting.setSmsRemind(smsRemindStr);

			if (T9Utility.isNullorEmpty(sms2RemindStr)) {
				sms2RemindStr = "0";
			}
			meeting.setSms2Remind(sms2RemindStr);
			int resendSeveral = 0;
			if (!T9Utility.isNullorEmpty(resendSeveralStr)) {
				resendSeveral = Integer.parseInt(resendSeveralStr);
			}
			meeting.setResendSeveral(resendSeveral);
			int resendLong = 0;
			if (!T9Utility.isNullorEmpty(resendLongStr)) {
				resendLong = Integer.parseInt(resendLongStr);
			}
			meeting.setResendLong(resendLong);
			if (!T9Utility.isNullorEmpty(equipmentIdStr)) {
			}
			meeting.setEquipmentIdStr(equipmentIdStr);
			if (T9Utility.isNullorEmpty(calendarStr)) {
				calendarStr = "0";
			}
			meeting.setCalendar(calendarStr);
			if (T9Utility.isNullorEmpty(recorderStr)) {
				recorderStr = "";
			}
			meeting.setRecorder(recorderStr);
			if (T9Utility.isNullorEmpty(mStatus)) {
				mStatus = "0";
			}
			meeting.setMStatus(mStatus);
			if (T9Utility.isNullorEmpty(cycle)) {
				cycle = "0";
			}
			meeting.setCycle(cycle);
			orm.updateSingle(dbConn, meeting);

			// int maxSeqId = this.getMaxMeetingId(dbConn);
			String mManagerStr = mManager;
			String userName = this.getUserNameLogic(dbConn, person.getSeqId());
			String content = userName + " 向您提交会议申请，请批示！";
			int fromId = person.getSeqId();
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde1)) {
				String remindUrl = "/subsys/oa/meeting/query/meetingdetail.jsp?seqId=" + meeting.getSeqId() + "&openFlag=1&openWidth=860&openHeight=650";
				this.doSmsBack2(dbConn, content, fromId, mManagerStr, "8", remindUrl, T9Utility.parseTimeStamp());
			}
			if (!T9Utility.isNullorEmpty(mManagerStr) && "1".equals(smsReminde2)) {
				T9MobileSms2Logic sbl = new T9MobileSms2Logic();
				sbl.remindByMobileSms(dbConn, mManagerStr, fromId, content, T9Utility.parseTimeStamp());
			}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 处理上传附件，返回附件id，附件名称--wyw
	 * 
	 * @param fileForm
	 * @return
	 * @throws Exception
	 */
	public Map<Object, Object> fileUploadLogic(T9FileUploadForm fileForm, String attachmentFolder) throws Exception {
		Map<Object, Object> result = new HashMap<Object, Object>();
		try {
			Iterator<String> iKeys = fileForm.iterateFileFields();
			boolean uploadFlag = false;
			String attachmentId = "";
			String attachmentName = "";
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyMM");
			String currDate = format.format(date);
			String separator = File.separator;
			String filePath = T9SysProps.getAttachPath() + separator + attachmentFolder + separator + currDate;

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
				fileForm.saveFile(fieldName, filePath +File.separator + fileName);
			}
			result.put("attachmentId", attachmentId);
			result.put("attachmentName", attachmentName);
			result.put("uploadFlag", uploadFlag);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 更新浮动菜单附件
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param attachId
	 * @param attachName
	 * @return
	 * @throws Exception
	 */
	public boolean updateFloadFile(Connection dbConn, int seqId, String attachId, String attachName, String delOpt) throws Exception {
		boolean flag = false;
		T9ORM orm = new T9ORM();
		try {
			T9Meeting meeting = (T9Meeting) orm.loadObjSingle(dbConn, T9Meeting.class, seqId);
			if (meeting != null) {
				if ("summary".equals(delOpt)) {
					meeting.setAttachmentId1(T9Utility.null2Empty(attachId));
					meeting.setAttachmentName1(T9Utility.null2Empty(attachName));
				} else {
					meeting.setAttachmentId(T9Utility.null2Empty(attachId));
					meeting.setAttachmentName(T9Utility.null2Empty(attachName));
				}
				orm.updateSingle(dbConn, meeting);
				flag = true;
			}
		} catch (Exception e) {
			throw e;
		}
		return flag;
	}

}
