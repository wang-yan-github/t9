package t9.core.oaknow.act;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
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
import t9.core.oaknow.data.T9CategoriesType;
import t9.core.oaknow.data.T9OAAsk;
import t9.core.oaknow.data.T9OAKnowUser;
import t9.core.oaknow.logic.T9OAAskQuestionLogic;
import t9.core.oaknow.logic.T9OAKnowLogic;
import t9.core.oaknow.logic.T9OAKnowMyPanelLogic;
import t9.core.oaknow.util.T9AjaxUtil;
import t9.core.oaknow.util.T9CountUtil;
import t9.core.oaknow.util.T9DateFormatUtil;
import t9.core.oaknow.util.T9PageUtil;
import t9.core.oaknow.util.T9StringUtil;
import t9.core.oaknow.util.T9OAToJsonUtil;
import t9.core.util.T9Utility;
/**
 * OA知道首页
 * @author qwx110
 *
 */
public class T9OAKnowAct{
    private  T9OAKnowLogic oaLogic = new T9OAKnowLogic();
    private T9OAAskQuestionLogic aqLogic = new T9OAAskQuestionLogic();
    private T9OAKnowMyPanelLogic panelLogic = new T9OAKnowMyPanelLogic();
    private  T9PageUtil pu = new T9PageUtil();
    private static Logger log = Logger
    .getLogger("t9.core.act.T9OAKnowAct");
    /**
     * OA知道首页
     * @param request
     * @param response
     * @return
     * @throws Exception 
     */
    public String OAKnowIndex(HttpServletRequest request, HttpServletResponse response) throws Exception{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      try{
        dbConn = requestDbConn.getSysDbConn();
        int count = oaLogic.findRegCount(dbConn);                //注册的用户数
        List<T9OAKnowUser> users = oaLogic.findJiFenBang(dbConn);//注册用户的列表  
        List<T9CategoriesType>  types = oaLogic.findKind(dbConn);//问题的分类        
        int hadResolvedCont = oaLogic.hadResolved(dbConn, 1);    //已经解决的问题
        int hadNoResolvedCont = oaLogic.hadResolved(dbConn, 0);  //待解决的问题
        //T9Out.println(hadNoResolvedCont+"------------------------------");
        List<T9OAAsk> askList = oaLogic.findGoodAnswer(dbConn);  //精彩问题推荐        
        List<T9OAAsk> noResolvedList = oaLogic.findNoResolvedAsk(dbConn);//没有解决的问题
        //T9Out.println(noResolvedList.size()+"------------------------------");
        List<T9OAAsk> resolvedList = oaLogic.findResolvedAsk(dbConn);  //最近解决的问题       
        T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");//获得登陆用户
        List<T9OAAsk> myAsk = oaLogic.findMyAsk(dbConn, user.getSeqId()+"");//我的问题
       // T9Out.println(myAsk.toString());
        String oaName = panelLogic.findOAName(dbConn).trim();        
        request.setAttribute("oaName", oaName);  
        int clock = T9CountUtil.getInstance().readCount(request);
        request.setAttribute("clock", clock);    
        request.setAttribute("types", types);
        request.setAttribute("count", count);
        request.setAttribute("users", users);
        request.setAttribute("hadResolvedCont", hadResolvedCont);
        request.setAttribute("hadNoResolvedCont", hadNoResolvedCont);
        request.setAttribute("askList", askList);
        request.setAttribute("noResolvedList", noResolvedList);
        request.setAttribute("resolvedList", resolvedList);
        request.setAttribute("myAsk", myAsk);
      } catch (Exception e){ 
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw e;
      }
      return "/core/oaknow/oaknowindex.jsp";
    }
  
    /**
     * goonask.jsp
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public String askQuestion(HttpServletRequest request, HttpServletResponse response) throws Exception, SQLException{
      String question = request.getParameter("questions");
      T9OAAsk  ask = new T9OAAsk();
      ask.setAsk(question);
      ask.setReplyKeyWord(question);
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);     
      try{
        dbConn = requestDbConn.getSysDbConn();
        List<T9OAAsk> asksList = oaLogic.referenceQuestion(dbConn, question);
        List<T9CategoriesType>  types = oaLogic.findKind(dbConn);
        String jsonString = T9OAToJsonUtil.toJsonTwo(types);
        String oaName = panelLogic.findOAName(dbConn).trim();        
        request.setAttribute("oaName", oaName);
        request.setAttribute("ask", ask);
        request.setAttribute("asktitle", T9StringUtil.toChange(ask.getAsk()));
        request.setAttribute("askkey", T9StringUtil.toChange(ask.getAsk()));
        request.setAttribute("asksList", asksList);
        request.setAttribute("jsonString", jsonString);
      }catch(Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
        throw e; 
      }
      return "/core/oaknow/goonask.jsp";
    }
    
    /**
     * 保存新问题
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public String saveAsk(HttpServletRequest request, HttpServletResponse response)throws Exception, SQLException{
      try{
        Connection dbConn = null;
        T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
        dbConn = requestDbConn.getSysDbConn();
        T9OAAsk ask = new T9OAAsk();
        T9Person user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
        int typeId = -1;
        String title = request.getParameter("title");//题目
        String keyword = request.getParameter("keyword");//标签
        String tid = request.getParameter("typeId");
        if(tid =="" || tid == null){
          typeId = -1;
        }else{
          typeId = Integer.parseInt(tid);
        }
        String content = request.getParameter("content");//内容
        ask.setCreator(user.getSeqId()+"");
        ask.setAsk(title);
        ask.setReplyKeyWord(keyword);
        ask.setTypeId(typeId);
        ask.setAskComment(content);
        ask.setCreateDateStr(T9DateFormatUtil.dateFormat(new Date()));
        ask.setCreateDate(new Date());
        int id = oaLogic.saveAsk(dbConn, ask);
        T9AjaxUtil.ajax(id, response);
      } catch (Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e; 
      }      
      return null;
    }
    /**
     * 提问分页
     * @return
     * @throws Exception 
     */
    public String askQuestionByPage(HttpServletRequest request, HttpServletResponse response) throws Exception{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      try{
        dbConn = requestDbConn.getSysDbConn();
        String askName = request.getParameter("title").trim();//题目
        String currNo = request.getParameter("currNo");//当前页
        if(T9StringUtil.isEmpty(currNo)){
          currNo = "1";
        }
        //askName = URLDecoder.decode(askName,"UTF-8");
        int total = aqLogic.findAsksCount(dbConn, askName);   //总的元素数
        pu.setPageSize(10);
        pu.setElementsCount(total);
        pu.setCurrentPage(Integer.parseInt(currNo));
        String asksJson = aqLogic.findAsks(dbConn, askName, pu);
        String newJson = toJson(asksJson, pu);
        T9AjaxUtil.ajax(newJson, response);
      }  catch (Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e; 
      }
      return null;
    }
    /**
     * 工具方法
     * @param pages  页面上显示的记录
     * @param pageNo 分页
     * @return
     */
    public String toJson(String pages, T9PageUtil pu){
      StringBuffer sb = new StringBuffer();
      sb.append("{").append("page:").append(pages).append(",");
      sb.append("currNo:").append(pu.getCurrentPage()).append(",");
      sb.append("totalNo:").append(pu.getPagesCount());
      sb.append("}");
      //T9Out.println(sb.toString());
      return sb.toString();
    }
    
    public String ajaxOaDesk(HttpServletRequest request, HttpServletResponse response)throws Exception{
      Connection dbConn = null;
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);    
      try{
        dbConn = requestDbConn.getSysDbConn();
        String asks = oaLogic.oaDesk(dbConn);
        T9AjaxUtil.ajax(asks, response);
      }catch(Exception e){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
        throw e; 
      }
      return null;
    }
}
