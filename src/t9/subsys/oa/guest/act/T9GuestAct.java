package t9.subsys.oa.guest.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.codeclass.data.T9CodeItem;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.dept.act.T9DeptAct;
import t9.core.funcs.dept.logic.T9DeptLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.guest.data.T9Guest;
import t9.subsys.oa.guest.logic.T9GuestLogic;
import t9.subsys.oa.profsys.logic.in.T9InProjectLogic;
import t9.subsys.oa.profsys.source.org.data.T9SourceOrg;
import t9.subsys.oa.profsys.source.org.logic.T9SourceOrgLogic;

public class T9GuestAct {
  /**
   * 新建更新
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addUpdateGuest(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      T9Guest guest = (T9Guest)T9FOM.build(request.getParameterMap());
      T9GuestLogic guestLogic = new T9GuestLogic();

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
      guest.setAttachmentId(attachmentId);
      guest.setAttachmentName(attachmentName);
      int seqId = 0;
      if(guest !=null){

        if(guest.getSeqId()>0){
          seqId = guest.getSeqId();
          guestLogic.updateGuest(dbConn, guest);
        }else{
          seqId = guestLogic.addGuest(dbConn, guest);
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
   * 克隆ById

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String clonGuest(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String guestNum = request.getParameter("guestNum");
      String guestName = request.getParameter("guestName");
      T9GuestLogic guestLogic = new T9GuestLogic();
      if(T9Utility.isInteger(seqId)){
        T9Guest guest = T9GuestLogic.selectGuestById(dbConn, seqId);
        if(guest != null){
          guest.setGuestName(guestName);
          guest.setGuestNum(guestNum);
          guestLogic.addGuest(dbConn, guest);
        }
        
      } 

      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询所有
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String queryGuest(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
    //通用查询数据
      String data = T9GuestLogic.queryGuest(dbConn,request.getParameterMap());
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
   * 按条件查询
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String queryGuestTerm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String guestNum = request.getParameter("guestNum") == null ? "" : request.getParameter("guestNum");
      String guestType = request.getParameter("guestType") == null ? "" : request.getParameter("guestType");
      String guestName = request.getParameter("guestName") == null ? "" : request.getParameter("guestName");
      String guestDiner = request.getParameter("guestDiner") == null ? "" : request.getParameter("guestDiner");
      String guestUnit = request.getParameter("guestUnit") == null ? "" : request.getParameter("guestUnit");
      String guestPhone = request.getParameter("guestPhone") == null ? "" : request.getParameter("guestPhone");
      String guestAttendTime = request.getParameter("guestAttendTime") == null ? "" : request.getParameter("guestAttendTime");
      String guestAttendTime1 = request.getParameter("guestAttendTime1") == null ? "" : request.getParameter("guestAttendTime1");
      String guestLeaveTime = request.getParameter("guestLeaveTime") == null ? "" : request.getParameter("guestLeaveTime");
      String guestLeaveTime1 = request.getParameter("guestLeaveTime1") == null ? "" : request.getParameter("guestLeaveTime1");
      String guestCreator = request.getParameter("guestCreator") == null ? "" : request.getParameter("guestCreator");
      String guestDept = request.getParameter("guestDept") == null ? "" : request.getParameter("guestDept");
      String guestNote = request.getParameter("guestNote") == null ? "" : request.getParameter("guestNote");
    //通用查询数据
      String data = T9GuestLogic.queryGuestTrem(dbConn,request.getParameterMap(),guestNum,guestType,guestName,guestDiner,guestUnit
          ,guestPhone,guestAttendTime,guestAttendTime1,guestLeaveTime,guestLeaveTime1,guestCreator,guestDept,guestNote);
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
   * 根据ID字符串取得部门名称

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDeptNameBySeqIds(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String deptId = request.getParameter("deptId");
      String data = "";
      if(!T9Utility.isNullorEmpty(deptId)){
        T9DeptLogic d = new  T9DeptLogic();
        String deptName = T9Utility.encodeSpecial(d.getNameByIdStr(deptId, dbConn));
        data = "{deptName:\"" + deptName +"\"}";
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delGuest(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if(T9Utility.isInteger(seqId)){
        T9GuestLogic.delGuest(dbConn, seqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");

    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询ById

   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getGuestById(HttpServletRequest request,HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String data = "";
      if(T9Utility.isInteger(seqId)){
        T9Guest guest = T9GuestLogic.selectGuestById(dbConn, seqId);
        if(guest != null){
          String deptName = "";
          if(!T9Utility.isNullorEmpty(guest.getGuestDept())){
            T9DeptLogic d = new  T9DeptLogic();
            deptName = T9Utility.encodeSpecial(d.getNameByIdStr(guest.getGuestDept(), dbConn));
          }
          String guestTypeDesc = "";
          if(!T9Utility.isNullorEmpty(guest.getGuestType())){
            T9CodeItem  codeItem = T9InProjectLogic.getCodeItem(dbConn, guest.getGuestType());
            if(codeItem != null){
              guestTypeDesc = T9Utility.encodeSpecial(codeItem.getClassDesc());
            }
          }
          String guestCreatorName = "";
          if(!T9Utility.isNullorEmpty(guest.getGuestCreator())){
            T9PersonLogic personLogic = new T9PersonLogic();
            guestCreatorName = T9Utility.encodeSpecial(personLogic.getNameBySeqIdStr(guest.getGuestCreator(), dbConn));
          }
          data = data + T9FOM.toJson(guest).toString().substring(0, T9FOM.toJson(guest).toString().length()-1) + ",deptName:\"" +deptName + "\",guestCreatorName:\"" + guestCreatorName + "\",guestTypeDesc:\"" + guestTypeDesc + "\"}";
        }
      } 
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传成功");
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "文件上传失败");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
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
      T9GuestLogic projectLogic = new T9GuestLogic();
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
      T9GuestLogic orgLogic = new T9GuestLogic();
      T9Guest guest  = null ;
      String updateFlag = "0";
      if(seqId!=null&&!seqId.equals("")){
        guest = orgLogic.selectGuestById(dbConn, seqId)  ;
       if(guest!=null){
         String attachmentId = guest.getAttachmentId();
         String attachmentName = guest.getAttachmentName();
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
         
         orgLogic.updateFile(dbConn,"GUEST", newAttachmentId, newAttachmentName, seqId);
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
