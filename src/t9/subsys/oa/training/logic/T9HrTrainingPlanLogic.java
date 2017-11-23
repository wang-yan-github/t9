package t9.subsys.oa.training.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.training.data.T9HrTrainingPlan;

public class T9HrTrainingPlanLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.training.logic.T9HrTrainingPlanLogic.java");
	
	/**
	 * 获取下拉列表值--wyw
	 * @param dbConn
	 * @param parentNo
	 * @return
	 * @throws Exception
	 */
	public String getSelectOption(Connection dbConn, String classNo) throws Exception {
		String data = "";
		StringBuffer sb = new StringBuffer("[");
		classNo = T9Utility.null2Empty(classNo);
		String query = "select SEQ_ID,CLASS_CODE,CLASS_DESC from CODE_ITEM where CLASS_NO='" + classNo + "'";
		Statement stm1 = null;
		ResultSet rs1 = null;
		try {
			boolean isHave = false;
			stm1 = dbConn.createStatement();
			rs1 = stm1.executeQuery(query);
			while (rs1.next()) {
				int seqId = rs1.getInt("SEQ_ID");
				String codeNo = T9Utility.null2Empty(rs1.getString("CLASS_CODE"));
				String codeName = T9Utility.null2Empty(rs1.getString("CLASS_DESC"));
				sb.append("{");
				sb.append("seqId:\"" + seqId + "\"");
				sb.append(",value:\"" + T9Utility.encodeSpecial(codeNo) + "\"");
				sb.append(",text:\"" + T9Utility.encodeSpecial(codeName) + "\"");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stm1, rs1, null);
		}
		data = sb.toString();
		return data;
	}
	/**
	  * 获取系统代码表中对映的字段描述--wyw
	  * @param conn
	  * @param classCode
	  * @param classNo
	  * @return
	  * @throws Exception
	  */
	  public String getCodeNameLogic(Connection conn, String classCode, String classNo) throws Exception {
	    String result = "";
	    String sql = " select CLASS_DESC from CODE_ITEM where CLASS_CODE = '" + classCode + "' and CLASS_NO = '" + classNo + "'";
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
			result.put("fromFolderFlag", fromFolderFlag);
			result.put("forlderAttchId", forlderAttchId);
			result.put("forlderAttchName", forlderAttchName);

			result.put("uploadFlag", uploadFlag);
			result.put("uploadAttchId", uploadAttchId);
			result.put("uploadAttchName", uploadAttchName);
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 新建培训计划设值--wyw
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void setTrainingPlanInfoLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String forlderAttchId = (String) map.get("forlderAttchId");
			String forlderAttchName = (String) map.get("forlderAttchName");

			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String uploadAttchId = (String) map.get("uploadAttchId");
			String uploadAttchName = (String) map.get("uploadAttchName");

			T9HrTrainingPlan trainingPlan = new T9HrTrainingPlan();

			if (fromFolderFlag && uploadFlag) {
				trainingPlan.setAttachmentId(forlderAttchId.trim() + uploadAttchId.trim());
				trainingPlan.setAttachmentName(forlderAttchName.trim() + uploadAttchName.trim());
			} else if (fromFolderFlag) {
				trainingPlan.setAttachmentId(forlderAttchId.trim());
				trainingPlan.setAttachmentName(forlderAttchName.trim());
			} else if (uploadFlag) {
				trainingPlan.setAttachmentId(uploadAttchId.trim());
				trainingPlan.setAttachmentName(uploadAttchName.trim());
			}

			String tPlanNo = fileForm.getParameter("tPlanNo"); // 计划编号
			String tPlanName = fileForm.getParameter("tPlanName"); // 计划名称
			String tChannel = fileForm.getParameter("tChannel"); // 0
			String tBcwsStr = fileForm.getParameter("tBcws");
			String courseStartDate = fileForm.getParameter("courseStartDate"); // 2010-09-13
			String courseEndDate = fileForm.getParameter("courseEndDate"); // 2010-09-17
			String assessingOfficer = fileForm.getParameter("assessingOfficer"); // 2010-09-17
			String tJoinNumStr = fileForm.getParameter("tJoinNum"); // 20
			String tJoinDept = fileForm.getParameter("tJoinDept"); // 14,15,16
			String tJoinPerson = fileForm.getParameter("tJoinPerson"); // 1,581
			String tRequires = fileForm.getParameter("tRequires"); // 培训要求
			String tInstitutionName = fileForm.getParameter("tInstitutionName"); // 培训机构名称
			String tInstitutionInfo = fileForm.getParameter("tInstitutionInfo"); // 培训机构相关信息
			String tInstitutionContact = fileForm.getParameter("tInstitutionContact"); // 培训机构联系人
			String tInstituContactInfo = fileForm.getParameter("tInstituContactInfo");
			String tCourseName = fileForm.getParameter("tCourseName"); // 培训内容
			String sponsoringDepartment = fileForm.getParameter("sponsoringDepartment"); // 35
			String chargePerson = fileForm.getParameter("chargePerson"); // 1
			String courseHoursStr = fileForm.getParameter("courseHours"); // 总课时
			String tCourseTypes = fileForm.getParameter("tCourseTypes"); // 2
			String tDescription = fileForm.getParameter("tDescription"); // 培训说明
			String tAddress = fileForm.getParameter("tAddress"); // 培训地点
			String tContent = fileForm.getParameter("tContent"); // <p>培训内容</p>
			String remark = fileForm.getParameter("remark"); // 备注
			String smsRemind = fileForm.getParameter("smsRemind");
			String sms2Remind = fileForm.getParameter("sms2Remind");

			double tBcws = 0;
			if (T9Utility.isNumber(tBcwsStr)) {
				tBcws = Double.parseDouble(tBcwsStr);
			}
			int tJoinNum = 0;
			if (!T9Utility.isNullorEmpty(tJoinNumStr)) {
				tJoinNum = Integer.parseInt(tJoinNumStr);
			}
			int courseHours = 0;
			if (!T9Utility.isNullorEmpty(courseHoursStr)) {
				courseHours = Integer.parseInt(courseHoursStr);
			}
			trainingPlan.setTPlanNo(tPlanNo.trim());
			trainingPlan.setTPlanName(tPlanName.trim());
			trainingPlan.setTChannel(tChannel);
			trainingPlan.setTBcws(tBcws);
			trainingPlan.setCourseStartDate(T9Utility.parseDate(courseStartDate));
			trainingPlan.setCourseEndDate(T9Utility.parseDate(courseEndDate));
			trainingPlan.setAssessingOfficer(assessingOfficer);
			trainingPlan.setTJoinNum(tJoinNum);
			trainingPlan.setTJoinDept(tJoinDept);
			trainingPlan.setTJoinPerson(tJoinPerson);
			trainingPlan.setTRequires(tRequires);
			trainingPlan.setTInstitutionName(tInstitutionName);
			trainingPlan.setTInstitutionInfo(tInstitutionInfo);
			trainingPlan.setTInstitutionContact(tInstitutionContact);
			trainingPlan.setTInstituContactInfo(tInstituContactInfo);
			trainingPlan.setTCourseName(tCourseName);
			trainingPlan.setSponsoringDepartment(sponsoringDepartment);
			trainingPlan.setChargePerson(chargePerson);
			trainingPlan.setCourseHours(courseHours);
			trainingPlan.setTCourseTypes(tCourseTypes);
			trainingPlan.setTDescription(tDescription);
			trainingPlan.setTAddress(tAddress);
			trainingPlan.setTContent(tContent);
			trainingPlan.setRemark(remark);
			int assessingStatus = 0;
			trainingPlan.setAssessingStatus(assessingStatus);

			trainingPlan.setCreateUserId(String.valueOf(person.getSeqId()));
			trainingPlan.setCreateDeptId(person.getDeptId());
			trainingPlan.setAddTime(T9Utility.parseTimeStamp());
			orm.saveSingle(dbConn, trainingPlan);

			T9TrainingApprovalLogic approvalLogic = new T9TrainingApprovalLogic();
			T9MobileSms2Logic sbl = new T9MobileSms2Logic();
			String remindUrl = "";
			String smsContent = "";
			remindUrl = "/subsys/oa/training/approval/manage.jsp?assessingStatus=0&openFlag=1&openWidth=860&openHeight=650";
			smsContent = person.getUserName() + " 提交培训计划，请批示！";
			if ("1".equals(smsRemind)) {
				approvalLogic.doSmsBackTime(dbConn, smsContent, person.getSeqId(), assessingOfficer, "61", remindUrl, new java.util.Date());
			}
			//if ("1".equals(sms2Remind)) {
				//sbl.remindByMobileSms(dbConn, assessingOfficer, person.getSeqId(), smsContent, new java.util.Date());
			//}

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 更新培训计划设值--wyw
	 * 
	 * @param dbConn
	 * @param fileForm
	 * @param person
	 * @param map
	 * @throws Exception
	 */
	public void updateTrainingPlanInfoLogic(Connection dbConn, T9FileUploadForm fileForm, T9Person person, Map<Object, Object> map) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			boolean fromFolderFlag = (Boolean) map.get("fromFolderFlag");
			String forlderAttchId = (String) map.get("forlderAttchId");
			String forlderAttchName = (String) map.get("forlderAttchName");

			boolean uploadFlag = (Boolean) map.get("uploadFlag");
			String uploadAttchId = (String) map.get("uploadAttchId");
			String uploadAttchName = (String) map.get("uploadAttchName");

			String seqIdStr = fileForm.getParameter("seqId");
			int seqId = 0;
			if (!T9Utility.isNullorEmpty(seqIdStr)) {
				seqId = Integer.parseInt(seqIdStr);
			}

			T9HrTrainingPlan trainingPlan = (T9HrTrainingPlan) orm.loadObjSingle(dbConn, T9HrTrainingPlan.class, seqId);
			if (trainingPlan != null) {
				String dbAttachmentId = T9Utility.null2Empty(trainingPlan.getAttachmentId());
				String dbAttachmentName = T9Utility.null2Empty(trainingPlan.getAttachmentName());

				if (fromFolderFlag && uploadFlag) {
					trainingPlan.setAttachmentId(dbAttachmentId.trim() + forlderAttchId.trim() + uploadAttchId.trim());
					trainingPlan.setAttachmentName(dbAttachmentName.trim() + forlderAttchName.trim() + uploadAttchName.trim());
				} else if (fromFolderFlag) {
					trainingPlan.setAttachmentId(dbAttachmentId.trim() + forlderAttchId.trim());
					trainingPlan.setAttachmentName(dbAttachmentName.trim() + forlderAttchName.trim());
				} else if (uploadFlag) {
					trainingPlan.setAttachmentId(dbAttachmentId.trim() + uploadAttchId.trim());
					trainingPlan.setAttachmentName(dbAttachmentName.trim() + uploadAttchName.trim());
				}

				String tPlanNo = fileForm.getParameter("tPlanNo"); // 计划编号
				String tPlanName = fileForm.getParameter("tPlanName"); // 计划名称
				String tChannel = fileForm.getParameter("tChannel"); // 0
				String tBcwsStr = fileForm.getParameter("tBcws"); // 计划名称
				String courseStartDate = fileForm.getParameter("courseStartDate"); // 2010-09-13
				String courseEndDate = fileForm.getParameter("courseEndDate"); // 2010-09-17
				String assessingOfficer = fileForm.getParameter("assessingOfficer"); // 2010-09-17
				String tJoinNumStr = fileForm.getParameter("tJoinNum"); // 20
				String tJoinDept = fileForm.getParameter("tJoinDept"); // 14,15,16
				String tJoinPerson = fileForm.getParameter("tJoinPerson"); // 1,581
				String tRequires = fileForm.getParameter("tRequires"); // 培训要求
				String tInstitutionName = fileForm.getParameter("tInstitutionName"); // 培训机构名称
				String tInstitutionInfo = fileForm.getParameter("tInstitutionInfo"); // 培训机构相关信息
				String tInstitutionContact = fileForm.getParameter("tInstitutionContact");
				String tInstituContactInfo = fileForm.getParameter("tInstituContactInfo");
				String tCourseName = fileForm.getParameter("tCourseName"); // 培训内容
				String sponsoringDepartment = fileForm.getParameter("sponsoringDepartment"); // 35
				String chargePerson = fileForm.getParameter("chargePerson"); // 1
				String courseHoursStr = fileForm.getParameter("courseHours"); // 总课时
				String coursePay = fileForm.getParameter("coursePay"); // 实际费用
				String tCourseTypes = fileForm.getParameter("tCourseTypes"); // 2
				String tDescription = fileForm.getParameter("tDescription"); // 培训说明
				String tAddress = fileForm.getParameter("tAddress"); // 培训地点
				String tContent = fileForm.getParameter("tContent"); // <p>培训内容</p>
				String remark = fileForm.getParameter("remark"); // 备注
				String smsRemind = fileForm.getParameter("smsRemind");
				String sms2Remind = fileForm.getParameter("sms2Remind");

				double tBcws = 0;
				if (T9Utility.isNumber(tBcwsStr)) {
					tBcws = Double.parseDouble(tBcwsStr);
				}
				int tJoinNum = 0;
				if (!T9Utility.isNullorEmpty(tJoinNumStr)) {
					tJoinNum = Integer.parseInt(tJoinNumStr);
				}
				int courseHours = 0;
				if (!T9Utility.isNullorEmpty(courseHoursStr)) {
					courseHours = Integer.parseInt(courseHoursStr);
				}
				trainingPlan.setTPlanNo(tPlanNo);
				trainingPlan.setTPlanName(tPlanName);
				trainingPlan.setTChannel(tChannel);
				trainingPlan.setTBcws(tBcws);
				trainingPlan.setCourseStartDate(T9Utility.parseDate(courseStartDate));
				trainingPlan.setCourseEndDate(T9Utility.parseDate(courseEndDate));
				trainingPlan.setAssessingOfficer(assessingOfficer);
				trainingPlan.setTJoinNum(tJoinNum);
				trainingPlan.setTJoinDept(tJoinDept);
				trainingPlan.setTJoinPerson(tJoinPerson);
				trainingPlan.setTRequires(tRequires);
				trainingPlan.setTInstitutionName(tInstitutionName);
				trainingPlan.setTInstitutionInfo(tInstitutionInfo);
				trainingPlan.setTInstituContactInfo(tInstituContactInfo);
				trainingPlan.setTInstitutionContact(tInstitutionContact);
				trainingPlan.setTCourseName(tCourseName);
				trainingPlan.setSponsoringDepartment(sponsoringDepartment);
				trainingPlan.setChargePerson(chargePerson);
				trainingPlan.setCourseHours(courseHours);
				trainingPlan.setTCourseTypes(tCourseTypes);
				trainingPlan.setTDescription(tDescription);
				trainingPlan.setTAddress(tAddress);
				trainingPlan.setTContent(tContent);
				trainingPlan.setRemark(remark);

				trainingPlan.setCreateUserId(String.valueOf(person.getSeqId()));
				trainingPlan.setCreateDeptId(person.getDeptId());
				trainingPlan.setAddTime(T9Utility.parseTimeStamp());
				orm.updateSingle(dbConn, trainingPlan);
				
				T9TrainingApprovalLogic approvalLogic = new T9TrainingApprovalLogic();
				T9MobileSms2Logic sbl = new T9MobileSms2Logic();
				String remindUrl = "";
				String smsContent = "";
				remindUrl = "/subsys/oa/training/approval/manage.jsp?assessingStatus=0&openFlag=1&openWidth=860&openHeight=650";
				smsContent = person.getUserName() + " 提交培训计划，请批示！";
				if ("1".equals(smsRemind)) {
					approvalLogic.doSmsBackTime(dbConn, smsContent, person.getSeqId(), assessingOfficer, "61", remindUrl, new java.util.Date());
				}
				//if ("1".equals(sms2Remind)) {
					//sbl.remindByMobileSms(dbConn, assessingOfficer, person.getSeqId(), smsContent, new java.util.Date());
				//}
				
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 管理培训计划列表 -wyw
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getTrainingPlanListLogic(Connection dbConn, Map request, T9Person person) throws Exception {
	  String sql = "";
	  if(person.isAdminRole()){
	    sql = "select " 
	      + " SEQ_ID" 
	      + ", T_PLAN_NO" 
	      + ", T_PLAN_NAME" 
	      + ", T_CHANNEL" 
	      + ", T_COURSE_TYPES" 
	      + ", ASSESSING_STATUS"
	      + " from  HR_TRAINING_PLAN where 1=1 order by ADD_TIME desc";
	  }else{
	    sql = "select " 
        + " SEQ_ID" 
        + ", T_PLAN_NO" 
        + ", T_PLAN_NAME" 
        + ", T_CHANNEL" 
        + ", T_COURSE_TYPES" 
        + ", ASSESSING_STATUS"
        + " from  HR_TRAINING_PLAN where CREATE_USER_ID = '" + person.getSeqId() + "' order by ADD_TIME desc";
	  }
	
		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	/**
	 * 获取培训计划详细信息--wyw
	 * 
	 * @param conn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9HrTrainingPlan getTriningPlanDetailLogic(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9HrTrainingPlan) orm.loadObjSingle(conn, T9HrTrainingPlan.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	/**
	 * 更新附件数据--wyw
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
			T9HrTrainingPlan trainingPlan = (T9HrTrainingPlan) orm.loadObjSingle(dbConn, T9HrTrainingPlan.class, seqId);
			String dbAttachId = "";
			String dbAttachName = "";
			if (trainingPlan != null) {
				dbAttachId = T9Utility.null2Empty(trainingPlan.getAttachmentId());
				dbAttachName = T9Utility.null2Empty(trainingPlan.getAttachmentName());
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
					trainingPlan.setAttachmentId(attachmentId.trim());
					trainingPlan.setAttachmentName(attachmentName.trim());
					orm.updateSingle(dbConn, trainingPlan);
					returnFlag = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return returnFlag;
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

					T9HrTrainingPlan trainingPlan = (T9HrTrainingPlan) orm.loadObjSingle(dbConn, T9HrTrainingPlan.class, Integer.parseInt(seqId));
					String attachmentId = T9Utility.null2Empty(trainingPlan.getAttachmentId());
					String attachmentName = T9Utility.null2Empty(trainingPlan.getAttachmentName());
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
					orm.deleteSingle(dbConn, trainingPlan);
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
	 * 培训计划查询 --wyw
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
	public String queryTrainingPlanListJsonLogic(Connection dbConn, Map request, Map map, T9Person person) throws Exception {
		String tPlanName = (String) map.get("tPlanName");
		String tChannel = (String) map.get("tChannel");
		String tCourseTypes = (String) map.get("tCourseTypes");
		String tAddress = (String) map.get("tAddress");
		String tInstitutionName = (String) map.get("tInstitutionName");
		String courseStartDate1 = (String) map.get("courseStartDate1");
		String courseStartDate2 = (String) map.get("courseStartDate2");
		String conditionStr = "";
		String sql = "";
		try {
			if (!T9Utility.isNullorEmpty(tPlanName)) {
				conditionStr = " and T_PLAN_NAME ='" + T9DBUtility.escapeLike(tPlanName) + "'";
			}
			if (!T9Utility.isNullorEmpty(tChannel)) {
				conditionStr += " and T_CHANNEL ='" + T9DBUtility.escapeLike(tChannel) + "'";
			}
			if (!T9Utility.isNullorEmpty(tAddress)) {
				conditionStr += " and T_ADDRESS like '%" + T9DBUtility.escapeLike(tAddress) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(tInstitutionName)) {
				conditionStr += " and T_INSTITUTION_NAME like '%" + T9DBUtility.escapeLike(tInstitutionName) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(tCourseTypes)) {
				conditionStr += " and T_COURSE_TYPES ='" + T9DBUtility.escapeLike(tCourseTypes) + "'";
			}

			if (!T9Utility.isNullorEmpty(courseStartDate1)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("COURSE_START_DATE", courseStartDate1, ">=");
			}
			if (!T9Utility.isNullorEmpty(courseStartDate2)) {
				conditionStr += " and " + T9DBUtility.getDateFilter("COURSE_START_DATE", courseStartDate2, "<=");
			}
			if(person.isAdminRole()){
		    sql = "select " 
	        + " SEQ_ID" 
	        + ", T_PLAN_NO" 
	        + ", T_PLAN_NAME" 
	        + ", T_CHANNEL" 
	        + ", T_COURSE_TYPES" 
	        + ", ASSESSING_STATUS"
	        + " from  HR_TRAINING_PLAN where 1=1 " + conditionStr + " order by ADD_TIME desc";
			}else{
		    sql = "select " 
	        + " SEQ_ID" 
	        + ", T_PLAN_NO" 
	        + ", T_PLAN_NAME" 
	        + ", T_CHANNEL" 
	        + ", T_COURSE_TYPES" 
	        + ", ASSESSING_STATUS"
	        + " from  HR_TRAINING_PLAN where CREATE_USER_ID = '" + person.getSeqId() + "' " + conditionStr + " order by ADD_TIME desc";
			}

			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
			return pageDataList.toJson();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 检查数据库是否已经有该值
	 * @param dbConn
	 * @param checkPlanNo
	 * @param seqIdStr
	 * @return
	 * @throws Exception
	 */
	public int checkPlanNoLogic(Connection dbConn,String checkPlanNo,String seqIdStr) throws Exception{
		int num =0;
		PreparedStatement stmt = null;
    ResultSet rs = null;
    int seqId=0;
    if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		try {
			String queryStr = "select count(*) from HR_TRAINING_PLAN where T_PLAN_NO='" + checkPlanNo.replace("'", "''") + "'"; 
			if (!"".equals(seqIdStr) && seqId !=0) {
				queryStr = "select count(*) from HR_TRAINING_PLAN where T_PLAN_NO='" + checkPlanNo.replace("'", "''") + "' and SEQ_ID !="+ seqId; 
			}
      stmt = dbConn.prepareStatement(queryStr);
      rs = stmt.executeQuery();
      if (rs.next()) {
				num = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		return num;
	}
	
	
	

}
