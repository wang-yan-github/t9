package t9.subsys.oa.profsys.act.out;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectMem;
import t9.subsys.oa.profsys.logic.out.T9OutProjectMemLogic;

public class T9OutProjectMemAct {
  /**
   * 新建项目——人员
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addProjectMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectMem mem = (T9ProjectMem)T9FOM.build(request.getParameterMap());
      
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

      mem.setAttachmentId(attachmentId);
      mem.setAttachmentName(attachmentName);
      
      T9OutProjectMemLogic.addProjectMem(dbConn, mem);
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
   * 来访项目人员By ProjId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryOutMemByProjId(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String projId = request.getParameter("projId");
      if(T9Utility.isNullorEmpty(projId)){
        projId = "0";
      }
      String data = T9OutProjectMemLogic.toSearchData(dbConn,request.getParameterMap(),projId);
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
   * 修改项目
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateProjectMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectMem mem = (T9ProjectMem)T9FOM.build(request.getParameterMap());
      
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

      mem.setAttachmentId(attachmentId);
      mem.setAttachmentName(attachmentName);
      
      T9OutProjectMemLogic.updateProjectMem(dbConn, mem);
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
   *查询所有ID串名字
   */
  public String userName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");

      String data = T9OutProjectMemLogic.userName(dbConn,seqId);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 来访项目人员查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectMem mem = new T9ProjectMem();
      String memNum = request.getParameter("memNum");
      String memPosition = request.getParameter("memPosition");
      String memName = request.getParameter("memName");
      String memSex = request.getParameter("memSex");
      String memBirth = request.getParameter("memBirth");
      String memIdNum = request.getParameter("memIdNum");
      String projMemType = request.getParameter("projMemType");
      if (!T9Utility.isNullorEmpty(memBirth)) {
        mem.setMemBirth(Date.valueOf(memBirth));
      }
      mem.setMemNum(memNum);
      mem.setMemPosition(memPosition);
      mem.setMemName(memName);
      mem.setMemSex(memSex);
      mem.setMemIdNum(memIdNum);
      mem.setProjMemType(projMemType);
      //String projId = T9OutProjectMemLogic.profsysSelectMem(dbConn,mem);
      //通用查询数据
      String data = T9OutProjectMemLogic.profsysMemList(dbConn,request.getParameterMap(),projMemType,mem);
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
