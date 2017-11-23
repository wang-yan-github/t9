package t9.core.esb.client.doc;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

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
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9EsbRecConfigAct {

  private static Logger log = Logger
      .getLogger("t9.core.esb.client.doc.T9EsbRecConfigAct");
    public String getPriv(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9PersonLogic personLogic = new T9PersonLogic();
        T9DeptLogic deptLogic = new T9DeptLogic();
        T9UserPrivLogic privLogic = new T9UserPrivLogic();
        String deptIds = "";
        String privIds ="";
        String userIds = "";
        String query = "select USER_ID , DEPT_ID, USER_PRIV from ESB_REC_PERSON";
        Statement stmt = null;
        ResultSet rs = null;
        try {
          stmt = dbConn.createStatement();
          rs = stmt.executeQuery(query);
          if (rs.next()) {
            deptIds =T9Utility.null2Empty(rs.getString("DEPT_ID"));
             privIds =T9Utility.null2Empty(rs.getString("USER_PRIV"));
             userIds = T9Utility.null2Empty(rs.getString("USER_ID"));
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stmt, rs, null);
        }
        
        
        String deptNames = "";
        if ("0".equals(deptIds)) {
          deptNames = "全体部门";
        } else {
          deptNames = deptLogic.getNameByIdStr(deptIds , dbConn);
        }
        String privNames = privLogic.getNameByIdStr(privIds , dbConn);
        Map map = personLogic.getMapBySeqIdStr(userIds , dbConn);
        String userNames = (String)map.get("name");
        userIds = (String)map.get("id");
        StringBuffer sb = new StringBuffer("{");
        sb.append("userId:'" + userIds + "',");
        sb.append("deptId:'" + deptIds + "',");
        sb.append("privId:'" + privIds + "',");
        sb.append("userName:'" + userNames + "',");
        sb.append("deptName:'" + deptNames + "',");
        sb.append("privName:'" + privNames + "'");
        sb.append("}");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
    public String savePriv(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      String userId = T9Utility.null2Empty(request.getParameter("user"));
      String deptId = T9Utility.null2Empty(request.getParameter("dept"));
      String privId = T9Utility.null2Empty(request.getParameter("role"));
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
          stmt = dbConn.createStatement();
          String query = "select 1 from ESB_REC_PERSON";
          rs = stmt.executeQuery(query);
          if (rs.next()) {
             sql = "update ESB_REC_PERSON set USER_ID='"+userId+"' , DEPT_ID ='"+deptId+"' , USER_PRIV='"+privId+"'";
          } else {
            sql = "insert into  ESB_REC_PERSON  (USER_ID, DEPT_ID , USER_PRIV) VALUES ('"+userId+"' , '"+deptId+"' , '"+privId+"')";
          }
        } catch (Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stmt, rs, null);
        }
        
        Statement stm = null;
        try {
          stm = dbConn.createStatement();
          stm.executeUpdate(sql);
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm, null, null); 
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
}
