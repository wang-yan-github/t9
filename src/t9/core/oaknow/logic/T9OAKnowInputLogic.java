package t9.core.oaknow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.util.T9DateFormatUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

/**
 * 知道导入
 * @author qwx110
 *
 */
public class T9OAKnowInputLogic{
  
  /**
   * 知道录入
   * @param dbConn
   * @param ask
   * @return
   * @throws Exception
   */
  public int insertNewAsk(Connection dbConn, T9OAAsk ask) throws Exception{
    PreparedStatement ps = null;
    ResultSet rs = null;
    String sql = "insert into wiki_ask(CREATOR ,CREATE_TIME ,ASK_COMMENT ,ASK ,RELATED_KEYWOED, CATEGORIE_ID, ask_status, ASK_REPLY_COUNT, RESOLUTION_TIME) values(?,"+ T9DBUtility.currDateTime()+",?,?,?,?,'1',1, "+ T9DBUtility.currDateTime() +")";
    try{
      //T9Out.println(sql);
      String[] str = {"SEQ_ID"};
      ps = dbConn.prepareStatement(sql, str);
      //T9Out.println(ask.getCreator());
      ps.setString(1, ask.getCreator());     
      ps.setString(2, T9StringUtil.replaceSQ(ask.getAskComment()));
      ps.setString(3, T9StringUtil.replaceSQ(ask.getAsk()));
      ps.setString(4, T9StringUtil.replaceSQ(ask.getReplyKeyWord()));
      ps.setInt(5, ask.getTypeId());
 
      int id = ps.executeUpdate(); 
      if(id != 0){
        rs = ps.getGeneratedKeys();
        if(rs.next()){
          ask.setSeqId(rs.getInt(1));
        }
        insertAnswer(dbConn, ask);
        addFen(dbConn, ask);
      }
      return id;
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, rs, null);
    }
  }
  /**
   * 插入答案
   * @param dbConn
   * @param ask
   * @return
   * @throws Exception
   */
  public int insertAnswer(Connection dbConn, T9OAAsk ask)throws Exception{
    PreparedStatement ps = null;  
    String sql = "insert into wiki_ask_answer(ASK_ID,ANSWER_USER,ANSWER_TIME,ANSWER_CONTENT,GOOD_ANSWER) values(?,?,"+ T9DBUtility.currDateTime() +",?,1)";
    try{
      //T9Out.println(sql);
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1, ask.getSeqId());
      ps.setString(2, ask.getCreator());     
      ps.setString(3, ask.getAnswer());
      int id = ps.executeUpdate();
      return id;
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
  /**
   * 用户加分
   * @param dbConn
   * @param ask
   * @return
   * @throws Exception
   */
  public int addFen(Connection dbConn, T9OAAsk ask)throws Exception{
    PreparedStatement ps = null;  
    String sql = "update person set score = score + 1 where seq_id = " + ask.getCreatorId();
    try{
      //T9Out.println(sql);
      ps = dbConn.prepareStatement(sql);
      int id = ps.executeUpdate();
      return id;
    } catch (Exception e){
      throw e;
    }finally{
      T9DBUtility.close(ps, null, null);
    }
  }
}
