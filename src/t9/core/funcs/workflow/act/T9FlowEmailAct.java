package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
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

public class T9FlowEmailAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9FlowEmailAct");
    public String getEmailMsg(HttpServletRequest request,
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
        if (logic.hasModulePriv(userPrivs, T9MoreOperateLogic.EMAIL_MENU_ID)) {
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
    public String saveEmail(HttpServletRequest request,
        HttpServletResponse response) throws Exception{
      String runId = request.getParameter("RUN_ID");
      String flowView = T9Utility.null2Empty(request.getParameter("FLOW_VIEW"));
      String toId = T9Utility.null2Empty(request.getParameter("TO_ID"));
      String copyToId =  T9Utility.null2Empty(request.getParameter("COPY_TO_ID"));
      String secretToId = T9Utility.null2Empty( request.getParameter("SECRET_TO_ID"));
      
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        String imgPath = T9WorkFlowUtility.getImgPath(request);
        T9MyWorkLogic myworklogic = new T9MyWorkLogic();
        T9FlowRunLogic frl = new T9FlowRunLogic();
        T9FlowTypeLogic tl = new T9FlowTypeLogic();
        T9FlowRun flowRun = frl.getFlowRunByRunId(Integer.parseInt(runId) , dbConn);
        T9FlowType flowType = tl.getFlowTypeById(flowRun.getFlowId(), dbConn);
        
        String html = myworklogic.getFlowRunHtml(flowRun, dbConn, person, imgPath ,flowView.replace("2", ""));
        boolean flag = true;
        if ("1".equals(flowType.getFlowType())) {
           flag = logic.hasAttachDownPriv(dbConn, flowRun.getFlowId(), flowRun.getRunId(), person.getSeqId());
        }
        
        logic.saveEmail(dbConn, flowRun, html, person, toId, copyToId, secretToId, flowView , flag);
        
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "邮件发送成功！");
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
}
