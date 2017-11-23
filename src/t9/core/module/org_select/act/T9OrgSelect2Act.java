package t9.core.module.org_select.act;

import java.sql.Connection;
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
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.data.T9DepartmentCache;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9OrgSelect2Act {
  /**
   * 按部门选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserByDept(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptStr = request.getParameter("deptId");
      String moduleId = request.getParameter("moduleId");
      String privNoFlagStr = request.getParameter("privNoFlag");
      int privNoFlag = 0;
      if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
        privNoFlag = Integer.parseInt(privNoFlagStr);
      }
      String notLoginInStr = request.getParameter("notLoginIn");
      boolean notLoginIn = false;
      if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
        notLoginIn = Boolean.parseBoolean(notLoginInStr);
      }
      long date1 = System.currentTimeMillis();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int deptId = Integer.parseInt(deptStr);
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
      boolean hasModule = false;
      if (moduleId != null && !"".equals(moduleId)) {
        hasModule = true;
      }
      StringBuffer data = osl.deptUser2Json(dbConn, deptId,mp,person , hasModule , notLoginIn);
      long date2 = System.currentTimeMillis();
      long date3 = date2 - date1;
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 按角色选择人员（包括辅助角色）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserByRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    String notLoginInStr = request.getParameter("notLoginIn");
    boolean notLoginIn = false;
    if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
      notLoginIn = Boolean.parseBoolean(notLoginInStr);
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String roleIdStr = request.getParameter("roleId");
      int roleId = Integer.parseInt(roleIdStr);
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, user, moduleId, privNoFlag);
      boolean hasModule = false;
      if (moduleId != null && !"".equals(moduleId)) {
        hasModule = true;
      }
      StringBuffer data = osl.getRoleUser(dbConn, roleId,  user,  hasModule  ,  mp , notLoginIn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 选择在线用户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserByOnline(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, user, moduleId, privNoFlag);
      boolean hasModule = false;
      if (moduleId != null && !"".equals(moduleId)) {
        hasModule = true;
      }
      StringBuffer data = osl.getOnlineUser2Json(dbConn,  user,  hasModule ,  mp);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据查询选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserBySearch(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    String notLoginInStr = request.getParameter("notLoginIn");
    boolean notLoginIn = false;
    if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
      notLoginIn = Boolean.parseBoolean(notLoginInStr);
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userName = request.getParameter("userName");
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, user, moduleId, privNoFlag);
      boolean hasModule = false;
      if (moduleId != null && !"".equals(moduleId)) {
        hasModule = true;
      }
      StringBuffer data = osl.getQueryUser2Json(dbConn, userName , user,  hasModule ,  mp , notLoginIn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据分组（自定义分组/公共分组）选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserByGroup(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    String notLoginInStr = request.getParameter("notLoginIn");
    boolean notLoginIn = false;
    if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
      notLoginIn = Boolean.parseBoolean(notLoginInStr);
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupIdStr = request.getParameter("groupId");
      int groupId = Integer.parseInt(groupIdStr);
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9MyPriv mp = T9PrivUtil.getMyPriv(dbConn, user, moduleId, privNoFlag);
      boolean hasModule = false;
      if (moduleId != null && !"".equals(moduleId)) {
        hasModule = true;
      }
      StringBuffer data = osl.getGorupUser2Json(dbConn, groupId, user,  hasModule ,  mp , notLoginIn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 由部门Id选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonsByDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String moduleId = request.getParameter("moduleId");
      String privNoFlagStr = request.getParameter("privNoFlag");
      String notLoginInStr = request.getParameter("notLoginIn");
      boolean notLoginIn = false;
      if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
        notLoginIn = Boolean.parseBoolean(notLoginInStr);
      }
      int privNoFlag = 0;
      if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
        privNoFlag = Integer.parseInt(privNoFlagStr);
      }
      long date1 = System.currentTimeMillis();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9OrgSelect2Logic onlinelogic = new T9OrgSelect2Logic();
      List<T9Person> list = onlinelogic.getPersonsByDept(dbConn, Integer.parseInt(deptId) ,notLoginIn);
      long date2 = System.currentTimeMillis();
      long date3 = date2 - date1;
      T9Department d = T9DepartmentCache.getDepartmentCache(dbConn, Integer.parseInt(deptId));
      String deptName = "";
      if (d != null) 
        deptName = d.getDeptName();
      
      StringBuffer data = new StringBuffer("[");
      StringBuffer sb = new StringBuffer();
      T9MyPriv mp = new T9MyPriv();
      
      boolean  hasModule = false;
      T9ModulePriv priv = T9PrivUtil.getMyPrivByModel(dbConn,  person.getSeqId(), moduleId);
      if (moduleId != null && !"".equals(moduleId)) {
        mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag , priv);
        hasModule = true;
      }
      String manager = T9PrivUtil.getDeptManager( dbConn ,priv, person, moduleId, privNoFlag) ;
      for (T9Person p : list) {
        if(hasModule 
            && (!T9PrivUtil.isUserPriv(dbConn, p.getSeqId(), mp,  person.getPostPriv(), person.getPostDept(), person.getSeqId(), person.getDeptId())) && !T9WorkFlowUtility.findId(manager, p.getSeqId() + "")){
          continue;
        }
        if(!"".equals(sb.toString())){
          sb.append(",");
        }
        String userId = String.valueOf(p.getSeqId());
        String userName = p.getUserName();
        sb.append("{");
        sb.append("userId:'" + userId + "',");
        sb.append("userName:\"" + T9Utility.encodeSpecial(userName) + "\"");
        sb.append(",isOnline:\"").append(onlinelogic.isUserOnline(dbConn, p.getSeqId())).append("\"");
        sb.append("}");
      }
      data.append(sb).append("]");
      //System.out.println(sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, deptName);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 公共自定义组列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      String isPublic = request.getParameter("isPublic");
      String query = "1=1 and (user_Id is null or user_Id ='') order by ORDER_NO";
      if (isPublic == null 
          || "".equals(isPublic)) {
        query = "1=1 and user_Id  ='"+ userId +"' order by ORDER_NO";
      }
      T9ORM orm = new T9ORM();
      List<Map> list = new ArrayList();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{query};
      List funcList = new ArrayList();
      funcList.add("userGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("USER_GROUP"));
      for(Map ms : list){
        String groupName = ms.get("groupName") == null ? "" : (String)ms.get("groupName");
        String groupNo = ms.get("orderNo") == null ? "" : (String)ms.get("orderNo");
        groupName = groupName.replaceAll("\"", "\\\\\"");
        groupNo = groupNo.replaceAll("\"", "\\\\\"");
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + T9Utility.encodeSpecial(groupName) + "\"");
        sb.append(",orderNo:\"" + groupNo + "\"");
        sb.append("},");
      }
      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1); 
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得人员的状态与名字
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String ids = request.getParameter("ids");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9OrgSelect2Logic logic = new T9OrgSelect2Logic();
      String str  =  "[" +  logic.getPersons(ids, dbConn) + "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据查询选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDefaultUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String moduleId = request.getParameter("moduleId");
    boolean hasModule = false;
    if (moduleId != null && !"".equals(moduleId)) {
      hasModule = true;
    }
    String notLoginInStr = request.getParameter("notLoginIn");
    boolean notLoginIn = false;
    if (!T9Utility.isNullorEmpty(notLoginInStr) ) {
      notLoginIn = Boolean.parseBoolean(notLoginInStr);
    }
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      String name = "请选择部门";
      T9MyPriv mp = new T9MyPriv();
      boolean isMyDept = true;
      if (hasModule) {
        mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
        String deptPriv = mp.getDeptPriv();
        if (!"1".equals(deptPriv) && !"0".equals(deptPriv)){
          isMyDept = false;
        }
      } 
      StringBuffer data = new StringBuffer("[");
      if (isMyDept) {
        T9OrgSelect2Logic onlinelogic = new T9OrgSelect2Logic();
        List<T9Person> list = onlinelogic.getPersonsByDept(dbConn, person.getDeptId() ,notLoginIn);
        T9DeptLogic deptLogic = new T9DeptLogic();
        name = deptLogic.getNameById(person.getDeptId(), dbConn);
        
        StringBuffer sb = new StringBuffer();
        for (T9Person p : list) {
          if(hasModule && !T9PrivUtil.isUserPriv(dbConn, p.getSeqId(), mp,  person.getPostPriv(), person.getPostDept(), person.getSeqId(), person.getDeptId())){
            continue;
          }
          if(!"".equals(sb.toString())){
            sb.append(",");
          }
          String userId = String.valueOf(p.getSeqId());
          String userName = p.getUserName();
          sb.append("{");
          sb.append("userId:'" + userId + "',");
          sb.append("userName:\"" + T9Utility.encodeSpecial(userName) + "\"");
          sb.append(",isOnline:\"").append(onlinelogic.isUserOnline(dbConn, p.getSeqId())).append("\"");
          sb.append("}");
        }
        data.append(sb.toString());
      }
      data.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, name);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
