package t9.project.forum.data;

import java.util.Date;


public class T9ProjForum{
	private int seqId;//唯一标识
	private int projId;//项目Id
	private String userId;//用户Id
	private String subject;//标题
	private String content;//帖子内容
	private String attachmentId;//附件ID
	private String attachmentName;//附件名称
	private Date submitTime;//提交时间
	private int replyCont;//回复数量
	private int parent;//父帖子ID
	private Date oldSubmitTime;
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public int getReplyCont() {
		return replyCont;
	}
	public void setReplyCont(int replyCont) {
		this.replyCont = replyCont;
	}
	public int getParent() {
		return parent;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}
	public Date getOldSubmitTime() {
		return oldSubmitTime;
	}
	public void setOldSubmitTime(Date oldSubmitTime) {
		this.oldSubmitTime = oldSubmitTime;
	}
	
	
	
}