package t9.core.funcs.mobilesms.act;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.mobilesms.logic.T9MobileSelectLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9MobileSelectAct {

  
  /**
   * 取得通讯簿Json数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
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
      String[] filters = new String[]{"GROUP_ID=" + groupId + " and USER_ID=" + userId +" and (MOBIL_NO is not null AND MOBIL_NO <> '')"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      for(Map ms : list){
        sb.append("{");
        sb.append("psnName:\"" + ms.get("psnName")+"(" + ms.get("mobilNo") + ")" + "\"");
        sb.append(",mobilNo:\"" + ms.get("mobilNo") + "\"");
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
      String[] filters = new String[]{"GROUP_ID=" + groupId + " and USER_ID is null and (MOBIL_NO is not null and MOBIL_NO <> '')"};
      List funcList = new ArrayList();
      funcList.add("address");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("ADDRESS"));
      for(Map ms : list){
        sb.append("{");
        sb.append("psnName:\"" + ms.get("psnName")+"(" + ms.get("mobilNo") + ")" + "\"");
        sb.append(",mobilNo:\"" + ms.get("mobilNo") + "\"");
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

  public String isShowSmsRmind(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String type = request.getParameter("type");
      T9Person loginPerson = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      T9MobileSelectLogic msl = new T9MobileSelectLogic();
      StringBuffer data = msl.getSmsRimdData(dbConn, loginPerson.getSeqId(), type);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
