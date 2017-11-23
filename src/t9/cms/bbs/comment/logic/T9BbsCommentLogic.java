package t9.cms.bbs.comment.logic;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import t9.cms.area.data.T9CmsArea;
import t9.cms.bbs.board.logic.T9BbsBoardLogic;
import t9.cms.bbs.comment.data.T9BbsComment;
import t9.cms.bbs.documentinfo.data.T9BbsDocumentInfo;
import t9.core.funcs.diary.logic.T9DiaryUtil;
import t9.core.funcs.office.ntko.data.T9NtkoCont;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;


public class T9BbsCommentLogic{
	
	
	  public static final String attachmentFolder = "cms";
	  public static String filePath = T9SysProps.getAttachPath() + File.separator + "cms";
	/**
	 * 获取我发布的帖子信息
	 * @param dbConn
	 * @param person
	 * @return
	 */
	public String getMyComment(Connection dbConn, T9Person person) {
		String sql="";
		StringBuffer sb=new StringBuffer("[");
		PreparedStatement ps=null;
		ResultSet rs=null;
		int nums=0;
		int looknums=0;
		try{
			sql="select bc.seq_id,bc.comment_title,bb.board_name from bbs_comment bc  join bbs_board bb on bb.seq_id=bc.board_id where bc.parent_comment_id=0 and bc.create_id=? order by seq_id  desc";
			ps=dbConn.prepareStatement(sql);
			ps.setInt(1, person.getSeqId());
			rs=ps.executeQuery();
			while(rs.next()){
				sb.append("{\"seqId\":\""+rs.getInt(1)+"\"");
				int infoId=this.getDocInfoId(dbConn, rs.getInt(1));
				sb.append(",\"infoId\":\""+infoId+"\"");
				String[] lastComment=this.getCurCommentLastRly(dbConn, rs.getInt(1));
				nums=this.getCommentsCountByDid(dbConn,String.valueOf(infoId));
				 looknums=this.getLookNums(dbConn,infoId);
				sb.append(",\"commentTitle\":\""+rs.getString(2)+"\"");
				sb.append(",\"lastCommentId\":\""+lastComment[0]+"\"");
				sb.append(",\"lastCommentUser\":\""+lastComment[1]+"\"");
				sb.append(",\"lastCommentTime\":\""+lastComment[2]+"\"");
				sb.append(",\"replyNums\":\""+nums+"\"");
				sb.append(",\"lookNums\":\""+looknums+"\"");
				sb.append(",\"boardName\":\""+rs.getString(3)+"\"},");
			}
			if(sb.length()>3){
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append("]");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return sb.toString();
	}
	/**
	 * 获取帖子查看次数
	 * @param dbConn
	 * @param infoId
	 * @return
	 */
	private int getLookNums(Connection dbConn, int infoId) {
		String sql="";
		int num=0;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			sql="select doc_look_count  from bbs_document_info where seq_id="+infoId;
			ps=dbConn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				num=rs.getInt(1);
			}	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return num;
	}

	/**
	 * 获取我的帖子数
	 * @param dbConn
	 * @param person
	 * @return
	 */
	
	public int getMyComments(Connection dbConn, T9Person person) {
		String sql="";
		int num=0;
		PreparedStatement ps=null;
		ResultSet rs=null;
		try{
			sql="select count(*) from bbs_comment bc  join bbs_board bb on bb.seq_id=bc.board_id where bc.parent_comment_id=0 and bc.create_id=?";
			ps=dbConn.prepareStatement(sql);
			ps.setInt(1, person.getSeqId());
			rs=ps.executeQuery();
			while(rs.next()){
				num=rs.getInt(1);
			}	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return num;
	}
	/**
	 * 发表帖子
	 * @param dbConn
	 * @param person
	 * @param fileForm
	 * @throws ParseException 
	 */
	public void addComment(Connection dbConn, T9Person person,T9FileUploadForm fileForm) throws ParseException {
		String title=fileForm.getParameter("title");
		String content=fileForm.getParameter("commentContent");
		String attachId=fileForm.getParameter("attachmentId");
		String attachName=fileForm.getParameter("attachmentName");
		String boardId=fileForm.getParameter("boardId");
		PreparedStatement ps=null;
		ResultSet rs=null;
		
		try{
			T9ORM orm = new T9ORM();
			String curTime = T9Utility.getCurDateTimeStr();
			T9BbsComment bbsComment = new T9BbsComment();
			bbsComment.setCommentTitle(title);
			bbsComment.setCommentContent(content);
			bbsComment.setCreateTime(T9Utility.parseDate(curTime));
			bbsComment.setCreateId(person.getSeqId());
			bbsComment.setBoardId(Integer.parseInt(boardId));
			bbsComment.setAttachmentId(attachId);
			bbsComment.setAttachmentName(attachName);
			orm.saveSingle(dbConn, bbsComment);
			int currentId = this.getMaxId(dbConn);
			T9BbsDocumentInfo docInfo = new T9BbsDocumentInfo();
			docInfo.setComId(currentId);
			docInfo.setBoardId(Integer.parseInt(boardId));
			docInfo.setDocCreaterid(person.getSeqId());
			docInfo.setDocCreatername(person.getUserName());
			docInfo.setDocCreatetime(T9Utility.parseDate(curTime));
			docInfo.setDocTitle(title);
			docInfo.setDocLight(0);
			docInfo.setDocLookCount(0);
			docInfo.setDocNice(0);
			docInfo.setDocOriginal(0);
			docInfo.setDocSelect(0);
			docInfo.setDocStatues(0);
			docInfo.setDocTopstatues(0);
			orm.saveSingle(dbConn, docInfo);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	   * 附件批量上传页面处理
	   * 
	   * @return
	   * @throws Exception
	   */
	  public StringBuffer uploadMsrg2Json(Connection dbConn,T9FileUploadForm fileForm)
	      throws Exception {
	    StringBuffer sb = new StringBuffer();
	    Map<String, String> attr = null;
	    String attachmentId = "";
	    String attachmentName = "";
	    try {
	      attr = this.fileUploadLogic(fileForm);
	      Set<String> attrKeys = attr.keySet();
	      for (String key : attrKeys) {
	        String fileName = attr.get(key);
	        attachmentId += key + ",";
	        attachmentName += fileName + "*";
	      }
	      long size = this.getSize(attr, attachmentFolder);
	      sb.append("{");
	      sb.append("'attachmentId':").append("\"").append(attachmentId).append("\",");
	      sb.append("'attachmentName':").append("\"").append(attachmentName).append("\",");
	      sb.append("'size':").append("").append(size);
	      sb.append("}");
	    } catch (Exception e) {
	      e.printStackTrace();
	      throw e;
	    }
	    return sb;
	  }
	  
	  
	  /**
	   * 文件上传处理
	   * @param fileForm
	   * @return
	   * @throws Exception
	   */
	  public Map<String, String> fileUploadLogic(T9FileUploadForm fileForm) throws Exception {
		    Map<String, String> result = new HashMap<String, String>();
		    try {
		      Calendar cld = Calendar.getInstance();
		      int year = cld.get(Calendar.YEAR) % 100;
		      int month = cld.get(Calendar.MONTH) + 1;
		      String mon = month >= 10 ? month + "" : "0" + month;
		      String hard = year + mon;
		      Iterator<String> iKeys = fileForm.iterateFileFields();
		      while (iKeys.hasNext()) {
		        String fieldName = iKeys.next();
		        String fileName = fileForm.getFileName(fieldName);
		        String fileNameV = fileName;
		        if (T9Utility.isNullorEmpty(fileName)) {
		          continue;
		        }
		        String rand = T9DiaryUtil.getRondom();
		        fileName = rand + "_" + fileName;
		        
		        while (T9DiaryUtil.getExist(T9SysProps.getAttachPath() + File.separator + hard, fileName)) {
		          rand = T9DiaryUtil.getRondom();
		          fileName = rand + "_" + fileName;
		        }
		        result.put(hard + "_" + rand, fileNameV);
		        fileForm.saveFile(fieldName, T9SysProps.getAttachPath() + File.separator + attachmentFolder + File.separator + hard + File.separator + fileName);
		        T9SelAttachUtil selA = new T9SelAttachUtil(fileForm, attachmentFolder);
		        result.putAll(selA.getAttachInFo());
		      }
		    } catch (Exception e) {
		      throw e;
		    }
		    return result;
		  }
	  /**
	   * 获取附件大小
	   * @param attr
	   * @param module
	   * @return
	   * @throws Exception
	   */
	  public long getSize(Map<String, String> attr, String module) throws Exception {
		    long result = 0l;
		    Set<String> attrKeys = attr.keySet();
		    String fileName = "";
		    String path = "";
		    for (String attachmentId : attrKeys) {
		      String attachmentName = attr.get(attachmentId);
		      if(attachmentId != null && !"".equals(attachmentId)){
		        if(attachmentId.indexOf("_") > 0){
		          String attIds[] = attachmentId.split("_");
		          fileName = attIds[1] + "." + attachmentName;
		          path = T9SysProps.getAttachPath()+ File.separator + module + File.separator + attIds[0] + File.separator  + fileName;
		        }else{
		          fileName = attachmentId + "." + attachmentName;
		          path = T9SysProps.getAttachPath() + File.separator + module + File.separator  + fileName;
		        }
		        
		        File file = new File(path);
		        if(!file.exists()){
		          if(attachmentId.indexOf("_") > 0){
		            String attIds[] = attachmentId.split("_");
		            fileName = attIds[1] + "_" + attachmentName;
		            path = T9NtkoCont.ATTA_PATH + File.separator + module + File.separator + attIds[0] + File.separator  + fileName;
		          }else{
		            fileName = attachmentId + "_" + attachmentName;
		            path = T9NtkoCont.ATTA_PATH + File.separator + module + File.separator  + fileName;
		          }
		          file = new File(path);
		        }
		        if(!file.exists()){
		          continue;
		        }
		        //this.fileName = fileName;
		        result += file.length();
		      }
		    }
		    return result;
		  }
	  /**
	   * 浮动菜单文件删除
	   * 
	   * @param dbConn
	   * @param attId
	   * @param attName
	   * @param contentId
	   * @throws Exception
	   */
	  public boolean delFloatFile(Connection dbConn, String attId, String attName, int seqId) throws Exception {
	    boolean updateFlag = false;
	    if (seqId != 0) {
	      T9ORM orm = new T9ORM();
	      T9CmsArea area = (T9CmsArea)orm.loadObjSingle(dbConn, T9CmsArea.class, seqId);
	      String[] attIdArray = {};
	      String[] attNameArray = {};
	      String attachmentId = area.getAttachmentId();
	      String attachmentName = area.getAttachmentName();
	      //T9Out.println("attachmentId"+attachmentId+"--------attachmentName"+attachmentName);
	      if (!"".equals(attachmentId.trim()) && attachmentId != null && attachmentName != null) {
	        attIdArray = attachmentId.trim().split(",");
	        attNameArray = attachmentName.trim().split("\\*");
	      }
	      String attaId = "";
	      String attaName = "";
	  
	      for (int i = 0; i < attIdArray.length; i++) {
	        if (attId.equals(attIdArray[i])) {
	          continue;
	        }
	        attaId += attIdArray[i] + ",";
	        attaName += attNameArray[i] + "*";
	      }
	      //T9Out.println("attaId=="+attaId+"--------attaName=="+attaName);
	      area.setAttachmentId(attaId.trim());
	      area.setAttachmentName(attaName.trim());
	      orm.updateSingle(dbConn, area);
	    }
	  //处理文件
	    String[] tmp = attId.split("_");
	    String path = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "_" + attName;
	    File file = new File(path);
	    if(file.exists()){
	      file.delete();
	    } else {
	      //兼容老的数据
	      String path2 = filePath + File.separator  + tmp[0] + File.separator + tmp[1] + "." + attName;
	      File file2 = new File(path2);
	      if(file2.exists()){
	        file2.delete();
	      }
	    }
	    updateFlag=true;
	    return updateFlag;
	  }
	  /**
	   * 获取最大ID
	   * @param conn
	   * @return
	 * @throws SQLException 
	   */
	  public int getMaxId(Connection conn) throws SQLException{
		  int  maxId=0;
		  String sql="";
		  PreparedStatement ps=null;
		  ResultSet rs=null;
		  try{
			  sql="select max(seq_id) from bbs_comment";
			  ps=conn.prepareStatement(sql);
			  rs=ps.executeQuery();
			  while(rs.next()){
				  maxId=rs.getInt(1);
			  }
		  }catch(Exception ex){
			  ex.printStackTrace();
		  }
		  return maxId;
	  }
	  
	  /**
	   * 获取最后回帖信息
	   */
	 public String[] getCurCommentLastRly(Connection conn ,int commentId){
		 String[] lastComment={"","",""};
		 String sql="";
		 PreparedStatement ps=null;
		 ResultSet rs=null;
		 T9BbsBoardLogic logic=new T9BbsBoardLogic();
		 try{
			 sql="select seq_id,create_id,create_time from bbs_comment where parent_comment_id='"+commentId+"'  order by seq_id desc";
			 ps=conn.prepareStatement(sql);
			 rs=ps.executeQuery();
			 if(rs.next()){
				 if(rs.getString(1)==null || "".equals(rs.getString(1))){
					 lastComment[0]="无";
				 }else{
					 lastComment[0]=rs.getString(1);
				 }
				 if(rs.getString(2)==null || "".equals(rs.getString(2))){
					 lastComment[1]="无";
				 }else{
					 lastComment[1]=logic.queryUsername(conn, rs.getString(2));
				 }
				 if(rs.getString(3)==null || "".equals(rs.getString(3))){
					 lastComment[2]="";
				 }else{
					 lastComment[2]=rs.getString(3);
				 }
			 }
		 }catch(Exception ex){
			 ex.printStackTrace();
		 }
		 return lastComment;
	 }
	  
	  /**
	   * 获取回复列表
	   * @param dbConn
	   * @param person
	   * @return
	   */
	  public String getCommentsByDid(Connection dbConn, String did,int nPageSize, int nCurrentPage) {
			String sql="";
			StringBuffer sb=new StringBuffer("[");
			PreparedStatement ps=null;
			ResultSet rs=null;
		    boolean flag = false;
		    int nTotalSize = getCommentsCountByDid(dbConn,did);
		    int comid = getComIdByDid(dbConn,did);
			try{
				sql="select c1.seq_id as seqid, c1.COMMENT_CONTENT as content,c1.CREATE_TIME as createtime, p1.USER_ID as userid from bbs_comment c1 left outer join person p1 on c1.CREATE_ID = p1.seq_id where PARENT_COMMENT_ID = '"+comid+"'";
				ps=dbConn.prepareStatement(sql);
				rs=ps.executeQuery();
				/**
			       * 蛋疼的分页 不过靠谱 不知道t9怎么分的
			       */
			      int nPageCount = ((nTotalSize + nPageSize) - 1) / nPageSize;
		          if(nCurrentPage > nPageCount)
		              nCurrentPage = nPageCount;
		          int i = (nCurrentPage - 1) * nPageSize;
		          for(int j = 0; j < i; j++)
		              rs.next();
		          for(i = 0; i < nPageSize && rs.next(); i++){
					sb.append("{\"createtime\":\""+rs.getString("createtime")+"\"");
					sb.append(",\"content\":\""+rs.getString("content").replace("\"", "\\\"")+"\"");
					sb.append(",\"seqId\":\""+rs.getString("seqid")+"\"");
					sb.append(",\"uid\":\""+rs.getString("userid")+"\"},");
					flag = true;
				}
			  if(flag){
				  sb = sb.deleteCharAt(sb.length() - 1);
	 	        }
				sb.append("]");
				
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return sb.toString();
		}
	  /**
	   * 获取数量
	   * @param dbConn
	   * @param did
	   * @return
	   */
	  public int getCommentsCountByDid(Connection dbConn,  String did) {
			String sql="";
			PreparedStatement ps=null;
			ResultSet rs=null;
			int count = 0;
			 int comid = getComIdByDid(dbConn,did);
			try{
				sql="select count(*) as count from bbs_comment  where PARENT_COMMENT_ID = '"+comid+"'";
				ps=dbConn.prepareStatement(sql);
				rs=ps.executeQuery();
				if(rs.next()){
					count = rs.getInt("count");
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return count;
		}
	  
	  public void addComment1(Connection dbConn, String did,String content,T9Person person) {
			String sql="";
			PreparedStatement ps=null;
			String time=T9Utility.getCurDateTimeStr();
			int comId=this.getComIdByDid(dbConn, did);
			T9ORM orm=new T9ORM();
			try{
				T9BbsComment comment=(T9BbsComment)orm.loadObjSingle(dbConn, T9BbsComment.class, comId);
				T9BbsDocumentInfo info=(T9BbsDocumentInfo)orm.loadObjSingle(dbConn, T9BbsDocumentInfo.class, Integer.parseInt(did));
				sql="insert into bbs_comment(board_id,COMMENT_CONTENT,PARENT_COMMENT_ID,CREATE_TIME,CREATE_ID)values(?,?,?,?,?)";
				/*		+"'"+content+"',"
						+"'"+comId+"',"
						+T9Utility.parseSqlDate(time)+","
						+"'"+person.getSeqId()+"'"
						+")";*/
				ps=dbConn.prepareStatement(sql);
				ps.setInt(1, comment.getBoardId());
				ps.setString(2,content);
				ps.setInt(3, comId);
				ps.setDate(4, T9Utility.parseSqlDate(time));
				ps.setInt(5, person.getSeqId());
				ps.execute();
				int replyCounts=info.getDocReplayCount()+1;
				info.setDocReplayCount(replyCounts);
				info.setDocLastReplayUser(person.getUserName());
				info.setDocLastReplayTime(T9Utility.parseDate(T9Utility.getCurDateTimeStr()));
				orm.updateSingle(dbConn, info);
			}catch(Exception ex){
				ex.printStackTrace();
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
		}
	  
	  /**
	   *获取docInfo表中ID
	   * @param conn
	   * @param seqId
	   * @return
	   */
	  public int getDocInfoId(Connection conn , int seqId){
			String sql="";
			PreparedStatement ps=null;
			ResultSet rs=null;
			int infoId = 0;
			try{
				sql="select seq_id from bbs_document_info  where com_id = '"+seqId+"'";
				ps=conn.prepareStatement(sql);
				rs=ps.executeQuery();
				if(rs.next()){
					infoId = rs.getInt("seq_id");
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return infoId;
	  }
	  
	  public int getComIdByDid(Connection conn , String did){
			String sql="";
			PreparedStatement ps=null;
			ResultSet rs=null;
			int infoId = 0;
			try{
				sql="select com_id from bbs_document_info  where seq_id = '"+did+"'";
				ps=conn.prepareStatement(sql);
				rs=ps.executeQuery();
				if(rs.next()){
					infoId = rs.getInt("com_id");
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return infoId;
	  }
	  
	  /**
	   * 更新查看数
	   * @param conn
	   * @param seqId
	   * @throws Exception
	   */
	  public void updateLookNums(Connection conn, int seqId) throws Exception{
		  int lookNums=this.getLookNums(conn, seqId)+1;
		  T9ORM orm=new T9ORM();
		  T9BbsDocumentInfo info=(T9BbsDocumentInfo)orm.loadObjSingle(conn, T9BbsDocumentInfo.class, seqId);
		  info.setDocLookCount(lookNums);
		  orm.updateSingle(conn, info);
	  }
	  /**
	   * 更新回复表
	   * @param conn
	   * @param sql
	   * @throws Exception
	   */
	  public void updateCommentById(Connection conn, String sql) throws Exception{
			PreparedStatement ps=null;
			try{
				ps=conn.prepareStatement(sql);
				ps.executeUpdate();
				conn.commit();
			}catch(Exception ex){
				conn.rollback();
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
	  }
}