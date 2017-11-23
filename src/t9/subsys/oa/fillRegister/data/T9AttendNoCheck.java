package t9.subsys.oa.fillRegister.data;

import java.util.Date;

public class T9AttendNoCheck {
	private int seqId;
	private Date noCheckDate;
	private String proposer;
	
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	public Date getNoCheckDate() {
		return noCheckDate;
	}
	public void setNoCheckDate(Date noCheckDate) {
		this.noCheckDate = noCheckDate;
	}
	public String getProposer() {
		return proposer;
	}
	public void setProposer(String proposer) {
		this.proposer = proposer;
	}
	
	

}
