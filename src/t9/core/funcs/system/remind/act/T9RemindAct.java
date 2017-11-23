package t9.core.funcs.system.remind.act;

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
import t9.core.funcs.org.data.T9Organization;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.system.censorwords.data.T9CensorModule;
import t9.core.funcs.system.data.T9SysFunction;
import t9.core.funcs.system.data.T9SysMenu;
import t9.core.funcs.system.diary.data.T9Diary;
import t9.core.funcs.system.diary.logic.T9DiaryLogic;
import t9.core.funcs.system.mobilesms.data.T9Sms2Priv;
import t9.core.funcs.system.remind.data.T9Remind;
import t9.core.funcs.system.remind.logic.T9RemindLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9RemindAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.system.diary.T9DiaryAct");
  public String getRemind(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9RemindLogic orgLogic = new T9RemindLogic();
      T9Remind org = null;
      String data = null;
      org = orgLogic.get(dbConn);
      if (org == null) {
        org = new T9Remind();
      }
      data = T9FOM.toJson(org).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getRemindCode(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"CLASS_NO='SMS_REMIND' AND CLASS_CODE!='0' order by SORT_NO asc"};
      List funcList = new ArrayList();
      funcList.add("codeItem");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("CODE_ITEM"));
      if(list.size() > 1){
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",codeNo:\"" + m.get("classCode") + "\"");
          sb.append(",codeName:\"" + m.get("classDesc") + "\"");
          sb.append(",codeOrder:\"" + m.get("sortNo") + "\"");
          sb.append("},");
        }
        sb.deleteCharAt(sb.length() - 1); 
      }else{
        for(Map m : list){
          sb.append("{");
          sb.append("seqId:\"" + m.get("seqId") + "\"");
          sb.append(",codeNo:\"" + m.get("classCode") + "\"");
          sb.append(",codeName:\"" + m.get("classDesc") + "\"");
          sb.append(",codeOrder:\"" + m.get("sortNo") + "\"");
          sb.append("}");
        }
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
  
  public String updateRemind(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9RemindLogic orgLogic = new T9RemindLogic();
      String remindSum = request.getParameter("remindSum");
      int seqId = Integer.parseInt(request.getParameter("seqIdRemind"));
      //System.out.println(remindSum+":::"+seqId);
      orgLogic.updateRemind(dbConn, seqId, remindSum);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String addDiary(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DiaryLogic orgLogic = new T9DiaryLogic();
      String statrTime = request.getParameter("statrTime");
      String endTime = request.getParameter("endTime");
      //Date d =new SimpleDateFormat("yyyy-MM-dd").parse(endTime);
      String days = request.getParameter("days");
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      String sumStr = statrTime.substring(0,10)+","+endTime.substring(0,10)+","+days;
      //System.out.println(sumStr);
      orgLogic.add(dbConn, sumStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"工作日志设置已修改");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DiaryLogic orgLogic = new T9DiaryLogic();
      T9Diary org = null;
      String data = null;
      org = orgLogic.getNotify(dbConn);
      if (org == null) {
        org = new T9Diary();
      }
      data = T9FOM.toJson(org).toString();
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String getRemindPriv(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String loginUserId = String.valueOf(person.getSeqId());
      T9ORM orm = new T9ORM();
      String[] moduleFalg = null;
      String privFlag = "0";
      ArrayList<T9Sms2Priv> priv = (ArrayList<T9Sms2Priv>) orm.loadListSingle(dbConn, T9Sms2Priv.class, moduleFalg);
      if(priv.size() == 0){
        privFlag = "0";
      }else{
        String[] privStr = T9Utility.null2Empty(priv.get(0).getRemindPriv()).split(",");
        for(int i = 0; i < privStr.length; i++){
          if(privStr[i].equals(loginUserId)){
            privFlag = "1";
          }
        }
      }
      //System.out.println(privFlag+"JHYUIKJHYUIKJ");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, privFlag);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
