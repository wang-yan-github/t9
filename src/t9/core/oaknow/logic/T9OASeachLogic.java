package t9.core.oaknow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
/**
 * oa知道搜索
 * @author qwx110
 *
 */
public class T9OASeachLogic{ 
/**
 * 与name相关的解决的问题
 * @param dbConn
 * @param name
 * @param pu
 * @return
 * @throws Exception
 */
  public List<T9OAAsk> findAllAskResolved(Connection dbConn, String name, T9PageUtil pu) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{  
      String sql =  "select  "
                          +" ask.seq_id,ask.ASK" 
                          +" ,ask.ASK_COMMENT" 
                          +" ,ask.ask_status"
                          +" ,ans.good_answer"
                          +" ,ans.ANSWER_CONTENT"          
                     +" from wiki_ask ask, wiki_ask_answer ans "
                     +" where  ask.SEQ_ID = ans.ASK_ID "
                     +" and ask.ASK_STATUS = 1"
                     +" and ans.GOOD_ANSWER = 1 "        

                      +" and (ask like '%"+ T9DBUtility.escapeLike(name.trim()) +"%' " +T9DBUtility.escapeLike()
                      +" or ASK_COMMENT like  '%"+ T9DBUtility.escapeLike(name.trim())+"%'"+T9DBUtility.escapeLike()  
                      +" or ANSWER_CONTENT like  '%"+ T9DBUtility.escapeLike(name.trim())+"%' "+ T9DBUtility.escapeLike() +")";
      //T9Out.println(sql);
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);    
      ps.setMaxRows(pu.getCurrentPage() * pu.getPageSize());
      rs = ps.executeQuery();
      rs.first();   
      rs.relative((pu.getCurrentPage()-1) * pu.getPageSize() -1); 
      while(rs.next()){
        T9OAAsk ask = new T9OAAsk();
        ask.setSeqId(rs.getInt(1));
        ask.setAsk(T9StringUtil.toBright(rs.getString(2), name, 50));
        ask.setAskComment(T9StringUtil.toBright(rs.getString(3), name, 250));        
        ask.setAnswer(T9StringUtil.toBright(rs.getString(4), name, 250));
        askList.add(ask);
      }
    }catch(Exception e){
      throw e; 
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return askList;
  }  
  
 /**
  * 查找个数 
  * @param dbConn
  * @param name
  * @return
  * @throws Exception
  */
  public int findAllAskResolvedCount(Connection dbConn, String name)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String sql = 
    /*  "select count(*)"
      +" from ("*/
            "select  "
              +" ask.seq_id,ask.ASK" 
              +" ,ask.ASK_COMMENT" 
              +" ,ask.ask_status"
              +" ,ans.good_answer"
              +" ,ans.ANSWER_CONTENT"          
           +" from wiki_ask ask, wiki_ask_answer ans "
          +" where  ask.SEQ_ID = ans.ASK_ID "
         +" and ask.ASK_STATUS = 1"
         +" and ans.GOOD_ANSWER = 1 "         
  /*   +")"*/
    +" and (ask like '%"+ T9DBUtility.escapeLike(name.trim()) +"%' " + T9DBUtility.escapeLike()
    +" or ASK_COMMENT like  '%"+ T9DBUtility.escapeLike(name.trim())+"%' "+ T9DBUtility.escapeLike()
   +" or ANSWER_CONTENT like  '%"+ T9DBUtility.escapeLike(name.trim())+"%' "+ T9DBUtility.escapeLike()+")";
    //T9Out.println(sql);
    try{
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      int cnt = 0;
      while(rs.next()){
         cnt ++;
      }
      return cnt;
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }   
  }
 /**
  *  与name相关的未解决的问题
  * @param dbConn
  * @param name
  * @param pu
  * @return
  * @throws Exception
  */
  public List<T9OAAsk> findAllAskNoResolved(Connection dbConn, String name, T9PageUtil pu) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{
      String ids = findAskNoResolvedIds(dbConn, name);
      if(T9Utility.isNullorEmpty(ids)){
        ids = "0";
      }
      String sql =  "select  "
                        +" ask.SEQ_ID" 
                        +",ask.ASK" 
                        +",ask.ASK_COMMENT" 
                        +",ask.ASK_STATUS"
                   +" from WIKI_ASK ask where seq_id in("+ ids +")";
      //T9Out.println(sql+"***");     
      ps = dbConn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);    
      ps.setMaxRows(pu.getCurrentPage() * pu.getPageSize());
      rs = ps.executeQuery();
      rs.first();   
      rs.relative((pu.getCurrentPage()-1) * pu.getPageSize() -1);  
      while(rs.next()){
        T9OAAsk ask = new T9OAAsk();
        ask.setSeqId(rs.getInt(1));
        ask.setAsk(T9StringUtil.toBright(rs.getString(2), name,50));
        ask.setAskComment(T9StringUtil.toBright(rs.getString(3), name, 250));        
        ask.setAnswer(T9StringUtil.toBright(rs.getString(4), name, 250));
        askList.add(ask);
      }
    }catch(Exception e){
      throw e; 
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return askList;
  }  
 
 /**
  * 待解决的问题的个数
  * @param dbConn
  * @param name 查询的内容
  * @return
  * @throws Exception
  */
  public int findAskNoResolvedCount(Connection dbConn, String name)throws Exception{
    String ids = findAskNoResolvedIds(dbConn, name);
    if(T9Utility.isNullorEmpty(ids)){
      return 0;
    }
    String[] id = ids.split(",");
    return  id.length;
  }
  
  /**
   * 获得待解决的问题的id串
   * @param dbConn
   * @param name  查询的内容
   * @return
   * @throws Exception
   */
  public String findAskNoResolvedIds(Connection dbConn, String name) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    String sql = " select ask.SEQ_ID as AID from wiki_ask ask where ask.ask_status = 0 ";
           sql +=" and (ask.ASK like '%"+ T9DBUtility.escapeLike(name) +"%' " +T9DBUtility.escapeLike();
           sql +=" or ask.ASK_COMMENT like  '%"+ T9DBUtility.escapeLike(name) +"%' "+T9DBUtility.escapeLike()+")" ;
           sql +=" union ";
           sql +=" select ans.ASK_ID as AID from wiki_ask_answer ans where ans.GOOD_ANSWER = 0 and ";
           sql +=" ans.ANSWER_CONTENT like  '%"+ T9DBUtility.escapeLike(name) +"%' " + T9DBUtility.escapeLike();
     //T9Out.println(sql);
     ps = dbConn.prepareStatement(sql);
     rs = ps.executeQuery();
     String ids = "";
     while(rs.next()){
       ids += rs.getInt("AID")+",";
     }    
    return ids.substring(0, ids.lastIndexOf(",")==-1?0:ids.lastIndexOf(","));
  }
}
