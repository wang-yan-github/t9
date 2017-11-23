package t6.fis.funcs.voucher.data;

import java.util.Date;

public class T9VersionLog {
  /**流水号**/
  private int seqId;
  /**版本号**/
  private int versionNo;
  /**版本排序**/
  private String verSionDesc;
  /**修改日期**/
  private Date updateTime;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getVersionNo() {
    return versionNo;
  }
  public void setVersionNo(int versionNo) {
    this.versionNo = versionNo;
  }
  public String getVerSionDesc() {
    return verSionDesc;
  }
  public void setVerSionDesc(String verSionDesc) {
    this.verSionDesc = verSionDesc;
  }
  public Date getUpdateTime() {
    return updateTime;
  }
  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

}
