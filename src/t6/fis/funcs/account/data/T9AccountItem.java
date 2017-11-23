package t6.fis.funcs.account.data;

public class T9AccountItem {
  /**唯一ID（自动增加列）**/
  private int seqId;
  /**会计年度**/
  private String acctYear;
  /**核算项目标识号**/
  private String itemNo ;
  /**核算项目描述**/
  private String itemDesc;
  /**数据表编码**/
  private String tableNo;
  /**编码ID字段名**/
  private String codeFldNo;
  /**关系编码字段（输入验证）**/
  private String nameFldNo;
  /**相关表关系标识号**/
  private String relaSet;
  /**相关表字段编码串**/
  private String relaFldNos;
  /**是否处理数量（暂不处理）1－处理 0－不处理**/
  private String qutyFlag;
  /**是否预算控制1－控制  0－不控制**/
  private String budgetFlag;
  /**项目分类（CURENCY-币种； DEPARTMT-部门；UNIT-单位；PERSON-人员； SUBJECT-科目； BANK-银行账号;PROJECT-项目…） **/
  private String itemSort;
  /**使用标记（0-使用；1-停用；9-不用）**/
  private String usedFlag;
  /**项目属性（S-系统表；O-其他）**/
  private String extFlag;
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
  public String getItemNo() {
    return itemNo;
  }
  public void setItemNo(String itemNo) {
    this.itemNo = itemNo;
  }
  public String getItemDesc() {
    return itemDesc;
  }
  public void setItemDesc(String itemDesc) {
    this.itemDesc = itemDesc;
  }
  public String getTableNo() {
    return tableNo;
  }
  public void setTableNo(String tableNo) {
    this.tableNo = tableNo;
  }
  public String getCodeFldNo() {
    return codeFldNo;
  }
  public void setCodeFldNo(String codeFldNo) {
    this.codeFldNo = codeFldNo;
  }
  public String getNameFldNo() {
    return nameFldNo;
  }
  public void setNameFldNo(String nameFldNo) {
    this.nameFldNo = nameFldNo;
  }
  public String getRelaSet() {
    return relaSet;
  }
  public void setRelaSet(String relaSet) {
    this.relaSet = relaSet;
  }
  public String getRelaFldNos() {
    return relaFldNos;
  }
  public void setRelaFldNos(String relaFldNos) {
    this.relaFldNos = relaFldNos;
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
  public String getItemSort() {
    return itemSort;
  }
  public void setItemSort(String itemSort) {
    this.itemSort = itemSort;
  }
  public String getUsedFlag() {
    return usedFlag;
  }
  public void setUsedFlag(String usedFlag) {
    this.usedFlag = usedFlag;
  }
  public String getExtFlag() {
    return extFlag;
  }
  public void setExtFlag(String extFlag) {
    this.extFlag = extFlag;
  }

}
