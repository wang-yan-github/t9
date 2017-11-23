package t9.core.funcs.doc.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.logic.T9AttachmentLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9FormEditLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.file.T9FileUploadForm;

public class T9FormEditAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FormEditAct");
  /**
   * 取得编辑界面的一相关数据,主要有表单，附件
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getEditData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      //验证用户是否登陆
      if(loginUser == null){
        String message = T9WorkFlowUtility.Message("用户未登录，请<a href='" + request.getContextPath() +"'>重新登录!</a>",2);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        int runId = Integer.parseInt(runIdStr);
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        if (flowRunLogic.hasDelete(runId, dbConn)) {
          String tmp = "此工作已经删除，您不能办理！";
          this.setRequestError(request, tmp);
        } else {
        //取表单相关信息
          T9FormEditLogic edit = new T9FormEditLogic();
          String imgPath = T9WorkFlowUtility.getImgPath(request);
          String msg = edit.getEditMsg(loginUser , runId  , request.getRemoteAddr() , dbConn , imgPath);
          this.setRequestSuccess(request, "get Success", msg);
        }
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存表单
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveFormData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String flowIdStr = request.getParameter("flowId");
    String runIdStr = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(runIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      T9FormEditLogic logic = new T9FormEditLogic();
      //验证是否有权限
      boolean hasRight = logic.hasEditRight(flowId  , loginUser , dbConn);
      if(!hasRight){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程编辑权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        //取表单相关信息
        String msg = logic.saveFormData(loginUser, flowId, runId, request, dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功!");
      }
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 设置错误信息
   * @param request
   * @param message
   */
  public  void setRequestError(HttpServletRequest request , String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }
  /**
   * 设置成功信息
   * @param request
   * @param message
   */
  public  void setRequestSuccess(HttpServletRequest request , String message) {
    request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }
  /**
   * 设置成功信息 
   * @param request  
   * @param message 
   * @param data
   */
  public  void setRequestSuccess(HttpServletRequest request , String message , String data) {
    request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }
  /**
   * 上传附件处理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       
      T9FileUploadForm fileForm = new T9FileUploadForm();
      //注意这里的
      fileForm.parseUploadRequest(request);
      int runId = Integer.parseInt(fileForm.getParameter("runId"));
      int flowId = Integer.parseInt(fileForm.getParameter("flowId"));
      
      T9FormEditLogic logic1 = new T9FormEditLogic();
      //验证是否有权限
      boolean hasRight = logic1.hasEditRight(flowId  , loginUser , dbConn);
      if(!hasRight){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程编辑权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        logic.addAttachment(runId , fileForm, dbConn);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");  
        PrintWriter out = response.getWriter();
        out.print("<body onload=\"window.parent.callBack()\"/>");
        out.flush();
        out.close();
      }
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getAttachments(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      
      T9FormEditLogic logic1 = new T9FormEditLogic();
      //验证是否有权限
      boolean hasRight = logic1.hasEditRight(flowId  , loginUser , dbConn);
      if(!hasRight){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程编辑权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        String data = "[" + logic.getAttachments(loginUser, runId  , flowId , dbConn) + "]";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "取得成功 ");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      
      T9FormEditLogic logic1 = new T9FormEditLogic();
      //验证是否有权限
      boolean hasRight = logic1.hasEditRight(flowId  , loginUser , dbConn);
      if(!hasRight){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程编辑权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }  else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        logic.delAttachment(runId, attachmentId, attachmentName , dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功 ");
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String createAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String newType = request.getParameter("newType");
      String newName = request.getParameter("newName");
      
      T9FormEditLogic logic1 = new T9FormEditLogic();
      //验证是否有权限
      boolean hasRight = logic1.hasEditRight(flowId  , loginUser , dbConn);
      if(!hasRight){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程编辑权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        String realPath = request.getRealPath("/");
        logic.createAttachment(runId, newType, newName , dbConn , realPath);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功 ");
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
