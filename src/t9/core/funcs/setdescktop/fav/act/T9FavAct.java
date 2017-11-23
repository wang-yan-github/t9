package t9.core.funcs.setdescktop.fav.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.setdescktop.fav.logic.T9FavLogic;
import t9.core.funcs.setdescktop.userinfo.act.T9UserinfoAct;
import t9.core.funcs.system.url.data.T9Url;
import t9.core.funcs.system.url.logic.T9UrlLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.global.T9SysProps;
import t9.core.load.T9PageLoader;
import t9.core.menu.data.T9SysMenu;

public class T9FavAct {
  private T9FavLogic logic = new T9FavLogic();
  
  /**
   * 新增url
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addUrl(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map map = request.getParameterMap();
      T9Url url = (T9Url)T9FOM.build(map);
      
      url.setUrl(T9Utility.encodeSpecial(url.getUrl()));
      url.setUrlDesc(T9Utility.encodeSpecial(url.getUrlDesc()));
      
      url.setUser(String.valueOf(user.getSeqId()));
      this.logic.addUrl(dbConn,url);
      
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
   * 修改url
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String modifyUrl(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      Map map = request.getParameterMap();
      T9Url url = (T9Url)T9FOM.build(map);
      url.setUser(String.valueOf(user.getSeqId()));
      this.logic.modifyUrl(dbConn,url);
      
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
   * 修改url
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String list(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      List<Map<String,String>> list = this.logic.list(dbConn, user.getSeqId());
      
      StringBuffer sb = new StringBuffer("[");
      
      for(Map<String,String> m : list){
        sb.append(T9FOM.toJson(m));
        sb.append(",");
      }
        
      if(list.size()>0){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
      
      PrintWriter pw = response.getWriter();
      pw.println(sb.toString().trim());
      pw.flush();
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return null;
  }
  
  /**
   * 删除记录url
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteUrl(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      
      this.logic.deleteUrl(dbConn, Integer.parseInt(seqId));
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功删除");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  public String getPage(HttpServletRequest request, 
      HttpServletResponse response) throws Exception { 

    Connection dbConn = null; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn(); 
      
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      
      String sql = "";
      String dbms = T9SysProps.getProp("db.jdbc.dbms");
      if (dbms.equals("sqlserver")) {
        sql = "select SEQ_ID" +
        ",URL_NO" +
        ",URL_DESC" +
        ",URL" +
        ",charindex(URL,'1:') as OPEN_TYPE" +
        " from URL" +
        " where [USER] = '" + 
        user.getSeqId() + "'" +
        " order by URL_NO";
      }
      else if (dbms.equals("mysql")){
        sql = "select SEQ_ID" +
        ",URL_NO" +
        ",URL_DESC" +
        ",URL" +
        ",instr(URL,'1:') as OPEN_TYPE" +
        " from URL" +
        " where USER = '" + 
        user.getSeqId() + "'" +
        " order by URL_NO";
      }
      else if (dbms.equals("oracle")){
        sql = "select SEQ_ID" +
        ",URL_NO" +
        ",URL_DESC" +
        ",URL" +
        ",instr(URL,'1:') as OPEN_TYPE" +
        " from URL" +
        " where \"USER\" = '" + 
        user.getSeqId() + "'" +
        " order by URL_NO";
      }
      //System.out.println(sql);
      T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request.getParameterMap()); 
      T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, 
      queryParam, 
      sql);
      
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