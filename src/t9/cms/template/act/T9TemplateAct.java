package t9.cms.template.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.cms.template.data.T9CmsTemplate;
import t9.cms.template.logic.T9TemplateLogic;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.codeclass.logic.T9CodeClassLogic;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.file.T9FileUploadForm;
import t9.core.util.form.T9FOM;

public class T9TemplateAct {
  
  /**
   * 得到模板的所有类型

   * 根据seqId（codeClass） 得到所有的codeItem
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeItem(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CodeClassLogic codeLogic = new T9CodeClassLogic();
      String data = "[";
      List<T9CodeItem> itemList = new ArrayList<T9CodeItem>();
      itemList = codeLogic.getCodeItem(dbConn, "TEMPLATE_TYPE");
      for (int i = 0; i < itemList.size(); i++) {
        T9CodeItem item = itemList.get(i);
        data = data + T9FOM.toJson(item) + ",";
      }
      if (itemList.size() > 0) {
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";
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
  * CMS模板 添加
  * 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String addTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Connection dbConn = null;
    String contexPath = request.getContextPath();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsTemplate template  = (T9CmsTemplate) T9FOM.build(fileForm.getParamMap(), T9CmsTemplate.class, null); 
      T9TemplateLogic logic = new T9TemplateLogic();
      logic.addTemplate(dbConn, template, person, fileForm);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    response.sendRedirect(contexPath + "/cms/template/manage.jsp");
    return null;
  }
  
  /**
   * CMS模板 通用列表
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTemplateList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      String stationId = request.getParameter("stationId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9TemplateLogic logic = new T9TemplateLogic();
      String data = logic.getTemplateList(dbConn, request.getParameterMap(), person, stationId);
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
  public String getTemplateDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    String stationId=request.getParameter("stationId");
    if (T9Utility.isNullorEmpty(seqId)) {
      seqId = "0";
    }
    String flag = request.getParameter("flag");
    if (T9Utility.isNullorEmpty(flag)) {
      flag = "0";
    }
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9TemplateLogic logic = new T9TemplateLogic();
      StringBuffer data = logic.getTemplateDetailLogic(dbConn, Integer.parseInt(seqId), Integer.parseInt(flag),stationId);
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
   * CMS模板 修改
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    T9FileUploadForm fileForm = new T9FileUploadForm();
    fileForm.parseUploadRequest(request);
    Connection dbConn = null;
    String contexPath = request.getContextPath();
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9CmsTemplate template  = (T9CmsTemplate) T9FOM.build(fileForm.getParamMap(), T9CmsTemplate.class, null); 
      T9TemplateLogic logic = new T9TemplateLogic();
      logic.updateTemplate(dbConn, template, fileForm);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功修改数据");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    response.sendRedirect(contexPath + "/cms/template/manage.jsp");
    return null;
  }
  
  /**
   * 删除模板
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteTempalte(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqIdStr = request.getParameter("seqId");
    String stationId=request.getParameter("stationId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9TemplateLogic logic = new T9TemplateLogic();
      logic.deleteTemplateLogic(dbConn, seqIdStr,stationId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 模板查询
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<Object, Object> map = new HashMap<Object, Object>();
      map.put("templateName", T9DBUtility.escapeLike(request.getParameter("templateName")));
      map.put("templateFileName", T9DBUtility.escapeLike(request.getParameter("templateFileName")));
      map.put("templateType", T9DBUtility.escapeLike(request.getParameter("templateType")));
      String data = "";
      T9TemplateLogic logic = new T9TemplateLogic();
      data = logic.queryTemplateLogic(dbConn, request.getParameterMap(), map, person);
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
   * 获取所有站点
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectStationName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9TemplateLogic logic = new T9TemplateLogic();
      String data = logic.getStationName(dbConn);
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
}
