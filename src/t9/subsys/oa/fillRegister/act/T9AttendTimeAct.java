package t9.subsys.oa.fillRegister.act;

import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.data.T9AttendTime;
import t9.subsys.oa.fillRegister.logic.T9AttendTimeLogic;

public class T9AttendTimeAct {
  private T9AttendTimeLogic logic = new T9AttendTimeLogic();

  /**
   * 获取迟到时间管理列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendTimeList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String dutyIdStr = request.getParameter("dutyId");
      int dutyId = 0;
      if(!T9Utility.isNullorEmpty(dutyIdStr)){
        dutyId = Integer.parseInt(dutyIdStr);
      }
      String data = this.logic.getAttendTimeListLogic(dbConn, dutyId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 增加迟到时间管理项
   * 
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAttendTimeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String, String[]> map = request.getParameterMap();
      T9AttendTime attendTime = (T9AttendTime) T9FOM.build(map,
          T9AttendTime.class, "");
      this.logic.addAttendTimeItemLogic(dbConn, attendTime);
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
   * 更新迟到时间管理项
   * 
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateAttendTimeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    String minLateTimeStr = request.getParameter("minLateTime");
    String maxLateTimeStr = request.getParameter("maxLateTime");
    String scoreStr = request.getParameter("score");
    String dutyIdStr = request.getParameter("dutyId");

    int seqId = 0;
    if (!T9Utility.isNullorEmpty(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    double score = 0.0;
    int minLateTime = 0;
    int maxLateTime = 0;
    if (T9Utility.isNumber(scoreStr)) {
      score = Double.parseDouble(scoreStr);
    }
    if (T9Utility.isNumber(minLateTimeStr)) {
      minLateTime = Integer.parseInt(minLateTimeStr);
    }
    if (T9Utility.isNumber(maxLateTimeStr)) {
      maxLateTime = Integer.parseInt(maxLateTimeStr);
    }

    int dutyId = 0;
    if (!T9Utility.isNullorEmpty(dutyIdStr)) {
      dutyId = Integer.parseInt(dutyIdStr);
    }

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String, String[]> map = request.getParameterMap();
      T9AttendTime attendTime = (T9AttendTime) T9FOM.build(map,
          T9AttendTime.class, "");
      attendTime.setSeqId(seqId);
      attendTime.setMinLateTime(minLateTime);
      attendTime.setMaxLateTime(maxLateTime);
      attendTime.setScore(score);
      attendTime.setDutyId(dutyId);
      this.logic.updateAttendTimeItemLogic(dbConn, attendTime);
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
   * 更新迟到时间管理项
   * 
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delAttendTimeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    int seqId = 0;
    if (!T9Utility.isNullorEmpty(seqIdStr)) {
      seqId = Integer.parseInt(seqIdStr);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String, String[]> map = request.getParameterMap();
      T9AttendTime attendTime = (T9AttendTime) T9FOM.build(map,
          T9AttendTime.class, "");
      attendTime.setSeqId(seqId);
      this.logic.delAttendTimeItemLogic(dbConn, attendTime);
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
   * 根据条件获取迟到时间管理列表(排班类型)
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendTimeListById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String dutyIdStr = request.getParameter("dutyId");
    String dutyType = request.getParameter("dutyType");
    String registerType = request.getParameter("registerType");

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getAttendTimeListByIdLogic(dbConn, dutyIdStr,
          dutyType, registerType);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 增加迟到时间管理项(排班类型)
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addAttendTimeItemById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String minLateTimeStr = request.getParameter("minLateTime");
    String maxLateTimeStr = request.getParameter("maxLateTime");
    String scoreStr = request.getParameter("score");

    String dutyIdStr = request.getParameter("dutyId");
    String dutyType = request.getParameter("dutyType");
    String registerType = request.getParameter("registerType");

    int minLateTime = 0;
    int maxLateTime = 0;
    double score = 0;
    if (!T9Utility.isNullorEmpty(minLateTimeStr)) {
      minLateTime = Integer.parseInt(minLateTimeStr);
    }
    if (!T9Utility.isNullorEmpty(maxLateTimeStr)) {
      maxLateTime = Integer.parseInt(maxLateTimeStr);
    }
    if (!T9Utility.isNullorEmpty(scoreStr)) {
      score = Double.parseDouble(scoreStr);
    }
    int dutyId = 0;
    if (!T9Utility.isNullorEmpty(dutyIdStr)) {
      dutyId = Integer.parseInt(dutyIdStr);
    }
    if (T9Utility.isNullorEmpty(dutyType)) {
      dutyType = "";
    }
    if (T9Utility.isNullorEmpty(registerType)) {
      registerType = "";
    }

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String, String[]> map = request.getParameterMap();
      T9AttendTime attendTime = new T9AttendTime();
      attendTime.setMinLateTime(minLateTime);
      attendTime.setMaxLateTime(maxLateTime);
      attendTime.setDutyId(dutyId);
      attendTime.setDutyType(dutyType);
      attendTime.setRegisterType(registerType);

      attendTime.setScore(score);
      this.logic.addAttendTimeItemLogic(dbConn, attendTime);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

}
