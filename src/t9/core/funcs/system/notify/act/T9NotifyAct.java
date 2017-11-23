package t9.core.funcs.system.notify.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.diary.logic.T9DiaryLogic;
import t9.core.funcs.system.notify.logic.T9NotifyLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9NotifyAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.diary.T9DiaryAct");
  
  public String updateNotifyDaysd(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String topDays = request.getParameter("topDays");
      //Date d =new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.updateNotifyDays(dbConn, seqId, topDays);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateNotifyCheck(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String checkAuding = request.getParameter("checkAuding");
      //Date d =new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.updateNotifyCheck(dbConn, seqId, checkAuding);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    } catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateNotifyRangeAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String rangeAllId = request.getParameter("rangeAllId");
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.updateNotifyDays(dbConn, seqId, rangeAllId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateNotifyExceptionId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String rangeExceptionId = request.getParameter("rangeExceptionId");
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.updateNotifyExceptionId(dbConn, seqId, rangeExceptionId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addNotifyRangeAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String rangeAllId = request.getParameter("rangeAllId");
      orgLogic.addRangeAll(dbConn, rangeAllId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addNotifyExceptionId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String rangeExceptionId = request.getParameter("rangeExceptionId");
      orgLogic.addExceptionId(dbConn, rangeExceptionId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addNotifyDays(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String topDays = request.getParameter("topDays");
      //Date d =new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
      //int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.addDays(dbConn, topDays);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addNotifyCheck(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      String checkAuding = request.getParameter("checkAuding");
      //Date d =new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
      //int seqId = Integer.parseInt(request.getParameter("seqId"));
      orgLogic.addCheck(dbConn, checkAuding);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      T9Diary org = null;
      String data = null;
      org = orgLogic.getNotify(dbConn);
      if (org == null) {
        org = new T9Diary();
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
  
  public String getNotifySingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      T9Diary org = null;
      String data = null;
      org = orgLogic.getNotifySingle(dbConn);
      if (org == null) {
        org = new T9Diary();
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
  
  public String getNotifyRangeAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      T9Diary org = null;
      String data = null;
      org = orgLogic.getNotifyRangeAll(dbConn);
      if (org == null) {
        org = new T9Diary();
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
  
  public String getNotifyExceptionId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyLogic orgLogic = new T9NotifyLogic();
      T9Diary org = null;
      String data = null;
      org = orgLogic.getNotifyExceptionId(dbConn);
      if (org == null) {
        org = new T9Diary();
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
}
