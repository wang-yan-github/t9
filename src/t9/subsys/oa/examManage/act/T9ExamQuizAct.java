package t9.subsys.oa.examManage.act;
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
import t9.subsys.oa.examManage.data.T9ExamQuiz;
import t9.subsys.oa.examManage.logic.T9ExamQuizLogic;

public class T9ExamQuizAct {

  public static final String attachmentFolder = "examManage";
  private T9ExamQuizLogic logic = new T9ExamQuizLogic();

  /**
   * 新建试题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addQuiz(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      String answers = request.getParameter("answers");
      T9ExamQuiz quiz = (T9ExamQuiz) T9FOM.build(map, T9ExamQuiz.class, "");
      quiz.setAnswers(answers.toUpperCase());
      this.logic.addQuiz(dbConn, quiz);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }

    return "/core/inc/rtjson.jsp";
  }

  /**
   * 得到试题BySeqId ---syl
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getQuizById(HttpServletRequest request,
      HttpServletResponse response) throws Exception{

    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      String data = "";
      T9ExamQuizLogic quizLogic = new  T9ExamQuizLogic();
      if(T9Utility.isInteger(seqId)){
        T9ExamQuiz quiz = quizLogic.selectQuizById(dbConn, Integer.parseInt(seqId));
        if(quiz != null){
          data = T9FOM.toJson(quiz).toString();
        }
      }
      if(data.equals("")){
        data = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA, data); 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *查询所有(分页)通用列表显示数据--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectQuiz(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String roomId = request.getParameter("roomId");
      String data = T9ExamQuizLogic.selectQuiz(dbConn,request.getParameterMap(),roomId);
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
  /**
   *删除--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteQuiz(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ExamQuizLogic.deleteQuiz(dbConn,seqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   *修改--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateQuiz(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9ExamQuiz quiz = (T9ExamQuiz)T9FOM.build(request.getParameterMap());
      T9ExamQuizLogic.updateQuiz(dbConn, quiz);//修改数据
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   *查询--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showQuiz(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      //定义数组将数据保存到Json中
      String data = "";
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ExamQuiz flow = (T9ExamQuiz)T9ExamQuizLogic.showQuiz(dbConn, seqId);
        if(flow != null) {
          data = data + T9FOM.toJson(flow);
          data = data.replaceAll("\\n", "");
          data = data.replaceAll("\\r", "");
        }
        data = data + "";
        if(data.equals("")){
          data = "{}";
        }
      }
      //保存查询数据是否成功，保存date
      request.setAttribute(T9ActionKeys.RET_DATA, data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
