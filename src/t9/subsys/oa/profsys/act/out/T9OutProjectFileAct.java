package t9.subsys.oa.profsys.act.out;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectFile;
import t9.subsys.oa.profsys.logic.out.T9OutProjectFileLogic;

public class T9OutProjectFileAct {
  /**
   * 新建会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addProjectFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectFile file = (T9ProjectFile)T9FOM.build(request.getParameterMap());
      
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

      file.setAttachmentId(attachmentId);
      file.setAttachmentName(attachmentName);
      
      T9OutProjectFileLogic.addProjectFile(dbConn,file);
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
   * 查询相关文档
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryOutFileByProjId(HttpServletRequest request,
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
      String data = T9OutProjectFileLogic.toSearchData(dbConn,request.getParameterMap(),projId);
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
   * 删除相关文档
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFileById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9OutProjectFileLogic.deleteFileById(dbConn, seqId);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询相关文档
   * @param request
   * @param response
   * @return
   * @throws Exception updateProjectFile
   */
  public String getFileById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      String data = "";
      if (T9Utility.isInteger(seqId)) {
        T9ProjectFile file = T9OutProjectFileLogic.getFileById(dbConn, seqId);
        if(file != null){
          data = T9FOM.toJson(file).toString();
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 修改相关文档
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String updateProjectFile(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectFile file = (T9ProjectFile)T9FOM.build(request.getParameterMap());
      
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

      file.setAttachmentId(attachmentId);
      file.setAttachmentName(attachmentName);
      
      T9OutProjectFileLogic.updateProjectFile(dbConn,file);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 文档查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectFile(HttpServletRequest request,
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
      file.setFileNum(fileNum);
      file.setFileName(fileName);
      file.setFileType(fileType);
      file.setProjCreator(projCreator);
      file.setFileTitle(fileTitle);
      file.setProjFileType(projFileType);

      //String projId = T9OutProjectFileLogic.profsysSelectFile(dbConn,file);
      //通用查询数据
      String data = T9OutProjectFileLogic.profsysFileList(dbConn,request.getParameterMap(),projFileType, file);
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
