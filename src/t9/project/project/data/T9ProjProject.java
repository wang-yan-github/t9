package t9.project.project.data;

import java.util.Date;


public class T9ProjProject{
	private int seqId;//项目id
	private String projName;//项目名称
	private String projNum;//项目编号
	private String projDescription;//项目描述
	private int projType;//项目类型
	private String projDept;//项目参与部门
	private Date projUpdateTime ;//项目更新时间
	private Date projStartTime;//项目开始时间
	private Date projEndTime;//项目结束时间
	private Date projActEndTime;//项目实际结束时间
	private String projOwner;//项目创建人
	private String projLeader;//项目负责人
	private String projViwer;//项目查看人员
	private String projUser;//项目参与人
	private String projPriv; //项目人员角色
	private String projManager ;//项目审批人
	private String projComment ;
	private int projStatus;//项目状态
	private int projPercentComplete;
	private String costType;
	private String costMoney;
	private String approveLog; //审批日志内容
	private String attachmentId;//附件id;
	private String attachmentName;//附件名称
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public String getProjName() {
		return projName;
	}
	public void setProjName(String projName) {
		this.projName = projName;
	}
	public String getProjNum() {
		return projNum;
	}
	public void setProjNum(String projNum) {
		this.projNum = projNum;
	}
	public String getProjDescription() {
		return projDescription;
	}
	public void setProjDescription(String projDescription) {
		this.projDescription = projDescription;
	}
	public int getProjType() {
		return projType;
	}
	public void setProjType(int projType) {
		this.projType = projType;
	}
	public String getProjDept() {
		return projDept;
	}
	public void setProjDept(String projDept) {
		this.projDept = projDept;
	}
	public Date getProjUpdateTime() {
		return projUpdateTime;
	}
	public void setProjUpdateTime(Date projUpdateTime) {
		this.projUpdateTime = projUpdateTime;
	}
	public Date getProjStartTime() {
		return projStartTime;
	}
	public void setProjStartTime(Date projStartTime) {
		this.projStartTime = projStartTime;
	}
	public Date getProjEndTime() {
		return projEndTime;
	}
	public void setProjEndTime(Date projEndTime) {
		this.projEndTime = projEndTime;
	}
	public Date getProjActEndTime() {
		return projActEndTime;
	}
	public void setProjActEndTime(Date projActEndTime) {
		this.projActEndTime = projActEndTime;
	}
	public String getProjOwner() {
		return projOwner;
	}
	public void setProjOwner(String projOwner) {
		this.projOwner = projOwner;
	}
	public String getProjLeader() {
		return projLeader;
	}
	public void setProjLeader(String projLeader) {
		this.projLeader = projLeader;
	}
	public String getProjViwer() {
		return projViwer;
	}
	public void setProjViwer(String projViwer) {
		this.projViwer = projViwer;
	}
	public String getProjUser() {
		return projUser;
	}
	public void setProjUser(String projUser) {
		this.projUser = projUser;
	}
	public String getProjPriv() {
		return projPriv;
	}
	public void setProjPriv(String projPriv) {
		this.projPriv = projPriv;
	}
	public String getProjManager() {
		return projManager;
	}
	public void setProjManager(String projManager) {
		this.projManager = projManager;
	}
	public String getProjComment() {
		return projComment;
	}
	public void setProjComment(String projComment) {
		this.projComment = projComment;
	}
	public int getProjStatus() {
		return projStatus;
	}
	public void setProjStatus(int projStatus) {
		this.projStatus = projStatus;
	}
	public int getProjPercentComplete() {
		return projPercentComplete;
	}
	public void setProjPercentComplete(int projPercentComplete) {
		this.projPercentComplete = projPercentComplete;
	}
	public String getCostType() {
		return costType;
	}
	public void setCostType(String costType) {
		this.costType = costType;
	}
	public String getCostMoney() {
		return costMoney;
	}
	public void setCostMoney(String costMoney) {
		this.costMoney = costMoney;
	}
	public String getApproveLog() {
		return approveLog;
	}
	public void setApproveLog(String approveLog) {
		this.approveLog = approveLog;
	}
	public String getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getAttachmentName() {
		return attachmentName;
	}
	public void setAttachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
	}
}