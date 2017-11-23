package t9.mobile.news.logic;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaCommentLogic {
 
  public List<Map<String,String>> refreshList(Connection conn , T9Person person ,String query , boolean flag , String CURRITERMS) throws Exception {
    List<Map<String,String>> list =  null;
    List<Map<String,String>> reusltList = new ArrayList<Map<String,String>>();
    if (flag)  {
      list = T9QuickQuery.quickQueryList(conn, query , CURRITERMS);
    } else {
      list = T9QuickQuery.quickQueryList(conn, query);
    }
    for(Map<String,String> data:list){
      String SEQ_ID = data.get("SEQ_ID");
      String NEWS_ID = data.get("NEWS_ID");
      String USER_ID = data.get("USER_ID");
      String NICK_NAME = data.get("NICK_NAME");
      String CONTENT = data.get("CONTENT");
      String RE_TIME = data.get("RE_TIME");
      
      Map resultMap = new HashMap();
      resultMap.put("q_id", SEQ_ID);
      resultMap.put("news_id", NEWS_ID);
      resultMap.put("user_id", USER_ID);
      resultMap.put("nick_name", T9Utility.encodeSpecial(T9Utility.null2Empty(NICK_NAME)));
      resultMap.put("content", T9Utility.encodeSpecial(T9Utility.null2Empty(CONTENT)));
      resultMap.put("re_time", RE_TIME);
      reusltList.add(resultMap);
    }
    return reusltList;
  }

  public Map save(Connection conn , T9Person person ,String query , boolean flag) throws Exception {
	  Map resultMap = new HashMap();
	  if(flag){
		  T9QuickQuery.update(conn, query);
		  resultMap.put("status", "YES");
	  }else{
		  resultMap.put("status", "No");
	  }
	  return resultMap;
  }
  
  public Map delete(Connection conn , T9Person person ,String query , boolean flag) throws Exception {
	  Map resultMap = new HashMap();
	  Statement stmt = null;
	  stmt = conn.createStatement();
	  if(flag){
		  int i = stmt.executeUpdate(query);
		  if(i > 0){
			  resultMap.put("status", "YES");
		  }else{
			  resultMap.put("status", "No");
		  }
	  }else{
		  resultMap.put("status", "No");
	  }
	  return resultMap;
  }
  
  
}
