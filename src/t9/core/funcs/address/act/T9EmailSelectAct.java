package t9.core.funcs.address.act;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9AddressGroup;
import t9.core.funcs.address.logic.T9EmailSelectLogic;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9EmailSelectAct {
  /**
   * 按部门选择email
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
  public String getEmailByRole(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String roleIdStr = request.getParameter("roleId");
      int roleId = Integer.parseInt(roleIdStr);
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer data = onlinelogic.getRoleEmail(dbConn, roleId,  user);
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
  public String getEmailByOnline(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer data = onlinelogic.getOnlineUserEmail2Json(dbConn,  user);
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
  public String getEmailByGroup(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupIdStr = request.getParameter("groupId");
      int groupId = Integer.parseInt(groupIdStr);
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer data = onlinelogic.getGorupEmail2Json(dbConn, groupId, user);
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
   * 由部门Id选择email
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getEmailsByDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      List<T9Person> list = onlinelogic.getEmailsByDept(dbConn, Integer.parseInt(deptId) ,true);
      T9DeptLogic deptLogic = new T9DeptLogic();
      String deptName = deptLogic.getNameById(Integer.parseInt(deptId), dbConn);
      
      StringBuffer data = new StringBuffer("[");
      StringBuffer sb = new StringBuffer();
      
      for (T9Person p : list) {
        if(!"".equals(sb.toString())){
          sb.append(",");
        }
        String userName = p.getUserName();
        sb.append("{");
        sb.append("email:\"" + T9Utility.encodeSpecial(p.getEmail()) + "\",");
        sb.append("userName:\"" + T9Utility.encodeSpecial(userName) + "\"");
        sb.append(",isOnline:\"").append(onlinelogic.isUserOnline(dbConn, p.getSeqId())).append("\"");
        sb.append("}");
      }
      data.append(sb).append("]");
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
   * 公共自定义组列表
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
      String state = logic.getStates(ids, dbConn);
      if (state.endsWith(",")) {
        state = state.substring(0, state.length() - 1);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + state + "\"");
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
  public String getDefaultEmail(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      String name = "请选择部门";
      StringBuffer data = new StringBuffer("[");
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      List<T9Person> list = onlinelogic.getEmailsByDept(dbConn,person.getDeptId()  ,true);
      T9DeptLogic deptLogic = new T9DeptLogic();
      name = deptLogic.getNameById(person.getDeptId(), dbConn);
      
      StringBuffer sb = new StringBuffer();
      for (T9Person p : list) {
        if(!"".equals(sb.toString())){
          sb.append(",");
        }
        String userName = p.getUserName();
        sb.append("{");
        sb.append("email:\"" + T9Utility.encodeSpecial(p.getEmail()) + "\",");
        sb.append("userName:\"" + T9Utility.encodeSpecial(userName) + "\"");
        sb.append(",isOnline:\"").append(onlinelogic.isUserOnline(dbConn, p.getSeqId())).append("\"");
        sb.append("}");
      }
      data.append(sb.toString());
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
  /**
   * 根据查询选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getEmailBySearch(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userName = request.getParameter("userName");
      T9EmailSelectLogic onlinelogic = new T9EmailSelectLogic();
      
      StringBuffer data = onlinelogic.getQueryEmail2Json(dbConn, userName );
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
  public String getContactPersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9AddressGroup> addressGroup = null;
      String[] filters = new String[]{"USER_ID = '" 
          + loginSeqId + "' or (USER_ID is null and ("
          + T9DBUtility.findInSet(String.valueOf(loginSeqId),"PRIV_USER")
          + " or "+ T9DBUtility.findInSet("0","PRIV_DEPT")
          + " or "+ T9DBUtility.findInSet("ALL_DEPT","PRIV_DEPT")
          + " or "+ T9DBUtility.findInSet(String.valueOf(loginDeptId),"PRIV_DEPT")
          + " or "+ T9DBUtility.findInSet(String.valueOf(loginUserPriv),"PRIV_ROLE")
          + ")) order by USER_ID asc, ORDER_NO asc, GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      for(Map ms : list){
        //if(ms.get("userId") == null && (findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) || findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) || findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId)))){
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
        //}
      }
      sb.deleteCharAt(sb.length() - 1); 
      if(list.size() == 0){
        sb = new StringBuffer("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNM");
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
  public String getPublicContactPersonGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginSeqId = person.getSeqId();
      int loginDeptId = person.getDeptId();
      String loginSeqIdStr = String.valueOf(person.getDeptId());
      String loginDeptIdStr = String.valueOf(person.getDeptId());
      int loginUserPriv = Integer.parseInt(person.getUserPriv());
      String loginUserId = person.getUserId(); 
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      
      String[] filters = new String[]{"(USER_ID is null and ("
          + findInSet(String.valueOf(loginSeqId),"PRIV_USER")
          + " or "+ findInSet(String.valueOf(loginDeptId),"PRIV_DEPT")
          + " or "+ findInSet(String.valueOf(loginUserPriv),"PRIV_ROLE")
           + " or "+ findInSet("0","PRIV_DEPT")
            + " or "+ findInSet("ALL_DEPT","PRIV_DEPT")
          + ")) order by USER_ID asc, ORDER_NO asc, GROUP_NAME asc"};
      
      //and (" + T9DBUtility.findInSet(loginDeptIdStr, "SUPPORT_DEPT") +" or (SUPPORT_DEPT like 0 or SUPPORT_DEPT like 'ALL_DEPT') or " + T9DBUtility.findInSet(loginSeqIdStr, "SUPPORT_USER") +" )
      String[] filters2 = new String[]{"USER_ID is null order by ORDER_NO asc, GROUP_NAME asc"};
      List funcList = new ArrayList();
      funcList.add("addressGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters2);
      list.addAll((List<Map>) map.get("ADDRESS_GROUP"));
      int flag = 0;
      for(Map ms : list){
        if(!"0".equals(String.valueOf(ms.get("privDept")))){
          if(!findId(String.valueOf(ms.get("privDept")), String.valueOf(loginDeptId)) && !findId(String.valueOf(ms.get("privRole")), String.valueOf(loginUserPriv)) && !findId(String.valueOf(ms.get("privUser")), String.valueOf(loginSeqId))){
            flag++;
            continue;
          }
        }
        
        String groupName = (String) ms.get("groupName");
        if(!T9Utility.isNullorEmpty(groupName)){
          groupName = groupName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",userId:\"" + (ms.get("userId") == null ? "" : ms.get("userId")) + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      
      if(list.size() == 0 || flag == list.size()){
        sb.append("[");
      }
      sb.append("]");
      //System.out.println(sb+"NNNNMssss");
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
   * mysql findInSet 处理
   * @param str
   * @param dbFieldName
   * @return
   * @throws SQLException
   */
  public String findInSet(String str,String dbFieldName) throws SQLException{
    String dbms = T9SysProps.getProp("db.jdbc.dbms");
    String result = "";
    if (dbms.equals("sqlserver")) {
      result = "find_in_set('" +str+ "'," + dbFieldName + ")";
    }else if (dbms.equals("mysql")) {
      result = "find_in_set('" +str+ "'," + dbFieldName + ")";
    }else if (dbms.equals("oracle")) {
      result = "instr(" + dbFieldName + ",'" +str+ "') > 0";
    }else {
      throw new SQLException("not accepted dbms");
    }
    
    return result;
  }
  /** 
   * 判段id是不是在str里面 
   * @param str 
   * @param id 
   * @return 
   */ 
   public static boolean findId(String str, String id) {
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
}
