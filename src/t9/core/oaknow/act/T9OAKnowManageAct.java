package t9.core.oaknow.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.logic.T9OAKnowAnswerLogic;
import t9.core.oaknow.logic.T9OAKnowManageLogic;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Utility;

/**
 * 知道管理
 * @author qwx110
 *
 */
public class T9OAKnowManageAct{
  private T9OAKnowManageLogic knowLogic = new T9OAKnowManageLogic();
  private T9PageUtil pu = new T9PageUtil();
  private T9OAKnowAnswerLogic oaLogic = new T9OAKnowAnswerLogic();
  /**
   * 跳转到知道管理页面
   * @param request
   * @param response
   * @throws Exception
   */
  public String gotoManage(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
    try{
      dbConn = requestDbConn.getSysDbConn();
      String ask = request.getParameter("ask");
      String startTime = request.getParameter("startTime");
      String endTime = request.getParameter("endTime");
      String status = request.getParameter("status");
      String crrNo = request.getParameter("currNo");//当前的页码
      int currNo = 1;
      if(T9StringUtil.isEmpty(crrNo)){
        currNo = 1;
      }else{
        currNo = Integer.parseInt(crrNo);
      }      
      int count = knowLogic.getCount(dbConn, status, startTime, endTime, ask);
      pu.setElementsCount(count);
      pu.setPageSize(10);
      pu.setCurrentPage(currNo);
      List<T9OAAsk> askList  = new ArrayList<T9OAAsk>();
      askList =  knowLogic.getAsks(dbConn, pu, status, startTime, endTime, ask);
      request.setAttribute("askList", askList);
      request.setAttribute("page", pu);
      request.setAttribute("ask", ask);
      request.setAttribute("startTime", startTime);
      request.setAttribute("endTime", endTime);
      if(T9StringUtil.isEmpty(status)){
        request.setAttribute("status", "");
      }else{
        request.setAttribute("status", status);
      }
      
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/panel/allask.jsp";
  }
  /**
   * 推荐状态和非推荐状态
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String toTuiJian(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      dbConn = requestDbConn.getSysDbConn();
      int askId = Integer.parseInt(request.getParameter("askId"));
      int flag  = Integer.parseInt(request.getParameter("flag"));
      int id = 0;
      if(flag == 1){
        id = oaLogic.tuiJianStatus(dbConn, askId, 1);  //推荐状态
      }else if(flag == 0){
        id = oaLogic.tuiJianStatus(dbConn, askId, 0);  //取消推荐 状态  
      }   
      T9AjaxUtil.ajax(id, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;
  }
 /**
  * 删除答案 
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
  public String deleteAsk(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try{
      dbConn = requestDbConn.getSysDbConn();
      String askId = request.getParameter("askId");
      String status = request.getParameter("status");
      int id = knowLogic.deleteAsk(dbConn, Integer.parseInt(askId), Integer.parseInt(status));
      T9AjaxUtil.ajax(id, response);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;
  }
}
