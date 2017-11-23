package t9.core.module.org_select.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.db.T9DBUtility;

public class T9OrgSelectModuleLogic {
  private static Logger log = Logger
  .getLogger("t9.core.module.org_select.logic.T9OrgSelectModuleLogic");
  
  public ArrayList<T9Department> getChildDept(Connection conn,int deptId) throws Exception{
    ArrayList<T9Department> depts = new ArrayList<T9Department>();
    String sql = "select " + "  SEQ_ID ,DEPT_NAME" + " from " + " DEPARTMENT "
    + " where " + " DEPT_PARENT = " + deptId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        T9Department dept = new T9Department();
        int deptSeqId = rs.getInt(1);
        String deptName = rs.getString(2);
        dept.setSeqId(deptSeqId);
        dept.setDeptName(deptName);
        depts.add(dept);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return depts;
  }
  /**
   * 取得当前部门下所有符合当前登录用户管理范围的用户
   * @param conn
   * @param deptId
   * @param mp
   * @param loginUser
   * @return
   * @throws Exception
   */
  public ArrayList<T9Person> getChildDeptPerson(Connection conn,int deptId,T9MyPriv mp ,T9Person loginUser) throws Exception{
    ArrayList<T9Person> persons = new ArrayList<T9Person>();
    String sql = "SELECT SEQ_ID , USER_NAME FROM PERSON WHERE SEQ_ID != " + loginUser.getSeqId() + " AND DEPT_ID = " + deptId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        int seqId = rs.getInt(1);
        if(T9PrivUtil.isUserPriv(conn, seqId, mp, loginUser.getPostPriv(), loginUser.getPostDept(), loginUser.getSeqId(), loginUser.getDeptId())){
          continue;
        }
        T9Person person = new T9Person();
        
        String userName = rs.getString(2);
        person.setSeqId(seqId);
        person.setUserName(userName);
        persons.add(person);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(ps, rs, log);
    }
    return persons;
  }
}
