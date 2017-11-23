package t9.project.bug.data;

import java.util.Date;


public class T9ProjBug{
	private int seqId;//问题唯一标识符
	private int projId;//项目id
	private int taskId;//任务Id
	private String dealUser;//处理问题用户
	private String beginUser; //问题提交用户
	private  String bugName;//问题名称
	private Date deadLine;//截止时间
	private Date creatTime;//问题创建时间
	private int level ;//问题严重级别
	private String bugDesc;//问题描述
	private int status;//问题所处状态
	private String attachmentId;//附件id
	private String attachmentName;//附件名称
	private String result;//处理结果
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public int getProjId() {
		return projId;
	}
	public void setProjId(int projId) {
		this.projId = projId;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getDealUser() {
		return dealUser;
	}
	public void setDealUser(String dealUser) {
		this.dealUser = dealUser;
	}
	public String getBeginUser() {
		return beginUser;
	}
	public void setBeginUser(String beginUser) {
		this.beginUser = beginUser;
	}
	public String getBugName() {
		return bugName;
	}
	public void setBugName(String bugName) {
		this.bugName = bugName;
	}
	public Date getDeadLine() {
		return deadLine;
	}
	public void setDeadLine(Date deadLine) {
		this.deadLine = deadLine;
	}
	public Date getCreatTime() {
		return creatTime;
	}
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getBugDesc() {
		return bugDesc;
	}
	public void setBugDesc(String bugDesc) {
		this.bugDesc = bugDesc;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
}