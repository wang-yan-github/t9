package t9.core.funcs.setdescktop.pass.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.T9Utility;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.pass.logic.T9PassLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.oa.logic.T9OaSyncLogic;
import t9.core.module.report.logic.T9PersonSyncLogic;
import t9.core.module.report.logic.T9ReportSyncLogic;
import t9.core.util.auth.T9PassEncrypt;

public class T9PassAct {
  public final static String SYS_LOG_PASS = "14";
  private T9PassLogic logic = new T9PassLogic();
  
  /**
   * 获取系统参数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSysPara(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      List<String> list = this.logic.getSysPara(dbConn);
      String time = this.logic.getLastPassTime(dbConn, user.getSeqId());
      StringBuffer sb = new StringBuffer("{");
      for(String s : list){
        sb.append(s);
        sb.append(",");
      }
      sb.append("LAST_PASS_TIME:\"");
      if (time == null) {
        time = "";
      }
      
      if (time.length() > 19) {
        time = time.substring(0, 19);
      }
      sb.append(time);
      //演示版禁止更改密码

      String isDemoStr = T9SysProps.getString("IS_ONLINE_EVAL");
      if (!T9Utility.isNullorEmpty(isDemoStr) && "1".equals(isDemoStr)) {
        sb.append("\",\"isDemo\": \"1");
      }
      sb.append("\"}");
      
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取修改密码的历史记录(最后10条)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSysLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      List<Map> list = this.logic.getSysLog(dbConn, user.getSeqId(),T9PassAct.SYS_LOG_PASS);
      StringBuffer sb = new StringBuffer("{\"total\":");
      sb.append(list.size());
      sb.append(",\"records\":[");
      
      for(Map<String,String> map : list){
        
        String time = map.get("TIME");
        if (time != null) {
          time = time.substring(0, time.length() > 19 ? 19 : time.length());
        }
        map.put("TIME", time);
        
        sb.append(this.toJson(map));
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      PrintWriter pw = response.getWriter();
      
      pw.println(sb.toString().trim());
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "";
  }
  
  /**
   * 修改密码
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePassWord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String pw = request.getParameter("PASS1");
    String formPw = request.getParameter("PASS0");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      boolean result = false;
      if(T9PassEncrypt.isValidPas(formPw, user.getPassword())){
        
        //加密
        pw = T9PassEncrypt.encryptPass(pw);
        result = this.logic.updatePassWord(dbConn, user.getSeqId(), pw);
        if (T9ReportSyncLogic.hasSync) {
          Connection reportConn = T9ReportSyncLogic.getReportConn();
          T9PersonSyncLogic logic = new T9PersonSyncLogic();
          logic.updatePersonPwd(user.getSeqId() + "", reportConn, pw);
          if (reportConn != null) {
            reportConn.close();
          }
        }
        if (T9OaSyncLogic.hasSync) {
          Connection reportConn = T9OaSyncLogic.getOAConn();
          t9.core.module.oa.logic.T9PersonSyncLogic logic = new t9.core.module.oa.logic.T9PersonSyncLogic();
          logic.updatePersonPwd(user.getSeqId() + "", reportConn, pw);
          if (reportConn != null) {
            reportConn.close();
          }
        }
        
      }
      
      if(result){
        user.setPassword(pw);
        t9.core.funcs.system.syslog.logic.T9SysLogLogic.addSysLog(
                  dbConn, T9PassAct.SYS_LOG_PASS, "用户修改密码", user.getSeqId(), T9SysLogLogic.getIpAddr(request));
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功设置密码");
      }
      else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"设置密码失败");
      }
      
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 检查原密码是否正确
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String checkPassWord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String pw = request.getParameter("PASSWORD");
    try {
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      if(pw != null && !"".equals(pw)){
        if(!T9PassEncrypt.isValidPas(pw, user.getPassword())){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "输入的原密码错误");
          return "/core/inc/rtjson.jsp";
        }
      }
      else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "");
        return "/core/inc/rtjson.jsp";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  private String toJson(Map<String,String> m) throws Exception{
    StringBuffer sb = new StringBuffer("{");
    for (Iterator<Entry<String,String>> it = m.entrySet().iterator(); it.hasNext();){
      Entry<String,String> e = it.next();
      sb.append(e.getKey());
      sb.append(":\"");
      sb.append(e.getValue());
      sb.append("\",");
    }
    if (sb.charAt(sb.length() - 1) == ','){
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("}");
    return sb.toString();
  }
}