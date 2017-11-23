package t9.core.esb.client.logic;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import t9.core.esb.client.data.T9ExtDept;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Guid;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.user.api.core.db.T9DbconnWrap;

public class T9DeptTreeLogic {
  public String getDeptTreeJson(String deptId , String seqId, Connection conn) throws Exception{
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    this.getDeptTree(deptId, sb, 0 , seqId , conn);
    if(sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    
    return sb.toString();
  }
  public String getDeptName(Connection conn , String deptId) throws Exception{
    String result = "";
    String sql = " select DEPT_NAME from EXT_DEPT where DEPT_ID = '" + deptId + "'";
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return result;
  }
  public T9ExtDept getDeptByEsbUser(Connection conn , String esbUser) throws Exception {
    T9ExtDept dept = null;
    String query = "select * from EXT_DEPT WHERE ESB_USER = '" + esbUser + "'";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        String deptNo = rs.getString("DEPT_NO");
        String deptName = rs.getString("DEPT_NAME"); 
         String deptParent = rs.getString("DEPT_PARENT");
         String deptDesc = rs.getString("DEPT_DESC");
         String deptId = rs.getString("DEPT_ID");
         dept = new T9ExtDept( deptNo ,  deptName,  esbUser,
              deptParent,  deptDesc);
         dept.setDeptId(deptId);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return dept; 
  }
  public void getDeptTree(String deptId , StringBuffer sb , int level , String getOutId, Connection conn) throws Exception{
    //首选分级，然后记录级数，是否为最后一个。。。
    List<Map> list = new ArrayList();
    String query = "select DEPT_ID, DEPT_NAME from ext_dept where DEPT_PARENT='" + deptId + "' order by DEPT_NO, DEPT_NAME asc";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        String deptName = rs.getString("DEPT_NAME");
        String seqId = rs.getString("DEPT_ID");
        if (!T9Utility.isNullorEmpty(getOutId) 
            && getOutId.equals(seqId)) {
          continue;
        }
        Map map = new HashMap();
        map.put("deptName", deptName);
        map.put("seqId", seqId);
        list.add(map);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for(int i = 0; i < list.size(); i++){
      String flag = "&nbsp;├";
      if(i == list.size() - 1 ){
        flag = "&nbsp;└";
      }
      String tmp = "";
      for(int j = 0 ;j < level ; j++){
        tmp += "&nbsp;│";
      }
      flag = tmp + flag;
      
      Map dp = list.get(i);
      String seqId = (String)dp.get("seqId");
      String deptName = (String)dp.get("deptName");
      sb.append("{");
      sb.append("text:\"" + flag + T9Utility.encodeSpecial(deptName) + "\",");
      sb.append("value:'" + seqId + "'" );
      sb.append("},");
      this.getDeptTree(seqId, sb, level + 1 , getOutId , conn);
    }
   
  }
  public void getDeptsByDeptParent(Connection conn , String deptParent , int level , StringBuffer sb ) throws Exception {
    String query = "select DEPT_ID , ESB_USER, DEPT_NAME from ext_dept where DEPT_ID = '"+deptParent+"' order by DEPT_NO ASC, DEPT_NAME asc";
    Statement stmt = null;
    ResultSet rs = null;
    String nbsp = "├";
    for(int i = 0 ;i < level;i++){
      nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + nbsp;
    }
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        String deptName = T9Utility.encodeSpecial(rs.getString("DEPT_NAME"));
        String deptId =  T9Utility.encodeSpecial(rs.getString("DEPT_ID"));
        String esbUser =  T9Utility.encodeSpecial(rs.getString("ESB_USER"));
        sb.append("{");
        sb.append("deptName:\"").append(nbsp).append(deptName).append("\"");
        sb.append(",deptId:\"").append(deptId).append("\"");
        sb.append(",esbUser:\"").append(esbUser).append("\"");
        sb.append("},");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
     query = "select DEPT_ID from ext_dept where DEPT_PARENT = '"+deptParent+"' order by DEPT_NO ASC, DEPT_NAME asc";
    List<String> list = new ArrayList();
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        String deptId =  T9Utility.encodeSpecial(rs.getString("DEPT_ID"));
        list.add(deptId);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for (String ss : list) {
      this.getDeptsByDeptParent(conn, ss, level + 1, sb);
    }
  }
  public String getDepts2(Connection conn) throws Exception {
    String query = "select DEPT_ID,DEPT_NO , DEPT_DESC , ESB_USER,DEPT_PARENT , DEPT_NAME from ext_dept ";
    Statement stmt = null;
    ResultSet rs = null;
    int count = 0 ;
    StringBuffer sb = new StringBuffer();
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        String deptName = rs.getString("DEPT_NAME");
        String deptId = rs.getString("DEPT_ID");
        String deptDesc  =  rs.getString("DEPT_DESC");
        String deptNo = rs.getString("DEPT_NO");
        String deptParent = rs.getString("DEPT_PARENT");
        String esbUser = rs.getString("ESB_USER");
        T9ExtDept ed = new T9ExtDept( deptNo ,  deptName,  esbUser,
             deptParent,  deptDesc);
        ed.setDeptId(deptId);
        sb.append(T9ObjectUtility.writeObject(ed)).append(",");
        count++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (count>0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }
  public List<T9ExtDept> getList(String xml) throws Exception {
    SAXReader saxReader = new SAXReader(); 
    StringReader rs = new StringReader(xml);
    Document document = saxReader.read(rs);
    Element root = document.getRootElement(); 
    
    return null;
  }
  public T9ExtDept getDept(Connection conn, String deptId) throws Exception {
    String query = "select DEPT_ID,DEPT_NO , DEPT_DESC , ESB_USER,DEPT_PARENT , DEPT_NAME from ext_dept where DEPT_ID='" + deptId + "'";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      if (rs.next()) {
        String deptName = rs.getString("DEPT_NAME");
        T9ExtDept dept = new T9ExtDept();
        dept.setDeptDesc(rs.getString("DEPT_DESC"));
        dept.setDeptName(deptName);
        dept.setDeptId(rs.getString("DEPT_ID"));
        dept.setDeptNo(rs.getString("DEPT_NO"));
        dept.setDeptParent(rs.getString("DEPT_PARENT"));
        dept.setEsbUser(rs.getString("ESB_USER"));
        return dept;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    return null;
  }
  public void saveDept(Connection conn, T9ExtDept de, String seqId) throws Exception {
    // TODO Auto-generated method stub
    String query = "";
    if (!T9Utility.isNullorEmpty(seqId)) {
       query = "update  ext_dept set DEPT_NO = ? , DEPT_NAME = ? , DEPT_DESC = ? , DEPT_PARENT = ?  , ESB_USER = ?  where DEPT_ID=?";
    } else {
      query = "insert into  ext_dept ( DEPT_NO , DEPT_NAME , DEPT_DESC , DEPT_PARENT , ESB_USER , DEPT_ID ) values (? , ? ,? ,? , ? ,?  ) ";
    }
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(query);
      stmt.setString(1, de.getDeptNo());
      stmt.setString(2, de.getDeptName());
      stmt.setString(3, de.getDeptDesc());
      stmt.setString(4, de.getDeptParent());
      stmt.setString(5, de.getEsbUser());
      if (T9Utility.isNullorEmpty(seqId)) {
        seqId = T9Guid.getRawGuid();
      } 
      stmt.setString(6, seqId);
      stmt.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  public void saveDept(Connection conn, T9ExtDept de) throws Exception {
    // TODO Auto-generated method stub
    String query = "insert into  ext_dept ( DEPT_NO , DEPT_NAME , DEPT_DESC , DEPT_PARENT , ESB_USER , DEPT_ID ) values (? , ? ,? ,? , ? ,?  ) ";
    PreparedStatement stmt = null;
    try {
      stmt = conn.prepareStatement(query);
      stmt.setString(1, de.getDeptNo());
      stmt.setString(2, de.getDeptName());
      stmt.setString(3, de.getDeptDesc());
      stmt.setString(4, de.getDeptParent());
      stmt.setString(5, de.getEsbUser());
      stmt.setString(6, de.getDeptId());
      stmt.executeUpdate();
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, null);
    }
  }
  public void selectDept(List<String> list , Connection conn , String deptId) throws Exception {
    String sql = "select dept_id from EXT_DEPT where DEPT_PARENT = '" + deptId + "'";
    //list.add(deptId);
    Statement stmt = null;
    ResultSet rs = null;
    List<String> list2 = new ArrayList();
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        list.add(rs.getString("dept_id"));
        list2.add(rs.getString("dept_id"));
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    for (String ss : list2) {
      if (!T9Utility.isNullorEmpty(ss)) {
        this.selectDept(list, conn, ss);
      }
    }
  }
  public void delDept(Connection conn , String deptId) throws Exception {
    List<String> list = new ArrayList();
    this.selectDept(list, conn, deptId);
    list.add(deptId);
    String sss = "";
    for (String ss : list){
      if (!T9Utility.isNullorEmpty(ss)) {
        sss += "'" + ss + "',";
      }
    }
    if (sss.endsWith(",")) {
      sss = sss.substring(0, sss.length() - 1);
    }
    String sql = "delete from ext_dept where DEPT_ID IN (" + sss + ")";
    this.exeSql(sql, conn);
  }
  /**
   * 同步
   * @param depts
   */
  public void updateDept(String depts) {
    String[] objects = depts.split(",");
    T9DbconnWrap dbUtil = new T9DbconnWrap();
    Connection dbConn = null;
    try {      
      dbConn = dbUtil.getSysDbConn();
      String sql = "delete from ext_dept";
      this.exeSql(sql, dbConn);
      for (String ss : objects) {
        if (!T9Utility.isNullorEmpty(ss)) {
          T9ExtDept ext = (T9ExtDept) T9ObjectUtility.readObject(ss);
          this.saveDept(dbConn, ext);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dbUtil.closeAllDbConns();
    }
  }
  public void exeSql(String sql , Connection conn) throws Exception {
    Statement stm = null;
    try {
      stm = conn.createStatement();
      stm.executeUpdate(sql);
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, null, null); 
    }
  }
  public  String getEsbUsers(Connection conn , String deptId) throws Exception {
    String deptIn = T9WorkFlowUtility.getInStr(deptId);
    String query = "select ESB_USER FROM EXT_DEPT WHERE DEPT_ID IN (" + deptIn + ")";
    Statement stmt = null;
    ResultSet rs = null;
    
    String esbUser = "";
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(query);
      while (rs.next()) {
        esbUser += rs.getString("ESB_USER") + ",";
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (esbUser.endsWith(","))
      esbUser = esbUser.substring(0, esbUser.length() - 1);
    return esbUser;
  }
  public static String getInStr(String str) {
    if (str == null || "".equals(str)) {
      return "";
    }
    String[] strs = str.split(",");
    String newStr = "";
    for (String tmp : strs) {
      
      if (tmp != null && !"".equals(tmp)) {
        if (tmp.startsWith("'") && tmp.endsWith("'")) {
          newStr += "" + tmp + ",";
        } else {
          newStr += "'" + tmp + "',";
        }
      } 
    }
    if (newStr.endsWith(",") ) {
      newStr = newStr.substring(0, newStr.length() - 1);
    }
    return newStr;
  }
}
