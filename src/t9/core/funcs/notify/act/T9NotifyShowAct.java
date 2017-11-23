package t9.core.funcs.notify.act;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.news.data.T9News;
import t9.core.funcs.news.data.T9NewsComment;
import t9.core.funcs.news.logic.T9NewsManageLogic;
import t9.core.funcs.news.logic.T9NewsShowLogic;
import t9.core.funcs.notify.data.T9Notify;
import t9.core.funcs.notify.logic.T9NotifyManageLogic;
import t9.core.funcs.notify.logic.T9NotifyShowLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysPropKeys;
import t9.core.global.T9SysProps;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9NotifyShowAct {
  private static Logger log = Logger.getLogger("t9.core.funcs.dept.act");
  private static final int MAX = 10;
  private T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();
  public boolean findToId(String object,String object2){
    boolean temp = false;
    if(object != null && !"".equals(object)){
      String[] toIds = object.split(",");
      for(int j = 0 ;j < toIds.length ; j++){
        String toIdTemp =  toIds[j];
        if(toIdTemp.equals(object2)){
          temp = true;
          break;
        }
      }
    }
    return temp;
  }
  
  public String getnotifyNoReadList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String format = "";
    String anonymityYn = "";
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
    if(pageIndexStr == null || pageIndexStr.trim() == ""|| pageIndexStr.replaceAll(" ", "").length() <1) {
      pageIndexStr = "1";
    }

//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      String data = "";
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
        data = notifyShowLogic.getnotifyNoReadList(dbConn, loginUser, Integer.parseInt(showLenStr),type,ascDesc,field, 
            Integer.parseInt(pageIndexStr));
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  //全部公告
  public String getnotifyShowList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String format = "";
    String anonymityYn = "";
    String type = request.getParameter("type");//下拉框中类型
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段

    String sendTime = request.getParameter("sendTime");
    String showLenStr = request.getParameter("showLength");//每页显示长度
    String pageIndexStr = request.getParameter("pageIndex");//页码数
    if(pageIndexStr == null || pageIndexStr.trim() == ""|| pageIndexStr.replaceAll(" ", "").length() <1) {
      pageIndexStr = "1";
    }

//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    List<T9Notify> list = new ArrayList<T9Notify>();
    try {
      String data = "";
      int temp = 0;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int loginDeptId = loginUser.getDeptId();
      String userPriv = loginUser.getUserPriv();
      int seqUserId = loginUser.getSeqId();
        data = notifyShowLogic.getnotifyShowList(dbConn, loginUser, Integer.parseInt(showLenStr),type,ascDesc,field, 
            Integer.parseInt(pageIndexStr),sendTime);
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
        request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String showObject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9ORM orm = new T9ORM();
  
    T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();
    String seqId = request.getParameter("seqId");
    String isManage = request.getParameter("isManage");
  
    String data = "";
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Notify notify = (T9Notify)orm.loadObjSingle(dbConn, T9Notify.class, Integer.parseInt(seqId));
      Date endDate = null;
      String fromId = "";
      if (notify != null) {
        endDate = notify.getEndDate();
        fromId = notify.getFromId();
        if (!T9Utility.null2Empty(notify.getFormat()).equals("2")) {
          byte[] byteContent = notify.getCompressContent();
          if (byteContent == null) {
            notify.setContent("");
          }else {
            notify.setContent(new String(byteContent, "UTF-8"));
          }
        }
      }else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "该公告已删除");
        return "/core/inc/rtjson.jsp";
      }
      
      String flag = T9Utility.null2Empty(request.getParameter("flag"));
      if ("".equals(flag)) {
        if((!"".equals(endDate)&&endDate!=null)&&!"1".equals(isManage)&&endDate.compareTo(new Date())<0) {
          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
          request.setAttribute(T9ActionKeys.RET_MSRG, "该公告通知已终止");
          return "/core/inc/rtjson.jsp";
        }
      }
      
      
//      if(!"1".equals(notify.getPublish())) {
//        if(!fromId.equals(Integer.toString(person.getSeqId()))) {
//          request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
//          request.setAttribute(T9ActionKeys.RET_MSRG, "该公告通知未发布。");
//          return "/core/inc/rtjson.jsp";
//        }
//      }
      
      if(!T9Utility.isNullorEmpty(fromId)&&!"1".equals(person.getUserPriv()) && !"1".equals(person.getPostPriv()) && !fromId.equals(String.valueOf(person.getSeqId()))
          &&!"0".equals(notify.getToId())&&findToId(notify.getToId(),Integer.toString(person.getDeptId()))==false
          &&findToId(notify.getPrivId(),person.getUserPriv())==false
          &&findToId(notify.getUserId(),Integer.toString(person.getSeqId()))==false && !notify.getAuditer().equalsIgnoreCase(person.getSeqId()+"")) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "您无权限查看该公告。");
        return "/core/inc/rtjson.jsp";
      }
      data = notifyShowLogic.showObject(dbConn,person,Integer.parseInt(seqId),isManage,notify);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "该公告已删除。");
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  //我的办公桌公告查询
  public String queryNotify(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String data = "";
    int pageIndex = 1;
    int showLen = 10;
    T9Notify notify = (T9Notify) T9FOM.build(request.getParameterMap());
//    String pageIndexStr = request.getParameter("pageIndex");
//    String showLengthStr = request.getParameter("showLength");
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段
    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    T9Person loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();

      data = notifyShowLogic.queryNotify(dbConn,notify,loginUser,beginDate,endDate,ascDesc,field);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage().substring(ex.getMessage().lastIndexOf(":")+1,ex.getMessage().length()),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  public String queryNotify2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String data = "";
    int pageIndex = 1;
    int showLen = 10;
    T9Notify notify = (T9Notify) T9FOM.build(request.getParameterMap());
//    String pageIndexStr = request.getParameter("pageIndex");
//    String showLengthStr = request.getParameter("showLength");
    String ascDesc = request.getParameter("ascDesc");//升序还是降序
    String field = request.getParameter("field");//排序的字段

    String beginDate = request.getParameter("beginDate");
    String endDate = request.getParameter("endDate");
    T9Person loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
     
      T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();

      data = notifyShowLogic.queryNotify2(dbConn,notify,loginUser,beginDate,endDate,ascDesc,field);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage().substring(ex.getMessage().lastIndexOf(":")+1,ex.getMessage().length()),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  public String showReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9Notify notify = null;
    String seqId = request.getParameter("seqId");
    String displayAll = request.getParameter("displayAll");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9NotifyShowLogic notifyShowLogic = new T9NotifyShowLogic();
      String data = notifyShowLogic.showReader(dbConn, seqId,displayAll);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String test(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    T9News news = null;
    T9DeptLogic deptLogic = new T9DeptLogic();
    String seqId = request.getParameter("seqId");
    String displayAll = request.getParameter("displayAll");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = deptLogic.getDeptTreeJson(0, dbConn);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
  
  public String deleteReader(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    T9NotifyManageLogic notifyManageLogic = new T9NotifyManageLogic();
    T9Person loginUser = null;
    Connection dbConn = null;
    String seqId = request.getParameter("seqId");    
     loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
     Statement st = null;
     ResultSet rs = null;
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn) request
           .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       String updateSql = "update NOTIFY set READERS='' where SEQ_ID='"+seqId+"'";
       if(!"1".equals(loginUser.getUserPriv())){
         updateSql  =  updateSql + " and FROM_ID ='"+loginUser.getSeqId()+"'";
       }
       st = dbConn.createStatement();
      st.executeUpdate(updateSql);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    request.setAttribute(T9ActionKeys.RET_MSRG,"终止生效状态已修改");
  } catch (Exception ex) {
    String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, message);
    throw ex;
  }
  //return "/core/funcs/dept/deptinput.jsp";
  //?deptParentDesc=+deptParentDesc
  return "/core/funcs/notify/show/showReader.jsp";
  }
  
  /**
   * 标记所有为已读
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String changeNoReadAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String changeSql = "update NOTIFY set READERS ="+dbLikeLast("READERS",String.valueOf(loginUser.getSeqId()))+" where PUBLISH='1' and "+ dbLikePre("READERS","','")+" not like '%"+","+loginUser.getSeqId()+",%' or READERS is null";
      //T9Out.println(changeSql);
      stmt = dbConn.createStatement();
      stmt.executeUpdate(changeSql);
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    String foward = request.getParameter(T9ActionKeys.RET_METHOD_FORWARD);
    if (T9Utility.null2Empty(foward).equalsIgnoreCase("all")) {
      return "/core/funcs/notify/show/notifyAll.jsp";
    }else {
      return "/core/funcs/notify/show/notifyNoRead.jsp";
    }
  }
  
  public static String dbLikeLast(String fieldName, String value) throws SQLException {
    String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
          return "ISNULL("+fieldName+",'')" + "+ '" + value+",' ";
        }else if (dbms.equals("mysql")) {
          return "concat(IFNULL("+fieldName+",''),'" + value + ",')";
        }else if (dbms.equals("oracle")) {
          return fieldName + "||'" + value+",'";
        }else {
          throw new SQLException("not accepted dbms");
        } 
    }
    
    public static String dbLikePre(String fieldName, String fix) throws SQLException {
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
          return fix +"+" +" ISNULL("+ fieldName +",'')";
        }else if (dbms.equals("mysql")) {
          return "concat("+fix+",IFNULL("+fieldName+",''))";
        }else if (dbms.equals("oracle")) {
          return fix + "||" + fieldName;
        }else {
          throw new SQLException("not accepted dbms");
        } 
    }
  
  /**
   * 公告通知桌面模块
   * @param request
   * @param response
   * @return
   * @throws Exception
   */

  public String getdeskNotifyAllList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    Statement stmt = null;
    ResultSet rs = null;
    StringBuffer sb = new StringBuffer("[");
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    int count = 0;
    int len = 0;
    String queryNotifySql = "";
//  String loginUserId = request.getParameter("loginUserId");
    T9Person loginUser = null;
    loginUser = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String type = request.getParameter("type");
    List<T9News> list = new ArrayList<T9News>();
  
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int loginDeptId = loginUser.getDeptId();
      String userPriv = loginUser.getUserPriv();
      int seqUserId = loginUser.getSeqId();
      String dbms = T9SysProps.getString(T9SysPropKeys.DBCONN_DBMS);
      if(loginUser.isAdmin()){
        if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
          queryNotifySql= "SELECT SEQ_ID,FROM_ID,TO_ID,SUBJECT,[TOP],TOP_DAYS,PRIV_ID,USER_ID,READERS,"
            +"TYPE_ID,SEND_TIME,BEGIN_DATE,FORMAT from NOTIFY  where  BEGIN_DATE<="+ T9DBUtility.currDateTime()+" and (END_DATE>" + T9DBUtility.currDateTime()
            +" or END_DATE is null) and PUBLISH='1' ";
        }else{
          queryNotifySql= "SELECT SEQ_ID,FROM_ID,TO_ID,SUBJECT,TOP,TOP_DAYS,PRIV_ID,USER_ID,READERS,"
            +"TYPE_ID,SEND_TIME,BEGIN_DATE,FORMAT from NOTIFY  where  BEGIN_DATE<="+ T9DBUtility.currDateTime()+" and (END_DATE>" + T9DBUtility.currDateTime()
            +" or END_DATE is null) and PUBLISH='1' ";
        }
      }else{     
        if (dbms.equals(T9Const.DBMS_SQLSERVER)) {
          queryNotifySql = "SELECT SEQ_ID,FROM_ID,TO_ID,SUBJECT,[TOP],TOP_DAYS,PRIV_ID,USER_ID,READERS,"
            +"TYPE_ID,SEND_TIME,BEGIN_DATE,FORMAT from NOTIFY  where  BEGIN_DATE<="+ T9DBUtility.currDateTime() +" and (END_DATE>"+T9DBUtility.currDateTime() +
            " or END_DATE is null) and PUBLISH='1' and ("+ T9DBUtility.findInSet(Integer.toString(loginDeptId), "TO_ID")
            + " or " + T9DBUtility.findInSet(userPriv,"PRIV_ID") +" or " 
            + T9DBUtility.findInSet(Integer.toString(seqUserId),"USER_ID" ) + " or "+T9DBUtility.findInSet("0", "TO_ID")+") ";
        }else {
          queryNotifySql = "SELECT SEQ_ID,FROM_ID,TO_ID,SUBJECT,TOP,TOP_DAYS,PRIV_ID,USER_ID,READERS,"
            +"TYPE_ID,SEND_TIME,BEGIN_DATE,FORMAT from NOTIFY  where  BEGIN_DATE<="+ T9DBUtility.currDateTime() +" and (END_DATE>"+T9DBUtility.currDateTime() +
            " or END_DATE is null) and PUBLISH='1' and ("+ T9DBUtility.findInSet(Integer.toString(loginDeptId), "TO_ID")
            + " or " + T9DBUtility.findInSet(userPriv,"PRIV_ID") +" or " 
            + T9DBUtility.findInSet(Integer.toString(seqUserId),"USER_ID" ) + " or "+T9DBUtility.findInSet("0", "TO_ID")+") ";
        }
      }
      if (!T9Utility.isNullorEmpty(type)) {
        if (!"0".equals(type)) {
          queryNotifySql +=  " and TYPE_ID='"+ type + "' ";
        } else {
          queryNotifySql = queryNotifySql + " and (TYPE_ID='' or TYPE_ID=' ' or TYPE_ID is null)";
        }
      }
      
      queryNotifySql += " order by SEND_TIME desc";
      
      //T9Out.println(queryNotifySql);
      stmt = dbConn.createStatement();
      rs = stmt.executeQuery(queryNotifySql);
      
      while(rs.next() && ((++len) < MAX)) {
/*        if("ALL_DEPT".equals(rs.getString("TO_ID"))||
            "0".equals(rs.getString("TO_ID"))||
            findToId(rs.getString("TO_ID"),Integer.toString(loginDeptId))==true||
            findToId(rs.getString("PRIV_ID"),userPriv)==true||
            findToId(rs.getString("USER_ID"),Integer.toString(seqUserId))==true){*/
          count ++;
          int readerCount = 0;
          String readers = rs.getString("READERS");
          if(!"".equals(readers)&&readers!=null){
            readerCount = readers.split(",").length;
          }
          sb.append("{");
          sb.append("seqId:" + rs.getInt("SEQ_ID"));
          sb.append(",subject:\"" + T9Utility.encodeSpecial(rs.getString("SUBJECT")) + "\"");
          sb.append(",readerCount:\"" + readerCount + "\"");
          sb.append(",sendTime:\"" + rs.getDate("SEND_TIME") + "\"");
          sb.append(",iread:\"" + notifyShowLogic.haveRead(dbConn, loginUser.getSeqId(), rs.getInt("SEQ_ID")) + "\"");
          sb.append("},");
       /* }*/
      }
      if(count>0) {
        sb.deleteCharAt(sb.length() - 1); 
        }
      sb.append("]");
      //T9Out.println(sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    } catch (Exception ex) {
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    //return "/core/funcs/dept/deptinput.jsp";
    //?deptParentDesc=+deptParentDesc
    return "/core/inc/rtjson.jsp";
  }
}
