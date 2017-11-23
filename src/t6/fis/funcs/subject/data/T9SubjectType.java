package t6.fis.funcs.subject.data;

public class T9SubjectType {
  /** 流水号 **/
  private int seqId ;
  /** 会计年度 **/
  private String acctYear;
  /** 科目类型：比如：1资产；2=负债；3=权益；4=成本；5=损益 **/
  private String typeNo;
  private String typeName;
  private String typeSort;
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
  public String getTypeNo() {
    return typeNo;
  }
  public void setTypeNo(String typeNo) {
    this.typeNo = typeNo;
  }
  public String getTypeName() {
    return typeName;
  }
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
  public String getTypeSort() {
    return typeSort;
  }
  public void setTypeSort(String typeSort) {
    this.typeSort = typeSort;
  }

}
