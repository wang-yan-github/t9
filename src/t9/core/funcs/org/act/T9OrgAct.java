package t9.core.funcs.org.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.org.logic.T9OrgLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.form.T9FOM;

public class T9OrgAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.org.act.T9OrgAct");
  
  T9OrgLogic orgLogic = new T9OrgLogic();
  
  public String getOrganization(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Organization org = null;
      String data = null;
            
      org = orgLogic.get(dbConn);
      if (org == null) {
        org = new T9Organization();
      }
      
      data = T9FOM.toJson(org).toString();
      //System.out.println(data);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateOrganization(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String unitName = request.getParameter("unitName");
      if(unitName == null || "".equals(unitName)) {
        return "/core/inc/rtjson.jsp";
      }
      T9Organization org = (T9Organization)T9FOM.build(request.getParameterMap());     
      orgLogic.update(dbConn, org);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"单位信息已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addOrganization(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String unitName = request.getParameter("unitName");
      if(unitName == null || "".equals(unitName)) {
        return "/core/inc/rtjson.jsp";
      }
      T9Organization org = (T9Organization)T9FOM.build(request.getParameterMap());
      orgLogic.add(dbConn, org);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"单位信息已添加");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
}
