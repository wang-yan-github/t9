package t9.subsys.oa.profsys.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.subsys.oa.profsys.data.T9ProjectMem;
import t9.subsys.oa.profsys.logic.T9ProjectLogic;
import t9.subsys.oa.profsys.logic.T9ProjectMemLogic;

public class T9ProjectMemAct {

  /**
   * 删除来访项目人员ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String  deleteMemById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      int userId = user.getSeqId();
      String seqId = request.getParameter("seqId");
      String data = "";
      if (T9Utility.isInteger(seqId)) {
        T9ProjectMemLogic memLogic = new T9ProjectMemLogic();
        memLogic.deleteMemById(dbConn, seqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "{}");
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
      T9ProjectMemLogic memLogic = new T9ProjectMemLogic();
      T9ProjectMem mem  = null ;
      String updateFlag = "0";
      if(seqId!=null&&!seqId.equals("")){
        mem = memLogic.getMemById(dbConn, seqId) ;
       if(mem!=null){
         String attachmentId = mem.getAttachmentId();
         String attachmentName = mem.getAttachmentName();
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
         if(!newAttachmentId.equals("")){
           newAttachmentId = newAttachmentId.substring(0, newAttachmentId.length()-1);
         }
         for (int i = 0; i < attachmentNameArray.length; i++) {
           if(!attachmentNameArray[i].equals(attachName)){
             newAttachmentName = newAttachmentName +attachmentNameArray[i] + "*";
           }
         }
         if(!newAttachmentName.equals("")){
           newAttachmentName = newAttachmentName.substring(0, newAttachmentName.length()-1);
         }
         T9ProjectLogic pl = new T9ProjectLogic();  
         pl.updateFile(dbConn,"PROJECT_MEM" ,newAttachmentId, newAttachmentName, seqId);
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
