package t9.core.funcs.workflow.act;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.send.logic.T9DocLogic;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowRun;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9ConfigLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9MoreOperateLogic;
import t9.core.funcs.workflow.logic.T9MyWorkLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.subsys.oa.rollmanage.data.T9RmsFile;

public class T9FlowRollAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9FlowRollAct");
    public String getRollMsg(HttpServletRequest request,
        HttpServletResponse response) throws Exception{
      Connection dbConn = null;
      try{
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
        List<String> userPrivs = logic.getUserPriv(dbConn, loginUser.getUserPriv(), loginUser.getUserPrivOther());
        
        String ss = "{userPriv:";
        if (logic.hasModulePriv(userPrivs, T9MoreOperateLogic.ROLL_MENU_ID)) {
          ss += "true";
        } else {
          ss += "false";
        }
        ss += "}";
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功取得数据");
        request.setAttribute(T9ActionKeys.RET_DATA, ss);
      }catch (Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        ex.printStackTrace();
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
    public String saveRoll(HttpServletRequest request,
        HttpServletResponse response) throws Exception{
      
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        T9FileUploadForm fileForm = new T9FileUploadForm();
        //注意这里的
        fileForm.parseUploadRequest(request);
        
        String oldId = T9Utility.null2Empty( fileForm.getParameter("ATTACHMENT_ID_OLD"));
        if (!oldId.endsWith(",")) {
          oldId += ",";
        }
        String oldName = T9Utility.null2Empty(  fileForm.getParameter("ATTACHMENT_NAME_OLD"));
        if (!oldName.endsWith(",")) {
          oldName += "*";
        }
        
        String[] atta = logic.saveAttachment(fileForm);
        atta[0] = oldId + atta[0];
        atta[1] = oldName + atta[1];
        
        String FILE_CODE = T9Utility.null2Empty(fileForm.getParameter("FILE_CODE"));
        String FILE_TITLE = T9Utility.null2Empty(fileForm.getParameter("FILE_TITLE"));
        String FILE_TITLE0 = T9Utility.null2Empty(fileForm.getParameter("FILE_TITLE0"));
        String FILE_SUBJECT = T9Utility.null2Empty(fileForm.getParameter("FILE_SUBJECT"));
        String SEND_UNIT = T9Utility.null2Empty(fileForm.getParameter("SEND_UNIT"));
        String SEND_DATE = T9Utility.null2Empty(fileForm.getParameter("SEND_DATE"));
        String SECRET = T9Utility.null2Empty(fileForm.getParameter("SECRET"));
        String URGENCY = T9Utility.null2Empty(fileForm.getParameter("URGENCY"));
        String FILE_KIND = T9Utility.null2Empty(fileForm.getParameter("FILE_KIND"));
        String FILE_TYPE = T9Utility.null2Empty(fileForm.getParameter("FILE_TYPE"));
        String FILE_PAGE = T9Utility.null2Empty(fileForm.getParameter("FILE_PAGE"));
        String PRINT_PAGE = T9Utility.null2Empty(fileForm.getParameter("PRINT_PAGE"));
        String REMARK = T9Utility.null2Empty(fileForm.getParameter("REMARK"));
        String ROLL_ID = T9Utility.null2Empty(fileForm.getParameter("rollId"));
        
        
        T9RmsFile rmsFile = new T9RmsFile();
        rmsFile.setAttachmentId(atta[0]);
        rmsFile.setAttachmentName( atta[1]);

        rmsFile.setAddUser(String.valueOf(person.getSeqId()));
        rmsFile.setAddTime(T9Utility.parseTimeStamp());
        rmsFile.setFileCode(FILE_CODE);

        rmsFile.setFileTitle(FILE_TITLE);
        rmsFile.setFileTitleo(FILE_TITLE0);
        rmsFile.setFileSubject(FILE_SUBJECT);

        rmsFile.setSendUnit(SEND_UNIT);
        rmsFile.setSendDate(T9Utility.parseDate(SEND_DATE));
        rmsFile.setSecret(SECRET);
        rmsFile.setUrgency(URGENCY);
        rmsFile.setFileKind(FILE_KIND);

        rmsFile.setFileType(FILE_TYPE);
        rmsFile.setFilePage(FILE_PAGE);
        rmsFile.setPrintPage(PRINT_PAGE);
        rmsFile.setRemark(REMARK);
        rmsFile.setRollId(Integer.parseInt(ROLL_ID));
        T9ORM orm = new T9ORM();
        orm.saveSingle(dbConn, rmsFile);
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw ex;
      }
      return "/core/funcs/workflow/flowrun/list/rollsucess.jsp";
    }
    public String loadRollData(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try{
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        int runId = Integer.parseInt(request.getParameter("runId"));
        //生成附件
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        
        String doc = logic.getFlowData(runId, dbConn);
        String imgPath = T9WorkFlowUtility.getImgPath(request);
        
        T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
        List<String> userPrivs = logic.getUserPriv(dbConn, loginUser.getUserPriv(), loginUser.getUserPrivOther());
        String ss = "{userPriv:";
        if (logic.hasModulePriv(userPrivs, T9MoreOperateLogic.ROLL_MENU_ID)) {
          ss += "true,";
          T9MyWorkLogic myworklogic = new T9MyWorkLogic();
          T9FlowRunLogic frl = new T9FlowRunLogic();
          T9FlowRun flowRun = frl.getFlowRunByRunId( runId , dbConn);
          String html = myworklogic.getFlowRunHtml(flowRun, dbConn, loginUser, imgPath ,"13");
          String[] sss = logic.storeFormToRoll(html, dbConn);
          String[] attach = logic.storeToRoll(runId, dbConn);
          
          attach[0] = sss[0] + "," + attach[0];
          attach[1] = sss[1] + "*" + attach[1];
          String str = "runData:" + doc 
              + ",attachmentId:\"" + attach[0]  + "\""
              + ",attachmentName:\"" +  T9WorkFlowUtility.encodeSpecial(attach[1]) + "\"";
          ss += str;
        } else {
          ss += "false";
        }
        ss += "}";
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, " ");
        request.setAttribute(T9ActionKeys.RET_DATA, ss);
      } catch (Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }  
}
