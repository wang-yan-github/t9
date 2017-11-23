package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowRunFeedback;
import t9.core.funcs.doc.logic.T9FeedbackLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9FeedbackAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FeedbackAct");
  public String handlerFinish(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      int flowPrcs = Integer.parseInt(request.getParameter("flowPrcs"));
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if("".equals(roleStr)){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程办理权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        String sortId = request.getParameter("sortId");
        if (sortId == null) {
          sortId = "";
        }
        String skin = request.getParameter("skin");
        if (skin == null) {
          skin = "";
        }
        String nextPage = flowRunLogic.handlerFinish(loginUser, runId, flowId, prcsId, flowPrcs,request.getRemoteAddr() , sortId , skin , dbConn ,request.getContextPath());
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "办理完毕!");
        request.setAttribute( T9ActionKeys.RET_DATA,  nextPage );
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String feedback(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      int flowPrcs = Integer.parseInt(request.getParameter("flowPrcs"));
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if("".equals(roleStr)){//没有权限
        String message = T9WorkFlowUtility.Message("没有该流程办理权限，请与OA管理员联系",0);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, message);
      }else{
        T9FlowRunLogic flowRunLogic = new T9FlowRunLogic();
        String sortId = request.getParameter("sortId");
        if (sortId == null) {
          sortId = "";
        }
        String skin = request.getParameter("skin");
        if (skin == null) {
          skin = "";
        }
        flowRunLogic.handlerFinish(loginUser, runId, flowId, prcsId, flowPrcs , request.getRemoteAddr() , sortId , skin ,dbConn , request.getContextPath());
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "办理完毕!");
      }
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getRecFeedback(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer sb = new StringBuffer("[");
      int count = 0 ;
      String query  = "SELECT CONTENT from "+ T9WorkFlowConst.FLOW_RUN_FEEDBACK +" where USER_ID='"+loginUser.getSeqId()+"' order by EDIT_TIME desc";
      Statement stm5 = null;
      ResultSet rs5 = null;
      String contentAll = "";
      try {
        stm5 = dbConn.createStatement();
        rs5 = stm5.executeQuery(query);
        while(rs5.next()) {
          String content = T9Utility.null2Empty(rs5.getString("CONTENT")).trim();
          if (T9Utility.isNullorEmpty(content) 
              || contentAll.indexOf("<-->" + content + "<-->") != -1) {
            continue;
          }
          contentAll += "<-->" + content + "<-->";
          String contentView = T9Utility.encodeSpecial(content);
          if (contentView.length() > 35) {
            contentView = contentView.substring(0 , 35) + "...";
          }
          sb.append("{content:\"").append(content).append("\"").append(",contentView:\"").append(contentView).append("\"},");
          count++;
          if (count >= 50) {
            break;
          }
        }
      } catch(Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm5, rs5, null); 
      }
      if (count > 0 ) {
        sb.deleteCharAt(sb.length() -  1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 保存会签
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveFeedback(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String content = request.getParameter("content");
    String seqIdStr = request.getParameter("seqId");
    String attachmentId = request.getParameter("attachmentId");
    if (attachmentId == null) {
      attachmentId = "";
    }
    String attachmentName = request.getParameter("attachmentName");
    if (attachmentName == null) {
      attachmentName = "";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = 0;
      String sRunId = request.getParameter("runId");
      if (T9Utility.isInteger(sRunId)) {
        runId = Integer.parseInt(sRunId);
      }
      int prcsId = 0;
      String sPrcsId = request.getParameter("prcsId");
      if (T9Utility.isInteger(sPrcsId)) {
        prcsId = Integer.parseInt(sPrcsId);
      }
      int flowId = 0;
      String sFlowId = request.getParameter("flowId");
      if (T9Utility.isInteger(sFlowId)) {
        flowId = Integer.parseInt(sFlowId);
      }
      int flowPrcs = 0;
      String sflowPrcs = request.getParameter("flowPrcs");
      if (T9Utility.isInteger(sflowPrcs)) {
        flowPrcs = Integer.parseInt(sflowPrcs);
      }
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限
      String roleStr = roleUtility.runRole ( runId, flowId, prcsId, loginUser , dbConn) ;
      if ( "".equals (roleStr) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程权限，请与OA管理员联系");
      } else {
        String signData = request.getParameter("signData");
        if ( seqIdStr != null && !"".equals(seqIdStr) ) {
          T9FeedbackLogic fbLogic = new T9FeedbackLogic();
          fbLogic.updateFeedback(Integer.parseInt(seqIdStr), content , attachmentId , attachmentName , dbConn);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功!");
        }else{
          T9FeedbackLogic fbLogic = new T9FeedbackLogic();
          T9DocFlowRunFeedback fb = new T9DocFlowRunFeedback();
          if (content.startsWith("<p>")) {
            content = content.replaceFirst("<p>", "");
          }
          if (content.endsWith("</p>")) {
            content = content.substring(0, content.lastIndexOf("</p>"));
          }
          fb.setContent(content);
          Date date = new Date();
          fb.setEditTime(date);
          fb.setPrcsId(prcsId);
          fb.setRunId(runId);
          fb.setUserId(loginUser.getSeqId());
          fb.setAttachmentId(attachmentId);
          fb.setAttachmentName(attachmentName);
          fb.setFlowPrcs(flowPrcs);
          fb.setSignData(signData);
          fbLogic.saveFeedback(fb , dbConn);
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功!");
        }
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getSignData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("feedId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FeedbackLogic fbLogic = new T9FeedbackLogic(); 
      String signData = fbLogic.getSignData(Integer.parseInt(seqId), dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
      request.setAttribute(T9ActionKeys.RET_DATA, "'" + signData + "'");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 取得会签
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFeedbacks(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限
      String roleStr = roleUtility.runRole ( runId, flowId, prcsId, loginUser ,dbConn) ;
      if ( "".equals ( roleStr ) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程权限，请与OA管理员联系");
      } else {
        T9FeedbackLogic fbLogic = new T9FeedbackLogic();
        String feedbacks = "";
        if ( seqId != null && !"".equals(seqId) ) {
          feedbacks = fbLogic.getFeedback(Integer.parseInt(seqId), dbConn);
        } else {
          int flowPrcs = Integer.parseInt(request.getParameter("flowPrcs"));
          feedbacks = "[" +  fbLogic.getFeedbacks(loginUser, runId, prcsId, flowId , flowPrcs , dbConn)+ "]";
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "get success");
        request.setAttribute(T9ActionKeys.RET_DATA, feedbacks );
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delFeedback(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限
      String roleStr = roleUtility.runRole ( runId, flowId, prcsId, loginUser , dbConn) ;
      if ( "".equals ( roleStr ) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程权限，请与OA管理员联系");
      } else {
        T9FeedbackLogic fbLogic = new T9FeedbackLogic();
        fbLogic.delFeedback(seqId , dbConn);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.toString());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
