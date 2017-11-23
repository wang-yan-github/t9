package t9.cms.bbs.documentinfo.data;

import java.util.Date;

/**
 * 
 * @author zp
 *帖子信息表
 */
public class T9BbsDocumentInfo {

	private int seqId;//该表主键
	private int comId;//Comment表的主键
	private int boardId;//板块表主键 板块表主键
	private String docTitle;//帖子标题
	private Date docCreatetime;//创建时间
	private int docCreaterid;//创建者id Person表主键
	private String docCreatername;//创建者名字
	private int docStatues;//帖子状态
	private int docTopstatues;//帖子置顶状态
	private int docSelect;//帖子是否为精华 0 否 1是
	private int docLight;//帖子是否高亮
	private int docNice;//帖子是否为推荐
	private int docOriginal;//帖子是否原创
	private int docImgAttch;//是否有图片附件
	private int docAudioAttch;//是否有音频附件
	private int docOtherAttch;//是否有其他附件
	private int docVedioAttch;//是否有视频附件
	private int docLookCount;//帖子查看数量
	private int docReplayCount;//帖子回复数量
	private String docLastReplayUser;//最后回帖人名
	private int docLastReplayUserid;//最后回帖人ID person 表主键
	private Date docLastReplayTime;//最后回帖时间
	public int getSeqId() {
		return seqId;
	}
	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}
	
	public int getBoardId() {
		return boardId;
	}
	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	public Date getDocCreatetime() {
		return docCreatetime;
	}
	public void setDocCreatetime(Date docCreatetime) {
		this.docCreatetime = docCreatetime;
	}
	public int getDocCreaterid() {
		return docCreaterid;
	}
	public void setDocCreaterid(int docCreaterid) {
		this.docCreaterid = docCreaterid;
	}
	public int getDocStatues() {
		return docStatues;
	}
	public void setDocStatues(int docStatues) {
		this.docStatues = docStatues;
	}
	public int getDocTopstatues() {
		return docTopstatues;
	}
	public void setDocTopstatues(int docTopstatues) {
		this.docTopstatues = docTopstatues;
	}
	public int getDocSelect() {
		return docSelect;
	}
	public void setDocSelect(int docSelect) {
		this.docSelect = docSelect;
	}
	public int getDocLight() {
		return docLight;
	}
	public void setDocLight(int docLight) {
		this.docLight = docLight;
	}
	public int getDocImgAttch() {
		return docImgAttch;
	}
	public void setDocImgAttch(int docImgAttch) {
		this.docImgAttch = docImgAttch;
	}
	public int getDocAudioAttch() {
		return docAudioAttch;
	}
	public void setDocAudioAttch(int docAudioAttch) {
		this.docAudioAttch = docAudioAttch;
	}
	public int getDocOtherAttch() {
		return docOtherAttch;
	}
	public void setDocOtherAttch(int docOtherAttch) {
		this.docOtherAttch = docOtherAttch;
	}
	public int getDocVedioAttch() {
		return docVedioAttch;
	}
	public void setDocVedioAttch(int docVedioAttch) {
		this.docVedioAttch = docVedioAttch;
	}
	public int getDocLookCount() {
		return docLookCount;
	}
	public void setDocLookCount(int docLookCount) {
		this.docLookCount = docLookCount;
	}
	public int getDocReplayCount() {
		return docReplayCount;
	}
	public void setDocReplayCount(int docReplayCount) {
		this.docReplayCount = docReplayCount;
	}
	public String getDocLastReplayUser() {
		return docLastReplayUser;
	}
	public void setDocLastReplayUser(String docLastReplayUser) {
		this.docLastReplayUser = docLastReplayUser;
	}
	public int getDocLastReplayUserid() {
		return docLastReplayUserid;
	}
	public void setDocLastReplayUserid(int docLastReplayUserid) {
		this.docLastReplayUserid = docLastReplayUserid;
	}
	public Date getDocLastReplayTime() {
		return docLastReplayTime;
	}
	public void setDocLastReplayTime(Date docLastReplayTime) {
		this.docLastReplayTime = docLastReplayTime;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public String getDocCreatername() {
		return docCreatername;
	}
	public void setDocCreatername(String docCreatername) {
		this.docCreatername = docCreatername;
	}
	public int getDocNice() {
		return docNice;
	}
	public void setDocNice(int docNice) {
		this.docNice = docNice;
	}
	public int getDocOriginal() {
		return docOriginal;
	}
	public void setDocOriginal(int docOriginal) {
		this.docOriginal = docOriginal;
	}
	public int getComId() {
		return comId;
	}
	public void setComId(int comId) {
		this.comId = comId;
	}
	
	
}
