package t9.subsys.oa.hr.score.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.hr.score.data.T9ScoreData;
import t9.subsys.oa.hr.score.data.T9ScoreShow;

public class T9ScoreDataLogic {
  private static Logger log = Logger.getLogger("t9.subsys.oa.hr.score.logic.T9ScoreDateLogic");

  /**
   *  查询
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
  */
  public static List<T9ScoreData> selectData(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9ScoreData> dataList = new ArrayList<T9ScoreData>();
    dataList = orm.loadListSingle(dbConn, T9ScoreData.class, str);
    return dataList;
  }

  /**
   *  查询
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
  */
  public static List<T9ScoreShow> selectData2(Connection dbConn,String[] str) throws Exception {
    T9ORM orm = new T9ORM();
    List<T9ScoreShow> dataList = new ArrayList<T9ScoreShow>();
    dataList = orm.loadListSingle(dbConn, T9ScoreShow.class, str);
    return dataList;
  }
  /**
   * 新建
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static int addData(Connection dbConn,T9ScoreData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, data);
    return getMaSeqId(dbConn, "SCORE_DATA");
  }
  /**
   * 新建
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static int addData2(Connection dbConn,T9ScoreShow data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, data);
    return getMaSeqId(dbConn, "SCORE_SHOW");
  }
  /**
   * 编辑 
   * @param dbConn
   * @param item
   * @return
   * @throws Exception
   */
  public static void updateData(Connection dbConn,T9ScoreData data) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, data);
  }
  
  public static void updateDate(Connection dbConn,String seqId,String score,String answer,String memo) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    if(!T9Utility.isNullorEmpty(memo)){
    	memo = memo.replace("'", "''");    	
    }else{
    	memo = "";
    }
    String sql = "update score_data set SCORE = '" + score + "' ,ANSWER = '" + answer + "' , MEMO = '" + memo + "'  where seq_id = " + seqId ;
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
   }
  public static void updateDate2(Connection dbConn,String seqId,String score,String answer,String memo) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    if(!T9Utility.isNullorEmpty(memo)){
      memo = memo.replace("'", "''");     
    }else{
      memo = "";
    }
    String sql = "update score_show set SCORE = '" + score + "' ,ANSWER = '" + answer + "' , MEMO = '" + memo + "'  where seq_id = " + seqId ;
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
   }
  public static int getMaSeqId(Connection dbConn,String tableName)throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    int maxSeqId = 0;
    String sql = "select max(SEQ_ID) as SEQ_ID from " + tableName;
    try{
     stmt = dbConn.createStatement();
     rs = stmt.executeQuery(sql);
     if(rs.next()){
       maxSeqId = rs.getInt("SEQ_ID");
     }
      
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return maxSeqId;
  }
  
  /**
   * 查询是否已录入过数据
   * @param dbConn
   * @param person
   * @param userId
   * @param flowId
   * @return
   * @throws Exception
   */
  public boolean getOperationFlag(Connection dbConn, T9Person person, String userId, int flowId)
  throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      
      String sql = "SELECT count(*) from SCORE_DATA where FLOW_ID ="+flowId+" and RANKMAN ="+person.getSeqId()+" and PARTICIPANT="+userId+"";
      rs = stmt.executeQuery(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  /**
   * 添加考核数据
   * @param dbConn
   * @param flowId
   * @param loginUserId
   * @param participant
   * @param score
   * @param memo
   * @throws Exception
   */
  public void addScoreData(Connection dbConn, int flowId, String loginUserId, String participant, String score, String memo, String checkFlag) throws Exception {
//    String sql = "insert into SCORE_DATA (FLOW_ID,RANKMAN,PARTICIPANT,SCORE,RANK_DATE,MEMO,CHECK_FLAG) values("+flowId+","+loginUserId+",'"+participant+"','"+score+"',?,'"+memo+"','"+checkFlag+"')";
    String sql = "insert into SCORE_DATA (FLOW_ID,RANKMAN,PARTICIPANT,SCORE,RANK_DATE,MEMO,CHECK_FLAG) values(?,?,?,?,?,?,?)";
    PreparedStatement stmt = null ; 
    ResultSet rs = null;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setInt(1, flowId);
      stmt.setString(2, loginUserId);
      stmt.setString(3, participant);
      stmt.setString(4, score);
      stmt.setDate(5, T9Utility.parseSqlDate(T9Utility.getCurDateTimeStr()));
      stmt.setString(6, memo);
      stmt.setString(7, checkFlag);
      stmt.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  /**
   * 修改考核数据
   * @param dbConn
   * @param flowId
   * @param score
   * @param memo
   * @param participant
   * @param person
   * @throws Exception
   */
  public static void updateScoreDate(Connection dbConn,int flowId, String score, String memo, String participant, T9Person person, String checkFlag) throws Exception {
    if (T9Utility.isNullorEmpty(memo)) {
			memo = "";
		}
  	PreparedStatement stmt = null ; 
    ResultSet rs = null;
//    String sql = "update SCORE_DATA set SCORE = '" + score + "' ,RANK_DATE = ? , MEMO = '" + memo + "', CHECK_FLAG = '" + checkFlag + "'  where FLOW_ID = " + flowId + " and RANKMAN =" +person.getSeqId()+" and PARTICIPANT = "+participant+"";
    String sql = "update SCORE_DATA set SCORE = ?, RANK_DATE = ? , MEMO = ?, CHECK_FLAG = ?  where FLOW_ID = ? and RANKMAN = ? and PARTICIPANT = ?";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.setString(1, score);
      stmt.setDate(2, T9Utility.parseSqlDate(T9Utility.getCurDateTimeStr()));
      stmt.setString(3, memo);
      stmt.setString(4, checkFlag);
      stmt.setInt(5, flowId);
      stmt.setString(6, String.valueOf(person.getSeqId()));
      stmt.setString(7, participant);
      stmt.executeUpdate();
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }
  
  public static T9Person showPerson(Connection dbConn,String participant) throws Exception {
    String sql = "select son.SEQ_ID as seqId,son.USER_NAME as userName,dep.DEPT_NAME as deptName "
      + ",priv.PRIV_NAME as privName FROM PERSON son "
      + " left outer join DEPARTMENT dep on dep.SEQ_ID = son.DEPT_ID "
      + " left outer join USER_PRIV priv on priv.SEQ_ID = son.USER_PRIV "
      + " WHERE son.SEQ_ID =" + participant;
    T9Person  person = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        person = new T9Person();
        person.setSeqId(rs.getInt("seqId"));
        person.setUserName(rs.getString("userName"));
        person.setUserId(rs.getString("deptName"));
        person.setUserPriv(rs.getString("privName"));
      }
    }catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs,log);
    }
    return person;
  }
  
  public static List<T9ScoreData> scoreDataList(Connection dbConn,int flowId) throws Exception {
    String sql = "SELECT PARTICIPANT, SCORE, RANKMAN, MEMO from SCORE_DATA where FLOW_ID= " + flowId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9ScoreData> list = new ArrayList<T9ScoreData>();
    T9ScoreData flow = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        flow = new T9ScoreData();
        String userId = rs.getString(1);
        T9Person person = showPerson(dbConn,userId);
        flow.setAnswer(person.getUserId());//部门   用其它字段代替一下部门名称        flow.setParticipant(person.getUserName());//姓名
        flow.setCheckFlag(String.valueOf(person.getUserPriv()));//角色
        flow.setScore(rs.getString(2));
        flow.setRankman(rs.getString(3));
        flow.setMemo(rs.getString(4));
        list.add(flow);
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return list;
  }
  
  public static List<T9ScoreData> itemNameList(Connection dbConn,int groupId) throws Exception {
    String sql = "SELECT ITEM_NAME from SCORE_ITEM where GROUP_ID= '" + groupId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9ScoreData> list = new ArrayList<T9ScoreData>();
    T9ScoreData data = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        data = new T9ScoreData();
        data.setMemo(rs.getString(1));//考核项目  用考核项目替换批注字符串(不是同一长表)
        list.add(data);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return list;
  }
  
  public List getItemName(Connection dbConn, String groupId) throws Exception {
    List list = new ArrayList();
    Statement stmt = null;
    ResultSet rs = null;
    Statement stmt2 = null;
    ResultSet rs2 = null;
    list.add("部门");
    list.add("名称");
    list.add("角色");
    String sql = "SELECT ITEM_NAME from SCORE_ITEM where GROUP_ID= '" + groupId + "'";
    String sql2 = "SELECT ITEM_NAME from SCORE_ANSWER where GROUP_ID= '" + groupId + "'";
    try { 
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String itemName = rs.getString(1);
        list.add(itemName);
      }
      stmt2 = dbConn.createStatement();
      rs2 = stmt2.executeQuery(sql2);
      while (rs2.next()) {
        String itemName = rs2.getString(1);
        list.add(itemName);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
      T9DBUtility.close(stmt2, rs2, log);
    }
    return list;
  }
  /**
   * 获取查看工作日志、工作安排--wyw
   * @param dbConn
   * @param groupId
   * @return
   * @throws Exception
   */
  public String getGroupReferLogic(Connection dbConn,int groupId) throws Exception{
  	 String sql = "select GROUP_REFER from score_group where SEQ_ID =" + groupId;
     PreparedStatement stmt = null;
     ResultSet rs = null;
     String groupReferStr = "";
     String data = "";
     String returnValue = "";
     boolean diaryFlag = false;
     boolean calendarFlag = false;
  	try {
  		stmt = dbConn.prepareStatement(sql);
  		rs = stmt.executeQuery();
  		if(rs.next()){
  			groupReferStr = T9Utility.null2Empty(rs.getString("GROUP_REFER"));
  		}
  		String [] groupReferArry = groupReferStr.split(",");
  		if(groupReferArry !=null && groupReferArry.length>0){
  			for(String tmp:groupReferArry){
  				if ("DIARY".equals(tmp.trim())) {
  					diaryFlag = true;
					}
  				if ("CALENDAR".equals(tmp.trim())) {
  					calendarFlag = true;
  				}
  			}
  		}
  		if (diaryFlag && calendarFlag) {
  			returnValue = "both";
			}else if (diaryFlag) {
				returnValue = "diary";
			}else if(calendarFlag){
				returnValue = "calendar";
			}
  		data = "{groupRefer:\"" +  returnValue  + "\"}";
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
  	return data;
  }
  
  
  public String getScoreGroupSelect(Connection dbConn, String userIdStr) throws Exception {
    String data = ""; 
    StringBuffer sb = new StringBuffer("["); 
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      boolean isHave = false; 
      String sql = "select SEQ_ID, GROUP_NAME from SCORE_GROUP where "
          + T9DBUtility.findInSet(userIdStr, "USER_PRIV");
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      while (rs.next()) {
        int flowId = rs.getInt(1);
        String groupName = rs.getString("GROUP_NAME");
        sb.append("{"); 
        sb.append("seqId:\"" + flowId + "\""); 
        sb.append(",text:\"" + T9Utility.encodeSpecial(groupName) + "\""); 
        sb.append("},"); 
        isHave = true; 
      }
      if (isHave) { 
        sb.deleteCharAt(sb.length() - 1); 
        } 
        sb.append("]"); 
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    data = sb.toString(); 
    return data;
  }
  
  public String getGroupId(Connection conn, String roleId) throws Exception {
    String result = "";
    String sql = "select SEQ_ID from SCORE_GROUP where "
      + T9DBUtility.findInSet(roleId, "USER_PRIV");
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String toId = rs.getString(1);
        if (toId != null) {
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
}
