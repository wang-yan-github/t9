package t6.fis.funcs.voucher.data;

public class T9VouchOptsData {
  /** 流水号 **/
  private int seqId;
  /** 子系统编码 **/
  private String subsysNo;
  /** 凭证类别 **/
  private String vouchSort;
  /** 凭证流水号 **/
  private int vouchSeqId;
  /** 业务数据流水号 **/
  private int optsdataSeqId;
  /** 状态标记 **/
  private String stateFlag;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getSubsysNo() {
    return subsysNo;
  }
  public void setSubsysNo(String subsysNo) {
    this.subsysNo = subsysNo;
  }
  public String getVouchSort() {
    return vouchSort;
  }
  public void setVouchSort(String vouchSort) {
    this.vouchSort = vouchSort;
  }
  public int getVouchSeqId() {
    return vouchSeqId;
  }
  public void setVouchSeqId(int vouchSeqId) {
    this.vouchSeqId = vouchSeqId;
  }
  public int getOptsdataSeqId() {
    return optsdataSeqId;
  }
  public void setOptsdataSeqId(int optsdataSeqId) {
    this.optsdataSeqId = optsdataSeqId;
  }
  public String getStateFlag() {
    return stateFlag;
  }
  public void setStateFlag(String stateFlag) {
    this.stateFlag = stateFlag;
  }
}
