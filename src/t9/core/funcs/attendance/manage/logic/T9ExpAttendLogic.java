package t9.core.funcs.attendance.manage.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.funcs.person.logic.T9PersonLogic;

public class T9ExpAttendLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public  ArrayList<T9DbRecord>  getOutCVS(Connection dbConn,List<T9AttendOut> outList) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ArrayList<T9DbRecord>  cvs = new   ArrayList<T9DbRecord> ();
    T9PersonLogic tpl = new T9PersonLogic();
    T9ManageOutLogic t9aol = new T9ManageOutLogic();
    for (int i = 0; i < outList.size(); i++) {
      T9AttendOut out = new T9AttendOut();
      out = outList.get(i);
      T9DbRecord rc = new T9DbRecord();
      String userName = tpl.getNameBySeqIdStr(out.getUserId(), dbConn);
      String leaderName = tpl.getNameBySeqIdStr(out.getLeaderId() , dbConn);
      String deptName = t9aol.selectByUserIdDept(dbConn, out.getUserId());
      String allow = out.getAllow();
      String status = out.getStatus();
      rc.addField("部门",deptName);
      rc.addField("姓名",userName);
      
      
      rc.addField("外出原因",out.getOutType());
      rc.addField("登记IP",out.getRegisterIp());
      
      rc.addField("外出日期",out.getSubmitTime());  
      rc.addField("外出时间",out.getOutTime1());
      rc.addField("归来时间",out.getOutTime2());
      
      rc.addField("审批人员",leaderName);  
      String outStatus = "待批";
      if(allow.equals("1")&&status.equals("0")){
        outStatus = "审批";
      }
      if(allow.equals("2")&&status.equals("0")){
        outStatus = "未批";
      }
      if(allow.equals("1")&&status.equals("1")){
        outStatus = "已归来";
      }
      rc.addField("状态",outStatus);
      cvs.add(rc);
      
    }
    return cvs;
  }
  public  ArrayList<T9DbRecord>  getLeaveCVS(Connection dbConn,List<T9AttendLeave> leaveList) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ArrayList<T9DbRecord>  cvs = new   ArrayList<T9DbRecord> ();
    T9PersonLogic tpl = new T9PersonLogic();
    T9ManageOutLogic t9aol = new T9ManageOutLogic();
    for (int i = 0; i < leaveList.size(); i++) {
      T9AttendLeave leave = new T9AttendLeave();
      leave = leaveList.get(i);
      T9DbRecord rc = new T9DbRecord();
      String userName = tpl.getNameBySeqIdStr(leave.getUserId(), dbConn);
      String leaderName = tpl.getNameBySeqIdStr(leave.getLeaderId() , dbConn);
      String deptName = t9aol.selectByUserIdDept(dbConn, leave.getUserId());
      String allow = leave.getAllow();
      String status = leave.getStatus();
      rc.addField("部门",deptName);
      rc.addField("姓名",userName);
      
      
      rc.addField("请假原因",leave.getLeaveType());
      rc.addField("占休年假",leave.getAnnualLeave());
      rc.addField("登记IP",leave.getRegisterIp());
      
      rc.addField("开始日期",leave.getLeaveDate1());  
      rc.addField("结束日期",leave.getLeaveDate2());
      
      rc.addField("审批人员",leaderName);  
      String leaveStatus = "待批";
      if(status.equals("1")&&allow.equals("1")){
        leaveStatus = "现行";
      }
      if(status.equals("1")&&allow.equals("2")){
        leaveStatus = "未批";
      }
      if(status.equals("1")&&allow.equals("3")){
        leaveStatus = "现行";
      }
      if(status.equals("2")&&allow.equals("3")){
        leaveStatus = "已销毁";
      }
      rc.addField("状态",leaveStatus);
      cvs.add(rc);
    }
    return cvs;
  }
  public  ArrayList<T9DbRecord>  getEvectionCVS(Connection dbConn,List<T9AttendEvection> evectionList) throws Exception {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    ArrayList<T9DbRecord>  cvs = new   ArrayList<T9DbRecord> ();
    T9PersonLogic tpl = new T9PersonLogic();
    T9ManageOutLogic t9aol = new T9ManageOutLogic();
    for (int i = 0; i < evectionList.size(); i++) {
      T9AttendEvection evection = new T9AttendEvection();
      evection = evectionList.get(i);
      T9DbRecord rc = new T9DbRecord();
      String userName = tpl.getNameBySeqIdStr(evection.getUserId(), dbConn);
      String leaderName = tpl.getNameBySeqIdStr(evection.getLeaderId() , dbConn);
      String deptName = t9aol.selectByUserIdDept(dbConn, evection.getUserId());
      String allow = evection.getAllow();
      String status = evection.getStatus();
      rc.addField("部门",deptName);
      rc.addField("姓名",userName);
      
      
      rc.addField("出差地点",evection.getEvectionDest());
      rc.addField("登记IP",evection.getRegisterIp());
      
      rc.addField("开始日期",evection.getEvectionDate1());  
      rc.addField("结束日期",evection.getEvectionDate2());
      
      rc.addField("审批人员",leaderName);  
      String evectionStatus = "在外";
      if(status.equals("1")&&allow.equals("0")){
        evectionStatus = "待批";
      }
      if(status.equals("1")&&allow.equals("1")){
        evectionStatus = "现行";
      }
      if(status.equals("2")&&allow.equals("1")){
        evectionStatus = "归来";
      }
      rc.addField("状态",evectionStatus);
      cvs.add(rc);
    }
    return cvs;
  }
}
