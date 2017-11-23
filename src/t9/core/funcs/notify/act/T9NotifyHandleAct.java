package t9.core.funcs.notify.act;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.notify.data.T9NotifyCont;
import t9.core.funcs.notify.logic.T9NotifyManageLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.file.T9FileUtility;
import t9.core.util.form.T9FOM;

public class T9NotifyHandleAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  private T9SysLogLogic logLogic = new T9SysLogLogic();
  private T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
  public String beforeAddNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      String data = notifyManageLogic.beforeAddnotify(dbConn,person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String addNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String urlAdd = request.getParameter("urlAdd"); 
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map paramMap = fileForm.getParamMap(); 
    String subjectFont = fileForm.getParameter("subjectFont");
    String subject = fileForm.getParameter("subject"); 
    String flag = (String)paramMap.get("publish");    
    if("2".equalsIgnoreCase(flag)){
      paramMap.put("subject", URLDecoder.decode(subject, T9Const.DEFAULT_CODE));
      paramMap.put("content", URLDecoder.decode((String)paramMap.get("content"), T9Const.DEFAULT_CODE));
    }   
    
    if(paramMap.get("print")==null){
      paramMap.put("print", "0");
    }
    if(paramMap.get("download") == null){
      paramMap.put("download", "0");
    }
    String publish = null;
    try{
      publish = ((String[]) paramMap.get("publish"))[0];
    }catch(Exception e){
      publish = (String) paramMap.get("publish");
    }
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      notifyManageLogic.saveMailLogic(dbConn, fileForm,person,T9SysProps.getAttachPath(), publish, subjectFont);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      if("0".equals(publish)){
        request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知保存成功");
      }
      if("1".equals(publish)){
        request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知发布成功");
      }
      if("2".equals(publish)){
        request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知提交审批成功");
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw ex;
    }
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/core/funcs/notify/manage/notifySaveOk.jsp?publish="+publish;
  }
  
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
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      StringBuffer sb = notifyManageLogic.uploadMsrg2Json(fileForm);
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
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    try{
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      Map<String, String> attr = null;
      String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
      String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
      String data = "";
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      attr = notifyManageLogic.fileUploadLogic(fileForm);
      Set<String> keys = attr.keySet();
      for (String key : keys) {
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
    }catch (SizeLimitExceededException ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败：文件需要小于" + T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) + "兆");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
    }
    return "/core/inc/rtuploadfile.jsp";
  }
  /**
   * 存草稿
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveNotifyByUp(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection conn = null;
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    String urlAdd = request.getParameter("urlAdd");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int bId = -1 ;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9InnerEMailLogic ieml = new T9InnerEMailLogic();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      bId = notifyManageLogic.savettachMailLogic(conn, fileForm,person.getSeqId(),T9SysProps.getAttachPath());
      request.setAttribute("bId", bId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知保存成功！");
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "公告通知保存失败！" + e.getMessage());
      e.printStackTrace();
    }
    return "/core/funcs/notify/manage/notifyAdd.jsp?seqId="+ bId;
  }
  
  //分页取出公告列表数据
  public String getnotifyManagerList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
    Connection dbConn = null;
    String data = "";
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段

    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数    if(pageIndexStr == null || pageIndexStr==""){
      pageIndexStr ="1";
    }

    if("".equals(ascDesc)) {
      ascDesc = "1";
    }
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      data = notifyManageLogic.getnotifyManagerList(dbConn, loginUser, type,
             ascDesc, field, Integer.parseInt(showLenStr), Integer.parseInt(pageIndexStr));
      //T9Out.println(data+"****************");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出公告数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteAllNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    String ip = request.getRemoteAddr();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      boolean success =notifyManageLogic.deleteAllNotify(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv,ip);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  public String deleteCheckNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String pageIndex = request.getParameter("pageIndex");
    String showLength = request.getParameter("showLength");
    String deleteStr = request.getParameter("delete_str");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    String ip = request.getRemoteAddr();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      boolean success =notifyManageLogic.deleteCheckNotify(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv, deleteStr,ip);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String queryNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String data = "";
//    int pageIndex = 1;
//    int showLength = 10;
    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    String stat = request.getParameter("stat");
    if(stat == null || stat.length() <1){
      stat = "";
    }
    T9Notify notify = (T9Notify)T9FOM.build(request.getParameterMap());
//    String pageIndexStr = request.getParameter("pageIndex");
//    String showLengthStr = request.getParameter("showLength");
//    if(!"".equals(pageIndexStr)&&pageIndexStr!=null){
//      pageIndex = Integer.parseInt(pageIndexStr);
//    }
//    if(!"".equals(showLengthStr)&&showLengthStr!=null){
//      showLength = Integer.parseInt(showLengthStr);
//    }
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyMangerLogic = new T9NotifyManageLogic();
      data = notifyMangerLogic.queryNotify(dbConn,notify,person,beginDate,endDate,stat);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage().substring(ex.getMessage().lastIndexOf(":")+1,ex.getMessage().length()),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String editNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Notify notify = null;
    String data = "";
    String seqId = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Statement st = null;
      ResultSet rs = null;
      T9ORM orm = new T9ORM();
      notify = (T9Notify)orm.loadObjSingle(dbConn, T9Notify.class, Integer.parseInt(seqId));
      
      if (notify == null) {
        notify = new T9Notify();
      }else if (!T9Utility.null2Empty(notify.getFormat()).equals("2")) {
        byte[] byteContent = notify.getCompressContent();
        if (byteContent == null) {
          notify.setContent("");
        }else {
          notify.setContent(new String(byteContent, "UTF-8"));
        }
      }
      
      data = notify.toJSON();
//      StringBuffer sb = new StringBuffer(data);
//      sb.deleteCharAt(data.length()-1);
//      sb.append(",attachList:[");
//      String attachmentId = notify.getAttachmentId();
//      String attachmentName = notify.getAttachmentName();
//      List attachmentList = new ArrayList();
//      if(!"".equals(attachmentId)&&attachmentId!=null) {
//        String[] attachmentIds = attachmentId.split(",");
//        for(int i=0;i<attachmentIds.length;i++) {
//          attachmentList.add(attachmentIds[i]);
//        }
//      }
//      String[] attachmentNames = null;
//      if(!"".equals(attachmentName)&&attachmentName!=null) {
//        attachmentNames = attachmentId.split(",");
//        for(int i=0;i<attachmentNames.length;i++) {
//          sb.append("{");
//          sb.append("attachmentId:\"" + attachmentList.get(i) + "\"");
//          sb.append(",fattachmentName:\"" + attachmentNames[i] + "\"");
//          sb.append("},");
//        }
//      }
//      if(attachmentNames.length>0) {  
//        sb.deleteCharAt(sb.length() - 1); 
//        }
//       sb.append("]");
//      sb.append("}");
//      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String downPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
      InputStream in = null;
      OutputStream out = null;
      try{
        String path = request.getParameter("path");
        response.setContentType("application/octet-stream");
        response.setHeader("Cache-Control","maxage=3600");
        response.setHeader("Pragma","public");
        response.setHeader("Content-Disposition", "attachment;filename=" + T9FileUtility.getFileName(path));
        in = new FileInputStream(path);
        out = response.getOutputStream();
        byte[] buff = new byte[1024];
        int readLength = 0;
        while ((readLength = in.read(buff)) > 0) {        
           out.write(buff, 0, readLength);
        }
        out.flush();
      }catch(Exception ex) {
        ex.printStackTrace();
      }finally {
        try {
          if (in != null) {
            in.close();
          }
        }catch(Exception ex) {
          ex.printStackTrace();
        }
      }
      return null;
  }
  
/**
 * 改变公告状态，终止，生效等
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
public String changeState(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
    String seqId = request.getParameter("seqId");//seqId
    String operation = request.getParameter("operation");//操作
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数    if(pageIndexStr == null || pageIndexStr==""){
      pageIndexStr ="1";
    }

//    String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    Connection dbConn = null;
    boolean success = false;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       success = notifyManageLogic.changeState(dbConn, loginUser,seqId,operation);
    
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG,"终止生效状态已修改");
  } catch (Exception ex) {
    String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    throw ex;
  }
  String forward = T9Utility.null2Empty(request.getParameter(T9ActionKeys.RET_METHOD_FORWARD));
  if (forward.equals("rtJson")) {
    return "/core/inc/rtjson.jsp";
  }
  return "/core/funcs/notify/manage/notifyList.jsp";
}
  
  public String changeStateGroup(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
    String delete_str = request.getParameter("delete_str");//seqId
    String operation = request.getParameter("operation");//操作

//    String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    Connection dbConn = null;
    boolean success = false;
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       success = notifyManageLogic.changeStateGroup(dbConn, loginUser,delete_str,operation);
    
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG,"终止生效状态已修改");
  } catch (Exception ex) {
    String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    throw ex;
  }
  //return "/core/funcs/dept/deptinput.jsp";
  //?deptParentDesc=+deptParentDesc
  return "/core/inc/rtjson.jsp";
  }
  
  public String cancelTop(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String pageIndex = request.getParameter("pageIndex");
    String showLength = request.getParameter("showLength");
    String deleteStr = request.getParameter("delete_str");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String loginUserPriv = person.getUserPriv();
    String postPriv = person.getPostPriv();
    int loginUserId = person.getSeqId();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      notifyManageLogic.cancelTop(dbConn, Integer.toString(loginUserId), loginUserPriv, postPriv, deleteStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取消置顶");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  
  public String getNoteById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      String data = notifyManageLogic.getNoteById(dbConn,person,Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 点击标题，查看
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showObject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      String data = notifyManageLogic.showObject(dbConn,person,Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String getnotifyType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9News news = null;
    StringBuffer sb = new StringBuffer();
    sb.append("{");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int typeNum = 0;
      String getTypeSql = "select SEQ_ID,CLASS_DESC from CODE_ITEM where CLASS_NO='NOTIFY'";
      Statement typeSt = dbConn.createStatement();
      ResultSet typeRs = typeSt.executeQuery(getTypeSql);
      sb.append("typeData:[");
      while(typeRs.next()){
        typeNum ++;
        sb.append("{");
        sb.append("typeId:\"" + typeRs.getInt("SEQ_ID") + "\"");
        sb.append(",typeDesc:\"" + typeRs.getString("CLASS_DESC") + "\"");
        sb.append("},");
      }
      if(typeNum >0) {
        sb.deleteCharAt(sb.length() - 1); 
        }
      sb.append("]");
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 浮动菜单文件删除
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delFloatFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String attachId = request.getParameter("attachId");
    String attachName = request.getParameter("attachName");
    String sSeqId = request.getParameter("seqId");
    //T9Out.println(sSeqId);
    if (attachId == null) {
      attachId = "";
    }
    if (attachName == null) {
      attachName = "";
    }
    int seqId = 0 ;
    if (sSeqId != null && !"".equals(sSeqId)) {
      seqId = Integer.parseInt(sSeqId);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requesttDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requesttDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();

      boolean updateFlag = notifyManageLogic.delFloatFile(dbConn, attachId, attachName , seqId);
      if(updateFlag){
        T9SysLogLogic.addSysLog(dbConn, "15", "删除附件，附件名称:"+attachName, person.getSeqId(), logLogic.getIpAddr(request));
      }
      String isDel="";
      if (updateFlag) {
        isDel ="isDel"; 

      }
      String data = "{updateFlag:\"" + isDel + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }

    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 导出到excel
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toExcel(HttpServletRequest request, HttpServletResponse response)throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    try{
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String stat = request.getParameter("stat");
      if(stat == null || stat.length() <1){
        stat = "";
      }
      T9Notify notify = (T9Notify)T9FOM.build(request.getParameterMap());    
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      //T9Out.println(person.getSeqId());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyMangerLogic = new T9NotifyManageLogic();
      List<Map<String, String>> notifys = notifyMangerLogic.toExcel(dbConn,notify,person,beginDate,endDate,stat);
      String fileName = URLEncoder.encode("公告.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream(); 
      ArrayList<T9DbRecord > dbL = notifyMangerLogic.convertList(notifys);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception e){
      e.printStackTrace();
      throw e;
    }finally{
      ops.close();
    }
    return null;
  }
  
  /**
   * 删除选择的公告
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String deleteSelNotify(HttpServletRequest request, HttpServletResponse response) throws Exception, SQLException{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
    .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      try{
        dbConn = requestDbConn.getSysDbConn();    
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        String stat = request.getParameter("stat");
        if(stat == null || stat.length() <1){
          stat = "";
        }
        T9Notify notify = (T9Notify)T9FOM.build(request.getParameterMap());
        T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        notifyManageLogic.deleteSelNotify(dbConn, notify, person, beginDate, endDate, stat);    
        int count = notifyManageLogic.getCount();
        request.setAttribute("count",count);
      } catch (Exception ex){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());  
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw ex;
      }      
      return "/core/funcs/notify/manage/msg.jsp";
  }
  
  /**
   * 查看最大的系统设置的置顶时间
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getManageTopDays(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
      int topDays = notifyManageLogic.getNotifyTopDay(dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, topDays);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
