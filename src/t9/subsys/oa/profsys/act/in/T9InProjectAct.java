package t9.subsys.oa.profsys.act.in;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.codeclass.logic.T9CodeClassLogic;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9Project;
import t9.subsys.oa.profsys.data.T9ProjectComm;
import t9.subsys.oa.profsys.data.T9ProjectFile;
import t9.subsys.oa.profsys.logic.T9ProjectLogic;
import t9.subsys.oa.profsys.logic.in.T9InProjectLogic;


public class T9InProjectAct {
  /**
   * 根据seqId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getProjectById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
  
      T9CodeClassLogic codeLogic = new T9CodeClassLogic();
      String data = "";
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      T9PersonLogic personLogic = new T9PersonLogic();
      if(T9Utility.isInteger(seqId)){
        T9Project project = projectLogic.getProjectById(dbConn, seqId);
        if(project!=null){
          String projLeaderName = "";
          if(!T9Utility.isNullorEmpty(project.getProjLeader())){
            projLeaderName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(project.getProjLeader(),dbConn));
          }
          String projManagerName = ""; 
          if(!T9Utility.isNullorEmpty(project.getProjManager())){
            projManagerName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(project.getProjManager(),dbConn));
          }
          String projDeptName = "";
          if(!T9Utility.isNullorEmpty(project.getProjDept())){
            projDeptName = T9Utility.encodeSpecial(T9ProjectLogic.deptStr(dbConn,project.getProjDept()));
          }
          String projViwerName = "";
          if(!T9Utility.isNullorEmpty(project.getProjViwer())){
            projViwerName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(project.getProjViwer(),dbConn));
          }
          String projVisitTypeName = "";
          if(!T9Utility.isNullorEmpty(project.getProjVisitType())){
            T9CodeItem  codeItem = T9InProjectLogic.getCodeItem(dbConn, project.getProjVisitType());
            if(codeItem != null){
              projVisitTypeName = T9Utility.encodeSpecial(codeItem.getClassDesc());
            }
          }
          String projActiveTypeName = "";
          if(!T9Utility.isNullorEmpty(project.getProjActiveType())){
            T9CodeItem  codeItem = T9InProjectLogic.getCodeItem(dbConn, project.getProjActiveType());
            if(codeItem != null){
              projActiveTypeName = T9Utility.encodeSpecial(codeItem.getClassDesc());
            }
          }
          String budgetItem = "";
          if(project.getBudgetId()>0){
            budgetItem = T9Utility.encodeSpecial(T9InProjectLogic.getBudgetApplyById(dbConn, project.getBudgetId()+"" ));
          }
          
          data = T9FOM.toJson(project).toString().substring(0, T9FOM.toJson(project).toString().length()-1) + ",projLeaderName:\"" +projLeaderName + "\","
                 + "projManagerName:\"" + projManagerName + "\",projDeptName:\"" + projDeptName + "\","
                 + "projViwerName:\"" + projViwerName + "\",projVisitTypeName:\"" + projVisitTypeName + "\","
                 + "projActiveTypeName:\"" + projActiveTypeName + "\",budgetItem:\"" + budgetItem + "\"}";
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 来访项目
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String queryInProject(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        int userId = user.getSeqId();
        String userPriv = user.getUserPriv();//角色
        String postpriv = user.getPostPriv();//管理范围
        String postDept = user.getPostDept();//管理范围指定部门
        if(T9Utility.isNullorEmpty(userPriv)){
          userPriv = "";
        }
        if(T9Utility.isNullorEmpty(postpriv)){
          postpriv = "";
        }
        if(T9Utility.isNullorEmpty(postDept)){
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
        if(T9Utility.isNullorEmpty(projType)){
          projType = "0";//默认为来访
        }
        if(T9Utility.isNullorEmpty(projStatus)){
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

        T9InProjectLogic tbal = new T9InProjectLogic();
        String data = tbal.toSearchData(dbConn, request.getParameterMap(),projType,projNum,projActiveType,projStartTime1,projStartTime2,projGropName,projVisitType,projEndTime1,projEndTime2,projLeader,deptId,managerStr,projStatus,userId);
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
 * 导出
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
        String fileName = URLEncoder.encode("来访信息.xls","UTF-8");
        fileName = fileName.replaceAll("\\+", "%20");
        response.setHeader("Cache-control","private");
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Accept-Ranges","bytes");
        response.setHeader("Cache-Control","maxage=3600");
        response.setHeader("Pragma","public");
        response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
        ops = response.getOutputStream();
        //接受参数
        String seqId = request.getParameter("printStr");
        ArrayList<T9DbRecord > dbL = new ArrayList<T9DbRecord >();
        if(!T9Utility.isNullorEmpty(seqId)){
          dbL = T9InProjectLogic.toInExp(dbConn, seqId);
        }

        T9JExcelUtil.writeExc(ops, dbL);
        //T9CSVUtil.CVSWrite(ops, dbL);
      } catch (Exception ex) {
        ex.printStackTrace();
        throw ex;
      } finally {
        ops.close();
      }
      return null;
    }
    /**
     * 打印
     * 
     * */
    public String printIn(HttpServletRequest request,
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
      return "/subsys/oa/profsys/in/baseinfo/news/print.jsp";
    }
    
    /**
     *按条件查询所有(分页)通用列表显示数据
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public String queryProject(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();

        String projNum = request.getParameter("projNum");
        String projActiveType = request.getParameter("projActiveType");
        String projStartTime = request.getParameter("projStartTime");
        String projStartTime1 = request.getParameter("projStartTime1");
        String budgetId = request.getParameter("budgetId");
        String projVisitType = request.getParameter("projVisitType");
        String projEndTime = request.getParameter("projEndTime");
        String projEndTime1 = request.getParameter("projEndTime1");
        String projLeader = request.getParameter("projLeader");
        String projDept = request.getParameter("projDept");
        String projManager = request.getParameter("projManager");
        //实体类封装


        T9Project project = new T9Project();
        project.setProjNum(projNum);
        project.setProjActiveType(projActiveType);
        if (!T9Utility.isNullorEmpty(budgetId)) {
          project.setBudgetId(Integer.parseInt(budgetId));
        }
        project.setProjLeader(projLeader);
        project.setProjVisitType(projVisitType);
        project.setProjDept(projDept);
        project.setProjManager(projManager);
        if (!T9Utility.isNullorEmpty(projStartTime)) {
          project.setProjArriveTime(Date.valueOf(projStartTime));
        }
        if (!T9Utility.isNullorEmpty(projEndTime)) {
          project.setProjLeaveTime(Date.valueOf(projEndTime));
        }
        
        Date statrTime = null;
        Date endTime = null;
        if (!T9Utility.isNullorEmpty(projStartTime1)) {
          statrTime = Date.valueOf(projStartTime1);
        }
        if (!T9Utility.isNullorEmpty(projEndTime1)) {
          endTime = Date.valueOf(projEndTime1);
        }

        String data = T9InProjectLogic.queryProject(dbConn,request.getParameterMap(),project,statrTime,endTime);
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
     * 根基项目人员查询项目
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
        String memBirth = request.getParameter("memBirth");
        String memIdNum = request.getParameter("memIdNum");
        String projMemType = request.getParameter("projMemType");

       // String projId = T9ProjectMemLogic.queryMemToProjId(dbConn,memNum,memPosition,memName,memSex,memBirth,memIdNum,"0");
      //通用查询数据
        String data = T9InProjectLogic.queryProjectMem(dbConn,request.getParameterMap(),"0",memNum,memPosition,memName,memSex,memBirth,memIdNum,"0");
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
          //  activePartner,startTime,startTime1,endTime,endTime1,"0");
        //通用查询数据
        String data = T9InProjectLogic.queryProjectCalendar(dbConn,request.getParameterMap(),"0",activeType,activeContent,activeLeader,
            activePartner,startTime,startTime1,endTime,endTime1,"0");
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
        String data = T9InProjectLogic.queryProjectComm(dbConn,request.getParameterMap(),"0",commNum,commMemCn,commMemFn,commName,commTime,commPlace,"0");
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

       // String projId = T9ProjectFileLogic.queryCommToProjId(dbConn,fileNum,fileName,fileType,projCreator,fileTitle,"0");
      //通用查询数据
        String data = T9InProjectLogic.queryProjectFile(dbConn,request.getParameterMap(),"0",fileNum,fileName,fileType,projCreator,fileTitle,"0");
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
