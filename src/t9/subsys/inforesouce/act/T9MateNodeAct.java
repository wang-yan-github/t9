package t9.subsys.inforesouce.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.exps.T9InvalidParamException;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.subsys.inforesouce.data.T9MateNode;
import t9.subsys.inforesouce.logic.T9MateNodeLogic;
import t9.subsys.inforesouce.util.T9AjaxUtil;
/**
 * 
 * @author qwx110
 *
 */

public class T9MateNodeAct{
  
  private T9MateNodeLogic nodeLogic = new T9MateNodeLogic();
  
  /**
   * 保存选择的树中选择的要显示的节点
   * @param request
   * @param response
   * @return
   * @throws Exception 
   * @throws T9InvalidParamException 
   */
  public String saveNode(HttpServletRequest request, HttpServletResponse response) throws T9InvalidParamException, Exception{
    String tagname = request.getParameter("tagname");
    String nodes = request.getParameter("nodes");
    String nodeType = request.getParameter("nodeType");
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER"); 
      T9MateNode node = new T9MateNode();
      node.setNodes(nodes);
      node.setTagName(tagname);
      node.setUserId(user.getSeqId());
      int ok = nodeLogic.saveAjax(dbConn, node, nodeType);
      T9AjaxUtil.ajax(ok, response);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;   
    }    
    return null;
  }
  
  /**
   * 查询出某个用户下的所有的自定义标签
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findTagNameAjax(HttpServletRequest request, HttpServletResponse response) throws  Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;    
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER"); 
      String nodes = nodeLogic.tagName(dbConn, user, "1");
      //T9Out.println(nodes);
      T9AjaxUtil.ajax(nodes, response);
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return null;
  }
  
  /**
   * 删除标签
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteTagName(HttpServletRequest request, HttpServletResponse response)throws  Exception{
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;    
    try{
      dbConn = requestDbConn.getSysDbConn();
      int seqId = Integer.parseInt(request.getParameter("seqId"));
      int ok  = nodeLogic.deleteTagName(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"删除成功！");       
    } catch (Exception ex){
      String message = T9WorkFlowUtility.Message(ex.getMessage(),1);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, message);
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
}
