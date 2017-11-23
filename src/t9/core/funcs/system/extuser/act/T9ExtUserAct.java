package t9.core.funcs.system.extuser.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.accesscontrol.logic.T9AccesscontrolLogic;
import t9.core.funcs.system.accesscontrol.logic.T9IpRuleLogic;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.diary.logic.T9DiaryLogic;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.system.extuser.logic.T9ExtUserLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9ExtUserAct {
  private static Logger log = Logger.getLogger("cc.t9.core.funcs.system.extuser.T9ExtUserAct");
  public String addExtUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String password = request.getParameter("password");
      String useFlagstr = request.getParameter("useFlagstr");
      String moduleSmsStr = request.getParameter("moduleSmsStr");
      String moduleWorkflowStr = request.getParameter("moduleWorkflowStr");
      String postfix = request.getParameter("postfix");
      String remark = request.getParameter("remark");
      String sumStr = "";
      T9ExtUserLogic dl = new T9ExtUserLogic();
      if(dl.existsTableNo(dbConn, userId)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "错误  用户名"+userId+"以存在，请重新填写！");
        return "/core/inc/rtjson.jsp";
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }
      if(moduleSmsStr.equals("1") && moduleWorkflowStr.equals("4")){
        sumStr = moduleSmsStr+","+moduleWorkflowStr+",";
      }else if(moduleSmsStr.equals("1")&&moduleWorkflowStr.equals("0")){
        sumStr = moduleSmsStr;
      }else if(moduleSmsStr.equals("0")&&moduleWorkflowStr.equals("4")){
        sumStr = moduleWorkflowStr;
      }else if(moduleSmsStr.equals("0")&&moduleWorkflowStr.equals("0")){
        sumStr = "";
      }
      
      Map m =new HashMap();
      m.put("userId", userId);
      m.put("password", password);
      m.put("useFlag", useFlagstr);
      m.put("authModule", sumStr);
      m.put("postfix", postfix);
      m.put("remark", remark);
      m.put("sysUser", "0");
      T9ORM t = new T9ORM();
      
      t.saveSingle(dbConn, "extUser", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getExtUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ExtUserLogic extLogic = new T9ExtUserLogic();
      ArrayList<T9ExtUser> extList = new ArrayList<T9ExtUser>();
      extList = extLogic.getExtUser(dbConn);
      request.setAttribute("extList", extList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/extuser/manage.jsp";
  }
  
  public String getCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ExtUserLogic extLogic = new T9ExtUserLogic();
      long sum = 0;
      sum = extLogic.existsCount(dbConn, 0);
      StringBuffer sb = new StringBuffer("[");
      sb.append("{");
      sb.append("sum:\"" + sum + "\"");
      sb.append("}");
      sb.append("]");
      request.setAttribute("extListSum", sum);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
    //return "/core/funcs/system/extuser/manage.jsp";
  }
  
  public String getEditExtUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      ArrayList<T9UserPriv> perList = null;
      String[] filters = new String[]{"SEQ_ID="+seqId};
      List funcList = new ArrayList();
      funcList.add("extUser");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("EXT_USER");
      for(Map m : list) {
        
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",userId:\"" + m.get("userId") + "\"");
        sb.append(",authModule:\"" + m.get("authModule") + "\"");
        sb.append(",useFlag:\"" + m.get("useFlag") + "\"");
        sb.append(",postfix:\"" + (m.get("postfix") == null ? "" : T9Utility.encodeSpecial((String)m.get("postfix"))) + "\"");
        sb.append(",remark:\"" + (m.get("remark") == null ? "" : T9Utility.encodeSpecial((String)m.get("remark"))) + "\"");
        sb.append("}");
      }       
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateExtUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AccesscontrolLogic orgLogic = new T9AccesscontrolLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String userId = request.getParameter("userId");
      String password = request.getParameter("password");
      String useFlagstr = request.getParameter("useFlagstr");
      String remark = request.getParameter("remark");
      String moduleSmsStr = request.getParameter("moduleSmsStr");
      String moduleWorkflowStr = request.getParameter("moduleWorkflowStr");
      String postfix = request.getParameter("postfix");
      String sumStr = "";
      if(moduleSmsStr.equals("1") && moduleWorkflowStr.equals("4")){
        sumStr = moduleSmsStr+","+moduleWorkflowStr+",";
      }else if(moduleSmsStr.equals("1")&&moduleWorkflowStr.equals("0")){
        sumStr = moduleSmsStr;
      }else if(moduleSmsStr.equals("0")&&moduleWorkflowStr.equals("4")){
        sumStr = moduleWorkflowStr;
      }else if(moduleSmsStr.equals("0")&&moduleWorkflowStr.equals("0")){
        sumStr = "";
      }
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("userId", userId);
      m.put("password", password);
      m.put("useFlag", useFlagstr);
      m.put("authModule", sumStr);
      m.put("postfix", postfix);
      m.put("remark", remark);
      t.updateSingle(dbConn, "extUser", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteExtUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("idStrs"));
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      //t.deleteSingle(dbConn, "extUser", m);
      T9ExtUserLogic extLogic = new T9ExtUserLogic();
      extLogic.deleteAll(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
