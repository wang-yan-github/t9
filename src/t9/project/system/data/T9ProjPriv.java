package t9.project.system.data;



public class T9ProjPriv{
	private int seqId;//主键
	private String privCode;//权限代码
	private String privUser;//权限用户名称
	private String privRole;//权限角色名称
	private String privDept;//权限部门名称
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public String getPrivCode() {
		return privCode;
	}
	public void setPrivCode(String privCode) {
		this.privCode = privCode;
	}
	public String getPrivUser() {
		return privUser;
	}
	public void setPrivUser(String privUser) {
		this.privUser = privUser;
	}
	public String getPrivRole() {
		return privRole;
	}
	public void setPrivRole(String privRole) {
		this.privRole = privRole;
	}
	public String getPrivDept() {
		return privDept;
	}
	public void setPrivDept(String privDept) {
		this.privDept = privDept;
	}
	
}