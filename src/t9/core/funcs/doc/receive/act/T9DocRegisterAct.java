package t9.core.funcs.doc.receive.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import srvSeal.SrvSealUtil;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.logic.T9FlowManageLogic;
import t9.core.funcs.doc.receive.data.T9DocConst;
import t9.core.funcs.doc.receive.logic.T9DocReceiveLogic;
import t9.core.funcs.doc.receive.logic.T9DocReceiveRegLogic;
import t9.core.funcs.doc.receive.logic.T9DocRegisterLogic;
import t9.core.funcs.doc.util.T9DocUtility;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
public class T9DocRegisterAct{
  public final static byte[] loc = new byte[1];
  /**
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRegList2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      PrintWriter pw = response.getWriter();
      T9DocRegisterLogic doc = new T9DocRegisterLogic();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = doc.getSendMesage2(user, dbConn, request.getParameterMap(), request.getRealPath("/") , "1");
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 取得已登记的收文
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRegList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      PrintWriter pw = response.getWriter();
      T9DocRegisterLogic doc = new T9DocRegisterLogic();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = doc.getRegList(user, dbConn, request.getParameterMap(), request.getRealPath("/"));
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 查询登记
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryRegList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      PrintWriter pw = response.getWriter();
      T9DocRegisterLogic doc = new T9DocRegisterLogic();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String data = doc.queryRegList(user, dbConn, request.getParameterMap(), request.getRealPath("/"));
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      String webroot = request.getRealPath("/");
      T9DocUtility util =  new T9DocUtility();
      String str = T9DocConst.getProp(webroot  , T9DocConst.DOC_RECEIVE_FLOW_SORT) ;
      String sortId = util.getSortIds(str, dbConn);
      Map map = util.getFlowBySortIds(sortId, dbConn  , user);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, sortId);
      request.setAttribute(T9ActionKeys.RET_DATA, this.mapTojson(map));
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String mapTojson(Map map) {
    StringBuffer sb = new StringBuffer().append("[");
    int count=0;
    Set<String> set = map.keySet();
    for (String tmp : set) {
      String name = (String)map.get(tmp);
      sb.append("{id:\"").append(tmp).append("\"").append(",").append("name:\"").append(name).append("\"},");
      count++;
    }
    if (count > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }
  /**
   * 根据seq_id取得未登记的记录
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRecReg(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String rec_seqId = request.getParameter("rec_seqId");
      String webroot = request.getRealPath("/");
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      String str = logic.getRecReg(dbConn, rec_seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getRecRegBySeqId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String webroot = request.getRealPath("/");
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      String str = logic.getRecRegBySeqId(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
    } catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 获得最大的收文编号
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMaxOrderNo(HttpServletRequest request, HttpServletResponse response)throws Exception{
    try{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String type = request.getParameter("type");
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      synchronized(loc) {
        int max = logic.getMaxOrderNo(dbConn, type);
        dbConn.commit();
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
        request.setAttribute(T9ActionKeys.RET_DATA, String.valueOf(max));
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String register(HttpServletRequest request, HttpServletResponse response)throws Exception{
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      String seqId = request.getParameter("seqId");
      String recId = request.getParameter("recId");
      String recType = request.getParameter("recType");
      String recNo = request.getParameter("recNo");
      String fromDeptName = request.getParameter("fromDeptName");
      String fromDeptId = request.getParameter("fromDeptId");
      String secretsLevel = request.getParameter("secretsLevel");
      String sendDocNo = request.getParameter("sendDocNo");
      String title = request.getParameter("title");
      String copies = request.getParameter("copies");
      String recDocId = request.getParameter("recDocId");
      String recDocName = request.getParameter("recDocName");
      String attachmentId = request.getParameter("attachmentId");
      String attachmentName = request.getParameter("attachmentName");
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      
      String flag = request.getParameter("flag");
      synchronized(loc) {
        if (T9Utility.isNullorEmpty(seqId)) {
          /*
          T9WorkFlowUtility util = new T9WorkFlowUtility();
          String path = util.getAttachPath(recDocId, recDocName, "doc");
          
          //path = path.toLowerCase();
          String newPath = "";
          if (path.endsWith(".doc") || path.endsWith("docx")) {
            SrvSealUtil ssu = new SrvSealUtil();
            int nObjID = ssu.openObj("", 0, 0);
            ssu.login(nObjID, 4, "HWSEALDEMOXX", "");
            ssu.addPage(nObjID, path, "");
            
            String [] p = util.getNewAttachPath(recDocName, "doc");
            newPath = p[1];
            recDocId = p[0];
            ssu.saveFile(nObjID, newPath, "doc");
          }*/
          logic.register(conn ,recId, recType ,recNo,fromDeptName,fromDeptId,secretsLevel,sendDocNo,title,copies,recDocId,recDocName,attachmentId,attachmentName,user.getSeqId());
          if (!T9Utility.isNullorEmpty(recId)) {
            logic.updateStatus(conn, recId);
          }
        } else {
          logic.update(conn , seqId,recId, recType ,recNo,fromDeptName,fromDeptId,secretsLevel,sendDocNo,title,copies,recDocId,recDocName,attachmentId,attachmentName);
        } 
        conn.commit();
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  public static void main(String args[]) {
    String path = "D:\\sss.docx";
    
    String newPath = "";
    if (path.endsWith(".doc") || path.endsWith("docx")) {
      SrvSealUtil ssu = new SrvSealUtil();
      int nObjID = ssu.openObj("", 0, 0);
      ssu.login(nObjID, 4, "HWSEALDEMOXX", "");
      ssu.addPage(nObjID, path, "");
      
      newPath = "D:\\333.doc";
      ssu.saveFile(nObjID, newPath, "doc");
    }
  }
  public String uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    try{
      T9DocReceiveLogic  docLogic = new T9DocReceiveLogic();
      attr = docLogic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
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
      data = "{attrId:\"" + T9Utility.encodeSpecial(attrId) + "\",attrName:\"" + T9Utility.encodeSpecial(attrName) + "\"}";
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

  /**
   * 强制结束工作流

   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String endWorkFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runIdStr = request.getParameter("runIdStr");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String s = "";
      boolean isManage = true;

      if (!"".equals(runIdStr)) {
        int runId = Integer.parseInt(runIdStr);
        T9FlowManageLogic manage = new T9FlowManageLogic();
        manage.endWorkFlow(runId, loginUser, dbConn);
      }
      this.setRequestSuccess(request, "结束流水号为[" + runIdStr + "]的工作,操作成功！");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 设置错误信息
   * 
   * @param request
   * @param message
   */
  public void setRequestError(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   */
  public void setRequestSuccess(HttpServletRequest request, String message) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
  }

  /**
   * 设置成功信息
   * 
   * @param request
   * @param message
   * @param data
   */
  public void setRequestSuccess(HttpServletRequest request, String message,
      String data) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    request.setAttribute(T9ActionKeys.RET_DATA, data);
  }
  /**
   * 恢复执行
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String restore(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String runId = request.getParameter("runId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowManageLogic manage = new T9FlowManageLogic();
      boolean reslut = manage.restore(Integer.parseInt(runId), loginUser
          .getSeqId(), dbConn);
      if (!reslut) {
        this.setRequestSuccess(request, "您的恢复执行操作没有成功!");
      } else {
        this.setRequestSuccess(request, "流水号为[" + runId + "]的工作已经恢复到执行状态!");
      }
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除登记
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delRegister(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      logic.delRegister(dbConn,seqId);
      this.setRequestSuccess(request, "操作成功!");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 恢复删除的登记
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String restoreDelRegister(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocRegisterLogic logic = new T9DocRegisterLogic();
      logic.restoreDelRegister(dbConn,seqId);
      this.setRequestSuccess(request, "操作成功!");
    } catch (Exception ex) {
      this.setRequestError(request, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
