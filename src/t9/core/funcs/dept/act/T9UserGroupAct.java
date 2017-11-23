package t9.core.funcs.dept.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.address.logic.T9AddressLogic;
import t9.core.funcs.dept.data.T9UserGroup;
import t9.core.funcs.dept.logic.T9UserGroupLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9UserGroupAct {
  private static Logger log = Logger.getLogger("cc.t9.core.funcs.system.address.T9AddressAct");

  /**
   * 添加自定义组  
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String seqIdStr = String.valueOf(seqId);
      dbConn = requestDbConn.getSysDbConn();
      T9UserGroup dpt = (T9UserGroup) T9FOM.build(request.getParameterMap());
      T9ORM orm = new T9ORM();
      //dpt.setUserId(seqIdStr);
      orm.saveSingle(dbConn, dpt);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
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
      T9ORM orm = new T9ORM();
      List<Map> list = new ArrayList();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"1=1 and (user_Id is null or user_Id ='') order by ORDER_NO"};
      List funcList = new ArrayList();
      funcList.add("userGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("USER_GROUP"));
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        groupName = T9Utility.encodeSpecial(groupName);
        String orderNo = (String) ms.get("orderNo");
        orderNo = T9Utility.encodeSpecial(orderNo);
       
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",orderNo:\"" + (ms.get("orderNo") == null ? "" : orderNo) + "\"");
        sb.append("},");
      }
      if(list.size() == 0){
        sb.append("[");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(sb);
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
   * 编辑用户组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserGroupEdit(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqIdStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqIdStr);
      T9ORM orm = new T9ORM();
      List<Map> list = new ArrayList();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID = " + seqId};
      List funcList = new ArrayList();
      funcList.add("userGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("USER_GROUP"));
      for(Map ms : list){
        String groupName = (String) ms.get("groupName");
        groupName = T9Utility.encodeSpecial(groupName);
        String orderNo = (String) ms.get("orderNo");
        orderNo = T9Utility.encodeSpecial(orderNo);
       
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",groupName:\"" + (ms.get("groupName") == null ? "" : groupName) + "\"");
        sb.append(",orderNo:\"" + (ms.get("orderNo") == null ? "" : orderNo) + "\"");
        sb.append("},");
      }
      if(list.size() == 0){
        sb.append("[");
      }
      sb.deleteCharAt(sb.length() - 1); 
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
   * 删除自定义组
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String deleteUserGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int loginUserSeqId = person.getSeqId();
      String seqId = request.getParameter("seqId");
      T9UserGroupLogic ccl = new T9UserGroupLogic();
      ccl.deleteUserGroup(dbConn, seqId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateUserGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String orderNo = request.getParameter("orderNo");
      String groupName = request.getParameter("groupName");
    
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("orderNo", orderNo);
      m.put("groupName", groupName);
      T9ORM t = new T9ORM();
      t.updateSingle(dbConn, "userGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据SEQ_ID获取编辑数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getEditUserGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      String seqId = request.getParameter("seqId");
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("userGroup");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("USER_GROUP"));
      for(Map ms : list){
        sb.append("{");
        sb.append("userStr:\"" + (ms.get("userStr") == null ? "" : ms.get("userStr")) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      sb.append("]");
      //System.out.println(sb);
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
   * 设置用户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String updateManageUserGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqIdUser = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int orderNo = 0;
      String seqId = request.getParameter("seqId");
      String user = request.getParameter("user");
   
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("userStr", user);
      T9ORM t = new T9ORM();
      t.updateSingle(dbConn, "userGroup", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
