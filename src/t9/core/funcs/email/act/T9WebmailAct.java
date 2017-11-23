package t9.core.funcs.email.act;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.email.data.T9WebmailBody;
import t9.core.funcs.email.logic.T9InnerEMailLogic;
import t9.core.funcs.email.logic.T9WebmailLogic;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;

/**
 * 
 * @author tulaike
 *
 */
public class T9WebmailAct {
  /**
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String setWebmailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      String seqId = wbl.setWebmail(dbConn, request.getParameterMap(), person);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\""+seqId+"\"");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String updateWebmailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      wbl.updateWebmail(dbConn, request.getParameterMap(),person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String listWebmailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    try{
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      String data = wbl.listWebmail(dbConn, person.getSeqId());
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String loadWebmailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String seqId = request.getParameter("seqId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      String data = wbl.getWebmail(dbConn, Integer.valueOf(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 删除邮箱配置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deletWebmailInfo(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String seqId = request.getParameter("seqId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      wbl.deletWebmail(dbConn, Integer.valueOf(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"邮箱配置删除成功!");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String hasLagerAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String bodyId = request.getParameter("bodyId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      String data = wbl.hasLagerAttachment(dbConn, Integer.valueOf(bodyId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
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
  public String refreshLagerAttachment(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String bodyId = request.getParameter("bodyId");
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      Connection dbConn = requestDbConn.getSysDbConn();
      T9WebmailLogic wbl = new T9WebmailLogic();
      T9WebmailBody wmb =  null;
      
      String attachmentId = "";
      String attachmentName = "";
      String refreshState = "0";
      try {
        wmb =  wbl.refreshLagerAttachmentMail(dbConn, Integer.valueOf(bodyId));
        attachmentId = wmb.getAttachmentId();
        attachmentName = wmb.getAttachmentName();
      } catch (Exception e) {
        refreshState = "1";
      }
      
      String data = "{bodyId:\"" + bodyId + "\",refreshState:\"" + refreshState + "\",attachmentId:\"" + attachmentId + "\",attachmentName:\"" + attachmentName + "\"}";
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,data);
    } catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
}
