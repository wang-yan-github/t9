package t9.core.funcs.attendance.manage.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.attendance.personal.data.T9AttendLeave;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.fillRegister.data.T9AttendFill;

public class T9ManageAttendLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public long getLongByDutyTime(String dutyTime){
    long time = 0;
    String times[] = dutyTime.split(":");
    int length = times.length;
    for (int i = 0; i < times.length; i++) {
      time = time + Long.parseLong(times[i])* (long)(Math.pow(60, length-1-i)) ;
    }
    return time;
  }
  public List<T9AttendLeave> selectLeave(Connection dbConn,String str[])throws Exception {
    T9ORM orm = new T9ORM();
    List<T9AttendLeave> leaveList = (List<T9AttendLeave>) orm.loadListSingle(dbConn, T9AttendLeave.class, str);
    return leaveList;
  }  
  public List<T9AttendEvection> selectEvection(Connection dbConn,String str[])throws Exception {
    T9ORM orm = new T9ORM();
    List<T9AttendEvection> evectonList = (List<T9AttendEvection>) orm.loadListSingle(dbConn, T9AttendEvection.class, str);
    return evectonList;
  }
  public List<T9AttendOut> selectOut(Connection dbConn,String str[])throws Exception {
    T9ORM orm = new T9ORM();
    List<T9AttendOut> outList = (List<T9AttendOut>) orm.loadListSingle(dbConn, T9AttendOut.class, str);
    return outList;
  }
  
  public List<T9AttendFill> getFillRegister(Connection dbConn,String str[])throws Exception {
    T9ORM orm = new T9ORM();
    List<T9AttendFill> evectonList = (List<T9AttendFill>) orm.loadListSingle(dbConn, T9AttendFill.class, str);
    return evectonList;
  }
  //根据排班类型得到所有人员
  public List<T9Person> selectPerson(Connection dbConn,String[] str) throws Exception{
    List<T9Person> personList = new ArrayList<T9Person>();
    T9ORM orm = new T9ORM();
    personList = orm.loadListSingle(dbConn, T9Person.class, str);
    return personList;
  }
  //根据部门得到所有人员Id
  public String selectUserIds(int deptId,Connection dbConn) throws Exception{
    String userIds = "";
    Statement stmt = null;
    ResultSet rs = null;
    String deptIds = this.getDeptTreeSeqIds(deptId, dbConn);
    if(!deptIds.equals("")){
      String sql = "select p.SEQ_ID as SEQ_ID from PERSON p,DEPARTMENT d where p.DEPT_ID = d.SEQ_ID and d.SEQ_ID in(" + deptIds + ")";
      //System.out.println(sql);
        try {
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(sql);
          while(rs.next()){
            userIds = userIds +rs.getString("SEQ_ID")+",";
          }
          if(!userIds.equals("")){
            userIds = userIds.substring(0, userIds.length()-1);
          }
        }catch(Exception ex) {
          throw ex;
       }finally {
         T9DBUtility.close(stmt, rs, log);
     }      
    }   
    return userIds;
  }
  //根据部门得到子部门Id和本部门Id
  public  String getDeptTreeSeqIds(int deptId , Connection conn) throws Exception{
    StringBuffer sb = new StringBuffer();
    this.getDeptTree(deptId, sb, 0 , conn);
    //System.out.println(sb.length()+":"+(sb==null));
    if(sb.length()>0){
      sb.deleteCharAt(sb.length() - 1);
    }
    if(deptId!=0&&sb.length()>0){
      sb.append(","+deptId);  
    }else if(deptId!=0&&sb.length()<=0){
      sb.append(deptId);  
    }
    return sb.toString();
  }
  public void getDeptTree(int deptId , StringBuffer sb , int level , Connection conn) throws Exception{
    //首选分级，然后记录级数，是否为最后一个。。。

    List<T9Department> list = this.getDeptByParentId(deptId , conn);
    
    for(int i = 0 ;i < list.size() ;i ++){
      T9Department dp = list.get(i);
      sb.append(dp.getSeqId());
      sb.append(",");
      this.getDeptTree(dp.getSeqId(), sb, level + 1 , conn);
    }
   
  }
  //部门
  public List<T9Department> getDeptByParentId(int deptId ,Connection conn) throws Exception {
    // TODO Auto-generated method stub
    T9ORM orm = new T9ORM();
    List<T9Department> list = new ArrayList();
    Map filters = new HashMap();
    filters.put("DEPT_PARENT", deptId);
    list  = orm.loadListSingle(conn ,T9Department.class , filters);
    return list;
    
  }
}
