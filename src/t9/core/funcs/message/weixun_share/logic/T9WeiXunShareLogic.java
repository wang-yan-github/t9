package t9.core.funcs.message.weixun_share.logic;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.message.weixun_share.data.T9WeixunShare;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9WeiXunShareLogic{
	private static Logger log = Logger.getLogger("t9.core.funcs.message.weixun_share.act");
	public void addWeiXunShare(Connection dbConn, T9WeixunShare weiXun) throws Exception {
/*	    try {
	        T9ORM orm = new T9ORM();
	        orm.saveSingle(dbConn, weiXun);
	      } catch (Exception ex) {
	        throw ex;
	      }*/
		
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	    /*  //String[] addTime=T9Utility.getDateLimitStr();
	      String sql = "insert into WEIXUN_SHARE" +
	          " ( UID,CONTENT,ADDTIME)" +
	          " values( ?,?,?)";
	      ps = dbConn.prepareStatement(sql);
	      ps.setInt(1, weiXun.getuId());
	      ps.setString(2, weiXun.getContent());
	      //ps.setLong(3, addTime);
	      System.out.println(sql);
	      ps.executeUpdate();
	      */
	    	
	    	T9WeixunShare ws =new T9WeixunShare();
	    //	ws.setTopics(topics)
	    //	ws.setAddTime(T9Utility.getCurDateTimeStr());
         //   ws.setContent(content);
	    	
	    	
	    } catch(Exception ex) {
	      throw ex;
	    } finally {
	      T9DBUtility.close(ps, rs, log);
	    }
	}
	
	
	 public String getWeiXunContent(Connection conn,Map request,int userId,int pageNo,int pageSize) throws Exception{
		    T9PageDataList data = toWeiXunJson(conn, request, userId,pageNo,pageSize,false);
		     return data.toJson();
		  }
	 
   public String getWeiXunContentPerson(Connection conn,Map request,int userId,int pageNo,int pageSize) throws Exception{
     T9PageDataList data = toWeiXunJsonPerson(conn, request, userId,pageNo,pageSize,false);
      return data.toJson();
   }
		  
   public String getWeiXunContentMention(Connection conn,Map request,int userId,int pageNo,int pageSize) throws Exception{
     T9PageDataList data = toWeiXunJsonMention(conn, request, userId,pageNo,pageSize,false);
      return data.toJson();
   }
   
   public String getWeiXunContentTopic(Connection conn,Map request,String topic,int pageNo,int pageSize) throws Exception{
     T9PageDataList data = toWeiXunJsonTopic(conn, request, topic,pageNo,pageSize,false);
      return data.toJson();
   }
    

   public  FileInputStream getUserAvatorStream(Connection conn,String seqId,String webPath)throws Exception{
     FileInputStream result = null;
     T9ORM orm = new T9ORM();
     try{
       T9Person person = (T9Person)orm.loadObjSingle(conn, T9Person.class, Integer.parseInt(seqId));
       String avator=person.getAuatar();
       String sex=person.getSex();
       String Filepath=webPath+"/attachment/avatar/"+avator;
       Filepath=this.getFilePath(Filepath);
       File file =new File(Filepath);
       if(!file.exists()){
         String fileStr=webPath+"/core/styles/imgs/avatar/1.gif";
         if("1".equals(sex)){
           fileStr=webPath+"/core/styles/imgs/avatar/g.gif";
         }
         fileStr=this.getFilePath(fileStr);
         file=new File(fileStr);
       }
       result= new FileInputStream(file);
     }catch(Exception ex){
       ex.printStackTrace();
     }
     
     return result;
   }
   
	  
	  /**
	   * 装换路径表示
	   */
	  public String getFilePath(String filePath)throws Exception{

	    return filePath.replaceAll("//", File.separator).replace("/", File.separator).replace("\\", File.separator);
	    
	  }
	  
	 
	 
		 
    public T9PageDataList toWeiXunJson(Connection conn,Map request,int userId,int pageIndex,int pageSize,boolean isQuery) throws Exception{
      String whereStr =  "";
      String sql = "select PERSON.SEQ_ID,WEIXUN_SHARE.seq_id,WEIXUN_SHARE.CONTENT,PERSON.USER_NAME,ADD_TIME,BROADCAST_IDS from WEIXUN_SHARE,PERSON " +
      " where  WEIXUN_SHARE.USER_ID = PERSON.SEQ_ID and "+T9DBUtility.isFieldNotNull("WEIXUN_SHARE.CONTENT")+"  order by ADD_TIME desc";
      String nameStr = "seqId,wxId,content,userName,addtime,broadcastIds";
      T9PageQueryParam queryParam = new T9PageQueryParam();
      queryParam.setNameStr(nameStr);
      queryParam.setPageIndex(pageIndex);
      queryParam.setPageSize(pageSize);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      pageDataList=this.getBroadcast(conn, pageDataList);//获取转播内容
      return pageDataList;
    }
    
    public T9PageDataList toWeiXunJsonPerson(Connection conn,Map request,int userId,int pageIndex,int pageSize,boolean isQuery) throws Exception{
      String whereStr =  "";
      String sql = "select PERSON.SEQ_ID,WEIXUN_SHARE.seq_id,WEIXUN_SHARE.CONTENT,PERSON.USER_NAME,ADD_TIME,BROADCAST_IDS from WEIXUN_SHARE,PERSON " +
      " where  WEIXUN_SHARE.USER_ID = PERSON.SEQ_ID and  "+T9DBUtility.isFieldNotNull("WEIXUN_SHARE.CONTENT")+" and WEIXUN_SHARE.user_id='"+userId+"'  order by ADD_TIME desc";
      String nameStr = "seqId,wxId,content,userName,addtime,broadcastIds";
      T9PageQueryParam queryParam = new T9PageQueryParam();
      queryParam.setNameStr(nameStr);
      queryParam.setPageIndex(pageIndex);
      queryParam.setPageSize(pageSize);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      pageDataList=this.getBroadcast(conn, pageDataList);//获取转播内容
      return pageDataList;
    }

    public T9PageDataList toWeiXunJsonMention(Connection conn,Map request,int userId,int pageIndex,int pageSize,boolean isQuery) throws Exception{
      String whereStr =  "";
      String sql = "select PERSON.SEQ_ID,WEIXUN_SHARE.seq_id,WEIXUN_SHARE.CONTENT,PERSON.USER_NAME,ADD_TIME,BROADCAST_IDS from WEIXUN_SHARE,PERSON " +
      " where  WEIXUN_SHARE.USER_ID = PERSON.SEQ_ID and "+T9DBUtility.isFieldNotNull("WEIXUN_SHARE.CONTENT")+"  and "+T9DBUtility.findInSet(""+userId, "MENTIONED_IDS")+"  order by ADD_TIME desc";
      String nameStr = "seqId,wxId,content,userName,addtime,broadcastIds";
      T9PageQueryParam queryParam = new T9PageQueryParam();
      queryParam.setNameStr(nameStr);
      queryParam.setPageIndex(pageIndex);
      queryParam.setPageSize(pageSize);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      pageDataList=this.getBroadcast(conn, pageDataList);//获取转播内容
      return pageDataList;
    }
    
    
    public T9PageDataList toWeiXunJsonTopic(Connection conn,Map request,String topic,int pageIndex,int pageSize,boolean isQuery) throws Exception{
      String whereStr =  "";
      String sql = "select PERSON.SEQ_ID,WEIXUN_SHARE.seq_id,WEIXUN_SHARE.CONTENT,PERSON.USER_NAME,ADD_TIME,BROADCAST_IDS from WEIXUN_SHARE,PERSON " +
      " where  WEIXUN_SHARE.USER_ID = PERSON.SEQ_ID and WEIXUN_SHARE.CONTENT like '%"+topic+"%'  order by ADD_TIME desc";
      String nameStr = "seqId,wxId,content,userName,addtime,broadcastIds";
      T9PageQueryParam queryParam = new T9PageQueryParam();
      queryParam.setNameStr(nameStr);
      queryParam.setPageIndex(pageIndex);
      queryParam.setPageSize(pageSize);
      T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
      pageDataList=this.getBroadcast(conn, pageDataList);//获取转播内容
      return pageDataList;
    }
	
	public String getweiXunShare(Connection conn){
	  Statement stmt=null;
	  ResultSet rs=null;
	  String data="";
	  try{
	    String sql=" select * from weixun_share_topic order by seq_id desc ";
	    stmt=conn.createStatement();
	    rs=stmt.executeQuery(sql);
	    int num=0;
	    while(rs.next()){
	      data+="{";
        data+="seqId:'"+rs.getString("seq_id")+"',";
        data+="name:'"+rs.getString("TOPIC_NAME")+"'";
        data+="},";
        num++;
        if(num>8){
          break;
        }
	    }
	  }catch(Exception e){
	    e.printStackTrace();
	  }
	  if(data.endsWith(",")){
	    data=data.substring(0, data.length()-1);
	  }
	  
	  return data;
	}
	
	/**
	 *brocast 
	 **/
	public T9PageDataList getBroadcast(Connection conn, T9PageDataList data )throws Exception{
	  T9PageDataList reData=new T9PageDataList();
	  reData.setTotalRecord(data.getTotalRecord());
	  for(int i=0;i<data.getRecordCnt();i++){
	     T9DbRecord record = data.getRecord(i);
	     String BroadcastId=(String)record.getValueByName("broadcastIds");
	     String BCContent=this.Broadcast(conn, BroadcastId);//获取转播的内容
	     record.addField("broadcast", BCContent); 
	     reData.addRecord(record);
	  }
   
	  return reData;
	}	
	
	public String Broadcast(Connection dbConn,String wxid) throws NumberFormatException, Exception{
	  if(T9Utility.isNullorEmpty(wxid)){
	    return "";
	  }
	  String bData="";
	   T9ORM orm = new T9ORM();
	   T9WeixunShare ws =(T9WeixunShare)orm.loadObjSingle(dbConn, T9WeixunShare.class, Integer.parseInt(wxid));
	   
	   bData+=" userName:'"+this.getUserName(dbConn, ws.getUserId()+"")+"', ";
	   bData+=" uid:'"+ws.getUserId()+"', ";
	   bData+=" content:'"+T9Utility.encodeSpecial(ws.getContent())+"', ";
	   bData+=" time:'"+ws.getAddTime()+"', ";
	   bData+=" id:'"+ws.getSeqId()+"', ";
	   bData+=" num:'"+this.getBrocatNum(dbConn, wxid)+"' ";
	   
	   return "{"+bData+"}";
	   
	}
	
	
  public String getBrocatNum(Connection conn,String wxid){
    Statement stmt=null;
    ResultSet rs=null;
    String data="";
    try{
      String sql=" select count(*) from weixun_share where "+T9DBUtility.findInSet(wxid, "BROADCAST_IDS")+" ";
      stmt=conn.createStatement();
      rs=stmt.executeQuery(sql);
      if(rs.next()){
        data=rs.getString(1);
      }
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
   
    return data;
  }
	
	 public String getweiXunById(Connection conn,String wxid){
	    Statement stmt=null;
	    ResultSet rs=null;
	    String data="";
	    try{
	      String sql=" select * from weixun_share where seq_id='"+wxid+"' order by seq_id desc ";
	      stmt=conn.createStatement();
	      rs=stmt.executeQuery(sql);
	      while(rs.next()){
	        data+="{";
	        data+="seqId:'"+rs.getString("seq_id")+"',";
	     //   data+="name:'"+rs.getString("TOPIC_NAME")+"'";
	        data+="userId:'"+rs.getString("user_id")+"', ";
	        data+="ADD_TIME:'"+rs.getString("ADD_TIME")+"', ";
	        data+="content:'"+rs.getString("content")+"',";
	        data+="userName:'"+this.getUserName(conn, rs.getString("user_id"))+"'";
	        data+="}"; 
	      }
	    }catch(Exception e){
	      e.printStackTrace();
	    }
	   
	    return data;
	  }
	
	
	 public String getDeptName(Connection conn,String dId){
	    String data="";
	    T9ORM orm = new T9ORM();
	    T9Department dp;
      try {
        dp = (T9Department)orm.loadObjSingle(conn, T9Department.class, Integer.parseInt(dId));
        data=dp.getDeptName();
      } catch (NumberFormatException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
	   
	    return data;
	  }
	 
	 
   public String getUserName(Connection conn,String dId){
     String data="";
     T9ORM orm = new T9ORM();
     T9Person dp;
     try {
       dp = (T9Person)orm.loadObjSingle(conn, T9Person.class, Integer.parseInt(dId));
       data=dp.getUserName();
     } catch (NumberFormatException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
     } catch (Exception e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
     }
    
     return data;
   }
	  
}

