package t9.core.funcs.system.attendance.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.attendance.data.T9AttendConfig;
import t9.core.funcs.system.attendance.data.T9AttendHoliday;
import t9.core.funcs.system.attendance.logic.T9AttendConfigLogic;
import t9.core.funcs.system.attendance.logic.T9AttendHolidayLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9AttendHolidayAct {
  /**
   * 
   * 添加节假日
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addHoliday(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendHoliday holiday = new T9AttendHoliday();
      T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
      T9FOM fom = new T9FOM();
      holiday =  (T9AttendHoliday)fom.build(request.getParameterMap());
      t9ahl.addHoliday(dbConn, holiday);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/attendance/holiday.jsp";
  }
  /**
   * 查询所有holiday 返回Data
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHoliday(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendHoliday holiday = new T9AttendHoliday();
      T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
      String data = "[";
      Map map = new HashMap();
      String[] str = {T9DBUtility.getDateFilter("BEGIN_DATE", "1990-01-01", ">=")+"order by BEGIN_DATE desc"};
      List<T9AttendHoliday> holidayList = t9ahl.selectHoliday(dbConn, str);
      for (int i = 0; i < holidayList.size(); i++) {
        data = data+(T9FOM.toJson(holidayList.get(i))).toString() + ",";
      }
      if(holidayList.size() > 0 ){
        data = data.substring(0, data.length() - 1);
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
  /**
   * 查询所有holiday 返回List
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public List<T9AttendHoliday> selectHolidayList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendHoliday holiday = new T9AttendHoliday();
      T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
      Map map = new HashMap();
      String[] str = {T9DBUtility.getDateFilter("BEGIN_DATE", "1990-01-01", ">=")+"order by BEGIN_DATE desc"};
      List<T9AttendHoliday> holidayList = t9ahl.selectHoliday(dbConn, str);
      return holidayList;
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
  }
  /**
   * 查询holiday ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHolidayById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AttendHoliday holiday = new T9AttendHoliday();
      T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
      String data = "";
      //System.out.println(seqId);
      if(!seqId.equals("")&&!seqId.equals("null")){
        holiday = t9ahl.selectHolidayById(dbConn, seqId);
        data = data+(T9FOM.toJson(holiday)).toString();
      }else{
        data = data + "{}";
      }
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
   * 更新节假日 ById
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateHolidayById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendHoliday holiday = new T9AttendHoliday();
      T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
      T9FOM fom = new T9FOM();
      holiday =  (T9AttendHoliday)fom.build(request.getParameterMap());
      t9ahl.updateHoliday(dbConn, holiday);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/attendance/holiday.jsp";
  }
  /**
  * 删除节假日 ById
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String deleteHolidayById(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     String seqId = request.getParameter("seqId");
     T9AttendHoliday holiday = new T9AttendHoliday();
     T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
     t9ahl.deleteHoliday(dbConn, seqId);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
     //request.setAttribute(T9ActionKeys.RET_DATA, "data");
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/funcs/system/attendance/holiday.jsp";
 }
 /**
  * 删除所有节假日 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String deleteAllHoliday(HttpServletRequest request,
     HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
     dbConn = requestDbConn.getSysDbConn();
     T9AttendHoliday holiday = new T9AttendHoliday();
     T9AttendHolidayLogic t9ahl = new T9AttendHolidayLogic();
     t9ahl.deleteAllHoliday(dbConn);
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
     request.setAttribute(T9ActionKeys.RET_MSRG, "全部删除成功！");
     //request.setAttribute(T9ActionKeys.RET_DATA, "data");
   }catch(Exception ex) {
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
     throw ex;
   }
   return "/core/funcs/system/attendance/holiday.jsp";
 }
}
