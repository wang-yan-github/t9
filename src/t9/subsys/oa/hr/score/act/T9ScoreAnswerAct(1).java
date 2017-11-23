package t9.subsys.oa.hr.score.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreAnswer;
import t9.subsys.oa.hr.score.logic.T9ScoreAnswerLogic;

public class T9ScoreAnswerAct {
  public static final String attachmentFolder = "scoreAnswer";
  private T9ScoreAnswerLogic logic = new T9ScoreAnswerLogic();
  /**
   * 选择添加考核项目--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addItemAnswer(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
     
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String itemId = request.getParameter("itemId");
      T9ScoreAnswer scoreAnswer = (T9ScoreAnswer) T9FOM.build(map, T9ScoreAnswer.class, "");
      scoreAnswer.setGroupId(Integer.parseInt(itemId));
      this.logic.addScoreAnswer(dbConn, scoreAnswer);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  
  public String getScoreAnswerList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String itemId = request.getParameter("seqId");
      String data = this.logic.getScoreAnswerList(dbConn, request.getParameterMap(), Integer.parseInt(itemId));
      PrintWriter pw = response.getWriter();
      pw.println(data);
      pw.flush();
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return null;
  }
  
  public String deleteSingle(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      
      this.logic.deleteSingle(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取考核项目选项信息--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreAnswerDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9ScoreAnswer paper = (T9ScoreAnswer)this.logic.getScoreAnswerDetail(dbConn, Integer.parseInt(seqId));
      if (paper == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "该考核指标集不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(paper);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data.toString()); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    
    return "/core/inc/rtjson.jsp";
  }
  

  public String updateScoreAnswer(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9ScoreAnswer scoreAnswer = (T9ScoreAnswer) T9FOM.build(map, T9ScoreAnswer.class, "");
      this.logic.updateScoreAnswer(dbConn, scoreAnswer);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  

}
