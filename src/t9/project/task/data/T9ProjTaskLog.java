package t9.project.task.data;

import java.util.Date;


public class T9ProjTaskLog{
	private int seqId;//任务日志唯一标识
	private int logType;//日志类型标识
	private int taskId;//任务ID
	private String logUser;//用户id
	private String logContent;//日志内容
	private Date logTime;//日志时间
	private   int percent;
	private String attachmentId;//附件ID
	private String attachmentName;//附件名称
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public int getLogType() {
		return logType;
	}
	public void setLogType(int logType) {
		this.logType = logType;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public String getLogUser() {
		return logUser;
	}
	public void setLogUser(String logUser) {
		this.logUser = logUser;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public Date getLogTime() {
		return logTime;
	}
	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(int percent) {
		this.percent = percent;
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