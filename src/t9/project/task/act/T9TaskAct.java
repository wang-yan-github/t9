package t9.project.task.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.project.task.data.T9ProjTask;
import t9.project.task.logic.T9TaskLogic;



public class T9TaskAct{
  
  /**
   * 增加任务信息
   * 2013-3-26
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addTaskInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      logic.addTaskInfo(dbConn, request, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  
  /**
   * 根据项目Id 查询任务列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskList(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String projId=request.getParameter("projId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      String data=logic.getTaskList(dbConn,projId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  
  /**
   * 获取任务信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String taskId=request.getParameter("taskId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      String data=logic.getTaskInfo(dbConn,taskId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 通过流程名称
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getflowNameByflowId(HttpServletRequest request, HttpServletResponse response) throws Exception{
	    Connection dbConn = null;
	    String flowId=request.getParameter("flowId");
	    try {
	      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
	      dbConn = requestDbConn.getSysDbConn();
	      T9TaskLogic logic = new T9TaskLogic();
	      String data=logic.getflowNameByflowId(dbConn,flowId);
	      
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
	      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
	      request.setAttribute(T9ActionKeys.RET_DATA, data);
	    } catch (Exception ex) {
	      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
	      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
	      throw ex;
	    }
	    return "/core/inc/rtjson.jsp"; 
	  }
  
  
  
  
  
  
  /**
   * 删除任务信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delTaskInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String taskId=request.getParameter("taskId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      logic.delTaskInfo(dbConn,taskId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据删除成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  
  /**
   * 获取任务用户
   * 2013-3-26
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String projId=request.getParameter("projId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      String data=logic.getTaskUser(dbConn,projId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  
  /**
   * 获取工作流表单
   * 2013-3-26
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowFromName(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
   // String projId=request.getParameter("projId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      String data=logic.getFlowFormName(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取任务名称
   * 2013-3-26
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskName(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String projId=request.getParameter("projId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      String data=logic.getTaskName(dbConn,projId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取任务(进行中/已结束)
   * @author zq
   * 2013-3-26
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9TaskLogic logic = new T9TaskLogic();
      String data = logic.getProjTaskTree(dbConn, request.getParameterMap(), person, request);
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
   * 获取流程
   * @author zq
   * 2013-3-26
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getflowName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String projId = request.getParameter("projId");
      String taskId = request.getParameter("taskId");
      T9TaskLogic logic = new T9TaskLogic();
      String data = logic.getFlowName(dbConn, request.getParameterMap(),Integer.parseInt(projId), String.valueOf(person.getSeqId()),Integer.parseInt(taskId));
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
   * 更新runId流程
   * @author yc
   * 2013-4-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateRunId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String projId = request.getParameter("projId");
      String taskId = request.getParameter("taskId");
      String runId = request.getParameter("runId");
      T9TaskLogic logic = new T9TaskLogic();
      logic.updaterunId(dbConn, Integer.parseInt(projId), runId, Integer.parseInt(taskId),String.valueOf(person.getSeqId()));
      //String data = logic.getFlowName(dbConn, request.getParameterMap(),Integer.parseInt(projId), String.valueOf(person.getSeqId()),Integer.parseInt(taskId));
    
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取正在进行中流程
   * @author yc
   * 2013-4-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getflowNowName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     // String projId = request.getParameter("projId");
      T9TaskLogic logic = new T9TaskLogic();
     // String runId = request.getParameter("runId");
     // String flowId = request.getParameter("flowId");
      String projId = request.getParameter("projId");
      String taskId = request.getParameter("taskId");
    //  String data = logic.getFlowName(dbConn, request.getParameterMap(),Integer.parseInt(projId), String.valueOf(person.getSeqId()));
    //  String data = logic.getFlowNowName(dbConn, request.getParameterMap(),Integer.parseInt(runId) , Integer.parseInt(flowId));
      String data = logic.getFlowNowName(dbConn, request.getParameterMap(), String.valueOf(person.getSeqId()), Integer.parseInt(projId), Integer.parseInt(taskId));
//      PrintWriter pw = response.getWriter();
//      pw.println(data);
//      pw.flush();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    // System.out.println(data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取结束流程
   * @author yc
   * 2013-4-12
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFinshflow(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
     // String projId = request.getParameter("projId");
      T9TaskLogic logic = new T9TaskLogic();
     // String runId = request.getParameter("runId");
     // String flowId = request.getParameter("flowId");
      String projId = request.getParameter("projId");
      String taskId = request.getParameter("taskId");
    //  String data = logic.getFlowName(dbConn, request.getParameterMap(),Integer.parseInt(projId), String.valueOf(person.getSeqId()));
    //  String data = logic.getFlowNowName(dbConn, request.getParameterMap(),Integer.parseInt(runId) , Integer.parseInt(flowId));
      String data = logic.getFinshflow(dbConn, request.getParameterMap(), String.valueOf(person.getSeqId()), Integer.parseInt(projId), Integer.parseInt(taskId));
//      PrintWriter pw = response.getWriter();
//      pw.println(data);
//      pw.flush();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    // System.out.println(data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取流程名称
   * @author yc
   * 2013-4-12
   * @param request
   * @param response
   * @returngetTaskList
   * @throws Exception
   */
  public String getNameByRunId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String runId = request.getParameter("runId");
      T9TaskLogic logic = new T9TaskLogic();
      String data = logic.getNameByRunId(dbConn, runId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据提取成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    // System.out.println(data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp"; 
  }
  /**
   * 获取参与项目
   * @author zq
   * 2013-3-28
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskListByUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9TaskLogic logic = new T9TaskLogic();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      data = logic.getProjListByUser(dbConn, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改任务状态
   * @author zq
   * 2013-3-28
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9TaskLogic logic = new T9TaskLogic();
      T9ProjTask projTask = logic.getTask(dbConn, Integer.parseInt(request.getParameter("seqId")));
      int status =Integer.parseInt(request.getParameter("status"));
      if(status==1){
        projTask.setTaskStatus(status);
        projTask.setTaskActEndTime(new Date());
      }else{
        projTask.setTaskStatus(status);
        projTask.setTaskActEndTime(T9Utility.parseDate("9999-12-31"));
      }
      logic.updateTask(dbConn, projTask);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改状态成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}