package t9.core.funcs.userinfo.act;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.diary.data.T9Diary;
import t9.core.funcs.diary.logic.T9DiaryLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.userinfo.logic.T9UserInfoLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;

public class T9UserInfoAct {
      private T9UserInfoLogic logic=new T9UserInfoLogic();

      /**
       * 获取登录用户菜单权限
       * 
       * @param request
       * @param response
       * @return
       * @throws Exception
       */
 public String getFuncStrAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn=null;
  try{
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   dbConn = requestDbConn.getSysDbConn();
   String userId = request.getParameter("userId");
   int uId = 0 ;
   if (T9Utility.isInteger(userId)) {
     uId = Integer.parseInt(userId);
   }
   T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
   String userPriv=person.getUserPriv();
   String login_funcs_str=this.logic.getFuncStrLogic(dbConn,userPriv);
  // userId
   T9MyPriv mp = new T9MyPriv();
   mp = T9PrivUtil.getMyPriv(dbConn, person, "3", 2);
   boolean isShow = false;
   if (T9PrivUtil.isUserPriv(dbConn, uId, mp, person)) {
     isShow = true;
   }
   login_funcs_str="{login_funcs_str:'"+login_funcs_str+"' , isShow:"+ isShow +"}";

   request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
   request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
   request.setAttribute(T9ActionKeys.RET_DATA, login_funcs_str);
  } catch (Exception ex) {
   request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
   request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
   throw ex;
 }
   return "/core/inc/rtjson.jsp";
 }
 
 /**
  * 获取查看用户的详细信息
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
public String getUserDetailAct(HttpServletRequest request, HttpServletResponse response) throws Exception {
Connection dbConn=null;
try{
T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
dbConn = requestDbConn.getSysDbConn();
T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);

String userId=request.getParameter("userId");
if(T9Utility.isNullorEmpty(userId)){
  userId=person.getSeqId()+"";
}
T9Person login_user=this.logic.getUserDetailLogic(dbConn,person,userId);

if (login_user == null) {
  request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
  request.setAttribute(T9ActionKeys.RET_MSRG, "未找到相应记录");
  return "/core/inc/rtjson.jsp";
}

StringBuffer data = T9FOM.toJson(login_user);
//System.out.println(data);
request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
} catch (Exception e) {
request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
throw e;
}
return "/core/inc/rtjson.jsp";
}
 /**
  * 获取共享日志
  * 
  * 
  * */

/**
 * 列出当前用户最新的十条工作日志
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
public String getDiaryShare(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    int login_user_Id = person.getSeqId();
    String userId=request.getParameter("userId");
    T9DiaryLogic dl = new T9DiaryLogic();
    List<T9Diary> diaryList = this.logic.getDiaryShareLogic(dbConn, login_user_Id,userId);
    StringBuffer data = dl.toJson(dbConn, diaryList);
    
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
  } catch (Exception ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
    throw ex;
  }
  return "/core/inc/rtjson.jsp";
}

/**
 * 日程安排
 * 
 * */
public String selectCalendarByTerm(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();

    String userId = request.getParameter("userId");

    T9Person user = (T9Person) request.getSession().getAttribute(
        T9Const.LOGIN_USER);
    int login_userId = user.getSeqId();
    T9CalendarLogic tcl = new T9CalendarLogic();
    List<T9Calendar> calendarList =this.logic.selectCalendarByTerm(dbConn, userId);
    List<Map<String, String>> calendarListCopy = new ArrayList<Map<String, String>>();
    Date date = new Date();
    long dateTime = date.getTime();
    long begin = 0;
    long end = 0;
    for (int i = 0; i < calendarList.size(); i++) {
      String status = "0";// 进行中 判断判断状态

      Map<String, String> map = new HashMap<String, String>();
      T9Calendar calendar = calendarList.get(i);
      map.put("seqId", String.valueOf(calendar.getSeqId()));
      map.put("userId", calendar.getUserId());
      map.put("calLevel", calendar.getCalLevel());
      map.put("calType", calendar.getCalType());
      map.put("content", calendar.getContent());
      map.put("managerId", calendar.getManagerId());
      if (calendar.getCalTime() != null) {
        map.put("calTime", dateFormat.format(calendar.getCalTime()));
      } else {
        map.put("calTime", "");
      }
      if (calendar.getEndTime() != null) {
        map.put("endTime", dateFormat.format(calendar.getEndTime()));
      } else {
        map.put("endTime", "");
      }

      map.put("overStatus", calendar.getOverStatus());
      // System.out.println(calendar.getManagerId());
      if (calendar.getManagerId() != null) {
        T9PersonLogic tpl = new T9PersonLogic();
        map.put("managerName", tpl.getNameBySeqIdStr(calendar.getManagerId(),
            dbConn));
      } else {
        map.put("managerName", "");
      }
      String overStatus1 = calendar.getOverStatus();
      if (overStatus1 == null || overStatus1.equals("0")
          || overStatus1.trim().equals("")) {
        begin = calendar.getCalTime().getTime();
        end = calendar.getEndTime().getTime();
        if (dateTime < begin) {
          status = "1";// 未开始

        }
        if (dateTime > end) {
          status = "2";// 超时
        }
      }
      map.put("status", status);
      calendarListCopy.add(map);
    }
    // Map<String,String> map = tcl.selectPersonById(dbConn, userId);
    request.setAttribute("calendarList", calendarListCopy);
    // request.setAttribute("person", map);
  } catch (Exception ex) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
    throw ex;
  }

  return "/core/funcs/userinfo/showcalendar.jsp";
}
public String getUserPrivAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String userPriv=request.getParameter("userPriv");
    
    String data = this.logic.getUserPrivLogic(dbConn, userPriv);
  
     data="{userName:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}


public String getDeptAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String deptId=request.getParameter("deptId");
    
    String data = this.logic.getDeptNameLogic(dbConn,deptId);
  
     data="{deptName:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}

public String getAvatarAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String userId=request.getParameter("userId");
    String data = this.logic.getAvatarLogic(dbConn,userId);
  
    if(data==null){
        data="";
    }
    
     data="{hrms_photo:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}

public String getDeptNoAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String deptId=request.getParameter("deptId");
    String[] data = this.logic.getDeptTelNoLogic(dbConn, deptId);
    if(data[0] == null){
        data[0] ="";
    }
    if(data[1] == null){
      data[1] ="";
    }
    String data1 = "{deptNo:'"+data[0]+"',faxNo:'"+ data[1] +"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data1);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}

public String getOnStatusAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String userId=request.getParameter("userId");
    String data = this.logic.getOnStatusLogic(dbConn,userId);
     
     data="{status:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}

public String getAuatarExitAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {

  try {
   
    String photo=request.getParameter("photo");
    String data = this.logic.getAuatarExitLogic(photo);
     
     data="{exit:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}


public String getUserPrivOtherNameAct(HttpServletRequest request,
    HttpServletResponse response) throws Exception {
  Connection dbConn = null;
  try {
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    dbConn = requestDbConn.getSysDbConn();
   
    String id=request.getParameter("id");
    String data = this.logic.getUserPrivOtherNameLogic(dbConn,id);
  
    if(data==null){
        data="";
    }
   
     data="{name:'"+data+"'}";
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
    request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
    }
  return "/core/inc/rtjson.jsp";
}

}
