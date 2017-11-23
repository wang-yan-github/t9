package t9.subsys.oa.profsys.act.active;

import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.profsys.data.T9ProjectMem;
import t9.subsys.oa.profsys.logic.active.T9ActiveProjectMemLogic;

public class T9ActiveProjectMemAct {

  /**
   * 来访项目人员By ProjId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryActiveMemByProjId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId(); 
      String projId = request.getParameter("projId");
      if(T9Utility.isNullorEmpty(projId)){
        projId = "0";
      }
      T9ActiveProjectMemLogic tbal = new T9ActiveProjectMemLogic();
      String data = tbal.toSearchData(dbConn, request.getParameterMap(),projId,"0");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 查询大型活动项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectActiveMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectMem mem = new T9ProjectMem();
      String memNum = request.getParameter("memNum");
      String memPosition = request.getParameter("memPosition");
      String memName = request.getParameter("memName");
      String memSex = request.getParameter("memSex");
      String unitNum = request.getParameter("unitNum");
      String unitName = request.getParameter("unitName");
      String projMemType = request.getParameter("projMemType");
      mem.setMemNum(memNum);
      mem.setMemPosition(memPosition);
      mem.setMemName(memName);
      mem.setMemSex(memSex);
      mem.setUnitNum(unitNum);
      mem.setUnitName(unitName);
      mem.setProjMemType(projMemType);
      String projId = T9ActiveProjectMemLogic.memSeqId(dbConn, mem);
      String data = T9ActiveProjectMemLogic.profsysSelectActiveMem(dbConn,request.getParameterMap(),projId,projMemType);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
