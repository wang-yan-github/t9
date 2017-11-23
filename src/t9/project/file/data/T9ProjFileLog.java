package t9.project.file.data;

import java.util.Date;


public class T9ProjFileLog{
	private int seqId;//唯一标识
	private int fileId;//文件ID
	private int action;
	private int userId;
	private Date actionTime;
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getActionTime() {
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
	
	
	
	
}