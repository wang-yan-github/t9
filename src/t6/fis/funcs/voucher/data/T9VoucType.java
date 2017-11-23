package t6.fis.funcs.voucher.data;

public class T9VoucType {
  /**流水号**/
  private int seqId;
  /**会计年度**/
  private String acctYear;
  /**类型名称**/
  private String typeName;
  /**借方科目及**/
  private String dSub1Set;
  /**贷方科目及**/
  private String cSub1Set;
  /**使用标记  0-使用  1-停用  9-不用**/
  private String usedFlag; /****/
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
  public String getTypeName() {
    return typeName;
  }
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
  public String getdSub1Set() {
    return dSub1Set;
  }
  public void setdSub1Set(String dSub1Set) {
    this.dSub1Set = dSub1Set;
  }
  public String getcSub1Set() {
    return cSub1Set;
  }
  public void setcSub1Set(String cSub1Set) {
    this.cSub1Set = cSub1Set;
  }
  public String getUsedFlag() {
    return usedFlag;
  }
  public void setUsedFlag(String usedFlag) {
    this.usedFlag = usedFlag;
  }

}
