package t9.core.funcs.system.interfaces.data;


import java.util.ArrayList;
import java.util.Iterator;

import t9.core.data.T9DsField;

public class T9SysPara {
  private int seqId;
  private String paraName;
  private String paraValue;
  
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getParaName() {
    return paraName;
  }
  public void setParaName(String paraName) {
    this.paraName = paraName;
  }
  public String getParaValue() {
    return paraValue;
  }
  public void setParaValue(String paraValue) {
    this.paraValue = paraValue;
  }
}
