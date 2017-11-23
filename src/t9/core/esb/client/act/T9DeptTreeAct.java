package t9.core.esb.client.act;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.esb.client.data.T9EsbClientConfig;
import t9.core.esb.client.data.T9EsbConst;
import t9.core.esb.client.data.T9EsbMessage;
import t9.core.esb.client.data.T9ExtDept;
import t9.core.esb.client.logic.T9DeptTreeLogic;
import t9.core.esb.client.service.T9WSCaller;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9DeptTreeAct {

  private static Logger log = Logger
      .getLogger("t9.core.esb.client.act.T9DeptTreeAct");
  public String getTree(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String idStr = request.getParameter("id");
    String orgId = "organizationNodeId";
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      if ((idStr == null || "".equals(idStr) || "0".equals(idStr)) && !orgId.equals(idStr)) {
        String name = T9Utility.encodeSpecial("外部组织机构");
        String imgAddress = request.getContextPath() + "/core/styles/style1/img/dtree/system.gif";
        sb.append("{");
        sb.append("nodeId:\"" + orgId + "\"");
        sb.append(",name:\"" + name + "\"");
        sb.append(",isHaveChild:" + 1 + "");
        sb.append(",imgAddress:\"" + imgAddress + "\"");
        sb.append(",title:\"" + name + "\"");
        sb.append("},");
      } else  {
        if(orgId.equals(idStr)) {
          idStr = "0";
        }
        String query = "select DEPT_ID,DEPT_NAME  from ext_dept where DEPT_PARENT = '" + idStr + "' order by DEPT_NO ASC, DEPT_NAME asc";
        ArrayList<T9ExtDept> depts = new ArrayList();
        Statement stm4 = null;
        ResultSet rs4 = null;
        try {
          stm4 = dbConn.createStatement();
          rs4 = stm4.executeQuery(query);
          while (rs4.next()) {
            T9ExtDept dept = new T9ExtDept();
            dept.setDeptId(rs4.getString("DEPT_ID"));
            dept.setDeptName(rs4.getString("DEPT_NAME"));
            depts.add(dept);
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm4, rs4, null);
        }
        for (T9ExtDept d : depts) {
          String nodeId = d.getDeptId();
          String name = T9Utility.encodeSpecial(d.getDeptName());
          int isHaveChild = IsHaveChild(dbConn, d.getDeptId());
          String imgAddress = request.getContextPath() + "/core/styles/style1/img/dtree/node_dept.gif";
          sb.append("{");
          sb.append("nodeId:\"" + nodeId + "\"");
          sb.append(",name:\"" + name + "\"");
          sb.append(",isHaveChild:" + isHaveChild + "");
          sb.append(",imgAddress:\"" + imgAddress + "\"");
          sb.append(",title:\"" + name + "\"");
          sb.append("},");
        }
        
      }
      if (sb.length() > 1) {
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
  
  public int IsHaveChild(Connection conn, String id) throws Exception {
    boolean flag = false;
    String query = "select 1 from ext_dept where DEPT_PARENT = '" + id + "'";
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
  public String dept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId = request.getParameter("deptId");
      T9DeptTreeLogic deptLogic = new T9DeptTreeLogic();
      T9ExtDept dept = deptLogic.getDept(dbConn, deptId);
      StringBuffer sb = T9FOM.toJson(dept);
      String ss = sb.toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, ss);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String selectDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DeptTreeLogic deptLogic = new T9DeptTreeLogic();
      String data = "";
      String deptId = request.getParameter("deptId");
       data = deptLogic.getDeptTreeJson("0" , deptId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptNo  = request.getParameter("DEPT_NO");
      String deptName  = request.getParameter("DEPT_NAME");
      String esbUser  = request.getParameter("ESB_USER");
      String deptParent  = request.getParameter("DEPT_PARENT");
      String deptDesc  = request.getParameter("DEPT_DESC");
      String deptId = request.getParameter("DEPT_ID");
      T9ExtDept de = new T9ExtDept(deptNo, deptName, esbUser, deptParent, deptDesc);
      T9DeptTreeLogic deptLogic = new T9DeptTreeLogic();
      deptLogic.saveDept(dbConn , de , deptId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 同步所有的esb用户的部门
   */
  public T9WSCaller caller = new T9WSCaller();
  public String broadcast(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EsbClientConfig config = T9EsbClientConfig.builder(request.getRealPath("/") + T9EsbConst.CONFIG_PATH);
      String extDept = "sysDept.xml";
      String filePath = config.getCachePath() + File.separator + extDept;
      T9DeptTreeLogic logic = new T9DeptTreeLogic();
      String data = logic.getDepts2(dbConn);
      T9EsbMessage ms = new T9EsbMessage();
      ms.setData(data);
      ms.setMessage("sysDept");
      T9FileUtility.storeString2File(filePath, ms.toXml());
      caller.setWS_PATH(config.getWS_PATH());
      caller.broadcast(filePath, config.getToken());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "同步成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId  = request.getParameter("deptId");
      T9DeptTreeLogic logic = new T9DeptTreeLogic();
      logic.delDept(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getDeptsByDeptParent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId  = request.getParameter("deptId");
      T9DeptTreeLogic logic = new T9DeptTreeLogic();
      StringBuffer sb  = new StringBuffer();
      logic.getDeptsByDeptParent(dbConn, deptId ,0, sb);
      String data = sb.toString();
      String flag = "1";
      if ("".equals(data.trim())) {
        flag = "0";
      } else {
        data = data.substring(0 , data.length() - 1);
      }
      data = "[" + data + "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_MSRG, flag);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
