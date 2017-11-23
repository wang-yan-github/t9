package t9.subsys.inforesouce.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Out;
import t9.core.util.T9Utility;
import t9.subsys.inforesouce.data.T9MateShow;
import t9.subsys.inforesouce.data.T9MateType;
import t9.subsys.inforesouce.logic.T9MateShowLogic;
import t9.subsys.inforesouce.util.T9StringUtil;

/**
 * 控制元数据树的类
 * @author qwx110
 *
 */
public class T9MateShowAct{
  private T9MateShowLogic show = new T9MateShowLogic();
  /**
   * 跳转到managemate.jsp
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String toManage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");      //获得登陆用户
      String ftype = request.getParameter("ftype");
      if(T9Utility.isNullorEmpty(ftype)){
        ftype = "1";
      }
      List<T9MateType> types = show.findMyMenus(dbConn, user, ftype); // 此方法查询所有的父元素，子元素，值域
      T9MateShow idString = show.findMyShow(dbConn, user, ftype);    // 用户第二次登陆显示 上次定义好的元素
      String saveOk = request.getParameter("saveOk");
      if(T9StringUtil.isEmpty(saveOk)){
        saveOk = "";
      }
      request.setAttribute("ftype", ftype);
      request.setAttribute("saveOk", saveOk);
      request.setAttribute("types", types);
      request.setAttribute("idStr", idString);
    } catch (Exception e){ 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;      
    } 
    return "/subsys/inforesource/managemate.jsp";
  } 
  
  /**
   * 保存用户所选的menu
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveAjax(HttpServletRequest request, HttpServletResponse response)throws Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    String saveOk = "fail";
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");      //获得登陆用户
      String showId = request.getParameter("show");                                   //获得用户选择的所有的顶级父节点，通过顶级父节点获得选择的项
      String newIds = newString(showId, request);
      String typeId = request.getParameter("ftype");
      int k = show.saveOrUpdate(dbConn, user, showId, newIds, typeId);
      if(k != 0){
        saveOk = "ok";
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/t9/subsys/inforesouce/act/T9MateShowAct/toManage.act?saveOk=" + saveOk;
  }
  
  /**
   * 工具方法
   * @param showId
   * @param request
   * @return
   */
  public String newString(String showId,HttpServletRequest request){
    String newIds = "";
    if(T9StringUtil.isNotEmpty(showId)){
      String[] pIds = showId.split(",");//把所有的父元素区分
      if(pIds.length != 0){        
        for(int i=0; i<pIds.length; i++){
         String[] ids = request.getParameterValues(pIds[i]);//每次获得父元素一个节点（包括子元素和值域 他们的name都是相同的（相当于key），获得他们的名字就能获得不同的值）
         String str = T9StringUtil.array2AString(ids);
         if(T9StringUtil.isNotEmpty(str)){
           newIds += str +",|";
         }       
        }
        //T9Out.println(newIds);
        newIds = newIds.substring(0, newIds.lastIndexOf("|")==-1?0:newIds.lastIndexOf("|"));
      }
    }
    return newIds;
  }
  
}
