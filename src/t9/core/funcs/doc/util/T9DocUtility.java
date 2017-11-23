package t9.core.funcs.doc.util;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.send.data.T9DocFlowRun;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9DocUtility {
  /**
   * 是否具有全局权限
   * @param userId
   * @param conn
   * @return
   * @throws Exception
   */
  public boolean haveAllRight(T9Person user , Connection conn) throws Exception{
    return haveAllRight( user.getSeqId() ,  user.getUserPriv() ,  user.getUserPrivOther() ,  conn) ;
  }
  /**
   * 是否具有全局权限
   * @param userId
   * @param conn
   * @return
   * @throws Exception
   */
  public boolean haveAllRight(int userId , String userPriv , String userPrivOther , Connection conn) throws Exception{
    String query = "select * from doc_recv_priv where dept_id = '-1'";
    Statement stm = null; 
    ResultSet rs = null; 
    String role = "";
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      if (rs.next()){ 
        role = T9Utility.null2Empty(rs.getString("USER_ID"));
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    if (T9WorkFlowUtility.findId(role, userPriv)
        || !"".equals(T9WorkFlowUtility.checkId(role, userPrivOther, true))
    ) {
      return true;
    } else {
      return false;
    }
  }
  public boolean haveEsbRecRight(T9Person user , Connection conn) throws Exception {
    String deptIds = "";
    String privIds ="";
    String userIds = "";
    String query = "select USER_ID , DEPT_ID, USER_PRIV from ESB_REC_PERSON";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        deptIds =T9Utility.null2Empty(rs.getString("DEPT_ID"));
        privIds =T9Utility.null2Empty(rs.getString("USER_PRIV"));
        userIds = T9Utility.null2Empty(rs.getString("USER_ID"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if(("ALL_DEPT".equals(deptIds) || "0".equals(deptIds))
        || T9WorkFlowUtility.findId(userIds , String.valueOf(user.getSeqId()))
        || T9WorkFlowUtility.findId(deptIds , String.valueOf(user.getDeptId()))
        || T9WorkFlowUtility.findId(privIds , user.getUserPriv())){
      return true;
    }
    return false;
  }
  /**
   * 具有那些部门的权限
   * @param userId
   * @param conn
   * @return
   * @throws Exception
   */
  public String deptRight(int userId , Connection conn) throws Exception{
    String query = "select * from doc_recv_priv where dept_id <> '-1' and " + T9DBUtility.findInSet(userId + "", "USER_ID");
    Statement stm = null; 
    ResultSet rs = null; 
    String dept = "";
    try { 
      stm = conn.createStatement(); 
      rs = stm.executeQuery(query); 
      while (rs.next()){ 
        String deptId = T9Utility.null2Empty(rs.getString("dept_id"));
        if (!T9WorkFlowUtility.findId(dept, deptId)) {
          dept += "'" +  deptId + "',";
        }
      } 
    } catch(Exception ex) { 
      throw ex; 
    } finally { 
      T9DBUtility.close(stm, rs, null); 
    } 
    return dept;
  }
  public Map getDeptByUser(int userId , Connection conn) throws Exception{
    String query = "select DEPT_ID , DEPT_NAME from DEPARTMENT,PERSON where DEPARTMENT.SEQ_ID = PERSON.DEPT_ID AND PERSON.SEQ_ID = " + userId;
    Statement stmt = null;
    ResultSet rs = null;
    String deptName = "";
    int deptId = 0 ;
    Map map = new HashMap();
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        deptName = rs.getString("DEPT_NAME");
        deptId = rs.getInt("DEPT_ID");
        map.put("DEPT_NAME", deptName);
        map.put("DEPT_ID", deptId);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return map;
  }
  public T9DocFlowRun getSendDocByRunId(int runId , Connection conn) throws Exception{
    HashMap map = new HashMap();
    map.put("RUN_ID", runId);
    T9ORM orm = new T9ORM();
    T9DocFlowRun flowRun = (T9DocFlowRun) orm.loadObjSingle(conn, T9DocFlowRun.class, map);
    return flowRun;
  }
  public String getSortIds(String sortName , Connection dbConn) throws Exception {
    String sortNamesNew = "";
    if (!T9Utility.isNullorEmpty(sortName)) {
      String[] news = sortName.split(",");
      for (String tmp : news) {
        if (!T9Utility.isNullorEmpty(tmp)) {
          sortNamesNew += "'" + tmp + "',";
        }
      }
    }
    if (sortNamesNew.endsWith(",")) {
      sortNamesNew = sortNamesNew.substring(0, sortNamesNew.length() - 1);
    }
    String result = "";
    try {
      if (!"".equals(sortNamesNew)) {
        String sql = "select seq_id from "+ T9WorkFlowConst.FLOW_SORT +" where sort_name in (" + sortNamesNew + ")"; 
        Statement stm = null;
        ResultSet rs = null;
        try {
          stm = dbConn.createStatement();
          rs = stm.executeQuery(sql);
          while (rs.next()) {
            result += rs.getInt("seq_id") + ",";
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm , rs , null);
        }
      }
    }catch(Exception ex) {
      throw ex;
    } 
    return result;
  }
  public Map getFlowBySortIds(String sortIds , Connection dbConn , T9Person user) throws Exception {
    Map<String , String> map = new HashMap<String , String>();
    sortIds = T9WorkFlowUtility.getOutOfTail(sortIds);
    try {
      if (!"".equals(sortIds)) {
        String sql = "select  SEQ_ID ,FLOW_NO, flow_name , flow_Type , NEW_USER ,query_User,query_User_Dept,manage_User,manage_User_Dept from "+ T9WorkFlowConst.FLOW_TYPE +" where flow_sort in (" + sortIds + ")"; 
        Statement stm = null;
        ResultSet rs = null;
        try {
          stm = dbConn.createStatement();
          rs = stm.executeQuery(sql);
          T9PrcsRoleUtility tru = new T9PrcsRoleUtility();
          while (rs.next()) {
            T9DocFlowType ft = new T9DocFlowType();
            int seqId = rs.getInt("SEQ_ID");
            String flowName = rs.getString("flow_name");
            String flowType = rs.getString("flow_Type");
            String newUser = rs.getString("NEW_USER");
            String queryUser = rs.getString("query_User");
            String queryUserDept = rs.getString("query_User_Dept");
            String manageUser = rs.getString("manage_User");
            String manageUserDept = rs.getString("manage_User_Dept");
            ft.setFlowType(flowType);
            ft.setFlowName(flowName);
            ft.setSeqId(seqId);
            ft.setNewUser(newUser);
            ft.setQueryUser(queryUser);
            ft.setQueryUserDept(queryUserDept);
            ft.setManageUser(manageUser);
            ft.setManageUserDept(manageUserDept);
            boolean flag = false;
            flag = tru.prcsRole(ft, 0, user, dbConn);
            if (flag) {
              map.put(seqId + "", flowName);
            }
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm , rs , null);
        }
      }
    }catch(Exception ex) {
      throw ex;
    } 
    return map;
  }
  public static boolean usingEsb() {
    String usingEsb = T9SysProps.getProp("USING_ESB");
    if ("1".equals(usingEsb) ) {
      return true;
    } else {
      return false;
    }
  }
}
