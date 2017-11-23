package t9.core.funcs.sms.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class T9Sms {
  private int seqId;
  private int toId;
  private String remindFlag;
  private String deleteFlag;
  private int bodySeqId;
  private Date remindTime;
  private ArrayList<T9SmsBody> smsBodyList = new ArrayList<T9SmsBody>();
  public ArrayList<T9SmsBody> getSmsBodyList(){
    return smsBodyList;
  }
  public void addSmsBodyList(T9SmsBody smsBody){
    if(smsBody != null){
      smsBodyList.add(smsBody);
    }
  }
  public void setSmsBodyList(ArrayList<T9SmsBody> smsBodyList){
    this.smsBodyList = smsBodyList;
  }
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
  public int getBodySeqId() {
    return bodySeqId;
  }
  public void setBodySeqId(int bodySeqId) {
    this.bodySeqId = bodySeqId;
  }
  public void setDeleteFlag(String deleteFlag) {
    this.deleteFlag = deleteFlag;
  }
  public Date getRemindTime() {
    return remindTime;
  }
  public void setRemindTime(Date remindTime) {
    this.remindTime = remindTime;
  }
  @Override
  public String toString(){
    return "T9Sms [bodySeqId=" + bodySeqId + ", deleteFlag=" + deleteFlag
        + ", remindFlag=" + remindFlag + ", remindTime=" + remindTime
        + ", seqId=" + seqId + ", smsBodyList=" + smsBodyList + ", toId="
        + toId + "]";
  }
  
}
