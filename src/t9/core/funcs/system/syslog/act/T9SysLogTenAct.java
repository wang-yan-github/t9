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

public class T9SysLogTenAct {
  private static Logger log = Logger.getLogger(" t9.core.funcs.system.syslog.act.T9SysLogAct");
  
  public String getMySysLog(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
        String str = "";
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person personLogin = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9SysLogSearchLogic syslog =new T9SysLogSearchLogic();
        str = syslog.getMySysTenLog(dbConn, 233);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出日志");
        request.setAttribute(T9ActionKeys.RET_DATA, str);
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
