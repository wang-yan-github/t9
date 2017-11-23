package t9.core.funcs.setdescktop.sealpass.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.mypriv.logic.T9MyprivLogic;
import t9.core.funcs.setdescktop.pass.logic.T9PassLogic;
import t9.core.funcs.setdescktop.sealpass.logic.T9SealPassLogic;
import t9.core.funcs.setdescktop.setports.data.T9Port;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.system.sealmanage.data.T9Seal;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.menu.data.T9SysMenu;

public class T9SealPassAct {
  private T9SealPassLogic logic = new T9SealPassLogic();
  public final static String SEAL_LOG_TYPE = "makeseal";
  
  /**
   * 获取印章信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSealInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      List<T9Seal> list = this.logic.getSealInfo(dbConn, user.getSeqId());
      
      
      StringBuffer sb = new StringBuffer("{\"total\":");
      sb.append(list.size());
      sb.append(",\"records\":[");
      
      for(T9Seal s : list){
        String uerStr = this.logic.getSealUserStr(dbConn, "(" + s.getUserStr() + ")");
        s.setUserStr(uerStr == null ? "" : uerStr);
        
        sb.append(T9FOM.toJson(s));
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]}");
      PrintWriter pw = response.getWriter();
      pw.println(sb.toString().trim());
      pw.flush();
      return "";
      
    }catch(Exception ex) {
      throw ex;
    }
  }
  
  /**
   * 更新印章信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateSealData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String data = request.getParameter("data");
    String sealId = request.getParameter("seqId");
    String pass = request.getParameter("pass");
    String sealName = request.getParameter("sealName");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      if(sealId != null && !"".equals(sealId)){
        this.logic.updateSealData(dbConn, data, sealId);
        this.logic.updateSealLog(dbConn, sealId, T9SealPassAct.SEAL_LOG_TYPE, new T9SysLogLogic().getIpAddr(request), user.getSeqId());
        
        String userList = this.logic.getUserList(dbConn, sealId);
        
        for(String s : userList.split(",")){
          if(Integer.parseInt(s) == user.getSeqId())
            continue;
          T9SmsBack sb = new T9SmsBack();
          sb.setFromId(user.getSeqId());
          sb.setContent("印章" + sealName + "密码已经修改，新的密码为:" + pass);
          sb.setSendDate(new Date());
          sb.setSmsType("0");
          sb.setToId(s);
          T9SmsUtil.smsBack(dbConn,sb);
        }
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
  
}