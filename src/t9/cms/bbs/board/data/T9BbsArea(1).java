package t9.cms.bbs.board.data;

import java.util.Date;



public class T9BbsArea{
  private int seqId;
  private String areaName;
  private String areaManager;
  private int areaIndex;
  private int createId;
  private Date createTime;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getAreaName() {
    return areaName;
  }
  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }
  public String getAreaManager() {
    return areaManager;
  }
  public void setAreaManager(String areaManager) {
    this.areaManager = areaManager;
  }
  public int getAreaIndex() {
    return areaIndex;
  }
  public void setAreaIndex(int areaIndex) {
    this.areaIndex = areaIndex;
  }
  public int getCreateId() {
    return createId;
  }
  public void setCreateId(int createId) {
    this.createId = createId;
  }
  public Date getCreateTime() {
    return createTime;
  }
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
  
  
}