package t9.core.funcs.doc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowProcess;
import t9.core.funcs.doc.data.T9DocRun;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9PrcsRoleUtility {
  public T9Department deptParent(T9Department dept , int flag ,Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    if(dept.getDeptParent() == 0){
      return dept;
    }else{
      T9Department parentDept = (T9Department) orm.loadObjSingle(conn, T9Department.class, dept.getDeptParent());
      if(parentDept == null){
        return dept;
      }else{
        if(flag == 1){
          return parentDept;
        }else{
          return this.deptParent(parentDept, flag , conn);
        }
      }
      
    }
  }
  public int getDeptParent(int deptId , int flag ,Connection conn) throws Exception{
    String sql = "select DEPT_PARENT from DEPARTMENT where SEQ_ID ='" + deptId + "'";
    Statement stm = null;
    ResultSet rs = null;
    int deptParent = 0;
    try{
      stm = conn.createStatement();
      rs = stm.executeQuery(sql);
      if (rs.next()) {
        deptParent = rs.getInt("DEPT_PARENT");
      }
    }catch(Exception e){
      throw e;
    }finally{
      T9DBUtility.close(stm, rs, null);
    }
    if(deptParent == 0){
      return deptId;
    }else{
      if(flag == 1){
        return deptParent;
      }else{
        return this.getDeptParent(deptParent, flag , conn);
      }
    }
  }
  /**
   * 
   * @param flowId 指定流程Id
   * @param checkType 0-检查办理权限；1-检查查询和管理权限
   * @param user 当前用户
   * @return true-有这个权限,false-没有这个权限
   * @throws Exception
   */
  public boolean prcsRole(int flowId  , int prcsId , int checkType , T9Person user , Connection conn) throws Exception{
    T9ORM orm = new T9ORM();
    T9DocFlowType flowType = (T9DocFlowType) orm.loadObjSingle(conn, T9DocFlowType.class, flowId);
    Map filters = new HashMap();
    filters.put("FLOW_SEQ_ID", flowId);
    if(prcsId == 0){
      List<T9DocFlowProcess> flowProcessList = new ArrayList();
      flowProcessList = orm.loadListSingle(conn, T9DocFlowProcess.class, filters);
      return this.prcsRole(flowType, flowProcessList, checkType, user , conn);
    }else{
      T9FlowProcessLogic flowPrcsLogic = new  T9FlowProcessLogic();
      //查出相关步骤
      T9DocFlowProcess flowProcess = flowPrcsLogic.getFlowProcessById(flowId, prcsId+ "" , conn);
      return this.prcsRole(flowType, flowProcess, checkType, user ,conn);
    }
    
  }
  /**
   * 判断当前用户是否有指定流程、指定步骤的权限
   * @param flowType 指定流程对象
   * @param flowProcess 指定步骤对象
   * @param checkType 
   * @param user
   * @return
   * @throws Exception 
   */
  public boolean prcsRole(T9DocFlowType flowType , T9DocFlowProcess flowProcess , int checkType  , T9Person user ,Connection conn) throws Exception{
    List<T9DocFlowProcess> flowProcessList = new ArrayList();
    flowProcessList.add(flowProcess);
    return this.prcsRole(flowType, flowProcessList, checkType, user , conn);
  }
  
  public boolean prcsRole(T9DocFlowType flowType , int checkType , T9Person user , Connection conn) throws Exception {
    boolean flag = false;
    List<T9DocFlowProcess> flowProcessList  =  null;
    if ("1".equals(flowType.getFlowType())) {
      String query = "select " 
        + " PRCS_ID"
        + " , PRCS_USER"
        + " , PRCS_DEPT"
        + " , PRCS_PRIV"
        + " from "+ T9WorkFlowConst.FLOW_PROCESS +" where FLOW_SEQ_ID=" + flowType.getSeqId() + " and PRCS_ID=1";
      
      flowProcessList  = new ArrayList();
      Statement stm = null;
      ResultSet rs = null;
      try{
        stm = conn.createStatement();
        rs = stm.executeQuery(query);
        if (rs.next()) {
          String prcsUser = rs.getString("PRCS_USER");
          String prcsDept = rs.getString("PRCS_DEPT");
          String prcsPriv = rs.getString("PRCS_PRIV");
          int prcsId = rs.getInt("PRCS_ID");
          
          T9DocFlowProcess fp = new T9DocFlowProcess();
          fp.setPrcsId(prcsId);
          fp.setPrcsUser(prcsUser);
          fp.setPrcsDept(prcsDept);
          fp.setPrcsPriv(prcsPriv);
          flowProcessList.add(fp);
        } else {
          return false;
        }
      }catch(Exception e){
        e.printStackTrace();
        throw e;
      }finally{
        T9DBUtility.close(stm, rs, null);
      }
      flag = this.prcsRole(flowType, flowProcessList, checkType, user, conn);
    } else {
      flag = this.prcsRole(flowType, flowProcessList, 0, user , conn);
    }
    return flag;
  }
/**
 * 判断当前用户是否有指定流程、指定步骤的权限
 * @param flowType 指定流程对象
 * @param flowProcessList 流程下的所有步骤
 * @param checkType 0-检查办理权限；1-检查查询和管理权限
 * @param user 当前用户
 * @return true-有这个权限,false-没有这个权限
 * @throws Exception 
 */
public boolean prcsRole(T9DocFlowType flowType , List<T9DocFlowProcess> flowProcessList , int checkType  , T9Person user , Connection conn) throws Exception{
  T9DocFlowProcess flowPrcs = null;
  if(flowProcessList != null 
      && flowProcessList.size() == 1){
    flowPrcs = flowProcessList.get(0);
  }
  if(user == null){
    return false;
  }
  //如果是管理员
  if(user != null 
      && user.isAdminRole()){
    return true;
  }
  if(checkType == 0){
    //如是是自由流程    if(flowType.getFlowType().equals("2")){
      //自由流程非第一步骤，所有人都有权限。即只要这个人能看到这个流程，他就可以执行相应的操作（办理、查询等）      if(flowPrcs != null && flowPrcs.getPrcsId() != 1){
        return true;
      }else{
        String newUser = flowType.getNewUser(conn);
        //是不是要加辅助部门ID串.辅助角色等???
        return this.checkPriv(user, newUser);
      }
    }else{
    //固定流程
      for(T9DocFlowProcess tmp : flowProcessList){
        String prcsUser = (tmp.getPrcsUser() == null ? "" : tmp.getPrcsUser());
        String prcsDept = T9OrgSelectLogic.changeDept(conn, tmp.getPrcsDept()); 
        prcsDept = prcsDept == null ? "" : prcsDept;
        String prcsPriv =  (tmp.getPrcsPriv() == null ? "" : tmp.getPrcsPriv());
        String userPrivOther = user.getUserPrivOther();
        String userDeptIdOther = user.getDeptIdOther();
        if(T9WorkFlowUtility.findId(prcsUser , String.valueOf(user.getSeqId()))){
          return true;
        }
        if(T9WorkFlowUtility.findId(prcsDept , String.valueOf(user.getDeptId()))){
          return true;
        }
        if(T9WorkFlowUtility.findId(prcsPriv,user.getUserPriv())){
          return true;
        }
        if(userPrivOther != null && !T9WorkFlowUtility.checkId(prcsPriv , userPrivOther , true).equals("")){
          return true;
        }
        if(userDeptIdOther != null && !T9WorkFlowUtility.checkId(prcsDept , userDeptIdOther , true).equals("")){
          return true;
        }
      }
    }
  //检查查询和管理权限,即$CHECK_TYPE == 1
  }else{
    String queryUser = flowType.getQueryUser();
    if(this.checkPriv(user, queryUser)){
      return true;
    }
    String queryUserDept = flowType.getQueryUserDept();
    if(this.checkPriv(user, queryUserDept)){
      return true;
    }
    String manageUser = flowType.getManageUser();
    if(this.checkPriv(user, manageUser)){
      return true;
    }
    String manageUserDept = flowType.getManageUserDept();
    if(this.checkPriv(user, manageUserDept)){
      return true;
    }
  }
  return false;
}
/**
 * 判断当前用户是否有指定流程、指定步骤的权限
 * @param flowType 指定流程对象
 * @param flowProcessList 流程下的所有步骤

 * @param checkType 0-检查办理权限；1-检查查询和管理权限
 * @param user 当前用户
 * @return true-有这个权限,false-没有这个权限
 */
public boolean prcsRoleByManager(T9DocFlowType flowType , List<T9DocFlowProcess> flowProcessList , int checkType  , T9Person user){
  T9DocFlowProcess flowPrcs = null;
  if(flowProcessList != null 
      && flowProcessList.size() == 1){
    flowPrcs = flowProcessList.get(0);
  }
  if(user == null){
    return false;
  }
  //如果是管理员
  if(user != null 
      && user.isAdminRole()){
    return true;
  }
  String manageUser = flowType.getManageUser();
  if(this.checkPriv(user, manageUser)){
    return true;
  }
  String manageUserDept = flowType.getManageUserDept();
  if(this.checkPriv(user, manageUserDept)){
    return true;
  }
  return false;
}
/**
 * 检查有没有这个查询权限,这里只针对固定流程
 * @param flowType
 * @param flowProcessList
 * @param checkType
 * @param user
 * @return
 * @throws Exception 
 */
public boolean prcsRoleByQuery(T9DocFlowType flowType  , T9Person user  , Connection conn) throws Exception{
  if(user == null){
    return false;
  }
  //如果是管理员
  if(user != null 
      && user.isAdminRole()){
    return true;
  }
  String query  = "SELECT PRCS_USER , PRCS_DEPT, PRCS_PRIV  from "+ T9WorkFlowConst.FLOW_PROCESS +" where FLOW_SEQ_ID=" + flowType.getSeqId();
  Statement stm = null;
  ResultSet rs = null;
  try {
    stm = conn.createStatement();
    rs = stm.executeQuery(query);
    while (rs.next()) {
      String prcsUser = rs.getString("PRCS_USER");
      String prcsDept = rs.getString("PRCS_DEPT");
      String prcsPriv = rs.getString("PRCS_PRIV");
      
      prcsDept = T9OrgSelectLogic.changeDept(conn, prcsDept); 
      String userPrivOther = user.getUserPrivOther();
      String userDeptIdOther = user.getDeptIdOther();
      if(T9WorkFlowUtility.findId(prcsUser , String.valueOf(user.getSeqId()))){
        return true;
      }
      if(T9WorkFlowUtility.findId(prcsDept , String.valueOf(user.getDeptId()))){
        return true;
      }
      if(T9WorkFlowUtility.findId(prcsPriv,user.getUserPriv())){
        return true;
      }
      if(userPrivOther != null && !T9WorkFlowUtility.checkId(prcsPriv , userPrivOther , true).equals("")){
        return true;
      }
      if(userDeptIdOther != null && !T9WorkFlowUtility.checkId(prcsDept , userDeptIdOther , true).equals("")){
        return true;
      }
    }
  } catch(Exception ex) {
    throw ex;
  } finally {
    T9DBUtility.close(stm, rs, null); 
  }
  String manageUser = flowType.getManageUser();
  if(this.checkPriv(user, manageUser)){
    return true;
  }
  String manageUserDept = flowType.getManageUserDept();
  if(this.checkPriv(user, manageUserDept)){
    return true;
  }
  String queryUser = flowType.getQueryUser();
  if(this.checkPriv(user, queryUser)){
    return true;
  }
  String queryUserDept = flowType.getQueryUserDept();
  if(this.checkPriv(user, queryUserDept)){
    return true;
  }
  return false;
}
  /**
   * 判断有没有这个权限
   * @param user
   * @param privStr
   * @return
   */
  public boolean checkPriv(T9Person user, String privStr){
    if(privStr == null || user == null){
      return false;
    }
    String[] aPriv = privStr.split("\\|");
    String privUser = "";
    if (aPriv.length > 0 ) {
      privUser = aPriv[0];
    }
    String privDept = "";
    if (aPriv.length > 1 ) {
      privDept = aPriv[1];
    }
    String privRole = "";
    if (aPriv.length > 2 ) {
      privRole = aPriv[2];
    }
    if( "0".equals(privDept)
        || "ALL_DEPT".equals(privDept)
        || T9WorkFlowUtility.findId(privUser,String.valueOf(user.getSeqId())) 
        || T9WorkFlowUtility.findId(privDept,String.valueOf(user.getDeptId())) 
        || T9WorkFlowUtility.findId(privRole,user.getUserPriv())
        || !T9WorkFlowUtility.checkId(privRole , user.getUserPrivOther() ,true).equals("")
        || !T9WorkFlowUtility.checkId(privDept , user.getDeptIdOther() ,true).equals("")){
      return true;
    }
    return false;
  }
  public String runRole(int flowRunId , int flowTypeId , int  prcsId , T9Person user , Connection conn) throws Exception{
    T9DocRun flowRun = this.getFlowRun(flowRunId, conn);
    int flowId = 0 ;
    if (flowRun != null) {
      flowId = flowRun.getFlowId();
    }
    T9DocFlowType flowType = this.getFlowType(flowId, conn);
    return runRole(flowRun , flowType ,  prcsId ,user , conn);
  }
  public String runRole(int flowRunId , int  prcsId , T9Person user , Connection conn) throws Exception{
    T9DocRun flowRun = this.getFlowRun(flowRunId, conn);
    int flowId = 0 ;
    if (flowRun != null) {
      flowId = flowRun.getFlowId();
    }
    T9DocFlowType flowType = this.getFlowType(flowId, conn);
    return runRole(flowRun , flowType ,  prcsId ,user , conn);
  }
  public T9DocFlowType getFlowType(int flowId , Connection conn) throws Exception {
    T9DocFlowType flowType = null;
    String query = "select QUERY_USER, QUERY_USER_DEPT , MANAGE_USER , MANAGE_USER_DEPT  from "+ T9WorkFlowConst.FLOW_TYPE +" where SEQ_ID=" + flowId;
    Statement stm5 = null;
    ResultSet rs5 = null;
    try {
      stm5 = conn.createStatement();
      rs5 = stm5.executeQuery(query);
      if(rs5.next()){
        flowType = new T9DocFlowType();
        flowType.setSeqId(flowId);
        flowType.setQueryUser(rs5.getString("QUERY_USER"));
        flowType.setQueryUserDept(rs5.getString("QUERY_USER_DEPT"));
        flowType.setManageUser(rs5.getString("MANAGE_USER"));
        flowType.setManageUserDept(rs5.getString("MANAGE_USER_DEPT"));
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, rs5, null); 
    }
    return flowType;
  }
  public T9DocRun getFlowRun(int runId , Connection conn) throws Exception {
    T9DocRun flowRun = null;
    String query = "select BEGIN_USER,FLOW_ID from "+ T9WorkFlowConst.FLOW_RUN +" WHERE RUN_ID = " + runId;
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = conn.createStatement();
      rs4 = stm4.executeQuery(query);
      if(rs4.next()){
        flowRun = new T9DocRun();
        flowRun.setBeginUser(rs4.getInt("BEGIN_USER"));
        flowRun.setRunId(runId);
        flowRun.setFlowId(rs4.getInt("FLOW_ID"));
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm4, rs4, null); 
    }
    return flowRun;
  }
  
  public String runRole(int flowRunId , T9Person user , Connection conn) throws Exception{
    T9DocRun flowRun = this.getFlowRun(flowRunId, conn);
    int flowId = 0 ;
    if (flowRun != null) {
      flowId = flowRun.getFlowId();
    }
    T9DocFlowType flowType = this.getFlowType(flowId, conn);
    return runRole(flowRun , flowType ,  0 ,user , conn);
  }
  /**
   * 
   * @param flowRun
   * @param flowType
   * @param prcsId
   * @param user
   * @return
   * @throws Exception
   */
  public String runRole(T9DocRun flowRun , T9DocFlowType flowType , int  prcsId , T9Person user , Connection conn) throws Exception{
    long date1 = System.currentTimeMillis();
    String runRole = "";
    if(flowRun == null){
      return runRole ;
    }
    //--- 系统管理员检查 ---
    if(user.isAdmin()){
      runRole += "1,";
    }
    //检查当前登录用户是否“主办人” ,功能达到,但此地方还有待调整得更好
    String query = "select 1 from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" WHERE user_id =" + user.getSeqId() + " and op_FLAG = 1 AND RUN_ID =" + flowRun.getRunId() ;
    if(prcsId != 0 ){
      query +=  " and prcs_ID =" + prcsId;
    }
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = conn.createStatement();
      rs4 = stm4.executeQuery(query);
      if(rs4.next()){
        runRole += "2,";
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm4, rs4, null); 
    }
    query = "select 1 from "+ T9WorkFlowConst.FLOW_RUN_PRCS +" WHERE user_id =" + user.getSeqId() + " AND RUN_ID =" + flowRun.getRunId() ;
    if(prcsId != 0 ){
      query +=  " and prcs_ID =" + prcsId;
    }
    Statement stm5 = null;
    ResultSet rs5 = null;
    try {
      stm5 = conn.createStatement();
      rs5 = stm5.executeQuery(query);
      if(rs5.next()){
        runRole += "4,6,";
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm5, rs5, null); 
    }
    //获取流程发起人所在的部门ID,要加辅助部门ID串?
    int beginUser = flowRun.getBeginUser();
    T9Person userTmp = null;
    query = "select SEQ_ID , DEPT_ID , USER_PRIV , DEPT_ID_OTHER , USER_PRIV_OTHER from PERSON where seq_id=" + beginUser;
    Statement stm = null;
    ResultSet rs = null;
    try {
      stm = conn.createStatement();
      rs = stm.executeQuery(query);
      if(rs.next()){
        userTmp = new T9Person();
        userTmp.setSeqId(rs.getInt("SEQ_ID"));
        userTmp.setDeptId(rs.getInt("DEPT_ID"));
        userTmp.setUserPriv(rs.getString("USER_PRIV"));
        userTmp.setDeptIdOther(rs.getString("DEPT_ID_OTHER"));
        userTmp.setUserPrivOther(rs.getString("USER_PRIV_OTHER"));
      }
    } catch(Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm, rs, null); 
    }
    
    String beginDept = "";
    String myDeptStr = "";
    if(userTmp != null){
      beginDept = String.valueOf(userTmp.getDeptId());
      myDeptStr = this.getMyDept(user.getDeptId(), 0 , conn);
    }
    
    String queryUser = flowType.getQueryUser();
    if(this.checkPriv(user, queryUser)){
    //管理与监控人检查      runRole += "5,";
    }
    String queryUserDept = flowType.getQueryUserDept();
    if(this.checkPriv(user, queryUserDept)){
      if(T9WorkFlowUtility.findId(myDeptStr , beginDept)){
        runRole += "5,";
      }
    }
    String manageUser = flowType.getManageUser();
    if(this.checkPriv(user, manageUser)){
      runRole += "3,";
    }
    String manageUserDept = flowType.getManageUserDept();
    if(this.checkPriv(user, manageUserDept)){
      if(T9WorkFlowUtility.findId(myDeptStr , beginDept)){
        runRole += "3,";
      }
    }
    return runRole;
  }
  public String getMyDept(int deptId ,int lower , Connection conn) throws Exception{
    StringBuffer sb = new StringBuffer();
    if(lower == 0){
      sb.append(deptId + ",");
    }
    this.getDeptByParentId(deptId, sb , conn);
    return sb.toString();
  }
  public void getDeptByParentId(int deptId , StringBuffer ids , Connection conn) throws Exception {
    // TODO Auto-generated method stub
    Statement stmt = null;
    ResultSet rs = null;
    try {
      String queryStr = "select SEQ_ID from DEPARTMENT where DEPT_PARENT=" + deptId; 
      stmt = conn.createStatement();
      rs = stmt.executeQuery(queryStr);
      while (rs.next()) {
        int id = rs.getInt("SEQ_ID");
        ids.append(id + ",");
        this.getDeptByParentId(id, ids , conn);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stmt , rs , null);
    }
    
  } 
  public String flowOtherSql(String prcsPriv ) throws Exception{
    if(prcsPriv == null || "".equals(prcsPriv)){
      return "";
    }
    String query = "";
    String[] aPriv = prcsPriv.split(",");
    for(String temp : aPriv){
      if(!"".equals(temp)){
        query += " or USER_PRIV_OTHER like '"+ T9Utility.encodeLike(temp) +",%' "  + T9DBUtility.escapeLike() + " or USER_PRIV_OTHER like '%,"+ T9Utility.encodeLike(temp) +",%' " + T9DBUtility.escapeLike() ;
      }
    }
    return query;
  }
  public String flowDeptOtherSql(String prcsDept) throws Exception{
    if(prcsDept == null || "".equals(prcsDept)){
      return "";
    }
    String query = "";
    String[] aPriv = prcsDept.split(",");
    for(String temp : aPriv){
      if(!"".equals(temp)){
        query += " or DEPT_ID_OTHER like '"+ T9Utility.encodeLike(temp) +",%' "+T9DBUtility.escapeLike() +" or DEPT_ID_OTHER like '%,"+ T9Utility.encodeLike(temp) +",%' " + T9DBUtility.escapeLike() ;
      }
    }
    return query;
  }
}             
