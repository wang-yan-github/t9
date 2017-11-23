package t9.core.funcs.workflow.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.funcs.workflow.data.T9FlowFormType;
import t9.core.funcs.workflow.data.T9FlowProcess;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowFormLogic;
import t9.core.funcs.workflow.logic.T9FlowProcessLogic;
import t9.core.funcs.workflow.logic.T9FlowSortLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9WorkflowSave2DataTableLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;

public class T9FlowTypeAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.workflow.act.T9FlowTypeAct");
  
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

      T9FlowTypeLogic logic = new T9FlowTypeLogic();

      boolean updateFlag = logic.delFloatFile(dbConn, attachId, attachName , seqId);
     
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
   * 单文件附件上传

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
  try{
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    T9FlowTypeLogic logic = new T9FlowTypeLogic();
    attr = logic.fileUploadLogic(fileForm, T9SysProps.getAttachPath());
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
  }catch (SizeLimitExceededException ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败：文件需要小于" + T9SysProps.getInt(T9SysPropKeys.MAX_UPLOAD_FILE_SIZE) + "兆");
  } catch (Exception e){
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
  }
  //return "/core/inc/rtjson.jsp";
  return "/core/inc/rtuploadfile.jsp";
}
  
  public String getFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    ArrayList<T9FlowSort> sortList = null;
    T9FlowSort flowSort = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      String data = "";
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      
      sortList = flowTypeLogic.getFlowSortList(dbConn);
      if(sortList.size() > 0) {
        for(int i = 0; i < sortList.size(); i++) {
          flowSort = sortList.get(i);
            sb.append("{");
            sb.append("seqId:\"" + flowSort.getSeqId() + "\"");
            sb.append(",sortName:\"" + flowSort.getSortName() + "\"");
            sb.append(",sortParent:\"" + flowSort.getSortParent() + "\"");
            sb.append("},");                   
        }
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      data = sb.toString();
      
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String flowType = request.getParameter("flowType");
    String flowName = request.getParameter("flowName");
    String formSeqId = request.getParameter("formSeqId");
    String flowDoc = request.getParameter("flowDoc");
    String manageUser = request.getParameter("manageUser");
    String flowNo = request.getParameter("flowNo");
    String flowSort = request.getParameter("flowSort");
    String autoName = request.getParameter("autoName");
    String autoNum = request.getParameter("autoNum");
    String autoLen = request.getParameter("autoLen");
    String queryUser = request.getParameter("queryUser");
    String flowDesc = request.getParameter("flowDesc");
    String autoEdit = request.getParameter("autoEdit");
    String newUser = request.getParameter("newUser");
    String queryItem = request.getParameter("queryItem");
    String commentPriv = request.getParameter("commentPriv");
    String deptId = request.getParameter("deptId");
    String freePreset = request.getParameter("freePreset");
    String freeOther = request.getParameter("freeOther");
    String queryUserDept = request.getParameter("queryUserDept");
    String manageUserDept = request.getParameter("manageUserDept");
    String editPriv = request.getParameter("editPriv");
    String listFldsStr = request.getParameter("listFldsStr");
    String allowPreSet = request.getParameter("allowPreSet");
    String modelId = request.getParameter("modelId");
    String modelName = request.getParameter("modelName");
    
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      
      Map map = new HashMap();
      
      map.put("flowName", flowName);
      map.put("formSeqId", formSeqId);
      map.put("flowDoc", flowDoc);
      map.put("flowType", flowType);
      map.put("manageUser", manageUser);
      map.put("flowNo", flowNo);
      map.put("flowSort", flowSort);
      map.put("autoName", autoName);
      map.put("autoNum", autoNum);
      map.put("autoLen", autoLen);
      map.put("queryUser", queryUser);
      map.put("flowDesc", flowDesc);
      map.put("autoEdit", autoEdit);
      map.put("newUser", newUser);
      map.put("queryItem", queryItem);
      map.put("commentPriv", commentPriv);
      map.put("deptId", deptId);
      map.put("freePreset", freePreset);
      map.put("freeOther", freeOther);
      map.put("queryUserDept", queryUserDept);
      map.put("manageUserDept", manageUserDept);
      map.put("editPriv", editPriv);
      map.put("listFldsStr", listFldsStr);
      map.put("allowPreSet", allowPreSet);
      map.put("modelId", modelId);
      map.put("modelName", modelName);

      flowTypeLogic.saveFlowType(dbConn, map);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功添加人员");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    ArrayList<T9FlowFormType> typeList = null;
    T9FlowFormType formType = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      typeList = flowTypeLogic.getFlowFormTypeList(dbConn);
      if(typeList.size() > 0) {
        for(int i = 0; i < typeList.size(); i++) {
            formType = typeList.get(i);
            sb.append("{");
            sb.append("seqId:\"" + formType.getSeqId() + "\"");
            sb.append(",formName:\"" + formType.getFormName() + "\"");
            sb.append("},");                   
        }
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 我的工作列表页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowTypeJson2(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    List<T9FlowType> typeList = new ArrayList();
    T9FlowType flowType = null;
    String checkTypeStr = request.getParameter("checkType");
    int checkType = 0;
    String sSortId = request.getParameter("sortId");
    try {
      if(checkTypeStr != null && !"".equals(checkTypeStr)){
        try {
          checkType = Integer.parseInt(checkTypeStr);
        } catch (Exception e) {
          checkType = 0;
        }
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (T9Utility.isNullorEmpty(sSortId)) {
        typeList = flowTypeLogic.getFlowTypeList(dbConn);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(sSortId , dbConn);
      }
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        
        List<T9FlowProcess> list = fpl.getFlowPrrocessByFlowId2(flowType.getSeqId() , dbConn);
        if(roleUtility.prcsRole(flowType, list , checkType, loginUser, dbConn)){
          sb.append("{");
          sb.append("seqId:\"" + flowType.getSeqId() + "\"");
          sb.append(",flowName:\"" + flowType.getFlowName() + "\"");
          sb.append("},"); 
          count ++;
        }
      }
      if(count > 0){  
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getFlowTypeJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    List<T9FlowType> typeList = new ArrayList();
    T9FlowType flowType = null;
    String checkTypeStr = request.getParameter("checkType");
    int checkType = 0;
    String sSortId = request.getParameter("sortId");
    try {
      if(checkTypeStr != null && !"".equals(checkTypeStr)){
        try {
          checkType = Integer.parseInt(checkTypeStr);
        } catch (Exception e) {
          checkType = 0;
        }
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (T9Utility.isNullorEmpty(sSortId)) {
        typeList = flowTypeLogic.getFlowTypeList(dbConn);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(sSortId , dbConn);
      }
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        List<T9FlowProcess> list = fpl.getFlowPrrocessByFlowId1(flowType.getSeqId() , dbConn);
        if(roleUtility.prcsRole(flowType, list , checkType, loginUser, dbConn)){
          sb.append("{");
          sb.append("seqId:\"" + flowType.getSeqId() + "\"");
          sb.append(",flowName:\"" + flowType.getFlowName() + "\"");
          sb.append("},"); 
          count ++;
        }
      }
      if(count > 0){  
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 取得管理权限
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowTypeJsonByManager(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    List<T9FlowType> typeList = new ArrayList();
    T9FlowType flowType = null;
    String sSortId = request.getParameter("sortId");
    String checkTypeStr = request.getParameter("checkType");
    int checkType = 0;
    try {
      if(checkTypeStr != null && !"".equals(checkTypeStr)){
        try {
          checkType = Integer.parseInt(checkTypeStr);
        } catch (Exception e) {
          checkType = 0;
        }
      }
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (T9Utility.isNullorEmpty(sSortId)) {
        typeList = flowTypeLogic.getFlowTypeList(dbConn);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(sSortId , dbConn);
      }
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        List<T9FlowProcess> list = fpl.getFlowPrrocessByFlowId1(flowType.getSeqId() , dbConn);
        if(roleUtility.prcsRoleByManager(flowType, list , checkType, loginUser)){
          sb.append("{");
          sb.append("seqId:\"" + flowType.getSeqId() + "\"");
          sb.append(",flowName:\"" + flowType.getFlowName() + "\"");
          sb.append("},"); 
          count ++;
        }
      }
      if(count > 0){  
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 主要是用于定义规则页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getFlowTypeJson1(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    List<T9FlowType> typeList = new ArrayList();
    T9FlowType flowType = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String sSortId = request.getParameter("sortId");
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (T9Utility.isNullorEmpty(sSortId)) {
        Map m = new HashMap();
        m.put("FREE_OTHER", "2");
        T9ORM orm = new T9ORM();
        typeList = orm.loadListSingle(dbConn, T9FlowType.class, m);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(sSortId , dbConn ,"2");
      }
      
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        List<T9FlowProcess> list = fpl.getFlowPrrocessByFlowId2(flowType.getSeqId() , dbConn);
        if(roleUtility.prcsRole(flowType, list , 0, loginUser, dbConn)){
          sb.append("{");
          sb.append("seqId:\"" + flowType.getSeqId() + "\"");
          sb.append(",flowName:\"" + flowType.getFlowName() + "\"");
          sb.append("},"); 
          count ++;
        }
      }
      if(count > 0){  
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getAddMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9DeptLogic deptLogic = new T9DeptLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer sb = new StringBuffer("{sortList:");
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      T9FlowSortLogic sortLogic = new T9FlowSortLogic();
      sb.append(sortLogic.getSortTreeJson( dbConn , u , 0 , false));
      sb.append(",formList:");
      sb.append(ffLogic.getFlowFormTypeOption(dbConn, u));
      String deptList  = deptLogic.getDeptTreeJson(u , dbConn);
      sb.append(",deptList:" + deptList);
      sb.append("}");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getEditMessage(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    try {
     
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("{flowType:");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
     
      T9DeptLogic deptLogic = new T9DeptLogic();
      sb.append(ft.toJson());
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      int formId = ft.getFormSeqId();
      
      String formList = T9Utility.encodeSpecial(ffLogic.getTitle(dbConn, formId));
      
      sb.append(",sortList:");
      T9FlowSortLogic sortLogic = new T9FlowSortLogic();
      sb.append(sortLogic.getSortTreeJson( dbConn , u , 0 , false));
      sb.append(",formList:");
      
      T9PersonLogic personLogic = new T9PersonLogic();
      String viewUserName = personLogic.getNameBySeqIdStr(ft.getViewUser(), dbConn);
      
     
      sb.append(ffLogic.getFlowFormTypeOption(dbConn , u));
      
      int workCount = flowTypeLogic.getWorkCountByFlowId(ft.getSeqId() , dbConn);
      int delCount = flowTypeLogic.getDelWorkCountByFlowId(ft.getSeqId() , dbConn);
      
      String deptList = deptLogic.getDeptTreeJson(u , dbConn);
      sb.append(",deptList:"  + deptList);
      sb.append(",workCount:" + workCount + ",delCount:" + delCount + ",formItem:\""+ formList +"\",viewUserName:\""+T9Utility.encodeSpecial(viewUserName)+"\"}");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String saveFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9FlowType ft = new T9FlowType();
      this.setFlowType(request, ft, false);
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      
      ft.setAttachmentId(request.getParameter("attachmentId"));
      ft.setAttachmentName(request.getParameter("attachmentName"));
      
      flowTypeLogic.saveFlowType(ft , dbConn);
      if (T9WorkFlowUtility.isSave2DataTable()) {
        int seqId = flowTypeLogic.getFlowTypeSeqId(dbConn);
        String formSeqId = request.getParameter("formId");
        String tableName = "" +T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + seqId + "_" + formSeqId;
        T9WorkflowSave2DataTableLogic logic = new T9WorkflowSave2DataTableLogic();
        logic.createTable(dbConn , tableName , formSeqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "保存成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String updateFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
      int formSeqIdOld = ft.getFormSeqId();
      this.setFlowType(request, ft, true);
      int formSeqIdNew = ft.getFormSeqId();
      
      ft.setAttachmentId(request.getParameter("attachmentId"));
      ft.setAttachmentName(request.getParameter("attachmentName"));
      
      flowTypeLogic.updateFlowType(ft , dbConn);
      if (T9WorkFlowUtility.isSave2DataTable() && formSeqIdOld != formSeqIdNew) {
        int seqId = ft.getSeqId();
        String formSeqId = request.getParameter("formId");
        String tableOldName = "" +T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + seqId + "_" + formSeqIdOld ;
        String tableNewName = "" +T9WorkflowSave2DataTableLogic.FORM_DATA_TABLE_PRE + seqId + "_" + formSeqIdNew ;
        T9WorkflowSave2DataTableLogic logic = new T9WorkflowSave2DataTableLogic();
        logic.dropTable(dbConn , tableOldName );
        logic.createTable(dbConn , tableNewName , formSeqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String delFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9FlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
      flowTypeLogic.delFlowType(ft , dbConn);
      String str = "成功删除流程：" + ft.getSeqId() + "，以及该流程下的所有数据";
      log.info(str);
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String ip = request.getRemoteAddr();
      T9SysLogLogic.addSysLog(dbConn, "60", str, u.getSeqId() ,  ip);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public void setFlowType(HttpServletRequest request,
      T9FlowType ft , boolean isUpdate) {
    String flowSort = request.getParameter("flowSort");
    String sFlowNo = request.getParameter("flowNo");
    int flowNo = 0;
    if(!"".equals(sFlowNo)){
      flowNo = Integer.parseInt(sFlowNo);
    }
    String flowName = request.getParameter("flowName");
    String flowType = request.getParameter("flowType");
    String formSeqId = request.getParameter("formId");
    String freePreset = request.getParameter("freePreset");
    String freeOther = request.getParameter("freeOther");   
    String flowDoc = request.getParameter("flowDoc");
    String flowDesc = request.getParameter("flowDesc");
    String autoName = request.getParameter("autoName");
    String sAutoNum = request.getParameter("autoNum");
    String sDeptId = request.getParameter("deptId");
    String  listFldsStr = request.getParameter("listFldsStr");
    String  viewPrivStr = request.getParameter("VIEW_PRIV");
    int viewPriv = 0 ;
    if (!T9Utility.isNullorEmpty(viewPrivStr)) {
      viewPriv = Integer.parseInt(viewPrivStr);
    }
    String  viewUserId = T9Utility.null2Empty(request.getParameter("VIEW_USER_ID"));
    int autoNum = 0;
    if(!"".equals(sAutoNum)){
      autoNum = Integer.parseInt(sAutoNum);
    }
    String sAutoLen = request.getParameter("autoLen");
    int autoLen = 0;
    if(!"".equals(sAutoLen)){
      autoLen = Integer.parseInt(sAutoLen);
    }
    String autoEdit = request.getParameter("autoEdit");
    ft.setViewPriv(viewPriv);
    ft.setViewUser(viewUserId);
    ft.setDeptId(Integer.parseInt(sDeptId));
    ft.setFlowSort(Integer.parseInt(flowSort));
    ft.setFlowNo(flowNo);
    ft.setFlowName(flowName);
    if(flowType != null){
      ft.setFlowType(flowType);
    }
    if(formSeqId != null){
      ft.setFormSeqId(Integer.parseInt(formSeqId));
    }
    ft.setFreePreset(freePreset);
    ft.setFreeOther(freeOther);
    ft.setFlowDoc(flowDoc);
    ft.setFlowDesc(flowDesc);
    ft.setAutoNum(autoNum);
    ft.setAutoName(autoName);
    ft.setAutoLen(autoLen);
    ft.setAutoEdit(autoEdit);
    if(isUpdate){
      ft.setListFldsStr(listFldsStr);
    } 
  }
  public String getFlowTypeBySort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sSortId = request.getParameter("sortId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      StringBuffer sb = new StringBuffer("[");
      String data = "";
      StringBuffer workCounts = new StringBuffer("[");
      StringBuffer formName = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      List<T9FlowType> typeList = flowTypeLogic.getFlowTypeList(Integer.parseInt(sSortId) , dbConn);
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int count = 0 ;
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      for(int i = 0; i < typeList.size(); i++) {
        T9FlowType flowType = typeList.get(i);
        if (!w.isHaveRight(flowType.getDeptId(), u, dbConn)) {
          continue;
        }
        sb.append(flowType.toJson() + ",");
        //取得工作数量
        int workCount = flowTypeLogic.getWorkCountByFlowId(flowType.getSeqId() , dbConn);
        int delCount = flowTypeLogic.getDelWorkCountByFlowId(flowType.getSeqId() , dbConn);
        workCounts.append("{workCount:" + workCount);
        workCounts.append(",delCount:" + delCount + "},");
        formName.append("'" + flowTypeLogic.getFormName(flowType.getFormSeqId(), dbConn) + "',");
        count++;
      }
      if (count >0 ){
        sb.deleteCharAt(sb.length() - 1);
        workCounts.deleteCharAt(workCounts.length() - 1);
        formName.deleteCharAt(formName.length() - 1);
      }
      workCounts.append("]");
      sb.append("]");
      formName.append("]");
      data = "{flowList:" + sb.toString() + ",workCounts:" + workCounts.toString() + ",formName:"+ formName.toString() +"}";
      request.setAttribute(T9ActionKeys.RET_DATA, data );
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "取出数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String empty(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int flowId = Integer.parseInt(sFlowId);
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      logic.empty(flowId, dbConn);
      String str = "成功清空流程：" + flowId + "下的所有数据";
      log.info(str);
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String ip = request.getRemoteAddr();
      T9SysLogLogic.addSysLog(dbConn, "60", str, u.getSeqId() ,  ip);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功清空数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
