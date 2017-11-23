package t9.core.funcs.person.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9UserPrivLogic {
  private static Logger log = Logger.getLogger("t9.core.funcs.person.act");
  public List deleteDeptMul(Connection dbConn, int seqId) {
    List list = new ArrayList();
    int seqID = 0;
    T9Person de = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT SEQ_ID FROM PERSON WHERE USER_PRIV = '" + seqId + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        de = new T9Person();
        de.setSeqId(rs.getInt("SEQ_ID"));
        list.add(de);
      }
//      for(Iterator it = list.iterator(); it.hasNext();){
////        for(int x = 0; x<list.size(); x++){
////          T9Department 
////          
////        }
//        T9Department der = (T9Department)(it.next());
//        List srclist = deleteDeptMul(dbConn,der.getSeqId());
//        list.addAll(srclist);
//      }
    } catch (Exception ex) {
      // throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }

  public boolean existsTableNo(Connection dbConn, String privNo)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM USER_PRIV WHERE PRIV_NO = '" + privNo
          + "'";
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      long count = 0;
      if (rs.next()) {
        count = rs.getLong(1);
      }
      if (count == 1) {
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
  }

  public List selectUserPriv(Connection dbConn) throws Exception {
    List list = new ArrayList();
    T9UserPriv dt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT * FROM USER_PRIV";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        dt = new T9UserPriv();
        dt.setSeqId(rs.getInt("SEQ_ID"));
        dt.setPrivNo(rs.getInt("PRIV_NO"));
        list.add(dt);
      }

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  
  public List selectPerson(Connection dbConn, int privNo) throws Exception {
    List list = new ArrayList();
    T9Person dt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT SEQ_ID FROM PERSON WHERE USER_PRIV = '" + privNo
      + "'";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        dt = new T9Person();
        dt.setSeqId(rs.getInt("SEQ_ID"));
        dt.setUserPriv(rs.getString("USER_PRIV"));
        list.add(dt);
      }

    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  
  public String selectUserPrivFun(Connection dbConn, String seqIdd) throws Exception {
    //System.out.println(seqIdd+"UUUUUUUUUUUUUU");
    int seqId = Integer.parseInt(seqIdd);
    List list = new ArrayList();
    String str = "";
    T9UserPriv dt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT FUNC_ID_STR FROM USER_PRIV WHERE SEQ_ID = " + seqId;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        //dt.setSeqId(rs.getInt("SEQ_ID"));
        str = rs.getString("FUNC_ID_STR");
        
        //list.add(dt);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return str;
  }

  public String otherPriv(Connection dbConn, String seqIdd) throws Exception {
    //System.out.println(seqIdd+"UUUUUUUUUUUUUU");
    int seqId = Integer.parseInt(seqIdd);
    List list = new ArrayList();
    String str = "";
    T9UserPriv dt = null;
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT USER_PRIV_OTHER FROM PERSON WHERE SEQ_ID = " + seqId;
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        //dt.setSeqId(rs.getInt("SEQ_ID"));
        str = rs.getString("USER_PRIV_OTHER");
        
        //list.add(dt);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return str;
  }
  /**
   * 主要是按模块选择角色
   * @param conn
   * @param person
   * @return
   * @throws Exception
   */
  public List getRoleList(Connection conn , String moduleId , T9Person u, int privNoFlag , String privOp) throws Exception{
    List list = new ArrayList();
    T9MyPriv priv = T9PrivUtil.getMyPriv(conn, u, moduleId, privNoFlag);
    String privIdStr = priv.getPrivId();
    String rolePriv = priv.getRolePriv();
    int  myPrivNo = priv.getPrivNo();
    if (!u.isAdminRole() &&  !T9Utility.isNullorEmpty(privOp)) {
      rolePriv = "0";
      if (myPrivNo == -1 ) {
        String query = "select PRIV_NO from USER_PRIV where SEQ_ID=" + u.getUserPriv();
        Statement stmt = null;
        ResultSet rs = null;
        try {
          stmt = conn.createStatement();
          rs = stmt.executeQuery(query);
          if (rs.next()) {
            myPrivNo = rs.getInt("PRIV_NO");
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stmt, rs, log);
        }
      }
    }
    String sql = "SELECT SEQ_ID , PRIV_NAME FROM USER_PRIV where 1=1 ";
    if ("0".equals(rolePriv)) {
      sql += " and PRIV_NO > " + myPrivNo;
    } else if ("1".equals(rolePriv)) {
      sql += " and PRIV_NO >= " + myPrivNo;
    } else if ("3".equals(rolePriv) && !"".equals(privIdStr) && privIdStr != null) {
      if (privIdStr.endsWith(",")) {
        privIdStr = privIdStr.substring(0 , privIdStr.length() - 1);
      }
      sql += " and SEQ_ID in (" + privIdStr + ")";
    }
    if (!u.isAdminRole()  &&  !T9Utility.isNullorEmpty(privOp) ) {
      sql += " and SEQ_ID !=1 ";
    }
    sql += " order by PRIV_NO";
    
    Statement stmt = null;
    ResultSet rs = null;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        T9UserPriv up = new T9UserPriv();
        up.setSeqId(rs.getInt("SEQ_ID"));
        up.setPrivName(rs.getString("PRIV_NAME"));
        list.add(up);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return list;
  }
  
  public List getRoleList(Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    List list = new ArrayList();
    Map filters = new HashMap();
    list  = orm.loadListSingle(conn ,T9UserPriv.class , filters);
    return list;
  }
  public String getRoleJson(Connection conn) throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer();
    int count  = 0 ;
    sb.append("[");
    try {
      stmt = conn.createStatement();
      String sql = "SELECT SEQ_ID , PRIV_NAME FROM USER_PRIV  order by PRIV_NO asc";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sb.append("{").append("seqId:\"").append(rs.getInt("SEQ_ID")).append("\"").append(",roleName:\"").append(T9Utility.encodeSpecial(rs.getString("PRIV_NAME"))).append("\"").append("},");
        count++;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
//  public List getRoleList2(Connection conn) throws Exception{
//    Statement stmt = null;
//    ResultSet rs = null;
//    long count = 0;
//    try {
//      stmt = conn.createStatement();
//      String sql = "SELECT SEQ_ID , USER_Nmae FROM PERSON WHERE USER_PRIV = '" + seqId + "'";
//      rs = stmt.executeQuery(sql);
//      //System.out.println(sql);
//      while (rs.next()) {
//        count = rs.getLong(1);
//      }
//    } catch (Exception ex) {
//      throw ex;
//    } finally {
//      T9DBUtility.close(stmt, rs, log);
//    }
//    return list;
//  }
  public T9UserPriv getRoleById(int roleId , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    T9UserPriv userPriv = new T9UserPriv();
    Map query = new HashMap();
    query.put("SEQ_ID", roleId);
    userPriv = (T9UserPriv) orm.loadObjSingle(conn, T9UserPriv.class, query);
    return userPriv;
  }
  public List getUserExternalList(Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    List list = new ArrayList();
    int deptId = 0;
    String[] filters = new String[]{"DEPT_ID = " + deptId};
    list  = orm.loadListSingle(conn ,T9Person.class , filters);
    return list;
  }
  public String getNameByIdStr(String ids , Connection conn) throws Exception, Exception{
    String names = "";
    String[] aId = ids.split(",");
    for(String tmp : aId){
      if(!"".equals(tmp)){
        T9UserPriv userPriv = getRoleById(Integer.parseInt(tmp), conn);
        if (userPriv != null) {
          names += userPriv.getPrivName() + ",";
        }
      }
    }
    if (names.endsWith(",")) {
      names = names.substring(0, names.length() - 1);
    }
    return names;
  }
  public String getNameById(int id ,Connection conn) throws Exception{
    T9UserPriv userPriv   = this.getRoleById(id , conn);
    if(userPriv != null){
      return userPriv.getPrivName();
    }
    return null; 
  }
//  public void update(Connection conn, T9Organization org)throws Exception {
//    PreparedStatement pstmt = null;
//    try{   
//      String queryStr = "update ORGANIZATION set UNIT_NAME = ?, TELEPHONE = ?, MAX = ?, POSTCODE = ?," +
//          " ADDRESS = ?, WEBSITE = ?, EMAIL = ?, SIGN_IN_USER = ?, ACCOUNT = ? where SEQ_ID = ?";
//      pstmt = conn.prepareStatement(queryStr);
//      pstmt.setString(1, org.getUnitName());
//      pstmt.setLong(2, org.getTelephone());
//      pstmt.setString(3, org.getMax());
//      pstmt.setString(4, org.getPostcode());
//      pstmt.setString(5, org.getAddress());
//      pstmt.setString(6, org.getWebsite());
//      pstmt.setString(7, org.getEmail());
//      pstmt.setString(8, org.getSignInUser());
//      pstmt.setString(9, org.getAccount());
//      pstmt.setInt(10, org.getSeqId());
//      pstmt.executeUpdate();
//    }catch(Exception ex) {
//      throw ex;
//    }finally {
//      T9DBUtility.close(pstmt, null, log);
//    }
//  }
  public static void main(String[] args){
    //System.out.println("lssss".split(",")[0]);
  }
  
  public String getUserPrivList(Connection conn,Map request) throws Exception{
    String sql =  "select SEQ_ID" +
                  ",PRIV_NO" +
                  ",PRIV_NAME,SEQ_ID from USER_PRIV order by PRIV_NO asc";
    //System.out.println(sql);
    T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(conn,queryParam,sql);
    return pageDataList.toJson();
  }
  
  public String  showReader(Connection conn,String seqId) throws Exception {
    T9ORM orm = new T9ORM();
    ResultSet rs = null;
    Statement st = null;
    String providerName = "";
    int deptId = 0;
    String toId = "0";//用于对显示的部门树的范围的控制
    String deptName = "";
//    String deptPriv ="";
    String optionText = "";
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    int seqID = 0;
    if(!"".equals(seqId)&&seqId!=null) {
      seqID = Integer.parseInt(seqId);
    }
    int unReaderCount = 0;
    try {
      T9Notify notify = (T9Notify)orm.loadObjSingle(conn, T9Notify.class, seqID);
     
//     optionText = deptTreeList(conn,0,toId,news,displayAll);
      
     String temp = getDeptTreeJson(notify,0 , conn,"0",seqId);
     sb.append("listData:").append(temp);
     sb.append("}");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }finally {
      T9DBUtility.close(st, rs, log);
    }
    return sb.toString();
  }
  
  public String getDeptTreeJson(T9Notify notify,int deptId , Connection conn,String toId, String seqId) throws Exception{
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    this.getDeptTree(notify,deptId, sb, 0 , conn,toId,seqId);
    if(sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    
    return sb.toString();
  }
  
  public void getDeptTree(T9Notify notify,int deptId , StringBuffer sb , int level , Connection conn,String toId, String seqId) throws Exception{
    //首选分级，然后记录级数，是否为最后一个。。。


    List<T9Department> list = this.getDeptByParentId(deptId , conn,toId);
    
    for(int i = 0 ;i < list.size() ;i ++){
      String flag = "├";
//      if(i == list.size() - 1 ){
//        flag = "└";
//      }
      String tmp = "";
      for(int j = 0 ;j < level ; j++){
        tmp += "　";
      }
      flag = tmp + flag;
      
      T9Department dp = list.get(i);
      sb.append("{");
      sb.append("deptName:\"" +flag + dp.getDeptName() + "\"");
      sb = getUserById(notify, conn,dp.getSeqId(),sb,seqId);
    
      this.getDeptTree(notify,dp.getSeqId(), sb, level + 1 , conn,toId,seqId);
    }
   
  }
  
  public List<T9Department> getDeptByParentId(int deptId ,Connection conn,String toId) throws Exception {
    // TODO Auto-generated method stub
    T9ORM orm = new T9ORM();
    List<T9Department> list = new ArrayList();
    List<T9Department> listNew = new ArrayList();
    Map filters = new HashMap();
    filters.put("DEPT_PARENT", deptId);
    list  = orm.loadListSingle(conn ,T9Department.class , filters);
    if(!"0".equals(toId)&&!"ALL_DEPT".equals(toId)){//判断是不是全体部门

      for(int i=0;i<list.size();i++) {
        boolean temp = false;
        T9Department dept = list.get(i);
        if(toId != null && !"".equals(toId)){
          String[] toIds = toId.split(",");
          for(int j = 0 ;j < toIds.length ; j++){
            String toIdTemp =  toIds[j];
            if(toIdTemp.equals(Integer.toString(dept.getSeqId()))||childToId(conn,toId,Integer.toString(dept.getSeqId()))==true){
              temp = true;
              break;
            }
          }
        }
        if(temp == true) {
          listNew.add(dept);
        }
      }
    }else{
      listNew = list;
    }
    return listNew;
    
  }
  
 public boolean childToId(Connection conn,String toId,String deptId) throws Exception{
   T9ORM orm = new T9ORM();
   List<T9Department> list = new ArrayList();
   Map filters = new HashMap();
   filters.put("DEPT_PARENT", deptId);
   list  = orm.loadListSingle(conn ,T9Department.class , filters);
   boolean result = false;
   for(int i=0;i<list.size();i++) {
     boolean temp = false;
     T9Department dept = list.get(i);
     if(toId != null && !"".equals(toId)){
       String[] toIds = toId.split(",");
       for(int j = 0 ;j < toIds.length ; j++){
         String toIdTemp =  toIds[j];
         if(toIdTemp.equals(Integer.toString(dept.getSeqId()))){
           temp = true;
           break;
         }
       }
     }
     boolean temp2 = false;
     temp2 = childToId(conn,toId,Integer.toString(dept.getSeqId()));
     if(temp==true||temp2==true){
       result = true;
     }
   }
  
   
   return result;
 }
 
 public boolean findToId(String object,String object2){
   boolean temp = false;
   if(object != null && !"".equals(object)){
     String[] toIds = object.split(",");
     for(int j = 0 ;j < toIds.length ; j++){
       String toIdTemp =  toIds[j];
       if(toIdTemp.equals(object2)){
         temp = true;
         break;
       }
     }
   }
   return temp;
 }
 
//没有从toId中取
  public StringBuffer getUserById(T9Notify notify, Connection conn, int deptIdtemp, StringBuffer sb, String seqId)throws Exception{
    String userNameStr = "";
    String userNameOtherStr = "";
    String userNameNotLoginStr = "";
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    String queryUser = "select USER_NAME from PERSON where USER_PRIV = " + seqId + " and DEPT_ID='"+deptIdtemp+"' and NOT_LOGIN='0' order by USER_NO,USER_NAME";
    Statement userSt1 = conn.createStatement();
    ResultSet userRs1 = userSt1.executeQuery(queryUser);
    while(userRs1.next()){
      count1++;
      String userName = userRs1.getString("USER_NAME");
      userNameStr += userName + ",";
    }//end while
    sb.append(",userNameStr:\"" +userNameStr + "\"");
    sb.append(",count1:\"" +count1 + "\"");
    
    String queryUserOther = "select USER_NAME from PERSON where "+T9DBUtility.findInSet(seqId, "USER_PRIV_OTHER")+" and DEPT_ID='"+deptIdtemp+"' and NOT_LOGIN='0' order by USER_NO,USER_NAME";
    Statement userSt2 = conn.createStatement();
    ResultSet userRs2 = userSt2.executeQuery(queryUserOther);
    //System.out.println(queryUserOther);
    while(userRs2.next()){
      count2++;
      String userName = userRs2.getString("USER_NAME");
      userNameOtherStr += userName + ",";
    }//end while
    
    sb.append(",userNameOtherStr:\"" +userNameOtherStr + "\"");
    sb.append(",count2:\"" +count2 + "\"");
  
    String queryNotLogin = "select USER_NAME from PERSON where USER_PRIV = " + seqId + " and DEPT_ID='"+deptIdtemp+"' and NOT_LOGIN='1' order by USER_NO,USER_NAME";
    Statement userSt3 = conn.createStatement();
    ResultSet userRs3 = userSt3.executeQuery(queryNotLogin);
    while(userRs3.next()){
      count3++;
      String userName = userRs3.getString("USER_NAME");
      userNameOtherStr += userName + ",";
    }//end while
    sb.append(",userNameNotLoginStr:\"" +userNameNotLoginStr + "\"");
    sb.append(",count3:\"" +count3 + "\"");
    sb.append("},");
    return sb;
  }
  
  public long allUsers(Connection dbConn, String seqId)
      throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM PERSON WHERE USER_PRIV = '" + seqId + "'";
      rs = stmt.executeQuery(sql);
      //System.out.println(sql);
      while (rs.next()) {
        count = rs.getLong(1);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return count;
  }
  
  public long notLoginUser(Connection dbConn, String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM PERSON WHERE USER_PRIV = '" + seqId + "' and NOT_LOGIN='1'";
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

  public long otherUser(Connection dbConn, String seqId) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM PERSON WHERE "+T9DBUtility.findInSet(seqId, "USER_PRIV_OTHER");
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
  
  public long userPrivNo(Connection dbConn, String userPriv) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    long count = 0;
    try {
      stmt = dbConn.createStatement();
      String sql = "SELECT count(*) FROM PERSON WHERE USER_PRIV = '" + userPriv + "'";
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
  public Map<String , String> otherPrivs(Connection dbConn, String seqIdd) throws Exception {
    Map<String, String> result = new HashMap();
    if (T9Utility.isNullorEmpty(seqIdd)) {
      return result;
    }
    if (seqIdd.endsWith(",")) {
      seqIdd = seqIdd.substring(0, seqIdd.length() - 1);
    }
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "SELECT SEQ_ID , USER_PRIV_OTHER FROM PERSON WHERE SEQ_ID IN  (" + seqIdd + ")";
    try {
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        String str = T9Utility.null2Empty(rs.getString("USER_PRIV_OTHER"));
        String seqId = String.valueOf(rs.getInt("SEQ_ID"));
        result.put(seqId, str);
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return result;
  }
  
  public void addUserPriv(Connection conn , String seqIdstr , String addPrivs) throws Exception {
    String privs[] = addPrivs.split(",");
    Map<String , String> result = this.otherPrivs(conn, seqIdstr);
    
    for (String seqId : result.keySet()){
      if (T9Utility.isNullorEmpty(seqId)) continue;
      String newStr = "";
      String str = (String)result.get(seqId);
      String[] ss = str.split(",");
      HashSet<String> hs = new HashSet();
      for (String s : ss) {
        if (T9Utility.isNullorEmpty(s)) continue;
        hs.add(s);
      }
      for (String priv : privs) {
        if (T9Utility.isNullorEmpty(priv)) continue;
        if (!hs.contains(priv)) {
          hs.add(priv);
        }
      }
      for (String s : hs) {
        if (T9Utility.isNullorEmpty(s)) continue;
        newStr += s + ",";
      }
      if (newStr.endsWith(",")) {
        newStr = newStr.substring(0, newStr.length() - 1);
      }
      this.updateSetOtherPriv(conn, seqId, newStr);
    }
  }
  public void updateSetOtherPriv(Connection conn , String seqId , String userPriv) throws Exception {
    String sql = "update  PERSON set  USER_PRIV_OTHER = '" + userPriv + "' WHERE SEQ_ID = '" + seqId + "'";
    Statement stmt = null;
    try {
      stmt = conn.createStatement();
      stmt.executeUpdate(sql);
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, null, log);
    }
  }
  /**
   * 去掉含有的权限
   * @param conn
   * @param seqIdstr
   * @param outPrivs
   * @throws Exception
   */
  public void getOutPriv(Connection conn , String seqIdstr , String outPrivs) throws Exception {
    String privs[] = outPrivs.split(",");
    Map<String , String> result = this.otherPrivs(conn, seqIdstr);
    
    for (String seqId : result.keySet()){
      if (T9Utility.isNullorEmpty(seqId)) continue;
      String newStr = "";
      String str = (String)result.get(seqId);
      String[] ss = str.split(",");
      HashSet<String> hs = new HashSet();
      for (String s : ss) {
        if (T9Utility.isNullorEmpty(s)) continue;
        hs.add(s);
      }
      for (String priv : privs) {
        if (T9Utility.isNullorEmpty(priv)) continue;
        if (hs.contains(priv)) {
          hs.remove(priv);
        }
      }
      for (String s : hs) {
        if (T9Utility.isNullorEmpty(s)) continue;
        newStr += s + ",";
      }
      if (newStr.endsWith(",")) {
        newStr = newStr.substring(0, newStr.length() - 1);
      }
      this.updateSetOtherPriv(conn, seqId, newStr);
    }
  }
}
