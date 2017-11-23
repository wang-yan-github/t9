package t6.fis.funcs.voucher.data;

import java.util.Date;

public class T9voucherImport {
  /**唯一ID 自动增长**/
  private int seqId;
  /**用户标识**/
  private String sessionId;
  /**凭证流水号**/
  private int voucSeqId;
  /**会计年度**/
  private String acctYear;
  /**凭证类型**/
  private int voucTypeId;
  /**会计期（由凭证日期确定）**/
  private int periodId;
  /**凭证号**/
  private int voucNo;
  /**凭证日期**/
  private float voucDate;
  /**金额（借方或贷方金额）**/
  private float amt;
  /**单据数**/
  private int appdxCnt;
  /**部门ID**/
  private int deptId;
  /**用户ID**/
  private int userId;
  /**制单人**/
  private String voucName;
  /**复核人**/
  private String checkName;
  /**记账人**/
  private String acctName;
  /**凭证来源**/
  private String voucSrc;
  /**插入时间**/
  private Date insertTime;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getSessionId() {
    return sessionId;
  }
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  public int getVoucSeqId() {
    return voucSeqId;
  }
  public void setVoucSeqId(int voucSeqId) {
    this.voucSeqId = voucSeqId;
  }
  public String getAcctYear() {
    return acctYear;
  }
  public void setAcctYear(String acctYear) {
    this.acctYear = acctYear;
  }
  public int getVoucTypeId() {
    return voucTypeId;
  }
  public void setVoucTypeId(int voucTypeId) {
    this.voucTypeId = voucTypeId;
  }
  public int getPeriodId() {
    return periodId;
  }
  public void setPeriodId(int periodId) {
    this.periodId = periodId;
  }
  public int getVoucNo() {
    return voucNo;
  }
  public void setVoucNo(int voucNo) {
    this.voucNo = voucNo;
  }
  public float getVoucDate() {
    return voucDate;
  }
  public void setVoucDate(float voucDate) {
    this.voucDate = voucDate;
  }
  public float getAmt() {
    return amt;
  }
  public void setAmt(float amt) {
    this.amt = amt;
  }
  public int getAppdxCnt() {
    return appdxCnt;
  }
  public void setAppdxCnt(int appdxCnt) {
    this.appdxCnt = appdxCnt;
  }
  public int getDeptId() {
    return deptId;
  }
  public void setDeptId(int deptId) {
    this.deptId = deptId;
  }
  public int getUserId() {
    return userId;
  }
  public void setUserId(int userId) {
    this.userId = userId;
  }
  public String getVoucName() {
    return voucName;
  }
  public void setVoucName(String voucName) {
    this.voucName = voucName;
  }
  public String getCheckName() {
    return checkName;
  }
  public void setCheckName(String checkName) {
    this.checkName = checkName;
  }
  public String getAcctName() {
    return acctName;
  }
  public void setAcctName(String acctName) {
    this.acctName = acctName;
  }
  public String getVoucSrc() {
    return voucSrc;
  }
  public void setVoucSrc(String voucSrc) {
    this.voucSrc = voucSrc;
  }
  public Date getInsertTime() {
    return insertTime;
  }
  public void setInsertTime(Date insertTime) {
    this.insertTime = insertTime;
  }
  

}
