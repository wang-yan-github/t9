package t9.core.oaknow.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.logic.T9CategoriesLogic;
import t9.core.oaknow.logic.T9OAKnowLogic;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.oaknow.util.T9OAToJsonUtil;
import t9.core.util.T9Out;

public class T9CategoriesAct{
  private  T9OAKnowLogic oaLogicIndex = new T9OAKnowLogic();
  private T9CategoriesLogic typeLogic = new T9CategoriesLogic();
  /**
   * 挑传到新建分类页面
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String goToCategoty(HttpServletRequest request,HttpServletResponse response) throws Exception{  
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
    T9CategoriesType type = new T9CategoriesType();;
    try{
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      int flag = 0;
      if(T9StringUtil.isNotEmpty(seqId)){
        type = typeLogic.findATypeById(dbConn, Integer.parseInt(seqId));       
        flag = 1;
     }   
      request.setAttribute("flag", flag);
      request.setAttribute("type", type);
      if(T9StringUtil.isNotEmpty(type.getName())){
        request.setAttribute("quot", T9StringUtil.toChange(type.getName()));
      }
      List<T9CategoriesType>  types = oaLogicIndex.findKind(dbConn);
      request.setAttribute("toJson", T9OAToJsonUtil.toJsonTwo(types));    
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
  
    return "/core/oaknow/panel/categories.jsp";
  }
  /**
   * 保存新的类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findCategory(HttpServletRequest request,HttpServletResponse response) throws Exception{  
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9CategoriesType type = new T9CategoriesType();
      String oderId = request.getParameter("oderId");
      String typeName = request.getParameter("typeName");
      String pearentid = request.getParameter("pearentid");
      String managernames = request.getParameter("manage");
      String seqId = request.getParameter("seqId");
      if(T9StringUtil.isNotEmpty(seqId)){
        type.setSeqId(Integer.parseInt(seqId));
      }else{
        type.setSeqId(0);
      }
      
      type.setOrderId(Integer.parseInt(oderId));
      type.setName(typeName);
      type.setPearentId((Integer.parseInt(pearentid)-Integer.parseInt(seqId)==0)?0:Integer.parseInt(pearentid));
      type.setManagers(managernames);
     // int id = typeLogic.saveCategoty(dbConn, type);
      int id = typeLogic.saveOrUpdateCategoty(dbConn, type);
      T9AjaxUtil.ajax(id, response);
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;
  }
  
  /**
   * 删除一个类型
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteType(HttpServletRequest request,HttpServletResponse response) throws Exception{  
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
    dbConn = requestDbConn.getSysDbConn();
    try{
      String seqId = request.getParameter("seqId");
      int id = typeLogic.deleteType(dbConn, Integer.parseInt(seqId)); 
      T9AjaxUtil.ajax(id, response);
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;
  }
}
