package t9.subsys.oa.profsys.act.active;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.profsys.data.T9Project;
import t9.subsys.oa.profsys.data.T9ProjectComm;
import t9.subsys.oa.profsys.data.T9ProjectFile;
import t9.subsys.oa.profsys.logic.T9ProjectLogic;
import t9.subsys.oa.profsys.logic.active.T9ActiveProjectLogic;

public class T9ActiveProjectAct {
  /**
   * 来访项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryActiveProject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String userPriv = user.getUserPriv();// 角色
      String postpriv = user.getPostPriv();// 管理范围
      String postDept = user.getPostDept();// 管理范围指定部门
      if (T9Utility.isNullorEmpty(userPriv)) {
        userPriv = "";
      }
      if (T9Utility.isNullorEmpty(postpriv)) {
        postpriv = "";
      }
      if (T9Utility.isNullorEmpty(postDept)) {
        postDept = "";
      }
      String managerStr = "";
      if(!userPriv.equals("1")){
        if(postpriv.equals("0")){
          managerStr = " = " + user.getDeptId();
        }
        if(postpriv.equals("2")){
          managerStr = " in(" + postDept + ")";
        }
      }
    
      String projType = request.getParameter("projType");
      String projStatus = request.getParameter("projStatus");
      if (T9Utility.isNullorEmpty(projType)) {
        projType = "0";// 默认为来访
      }
      if (T9Utility.isNullorEmpty(projStatus)) {
        projStatus = "0";
      }
      String projNum = request.getParameter("projNum");
      String projActiveType = request.getParameter("projActiveType");

      String projStartTime1 = request.getParameter("projStartTime1");
      String projStartTime2 = request.getParameter("projStartTime2");
      String projGropName = request.getParameter("projGropName");

      String projVisitType = request.getParameter("projVisitType");
      String projEndTime1 = request.getParameter("projEndTime1");
      String projEndTime2 = request.getParameter("projEndTime2");
      String projLeader = request.getParameter("projLeader");

      String deptId = request.getParameter("deptId");

      T9ActiveProjectLogic tbal = new T9ActiveProjectLogic();
      String data = tbal.toSearchData(dbConn, request.getParameterMap(),
          projType, projNum, projActiveType, projStartTime1, projStartTime2,
          projGropName, projVisitType, projEndTime1, projEndTime2, projLeader,
          deptId, managerStr, projStatus,userId);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }

  /**
   * 大型活动项目--信息检索
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectActiveProject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();

      String projType = request.getParameter("projType");
      String projStatus = request.getParameter("projStatus");
      if (T9Utility.isNullorEmpty(projType)) {
        projType = "0";// 默认为来访
      }

      T9ActiveProjectLogic tbal = new T9ActiveProjectLogic();
      String data = tbal.toSearchData(dbConn, request.getParameterMap(),
          projType);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 打印
   * 
   * */
  public String printActive(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String printStr = request.getParameter("printStr");
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      if (!T9Utility.isNullorEmpty(printStr)) {
        printStr = printStr.substring(0,printStr.length() - 1);
        T9ProjectLogic.printOut(dbConn,printStr);
        String[] str = {"SEQ_ID in (" + printStr + ")"};
        List<T9Project> project = T9ProjectLogic.queryProject(dbConn, str);
        request.setAttribute("printStr", printStr);
        request.setAttribute("project", project);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "打印成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/subsys/oa/profsys/active/baseinfo/news/print.jsp";
  }

  /**
   * 导出
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String exportXls(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("大型活动信息.xls", "UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control", "private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges", "bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      response.setHeader("Content-disposition", "attachment; filename=\""
          + fileName + "\"");
      ops = response.getOutputStream();
      // 接受参数
      String seqId = request.getParameter("printStr");
      ArrayList<T9DbRecord> dbL = new ArrayList<T9DbRecord>();
      if (!T9Utility.isNullorEmpty(seqId)) {
        dbL = T9ActiveProjectLogic.toInExp(dbConn, seqId);
      }

      T9JExcelUtil.writeExc(ops, dbL);
      // T9CSVUtil.CVSWrite(ops, dbL);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    } finally {
      ops.close();
    }
    return null;
  }

  /**
   * 查询大型活动项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectActive(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      T9Project project = new T9Project();
      String projNum = request.getParameter("projNum");
      String projActiveType = request.getParameter("projActiveType");
      String projStartTime = request.getParameter("projStartTime");
      String projStartTime1 = request.getParameter("projStartTime1");
      String budgetId = request.getParameter("budgetId");
      String projEndTime = request.getParameter("projEndTime");
      String projEndTime1 = request.getParameter("projEndTime1");
      String projLeader = request.getParameter("projLeader");
      String projType = request.getParameter("projType");

      project.setProjNum(projNum);
      project.setProjActiveType(projActiveType);
      project.setProjLeader(projLeader);
      project.setProjType("2");
      if (!T9Utility.isNullorEmpty(projStartTime)) {
        project.setProjStartTime(Date.valueOf(projStartTime));
      }
      if (!T9Utility.isNullorEmpty(projEndTime)) {
        project.setProjEndTime(Date.valueOf(projEndTime));
      }
      Date start = null;
      Date end = null;
      if (!T9Utility.isNullorEmpty(projStartTime1)) {
        start = Date.valueOf(projStartTime1);
      }
      if (!T9Utility.isNullorEmpty(projEndTime1)) {
        end = Date.valueOf(projEndTime1);
      }
      if (!T9Utility.isNullorEmpty(budgetId)) {
        project.setBudgetId(Integer.parseInt(budgetId));
      }
      String data = T9ActiveProjectLogic.profsysSelectActive(dbConn,request.getParameterMap(), project,start,end);
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 根据日程查询项目_人员
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryProjectByMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String memNum = request.getParameter("memNum");
      String memPosition = request.getParameter("memPosition");
      String memName = request.getParameter("memName");
      String memSex = request.getParameter("memSex");
      String unitNum = request.getParameter("unitNum");
      String unitName = request.getParameter("unitName");
      String projMemType = request.getParameter("projMemType");

      //先查出projId
     // String projId = T9ProjectCalendarLogic.queryCalendarToProjId(dbConn,activeType,activeContent,activeLeader,
         // activePartner,startTime,startTime1,endTime,endTime1,"2");
      //通用查询数据
      String data = T9ActiveProjectLogic.queryProjectMem(dbConn,request.getParameterMap(),"2",memNum,memPosition,memName,
          memSex,unitNum,unitName,"2");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 根据日程查询项目
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryProjectByCalendar(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String activeType = request.getParameter("activeType");
      String activeContent = request.getParameter("activeContent");
      String activeLeader = request.getParameter("activeLeader");
      String activePartner = request.getParameter("activePartner");
      String startTime = request.getParameter("startTime");
      String startTime1 = request.getParameter("startTime1");
      String endTime = request.getParameter("endTime");
      String endTime1 = request.getParameter("endTime1");
      String projCalendarType = request.getParameter("projCalendarType");

      //先查出projId
     // String projId = T9ProjectCalendarLogic.queryCalendarToProjId(dbConn,activeType,activeContent,activeLeader,
         // activePartner,startTime,startTime1,endTime,endTime1,"2");
      //通用查询数据
      String data = T9ActiveProjectLogic.queryProjectCalendar(dbConn,request.getParameterMap(),"2",activeType,activeContent,activeLeader,
          activePartner,startTime,startTime1,endTime,endTime1,"2");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  /**
   * 根据纪要查询项目
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryProjectByComm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectComm comm = new T9ProjectComm();
      String commNum = request.getParameter("commNum");
      String commMemCn = request.getParameter("commMemCn");
      String commMemFn = request.getParameter("commMemFn");
      String commName = request.getParameter("commName");
      String commTime = request.getParameter("commTime");
      String commPlace = request.getParameter("commPlace");
      //String projId = T9ProjectCommLogic.queryCommToProjId(dbConn,commNum,commMemCn,commMemFn,commName,commTime,commPlace,"0");
      //通用查询数据
      String data = T9ActiveProjectLogic.queryProjectComm(dbConn,request.getParameterMap(),"2",commNum,commMemCn,commMemFn,commName,commTime,commPlace,"2");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 根据文档查询项目
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryProjectByFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectFile file = new T9ProjectFile();
      String fileNum = request.getParameter("fileNum");
      String fileName = request.getParameter("fileName");
      String fileType = request.getParameter("fileType");
      String projCreator = request.getParameter("projCreator");
      String fileTitle = request.getParameter("fileTitle");
      String projFileType = request.getParameter("projFileType");

     // String projId = T9ProjectFileLogic.queryCommToProjId(dbConn,fileNum,fileName,fileType,projCreator,fileTitle,"2");
    //通用查询数据
      String data = T9ActiveProjectLogic.queryProjectFile(dbConn,request.getParameterMap(),"2",fileNum,fileName,fileType,projCreator,fileTitle,"2");
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
}
