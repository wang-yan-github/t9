package t9.core.funcs.workflow.act;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class T9FlowSaveFileAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9FlowSaveFileAct");
  public String toSaveFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9MoreOperateLogic logic =  new T9MoreOperateLogic();
      String runId = request.getParameter("runId");
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowRunLogic frl = new T9FlowRunLogic();
      T9FlowTypeLogic tl = new T9FlowTypeLogic();
      T9FlowRun flowRun = frl.getFlowRunByRunId(Integer.parseInt(runId) , dbConn);
      T9FlowType flowType = tl.getFlowTypeById(flowRun.getFlowId(), dbConn);
      boolean flag = true;
      if ("1".equals(flowType.getFlowType())) {
         flag = logic.hasAttachDownPriv(dbConn, flowRun.getFlowId(), flowRun.getRunId(), loginUser.getSeqId());
      }
      String imgPath = T9WorkFlowUtility.getImgPath(request);
      T9MyWorkLogic myworklogic = new T9MyWorkLogic();
      String html = myworklogic.getFlowRunHtml(flowRun, dbConn, loginUser, imgPath ,"13");
      String[] chars = "/,\\,*,:,?,\",<,>,|,\r,\n".split(",");
      InputStream isb = new ByteArrayInputStream(html.getBytes("UTF-8"));
      String runName = flowRun.getRunName();
      for (String ch : chars) {
        runName = runName.replace(ch, "");
      }
      String fileName =   runName + ".html";
      Map map = new HashMap();
      map.put(fileName, isb);
      List<File> list = myworklogic.getAttachement(flowRun.getAttachmentId(), flowRun.getAttachmentName()) ;
      for (int i = 0; i < list.size(); i++) {
        File file = list.get(i);
        InputStream in = new FileInputStream(file);
        String tmp = file.getName();
        if (T9WorkFlowUtility.isOffice(tmp) && !flag) {
          continue;
        }
        int index = tmp.indexOf("_") + 1;
        tmp = tmp.substring(index);
        map.put(tmp, in);
      }
      T9WorkFlowUtility util  = new T9WorkFlowUtility();
      SimpleDateFormat sdf = new SimpleDateFormat("yy-M-d");
      String ss = runName + "("+sdf.format(new Date())+").zip";
      String[] newAtta = util.getNewAttachPath(ss, "workflow");
      
      Set<String> key = map.keySet();
      org.apache.tools.zip.ZipOutputStream zipout = new org.apache.tools.zip.ZipOutputStream(new File(newAtta[1]));
      zipout.setEncoding("GBK");
      for (String tmp2 : key) {
        InputStream in = (InputStream) map.get(tmp2);
        T9MyWorkLogic.output(in, zipout, tmp2);
      }
      zipout.flush();
      zipout.close();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "{attachId:\""+newAtta[0]+"\",attachName:\""+T9Utility.encodeSpecial(ss)+"\"}");
      return "/core/inc/rtjson.jsp";
      //response.sendRedirect(request.getContextPath() + "/core/funcs/savefile/index.jsp?attachId=&attachName="+ URLEncoder.encode(ss) +"&module=workflow");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }
}
