package t9.subsys.oa.examManage.act;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamPaper;
import t9.subsys.oa.examManage.logic.T9ExamPaperLogic;

public class T9ExamPaperAct {
  public static final String attachmentFolder = "examManage";
  private T9ExamPaperLogic logic = new T9ExamPaperLogic();
  
  /**
   * 新建试卷--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addPaper(HttpServletRequest request,
       HttpServletResponse response) throws Exception{
      
     Connection dbConn = null;
     try {
       T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
       dbConn = requestDbConn.getSysDbConn();
       T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
       Map<String,String[]> map = request.getParameterMap();
       T9ExamPaper paper = (T9ExamPaper) T9FOM.build(map, T9ExamPaper.class, "");
       Date curTime = new Date();
       paper.setSendDate(curTime);
       paper.setUserId(String.valueOf(person.getSeqId()));
       this.logic.addPaper(dbConn, paper);
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
   * 获取最新插入试卷的seqId--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPaperSeqId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int paperSeqId = this.logic.getExmaPaperSeqId(dbConn);
      String data = String.valueOf(paperSeqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取试题数量--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getQuestionsCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("paperSeqId");
      String data = this.logic.getQuestionsCountLogic(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取所选试题ID串--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getQuestionsList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("paperSeqId");
      String data = this.logic.getQuestionsListLogic(dbConn, Integer.parseInt(seqId));
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取指定试卷包含的试题列表--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getExamPaperListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int paperSeqId = Integer.parseInt(request.getParameter("paperSeqId"));
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getExamPaperListJson(dbConn, request.getParameterMap(), person, paperSeqId);
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
   * 获取所属题库名称(联合查询)--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRoomName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paperSeqIdStr = request.getParameter("paperSeqId");
      int roomId = 0;
      if(!T9Utility.isNullorEmpty(paperSeqIdStr)){
        roomId = Integer.parseInt(paperSeqIdStr);
      }
      String data = this.logic.getRoomNameLogic(dbConn, roomId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + T9Utility.encodeSpecial(data) + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取所属题库名称(单表查询)--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRoomNameSingle(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String roomIdStr = request.getParameter("roomId");
      int roomId = 0;
      if(!T9Utility.isNullorEmpty(roomIdStr)){
        roomId = Integer.parseInt(roomIdStr);
      }
      String data = this.logic.getRoomNameSingleLogic(dbConn, roomId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + T9Utility.encodeSpecial(data) + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 在试卷管理表（EXAM_PAPER）中根据seqId查询roomId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRoomId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paperSeqIdStr = request.getParameter("paperSeqId");
      int paperSeqId = 0;
      if(!T9Utility.isNullorEmpty(paperSeqIdStr)){
        paperSeqId = Integer.parseInt(paperSeqIdStr);
      }
      String data = this.logic.getRoomIdLogic(dbConn, paperSeqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 自动选题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAutoTopics(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String questionCount = request.getParameter("questionCount");
      String questionsRank = request.getParameter("questionsRank");
      String questionsType = request.getParameter("questionsType");
      String paperSeqId = request.getParameter("paperSeqId");
      String data = "";
      if(T9Utility.isNullorEmpty(paperSeqId)){
        paperSeqId = "0";
      }
      T9ORM orm = new T9ORM();
      T9ExamPaper paper = (T9ExamPaper) orm.loadObjSingle(dbConn, T9ExamPaper.class, Integer.parseInt(paperSeqId));
      ArrayList<T9ExamPaper> paperList = this.logic.getExamPaperList(dbConn, questionCount, paper.getRoomId(), questionsRank, questionsType);
      int curNum = paperList.size();
      int questionsCount = paper.getQuestionsCount();
      if(curNum >= questionsCount){
        String[] intRet = new String[paperList.size()];
        for(int i = 0; i < paperList.size(); i++){
          intRet[i] = String.valueOf(paperList.get(i).getSeqId());
        }
        String[] seqIdStr = this.logic.getIntLogic(intRet, paper.getQuestionsCount());
        this.logic.updateQuestionList(dbConn, Integer.parseInt(paperSeqId), seqIdStr, paper);
        data = "0";
      }else{
        data = "1";
      }
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 管理试卷列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getExamPaperTitleJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getExamPaperTitleJson(dbConn, request.getParameterMap(), person);
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
   * 删除一条记录--cc
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
  
  public String getExamPaperDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9ExamPaper paper = (T9ExamPaper)this.logic.getExamPaperDetail(dbConn, Integer.parseInt(seqId));
      if (paper == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "该试卷不存在");
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
  
  /**
   * 修改试卷--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateExamPaper(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9ExamPaper record = (T9ExamPaper) T9FOM.build(map, T9ExamPaper.class, "");
      this.logic.updateExamPaper(dbConn, record);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 判断试卷是否正被使用--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String useredByPaper(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqIdStr = request.getParameter("seqId");
      int seqId = 0;
      if(!T9Utility.isNullorEmpty(seqIdStr)){
        seqId = Integer.parseInt(seqIdStr);
      }
      boolean bool = this.logic.useredByPaper(dbConn, seqId);
      String data = "";
      if(bool){
        data = "1";
      }else{
        data = "0";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 判断所选试题数量是否溢出--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String isCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String roomId = request.getParameter("roomId");
      String data = "";
      long questionsCount = 0;
      String questionStr = request.getParameter("questionsCount");
      if(!T9Utility.isNullorEmpty(questionStr)){
        questionsCount = Long.parseLong(questionStr);
      }
      long count = this.logic.isCount(dbConn, roomId);
      if(questionsCount > count){
        data = "1";
      }else{
        data = "0";
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取手动选题后的试题列表--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSelectManualJson(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String roomId = request.getParameter("roomId");
      String questionsType = request.getParameter("questionsType");
      String questionsRank = request.getParameter("questionsRank");
      String whereStr = "";
      List<Map> list = new ArrayList();
      T9ORM orm = new T9ORM();
      HashMap map = null;
      StringBuffer sb = new StringBuffer("[");
      if(!T9Utility.isNullorEmpty(questionsRank)){
        whereStr += " and QUESTIONS_RANK = '" + questionsRank + "'";
      }
      if(!T9Utility.isNullorEmpty(questionsType)){
        whereStr += " and QUESTIONS_TYPE = '" + questionsType + "'";
      }
      String[] filters = new String[]{"ROOM_ID=" + roomId + "" + whereStr + ""};
      List funcList = new ArrayList();
      funcList.add("examQuiz");
      
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("EXAM_QUIZ"));
      for(Map ms : list){
        String questions = (String) ms.get("questions");
        questions = T9Utility.encodeSpecial(questions);
        sb.append("{");
        sb.append("seqId:\"" + (ms.get("seqId") == null ? "" : ms.get("seqId")) + "\"");
        sb.append(",questionsType:\"" + (ms.get("questionsType") == null ? "" : ms.get("questionsType")) + "\"");
        sb.append(",questionsRank:\"" + (ms.get("questionsRank") == null ? "" : ms.get("questionsRank")) + "\"");
        sb.append(",questions:\"" + (ms.get("questions") == null ? "" :questions) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 
      if(list.size() == 0){
        sb = new StringBuffer("[");
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 手动选题－保存--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateSelectManual(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9ORM orm = new T9ORM();
      T9ExamPaper record = (T9ExamPaper) T9FOM.build(map, T9ExamPaper.class, "");
      int seqId = record.getSeqId();
      T9ExamPaper paper = (T9ExamPaper) orm.loadObjSingle(dbConn, T9ExamPaper.class, seqId);
      int paperGrade = 0;
      int questionsCount = 0;
      paperGrade = paper.getPaperGrade();
      String questionsList = record.getQuestionsList();
      String[] questStr = questionsList.split(",");
      for(int i = 0; i < questStr.length; i++){
        questionsCount++;
      }
      int aveCore = paperGrade/questionsCount;
      String quesCore = "";
      for(int i = 0; i < questStr.length; i++){
        if (!"".equals(quesCore)) {
          quesCore += ",";
        }
        quesCore += String.valueOf(aveCore);
      }
      
      Map m =new HashMap();
      m.put("seqId", seqId);
      m.put("questionsList", questionsList);
      m.put("questionsScore", quesCore);
      
      orm.updateSingle(dbConn, "examPaper", m);
      
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
