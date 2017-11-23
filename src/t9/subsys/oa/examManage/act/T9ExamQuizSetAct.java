package t9.subsys.oa.examManage.act;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamQuizSet;
import t9.subsys.oa.examManage.logic.T9ExamQuizSetLogic;

public class T9ExamQuizSetAct {

  public static final String attachmentFolder = "examManage";
  private T9ExamQuizSetLogic logic = new T9ExamQuizSetLogic();
  
  /**
   * 新建题库--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addBank(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
      
     Connection dbConn = null;
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       Map<String,String[]> map = request.getParameterMap();
       T9ExamQuizSet quiz = (T9ExamQuizSet) T9FOM.build(map, T9ExamQuizSet.class, "");
       this.logic.addBank(dbConn, quiz);
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
   * 获取题库列表--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getExamQuizSetList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getExamQuizSetList(dbConn, request.getParameterMap());
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
   * 删除题库--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
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
   * 获取题库详情(修改题库)--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getExamQuizSetDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9ExamQuizSet quiz = (T9ExamQuizSet)this.logic.getExamQuizSetDetail(dbConn, Integer.parseInt(seqId));
      if (quiz == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "该试卷不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(quiz);
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
  
  /**
   * 修改题库--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateExamQuizSet(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9ExamQuizSet record = (T9ExamQuizSet) T9FOM.build(map, T9ExamQuizSet.class, "");
      this.logic.updateExamQuizSet(dbConn, record);
      
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
