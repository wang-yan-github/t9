package t9.mobile.mobileseal.data;

import java.util.Date;

public class T9MobileDevice {
	
	private int seqId;
	private int uid;
	private Date submitTime;
	private int deviceType;
	private String deviceInfo;
	private String md5Check;
	private String deviceName;
	
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Date getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}
	public int getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}
	public String getDeviceInfo() {
		return deviceInfo;
	}
	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
	public String getMd5Check() {
		return md5Check;
	}
	public void setMd5Check(String md5Check) {
		this.md5Check = md5Check;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	
}
