package t9.core.funcs.attendance.manage.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.funcs.attendance.personal.data.T9AttendDuty;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9ManageOutLogic {
  private static Logger log = Logger.getLogger("t9.core.act.action.T9SysMenuLog");
  public List<T9AttendOut> selectOutManage(Connection dbConn,Map map) throws Exception {
    List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    T9ORM orm = new T9ORM();
    outList = orm.loadListSingle(dbConn, T9AttendOut.class, map);
    return outList;
  }
  public List<T9AttendOut> selectOutManage(Connection dbConn,String[] str) throws Exception {
    List<T9AttendOut> outList = new ArrayList<T9AttendOut>();
    T9ORM orm = new T9ORM();
    outList = orm.loadListSingle(dbConn, T9AttendOut.class, str);
    return outList;
  }
  public String selectByUserIdDept(Connection dbConn,String userId)  throws Exception{
    String deptName = "";
    Statement stmt = null;
    ResultSet rs = null;
    if(userId!=null&&!userId.equals("")){
      String sql = "select d.DEPT_NAME as DEPTNAME from PERSON P, DEPARTMENT d where p.DEPT_ID = d.SEQ_ID AND p.SEQ_ID = " + userId ;
      //System.out.println(sql);
        try {
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(sql);
          while(rs.next()){
            deptName = rs.getString("DEPTNAME");
          }  
        }catch(Exception ex) {
           throw ex;
        }finally {
          T9DBUtility.close(stmt, rs, log);
      } 
    }
      return deptName;
  }
}
