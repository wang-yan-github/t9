package t9.core.funcs.system.syslog.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogSearchLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;

public class T9SysLogSearchAct {
  private static Logger log = Logger.getLogger(" t9.core.funcs.system.syslog.act.T9SysLogAct");
  /**
   * 以下方法统计分别是:
   *str2 今年访问量 方法，str3本月访问量，str4今日访问量，str5平均每日访问量
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMySysLog(HttpServletRequest request,
   HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       T9Person personLogin = (T9Person)request.getSession().getAttribute("LOGIN_USER");
       T9SysLogSearchLogic syslog =new T9SysLogSearchLogic();
       String  str = syslog.getMySysLog(dbConn,233);
       String str1= syslog.getMySysLogAll(dbConn, 233);   
       String str2 =  syslog.getMySysYearLog(dbConn, 233);//今年访问量 方法
       String str3 =  syslog.getMySysMonthLog(dbConn, 233);//本月访问量
       String str4 = syslog.getMySysDayLog(dbConn, 233); //今日访问量
       String str5 = syslog.getMySysAveLog(dbConn, 233);//平均每日访问量
      /*
       String str1=""
       StringBuffer sb = new StringBuffer("{str1:");
       sb.append(str);
       sb.append(",str2:");
       //System.out.println(str);
       if(str != null){
       request.setAttribute("countDay",str);
          return "/core/funcs/system/syslog/logsituation.jsp";
        }
       request.setAttribute(T9ActionKeys.RET_MSRG,"日志添加成功：" + person.getUserName() + " 执行 ：" + remark);
       */
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出日志");
        request.setAttribute(T9ActionKeys.RET_DATA, "{str:" + str + ",str1:" + str1 +",str2:"+ str2 +",str3:"+str3+",str4:"+str4+",str5:"+str5+"}");
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
