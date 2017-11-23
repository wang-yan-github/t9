package t9.core.funcs.system.attendance.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.attendance.data.T9AttendManager;
import t9.core.funcs.system.attendance.logic.T9AttendManagerLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9AttendManagerAct {
  /**
   * 
   * 添加/更新审批管理 人员 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add_updateManager(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendManager manager = new T9AttendManager();
      String userIds = "";
      userIds = request.getParameter("user"); 
      //System.out.println(userIds);
      if(userIds == null){
        userIds = "";
      }
      manager.setManagers(userIds);
      T9AttendManagerLogic t9aml = new T9AttendManagerLogic();
      Map map = null;
      t9aml.add_updateManager(dbConn, manager, map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/attendance/index.jsp";
  }
  /**
   * 
   * 查询审批管理 人员 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectManager(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendManager manager = new T9AttendManager();
      T9AttendManagerLogic t9aml = new T9AttendManagerLogic();
      Map map = null;
      String ids = t9aml.selectManagerIds(dbConn, map);
      String names = "";
      //ids = ids.replaceAll(String.valueOf(userId)+",", "");
      if(!ids.equals("")){
        names = t9aml.getNamesByIds(dbConn, map);
      }
      //System.out.println(ids);
      String data = "{user:\"" + ids + "\",userDesc:\"" + names+ "\"}";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 查询审批管理 人员 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectManagerPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendManager manager = new T9AttendManager();
      T9AttendManagerLogic t9aml = new T9AttendManagerLogic();
      Map map = null;
      List<T9Person> personList = new ArrayList<T9Person>();
      personList = t9aml.getPersonByIds(dbConn, map);
      String data = "[";
      for (int i = 0; i < personList.size(); i++) {
        T9Person person = personList.get(i);
        data = data + "{seqId:" + person.getSeqId() + ",userName:\"" + person.getUserName() + "\"},";
      }
      if(personList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

}
