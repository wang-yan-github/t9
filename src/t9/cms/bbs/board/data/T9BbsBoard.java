package t9.cms.bbs.board.data;

import java.util.Date;



public class T9BbsBoard{
  private int seqId;
  private int areaId;
  private String boardName;
  private String boardAbstract;
  private String boardManager;
  private int lockDay;
  private int anonymity;
  private int isCheck;
  private String boardIndex;
  private int createId;
  private Date createTime;
  private int parentBoardId;
  private String dept;
  private String role;
  private String userIds;
  private String imageUrl;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public int getAreaId() {
    return areaId;
  }
  public void setAreaId(int areaId) {
    this.areaId = areaId;
  }
  public String getBoardName() {
    return boardName;
  }
  public void setBoardName(String boardName) {
    this.boardName = boardName;
  }
  public String getBoardAbstract() {
    return boardAbstract;
  }
  public void setBoardAbstract(String boardAbstract) {
    this.boardAbstract = boardAbstract;
  }
  public String getBoardManager() {
    return boardManager;
  }
  public void setBoardManager(String boardManager) {
    this.boardManager = boardManager;
  }
  public int getLockDay() {
    return lockDay;
  }
  public void setLockDay(int lockDay) {
    this.lockDay = lockDay;
  }
  public int getAnonymity() {
    return anonymity;
  }
  public void setAnonymity(int anonymity) {
    this.anonymity = anonymity;
  }
  public int getCreateId() {
    return createId;
  }
  public void setCreateId(int createId) {
    this.createId = createId;
  }
  public Date getCreateTime() {
    return createTime;
  }
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
  public int getParentBoardId() {
    return parentBoardId;
  }
  public void setParentBoardId(int parentBoardId) {
    this.parentBoardId = parentBoardId;
  }
  public String getDept() {
    return dept;
  }
  public void setDept(String dept) {
    this.dept = dept;
  }
  public String getRole() {
    return role;
  }
  public void setRole(String role) {
    this.role = role;
  }
  public String getUserIds() {
    return userIds;
  }
  public void setUserIds(String userIds) {
    this.userIds = userIds;
  }
  public String getBoardIndex() {
    return boardIndex;
  }
  public void setBoardIndex(String boardIndex) {
    this.boardIndex = boardIndex;
  }
  public int getIsCheck() {
    return isCheck;
  }
  public void setIsCheck(int isCheck) {
    this.isCheck = isCheck;
  }
  public String getImageUrl() {
    return imageUrl;
  }
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
	 
}