package t9.subsys.oa.meeting.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.subsys.oa.meeting.data.T9MeetingComment;

public class T9MeetingCommentLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.meeting.logic.T9MeetingCommentLogic.java");

	/**
	 * 新建会议纪要设置值--wyw
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void setCommentValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String newAttchId = (String) map.get("newAttchId");
			String newAttchName = (String) map.get("newAttchName");

			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String attachmentId = (String) map.get("attachmentId");
			String attachmentName = (String) map.get("attachmentName");

			T9MeetingComment comment = new T9MeetingComment();
			if (fromFolderFlag && uploadFlag) {
				comment.setAttachmentId(newAttchId.trim() + attachmentId.trim());
				comment.setAttachmentName(newAttchName.trim() + attachmentName.trim());
			} else if (fromFolderFlag) {
				comment.setAttachmentId(newAttchId.trim());
				comment.setAttachmentName(newAttchName.trim());
			} else if (uploadFlag) {
				comment.setAttachmentId(attachmentId.trim());
				comment.setAttachmentName(attachmentName.trim());
			}
			String meetingIdStr = fileForm.getParameter("meetingId");
			String content = fileForm.getParameter("content");

			int meetingId = 0;
			if (!T9Utility.isNullorEmpty(meetingIdStr)) {
				meetingId = Integer.parseInt(meetingIdStr);
			}
			comment.setMeetingId(meetingId);
			comment.setContent(content);
			comment.setReTime(T9Utility.parseTimeStamp());
			comment.setUserId(String.valueOf(person.getSeqId()));
			orm.saveSingle(dbConn, comment);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取评论信息--wyw
	 * @param conn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public String getCommentInfoLogic(Connection dbConn, T9Person person,int meetingId) throws Exception {
		T9PersonLogic personLogic = new T9PersonLogic();
		T9MeetingLogic meetingLogic = new T9MeetingLogic();
		StringBuffer buffer = new StringBuffer("["); 
		String query = "select SEQ_ID,CONTENT,RE_TIME,USER_ID,ATTACHMENT_ID,ATTACHMENT_NAME from MEETING_COMMENT where MEETING_ID =" + meetingId + " order by RE_TIME desc";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = dbConn.createStatement();
			rs = stmt.executeQuery(query);
			int count = 0;
			while(rs.next() && count++ < 5){
				int commentId = rs.getInt("SEQ_ID");
				String dbContent = T9Utility.null2Empty(rs.getString("CONTENT"));
				Date dbReTime = rs.getTimestamp("RE_TIME");
				String dbUserIdStr = T9Utility.null2Empty(rs.getString("USER_ID"));
				String dbAttachmentId = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
				String dbAttachmentName = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
				
				int dbUserId=-1;
				if (!T9Utility.isNullorEmpty(dbUserIdStr)) {
					dbUserId=Integer.parseInt(dbUserIdStr);
				}
				T9Person objPerson=getPersonObj(dbConn, dbUserId);
				String personName = objPerson.getUserName();
				String deptName=personLogic.getDeptName(dbConn, person.getDeptId());
				boolean isLoginUser = meetingLogic.isLonginUser(dbUserIdStr, String.valueOf(person.getSeqId()));
				int delPrivFlag=0;
				if (isLoginUser || person.isAdminRole()) {
					delPrivFlag = 1;
				}
				buffer.append("{");
				buffer.append("commentId:\"" + commentId + "\"");
				buffer.append(",content:\"" + T9Utility.encodeSpecial(dbContent) + "\"");
				buffer.append(",content:\"" + T9Utility.encodeSpecial(dbContent) + "\"");
				buffer.append(",reTime:\"" + T9Utility.getDateTimeStr(dbReTime) + "\"");
				buffer.append(",attachmentId:\"" + T9Utility.encodeSpecial(dbAttachmentId) + "\"");
				buffer.append(",attachmentName:\"" + T9Utility.encodeSpecial(dbAttachmentName) + "\"");
				buffer.append(",personName:\"" + T9Utility.encodeSpecial(personName) + "\"");
				buffer.append(",deptName:\"" + T9Utility.encodeSpecial(deptName) + "\"");
				buffer.append(",delPrivFlag:\"" + delPrivFlag + "\"");
				buffer.append("},");
			}
			if(buffer.length() > 1){
				buffer.deleteCharAt(buffer.length() - 1);
      }
			buffer.append("]");
		} catch (Exception ex) {
			throw ex;
		}finally {
			T9DBUtility.close(stmt, rs, log);
		} 
		return buffer.toString();
	}
	
	/**
	 * 获取人员对象
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9Person getPersonObj(Connection dbConn,int seqId) throws Exception{
		T9ORM orm=new T9ORM();
		try {
			return (T9Person) orm.loadObjSingle(dbConn, T9Person.class, seqId);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 删除会议记录（含附件）--wyw
	 * @param dbConn
	 * @param seqId
	 * @throws Exception 
	 */
	public void delCommentLogic(Connection dbConn, int seqId,String filePath) throws Exception{
		T9MeetingLogic meetingLogic = new T9MeetingLogic();
		T9ORM orm = new T9ORM();
		try {
			StringBuffer attIdBuffer = new StringBuffer();
			StringBuffer attNameBuffer = new StringBuffer();
			T9MeetingComment comment=(T9MeetingComment) orm.loadObjSingle(dbConn, T9MeetingComment.class, seqId);
			String dbAttachmentId = "";
			String dbAttachmentName= "";
			if (comment != null) {
				dbAttachmentId = T9Utility.null2Empty(comment.getAttachmentId());
				dbAttachmentName = T9Utility.null2Empty(comment.getAttachmentName());
				attIdBuffer.append(dbAttachmentId.trim());
				attNameBuffer.append(dbAttachmentName.trim());
			}
			String[] attIdArray = {};
			String[] attNameArray = {};
			if (!T9Utility.isNullorEmpty(attIdBuffer.toString()) && !T9Utility.isNullorEmpty(attNameBuffer.toString()) && attIdBuffer.length() > 0) {
				attIdArray = attIdBuffer.toString().trim().split(",");
				attNameArray = attNameBuffer.toString().trim().split("\\*");
			}
			if (attIdArray != null && attIdArray.length > 0) {
				for (int i = 0; i < attIdArray.length; i++) {
					Map<String, String> map = meetingLogic.getFileName(attIdArray[i], attNameArray[i]);
					if (map.size() != 0) {
						Set<String> set = map.keySet();
						// 遍历Set集合
						for (String keySet : set) {
							String key = keySet;
							String keyValue = map.get(keySet);
							String attaIdStr = meetingLogic.getAttaId(keySet);
							String fileNameValue = attaIdStr + "_" + keyValue;
							String fileFolder = meetingLogic.getFilePathFolder(key);
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
			// 删除数据库信息			orm.deleteSingle(dbConn, comment);
		} catch (Exception e) {
			throw e;
		}
	}
}
