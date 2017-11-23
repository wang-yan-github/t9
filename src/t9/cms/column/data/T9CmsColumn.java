package t9.cms.column.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import t9.cms.content.data.T9CmsContent;
import t9.cms.station.data.T9CmsStation;
import t9.cms.template.data.T9CmsTemplate;

public class T9CmsColumn {

  private int seqId;
  private String columnName;
  private String columnTitle;
  private int stationId;
  private int parentId;
  private String columnPath;
  private int archive;
  private int templateIndexId;
  private int templateArticleId;
  private int createId;
  private Date createTime;
  private int columnIndex;
  private int paging;
  private int maxIndexPage;
  private int pagingNumber;
  private String url;
  private int showMain;
  private String visitUser;
  private String editUser;
  private String newUser;
  private String delUser;
  private String relUser;
  private String editUserContent;
  private String approvalUserContent;
  private String releaseUserContent;
  private String recevieUserContent;
  private String orderContent;
  private List<T9CmsContent> contentList;
  public String getEditUserContent() {
    return editUserContent;
  }
  public void setEditUserContent(String editUserContent) {
    this.editUserContent = editUserContent;
  }
  public String getApprovalUserContent() {
    return approvalUserContent;
  }
  public void setApprovalUserContent(String approvalUserContent) {
    this.approvalUserContent = approvalUserContent;
  }
  public String getReleaseUserContent() {
    return releaseUserContent;
  }
  public void setReleaseUserContent(String releaseUserContent) {
    this.releaseUserContent = releaseUserContent;
  }
  public String getRecevieUserContent() {
    return recevieUserContent;
  }
  public void setRecevieUserContent(String recevieUserContent) {
    this.recevieUserContent = recevieUserContent;
  }
  public String getOrderContent() {
    return orderContent;
  }
  public void setOrderContent(String orderContent) {
    this.orderContent = orderContent;
  }
  public String getVisitUser() {
    return visitUser;
  }
  public void setVisitUser(String visitUser) {
    this.visitUser = visitUser;
  }
  public String getEditUser() {
    return editUser;
  }
  public void setEditUser(String editUser) {
    this.editUser = editUser;
  }
  public String getNewUser() {
    return newUser;
  }
  public void setNewUser(String newUser) {
    this.newUser = newUser;
  }
  public String getDelUser() {
    return delUser;
  }
  public void setDelUser(String delUser) {
    this.delUser = delUser;
  }
  public String getRelUser() {
    return relUser;
  }
  public void setRelUser(String relUser) {
    this.relUser = relUser;
  }
  public Iterator<T9CmsContent> itContent(){
    Iterator<T9CmsContent> iterator = new Iterator<T9CmsContent>() {
      
      @Override
      public void remove() {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public T9CmsContent next() {
        // TODO Auto-generated method stub
        return null;
      }
      
      @Override
      public boolean hasNext() {
        // TODO Auto-generated method stub
        return false;
      }
    };
    return iterator;
  }
  public int getShowMain() {
    return showMain;
  }
  public void setShowMain(int showMain) {
    this.showMain = showMain;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public int getSeqId() {
    return seqId;
  }
  public void setSeqId(int seqId) {
    this.seqId = seqId;
  }
  public String getColumnName() {
    return columnName;
  }
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
  public String getColumnTitle() {
    return columnTitle;
  }
  public void setColumnTitle(String columnTitle) {
    this.columnTitle = columnTitle;
  }
  public int getStationId() {
    return stationId;
  }
  public void setStationId(int stationId) {
    this.stationId = stationId;
  }
  public int getParentId() {
    return parentId;
  }
  public void setParentId(int parentId) {
    this.parentId = parentId;
  }
  public String getColumnPath() {
    return columnPath;
  }
  public void setColumnPath(String columnPath) {
    this.columnPath = columnPath;
  }
  public int getTemplateIndexId() {
    return templateIndexId;
  }
  public void setTemplateIndexId(int templateIndexId) {
    this.templateIndexId = templateIndexId;
  }
  public int getTemplateArticleId() {
    return templateArticleId;
  }
  public void setTemplateArticleId(int templateArticleId) {
    this.templateArticleId = templateArticleId;
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
  public int getColumnIndex() {
    return columnIndex;
  }
  public void setColumnIndex(int columnIndex) {
    this.columnIndex = columnIndex;
  }
  public int getArchive() {
    return archive;
  }
  public void setArchive(int archive) {
    this.archive = archive;
  }
  public int getPaging() {
    return paging;
  }
  public void setPaging(int paging) {
    this.paging = paging;
  }
  public int getMaxIndexPage() {
    return maxIndexPage;
  }
  public void setMaxIndexPage(int maxIndexPage) {
    this.maxIndexPage = maxIndexPage;
  }
  public int getPagingNumber() {
    return pagingNumber;
  }
  public void setPagingNumber(int pagingNumber) {
    this.pagingNumber = pagingNumber;
  }
  public List<T9CmsContent> getContentList() {
    return contentList;
  }
  public void setContentList(List<T9CmsContent> contentList) {
    this.contentList = contentList;
  }
  public List<T9CmsContent> getContents(int count){
    if(count == 999){
      return contentList;
    }
    List<T9CmsContent> contents = new ArrayList<T9CmsContent>();
    for(T9CmsContent content : contentList){
      if(count == 0){
        break;
      }
      contents.add(content);
      count--;
    }
    return contents;
  }
  
  
  public String getPage(T9CmsColumn column, int contentSize, T9CmsStation station, T9CmsTemplate template, int i, int type){
    int total = contentSize;
    if(total == 0){
      return "";
    }
    if(column.getPaging() == 1){
      int maxIndexPage = column.getMaxIndexPage();
      int pagingNumber = column.getPagingNumber(); 
      
      
      int page = total/pagingNumber + (total%pagingNumber > 0 ? 1 : 0);
      if(page > maxIndexPage){
        total = maxIndexPage * pagingNumber;
      }
      page = page > maxIndexPage ? maxIndexPage : page;
      
      String fileName = template.getTemplateFileName();
      String prv = "";
      String next = "";
      String last = "";
      if(i - 1 <= 0){
        prv = fileName+"."+station.getExtendName();
      }
      else{
        prv = fileName+(i - 1)+"."+station.getExtendName();
      }
      if(i + 1 >= page){
        next = fileName+(page-1 == 0 ? "" : page-1)+"."+station.getExtendName();
      }
      else{
        next = fileName+(i+1)+"."+station.getExtendName();
      }
      if(page - 1 == 0){
        last = "";
      }
      else{
        last = (page - 1) + "";
      }
      
      String tempStr = "";
      switch(type){
        case 1 : tempStr =  "<table width=\"100%\" border=\"0\" cellspacing=\"5\" cellpadding=\"0\">\n " 
                          + "<tr>\n " 
                          + "<td align=\"center\" class=\"dahei\">" 
                          + "<div> " 
                          + "共"+ total +"条新闻，分"+page+"页，当前第<font color=red>" + (i+1) + "</font>页&nbsp;&nbsp;"
                          + "<a href=\""+(fileName+"."+station.getExtendName())+"\">最前页</a> " 
                          + "<a href=\""+prv+"\">上一页</a> " 
                          + "<a href=\""+next+"\">下一页</a> " 
                          + "<a href=\""+(fileName+last+"."+station.getExtendName())+"\">最后页</a> " 
                          + "</div> "
                          + "</td>"
                          + "</tr>"
                          + "</table>";
                  break;
        
        case 2 : String pageStr = "";
          for(int j = 0; j < page; j++){
            fileName = template.getTemplateFileName();
            if(j != 0){
              fileName = fileName + j;
            }
            if(j == i){
              pageStr = pageStr + "<span class=\"current\">"+(j+1)+"</span>";
            }
            else{
              pageStr = pageStr + "<a href="+fileName+"."+station.getExtendName()+"><span>"+(j+1)+"</span></a>";
            }
          }
            
          tempStr =  "<a href=\""+prv+"\"><span class=\"prev\">&nbsp;</span></a>" 
                   +	pageStr
                   + "<a href=\""+next+"\"><span class=\"next\">&nbsp;</span></a>";
          break;
        default: break;
      }
      return tempStr;
    }
    return "";
  }
}
