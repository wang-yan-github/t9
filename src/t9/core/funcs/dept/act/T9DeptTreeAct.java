package t9.core.funcs.dept.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9DeptTreeAct {

  private static Logger log = Logger
      .getLogger("t9.core.funcs.dept.act.T9DeptTreeAct");

  public String getTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
   // int id = 0;
    String orgId = "organizationNodeId";
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9Organization> org = new ArrayList();
      T9DeptLogic dls = new T9DeptLogic();
      org = dls.getOrganization(dbConn);
      if ((idStr == null || "".equals(idStr) || "0".equals(idStr)) && !orgId.equals(idStr)) {
        for (T9Organization orgs : org) {
          String name = T9Utility.encodeSpecial(orgs.getUnitName());
          if(T9Utility.isNullorEmpty(name)){
            name = "";
          }
          String imgAddress = "/t9/core/styles/style1/img/dtree/system.gif";
          sb.append("{");
          sb.append("nodeId:\"" + orgId + "\"");
          sb.append(",name:\"" + name + "\"");
          sb.append(",isHaveChild:" + 1 + "");
          sb.append(",imgAddress:\"" + imgAddress + "\"");
          sb.append(",title:\"" + name + "\"");
          sb.append("},");
        }
      } else  {
        if(orgId.equals(idStr)) {
          idStr = "0";
        }
        String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = " + idStr + " order by DEPT_NO ASC, DEPT_NAME asc";
        ArrayList<T9Department> depts = new ArrayList();
        ArrayList<T9Department> deptStr = new ArrayList();
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

        // deptStr = (ArrayList<T9Department>) dls.deleteDeptMul(dbConn, id);
        // depts.addAll(deptStr);

        T9Person person = (T9Person) request.getSession().getAttribute(
            "LOGIN_USER");
        String deptIdOther = person.getDeptIdOther();
        String loginUserPriv = person.getUserPriv();
        String deptIdLogin = String.valueOf(person.getDeptId());
        String postDept = person.getPostDept();
        String postPriv = person.getPostPriv();
        T9PersonLogic dl = new T9PersonLogic();
        String data = "";
        String deptStrs = "";
        boolean isOaAdmin = person.isAdminRole();
        if ("0".equals(postPriv)) {
          deptStrs = dls.getChildDeptId(dbConn, person.getDeptId());
          postDept = deptStrs + deptIdLogin;

        } else if ("2".equals(postPriv)) {
          String[] postFunc = postDept.split(",");
          for (int x = 0; x < postFunc.length; x++) {
            int deptFunc = Integer.parseInt(postFunc[x]);
            if ("".equals(deptStrs)) {
              deptStrs = dls.getChildDeptId(dbConn, deptFunc);
            } else {
              deptStrs += "," + dls.getChildDeptId(dbConn, deptFunc);
            }
          }
          postDept += "," + deptStrs;
        }
        for (T9Department d : depts) {
          if (d.getSeqId() != Integer.parseInt(idStr)) {
            int nodeId = d.getSeqId();
            String deptId = String.valueOf(nodeId);
            String name = T9Utility.encodeSpecial(d.getDeptName());
            int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
            String extData = "";

            if (person.isAdminRole() || postPriv.equals("1")) {
              extData = "isPriv";
            } else {
              if (dl.findId(postDept, deptId)) {
                extData = "isPriv";
              } else {
                extData = "";
              }
            }
            String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
            sb.append("{");
            sb.append("nodeId:\"" + nodeId + "\"");
            sb.append(",name:\"" + name + "\"");
            sb.append(",isHaveChild:" + isHaveChild + "");
            sb.append(",extData:\"" + extData + "\"");
            sb.append(",imgAddress:\"" + imgAddress + "\"");
            sb.append(",title:\"" + name + "\"");
            sb.append("},");
          }
        }
        
      }
      if (sb.length() > 1) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      // long date2 = System.currentTimeMillis();
      // long date3 = date2 - date1;
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

  public String getTree1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String idStr = request.getParameter("id");
    String deptId = request.getParameter("deptId");
    int id = 0;
    if (idStr != null && !"".equals(idStr)) {
      id = Integer.parseInt(idStr);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String query = "select SEQ_ID , DEPT_NAME from DEPARTMENT where DEPT_PARENT = "
          + id;
      ArrayList<T9Department> depts = new ArrayList();
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
      StringBuffer sb = new StringBuffer("[");
      for (T9Department d : depts) {
        int nodeId = d.getSeqId();
        String name = d.getDeptName();
        int isHaveChilds = IsHaveChild(dbConn, Integer.parseInt(deptId));
        if (isHaveChilds != 0) {
          if (d.getSeqId() != Integer.parseInt(deptId)) {
            int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
            String extData = "";
            String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
            sb.append("{");
            sb.append("nodeId:\"" + nodeId + "\"");
            sb.append(",name:\"" + name + "\"");
            sb.append(",isHaveChild:" + isHaveChild + "");
            sb.append(",extData:\"" + extData + "\"");
            sb.append(",imgAddress:\"" + imgAddress + "\"");
            sb.append("},");
          }
        } else {
          int isHaveChild = IsHaveChild(dbConn, d.getSeqId());
          String extData = "";
          String imgAddress = "/t9/core/styles/style1/img/dtree/node_dept.gif";
          sb.append("{");
          sb.append("nodeId:\"" + nodeId + "\"");
          sb.append(",name:\"" + name + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",extData:\"" + extData + "\"");
          sb.append(",imgAddress:\"" + imgAddress + "\"");
          sb.append("},");
        }
      }
      sb.deleteCharAt(sb.length() - 1);
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

  public int IsHaveChild(Connection conn, int id) throws Exception {
    boolean flag = false;
    String query = "select 1 from DEPARTMENT where DEPT_PARENT = " + id;
    ArrayList<T9Department> depts = new ArrayList();
    Statement stm4 = null;
    ResultSet rs4 = null;
    try {
      stm4 = conn.createStatement();
      rs4 = stm4.executeQuery(query);
      if (rs4.next()) {
        flag = true;
      }
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stm4, rs4, null);
    }

    if (flag) {
      return 1;
    } else {
      return 0;
    }
  }
}
