package t9.core.oaknow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.data.T9OAKnowUser;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
/**
 * OA知道首页
 * @author qwx110
 *
 */
public class T9OAKnowLogic{
  /**
   * 查找积分榜用户列表，最多显示10条数据
   * @param dbConn
   * @return
   * @throws Exception 
   */
  public List<T9OAKnowUser> findJiFenBang(Connection dbConn) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;   
    List<T9OAKnowUser> users = new ArrayList<T9OAKnowUser>();
    try {
      String sql = "select USER_NAME" +
      		"               ,SCORE " +
      		"         from  person " +
      		"         where NOT_LOGIN !='1'  " +
      		"         and   DEPT_ID!=0 " +
      		"         and SCORE >0 " +
      		"         order by SCORE desc";
      ps=dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      //T9Out.println(sql);
      while(rs.next()){        
        T9OAKnowUser user = new T9OAKnowUser();
        user.setName(rs.getString(1));
        user.setScore(rs.getInt(2));
        users.add(user);
      }
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return users;
  }

/**
 * 注册的用户数
 * @param dbConn
 * @return
 * @throws Exception
 */
  public int findRegCount(Connection dbConn)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;   
    int totalCount = 0;   //总的注册的用户数
    try{
      String sql = "select count(*) as total " +
      		"         from person where NOT_LOGIN != '1'" +
      		"         and DEPT_ID!=0  ";
      		
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();     
      if(rs.next()){
        totalCount = rs.getInt(1);
      }
    }catch(Exception ex){
      throw ex;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return totalCount;
  }
  
  /**
   * 已经解决的问题数
   * @param dbConn
   * @param flag 1:表示解决,0：表示未解决
   * @return
   * @throws Exception
   */
  public int hadResolved(Connection dbConn, int flag) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;   
    int totalCount = 0;
    try{
      String sql = "select count(*) " +
      		"         from WIKI_ASK " +
      		"         where ASK_STATUS="+flag;
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        totalCount = rs.getInt(1);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return totalCount;
  }
  
  /**
   * 分类
   * @param dbConn
   * @return
   * @throws Exception
   */
  public List<T9CategoriesType> findKind(Connection dbConn) throws Exception{//查找父类
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9CategoriesType> outerList = new ArrayList<T9CategoriesType>();
    try{
        String sql = "select CATEGORIE_NAME" +
        		"                ,SEQ_ID " +
        		"                ,PEARENT_ID" +
        		"         from CATEGORIES_TYPE " +
        		"         where PEARENT_ID = '0' " +
        		"         order by ORDER_ID asc";
        ps = dbConn.prepareStatement(sql);
        rs = ps.executeQuery();
        //T9Out.println(sql);
        while(rs.next()){
          T9CategoriesType type = new T9CategoriesType();
          type.setName(rs.getString(1));
          type.setPearentId(0);
          type.setSeqId(rs.getInt(2));
          PreparedStatement ps2 = null;
          ResultSet rs2 = null; 
          String sql2 = "select CATEGORIE_NAME"+
          "                     ,SEQ_ID " +
          "                     ,PEARENT_ID" +
          "         from CATEGORIES_TYPE " +
          "         where PEARENT_ID =  " +rs.getInt(2)+
          "         order by ORDER_ID asc";
          ps2 = dbConn.prepareStatement(sql2);
          rs2 = ps2.executeQuery();
          //T9Out.println(sql2);
          List<T9CategoriesType> innerList = new ArrayList<T9CategoriesType>();
          while(rs2.next()){
            T9CategoriesType type2 = new T9CategoriesType();
            type2.setName(rs2.getString(1));
            type2.setPearentId(rs.getInt(2)); 
            type2.setSeqId(rs2.getInt(2));
            innerList.add(type2);
          }
          type.setList(innerList);
          outerList.add(type);
        }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
    return outerList;
  }
  
  /**
   * 精彩问题推荐，最多显示10条数据
   * @param dbConn
   * @return
   * @throws Exception
   */
  public List<T9OAAsk> findGoodAnswer(Connection dbConn)throws Exception{
    //PreparedStatement ps = null;
    Statement smt =null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{
      String sql = "select b.ASK" +
      		"               ,b.SEQ_ID" +
      		"               ,a.CATEGORIE_NAME" +
      		"               ,a.SEQ_ID" +
      		"               ,a.PEARENT_ID" +
      		"          from categories_type a " +
      		"          join wiki_ask b" +
      		"          on  a.SEQ_ID = b.CATEGORIE_ID" +
      		"          where b.COMMEND='1' " +      		
      		"          order by b.CREATE_TIME desc ";
      //ps = dbConn.prepareStatement(sql);
      smt = dbConn.createStatement();
      rs = smt.executeQuery(sql);
      //rs = ps.executeQuery(); 
      //T9Out.println(sql);
      while(rs.next()){
        T9OAAsk ask = new T9OAAsk();
        ask.setAsk(T9StringUtil.subString(30, rs.getString(1)));
        ask.setSeqId(rs.getInt(2));
        ask.setCategoryName(rs.getString(3));
        ask.setTypeId(rs.getInt(4));
        ask.setParentId(rs.getInt(5));
        askList.add(ask);
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(smt, rs, null);
    }
    return askList;
  }
  
  /**
   * 待解决的问题
   * @param dbConn
   * @return
   * @throws Exception
   */
  public  List<T9OAAsk> findNoResolvedAsk(Connection dbConn) throws Exception{
    PreparedStatement ps = null;
   
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{
      String sql = "select b.ASK" +
      		"               ,b.SEQ_ID" +
      		"               ,a.CATEGORIE_NAME" +
      		"               ,a.SEQ_ID" +
      		"               ,a.PEARENT_ID" +
      		"         from  categories_type a" +
      		"         ,  wiki_ask b" +      		
      		"         where b.ASK_STATUS='0'" +
      		"         and a.SEQ_ID = b.CATEGORIE_ID" +
      		"         order by b.SEQ_ID desc";

     ps = dbConn.prepareStatement(sql);     
     rs = ps.executeQuery();
     //T9Out.println(sql);
     while(rs.next()){
       T9OAAsk ask = new T9OAAsk();
       ask.setAsk(T9StringUtil.subString(30, rs.getString(1)));
       ask.setSeqId(rs.getInt(2));
       ask.setCategoryName(rs.getString(3));
       ask.setTypeId(rs.getInt(4));
       ask.setParentId(rs.getInt(5));
       PreparedStatement ps2 = null;
       ResultSet rs2 = null;
       String sql2 = "select count(*) from WIKI_ASK_ANSWER where ASK_ID = " + rs.getInt(2);
       ps2 = dbConn.prepareStatement(sql2);
       rs2 = ps2.executeQuery();
       //T9Out.println(sql);
       if(rs2.next()){
         ask.setAskCount(rs2.getInt(1));
       }
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
   * 最近解决的问题
   * @param dbConn
   * @return
   * @throws Exception
   */
  public  List<T9OAAsk> findResolvedAsk(Connection dbConn) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{
      String sql = "select b.ASK" +
      "               ,b.SEQ_ID" +
      "               ,a.CATEGORIE_NAME" +
      "               ,a.SEQ_ID" +
      "               ,a.PEARENT_ID" +
      "         from  categories_type a" +
      "         join  wiki_ask b" +
      "         on a.SEQ_ID = b.CATEGORIE_ID" +
      "         where b.ASK_STATUS='1'" +
     // "         and   ROWNUM <=15" +
      "         order by b.RESOLUTION_TIME desc";
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      //T9Out.println(sql);
      while(rs.next()){
        T9OAAsk ask = new T9OAAsk();
        ask.setAsk(T9StringUtil.subString(30,rs.getString(1)));
        ask.setSeqId(rs.getInt(2));
        ask.setCategoryName(rs.getString(3));
        ask.setTypeId(rs.getInt(4));
        ask.setParentId(rs.getInt(5));
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
   * 我的问题
   * @param dbConn
   * @param login_userId
   * @return
   * @throws Exception
   */
  public List<T9OAAsk> findMyAsk(Connection dbConn, String login_userId) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
      try{ 
        String sql = "select b.ASK" +
        		"               ,b.SEQ_ID" +
        		"               ,a.CATEGORIE_NAME" +
        		"               ,a.SEQ_ID" +
        	  "               ,a.PEARENT_ID" +
        		"         from categories_type a" +
        		"         join wiki_ask b on a.SEQ_ID = b.CATEGORIE_ID" +
        		"         where b.CREATOR = '"+ login_userId + "'"+
        		"         order by b.SEQ_ID desc";
       //T9Out.println(sql);
       ps = dbConn.prepareStatement(sql);
       rs = ps.executeQuery();
       while(rs.next()){
         T9OAAsk ask = new T9OAAsk();
         ask.setAsk(T9StringUtil.subString(30, rs.getString(1)));
         ask.setSeqId(rs.getInt(2));
         ask.setCategoryName(rs.getString(3));
         ask.setTypeId(rs.getInt(4));
         ask.setParentId(rs.getInt(5));
         PreparedStatement ps2 = null;
         ResultSet rs2 = null; 
         String sql2 = "SELECT count(*) FROM WIKI_ASK_ANSWER where ASK_ID = "+ rs.getInt(2);
         ps2 = dbConn.prepareStatement(sql2);
         rs2 = ps2.executeQuery();
         if(rs2.next()){
           ask.setAskCount(rs2.getInt(1));
         }
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
   * OA首页搜索
   * @param dbConn
   * @param flag
   * @return
   * @throws Exception
   */
  public List searchByContent(Connection dbConn, String flag) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<Object> askList = new ArrayList<Object>();
    try{
      //todo
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }    
    return askList;
  } 
  /**
   * 与ask相关的问题
   * @param dbConn
   * @param ask
   * @return
   */
  public List<T9OAAsk> referenceQuestion(Connection dbConn, String askName)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    try{
      String sql  = "select ASK"
                   + ",SEQ_ID"
                   + " from wiki_ask"
                   + " where ASK_STATUS = 1 and RELATED_KEYWOED like '"
                   + T9DBUtility.escapeLike(askName) +"' " + T9DBUtility.escapeLike();
                  
      //T9Out.println(sql);
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        T9OAAsk ask = new T9OAAsk();
        ask.setAsk(T9StringUtil.subString(35, rs.getString(1)));
        ask.setSeqId(rs.getInt(2));
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
   * 保存新问题
   * @param dbConn
   * @param ask
   * @return
   * @throws Exception
   */
  public int saveAsk(Connection dbConn, T9OAAsk ask) throws Exception{
    PreparedStatement ps = null;
    try{
      String sql = "insert into wiki_ask(CREATOR, CREATE_TIME,ASK_COMMENT,ASK,RELATED_KEYWOED,ASK_STATUS ,CATEGORIE_ID,COMMEND) values(?,"+ T9DBUtility.currDateTime()+",?,?,?,?,?,?)" ;
      //T9Out.println(sql);
      ps = dbConn.prepareStatement(sql);
      ps.setString(1, ask.getCreator());
     
      ps.setString(2, ask.getAskComment());
      ps.setString(3, ask.getAsk());
      ps.setString(4, ask.getReplyKeyWord());
      ps.setInt(5, 0);
      ps.setInt(6, ask.getTypeId());
      ps.setInt(7, 0);
      int id =ps.executeUpdate();
      return id;
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  
  public String oaDesk(Connection dbConn)throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null; 
    String sql = //"select seq_id, ASK, ASK_STATUS,CATEGORIE_ID, CATEGORIE_NAME, USER_NAME,PEARENT_ID,CREATE_TIME from(" +
                         " select wa.seq_id, wa.ASK, wa.ASK_STATUS, wa.CATEGORIE_ID, ct.CATEGORIE_NAME, p.USER_NAME, ct.PEARENT_ID, wa.CREATE_TIME from wiki_ask wa, categories_type ct, person p" + 
                         " where wa.CATEGORIE_ID = ct.SEQ_ID and wa.CREATOR = p.seq_id order by wa.CREATE_TIME desc" ;
                 // ") where rownum <= 10";
    //T9Out.println(sql);
    ps = dbConn.prepareStatement(sql);
    rs = ps.executeQuery();
    List<T9OAAsk> askList = new ArrayList<T9OAAsk>();
    int cnt = 0;
    while(rs.next() && ++cnt <=10){
      T9OAAsk ask = new T9OAAsk();
      ask.setSeqId(rs.getInt(1));
      ask.setAsk(T9StringUtil.subString(20, rs.getString(2)));
      ask.setStatus(rs.getInt(3));
      ask.setTypeId(rs.getInt(4));
      ask.setCategoryName(rs.getString(5));
      ask.setCreatorName(rs.getString(6));
      ask.setParentId(rs.getInt(7));
      ask.setCreateDate((Date)rs.getObject(8));
      //T9Out.println(ask.toString());
      askList.add(ask);
    }
    return toAString(askList);
  }
  
  public String  toAString(List<T9OAAsk> askList){
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    if(askList != null && askList.size()>0){
      for(int i=0; i<askList.size(); i++){
        if(i < askList.size()-1 ){
         sb.append(askList.get(i).toString()).append(",");
        }else{
         sb.append(askList.get(i).toString());
        }
      }
    }
    sb.append("]");
    return sb.toString();
  }
}