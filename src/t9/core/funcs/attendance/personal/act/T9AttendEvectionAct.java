package t9.core.funcs.attendance.personal.act;

import java.net.InetAddress;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.data.T9AttendEvection;
import t9.core.funcs.attendance.personal.logic.T9AttendEvectionLogic;
import t9.core.funcs.attendance.personal.logic.T9AttendLeaveLogic;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9FlowHookUtility;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.fillRegister.logic.T9AttendFillLogic;

public class T9AttendEvectionAct {
  private T9AttendEvectionLogic logic = new T9AttendEvectionLogic();
  /**
   * 添加出差申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String evectionDate1 = request.getParameter("evectionDate1");
      String evectionDate2 = request.getParameter("evectionDate2");
      String smsRemind = request.getParameter("smsRemind");
      String userSeqId = request.getParameter("user");
      if(userSeqId!=null&&!userSeqId.equals("")){
        userId = Integer.parseInt(userSeqId);
      }
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
      String reason = request.getParameter("reason");
      reason = reason.replaceAll("\\\n","");
      reason = reason.replaceAll("\\\r","");
      String evectionDest = request.getParameter("evectionDest");
      T9FOM fom = new T9FOM();
      evection = (T9AttendEvection) fom.build(request.getParameterMap());
      evection.setReason(reason);
      evection.setEvectionDest(evectionDest);
      evection.setRegisterIp(registerIp);
      evection.setAllow("0");
      evection.setStatus("1");
      evection.setUserId(String.valueOf(userId));
      evection.setEvectionDate1(T9Utility.parseDate("yyyy-MM-dd",evectionDate1));
      evection.setEvectionDate2(T9Utility.parseDate("yyyy-MM-dd",evectionDate2));
      T9AttendLeaveLogic all = new T9AttendLeaveLogic();
      double hour = all.getHourDiff(evectionDate1, evectionDate2, "yyyy-MM-dd");
      evection.setHour(hour);
      t9ael.addEvection(dbConn, evection);
      
      T9FlowHookUtility ut = new T9FlowHookUtility();
      int attendEvectionId = ut.getMax(dbConn, "select max(SEQ_ID) FROM ATTEND_EVECTION");
      Map dataArray = new HashMap();
      dataArray.put("KEY", attendEvectionId + "");
      dataArray.put("FIELD", "EVECTION_ID");
      dataArray.put("USER_ID", evection.getUserId()+"");
      T9PersonLogic p = new T9PersonLogic();
      String userName = p.getUserNameLogic(dbConn, Integer.parseInt(evection.getUserId()));
      dataArray.put("USER_NAME", userName);
      String leaderName= p.getUserNameLogic(dbConn, Integer.parseInt(evection.getLeaderId()));
      dataArray.put("LEADER_ID",leaderName);
      dataArray.put("REASON", reason);
      dataArray.put("EVECTION_DEST", evectionDest);
      dataArray.put("EVECTION_DATE1",evectionDate1);
      dataArray.put("EVECTION_DATE2",evectionDate2);
      String url = ut.runHook(dbConn, user, dataArray, "attend_evection");
      if (!"".equals(url)) {
        String path = request.getContextPath();
        response.sendRedirect(path+ url);
        return null;
      }
      
      
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交出差申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(evection.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,evection.getLeaderId(), userId, "提交出差申请，请批示:" + reason, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/evection.jsp");
    return "";
  }
  /*
   * 
   *查询所有出差记录
   */
  public String selectHistroyEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String whereStr = "";
      String ymd = year + "-" + month + "-" + "01";
      if(!T9Utility.isNullorEmpty(year) || !T9Utility.isNullorEmpty(month)){
        whereStr += " and " + T9DBUtility.getMonthFilter("EVECTION_DATE1", T9Utility.parseDate(ymd));
      }
      String data = "[";
      Map map = new HashMap();
      map.put("USER_ID", userId);
      map.put("STATUS", "2 order by EVECTION_DATE1 desc");
      String[] str = {"USER_ID='"+userId +"' and STATUS='2'" + whereStr + " order by EVECTION_DATE1 desc"};
      List<T9AttendEvection> evectionList = t9ael.selectEvection(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      for (int i = 0; i < evectionList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        evection = evectionList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(evection.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
          int runId = fu.isRunHook(dbConn, "EVECTION_ID", evection.getSeqId() + "");
          int flowId = 0;
          if (runId != 0) {
            flowId = ru.getFlowId(dbConn, runId);
        
          
          
        }
        data = data + T9FOM.toJson(evectionList.get(i)).toString().substring(0, T9FOM.toJson(evectionList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",leaderName:\"" + leaderName + "\"},";
      }
      if(evectionList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 
   *查询历史请假记录
   */
  public String selectEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      String data = "[";
      Map map = new HashMap();
      map.put("USER_ID", userId);
      map.put("STATUS", "1");
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      List<T9AttendEvection> evectionList = t9ael.selectEvection(dbConn, map);
      for (int i = 0; i < evectionList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        evection = evectionList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(evection.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        
        int runId = fu.isRunHook(dbConn, "EVECTION_ID", evection.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        data = data + T9FOM.toJson(evectionList.get(i)).toString().substring(0, T9FOM.toJson(evectionList.get(i)).toString().length()-1 ) + ","+"isHookRun:\""+runId+"\",flowId:\""+ flowId +"\",leaderName:\"" + leaderName + "\"},";
      }
      if(evectionList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 
   *查询一条请假记录ById
   *   */
  public String selectEvectionById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      evection = t9ael.selectEvectionById(dbConn, seqId);
      String data = "";
      if(!"".equals(evection.getLeaderId())){
        T9PersonLogic tpl = new T9PersonLogic();
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(evection.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName.substring(0, leaderName.length()-1));
        }
        data = data + T9FOM.toJson(evection).toString().substring(0, T9FOM.toJson(evection).toString().length()-1 ) + ",leaderName:\"" + leaderName + "\"}";
      }
      if(data.equals("")){
        data = data + "{}";
      }
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 
   * 删除出差记录ById
   */
  public String deleteEvectionById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      t9ael.deleteEvectionById(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新出差申请
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateEvection(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String evectionDate1 = request.getParameter("evectionDate1");
      String evectionDate2 = request.getParameter("evectionDate2");
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
      String reason = request.getParameter("reason");
      reason = reason.replaceAll("\\\n","");
      reason = reason.replaceAll("\\\r","");
      String evectionDest = request.getParameter("evectionDest"); 
      T9FOM fom = new T9FOM();
      evection = (T9AttendEvection) fom.build(request.getParameterMap());
    //  reason = new String(reason.getBytes("iso-8859-1"), "utf-8"); 
      evection.setReason(reason);
      evection.setEvectionDest(evectionDest);
      evection.setRegisterIp(registerIp);
      evection.setAllow("0");
      evection.setStatus("1");
      evection.setUserId(String.valueOf(userId));
      evection.setEvectionDate1(T9Utility.parseDate("yyyy-MM-dd",evectionDate1));
      evection.setEvectionDate2(T9Utility.parseDate("yyyy-MM-dd",evectionDate2));
      T9AttendLeaveLogic all = new T9AttendLeaveLogic();
      double hour = all.getHourDiff(evectionDate1, evectionDate2, "yyyy-MM-dd");
      evection.setHour(hour);
      t9ael.updateEvection(dbConn, evection);
      String smsRemind = request.getParameter("smsRemind");
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交出差申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(evection.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,evection.getLeaderId(), userId, "提交出差申请，请批示:" + reason, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/evection.jsp");
    return "";
  }
  /**
   * 更改出差状态/外出归来
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendEvection evection = new T9AttendEvection();
      T9AttendEvectionLogic t9ael = new T9AttendEvectionLogic();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String evectionDate1 = request.getParameter("evectionDate1");
      String evectionDate2 = request.getParameter("evectionDate2");
      String smsRemind = request.getParameter("smsRemind");
      String registerIp = request.getLocalAddr();;
      String reason = request.getParameter("reason");
      reason = reason.replaceAll("\\\n","");
      reason = reason.replaceAll("\\\r","");
      String evectionDest = request.getParameter("evectionDest");
      String seqId = request.getParameter("seqId");
      if(T9Utility.isInteger(seqId)){
        evection = t9ael.selectEvectionById(dbConn, seqId);
        evection.setEvectionDate1(T9Utility.parseDate(evectionDate1));
        evection.setEvectionDate2(T9Utility.parseDate(evectionDate2));
        evection.setEvectionDest(evectionDest);
        evection.setReason(reason);
        evection.setStatus("2");
        t9ael.updateEvection(dbConn, evection);
        //配合陈晨出差自动补登记
        T9AttendFillLogic fillLogic = new T9AttendFillLogic();
        fillLogic.addAttendScoreAbord(dbConn, user, evectionDate1, evectionDate2, userId+"",evection.getLeaderId());
        
        //短信smsType, content, remindUrl, toId, fromId
        if(smsRemind!=null){
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("6");
          sb.setContent("出差归来，请查看！！");
          sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
          sb.setToId(evection.getLeaderId());
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
      }
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 展示自动补登记、不需要审核日期--cc 20101126
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String data = "";
      data = this.logic.showTimeStr(dbConn, beginDate, endDate);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
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
   * 出差总时长
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAttendEvectionHour(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = String.valueOf(person.getSeqId());
      String userIdStr = request.getParameter("userIdStr");
      if(!T9Utility.isNullorEmpty(userIdStr)){
        userId = userIdStr;
      }
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      double data = this.logic.getAttendEvectionHourLogic(dbConn, year, month, userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

}
