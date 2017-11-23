package t9.project.task.data;

import java.util.Date;

public class T9ProjTask{
	
	private int seqId;//任务唯一标识ID
	private int projId;//项目唯一标识Id
	private String taskNo;//任务编号
	private String taskName;//任务名称
	private String taskDescription;//任务描述
	private String taskUser;//任务执行人
	private int taskMilestone;//里程碑标识
	private Date taskStartTime;//任务开始时间
	private Date taskEndTime;//任务结束时间
	private Date taskActEndTime;//任务实际结束时间
	private int taskTime;//任务周期（天）
	private String taskLevel;//任务级别
	private int preTask;//前置任务唯一标识ID
	private int taskPercentComplete;
	private String remark;//备注
	private String flowIdStr;//流程id 
	private String runIdStr;
	private int taskStatus;//任务状态
	private int taskConstrain;
	private int parentTask;//父任务ID
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
	public String getTaskNo() {
		return taskNo;
	}
	public void setTaskNo(String taskNo) {
		this.taskNo = taskNo;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getTaskUser() {
		return taskUser;
	}
	public void setTaskUser(String taskUser) {
		this.taskUser = taskUser;
	}
	public int getTaskMilestone() {
		return taskMilestone;
	}
	public void setTaskMilestone(int taskMilestone) {
		this.taskMilestone = taskMilestone;
	}
	public Date getTaskStartTime() {
		return taskStartTime;
	}
	public void setTaskStartTime(Date taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	public Date getTaskEndTime() {
		return taskEndTime;
	}
	public void setTaskEndTime(Date taskEndTime) {
		this.taskEndTime = taskEndTime;
	}
	public Date getTaskActEndTime() {
		return taskActEndTime;
	}
	public void setTaskActEndTime(Date taskActEndTime) {
		this.taskActEndTime = taskActEndTime;
	}
	public int getTaskTime() {
		return taskTime;
	}
	public void setTaskTime(int taskTime) {
		this.taskTime = taskTime;
	}
	public String getTaskLevel() {
		return taskLevel;
	}
	public void setTaskLevel(String taskLevel) {
		this.taskLevel = taskLevel;
	}
	public int getPreTask() {
		return preTask;
	}
	public void setPreTask(int preTask) {
		this.preTask = preTask;
	}
	public int getTaskPercentComplete() {
		return taskPercentComplete;
	}
	public void setTaskPercentComplete(int taskPercentComplete) {
		this.taskPercentComplete = taskPercentComplete;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFlowIdStr() {
		return flowIdStr;
	}
	public void setFlowIdStr(String flowIdStr) {
		this.flowIdStr = flowIdStr;
	}
	public String getRunIdStr() {
		return runIdStr;
	}
	public void setRunIdStr(String runIdStr) {
		this.runIdStr = runIdStr;
	}
	public int getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}
	public int getTaskConstrain() {
		return taskConstrain;
	}
	public void setTaskConstrain(int taskConstrain) {
		this.taskConstrain = taskConstrain;
	}
	public int getParentTask() {
		return parentTask;
	}
	public void setParentTask(int parentTask) {
		this.parentTask = parentTask;
	}
}