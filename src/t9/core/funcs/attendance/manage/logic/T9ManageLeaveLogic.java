package t9.core.funcs.attendance.manage.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9ManageLeaveLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public List<T9AttendLeave> selectLeaveManage(Connection dbConn,int userId) throws Exception {
    List<T9AttendLeave> leaveList = new ArrayList<T9AttendLeave>();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
    Statement stmt = null;
    ResultSet rs = null;
    //String sql ="select * from ATTEND_LEAVE where LEADER_ID = '" + userId +"' and STATUS = '1' and (ALLOW = '0' or ALLOW = '3')";
    String sql ="select * from ATTEND_LEAVE where LEADER_ID = '" + userId +"' and ALLOW = '0'";
      try {
        stmt = dbConn.createStatement();
        rs = stmt.executeQuery(sql);
        while(rs.next()){
          T9AttendLeave t9al = new T9AttendLeave();
          t9al.setSeqId(rs.getInt("SEQ_ID"));
          t9al.setUserId(rs.getString("USER_ID"));
          t9al.setLeaderId(rs.getString("LEADER_ID"));
          t9al.setAnnualLeave(rs.getInt("ANNUAL_LEAVE"));
          t9al.setLeaveType(rs.getString("LEAVE_TYPE"));
          t9al.setReason(rs.getString("REASON"));
          t9al.setRegisterIp(rs.getString("REGISTER_IP"));
          t9al.setStatus(rs.getString("STATUS"));
          t9al.setAllow(rs.getString("ALLOW"));
          if(rs.getString("DESTROY_TIME") != null){
            t9al.setDestroyTime(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", rs.getString("DESTROY_TIME")));
          }
          t9al.setLeaveDate1(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", rs.getString("LEAVE_DATE1")));
          t9al.setLeaveDate2(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss", rs.getString("LEAVE_DATE2")));
          leaveList.add(t9al);
        }  
      }catch(Exception ex) {
         throw ex;
      }finally {
        T9DBUtility.close(stmt, rs, log);
    } 
    return leaveList;
  }
  public  List<T9AttendLeave> selectLeave(Connection dbConn,String str[]) throws Exception {
    List<T9AttendLeave> leaveList = new ArrayList<T9AttendLeave>();
    T9ORM orm = new T9ORM();
    leaveList =  orm.loadListSingle(dbConn, T9AttendLeave.class, str);
    return leaveList;
  }
}
