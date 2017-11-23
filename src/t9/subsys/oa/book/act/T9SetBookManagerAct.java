package t9.subsys.oa.book.act;

import java.sql.Connection;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.oa.book.data.T9BookManager;
import t9.subsys.oa.book.logic.T9SetBookManagerLogic;

/**
 * 设置管理员
 * @author qwx110
 *
 */
public class T9SetBookManagerAct{
   
  /**
   * 跳转到增加管理员index页面
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String index(HttpServletRequest request,  HttpServletResponse response) throws Exception{   
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();    
      T9SetBookManagerLogic  mLogic = new T9SetBookManagerLogic();
      List<T9BookManager> managers = mLogic.findAllManager(dbConn);
      request.setAttribute("managers", managers);
    }catch(Exception e){      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;      
    }
    return "/subsys/oa/book/setmanager/index.jsp";
  }
  
  /**
   * 增加新的管理员
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String addManager(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9SetBookManagerLogic  mLogic = new T9SetBookManagerLogic();
      T9BookManager aManager = new T9BookManager();
      String managerIds = request.getParameter("manage");
      String deptIds = request.getParameter("dept");
      aManager.setManagerId(managerIds);
      aManager.setManageDeptId(deptIds);
      int k =  mLogic.newManager(dbConn,aManager);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;    
    } 
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9SetBookManagerAct/index.act";
  }
  
  /**
   * 编辑管理员
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String editManager(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      T9SetBookManagerLogic  mLogic = new T9SetBookManagerLogic();
      T9BookManager aManager = mLogic.editManager(dbConn, Integer.parseInt(seqId));
      request.setAttribute("manager", aManager);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;    
    } 
   
    
    return "/subsys/oa/book/setmanager/addManager.jsp";
  }
  
  /**
   * 更新管理员
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String updateManager(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9SetBookManagerLogic  mLogic = new T9SetBookManagerLogic();
      T9BookManager aManager = new T9BookManager();
      String managerIds = request.getParameter("manage");
      String deptIds = request.getParameter("dept");
      String seqId = request.getParameter("seqId");
      aManager.setManagerId(managerIds);
      aManager.setManageDeptId(deptIds);
      aManager.setSeqId(Integer.parseInt(seqId));
      int k =  mLogic.updateManager(dbConn,aManager);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;    
    } 
    request.setAttribute(T9ActionKeys.RET_METHOD, T9ActionKeys.RET_METHOD_REDIRECT);
    return "/t9/subsys/oa/book/act/T9SetBookManagerAct/index.act";    
  }
  
  /**
   * 删除管理员
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String delManager(HttpServletRequest request,  HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9SetBookManagerLogic  mLogic = new T9SetBookManagerLogic();
      String seqId = request.getParameter("seqId");
      int k = mLogic.delManager(dbConn, Integer.parseInt(seqId));
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    } 
    return "/t9/subsys/oa/book/act/T9SetBookManagerAct/index.act";  
  }
}
