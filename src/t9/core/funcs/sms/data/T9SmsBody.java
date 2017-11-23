package t9.core.funcs.sms.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class T9SmsBody {
  private int seqId;
  private int fromId;
  private String smsType;
  private String content;
  private Date sendTime;
  private String remindUrl;
  private ArrayList<T9Sms> smslist;
  public ArrayList<T9Sms> getSmslist() {
    return smslist;
  }
  public void setSmslist(ArrayList<T9Sms> smslist) {
    this.smslist = smslist;
  }
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getFromId() {
    return fromId;
  }
  public void setFromId(int fromId) {
    this.fromId = fromId;
  }
  public String getSmsType() {
    return smsType;
  }
  public void setSmsType(String smsType) {
    this.smsType = smsType;
  }
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }
  public Date getSendTime() {
    return sendTime;
  }
  public void setSendTime(Date sendTime) {
    this.sendTime = sendTime;
  }
  public String getRemindUrl() {
    return remindUrl;
  }
  public void setRemindUrl(String remindUrl) {
    this.remindUrl = remindUrl;
  }
  public Iterator itSmsl(){
    if(this.smslist == null){
      this.smslist = new ArrayList<T9Sms>();
    }
    return this.smslist.iterator();
  }
  @Override
  public String toString(){
    return "T9SmsBody [content=" + content + ", fromId=" + fromId
        + ", remindUrl=" + remindUrl + ", sendTime=" + sendTime + ", seqId="
        + seqId + ", smsType=" + smsType + ", smslist=" + smslist + "]";
  }
  
}
