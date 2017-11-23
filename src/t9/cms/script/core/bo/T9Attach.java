package t9.cms.script.core.bo;

import java.util.Date;
/**
 * 
 * @author shenhua
 *
 */
public class T9Attach {

	String attName;
	String attId;
	String attType;
	Date crTime;
	
	public Date getCrTime() {
		return crTime;
	}
	public void setCrTime(Date crTime) {
		this.crTime = crTime;
	}
	public String getAttName() {
		return attName;
	}
	public void setAttName(String attName) {
		this.attName = attName;
	}
	public String getAttId() {
		return attId;
	}
	public void setAttId(String attId) {
		this.attId = attId;
	}
	public String getAttType() {
		return attType;
	}
	public void setAttType(String attType) {
		this.attType = attType;
	}
	
}
