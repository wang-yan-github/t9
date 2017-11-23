package t9.mobile.news.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaNewsLogic {
 
  public List<Map<String,String>> refreshList(Connection conn , HttpServletRequest request, T9Person person ,String query , boolean flag , String CURRITERMS) throws Exception {
    List<Map<String,String>> list =  null;
    List<Map<String,String>> resultList =  new ArrayList<Map<String,String>>();
    if (flag) {
      list = T9QuickQuery.quickQueryList(conn, query , CURRITERMS);
    } else {
      list = T9QuickQuery.quickQueryList(conn, query);
    }
    for(Map<String,String> data:list){
      String NEWS_ID = data.get("SEQ_ID");
      String PROVIDER = data.get("PROVIDER");
      String SUBJECT = data.get("SUBJECT");
      String NEWS_TIME = data.get("NEWS_TIME");
      String TYPE_ID = data.get("TYPE_ID");
      String ATTACHMENT_ID = data.get("ATTACHMENT_ID");
      String ATTACHMENT_NAME = data.get("ATTACHMENT_NAME");
      String READERS = data.get("READERS");
      String SUBJECT_COLOR = data.get("SUBJECT_COLOR");
      String CLICK_COUNT = data.get("CLICK_COUNT");
      String LIKES = data.get("LIKES");
      
      // 文件下载
      String aId[] = null;
      String aName[] = null;
      List<String> DOWN_FILE_URL = new ArrayList<String>();
      if(!"".equals(ATTACHMENT_ID) && !"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_ID && null != ATTACHMENT_NAME){
          if(!"".equals(ATTACHMENT_ID) && null != ATTACHMENT_ID){
              ATTACHMENT_ID = ATTACHMENT_ID.substring(0, ATTACHMENT_ID.length()-1);
              aId = ATTACHMENT_ID.split(",");
          }
          if(!"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_NAME){
              ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0,ATTACHMENT_NAME.length()-1);
              aName = ATTACHMENT_NAME.split("\\*");
          }
          
          StringBuffer url = request.getRequestURL();
          String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
          for (int i = 0; i < aName.length; i++) {
              DOWN_FILE_URL.add(localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                      + aName[i]+"&attachmentId="
                      + aId[i]  +"&module=news");
          }
      }
      
      int COMMENT_NUMBER = 0;
      String TYPE_NAME = "";
      Map<String,String> rcd = T9QuickQuery.quickQuery(conn, "select class_desc from code_item where seq_id='"+TYPE_ID+"'");
      
      COMMENT_NUMBER = T9QuickQuery.getCount(conn,"select count(*) from news_comment where news_id='"+NEWS_ID+"'");
      
      TYPE_NAME = T9Utility.null2Empty(rcd.get("class_desc"));
      if(!T9Utility.isNullorEmpty(TYPE_NAME)){
        SUBJECT = "["+TYPE_NAME+"]"+SUBJECT;
      }
      String query1 = "SELECT USER_NAME from PERSON where SEQ_ID='"+PROVIDER+"'";
      List<Map<String,String>> list1 = T9QuickQuery.quickQueryList(conn, query1);
      String FROM_NAME = "";
      if(list1.size()!=0){
        FROM_NAME=list1.get(0).get("USER_NAME");
      }
      
      int has_attachment = 0;
      if(!T9Utility.isNullorEmpty(ATTACHMENT_ID) 
          && !T9Utility.isNullorEmpty(ATTACHMENT_NAME)){
        has_attachment = 1;
      }
      int read_flag = 0;
      if(!T9WorkFlowUtility.findId(READERS,person.getSeqId()+""))
      {
              read_flag = 0;
      }else{
              read_flag = 1;
      }
      
      String likesUid  = "";
      String likesNumber = "0";
      if(!"".equals(LIKES) && null != LIKES){
          String [] ls = LIKES.split("_");
          likesUid = ls[0];
          likesNumber = ls[1];
      }
      
      Map resultMap = new HashMap();
      resultMap.put("q_id", NEWS_ID); // 唯一标识
      resultMap.put("read_flag", "" + read_flag); // 阅读标记
      resultMap.put("subject", T9Utility.encodeSpecial(T9Utility.null2Empty(SUBJECT))); // 标题
      resultMap.put("subject_color", T9Utility.encodeSpecial(T9Utility.null2Empty(SUBJECT_COLOR))); // 标题颜色
      resultMap.put("send_time", NEWS_TIME); // 发布时间
      resultMap.put("from_name", T9Utility.encodeSpecial(T9Utility.null2Empty(FROM_NAME))); // 发布人
      resultMap.put("click_count", CLICK_COUNT); // 点击量
      resultMap.put("comment_number", COMMENT_NUMBER); // 评论数 
      resultMap.put("attachment_id", ATTACHMENT_ID); // 附件id
      List<Map<String, String>> downFile = new ArrayList<Map<String,String>>(); // 下载附件
      Map <String,String> fileUrl = new HashMap();
      for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
      	fileUrl.put("attachmentName", aName[i]); // 附件名
      	fileUrl.put("fileUrl", DOWN_FILE_URL.get(i)); // 附件url
      	downFile.add(fileUrl);
      }
      resultMap.put("down_file", downFile);
      resultMap.put("has_attachment", has_attachment); // 是否有附件：1：有附件 0:没有附件
      resultMap.put("likes_userId", likesUid);
      resultMap.put("likes_number", likesNumber);
      resultList.add(resultMap);
    }
    return resultList;
    
  }


public Map getNewsMap(Connection conn, HttpServletRequest request, T9Person person, String nEWS_ID) throws Exception {
    // TODO Auto-generated method stub
    Map map = new HashMap();
    String query = "SELECT * from NEWS where SEQ_ID='"+nEWS_ID+"' and PUBLISH='1' and (TO_ID='ALL_DEPT' or TO_ID='0' or "+T9DBUtility.findInSet(person.getDeptId()+"", "TO_ID")+" or "+T9DBUtility.findInSet(person.getUserPriv()+"", "PRIV_ID")+" or "+T9DBUtility.findInSet(person.getUserId()+"", "USER_ID")+")";
    Statement stmt = null;
    ResultSet rs = null;
    try{
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        String seqId = rs.getString("SEQ_ID");
        String subject =T9Utility.null2Empty( rs.getString("SUBJECT"));
        int clickCount = rs.getInt("CLICK_COUNT");
        String subjectColor = T9Utility.null2Empty(rs.getString("SUBJECT_FONT"));
        String anonymityYn = rs.getString("ANONYMITY_YN");
        String provider = T9Utility.null2Empty(rs.getString("PROVIDER"));
        String newsTime = T9Utility.getDateTimeStr(rs.getTimestamp("NEWS_TIME"));
        String format = T9Utility.null2Empty(rs.getString("FORMAT"));
        String readers = T9Utility.null2Empty(rs.getString("READERS"));
        String LIKES = T9Utility.null2Empty(rs.getString("LIKES"));
        //String compressContent = T9Utility.null2Empty(rs.getBlob("COMPRESS_CONTENT"));
        
        int typeId = rs.getInt("TYPE_ID");
        int COMMENTNUMBER = 0;
        COMMENTNUMBER = T9QuickQuery.getCount(conn,"select count(*) from news_comment where news_id='"+seqId+"'");
        
        clickCount++;
        Map<String,String> rcd = T9QuickQuery.quickQuery(conn, "select class_desc from code_item where seq_id='"+typeId+"'");
        
        String TYPE_NAME = T9Utility.null2Empty(rcd.get("class_desc"));
        if(!T9Utility.isNullorEmpty(TYPE_NAME)){
          subject = "["+TYPE_NAME+"]"+subject;
        }
        subject = "<font color='"+subjectColor+"'>" +subject+"</font>";
        
        String likesUid  = "";
        String likesNumber = "0";
        if(!"".equals(LIKES) && null != LIKES){
            String [] ls = LIKES.split("_");
            likesUid = ls[0];
            likesNumber = ls[1];
        }
        
        String content = "";
       // if (!T9Utility.isNullorEmpty(compressContent) 
           // && !"2".equals(format)) {
          //content = compressContent;
        //} else {
       // }
        content = rs.getString("CONTENT");
        
        String ATTACHMENT_ID = T9Utility.null2Empty(rs.getString("ATTACHMENT_ID"));
        String ATTACHMENT_NAME = T9Utility.null2Empty(rs.getString("ATTACHMENT_NAME"));
        
       // ----------------------文件下载 satrt -----------------------------------------
        String aId[] = null;
        String aName[] = null;
        List<String> DOWN_FILE_URL = new ArrayList();
        if(!"".equals(ATTACHMENT_ID) && !"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_ID && null != ATTACHMENT_NAME){
            if(!"".equals(ATTACHMENT_ID) && null != ATTACHMENT_ID){
                ATTACHMENT_ID = ATTACHMENT_ID.substring(0, ATTACHMENT_ID.length()-1);
                aId = ATTACHMENT_ID.split(",");
            }
            if(!"".equals(ATTACHMENT_NAME) && null != ATTACHMENT_NAME){
                ATTACHMENT_NAME = ATTACHMENT_NAME.substring(0,ATTACHMENT_NAME.length()-1);
                aName = ATTACHMENT_NAME.split("\\*");
            }
            
            StringBuffer url = request.getRequestURL();
            String localhostUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
            for (int i = 0; i < aName.length; i++) {
                DOWN_FILE_URL.add(localhostUrl + "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
                        + aName[i]+"&attachmentId="
                        + aId[i]  +"&module=news");
            }
        }
        // ----------------------文件下载 end -----------------------------------------
        String update = "";
        if (!T9WorkFlowUtility.findId(readers, person.getSeqId() + "")) {
          readers += person.getSeqId() + ",";
          update = "update news set READERS='"+readers+"',CLICK_COUNT='"+clickCount+"' where SEQ_ID='"+ nEWS_ID +"'";
        } else {
          update = "update NEWS set CLICK_COUNT='"+clickCount+"' where SEQ_ID='"+ nEWS_ID +"'";
        }
        T9MobileUtility.updateSql(conn, update);
        
        T9PersonLogic lg = new T9PersonLogic();
        String fromName = lg.getNameBySeqIdStr(provider, conn);
        
        map.put("seqId", seqId);
        map.put("typeName", TYPE_NAME);
        map.put("subject", subject);
        map.put("fromName", fromName);
        map.put("newsTime", newsTime);
        map.put("clickCount", clickCount);
        map.put("content", content);
        map.put("attachmentId", ATTACHMENT_ID);
        //map.put("attachmentName", ATTACHMENT_NAME);
        map.put("commentNumber", COMMENTNUMBER);
        map.put("anonymityYn", anonymityYn);
        map.put("format", format);
        
        // ------------------ 下载downFileUrl start -----------------------------------
        List<Map<String, String>> downFile = new ArrayList<Map<String,String>>();
        Map <String,String> fileUrl = new HashMap();
        for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
        	fileUrl.put("attachmentName", aName[i]);
        	fileUrl.put("fileUrl", DOWN_FILE_URL.get(i));
        	downFile.add(fileUrl);
        }
        map.put("down_file", downFile);
        map.put("likes_userId", likesUid);
        map.put("likes_number", likesNumber);
       // ------------------ 下载downFileUrl end -----------------------------------
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stmt, rs, null);
    }
    return map;
  }


public Map saveLikes(Connection conn, String newsId, int userId) {
	String likes = "";
	String sql = "";
	Map<String,String> map = new HashMap<String, String>();
	Map<String,String> reMap = new HashMap<String, String>(); 
	try {
		if(!"".equals(newsId) && null != newsId){
			sql = "SELECT LIKES FROM NEWS WHERE SEQ_ID = '" + newsId + "'";
			map = T9QuickQuery.quickQuery(conn, sql);
			likes = T9Utility.null2Empty(map.get("LIKES"));
		}
		
		if(!"".equals(likes) && null != likes){ 
			String [] ls = likes.split("_");
			String ids = ls[0];
			int number = Integer.parseInt(ls[1]);
			String isLikes = "no";
			if(ids!=null && ids !=""){
				String[] id = ids.split(",");
				for(int i=0; i<id.length; i++){
					if((userId+"").equalsIgnoreCase(id[i])){
						isLikes = "yes";
					}
				}
			}
			if(isLikes.equals("yes")){ // 用户已点赞
				reMap.put("likes_msg", "用户已点赞");
				reMap.put("likes_number", String.valueOf(number));
			}else{ // 未点赞
				ids += userId + ",";
				number += 1;
				sql = "UPDATE NEWS SET LIKES = '" + ids + "_" + String.valueOf(number) + "' WHERE SEQ_ID = '" + newsId + "'";
				T9MobileUtility.updateSql(conn, sql);
				reMap.put("likes_msg", "用户点赞成功");
				reMap.put("likes_number", String.valueOf(number));
			}
		}else{ // 第一次点赞
			likes = userId +"," + "_" + 1;
			sql = "UPDATE NEWS SET LIKES = '" + likes + "' WHERE SEQ_ID = '" + newsId + "'";
			T9MobileUtility.updateSql(conn, sql);
			reMap.put("likes_msg", "点赞成功");
			reMap.put("likes_number", String.valueOf(1));
		}
		return reMap;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}
}
