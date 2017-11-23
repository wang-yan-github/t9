package t9.core.funcs.message.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import t9.core.funcs.sms.data.T9Sms;

public class T9MessageBody {
  private int seqId;
  private int fromId;
  private String messageType;
  private String content;
  private Date sendTime;
  private String remindUrl;
  private ArrayList<T9Message> messagelist;
  
  
  
  
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




  public String getMessageType() {
    return messageType;
  }




  public void setMessageType(String smsType) {
    this.messageType = smsType;
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




  public ArrayList<T9Message> getMessagelist() {
    return messagelist;
  }




  public void setMessagelist(ArrayList<T9Message> messagelist) {
    this.messagelist = messagelist;
  }

  public Iterator itMessagel(){
    if(this.messagelist == null){
      this.messagelist = new ArrayList<T9Message>();
    }
    return this.messagelist.iterator();
  }


  @Override
  public String toString(){
    return "T9MessageBody [content=" + content + ", fromId=" + fromId
        + ", remindUrl=" + remindUrl + ", sendTime=" + sendTime + ", seqId="
        + seqId + ", messageType=" + messageType + ", messagelist=" + messagelist + "]";
  }
  
}
