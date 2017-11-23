package t9.core.esb.client.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class T9ExtDept implements Serializable {
  public T9ExtDept(String deptNo , String deptName, String esbUser,
      String deptParent, String deptDesc) {
    super();
    this.deptName = deptName;
    this.esbUser = esbUser;
    this.deptNo = deptNo;
    this.deptParent = deptParent;
    this.deptDesc = deptDesc;
    //this.syncState = syncState;
  }
  public T9ExtDept() {
    
  }
  public String deptId;
  
  public String getDeptId() {
    return deptId;
  }
  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getDeptName() {
    return deptName;
  }
  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }
  public String getEsbUser() {
    return esbUser;
  }
  public void setEsbUser(String esbUser) {
    this.esbUser = esbUser;
  }
  public String getDeptNo() {
    return deptNo;
  }
  public void setDeptNo(String deptNo) {
    this.deptNo = deptNo;
  }
  public String getDeptParent() {
    return deptParent;
  }
  public void setDeptParent(String deptParent) {
    this.deptParent = deptParent;
  }
  public String getDeptDesc() {
    return deptDesc;
  }
  public void setDeptDesc(String deptDesc) {
    this.deptDesc = deptDesc;
  }
  public String getSyncState() {
    return syncState;
  }
  public void setSyncState(String syncState) {
    this.syncState = syncState;
  }
  public int seqId;
  public String deptName;
  public String esbUser;
  public String deptNo;
  public String deptParent;
  public String deptDesc;
  public String syncState;
}
