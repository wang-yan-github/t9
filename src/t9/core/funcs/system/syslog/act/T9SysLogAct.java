package t9.core.funcs.system.syslog.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SysLogAct {
  private static Logger log = Logger.getLogger(" t9.core.funcs.system.syslog.act.T9SysLogAct");
  public String addLog(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
        String type = request.getParameter("type");
        String remark = request.getParameter("remark");
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9SysLogLogic.addSysLog(dbConn, type, remark, person.getSeqId(), request.getRemoteAddr());
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"日志添加成功：" + person.getUserName() + " 执行 ：" + remark);
    }catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
