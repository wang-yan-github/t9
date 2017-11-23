package t9.core.funcs.system.sealmanage.act;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.sealmanage.data.T9Seal;
import t9.core.funcs.system.sealmanage.data.T9SealLog;
import t9.core.funcs.system.sealmanage.logic.T9SealLogLogic;
import t9.core.funcs.system.sealmanage.logic.T9SealLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
public class T9SealLogAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.sealmanage.T9SealManageAct");

  /**
   * 印章权限管理列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getSealLogList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String logType = request.getParameter("logType");
      String sealName = request.getParameter("sealName");
      sealName = T9DBUtility.escapeLike(sealName);
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String userId = request.getParameter("user");
      String data = "";
      T9SealLogLogic sl = new T9SealLogLogic();
      if(!T9Utility.isNullorEmpty(logType) || !T9Utility.isNullorEmpty(sealName) || !T9Utility.isNullorEmpty(beginTime) || !T9Utility.isNullorEmpty(endTime) || !T9Utility.isNullorEmpty(userId)){
        //System.out.println(sealName+"SDESDE");
        data = sl.getSearchList(dbConn,request.getParameterMap(), logType, sealName, beginTime, endTime, userId);
      }else{
        data = sl.getSealList(dbConn,request.getParameterMap());
      }
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getSearchList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String sealId = request.getParameter("sealId");
      String sealName = request.getParameter("sealName");
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      T9SealLogic sl = new T9SealLogic();
      String data = sl.getSearchList(dbConn,request.getParameterMap(), sealId, sealName, beginTime, endTime);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String addPerson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    int num = 0;
    Map map = new HashMap();
    String userIdOld = "";
    String userId = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      
        String userName = request.getParameter("userName");
        String userPriv = request.getParameter("userPriv");
        String userPrivOther = request.getParameter("role");
        
        map.put("userId" , userId);
        map.put("userName" , userName);
        map.put("userPriv" , userPriv);
       
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn , "sealLog" , map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加人员");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSealPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = new HashMap();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
        String seqId = request.getParameter("seqId");
        String userStr = request.getParameter("userStr");
        
        map.put("seqId" , seqId);
        map.put("userStr" , userStr);
       
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "seal", map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加人员");
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取用户名称(多个人)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      String userIdStr = request.getParameter("userId");
      T9SealLogic dl = new T9SealLogic();
      if(!T9Utility.isNullorEmpty(userIdStr)){
        data = dl.getUserNameLogic(dbConn, userIdStr);
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getSealName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      String sId = request.getParameter("sId");
      T9SealLogLogic dl = new T9SealLogLogic();
      if(!T9Utility.isNullorEmpty(sId)){
        data = dl.getSealNameLogic(dbConn, sId);
      }
      //System.out.println(data+"KLLJJL");
      if(!T9Utility.isNullorEmpty(data)){
        data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取用户名称(单个人)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getUserOpName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userId");
      int userId = Integer.parseInt(userIdStr);
      T9SealLogLogic dl = new T9SealLogLogic();
      String data = dl.getUserNameLogic(dbConn, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除印章日志
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSealLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      String sumStrs = request.getParameter("sumStrs");
      T9SealLogLogic pl = new T9SealLogLogic();
      pl.deleteSealLog(dbConn, sumStrs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addSealLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Map map = new HashMap();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9SealLog slLog = new T9SealLog();
      String loginUserId = person.getUserId();
      int loginSeqId = person.getSeqId();
      String loginSeqIds = String.valueOf(loginSeqId);
      String sId = request.getParameter("sealId");
      //System.out.println(sId+"KLOIUIIU");
      String sealName = request.getParameter("sealName");
      //String sealData = request.getParameter("sealData");
      //System.out.println(sealData);
      String deptId = request.getParameter("deptId");
      Calendar cal = Calendar.getInstance();        
      java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
      String logTime = sdf.format(cal.getTime());    
      InetAddress addr = InetAddress.getLocalHost();
      String ip = addr.getHostAddress().toString();
      slLog.setsId(sId);
      slLog.setLogType("makeseal");
      slLog.setLogTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(logTime));
      slLog.setResult("制章成功");
      slLog.setIpAdd(request.getRemoteAddr());
      slLog.setUserId(loginSeqIds);
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, slLog);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加人员");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
