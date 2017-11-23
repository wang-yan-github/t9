package t9.core.funcs.doc.send.act;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.data.T9DocFlowFormItem;
import t9.core.funcs.doc.data.T9DocFlowRunData;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowRunLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.receive.act.T9DocReceiveHandlerAct;
import t9.core.funcs.doc.receive.data.T9DocConst;
import t9.core.funcs.doc.send.data.T9DocFlowRun;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.funcs.doc.send.logic.T9DocSendLogic;
import t9.core.funcs.doc.util.T9FlowRunUtility;
import t9.core.funcs.doc.util.T9WorkFlowConst;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUtility;
import t9.subsys.oa.rollmanage.data.T9RmsFile;
import t9.subsys.oa.rollmanage.logic.T9RmsFileLogic;

/**
 * ddddd
 * @author liuhan
 *
 */
public class T9DocAct {
  
  private T9RmsFileLogic logic = new T9RmsFileLogic();
  
  
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.send.act.T9DocAct");
  public String reNameDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String realPath = request.getRealPath("/");
      int runId = Integer.parseInt(request.getParameter("runId"));
      String docName = request.getParameter("docName");
      T9DocLogic logic = new T9DocLogic();
      String docId = logic.reNameAttachment(runId, docName, dbConn , realPath);
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
  public String hasSend(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String realPath = request.getRealPath("/");
      String flowIdStr = request.getParameter("flowId");
      String runIdStr = request.getParameter("runId");
      String flowPrcsStr = request.getParameter("flowPrcs");
      int runId = Integer.parseInt(runIdStr);
      int flowId = Integer.parseInt(flowIdStr);
      int flowPrcs = Integer.parseInt(flowPrcsStr);
      T9DocSendLogic logic = new T9DocSendLogic();
      if (!logic.hasSend(dbConn, runId, flowId, flowPrcs)) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      } else {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String saveDocCreateTime(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqIdStr = request.getParameter("seqId");
    String sRunId = request.getParameter("runId");
    int runId = 0 ;
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
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocLogic logic = new T9DocLogic();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      if (T9Utility.isNullorEmpty(seqIdStr)) {
        seqIdStr = logic.getFeedback(dbConn, runId, flowId, prcsId, user.getSeqId()) + "";
      } 
      logic.saveCreateTime(Integer.parseInt(seqIdStr), dbConn, runId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功 ");
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
      
      T9DocLogic logic = new T9DocLogic();
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
  public String getDocModule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocLogic logic = new T9DocLogic();
      String root = request.getRealPath("/");
      String docStyle = logic.getDocStyle(root);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, docStyle);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getBookmark(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      String root = request.getRealPath("/");
      String docStyle = logic.getDocStyle1(root , runId , dbConn);
      
      String content = logic.getContentStyle(root);
      String style = logic.getStyle(runId ,dbConn );
      String str = "{docStyle:"+ docStyle +",style:"+ style +",content:"+content+"}";
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
  public String getFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocLogic logic = new T9DocLogic();
      String root = request.getRealPath("/");
      String sb = "[";
      String query = "select flow_type.seq_Id , flow_type.flow_name from "+T9WorkFlowConst.FLOW_TYPE +" flow_type , DOC_FLOW_SORT" 
        + " where " 
        + " DOC_FLOW_SORT.SEQ_ID = FLOW_TYPE.FLOW_SORT  " 
        + " AND DOC_FLOW_SORT.SORT_NAME='"+T9DocConst.getProp(root, T9DocConst.DOC_SEND_FLOW_SORT)+"'";
      Statement stm = null;
      ResultSet rs = null;
      try {
        stm = dbConn.createStatement();
        rs = stm.executeQuery(query);
        while (rs.next()) {
          int seqId = rs.getInt("SEQ_ID");
          String flowName = rs.getString("FLOW_NAME");
          sb += "{seqId:'"+seqId+"' , flowName:'"+ flowName +"'},";
        } 
      } catch (Exception ex) {
        throw ex;
      } finally {
        T9DBUtility.close(stm, rs, null);
      }
      sb = T9WorkFlowUtility.getOutOfTail(sb);
      sb += "]";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, sb);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getRunData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      String root = request.getRealPath("/");
      String doc = logic.getBookmark(runId, dbConn);
      String str = "{runData:" + doc + "}";
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
  
  public String delDoc(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      logic.delDoc(runId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功 ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delDoc1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      logic.delDoc1(runId, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功 ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String saveDocStyle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      String docStyle = request.getParameter("docStyle");
      T9DocLogic logic = new T9DocLogic();
      logic.saveDocStyle(runId , docStyle , dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功 ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String loadRollData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      int prcsId = Integer.parseInt(request.getParameter("prcsId"));
      //生成附件
      T9DocLogic logic = new T9DocLogic();
      String doc = logic.getPigeonholeData(runId, dbConn);
      String docStr = logic.getDoc(runId, dbConn , true);
      String imgPath = T9WorkFlowUtility.getImgPath(request);
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String attachmentStr = logic.pigeonholeAttachment(runId , dbConn ,loginUser, imgPath);
      String handler = logic.getHandlerTime(runId , prcsId , dbConn) ;
      String str = "{"
        + "runData:" + doc 
        + ",doc:" + docStr 
        + ",attachment:" + attachmentStr 
        + ",handlerTime:'"+ handler +"'}";
      
      //加载流程数据
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
  /**
   * 取得归档的状态
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRollState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sRunId = request.getParameter("runId");
      String sFlowId = request.getParameter("flowId");
      String sPrcsId = request.getParameter("flowPrcs");
      int prcsId = 0 ;
      int flowId = 0 ;
      int runId = 0 ;
      if (T9Utility.isInteger(sRunId)) {
        runId = Integer.parseInt(sRunId);
      }
      if (T9Utility.isInteger(sFlowId)) {
        flowId = Integer.parseInt(sFlowId);
      }
      if (T9Utility.isInteger(sPrcsId)) {
        prcsId = Integer.parseInt(sPrcsId);
      }
      T9DocLogic logic = new T9DocLogic();
      boolean flag = logic.getRollState(runId, prcsId, flowId, dbConn);
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, flag + "");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public String roll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      String  docName  = request.getParameter("docName");
      String docId = request.getParameter("docId");
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      

      String fileCode = (String) request.getParameter("fileCode");
      String fileSubject = (String) request.getParameter("fileSubject");
      String fileTitle = (String) request.getParameter("fileTitle");

      String fileTitleo = (String) request.getParameter("fileTitleo");
      String sendUnit = (String) request.getParameter("sendUnit");
      String sendDate = (String) request.getParameter("sendDate");
      String secret = (String) request.getParameter("secret");
      String urgency = (String) request.getParameter("urgency");
      String fileType = (String) request.getParameter("fileType");
      String fileKind = (String) request.getParameter("fileKind");
      String filePage = (String) request.getParameter("filePage");
      String printPage = (String) request.getParameter("printPage");
      String remark = (String) request.getParameter("remark");
      String rollIdStr = (String) request.getParameter("rollId");
      String downloadYnStr = (String) request.getParameter("downloadYn");
      String handlerTime = (String)request.getParameter("handlerTime");
      String turnCount = (String)request.getParameter("turnCount");
      String fileWord = T9Utility.null2Empty((String)request.getParameter("fileWord"));
      String fileYear = T9Utility.null2Empty((String)request.getParameter("fileYear"));
      String issueNum = T9Utility.null2Empty((String)request.getParameter("issueNum"));
      
      int rollId = 0;
      int downloadYn = 0;
      if (!T9Utility.isNullorEmpty(rollIdStr)) {
        rollId = Integer.parseInt(rollIdStr);
      }
      if (!T9Utility.isNullorEmpty(downloadYnStr)) {
        downloadYn = Integer.parseInt(downloadYnStr);
      }


      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

      // 保存从文件柜、网络硬盘选择附件
      T9SelAttachUtil sel = new T9SelAttachUtil(request, "roll_manage");
      String attIdStr = sel.getAttachIdToString(",");
      String attNameStr = sel.getAttachNameToString("*");

      boolean fromFolderFlag = false;
      String newAttchId = "";
      String newAttchName = "";
      if (!"".equals(attIdStr) && !"".equals(attNameStr)) {
        newAttchId = attIdStr + ",";
        newAttchName = attNameStr + "*";
        fromFolderFlag = true;

      }
      T9RmsFile rmsFile = new T9RmsFile();
      rmsFile.setAttachmentId(attachmentId);
      rmsFile.setAttachmentName(attachmentName);
      rmsFile.setDocAttachmentId(docId);
      rmsFile.setDocAttachmentName(docName);

      rmsFile.setAddUser(String.valueOf(person.getSeqId()));
      rmsFile.setAddTime(T9Utility.parseTimeStamp());
      rmsFile.setFileCode(fileCode);

      rmsFile.setFileTitle(fileTitle);
      rmsFile.setFileTitleo(fileTitleo);
      rmsFile.setFileSubject(fileSubject);

      rmsFile.setSendUnit(sendUnit);
      rmsFile.setSendDate(T9Utility.parseDate(sendDate));
      rmsFile.setSecret(secret);
      rmsFile.setUrgency(urgency);
      rmsFile.setFileKind(fileKind);

      rmsFile.setFileType(fileType);
      rmsFile.setFilePage(filePage);
      rmsFile.setPrintPage(printPage);
      rmsFile.setRemark(remark);
      rmsFile.setRollId(rollId);
      rmsFile.setDownloadYn(downloadYn);
      rmsFile.setHandlerTime(handlerTime);
      rmsFile.setTurnCount(turnCount);
      rmsFile.setFileWord(fileWord);
      rmsFile.setFileYear(fileYear);
      rmsFile.setIssueNum(issueNum);
      
      this.logic.addRmsFileInfo(dbConn, rmsFile);
      T9DocLogic logic = new T9DocLogic();
      logic.updateFlowRun(runId, dbConn);
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }  
  public String getDocType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String flag = request.getParameter("flag");
      //是否需要验证权限
      boolean hasRight = true;
      if ("1".equals(flag)) {
        hasRight = false;
      }
      
      T9DocLogic logic = new T9DocLogic();
      String data = logic.getDocType(person, dbConn , hasRight);
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public String getDocWordByType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9DocLogic logic = new T9DocLogic();
      String type = request.getParameter("type");
      String flag = request.getParameter("flag");
      //是否需要验证权限
      boolean hasRight = true;
      if ("1".equals(flag)) {
        hasRight = false;
      }
      String data = logic.getDocWordByType(person, dbConn, type , hasRight);
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public String getWord(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String wordKey = request.getParameter("wordKey");
      if (wordKey == null) {
        wordKey = "";
      }
      String realPath = request.getRealPath("/");
      String path = realPath + "/subsys/inforesource/docmgr/sendManage/selectWord/js/keyword.txt";
      List<String> list = new ArrayList();
      T9FileUtility.loadLine2Array(path, list);
      StringBuffer sb = new StringBuffer();
      sb.append("[");
      int count = 0 ;
      for (String s : list) {
        if ("".equals(wordKey) || s.indexOf(wordKey) != -1 ) {
          sb.append("\"" + T9Utility.encodeSpecial(s) + "\",");
          count++;
        }
      }
      if (count > 0 ) {
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      //加载流程数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  } 
  public String getDocNum(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int runId = Integer.parseInt(request.getParameter("runId"));
      T9DocLogic logic = new T9DocLogic();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      String str = logic.getDocNum(dbConn,runId , person );
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
  public String getNum(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String docWord1 = request.getParameter("docWord");
      int docWord = Integer.parseInt(docWord1);
      String year = request.getParameter("docYear");
      T9DocLogic logic = new T9DocLogic();
      int str = logic.getNum(dbConn, year, docWord);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      request.setAttribute(T9ActionKeys.RET_DATA, "" + str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String sendNum(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String docWord1 = request.getParameter("docWordSeqId");
      int docWord = Integer.parseInt(docWord1);
      String doc = request.getParameter("doc");
      String year = request.getParameter("docYear");
      int runId = Integer.parseInt(request.getParameter("runId"));
      int docNum = Integer.parseInt(request.getParameter("docNum"));
      
      T9DocLogic logic = new T9DocLogic();
      if (logic.checkName(dbConn,doc) ) {
        logic.sendNum(dbConn, year, docWord , doc , runId , docNum , request.getRealPath("/"));
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, " ");
      } else {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_DATA, "true");
      }
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建一个工作
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String createWorkFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
   String flowName = request.getParameter("flowName");
   Connection dbConn = null;
    try {
      T9ORM orm = new T9ORM();
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowRunUtility fru = new T9FlowRunUtility();
      int flowId = Integer.parseInt(request.getParameter("flowId"));
      String docWord = request.getParameter("word");
      int wordId = Integer.parseInt(request.getParameter("wordId"));
      int docType = Integer.parseInt(request.getParameter("docType"));
      
      String docStyle = request.getParameter("docStyle");
      String flowRunName = docStyle.replace("${文件字}", docWord);
      String year = T9Utility.getCurDateTimeStr().split("-")[0];
      flowRunName = flowRunName.replace("${年号}", year);
     // flowRunName = flowRunName.replace("${文号}", "${文号}");
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DocFlowType flowType = flowTypeLogic.getFlowTypeById(flowId , dbConn);      
      //重名
      T9FlowRunLogic frl = new T9FlowRunLogic();
      synchronized(T9DocReceiveHandlerAct.loc) {
        int runId = frl.createNewWork(loginUser, flowType, flowRunName , dbConn );
        //flowRunName = flowRunName.replace("${文号}", "${预分配文号"+runId+"}");
        
        String query = "delete from doc_flow_run where RUN_ID='" + runId + "'";
        T9WorkFlowUtility.updateTableBySql(query, dbConn);
        T9DocFlowRun doc = new T9DocFlowRun(runId, flowRunName,year, wordId,
            new Date(),  docType);
        orm.saveSingle(dbConn, doc);
        
        frl.updateRunName(flowRunName, runId, dbConn);
        Map queryItem = new HashMap();
        queryItem.put("FORM_ID", flowType.getFormSeqId());
        
        List<T9DocFlowFormItem> list = orm.loadListSingle(dbConn, T9DocFlowFormItem.class, queryItem);
        for(T9DocFlowFormItem tmp : list){
          int itemId = tmp.getItemId();
          String itemData = "";
          if (!"AUTO".equals(tmp.getClazz())) {
            itemData = tmp.getValue();
          }
          String value = (String)request.getParameter(tmp.getTitle());
          if (!T9Utility.isNullorEmpty(value)) {
            itemData = value;
          }
          if ("文件字".equals(tmp.getTitle())) {
            itemData = docWord;
          }
          if (itemData != null) {
            Map queryMap = new HashMap();
            queryMap.put("RUN_ID", runId);
            queryMap.put("ITEM_ID", itemId);
            T9DocFlowRunData flowRunData = (T9DocFlowRunData) orm.loadObjSingle(dbConn, T9DocFlowRunData.class, queryMap);
            if(flowRunData != null){
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.updateSingle(dbConn, flowRunData);
            }else{
              flowRunData =  new T9DocFlowRunData();
              flowRunData.setItemId(itemId);
              flowRunData.setRunId(runId);
              flowRunData.setItemData((itemData == null ? "" : itemData));
              orm.saveSingle(dbConn, flowRunData);
            }
          }
        }
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "新建成功!");
        request.setAttribute(T9ActionKeys.RET_DATA, "{runId:" + runId + ",flowId:" + flowId + "}");
        dbConn.commit();
      } 
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
