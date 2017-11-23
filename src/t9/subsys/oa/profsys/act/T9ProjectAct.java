package t9.subsys.oa.profsys.act;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.codeclass.logic.T9CodeClassLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9UserPrivLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.finance.data.T9BudgetApply;
import t9.subsys.oa.finance.logic.T9BudgetApplyLogic;
import t9.subsys.oa.profsys.data.T9Project;
import t9.subsys.oa.profsys.data.T9ProjectCalendar;
import t9.subsys.oa.profsys.logic.T9ProjectCalendarLogic;
import t9.subsys.oa.profsys.logic.T9ProjectLogic;
import t9.subsys.oa.profsys.logic.in.T9InProjectLogic;

public class T9ProjectAct {
  /**
   * 新建更新项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addUpdateProject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9Project project = (T9Project)T9FOM.build(request.getParameterMap());
      T9ProjectLogic projectLogic = new T9ProjectLogic();

      //文件柜中上传附件
      String attachmentName = request.getParameter("attachmentName");
      String attachmentId = request.getParameter("attachmentId");
      T9SelAttachUtil sel = new T9SelAttachUtil(request,"profsys");
      String attachNewId = sel.getAttachIdToString(",");
      String attachNewName = sel.getAttachNameToString("*");
      if(!"".equals(attachNewId) && !"".equals(attachmentId) &&  !attachmentId.trim().endsWith(",")){
        attachmentId += ",";
      }
      attachmentId += attachNewId;
      if(!"".equals(attachNewName) && !"".equals(attachmentName)  && !attachmentName.trim().endsWith("*")){
        attachmentName += "*";
      }
      attachmentName += attachNewName;
      project.setAttachmentId(attachmentId);
      project.setAttachmentName(attachmentName);
      int seqId = 0;
      if(project !=null){
        project.setProjCreator(user.getSeqId() + "");
        project.setDeptId(user.getDeptId());
        project.setProjDate(new Date());
        if(project.getSeqId()>0){
          seqId = project.getSeqId();
          projectLogic.updateProject(dbConn, project);
        }else{
          project.setProjStatus("0");
          project.setPrintStatus("0");
          seqId = projectLogic.addProject(dbConn, project);
        }
      }
      String data = "{seqId:" + seqId + "}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 新建项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateProjectCopy(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMM");
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      Calendar cl = Calendar.getInstance();
      int curYear = cl.get(Calendar.YEAR);
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      // 保存从文件柜、网络硬盘选择附件
      T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "profsys");
      String attIdStr = sel.getAttachIdToString(",");
      String attNameStr = sel.getAttachNameToString("*");
      
      
      T9Project project = new T9Project();
      String seqId = fileForm.getParameter("seqId");
      project.setProjCreator(userId + "");
      project.setDeptId(user.getDeptId());
      project.setProjType(T9Utility.isNullorEmpty(fileForm.getParameter("projType")) ? "" : fileForm.getParameter("projType"));
      project.setProjNum(T9Utility.isNullorEmpty(fileForm.getParameter("projNum")) ? "" : fileForm.getParameter("projNum"));
      project.setProjGroupName(T9Utility.isNullorEmpty(fileForm.getParameter("projGroupName")) ? "" : fileForm.getParameter("projGroupName"));
      project.setProjVisitType (T9Utility.isNullorEmpty(fileForm.getParameter("projVisitType")) ? "" : fileForm.getParameter("projVisitType"));
      project.setProjActiveType(T9Utility.isNullorEmpty(fileForm.getParameter("projActiveType")) ? "" : fileForm.getParameter("projActiveType"));
      project.setProjLeader(T9Utility.isNullorEmpty(fileForm.getParameter("projLeader")) ? "" : fileForm.getParameter("projLeader"));
      project.setProjManager(T9Utility.isNullorEmpty(fileForm.getParameter("projManager")) ? "" : fileForm.getParameter("projManager"));
      project.setProjArriveTime(T9Utility.isNullorEmpty(fileForm.getParameter("projArriveTime")) ? null : dateFormat.parse(fileForm.getParameter("projArriveTime")));
      project.setProjLeaveTime(T9Utility.isNullorEmpty(fileForm.getParameter("projLeaveTime")) ? null : dateFormat.parse(fileForm.getParameter("projLeaveTime")));
      project.setProjStartTime(T9Utility.isNullorEmpty(fileForm.getParameter("projStartTime")) ? null : dateFormat.parse(fileForm.getParameter("projStartTime")));
      project.setProjEndTime(T9Utility.isNullorEmpty(fileForm.getParameter("projEndTime")) ? null : dateFormat.parse(fileForm.getParameter("projEndTime")));
      project.setProjList(T9Utility.isNullorEmpty(fileForm.getParameter("projList")) ? "" : fileForm.getParameter("projList"));
      project.setProjOrganizer(T9Utility.isNullorEmpty(fileForm.getParameter("projOrganizer")) ? "" : fileForm.getParameter("projOrganizer"));
      project.setProjOperator(T9Utility.isNullorEmpty(fileForm.getParameter("projOperator")) ? "" : fileForm.getParameter("projOperator"));
      project.setProjSponsor(T9Utility.isNullorEmpty(fileForm.getParameter("projSponsor")) ? "" : fileForm.getParameter("projSponsor"));
      project.setProjDept(T9Utility.isNullorEmpty(fileForm.getParameter("projDept")) ? "" : fileForm.getParameter("projDept"));
      project.setProjViwer(T9Utility.isNullorEmpty(fileForm.getParameter("projViwer")) ? "" : fileForm.getParameter("projViwer"));
      project.setProjLeaderDescription(T9Utility.isNullorEmpty(fileForm.getParameter("projLeaderDescription")) ? "" : fileForm.getParameter("projLeaderDescription"));
      project.setProjUnitDescription(T9Utility.isNullorEmpty(fileForm.getParameter("projUnitDescription")) ? "" : fileForm.getParameter("projUnitDescription"));
      project.setProjNote(T9Utility.isNullorEmpty(fileForm.getParameter("projNote")) ? "" : fileForm.getParameter("projNote"));
      project.setProjStatus(T9Utility.isNullorEmpty(fileForm.getParameter("projStatus")) ? "" : fileForm.getParameter("projStatus"));
      project.setPTotal(T9Utility.isInteger(fileForm.getParameter("pTotal")) ? Integer.parseInt(fileForm.getParameter("pTotal")) : 0);
      project.setPYx(T9Utility.isInteger(fileForm.getParameter("pYx")) ? Integer.parseInt(fileForm.getParameter("pYx")) : 0);
      project.setPCouncil(T9Utility.isInteger(fileForm.getParameter("pCouncil")) ? Integer.parseInt(fileForm.getParameter("pCouncil")) : 0);
      project.setPGuest(T9Utility.isInteger(fileForm.getParameter("pGuest")) ?  Integer.parseInt(fileForm.getParameter("pGuest")):0);
      project.setPurposeCountry(T9Utility.isNullorEmpty(fileForm.getParameter("purposeCountry")) ? "" : fileForm.getParameter("purposeCountry"));
      project.setCountryTotal(T9Utility.isInteger(fileForm.getParameter("countryTotal")) ? Integer.parseInt(fileForm.getParameter("countryTotal")) : 0);
      project.setBudgetId(T9Utility.isInteger(fileForm.getParameter("budgetId")) ? Integer.parseInt(fileForm.getParameter("budgetId")) : 0);
      project.setProjDate(new Date());
    
      Iterator<String> iKeys = fileForm.iterateFileFields();
      String filePath = T9SysProps.getAttachPath()  + File.separator  + "profsys" + File.separator  + dateFormat2.format(new Date()); // T9SysProps.getAttachPath()
      String attachmentId = "";
      String attachmentName = "";
      while (iKeys.hasNext()) {
        String fieldName = iKeys.next();
        String fileName = fileForm.getFileName(fieldName);
        String regName = fileName;

        if (T9Utility.isNullorEmpty(fileName)) {
          continue;
        }
        T9InnerEMailUtilLogic emul = new T9InnerEMailUtilLogic();
        String rand = emul.getRandom();
        attachmentId =  dateFormat2.format(new Date()) + "_" + attachmentId + rand+",";
        attachmentName = attachmentName + fileName+"*";
        fileName = rand + "_" + fileName;
        fileForm.saveFile(fieldName, filePath + File.separator  + fileName);
      }
      attachmentId = attachmentId + attIdStr;
      attachmentName = attachmentName + attNameStr;

 
      
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      if(T9Utility.isInteger(seqId)){//更新
        project.setSeqId(Integer.parseInt(seqId));
        
        //先查出数据库的附件，然后加上
        String attIdStrTemp = "";
        String attNameStrTemp = "";
        T9Project projectTemp  = projectLogic.getProjectById(dbConn, seqId)  ;
        if(projectTemp!=null){
          attIdStrTemp  = projectTemp.getAttachmentId();
          attNameStrTemp = projectTemp.getAttachmentName();
        }
        if(!T9Utility.isNullorEmpty(attIdStrTemp)){
          attachmentId = attachmentId + "," + attIdStrTemp;
        }
        if(!T9Utility.isNullorEmpty(attachmentName)){
          attachmentName = attachmentName + "*" + attNameStrTemp;
        }
        project.setAttachmentId(attachmentId);
        project.setAttachmentName(attachmentName);
        projectLogic.updateProject(dbConn, project);
      }else{//新增
        project.setAttachmentId(attachmentId);
        project.setAttachmentName(attachmentName);
        int seqIdInt = projectLogic.addProject(dbConn, project);
        seqId = seqIdInt + "";

      }
      String path = request.getContextPath();
      response.sendRedirect(path+ "/subsys/oa/profsys/in/baseinfo/news/base.jsp?seqId=" + seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "";
  }
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
      T9Project project = projectLogic.getProjectById(dbConn, seqId);
      if(project!=null){
        data = T9FOM.toJson(project).toString();
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
   * 从系统代码设置得到所有类型

   * 根据seqId（codeClass） 得到所有的codeItem
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeItem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String classNo = request.getParameter("classNo");
      if(classNo==null){
        classNo = "";
      }
      T9CodeClassLogic codeLogic = new T9CodeClassLogic();
      String data = "[";
      List<T9CodeItem> itemList = new ArrayList<T9CodeItem>();
      itemList = codeLogic.getCodeItem(dbConn, classNo);
      for (int i = 0; i < itemList.size(); i++) {
        T9CodeItem item = itemList.get(i);
        data = data + T9FOM.toJson(item) + ",";
      }
      if (itemList.size() > 0) {
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";
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
   * 取出国家
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCountry(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    String moduleId = request.getParameter("moduleId");
    String privOp = request.getParameter("privOp");
    String privNoFlagStr = request.getParameter("privNoFlag");
    int privNoFlag = 0;
    if (!T9Utility.isNullorEmpty(privNoFlagStr)) {
      privNoFlag = Integer.parseInt(privNoFlagStr);
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9UserPrivLogic logic = new T9UserPrivLogic();
      T9Person loginUser = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      List<T9CodeItem> itemList = new ArrayList<T9CodeItem>();
      T9CodeClassLogic codeLogic = new T9CodeClassLogic();
      itemList = codeLogic.getCodeItem(dbConn, "NATION");
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < itemList.size(); i++) {
        T9CodeItem item = itemList.get(i);
        String str = "{";
        str += "privNo:" + item.getSeqId() + ",";  
        str += "privName:\"" + T9Utility.encodeSpecial(item.getClassDesc()) + "\"";  
        str += "},";
        sb.append(str);
      }
      if (itemList.size() > 0) {
        sb.deleteCharAt(sb.length() - 1);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "get Success");
      request.setAttribute(T9ActionKeys.RET_DATA, "[" + sb.toString() + "]");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /***
   * 更新的附件ById
     删除一个附件
   * @return
   * @throws Exception 
   */
  public String deleleFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String attachId = request.getParameter("attachId");
      String attachName = request.getParameter("attachName");
      if(seqId==null){
        seqId = "";
      }
      if(attachId==null){
        attachId = "";
      }
      if(attachName==null){
        attachName = "";
      }
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      T9Project project  = null ;
      String updateFlag = "0";
      if(seqId!=null&&!seqId.equals("")){
        project = projectLogic.getProjectById(dbConn, seqId)  ;
       if(project!=null){
         String attachmentId = project.getAttachmentId();
         String attachmentName = project.getAttachmentName();
         if(attachmentId==null){
           attachmentId = "";
         }
         if(attachmentName==null){
           attachmentName = "";
         }
         String[] attachmentIdArray = attachmentId.split(",");
         String[] attachmentNameArray = attachmentName.split("\\*");
         String newAttachmentId = "";
         String newAttachmentName = "";
         for (int i = 0; i < attachmentIdArray.length; i++) {
           if(!attachmentIdArray[i].equals(attachId)){
             newAttachmentId = newAttachmentId +attachmentIdArray[i] + ",";
           }
         }
         for (int i = 0; i < attachmentNameArray.length; i++) {
           if(!attachmentNameArray[i].equals(attachName)){
             newAttachmentName = newAttachmentName +attachmentNameArray[i] + "*";
           }
         }
         
         projectLogic.updateFile(dbConn,"PROJECT", newAttachmentId, newAttachmentName, seqId);
         updateFlag = "1";
       }
      }
      String data = "{updateFlag:"+updateFlag+"}";
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
  
  
  
  /**
   * 检索项目BY projType
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

        String projType = request.getParameter("projType");
        if(T9Utility.isNullorEmpty(projType)){
          projType = "0";
        }
        T9ProjectLogic tbal = new T9ProjectLogic();
        String data = tbal.toSearchData(dbConn, request.getParameterMap(),projType);
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
