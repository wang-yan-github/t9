package t6.fis.funcs.voucher.data;

import org.apache.log4j.Logger;

public class T9Voucher {
  /** 流水号 **/
  private int seqId;
  /** 会计年度 **/
  private String acctYear;
  /** 凭证类型流水号 **/
  private int voucTypeId;  
  /** 会计期间 **/
  private int periodId;
  /** 凭证字 **/
  private int vouchWordSeqId;
  /** 凭证号 **/
  private int vouchWordNo;
  /** 凭证编码，序号 **/
  private int voucNo;
  /** 凭证日期 **/
  private String voucDate ;
  /** 凭证金额 **/
  private double amt;
  /** 附单据数 **/
  private int appdxCnt ;
   /** 部门ID **/
  private int deptId ;
  /** 用户ID **/
  private int userId;
  /** 制单人 **/
  private String voucName;
  /** 签字人 **/
  private String signName;
  /** 复核人 **/
  private String checkName;
  /** 记帐人 **/
  private String acctName ;
  /** 凭证来源 **/
  private String voucSrc ;
  /** 有效标志 **/
  private String validFlag ;
  /** 凭证状态 **/
  private int vouchState ;
  /** 被红冲的凭证流水号 **/
  private int srcSeqId ;
  /** **/
  private String formAcset;
  /**编号 **/
  private String formVouchId;
  /**导入的日期 **/
  private String importDate;
  /**导入者**/
  private String importUser;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
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
  public int getVouchWordSeqId() {
    return vouchWordSeqId;
  }
  public void setVouchWordSeqId(int vouchWordSeqId) {
    this.vouchWordSeqId = vouchWordSeqId;
  }
  public int getVouchWordNo() {
    return vouchWordNo;
  }
  public void setVouchWordNo(int vouchWordNo) {
    this.vouchWordNo = vouchWordNo;
  }
  public int getVoucNo() {
    return voucNo;
  }
  public void setVoucNo(int voucNo) {
    this.voucNo = voucNo;
  }
  public String getVoucDate() {
    return voucDate;
  }
  public void setVoucDate(String voucDate) {
    this.voucDate = voucDate;
  }
  public double getAmt() {
    return amt;
  }
  public void setAmt(double amt) {
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
  public String getSignName() {
    return signName;
  }
  public void setSignName(String signName) {
    this.signName = signName;
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
  public String getValidFlag() {
    return validFlag;
  }
  public void setValidFlag(String validFlag) {
    this.validFlag = validFlag;
  }
  public int getVouchState() {
    return vouchState;
  }
  public void setVouchState(int vouchState) {
    this.vouchState = vouchState;
  }
  public int getSrcSeqId() {
    return srcSeqId;
  }
  public void setSrcSeqId(int srcSeqId) {
    this.srcSeqId = srcSeqId;
  }
  public String getFormAcset() {
    return formAcset;
  }
  public void setFormAcset(String formAcset) {
    this.formAcset = formAcset;
  }
  public String getFormVouchId() {
    return formVouchId;
  }
  public void setFormVouchId(String formVouchId) {
    this.formVouchId = formVouchId;
  }
  public String getImportDate() {
    return importDate;
  }
  public void setImportDate(String importDate) {
    this.importDate = importDate;
  }
  public String getImportUser() {
    return importUser;
  }
  public void setImportUser(String importUser) {
    this.importUser = importUser;
  }
  
  
  

}
