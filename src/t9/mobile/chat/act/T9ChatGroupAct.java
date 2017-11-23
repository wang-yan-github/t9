package t9.mobile.chat.act;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.act.T9SystemAct;
import t9.core.funcs.system.act.adapter.T9LoginAdapter;
import t9.core.funcs.system.act.filters.T9PasswordValidator;
import t9.core.funcs.system.logic.T9SystemLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.mobile.chat.logic.T9ChatGroupLogic;
import t9.mobile.logic.T9PdaLoginLogic;
import t9.mobile.util.T9MobileConfig;
import t9.mobile.util.T9MobileUtility;

public class T9ChatGroupAct {
  public String group(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ChatGroupLogic logic = new T9ChatGroupLogic();
      String result = "";
      
       String ATYPE = request.getParameter("ATYPE");
      
      if ("getGroupList".equals(ATYPE))
        result = logic.getGroupList(dbConn , person.getSeqId());
      else if ("addGroup".equals(ATYPE)) {
        String groupName = request.getParameter("GROUP_NAME");
        String userId = request.getParameter("USER_ID");
        result = logic.addGroup(dbConn , person.getSeqId() ,groupName , userId ); 
      } else if ("updateGroup".equals(ATYPE)) {
        String groupName = request.getParameter("GROUP_NAME");
        String userId = request.getParameter("USER_ID");
        String groupId = request.getParameter("groupId");
        result = logic.updateGroup(dbConn , groupId , groupName, userId); 
      } else if ("getGroupById".equals(ATYPE)) {
        String groupId = request.getParameter("groupId");
        result = logic.getGroupById(dbConn , groupId); 
      }else if ("deleteGroupById".equals(ATYPE)) {
        String groupId = request.getParameter("groupId");
        logic.deleteGroupById(dbConn , groupId); 
      }
      T9MobileUtility.output(response, result);
      return null;
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "登录失败");
      throw ex;
    }
  }
  
}
