package t9.core.funcs.workplan.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.workplan.data.T9WorkPlanCont;
import t9.core.funcs.workplan.logic.T9WorkPlanManageLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;

import t9.core.util.file.T9FileUploadForm;


public class T9WorkPlanHandleAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  /**
   * 附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String fileLoad(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    PrintWriter pw = null;
    request.setCharacterEncoding(T9Const.DEFAULT_CODE);
    response.setCharacterEncoding(T9Const.DEFAULT_CODE);
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9WorkPlanManageLogic workPlanManageLogic = new T9WorkPlanManageLogic();
      StringBuffer sb = workPlanManageLogic.uploadMsrg2Json(fileForm);
      String data = "{'state':'0','data':" + sb.toString() + "}";
      pw = response.getWriter();
      pw.println(data.trim());
      pw.flush();
    }catch(Exception e){
      pw = response.getWriter();
      pw.println("{'state':'1'}".trim());
      pw.flush();
    } finally {
      pw.close();
    }
    return null;
  }
  
  /**
   * 单文件附件上传

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {

    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    try{
      T9WorkPlanManageLogic workPlanManageLogic = new T9WorkPlanManageLogic();
      attr = workPlanManageLogic.fileUploadLogic(fileForm);
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }
}
