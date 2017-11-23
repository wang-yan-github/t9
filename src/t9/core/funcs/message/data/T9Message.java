package t9.core.funcs.message.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class T9Message {
  private int seqId;
  private int toId;
  private String remindFlag;
  private String deleteFlag;
  private int bodySeqId;
  private Date remindTime;
  private ArrayList<T9MessageBody> messageBodyList = new ArrayList<T9MessageBody>();
  
  
  
  public int getSeqId() {
    return seqId;
  }



  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }



  public int getToId() {
    return toId;
  }



  public void setToId(int toId) {
    this.toId = toId;
  }



  public String getRemindFlag() {
    return remindFlag;
  }



  public void setRemindFlag(String remindFlag) {
    this.remindFlag = remindFlag;
  }



  public String getDeleteFlag() {
    return deleteFlag;
  }



  public void setDeleteFlag(String deleteFlag) {
    this.deleteFlag = deleteFlag;
  }



  public int getBodySeqId() {
    return bodySeqId;
  }



  public void setBodySeqId(int bodySeqId) {
    this.bodySeqId = bodySeqId;
  }



  public Date getRemindTime() {
    return remindTime;
  }



  public void setRemindTime(Date remindTime) {
    this.remindTime = remindTime;
  }



  public ArrayList<T9MessageBody> getMessageBodyList() {
    return messageBodyList;
  }



  public void setMessageBodyList(ArrayList<T9MessageBody> messageBodyList) {
    this.messageBodyList = messageBodyList;
  }



  @Override
  public String toString(){
    return "T9Message [bodySeqId=" + bodySeqId + ", deleteFlag=" + deleteFlag
        + ", remindFlag=" + remindFlag + ", remindTime=" + remindTime
        + ", seqId=" + seqId + ", messageBodyList=" + messageBodyList + ", toId="
        + toId + "]";
  }
  
}
