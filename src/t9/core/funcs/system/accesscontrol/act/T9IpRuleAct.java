package t9.core.funcs.system.accesscontrol.act;

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
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.sms.data.T9SmsBody;
import t9.core.funcs.system.accesscontrol.data.T9AccessControl;
import t9.core.funcs.system.accesscontrol.data.T9IpRule;
import t9.core.funcs.system.accesscontrol.logic.T9AccesscontrolLogic;
import t9.core.funcs.system.accesscontrol.logic.T9IpRuleLogic;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.diary.logic.T9DiaryLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9IpRuleAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.diary.T9DiaryAct");
  
  public String updateIpRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9AccesscontrolLogic orgLogic = new T9AccesscontrolLogic();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String secOcMark = request.getParameter("secOcMark");
      String beginIp = request.getParameter("beginIp");
      String endIp = request.getParameter("endIp");
      String type = request.getParameter("type");
      String remark = request.getParameter("remark");
      T9ORM t = new T9ORM();
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("type", secOcMark);
      m.put("beginIp", beginIp);
      m.put("endIp", endIp);
      m.put("type", type);
      m.put("remark", remark);
      t.updateSingle(dbConn, "ipRule", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addIpRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String secOcMark = request.getParameter("secOcMark");
      String beginIp = request.getParameter("beginIp");
      String endIp = request.getParameter("endIp");
      String remark = request.getParameter("remark");
      /* 没必要转特殊字符
      if(!T9Utility.isNullorEmpty(remark)){
        remark = remark.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
      }*/
      Map m =new HashMap();
      m.put("type", secOcMark);
      m.put("beginIp", beginIp);
      m.put("endIp", endIp);
      m.put("remark", remark);
      T9ORM t = new T9ORM();
      t.saveSingle(dbConn, "ipRule", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getIpRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9IpRuleLogic ruleLogic = new T9IpRuleLogic();
      ArrayList<T9IpRule> ruleList = new ArrayList<T9IpRule>();
      ruleList = ruleLogic.getIpRule(dbConn);
      request.setAttribute("ruleList", ruleList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    //return "/core/inc/rtjson.jsp";
    return "/core/funcs/system/accesscontrol/ip/index.jsp";
  }
  public String getEditIpRule(HttpServletRequest request,
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
      funcList.add("ipRule");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      List<Map> list = (List<Map>) map.get("IP_RULE");
      for(Map m : list) {
        String remark = (String) m.get("remark");
        remark = T9Utility.encodeSpecial(remark);
        sb.append("{");
        sb.append("seqId:\"" + m.get("seqId") + "\"");
        sb.append(",beginIp:\"" + m.get("beginIp") + "\"");
        sb.append(",endIp:\"" + m.get("endIp") + "\"");
        sb.append(",type:\"" + m.get("type") + "\"");
        sb.append(",remark:\"" + remark + "\"");
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
  
  public String deleteIpRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      Map m =new HashMap();
      m.put("seqId", seqId);
      T9ORM t = new T9ORM();
      t.deleteSingle(dbConn, "ipRule", m);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteAllIpRule(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      Map m =new HashMap();
      T9IpRuleLogic ipLogin = new T9IpRuleLogic();
      ipLogin.deleteAll(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
