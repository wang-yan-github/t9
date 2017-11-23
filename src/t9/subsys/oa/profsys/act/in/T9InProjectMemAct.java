package t9.subsys.oa.profsys.act.in;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.logic.T9InnerEMailUtilLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.selattach.util.T9SelAttachUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.util.T9Utility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.profsys.data.T9ProjectMem;
import t9.subsys.oa.profsys.logic.T9ProjectMemLogic;
import t9.subsys.oa.profsys.logic.in.T9InProjectMemLogic;
import t9.subsys.oa.profsys.logic.out.T9OutProjectMemLogic;

public class T9InProjectMemAct {
  /**
   * 新建项目——人员
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateMem(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectMem mem = (T9ProjectMem)T9FOM.build(request.getParameterMap());
      T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
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
      int userId = user.getSeqId();
      mem.setProjDate(new Date());
      mem.setProjCreator(userId + "");
      int seqId = 0 ;
        T9ProjectMemLogic memLogic = new T9ProjectMemLogic();
        if(mem.getSeqId()>0){//更新
          seqId = mem.getSeqId();
          memLogic.updateMem(dbConn, mem);
        }else{//新增
          int seqIdInt = memLogic.addMem(dbConn, mem);
          seqId = seqIdInt;
      }
      String data = "{seqId:" + seqId + "}";
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
   * 新建项目——人员
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUpdateMemCopy(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      String projId = fileForm.getParameter("projId");//项目ID
      String seqId = fileForm.getParameter("seqId");
      if(T9Utility.isInteger(projId)){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMM");
        T9Person user = (T9Person) request.getSession().getAttribute(
            T9Const.LOGIN_USER);
        int userId = user.getSeqId();
        Calendar cl = Calendar.getInstance();
        int curYear = cl.get(Calendar.YEAR);
      
        // 保存从文件柜、网络硬盘选择附件
        T9SelAttachUtil sel = new T9SelAttachUtil(fileForm, "profsys");
        String attIdStr = sel.getAttachIdToString(",");
        String attNameStr = sel.getAttachNameToString("*");
        
        
        T9ProjectMem mem = new T9ProjectMem();
        mem.setProjId(Integer.parseInt(projId));
        mem.setProjCreator(userId + "");
        mem.setProjDate(new Date());
        mem.setProjMemType("0");
        mem.setMemNum(T9Utility.isNullorEmpty(fileForm.getParameter("memNum")) ? "" : fileForm.getParameter("memNum"));
        mem.setMemRole(T9Utility.isNullorEmpty(fileForm.getParameter("memRole")) ? "" : fileForm.getParameter("memRole"));
        mem.setMemName (T9Utility.isNullorEmpty(fileForm.getParameter("memName")) ? "" : fileForm.getParameter("memName"));
        mem.setMemSex(T9Utility.isNullorEmpty(fileForm.getParameter("memSex")) ? "" : fileForm.getParameter("memSex"));
        mem.setMemPosition(T9Utility.isNullorEmpty(fileForm.getParameter("memPositionId")) ? "" : fileForm.getParameter("memPositionId"));
        mem.setMemNation(T9Utility.isNullorEmpty(fileForm.getParameter("memNation")) ? "" : fileForm.getParameter("memNation"));
        mem.setMemNativePlace(T9Utility.isNullorEmpty(fileForm.getParameter("memNativePlace")) ? "" : fileForm.getParameter("memNativePlace"));
        mem.setMemBirthplace(T9Utility.isNullorEmpty(fileForm.getParameter("memBirthplace")) ? "" : fileForm.getParameter("memBirthplace"));
        mem.setMemIdNum(T9Utility.isNullorEmpty(fileForm.getParameter("memIdNum")) ? "" : fileForm.getParameter("memIdNum"));
        mem.setMemPhone(T9Utility.isNullorEmpty(fileForm.getParameter("memPhone")) ? "" : fileForm.getParameter("memPhone"));
        mem.setMemMail(T9Utility.isNullorEmpty(fileForm.getParameter("memMail")) ? "" : fileForm.getParameter("memMail"));
        mem.setMemFax(T9Utility.isNullorEmpty(fileForm.getParameter("memFax")) ? "" : fileForm.getParameter("memFax"));
        mem.setMemAddress(T9Utility.isNullorEmpty(fileForm.getParameter("memAddress")) ? "" : fileForm.getParameter("memAddress"));
        mem.setMemNote(T9Utility.isNullorEmpty(fileForm.getParameter("memNote")) ? "" : fileForm.getParameter("memNote"));      
        mem.setMemBirth(T9Utility.isNullorEmpty(fileForm.getParameter("memBirth")) ? null : dateFormat.parse(fileForm.getParameter("memBirth")));
      
        Iterator<String> iKeys = fileForm.iterateFileFields();
        String filePath = T9SysProps.getAttachPath()  + File.separator  +"profsys"  + File.separator  + dateFormat2.format(new Date()); // T9SysProps.getAttachPath()
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

   
        
        T9ProjectMemLogic memLogic = new T9ProjectMemLogic();
        if(T9Utility.isInteger(seqId)){//更新
          mem.setSeqId(Integer.parseInt(seqId));
          
          //先查出数据库的附件，然后加上
          String attIdStrTemp = "";
          String attNameStrTemp = "";
          T9ProjectMem memTemp  = memLogic.getMemById(dbConn, seqId)  ;
          if(memTemp!=null){
            attIdStrTemp  = memTemp.getAttachmentId();
            attNameStrTemp = memTemp.getAttachmentName();
          }
          if(!T9Utility.isNullorEmpty(attIdStrTemp)){
            attachmentId = attachmentId + "," + attIdStrTemp;
          }
          if(!T9Utility.isNullorEmpty(attachmentName)){
            attachmentName = attachmentName + "*" + attNameStrTemp;
          }
          mem.setAttachmentId(attachmentId);
          mem.setAttachmentName(attachmentName);
          memLogic.updateMem(dbConn, mem);
        }else{//新增
          mem.setAttachmentId(attachmentId);
          mem.setAttachmentName(attachmentName);
          int seqIdInt = memLogic.addMem(dbConn, mem);
          seqId = seqIdInt + "";

        }
      }
     
      String path = request.getContextPath();
      response.sendRedirect(path+ "/subsys/oa/profsys/in/baseinfo/news/user.jsp?seqId=" + seqId +"&projId=" + projId);
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
   * 来访项目人员By ProjId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
    public String queryInMemByProjId(HttpServletRequest request,
        HttpServletResponse response) throws Exception {
      Connection dbConn = null;
      try {
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request
            .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
        dbConn = requestDbConn.getSysDbConn();
        T9Person user = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
        int userId = user.getSeqId(); 
        String projId = request.getParameter("projId");
        if(T9Utility.isNullorEmpty(projId)){
          projId = "0";
        }
        T9InProjectMemLogic tbal = new T9InProjectMemLogic();
        String data = tbal.toSearchData(dbConn, request.getParameterMap(),projId,"0");
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
   * 来访项目人员By ProjId
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getMemById(HttpServletRequest request,
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
        T9ProjectMem mem = memLogic.getMemById(dbConn, seqId);
        if(mem != null){
          String privName = T9OutProjectMemLogic.userName(dbConn,mem.getMemPosition()); 
          data = T9FOM.toJson(mem).toString().substring(0, T9FOM.toJson(mem).toString().length()-1) + ",memPositionName:\"" + T9Utility.encodeSpecial(privName) + "\"}";
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
}
