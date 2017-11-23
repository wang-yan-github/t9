package t9.subsys.oa.profsys.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectFile;
import t9.subsys.oa.profsys.logic.T9ProjectFileLogic;
import t9.subsys.oa.profsys.logic.T9ProjectLogic;
import t9.subsys.oa.profsys.logic.out.T9OutProjectFileLogic;

public class T9ProjectFileAct {
  /**
   * 新建更新 会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateProjectFile(HttpServletRequest request,
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
      if(file!=null){
        if(file.getSeqId()>0){
          T9OutProjectFileLogic.updateProjectFile(dbConn,file);
        }else{
          T9OutProjectFileLogic.addProjectFile(dbConn,file);
        }
      }
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
      String projFileType = request.getParameter("projFileType");
      if(T9Utility.isNullorEmpty(projFileType)){
        projFileType = "0";
      }

      if(T9Utility.isNullorEmpty(projId)){
        projId = "0";
      }
      String data = T9ProjectFileLogic.toSearchData(dbConn,request.getParameterMap(),projId,projFileType);
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
          T9PersonLogic personLogic = new T9PersonLogic();
          String projCreatorName = "";
          if(!T9Utility.isNullorEmpty(file.getProjCreator())){
            projCreatorName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(file.getProjCreator(), dbConn));
          }
          data = T9FOM.toJson(file).toString().substring(0, T9FOM.toJson(file).toString().length()-1) + ",projCreatorName:\"" + projCreatorName + "\"}";
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
      T9OutProjectFileLogic fileLogic = new T9OutProjectFileLogic();
      T9ProjectFile file  = null ;
      String updateFlag = "0";
      if(seqId!=null&&!seqId.equals("")){
        file = fileLogic.getFileById(dbConn, seqId) ;
       if(file!=null){
         String attachmentId = file.getAttachmentId();
         String attachmentName = file.getAttachmentName();
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
         if(!newAttachmentId.equals("")&&newAttachmentId.endsWith(",")){
           newAttachmentId = newAttachmentId.substring(0, newAttachmentId.length()-1);
         }
         for (int i = 0; i < attachmentNameArray.length; i++) {
           if(!attachmentNameArray[i].equals(attachName)){
             newAttachmentName = newAttachmentName +attachmentNameArray[i] + "*";
           }
         }
         if(!newAttachmentName.equals("")&& newAttachmentName.endsWith("*")){
           newAttachmentName = newAttachmentName.substring(0, newAttachmentName.length()-1);
         }
         T9ProjectLogic pl = new T9ProjectLogic();  
         pl.updateFile(dbConn,"PROJECT_FILE" ,newAttachmentId, newAttachmentName, seqId);
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
}
