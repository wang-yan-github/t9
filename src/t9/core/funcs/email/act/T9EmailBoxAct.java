package t9.core.funcs.email.act;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.data.T9EmailBox;
import t9.core.funcs.email.logic.T9EmailBoxLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;

public class T9EmailBoxAct{
  /**
   * 新建自定义邮箱
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveBox(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    //System.out.println(person);
    int userId = person.getSeqId();
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      embl.saveBox(dbConn, request.getParameterMap(), userId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱保存成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱保存失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改自定义邮箱
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateBox(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      embl.updateBox(dbConn, request.getParameterMap());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱修改成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮想修改失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改自定义邮箱
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBoxById(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      String boxIdStr = request.getParameter("boxId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9ORM orm = new T9ORM();
      T9EmailBox eb = (T9EmailBox) orm.loadObjSingle(dbConn, T9EmailBox.class, Integer.parseInt(boxIdStr));
      StringBuffer data = T9FOM.toJson(eb);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data.toString() );
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱修改成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮想修改失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 修改邮箱页数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setBoxPage(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      String seqIdstr = request.getParameter("seqId");
      String pageSizestr = request.getParameter("pageSize");
      String boxName = request.getParameter("boxName");
      int seqId = -1;
      if(seqIdstr != null && !"".equals(seqIdstr)){
        try {
          seqId = Integer.parseInt(seqIdstr);
        } catch (Exception e) {
          seqId = -1;
        }
      }else{
        seqId = -1;
      }
      int pageSize = Integer.parseInt(pageSizestr);
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      embl.setPageSize(dbConn, pageSize, seqId, boxName, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱修改成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮想修改失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除自定义邮箱
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteBox(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String idstr = request.getParameter("boxId");
    try{
      int id = Integer.valueOf(idstr.trim());
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      embl.deleteBox(dbConn, id);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱删除成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱删除失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 查询自定义邮箱
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String listBox(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    //System.out.println(person);
    int userId = person.getSeqId();
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      ArrayList<T9EmailBox> list = embl.listBoxByUser(dbConn, userId, false);
      request.setAttribute("boxList", list);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱列表读取成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱列表读取失败: " + e.getMessage());
    }
    return "/core/funcs/email/mailbox/boxManager.jsp";
  }
  /**
   * 取得自定义邮箱的容量
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBoxSize(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String boxIdStr = request.getParameter("boxId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      int boxId = Integer.valueOf(boxIdStr.trim());
      long result = embl.getBoxSizeById(dbConn, boxId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱容量读取成功！");
      request.setAttribute(T9ActionKeys.RET_DATA,"");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱容量读取失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setDefaultCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String boxIdStr = request.getParameter("boxId");
    String defCount = request.getParameter("defCount");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      int boxId = Integer.valueOf(boxIdStr.trim());
      embl.setBoxMailPage(dbConn, boxId, defCount);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置成功！");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSelf(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      StringBuffer data = embl.getBoxSelfLogic(dbConn, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSelfForLi(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      StringBuffer data = embl.getBoxSelfForList(dbConn, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getBoxName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    String boxId = request.getParameter("boxId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      StringBuffer data = embl.getBoxName(dbConn, person.getSeqId(),boxId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getDefBox(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      StringBuffer data = embl.getBoxDefLogic(dbConn, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString());
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String isBoxNameExist(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      String boxName = request.getParameter("boxName");
      String boxId = request.getParameter("boxId");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9EmailBoxLogic embl = new T9EmailBoxLogic();
      boolean isExit = embl.isNameExist(dbConn, person.getSeqId(), boxName,boxId);
      String data = "{isExist:" + isExit + "}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "邮箱默认每页显示邮件数设置失败: " + e.getMessage());
    }
    return "/core/inc/rtjson.jsp";
  }
}
