package t9.subsys.inforesouce.docmgr.act;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowRunData;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.subsys.inforesouce.docmgr.data.T9DocFlowRun;
import t9.subsys.inforesouce.docmgr.logic.T9Doc2Logic;
import t9.subsys.inforesouce.docmgr.logic.T9DocLogic;
import t9.subsys.oa.rollmanage.data.T9RmsFile;
import t9.subsys.oa.rollmanage.logic.T9RmsFileLogic;

public class T9Doc2Act {
  private static Logger log = Logger
    .getLogger("t9.subsys.inforesouce.docmgr.act.T9DocAct");
  public String saveDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String runId = request.getParameter("runId");
      String prcsId = request.getParameter("prcsId");
      String flowPrcs = request.getParameter("flowPrcs");
      String docContent = request.getParameter("docContent");
      
      T9Doc2Logic logic = new T9Doc2Logic();
      logic.saveDoc(dbConn, loginUser.getSeqId(), runId, docContent, prcsId, flowPrcs);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getDocHistory(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String runId = request.getParameter("runId");
      
      T9Doc2Logic logic = new T9Doc2Logic();
      String str = logic.getDocHistory(dbConn, runId, loginUser.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delDocHistory(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9Doc2Logic logic = new T9Doc2Logic();
      logic.delDocHistory(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String createDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String realPath = request.getRealPath("/");
      int runId = Integer.parseInt(request.getParameter("runId"));
      String docName = request.getParameter("docName");
      T9Doc2Logic logic = new T9Doc2Logic();
      String docStyle = "国-函模板.doc";
      if (docName.indexOf("报告") != -1) {
        docStyle = "国-报告——上行文用的模板.doc";
      } else if (docName.indexOf("函") != -1) {
        docStyle = "国-函模板.doc";
      }else if (docName.indexOf("会议纪要") != -1) {
        docStyle = "国-会议纪要模板.doc";
      }else if (docName.indexOf("决定") != -1) {
        docStyle = "国-决定模板.doc";
      }else if (docName.indexOf("批复") != -1) {
        docStyle = "国-批复模板.doc";
      }else if (docName.indexOf("请示") != -1) {
        docStyle = "国-请示模板.doc";
      }else if (docName.indexOf("通知") != -1) {
        docStyle = "国-通知模板.doc";
      }
      String docId = logic.createAttachment(runId, docName, dbConn , realPath , docStyle);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功 ");
      request.setAttribute(T9ActionKeys.RET_DATA, docId);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getContent(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      T9Doc2Logic logic2 = new T9Doc2Logic();
      String style = logic.getStyle(runId ,dbConn );
      String content = logic2.getContent(dbConn , runId);
      String str = "{style:"+ style +",content:\""+content+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      int runId = Integer.parseInt(request.getParameter("runId"));
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      int flowPrcs = Integer.parseInt(request.getParameter("flowPrcs"));
      
      T9Doc2Logic logic = new T9Doc2Logic();
      String doc = logic.getDoc(runId, flowPrcs , flowId, dbConn);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, doc);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
