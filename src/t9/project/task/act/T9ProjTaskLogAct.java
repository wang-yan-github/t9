package t9.project.task.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.project.project.data.T9ProjProject;
import t9.project.project.logic.T9ProjectLogic;
import t9.project.task.data.T9ProjTask;
import t9.project.task.data.T9ProjTaskLog;
import t9.project.task.logic.T9ProjTaskLogLogic;
import t9.project.task.logic.T9TaskLogic;


public class T9ProjTaskLogAct{
  
  
  /**
   * 获取任务日志信息
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskLogDetail(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String taskId=request.getParameter("taskId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      String data=logic.getTaskLogDetail(dbConn,taskId);
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
   * 新增日志
   * @author zq
   * 2013-4-1
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      T9ProjTaskLog log = new T9ProjTaskLog();
      log.setTaskId(Integer.parseInt(request.getParameter("taskId")));
      log.setAttachmentId(request.getParameter("attachmentId1"));
      log.setAttachmentName(request.getParameter("attachmentName1"));
      log.setLogContent(request.getParameter("logContent"));
      log.setPercent(Integer.parseInt(request.getParameter("percent")));
      log.setLogTime(new Date());
      log.setLogUser(person.getUserName());
      T9TaskLogic taskLogic = new T9TaskLogic();
      T9ProjTask task = taskLogic.getTask(dbConn, Integer.parseInt(request.getParameter("taskId")));
      T9ProjectLogic t9ProjectLogic = new T9ProjectLogic();
      T9ProjProject proj = t9ProjectLogic.getProj(dbConn, task.getProjId());
      logic.addLog(dbConn, log);
//      System.out.println(request.getParameter("sendSms"));
      if("1".equals(request.getParameter("sendSms"))){
      	T9SmsBack smsBack = new T9SmsBack();
      	String content = "["+proj.getProjName()+"]有新的进度日志，请查看";
      	String remindUrl = "/project/task/taskLogList.jsp?taskId="+task.getSeqId();         
      	smsBack.setContent(content);
      	smsBack.setFromId(person.getSeqId());
      	smsBack.setRemindUrl(remindUrl);
      	smsBack.setSmsType("88");
      	smsBack.setToId(proj.getProjOwner());
      	T9SmsUtil.smsBack(dbConn, smsBack);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 更新修改
   * @author zq
   * 2013-3-29
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
//      System.out.println("run updateLog");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      T9ProjTaskLog log = logic.getTaskBySeqId(dbConn, Integer.parseInt(request.getParameter("seqId")));
      log.setPercent(Integer.parseInt(request.getParameter("percent")));
      log.setAttachmentId(request.getParameter("attachmentId1"));
      log.setAttachmentName(request.getParameter("attachmentName1"));
      log.setLogContent(request.getParameter("logContent"));
      logic.updateLog(dbConn, log);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "获取数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 上传文件
   * @author zq
   * 2013-4-1
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    String data = "";
    try {
      fileForm.parseUploadRequest(request);
    } catch (Exception e) {
      data = "{type:1}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      return "/core/inc/rtuploadfile.jsp";
    }

    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId") == null) ? "" : fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName") == null) ? "" : fileForm.getParameter("attachmentName");

    try {
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      
      attr = logic.fileUploadLogic(fileForm, logic.ATT_PATCH);
      Set<String> keys = attr.keySet();
      for (String key : keys) {
        String value = attr.get(key);
        if (attrId != null && !"".equals(attrId)) {
          if (!(attrId.trim()).endsWith(",")) {
            attrId += ",";
          }
          if (!(attrName.trim()).endsWith("*")) {
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{type:0,attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  /**
   * 获取进度日志树
   * @author zq
   * 2013-3-27
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTaskLogTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      String data = logic.getTaskLogTree(dbConn, request.getParameterMap(), person, request);
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
   * 获取一个
   * @author zq
   * 2013-4-1
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getLogBySeqId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "[";
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      data = "["+T9FOM.toJson(logic.getTaskBySeqId(dbConn, Integer.parseInt(request.getParameter("seqId")))).toString()+"]";
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
   * 获取百分比
   * @author zq
   * 2013-4-8
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPercent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = "";
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      String percent = logic.getPercent(dbConn, Integer.parseInt(request.getParameter("taskId")));
      data = "[{percent:"+percent+"}]";
      //System.out.println(data);
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
   * 删除任务日志
   * 2013-4-10
   * @author ny
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delTaskLog(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      String taskId=request.getParameter("taskId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjTaskLogLogic logic = new T9ProjTaskLogLogic();
      logic.delTaskLog(dbConn,taskId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "数据删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}