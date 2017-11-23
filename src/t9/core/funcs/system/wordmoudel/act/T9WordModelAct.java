package t9.core.funcs.system.wordmoudel.act;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.wordmoudel.data.T9WordModel;
import t9.core.funcs.system.wordmoudel.data.T9WordModelCont;
import t9.core.funcs.system.wordmoudel.logic.T9WordModelLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.module.org_select.logic.T9OrgSelectLogic;
import t9.core.util.db.T9ORM;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9WordModelAct {
  
  private static Logger log = Logger.getLogger("t9.core.funcs.system.wordmoudel.act.T9WordModelAct");
/**
 * 保存模板文件
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
  public String saveWordModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9WordModelLogic wml = new T9WordModelLogic();
      wml.saveLogic(conn, fileForm,person.getSeqId(),T9WordModelCont.ATTA_PATH);
     
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/wordmodel/success.jsp";
  }
  /**
   * 更新套红模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateWordModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    String seqId = "";
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
        .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9FileUploadForm fileForm = new T9FileUploadForm();
      fileForm.parseUploadRequest(request);
      T9WordModelLogic wml = new T9WordModelLogic();
      wml.updateLogic(conn, fileForm,person.getSeqId(),T9WordModelCont.ATTA_PATH);
      seqId = fileForm.getParameter("seqId");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, "");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/funcs/system/wordmodel/success.jsp?seqId=" + seqId;
  }
  
  /**
   * 分页列出所有的套红模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listAllModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9WordModelLogic wml = new T9WordModelLogic();
      String data =  wml.listWordModelSearch(conn, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 查询套红模板--暂未启用
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listModelSearch(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9WordModelLogic wml = new T9WordModelLogic();
      String data =  wml.listWordModelSearch(conn, request.getParameterMap());
      PrintWriter pw = response.getWriter();
      pw.println(data);
      //System.out.println(data);
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  /**
   * 删除套红模块
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String idStr = request.getParameter("seqId");
      int id = Integer.valueOf(idStr);
      T9WordModelLogic wml = new T9WordModelLogic();
      wml.doDelete(conn, id,T9WordModelCont.ATTA_PATH);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功!");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 显示单个套红模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showModel(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      String idStr = request.getParameter("seqId");
      int id = Integer.valueOf(idStr);
      T9ORM orm = new T9ORM();
      T9WordModel wm = (T9WordModel) orm.loadObjSingle(conn, T9WordModel.class, id);
      StringBuffer data = T9FOM.toJson(wm);
      //System.out.println(data.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 显示单个套红模板
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAllDept(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection conn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      conn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      String data = T9OrgSelectLogic.getAlldept(conn);
      //System.out.println(data.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
