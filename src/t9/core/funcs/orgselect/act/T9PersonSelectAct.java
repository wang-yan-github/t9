package t9.core.funcs.orgselect.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.orgselect.logic.T9PersonSelectLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

public class T9PersonSelectAct {
  /**
   * 由部门Id选择人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPersonsByDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String deptId = request.getParameter("deptId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9PersonSelectLogic logic = new T9PersonSelectLogic();
      List<T9Person> list = logic.getPersonsByDept(dbConn, Integer.parseInt(deptId));
      StringBuffer sb = new StringBuffer("[");
      for (T9Person p : list) {
        String userId = String.valueOf(p.getSeqId());
        String userName = p.getUserName();
        sb.append("{");
        sb.append("userId:'" + userId + "',");
        sb.append("userName:'" + userName + "'");
        sb.append("},");
      }
      if (list.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
