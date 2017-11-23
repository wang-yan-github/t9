package t6.fis.funcs.summary.data;

public class T9Summary {
  /**唯一ID（自动增加列）**/
  private int seqId;
  /**助记码**/
  private String memoCode;
  /**摘要**/
  private String summDesc;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getMemoCode() {
    return memoCode;
  }
  public void setMemoCode(String memoCode) {
    this.memoCode = memoCode;
  }
  public String getSummDesc() {
    return summDesc;
  }
  public void setSummDesc(String summDesc) {
    this.summDesc = summDesc;
  }

}
