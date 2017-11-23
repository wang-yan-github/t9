package t9.core.funcs.modulepriv.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.modulepriv.logic.T9ModuleprivLogic;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9ModuleprivAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.modulepriv.act.T9ModuleprivAct");
  
  T9ModuleprivLogic moduleprivLogic = new T9ModuleprivLogic();
  
  public String beforepriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    String id = null;
    String uid = null;
    String userName= null;
    try {
       id = request.getParameter("id");
       uid = request.getParameter("uid");
       userName = request.getParameter("userName");
      //int userId = Integer.parseInt(request.getParameter("userId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
//      request.setAttribute("modulePriv", modulePriv);
//      data = T9FOM.toJson(obj).toString();
//      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
//      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
//      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/modulepriv/priv.jsp?id="+id+"&uid="+uid+"&userName="+userName;
  }
  
  public String updateModulepriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
     String apply_to_module = request.getParameter("apply_to_module");//应用到其他模块
     String apply_to_dept = request.getParameter("apply_to_dept");//应用到其他用户所在部门的限制
     String apply_to_priv = request.getParameter("apply_to_priv");//应用到其他用户所属角色的限制
     Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map map = request.getParameterMap();
      T9ModulePriv modulepriv = (T9ModulePriv)T9FOM.build(request.getParameterMap());  
      int moduleId = modulepriv.getModuleId();
//      String deptPriv = request.getParameter("deptPriv");
//      String rolePriv = request.getParameter("rolePriv");
//      String deptId = request.getParameter("deptId");
//      String privId = request.getParameter("privId");
//      String userId = request.getParameter("userId");
//      String userSeqId = request.getParameter("userSeqId");
//      String moduleId = request.getParameter("moduleId");
//     
//      T9ModulePriv modulepriv = new T9ModulePriv();
//      modulepriv.setDeptPriv(deptPriv);
//      modulepriv.setRolePriv(rolePriv);
//      modulepriv.setDeptId(deptId);
//      modulepriv.setPrivId(privId);
//      modulepriv.setUserId(userId);
//      modulepriv.setUserSeqId(Integer.parseInt(userSeqId));
//      modulepriv.setModuleId(moduleId);
      if(apply_to_module==null) {
        apply_to_module = "";
      }
      apply_to_module = apply_to_module + moduleId;
      moduleprivLogic.queryNeedSetMoudle(dbConn, modulepriv, apply_to_priv, apply_to_dept, apply_to_module);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"按模块设置管理范围信息已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String getJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9ModulePriv modulePriv = null;
    StringBuffer sb = new StringBuffer();
    String data = "";
    String id = request.getParameter("id");
    String uid = request.getParameter("uid");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Statement st = null;
      ResultSet rs = null;
//      T9ORM orm = new T9ORM();
//      Map map = new HashMap();
//      map.put("USER_SEQ_ID", uid);
//      map.put("MODULE_ID", id);
//      modulePriv = (T9ModulePriv)orm.loadObjSingle(dbConn, T9ModulePriv.class, map);
//      
//      if (modulePriv == null) {
//        modulePriv = new T9ModulePriv();
//      }
//      
//      data = modulePriv.toJSON();
      String sql = "select * from MODULE_PRIV where MODULE_ID='"+ id + "' and USER_SEQ_ID='"+ uid + "'";
      st = dbConn.createStatement();
      rs = st.executeQuery(sql);
      if(rs.next()) {
       int seqId =  rs.getInt("SEQ_ID");
       String deptPriiv = rs.getString("DEPT_PRIV");
       String rolePriv = rs.getString("ROLE_PRIV");
       
       String deptId = rs.getString("DEPT_ID");
       if("null".equals(deptId)||deptId==null){
         deptId = "";
       }
       String privId = rs.getString("PRIV_ID");
       if("null".equals(privId)||privId==null){
         privId = "";
       }
       String userId = rs.getString("USER_ID");
       if("null".equals(userId)||userId==null){
         userId = "";
       }
       sb.append("{");
       sb.append("seqId:\"" + seqId  + "\"");
       sb.append(",deptPriv:\"" + deptPriiv  + "\"");
       sb.append(",rolePriv:\"" + rolePriv  + "\"");
       sb.append(",moduleId:\"" + id  + "\"");
       sb.append(",userSeqId:\"" + uid  + "\"");
       sb.append(",deptId:\"" + deptId  + "\"");
       sb.append(",privId:\"" + privId  + "\"");
       sb.append(",userId:\"" + userId  + "\"");
       sb.append("}");
      }else{
         modulePriv = new T9ModulePriv();
        sb =  new StringBuffer(modulePriv.toJSON());
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
}
