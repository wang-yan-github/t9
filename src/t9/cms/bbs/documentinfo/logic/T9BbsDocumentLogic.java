package t9.cms.bbs.documentinfo.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import t9.core.util.db.T9DBUtility;

public class T9BbsDocumentLogic {

	 public String getDocumentInfoList(Connection dbConn, String boardId,int nPageSize, int nCurrentPage) throws Exception {
		 try {
		      StringBuffer data = new StringBuffer("[");
		      StringBuffer sb = new StringBuffer();
		      String sql = "";
		      
		      sb.append(" select b1.SEQ_ID,b1.BOARD_ID, b1.DOC_TITLE ,b1.DOC_CREATETIME,b1.DOC_CREATERID,b1.DOC_CREATERNAME,");
   		  sb.append("b1.DOC_STATUES,b1.DOC_TOPSTATUES,b1.DOC_SELECT,b1.DOC_LIGHT,b1.DOC_NICE,b1.DOC_ORIGINAL,b1.DOC_IMG_ATTCH,");
			  sb.append("b1.DOC_AUDIO_ATTCH,b1.DOC_OTHER_ATTCH,b1.DOC_VEDIO_ATTCH,b1.DOC_LOOK_COUNT,b1.DOC_REPLAY_COUNT,");
			  sb.append("b1.DOC_LAST_REPLAY_USER,b1.DOC_LAST_REPLAY_USERID,b1.DOC_LAST_REPLAY_TIME");
			  sb.append( " from BBS_DOCUMENT_INFO b1 ");
		      sb.append( " where b1.BOARD_ID = " + boardId +" and b1.DOC_TOPSTATUES = 0");
			 // sb.append(" limit "+index+" "+pagesize);
			  sb.append( " ORDER BY b1.SEQ_ID desc ");
			  sql = sb.toString();
		      PreparedStatement ps = null;
		      ResultSet rs = null;
		      boolean flag = false;
		      int nTotalSize = getDocumentsCountByDid(dbConn,boardId);
		      try {
		        ps = dbConn.prepareStatement(sql);
		        rs = ps.executeQuery();
		        int nPageCount = ((nTotalSize + nPageSize) - 1) / nPageSize;
		          if(nCurrentPage > nPageCount)
		              nCurrentPage = nPageCount;
		          int i = (nCurrentPage - 1) * nPageSize;
		          for(int j = 0; j < i; j++)
		              rs.next();
		          for(i = 0; i < nPageSize && rs.next(); i++){
			          data.append("{seqId:\""+rs.getInt("SEQ_ID")+"\",");
			          data.append("boardId:\""+rs.getInt("BOARD_ID")+"\",");
			          data.append("docTitle:\""+rs.getString("DOC_TITLE")+"\",");
			          data.append("docCreatetime:\""+rs.getString("DOC_CREATETIME").substring(0,10)+"\",");
			          data.append("docCreaterid:\""+rs.getInt("DOC_CREATERID")+"\",");
			          data.append("docCreatername:\""+rs.getString("DOC_CREATERNAME")+"\",");
			          data.append("docStatues:\""+rs.getInt("DOC_STATUES")+"\",");
			          data.append("docTopstatues:\""+rs.getInt("DOC_TOPSTATUES")+"\",");
			          data.append("docSelect:\""+rs.getInt("DOC_SELECT")+"\",");
			          data.append("docLight:\""+rs.getInt("DOC_LIGHT")+"\",");
			          data.append("docNice:\""+rs.getInt("DOC_NICE")+"\",");
			          data.append("docOriginal:\""+rs.getInt("DOC_ORIGINAL")+"\",");
			          data.append("docImgAttch:\""+rs.getInt("DOC_IMG_ATTCH")+"\",");
			          data.append("docAudioAttch:\""+rs.getInt("DOC_AUDIO_ATTCH")+"\",");
			          data.append("docOtherAttch:\""+rs.getInt("DOC_OTHER_ATTCH")+"\",");
			          data.append("docVedioAttch:\""+rs.getInt("DOC_VEDIO_ATTCH")+"\",");
			          data.append("docLookCount:\""+rs.getInt("DOC_LOOK_COUNT")+"\",");
			          data.append("docReplayCount:\""+rs.getInt("DOC_REPLAY_COUNT")+"\",");
			          if(rs.getString("DOC_LAST_REPLAY_USER")!=null){
			        	  data.append("docLastReplayUser:\""+rs.getString("DOC_LAST_REPLAY_USER")+"\",");
		        	  }else{
		        		  data.append("docLastReplayUser:\""+""+"\",");
		        	  }
			          data.append("docLastReplayUserid:\""+rs.getInt("DOC_LAST_REPLAY_USERID")+"\",");
			          if(rs.getString("DOC_LAST_REPLAY_TIME")!=null){
			        	  data.append("docLastreplayTime:\""+rs.getString("DOC_LAST_REPLAY_TIME").substring(0,10)+"\"},");
		        	  }else{
		        		  data.append("docLastreplayTime:\""+""+"\"},");
		        	  }
			          flag = true;
		        }
		      } catch (Exception e) {
		        e.printStackTrace();
		      } finally {
		        T9DBUtility.close(ps, rs, null);
		      }
		      if(flag){
		        data = data.deleteCharAt(data.length() - 1);
		      }
		      data.append("]");
		      return data.toString();
		    } catch (Exception e) {
		      throw e;
		    }
		  }
	 public String getDocumentInfoList(Connection dbConn, String boardId,String index,String pagesize ) throws Exception {
		    try {
		      StringBuffer data = new StringBuffer("[");
		      StringBuffer sb = new StringBuffer();
		      String sql = "";
		      
		      sb.append(" select b1.SEQ_ID,b1.BOARD_ID, b1.DOC_TITLE ,b1.DOC_CREATETIME,b1.DOC_CREATERID,b1.DOC_CREATERNAME,");
	   		  sb.append("b1.DOC_STATUES,b1.DOC_TOPSTATUES,b1.DOC_SELECT,b1.DOC_LIGHT,b1.DOC_NICE,b1.DOC_ORIGINAL,b1.DOC_IMG_ATTCH,");
				  sb.append("b1.DOC_AUDIO_ATTCH,b1.DOC_OTHER_ATTCH,b1.DOC_VEDIO_ATTCH,b1.DOC_LOOK_COUNT,b1.DOC_REPLAY_COUNT,");
				  sb.append("b1.DOC_LAST_REPLAY_USER,b1.DOC_LAST_REPLAY_USERID,b1.DOC_LAST_REPLAY_TIME");
			  sb.append( " from BBS_DOCUMENT_INFO b1 ");
		      sb.append( " where b1.BOARD_ID = " + boardId);
			  sb.append(" limit "+index+" "+pagesize);
			  sb.append( " ORDER BY c1.SEQ_ID desc ");
			  sql = sb.toString();
		      PreparedStatement ps = null;
		      ResultSet rs = null;
		      boolean flag = false;
		      try {
		        ps = dbConn.prepareStatement(sql);
		        rs = ps.executeQuery();
		        while (rs.next()) {
		          data.append("{seqId:\""+rs.getInt("SEQ_ID")+"\",");
		          data.append("boardId:\""+rs.getInt("BOARD_ID")+"\",");
		          data.append("docTitle:\""+rs.getString("DOC_TITLE")+"\",");
		          data.append("docCreatetime:\""+rs.getString("DOC_CREATETIME")+"\",");
		          data.append("docCreaterid:\""+rs.getInt("DOC_CREATERID")+"\",");
		          data.append("docCreatername:\""+rs.getInt("DOC_CREATERNAME")+"\",");
		          data.append("docStatues:\""+rs.getInt("DOC_STATUES")+"\",");
		          data.append("docTopstatues:\""+rs.getInt("DOC_TOPSTATUES")+"\",");
		          data.append("docSelect:\""+rs.getInt("DOC_SELECT")+"\",");
		          data.append("docLight:\""+rs.getInt("DOC_LIGHT")+"\",");
		          data.append("docNice:\""+rs.getInt("DOC_NICE")+"\",");
		          data.append("docOriginal:\""+rs.getInt("DOC_ORIGINAL")+"\",");
		          data.append("docImgAttch:\""+rs.getInt("DOC_IMG_ATTCH")+"\",");
		          data.append("docAudioAttch:\""+rs.getInt("DOC_AUDIO_ATTCH")+"\",");
		          data.append("docOtherAttch:\""+rs.getInt("DOC_OTHER_ATTCH")+"\",");
		          data.append("docVedioAttch:\""+rs.getInt("DOC_VEDIO_ATTCH")+"\",");
		          data.append("docLookCount:\""+rs.getInt("DOC_LOOK_COUNT")+"\",");
		          data.append("docReplayCount:\""+rs.getInt("DOC_REPLAY_COUNT")+"\",");
		          if(rs.getString("DOC_LAST_REPLAY_USER")!=null){
		        	  data.append("docLastReplayUser:\""+rs.getString("DOC_LAST_REPLAY_USER")+"\",");
	        	  }else{
	        		  data.append("docLastReplayUser:\""+""+"\",");
	        	  }
		          data.append("docLastReplayUserid:\""+rs.getInt("DOC_LAST_REPLAY_USERID")+"\",");
		          if(rs.getString("DOC_LAST_REPLAY_TIME")!=null){
		        	  data.append("docLastreplayTime:\""+rs.getString("DOC_LAST_REPLAY_TIME").substring(0,10)+"\"},");
	        	  }else{
	        		  data.append("docLastreplayTime:\""+""+"\"},");
	        	  }
		          flag = true;
		        }
		      } catch (Exception e) {
		        e.printStackTrace();
		      } finally {
		        T9DBUtility.close(ps, rs, null);
		      }
		      if(flag){
		        data = data.deleteCharAt(data.length() - 1);
		      }
		      data.append("]");
		      return data.toString();
		    } catch (Exception e) {
		      throw e;
		    }
		  }
	 /**
	  * 获取头部 置顶列表
	  * @param dbConn
	  * @param boardId
	  * @return
	  * @throws Exception
	  */
	 public String getDocumentInfoTopList(Connection dbConn, String boardId) throws Exception {
		    try {
		      StringBuffer data = new StringBuffer("[");
		      StringBuffer sb = new StringBuffer();
		      String sql = "";
		      
		      sb.append(" select b1.SEQ_ID,b1.BOARD_ID, b1.DOC_TITLE ,b1.DOC_CREATETIME,b1.DOC_CREATERID,b1.DOC_CREATERNAME,");
	   		  sb.append("b1.DOC_STATUES,b1.DOC_TOPSTATUES,b1.DOC_SELECT,b1.DOC_LIGHT,b1.DOC_NICE,b1.DOC_ORIGINAL,b1.DOC_IMG_ATTCH,");
				  sb.append("b1.DOC_AUDIO_ATTCH,b1.DOC_OTHER_ATTCH,b1.DOC_VEDIO_ATTCH,b1.DOC_LOOK_COUNT,b1.DOC_REPLAY_COUNT,");
				  sb.append("b1.DOC_LAST_REPLAY_USER,b1.DOC_LAST_REPLAY_USERID,b1.DOC_LAST_REPLAY_TIME");
				  sb.append( " from BBS_DOCUMENT_INFO b1 ");
		      sb.append( " where (b1.BOARD_ID = " + boardId+" and b1.DOC_TOPSTATUES = 1)");
			  sb.append(" or (b1.DOC_TOPSTATUES = 3)");
			  sb.append( " ORDER BY b1.DOC_TOPSTATUES desc ");
			  sql = sb.toString();
		      PreparedStatement ps = null;
		      ResultSet rs = null;
		      boolean flag = false;
		      try {
		        ps = dbConn.prepareStatement(sql);
		        rs = ps.executeQuery();
		        while (rs.next()) {
		          data.append("{seqId:\""+rs.getInt("SEQ_ID")+"\",");
		          data.append("boardId:\""+rs.getInt("BOARD_ID")+"\",");
		          data.append("docTitle:\""+rs.getString("DOC_TITLE")+"\",");
		          data.append("docCreatetime:\""+rs.getString("DOC_CREATETIME").substring(0,10)+"\",");
		          data.append("docCreaterid:\""+rs.getInt("DOC_CREATERID")+"\",");
		          data.append("docCreatername:\""+rs.getString("DOC_CREATERNAME")+"\",");
		          data.append("docStatues:\""+rs.getInt("DOC_STATUES")+"\",");
		          data.append("docTopstatues:\""+rs.getInt("DOC_TOPSTATUES")+"\",");
		          data.append("docSelect:\""+rs.getInt("DOC_SELECT")+"\",");
		          data.append("docLight:\""+rs.getInt("DOC_LIGHT")+"\",");
		          data.append("docNice:\""+rs.getInt("DOC_NICE")+"\",");
		          data.append("docOriginal:\""+rs.getInt("DOC_ORIGINAL")+"\",");
		          data.append("docImgAttch:\""+rs.getInt("DOC_IMG_ATTCH")+"\",");
		          data.append("docAudioAttch:\""+rs.getInt("DOC_AUDIO_ATTCH")+"\",");
		          data.append("docOtherAttch:\""+rs.getInt("DOC_OTHER_ATTCH")+"\",");
		          data.append("docVedioAttch:\""+rs.getInt("DOC_VEDIO_ATTCH")+"\",");
		          data.append("docLookCount:\""+rs.getInt("DOC_LOOK_COUNT")+"\",");
		          data.append("docReplayCount:\""+rs.getInt("DOC_REPLAY_COUNT")+"\",");
		          if(rs.getString("DOC_LAST_REPLAY_USER")!=null){
		        	  data.append("docLastReplayUser:\""+rs.getString("DOC_LAST_REPLAY_USER")+"\",");
	        	  }else{
	        		  data.append("docLastReplayUser:\""+""+"\",");
	        	  }
		          data.append("docLastReplayUserid:\""+rs.getInt("DOC_LAST_REPLAY_USERID")+"\",");
		          if(rs.getString("DOC_LAST_REPLAY_TIME")!=null){
		        	  data.append("docLastreplayTime:\""+rs.getString("DOC_LAST_REPLAY_TIME").substring(0,10)+"\"},");
	        	  }else{
	        		  data.append("docLastreplayTime:\""+""+"\"},");
	        	  }
		          flag = true;
		        }
		      } catch (Exception e) {
		        e.printStackTrace();
		      } finally {
		        T9DBUtility.close(ps, rs, null);
		      }
		      if(flag){
		        data = data.deleteCharAt(data.length() - 1);
		      }
		      data.append("]");
		      return data.toString();
		    } catch (Exception e) {
		      throw e;
		    }
		  }
	 /**
	  * 获取文章 连接查询
	  * @param dbConn
	  * @param id
	  * @return
	  * @throws Exception
	  */
	 public String getDocumentById(Connection dbConn,String id) throws Exception{
	    try {
	      StringBuffer data = new StringBuffer("[");
	      String sql = " Select d1.SEQ_ID as id,c1.COMMENT_TITLE as title,c1.COMMENT_CONTENT as content,d1.DOC_CREATERNAME as creatername,d1.DOC_CREATETIME as createtime "
	      		+",d1.DOC_LOOK_COUNT as lookcount,d1.DOC_REPLAY_COUNT as replaycount "
	      		+""
	    		+"from bbs_document_info d1 left outer join  bbs_comment c1 on d1.COM_ID = c1.seq_id where d1.seq_id = '"+id+"'";
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      boolean flag = false;
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	          data.append("{seqId:"+rs.getInt("id")+","
	                    + "creatername:\""+rs.getString("creatername")+"\","
	                    + "createtime:\""+rs.getString("createtime")+"\","
	                    + "lookcount:\""+rs.getString("lookcount")+"\","
	                    + "replaycount:\""+rs.getString("replaycount")+"\","
	                    + "title:\""+rs.getString("title")+"\","
	                    +"content:\""+rs.getString("content").replace("\"", "\\\"")+"\"},");
	          flag = true;
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      if(flag){
	        data = data.deleteCharAt(data.length() - 1);
	      }
	      data.append("]");
	      return data.toString();
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	 
	 /**
	  * 
	  * @param dbConn
	  * @param did
	  * @return
	  */
	 public int getDocumentsCountByDid(Connection dbConn,  String boardId) {
			String sql="select  count(*) as count  from BBS_DOCUMENT_INFO b1  where b1.BOARD_ID = " + boardId;
			StringBuffer sb=new StringBuffer("");
			PreparedStatement ps=null;
			ResultSet rs=null;
			int count = 0;
			try{
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
	 /**
	  * 对文章列表进行 操作包括 置顶 高亮 精华 删除
	  * @param dbConn
	  * @param dids
	  * @param stat
	  * @param method
	  * @return
	  */
	 public int manageDocument(Connection dbConn, String dids,String stat,String method) {
		 if(dids == null || "".equals(dids)){
			 return  -1;
		 }
		 if(",".equals(dids)){
			 return  -1;
		 }
		 /**
		  * 截取最后一个逗号 
		  */
		 dids = dids.substring(0, dids.length() -1);
		 if(method != null && "zhiding".equals(method)){
			 return zhidingDocument(dbConn,dids,stat);
		 }
		 if(method != null && "jinghua".equals(method)){
			 return jinghuaDocument(dbConn,dids,stat);
		 }
		 if(method != null && "gaoliang".equals(method)){
			 return gaoliangDocument(dbConn,dids,stat);
		 }
		 if(method != null && "del".equals(method)){
			 return delDocument(dbConn,dids);
		 }
		 if(method != null && "gbdk".equals(method)){
			 return GBDKDocument(dbConn,dids,stat);
		 }
		return -1;
	 }
	 /**
	  * 置顶 文章
	  * @param dbConn
	  * @param boardId
	  * @return
	  */
	 public int zhidingDocument(Connection dbConn,  String dids,String stat) {
			String sql="update bbs_document_info set DOC_TOPSTATUES = '" + stat +"' where seq_id  in ("+dids+")";
			PreparedStatement ps=null;
			int result = 0;
			try{
				ps=dbConn.prepareStatement(sql);
				result = ps.executeUpdate();
				dbConn.commit();
			}catch(Exception ex){
				try {
					dbConn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return result;
		}
	 /**
	  * 
	  *高亮
	  * @param dbConn
	  * @param did
	  * @param stat
	  * @return
	  */
	 public int gaoliangDocument(Connection dbConn,  String did,String stat) {
			String sql="update bbs_document_info set DOC_LIGHT = '" + stat +"' where seq_id  in ("+did+")";
			PreparedStatement ps=null;
			int result = 0;
			try{
				ps=dbConn.prepareStatement(sql);
				result = ps.executeUpdate();
				dbConn.commit();
			}catch(Exception ex){
				try {
					dbConn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return result;
		}
	 /**
	  * 精华
	  * @param dbConn
	  * @param did
	  * @param stat
	  * @return
	  */
	 public int jinghuaDocument(Connection dbConn,  String did,String stat) {
			String sql="update bbs_document_info set DOC_SELECT = '" + stat +"' where seq_id  in ("+did+")";
			PreparedStatement ps=null;
			int result = 0;
			try{
				ps=dbConn.prepareStatement(sql);
				result = ps.executeUpdate();
				dbConn.commit();
			}catch(Exception ex){
				try {
					dbConn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return result;
		}
	 /**
	  * 关闭打开
	  * @param dbConn
	  * @param did
	  * @param stat
	  * @return
	  */
	 public int GBDKDocument(Connection dbConn,  String did,String stat) {
			String sql="update bbs_document_info set DOC_STATUES = '" + stat +"' where seq_id  in ("+did+")";
			PreparedStatement ps=null;
			int result = 0;
			try{
				System.out.println(sql);
				ps=dbConn.prepareStatement(sql);
				result = ps.executeUpdate();
				dbConn.commit();
			}catch(Exception ex){
				try {
					dbConn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return result;
		}
	 /**
	  * 删除 文章
	  * @param dbConn
	  * @param did
	  * @param stat
	  * @return
	  */
	 public int delDocument(Connection dbConn,  String dids) {
			String sql="delete from bbs_document_info where seq_id in ("+dids+")";
			PreparedStatement ps=null;
			int result = 0;
			try{
				ps=dbConn.prepareStatement(sql);
				result = ps.executeUpdate();
				dbConn.commit();
			}catch(Exception ex){
				try {
					dbConn.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} finally{
			      T9DBUtility.close(ps, null, null);
		    }
			return result;
		}
	 
}
