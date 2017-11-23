package t9.core.oaknow.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9AskAnswer;
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.data.T9OAComment;
import t9.core.oaknow.logic.T9OAKnowAnswerLogic;
import t9.core.oaknow.logic.T9OAKnowLogic;
import t9.core.oaknow.logic.T9OAKnowMyPanelLogic;
import t9.core.oaknow.logic.T9OAKnowTypeLogic;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.util.T9Out;

/**
 * 与问题相关
 * @author qwx110
 *
 */
public class T9OAAskReferenceAct{

  private  T9OAKnowAnswerLogic oaLogic = new T9OAKnowAnswerLogic();
  private  T9OAKnowLogic oaLogicIndex = new T9OAKnowLogic();
  private T9OAKnowTypeLogic typeLogic = new T9OAKnowTypeLogic();
  private T9OAKnowMyPanelLogic panelLogic = new T9OAKnowMyPanelLogic();
  private static Logger log = Logger
  .getLogger("t9.core.act.T9OAAskReference");
  
  /**
   * 问题状态，最佳答案，相关问题，最佳答案的评论
   * @return
   * @throws Exception 
   */
  public String findAskRef(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    int askId = Integer.parseInt(request.getParameter("askId"));
    try{
      
      dbConn = requestDbConn.getSysDbConn();
      T9OAAsk ask = oaLogic.findAskStatus(dbConn, askId);//查找问题状态
      T9AskAnswer goodAnswer = oaLogic.findBetterAnswer(dbConn, askId);//最佳答案
      List<T9AskAnswer> otherAnswers = oaLogic.findOtherAnswer(dbConn, askId);//其他答案
      List<T9OAComment> pinLun = oaLogic.findBetterAnswerPingLun(dbConn, goodAnswer.getAnswerId());//对最佳答案的评论      
      List<T9OAAsk> askList = oaLogic.findRefAsk(dbConn, askId);  //相关问题
      List<T9CategoriesType> types = typeLogic.findTypseUtil3(dbConn, askId);
      String oaName = panelLogic.findOAName(dbConn).trim();  
      String showFlag = request.getParameter("showFlag");
      if(T9StringUtil.isNotEmpty(showFlag)){
        request.setAttribute("showFlag", showFlag);
      }
      request.setAttribute("oaName", oaName);
      request.setAttribute("types", types);
      request.setAttribute("ask", ask);
      request.setAttribute("askList", askList);
      request.setAttribute("goodAnswer", goodAnswer);
      request.setAttribute("otherAnswers", otherAnswers);
      request.setAttribute("pinLun", pinLun);
      
    }catch(Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    
    return "/core/oaknow/oaknowask.jsp";
  }
  
  /**
   * 回答问题
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
  public String toAnswerAjax(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9AskAnswer answer = new T9AskAnswer();
      int askId = Integer.parseInt(request.getParameter("askId"));
      String answerComment = request.getParameter("content");
      T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
      answer.setAskId(askId);
      answer.setAnswerComment(answerComment);
      answer.setAnswerUserId(user.getSeqId()+"");
      answer.setAnswerTime(new Date());
      answer.setGoodAnswer(0);
      int id = oaLogic.insertAnswer(dbConn, answer);
      PrintWriter pw = response.getWriter();
      if(id !=0 ){
        String rtData = "{rtState:'0',rtMsrg:'提交答案成功'}";
        pw.println(rtData);       
      }else{
        String rtData = "{rtState:'1',rtMsrg:'提交答案失败'}";
        pw.println(rtData);        
      }
      pw.flush();
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    } 
    return null;
  }
 
  /**
   * 对最佳答案的评论输入
   * @param request
   * @param response
   * @return
   * @throws Exception 
   */
 public String goodAnsPingLun(HttpServletRequest request, HttpServletResponse response) throws Exception {
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   try{
    dbConn = requestDbConn.getSysDbConn();
    String askId = request.getParameter("askId").trim();    
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
    int userId = user.getSeqId();
    //T9Out.println(askId+"--------------------------userId========" + userId);
    String comm = request.getParameter("comment");
    T9OAComment comment = new T9OAComment();
    comment.setAskId(Integer.parseInt(askId));
    comment.setComment(comm);
    comment.setMamber(userId+"");
    comment.setDateTime(new Date());
    
    int id = oaLogic.goodAnswerPingLun(dbConn, comment);
    PrintWriter pw = response.getWriter();
    if(id !=0 ){
      String rtData = "{rtState:'0',rtMsrg:'提交答案成功'}";
      pw.println(rtData);       
    }else{
      String rtData = "{rtState:'1',rtMsrg:'提交答案失败'}";
      pw.println(rtData);        
    }
    pw.flush();
  } catch (Exception e){
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
  } 
  return null;
 }
 
/**
 * 采纳为答案
 * @param request
 * @param response
 * @return
 * @throws Exception 
 */
 public String changeToGoodAnswer(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   try{
    dbConn = requestDbConn.getSysDbConn();
    int askId = Integer.parseInt(request.getParameter("askId"));
    int answerId = Integer.parseInt(request.getParameter("answerId"));
    int userId = Integer.parseInt(request.getParameter("userId"));
    int status = oaLogic.changeToGoodAnswer(dbConn, askId, answerId, userId);
    
    PrintWriter pw = response.getWriter();
    if(status !=0 ){
      String rtData = "{rtState:'0',rtMsrg:'提交答案成功'}";
      pw.println(rtData);       
    }else{
      String rtData = "{rtState:'1',rtMsrg:'提交答案失败'}";
      pw.println(rtData);        
    }
    pw.flush();
  }  catch (Exception e){    
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
  }
   return null;
 }
 
 /**
  * 推荐某个问题为推荐状态
  * @param request
  * @param response
  * @return
 * @throws Exception 
  */
 public String tuiJianStatus(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int id =0;
   try{
     dbConn = requestDbConn.getSysDbConn();
     int askId = Integer.parseInt(request.getParameter("askId"));
     int flag  = Integer.parseInt(request.getParameter("flag"));
     if(flag == 1){
       id = oaLogic.tuiJianStatus(dbConn, askId, 1);  //推荐状态
     }else if(flag == 0){
       id = oaLogic.tuiJianStatus(dbConn, askId, 0);  //取消推荐 状态  
     }      
     T9AjaxUtil.ajax(id, response);
   }catch(Exception e){
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
     throw e;
   }
   return null;
 }
 
 /**
  * 编辑问题
  * @param request
  * @param response
  * @return
 * @throws Exception 
  */
 public String editAsk(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int askId = Integer.parseInt(request.getParameter("askId"));
   try{
     
     dbConn = requestDbConn.getSysDbConn();
     T9OAAsk ask = oaLogic.findAskStatus(dbConn, askId);//查找问题状态
     T9AskAnswer goodAnswer = oaLogic.findBetterAnswer(dbConn, askId);//最佳答案
     List<T9AskAnswer> otherAnswers = oaLogic.findOtherAnswer(dbConn, askId);//其他答案
     List<T9OAComment> pinLun = oaLogic.findBetterAnswerPingLun(dbConn, goodAnswer.getAnswerId());//对最佳答案的评论      
     List<T9CategoriesType>  types = oaLogicIndex.findKind(dbConn);
     List<T9OAAsk> askList = oaLogic.findRefAsk(dbConn, askId);  //相关问题
     List<T9CategoriesType> kinds = typeLogic.findTypseUtil3(dbConn, askId);
     String oaName = panelLogic.findOAName(dbConn).trim();        
     request.setAttribute("oaName", oaName);
     request.setAttribute("kinds", kinds);
     request.setAttribute("askList", askList);
     request.setAttribute("toJson", toJson(types));
     request.setAttribute("ask", ask);
     request.setAttribute("goodAnswer", goodAnswer);
     request.setAttribute("otherAnswers", otherAnswers);
     request.setAttribute("pinLun", pinLun);
     
   }catch(Exception e){
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
     request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
     throw e;
   }
   return "/core/oaknow/oaknoweditask.jsp";
 }

 /**
  * 问题编辑页面的管理员删除答案问题
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String deleteAnswer(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int answerId = Integer.parseInt(request.getParameter("answerId"));
   int flag = Integer.parseInt(request.getParameter("flag"));
   int userId = Integer.parseInt(request.getParameter("userId"));
   int askId = Integer.parseInt(request.getParameter("askId"));
   int id = 0;
   try{
    dbConn = requestDbConn.getSysDbConn();
     if( flag == 1){ //删除最佳答案
      id = oaLogic.deteteAnswerByFlag(dbConn, answerId, askId, 1, userId);
     }else if(flag == 0){ //删除一般答案
      id = oaLogic.deteteAnswerByFlag(dbConn, answerId, askId, 0, userId);
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
 * 问题编辑页面的采纳答案问题 
 * @param request
 * @param response
 * @return
 * @throws Exception 
 */
 public String agreeToGoodAnswer(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int goodAnswerId = Integer.parseInt(request.getParameter("goodAnswerId")); //以前的最佳答案的id
   int newAnswerId  = Integer.parseInt(request.getParameter("newAnswerId"));  //现在采纳的最佳答案的id
   int oldUserId = Integer.parseInt(request.getParameter("oldUserId"));
   int newUserId = Integer.parseInt(request.getParameter("newUserId"));
   int oldAskId = Integer.parseInt(request.getParameter("oldAskId"));
   int newAskId = Integer.parseInt(request.getParameter("newAskId"));   
   int id= 0;
   try{
    dbConn = requestDbConn.getSysDbConn();   
    id = oaLogic.agreeToGoodAnswer(dbConn, goodAnswerId, newAnswerId, oldUserId, newUserId, oldAskId, newAskId);
    T9AjaxUtil.ajax(id, response);
  } catch (Exception e){    
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
  }
   return null;
 } 
 /**
  * 问题编辑页面删除评论
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String deleteComment(HttpServletRequest request, HttpServletResponse response) throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int commentId = Integer.parseInt(request.getParameter("commentId"));
   int id = 0;
   try{
    dbConn = requestDbConn.getSysDbConn();
    id = oaLogic.deleteComment(dbConn, commentId);
    T9AjaxUtil.ajax(id, response);
  } catch (Exception e){
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
  }
  return null; 
 }
 
 /**
  * 更改问题的答案
  * @param request
  * @param response
  * @return
  * @throws Exception
  */
 public String changeAnswers(HttpServletRequest request, HttpServletResponse response)throws Exception{
   Connection dbConn = null;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
   int answerId = Integer.parseInt(request.getParameter("answerId").trim());
   String content = request.getParameter("content");
   try{
    dbConn = requestDbConn.getSysDbConn();
     int id = 0;
     id = oaLogic.changeAnswer(dbConn, answerId, content);
     T9AjaxUtil.ajax(id, response);
  } catch (Exception e){
    request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
    request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
    throw e;
  }
  return null;   
 }
 /**
  * 更改问题
  * @param request
  * @param response
  * @return
  * @throws Exception
  */ 
 public String changeAsk(HttpServletRequest request, HttpServletResponse response)throws Exception{
   Connection dbConn = null;
   int id = 0;
   T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
   int askId = Integer.parseInt(request.getParameter("askId").trim());  //问题id
   int typeId = Integer.parseInt(request.getParameter("typeId").trim());//类型id
   String as = request.getParameter("ask");                             //问题题目
   String keyword = request.getParameter("keyword");                    //标签
   String content = request.getParameter("content");                    //问题内容
   T9OAAsk ask = new T9OAAsk();
   ask.setSeqId(askId);
   ask.setAsk(as);
   ask.setAskComment(content);
   ask.setReplyKeyWord(keyword);
   ask.setTypeId(typeId);
   try{
     dbConn = requestDbConn.getSysDbConn();
     id = oaLogic.changeAsk(dbConn, ask);
     T9AjaxUtil.ajax(id, response);
   }catch(Exception e){
     request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
     request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
     throw e;
   }
   return null;
 }
 
 /**
  * 生成json的工具类
  * @param list
  * @return
  */
 public String toJson(List<T9CategoriesType> list){
   StringBuffer sb = new StringBuffer();
   sb.append("[");
     if(list != null && list.size() != 0){
        for(int i=0; i < list.size(); i++){
           if(i < list.size()-1){
              sb.append(list.get(i).toString()).append(",");
           }else{
             sb.append(list.get(list.size()-1).toString());
           }
        }
     }
   sb.append("]");
   //T9Out.println(sb.toString()+"****************");
   return sb.toString();
 }
}
