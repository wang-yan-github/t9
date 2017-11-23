package t6.fis.funcs.voucher.data;

import java.util.Date;

public class T9VoucDetlImport {
  /**唯一ID（自动增加列）**/
  private int seqId;
  private String sessionId;
  /**凭证号**/
  private int VoucNo;
  /**凭证序号**/
  private int voucSeqId;
  /**凭证摘要**/
  private String summary;
  /**记帐科目ID**/
  private int SubNoId;
  /**记帐科目编码**/
  private String SubNo;
  /**核算币别ID**/
  private int currId;
  /**借贷方向**/
  private String dcFlag;
  /**单价（数量类科目有效）**/
  private float price;
  /**数量（数量类科目有效）**/
  private float quty;
  /**本位币金额**/
  private float amt;
  /**原币金额（外币类科目有效**/
  private float fAmt;
  /**原币兑本位币汇率（外币类科目有效）**/
  private float bsFrate;
  /**结算日期（银行类科目有效）**/
  private String rpdate;
  /**结算方式（银行类科目有效）**/
  private String rpMode;
  /**结算单号（银行类科目有效）**/
  private String rpNo;
  /**辅助核算项目标识号1**/
  private String item1No;
  /**辅助核算项目标识号1代码ID**/
  private int  code1NoId;
  /**辅助核算项目标识号2**/
  private String item2No;
  /**辅助核算项目标识号2代码ID**/
  private int  code2NoId;
  /**辅助核算项目标识号3**/
  private String item3No;
  /**辅助核算项目标识号3代码ID**/
  private int  code3NoId;
  /**辅助核算项目标识号4**/
  private String item4No;
  /**辅助核算项目标识号4代码ID**/
  private int  code4NoId;
  /**辅助核算项目标识号5**/
  private String item5No;
  /**辅助核算项目标识号5代码ID**/
  private int  code5NoId;
  /**辅助核算项目标识号6**/
  private String item6No;
  /**辅助核算项目标识号6代码ID**/
  private int  code6NoId;
  /**插入时间**/
  private Date intertTime;
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getSessionId() {
    return sessionId;
  }
  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
  public int getVoucNo() {
    return VoucNo;
  }
  public void setVoucNo(int voucNo) {
    VoucNo = voucNo;
  }
  public int getVoucSeqId() {
    return voucSeqId;
  }
  public void setVoucSeqId(int voucSeqId) {
    this.voucSeqId = voucSeqId;
  }
  public String getSummary() {
    return summary;
  }
  public void setSummary(String summary) {
    this.summary = summary;
  }
  public int getSubNoId() {
    return SubNoId;
  }
  public void setSubNoId(int subNoId) {
    SubNoId = subNoId;
  }
  public String getSubNo() {
    return SubNo;
  }
  public void setSubNo(String subNo) {
    SubNo = subNo;
  }
  public int getCurrId() {
    return currId;
  }
  public void setCurrId(int currId) {
    this.currId = currId;
  }
  public String getDcFlag() {
    return dcFlag;
  }
  public void setDcFlag(String dcFlag) {
    this.dcFlag = dcFlag;
  }
  public float getPrice() {
    return price;
  }
  public void setPrice(float price) {
    this.price = price;
  }
  public float getQuty() {
    return quty;
  }
  public void setQuty(float quty) {
    this.quty = quty;
  }
  public float getAmt() {
    return amt;
  }
  public void setAmt(float amt) {
    this.amt = amt;
  }
  public float getfAmt() {
    return fAmt;
  }
  public void setfAmt(float fAmt) {
    this.fAmt = fAmt;
  }
  public float getBsFrate() {
    return bsFrate;
  }
  public void setBsFrate(float bsFrate) {
    this.bsFrate = bsFrate;
  }
  public String getRpdate() {
    return rpdate;
  }
  public void setRpdate(String rpdate) {
    this.rpdate = rpdate;
  }
  public String getRpMode() {
    return rpMode;
  }
  public void setRpMode(String rpMode) {
    this.rpMode = rpMode;
  }
  public String getRpNo() {
    return rpNo;
  }
  public void setRpNo(String rpNo) {
    this.rpNo = rpNo;
  }
  public String getItem1No() {
    return item1No;
  }
  public void setItem1No(String item1No) {
    this.item1No = item1No;
  }
  public int getCode1NoId() {
    return code1NoId;
  }
  public void setCode1NoId(int code1NoId) {
    this.code1NoId = code1NoId;
  }
  public String getItem2No() {
    return item2No;
  }
  public void setItem2No(String item2No) {
    this.item2No = item2No;
  }
  public int getCode2NoId() {
    return code2NoId;
  }
  public void setCode2NoId(int code2NoId) {
    this.code2NoId = code2NoId;
  }
  public String getItem3No() {
    return item3No;
  }
  public void setItem3No(String item3No) {
    this.item3No = item3No;
  }
  public int getCode3NoId() {
    return code3NoId;
  }
  public void setCode3NoId(int code3NoId) {
    this.code3NoId = code3NoId;
  }
  public String getItem4No() {
    return item4No;
  }
  public void setItem4No(String item4No) {
    this.item4No = item4No;
  }
  public int getCode4NoId() {
    return code4NoId;
  }
  public void setCode4NoId(int code4NoId) {
    this.code4NoId = code4NoId;
  }
  public String getItem5No() {
    return item5No;
  }
  public void setItem5No(String item5No) {
    this.item5No = item5No;
  }
  public int getCode5NoId() {
    return code5NoId;
  }
  public void setCode5NoId(int code5NoId) {
    this.code5NoId = code5NoId;
  }
  public String getItem6No() {
    return item6No;
  }
  public void setItem6No(String item6No) {
    this.item6No = item6No;
  }
  public int getCode6NoId() {
    return code6NoId;
  }
  public void setCode6NoId(int code6NoId) {
    this.code6NoId = code6NoId;
  }
  public Date getIntertTime() {
    return intertTime;
  }
  public void setIntertTime(Date intertTime) {
    this.intertTime = intertTime;
  }

}
