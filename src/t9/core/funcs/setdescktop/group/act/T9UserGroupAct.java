package t9.core.funcs.setdescktop.group.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.fav.logic.T9FavLogic;
import t9.core.funcs.setdescktop.group.data.T9UserGroup;
import t9.core.funcs.setdescktop.group.logic.T9UserGroupLogic;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.funcs.system.url.logic.T9UrlLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.load.T9PageLoader;
import t9.core.menu.data.T9SysMenu;

public class T9UserGroupAct {
  private T9UserGroupLogic logic = new T9UserGroupLogic();
  
  /**
   * 新增userGroup
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map map = request.getParameterMap();
      T9UserGroup ug = (T9UserGroup)T9FOM.build(map, T9UserGroup.class, "");
      
      ug.setUserId(String.valueOf(user.getSeqId()));
      
      this.logic.add(dbConn, ug);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改userGroup
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String update(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map map = request.getParameterMap();
      T9UserGroup ug = (T9UserGroup)T9FOM.build(map, T9UserGroup.class, "");
      ug.setUserId(String.valueOf(user.getSeqId()));
      this.logic.update(dbConn, ug);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功查询属性");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除userGroup
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String delete(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();

      String seqId = request.getParameter("seqId");
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      if (this.logic.delete(dbConn, Integer.parseInt(seqId), user.getSeqId())){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
        request.setAttribute(T9ActionKeys.RET_MSRG,"删除成功");
      }
      else{
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG,"删除失败");
      }
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 设置userGroup
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String userId = request.getParameter("userId");
    String seqId = request.getParameter("seqId");
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      this.logic.setUser(dbConn, Integer.parseInt(seqId), userId);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "设置成功");
    }catch(NumberFormatException e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "id有误");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  
  /**
   * 设置userGroup
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String queryUser(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userStr = this.logic.queryUser(dbConn, Integer.parseInt(seqId));
      StringBuffer sb = new StringBuffer();
      if (userStr != null && !"".equals(userStr.trim())){
        sb.append("{\"userStr\":\"");
        sb.append(userStr);
        sb.append("\",\"userDesc\":\"");
        for (String s : userStr.split(",")){
          try{
            sb.append(this.logic.queryUserName(dbConn, Integer.parseInt(s)));
            sb.append(",");
          }catch(NumberFormatException e){
            
          }
        }
        if (sb.charAt(sb.length() - 1) == ','){
          sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("\"}");
      }
      else{
        sb.append("{\"userStr\":\"\",\"userDesc\":\"\"}");
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "设置成功");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(NumberFormatException e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "id有误");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  /** 
   * 取得管理用户组并分页
   * @param request 
   * @param response 
   * @return 
   * @throws Exception 
   */ 
   public String getPage(HttpServletRequest request, 
   HttpServletResponse response) throws Exception { 

     Connection dbConn = null; 
     try { 
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn(); 
       
       T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
       
       String sql = "select SEQ_ID" +
       		 ",ORDER_NO" +
       		 ",GROUP_NAME" +
       		 ",USER_STR" +
           " from USER_GROUP" +
           " where USER_ID = " +
           user.getSeqId() +
           " order by ORDER_NO";
       T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
       T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
       queryParam, sql); 
     
       PrintWriter pw = response.getWriter(); 
       pw.println(pageDataList.toJson()); 
       pw.flush(); 
   
       return null; 
     }catch (Exception e) {
       request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
       request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
       throw e; 
     } 
   }
}