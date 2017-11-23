package t9.core.funcs.email.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9EmailSelectAct{
  
  public String getMobileSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String groupId = request.getParameter("seqId");
    String userId = request.getParameter("userId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"GROUP_ID=" + groupId + " and USER_ID=" + userId +" and (EMAIL is not null AND EMAIL <> '')"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      for(Map ms : list){
        sb.append("{");
        sb.append("userName:\"" + T9Utility.encodeSpecial((String)ms.get("psnName")) + "\"");
        sb.append(",email:\"" + T9Utility.encodeSpecial((String)ms.get("email")) + "\"");
        sb.append("},");
      }
      if (sb.charAt(sb.length() - 1) == ',') {
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
  
  public String getPublicMobileSelect(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String groupId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"GROUP_ID=" + groupId + " and USER_ID is null and (EMAIL is not null and EMAIL <> '')"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      for(Map ms : list){
        sb.append("{");
        sb.append("userName:\"" + T9Utility.encodeSpecial((String)ms.get("psnName")) + "\"");
        sb.append(",email:\"" + T9Utility.encodeSpecial((String)ms.get("email")) + "\"");
        sb.append("},");
      }
      if (sb.charAt(sb.length() - 1) == ',') {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      //System.out.println(sb+"HJHJ");
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
