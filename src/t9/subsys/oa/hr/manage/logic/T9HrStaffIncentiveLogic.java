package t9.subsys.oa.hr.manage.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.manage.data.T9HrStaffIncentive;
import t9.subsys.oa.hr.setting.act.T9HrSetOtherAct;

public class T9HrStaffIncentiveLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.hr.manage.logic.T9HrStaffIncentiveLogic.java");

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
			// 保存从文件柜、网络硬盘选择附件
			T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, attachmentFolder);
			String attIdStr = sel.getAttachIdToString(",");
			String attNameStr = sel.getAttachNameToString("*");
			boolean fromFolderFlag = false;
			String forlderAttchId = "";
			String forlderAttchName = "";
			if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
				forlderAttchId = attIdStr + ",";
				forlderAttchName = attNameStr + "*";
				fromFolderFlag = true;
			}
			Iterator<String> iKeys = fileForm.iterateFileFields();
			boolean uploadFlag = false;
			String uploadAttchId = "";
			String uploadAttchName = "";
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
				uploadAttchId += currDate + "_" + rand + ",";
				uploadAttchName += fileName + "*";
				uploadFlag = true;

				fileName = rand + "_" + fileName;
				fileForm.saveFile(fieldName, filePath + File.separator + fileName);
			}
			boolean attachFlag = false;
			String attachmentIds = "";
			String attachmentNames = "";
			if (fromFolderFlag && uploadFlag) {
				attachmentIds = forlderAttchId + uploadAttchId;
				attachmentNames = forlderAttchName + uploadAttchName;
				attachFlag = true;
			} else if (fromFolderFlag) {
				attachmentIds = forlderAttchId;
				attachmentNames = forlderAttchName;
				attachFlag = true;
			} else if (uploadFlag) {
				attachmentIds = uploadAttchId;
				attachmentNames = uploadAttchName;
				attachFlag = true;
			}
			result.put("attachFlag", attachFlag);
			result.put("attachmentIds", attachmentIds);
			result.put("attachmentNames", attachmentNames);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 新建奖惩信息
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @throws Exception
	 */
	public void setNewStaffincentiveValueLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		String staffNameStr = fileForm.getParameter("staffName");
		String incentiveItem = fileForm.getParameter("incentiveItem");
		String incentiveTimeStr = fileForm.getParameter("incentiveTime");
		String salaryMonth = fileForm.getParameter("salaryMonth");
		String incentiveType = fileForm.getParameter("incentiveType");
		String incentiveAmountStr = fileForm.getParameter("incentiveAmount");
		String remark = fileForm.getParameter("remark");
		String incentiveDescription = fileForm.getParameter("incentiveDescription");
		String addScoreStr = fileForm.getParameter("addScore");
		String reduceScoreStr = fileForm.getParameter("reduceScore");
		String yearScoreStr = fileForm.getParameter("yearScore");
		String smsRemind = fileForm.getParameter("smsRemind");
		String sms2Remind = fileForm.getParameter("sms2Remind");

		Date incentiveTime = T9Utility.parseDate("yyyy-MM-dd", incentiveTimeStr);
		double incentiveAmount = 0;
		double yearScore = 0;
		if (T9Utility.isNumber(incentiveAmountStr)) {
			incentiveAmount = Double.parseDouble(incentiveAmountStr);
		}
		if (T9Utility.isNumber(yearScoreStr)) {
			yearScore = Double.parseDouble(yearScoreStr);
		}
		Map<Object, Object> map = this.fileUploadLogic(fileForm, T9HrSetOtherAct.attachmentFolder);
		boolean attachFlag = (Boolean) map.get("attachFlag");
		String attachmentIds = (String) map.get("attachmentIds");
		String attachmentNames = (String) map.get("attachmentNames");

		double addScore = 0;
		double reduceScore = 0;
		if (T9Utility.isNumber(addScoreStr) && "1".equals(incentiveType.trim())) {
			addScore = Double.parseDouble(addScoreStr);
		}
		if (T9Utility.isNumber(reduceScoreStr) && "2".equals(incentiveType.trim())) {
			reduceScore = Double.parseDouble(reduceScoreStr);
		}
		try {
			String[] staffNameArry = staffNameStr.split(",");
			if (staffNameArry != null && staffNameArry.length > 0) {
				for (String staffName : staffNameArry) {
					T9HrStaffIncentive staffIncentive = new T9HrStaffIncentive();
					staffIncentive.setStaffName(staffName);
					staffIncentive.setIncentiveItem(incentiveItem);
					staffIncentive.setIncentiveTime(incentiveTime);
					staffIncentive.setSalaryMonth(salaryMonth);
					staffIncentive.setIncentiveType(incentiveType);
					staffIncentive.setIncentiveAmount(incentiveAmount);
					staffIncentive.setRemark(remark);
					staffIncentive.setAddTime(T9Utility.parseTimeStamp());
					staffIncentive.setIncentiveDescription(incentiveDescription);
					staffIncentive.setCreateUserId(String.valueOf(person.getSeqId()));
					staffIncentive.setCreateDeptId(person.getDeptId());
					staffIncentive.setAddScore(addScore);
					staffIncentive.setReduceScore(reduceScore);
					staffIncentive.setYearScore(yearScore);
					if (attachFlag) {
						staffIncentive.setAttachmentId(attachmentIds);
						staffIncentive.setAttachmentName(attachmentNames);
					}
					orm.saveSingle(dbConn, staffIncentive);
					int maxSeqId = this.getMaxSeqId(dbConn);
					String incentiveTypeName = "";
					if ("1".equals(incentiveType.trim())) {
						incentiveTypeName = "奖励";
					}
					if ("2".equals(incentiveType.trim())) {
						incentiveTypeName = "惩罚";
					}
					T9MobileSms2Logic sbl = new T9MobileSms2Logic();
					String remindUrl = "/subsys/oa/hr/manage/staffIncentive/incentiveDetail.jsp?seqId=" + maxSeqId + "&openFlag=1&openWidth=860&openHeight=650";
					String smsContent = "请查看" + incentiveTypeName + "信息！";
					// 短信提醒
					if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
						this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), staffNameStr, "58", remindUrl, new Date());
					}
					// 手机提醒
					if (!T9Utility.isNullorEmpty(sms2Remind) && "1".equals(sms2Remind.trim())) {
						smsContent = "OA奖惩管理:" + incentiveTypeName + " " + getUserNameLogic(dbConn, staffNameStr) + " " + incentiveAmount;
						sbl.remindByMobileSms(dbConn, staffNameStr, person.getSeqId(), smsContent, new Date());
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 奖惩信息 通用列表
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getStaffincentiveJsonLogic(Connection dbConn, Map request, T9Person person) throws Exception {
		try {
	    String deptIdStr = getHrManagerPriv(dbConn, person);
		  
			String sql1 = " select  SEQ_ID, STAFF_NAME, INCENTIVE_ITEM, INCENTIVE_TIME, INCENTIVE_TYPE, INCENTIVE_AMOUNT "
					       + " from HR_STAFF_INCENTIVE " 
					       + " where CREATE_USER_ID = "+ person.getSeqId()
					       + " or CREATE_DEPT_ID in "+ deptIdStr
					       + " ORDER BY SEQ_ID desc ";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql1);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取单位员工用户名称
	 * 
	 * @param conn
	 * @param userIdStr
	 * @return
	 * @throws Exception
	 */
	public String getUserNameLogic(Connection conn, String userIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(userIdStr)) {
			userIdStr = "-1";
		}
		if (userIdStr.endsWith(",")) {
			userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
		}
		String result = "";
		String sql = " select USER_NAME from PERSON where SEQ_ID IN (" + userIdStr + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String toId = rs.getString(1);
				if (!"".equals(result)) {
					result += ",";
				}
				result += toId;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	/**
	 * 删除文件--wyw
	 * 
	 * @param dbConn
	 * @param seqIdStr
	 * @throws Exception
	 */
	public void deleteFileLogic(Connection dbConn, String seqIdStr, String filePath) throws Exception {
		T9ORM orm = new T9ORM();
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			seqIdStr = "";
		}
		try {
			String seqIdArry[] = seqIdStr.split(",");
			if (!"".equals(seqIdArry) && seqIdArry.length > 0) {
				for (String seqId : seqIdArry) {
					StringBuffer attIdBuffer = new StringBuffer();
					StringBuffer attNameBuffer = new StringBuffer();
					T9HrStaffIncentive staffIncentive = (T9HrStaffIncentive) orm.loadObjSingle(dbConn, T9HrStaffIncentive.class, Integer.parseInt(seqId));
					String attachmentId = T9Utility.null2Empty(staffIncentive.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(staffIncentive.getAttachmentName());
					attIdBuffer.append(attachmentId.trim());
					attNameBuffer.append(attachmentName.trim());
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
					orm.deleteSingle(dbConn, staffIncentive);
				}
			}
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
		if (T9Utility.isNullorEmpty(attachmentId) || T9Utility.isNullorEmpty(attachmentName)) {
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
	 * 得到附件的Id 兼老数据--wyw
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
	 * 获取详情
	 * 
	 * @param conn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9HrStaffIncentive getIncentiveDetailLogic(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9HrStaffIncentive) orm.loadObjSingle(conn, T9HrStaffIncentive.class, seqId);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 奖惩信息查询
	 * 
	 * @param dbConn
	 * @param request
	 * @param map
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String queryIncentiveListJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
	  
	  String deptIdStr = getHrManagerPriv(dbConn, person);
    
		String staffName = (String) map.get("staffName");
		String incentiveTime1 = (String) map.get("incentiveTime1");
		String incentiveTime2 = (String) map.get("incentiveTime2");
		String incentiveItem = (String) map.get("incentiveItem");
		String incentiveType = (String) map.get("incentiveType");
		String conditionStr = "";
		String sql1 = "";
		try {
			if (!T9Utility.isNullorEmpty(staffName)) {
				conditionStr = " and STAFF_NAME ='" + T9DBUtility.escapeLike(staffName) + "'";
			}
			if (!T9Utility.isNullorEmpty(incentiveItem)) {
				conditionStr += " and INCENTIVE_ITEM ='" + T9DBUtility.escapeLike(incentiveItem) + "'";
			}
			if (!T9Utility.isNullorEmpty(incentiveType)) {
				conditionStr += " and INCENTIVE_TYPE ='" + T9DBUtility.escapeLike(incentiveType) + "'";
			}
			if (!T9Utility.isNullorEmpty(incentiveTime1)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("INCENTIVE_TIME", incentiveTime1, ">=");
			}
			if (!T9Utility.isNullorEmpty(incentiveTime2)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("INCENTIVE_TIME", incentiveTime2, "<=");
			}
			sql1 = " select SEQ_ID, STAFF_NAME, INCENTIVE_ITEM, INCENTIVE_TIME, INCENTIVE_TYPE, INCENTIVE_AMOUNT"
					+ " from HR_STAFF_INCENTIVE " 
			    +	" where (CREATE_USER_ID = "+ person.getSeqId()
          + " or CREATE_DEPT_ID in "+ deptIdStr + ")" 
			    + conditionStr 
			    + " ORDER BY ADD_TIME desc";
			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql1);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取分值
	 * 
	 * @param dbConn
	 * @param year
	 * @param month
	 * @param userIdStr
	 * @return
	 * @throws Exception
	 */
	public double getScoreLogic(Connection dbConn, String year, String month, String userIdStr) throws Exception {
		if (T9Utility.isNullorEmpty(userIdStr)) {
			userIdStr = "0";
		}
		if (userIdStr.endsWith(",")) {
			userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
		}
		String ymd = "";
		if (T9Utility.isNullorEmpty(year)) {
			ymd = year + "-" + month + "-" + "07";
		} else {
			ymd = year + "-" + month + "-" + "07";
		}
		double addScore = 0;
		double reduceScore = 0;
		double score = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select ADD_SCORE,REDUCE_SCORE from HR_STAFF_INCENTIVE where STAFF_NAME in(" + userIdStr + ") and "
					+ T9DBUtility.getMonthFilter("INCENTIVE_TIME", T9Utility.parseDate(ymd));
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				addScore += rs.getDouble("ADD_SCORE");
				reduceScore += rs.getDouble("REDUCE_SCORE");
			}
			score = addScore - reduceScore;
			return score;
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}

	/**
	 * 编辑奖惩信息
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @throws Exception
	 */
	public void updateIncentiveInfoLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person) throws Exception {
		T9ORM orm = new T9ORM();
		String seqIdStr = fileForm.getParameter("seqId");
		String staffNameStr = fileForm.getParameter("staffName");
		String incentiveItem = fileForm.getParameter("incentiveItem");
		String incentiveTimeStr = fileForm.getParameter("incentiveTime");
		String salaryMonth = fileForm.getParameter("salaryMonth");
		String incentiveType = fileForm.getParameter("incentiveType");
		String incentiveAmountStr = fileForm.getParameter("incentiveAmount");
		String remark = fileForm.getParameter("remark");
		String incentiveDescription = fileForm.getParameter("incentiveDescription");
		String addScoreStr = fileForm.getParameter("addScore");
		String reduceScoreStr = fileForm.getParameter("reduceScore");
		String yearScoreStr = fileForm.getParameter("yearScore");
		String smsRemind = fileForm.getParameter("smsRemind");
		String sms2Remind = fileForm.getParameter("sms2Remind");

		Date incentiveTime = T9Utility.parseDate("yyyy-MM-dd", incentiveTimeStr);
		double incentiveAmount = 0;
		double yearScore = 0;
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		if (T9Utility.isNumber(incentiveAmountStr)) {
			incentiveAmount = Double.parseDouble(incentiveAmountStr);
		}
		if (T9Utility.isNumber(yearScoreStr)) {
			yearScore = Double.parseDouble(yearScoreStr);
		}
		Map<Object, Object> map = this.fileUploadLogic(fileForm, T9HrSetOtherAct.attachmentFolder);
		boolean attachFlag = (Boolean) map.get("attachFlag");
		String attachmentIds = (String) map.get("attachmentIds");
		String attachmentNames = (String) map.get("attachmentNames");
		double addScore = 0;
		double reduceScore = 0;
		if (T9Utility.isNumber(addScoreStr) && "1".equals(incentiveType.trim())) {
			addScore = Double.parseDouble(addScoreStr);
		}
		if (T9Utility.isNumber(reduceScoreStr) && "2".equals(incentiveType.trim())) {
			reduceScore = Double.parseDouble(reduceScoreStr);
		}
		
		try {
			T9HrStaffIncentive staffIncentive = (T9HrStaffIncentive) orm.loadObjSingle(dbConn, T9HrStaffIncentive.class, seqId);
			if (staffIncentive != null) {
				String dbAttachId = T9Utility.null2Empty(staffIncentive.getAttachmentId());
				String dbAttachName = T9Utility.null2Empty(staffIncentive.getAttachmentName());
				staffIncentive.setStaffName(staffNameStr);
				staffIncentive.setIncentiveItem(incentiveItem);
				staffIncentive.setIncentiveTime(incentiveTime);
				staffIncentive.setSalaryMonth(salaryMonth);
				staffIncentive.setIncentiveType(incentiveType);
				staffIncentive.setIncentiveAmount(incentiveAmount);
				staffIncentive.setRemark(remark);
				staffIncentive.setAddTime(T9Utility.parseTimeStamp());
				staffIncentive.setIncentiveDescription(incentiveDescription);
				staffIncentive.setCreateUserId(String.valueOf(person.getSeqId()));
				staffIncentive.setAddScore(addScore);
				staffIncentive.setReduceScore(reduceScore);
				staffIncentive.setYearScore(yearScore);
				if (attachFlag) {
					staffIncentive.setAttachmentId(dbAttachId.trim() + attachmentIds.trim());
					staffIncentive.setAttachmentName(dbAttachName.trim() + attachmentNames.trim());
				}
				orm.updateSingle(dbConn, staffIncentive);
				String incentiveTypeName = "";
				if ("1".equals(incentiveType.trim())) {
					incentiveTypeName = "奖励";
				}
				if ("2".equals(incentiveType.trim())) {
					incentiveTypeName = "惩罚";
				}
				T9MobileSms2Logic sbl = new T9MobileSms2Logic();
				String remindUrl = "/subsys/oa/hr/manage/staffIncentive/incentiveDetail.jsp?seqId=" + seqId + "&openFlag=1&openWidth=860&openHeight=650";
				String smsContent = "请查看" + incentiveTypeName + "信息！";
				// 短信提醒
				if (!T9Utility.isNullorEmpty(smsRemind) && "1".equals(smsRemind.trim())) {
					this.doSmsBackTime(dbConn, smsContent, person.getSeqId(), staffNameStr, "58", remindUrl, new Date());
				}
				// 手机提醒
				if (!T9Utility.isNullorEmpty(sms2Remind) && "1".equals(sms2Remind.trim())) {
					smsContent = "OA奖惩管理:" + incentiveTypeName + " " + getUserNameLogic(dbConn, staffNameStr) + " " + incentiveAmount;
					sbl.remindByMobileSms(dbConn, staffNameStr, person.getSeqId(), smsContent, new Date());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 短信提醒(带时间)
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
	public static void doSmsBackTime(Connection conn, String content, int fromId, String toId, String type, String remindUrl, Date sendDate)
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
	 * 获取最大的SeqId值
	 * 
	 * @param dbConn
	 * @return
	 */
	public int getMaxSeqId(Connection dbConn) {
		String sql = "select SEQ_ID from HR_STAFF_INCENTIVE where SEQ_ID=(select MAX(SEQ_ID) from HR_STAFF_INCENTIVE )";
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
	 * 返回两个日期的相隔月份
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public List<String> getDateValue(String startDateStr, String endDateStr) throws Exception {
		List<String> list = new ArrayList<String>();
		if (T9Utility.isNullorEmpty(endDateStr) && !T9Utility.isNullorEmpty(startDateStr)) {
			endDateStr = startDateStr;
			list.add(startDateStr);
			list.add(endDateStr);
			return list;
		} else if (T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
			startDateStr = endDateStr;
			list.add(startDateStr);
			list.add(endDateStr);
			return list;
		}
		try {
			if (!T9Utility.isNullorEmpty(startDateStr) && !T9Utility.isNullorEmpty(endDateStr)) {
				String startDateArry[] = startDateStr.split("-");
				String endDateArry[] = endDateStr.split("-");
				int startYear = Integer.parseInt(startDateArry[0]);
				int startMonth = Integer.parseInt(startDateArry[1]);
				int endMonth = Integer.parseInt(endDateArry[1]);
				String result = "";
				if (startMonth < endMonth) {
					list.add(startDateStr);
					int tmp = endMonth - startMonth;
					if (tmp <= 11) {
						for (int i = 1; i < tmp; i++) {
							int tmpMonth = startMonth + i;
							String str = "";
							if (tmpMonth < 10) {
								str = "0";
							}
							result = startYear + "-" + str + tmpMonth;
							list.add(result);
						}
					}
					list.add(endDateStr);
				} else if (startMonth == endMonth) {
					list.add(startDateStr);
					list.add(endDateStr);

				} else if (startMonth > endMonth) {
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return list;
	}

	/**
	 * 更新附件数据
	 * 
	 * @param dbConn
	 * @param seqIdStr
	 * @param attachId
	 * @return
	 * @throws Exception
	 */
	public boolean updateFloadFile(Connection dbConn, String seqIdStr, String attachId) throws Exception {
		boolean returnFlag = false;
		T9ORM orm = new T9ORM();
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		try {
			T9HrStaffIncentive staffIncentive = (T9HrStaffIncentive) orm.loadObjSingle(dbConn, T9HrStaffIncentive.class, seqId);
			String dbAttachId = "";
			String dbAttachName = "";
			if (staffIncentive != null) {
				dbAttachId = T9Utility.null2Empty(staffIncentive.getAttachmentId());
				dbAttachName = T9Utility.null2Empty(staffIncentive.getAttachmentName());
				String[] dbAttachIdArrays = dbAttachId.split(",");
				String[] dbAttachNameArrays = dbAttachName.split("\\*");
				String attachmentId = "";
				String attachmentName = "";
				if (!T9Utility.isNullorEmpty(attachId) && dbAttachIdArrays.length > 0) {
					for (int i = 0; i < dbAttachIdArrays.length; i++) {
						if (attachId.equals(dbAttachIdArrays[i])) {
							continue;
						}
						attachmentId += dbAttachIdArrays[i] + ",";
						attachmentName += dbAttachNameArrays[i] + "*";
					}
					staffIncentive.setAttachmentId(attachmentId.trim());
					staffIncentive.setAttachmentName(attachmentName.trim());
					orm.updateSingle(dbConn, staffIncentive);
					returnFlag = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return returnFlag;
	}

	public String getHrManagerPriv(Connection dbConn, T9Person person) throws SQLException{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = " select DEPT_ID from hr_manager where "+T9DBUtility.findInSet(String.valueOf(person.getSeqId()), "DEPT_HR_MANAGER");
    String deptIdStr = "";
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        deptIdStr = deptIdStr + rs.getInt("DEPT_ID")+",";
      }
      if(deptIdStr.length() > 0){
        deptIdStr = "("+deptIdStr.substring(0, deptIdStr.length()-1)+")";
      }
      else{
        deptIdStr = "(0)";
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return deptIdStr;
	}
}
