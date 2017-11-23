package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.workflow.data.T9FlowFormItem;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FlowUserSelectLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.funcs.workflow.util.sort.T9FlowProcessComparator;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
public class T9ProcessUserSelectAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9ProcessUserSelectAct");

  public String getUsers(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PersonLogic logic = new T9PersonLogic();
      T9FlowProcessLogic fp = new T9FlowProcessLogic();
      T9FlowProcess proc = fp.getFlowProcessById(Integer.parseInt(seqId) , dbConn);

      String ids = proc.getPrcsUser();

      String data = logic.getPersonSimpleJson(ids , dbConn);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + data + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String getPrivUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sPrcsId = request.getParameter("prcsId");
    String sFlowId = request.getParameter("flowId");
    String sSeqId  = request.getParameter("seqId");
    
    String sDeptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowProcess fp  = null; 
      
      if(sSeqId == null || "".equals(sSeqId) || "null".equals(sSeqId)){
        int flowId =  Integer.parseInt(sFlowId);
        fp = logic.getFlowProcessById(flowId, sPrcsId , dbConn);
      }else{
        int seqId = Integer.parseInt(sSeqId);
        fp = logic.getFlowProcessById(seqId, dbConn);
      }
      String data = "";
      if (fp != null) {
        String user = fp.getPrcsUser();//人员
        String dept = sDeptId;
        String priv = fp.getPrcsPriv();//角色
        T9FlowUserSelectLogic select = new T9FlowUserSelectLogic();
        
        dept = T9OrgSelectLogic.changeDept(dbConn, fp.getPrcsDept()); ///部门
        data = select.getPersonInDept(user, dept, priv, dbConn, sDeptId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + data + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getUserByRole(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String sPrcsId = request.getParameter("prcsId");
    String sFlowId = request.getParameter("flowId");
    String sSeqId  = request.getParameter("seqId");
    String sRoleId = request.getParameter("roleId");
    int roleId = Integer.parseInt(sRoleId);
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowProcessLogic logic = new T9FlowProcessLogic();
      T9FlowProcess fp;
      if(sSeqId == null || "".equals(sSeqId) || "null".equals(sSeqId)){
        int flowId =  Integer.parseInt(sFlowId);
        fp = logic.getFlowProcessById(flowId, sPrcsId , dbConn);
      }else{
        int seqId = Integer.parseInt(sSeqId);
        fp = logic.getFlowProcessById(seqId, dbConn);
      }
      String deptStr = T9OrgSelectLogic.changeDept(dbConn, fp.getPrcsDept()); 
      String userStr =  fp.getPrcsUser() == null ? "" : fp.getPrcsUser();
      String roleStr = fp.getPrcsPriv()  == null ? "" : fp.getPrcsPriv();
      deptStr = deptStr  == null ? "" : deptStr;
      T9FlowUserSelectLogic select = new T9FlowUserSelectLogic();
      
      //取转交相关数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, "{principalRole:[" + select.getUserByRoleP(roleId, fp, dbConn , user) 
          + "],supplementRole:["
          + select.getUserBySupplementRoleP(roleId, fp, dbConn , user) + "]}");
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
