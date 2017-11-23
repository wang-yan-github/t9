package t9.subsys.oa.profsys.act.out;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9Project;
import t9.subsys.oa.profsys.logic.out.T9OutProjectCalendarLogic;
import t9.subsys.oa.profsys.logic.out.T9OutProjectCommLogic;
import t9.subsys.oa.profsys.logic.out.T9OutProjectFileLogic;
import t9.subsys.oa.profsys.logic.out.T9OutProjectMemLogic;
import t9.subsys.oa.profsys.logic.out.T9ProjectLogic;



public class T9ProjectAct {
  /**
   * 新建项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addProject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Project project = (T9Project)T9FOM.build(request.getParameterMap());

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

      String date = T9ProjectLogic.addProject(dbConn,project);
      date = "{seqId:" + date + "}";
      request.setAttribute(T9ActionKeys.RET_DATA,date);
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
   * 修改项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateProject(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Project project = (T9Project)T9FOM.build(request.getParameterMap());

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

      T9ProjectLogic.updateProject(dbConn,project);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelect(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      //      管理范围为全体，POST_PRIV=1
      //      管理范围为本部门,登录者自己部门的，POST_PRIV=0
      //      管理范围为指定部门，POST_PRIV=2 为指定部门ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      String deptId = String.valueOf(user.getDeptId());//登录者的部门
      if(T9Utility.isNullorEmpty(postpriv)){
        postpriv = "";
      }
      if(T9Utility.isNullorEmpty(postDept)){
        postDept = "0";
      }
      if(T9Utility.isNullorEmpty(deptId)){
        deptId = "0";
      }

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

      if (!T9Utility.isNullorEmpty(projStartTime)) {
        project.setProjStartTime(Date.valueOf(projStartTime));
      }
      if (!T9Utility.isNullorEmpty(projEndTime)) {
        project.setProjEndTime(Date.valueOf(projEndTime));
      }

      Date statrTime = null;
      Date endTime = null;
      if (!T9Utility.isNullorEmpty(projStartTime1)) {
        statrTime = Date.valueOf(projStartTime1);
      }
      if (!T9Utility.isNullorEmpty(projEndTime1)) {
        endTime = Date.valueOf(projEndTime1);
      }

      String data = T9ProjectLogic.profsysSelect(dbConn,request.getParameterMap(),project,statrTime,endTime,postpriv,postDept,deptId);
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
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysHistory(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      //    管理范围为全体，POST_PRIV=1
      //      管理范围为本部门,登录者自己部门的，POST_PRIV=0
      //      管理范围为指定部门，POST_PRIV=2 为指定部门ID
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String postpriv = user.getPostPriv();//管理范围
      String postDept = user.getPostDept();//管理范围指定部门
      String deptId = String.valueOf(user.getDeptId());//登录者的部门
      if(T9Utility.isNullorEmpty(postpriv)){
        postpriv = "";
      }
      if(T9Utility.isNullorEmpty(postDept)){
        postDept = "0";
      }
      if(T9Utility.isNullorEmpty(deptId)){
        deptId = "0";
      }

      String projNum = request.getParameter("projNum");
      String projActiveType = request.getParameter("projActiveType");
      String projStartTime = request.getParameter("projStartTime");
      String projStartTime1 = request.getParameter("projStartTime1");
      String budgetId = request.getParameter("budgetId");
      String projVisitType = request.getParameter("projVisitType");
      String projEndTime = request.getParameter("projEndTime");
      String projEndTime1 = request.getParameter("projEndTime1");

      //实体类封装
      T9Project project = new T9Project();
      project.setProjNum(projNum);
      project.setProjActiveType(projActiveType);
      if (!T9Utility.isNullorEmpty(budgetId)) {
        project.setBudgetId(Integer.parseInt(budgetId));
      }
      project.setProjVisitType(projVisitType);

      if (!T9Utility.isNullorEmpty(projStartTime)) {
        project.setProjStartTime(Date.valueOf(projStartTime));
      }
      if (!T9Utility.isNullorEmpty(projEndTime)) {
        project.setProjEndTime(Date.valueOf(projEndTime));
      }

      Date statrTime = null;
      Date endTime = null;
      if (!T9Utility.isNullorEmpty(projStartTime1)) {
        statrTime = Date.valueOf(projStartTime1);
      }
      if (!T9Utility.isNullorEmpty(projEndTime1)) {
        endTime = Date.valueOf(projEndTime1);
      }
      String data = T9ProjectLogic.profsysHistory(dbConn,request.getParameterMap(),project,statrTime,endTime,postpriv,postDept,deptId);
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
   * 删除项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delProj(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ProjectLogic.delProj(dbConn,Integer.parseInt(seqId));
        T9OutProjectCalendarLogic.delCalendar(dbConn,Integer.parseInt(seqId));
        T9OutProjectCommLogic.delComm(dbConn,Integer.parseInt(seqId));
        T9OutProjectFileLogic.delFile(dbConn,Integer.parseInt(seqId));
        T9OutProjectMemLogic.delMem(dbConn,Integer.parseInt(seqId));
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 结束项目状态
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String status = request.getParameter("status");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ProjectLogic.updateStatus(dbConn,Integer.parseInt(seqId),status);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "结束成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * 详细信息
   * */
  public String showDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9Project project = null;
      if (!T9Utility.isNullorEmpty(seqId)) {
        project = T9ProjectLogic.showDetail(dbConn,Integer.parseInt(seqId));
      }
      //定义数组将数据保存到Json中
      String data = "";
      if(project != null) {
        data = data + T9FOM.toJson(project);
        data = data.replaceAll("\\n", "");
        data = data.replaceAll("\\r", "");
      }
      data = data + "";
      if(data.equals("")){
        data = "{}";
      }
      //保存查询数据是否成功，保存date
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      //System.out.println(data);

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 导出
   * 
   * */
  public String exportXls(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    OutputStream ops = null;
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String fileName = URLEncoder.encode("出访信息.xls","UTF-8");
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
      //返回LIST集合
      String[] printStr = {"SEQ_ID in (" + seqId + ")"};
      List<T9Project> project = T9ProjectLogic.projectPrint(dbConn,printStr);
      ArrayList<T9DbRecord > dbL = T9ProjectLogic.getDbRecord(project, dbConn);
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
  public String printOut(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String printStr = request.getParameter("printStr");
      if (!T9Utility.isNullorEmpty(printStr)) {
        printStr = printStr.substring(0,printStr.length() - 1);
        T9ProjectLogic.printOut(dbConn,printStr);
        String[] str = {"SEQ_ID in (" + printStr + ")"};
        List<T9Project> project = T9ProjectLogic.projectPrint(dbConn,str);
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
    return "/subsys/oa/profsys/out/baseinfo/news/print.jsp";
  }

  /**
   * 单文件附件上传
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String uploadFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Map<String, String> attr = null;
    String attrId = (fileForm.getParameter("attachmentId")== null )? "":fileForm.getParameter("attachmentId");
    String attrName = (fileForm.getParameter("attachmentName")== null )? "":fileForm.getParameter("attachmentName");
    String data = "";
    try{
      T9ProjectLogic projectLogic = new T9ProjectLogic();
      attr = projectLogic.fileUploadLogic(fileForm);
      Set<String> keys = attr.keySet();
      for (String key : keys){
        String value = attr.get(key);
        if(attrId != null && !"".equals(attrId)){
          if(!(attrId.trim()).endsWith(",")){
            attrId += ",";
          }
          if(!(attrName.trim()).endsWith("*")){
            attrName += "*";
          }
        }
        attrId += key + ",";
        attrName += value + "*";
      }
      data = "{attrId:\"" + attrId + "\",attrName:\"" + attrName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data);

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtuploadfile.jsp";
  }

  /**
   *查询所有(分页)通用列表显示数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelect2(HttpServletRequest request,
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

      if (!T9Utility.isNullorEmpty(projStartTime)) {
        project.setProjStartTime(Date.valueOf(projStartTime));
      }
      if (!T9Utility.isNullorEmpty(projEndTime)) {
        project.setProjEndTime(Date.valueOf(projEndTime));
      }

      Date statrTime = null;
      Date endTime = null;
      if (!T9Utility.isNullorEmpty(projStartTime1)) {
        statrTime = Date.valueOf(projStartTime1);
      }
      if (!T9Utility.isNullorEmpty(projEndTime1)) {
        endTime = Date.valueOf(projEndTime1);
      }

      String data = T9ProjectLogic.profsysSelect2(dbConn,request.getParameterMap(),project,statrTime,endTime);
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
