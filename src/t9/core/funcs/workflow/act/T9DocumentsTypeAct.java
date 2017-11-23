package t9.core.funcs.workflow.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.data.T9DocWord;
import t9.core.funcs.workflow.data.T9DocumentsType;
import t9.core.funcs.workflow.logic.T9DocumentsTypeLogic;
import t9.core.funcs.workflow.logic.T9FlowTypeLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
public class T9DocumentsTypeAct {

  private T9DocumentsTypeLogic logic = new T9DocumentsTypeLogic();
  /**
   * 新建文件类型
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addDocumentsTypeInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String contexPath = request.getContextPath();
    T9FOM fom = new T9FOM();
    Map map = fom.buildMap(request.getParameterMap());
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.addDocumentsTypeInfo(dbConn, map, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    response.sendRedirect(contexPath + "/core/funcs/workflow/flowrun/documentsType/newRemind.jsp");
    return null;
  }
  
  /**
   *文件类型 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDocumentsTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getDocumentsTypeList(dbConn, request.getParameterMap(), person);
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
   * 获取详情
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDocumentsTypeDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9DocumentsType documentsType = (T9DocumentsType) this.logic.getDocumentsTypeDetail(dbConn, Integer.parseInt(seqId));
      if (documentsType == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未找到相应记录");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(documentsType);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 编辑文件类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateDocumentsTypeInfo(HttpServletRequest request, HttpServletResponse response) throws Exception{
    String contexPath = request.getContextPath();
    T9FOM fom = new T9FOM();
    Map map = fom.buildMap(request.getParameterMap());
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      this.logic.updateDocumentsTypeInfo(dbConn, map, person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    response.sendRedirect(contexPath + "/core/funcs/workflow/flowrun/documentsType/manage.jsp");
    return null;
  }
  
  /**
   * 删除文件--wyw
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      this.logic.deleteFileLogic(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 员工关怀查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryDocumentsTypeList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9FOM fom = new T9FOM();
      Map map = fom.buildMap(request.getParameterMap());
      String data = "";
      data = this.logic.queryDocumentsTypeList(dbConn, request.getParameterMap(), map, person);
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
   * 得到ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getFlowTypeName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      T9FlowTypeLogic logic = new T9FlowTypeLogic();
      String docWordNameStr =  logic.getFlowTypeName(Integer.parseInt(seqIdStr), dbConn);
      request.setAttribute(T9ActionKeys.RET_DATA,"\""+docWordNameStr+"\"");
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
   * 得到ById
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String getDocWordById(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      String docWordNameStr = this.logic.getDocumentsTypeById(dbConn, seqIdStr);
      request.setAttribute(T9ActionKeys.RET_DATA,"\""+docWordNameStr+"\"");
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
   * 文件字-模糊查找
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDocumentsTypeListSelect(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    String condition = request.getParameter("condition");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      StringBuffer sb = new StringBuffer("[");
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      List<T9DocWord> docWords = this.logic.getDocumentsTypeListSelect(dbConn,person,condition);
      boolean isHave = false;
      if (docWords!=null && docWords.size()>0) {
        for(T9DocWord docWord:docWords){
          sb.append("{");
          sb.append("dwId:\"" +  docWord.getSeqId() + "\"");
          sb.append(",dwName:\"" + docWord.getDwName() + "\"");
          sb.append("},");
          isHave = true;
        }
        if (isHave) {
          sb.deleteCharAt(sb.length()-1);
        }
        sb.append("]");
        
      }else {
        sb.append("]");
      }
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
