package t9.core.funcs.doc.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.doc.data.T9DocFlowFormType;
import t9.core.funcs.doc.data.T9DocFlowProcess;
import t9.core.funcs.doc.data.T9DocFlowSort;
import t9.core.funcs.doc.data.T9DocFlowType;
import t9.core.funcs.doc.logic.T9FlowFormLogic;
import t9.core.funcs.doc.logic.T9FlowProcessLogic;
import t9.core.funcs.doc.logic.T9FlowSortLogic;
import t9.core.funcs.doc.logic.T9FlowTypeLogic;
import t9.core.funcs.doc.util.T9PrcsRoleUtility;
import t9.core.funcs.doc.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;

public class T9FlowTypeAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.doc.act.T9FlowTypeAct");
  public String getFlowSort(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    ArrayList<T9DocFlowSort> sortList = null;
    T9DocFlowSort flowSort = null;
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
    ArrayList<T9DocFlowFormType> typeList = null;
    T9DocFlowFormType formType = null;
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
  public String getFlowTypeJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    List<T9DocFlowType> typeList = new ArrayList();
    T9DocFlowType flowType = null;
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
        List<T9DocFlowProcess> list = fpl.getFlowPrrocessByFlowId1(flowType.getSeqId() , dbConn);
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
    List<T9DocFlowType> typeList = new ArrayList();
    T9DocFlowType flowType = null;
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
        List<T9DocFlowProcess> list = fpl.getFlowPrrocessByFlowId1(flowType.getSeqId() , dbConn);
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
    List<T9DocFlowType> typeList = new ArrayList();
    T9DocFlowType flowType = null;
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
        typeList = orm.loadListSingle(dbConn, T9DocFlowType.class, m);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(sSortId , dbConn ,"2");
      }
      
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility roleUtility = new T9PrcsRoleUtility();
      T9FlowProcessLogic fpl = new T9FlowProcessLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        List<T9DocFlowProcess> list = fpl.getFlowPrrocessByFlowId1(flowType.getSeqId() , dbConn);
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
      StringBuffer sb = new StringBuffer("{sortList:[");
      T9FlowSortLogic flowSortLogic = new T9FlowSortLogic();
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
     
      List<T9DocFlowSort> sortList = flowSortLogic.getFlowSort(dbConn);
     
      if(sortList.size() > 0) {
        T9WorkFlowUtility w = new T9WorkFlowUtility();
        for(T9DocFlowSort tmp : sortList) {
          if (!w.isHaveSortRight(tmp.getDeptId(), u, dbConn)) {
            continue;
          }
          sb.append("{");
          sb.append("value:" + tmp.getSeqId() + ",");
          sb.append("text:'├" + tmp.getSortName() + "'");
          sb.append("},");
        }
        if(sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      sb.append("],formList:");
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
      T9FlowSortLogic flowSortLogic = new T9FlowSortLogic();
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DocFlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
     
      T9DeptLogic deptLogic = new T9DeptLogic();
      sb.append(ft.toJson());
      T9FlowFormLogic ffLogic = new T9FlowFormLogic();
      List<T9DocFlowSort> sortList = flowSortLogic.getFlowSort(dbConn);
      int formId = ft.getFormSeqId();
      
      String formList = T9Utility.encodeSpecial(ffLogic.getTitle(dbConn, formId));
      
      sb.append(",sortList:[");
      if(sortList.size() > 0) {
        T9WorkFlowUtility w = new T9WorkFlowUtility();
        for(T9DocFlowSort tmp : sortList) {
          if (!w.isHaveSortRight(tmp.getDeptId(), u, dbConn)) {
            continue;
          }
          sb.append("{");
          sb.append("value:" + tmp.getSeqId() + ",");
          sb.append("text:'├" + tmp.getSortName() + "'");
          sb.append("},");
        }
        if(sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
      }
      sb.append("],formList:");
      
     
      sb.append(ffLogic.getFlowFormTypeOption(dbConn , u));
      
      int workCount = flowTypeLogic.getWorkCountByFlowId(ft.getSeqId() , dbConn);
      int delCount = flowTypeLogic.getDelWorkCountByFlowId(ft.getSeqId() , dbConn);
      
      String deptList = deptLogic.getDeptTreeJson(u , dbConn);
      sb.append(",deptList:"  + deptList);
      sb.append(",workCount:" + workCount + ",delCount:" + delCount + ",formItem:\""+ formList +"\"}");
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
      T9DocFlowType ft = new T9DocFlowType();
      this.setFlowType(request, ft, false);
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      flowTypeLogic.saveFlowType(ft , dbConn);
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
      T9DocFlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
      this.setFlowType(request, ft, true);
      flowTypeLogic.updateFlowType(ft , dbConn);
      //flowTypeLogic.
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
      T9DocFlowType ft = flowTypeLogic.getFlowTypeById(Integer.parseInt(sFlowId) , dbConn);
      flowTypeLogic.delFlowType(ft , dbConn);
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
      T9DocFlowType ft , boolean isUpdate) {
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
      List<T9DocFlowType> typeList = flowTypeLogic.getFlowTypeList(Integer.parseInt(sSortId) , dbConn);
      T9Person u = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int count = 0 ;
      T9WorkFlowUtility w = new T9WorkFlowUtility();
      for(int i = 0; i < typeList.size(); i++) {
        T9DocFlowType flowType = typeList.get(i);
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
