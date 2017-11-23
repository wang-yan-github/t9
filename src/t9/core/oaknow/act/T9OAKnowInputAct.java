package t9.core.oaknow.act;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.logic.T9OAKnowInputLogic;
import t9.core.oaknow.logic.T9OAKnowLogic;
import t9.core.oaknow.util.T9OAToJsonUtil;

/**
 * 知道录入
 * @author qwx110
 *
 */
public class T9OAKnowInputAct{
  private  T9OAKnowLogic oaLogicIndex = new T9OAKnowLogic();
  private T9OAKnowInputLogic inputLogic = new T9OAKnowInputLogic();
  /**
   * 跳转到知道录入
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String oaInput(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
      //T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      dbConn = requestDbConn.getSysDbConn();
      List<T9CategoriesType>  types = oaLogicIndex.findKind(dbConn);    
      request.setAttribute("toJson", T9OAToJsonUtil.toJsonTwo((types)));
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }    
    return "/core/oaknow/panel/oauserinput.jsp";
  }
  /**
   * 知道录入
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String input(HttpServletRequest request, HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);   
    T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
    try{
      dbConn = requestDbConn.getSysDbConn();
      T9OAAsk ask = new T9OAAsk();
      String askName = request.getParameter("ask");
      String categorieid = request.getParameter("typeId");
      String content = request.getParameter("content");
      String tab = request.getParameter("tab");    
      String answer = request.getParameter("answer");
      ask.setAsk(askName);
      ask.setCreator(user.getSeqId()+"");
      ask.setAskComment(content);
      ask.setTypeId(Integer.parseInt(categorieid));
      ask.setCreateDate(new Date());
      ask.setReplyKeyWord(tab);
      ask.setAnswer(answer);
      int id = inputLogic.insertNewAsk(dbConn, ask);
      List<T9CategoriesType>  types = oaLogicIndex.findKind(dbConn);    
      request.setAttribute("toJson", T9OAToJsonUtil.toJsonTwo((types)));
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }  
    return "/core/oaknow/panel/oauserinput.jsp";
  }
}
