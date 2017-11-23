package t9.core.module.org_select.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.orgselect.logic.T9DeptSelectLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9LogConst;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9OrgSelectModule {
  public String getTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    String orgId = "organizationNodeId";
    // int id = 0;
    // if (!T9Utility.isNullorEmpty(idStr)) {
    // id = Integer.parseInt(idStr);
    // }
    String moduleId = request.getParameter("MODULE_ID");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 2;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    String hrFlag = request.getParameter("hrFlag");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      StringBuffer record = new StringBuffer();
      StringBuffer sb = new StringBuffer();
      ArrayList<T9Organization> org = new ArrayList();
      T9DeptLogic dls = new T9DeptLogic();
      org = dls.getOrganization(dbConn);
      if ((idStr == null || "".equals(idStr) || "0".equals(idStr))
          && !orgId.equals(idStr)) {
        for (T9Organization orgs : org) {
          String name = orgs.getUnitName();
          String imgAddress = "/t9/core/styles/style1/img/dtree/system.gif";
          record.append("{");
          record.append("nodeId:\"" + orgId + "\"");
          record.append(",name:\"" + name + "\"");
          record.append(",isHaveChild:" + 1 + "");
          record.append(",imgAddress:\"" + imgAddress + "\"");
          record.append(",title:\"" + name + "\"");
          record.append("},");
        }
      } else {
        if (orgId.equals(idStr)) {
          idStr = "0";
        }
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = "
            + idStr + " order by DEPT_NO ASC, DEPT_NAME ASC";
        ArrayList<T9Department> depts = new ArrayList<T9Department>();
        ArrayList<T9Person> persons = new ArrayList<T9Person>();
        Statement stm4 = null;
        ResultSet rs4 = null;
        try {
          stm4 = dbConn.createStatement();
          rs4 = stm4.executeQuery(query);
          while (rs4.next()) {
            T9Department dept = new T9Department();
            dept.setSeqId(rs4.getInt("SEQ_ID"));
            dept.setDeptName(rs4.getString("DEPT_NAME"));
            depts.add(dept);
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm4, rs4, null);
        }
        T9DeptSelectLogic dsl = new T9DeptSelectLogic();
        boolean hasModule = false;
        if (moduleId != null && !"".equals(moduleId)) {
          hasModule = true;
        }
        String noLoginInStr = request.getParameter("noLoginIn");
        boolean noLoginIn = false;
        if (!T9Utility.isNullorEmpty(noLoginInStr)) {
          noLoginIn = true;
        }
        T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
        if (Integer.parseInt(idStr) != 0) {
          persons = osl.getDeptUser(dbConn, Integer.parseInt(idStr)  , noLoginIn);
        }
        String allDef = "";
        T9MyPriv mp = new T9MyPriv();
        mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
        allDef = dsl.getDefUserDept(dbConn, mp, person.getDeptId());
        String contextPath = request.getContextPath();
        for (T9Person per : persons) {
          int seqId = per.getSeqId();
          //判断是否为HR模块参数，isHrManageDept方法为判断当前登录人员的HR权限，用于人力资源年休假设置
          if("1".equals(hrFlag)){
            if( !"1".equals(person.getUserPriv()) && !T9PrivUtil.isHrManageDept(dbConn, per.getDeptId(), person)){
              continue;
            }
          }
          else{
            if (!T9PrivUtil.isUserPriv(dbConn, seqId, mp, person)) {
              continue;
            }
          }
          int deptId = per.getDeptId();
          String deptName = osl.getDeptName(dbConn, deptId);
          String email = per.getEmail();
          int roleId = Integer.parseInt(per.getUserPriv());
          String roleName = osl.getRoleName(dbConn, roleId);
          String telNoDept = per.getTelNoDept();
          if (T9Utility.isNullorEmpty(email)) {
            email = "";
          }
          if (T9Utility.isNullorEmpty(telNoDept)) {
            telNoDept = "";
          }
          String oicq = per.getOicq();
          if (T9Utility.isNullorEmpty(oicq)) {
            oicq = "";
          }
          if (!"".equals(record.toString())) {
            record.append(",");
          }
          String myStatus = per.getMyStatus();
          String myState = "";
          if (T9Utility.isNullorEmpty(myStatus)) {
            myState = "";
          } else {
            myState = "\\n人员状态:" + myStatus + "";
          }
          String userId = per.getUserId(); // cc 20100617
          record.append("{");
          record.append("nodeId:\"r" + seqId + "\"");
          record.append(",name:\"" + T9Utility.encodeSpecial(per.getUserName())
              + "\"");
          record.append(",isHaveChild:" + 0);
          record.append(",extData:\"" + userId + "\"");
          record.append(",imgAddress:\"" + request.getContextPath()
              + "/core/styles/style1/img/dtree/0-1.gif\"");
          record.append(",title:\"部门:" + T9Utility.encodeSpecial(deptName)
              + "\\n角色:" + T9Utility.encodeSpecial(roleName) + "\\n工作电话:"
              + telNoDept + "\\nemail:" + T9Utility.encodeSpecial(email)
              + "\\nQQ:" + oicq + T9Utility.encodeSpecial(myState) + "\"");
          record.append("}");
        }
        for (T9Department d : depts) {
          int nodeId = d.getSeqId();
          String name = d.getDeptName();
          int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
          boolean extData = false;
          if (T9PrivUtil.isDeptPriv(dbConn, nodeId, mp, person)) {
            extData = true;
          }
          String imgAddress = contextPath
              + "/core/styles/style1/img/dtree/node_dept.gif";
          if (!"".equals(record.toString())) {
            record.append(",");
          }
          record.append("{");
          record.append("nodeId:\"" + nodeId + "\"");
          record.append(",name:\"" + T9Utility.encodeSpecial(name) + "\"");
          record.append(",isHaveChild:" + isHaveChild + "");
          record.append(",title:\"" + T9Utility.encodeSpecial(name) + "\"");
          record.append(",extData:" + extData);
          record.append(",imgAddress:\"" + T9Utility.encodeSpecial(imgAddress)
              + "\"");
          record.append("}");
        }
      }
      sb.append("[").append(record).append("]");
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

  public String getPersonTree(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    String idStr = request.getParameter("id");
    String orgId = "organizationNodeId";
    // int id = 0;
    // if (!T9Utility.isNullorEmpty(idStr)) {
    // id = Integer.parseInt(idStr);
    // }
    String moduleId = request.getParameter("MODULE_ID");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 2;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      StringBuffer record = new StringBuffer();
      StringBuffer sb = new StringBuffer();
      ArrayList<T9Organization> org = new ArrayList();
      T9DeptLogic dls = new T9DeptLogic();
      org = dls.getOrganization(dbConn);
      if ((idStr == null || "".equals(idStr) || "0".equals(idStr))
          && !orgId.equals(idStr)) {
        for (T9Organization orgs : org) {
          String name = orgs.getUnitName();
          String imgAddress = "/t9/core/styles/style1/img/dtree/system.gif";
          record.append("{");
          record.append("nodeId:\"" + orgId + "\"");
          record.append(",name:\"" + name + "\"");
          record.append(",isHaveChild:" + 1 + "");
          record.append(",imgAddress:\"" + imgAddress + "\"");
          record.append(",title:\"" + name + "\"");
          record.append("},");
        }
      } else {
        if (orgId.equals(idStr)) {
          idStr = "0";
        }
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = "
            + idStr + " order by DEPT_NO ASC , DEPT_NAME asc";
        ArrayList<T9Department> depts = new ArrayList<T9Department>();
        ArrayList<T9Person> persons = new ArrayList<T9Person>();
        Statement stm4 = null;
        ResultSet rs4 = null;
        try {
          stm4 = dbConn.createStatement();
          rs4 = stm4.executeQuery(query);
          while (rs4.next()) {
            T9Department dept = new T9Department();
            dept.setSeqId(rs4.getInt("SEQ_ID"));
            dept.setDeptName(rs4.getString("DEPT_NAME"));
            depts.add(dept);
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm4, rs4, null);
        }
        T9DeptSelectLogic dsl = new T9DeptSelectLogic();
        boolean hasModule = false;
        if (moduleId != null && !"".equals(moduleId)) {
          hasModule = true;
        }
        T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
        T9OrgSelectLogic osl2 = new T9OrgSelectLogic();
        if (Integer.parseInt(idStr) != 0) {
          persons = osl.getDeptUser2(dbConn, Integer.parseInt(idStr));
        }
        String allDef = "";
        T9MyPriv mp  = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
        allDef = dsl.getDefUserDept(dbConn, mp, person.getDeptId());
        String contextPath = request.getContextPath();
        String ipFlag = getIpState(request, response, dbConn);
        Map<Integer , String > deptNameMap = new HashMap();
        Map<Integer , String > privNameMap = new HashMap();
        
        for (T9Person per : persons) {
          int seqId = per.getSeqId();
          if (!T9PrivUtil.isUserPriv(dbConn, per, mp, person)) {
            continue;
          }
          int deptId = per.getDeptId();
          
          String deptName =deptNameMap.get(deptId);
          if (deptName == null) {
            deptName = osl.getDeptName(dbConn, deptId);
            deptNameMap.put(deptId, deptName);
          }
              
          String email = per.getEmail();
          int roleId = Integer.parseInt(per.getUserPriv());
          String roleName = privNameMap.get(roleId);
          if (roleName == null) {
            roleName = osl.getRoleName(dbConn, roleId);
            privNameMap.put(roleId, roleName);
          }
          
          String telNoDept = per.getTelNoDept();
          if (T9Utility.isNullorEmpty(email)) {
            email = "";
          }
          if (T9Utility.isNullorEmpty(telNoDept)) {
            telNoDept = "";
          }
          String oicq = per.getOicq();
          if (T9Utility.isNullorEmpty(oicq)) {
            oicq = "";
          }
          if (!"".equals(record.toString())) {
            record.append(",");
          }
          String myStatus = per.getMyStatus();
          String myState = "";
          if (!T9Utility.isNullorEmpty(myStatus)) {
            myState = "\\n人员状态:" + T9Utility.encodeSpecial(myStatus) + "";
          }
          
          String showIp = "";
          if("1".equals(ipFlag)){
            if(person.isAdminRole()){
              String ip = osl2.getShowIp(dbConn, T9LogConst.LOGIN, seqId);
              ip = T9Utility.encodeSpecial(ip);
              showIp = "\\n最后登录IP:" + ip +"";
            }
          }else if("2".equals(ipFlag)){
            String ip = osl2.getShowIp(dbConn, T9LogConst.LOGIN,seqId);
            ip = T9Utility.encodeSpecial(ip);
            showIp = "\\n最后登录IP:" + ip +"";
          }
          
          String userId = per.getUserId(); // cc 20100617
          record.append("{");
          record.append("nodeId:\"r" + seqId + "\"");
          record.append(",name:\"" + T9Utility.encodeSpecial(per.getUserName())
              + "\"");
          record.append(",isHaveChild:" + 0);
          record.append(",extData:\"" + userId + "\"");
          record.append(",imgAddress:\"" + request.getContextPath()
              + "/core/styles/style1/img/dtree/0-1.gif\"");
          record.append(",title:\"部门:" + T9Utility.encodeSpecial(deptName)
              + "\\n角色:" + T9Utility.encodeSpecial(roleName) + "\\n工作电话:"
              + telNoDept + "\\nemail:" + T9Utility.encodeSpecial(email)
              + "\\nQQ:" + oicq + myState + showIp + "\"");
          record.append("}");
        }
        for (T9Department d : depts) {
          int nodeId = d.getSeqId();
          String name = d.getDeptName();
          int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
          boolean extData = false;
          if (T9PrivUtil.isDeptPriv(dbConn, nodeId, mp, person)) {
            extData = true;
          }
          String imgAddress = contextPath
              + "/core/styles/style1/img/dtree/node_dept.gif";
          if (!"".equals(record.toString())) {
            record.append(",");
          }
          record.append("{");
          record.append("nodeId:\"" + nodeId + "\"");
          record.append(",name:\"" + T9Utility.encodeSpecial(name) + "\"");
          record.append(",isHaveChild:" + isHaveChild + "");
          record.append(",title:\"" + T9Utility.encodeSpecial(name) + "\"");
          record.append(",extData:" + extData);
          record.append(",imgAddress:\"" + T9Utility.encodeSpecial(imgAddress)
              + "\"");
          record.append("}");
        }
      }
      sb.append("[").append(record).append("]");
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
   * 判断是否有子部门
   * 
   * @param dbConn
   * @param response
   * @param id
   * @return
   * @throws Exception
   */
  // 判断是否有子部门和本部门是否有人:1为有子部门或者本部门有人0为都不是
  public int IsHaveChild(Connection dbConn, int id) throws Exception {
    T9ORM orm = new T9ORM();
    Map map = new HashMap();
    map.put("DEPT_PARENT", id);
    // 判断是否有子部门
    T9OrgSelectLogic osl = new T9OrgSelectLogic();
    ArrayList<T9Department> list = osl.getDepartmentList(dbConn, id);

    // List<T9Department> list = orm.loadListSingle(dbConn, T9Department.class,
    // map);
    // 判断本部门是否有人    // System.out.println(list.size()+"=FGHJT");
    String[] str = { "DEPT_ID =" + id };
    String whereStr = "DEPT_ID =" + id;
    // List<T9Person> personList = orm.loadListSingle(dbConn,
    // T9Person.class,str);
    List<T9Person> personList = osl.getPersonList(dbConn, whereStr);
    if (list.size() > 0 || personList.size() > 0) {
      return 1;
    } else {
      return 0;
    }
  }

  public boolean findId(String str, String id) {
    if (str == null || id == null || "".equals(str) || "".equals(id)) {
      return false;
    }
    String[] aStr = str.split(",");
    for (String tmp : aStr) {
      if (tmp.equals(id)) {
        return true;
      }
    }
    return false;
  }
  public String getIpState(HttpServletRequest request,
      HttpServletResponse response, Connection dbConn) throws Exception {
    String showIpFlag = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9OrgSelectLogic osl = new T9OrgSelectLogic();
      showIpFlag = osl.getSecrityShowIp(dbConn);
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return showIpFlag;
  }
}
