package t9.subsys.inforesouce.docmgr.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.inforesouce.docmgr.logic.T9DoCReceiveDeskLogic;

public class T9DocReceiveDeskAct{
  /**
   * 查找10个批办单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findDocFileAjax(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9DoCReceiveDeskLogic  docLogic = new T9DoCReceiveDeskLogic();
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      String docJson = docLogic.myDocReceiveJsonDesk(dbConn, user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
      request.setAttribute(T9ActionKeys.RET_DATA, docJson);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    
    return "/core/inc/rtjson.jsp";
  }
}
