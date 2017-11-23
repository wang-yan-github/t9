package t9.core.funcs.calendar.info.act;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.manage.logic.T9ManageOutLogic;
import t9.core.funcs.calendar.act.T9AffairAct;
import t9.core.funcs.calendar.act.T9CalendarAct;
import t9.core.funcs.calendar.data.T9Affair;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.data.T9Task;
import t9.core.funcs.calendar.info.logic.T9InfoLogic;
import t9.core.funcs.calendar.logic.T9AffairLogic;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.calendar.logic.T9TaskLogic;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.modulepriv.data.T9ModulePriv;
import t9.core.funcs.modulepriv.logic.T9ModuleprivLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.data.T9UserPriv;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;

public class T9InfoAct {
  /**
   * 根据用户的管理权限得到所有部门（日程安排）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptByParentId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      int userDeptId = user.getDeptId();
      T9DeptLogic deptLogic = new T9DeptLogic();
      String userDeptName = deptLogic.getNameByIdStr(String.valueOf(userDeptId), dbConn);
      String deptId = request.getParameter("deptId");
      if(deptId.equals("")){
       deptId = "0";
      }
      //得到辅助管理范围
      String data = "";
      if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
        data =  deptLogic.getDeptTreeJson(0,dbConn) ;
        
      }else{
        List<T9ModulePriv> moduleList = getModulePrive(dbConn, userId, request, response);
        
        if(moduleList.size()>0){
          T9ModulePriv modulePriv = moduleList.get(0);
          String deptPriv = modulePriv.getDeptPriv();
          String RolrPriv = modulePriv.getRolePriv();
          String deptIdPriv = modulePriv.getDeptId();
          String privId = modulePriv.getPrivId();
          String userIdPriv = modulePriv.getUserId();
          if(deptPriv.equals("0")){
            //data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
            String[] postDeptArray = {String.valueOf(userDeptId)};
            data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
          }
          if(deptPriv.equals("1")){
            data =  deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn) ;
          }
          if(deptPriv.equals("2")){
            if(deptIdPriv==null||deptIdPriv.equals("")){
              data = "[]";
            }else{
              String[] postDeptArray = deptIdPriv.split(",");
              data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
            }
          }
          if(deptPriv.equals("3")){
            data = "[{text:0,value:0}]";
          }
          if(deptPriv.equals("4")){
            data = "[{text:0,value:0}]";
          }
        }else{
          if(postpriv.equals("0")){
            //data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
            String[] postDeptArray = {String.valueOf(userDeptId)};
            data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
          }
          if(postpriv.equals("1")){
            data =  deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn) ;
          }
          if(postpriv.equals("2")){
            if(postDept==null||postDept.equals("")){
              data = "[]";
            }else{
               String[] postDeptArray = postDept.split(",");
               data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";

            }
          }
        }
      }
      
      if(data.equals("")){
        data = "[]";
      }
      data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replace("\r", "");
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId));
      request.setAttribute(T9ActionKeys.RET_DATA, data.replace("&nbsp;" , " "));
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据用户的管理权限得到所有部门（日程安排-工作日志）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToPlan(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9DeptLogic deptLogic = new T9DeptLogic();
      //得到查看人员的SEQ_ID
      String userIdStr = request.getParameter("userId");
      T9PersonLogic personLogic = new T9PersonLogic();
      //得到该人员的部门
      T9Person personTemp = null;
      String personTempDeptId = "";
      if(userIdStr!=null&&!userIdStr.equals("")){
         personTemp = personLogic.getPerson(dbConn, userIdStr);
         personTempDeptId = String.valueOf(personTemp.getDeptId());
      }
      
      String data = "";
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      int userDeptId = user.getDeptId();
    
      String userDeptName = deptLogic.getNameByIdStr(String.valueOf(userDeptId), dbConn);
      String deptId = request.getParameter("deptId");
      if(personTemp!=null){
        if(deptId.equals("")){
         deptId = "0";
        }
        //得到辅助管理范围
        if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
          data =  deptLogic.getDeptTreeJson(0,dbConn) ;
        }else{
          List<T9ModulePriv> moduleList = getModulePrive(dbConn, userId, request, response);
          if(moduleList.size()>0){
            T9ModulePriv modulePriv = moduleList.get(0);
            String deptPriv = modulePriv.getDeptPriv();
            String RolrPriv = modulePriv.getRolePriv();
            String deptIdPriv = modulePriv.getDeptId();
            String privId = modulePriv.getPrivId();
            String userIdPriv = modulePriv.getUserId();
            if(deptPriv.equals("0")){
              //data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
              String[] postDeptArray = {String.valueOf(userDeptId)};
              data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
            }
            if(deptPriv.equals("1")){
              if(!deptId.equals("0")){
                if(deptId.equals(personTemp.getDeptId())){
                  data =  deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn) ;
                }
              }else{
                data =  deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn) ;
              }
            }
            if(deptPriv.equals("2")){
              if(deptIdPriv==null||deptIdPriv.equals("")){
                data = "[]";
              }else{
                String[] postDeptArray = deptIdPriv.split(",");
                for (int i = 0; i < postDeptArray.length; i++) {
                  if(postDeptArray[i].equals(personTemp.getDeptId())){
                    data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
                    break;
                  }
                }
              }
            }
            if(deptPriv.equals("3")){
              data = "[{text:0,value:0}]";
            }
            if(deptPriv.equals("4")){
              data = "[{text:0,value:0}]";
            }
          }else{
            if(postpriv.equals("0")){
              if(deptId.equals(personTemp.getDeptId())){
                //data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
                String[] postDeptArray = {String.valueOf(userDeptId)};
                data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
              }
            }
            if(postpriv.equals("1")){
              
              data =  deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn) ;
            }
            if(postpriv.equals("2")){
              if(postDept==null||postDept.equals("")){
                data = "[]";
              }else{
                 String[] postDeptArray = postDept.split(",");
                 for (int i = 0; i < postDeptArray.length; i++) {
                   if(postDeptArray[i].equals(personTemp.getDeptId())){
                      data =  "[" + deptLogic.getDeptTreeJson(Integer.parseInt(deptId),dbConn,postDeptArray)+ "]";
                   }
                 }
              }
            }
          }
        }

      
      }else{
        data = "[]";
      }
      if(data.equals("")){
        data = "[]";
      }
      data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replace("\r", "");
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,personTempDeptId);
      request.setAttribute(T9ActionKeys.RET_DATA, data.replace("&nbsp;" , " "));
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 根据用户的管理权限得到所有部门（考勤统计）
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectDeptToAttendance(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      int userDeptId = user.getDeptId();
      T9DeptLogic deptLogic = new T9DeptLogic();
      String userDeptName = deptLogic.getNameByIdStr(String.valueOf(userDeptId), dbConn);
      String data = "";
      if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
        data =  deptLogic.getDeptTreeJson(0,dbConn) ;
        
      }else{
        if(postpriv.equals("0")){
         // data = "[{text:\"" + userDeptName + "\",value:" + userDeptId + "}]";
          String[] postDeptArray = {String.valueOf(userDeptId)};
          data =  "[" + deptLogic.getDeptTreeJson(0,dbConn,postDeptArray)+ "]";
        }
        if(postpriv.equals("1")){
          data =  deptLogic.getDeptTreeJson(0,dbConn) ;
        }
        if(postpriv.equals("2")){
          if(postDept==null||postDept.equals("")){
            data = "[]";
          }else{
             String[] postDeptArray = postDept.split(",");
             data =  "[" + deptLogic.getDeptTreeJson(0,dbConn,postDeptArray)+ "]";

          }
        }
      }
      if(data.equals("")){
        data = "[]";
      }
      data = data.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r\n", "").replace("\n", "").replace("\r", "");
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userDeptId)+","+postpriv);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 根据一个部门的Id得到本部门所有人的日程安排
   */
  public String selectCalendarByDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIds = request.getParameter("userIds");
      //得到本人的ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();

      T9PersonLogic personLogic = new T9PersonLogic();
    
      List<T9Person> personList = new ArrayList<T9Person>();

      String userJson = "[";
      String userNames = personLogic.getNameBySeqIdStr(userIds, dbConn);
      T9InfoLogic infoLogic= new T9InfoLogic();

      List<T9Person> personListTemp = infoLogic.getPersonByIds(dbConn, userIds);
    /*  if(!userNames.equals("")){
        userNames = userNames.substring(0, userNames.length()-1);
      }*/
      if(!userIds.equals("")){
        for (int i = 0; i <personListTemp.size() ; i++) {
          T9Person person = personListTemp.get(i);
          String userName = "";
          userName = person.getUserName();
          if(userName!=null&&!userName.equals("")){
            userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          userJson = userJson + "{value:\"" + person.getSeqId() + "\",name:\"" + userName + "\"},";
        }
      }
      if(!userIds.equals("")){
        userJson = userJson.substring(0, userJson.length()-1);
      }
      userJson = userJson + "]";
      Date dateCur = new Date();
      long dateTime = dateCur.getTime();    
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormatday = new SimpleDateFormat("yyyy-MM-dd");
      String year =  request.getParameter("year");
      String month = request.getParameter("month");
      String day = request.getParameter("day");
      String date = year+"-"+month+"-"+day;
      String status1 = request.getParameter("status");
      String dateStr1 = date + " 00:00:00";
      String dateStr2 = date + " 23:59:59";
      //System.out.println(dateStr1);
      String calTime1 = T9DBUtility.getDateFilter("CAL_TIME", dateStr1, ">=");
      String calTime2 = T9DBUtility.getDateFilter("CAL_TIME", dateStr2, "<=");
      String endTime1 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      String endTime2 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      List<T9Calendar> calendarList= infoLogic.selectCalendarByDept(dbConn,userIds,calTime1,calTime2,endTime1, endTime2,status1);
      String data = "[";
      for (int i = 0; i < calendarList.size(); i++) {
        T9Calendar calendar = new T9Calendar();
        calendar = calendarList.get(i);
        long begin = 0;
        long end = 0;
        int status = 0;//进行中 判断判断状态
        //System.out.println(calendar.getCalTime());
        begin = calendar.getCalTime().getTime();
        end = calendar.getEndTime().getTime();  
        if(dateTime<begin){
          status = 1;//未开始
        }
        if(dateTime>end){
          status = 2;//超时
        }
        calendar = calendarList.get(i);
        //判断是否跨天,并且判断是哪种跨天
        int dayStatus = 0;//没跨天
        //System.out.println(calendar.getSeqId());
        if(!dateFormatday.format(calendar.getCalTime()).equals(dateFormatday.format(calendar.getEndTime()))){
          if(date.compareTo(dateFormatday.format(calendar.getCalTime()))>0&&date.compareTo(dateFormatday.format(calendar.getEndTime()))==0){
            dayStatus = 1;//过期跨天
          }
          //System.out.println(dateFormatday.format(calendar.getEndTime()).substring(0, 10));
          //System.out.println(date);
          //System.out.println(date.compareTo(dateFormatday.format(calendar.getEndTime()).substring(0, 10))<0);
          if(date.compareTo(dateFormatday.format(calendar.getCalTime()))==0&&date.compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 2;//未过跨天
          }
          if(date.compareTo(dateFormatday.format(calendar.getCalTime()))>0&&date.compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 3;//跨天
          }
        }
        //System.out.println(calendar.getUserId());
        String userName = personLogic.getNameBySeqIdStr(calendar.getUserId(), dbConn);
        if(userName!=null&&!userName.equals("")){
          userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String managerName = T9InfoLogic.getUserName(calendar.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          
        }
        T9ManageOutLogic managerLogic = new T9ManageOutLogic();
        String deptName = managerLogic.selectByUserIdDept(dbConn, calendar.getUserId());
        if(deptName!=null&&!deptName.equals("")){
          deptName = deptName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        data = data + T9FOM.toJson(calendar).substring(0, T9FOM.toJson(calendar).length()-1)+",dayStatus:" + dayStatus+",status:" + status +",managerName:\""+managerName+"\",userName:\""+userName+"\",deptName:\""+deptName+ "\"},";
      }
      if(calendarList.size()>0){
        data = data.substring(0, data.length()-1);
      }
     // data = data + "{" + userJson + "}]";
     data = data + "]";
     data = "{data:"+data+",users:"+userJson +"}";
      //System.out.println(data);      
      //request.setAttribute("calendarList", calendarList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  public String getUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      //得到本人的ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色的seqId
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      //根据角色的seqId得到角色的序号
      T9UserPrivLogic userPrivLogic = new T9UserPrivLogic();
      T9UserPriv userPrivClass = null;
      int privNo = 0;
      if(userPriv!=null&&!userPriv.trim().equals("")){
        userPrivClass = userPrivLogic.getRoleById(Integer.parseInt(userPriv), dbConn);
        privNo = userPrivClass.getPrivNo();
      }
  
      //得到部门Id
      String deptId = request.getParameter("deptId");
      //System.out.println(deptId);
      if(deptId.equals("")){
       deptId = "0";
      }
      //有部门得到所有人员的Ids;
   
      T9PersonLogic personLogic = new T9PersonLogic();
      T9InfoLogic infoLogic = new T9InfoLogic();
      List<T9Person> personList = new ArrayList<T9Person>();

      String userIds = "";
      String userJson = "[";
      if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
        String[]tempStr = {"DEPT_ID = " + deptId };
        personList = personLogic.getPersonByPriv(dbConn, tempStr);
      }else{
        List<T9ModulePriv> moduleList = getModulePrive(dbConn, userId, request, response);//模块权限
        
        if(moduleList.size()>0){
          T9ModulePriv modulePriv = moduleList.get(0);
          String deptPriv = modulePriv.getDeptPriv();
          String RolrPriv = modulePriv.getRolePriv();
          String deptIdPriv = modulePriv.getDeptId();
          String privId = modulePriv.getPrivId();
          String userIdPriv = modulePriv.getUserId();
          if(deptPriv.equals("3")){
            //userIds = userIdPriv;
            String[] userIdArray = userIdPriv.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              T9Person person = personLogic.getPerson(dbConn,userIdArray[i]);
              T9UserPriv userPrivClassTemp = null;
              int privNoTemp = 0;
              if(person.getUserPriv()!=null&&!person.getUserPriv().trim().equals("")){
                userPrivClassTemp = userPrivLogic.getRoleById(Integer.parseInt(person.getUserPriv()), dbConn);
                privNoTemp = userPrivClassTemp.getPrivNo();
              }
              privNo = userPrivClass.getPrivNo();
              if(RolrPriv.equals("0")){
                if((privNo-privNoTemp)<0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("1")){
                if((privNo-privNoTemp)<=0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("2")){
                userIds = userIds + userIdArray[i] + ",";
              }
              if(RolrPriv.equals("3")){
                if(person.getUserPriv().equals(privId)){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
            }
          }else{
            if(RolrPriv.equals("0")){
             // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
              //personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
            }
            if(RolrPriv.equals("1")){
              //String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV >= " + userPriv };
             // personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">=", deptId);
            }
            if(RolrPriv.equals("2")){
              String[]tempStr = {"DEPT_ID = " + deptId };
              personList = personLogic.getPersonByPriv(dbConn, tempStr);
            }
            if(RolrPriv.equals("3")){
              if(!privId.trim().equals("")){
                String[] privIdArray = privId.split(",");
                String newPrivId = "";
                for (int i = 0; i < privIdArray.length; i++) {
                  newPrivId = newPrivId +"'" + privIdArray[i] + "',";
                }
                newPrivId = newPrivId.substring(0, newPrivId.length()-1);
                String[]tempStr = {"DEPT_ID = " + deptId ,"USER_PRIV in (" + newPrivId + ")"};
                personList = personLogic.getPersonByPriv(dbConn, tempStr);
              }
         
            }
          }
    
        }else{
         // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
         // personList = personLogic.getPersonByPriv(dbConn, tempStr);
          personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
          
        }
       
      }
      for (int i = 0; i < personList.size(); i++) {
        userIds = userIds + personList.get(i).getSeqId() + ",";
      }
      //System.out.println(userIds);
      if(!userIds.equals("")){
        userIds  = userIds.substring(0, userIds.length()-1);
      }
      String userNames = personLogic.getNameBySeqIdStr(userIds, dbConn);
    /*  if(!userNames.equals("")){
        userNames = userNames.substring(0, userNames.length()-1);
      }*/
   /*   if(!userIds.equals("")){
        for (int i = 0; i <userIds.split(",").length ; i++) {
          userJson = userJson + "{value:\"" + userIds.split(",")[i] + "\",name:\"" + userNames.split(",")[i] + "\"},";
        }
      }
      if(!userIds.equals("")){
        userJson = userJson.substring(0, userJson.length()-1);
      }
      userJson = userJson + "]";*/
      String data = "{userId:\"" + userIds + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
    
  }
  
  public List<T9ModulePriv>  getModulePrive(Connection dbConn,int userId,HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    List<T9ModulePriv>  module =  T9ModuleprivLogic.selectModulePriv(dbConn, String.valueOf(userId));
    return module;
  }
  //日程 :根据部门按周显示
  public String selectCalendarByDeptWeek(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Date dateCur = new Date();
      long dateTime = dateCur.getTime();    
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormatday = new SimpleDateFormat("yyyy-MM-dd");
      String dateCurStr = dateFormatday.format(dateCur);
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year =  request.getParameter("year");
      String weekth = request.getParameter("weekth");
      String status1 = request.getParameter("status");
      String userIds = request.getParameter("userIds");
      //String userIds = T9InfoLogic.getUserIds(deptId, dbConn);
      String userJson = "[";
      String userNames = T9InfoLogic.getUserName(userIds, dbConn);
      T9InfoLogic infoLogic= new T9InfoLogic();
      List<T9Person> personListTemp = infoLogic.getPersonByIds(dbConn, userIds);
      if(!userIds.equals("")){
        for (int i = 0; i <personListTemp.size() ; i++) {
          T9Person person = personListTemp.get(i);
          String userName = person.getUserName();
          if(userName!=null&&!userName.equals("")){
            userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          userJson = userJson + "{value:\"" + person.getSeqId() + "\",name:\"" + userName + "\"},";
        }
      }
      if(!userIds.equals("")){
        userJson = userJson.substring(0, userJson.length()-1);
      }
      userJson = userJson + "]";
      Calendar[] darr = T9CalendarAct.getStartEnd(Integer.parseInt(year),Integer.parseInt(weekth));
      String dateStr1 = T9CalendarAct.getFullTimeStr(darr[0]) + " 00:00:00";
      String dateStr2 = T9CalendarAct.getFullTimeStr(darr[1]) + " 23:59:59";
      //System.out.println(dateStr1);
      String calTime1 = T9DBUtility.getDateFilter("CAL_TIME", dateStr1, ">=");
      String calTime2 = T9DBUtility.getDateFilter("CAL_TIME", dateStr2, "<=");
      String endTime1 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      String endTime2 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      //System.out.println(calTime2+status1);
      T9CalendarLogic tcl = new T9CalendarLogic();
      List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
      if(!userIds.equals("")){
        calendarList= tcl.selectCalendarByDay(dbConn,userIds,calTime1,calTime2,endTime1, endTime2,status1);
      }
      String data = "[";
      for (int i = 0; i < calendarList.size(); i++) {
        T9Calendar calendar = new T9Calendar();
        calendar = calendarList.get(i);
        long begin = 0;
        long end = 0;
        int status = 0;//进行中 判断判断状态
        //System.out.println(calendar.getCalTime());
        begin = calendar.getCalTime().getTime();
        end = calendar.getEndTime().getTime();  
        if(dateTime<begin){
          status = 1;//未开始
        }
        if(dateTime>end){
          status = 2;//超时
        }
        calendar = calendarList.get(i);
        //判断是否跨天,并且判断是哪种跨天
        int dayStatus = 0;//没跨天
        //System.out.println(calendar.getSeqId());
        if(!dateFormatday.format(calendar.getCalTime()).equals(dateFormatday.format(calendar.getEndTime()))){
          //System.out.println(calendar.getSeqId()+"------------");
          //System.out.println(T9CalendarAct.getFullTimeStr(darr[0]).compareTo(dateFormatday.format(calendar.getCalTime()))>0);
          //System.out.println(T9CalendarAct.getFullTimeStr(darr[1]).compareTo(dateFormatday.format(calendar.getEndTime()))>=0);
          if(T9CalendarAct.getFullTimeStr(darr[0]).compareTo(dateFormatday.format(calendar.getCalTime()))>0&&T9CalendarAct.getFullTimeStr(darr[1]).compareTo(dateFormatday.format(calendar.getEndTime()))>=0){
            dayStatus = 1;//过期跨周
          }else if(T9CalendarAct.getFullTimeStr(darr[0]).compareTo(dateFormatday.format(calendar.getCalTime()))<=0&&T9CalendarAct.getFullTimeStr(darr[1]).compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 2;//未过跨周
          }else if(T9CalendarAct.getFullTimeStr(darr[0]).compareTo(dateFormatday.format(calendar.getCalTime()))>0&&T9CalendarAct.getFullTimeStr(darr[1]).compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 3;//跨周
          }else{
            dayStatus = 4;//本周跨天
          }
        }
        String userName = T9InfoLogic.getUserName(calendar.getUserId(), dbConn);
        if(userName!=null&&!userName.equals("")){
          userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String managerName = T9InfoLogic.getUserName(calendar.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        data = data + T9FOM.toJson(calendar).substring(0, T9FOM.toJson(calendar).length()-1)+",dayStatus:" + dayStatus+",status:" + status + ",userName:\""+userName+"\",managerName:\""+managerName+"\"},";
      }
      if(calendarList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      data = "{data:"+data+",users:"+userJson +"}";
      //System.out.println(data);
      //request.setAttribute("calendarList", calendarList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  //由部门得到次部门的所有人员 
  public String selectPersonByDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
     // String deptId = request.getParameter("deptId");
      String userIds = request.getParameter("userIds");
     // String userIds = T9InfoLogic.getUserIds(deptId, dbConn);
      String userJson = "[";
      String userNames = T9InfoLogic.getUserName(userIds, dbConn);

      if(userIds!=null&&!userIds.equals("")){
        T9InfoLogic infoLogic= new T9InfoLogic();
        List<T9Person> personListTemp = infoLogic.getPersonByIds(dbConn, userIds);
        for (int i = 0; i <personListTemp.size() ; i++) {
          T9Person person = personListTemp.get(i);
          String userName = person.getUserName();
          if(userName!=null&&!userName.equals("")){
            userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          }
          userJson = userJson + "{value:\"" + person.getSeqId() + "\",name:\"" + userName+ "\"},";
        }
      }
      if(userIds!=null&&!userIds.equals("")){
        userJson = userJson.substring(0, userJson.length()-1);
      }
      userJson = userJson + "]";
      //System.out.println(userJson);
      //request.setAttribute("calendarList", calendarList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userId));
      request.setAttribute(T9ActionKeys.RET_DATA, userJson);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  //日程：按月查询
  public String selectCalendarByDeptMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Date dateCur = new Date();
      long dateTime = dateCur.getTime();    
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormatday = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int personId = user.getSeqId();
      String dateCurStr = dateFormatday.format(dateCur);
      String deptId = request.getParameter("deptId");
      String userId =request.getParameter("userId");
      String year =  request.getParameter("year");
      String month = request.getParameter("month");
      String status1 = request.getParameter("status");
      Calendar time=Calendar.getInstance(); 
      time.clear(); 
      time.set(Calendar.YEAR,Integer.parseInt(year)); //year 为 int 
      time.set(Calendar.MONTH,Integer.parseInt(month)-1);//注意,Calendar对象默认一月为0           
      int maxDay=time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
      //System.out.println(status1+"........s");
      if(String.valueOf(month).length()==1){
        month = "0"+month;
      }
      String dateStr1 = year+"-"+month + "-01 00:00:00";
      String dateStr2 = year+"-"+month+ "-"+maxDay + " 23:59:59";
      //System.out.println(dateStr1);
      String calTime1 = T9DBUtility.getDateFilter("CAL_TIME", dateStr1, ">=");
      String calTime2 = T9DBUtility.getDateFilter("CAL_TIME", dateStr2, "<=");
      String endTime1 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      String endTime2 = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      //System.out.println(calTime2+status1);
      T9CalendarLogic tcl = new T9CalendarLogic();
      List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
      if(!userId.equals("")){
        calendarList= tcl.selectCalendarByDay(dbConn,String.valueOf(userId),calTime1,calTime2,endTime1, endTime2,status1);       
      }
      String data = "[";  
      for (int i = 0; i < calendarList.size(); i++) {
        T9Calendar calendar = new T9Calendar();
        calendar = calendarList.get(i);
        long begin = 0;
        long end = 0;
        int status = 0;//进行中 判断判断状态
        //System.out.println(calendar.getCalTime());
        begin = calendar.getCalTime().getTime();
        end = calendar.getEndTime().getTime();  
        if(dateTime<begin){
          status = 1;//未开始
        }
        if(dateTime>end){
          status = 2;//超时
        }
        calendar = calendarList.get(i);
        //判断是否跨天,并且判断是哪种跨天
        int dayStatus = 0;//没跨天
        //System.out.println(calendar.getSeqId());
        dateStr1 = dateStr1.substring(0, 10); 
        dateStr2 = dateStr2.substring(0,10);
        if(!dateFormatday.format(calendar.getCalTime()).equals(dateFormatday.format(calendar.getEndTime()))){
          //System.out.println(dateStr1.compareTo(dateFormatday.format(calendar.getCalTime()))>0);
          if(dateStr1.compareTo(dateFormatday.format(calendar.getCalTime()))>0&&dateStr2.compareTo(dateFormatday.format(calendar.getEndTime()))>=0){
            dayStatus = 1;//过期跨月
          }else if(dateStr1.compareTo(dateFormatday.format(calendar.getCalTime()))<=0&&dateStr2.compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 2;//未过跨月
          }else if(dateStr1.compareTo(dateFormatday.format(calendar.getCalTime()))>0&&dateStr2.compareTo(dateFormatday.format(calendar.getEndTime()))<0){
            dayStatus = 3;//跨月
          }else{
            dayStatus = 4;//本月跨天
          }
        }
        String userName = T9InfoLogic.getUserName(calendar.getUserId(), dbConn);
        if(userName!=null&&!userName.equals("")){
          userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        String managerName = T9InfoLogic.getUserName(calendar.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          
        }
        data = data + T9FOM.toJson(calendar).substring(0, T9FOM.toJson(calendar).length()-1)+",dayStatus:" + dayStatus+",status:" + status + ",userName:\""+userName+"\",managerName:\""+managerName+"\"},";
      }
      if(calendarList.size()>0){
        data = data.substring(0, data.length()-1);
      }
      data = data + "]";
      //System.out.println(data);
      //request.setAttribute("calendarList", calendarList);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(personId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 把事务显示到日程安排查询中  按日查询
   */
  public String selectAffairByDeptDay(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date dateCur = new Date();  
      String userIds = request.getParameter("userIds");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String day = request.getParameter("day");
      String date = year+"-"+month+"-"+day;
      String dateEnd = date+" 23:59:59";
      //System.out.println(date);
      Date newDate = T9Utility.parseDate(date);//转换为DATE类型
      Calendar cld = Calendar.getInstance();
      cld.setTime(newDate);
      int weekInt = cld.get(Calendar.DAY_OF_WEEK);
      //String userIds = T9InfoLogic.getUserIds(deptId, dbConn);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateEnd, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", date, ">=");
    
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = new ArrayList<T9Affair>();
      if(!userIds.equals("")){
        String newUserId  = "";
        String[] userIdArray = userIds.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
          newUserId = newUserId + "'" +userIdArray[i] + "',";
        }
        newUserId = newUserId.substring(0, newUserId.length()-1);
        String str[]= {"USER_ID in("+newUserId+")",beginDate};
        affairList = tal.selectAffair(dbConn, str);
      }
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          String isWeekend  = affair.getIsWeekend(); 
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in("+userIds+")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(isWeekend!=null&&isWeekend.equals("1")){
              if(weekInt!=1&weekInt!=7){
                if(affairListTemp.size()>0){
                  affair = affairListTemp.get(0);
                  affairList2.add(affair);
                }
              }
            }else{
              if(affairListTemp.size()>0){
                affair = affairListTemp.get(0);
                affairList2.add(affair);
              }
            }
          }else{
            if(isWeekend!=null&&isWeekend.equals("1")){
              if(weekInt!=1&weekInt!=7){
                affairList2.add(affair);
              }
            }else{
              affairList2.add(affair);
            }
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in("+userIds+")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String week = affair.getRemindDate();
              GregorianCalendar d = new GregorianCalendar(); 
              Date mydate= dateFormat.parse(date); 
              d.setTime(mydate);
              int today = d.get(Calendar.DAY_OF_WEEK);
              if(today==1){
                today = 7;  
              }else{
                today = today - 1;
              }
              if(String.valueOf(today).equals(week)){
                affairList2.add(affair);
              }
            }
          }else{
            String week = affair.getRemindDate();
            GregorianCalendar d = new GregorianCalendar(); 
            Date mydate= dateFormat.parse(date); 
            d.setTime(mydate);
            int today = d.get(Calendar.DAY_OF_WEEK);
            if(today==1){
              today = 7;  
            }else{
              today = today - 1;
            }
            if(String.valueOf(today).equals(week)){
              affairList2.add(affair);
            }
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in("+userIds+")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String d = affair.getRemindDate();
              if(d.length()==1){
                d = "0"+ d;
              }
              if(d.equals(day)){
                affairList2.add(affair);
              }
            }
          } else{
            String d = affair.getRemindDate();
            //System.out.println(d+"dddd");
            if(d.length()==1){
              d = "0"+ d;
            }
            if(day.equals(d)){
              affairList2.add(affair);
            }    
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in("+userIds+")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              if(m.length()==1){
                m = "0"+ m;
              }
              if(d.length()==1){
                d = "0"+ d;
              }
              if((month+day).equals(m+d)){
                affairList2.add(affair);
              }
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            if(m.length()==1){
              m = "0"+ m;
            }
            if(d.length()==1){
              d = "0"+ d;
            }
            if((month+day).equals(m+d)){
              affairList2.add(affair);
            }
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        String managerName =    T9InfoLogic.getUserName(affair.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        data = data+ T9FOM.toJson(affair).toString().substring(0,T9FOM.toJson(affair).toString().length()-1 )+",managerName:\""+managerName+"\"},";
      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
      //System.out.println(data);      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userSeqId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 把事务显示到日程安排中  按周查询
   */
  public String selectAffairByDeptWeek(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
      Date dateCur = new Date();  
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String year =  request.getParameter("year");
      String weekth = request.getParameter("week");
      //String deptId = request.getParameter("deptId");
      String userIds = request.getParameter("userIds");
      //String userIds = T9InfoLogic.getUserIds(deptId, dbConn);
      Calendar[] darr = T9CalendarAct.getStartEnd(Integer.parseInt(year),Integer.parseInt(weekth));
      String dateStr1 = T9CalendarAct.getFullTimeStr(darr[0]) + " 00:00:00";
      String dateStr2 = T9CalendarAct.getFullTimeStr(darr[1]) + " 23:59:59";
      //System.out.println(dateStr1);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateStr2, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = new ArrayList<T9Affair>();
      if(!userIds.equals("")){
        String newUserId = "";
        String[] userIdArray = userIds.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
          newUserId = newUserId + "'" + userIdArray[i] + "',";
        }
        newUserId = newUserId.substring(0, newUserId.length()-1);
        String str[]= {"USER_ID in(" + newUserId + ")",beginDate};
        affairList = tal.selectAffair(dbConn, str);
      }
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in(" + userIds + ")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in(" + userId + ")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in(" + userId + ")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String d = affair.getRemindDate();
              Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
              Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
              String monthStr1 = dateFormatMonth.format(date1);
              String monthStr2 = dateFormatMonth.format(date2);
              //System.out.println(monthStr2);
              Date date3 = dateFormat.parse(year+"-"+monthStr1+"-"+d);
              Date date4 = dateFormat.parse(year+"-"+monthStr2+"-"+d);                 
              if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
                affairList2.add(affair);
              }
            }
          } else{
            String d = affair.getRemindDate();
            Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
            Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
            String monthStr1 = dateFormatMonth.format(date1);
            String monthStr2 = dateFormatMonth.format(date2);
            //System.out.println(monthStr2);
            Date date3 = dateFormat.parse(year+"-"+monthStr1+"-"+d);
            Date date4 = dateFormat.parse(year+"-"+monthStr2+"-"+d);                 
            if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
              affairList2.add(affair);
            }  
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID in(" + userId + ")",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
              Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
              String monthStr1 = dateFormatMonth.format(date1);
              String monthStr2 = dateFormatMonth.format(date2);
              //System.out.println(monthStr2);
              Date date3 = dateFormat.parse(year+"-"+m+"-"+d);
              if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
                affairList2.add(affair);
              }  
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            Date date1 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[0]));
            Date date2 = dateFormat.parse(T9CalendarAct.getFullTimeStr(darr[1]));
            String monthStr1 = dateFormatMonth.format(date1);
            String monthStr2 = dateFormatMonth.format(date2);
            //System.out.println(monthStr2);
            Date date3 = dateFormat.parse(year+"-"+m+"-"+d);
            if(date3.getTime()>=date1.getTime()&&date3.getTime()<=date2.getTime()){
              affairList2.add(affair);
            }  
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        String managerName =    T9InfoLogic.getUserName(affair.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
          
        }
        data = data+ T9FOM.toJson(affair).toString().substring(0,T9FOM.toJson(affair).toString().length()-1 )+",managerName:\""+managerName+"\"},";
      }
      if(affairList2.size()>0){
        //System.out.println(data);
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
      //System.out.println(data);      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 把事务显示到日程安排中  按月查询
   */
  public String selectAffairByDeptMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MM");
      Date dateCur = new Date();  
      String userId = request.getParameter("userId");
      String year =  request.getParameter("year");
      String month = request.getParameter("month");
      Calendar time=Calendar.getInstance(); 
      time.clear(); 
      time.set(Calendar.YEAR,Integer.parseInt(year)); //year 为 int 
      time.set(Calendar.MONTH,Integer.parseInt(month)-1);//注意,Calendar对象默认一月为0           
      int maxDay=time.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数 
      if(String.valueOf(month).length()==1){
        month = "0"+month;
      }
      String dateStr1 = year+"-"+month + "-01 00:00:00";
      String dateStr2 = year+"-"+month+ "-"+maxDay + " 23:59:59";
      //System.out.println(dateStr1);
      String beginDate = T9DBUtility.getDateFilter("BEGIN_TIME", dateStr2, "<=");
      String endDate = T9DBUtility.getDateFilter("END_TIME", dateStr1, ">=");
    
      T9AffairLogic  tal = new T9AffairLogic();
      List<T9Affair> affairList = new ArrayList<T9Affair>();
      if(!userId.equals("")){
        String newUserId = "";
        String[] userIdArray = userId.split(",");
        for (int i = 0; i < userIdArray.length; i++) {
          newUserId = newUserId = "'" + userIdArray[i] + "',";
        }
        newUserId = newUserId.substring(0, newUserId.length()-1);
        String str[]= {"USER_ID ="+newUserId +"",beginDate};
        affairList = tal.selectAffair(dbConn, str);
      }
      List<T9Affair> affairList2 = new ArrayList<T9Affair>();
      for (int i = 0; i < affairList.size(); i++) {
        T9Affair affair = affairList.get(i);
        //System.out.println(affair.getEndTime());
        String type = affair.getType();
        //判断日提醒
        if(type.equals("2")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID='"+userId+"'",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          }
          //判断周提醒
        }else if(type.equals("3")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID='"+userId+"'",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          }else{
            affairList2.add(affair);
          } 
          //判断月提醒
        }else if(type.equals("4")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID='"+userId+"'",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              affairList2.add(affair);
            }
          } else{
            affairList2.add(affair);  
          }
          //按年提醒
        }else if(type.equals("5")){
          if(affair.getEndTime()!=null){
            String str2[]= {"USER_ID='"+userId+"'",beginDate,endDate,"SEQ_ID="+affair.getSeqId()};
            List<T9Affair> affairListTemp = tal.selectAffair(dbConn, str2);
            if(affairListTemp.size()>0){
              affair = affairListTemp.get(0);
              String monthday = affair.getRemindDate();
              String m = monthday.split("-")[0];
              String d = monthday.split("-")[1];
              if(m.length()==1){
                m = "0" + m;
              }
              if(m.equals(month)){
                affairList2.add(affair);
              }  
            }
          } else{
            String monthday = affair.getRemindDate();
            String m = monthday.split("-")[0];
            String d = monthday.split("-")[1];
            if(m.length()==1){
              m = "0" + m;
            }
            if(m.equals(month)){
              affairList2.add(affair);
            }  
          }
        }  
      }
      String data = "[";
      for (int i = 0; i < affairList2.size(); i++) {
        T9Affair affair = affairList2.get(i);
        String managerName =    T9InfoLogic.getUserName(affair.getManagerId(), dbConn);
        if(managerName!=null&&!managerName.equals("")){
          managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        data = data+ T9FOM.toJson(affair).toString().substring(0,T9FOM.toJson(affair).toString().length()-1 )+",managerName:\""+managerName+"\"},";
      }
      if(affairList2.size()>0){
        data =  data.substring(0, data.length()-1);
      }
      data = data+"]";
      //System.out.println(data);      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(maxDay));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 
  /*
   * 任务 : 根据部门按日显示
   */
  public String selectTaskByDeptDay(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userIds = request.getParameter("userIds");
    /*  System.out.println(deptId);
      if(deptId.equals("")){
       deptId = "0";
      }*/
      //有部门得到所有人员的Ids;
      T9PersonLogic personLogic = new T9PersonLogic();
     // List<T9Person> personList = personLogic.getPersonByDept(Integer.parseInt(deptId), dbConn);
     // String userIds = "";
  /*    for (int i = 0; i < personList.size(); i++) {
        userIds = userIds + personList.get(i).getSeqId() + ",";
      }
      //System.out.println(userIds);
      if(!userIds.equals("")){
        userIds  = userIds.substring(0, userIds.length()-1);
      }*/
      T9InfoLogic infoLogic = new T9InfoLogic();
      List<Map<String,String>> taskList = new ArrayList<Map<String,String>>();
      if(!userIds.equals("")){
        taskList = infoLogic.selectTask(dbConn, userIds);
      }
      StringBuffer buffer=new StringBuffer("["); 
      for(Map<String, String> equipmentsMap:taskList){ 
      buffer.append("{"); 
      Set<String>keySet=equipmentsMap.keySet(); 
      for(String mapStr:keySet){ 
        //System.out.println(mapStr + ":>>>>>>>>>>>>" + equipmentsMap.get(mapStr)); 
        String name=equipmentsMap.get(mapStr); 
        if(name!=null){
          name = name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        buffer.append( mapStr+":\"" + (name==null? "":name) + "\","); 
      } 
      String managerName =    T9InfoLogic.getUserName(equipmentsMap.get("managerId"), dbConn);
      if(managerName!=null&&!managerName.equals("")){
        managerName = managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        
      }
      buffer.append("managerName:\""+managerName+"\",");
      buffer.deleteCharAt(buffer.length()-1); 
      buffer.append("},"); 
      }
      buffer.deleteCharAt(buffer.length()-1); 
      if (taskList.size()>0) { 
        buffer.append("]"); 
      }else { 
        buffer.append("[]"); 
      }
      String data = buffer.toString();
      //System.out.println(data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 查询所有任务根据UserId
   */
  public String selectTaskByUserId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userSeqId = user.getSeqId();
      String userId = request.getParameter("userId");
      T9TaskLogic ttl = new T9TaskLogic();
      List<Map<String,String>> taskList = new ArrayList<Map<String,String>>();
      if(!userId.equals("")){
        taskList = ttl.selectTask(dbConn, Integer.parseInt(userId));
      }
      StringBuffer buffer=new StringBuffer("["); 
      for(Map<String, String> equipmentsMap:taskList){ 
      buffer.append("{"); 
      Set<String>keySet=equipmentsMap.keySet(); 
      for(String mapStr:keySet){ 
        //System.out.println(mapStr + ":>>>>>>>>>>>>" + equipmentsMap.get(mapStr)); 
        String name=equipmentsMap.get(mapStr); 
        if(name!=null){
          name = name.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        }
        buffer.append( mapStr+":\"" + (name==null? "":name) + "\","); 
      } 
      //System.out.println(equipmentsMap.get("managerId")+equipmentsMap.get("seqId"));
      String managerName =    T9InfoLogic.getUserName(equipmentsMap.get("managerId"), dbConn);
      if(managerName!=null&&!managerName.equals("")){
        managerName =  managerName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        
      }
      buffer.append("managerName:\""+managerName+"\",");
      buffer.deleteCharAt(buffer.length()-1); 
      buffer.append("},"); 
      }
      buffer.deleteCharAt(buffer.length()-1); 
      if (taskList.size()>0) { 
        buffer.append("]"); 
      }else { 
        buffer.append("[]"); 
      }
      String data = buffer.toString();
      //System.out.println(data);
    
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, String.valueOf(userSeqId));
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  public String selectCalendarByDeptTerm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      String deptId = request.getParameter("deptId");
      //System.out.println(deptId);
      String minTime = request.getParameter("minTime");
      String maxTime = request.getParameter("maxTime");
      String calLevel = request.getParameter("calLevel");
      String overStatus = request.getParameter("overStatus");
      String content = request.getParameter("content");
      String manager = request.getParameter("manager");
      //有部门得到所有人员的Ids;
      T9PersonLogic personLogic = new T9PersonLogic();
      //得到本人的ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门

      //得到部门Id
      //System.out.println(deptId);
      if(deptId==null||deptId.equals("")){
       deptId = "0";
      }
      //根据角色的seqId得到角色的序号
      T9UserPrivLogic userPrivLogic = new T9UserPrivLogic();
      T9UserPriv userPrivClass = null;
      int privNo = 0;
      if(userPriv!=null&&!userPriv.trim().equals("")){
        userPrivClass = userPrivLogic.getRoleById(Integer.parseInt(userPriv), dbConn);
        privNo = userPrivClass.getPrivNo();
      }
      T9InfoLogic infoLogic = new T9InfoLogic();
      List<T9Person> personList = new ArrayList<T9Person>();

      String userIds = "";
      String userJson = "[";
      if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
        String[]tempStr = {"DEPT_ID = " + deptId };
        personList = personLogic.getPersonByPriv(dbConn, tempStr);
      }else{
        List<T9ModulePriv> moduleList = getModulePrive(dbConn, userId, request, response);//模块权限
        
        if(moduleList.size()>0){
          T9ModulePriv modulePriv = moduleList.get(0);
          String deptPriv = modulePriv.getDeptPriv();
          String RolrPriv = modulePriv.getRolePriv();
          String deptIdPriv = modulePriv.getDeptId();
          String privId = modulePriv.getPrivId();
          String userIdPriv = modulePriv.getUserId();
          if(deptPriv.equals("3")){
            //userIds = userIdPriv;
            String[] userIdArray = userIdPriv.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              T9Person person = personLogic.getPerson(dbConn,userIdArray[i]);
              T9UserPriv userPrivClassTemp = null;
              int privNoTemp = 0;
              if(person.getUserPriv()!=null&&!person.getUserPriv().trim().equals("")){
                userPrivClassTemp = userPrivLogic.getRoleById(Integer.parseInt(person.getUserPriv()), dbConn);
                privNoTemp = userPrivClassTemp.getPrivNo();
              }
              privNo = userPrivClass.getPrivNo();
              if(RolrPriv.equals("0")){
                if((privNo-privNoTemp)<0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("1")){
                if((privNo-privNoTemp)<=0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("2")){
                userIds = userIds + userIdArray[i] + ",";
              }
              if(RolrPriv.equals("3")){
                if(person.getUserPriv().equals(privId)){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
            }
          }else{
            if(RolrPriv.equals("0")){
             // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
              //personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
            }
            if(RolrPriv.equals("1")){
              //String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV >= " + userPriv };
             // personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">=", deptId);
            }
            if(RolrPriv.equals("2")){
              String[]tempStr = {"DEPT_ID = " + deptId };
              personList = personLogic.getPersonByPriv(dbConn, tempStr);
            }
            if(RolrPriv.equals("3")){
              if(!privId.trim().equals("")){
                String[] privIdArray = privId.split(",");
                String newPrivId = "";
                for (int i = 0; i < privIdArray.length; i++) {
                  newPrivId = newPrivId +"'" + privIdArray[i] + "',";
                }
                newPrivId = newPrivId.substring(0, newPrivId.length()-1);
                String[]tempStr = {"DEPT_ID = " + deptId ,"USER_PRIV in (" + newPrivId + ")"};
                personList = personLogic.getPersonByPriv(dbConn, tempStr);
              }
         
            }
          }
    
        }else{
         // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
         // personList = personLogic.getPersonByPriv(dbConn, tempStr);
          personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
          
        }
       
      }
      for (int i = 0; i < personList.size(); i++) {
        userIds = userIds + personList.get(i).getSeqId() + ",";
      }
      //System.out.println(userIds);
      if(!userIds.equals("")){
        userIds  = userIds.substring(0, userIds.length()-1);
      }
      T9CalendarLogic tcl = new T9CalendarLogic();
      List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
      if(!userIds.equals("")){
        calendarList = tcl.selectCalendarByTerm(dbConn, userIds, minTime, maxTime, calLevel,"1", overStatus,content,manager);
      }
      List<Map<String,String>> calendarListCopy = new ArrayList<Map<String,String>>();
      Date date = new Date();
      long dateTime = date.getTime();
      long begin = 0;
      long end = 0;
      String status = "0";//进行中 判断判断状态
      for (int i = 0; i < calendarList.size(); i++) {
        Map<String,String> map = new HashMap<String,String>();
        T9Calendar calendar  = calendarList.get(i);
        map.put("seqId", String.valueOf(calendar.getSeqId()));
        map.put("userId", calendar.getUserId());
        map.put("calLevel",calendar.getCalLevel());
        map.put("calType", calendar.getCalType());
        map.put("content", calendar.getContent());
        map.put("managerId", calendar.getManagerId());
        map.put("calTime", dateFormat.format(calendar.getCalTime()));
        map.put("endTime", dateFormat.format(calendar.getEndTime()));
        map.put("overStatus", calendar.getOverStatus());
        //System.out.println(calendar.getManagerId());
        T9PersonLogic tpl = new T9PersonLogic(); 
        String userName =  tpl.getNameBySeqIdStr(calendar.getUserId(), dbConn);
        map.put("userName",userName);
        if(calendar.getManagerId()!=null){    
          String managerName = tpl.getNameBySeqIdStr(calendar.getManagerId(), dbConn);
          map.put("managerName",managerName);
        }else{
          map.put("managerName", "");
        }
        String overStatus1 = calendar.getOverStatus();
        if(overStatus1==null||overStatus1.equals("0")||overStatus1.trim().equals("")){
          begin = calendar.getCalTime().getTime();
          end = calendar.getEndTime().getTime();  
          if(dateTime<begin){
            status = "1";//未开始
          }
          if(dateTime>end){
            status = "2";//超时
          }
        }
        map.put("status", status);
        calendarListCopy.add(map);
      }
      //System.out.println(calendarListCopy.size()+"-----------------------");
      request.setAttribute("calendarList", calendarListCopy);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/calendar/info/querycalendar.jsp";
  }
  /**
   * 查询导出成excle
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectCalendarByDeptTermOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    OutputStream ops = null;
    InputStream is = null;
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      String deptId = request.getParameter("deptId");
      //System.out.println(deptId);
      String minTime = request.getParameter("minTime");
      String maxTime = request.getParameter("maxTime");
      String calLevel = request.getParameter("calLevel");
      String overStatus = request.getParameter("overStatus");
      String content = request.getParameter("content");  
      String manager = request.getParameter("manager");

      //fileName = URLEncoder.encode(fileName, "UTF-8");//fileName.getBytes("GBK"), "iso8859-1")
      String fileName = "日程安排.xls";
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename="
          + new String(fileName.getBytes("GBK"), "iso8859-1") );
      ops = response.getOutputStream();
      //T9ExportLogic expl = new T9ExportLogic();
 
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      
      //有部门得到所有人员的Ids;
      T9PersonLogic personLogic = new T9PersonLogic();
      //得到本人的ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();//角色
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门

      //得到部门Id
      //System.out.println(deptId);
      if(deptId==null||deptId.equals("")){
       deptId = "0";
      }
      //根据角色的seqId得到角色的序号

      T9UserPrivLogic userPrivLogic = new T9UserPrivLogic();
      T9UserPriv userPrivClass = null;
      int privNo = 0;
      if(userPriv!=null&&!userPriv.trim().equals("")){
        userPrivClass = userPrivLogic.getRoleById(Integer.parseInt(userPriv), dbConn);
        privNo = userPrivClass.getPrivNo();
      }
      T9InfoLogic infoLogic = new T9InfoLogic();
      List<T9Person> personList = new ArrayList<T9Person>();

      String userIds = "";
      String userJson = "[";
      if(userPriv!=null&&userPriv.equals("1")&&user.getUserId().trim().equals("admin")){//假如是系统管理员的都快要看得到.而且是ADMIN用户
        String[]tempStr = {"DEPT_ID = " + deptId };
        personList = personLogic.getPersonByPriv(dbConn, tempStr);
      }else{
        List<T9ModulePriv> moduleList = getModulePrive(dbConn, userId, request, response);//模块权限
        
        if(moduleList.size()>0){
          T9ModulePriv modulePriv = moduleList.get(0);
          String deptPriv = modulePriv.getDeptPriv();
          String RolrPriv = modulePriv.getRolePriv();
          String deptIdPriv = modulePriv.getDeptId();
          String privId = modulePriv.getPrivId();
          String userIdPriv = modulePriv.getUserId();
          if(deptPriv.equals("3")){
            //userIds = userIdPriv;
            String[] userIdArray = userIdPriv.split(",");
            for (int i = 0; i < userIdArray.length; i++) {
              T9Person person = personLogic.getPerson(dbConn,userIdArray[i]);
              T9UserPriv userPrivClassTemp = null;
              int privNoTemp = 0;
              if(person.getUserPriv()!=null&&!person.getUserPriv().trim().equals("")){
                userPrivClassTemp = userPrivLogic.getRoleById(Integer.parseInt(person.getUserPriv()), dbConn);
                privNoTemp = userPrivClassTemp.getPrivNo();
              }
              privNo = userPrivClass.getPrivNo();
              if(RolrPriv.equals("0")){
                if((privNo-privNoTemp)<0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("1")){
                if((privNo-privNoTemp)<=0){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
              if(RolrPriv.equals("2")){
                userIds = userIds + userIdArray[i] + ",";
              }
              if(RolrPriv.equals("3")){
                if(person.getUserPriv().equals(privId)){
                  userIds = userIds + userIdArray[i] + ",";
                }
              }
            }
          }else{
            if(RolrPriv.equals("0")){
             // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
              //personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
            }
            if(RolrPriv.equals("1")){
              //String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV >= " + userPriv };
             // personList = personLogic.getPersonByPriv(dbConn, tempStr);
              personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">=", deptId);
            }
            if(RolrPriv.equals("2")){
              String[]tempStr = {"DEPT_ID = " + deptId };
              personList = personLogic.getPersonByPriv(dbConn, tempStr);
            }
            if(RolrPriv.equals("3")){
              if(!privId.trim().equals("")){
                String[] privIdArray = privId.split(",");
                String newPrivId = "";
                for (int i = 0; i < privIdArray.length; i++) {
                  newPrivId = newPrivId +"'" + privIdArray[i] + "',";
                }
                newPrivId = newPrivId.substring(0, newPrivId.length()-1);
                String[]tempStr = {"DEPT_ID = " + deptId ,"USER_PRIV in (" + newPrivId + ")"};
                personList = personLogic.getPersonByPriv(dbConn, tempStr);
              }
         
            }
          }
    
        }else{
         // String[]tempStr = {"DEPT_ID = " + deptId,"USER_PRIV > " + userPriv };
         // personList = personLogic.getPersonByPriv(dbConn, tempStr);
          personList = infoLogic.getPersonByPriv(dbConn, String.valueOf(privNo), ">", deptId);
          
        }
       
      }
      for (int i = 0; i < personList.size(); i++) {
        userIds = userIds + personList.get(i).getSeqId() + ",";
      }
      //System.out.println(userIds);
      if(!userIds.equals("")){
        userIds  = userIds.substring(0, userIds.length()-1);
      }
      T9CalendarLogic tcl = new T9CalendarLogic();
      List<T9Calendar> calendarList = new ArrayList<T9Calendar>();
      if(!userIds.equals("")){
        calendarList = tcl.selectCalendarByTerm(dbConn, userIds, minTime, maxTime, calLevel,"1", overStatus,content,manager);
      }
      List<Map<String,String>> calendarListCopy = new ArrayList<Map<String,String>>();
      Date date = new Date();
      long dateTime = date.getTime();
      long begin = 0;
      long end = 0;
      String status = "进行中";//进行中 判断判断状态
      String calType = "工作事务";
      String calLevelTemp = "未指定";
      T9ManageOutLogic tmol =  new T9ManageOutLogic();
      T9PersonLogic tpl = new T9PersonLogic(); 
      for (int i = 0; i < calendarList.size(); i++) {
        Map<String,String> map = new HashMap<String,String>();
        T9Calendar calendar  = calendarList.get(i);
        
        T9DbRecord rd = new T9DbRecord();
        String userName =  tpl.getNameBySeqIdStr(calendar.getUserId(), dbConn);
        String managerName = "";
        if(calendar.getManagerId()!=null){    
           managerName = tpl.getNameBySeqIdStr(calendar.getManagerId(), dbConn);
        }
        
        String overStatus1 = calendar.getOverStatus();
        if(overStatus1==null||overStatus1.equals("0")||overStatus1.trim().equals("")){
          begin = calendar.getCalTime().getTime();
          end = calendar.getEndTime().getTime();  
          if(dateTime<begin){
            status = "未开始";

          }
          if(dateTime>end){
            status = "已超时";
          }
        }else{
          status = "已完成";
        }
        String calType2 = calendar.getCalType();
        if(calType2!=null&&calType2.equals("2")){
          calType = "个人事务";
        }
        String calLevel2 = calendar.getCalLevel();
        if(calLevel2!=null&&calLevel2.equals("1")){
          calLevelTemp = "重要/紧急";
        }
        if(calLevel2!=null&&calLevel2.equals("2")){
          calLevelTemp = "重要/不紧急";
        }
        if(calLevel2!=null&&calLevel2.equals("3")){
          calLevelTemp = "不重要/紧急";
        }
        if(calLevel2!=null&&calLevel2.equals("4")){
          calLevelTemp = "不重要/不紧急";
        }
        rd.addField("部门", tmol.selectByUserIdDept(dbConn, calendar.getUserId()));
        rd.addField("用户",userName);
        rd.addField("开始时间",dateFormat.format(calendar.getCalTime()));
        rd.addField("结束时间", dateFormat.format(calendar.getEndTime()));
        rd.addField("事务类型", calType);
        
        rd.addField("事务内容", calendar.getContent());
        rd.addField("优先程度",calLevelTemp);
        
        rd.addField("安排人",managerName);
        rd.addField("状态",status);
        dbL.add(rd);
      }
      T9JExcelUtil.writeExc(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return "";
  }
  /*
   * 根据Id得到人员的名字
   */
  public String selectUserNames(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String userName =  T9InfoLogic.getUserName2(userId, dbConn);
      if(userName!=null&&!userName.equals("")){
        userName = userName.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        
      }
      String data = "{userName:\""+userName+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 根据一个部门得到所有的人员id和name
   */
  public String selectUserNamesByDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId = request.getParameter("deptId");
      String userIds = T9InfoLogic.getUserIds2(deptId, dbConn);
      String userNames =  T9InfoLogic.getUserName2(userIds, dbConn);
      if(userNames!=null&&!userNames.equals("")){
        userNames = userNames.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "").replace("\n", "");
        
      }
      String data = "{userIds:\""+userIds+"\",userNames:\""+userNames+"\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
 /*
  * 新增日程安排,多个新增,
  */
  public String addCalendar(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Calendar calendar = new T9Calendar();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      String userIdStr = request.getParameter("user");
      String userIds[] = userIdStr.split(",");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String calTime = request.getParameter("calTime");
      String endTime = request.getParameter("endTime");
      String calType = request.getParameter("calType");
      String calLevel = request.getParameter("calLevel");
      String content = request.getParameter("content");
      String smsRemind = request.getParameter("smsRemind");
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      String beforeDay = request.getParameter("beforeDay");
      String beforeHour = request.getParameter("beforeHour");
      String beforeMin = request.getParameter("beforeMin");
      int beforeDayInt = 0;
      int beforeHourInt = 0;
      int beforeMinInt = 0;
      if(beforeDay!=null&&!beforeDay.equals("")&&T9Utility.isInteger(beforeDay)){
        beforeDayInt = Integer.parseInt(beforeDay);
      }
      if(beforeHour!=null&&!beforeHour.equals("")&&T9Utility.isInteger(beforeHour)){
        beforeHourInt = Integer.parseInt(beforeHour);
      }
      if(beforeMin!=null&&!beforeMin.equals("")&&T9Utility.isInteger(beforeMin)){
        beforeMinInt = Integer.parseInt(beforeMin);
      }
 
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      //System.out.println(content);
      calTime = calTime + ":00";
      endTime = endTime + ":00";
      //System.out.println(calTime);
   
      calendar.setCalTime(dateFormat.parse(calTime));
      calendar.setEndTime(dateFormat.parse(endTime));
      calendar.setOverStatus("0");
      calendar.setCalType(calType);
      calendar.setCalLevel(calLevel);
      calendar.setContent(content);
      calendar.setManagerId(String.valueOf(userId));
      T9CalendarLogic tcl = new T9CalendarLogic();
      //System.out.println(userIds.length);
      Date curDate = new Date();
      Calendar c = Calendar.getInstance();
      c.setTime(dateFormat.parse(calTime));
      c.add(Calendar.DATE,-beforeDayInt) ;
      c.add(Calendar.HOUR, -beforeHourInt);
      c.add(Calendar.MINUTE, -beforeMinInt);
      Date newDate = c.getTime();
      for (int i = 0; i < userIds.length; i++) {
        calendar.setUserId(userIds[i]);
        int maxSeqId = tcl.addCalendar(dbConn, calendar);
        if(smsRemind!=null){
          //短信smsType, content, remindUrl, toId, fromId
          T9SmsBack sb = new T9SmsBack();
          if(curDate.compareTo(newDate)<0){
            sb.setSendDate(newDate);
          }
          sb.setSmsType("5");
          sb.setContent(user.getUserName()+"为你安排新的工作。内容"+content);
          sb.setRemindUrl("/core/funcs/calendar/mynote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
          sb.setToId(userIds[i]);
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
        if(moblieSmsRemind!=null){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn, userIds[i], userId,"日程安排："+user.getUserName()+"为你安排新的工作。内容"+ content, new Date());
        }
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 添加事务多个新增,
   */
  public String addAffair(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      T9Affair affair = new T9Affair();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date curDate = new Date();
      Date curDate1 = T9Utility.parseDate(T9Utility.getCurDateTimeStr("yyyy-MM-dd")+" 23:59:59");
      //判断今天是否可以为提醒时间
      Calendar calendar = Calendar.getInstance();
      int week = calendar.get(Calendar.DAY_OF_WEEK);
      int day = calendar.get(Calendar.DATE);
      int month = calendar.get(Calendar.MONTH);
      if(week==1){
        week = 7;
      }else{
        week = week-1;
      }
      month = month+1;
      
      String curDateStr = dateFormat.format(curDate);
      String userIdStr = request.getParameter("user");
      String userIds[] = userIdStr.split(",");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String beginTime = request.getParameter("beginTime");
      String endTime = request.getParameter("endTime");
      String content = request.getParameter("content");
      String type = request.getParameter("type");
      String isWeekend = request.getParameter("isWeekend");
      //System.out.println(type);
      String remindTime = request.getParameter("remindTime");
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      String remindDate = "";
      if(isWeekend!=null){
        affair.setIsWeekend("1");
      }
      if(type.equals("3")){
        remindDate = request.getParameter("remindDate3");
        remindTime = request.getParameter("remindTime3");
      }
      if(type.equals("4")){
        remindDate = request.getParameter("remindDate4");
        remindTime = request.getParameter("remindTime4");
      }
      if(type.equals("5")){
        remindDate = request.getParameter("remindDate5Mon")+"-"+request.getParameter("remindDate5Day");
        remindTime = request.getParameter("remindTime5");
      }
      if(beginTime.equals("")){
        affair.setBeginTime(curDate);
      }else{
        affair.setBeginTime(dateFormat.parse(beginTime));
      }
      if(!endTime.equals("")){
        affair.setEndTime(dateFormat.parse(endTime));
      }
      //System.out.println(remindTime);
      if(remindTime.equals("")){
        affair.setRemindTime(curDateStr.substring(11, 19));
      }else{
        affair.setRemindTime(remindTime);
      }
      affair.setRemindDate(remindDate);
      affair.setContent(content);
      affair.setType(type);
      affair.setManagerId(String.valueOf(userId));
      
      //判断同时要不要加最后一次提醒时间
/*      if(affair.getBeginTime().compareTo(curDate1)<=0){
        if(type.equals("2")){
          affair.setLastRemind(curDate);
        }else if(type.equals("3")){
          if(remindDate.equals(String.valueOf(week))){
            affair.setLastRemind(curDate);
          }
        }else if(type.equals("4")&&remindDate.equals(String.valueOf(day))){
          affair.setLastRemind(curDate);
        }else if(type.equals("5")&&remindDate.equals(String.valueOf(month)+"-"+String.valueOf(day))){
          affair.setLastRemind(curDate);
        }
      }*/
      
      T9AffairLogic tal = new T9AffairLogic();
      for (int i = 0; i < userIds.length; i++) {
        affair.setUserId(userIds[i]);
        int maxSeqId = tal.addAffair(dbConn, affair);
          //短信smsType, content, remindUrl, toId, fromId
      /*    T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          sb.setContent("日常事务提醒："+content);
          sb.setRemindUrl("/core/funcs/calendar/affairnote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
          sb.setToId(userIds[i]);
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);*/
          T9AffairAct.selectAffairRemindByToday(request, response); //判断时候需要提醒
          if(moblieSmsRemind!=null){
            T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
            sms2Logic.remindByMobileSms(dbConn,userIds[i], userId, "日常事务："+content, new Date());
          }
      }  
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /*
   * 添加任务多个新增,
   */
  public String addTask(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      String userIdStr = request.getParameter("user");
      String userIds[] = userIdStr.split(",");
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
      int userId = user.getSeqId();
      T9Task task = new T9Task();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String remindTime = request.getParameter("remindTime");
      String finishTime = request.getParameter("finishTime");
      String content = request.getParameter("content");
      String taskNo = request.getParameter("taskNo");
      String totalTime = request.getParameter("totalTime");
      String useTime = request.getParameter("useTime");
      String rate = request.getParameter("rate");
      String color = request.getParameter("color");
      String important = request.getParameter("important");
      String taskType = request.getParameter("taskType");
      String taskStatus = request.getParameter("taskStatus");
      String subject = request.getParameter("subject");  
      String smsRemind = request.getParameter("smsRemind");
      String moblieSmsRemind = request.getParameter("moblieSmsRemind");
      content = content.replaceAll("\\\n", "");
      content = content.replaceAll("\\\r", "");
      if(!beginDate.equals("")){
        task.setBeginDate(dateFormat1.parse(beginDate));
      }
      if(!endDate.equals("")){
        task.setEndDate(dateFormat1.parse(endDate));
      }
      if(!finishTime.equals("")){
        task.setFinishTime(dateFormat.parse(finishTime));
      }

      task.setColor(color);
      task.setImportant(important);
      task.setRate(rate);
      task.setContent(content);
      task.setTaskStatus(taskStatus);
      task.setTaskType(taskType);
      task.setSubject(subject);
      task.setManagerId(String.valueOf(userId));
      if(taskNo.equals("")){
        taskNo = "0";
      }
      task.setTaskNo(Integer.parseInt(taskNo));
      if(totalTime.equals("")){
        totalTime = "0";
      }
      task.setTotalTime(totalTime);
      if(useTime.equals("")){
        useTime = "0";
      }
      task.setUseTime(useTime);
      T9TaskLogic ttl = new T9TaskLogic();
      for (int i = 0; i < userIds.length; i++) {
        task.setUserId(userIds[i]);
        int maxSeqId  = ttl.addTask(dbConn, task);
        if(smsRemind!=null && !T9Utility.isNullorEmpty(remindTime)){
          //短信smsType, content, remindUrl, toId, fromId
          T9SmsBack sb = new T9SmsBack();
          sb.setSmsType("5");
          if(remindTime!=null&&!remindTime.equals("")){
            sb.setSendDate(T9Utility.parseDate(remindTime));
          }
          sb.setContent("请查看"+user.getUserName()+"安排的任务！ 标题："+subject);
          sb.setRemindUrl("/core/funcs/calendar/tasknote.jsp?seqId="+maxSeqId+"&openFlag=1&openWidth=300&openHeight=250");
          sb.setToId(userIds[i]);
          sb.setFromId(userId);
          T9SmsUtil.smsBack(dbConn, sb);
        }
  
        if(moblieSmsRemind!=null ){
          T9MobileSms2Logic sms2Logic = new T9MobileSms2Logic();
          sms2Logic.remindByMobileSms(dbConn,userIds[i], userId, "任务安排："+content, new Date());
        }
      }
      //request.setAttribute(T9ActionKeys.RET_DATA, "data");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
