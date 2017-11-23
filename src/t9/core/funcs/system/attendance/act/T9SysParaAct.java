package t9.core.funcs.system.attendance.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.attendance.logic.T9SysParaLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SysParaAct {
  /**
   * 
   * 更新或者添加时间参数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String update_addInteval(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String dutyIntervalBefore1 = request.getParameter("dutyIntervalBefore1");
      String dutyIntervalAfter1  = request.getParameter("dutyIntervalAfter1");
      String dutyIntervalBefore2 = request.getParameter("dutyIntervalBefore2");
      String dutyIntervalAfter2  = request.getParameter("dutyIntervalAfter2");
      T9SysParaLogic t9pl = new T9SysParaLogic();
      t9pl.update_addPara(dbConn, "DUTY_INTERVAL_BEFORE1", dutyIntervalBefore1);
      t9pl.update_addPara(dbConn, "DUTY_INTERVAL_AFTER1", dutyIntervalAfter1);
      t9pl.update_addPara(dbConn, "DUTY_INTERVAL_BEFORE2", dutyIntervalBefore2);
      t9pl.update_addPara(dbConn, "DUTY_INTERVAL_AFTER2", dutyIntervalAfter2);
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
   * 查询时间参数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectParaInteval(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysParaLogic t9pl = new T9SysParaLogic();
      String dutyIntervalBefore1 = t9pl.selectPara(dbConn, "DUTY_INTERVAL_BEFORE1");
      String dutyIntervalAfter1 = t9pl.selectPara(dbConn, "DUTY_INTERVAL_AFTER1");
      String dutyIntervalBefore2 = t9pl.selectPara(dbConn, "DUTY_INTERVAL_BEFORE2");
      String dutyIntervalAfter2 = t9pl.selectPara(dbConn, "DUTY_INTERVAL_AFTER2");
      if(dutyIntervalBefore1==null){
        dutyIntervalBefore1 = "";
      }
      if(dutyIntervalAfter1==null){
        dutyIntervalAfter1 = "";
      }
      if(dutyIntervalBefore2==null){
        dutyIntervalBefore2 = "";
      }
      if(dutyIntervalAfter2==null){
        dutyIntervalAfter2 = "";
      }
      String data = "{";
      data = data + "dutyIntervalBefore1:\"" + dutyIntervalBefore1 +"\"," +"dutyIntervalAfter1:\"" + dutyIntervalAfter1 +"\","
      + "dutyIntervalBefore2:\"" + dutyIntervalBefore2 +"\"," + "dutyIntervalAfter2:\"" + dutyIntervalAfter2 +"\"}";
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
   * 查询免签人员参数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectParaNoDutyUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9SysParaLogic t9pl = new T9SysParaLogic();
      String noDutyUserId = t9pl.selectPara(dbConn, "NO_DUTY_USER");
      String noDutyUserName = t9pl.getNamesByIds(dbConn, "NO_DUTY_USER");
      String data = "{user:\"" + noDutyUserId + "\",userDesc:\"" + noDutyUserName +  "\"}";
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
   * 更新或者添加免签人员参数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String update_addNoDutyUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String noDutyUserId = request.getParameter("user");
      //System.out.println(noDutyUserId);
      T9SysParaLogic t9pl = new T9SysParaLogic();
      t9pl.update_addPara(dbConn, "NO_DUTY_USER", noDutyUserId);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/attendance/index.jsp";
  }
}
