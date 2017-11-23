package t9.core.funcs.dept.data;

import java.util.ArrayList;
import java.util.Iterator;

public class T9DeptPosition {

  private int seqId = 0;
  private int deptSeqId = 0;
  private int positionNo = 0;
  private int sortNo = 0;
  private String positionName = null;
  private String positionDesc = null;
  private String positionFlag = null;
  
  private ArrayList<T9PositionPerson> personList = null;
  public Iterator itPerson(){
    if(personList == null){
      personList = new ArrayList<T9PositionPerson>();
    }
    return personList.iterator();
  }
  public ArrayList<T9PositionPerson> getPersonList() {
    return personList;
  }
  public void setPersonList(ArrayList<T9PositionPerson> personList) {
    this.personList = personList;
  }
  
  private ArrayList<T9PositionPriv> privList = null;
  public Iterator itPriv(){
    if(privList == null){
      privList = new ArrayList<T9PositionPriv>();
    }
    return privList.iterator();
  }
  public ArrayList<T9PositionPriv> getPrivList() {
    return privList;
  }
  public void setPrivList(ArrayList<T9PositionPriv> privList) {
    this.privList = privList;
  }
  
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getDeptSeqId() {
    return deptSeqId;
  }
  public void setDeptSeqId(int deptSeqId) {
    this.deptSeqId = deptSeqId;
  }
  public int getPositionNo() {
    return positionNo;
  }
  public void setPositionNo(int positionNo) {
    this.positionNo = positionNo;
  }
  public int getSortNo() {
    return sortNo;
  }
  public void setSortNo(int sortNo) {
    this.sortNo = sortNo;
  }
  public String getPositionName() {
    return positionName;
  }
  public void setPositionName(String positionName) {
    this.positionName = positionName;
  }
  public String getPositionDesc() {
    return positionDesc;
  }
  public void setPositionDesc(String positionDesc) {
    this.positionDesc = positionDesc;
  }
  public String getPositionFlag() {
    return positionFlag;
  }
  public void setPositionFlag(String positionFlag) {
    this.positionFlag = positionFlag;
  }
  @Override
  public String toString() {
    return "T9DeptPosition [deptSeqId=" + deptSeqId + ", personList="
        + personList + ", positionDesc=" + positionDesc + ", positionFlag="
        + positionFlag + ", positionName=" + positionName + ", positionNo="
        + positionNo + ", privList=" + privList + ", seqId=" + seqId
        + ", sortNo=" + sortNo + "]";
  }
}
