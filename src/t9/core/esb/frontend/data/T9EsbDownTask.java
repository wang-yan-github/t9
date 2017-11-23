package t9.core.esb.frontend.data;

public class T9EsbDownTask {
   public int seqId;
   public String fileName;
   public String guid;
   public int status;
   public String fromId;
   public String optGuid;
   public String message;
   
  public String getOptGuid() {
    return optGuid;
  }
  public void setOptGuid(String optGuid) {
    this.optGuid = optGuid;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public String getFromId() {
    return fromId;
  }
  public void setFromId(String fromId) {
    this.fromId = fromId;
  }
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  public String getGuid() {
    return guid;
  }
  public void setGuid(String guid) {
    this.guid = guid;
  }
  public int getStatus() {
    return status;
  }
  public void setStatus(int status) {
    this.status = status;
  }
}
