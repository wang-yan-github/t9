package t6.fis.funcs.trnsperiod.data;

public class T9TrnsvDetl {
  /**唯一ID(自动增长)**/
  private int  seqId;
  /**功能ID**/
  private int trnsvId;
  /**装出科目**/
  private String formSubNO;
  /**转出辅助核算项目**/
  private String formSubItem;
  /**装出类型(账务数据表中转账)**/
  private String fromAmtFlag;
  /**记账方向 (1-借;2-贷)**/
  private String formDcFlag;
  /**转入科目**/
  private String ToSubNo;
  /**转入辅助核算项目**/
  private String ToSubItem;
  /**记账方向（1-借;2-贷）**/
  private String ToDcFlag;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getTrnsvId() {
    return trnsvId;
  }
  public void setTrnsvId(int trnsvId) {
    this.trnsvId = trnsvId;
  }
  public String getFormSubNO() {
    return formSubNO;
  }
  public void setFormSubNO(String formSubNO) {
    this.formSubNO = formSubNO;
  }
  public String getFormSubItem() {
    return formSubItem;
  }
  public void setFormSubItem(String formSubItem) {
    this.formSubItem = formSubItem;
  }
  public String getFromAmtFlag() {
    return fromAmtFlag;
  }
  public void setFromAmtFlag(String fromAmtFlag) {
    this.fromAmtFlag = fromAmtFlag;
  }
  public String getFormDcFlag() {
    return formDcFlag;
  }
  public void setFormDcFlag(String formDcFlag) {
    this.formDcFlag = formDcFlag;
  }
  public String getToSubNo() {
    return ToSubNo;
  }
  public void setToSubNo(String toSubNo) {
    ToSubNo = toSubNo;
  }
  public String getToSubItem() {
    return ToSubItem;
  }
  public void setToSubItem(String toSubItem) {
    ToSubItem = toSubItem;
  }
  public String getToDcFlag() {
    return ToDcFlag;
  }
  public void setToDcFlag(String toDcFlag) {
    ToDcFlag = toDcFlag;
  }
 
}
