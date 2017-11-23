package t9.core.funcs.doc.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.doc.data.T9DocFlowFormReglex;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.logic.T9FlowFormLogic;
import t9.core.funcs.doc.praser.T9FormPraser;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9FlowFormAct {
  private static Logger log = Logger
    .getLogger("t9.core.funcs.doc.act.T9FlowFormAct");
  public String insertFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String formName = request.getParameter("formName");
      String deptId = request.getParameter("deptId");
      
      T9DocFlowFormType form = new T9DocFlowFormType();
      form.setDeptId(Integer.parseInt(deptId));
      form.setFormName(formName);
      T9ORM orm = new T9ORM();
      orm.saveSingle(dbConn, form);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doUploadImage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    try{
      String module = request.getParameter("module");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9WorkFlowUtility util = new T9WorkFlowUtility();
      String fileName = fileForm.getFileName();
      String[] tmp =  util.getNewAttachPath(fileName, module);
      String filePath = tmp[1];
      fileForm.saveFile(filePath);
      String contextPath = request.getContextPath();
      String requestPath = contextPath + "/t9/core/funcs/office/ntko/act/T9NtkoAct/upload.act?attachmentName="+T9Utility.encodeURL(fileName)+"&attachmentId="+tmp[0]+"&module="+module+"&directView=1";
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/html");
      response.setHeader("Cache-Control", "no-cache");  
      PrintWriter out = response.getWriter();
      out.print("<body onload=\"window.parent.OnUploadCompleted(0, '" + requestPath + "', '" + fileName + "', 'success' )\"/>");
      out.flush();
      out.close();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9ORM orm = new T9ORM();
      T9DocFlowFormType obj = (T9DocFlowFormType) orm.loadObjSingle(dbConn, T9DocFlowFormType.class, seqId);
      T9FlowFormLogic logic = new T9FlowFormLogic();
      data = "{seqId:"+ obj.getSeqId() +",formName:'"+obj.getFormName()+"',deptId:"+ obj.getDeptId() +"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "{formData:" +data + ",noDelete:" + logic.isExistFlowRun(seqId, dbConn)+"}");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getFormView(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
    //  String printModel = request.getParameter("printModel");
    //  String[] str = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = null;
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      Map map = ffl.selectFlowForm(dbConn, seqId, "PRINT_MODEL");
      data = toJs(map).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public StringBuffer toJs(Map map) throws Exception {
    StringBuffer rtBuf = new StringBuffer("{");
    Iterator it = map.entrySet().iterator();
    while (it.hasNext()){
      Map.Entry entry = (Map.Entry) it.next();
      Object key = entry.getKey();
      Object value = entry.getValue();
      rtBuf.append("'");
      rtBuf.append(key);
      rtBuf.append("'");
      rtBuf.append(":");
      rtBuf.append("\"");
      rtBuf.append(value);
      rtBuf.append("\"");
      //System.out.println(key+":"+value+"ffffffff");
    }
    rtBuf.append("}");
    //System.out.println(rtBuf+"eeeeeeeee");
    return rtBuf;
  }
  
  public String updateForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqStr = request.getParameter("seqId");
      String printModel = request.getParameter("printModel");
      String printModelNew = "";
      //System.out.println(seqStr);
      int seqId = Integer.parseInt(seqStr.trim());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      printModel = printModel.replaceAll("\"", "\\\\\"");
      printModel = printModel.replaceAll("\r\n", "");
      HashMap hm = (HashMap) T9FormPraser.praserHTML2Dom(printModel);
      Map<String, Map> m1 = T9FormPraser.praserHTML2Arr(hm);
      printModelNew = T9FormPraser.toShortString(m1, printModel, T9DocFlowFormReglex.CONTENT);
      ffl.updateFlowForm(dbConn, seqId, new String[]{"PRINT_MODEL","PRINT_MODEL_SHORT"},  new String[]{printModel,printModelNew});
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateFlowForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqStr = request.getParameter("seqId");
      int seqId = Integer.parseInt(seqStr.trim());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocFlowFormType form = (T9DocFlowFormType) T9FOM.build(request.getParameterMap());
      form.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, form);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String deleteForm(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      String seqId = request.getParameter("seqId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic logic = new T9FlowFormLogic();
      logic.deleteForm(Integer.parseInt(seqId), dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功删除数据库的数据");
    }catch (Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateDesign(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocFlowFormType form = (T9DocFlowFormType) T9FOM.build(request.getParameterMap());
      form.setSeqId(seqId);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, form);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功更改数据库的数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 表单分类管理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listBySort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String data = "";
    int sortId = 0 ;
    try{
      sortId = Integer.parseInt(request.getParameter("sortId"));
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer sb = ffl.flowFormType2Json(dbConn, sortId , u );
      data = "{flowList:" + sb.toString() + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //System.out.println(data);
    return "/core/inc/rtjson.jsp";
  }
  public String search(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String data = "";
    try{
      String search = request.getParameter("searchKey");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowFormLogic ffl = new T9FlowFormLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer sb = ffl.search(dbConn, search , u);
      data = "{flowList:" + sb.toString() + "}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //System.out.println(data);
    return "/core/inc/rtjson.jsp";
  }
}
