package t9.cms.bbs.board.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import t9.cms.bbs.board.data.T9BbsArea;
import t9.cms.bbs.board.data.T9BbsBoard;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9BbsBoardLogic {
  public static final String attachmentFolder = "cms";
  
  
  /**
   * bbs 版块
   * 
   */
  public void addBoard(Connection dbConn, T9BbsBoard board, T9Person person) throws Exception{
    T9ORM orm = new T9ORM();
    
    board.setCreateId(person.getSeqId());
    board.setCreateTime(T9Utility.parseTimeStamp());
    board.setParentBoardId(0);
    
    orm.saveSingle(dbConn, board);
  }
  
  
  /**
   * bbs 版块
   * 
   */
  public void modifyBoard(Connection dbConn, T9BbsBoard board, T9Person person) throws Exception{
    T9ORM orm = new T9ORM();
    
    board.setCreateId(person.getSeqId());
    board.setCreateTime(T9Utility.parseTimeStamp());
    board.setParentBoardId(0);
    
    orm.updateSingle(dbConn, board);
  }
  
  
  /**
   * bbs 专区
   * 
   */
  public void addArea(Connection dbConn, T9BbsArea area, T9Person person) throws Exception{
    T9ORM orm = new T9ORM();
    
    area.setCreateId(person.getSeqId());
    area.setCreateTime(T9Utility.parseTimeStamp());
    
    orm.saveSingle(dbConn, area);
  }
  
  
  /**
   * bbs 专区
   * 
   */
  public String getArea(Connection dbConn) throws Exception{
    try {
      StringBuffer data = new StringBuffer("[");
      String sql = " select SEQ_ID, AREA_NAME, AREA_MANAGER, AREA_INDEX, CREATE_ID, CREATE_TIME from bbs_area order by AREA_INDEX";
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          data.append("{seqId:"+rs.getInt("SEQ_ID")+","
                    + "areaName:\""+rs.getString("AREA_NAME")+"\","
                    +	"areaIndex:\""+rs.getString("AREA_INDEX")+"\"},");
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
   * 获取区域和板块列表树
   * @param dbConn
   * @param bid
   * @return
   * @throws Exception
   */
  public String getLeftTree(Connection dbConn,String bid) throws Exception{
	    try {
	      StringBuffer data = new StringBuffer("{root:[");
	      StringBuffer sb1 =null;
	      String sql = "SELECT b1.SEQ_ID, b1.AREA_NAME FROM BBS_AREA  b1 ORDER BY b1.AREA_INDEX DESC";
	      String sql1 = "SELECT b1.SEQ_ID,b1.BOARD_NAME FROM BBS_BOARD b1 WHERE b1.AREA_ID = '";
	      PreparedStatement ps = null;
	      PreparedStatement ps1 = null;
	      ResultSet rs = null;
	      ResultSet rs1 = null;
	      boolean flag = false;
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	        	 data.append("{id:'"+rs.getInt("SEQ_ID")+"',");
	        	 data.append("name:'"+rs.getString("AREA_NAME")+"',");
	        	 data.append("children:[");
		        	ps1 = dbConn.prepareStatement(sql1+rs.getInt("SEQ_ID")+"'");
		 	        rs1 = ps1.executeQuery();
		 	        String s1 = null;
		 	        s1 = "";
		 	        sb1 = new StringBuffer("");
		 	        while (rs1.next()) {
		 	        	sb1.append(",{id:'"+rs1.getString("SEQ_ID")+"',");
		 	        	sb1.append("name:'"+rs1.getString("BOARD_NAME")+"'}");
		 	        }
		 	        if(sb1 != null && sb1.length() > 1){
		 	        	s1 = sb1.substring(1);
		 	        }
		 	       data.append(s1+"]},");
		 	       ps1.close();
		 	       ps1 = null;
		 	       rs1.close();
		 	       rs1 = null;
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
	      data.append("]}");
	      return data.toString();
	    } catch (Exception e) {
	      throw e;
	    }
	  }
  
  
  /**
   * bbs 版块
   * 
   */
  public String getBoard(Connection dbConn, int areaId) throws Exception{
    try {
      StringBuffer data = new StringBuffer("[");
      String sql = " select SEQ_ID, AREA_ID, BOARD_NAME,BOARD_ABSTRACT, BOARD_MANAGER, LOCK_DAY, ANONYMITY, "
      		       + " IS_CHECK, BOARD_INDEX, CREATE_ID, CREATE_TIME, PARENT_BOARD_ID, dept, role, user_ids, IMAGE_URL from bbs_board"
      		       + " where AREA_ID = "+ areaId +" order by BOARD_INDEX asc";
      PreparedStatement ps = null;
      ResultSet rs = null;
      boolean flag = false;
      try {
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        while (rs.next()) {
          data.append("{seqId:"+rs.getInt("SEQ_ID")+","
                     + "areaId:"+rs.getInt("AREA_ID")+","
                     + "boardIndex:"+rs.getInt("BOARD_INDEX")+","
                     + "imageUrl:\""+rs.getString("IMAGE_URL")+"\","
                     + "boardManager:\""+queryUsername(dbConn, rs.getString("BOARD_MANAGER"))+"\","
                     + "boardName:\""+rs.getString("BOARD_NAME")+"\"},");
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
   * bbs 版块
   * 
   */
  public String getBoardById(Connection dbConn, T9BbsBoard board, StringBuffer data) throws Exception{
    
    String boardManagerDesc = queryUsername(dbConn, board.getBoardManager());
    String deptDesc = "";
    if("0".equals(board.getDept())){
      deptDesc = "全体部门";
    }
    else{
      deptDesc = queryDeptname(dbConn, board.getDept());
    }
        
    String roleDesc = queryPrivname(dbConn, board.getRole());
    String userDesc = queryUsername(dbConn, board.getUserIds());
    
    if(data.length() > 3){
      data = data.deleteCharAt(data.length() - 1);
      data.append(",\"boardManagerDesc\":\""+boardManagerDesc+"\"");
      data.append(",\"deptDesc\":\""+deptDesc+"\"");
      data.append(",\"roleDesc\":\""+roleDesc+"\"");
      data.append(",\"userDesc\":\""+userDesc+"\"}");
    }
       return data.toString();
  }
  /**
   * 板块信息 用于板块页
   * @param dbConn
   * @param board
   * @param data
   * @return
   * @throws Exception
   */
  public String getBoardInfoById(Connection dbConn, T9BbsBoard board, StringBuffer data) throws Exception{
	    
	    String boardManagerDesc = queryUsername(dbConn, board.getBoardManager());
	    String deptDesc = "";
	        
	    int todayNum = getTodayCommentsByBoardId(dbConn,board.getSeqId());
	    int topicNum = getCommentNumsAndRnum(dbConn,board.getSeqId());
	    String areaName = getAreaName(dbConn,board.getAreaId());
	    if(data.length() > 3){
	      data = data.deleteCharAt(data.length() - 1);
	      data.append(",\"boardManagerDesc\":\""+boardManagerDesc+"\"");
	      data.append(",\"boardName\":\""+board.getBoardName()+"\"");
	      data.append(",\"todayNum\":\""+todayNum+"\"");
	      data.append(",\"areaName\":\""+areaName+"\"");
	      data.append(",\"topicNum\":\""+topicNum+"\"}");
	    }
	    return data.toString();
	  }
  
  /**
   * 获取部门名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryDeptname(Connection dbConn, String seqIds) throws Exception {
    if(T9Utility.isNullorEmpty(seqIds)){
      return "";
    }
    if (seqIds.endsWith(",")) {
      seqIds = seqIds.substring(0, seqIds.length() - 1);
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select dept_name from department where seq_id in ("+ seqIds +")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String deptName = rs.getString("dept_name");
        sb.append(deptName+",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if(sb.length() > 1){
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

 /**
  * 获取论坛首页分区及板块信息 
  * @param conn
  * @return
  */
  
  public String getBbsAreaAndBoard(Connection conn){
	  StringBuffer sb=new StringBuffer("[");
	   PreparedStatement ps=null;
	   ResultSet rs=null;
	   PreparedStatement ps1=null;
	   ResultSet rs1=null;
	   String boardAbstract="";
	   String sql="";
	   try{
		   sql="select seq_id,area_name from bbs_area";
		   ps=conn.prepareStatement(sql);
		   rs=ps.executeQuery();
		   while(rs.next()){
			   sb.append("{\"areaId\":"+rs.getInt(1)+",");
			   sb.append("\"areaName\":\""+rs.getString(2)+"\",");
			   sb.append("\"board\":[");
			   String sqlStr="select seq_id,image_url, board_name,board_abstract ,board_manager from bbs_board where area_id="+rs.getInt(1);
			   ps1=conn.prepareStatement(sqlStr);
			   rs1=ps1.executeQuery();
			   while(rs1.next()){
				   int num=this.getTodayCommentsByBoardId(conn, rs1.getInt(1));
				   String[] board=this.getLastComment(conn, rs1.getInt(1));
				   int totalComments=this.getCommentNumsAndRnum(conn,  rs1.getInt(1));
				   int replyNum=this.getCommenRnum(conn,  rs1.getInt(1));
				   if(rs1.getString(4)==null){
					   boardAbstract="";
				   }else{
					   boardAbstract=rs1.getString(4);
				   }
				   sb.append("{\"boardId\":"+rs1.getInt(1)+",");
				   sb.append("\"imageUrl\":\""+rs1.getString(2)+"\",");
				   sb.append("\"boardAbstract\":\""+boardAbstract+"\",");
				   String boardManager=this.queryUsername(conn, rs1.getString(5));
				   sb.append("\"boardManager\":\""+boardManager+"\",");
				   sb.append("\"lastCommentTitle\":\""+board[0]+"\",");
				   sb.append("\"lastCommentAuther\":\""+board[2]+"\",");
				   sb.append("\"lastCommentTime\":\""+board[1]+"\",");
				   sb.append("\"totalComments\":"+totalComments+",");
				   sb.append("\"totalReply\":"+replyNum+",");
				   sb.append("\"todayNum\":"+num+",");
				   sb.append("\"boardName\":\""+rs1.getString(3)+"\"},");
			   }
			   if(sb.length()>3 && rs1.next()){
				   sb.deleteCharAt(sb.length()-1);
			   }
			 /*  if(!rs1.first()){
				   sb.append("[");
			   }*/
			   sb.append("]},");
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
   * 获取角色名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryPrivname(Connection dbConn, String seqIds) throws Exception {
    if(T9Utility.isNullorEmpty(seqIds)){
      return "";
    }
    if (seqIds.endsWith(",")) {
      seqIds = seqIds.substring(0, seqIds.length() - 1);
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select priv_name from user_priv where seq_id in ("+ seqIds +")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String privName = rs.getString("priv_name");
        sb.append(privName+",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if(sb.length() > 1){
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  /**
   * 获取人员名称
   * 
   * @param dbConn
   * @param seqIds
   * @return String
   */
  public String queryUsername(Connection dbConn, String seqIds) throws Exception {
    if(T9Utility.isNullorEmpty(seqIds)){
      return "";
    }
    if (seqIds.endsWith(",")) {
      seqIds = seqIds.substring(0, seqIds.length() - 1);
    }
    StringBuffer sb = new StringBuffer("");
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select user_name from person where seq_id in ("+ seqIds +")";
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String userName = rs.getString("user_name");
        sb.append(userName+",");
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if(sb.length() > 1){
      sb = sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  
  
/**
 * 获取帖子今日数，昨日数，帖子总数
 * @param dbConn
 * @return
 */

public String getBbsComments(Connection dbConn) {
	StringBuffer data=new StringBuffer("{");
	String sql="";
	String tsql="";
	String ysql="";
	PreparedStatement ps=null;
	ResultSet rs=null;
	PreparedStatement tps=null;
	ResultSet trs=null;
	PreparedStatement yps=null;
	ResultSet yrs=null;
	String curDatetime=T9Utility.getCurDateTimeStr();

	try{
	    Date time=T9Utility.getDayBefore(curDatetime,1);
	    String yesterdayTime=T9Utility.getDateTimeStr(time);
		sql="select  count(*) from bbs_comment";
		ysql=" select count(*) from bbs_comment where "+T9DBUtility.getDateFilter("CREATE_TIME",yesterdayTime.substring(0,10) , ">=")
				+" and "+T9DBUtility.getDateFilter("CREATE_TIME",yesterdayTime.substring(0,10)+" 23:59:59" , "<=");
		tsql="select count(*) from bbs_comment  where "+T9DBUtility.getDateFilter("CREATE_TIME",curDatetime.substring(0,10) , ">=")
				+" and " +T9DBUtility.getDateFilter("CREATE_TIME",curDatetime.substring(0,10)+" 23:59:59" , "<=");
		ps=dbConn.prepareStatement(sql);
		rs=ps.executeQuery();
		tps=dbConn.prepareStatement(tsql);
		trs=tps.executeQuery();
		yps=dbConn.prepareStatement(ysql);
		yrs=yps.executeQuery();
		while(trs.next()){
			data.append("\"todayComments\":"+trs.getInt(1));
		}
		while(yrs.next()){
			data.append(",\"yesterdayComments\":"+yrs.getInt(1));
		}
		while(rs.next()){
			data.append(",\"totalComments\":"+rs.getInt(1));
		}
		data.append("}");
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return data.toString();
}

/**
 * 根据板块Id获取当日帖子数
 * @param boardId
 * @return
 */
 public int getTodayCommentsByBoardId(Connection conn,int boardId){
	int nums=0;
	PreparedStatement ps=null;
	ResultSet rs=null;
	String sql="";
	String curDatetime=T9Utility.getCurDateTimeStr();
	try{
		sql="select count(*) from bbs_comment  where board_id=?  and  "+T9DBUtility.getDateFilter("CREATE_TIME",curDatetime.substring(0,10) , ">=")
				+" and " +T9DBUtility.getDateFilter("CREATE_TIME",curDatetime.substring(0,10)+" 23:59:59" , "<=");
		ps=conn.prepareStatement(sql);
		ps.setInt(1, boardId);
		rs=ps.executeQuery();
		while(rs.next()){
			nums=rs.getInt(1);
		}
	}catch(Exception ex){
		ex.printStackTrace();
	}
	return nums;
}
 /**
  * 获取当前板块最后一贴的信息
  * @param conn
  * @param boardId
  * @return
  */
 public String[] getLastComment(Connection conn,int boardId){
	 String[] lastComment={"","",""};
	 PreparedStatement ps=null;
	 ResultSet rs=null;
	 String sql="";
	 try{
		 sql="select seq_id,comment_title,create_time,create_id from bbs_comment where board_id=? order by seq_id desc";
		 ps=conn.prepareStatement(sql);
		 ps.setInt(1,boardId);
		 rs=ps.executeQuery();
		 if(rs.next()){
			 if(rs.getString(2)==null || "null".equals(rs.getString(2)) || "".equals(rs.getString(2))){
				 lastComment[0]="";
			 }else{
				 lastComment[0]=rs.getString(2);
			 }
			 if(rs.getString(3)==null || "null".equals(rs.getString(3))||"".equals(rs.getString(3))){
				 lastComment[1]="";
			 }else{
				 lastComment[1]=rs.getString(3).substring(0,19);
			 }
			 if(rs.getString(4)==null || "null".equals(rs.getString(4)) || "".equals(rs.getString(4))){
				 lastComment[2]="";
			 }else{
				 lastComment[2]=this.queryUsername(conn, rs.getString(4));
			 }
			 
		 }
	 }catch(Exception ex){
		 ex.printStackTrace();
	 }
	 return lastComment;
 }
 /**
  * 
  * 获取当前板块的总帖子数
  * @param conn
  * @param boardId
  * @return
  */
 public int getCommentNumsAndRnum(Connection conn,int boardId){
	 int nums=0;
	 PreparedStatement ps=null;
	 ResultSet rs=null;
	 String sql="";
	 try{
		 sql="select count(*) from bbs_comment where board_id=?";
		 ps=conn.prepareStatement(sql);
		 ps.setInt(1,boardId);
		 rs=ps.executeQuery();
		 while(rs.next()){
			 nums=rs.getInt(1);
		 }
	 }catch(Exception ex){
		 ex.printStackTrace();
	 }
	 return nums;
 }
 /**
  * 获取 区域名字
  * @param conn
  * @param areaId
  * @return
  */
 public String getAreaName(Connection conn,int areaId){
	 String areaName = "";
	 PreparedStatement ps=null;
	 ResultSet rs=null;
	 String sql="";
	 try{
		 sql="select AREA_NAME from Bbs_area where seq_id=?";
		 ps=conn.prepareStatement(sql);
		 ps.setInt(1,areaId);
		 rs=ps.executeQuery();
		 if(rs.next()){
			 areaName = rs.getString("AREA_NAME");
		 }
	 }catch(Exception ex){
		 ex.printStackTrace();
	 }
	 return areaName;
 }
 /**
  * 获取当前板块的回帖总数
  * @param conn
  * @param boardId
  * @return
  */
 public int getCommenRnum(Connection conn,int boardId){
	 int nums=0;
	 PreparedStatement ps=null;
	 ResultSet rs=null;
	 String sql="";
	 try{
		 sql="select count(*) from bbs_comment where board_id=? and PARENT_COMMENT_ID!=0";
		 ps=conn.prepareStatement(sql);
		 ps.setInt(1,boardId);
		 rs=ps.executeQuery();
		 while(rs.next()){
			 nums=rs.getInt(1);
		 }
	 }catch(Exception ex){
		 ex.printStackTrace();
	 }
	 return nums;
 }
  /**
   * 判當前用戶是不是版主
   * @param conn
   * @param person
   * @param bid
   * @return
   */
  public int isBoardManager(Connection conn , T9Person person,String bid){
	  int flag=0;
	  PreparedStatement ps=null;
	  ResultSet rs=null;
	  String sql="";
	  String ids="";
	  String curUserId=String.valueOf(person.getSeqId());
	  try{
		  sql="select board_manager from bbs_board where seq_id="+bid;
		  ps=conn.prepareStatement(sql);
		  rs=ps.executeQuery();
		  while(rs.next()){
			  ids=rs.getString(1);
		  }
		  String[] managerIds=ids.split(",");
		  for(int i=0; i<managerIds.length;i++){
			  if(managerIds[i].equals(curUserId)){
				  flag=1;
				  continue;
			  }
		  }
	  }catch(Exception ex){
		  ex.printStackTrace();
	  }
	  return flag;
  }
}