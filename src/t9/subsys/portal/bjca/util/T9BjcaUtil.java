package t9.subsys.portal.bjca.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.tools.ant.types.selectors.DepthSelector;

import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.auth.T9PassEncrypt;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.portal.bjca.logic.services.Department.DepartmentServiceLocator;
import t9.subsys.portal.bjca.logic.services.Department.DepartmentSoapBindingStub;
import t9.subsys.portal.bjca.logic.services.User.UserServiceLocator;
import t9.subsys.portal.bjca.logic.services.User.UserSoapBindingStub;

import com.bjca.uums.client.bean.DepartmentInformation;
import com.bjca.uums.client.bean.LoginInformation;
import com.bjca.uums.client.bean.PersonInformation;
import com.bjca.uums.client.bean.UserInformation;

public class T9BjcaUtil {

  /**
   * 取得用户的信息
   * @param userId
   * @return
   * @throws Exception
   */
  public static UserInformation getUserInformationById(String userId)
      throws Exception {
    UserInformation userInfo = null;
    UserSoapBindingStub binding;
    binding = (UserSoapBindingStub) new UserServiceLocator()
        .getUser();
    binding.setTimeout(60000);
    userInfo = binding.findUserInfosByUserID(userId);
    return userInfo;
  }
  /**
   * 取得用户的信息
   * @param userId
   * @return
   * @throws Exception
   */
  public static PersonInformation getPersonInformationById(String userId)
      throws Exception {
    PersonInformation personInfo = null;
    UserSoapBindingStub binding;
    binding = (UserSoapBindingStub) new UserServiceLocator()
        .getUser();
    binding.setTimeout(60000);
    personInfo = binding.findPersonInfosByUserID(userId);
    return personInfo;
  }
  /**
   * 取得登录用户的信息
   * @param userId
   * @return
   * @throws Exception
   */
  public static LoginInformation getLoginUserInfoByUserId(String userId)
      throws Exception {
    LoginInformation userInfo = null;
    UserSoapBindingStub binding;
    binding = (UserSoapBindingStub) new UserServiceLocator()
        .getUser();
    binding.setTimeout(60000);
    userInfo = binding.getLoginInformationByUserID(userId);
    return userInfo;
  }
  
  /**
   * 取得部门的信息
   * @param userId
   * @return
   * @throws Exception
   */
  public static DepartmentInformation getDeptBydeptId(String deptId)
      throws Exception {
    DepartmentInformation deptInfo = null;
    DepartmentSoapBindingStub binding;
    binding = (DepartmentSoapBindingStub) new DepartmentServiceLocator().getDepartment();
     
    binding.setTimeout(60000);
    deptInfo = binding.findDepartByDepartCode(deptId);
    return deptInfo;
  }
  
  /**
   * 判断当前部门是否有父部门
   * @param userId
   * @return
   * @throws Exception
   */
  public static boolean hasParentDept(String deptCode,String upDeptCode)
      throws Exception {
    boolean  hasParent = true; 
   /* DepartmentInformation deptInfo = null;
    DepartmentSoapBindingStub binding;
    binding = (DepartmentSoapBindingStub) new DepartmentServiceLocator().getDepartment();
     
    binding.setTimeout(60000);
    deptInfo = binding.findDepartByDepartCode(deptCode);*/
    if(upDeptCode == null || "0".equals(upDeptCode) || deptCode.equals(upDeptCode)){
      hasParent =  false;
    }
    return hasParent;
  }
  /**
   * 组织T9用户的基本信息（不包括部门信息）
   * @param personInfo
   * @param loginInfo
   * @return
   */
  public static T9Person toT9PersonBySyn(PersonInformation personInfo,LoginInformation loginInfo){
    T9Person person = new T9Person();
    person.setUniqueId(personInfo.getUserIdcode());
    person.setUserName(personInfo.getUserName());
    person.setAddHome(personInfo.getUserAddress());
    person.setPostNoHome(personInfo.getUserPostcode());
    person.setTelNoHome(personInfo.getUserPhone());
    person.setMobilNo(personInfo.getUserMobile());
    person.setEmail(personInfo.getUserEmail());
    person.setSex(personInfo.getDefault1());
    person.setUserId(loginInfo.getLoginName());
    person.setByname(loginInfo.getLoginNickName());
    person.setNotLogin("0");
    person.setPassword(T9PassEncrypt.encryptPass(""));
    return person;
  }
  
  public static int getDefualtUserPriv(Connection conn) throws Exception{
    String sql = "select SEQ_ID from user_priv";
    ResultSet rs = null;
    Statement st = null;
    int userPrivTem = 1;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      while (rs.next()) {
        if(userPrivTem < rs.getInt(1)){
          userPrivTem = rs.getInt(1);
        }
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return userPrivTem;
  }
  /**
   * 根据通不过来的deptCode取得T9的部门Id
   * 如果返回值为-1则表示此部门还没有同步
   * @param conn
   * @param deptCode 同步过来的部门deptCode
   * @return
   * @throws Exception
   */
  public static int getT9DeptIdBySynDeptCode(Connection conn,String deptCode,String deptName) throws Exception{
    int deptId = -1;
    String sql = "select SEQ_ID FROM DEPARTMENT WHERE DEPT_CODE='" + deptCode + "' or DEPT_NAME='" + deptName + "'";
    Statement st = null;
    ResultSet rs = null;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        deptId = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return deptId;
  }
  
  /**
   * 根据通不过来的deptCode取得T9的部门Id
   * 如果返回值为-1则表示此部门还没有同步
   * @param conn
   * @param deptCode 同步过来的部门deptCode
   * @return
   * @throws Exception
   */
  public static int getT9DeptIdBySynDeptCode(Connection conn,String deptCode) throws Exception{
    int deptId = -1;
    String sql = "select SEQ_ID FROM DEPARTMENT WHERE DEPT_CODE='" + deptCode + "'";
    Statement st = null;
    ResultSet rs = null;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        deptId = rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return deptId;
  }
  /**
   * 添加单个用户
   * @param conn
   * @param person
   * @throws Exception 
   */
  public static void addPersonBySyn(Connection conn , T9Person person) throws Exception{
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, person);
  }
  /**
   * 添加单个用户
   * @param conn
   * @param person
   * @throws Exception 
   */
  public static void updatePersonBySyn(Connection conn , T9Person person) throws Exception{
    T9ORM orm = new T9ORM();
    orm.updateSingle(conn, person);
  }
  /**
   * 添加单个用户
   * @param conn
   * @param person
   * @throws Exception 
   */
  public static void updateDeptBySyn(Connection conn , T9Department depart) throws Exception{
    T9ORM orm = new T9ORM();
    orm.updateSingle(conn, depart);
  }
  
  /**
   * 添加单个用户
   * @param conn
   * @param person
   * @throws Exception 
   */
  public static void saveDeptBySyn(Connection conn , T9Department depart) throws Exception{
    T9ORM orm = new T9ORM();
    orm.saveSingle(conn, depart);
  }
  /**
   * 
   * @param conn
   * @param personInfo
   * @param loginInfo
   * @return
   * @throws Exception 
   */
  public static void addUser(Connection conn,PersonInformation personInfo,LoginInformation loginInfo) throws Exception{
    T9Person person = toT9PersonBySyn(personInfo, loginInfo);
    Collection<DepartmentInformation> departs = personInfo.getDeparts();
    int deptId = 0;
    String  otherDeptIdStr = "";
    int i = 0;
    for (DepartmentInformation depart : departs) {
      addDept(conn, depart);
      int deptIdTem = getT9DeptIdBySynDeptCode(conn, depart.getDepartCode(),depart.getDepartName());
      if(i == 0){
        deptId = deptIdTem;
      }else{
        otherDeptIdStr += deptIdTem + ",";
      }
      i ++;
    }
    person.setUserPriv(String.valueOf(getDefualtUserPriv(conn)));
    person.setDeptId(deptId);
    person.setDeptIdOther(otherDeptIdStr);
    addPersonBySyn(conn, person);
  }
  /**
   * 更新用户信息
   * @param conn
   * @param personInfo
   * @param loginInfo
   * @throws Exception
   */
  public static void modifyUser(Connection conn,PersonInformation personInfo,LoginInformation loginInfo) throws Exception{
    T9Person person = toT9PersonBySyn(personInfo, loginInfo);
    Collection<DepartmentInformation> departs = personInfo.getDeparts();
    int deptId = 0;
    String  otherDeptIdStr = "";
    int i = 0;
    for (DepartmentInformation depart : departs) {
      addDept(conn, depart);
      int deptIdTem = getT9DeptIdBySynDeptCode(conn, depart.getDepartCode());
      if(i == 0){
        deptId = deptIdTem;
      }else{
        otherDeptIdStr += deptIdTem + ",";
      }
      i ++;
    }
    System.out.println(deptId);
    person.setDeptId(deptId);
    person.setDeptIdOther(otherDeptIdStr);
    int seqId = 0;
    int seqIdTem = 0;
    if((seqIdTem = isExisPersonByAdmin(conn, person.getUserId())) == -1){
      if(isExisPersonUserId(conn, person.getUserId(), personInfo.getUserIdcode()) != -1){
        throw new Exception();
      }
      seqId =  isExisPerson(conn, personInfo.getUserIdcode());
    }else{
      seqId =  seqIdTem;
    }
    isExisPerson(conn, personInfo.getUserIdcode());
    person.setSeqId(seqId);
    updatePersonBySyn(conn, person);
  }
  /**
   * 删除用户，将用户置为不可登录
   * @param conn
   * @param userId
   * @throws Exception 
   */
  public static void deleteUser(Connection conn,String userId) throws Exception{
    String sql = "update  PERSON set NOT_LOGIN='1' where UNIQUE_ID='" + userId + "'";
    Statement st = null;
    try {
      st = conn.createStatement();
      st.execute(sql);
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, null, null);
    }
  }
  /**
   * 添加部门，迭代的添加部门
   * @param conn
   * @param deptCode
   * @return
   * @throws Exception 
   */
  public static void addDept(Connection conn, String deptCode) throws Exception{
    DepartmentInformation departInfo = getDeptBydeptId(deptCode);
    addDept(conn, departInfo);
  }
  /**
   * 添加部门，迭代的添加部门
   * @param conn
   * @param departInfo
   * @return
   * @throws Exception 
   */
  public static void addDept(Connection conn, DepartmentInformation departInfo) throws Exception{
    int deptId = 0;
    String deptCode = departInfo.getDepartCode();
    deptId = getT9DeptIdBySynDeptCode(conn, deptCode,departInfo.getDepartName());
    if(deptId != -1){ //此部门已存在
      if(getT9DeptIdBySynDeptCode(conn, deptCode) == -1){
        modifyDept(conn, departInfo);
      }
      return;
    }

    T9Department depart = toT9DepartmentBySyn(departInfo);
    depart.setDeptNo("001");

    int deptParentId = 0; 
    if(hasParentDept(deptCode, departInfo.getDepartUpcode())){
      //存在父部门
      addDept(conn,  departInfo.getDepartUpcode());
      System.out.println(departInfo.getDepartUpcode());
      deptParentId = getT9DeptIdBySynDeptCode(conn, departInfo.getDepartUpcode());
    }
    System.out.println("ss:" + depart.getDeptCode());
    depart.setDeptParent(deptParentId);
    saveDeptBySyn(conn, depart);
  }
  /**
   * 修改部门，此功能会迭代添加父级部门
   * @param conn
   * @param deptCode
   * @throws Exception
   */
  public static void modifyDept(Connection conn, String deptCode) throws Exception{
    DepartmentInformation departInfo = getDeptBydeptId(deptCode);
    modifyDept(conn, departInfo);
  }
  /**
   * 修改部门，此功能会迭代添加父级部门
   * @param conn
   * @param departInfo
   * @return
   * @throws Exception 
   */
  public static void modifyDept(Connection conn, DepartmentInformation departInfo) throws Exception{
    int deptId = 0;
    String deptCode = departInfo.getDepartCode();
    deptId = getT9DeptIdBySynDeptCode(conn, deptCode);
    if(deptId == -1){ //此部门已存在
      //if(getT9DeptIdBySynDeptCode(conn, deptCode) == -1){
        return ;
     // }
    }
    T9Department depart = toT9DepartmentBySyn(departInfo);
    depart.setSeqId(deptId);
    int deptParentId = 0; 
    if(hasParentDept(deptCode, departInfo.getDepartUpcode())){
      //存在父部门
      addDept(conn,  departInfo.getDepartUpcode());
      deptParentId = getT9DeptIdBySynDeptCode(conn, departInfo.getDepartUpcode(),departInfo.getDepartName());
    }
    depart.setDeptParent(deptParentId);
    updateDeptBySyn(conn, depart);
  }
  
  /**
   * 删除部门
   * @param conn
   * @param deptCode
   * @throws Exception 
   */
  public static void deleteDept(Connection conn,String deptCode) throws Exception{
    //删除用户
    int deptId = getT9DeptIdBySynDeptCode(conn, deptCode);
    deleteDeptByT9(conn, deptId);
  }
  /**
   * 级联删除t9的部门，部门的用户也会被删除
   * @param conn
   * @param deptId
   * @throws Exception
   */
  public static void deleteDeptByT9(Connection conn,int deptId) throws Exception{
    String sql = "select SEQ_ID FROM DEPARTMENT WHERE DEPT_PARENT=" + deptId;
    ResultSet rs = null;
    Statement st = null;
    ArrayList<Integer> ids = new ArrayList<Integer>();
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      while (rs.next()) {
        ids.add(rs.getInt(1));
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    for (Integer id : ids) {
      deleteDeptByT9(conn,id);
    }
    String delsql = "delete from DEPARTMENT where SEQ_ID = " + deptId;
    String delPerson = "delete from PERSON WHERE DEPT_ID=" + deptId;
    try {
      st = conn.createStatement();
      st.execute(delsql);
      st.execute(delPerson);
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
  }
  /**
   * 组织T9部门信息
   * @param personInfo
   * @param loginInfo
   * @return
   */
  public static T9Department toT9DepartmentBySyn(DepartmentInformation departInfo){
    T9Department depart = new T9Department();
    depart.setDeptName(departInfo.getDepartName());
    depart.setTelNo(departInfo.getDepartPhone());
    depart.setDeptCode(departInfo.getDepartCode());
    return depart;
  }
  /**
   * 用户是否存在
   * @param conn
   * @param personUnid
   * @return
   * @throws Exception
   */
  public static int isExisPerson(Connection conn,String personUnid) throws Exception{
    String sql = "select SEQ_ID FROM PERSON WHERE UNIQUE_ID='" + personUnid + "'";
    Statement st = null;
    ResultSet rs = null;
    int result = -1;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        result =  rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return result;
  }
  
  /**
   * 用户是否存在
   * @param conn
   * @param personUnid
   * @return
   * @throws Exception
   */
  public static int isExisPersonByAdmin(Connection conn,String userId) throws Exception{
    String sql = "select SEQ_ID FROM PERSON WHERE USER_ID='admin'";
    Statement st = null;
    ResultSet rs = null;
    int result = -1;
    if(!"admin".equalsIgnoreCase(userId)){
      return result;
    }
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        result =  rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return result;
  }
  
  /**
   * 用户是否存在
   * @param conn
   * @param personUnid
   * @return
   * @throws Exception
   */
  public static int isExisPersonUserId(Connection conn,String userId,String personUnid) throws Exception{
    String sql = "select SEQ_ID FROM PERSON WHERE USER_ID='" + userId + "' and UNIQUE_ID!='" + personUnid + "'";
    Statement st = null;
    ResultSet rs = null;
    int result = -1;
    try {
      st = conn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()){
        result =  rs.getInt(1);
      }
    } catch (Exception e) {
      throw e;
    } finally{
      T9DBUtility.close(st, rs, null);
    }
    return result;
  }
}
