package t9.core.funcs.system.code.data;

import java.util.ArrayList;
import java.util.Iterator;

import t9.core.data.T9DsField;

public class T9Code {
  private int seqId;
  private String codeNo;
  private String codeName;
  private String codeOrder;
  private String parentNo;
  private String codeFlag;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getCodeNo() {
    return codeNo;
  }
  public void setCodeNo(String codeNo) {
    this.codeNo = codeNo;
  }
  public String getCodeName() {
    return codeName;
  }
  public void setCodeName(String codeName) {
    this.codeName = codeName;
  }
  public String getCodeOrder() {
    return codeOrder;
  }
  public void setCodeOrder(String codeOrder) {
    this.codeOrder = codeOrder;
  }
  public String getParentNo() {
    return parentNo;
  }
  public void setParentNo(String parentNo) {
    this.parentNo = parentNo;
  }
  public String getCodeFlag() {
    return codeFlag;
  }
  public void setCodeFlag(String codeFlag) {
    this.codeFlag = codeFlag;
  }
}
