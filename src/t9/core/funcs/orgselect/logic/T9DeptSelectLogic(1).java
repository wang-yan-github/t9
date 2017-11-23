package t9.core.funcs.orgselect.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.data.T9DepartmentCache;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9DeptSelectLogic {
  /**
   * 取得所有部门的Json数据
   * @param depts
   * @param procId
   * @return
   * @throws Exception
   */
  public StringBuffer getDeptJson(List depts) throws Exception{
    return this.getDeptJson(depts, 0);
  }
  /**
   * 取得部门的Json数据
   * @param depts
   * @param procId
   * @param deptId
   * @return
   * @throws Exception
   */
  public StringBuffer getDeptJson(List depts , int deptId) throws Exception{
    StringBuffer sb = new StringBuffer("[");
    T9Department dept = new T9Department();
    if(deptId != 0){
      for(int i = 0 ;i < depts.size();i ++){
        dept = (T9Department) depts.get(i);
        if(dept.getSeqId() == deptId){
          break;
        }
      }
      this.setDeptSingle(dept, depts, sb , 0);
    }else{
      for(int i = 0 ;i < depts.size();i ++){
        dept = (T9Department) depts.get(i);
        if(dept.getDeptParent() == 0){
          this.setDeptSingle(dept, depts, sb , 0);
        }
      }
    }
    
    sb.deleteCharAt(sb.length() - 1);
    
    sb.append("]");
    return sb;
  }
  
  /**
   * 取得一个部门的节点定义
   * @param dept
   * @param depts
   * @param sb
   * @param level
   */
  public void setDeptSingle(T9Department dept , List depts, StringBuffer sb ,int level){
    String deptName = dept.getDeptName();
    int deptId = dept.getSeqId();
    boolean isChecked = false;
    String nbsp = "├";
    for(int i = 0 ;i < level;i++){
      nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + nbsp;
    }
    sb.append("{");
    sb.append("deptName:\"" + nbsp + T9Utility.encodeSpecial(deptName) + "\",");
    sb.append("deptId:'" + deptId + "',");
    sb.append("isChecked:" + isChecked) ;
    sb.append("},");
    //depts.remove(dept);
    
    level++;
    for(int i = 0 ;i < depts.size() ; i++){
      T9Department  deptTmp = (T9Department) depts.get(i);
      if(deptTmp.getDeptParent() == deptId){
        setDeptSingle(deptTmp, depts, sb, level);
      }
    }
  }
  /**
   * 判段id是不是在str里面
   * @param str
   * @param id
   * @return
   */
  public  boolean findId(String str, String id) {
    if(str == null || id == null || "".equals(str) || "".equals(id)){
      return false;
    }
    String[] aStr = str.split(",");
    for(String tmp : aStr){
      if(tmp.equals(id)){
        return true;
      }
    }
    return false;
  }
  public String getParentDept(Connection conn , int myDept) throws Exception {
    int deptParent = 0 ;
    String deptStr = myDept + "";
    
    T9Department d = T9DepartmentCache.getDepartmentCache(conn, myDept);
    if (d != null) {
      deptParent = d.getDeptParent();
      deptStr += "," + deptParent;
    }
    
    if (deptParent != 0) {
      T9Department d2 = T9DepartmentCache.getDepartmentCache(conn, deptParent);
        if (d2 !=  null){
          deptParent = d2.getDeptParent(); 
          deptStr += "," + deptParent;
        }
    }
    return deptStr;
  }
  public String getDefUserDept(Connection conn,T9MyPriv mp , int myDept) throws Exception{
    //指定人员
    String result = getParentDept(conn ,  myDept);
    if (!"".equals(result) 
        && !result.endsWith(",")) {
      result += ",";
    }
    if( "3".equals(mp.getDeptPriv())){
      String users = mp.getUserId();
      if (users != null) {
        String[] userIds = users.split(",");
        for (int i = 0; i < userIds.length; i++) {
          if(!"".equals(userIds[i].trim())){
            int userId = Integer.valueOf(userIds[i].trim());
            int deptId = getDeptId(conn, userId);
            if (!this.findId(result, String.valueOf(deptId))) {
              result += deptId + ",";
            }
          }
        }
      }
      return result;
      //指定部门
    }else if( "2".equals(mp.getDeptPriv())){
      String depts = mp.getDeptId();
      StringBuffer sb = new StringBuffer();
      if (depts != null) {
        String[] aDept = depts.split(",");
        for (int i = 0 ;i < aDept.length ; i++ ){
          if (T9Utility.isInteger(aDept[i])) {
            int deptTmp = Integer.parseInt(aDept[i]);
            String rss =  getParentDept(conn ,  deptTmp);
            result += rss + ",";
            this.getAllChildDept(deptTmp, conn, sb);
          }
        }
        if (!"".equals(result) && !result.endsWith(",")) {
          result = result + "," + sb.toString();
        } else {
          result += sb.toString();
        }
        if (depts.endsWith(",")) {
          depts = depts.substring(0, depts.length() - 1);
        }
        if (!"".equals(result) && !result.endsWith(",")) {
          depts = result + "," + depts;
        } else {
          depts = result + depts;
        }
        return depts;
      } else {
        return "";
      }
    } else if ("0".equals(mp.getDeptPriv())) {
      StringBuffer sb = new StringBuffer();
      this.getAllChildDept(myDept, conn, sb);
      result += sb.toString();
      if (result.endsWith(",")) {
        result = result.substring(0, result.length() - 1);
      }
      return result;
    } 
    return "";
  }
  /**
   * 根据用户ID取得用户的部门ID
   * @param conn
   * @param userId
   * @return
   * @throws Exception
   */
  public Integer getDeptId(Connection conn, int userId) throws Exception{
    int result = 0;
    String sql = "select DEPT_ID FROM PERSON WHERE SEQ_ID=" + userId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
         result = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  public void getAllChildDept(int deptId , Connection conn , StringBuffer sb) throws Exception {
    String s = sb.toString();
    if (!this.findId(s, String.valueOf(deptId))) {
      sb.append(deptId + ",");
    }
    String query = "select SEQ_ID from DEPARTMENT where DEPT_PARENT=" + deptId;
    Statement stmt = null;
    ResultSet rs = null;
    List<Integer> as = new ArrayList();
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        int seqId = rs.getInt("SEQ_ID");
        as.add(seqId);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for (Integer a : as) {
      getAllChildDept(a , conn , sb);
    }
  }
}
