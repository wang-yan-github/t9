package t9.project.bug.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.project.bug.logic.T9ProjBugLogic;



public class T9ProjBugAct{
  
  
  /**
   * 获取项目问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getBugInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      String projId=request.getParameter("projId");
      try {
          T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requesttDbConn.getSysDbConn();
          T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
          T9ProjBugLogic logic = new T9ProjBugLogic();
          String data=logic.getBugInfoList(dbConn,projId);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功!");
          request.setAttribute(T9ActionKeys.RET_DATA, data);
      }catch (Exception e) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
          throw e;
        }
      return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取项目问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getBugInfoListByUserId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String flag=request.getParameter("flag");
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      String data=logic.getBugInfoListByUserId(dbConn,person,flag);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据任务Id获取项目列表信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBugList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String taskId=request.getParameter("taskId");
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      String data=logic.getBugList(dbConn,taskId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
 /**
  * 根据seqId 查询问题具体信息
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String getBugInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String bugId=request.getParameter("bugId");
    try {
        T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requesttDbConn.getSysDbConn();
        T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        T9ProjBugLogic logic = new T9ProjBugLogic();
        String data=logic.getBugInfo(dbConn,bugId);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "数据获取成功!");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch (Exception e) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e;
      }
    return "/core/inc/rtjson.jsp";
}
  
  /**
   *  保存项目问题信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addBugInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      logic.addBugInfo(dbConn,request,person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 回退意见添加
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String subResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      String bugId=request.getParameter("returnBugId");
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      logic.subResult(dbConn,request,bugId,person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据保存成功!");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 根据问题Id删除Bug
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delBugInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String bugId=request.getParameter("bugId");
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      logic.delBugInfo(dbConn,bugId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 提交项目问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String subBug(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String bugId=request.getParameter("bugId");
    String sysRemind=request.getParameter("sysRemind");
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      logic.subBug(dbConn,person,bugId,sysRemind);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 提交项目问题处理结果
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String subSolveResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String bugId=request.getParameter("bugId");
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjBugLogic logic = new T9ProjBugLogic();
      logic.subSolveResult(dbConn,request,bugId,person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}