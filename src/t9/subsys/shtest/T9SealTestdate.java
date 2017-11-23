package t9.subsys.shtest;

public class T9SealTestdate {
 private int seqId;
 private String title;
 private String content;
 private String yijian;
 private String zhang;
 private String fileno;
 private String uid;
 private String md5;

public int getSeqId() {
	return seqId;
}
public void setSeqId(int seqId) {
	this.seqId = seqId;
}
public String getUid() {
	return uid;
}
public void setUid(String uid) {
	this.uid = uid;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getYijian() {
	return yijian;
}
public void setYijian(String yijian) {
	this.yijian = yijian;
}
public String getZhang() {
	return zhang;
}
public void setZhang(String zhang) {
	this.zhang = zhang;
}
public String getFileno() {
	return fileno;
}
public void setFileno(String fileno) {
	this.fileno = fileno;
}

public String getMd5() {
	return md5;
}
public void setMd5(String md5) {
	this.md5 = md5;
}
public String toString(){
    return "T9ADseal [seqId=" + seqId + ", title=" + title
        + ", content=" + content + ", yijian=" + yijian + ", zhang="
        + zhang +",md5="+md5+ ", fileno=" + fileno + ", uid=" + uid + "]";
  }
}
