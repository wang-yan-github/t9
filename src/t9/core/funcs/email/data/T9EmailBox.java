package t9.core.funcs.email.data;

public class T9EmailBox{
  private int seqId;
  private int boxNo;
  private String boxName;
  private int userId;
  private int defaultCount = 10;
  public int getSeqId(){
    return seqId;
  }
  public void setSeqId(int seqId){
    this.seqId = seqId;
  }
  public int getBoxNo(){
    return boxNo;
  }
  public void setBoxNo(int boxNo){
    this.boxNo = boxNo;
  }
  public String getBoxName(){
    return boxName;
  }
  public void setBoxName(String boxName){
    this.boxName = boxName;
  }
  public int getUserId(){
    return userId;
  }
  public void setUserId(int userId){
    this.userId = userId;
  }
  public int getDefaultCount(){
    return defaultCount;
  }
  public void setDefaultCount(int defaultCount){
    this.defaultCount = defaultCount;
  }
  @Override
  public String toString(){
    return "T9EmailBox [boxName=" + boxName + ", boxOn=" + boxNo
        + ", defaultCount=" + defaultCount + ", seqId=" + seqId + ", userId="
        + userId + "]";
  }
  
}
