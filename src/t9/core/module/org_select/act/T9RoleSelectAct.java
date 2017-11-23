package t9.core.module.org_select.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9RoleSelectAct {
  public String getRoles(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String moduleId = request.getParameter("moduleId");
    String privOp = request.getParameter("privOp");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic logic = new T9UserPrivLogic();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9UserPriv> list = null;
      if ((moduleId != null && !"".equals(moduleId)) || !T9Utility.isNullorEmpty(privOp)) {
        list = logic.getRoleList(dbConn , moduleId , loginUser , privNoFlag , privOp);
      } else {
        list = logic.getRoleList(dbConn);
      }
      StringBuffer sb = new StringBuffer();
      for (T9UserPriv up : list) {
        String str = "{";
        str += "privNo:" + up.getSeqId() + ",";  
        str += "privName:\"" + T9Utility.encodeSpecial(up.getPrivName()) + "\"";  
        str += "},";
        sb.append(str);
      }
      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + sb.toString() + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
