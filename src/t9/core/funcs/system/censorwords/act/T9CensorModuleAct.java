package t9.core.funcs.system.censorwords.act;

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
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.censorwords.data.T9CensorModule;
import t9.core.funcs.system.censorwords.data.T9CensorWords;
import t9.core.funcs.system.censorwords.logic.T9CensorModuleLogic;
import t9.core.funcs.system.censorwords.logic.T9CensorWordsLogic;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.extuser.data.T9ExtUser;
import t9.core.funcs.system.extuser.logic.T9ExtUserLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9CensorModuleAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.extuser.T9ExtUserAct");
  public String insertCensorModule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      String useFlag = request.getParameter("useFlag"); 
      String smsRemind = request.getParameter("smsRemind"); 
      String sms2Remind = request.getParameter("sms2Remind");
      String checkUser = request.getParameter("checkUser");
      String bannedHint = request.getParameter("bannedHint");
      String modHint = request.getParameter("modHint");
      String filterHint = request.getParameter("filterHint");
      String moduleCode = request.getParameter("moduleCode");
      Map m =new HashMap();
      
      m.put("checkUser", checkUser);
      m.put("bannedHint", bannedHint);
      m.put("modHint", modHint);
      m.put("moduleCode", moduleCode);
      m.put("filterHint", filterHint);
      m.put("useFlag", useFlag);
      m.put("smsRemind", smsRemind);
      m.put("sms2Remind", sms2Remind);
      T9ORM t = new T9ORM();
      
      T9CensorModuleLogic cwLogic = new T9CensorModuleLogic();
      if(cwLogic.existsCensorModule(dbConn, moduleCode)){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "错误  该模块已经存在！");
        return "/core/inc/rtjson.jsp";
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
      }

      t.saveSingle(dbConn, "censorModule", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String updateSingleWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      String find = request.getParameter("find");
      String replacement = request.getParameter("replacement");
    
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("find", find);
      m.put("replacement", replacement);
      orgLogic.updateSingleWords(dbConn, find, replacement);
      //t.updateSingle(dbConn, "censorWords", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getCensorModule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String userName = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int seqId = person.getSeqId();
      userName = person.getUserName();
      T9CensorModuleLogic extLogic = new T9CensorModuleLogic();
      ArrayList<T9CensorModule> moduleList = new ArrayList<T9CensorModule>();
      
      moduleList = extLogic.getCensorModule(dbConn);
      //System.out.println(moduleList.size()+"UTUGIUHIOJ");
      request.setAttribute("moduleList", moduleList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/censorwords/module/index.jsp?userName="+userName;
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
  
  public String getCensorModuleId(HttpServletRequest request,
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
      funcList.add("censorModule");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("CENSOR_MODULE");
      for(Map m : list) {
        String bannedHint = (String) m.get("bannedHint");
        if(!T9Utility.isNullorEmpty(bannedHint)){
          bannedHint = T9Utility.encodeSpecial(bannedHint);
        }
        String modHint = (String) m.get("modHint");
        if(!T9Utility.isNullorEmpty(modHint)){
          //modHint = modHint.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          modHint = T9Utility.encodeSpecial(modHint);
        }
        String filterHint = (String) m.get("filterHint");
        if(!T9Utility.isNullorEmpty(filterHint)){
          //filterHint = filterHint.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          filterHint = T9Utility.encodeSpecial(filterHint);
        }
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",moduleCode:\"" + m.get("moduleCode") + "\"");
        sb.append(",useFlag:\"" + m.get("useFlag") + "\"");
        sb.append(",checkUser:\"" + m.get("checkUser") + "\"");
        sb.append(",smsRemind:\"" + m.get("smsRemind") + "\"");
        sb.append(",sms2Remind:\"" + m.get("sms2Remind") + "\"");
        sb.append(",bannedHint:\"" + (m.get("bannedHint") == null ? "" : bannedHint) + "\"");
        sb.append(",modHint:\"" + (m.get("modHint") == null ? "" : modHint) + "\"");
        sb.append(",filterHint:\"" + (m.get("filterHint") == null ? "" : filterHint) + "\"");
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
  
  public String updateCensorModule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic orgLogic = new T9CensorWordsLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String moduleCode = request.getParameter("moduleCode");
      String useFlag = request.getParameter("useFlag");
      String smsRemind = request.getParameter("smsRemind");
      String sms2Remind = request.getParameter("sms2Remind");
      String checkUser = request.getParameter("checkUser");
      String bannedHint = request.getParameter("bannedHint");
      //bannedHint = T9Utility.encodeSpecial(bannedHint);
      String modHint = request.getParameter("modHint");
      //modHint = T9Utility.encodeSpecial(modHint);
      String filterHint = request.getParameter("filterHint");
      //filterHint = T9Utility.encodeSpecial(filterHint);
      String moduleCodeOld = request.getParameter("moduleCodeOld");
    
      if(!moduleCode.equals(moduleCodeOld)){
        T9CensorModuleLogic cwLogic = new T9CensorModuleLogic();
        if(cwLogic.existsCensorModule(dbConn, moduleCode)){
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "错误  该模块已经存在！");
          return "/core/inc/rtjson.jsp";
        }else{
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
          request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功");
        }
      }
      
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("moduleCode", moduleCode);
      m.put("useFlag", useFlag);
      m.put("smsRemind", smsRemind);
      m.put("sms2Remind", sms2Remind);
      m.put("checkUser", checkUser);
      m.put("bannedHint", bannedHint);
      m.put("modHint", modHint);
      m.put("filterHint", filterHint);
      t.updateSingle(dbConn, "censorModule", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteModuleWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("idStrs");
      T9CensorModuleLogic wordsLogic = new T9CensorModuleLogic();
      wordsLogic.deleteAll(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteAllCensorWords(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = person.getSeqId();
      T9CensorWordsLogic wordsLogic = new T9CensorWordsLogic();
      if(userId == 196){//管理员seqId
        wordsLogic.deleteAllWords(dbConn);
      }else{
        wordsLogic.deleteAllFast(dbConn, userId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
