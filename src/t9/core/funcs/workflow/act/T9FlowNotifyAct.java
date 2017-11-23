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
import t9.core.funcs.workflow.logic.T9ConfigLogic;
import t9.core.funcs.workflow.logic.T9FlowRunLogic;
import t9.core.funcs.workflow.logic.T9MoreOperateLogic;
import t9.core.funcs.workflow.logic.T9MyWorkLogic;
import t9.core.funcs.workflow.util.T9FlowRunUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9FlowNotifyAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9FlowNotifyAct");
    public String getNotifyMsg(HttpServletRequest request,
        HttpServletResponse response) throws Exception{
      Connection dbConn = null;
      try{
        String runId = request.getParameter("runId");
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
        List<String> userPrivs = logic.getUserPriv(dbConn, loginUser.getUserPriv(), loginUser.getUserPrivOther());
        
        String ss = "{userPriv:";
        if (logic.hasModulePriv(userPrivs, T9MoreOperateLogic.NOTIFY_MENU_ID)) {
          ss += "true,";
          T9ConfigLogic logic2 = new T9ConfigLogic();
          String paraValue = T9Utility.null2Empty(logic2.getSysPar("NOTIFY_AUDITING_SINGLE", dbConn));
          if (T9Utility.isNullorEmpty(paraValue)) {
            paraValue = "0";
          } 
          ss += "flag:" + paraValue + ",";
          T9FlowRunUtility util = new T9FlowRunUtility();
          ss += "runName:\"" +T9WorkFlowUtility.encodeSpecial(util.getRunNameById(Integer.parseInt(runId), dbConn)) + "\",";
          ss += "auditer:" + logic.getAuditingUser(dbConn);
          ss += ",typeData:" + logic.getNotifyType(dbConn);
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
    public String saveNotify(HttpServletRequest request,
        HttpServletResponse response) throws Exception{
      String subject = request.getParameter("SUBJECT"); 
      String flag = request.getParameter("PUBLISH");    
      String print = request.getParameter("PRINT");
      String download = request.getParameter("DOWNLOAD");
      String top = request.getParameter("TOP");
      String runId = request.getParameter("RUN_ID");
      String toId = T9Utility.null2Empty(request.getParameter("TO_ID"));
      String copyToId =  T9Utility.null2Empty(request.getParameter("COPY_TO_ID"));
      String privId = T9Utility.null2Empty(request.getParameter("PRIV_ID"));
      
      
      if(T9Utility.isNullorEmpty(print)){
        print = "0";
      }
      if(T9Utility.isNullorEmpty(top)){
        top = "0";
      }
      if(T9Utility.isNullorEmpty(download)){
        download = "0";
      }
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9MoreOperateLogic logic =  new T9MoreOperateLogic();
        T9Notify notify = new T9Notify();
        notify.setTop(top);
        notify.setPrint(print);
        notify.setDownload(download);
        notify.setToId(toId);
        notify.setSubject(subject);
        notify.setTypeId(T9Utility.null2Empty(request.getParameter("TYPE_ID")));
        notify.setAuditer(T9Utility.null2Empty(request.getParameter("AUDITER")));
        String imgPath = T9WorkFlowUtility.getImgPath(request);
        T9MyWorkLogic myworklogic = new T9MyWorkLogic();
        T9FlowRunLogic frl = new T9FlowRunLogic();
        T9FlowRun flowRun = frl.getFlowRunByRunId(Integer.parseInt(runId) , dbConn);
        String html = myworklogic.getFlowRunHtml(flowRun, dbConn, person, imgPath ,"13");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        
        if (T9Utility.isNullorEmpty(beginDate)) {
          notify.setBeginDate(null);
        } else {
          notify.setBeginDate(sdf.parse(beginDate));
        }
        if (T9Utility.isNullorEmpty(endDate)) {
          notify.setEndDate(null);
        } else {
          notify.setEndDate(sdf.parse(endDate));
        }
        notify.setFormat("0");
        notify.setPublish(flag);
        notify.setPrivId(privId);
        notify.setUserId(copyToId);
        
        String mailRemind = request.getParameter("mailRemind");
        String mobileRemind = request.getParameter("remind");
        logic.saveNotify(dbConn , notify , flowRun ,html , person , mailRemind , mobileRemind);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        if("0".equals(flag)){
          request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知保存成功");
        }
        if("1".equals(flag)){
          request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知发布成功");
        }
        if("2".equals(flag)){
          request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知提交审批成功");
        }
      } catch (Exception ex) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw ex;
      }
      return "/core/inc/rtjson.jsp";
    }
}
