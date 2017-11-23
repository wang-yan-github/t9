package t9.core.funcs.workflow.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.workflow.act.T9FlowRunAct;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9FlowTimerLogic {
  public void saveTimer(Connection conn , String seqId ,  String flowId , String type , String privUser, String remindDate , String remindTime) throws Exception {
    String query = "";
    Date now = new Date();
    String nowStr = T9Utility.getDateTimeStr(now);
    if (T9Utility.isNullorEmpty(remindDate)) {
      remindDate = nowStr.split(" ")[0];
    }
    if (T9Utility.isNullorEmpty(remindTime)) {
      remindTime = nowStr.split(" ")[0];
    }
    if( !"".equals(seqId) ) {
       query ="update FLOW_TIMER set TYPE='"+type+"', USER_STR='"+privUser+"', REMIND_DATE='"+remindDate+"', REMIND_TIME='"+remindTime+"' where SEQ_ID='"+seqId+"'";
    } else {
      query ="insert into FLOW_TIMER (FLOW_ID,TYPE,USER_STR,REMIND_DATE,REMIND_TIME) values ('"+flowId+"','"+type+"','"+privUser+"','"+remindDate+"','"+remindTime+"')";
    }
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement(query);
      ps.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally{
      T9DBUtility.close(ps, null, null);
    }
  }

  public void delTimer(Connection dbConn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    String del = "delete from FLOW_TIMER where SEQ_ID =" + seqId;
    T9WorkFlowUtility.updateTableBySql(del, dbConn);
  }

  public String getTimers(Connection dbConn, String flowId) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    String sql = "select * from flow_timer where flow_ID =" + flowId;
    
    sb.append("[");
    Statement stm = null;
    ResultSet rs = null;
    int count = 0 ;
    T9PersonLogic logic = new T9PersonLogic();
    try{
      stm = dbConn.createStatement();
      rs = stm.executeQuery(sql);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        
        String remindDate =T9Utility.null2Empty( rs.getString("REMIND_DATE"));
        String remindTime = T9Utility.null2Empty(rs.getString("REMIND_TIME"));
        
        String type = rs.getString("TYPE");
        if ("1".equals(type)) {
          type = "只运行一次";
        } else if ("2".equals(type)) {
          type = "按日";
          remindDate = "";
        }else if ("3".equals(type)) {
          type = "按周";
          remindDate = "周" + remindDate;
        }else if ("4".equals(type)) {
          type = "按月";
          remindDate =  remindDate + "日";
        }else if ("5".equals(type)) {
          type = "按年";
          remindDate = remindDate.replace("-","月");
          remindDate =  remindDate + "日";
        }
        
        String userStr = T9Utility.null2Empty(rs.getString("USER_STR"));
        userStr = logic.getNameBySeqIdStr(userStr, dbConn);
        sb.append("{"); 
        sb.append("seqId:" + seqId);
        sb.append(",type:\"" + type + "\"");
        sb.append(",privUser:\"" + T9Utility.encodeSpecial(userStr)+ "\"");
        sb.append(",remindDate:\"" +remindDate + "\"");
        sb.append(",remindTime:\"" + remindTime + "\"");
        sb.append("},"); 
        count++;
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, null);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1 );
    }
    sb.append("]");
    return sb.toString();
  }

  public String getTimer(Connection dbConn, String seqId) throws Exception {
    // TODO Auto-generated method stub
    StringBuffer sb = new StringBuffer();
    String sql = "select * from flow_timer where seq_ID =" + seqId;
    
    
    Statement stm = null;
    ResultSet rs = null;
    T9PersonLogic logic = new T9PersonLogic();
    try{
      stm = dbConn.createStatement();
      rs = stm.executeQuery(sql);
      if (rs.next()) {
        
        String remindDate = rs.getString("REMIND_DATE");
        String remindTime = rs.getString("REMIND_TIME");
        
        
        String type = rs.getString("TYPE");
        String userStr = T9Utility.null2Empty(rs.getString("USER_STR"));
        String privUserName = logic.getNameBySeqIdStr(userStr, dbConn);
        sb.append("{"); 
        sb.append("type:\"" + type + "\"");
        sb.append(",privUser:\"" + userStr+ "\"");
        sb.append(",privUserName:\"" + T9Utility.encodeSpecial(privUserName)+ "\"");
        sb.append(",remindDate:\"" +remindDate + "\"");
        sb.append(",remindTime:\"" + remindTime + "\"");
        sb.append("}"); 
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, null);
    }
    return sb.toString();
  }
  public String getTime(int d) {
    if (d > 10) {
      return d+"";
    } else {
      return "0" + d;
    }
  }
  public void timeRun(Connection conn ) throws Exception {
    String sql = "select * from flow_timer " ;
    synchronized(T9FlowRunAct.loc) {
      Statement stm = null;
      ResultSet rs = null;
      int count = 0 ;
      T9PersonLogic logic = new T9PersonLogic();
      Date now = new Date();
      long s5 = 5 * 60 * 1000;
      long d1 = 1000 * 60 * 60 * 24;
      long d7 = 7 * 1000 * 60 * 60 * 24;
      
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
      try{
        stm = conn.createStatement();
        rs = stm.executeQuery(sql);
        while (rs.next()) {
          boolean isNew = false;
          int flowId = rs.getInt("FLOW_ID");
          int seqId = rs.getInt("SEQ_ID");
          
          Date lastTime = rs.getTimestamp("LAST_TIME");
          String remindDate =T9Utility.null2Empty(rs.getString("REMIND_DATE"));
          String userStr =T9Utility.null2Empty(rs.getString("USER_STR"));
          String remindTime = T9Utility.null2Empty(rs.getString("REMIND_TIME"));
          
          String type = rs.getString("TYPE");
          if ("1".equals(type)) {
            String time = remindDate + " " + remindTime;
            Date date = sdf.parse(time);
            
            long c = Math.abs(now.getTime() - date.getTime()) ;
            if (lastTime == null && c <= s5) {
              isNew = true;
            }
          } else if ("2".equals(type)) {
              String da = sdf2.format(now) + " " + remindTime.trim();
              Date date2 = sdf.parse(da);
              long c = Math.abs(now.getTime() - date2.getTime()) ;
              if (c <= s5) {
                isNew = true;
              }
          }else if ("3".equals(type)) {
            int today = now.getDay();
            if (today == Integer.parseInt(remindDate)) {
              String da = sdf2.format(now) + " " + remindTime.trim();
              Date date2 = sdf.parse(da);
              long c = Math.abs(now.getTime() - date2.getTime()) ;
              if (c <= s5) {
                isNew = true;
              }
            }
          }else if ("4".equals(type)) {
            int today = now.getDate();
            if (today == Integer.parseInt(remindDate)) {
              String da = sdf2.format(now) + " " + remindTime.trim();
              Date date2 = sdf.parse(da);
              long c = Math.abs(now.getTime() - date2.getTime()) ;
              if (c <= s5) {
                isNew = true;
              }
            }
          }else if ("5".equals(type)) {
            String mon = remindDate.split("-")[0];
            String day = remindDate.split("-")[1];
            int today = now.getDate();
            int nowMon = now.getMonth();
            if (today == Integer.parseInt(day) && nowMon == Integer.parseInt(mon)) {
              String da = sdf2.format(now) + " " + remindTime.trim();
              Date date2 = sdf.parse(da);
              long c = Math.abs(now.getTime() - date2.getTime()) ;
              if (c <= s5) {
                isNew = true;
              }
            }
          }
          if (isNew) {
            this.createFlow(conn, flowId, userStr);
            this.updateLastTime(conn, seqId, now);
          }
        }
      }catch(Exception e){
        throw e;
      }finally{
        T9DBUtility.close(stm, rs, null);
      }
      conn.commit();
    }
  }
  public void createFlow(Connection conn , int flowId , String userStr) throws Exception {
    String[] userIds = userStr.split(",");
    T9FlowProcessLogic fpl = new T9FlowProcessLogic();
    T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
    List<T9FlowProcess> list = fpl.getFlowProcessByFlowId(flowId , conn);
    T9FlowType flowType = flowTypeLogic.getFlowTypeById(flowId , conn);
    T9FlowRunLogic frl = new T9FlowRunLogic();
    T9PersonLogic  perLogic  = new T9PersonLogic();
    for (String user : userIds) {
      T9Person p = perLogic.getPerson(conn, user);
      boolean flag = T9WorkFlowUtility.checkPriv(flowType, list, p , conn);
      if (!flag) {
        String runName = frl.getRunName(flowType, p , conn , false) ;
        frl.createNewWork(p, flowType, runName , conn);
      }
    }
  }
  public void updateLastTime(Connection conn , int seqId , Date date) throws Exception {
   String   query ="update FLOW_TIMER set  LAST_TIME=? where SEQ_ID='"+seqId+"'";
   PreparedStatement ps = null;
   try {
     ps = conn.prepareStatement(query);
     Timestamp ts = new Timestamp(date.getTime());
     ps.setTimestamp(1, ts);
     ps.executeUpdate();
   } catch (Exception ex) {
     throw ex;
   } finally{
     T9DBUtility.close(ps, null, null);
   }
  }
}
