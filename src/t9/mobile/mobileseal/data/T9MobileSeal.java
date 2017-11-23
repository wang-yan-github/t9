package t9.mobile.mobileseal.data;

import java.util.Date;

public class T9MobileSeal {

	private int seqId;
	private String deviceList;
	private String sealData;
	private int deptId;
	private String sealName;
	private Date createTime;
	private String createUser;
	
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public String getDeviceList() {
		return deviceList;
	}
	public void setDeviceList(String deviceList) {
		this.deviceList = deviceList;
	}
	public String getSealData() {
		return sealData;
	}
	public void setSealData(String sealData) {
		this.sealData = sealData;
	}
	public int getDeptId() {
		return deptId;
	}
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	public String getSealName() {
		return sealName;
	}
	public void setSealName(String sealName) {
		this.sealName = sealName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	
}
