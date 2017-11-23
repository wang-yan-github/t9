package t9.subsys.portal.bjca.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.portal.bjca.logic.T9BjcaSSOLogic;

public class T9BjcaSSOAct {
  public String getUserNameByCa(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userUnId = request.getParameter("userId");
      String data = "";
      if(userUnId != null && !"".equals(userUnId)){
        T9BjcaSSOLogic bsso = new T9BjcaSSOLogic();
        data = bsso.getUserNameByCa(dbConn, userUnId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "取得数据成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\""); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
}
