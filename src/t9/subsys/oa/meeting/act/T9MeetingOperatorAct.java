package t9.subsys.oa.meeting.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.meeting.data.T9MeetingOperator;
import t9.subsys.oa.meeting.logic.T9MeetingOperatorLogic;

public class T9MeetingOperatorAct {
  private T9MeetingOperatorLogic logic = new T9MeetingOperatorLogic();
  private static Logger log = Logger.getLogger("t9.core.act.action.T9TestAct");
  

  /**
   * 获取会议室管理员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMeetingOperator(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9MeetingOperator org = this.logic.getMeetingOperator(dbConn);
      if (org == null) {
        org = new T9MeetingOperator();
      }
      data = T9FOM.toJson(org).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改会议室管理员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateMeetingOperator(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String paraValue = request.getParameter("meetingOperator");

      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("paraValue", paraValue);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "sysPara", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"管理员设置已修改");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 新增会议管理员字段和信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addMeetingOperator(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paraValue = request.getParameter("meetingOperator");

      Map m =new HashMap();
      m.put("paraValue", paraValue);
      m.put("paraName", "MEETING_OPERATOR");
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, "sysPara", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"管理员设置已修改");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取会议室管理制度
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMeetingRoomRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9MeetingOperator org = this.logic.getMeetingRoomRule(dbConn);
      if (org == null) {
        org = new T9MeetingOperator();
      }
      data = T9FOM.toJson(org).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 修改会议室管理制度
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateMeetingRoomRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String paraValue = request.getParameter("paraValue");

      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("paraValue", paraValue);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "sysPara", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"管理员设置已修改");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addMeetingRoomRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paraValue = request.getParameter("paraValue");

      Map m =new HashMap();
      m.put("paraValue", paraValue);
      m.put("paraName", "MEETING_ROOM_RULE");
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, "sysPara", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"管理员设置已修改");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
