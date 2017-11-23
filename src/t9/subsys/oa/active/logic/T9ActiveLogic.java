package t9.subsys.oa.active.logic;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.active.data.T9Active;

public class T9ActiveLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public String getUserIds (Connection dbConn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String userIds = "";
    try {
      stmt = dbConn.createStatement();
      String sql = "select SEQ_ID , PARA_VALUE from SYS_PARA where PARA_NAME = 'ACTIVE_SET_USER'";
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        userIds = T9Utility.null2Empty(rs.getString("PARA_VALUE"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return userIds;
  }
  public int addActive(Connection dbConn,T9Active active) throws Exception {
    T9ORM orm = new T9ORM();
    orm.saveSingle(dbConn, active);
    return 1;//T9CalendarLogic.getMaSeqId(dbConn, "TASK");
  }
  public void updateActive(Connection dbConn,T9Active active) throws Exception {
    T9ORM orm = new T9ORM();
    orm.updateSingle(dbConn, active);
  }
  public T9Active selectActiveById(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    T9Active active =  (T9Active) orm.loadObjSingle(dbConn, T9Active.class, seqId);
    return active;
  }
  public List<T9Active>  selectActiveByWeek(Connection dbConn,String beginDate,String endDate,String userId) throws Exception {
    T9ORM orm = new T9ORM();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    List<T9Active> activeList = new ArrayList<T9Active>();
    Statement stmt = null;
    ResultSet rs = null;
    String dateStr1  = "";
    String dateStr2 = "";
    if(!beginDate.equals("")){
       dateStr1    = T9DBUtility.getDateFilter("ACTIVE_TIME", beginDate, ">=");
    }
    if(!endDate.equals("")){
      dateStr2 = T9DBUtility.getDateFilter("ACTIVE_TIME", endDate + " 24:59:59", "<=");
    }
    
    String sql = "select * from  ACTIVE where " + dateStr1 + " and " + dateStr2;
    sql = sql  + " order by ACTIVE_TIME";
    try {
      stmt = (Statement) dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while(rs.next()){
        T9Active active = new T9Active();
        active.setSeqId(rs.getInt("SEQ_ID"));
        active.setActiveContent(rs.getString("ACTIVE_CONTENT"));
        active.setActiveUser(rs.getString("ACTIVE_USER"));
        active.setActiveTimeRang(rs.getString("ACTIVE_TIME_RANG"));
        active.setAttachmentId(rs.getString("ATTACHMENT_ID"));
        active.setAttachmentName(rs.getString("ATTACHMENT_ID"));
        active.setOpUserId(rs.getString("OP_USER_ID"));
        active.setOverStatus(rs.getString("OVER_STATUS"));
        if(rs.getString("ACTIVE_TIME")!=null){
          active.setActiveTime(df.parse(rs.getString("ACTIVE_TIME")));
        }else{
          active.setActiveTime(null);
        }
        if(rs.getString("OP_DATETIME")!=null){
          active.setOpDatetime(df.parse(rs.getString("OP_DATETIME")));
        }else{
          active.setOpDatetime(null);
        }
        if(!userId.equals("")){
          if(rs.getString("ACTIVE_USER")!=null&&!rs.getString("ACTIVE_USER").trim().equals("")){
            String userIdsa = rs.getString("ACTIVE_USER");
            String[] userIdArray = userIdsa.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              if(userIdArray[i].equals(userId)){
                activeList.add(active);
                break;
              }
            }
          }
        }else{
          activeList.add(active);
        }
      }  
    }catch(Exception ex) {
       throw ex;
    }finally {
      T9DBUtility.close(stmt, rs, log);
    }
    //T9Active active =  (T9Active) orm.loadObjSingle(dbConn, T9Active.class, seqId);
    return activeList;
  }
  public void delActiveById(Connection dbConn,int seqId) throws Exception {
    T9ORM orm = new T9ORM();
    orm.deleteSingle(dbConn, T9Active.class, seqId);
  }
  public void delActiveCadenarById(Connection dbConn,int seqId) throws Exception {
    T9Active a = this.selectActiveById(dbConn, seqId);
    String ids = T9Utility.null2Empty(a.getCalendars());
    
    if (!T9Utility.isNullorEmpty(ids)) {
      ids = T9WorkFlowUtility.getOutOfTail(ids);
      T9CalendarLogic l = new T9CalendarLogic();
      l.deleteCalendar(dbConn, ids);
    }
  }
}
