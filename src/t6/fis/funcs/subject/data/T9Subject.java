package t6.fis.funcs.subject.data;

public class T9Subject {
  /** 流水号 **/
  private int seqId;
  /** 会计年度 **/
  private String acctYear;
  /** 科目编码 **/
  private String subNo;
  /** 科目名称 **/
  private String subName;
  /** 科目类型：1资产；2=负债；3=权益；4=成本；5=损益 **/
  private String typeNo;
  /** 科目性质：1=现金；2=银行；3=应收；4=应付；5=存货；9=其他 **/
  private String subSort;
  /** 借贷方向：0=借；1=贷 **/
  private String dcFlag;
  /** 是否明细：1=是；0=不是 **/
  private int detlFlag = 0;
  /** 辅助核算项目 **/
  private String acctItem;
  /** 币种 **/
  private int currId;
  /**是否多币种 **/
  private String multyCurr;
  /** 是否核算数量 **/
  private String qutyFlag;
  /** 是否预算控制 **/
  private String budgetFlag;
  /** 单位 **/
  private String acctUnit;
  /** 是否转会率价差 **/
  private String adjExch;
  /** 打印级次 **/
  private String printLevel;
  /** 是否使用 **/
  private String usedFlag;
  /** 助记码 **/
  private String memoCode;
  /** 附加信息类型 **/
  private String addInfSort;
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
  public String getSubNo() {
    return subNo;
  }
  public void setSubNo(String subNo) {
    this.subNo = subNo;
  }
  public String getSubName() {
    return subName;
  }
  public void setSubName(String subName) {
    this.subName = subName;
  }
  public String getTypeNo() {
    return typeNo;
  }
  public void setTypeNo(String typeNo) {
    this.typeNo = typeNo;
  }
  public String getSubSort() {
    return subSort;
  }
  public void setSubSort(String subSort) {
    this.subSort = subSort;
  }
  public String getDcFlag() {
    return dcFlag;
  }
  public void setDcFlag(String dcFlag) {
    this.dcFlag = dcFlag;
  }
  public int getDetlFlag() {
    return detlFlag;
  }
  public void setDetlFlag(int detlFlag) {
    this.detlFlag = detlFlag;
  }
  public String getAcctItem() {
    return acctItem;
  }
  public void setAcctItem(String acctItem) {
    this.acctItem = acctItem;
  }
  public int getCurrId() {
    return currId;
  }
  public void setCurrId(int currId) {
    this.currId = currId;
  }
  public String getMultyCurr() {
    return multyCurr;
  }
  public void setMultyCurr(String multyCurr) {
    this.multyCurr = multyCurr;
  }
  public String getQutyFlag() {
    return qutyFlag;
  }
  public void setQutyFlag(String qutyFlag) {
    this.qutyFlag = qutyFlag;
  }
  public String getBudgetFlag() {
    return budgetFlag;
  }
  public void setBudgetFlag(String budgetFlag) {
    this.budgetFlag = budgetFlag;
  }
  public String getAcctUnit() {
    return acctUnit;
  }
  public void setAcctUnit(String acctUnit) {
    this.acctUnit = acctUnit;
  }
  public String getAdjExch() {
    return adjExch;
  }
  public void setAdjExch(String adjExch) {
    this.adjExch = adjExch;
  }
  public String getPrintLevel() {
    return printLevel;
  }
  public void setPrintLevel(String printLevel) {
    this.printLevel = printLevel;
  }
  public String getUsedFlag() {
    return usedFlag;
  }
  public void setUsedFlag(String usedFlag) {
    this.usedFlag = usedFlag;
  }
  public String getMemoCode() {
    return memoCode;
  }
  public void setMemoCode(String memoCode) {
    this.memoCode = memoCode;
  }
  public String getAddInfSort() {
    return addInfSort;
  }
  public void setAddInfSort(String addInfSort) {
    this.addInfSort = addInfSort;
  }

}
