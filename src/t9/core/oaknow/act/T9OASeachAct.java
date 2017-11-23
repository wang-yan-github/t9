package t9.core.oaknow.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.logic.T9OAKnowMyPanelLogic;
import t9.core.oaknow.logic.T9OASeachLogic;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.oaknow.util.T9Escape;
/**
 * oa知道搜索
 * @author qwx110
 *
 */

public class T9OASeachAct{
  
  private T9OASeachLogic seachLogic = new T9OASeachLogic();
  private T9OAKnowMyPanelLogic panelLogic = new T9OAKnowMyPanelLogic();
  private T9PageUtil pu = new T9PageUtil();
 /**
  * 搜索与name相关问题并分页
  * @param request
  * @param response
  * @return
 * @throws Exception 
  */
 @SuppressWarnings("deprecation")
public String findResolveStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{  
     Connection dbConn = null;
     T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
     List<T9OAAsk> askList = null;
     int currNo = 1;
     try{
      dbConn = requestDbConn.getSysDbConn(); 
      String crrNo = request.getParameter("currNo").trim();
      if(T9StringUtil.isEmpty(crrNo)){
        currNo = 1;
      }else{
        currNo = Integer.parseInt(crrNo);
      }
      String askName = request.getParameter("question");//搜索题目
     // String askName = java.net.URLDecoder.decode(ask, "utf-8");
      String flag = request.getParameter("flag").trim();
      //askName = StringUtil.toChange(askName);
      int total = 1;
      if("resolve".equals(flag)){
        total = seachLogic.findAllAskResolvedCount(dbConn, askName);//解决的问题        
      }else if("noresolve".equals(flag)){
        total = seachLogic.findAskNoResolvedCount(dbConn, askName);        
      }     
      pu.setCurrentPage(currNo);
      pu.setPageSize(10);
      pu.setElementsCount(total); 
      if("resolve".equals(flag)){
        askList = seachLogic.findAllAskResolved(dbConn, askName, pu);
      }else if("noresolve".equals(flag)){
        askList = seachLogic.findAllAskNoResolved(dbConn, askName, pu);        
      } 
      String oaName = panelLogic.findOAName(dbConn).trim();        
      request.setAttribute("oaName", oaName);
      request.setAttribute("askList", askList);
      request.setAttribute("page", pu);
      request.setAttribute("askName", T9StringUtil.toChange(askName));
      request.setAttribute("flag", "'"+flag+"'");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }   
    return "/core/oaknow/oaseach.jsp";
 }
}
