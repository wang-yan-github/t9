package t9.core.funcs.calendar.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.calendar.data.T9Affair;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9AffairLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public int addAffair(Connection dbConn,T9Affair affair) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, affair);
    return T9CalendarLogic.getMaSeqId(dbConn, "AFFAIR");
  }
  public void updateAffair(Connection dbConn,T9Affair affair) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, affair);
  }
  public List<T9Affair> selectAffair(Connection dbConn,String str[]) throws Exception{
    List<T9Affair> affairList = new ArrayList<T9Affair>();
    T9ORM orm = new T9ORM();
    affairList = orm.loadListSingle(dbConn, T9Affair.class, str);
    return affairList;
  }
  public String toSearchData(Connection conn,Map request,int userId) throws Exception{
    String sql = "select SEQ_ID,BEGIN_TIME,END_TIME,TYPE,REMIND_DATE,REMIND_TIME,CONTENT,MANAGER_ID,USER_ID from AFFAIR where USER_ID  ='" + userId+"'  order by BEGIN_TIME desc";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    
    return pageDataList.toJson();
  }
  public T9Affair selectAffairById(Connection dbConn,int seqId) throws Exception{
    T9Affair affair = new T9Affair();
    T9ORM orm = new T9ORM();
    affair = (T9Affair) orm.loadObjSingle(dbConn, T9Affair.class, seqId);
    return affair;
  }
  public void deleteAffairById(Connection dbConn,int seqId) throws Exception{
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9Affair.class,seqId);
  }
  public List<Map<String,String>> selectAffairByTerm(Connection dbConn,int userId,String minTime,String maxTime,String content)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<Map<String,String>> affairList = new ArrayList<Map<String,String>>();
    String sql = "select *from AFFAIR where USER_ID='" + userId + "'" ;
    if(!content.equals("")){
      content = T9DBUtility.escapeLike(content);
      sql = sql + " and CONTENT like '%" + content + "%' " + T9DBUtility.escapeLike(); 
    }
    if(!minTime.equals("")){
      String temp = T9DBUtility.getDateFilter("BEGIN_TIME", minTime, ">=");
      sql = sql + " and " + temp;
    }
    if(!maxTime.equals("")){
      maxTime = maxTime+" 23:59:59";
      String temp = T9DBUtility.getDateFilter("END_TIME", maxTime, "<=");
      sql = sql + " and " + temp;
    }
    sql = sql + " order by BEGIN_TIME";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        Map<String,String> map = new HashMap<String,String>();
        map.put("seqId",rs.getString("SEQ_ID"));
        map.put("userId",rs.getString("USER_ID"));
        map.put("content",rs.getString("CONTENT"));
        map.put("remindDate",rs.getString("REMIND_DATE"));
        map.put("remindTime",rs.getString("REMIND_TIME"));
        map.put("managerId",rs.getString("MANAGER_ID"));
        map.put("type",rs.getString("TYPE"));
        map.put("sms2Remind",rs.getString("SMS2_REMIND"));
        map.put("beginTime", rs.getString("BEGIN_TIME"));
        map.put("endTime",rs.getString("END_TIME"));
        map.put("lastRemind", rs.getString("LAST_REMIND"));
        map.put("lastSms2Remind", rs.getString("LAST_SMS2_REMIND"));
        affairList.add(map);
      }  
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return affairList;
  }
  public void deleteAffair(Connection dbConn,String seqIds)throws Exception{ 
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "delete from AFFAIR where SEQ_ID in(" + seqIds+")";
    //System.out.println(sql);
    try {
      stmt = dbConn.createStatement();
      stmt.executeUpdate(sql);
    }catch(Exception ex) {
      throw ex;
    }finally {
     T9DBUtility.close(stmt, rs, log);
    }
  }
  /*
   * 判断结束日期是否为空
   */
  public boolean checkEndTime(Connection dbConn,String userId,String[] str ) throws Exception{
     Statement stmt = null;
     ResultSet rs = null;
     String sql="";
     try {
       stmt = dbConn.createStatement();
       stmt.executeUpdate(sql);
     }catch(Exception ex) {
       throw ex;
     }finally {
      T9DBUtility.close(stmt, rs, log);
     }
      return true;
   }

}
