package t9.project.file.data;




public class T9ProjFileSort{
	private int seqId;//唯一标识字段
	private int sortParent;//父节点ID
	private int projId;//项目Id
	private String sortNo;//排序号
	private String sortName;//目录名称
	private String sortType;//目录类型
	private String viewUser;//查看权限
	private String newUser;//先建权限
	private String manageUser;//管理权限
	private String modifyUser;//修改权限
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public int getSortParent() {
		return sortParent;
	}
	public void setSortParent(int sortParent) {
		this.sortParent = sortParent;
	}
	public int getProjId() {
		return projId;
	}
	public void setProjId(int projId) {
		this.projId = projId;
	}
	public String getSortNo() {
		return sortNo;
	}
	public void setSortNo(String sortNo) {
		this.sortNo = sortNo;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getSortType() {
		return sortType;
	}
	public void setSortType(String sortType) {
		this.sortType = sortType;
	}
	public String getViewUser() {
		return viewUser;
	}
	public void setViewUser(String viewUser) {
		this.viewUser = viewUser;
	}
	public String getNewUser() {
		return newUser;
	}
	public void setNewUser(String newUser) {
		this.newUser = newUser;
	}
	public String getManageUser() {
		return manageUser;
	}
	public void setManageUser(String manageUser) {
		this.manageUser = manageUser;
	}
	public String getModifyUser() {
		return modifyUser;
	}
	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}
	
	
	
	
}