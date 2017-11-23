package t9.core.funcs.workflow.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.logic.T9FlowHookLogic;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
public class T9FlowHookAct {
  private T9FlowHookLogic logic=new T9FlowHookLogic();
  
  public String addHookAct(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String fId = request.getParameter("fId"); 
      Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
    
      dbConn = requestDbConn.getSysDbConn();
      this.logic.addHookLogic(dbConn,request.getParameterMap(),person);
      request.setAttribute(T9ActionKeys.RET_STATE,T9Const.RETURN_OK);     
   
    } catch (Exception ex){
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getHookListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String realPath = request.getRealPath("/");
      String data = this.logic.getHookJsonLogic(dbConn, request.getParameterMap(), person ,realPath);
      
      PrintWriter pw = response.getWriter();
      //System.out.println(data);
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String getFlowNameAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String flowId=request.getParameter("flowId");
      String data = this.logic.getFlowNameLogic(dbConn, flowId);
      data="{flowName:'"+data+"'}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteHookAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId=request.getParameter("seqId");
      this.logic.deleteHookLogic(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getHook(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String hid = request.getParameter("hid"); 
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FlowHookLogic logic = new T9FlowHookLogic();
      String realPath = request.getRealPath("/");
      String str = logic.getHook(dbConn, Integer.parseInt(hid) , realPath);
      request.setAttribute(T9ActionKeys.RET_DATA, str);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String updateHook(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String hid = request.getParameter("hid"); 
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if (!T9Utility.isNullorEmpty(hid)) {
        String hname = request.getParameter("hname");
        String hdesc = request.getParameter("hdesc");
        String status = request.getParameter("status");
        String openeddata = request.getParameter("openeddata");
        String openedplugin = request.getParameter("openedplugin");
        
        String system = request.getParameter("system");
        String query = "update FLOW_HOOK set hname='"+ hname +"',hdesc='"+ hdesc +"',status='"+status+"'";
        if ("1".equals(openeddata)) {
          String mapIn = request.getParameter("mapIn");
          String flowId = request.getParameter("flows");
          query += ",map='"+mapIn+"',flow_id='"+flowId+"'";
        }
        if ("1".equals(openedplugin)) {
          String conditionSet = request.getParameter("prcsInSet");
          String condition = request.getParameter("prcsIn").replaceAll("\r\n", ""); 
          condition = condition.replace("'", "''");
          conditionSet = conditionSet.replace("]AND", "] AND");
          conditionSet = conditionSet.replace("]OR", "] OR");
          conditionSet = conditionSet.replace("AND[", "AND [");
          conditionSet = conditionSet.replace("OR[", "OR [");
          query += ",condition_set='"+conditionSet+"',FLOW_HOOK.condition='"+condition+"'";
          if ("0".equals(system)) {
            String plugin = request.getParameter("plugin");
            query += ",plugin='"+plugin+"' ";
          }
        }
        query += "  where SEQ_ID='"+hid+"'";
        T9WorkFlowUtility.updateTableBySql(query, dbConn);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    }catch(Exception ex){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
