package t9.core.funcs.workflow.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9FlowSort;
import t9.core.funcs.workflow.data.T9FlowType;
import t9.core.funcs.workflow.logic.T9FlowSortLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.funcs.workflow.logic.T9FlowWorkAdSearchLogic;
import t9.core.funcs.workflow.logic.T9FlowWorkSearchLogic;
import t9.core.funcs.workflow.logic.T9FormVersionLogic;
import t9.core.funcs.workflow.util.T9PrcsRoleUtility;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;

public class T9WorkQueryAct {
  private static Logger log = Logger
      .getLogger("t9.core.funcs.workflow.act.T9WorkQueryAct");
  public String getWorkList1(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String sSortId =  request.getParameter("sortId");
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      StringBuffer result = myWorkLogic.getWorkList(dbConn,request.getParameterMap(), loginUser , sSortId);
      PrintWriter pw = response.getWriter();
      pw.println( result.toString());
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  public String getManagerRole(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
    
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      String runId = request.getParameter("runId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      StringBuffer result = myWorkLogic.getManagerRoleLogic(dbConn, loginUser, runId);
      request.setAttribute(T9ActionKeys.RET_DATA, result.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
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
    String sSortId = request.getParameter("sortId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      StringBuffer sb = new StringBuffer("[");
      T9FlowTypeLogic flowTypeLogic = new T9FlowTypeLogic();
      if (!T9Utility.isNullorEmpty(sSortId)) {
        typeList = flowTypeLogic.getFlowTypeList(sSortId, dbConn);
      } else {
        typeList = flowTypeLogic.getFlowTypeList(dbConn);
      }
      int count = 0 ;
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      
      T9PrcsRoleUtility prcsRole = new T9PrcsRoleUtility();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        boolean canShow = true;
        if ("1".equals(flowType.getFlowType())) {
          String seqId = String.valueOf(loginUser.getSeqId());
          if (!(loginUser.isAdminRole() 
              || T9WorkFlowUtility.findId(flowType.getManageUser(), seqId)
              || T9WorkFlowUtility.findId(flowType.getQueryUser() , seqId)
              || prcsRole.prcsRoleByQuery(flowType, loginUser, dbConn))) {
            canShow = false;
          }
        }
        if (canShow) {
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
  public String getFlowType(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String sSortId = request.getParameter("sortId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      StringBuffer sb2 = new StringBuffer("[");
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      T9FlowType flowType = null;
      T9PrcsRoleUtility prcsRole = new T9PrcsRoleUtility();
      List<T9FlowType> typeList = ft.getFlowTypeList(sSortId , dbConn);
      int flowTypeCount = 0;
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      T9FormVersionLogic logic = new T9FormVersionLogic();
      for(int i = 0; i < typeList.size(); i++) {
        flowType = typeList.get(i);
        boolean canShow = true;
        if ("1".equals(flowType.getFlowType())) {
          String seqId = String.valueOf(loginUser.getSeqId());
          if (!(loginUser.isAdminRole() 
              || T9WorkFlowUtility.findId(flowType.getManageUser(), seqId)
              || T9WorkFlowUtility.findId(flowType.getQueryUser() , seqId)
              || prcsRole.prcsRoleByQuery(flowType, loginUser, dbConn))) {
            canShow = false;
          }
        }
        if (canShow) {
          int flowId = flowType.getSeqId();
          
          sb2.append("{");
          sb2.append("flowId:\"" + flowId  + "\"");
          sb2.append(",flowName:\"" + flowType.getFlowName() + "\"");
          Map map = myWorkLogic.getWorkCount(flowId,loginUser.getSeqId() , dbConn);
          int endWorkCount = (Integer)map.get("endCount");
          int handlerWorkCount =  (Integer)map.get("handlerCount");
          int newCount = (Integer)map.get("newCount");
          int dealCount = (Integer)map.get("dealCount");
          int overCount = (Integer)map.get("overCount");
          
          sb2.append(",newCount:" + newCount);
          sb2.append(",dealCount:" + dealCount);
          sb2.append(",overCount:" + overCount);
          sb2.append(",endWorkCount:" + endWorkCount);
          sb2.append(",handlerWorkCount:" + handlerWorkCount);
          String flowDoc = flowType.getFlowDoc();
          if (flowDoc == null) {
            flowDoc = "1";
          }
          sb2.append(",flowDoc:" + flowDoc );
          sb2.append(",flowType:" + flowType.getFlowType() );
          boolean hasFormVersion =  logic.hasFormVersion(dbConn, flowType.getFormSeqId());
          sb2.append(",hasFormVersion:" + hasFormVersion);
          sb2.append("},"); 
          flowTypeCount ++;
        }
      }
      if (flowTypeCount > 0) {
        sb2.deleteCharAt(sb2.length() - 1);
      }
      sb2.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb2.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, sSortId);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getFlowList(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9FlowType flowType = null;
    String sSortId = request.getParameter("sortId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9PrcsRoleUtility prcsRole = new T9PrcsRoleUtility();
      StringBuffer sb = new StringBuffer("[");
      List<T9FlowSort> list  = new ArrayList();
      T9FlowSortLogic fs = new T9FlowSortLogic();
      if (sSortId == null || "".equals(sSortId)) {
        list = fs.getFlowSort(dbConn);
      } else {
        sSortId = T9WorkFlowUtility.getInStr(sSortId);
        list =  fs.getFlowSortByIds(dbConn , sSortId);
      }
      T9FlowTypeLogic ft = new T9FlowTypeLogic();
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      int count = 0;
      for(T9FlowSort tmp : list){
        StringBuffer sb2 = new StringBuffer();
        sb2.append("[");
//        if (count  <= 1 ) {
//          List<T9FlowType> typeList = ft.getFlowTypeList(String.valueOf(tmp.getSeqId()) , dbConn);
//          int flowTypeCount = 0 ;
//          for(int i = 0; i < typeList.size(); i++) {
//            flowType = typeList.get(i);
//            boolean canShow = true;
//            if ("1".equals(flowType.getFlowType())) {
//              String seqId = String.valueOf(loginUser.getSeqId());
//              if (!(loginUser.isAdminRole() 
//                  || T9WorkFlowUtility.findId(flowType.getManageUser(), seqId)
//                  || T9WorkFlowUtility.findId(flowType.getQueryUser() , seqId)
//                  || prcsRole.prcsRoleByQuery(flowType, loginUser, dbConn))) {
//                canShow = false;
//              }
//            }
//            if (canShow) {
//              int flowId = flowType.getSeqId();
//              sb2.append("{");
//              sb2.append("flowId:\"" + flowId  + "\"");
//              sb2.append(",flowName:\"" + flowType.getFlowName() + "\"");
//              Map map = myWorkLogic.getWorkCount(flowId,loginUser.getSeqId() , dbConn);
//              int endWorkCount = (Integer)map.get("endCount");
//              int handlerWorkCount =  (Integer)map.get("handlerCount");
//              int newCount = (Integer)map.get("newCount");
//              int dealCount = (Integer)map.get("dealCount");
//              int overCount = (Integer)map.get("overCount");
//              
//              sb2.append(",newCount:" + newCount);
//              sb2.append(",dealCount:" + dealCount);
//              sb2.append(",overCount:" + overCount);
//              sb2.append(",endWorkCount:" + endWorkCount);
//              sb2.append(",handlerWorkCount:" + handlerWorkCount);
//              String flowDoc = flowType.getFlowDoc();
//              if (flowDoc == null) {
//                flowDoc = "1";
//              }
//              sb2.append(",flowDoc:" + flowDoc );
//              sb2.append(",flowType:" + flowType.getFlowType() );
//              sb2.append("},"); 
//              flowTypeCount ++;
//            }
//          }
//          if (flowTypeCount > 0) {
//            sb2.deleteCharAt(sb2.length() - 1);
//          }
//        }
        sb2.append("]");
        sb.append("{");
        sb.append("seqId:" + tmp.getSeqId());
        sb.append(",sortName:'" + tmp.getSortName() + "'");
        sb.append(",flows:" + sb2.toString());
        sb.append("},");
        count++;
      }
      if (list.size() > 0 ) {
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
  public String getQueryItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String sFlowId = request.getParameter("flowId");
    String sversionNo = request.getParameter("versionNo");
    int flowId = 0 ;
    if (T9Utility.isInteger(sFlowId)) {
      flowId = Integer.parseInt(sFlowId);
    }
    int versionNo = 1;
    if(!T9Utility.isNullorEmpty(sversionNo)) {
      versionNo = Integer.parseInt(sversionNo);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FlowWorkSearchLogic myWorkLogic = new T9FlowWorkSearchLogic();
      String result = myWorkLogic.getFormItem(flowId ,loginUser, dbConn , versionNo);
      request.setAttribute(T9ActionKeys.RET_DATA, result);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "全部取出流程分类数据！");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String doQuery(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String returnUrl = "/core/funcs/workflow/flowrun/query/doQuery.jsp";
    String sSortId =  request.getParameter("sortId");
    if (sSortId == null) {
      sSortId = "";
    }
    String sFlowId = request.getParameter("flowId");
    String condFormula = request.getParameter("condFormula");
    String sVersionNo = request.getParameter("versionNo");
    int versionNo = 1;
    if (!T9Utility.isNullorEmpty(sVersionNo)) {
      versionNo = Integer.parseInt(sVersionNo);
    }
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowWorkAdSearchLogic myWorkLogic = new T9FlowWorkAdSearchLogic();
      String skin = request.getParameter("skin");
      if (skin == null) {
        skin = "";
      }
      //--- 检查表单字段条件公式的正确性 ---
      String msg = myWorkLogic.checkFormula(condFormula, sFlowId, sSortId , skin, request.getContextPath());
      if (msg != null) {
        request.setAttribute("error", msg);
        return returnUrl;
      }
      List<Map> result = myWorkLogic.doQuery(Integer.parseInt(sFlowId) , request ,  dbConn, loginUser , versionNo );
      request.setAttribute("result", result);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return returnUrl;
  }
  public String getExcelData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String runIds = request.getParameter("runIds");
    String sFlowId = request.getParameter("flowId");
    int flowId = 0 ;
    if (T9Utility.isInteger(sFlowId)) {
      flowId = Integer.parseInt(sFlowId);
    }
    T9Person loginUser = null;
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      String flowName = logic.getFlowTypeName(flowId, conn);
      String fileName =  URLEncoder.encode("流程：" + flowName + "_-_数据报表.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();
      T9FlowWorkAdSearchLogic myWorkLogic = new T9FlowWorkAdSearchLogic();
      ArrayList<T9DbRecord > dbL = myWorkLogic.getExcelData(flowId, runIds, conn,  request.getContextPath(),loginUser);
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }
  public String flowReport(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String returnUrl = "/core/funcs/workflow/flowrun/query/flowReport.jsp";
    String sSortId =  request.getParameter("sortId");
    if (sSortId == null) {
      sSortId = "";
    }
    String sFlowId = request.getParameter("flowId");
    String condFormula = request.getParameter("condFormula");
    String listFldsStr = request.getParameter("listFldsStr");
    String isHaveCount = request.getParameter("isHaveCount");
    String sVersionNo = request.getParameter("versionNo");
    
    int versionNo = 1;
    if (!T9Utility.isNullorEmpty(sVersionNo)) {
      versionNo = Integer.parseInt(sVersionNo);
    }
    
    if("1".equals(isHaveCount)) {
      returnUrl += "?isHaveCount=" + isHaveCount;
    }
    if (listFldsStr == null) {
      listFldsStr = "";
    }
    T9Person loginUser = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      loginUser = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9FlowWorkAdSearchLogic myWorkLogic = new T9FlowWorkAdSearchLogic();
      //--- 检查表单字段条件公式的正确性 ---
      String skin = request.getParameter("skin");
      if (skin == null) {
        skin = "";
      }
      String msg = myWorkLogic.checkFormula(condFormula, sFlowId, sSortId , skin, request.getContextPath());
      if (msg != null) {
        request.setAttribute("error", msg);
        return returnUrl;
      }
      Map result = myWorkLogic.flowReport(Integer.parseInt(sFlowId) , request ,  dbConn, loginUser  ,listFldsStr , versionNo);
      Map title = (Map)result.get("title");
      List<Map> re = (List<Map>)result.get("result");
      request.setAttribute("result", re);
      request.setAttribute("title", title);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return returnUrl;
  }
}
