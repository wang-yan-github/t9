package t6.fis.funcs.trnsperiod.data;

public class T9TrnsDesc {
  /**唯一ID(自动增加)**/
  private int seqId;
  /**会计年度**/
  private String acctYear;
  /**功能编码**/
  private String trnsvNO;
  /**功能描述(用于菜单)**/
  private String trnsvDesc;
  /**凭证摘要**/
  private String voucDesc;
  /**转账方式（1-结转）**/
  private String trnsvMode;
  /**凭证类型**/
  private int  voucTypeId;
  /**F-帐务核算;A-固定资产**/
  private String trnsvSrt;
  /**下年度是否继续使用1=使用0=不使用**/
  private String nextYear;
  /**凭证来源编码**/
  private String vouchSrc;
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
  public String getTrnsvNO() {
    return trnsvNO;
  }
  public void setTrnsvNO(String trnsvNO) {
    this.trnsvNO = trnsvNO;
  }
  public String getTrnsvDesc() {
    return trnsvDesc;
  }
  public void setTrnsvDesc(String trnsvDesc) {
    this.trnsvDesc = trnsvDesc;
  }
  public String getVoucDesc() {
    return voucDesc;
  }
  public void setVoucDesc(String voucDesc) {
    this.voucDesc = voucDesc;
  }
  public String getTrnsvMode() {
    return trnsvMode;
  }
  public void setTrnsvMode(String trnsvMode) {
    this.trnsvMode = trnsvMode;
  }
  public int getVoucTypeId() {
    return voucTypeId;
  }
  public void setVoucTypeId(int voucTypeId) {
    this.voucTypeId = voucTypeId;
  }
  public String getTrnsvSrt() {
    return trnsvSrt;
  }
  public void setTrnsvSrt(String trnsvSrt) {
    this.trnsvSrt = trnsvSrt;
  }
  public String getNextYear() {
    return nextYear;
  }
  public void setNextYear(String nextYear) {
    this.nextYear = nextYear;
  }
  public String getVouchSrc() {
    return vouchSrc;
  }
  public void setVouchSrc(String vouchSrc) {
    this.vouchSrc = vouchSrc;
  }
 
}
