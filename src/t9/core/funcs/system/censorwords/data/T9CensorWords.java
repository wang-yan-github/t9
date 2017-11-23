package t9.core.funcs.system.censorwords.data;

import java.util.ArrayList;
import java.util.Iterator;

import t9.core.data.T9DsField;

public class T9CensorWords {
  private int seqId;
  private int userId;
  private String find;
  private String replacement;
  
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  
  public int getUserId() {
    return userId;
  }
  public void setUserId(int userId) {
    this.userId = userId;
  }
  public String getFind() {
    return find;
  }
  public void setFind(String find) {
    this.find = find;
  }
  public String getReplacement() {
    return replacement;
  }
  public void setReplacement(String replacement) {
    this.replacement = replacement;
  }
}
