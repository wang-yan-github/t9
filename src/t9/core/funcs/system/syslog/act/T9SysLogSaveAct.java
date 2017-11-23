package t9.core.funcs.system.syslog.act;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogSaveLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9SysLogSaveAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.syslog.act.T9SysLogAct");
  
  public String SaveLog(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        //T9Person personLogin = (T9Person)request.getSession().getAttribute("233");
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9SysLogSaveLogic save = new T9SysLogSaveLogic();
        String OkandSory="";
        if(person.isAdmin()){
            OkandSory =  save.getSaveLog(dbConn, person);
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute("data", OkandSory);
        request.setAttribute(T9ActionKeys.RET_DATA,"'"+ OkandSory+"'");
    }catch(SQLException ex) {
        String no="同一天不能结转两次";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,no);
        return "/core/inc/rtjson.jsp";
    }
    catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getsysradio(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9SysLogSaveLogic save = new T9SysLogSaveLogic();
        //List list =  save.getOkSaveLog(dbConn, person);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        //request.setAttribute("data", list);
    }
    catch(Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
    }
    return "/core/funcs/system/syslog/manager.jsp";
  }
}
