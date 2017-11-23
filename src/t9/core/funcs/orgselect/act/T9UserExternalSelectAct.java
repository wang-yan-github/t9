package t9.core.funcs.orgselect.act;

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

public class T9UserExternalSelectAct {
  public String getUserExternal(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic logic = new T9UserPrivLogic();
      List<T9Person> list = logic.getUserExternalList(dbConn);
      StringBuffer sb = new StringBuffer();
      for (T9Person up : list) {
        sb.append(up.getJsonSimple());
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
