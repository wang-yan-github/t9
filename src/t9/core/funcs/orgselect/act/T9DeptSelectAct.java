package t9.core.funcs.orgselect.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.orgselect.logic.T9DeptSelectLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9DeptSelectAct {
  /**
   * 取得部门树状列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
public String getDeptTree(HttpServletRequest request, HttpServletResponse response)
  throws Exception {
  String idStr = request.getParameter("id");
  int id = 0;
  if (!T9Utility.isNullorEmpty(idStr)) {
    id = Integer.parseInt(idStr);
  }
  String moduleId = request.getParameter("moduleId");
  
  String privNoFlagStr = request.getParameter("privNoFlag");
  int privNoFlag = 0;
  if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
    privNoFlag = Integer.parseInt(privNoFlagStr);
  }
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
    T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
    long date1 = System.currentTimeMillis();
    T9ModulePriv priv = T9PrivUtil.getMyPrivByModel(dbConn,  person.getSeqId(), moduleId);
    String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = " + id  + " order by DEPT_NO ASC , DEPT_NAME asc";
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
    T9DeptSelectLogic dsl = new T9DeptSelectLogic();
    boolean hasModule = false;
    if (moduleId != null && !"".equals(moduleId)) {
      hasModule = true;
    }
    String allDef = "";
    T9MyPriv mp = new T9MyPriv();
    if (hasModule || privNoFlag != 0) {
      mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag , priv);
      allDef = dsl.getDefUserDept(dbConn, mp, person.getDeptId());
    }
    if (allDef== null) {
      allDef = "";
    }
    String deptss = "";
    if (priv != null && "2".equals(priv.getDeptPriv()) 
        && !T9Utility.isNullorEmpty(priv.getDeptId())) {
      String deptStr = priv.getDeptId();
      deptss = deptStr;
    }
    if ("".equals(deptss) || deptss.endsWith(",")) {
      deptss += person.getDeptId();
    }else{
      deptss += "," + person.getDeptId();
    } 
    StringBuffer sb = new StringBuffer("[");
    String contextPath = request.getContextPath();
    for (T9Department d : depts) {
      int nodeId = d.getSeqId();
      if (("11".equals(moduleId) || "12".equals(moduleId)) 
          && priv != null 
          && ("2".equals(priv.getDeptPriv())
          || "0".equals(priv.getDeptPriv())) && !T9OrgSelectLogic.isShow(dbConn, nodeId, deptss) ) {
        continue;
      }
      String name = d.getDeptName();
      int isHaveChild = isHaveSubDept2(dbConn, d.getSeqId() ,priv , moduleId ,deptss);
      boolean extData = false;
      if (hasModule || privNoFlag != 0) {
        //如果是全体部门,或者是部门在里面的话
        if("1".equals(mp.getDeptPriv()) 
            || this.findId(allDef, String.valueOf(nodeId))){
          extData = true;
        }
      } else {
        extData = true;
      }
      String imgAddress = contextPath +  "/core/styles/style1/img/dtree/node_dept.gif";
      sb.append("{");
      sb.append("nodeId:\"" + nodeId + "\"");
      sb.append(",name:\"" + T9Utility.encodeSpecial(name) + "\"");
      sb.append(",isHaveChild:" + isHaveChild + "");
      sb.append(",extData:"+ extData);
      sb.append(",imgAddress:\"" + imgAddress + "\"");
      sb.append("},");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("]");
    long date2 = System.currentTimeMillis();
    long date3 = date2 - date1;
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
    request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
  } catch (Exception ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
    throw ex;
  }
  return "/core/inc/rtjson.jsp";
}
  /**
   * 取得部门树状列表数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDeptTree2(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    int id = 0;
    String moduleId = request.getParameter("moduleId");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    StringBuffer sb = new StringBuffer("[");
    int count = 0;
    String contextPath = request.getContextPath();
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      if ("0".equals(idStr) || "".equals(idStr)) {
        String query = "select UNIT_NAME from organization ";
        Statement stm4 = null;
        ResultSet rs4 = null;
        String unitName = "";
        try {
          stm4 = dbConn.createStatement();
          rs4 = stm4.executeQuery(query);
          if (rs4.next()) {
            unitName = rs4.getString("UNIT_NAME");
          }
        }catch(Exception ex) {
          throw ex;
        }finally {
          T9DBUtility.close(stm4 , rs4 , null);
        }
        String imgAddress = contextPath +  "/core/styles/style1/img/dtree/system.gif";
        sb.append("{");
        sb.append("nodeId:\"organizationNodeId\"");
        sb.append(",name:\"" + T9Utility.encodeSpecial(unitName) + "\"");
        sb.append(",isHaveChild:1");
        sb.append(",extData:true");
        sb.append(",imgAddress:\"" + imgAddress + "\"");
        sb.append("}");
      } else {
        if ("organizationNodeId".equals(idStr)) {
          idStr = "0";
        }
        if (!T9Utility.isNullorEmpty(idStr)) {
          id = Integer.parseInt(idStr);
        }
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = " + id  + " order by DEPT_NO ASC, DEPT_NAME asc";
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
        T9DeptSelectLogic dsl = new T9DeptSelectLogic();
        boolean hasModule = false;
        if (moduleId != null && !"".equals(moduleId)) {
          hasModule = true;
        }
        String allDef = "";
        T9MyPriv mp = new T9MyPriv();
        if (hasModule || privNoFlag != 0) {
          mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
          allDef = dsl.getDefUserDept(dbConn, mp, person.getDeptId());
        }
        for (T9Department d : depts) {
          int nodeId = d.getSeqId();
          String name = d.getDeptName();
          int isHaveChild = isHaveSubDept(dbConn, d.getSeqId());
          boolean extData = false;
          //||  T9PrivUtil.isDeptPriv(dbConn, nodeId, mp , person.getPostPriv(), person.getPostDept(), person.getSeqId(), person.getDeptId()
          if (hasModule || privNoFlag != 0) {
            //如果是全体部门,或者是部门在里面的话
            if("1".equals(mp.getDeptPriv()) 
                || this.findId(allDef, String.valueOf(nodeId))){
              extData = true;
            }
          } else {
            extData = true;
          }
          String imgAddress = contextPath +  "/core/styles/style1/img/dtree/node_dept.gif";
          sb.append("{");
          sb.append("nodeId:\"" + nodeId + "\"");
          sb.append(",name:\"" + T9Utility.encodeSpecial(name) + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",extData:"+ extData);
          sb.append(",imgAddress:\"" + imgAddress + "\"");
          sb.append("},");
          count++;
        }
      }
      if (count > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
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
  /**
   * 取得部门的缩排Json数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDeptIndent(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DeptSelectLogic logic = new T9DeptSelectLogic();
      T9DeptLogic deptLogic = new T9DeptLogic();
      List deptList = this.getDeptList(dbConn);
      StringBuffer sb = new StringBuffer();
      if (deptId == null || "".equals(deptId)) {
        sb = logic.getDeptJson(deptList);
      } else {
        sb = logic.getDeptJson(deptList, Integer.parseInt(deptId));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public List getDeptList(Connection dbConn) throws Exception {
    List deptList = new ArrayList();
    String query = "select SEQ_ID , DEPT_NAME ,  DEPT_PARENT from DEPARTMENT order by DEPT_NO ASC, DEPT_NAME asc";
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = dbConn.createStatement();
      rs4 = stm4.executeQuery(query);
      while (rs4.next()) {
        T9Department dept  = new T9Department();
        dept.setSeqId(rs4.getInt("SEQ_ID"));
        dept.setDeptName(rs4.getString("DEPT_NAME"));
        dept.setDeptParent(rs4.getInt("DEPT_PARENT"));
        deptList.add(dept);
      }
    }catch(Exception ex) {
      throw ex;
    }finally {
      T9DBUtility.close(stm4 , rs4 , null);
    }
    return deptList;
  }
  /**
   * 查询默认部门
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDefaultDept(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    boolean hasModule = false;
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    if ((moduleId != null && !"".equals(moduleId)) || privNoFlag != 0) {
      hasModule = true;
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9MyPriv mp = new T9MyPriv();
      StringBuffer sb = new StringBuffer();
      String deptPriv = "1";
      T9DeptSelectLogic logic = new T9DeptSelectLogic();
      List deptList = this.getDeptList(dbConn);
      if (hasModule) {
        mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
        deptPriv = mp.getDeptPriv();
       
        if (!"1".equals(deptPriv) && !"0".equals(deptPriv)){
          String depts =  mp.getDeptId();
          if (depts == null) {
            depts = "";
          }
          String[] aDept = depts.split(",");
          if (aDept.length > 0 && T9Utility.isInteger(aDept[0])) {
            sb = logic.getDeptJson(deptList, Integer.parseInt(aDept[0]));
          } else {
            sb.append("[]");
          }
        } else if ("0".equals(deptPriv)){
          sb = logic.getDeptJson(deptList, person.getDeptId());
        } else {
          sb = logic.getDeptJson(deptList);
        }
      } else {
        sb = logic.getDeptJson(deptList);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, deptPriv);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getOutOf(Connection conn , String depts) throws Exception {
    String[] aDept = depts.split(",");
    String deptNew = "";
    for (String tmp : aDept) {
      if (T9Utility.isInteger(tmp)) {
        StringBuffer sb = new StringBuffer();
        this.getParentDept(conn, tmp, sb);
        String parentIds = sb.toString();
        String str = this.checkId(parentIds, depts, true);
        if ("".equals(str)) {
          deptNew += tmp + ",";
        }
      }
    }
    if (deptNew.endsWith(",")) {
      deptNew = deptNew.substring(0, deptNew.length() - 1);
    }
    return deptNew;
  }
  public void getParentDept(Connection conn,String seqId , StringBuffer sb)throws Exception{
    Statement stmt = null;
    ResultSet rs = null;
    String sql = "select SEQ_ID, DEPT_PARENT from DEPARTMENT where SEQ_ID ="+ seqId;
    int deptParent = 0;
    try {
      stmt = conn.createStatement();
      rs = stmt.executeQuery(sql);
      if (rs.next()) {
        deptParent = rs.getInt("DEPT_PARENT");
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
    if (deptParent == 0) {
      return ;
    } else {
      sb.append(deptParent + ",");
      this.getParentDept(conn, String.valueOf(deptParent), sb);
    }
    
  }
  /**
   * 交集字符串,非交集

   * @param str 字符串以,分,分割
   * @param flag true-取两字符串的交集,false-取非交集
   * @return
   */
  public  String checkId(String str, String ids , boolean flag) {
    if(ids == null){
      ids = "";
    }
    String[] aStr = ids.split(",");
    String idStr = "";
    for(String tmp : aStr){
      if(flag){
        if(findId(str , tmp)){
          idStr += tmp + ",";
        }
      }else{
        if(!findId(str , tmp)){
          idStr += tmp + ",";
        }
      }
    }
    return idStr;
  }
  /**
   * 判断是非有子部门
   * @param dbConn
   * @param response
   * @param id
   * @return
   * @throws Exception
   */
  private int isHaveSubDept(Connection dbConn, int id) throws Exception {
    try {
      boolean flag = false;
      String query = "select 1 from DEPARTMENT where DEPT_PARENT = " + id;
      ArrayList<T9Department> depts = new ArrayList();
      Statement stm4 = null;
      ResultSet rs4 = null;
      try {
        stm4 = dbConn.createStatement();
        rs4 = stm4.executeQuery(query);
        if (rs4.next()) {
          flag = true;
        }
      }catch(Exception ex) {
        throw ex;
      }finally {
        T9DBUtility.close(stm4 , rs4 , null);
      }
      
      if (flag) {
        return 1;
      } else {
        return 0;
      }
    }catch (Exception ex) {
      throw ex;
    }
  }
  /**
   * 判断是非有子部门
   * @param dbConn
   * @param response
   * @param id
   * @param priv 
   * @param moduleId 
   * @return
   * @throws Exception
   */
  private int isHaveSubDept2(Connection dbConn, int id, T9ModulePriv priv, String moduleId , String deptss) throws Exception {
    try {
      boolean flag = false;
      String query = "select SEQ_ID from DEPARTMENT where DEPT_PARENT = " + id;
      ArrayList<T9Department> depts = new ArrayList();
      Statement stm4 = null;
      ResultSet rs4 = null;
      
      try {
        stm4 = dbConn.createStatement();
        rs4 = stm4.executeQuery(query);
        while (rs4.next()) {
          int seqId = rs4.getInt("SEQ_ID");
          if (("11".equals(moduleId) || "12".equals(moduleId)) 
              && priv != null 
              && ("2".equals(priv.getDeptPriv())
              || "0".equals(priv.getDeptPriv())) && !T9OrgSelectLogic.isShow(dbConn, seqId, deptss) ) {
            continue;
          } else {
            flag = true;
          }
        }
      }catch(Exception ex) {
        throw ex;
      }finally {
        T9DBUtility.close(stm4 , rs4 , null);
      }
      
      if (flag) {
        return 1;
      } else {
        return 0;
      }
    }catch (Exception ex) {
      throw ex;
    }
  }
}
