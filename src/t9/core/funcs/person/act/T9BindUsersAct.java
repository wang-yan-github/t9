package t9.core.funcs.person.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9BindUsersLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;


public class T9BindUsersAct {
  
  public static final String BIND_SYS_ID = "gpowersoft=通元";
  private T9BindUsersLogic logic = new T9BindUsersLogic();
  
  public String bindInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String userId = request.getParameter("userId");
    
    Connection dbConn = null;
    try {
      
      if (userId == null || "".endsWith(userId.trim())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "USERID为空");
        return "/core/inc/rtjson.jsp";
      }
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String data = null;
      if (this.logic.isBind(dbConn, Integer.parseInt(userId))){
        data = this.logic.queryBindInfo(dbConn, Integer.parseInt(userId));
      }
      else{
        data = "0";
      }
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String bindUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    String userDescOther = request.getParameter("userDescOther");
    String userIdOther = request.getParameter("userIdOther");
    String userId = request.getParameter("userId");
    
    Connection dbConn = null;
    try {
      
      if (userIdOther == null || "".equals(userIdOther.trim())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有传递用户id");
        return "/core/inc/rtjson.jsp";
      }
      
      if (userId == null || "".endsWith(userId.trim())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "USERID为空");
        return "/core/inc/rtjson.jsp";
      }
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      if (this.logic.isBind(dbConn, Integer.parseInt(userId))){
        this.logic.rebindUser(dbConn, Integer.parseInt(userId), userIdOther, userDescOther, BIND_SYS_ID);
      }
      else{
        this.logic.bindUser(dbConn, Integer.parseInt(userId), userIdOther, userDescOther, BIND_SYS_ID);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "绑定成功");
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String removeBind(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    Connection dbConn = null;
    String userId = request.getParameter("userId");
    
    try {
      
      if (userId == null || "".endsWith(userId.trim())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "USERID为空");
        return "/core/inc/rtjson.jsp";
      }
      
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.removeBind(dbConn, Integer.parseInt(userId));
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "解除绑定成功");
      
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
