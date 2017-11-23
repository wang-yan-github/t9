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
import t9.subsys.oa.profsys.data.T9ProjectComm;
import t9.subsys.oa.profsys.logic.out.T9OutProjectCommLogic;


public class T9OutProjectCommAct {
  /**
   * 新建会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addProjectComm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectComm comm = (T9ProjectComm)T9FOM.build(request.getParameterMap());

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

      comm.setAttachmentId(attachmentId);
      comm.setAttachmentName(attachmentName);

      T9OutProjectCommLogic.addProjectComm(dbConn,comm);
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
   * 查询会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String queryOutCommByProjId(HttpServletRequest request,
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
      String data = T9OutProjectCommLogic.toSearchData(dbConn,request.getParameterMap(),projId);
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
   * 删除会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteCommById(HttpServletRequest request,
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
      T9OutProjectCommLogic.deleteCommById(dbConn,seqId);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询修改会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCommById(HttpServletRequest request,
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
        T9ProjectComm comm = T9OutProjectCommLogic.getCommById(dbConn,seqId);
        if(comm != null){
          data = T9FOM.toJson(comm).toString();
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
   * 修改会议纪要
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateProjectComm(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ProjectComm comm = (T9ProjectComm)T9FOM.build(request.getParameterMap());

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

      comm.setAttachmentId(attachmentId);
      comm.setAttachmentName(attachmentName);

      T9OutProjectCommLogic.updateProjectComm(dbConn,comm);
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
   * 会议纪要查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String profsysSelectComm(HttpServletRequest request,
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
      String projCommType = request.getParameter("projCommType");

      comm.setCommNum(commNum);
      comm.setCommMemCn(commMemCn);
      comm.setCommMemFn(commMemFn);
      comm.setCommName(commName);
      if (!T9Utility.isNullorEmpty(commTime)) {
        comm.setCommTime(Date.valueOf(commTime));
      }
      comm.setCommPlace(commPlace);
      comm.setProjCommType(projCommType);
      //String projId = T9OutProjectCommLogic.profsysSelectComm(dbConn,comm);
      //通用查询数据
      String data = T9OutProjectCommLogic.profsysCommList(dbConn,request.getParameterMap(),projCommType,comm);
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
