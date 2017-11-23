package t6.fis.funcs.account.data;

import java.util.Date;



public class T9AccountPeriod {
  /**唯一ID（自动增加列）**/
  private int seqId;
  /**会计年度**/
  private String periodYear;
  /**会计期序号（年度内1-12连续编号）**/
  private int preiodNum;
  /**起始日期**/
  private Date startDate;
  /**终止日期**/
  private Date endDate;
  /**结帐日期**/
  private Date closeDate;
  /**结帐人**/
  private String closeUser;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getPeriodYear() {
    return periodYear;
  }
  public void setPeriodYear(String periodYear) {
    this.periodYear = periodYear;
  }
  public int getPreiodNum() {
    return preiodNum;
  }
  public void setPreiodNum(int preiodNum) {
    this.preiodNum = preiodNum;
  }
  public Date getStartDate() {
    return startDate;
  }
  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
  public Date getEndDate() {
    return endDate;
  }
  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
  public Date getCloseDate() {
    return closeDate;
  }
  public void setCloseDate(Date closeDate) {
    this.closeDate = closeDate;
  }
  public String getCloseUser() {
    return closeUser;
  }
  public void setCloseUser(String closeUser) {
    this.closeUser = closeUser;
  }

}
