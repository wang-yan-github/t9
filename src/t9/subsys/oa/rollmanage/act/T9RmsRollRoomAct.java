package t9.subsys.oa.rollmanage.act;


import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsRollRoom;
import t9.subsys.oa.rollmanage.logic.T9RmsRollRoomLogic;
public class T9RmsRollRoomAct {
  private T9RmsRollRoomLogic logic = new T9RmsRollRoomLogic();
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");

  /**
   * 新建卷库
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String add(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      Date time = new Date();
      T9RmsRollRoom rmsRollRoom = (T9RmsRollRoom) T9FOM.build(map, T9RmsRollRoom.class, "");
      rmsRollRoom.setAddUser(String.valueOf(person.getSeqId()));
      rmsRollRoom.setAddTime(time);
      this.logic.add(dbConn, rmsRollRoom);
   
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 所属部门下拉框
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToAttendance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);

      T9DeptLogic deptLogic = new T9DeptLogic();
      String data = "";
      data = deptLogic.getDeptTreeJson(0, dbConn);
      if(T9Utility.isNullorEmpty(data)){
        data = "[]";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取卷库管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRmsRollRoomJosn(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getRmsRollRoomJson(dbConn,request.getParameterMap(), person);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getRmsRollRoomDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9RmsRollRoom rmsRollRoom = (T9RmsRollRoom)this.logic.getRmsRollRoomDetail(dbConn, Integer.parseInt(seqId));
      if (rmsRollRoom == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "卷库不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(rmsRollRoom);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  public String update(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      
      T9RmsRollRoom rmsRollRoom = (T9RmsRollRoom) T9FOM.build(map, T9RmsRollRoom.class, "");
      //rmsRollRoom.setModUser(modUser)
      this.logic.updateRmsRollRoom(dbConn, rmsRollRoom);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除一条卷库记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.deleteSingle(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String loginUserId = String.valueOf(person.getSeqId());
      this.logic.deleteAll(dbConn, loginUserId, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
}
