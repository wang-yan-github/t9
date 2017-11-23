package t9.core.module.org_select.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserOnline;
import t9.core.funcs.system.data.T9DepartmentCache;
import t9.core.funcs.system.logic.T9SystemService;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
public class T9OrgSelectLogic {
  private static Logger log = Logger.getLogger("t9.core.module.org_select.act");
  public static boolean isShow(Connection conn, int deptId ,String deptIds) throws Exception {
    if (T9WorkFlowUtility.findId(deptIds, deptId + "")) {
      return true;
    } else {
      Statement stmt = null;
      ResultSet rs = null;
      String sql = "SELECT SEQ_ID FROM DEPARTMENT WHERE DEPT_PARENT = '" + deptId + "' order by DEPT_NO ASC, DEPT_NAME ASC";
      try {
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
          int seqId = rs.getInt("SEQ_ID");
          if (isShow(conn , seqId , deptIds)) {
            return true;
          }
        }
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stmt, rs, log);
      }
    }
    return false;
  }
  public List<T9Department> searchDeptparent(Connection dbConn, int seqId) throws Exception {
    List list = new ArrayList();
    T9Department de = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT DEPT_PARENT,SEQ_ID,DEPT_NAME,DEPT_NO FROM DEPARTMENT WHERE SEQ_ID = '" + seqId + "' order by DEPT_NO ASC, DEPT_NAME ASC";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        de = new T9Department();
        de.setDeptParent(rs.getInt("DEPT_PARENT"));
        de.setSeqId(rs.getInt("SEQ_ID"));
        de.setDeptName(rs.getString("DEPT_NAME"));
        de.setDeptNo(rs.getString("DEPT_NO"));
        list.add(de);
        if(rs.getInt("DEPT_PARENT") == 0){
          return list;
        }
        List srclist = searchDeptparent(dbConn,rs.getInt("DEPT_PARENT"));
        list.addAll(srclist);
      }
//      for(Iterator it = list.iterator(); it.hasNext();){
//        T9Department der = (T9Department)(it.next());
//        List srclist = searchDeptparent(dbConn,der.getSeqId());
//        list.addAll(srclist);
//      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  
  /**
   * 获取人员是否在线
   * @param dbConn
   * @param userId
   * @return
   * @throws Exception
   */
  
  public String getUserStateImg(Connection dbConn ,int userId) throws Exception{
    String sql = " SELECT USER_STATE FROM USER_ONLINE WHERE USER_ID=" + userId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    synchronized(T9SystemService.onlineSync) {
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      while(rs.next()){
        result = rs.getString(1);
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
    dbConn.commit();
    }
    return result;
  }
  
  /**
   * 判断用户是否在线
   * @param dbConn
   * @param userId
   * @return
   * @throws Exception
   */
  
  public boolean getUserState(Connection dbConn ,int userId) throws Exception{
    String sql = " SELECT count(*) FROM USER_ONLINE WHERE USER_ID =" + userId;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String result = null;
    try{
      pstmt = dbConn.prepareStatement(sql);
      rs = pstmt.executeQuery();
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e){
      throw e;
    } finally {
      T9DBUtility.close(pstmt, rs, null);
    }
  }
  
  /**
   * 取得部门名称
   * @param conn
   * @param deptId
   * @return
   * @throws Exception
   */
  public String getDeptNameLogic(Connection conn , int deptId) throws Exception{
    String result = "";
    String sql = " select DEPT_NAME from DEPARTMENT where SEQ_ID = " + deptId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  
  /**
   * 取得角色名称
   * @param conn
   * @param roleId
   * @return
   * @throws Exception
   */
  public String getRoleNameLogic(Connection conn , int roleId) throws Exception{
    String result = "";
    String sql = " select PRIV_NAME from USER_PRIV where SEQ_ID = " + roleId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  /**
   * 当deptId等于0时调用此方法取得所有deptId
   * @param conn
   * @return
   * @throws Exception
   */
  public static String getAlldept(Connection conn) throws Exception{
    String result = "";
    String sql = "select SEQ_ID FROM DEPARTMENT";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        int deptId = rs.getInt(1);
        if(!"".equals(result)){
          result += ",";
        }
        result += deptId;
      }
    } catch (Exception e) {
      throw e;   
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return result;
  }
  /**
   * 当deptId等于0时调用此方法取得所有deptId
   * @param conn
   * @return
   * @throws Exception
   */
  public static String changeDept(Connection conn , String prcsDept) throws Exception{
    if ("0".equals(prcsDept)) {
      prcsDept  = T9OrgSelectLogic.getAlldept(conn);
    }
    return prcsDept;
  }
  /**
   * 当deptId等于0时调用此方法取得所有deptId
   * @param conn
   * @return
   * @throws Exception
   */
  public static String changePriv(Connection conn , String privStr) throws Exception{
    String result = "";
    if (privStr == null || "".equals(privStr)){
      return privStr;
    }
    String [] arra = privStr.split("\\|");
    String user = "";
    String priv = "";
    String dept = "";
    if (arra.length >= 2 ) {
      user = arra[0];
      dept = arra[1];
      dept = changeDept(conn, dept);
      if (arra.length == 3) {
        priv = arra[2];
      }
    } else {
      return privStr;
    }
    result = user + "|" + dept + "|" + priv;
    return result;
  }
  public Map<Integer , T9UserOnline> getUserOnlineMap(Connection dbConn) throws Exception{
    String query = "select DISTINCT USER_ID, USER_STATE from USER_ONLINE";
    Map<Integer , T9UserOnline> onLine = new HashMap();
    Statement stm4 = null;
    ResultSet rs4 = null;
    synchronized(T9SystemService.onlineSync) {
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9UserOnline dept  = new T9UserOnline();
        dept.setUserId(rs4.getInt("USER_ID"));
        dept.setUserState(rs4.getString("USER_STATE"));
        onLine.put(rs4.getInt("USER_ID") , dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    dbConn.commit();
    }
    return onLine;
  }
  public ArrayList<T9UserOnline> getUserOnlineList(Connection dbConn) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select DISTINCT USER_ID, USER_STATE from USER_ONLINE";
    ArrayList<T9UserOnline> onLine = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    synchronized(T9SystemService.onlineSync) {
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9UserOnline dept  = new T9UserOnline();
        dept.setUserId(rs4.getInt("USER_ID"));
        dept.setUserState(rs4.getString("USER_STATE"));
        onLine.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    dbConn.commit();
    }
    return onLine;
  }
  
  /**
   * 获取在线的人员
   * @param dbConn
   * @return
   * @throws Exception
   */
  public List<T9Person> getOnlineUsers(Connection dbConn) throws Exception{
    List<T9Person> persons = new ArrayList<T9Person>();
    Map<Integer , T9UserOnline> map = getUserOnlineMap(dbConn);
    
    Set<Integer> userIds = map.keySet();
    String userId = "";
    for (Integer u : userIds) {
      //T9Person p = (T9Person) orm.loadObjSingle(dbConn, T9Person.class, u.getUserId());
      if (u != 0) {
        userId += u + ",";
      }
    }
    if ("".equals(userId)) {
      return persons;
    }
    userId = t9.core.funcs.doc.util.T9WorkFlowUtility.getOutOfTail(userId);
    persons = this.getPersons(dbConn, userId ,  map);
    return persons;
  }
  public List<T9Person> getPersons(Connection dbConn , String str , Map<Integer , T9UserOnline> map) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<T9Person> list = new ArrayList();
    try {
      String sql = "select SEQ_ID,USER_PRIV,USER_NO,USER_ID,DEPT_ID,DEPT_ID_OTHER,USER_PRIV_OTHER,USER_NAME,SEX,ON_STATUS,TEL_NO_DEPT,EMAIL,OICQ,MY_STATUS from PERSON where SEQ_ID IN (" + str + ")";
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()){
        T9Person p = new T9Person();
        int seqId = rs.getInt("SEQ_ID");
        String userId = rs.getString("USER_ID");
        String userPriv = rs.getString("USER_PRIV");
        String onstatus = rs.getString("ON_STATUS");
        int userNo = rs.getInt("USER_NO");
        int deptId = rs.getInt("DEPT_ID");
        String deptIdOther = rs.getString("DEPT_ID_OTHER");
        String userPrivOther = rs.getString("USER_PRIV_OTHER");
        String userName = rs.getString("USER_NAME");
        String sex = rs.getString("SEX");
        String telNoDept = rs.getString("TEL_NO_DEPT");
        String email = rs.getString("EMAIL");
        String oicq = rs.getString("OICQ");
        String myState = rs.getString("MY_STATUS");
        
        p.setSeqId(seqId);
        p.setUserId(userId);
        p.setUserPriv(userPriv);
        p.setOnStatus(onstatus);
        p.setUserNo(userNo);
        p.setDeptId(deptId);
        p.setDeptIdOther(deptIdOther);
        p.setUserPrivOther(userPrivOther);
        p.setUserName(userName);
        p.setSex(sex);
        p.setTelNoDept(telNoDept);
        p.setEmail(email);
        p.setOicq(oicq);
        p.setMyStatus(myState);
        p.setOnStatus(map.get(seqId).getUserState());
        list.add(p);
      }
    } catch (Exception e) {
      throw e;   
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return list;
  }
  public String getUserPrivNo(Connection dbConn, String privId) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select PRIV_NO from USER_PRIV where SEQ_ID = ?";
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1, Integer.parseInt(privId));
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString("PRIV_NO");
      }
      return "";
    } catch (NumberFormatException e) {
      return "";
    } catch (Exception e) {
      throw e;   
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public String getUserDeptNo(Connection dbConn, int deptId) throws Exception {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "select DEPT_NO from DEPARTMENT where SEQ_ID = ?";
      ps = dbConn.prepareStatement(sql);
      ps.setInt(1, deptId);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString("DEPT_NO");
      }
      return "";
    } catch (NumberFormatException e) {
      return "";
    } catch (Exception e) {
      throw e;   
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
  }
  
  public String getUserOnlineUserId(Connection conn) throws Exception{
    String result = "";
    String sql = "select DISTINCT USER_ID from USER_ONLINE";
    PreparedStatement ps = null;
    ResultSet rs = null;
    synchronized(T9SystemService.onlineSync) {
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next()){
        int userId = rs.getInt("USER_ID");
        if(!"".equals(result)){
          result += ",";
        }
        result += userId;
      }
    } catch (Exception e) {
      throw e;   
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    conn.commit();
    }
    return result;
  }
  
  public String getUserStatesLogic(Connection conn , int userId) throws Exception{
    String result = "";
    String sql = " select USER_STATE from USER_ONLINE where USER_ID = " + userId ;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    synchronized(T9SystemService.onlineSync) {
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        String toId = rs.getString(1);
        if(toId != null){
          result = toId;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    conn.commit();
    }
    return result;
  }
  
  public ArrayList<T9Department> getDeptList(Connection dbConn, String whereStr) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where " + whereStr + "";
    ArrayList<T9Department> depts = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9Department dept  = new T9Department();
        dept.setSeqId(rs4.getInt("SEQ_ID"));
        dept.setDeptName(rs4.getString("DEPT_NAME"));
        depts.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return depts;
  }
  
  public ArrayList<T9Department> getDepartmentList(Connection dbConn, int deptParent) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select SEQ_ID from DEPARTMENT where DEPT_PARENT =" + deptParent;
    ArrayList<T9Department> depts = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      //System.out.println(query);
      while (rs4.next()) {
        T9Department dept  = new T9Department();
        dept.setSeqId(rs4.getInt("SEQ_ID"));
        depts.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return depts;
  }
  
  public long existsTableNo(Connection dbConn, int deptParent)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM DEPARTMENT WHERE DEPT_PARENT = '" + deptParent
          + "'";
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      if (rs.next()) {
        count = rs.getLong(1);
      }

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return count;
  }
  
  public  ArrayList<T9Person> getPersonList(Connection dbConn, String whereStr) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select SEQ_ID , USER_NAME from PERSON where " + whereStr + "";
    ArrayList<T9Person> person = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9Person dept  = new T9Person();
        dept.setSeqId(rs4.getInt("SEQ_ID"));
        dept.setUserName(rs4.getString("USER_NAME"));
        person.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return person;
  }
  
  public  ArrayList<T9Person> getPostPrivList(Connection dbConn, String whereStr) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select SEQ_ID , POST_PRIV from PERSON where " + whereStr + "";
    ArrayList<T9Person> person = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9Person dept  = new T9Person();
        dept.setSeqId(rs4.getInt("SEQ_ID"));
        dept.setPostPriv(rs4.getString("POST_PRIV"));
        person.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return person;
  }
  
  
  public  ArrayList<T9Person> getPersonPrivList(Connection dbConn, String whereStr, String userPrivStr, String loginUserPriv) throws Exception{
    T9ORM orm = new T9ORM();
    String query = "select PERSON.SEQ_ID " +
            ",PERSON.USER_ID" +
            ",PERSON.USER_NAME" +
            ",PERSON.DEPT_ID" +
            ",PERSON.SEX" +
            ",PERSON.USER_PRIV" +
            ",PERSON.EMAIL" +
            ",PERSON.TEL_NO_DEPT" +
            ",PERSON.OICQ" +
            ",PERSON.DEPT_ID_OTHER" +
    		    ",PERSON.POST_PRIV from PERSON "+userPrivStr+" where " + whereStr + "";
    ArrayList<T9Person> person = new ArrayList();
    Statement stm = null;
    ResultSet rs = null;
    //System.out.println(query+"+++");
    try {
      stm = dbConn.createStatement();
      rs = stm.executeQuery(query);
      while (rs.next()) {
        T9Person dept  = new T9Person();
        dept.setSeqId(rs.getInt("SEQ_ID"));
        dept.setUserId(rs.getString("USER_ID"));
        dept.setUserName(rs.getString("USER_NAME"));
        dept.setDeptId(rs.getInt("DEPT_ID"));
        dept.setSex(rs.getString("SEX"));
        dept.setUserPriv(rs.getString("USER_PRIV"));
        dept.setPostPriv(rs.getString("POST_PRIV"));
        dept.setEmail(rs.getString("EMAIL"));
        dept.setOicq(rs.getString("OICQ"));
        dept.setTelNoDept(rs.getString("TEL_NO_DEPT"));
        dept.setDeptIdOther(rs.getString("DEPT_ID_OTHER"));
        person.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm , rs , null);
    }
    return person;
  }
  
  /**
   * 递归 读取顶级部门
   * @param dbConn
   * @param deptId
   * @return
   * @throws Exception
   */
  public T9Department getDeptParentId(Connection dbConn, int deptId) throws Exception{
    String query = "select SEQ_ID,DEPT_PARENT,DEPT_NAME from DEPARTMENT where SEQ_ID ="+deptId;
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      if (rs4.next()) {
        int parentId = rs4.getInt("DEPT_PARENT");
        if (parentId == 0) {
          T9Department dept = new T9Department();
          dept.setSeqId(rs4.getInt("SEQ_ID"));
          dept.setDeptName(rs4.getString("DEPT_NAME"));
          dept.setDeptParent(parentId);
          return dept;
        } else {
          return this.getDeptParentId(dbConn, parentId);
        }
      } 
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return null;
  }
  
  public String getSecrityShowIp(Connection conn) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    String org = null;
    try {
      String queryStr = "select PARA_VALUE from SYS_PARA WHERE PARA_NAME='SEC_SHOW_IP'";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        org = rs.getString(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return org;
  }
  
  public String getShowIp(Connection conn, String sysLog, int userId) throws Exception {
    String result = "";
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select IP from SYS_LOG where type = '" + sysLog + "' and USER_ID = " + userId + " order by TIME desc ";
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      if (rs.next()) {
        String org = rs.getString(1);
        if(org != null){
          result = org;
        }
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  /**
   * 构造所有部门树
   * @param dbConn
   * @return
   * @throws Exception
   */
  public DefaultMutableTreeNode buildDeptTree(Connection dbConn) throws Exception {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new Object(), true);
    
    try {
      List<T9Department> list = T9DepartmentCache.getDepartmentListCache(dbConn);
      Map<Integer, DefaultMutableTreeNode> nodes = new HashMap<Integer, DefaultMutableTreeNode>();
      for (T9Department d : list) {
        nodes.put(d.getSeqId(), new DefaultMutableTreeNode(d));
      }
      List<DefaultMutableTreeNode> values = new ArrayList<DefaultMutableTreeNode>(nodes.values());
      Collections.sort(values, new Comparator<DefaultMutableTreeNode>() {
        public int compare(DefaultMutableTreeNode node1, DefaultMutableTreeNode node2) {
          T9Department d1 = (T9Department)node1.getUserObject();
          T9Department d2 = (T9Department)node2.getUserObject();
          int c = d1.getDeptNo().compareTo(d2.getDeptNo());
          if (c == 0) {
//            Collator cmp = Collator.getInstance(java.util.Locale.CHINA); 
//            c = cmp.compare(d1.getDeptName(), d2.getDeptName());
            //d1.getDeptCode().compareTo(d2.getDeptCode());
            c = d1.getDeptName().compareTo(d2.getDeptName());
          }
          return c;
        }
        
      });
      for (DefaultMutableTreeNode node : values) {
        T9Department d = (T9Department)node.getUserObject();
        int parentId = d.getDeptParent();
        if (parentId == 0) {
          root.add(node);
        }
        else {
          nodes.get(parentId).add(node);
        }
      }
      
    } catch (Exception e) {
      throw e;
    } finally {
      
    }
    return root;
  }
}
