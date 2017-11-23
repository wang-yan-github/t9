package t9.core.oaknow.act;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.system.syslog.logic.T9SysLogLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.logic.T9OAKnowAnswerLogic;
import t9.core.oaknow.logic.T9OAKnowLogic;
import t9.core.oaknow.logic.T9OAKnowMyPanelLogic;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.oaknow.util.T9OAToJsonUtil;
import t9.core.util.T9Out;

/**
 * oa知道管理面板
 * @author qwx110
 *
 */
public class T9OAKnowPanelAct{
  
  private T9OAKnowMyPanelLogic panelLogic = new T9OAKnowMyPanelLogic();
  private T9PageUtil pu = new T9PageUtil();
  private T9OAKnowAnswerLogic anLogic = new T9OAKnowAnswerLogic();
  private  T9OAKnowLogic oaLogicIndex = new T9OAKnowLogic();
  private T9SysLogLogic logLogic = new T9SysLogLogic();
  /**
   * oa知道管理面板左边的frame
   * @param request
   * @param response
   * @return
   */
  public String oAKonwLeftPanel(HttpServletRequest request,
      HttpServletResponse response){    
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    request.setAttribute("user", user);
    return "/core/oaknow/panel/oaleftpanel.jsp";
  }
  
  /**
   * 跳转到管理面板
   * @param request
   * @param response
   * @return
   */
  public String mainPanel(HttpServletRequest request,HttpServletResponse response){   
    return "/core/oaknow/panel/oaknowpanel.jsp";
  }
  
  /**
   * oa知道管理面板顶部的frame
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String topPanel(HttpServletRequest request,HttpServletResponse response) throws Exception{
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    request.setAttribute("user", user);
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String oAName = panelLogic.findOAName(dbConn);
      //T9Out.println("--------"+oAName);
      request.setAttribute("oAName", oAName);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }   
    return "/core/oaknow/panel/oaknowtopbar.jsp";
  }
  
  /**
   * oa知道管理面板右边的我的问题管理
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String leftPanel(HttpServletRequest request,HttpServletResponse response) throws Exception{ 
    return findMyAsk(request, response);
  }
  
  /**
   * oa知道管理面板右边的我的问题管理
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String findMyAsk(HttpServletRequest request,HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String crrNo = request.getParameter("currNo");//当前的页码
      int currNo = 1;
      if(T9StringUtil.isEmpty(crrNo)){
        currNo = 1;
      }else{
        currNo = Integer.parseInt(crrNo);
      }      
      int total = panelLogic.findMyAskCount(dbConn, user);
      pu.setCurrentPage(currNo);
      pu.setElementsCount(total);
      pu.setPageSize(10);
      List<T9OAAsk> asks = panelLogic.findMyAsks(dbConn, user, pu);
      request.setAttribute("asks", asks);
      request.setAttribute("page", pu);
    }catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }    
    return "/core/oaknow/panel/oamyask.jsp";
  }
  
  /**
   * oa管理面板的编辑我的问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String editMyAsk(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9OAAsk ask = new T9OAAsk();
      String askName = request.getParameter("ask");
      String typeId = request.getParameter("categorieid");
      String content = request.getParameter("content");
      String tab = request.getParameter("tab");
      ask.setAsk(askName);
      ask.setAskComment(content);
      ask.setTypeId(Integer.parseInt(typeId));
      ask.setReplyKeyWord(tab);
      ask.setSeqId(Integer.parseInt(request.getParameter("seqId")));
      ask.setCommend(Integer.parseInt(request.getParameter("commend")));
      int flag = anLogic.changeAsk(dbConn, ask);
      if(flag != 0){
        return findMyAsk(request, response);
      }
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return null;
  }
  
  /**
   * 跳转到编辑页面
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String goToEditPage (HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String askId = request.getParameter("askId");
      T9OAAsk ask = anLogic.findAskStatus(dbConn, Integer.parseInt(askId));
      List<T9CategoriesType>  types = oaLogicIndex.findKind(dbConn);
      
      request.setAttribute("toJson", T9OAToJsonUtil.toJsonTwo((types)));
      request.setAttribute("ask", ask);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/panel/oapaneledit.jsp";
  }
  /**
   * 删除我的问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAsk (HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String askId = request.getParameter("askId");
      int flag = anLogic.deleteMyAsk(dbConn, Integer.parseInt(askId));
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return findMyAsk(request, response);
  }
  
  /**
   * 我参与过的问题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findMyReferenceAsks (HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String crrNo = request.getParameter("currNo");//当前的页码
      int currNo = 1;
      if(T9StringUtil.isEmpty(crrNo)){
        currNo = 1;
      }else{
        currNo = Integer.parseInt(crrNo);
      }      
      int total = panelLogic.findMyReferenceAsksCount(dbConn, user);
      pu.setCurrentPage(currNo);
      pu.setElementsCount(total);
      pu.setPageSize(10);
      List<T9OAAsk> askList = panelLogic.findMyReferenceAsks(dbConn, user, pu);
      request.setAttribute("asks", askList);
      request.setAttribute("page", pu);
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/panel/myreference.jsp";
  }
  /**
   * 跳转到系统设置
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String findOAName(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String oaName = panelLogic.findOAName(dbConn).trim();
      request.setAttribute("oaName", oaName);
      request.setAttribute("flag", "0");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      e.printStackTrace();
    }
    return "/core/oaknow/panel/oachangename.jsp";
  }
  
  /**
   * 保存修改的oa名字
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String saveOaName(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String oaName = request.getParameter("oaName").trim();
      int flag = panelLogic.updateOrSave(dbConn, oaName);
      if(flag != 0){
        request.setAttribute("flag", "1");
        request.setAttribute("oaName", oaName);
      }
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      e.printStackTrace();
    }
    return "/core/oaknow/panel/oachangename.jsp";
  }
  
  /**
   * 用户管理
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String userManage(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String userKey = request.getParameter("userKey");
      int count = panelLogic.findPersonsCount(dbConn, userKey);
      pu.setElementsCount(count);
      pu.setPageSize(10);     
      int currNo = 1;
      if(T9StringUtil.isEmpty(request.getParameter("currNo"))){
        currNo = 1;
      }else{
        currNo = Integer.parseInt(request.getParameter("currNo"));
      }    
      pu.setCurrentPage(currNo);     
      List<T9Person> users = panelLogic.findPersons(dbConn, userKey, pu);
      request.setAttribute("users", users);
      request.setAttribute("page", pu);
      request.setAttribute("user", user);
      request.setAttribute("userKey", userKey);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/panel/oamamageuser.jsp";
  }
  
  public String findPerson(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      T9Person user = panelLogic.findPerson(dbConn, Integer.parseInt(userId));
      request.setAttribute("user", user);
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/oaknow/panel/useredit.jsp";
  }
  
  /**
   * 更新用户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updatePerson(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    T9Person user = new T9Person();
    try{
      dbConn = requestDbConn.getSysDbConn();
      String username = request.getParameter("username");
      String score = request.getParameter("score");
      String tderflag = request.getParameter("tderflag");
      String userId = request.getParameter("userId");
      user.setSeqId(Integer.parseInt(userId));
      user.setUserName(username);
      user.setScore(Integer.parseInt(score));
      user.setTderFlag(tderflag);      
      int flag = panelLogic.updatePerson(dbConn, user);
      if(flag !=0 ){
        T9Person p = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        T9SysLogLogic.addSysLog(dbConn, "8", p.getUserName()+"更新了"+user.getUserName()+"的用户的类型或分数", Integer.parseInt(userId), logLogic.getIpAddr(request));
        T9AjaxUtil.ajax(flag, response);
      }
   
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      e.printStackTrace();
    }
    return null ;
  }
  /**
   * 删除用户
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteUserByUserId(HttpServletRequest request,HttpServletResponse response)throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    try {
    dbConn = requestDbConn.getSysDbConn();
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    String userId = request.getParameter("userId");
    panelLogic.deleteUserReference(dbConn, Integer.parseInt(userId));
    T9SysLogLogic.addSysLog(dbConn, "8", user.getUserName()+"的用户删除"+userId+"的用户", Integer.parseInt(userId), logLogic.getIpAddr(request));
  } catch (Exception e) {
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      e.printStackTrace();
  }
  return userManage(request, response);
  }
}
