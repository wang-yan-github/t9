package t9.core.funcs.portal.data;

public class T9Port {
  private int seqId;
  private int type; // 手动添加 为 1
  private String fileName;
  private String nickname;
  private String remark;
  private int status;
  private String deptId;
  private String privId;
  private String userId;
  private int moduleNo;
  private String modulePos;
  private String viewType;
  private int moduleLines;
  private String moduleScroll;
  
  
  public String getNickname() {
    return nickname;
 }
  
 public void setNickname(String nickname) {
    this.nickname = nickname;
 }
 
 public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public int getModuleNo() {
    return moduleNo;
  }
  public void setModuleNo(int moduleNo) {
    this.moduleNo = moduleNo;
  }
  public String getModulePos() {
    return modulePos;
  }
  public void setModulePos(String modulePos) {
    this.modulePos = modulePos;
  }
  public String getViewType() {
    return viewType;
  }
  public void setViewType(String viewType) {
    this.viewType = viewType;
  }
  public int getModuleLines() {
    return moduleLines;
  }
  public void setModuleLines(int moduleLines) {
    this.moduleLines = moduleLines;
  }
  public String getModuleScroll() {
    return moduleScroll;
  }
  public void setModuleScroll(String moduleScroll) {
    this.moduleScroll = moduleScroll;
  }
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getStatus() {
    return status;
  }
  public void setStatus(int status) {
    this.status = status;
  }
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  public String getRemark() {
    return remark;
  }
  public void setRemark(String remark) {
    this.remark = remark;
  }
  public String getDeptId() {
    return deptId;
  }
  public void setDeptId(String deptId) {
    this.deptId = deptId;
  }
  public String getPrivId() {
    return privId;
  }
  public void setPrivId(String privId) {
    this.privId = privId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public boolean equals(Object o){
    return this.seqId == ((T9Port)o).getSeqId();
  }
}