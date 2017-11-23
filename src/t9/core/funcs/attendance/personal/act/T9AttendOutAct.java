package t9.core.funcs.attendance.personal.act;

import java.net.InetAddress;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.data.T9AttendOut;
import t9.core.funcs.attendance.personal.logic.T9AttendOutLogic;
import t9.core.funcs.calendar.info.logic.T9InfoLogic;
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

public class T9AttendOutAct {
  /**
   * 
   * 添加外出记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      //系统 管理员指定的人
      String userSeqId = request.getParameter("user");
      if(userSeqId!=null&&!userSeqId.equals("")){
        userId = Integer.parseInt(userSeqId);
      }
      String outDate = request.getParameter("outDate");
      String outTime1 = request.getParameter("outTime1");
      String submitDateStr = outDate + " " + outTime1 ; 
      String outType = request.getParameter("outType");
      String smsRemind = request.getParameter("smsRemind");
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
/*      String ip = T9SysLogLogic.getIpAddr( request);
      String ipd = request.getRemoteAddr();
      //System.out.println(ip);*/
      T9FOM fom = new T9FOM();
      out = (T9AttendOut) fom.build(request.getParameterMap());
      //System.out.println(outType);
      //outType = new String(outType.getBytes("iso-8859-1"), "utf-8"); 
      outType = outType.replaceAll("\\\n", "");
      outType = outType.replaceAll("\\\r", "");
      //System.out.println(outType);
      out.setOutType(outType);
      out.setRegisterIp(registerIp);
      out.setAllow("0");
      out.setStatus("0");
      out.setUserId(String.valueOf(userId));
      out.setCreateDate(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",dateFormat.format(new Date())));
      out.setSubmitTime(T9Utility.parseDate("yyyy-MM-dd HH:mm", submitDateStr));
      t9aol.addOut(dbConn, out);
      
      T9FlowHookUtility ut = new T9FlowHookUtility();
      int attendOutId = ut.getMax(dbConn, "select max(SEQ_ID) FROM ATTEND_OUT");
      Map dataArray = new HashMap();
      dataArray.put("KEY", attendOutId + "");
      dataArray.put("FIELD", "OUT_ID");
      dataArray.put("USER_ID", out.getUserId()+"");
      T9PersonLogic p = new T9PersonLogic();
      String userName = p.getUserNameLogic(dbConn, Integer.parseInt(out.getUserId()));
      dataArray.put("USER_NAME", userName);
     // String leaderName= p.getUserNameLogic(dbConn, Integer.parseInt(out.getLeaderId()));
      dataArray.put("LEADER_ID",out.getLeaderId());
      dataArray.put("REASON", out.getReason());
      dataArray.put("OUT_TYPE", out.getOutType());
      dataArray.put("OUT_DATE",outDate );
      dataArray.put("OUT_TIME1",outDate+" "+out.getOutTime1());
      dataArray.put("OUT_TIME2",outDate+" "+out.getOutTime2());
      String url = ut.runHook(dbConn, user, dataArray, "attend_out");
      if (!"".equals(url)) {
        String path = request.getContextPath();
        response.sendRedirect(path+ url);
        return null;
      }
      
      
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交外出申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(out.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,out.getLeaderId(), userId, "提交外出申请，请批示:" + outType, new Date());
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
    response.sendRedirect(path+ "/core/funcs/attendance/personal/out.jsp");
    return "";
  }
  /**
   * 
   * 查询所有外出记录根据自己的ID
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendOut out = new T9AttendOut();
      String data = "[";
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Map map = new HashMap();
      map.put("USER_ID", userId);
      map.put("STATUS", "0");
      String[] str = {"USER_ID='" + userId + "'" , "STATUS = '0' order by SUBMIT_TIME" };
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      List<T9AttendOut> outList = t9aol.selectOut(dbConn, str);
      for (int i = 0; i < outList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        out = outList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(out.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        
        int runId = fu.isRunHook(dbConn, "OUT_ID", out.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        
        
        
        data = data + T9FOM.toJson(outList.get(i)).toString().substring(0, T9FOM.toJson(outList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",leaderName:\"" + leaderName + "\"},";
      }
      if(outList.size()>0){
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
 
  /**
   * 
   * 查询一条记录根据自己的ID and ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectOutById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9AttendOut out = new T9AttendOut();
      String data = "";
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      out = t9aol.selectOutById(dbConn, seqId);
      String userName = T9InfoLogic.getUserName(out.getUserId(), dbConn);
      if(userName!=null&&!userName.equals("")){
        userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
      }
      data = data + T9FOM.toJson(out).toString().substring(0, T9FOM.toJson(out).toString().length()-1)+",userName:\"" + userName + "\"}" ;
 
      if(data.equals("")){
        data = "{}";
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
   * 删除一条记录ById
   */
  public String deleteOutById(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
     // System.out.println(seqId);
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      t9aol.deleteOutById(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /*
   * 
   * updateStatus ById
   */
  public String updateStatus(HttpServletRequest request,
  HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String seqId = request.getParameter("seqId");
      //System.out.println(seqId);
      String outDate = request.getParameter("outDate");
      String outTime1 = request.getParameter("outTime1");
      String outTime2 = request.getParameter("outTime2");
      String outType = request.getParameter("outType");
      String smsRemind = request.getParameter("smsRemind");
      String type = request.getParameter("type");
      outType = outType.replaceAll("\\\n", "");
      outType = outType.replaceAll("\\\r", "");
      String submitDateStr = outDate + " " + outTime1 + ":00"; 
      //System.out.println(submitDateStr);
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      if(seqId!=null&&!seqId.equals("")){
        out = t9aol.selectOutById(dbConn, seqId);
        out.setOutType(outType);
        out.setOutTime1(outTime1);
        out.setOutTime2(outTime2);
        out.setSubmitTime(T9Utility.parseDate(submitDateStr));
        out.setStatus("1");
        t9aol.updateOut(dbConn, out);
       
        //短信smsType, content, remindUrl, toId, fromId
        if(smsRemind!=null){
          T9SmsBack sb = new T9SmsBack();
          
          if(type!=null&&type.equals("1")){//审批人点击外出归来保存后
            
          }else{
            sb.setSmsType("6");
            sb.setContent("外出归来，请查看！！");
            //sb.setRemindUrl("/core/funcs/attendance/manage/manage.jsp");
            sb.setRemindUrl("/core/funcs/attendance/personal/readerEditOut.jsp?seqId="+seqId+"&openFlag=1&openWidth=600&openHeight=300");
            sb.setToId(out.getLeaderId());
            sb.setFromId(userId);
            T9SmsUtil.smsBack(dbConn, sb);
          }

        }
      }
      
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "更新成功！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";   
  }
  /**
   * 
   * 更新外出记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String outDate = request.getParameter("outDate");
      String outTime1 = request.getParameter("outTime1");
      String submitDateStr = outDate + " " + outTime1+":00" ; 
      String outType = request.getParameter("outType");
      String smsRemind = request.getParameter("smsRemind");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      //System.out.println(submitDateStr);
      //得到客户端的IP地址
      T9AttendDutyAct dutyAct = new T9AttendDutyAct();
      String registerIp = dutyAct.getIpAddr(request);
      InetAddress inet = InetAddress.getLocalHost();
      String localIp = inet.getHostAddress();
      if(registerIp!=null&&registerIp.equals("127.0.0.1")){
        registerIp = localIp;
      }
      T9FOM fom = new T9FOM();
      out = (T9AttendOut) fom.build(request.getParameterMap());
     // outType = new String(outType.getBytes("iso-8859-1"), "utf-8"); 
      outType = outType.replaceAll("\\\n", "");
      outType = outType.replaceAll("\\\r", "");
      out.setOutType(outType);
      out.setRegisterIp(registerIp);
      out.setAllow("0");
      out.setStatus("0");
      out.setUserId(String.valueOf(userId));
      out.setReason("");
      out.setCreateDate(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",dateFormat.format(new Date())));
      out.setSubmitTime(T9Utility.parseDate(submitDateStr));
      t9aol.updateOut(dbConn, out);
      //短信smsType, content, remindUrl, toId, fromId
      if(smsRemind!=null){
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("6");
        sb.setContent("提交外出申请，请批示！");
        sb.setRemindUrl("/core/funcs/attendance/manage/index.jsp");
        sb.setToId(out.getLeaderId());
        sb.setFromId(userId);
        T9SmsUtil.smsBack(dbConn, sb);
      }
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      if(moblieSmsRemind!=null){
        T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
        sms2Logic.remindByMobileSms(dbConn,out.getLeaderId(), userId, "提交外出申请，请批示:" + outType, new Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    String path = request.getContextPath();
    response.sendRedirect(path+ "/core/funcs/attendance/personal/out.jsp");
    return "";
  }
  /**
   * 
   * 外出历史记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectHistoryOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      Date date = new Date();
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String whereStr = "";
      String ymd = year + "-" + month + "-" + "01";
      if(!T9Utility.isNullorEmpty(year) || !T9Utility.isNullorEmpty(month)){
        whereStr += " and " + T9DBUtility.getMonthFilter("SUBMIT_TIME", T9Utility.parseDate(ymd));
      }
      Map map = new HashMap();
      T9FOM fom = new T9FOM();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      map.put("USER_ID", userId);
      map.put("ALLOW", "1");
      map.put("status", "1");
      String[] str = {"USER_ID='"+userId + "'and ALLOW='1' " + whereStr + " and STATUS = '1' order by SUBMIT_TIME desc"};
      String data = "[";
      List<T9AttendOut> outList = t9aol.selectHistoryOut(dbConn, str);
      T9FlowHookUtility fu = new T9FlowHookUtility();
      T9FlowRunUtility ru = new T9FlowRunUtility();
      for (int i = 0; i < outList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        out = outList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(out.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        int runId = fu.isRunHook(dbConn, "OUT_ID", out.getSeqId() + "");
        int flowId = 0;
        if (runId != 0) {
          flowId = ru.getFlowId(dbConn, runId);
        }
        data = data + T9FOM.toJson(outList.get(i)).toString().substring(0, T9FOM.toJson(outList.get(i)).toString().length()-1 ) + ",isHookRun:\""+runId+"\",flowId:\""+flowId+"\",leaderName:\"" + leaderName + "\"},";
      }
      if(outList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String selectOutToDisk(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      T9AttendOut out = new T9AttendOut();
      T9AttendOutLogic t9aol = new T9AttendOutLogic();
      Map map = new HashMap();
      T9FOM fom = new T9FOM();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      map.put("USER_ID", userId);
      map.put("ALLOW", "1");
      map.put("status", "1");
      String TEMP1 = T9DBUtility.getDateFilter("SUBMIT_TIME", format.format(new Date()), ">=");
      String TEMP2 = T9DBUtility.getDateFilter("SUBMIT_TIME", format.format(new Date()) + " 23:59:59", "<=");
      String[] str = {TEMP1,TEMP2,"ALLOW='1'","STATUS='0' order by SUBMIT_TIME desc"};
      String data = "[";
      List<T9AttendOut> outList = t9aol.selectOut(dbConn, str);
      for (int i = 0; i < outList.size(); i++) {
        T9PersonLogic tpl = new T9PersonLogic();
        out = outList.get(i);
        String leaderName = "";
        leaderName = tpl.getNameBySeqIdStr(out.getLeaderId() , dbConn);
        if(leaderName!=null&&!leaderName.equals("")){
          leaderName = T9Utility.encodeSpecial(leaderName);
        }
        String userName = tpl.getNameBySeqIdStr(out.getUserId(), dbConn);
        if(userName!=null&&!userName.equals("")){
          userName = T9Utility.encodeSpecial(userName);
        }
        data = data + T9FOM.toJson(outList.get(i)).toString().substring(0, T9FOM.toJson(outList.get(i)).toString().length()-1 ) + ",userName:\""+userName+"\",leaderName:\"" + leaderName + "\"},";
      }
      if(outList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getAttendOutCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
      T9AttendOutLogic adl = new T9AttendOutLogic();
      int data = adl.getAttendOutCountLogic(dbConn, year, month, userId);
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
