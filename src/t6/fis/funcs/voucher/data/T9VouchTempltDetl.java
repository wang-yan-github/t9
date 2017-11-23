package t6.fis.funcs.voucher.data;

public class T9VouchTempltDetl {
  /**流水号**/
  private int seqId;
  /**模板流水号**/
  private int templtSeqId;
  /**是否合并相同分录1=合并；0部合并**/
  private String mergeFlag;
  /**摘要，可能含公式**/
  private String summary;
  /**
   * 取得科目表达式
   * 指定单一科目
   * 资金来源科目：现金/银行
   * 按字段匹配科目，针对一张表的业务
   * 按项目号匹配科目，针对明细表的业务
   * **/
  private String subExpr;
  /**取得币种表达式**/
  private String currExp;
  /**取得金额表达式
    *取一张表的某个字段
    *取多个明细表的金额
    *按其他分录计算取得
  **/
  private String amtExpr;
  /**取得外币金额表达式**/
  private String amtFcExpr;
  /**取得当时汇率表达式**/
  private String exchRateExpr;
  /**计算分录标记 0=非计算;1=金额由其他行计算得来**/
  private String computFlag;
  /**这里的分录都需要理解为分录类别，
   * 因为一条分录规则对应的是一个分录的集合，
   * 这个集会很可能只有一个元素
   * **/
  private String entryGroupNo;
  /**借贷方向0=借；1=贷**/
  private String dcFlag;
  /**数据来源关键字**/
  private String dataSourceKey;
  /**数据源ID**/
  private int  DataSourceSeqId;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getTempltSeqId() {
    return templtSeqId;
  }
  public void setTempltSeqId(int templtSeqId) {
    this.templtSeqId = templtSeqId;
  }
  public String getMergeFlag() {
    return mergeFlag;
  }
  public void setMergeFlag(String mergeFlag) {
    this.mergeFlag = mergeFlag;
  }
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }
  public String getSubExpr() {
    return subExpr;
  }
  public void setSubExpr(String subExpr) {
    this.subExpr = subExpr;
  }
  public String getCurrExp() {
    return currExp;
  }
  public void setCurrExp(String currExp) {
    this.currExp = currExp;
  }
  public String getAmtExpr() {
    return amtExpr;
  }
  public void setAmtExpr(String amtExpr) {
    this.amtExpr = amtExpr;
  }
  public String getAmtFcExpr() {
    return amtFcExpr;
  }
  public void setAmtFcExpr(String amtFcExpr) {
    this.amtFcExpr = amtFcExpr;
  }
  public String getExchRateExpr() {
    return exchRateExpr;
  }
  public void setExchRateExpr(String exchRateExpr) {
    this.exchRateExpr = exchRateExpr;
  }
  public String getComputFlag() {
    return computFlag;
  }
  public void setComputFlag(String computFlag) {
    this.computFlag = computFlag;
  }
  public String getEntryGroupNo() {
    return entryGroupNo;
  }
  public void setEntryGroupNo(String entryGroupNo) {
    this.entryGroupNo = entryGroupNo;
  }
  public String getDcFlag() {
    return dcFlag;
  }
  public void setDcFlag(String dcFlag) {
    this.dcFlag = dcFlag;
  }
  public String getDataSourceKey() {
    return dataSourceKey;
  }
  public void setDataSourceKey(String dataSourceKey) {
    this.dataSourceKey = dataSourceKey;
  }
  public int getDataSourceSeqId() {
    return DataSourceSeqId;
  }
  public void setDataSourceSeqId(int dataSourceSeqId) {
    DataSourceSeqId = dataSourceSeqId;
  }
  

}
