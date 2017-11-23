package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9AttachmentLogic;
import t9.core.funcs.workflow.logic.T9FeedbackLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9MoreOperateLogic;
import t9.core.funcs.workflow.logic.T9MyWorkLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9FlowFormPrintAct {
  private static Logger log = Logger
  .getLogger("t9.core.funcs.workflow.act.T9FlowFormPrintAct");
  public String restoreFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      //验证是否有权限,并取出权限字符串
      String roleStr = roleUtility.runRole(runId, flowId, prcsId, loginUser , dbConn);
      if ( "".equals(roleStr) ) {//没有权限
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "没有该流程办理权限，请与OA管理员联系");
      } else {
        T9AttachmentLogic logic = new  T9AttachmentLogic();
        
        
//        T9FlowRunLogic runLogic = new T9FlowRunLogic();
//        T9FlowRun flowRun = runLogic.getFlowRunByRunId(runId , conn);
//        attachmentId = flowRun.getAttachmentId() != null ? flowRun.getAttachmentId() : "";
//        attachmentName = flowRun.getAttachmentName() != null ? flowRun.getAttachmentName() : "";
//        attachmentIdStr += attachmentId;
//        attachmentNameStr += attachmentName;
//        flowRun.setAttachmentId(attachmentIdStr);
//        flowRun.setAttachmentName(attachmentNameStr);
//        orm.updateSingle(conn, flowRun);
        //logic.createAttachment(runId, newType, newName , dbConn);
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
  public String getFormPrintInfo(HttpServletRequest request,
  HttpServletResponse response) throws Exception{
    String sRunId = request.getParameter("runId");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(sRunId);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9ORM orm = new T9ORM();
      HashMap map = new HashMap();
      map.put("RUN_ID", runId);
      T9FlowRun flowRun = (T9FlowRun) orm.loadObjSingle(dbConn, T9FlowRun.class, map);
      String runName = T9WorkFlowUtility.getRunName(flowRun.getRunName());
      String[] ss = T9Utility.null2Empty(flowRun.getAipFiles()).split("\n");
      String s = "[";
      for (String s1 : ss) {
        if ("".equals(s1)) {
          continue;
        }
        String[] tmp = s1.split(":");
        String query = "select T_NAME FROM FLOW_PRINT_TPL where SEQ_ID='"+tmp[0]+"'";
        Statement stm = null;
        ResultSet rs = null;
        String name = "";
        try {
          stm = dbConn.createStatement();
          rs = stm.executeQuery(query);
          if (rs.next()) {
            name = rs.getString("T_NAME");
          }
        } catch(Exception ex) {
          throw ex;
        } finally {
          T9DBUtility.close(stm, rs, null); 
        }
        s += "{name:'"+name+"' , value:'"+ tmp[1] +"'},";
      }
      if (s.endsWith(",")) {
        s = s.substring(0, s.length() - 1);
      }
      s += "]";
      T9FlowRunUtility util = new T9FlowRunUtility();
      int parentFlowId = 0 ;
      if (flowRun.getParentRun() != 0) {
        parentFlowId = util.getFlowId(dbConn, flowRun.getParentRun());
      }
      String childRun = util.getChildRun(dbConn, runId);
      T9MoreOperateLogic opt = new T9MoreOperateLogic();
      
      String viewUser = flowRun.getViewUser();
      if (!T9WorkFlowUtility.findId(viewUser, String.valueOf(loginUser.getSeqId()))) {
        viewUser = "";
      }
      
      String data = "{runName:'"+  runName +"',flowId:"+ flowRun.getFlowId() +",delFlag:"+flowRun.getDelFlag()+",aipFile:"+ s + ",parentFlowId:"+ parentFlowId +",parentRunId:"+ flowRun.getParentRun() + ",childRun:"+ childRun +",opts:" + opt.getFlowAction(dbConn, loginUser.getUserPriv(),loginUser.getUserPrivOther()) + ",viewUser:'"+viewUser+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public boolean getAip(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
        String sRunId = request.getParameter("runId");
        String flowId = request.getParameter("flowId");
        Connection dbConn = null;
        try{
          T9RequestDbConn requestDbConn = (T9RequestDbConn) request
              .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
          dbConn = requestDbConn.getSysDbConn();
          int runId = Integer.parseInt(sRunId);
          T9ORM orm = new T9ORM();
          HashMap map = new HashMap();
          map.put("RUN_ID", runId);
          T9FlowRun flowRun = (T9FlowRun) orm.loadObjSingle(dbConn, T9FlowRun.class, map);
          String ss = T9Utility.null2Empty(flowRun.getAipFiles());
          T9Person user = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
           //T9FlowPrintLogic logic=new T9FlowPrintLogic();
          String data =this.getTempOptionLogic(dbConn,user,flowId,sRunId);
          if ("".equals(ss) && "".equals(data)) {
            return false;
          } else {
            return true;
          }
        }catch(Exception ex){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
          throw ex;
        }
      }
  public String getFormPrintMsg(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String flowView = request.getParameter("flowView");
    String sRunId = request.getParameter("runId");
    String sFlowId = request.getParameter("flowId");
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowRunLogic frl = new T9FlowRunLogic();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowRun flowRun = frl.getFlowRunByRunId(Integer.parseInt(sRunId) , dbConn);
      T9FlowTypeLogic ftl = new T9FlowTypeLogic();
      T9FlowType ft = ftl.getFlowTypeById(Integer.parseInt(sFlowId),dbConn);
      StringBuffer sb = new StringBuffer("{");
      T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");
      String imgPath = T9WorkFlowUtility.getImgPath(request);
      String isAutoPrint = T9Utility.null2Empty(request.getParameter("isAutoPrint"));
      Map form =  frl.getPrintForm(user, flowRun , ft , false, dbConn , imgPath ,isAutoPrint) ;
      sb.append("js:'" + form.get("script") + "'") ;
      sb.append(",css:'" + form.get("css") + "'") ;
      if(flowView.indexOf("1") != -1){
        sb.append(",form:'" + (String)form.get("form")+ "'");
      }
      if(flowView.indexOf("2") != -1){
        if(sb.length() != 1){
          sb.append(",");
        }
        T9AttachmentLogic attachLogic = new T9AttachmentLogic();
        sb.append("attachment:["+ attachLogic.getAttachments(loginUser, flowRun.getRunId() , Integer.parseInt(sFlowId) , dbConn) +"]");
      }
      if(flowView.indexOf("3") != -1){
        if(sb.length() != 1){
          sb.append(",");
        }
        T9FeedbackLogic feedbackLogic = new T9FeedbackLogic();
        String feedbacks = feedbackLogic.getFeedbacks(loginUser, flowRun.getFlowId() , flowRun.getRunId() ,dbConn);
        sb.append("feedbacks:" + feedbacks);
      }
      if(flowView.indexOf("4") != -1){
        if(sb.length() != 1){
          sb.append(",");
        }
        T9MyWorkLogic workLogic = new T9MyWorkLogic();
        workLogic.getPrcsList(flowRun.getRunId(), ft , dbConn , sb);
      }
      
      if (T9WorkFlowUtility.findId(flowRun.getViewUser(), String.valueOf(user.getSeqId()))) {
        //写日志
        T9MyWorkLogic workLogic = new T9MyWorkLogic();
        workLogic.writeViewLog(dbConn, loginUser.getSeqId(), flowRun.getRunId());
        if(flowView.indexOf("5") != -1){
          if(sb.length() != 1){
            sb.append(",");
          }
          workLogic.getViewUser(flowRun, dbConn, sb);
        }
      }
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getWordAndHtml(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String flowView = request.getParameter("flowView");
    String sRunId = request.getParameter("runId");
    String sFlowId = request.getParameter("flowId");
    String ext = request.getParameter("ext");//扩展名
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowRunLogic frl = new T9FlowRunLogic();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowRun flowRun = frl.getFlowRunByRunId(Integer.parseInt(sRunId) , dbConn);
      T9FlowTypeLogic ftl = new T9FlowTypeLogic();
      T9FlowType ft = ftl.getFlowTypeById(Integer.parseInt(sFlowId),dbConn);
      if(flowView.indexOf("1") != -1){
        T9Person user = (T9Person) request.getSession().getAttribute("LOGIN_USER");
        String imgPath = T9WorkFlowUtility.getImgPath(request);
        Map result = frl.getPrintForm(user, flowRun, ft ,true, dbConn , imgPath , "") ;
        String  form = (String)result.get("form");
        form = form.replaceAll("\\\\\"", "\"");
        request.setAttribute("form", form);
      }
      if(flowView.indexOf("2") != -1){
        T9AttachmentLogic attachLogic = new T9AttachmentLogic();
        String attachment = attachLogic.getAttachmentsHtml(loginUser, flowRun.getRunId() , dbConn);
        request.setAttribute("attachment", attachment);
      }
      if(flowView.indexOf("3") != -1){
        T9FeedbackLogic feedbackLogic = new T9FeedbackLogic();
        String feedbacks = feedbackLogic.getFeedbacksHtml(loginUser, flowRun.getFlowId() , flowRun.getRunId() ,dbConn);
        request.setAttribute("feedback", feedbacks);
      }
      if(flowView.indexOf("4") != -1){
        T9MyWorkLogic workLogic = new T9MyWorkLogic();
        String prcs =  workLogic.getPrcsHtml(flowRun.getRunId(), ft , dbConn );
        request.setAttribute("prcs", prcs);
      }
      request.setAttribute("ext", ext);
      request.setAttribute("runName", flowRun.getRunName());
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/workflow/flowrun/list/print/wordAndHtml.jsp";
  }
  public String getTempOptionLogic(Connection conn,T9Person person,String flowId,String runId) throws Exception{
    Statement stmt=null;
    ResultSet rs=null;
    Statement stmt1=null;
    ResultSet rs1=null;
    String data="";
  try{
    String sql = "select SEQ_ID,T_NAME,FLOW_PRCS FROM FLOW_PRINT_TPL WHERE FLOW_ID='"+flowId+"' and T_TYPE = '1'";
   stmt=conn.createStatement();
   rs=stmt.executeQuery(sql);
    while(rs.next())
    {
        String flowPrcs = rs.getString("FLOW_PRCS");
        int seqId=rs.getInt("seq_id");
        String tName=rs.getString("t_name");
        if(T9Utility.isNullorEmpty(flowPrcs)){
          flowPrcs = "0";
        }
         if(flowPrcs.endsWith(",")){
           flowPrcs=flowPrcs.substring(0, flowPrcs.length()-1);
         }
       sql = "select * from FLOW_RUN_PRCS WHERE RUN_ID='"+runId+"' and USER_ID='"+person.getSeqId()+"' and FLOW_PRCS IN ("+flowPrcs+")";
        stmt1=conn.createStatement();
        rs1=stmt1.executeQuery(sql);
        if(rs1.next()){
          data+="{seqId:'"+seqId+"',tName:'"+tName+"'}";
          data+=",";
        }
          
    }     
  }catch(Exception ex){
    ex.printStackTrace();
  }finally{
    T9DBUtility.close(stmt, rs, null);
    T9DBUtility.close(stmt1, rs1, null);
  }
  if(data.endsWith(",")){
    data=data.substring(0,data.length()-1);
  }
  
 return data;
}
}
