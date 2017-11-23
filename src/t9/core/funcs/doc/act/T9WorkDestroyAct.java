package t9.core.funcs.doc.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.logic.T9WorkDestroyLogic;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
/**
 * 工作流销毁
 * @author Think
 *
 */
public class T9WorkDestroyAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.doc.act.T9WorkDestroyAct");
  // 工作监控 ACT
  public String getWorkList(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
      StringBuffer result = myWorkLogic.getWorkListLogic(dbConn,loginUser, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
    return null;
  }
  /**
   * 得到所有流程名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        
        T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
        String sortId = request.getParameter("sortId");
        String data = myWorkLogic.getFlow(dbConn  , sortId);
        request.setAttribute(T9ActionKeys.RET_DATA, data);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 直接销毁工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String destroyBysearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      T9Person loginUser = null;
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        
        T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
        StringBuffer runIds = myWorkLogic.getAlldeleteRunId(dbConn,loginUser, request.getParameterMap());
        int data =  myWorkLogic.destroyFlowWork(dbConn, "workFlow", runIds.toString(), loginUser.getSeqId(),request.getRemoteAddr());
        T9DocLogic logic = new T9DocLogic();
        logic.checkNum(dbConn);
        request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 查询后销毁工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String destroy(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      T9Person loginUser = null;
      Connection dbConn = null;
      try {
        String runIds = request.getParameter("runId");
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
        int data =   myWorkLogic.destroyFlowWork(dbConn, "workFlow", runIds, loginUser.getSeqId(),request.getRemoteAddr());
        T9DocLogic logic = new T9DocLogic();
        logic.checkNum(dbConn);
        request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 查询后还原工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String recover(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      
      T9Person loginUser = null;
      Connection dbConn = null;
      try {
        String runIds = request.getParameter("runId");
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
        int data =  myWorkLogic.recoverWork(dbConn, runIds, "workFlow", loginUser.getSeqId());
        request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
  /**
   * 直接还原工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String recoverBysearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      T9Person loginUser = null;
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        
        T9WorkDestroyLogic myWorkLogic = new T9WorkDestroyLogic();
        StringBuffer runIds = myWorkLogic.getAlldeleteRunId(dbConn, loginUser,request.getParameterMap());
        int data =  myWorkLogic.recoverWork(dbConn, runIds.toString(), "workFlow", loginUser.getSeqId());
        request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } catch (Exception ex) {
        ex.printStackTrace();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
}
