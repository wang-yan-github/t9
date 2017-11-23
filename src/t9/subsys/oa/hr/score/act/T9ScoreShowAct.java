package t9.subsys.oa.hr.score.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.calendar.data.T9Calendar;
import t9.core.funcs.calendar.logic.T9CalendarLogic;
import t9.core.funcs.diary.logic.T9MyPriv;
import t9.core.funcs.diary.logic.T9PrivUtil;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.person.logic.T9PersonLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.module.org_select.logic.T9OrgSelect2Logic;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.hr.score.data.T9ScoreAnswer;
import t9.subsys.oa.hr.score.data.T9ScoreData;
import t9.subsys.oa.hr.score.data.T9ScoreFlow;
import t9.subsys.oa.hr.score.data.T9ScoreShow;
import t9.subsys.oa.hr.score.logic.T9ScoreAnswerLogic;
import t9.subsys.oa.hr.score.logic.T9ScoreDataLogic;
import t9.subsys.oa.hr.score.logic.T9ScoreShowLogic;

public class T9ScoreShowAct {
  public static final String attachmentFolder = "scoreShow";
  private T9ScoreShowLogic logic = new T9ScoreShowLogic();
  
  /**
   * 获取本部门下的人员列表
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreFlowMonthData(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId = "245";//request.getParameter("flowId");
      T9ORM orm = new T9ORM();

      ArrayList<T9Person> persons = new ArrayList<T9Person>();
      String userIdStr = "";
      String moduleId = "";
      int privNoFlag = 2;
      T9MyPriv mp = new T9MyPriv();
      String userIdStrFunc = "";
      T9OrgSelect2Logic osl = new T9OrgSelect2Logic();
      mp = T9PrivUtil.getMyPriv(dbConn, person, moduleId, privNoFlag);
      if (person.getDeptId() != 0) {
        persons = osl.getDeptUser(dbConn, person.getDeptId()  , false);
      }
      HashMap map = null;
 //     ArrayList<T9Person> personList = (ArrayList<T9Person>) orm.loadListSingle(dbConn, T9Person.class, map);
      StringBuffer sb = new StringBuffer("[");
      for (T9Person per : persons) {
        int seqIds = per.getSeqId();
        if (!T9PrivUtil.isUserPriv(dbConn, seqIds, mp, person)) {
          continue;
        }
        if(seqIds == person.getSeqId()){
          continue;
        }
        sb.append("{");
        sb.append("seqId:\"" + per.getSeqId() + "\"");
        sb.append(",userId:\"" + (per.getUserId() == null ? "" : per.getUserId()) + "\"");
        sb.append(",userName:\"" + (per.getUserName() == null ? "" : per.getUserName()) + "\"");
        sb.append(",deptId:\"" + (per.getDeptId()) + "\"");
        sb.append(",userPriv:\"" + (per.getUserPriv() == null ? "" : per.getUserPriv()) + "\"");
        //sb.append(",img:\"" + "1" + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1); 

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
   * 新建--》取对象---syl  ----外办绩效考核
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String addData2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String flowId = request.getParameter("flowId");
      String groupId = request.getParameter("groupId");
      String userId = request.getParameter("userId");
      if(!T9Utility.isInteger(userId)){
        userId = user.getSeqId() + "";
      }
      int  seqId = 0;
      String dataStr = "";
      if(T9Utility.isInteger(groupId)){
        //先判断是否已经考核过了
        T9ScoreDataLogic dataLogic = new T9ScoreDataLogic();
        T9ScoreShow data = null;
        String[] str = {"GROUP_ID=" + flowId ,"PARTICIPANT = '" + userId+"'" };
        List<T9ScoreShow> dataList = dataLogic.selectData2(dbConn, str);
        if( dataList.size()>0){
          data = dataList.get(0);
          seqId = data.getSeqId();
        }else{
          data = new T9ScoreShow();
          data.setGroupId(Integer.parseInt(groupId));
          data.setParticipant(user.getSeqId()+"");
          data.setRankman(user.getSeqId()+"");
          data.setRankDate(new Date());
          seqId =  dataLogic.addData2(dbConn, data);
          data.setSeqId(seqId);
        }
        dataStr = T9FOM.toJson(data).toString();
      }
      if(dataStr.equals("")){
        dataStr = "{}";
      }
      request.setAttribute(T9ActionKeys.RET_DATA,dataStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 提交上下页--记录考核信息----外办
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  
  public String scoreData2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String quizIds = request.getParameter("quizIds");//选中的ID
      String quizIdpage = request.getParameter("quizIdpage");//当页的所有题目seqId字符串      String isSubmit =  request.getParameter("type");
      String currPage = request.getParameter("currPage");
      String groupId = request.getParameter("groupId");
      String userId = request.getParameter("userId");
      String checkFlag = request.getParameter("checkFlag");
      String checkEnd = request.getParameter("checkEnd");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String ymd = year + "-" + month;
      T9ScoreShowLogic dataLogic = new T9ScoreShowLogic();
      T9ScoreAnswerLogic answerLogic = new T9ScoreAnswerLogic();
      T9ScoreShow data = null;
      String score = "";
      String answe = "";
      String memo = "";
      int pageSize = 5;
        if(T9Utility.isInteger(groupId) && T9Utility.isInteger(userId)){
          //先判断是否已经考核过了
          
          String[] str = {"GROUP_ID=" + groupId + " and PARTICIPANT = '" + userId + "' and SCORE_TIME = '"+ymd+"'"};
          List<T9ScoreShow> dataList = dataLogic.selectData2(dbConn, str);
          if(T9Utility.isNullorEmpty(quizIds)){
            quizIds = "";
          }
          if(T9Utility.isNullorEmpty(isSubmit)){
            isSubmit = "";
          }
          if(!T9Utility.isInteger(currPage)){
            currPage = "1";
          }
          if(!T9Utility.isNullorEmpty(quizIdpage)){
            if(quizIdpage.endsWith(",")){
              quizIdpage = quizIdpage.substring(0, quizIdpage.length()-1);
            }
            String[] str2 = {"SEQ_ID in(" + quizIdpage + ")"};
            List<T9ScoreAnswer> quizList  = answerLogic.getAnswerByGroupId(dbConn, str2);//得到题目
            String[] quidArray = quizIds.split(",");//选中的ID
            for (int i = 0; i < quizList.size(); i++) {
              T9ScoreAnswer quiz = quizList.get(i);
              String isScore = " ";
              String isAnswe = " ";
              String isMemo = " ";
              isMemo = request.getParameter("memo_" + quiz.getSeqId());
              if(T9Utility.isNullorEmpty(isMemo)){
                isMemo = " ";
              }else{
                isMemo = isMemo.replace(",", "，");
              }
              String quizSeqId = quiz.getSeqId()+"";
              for (int j = 0; j < quidArray.length; j++) {
                String quidStr = quidArray[j];//seqId_A
                String[] quidStrArray =  quidStr.split("_");
                if(quidStrArray.length>=2){
                  String quidSeqId = quidStrArray[0];//得到选中的试题ID
                  String quidAnswers = quidStrArray[1];//得到选中的试题的多填写的答案
                  if(quizSeqId.equals(quidSeqId)&& !quidAnswers.equals("")){
                    if(quidAnswers.equals("A")){
                      isScore = quiz.getScoreA();
                      isAnswe = quidAnswers;
                    }
                    if(quidAnswers.equals("B")){
                      isScore = quiz.getScoreB();
                      isAnswe = quidAnswers;
                    }
                    if(quidAnswers.equals("C")){
                      isScore = quiz.getScoreC();
                      isAnswe = quidAnswers;
                    }
                    if(quidAnswers.equals("D")){
                      isScore = quiz.getScoreD();
                      isAnswe = quidAnswers;
                    }
                    if(quidAnswers.equals("E")){
                      isScore = quiz.getScoreE();
                      isAnswe = quidAnswers;
                    }
                    if(quidAnswers.equals("F")){
                      isScore = quiz.getScoreF();
                      isAnswe = quidAnswers;
                    }
                  }
                }
              }
              score = score + isScore + ",";
              answe = answe + isAnswe + ",";
              memo = memo  +  isMemo + ",";
            }
          }
          if(dataList.size() > 0){
            data = dataList.get(0);
            String oldScore = data.getScore();//得到以前的考核分数
            String oldAnswer = data.getAnswer();//得到以前的考核的答案            String oldMemo = data.getMemo();//得到以前的考核批准；            if(T9Utility.isNullorEmpty(oldScore)){
              oldScore = "";
            }
            if(T9Utility.isNullorEmpty(oldAnswer)){
              oldAnswer = "";
            }
            if(T9Utility.isNullorEmpty(oldMemo)){
              oldMemo = "";
            }
            String[] oldS = getStr(oldScore, Integer.parseInt(currPage), pageSize);//取得前面字段和后面字段            String[] oldA = getStr(oldAnswer, Integer.parseInt(currPage), pageSize);//取得前面字段和后面字段            String[] oldM = getStr(oldMemo, Integer.parseInt(currPage), pageSize);//取得前面字段和后面字段            //更新data表  
            if(T9Utility.isNullorEmpty(oldS[0])){
              if(!T9Utility.isNullorEmpty(oldS[1])){
                if(score.endsWith(",")){
                  score = score + oldS[1];
                }else{
                  score = score + ","+ oldS[1];
                }
              }
            }else{
              if(!T9Utility.isNullorEmpty(oldS[1])){
                if(score.endsWith(",")){
                  score = oldS[0] + score + oldS[1];
                }else{
                  score = oldS[0] + score + ","+ oldS[1];
                }
              }else{
                score = oldS[0] + score;
              }
            }
            if(T9Utility.isNullorEmpty(oldA[0])){
              if(!T9Utility.isNullorEmpty(oldA[1])){
                if(answe.endsWith(",")){
                  answe =  answe + oldA[1];
                }else{
                  answe =  answe + ","+ oldA[1];
                }  
              }
            }else{
              if(!T9Utility.isNullorEmpty(oldA[1])){
                if(answe.endsWith(",")){
                  answe = oldA[0] + answe + oldA[1];
                }else{
                  answe = oldA[0] + answe + ","+ oldA[1];
                }
              }else{
                answe = oldA[0] + answe;
              }
            }
            if(T9Utility.isNullorEmpty(oldM[0])){
              if(!T9Utility.isNullorEmpty(oldM[1])){
                if(memo.endsWith(",")){
                  memo =  memo + oldM[1];
                }else{
                  memo =  memo + ","+ oldM[1];
                }  
              }
            }else{
              if(!T9Utility.isNullorEmpty(oldM[1])){
                if(memo.endsWith(",")){
                  memo = oldM[0] + memo + oldM[1];
                }else{
                  memo = oldM[0] + memo + ","+ oldM[1];
                }
              }else{
                memo = oldM[0] + memo;
              }
            }
           dataLogic.updateDate2(dbConn, data.getSeqId() + "", score, answe, memo, checkEnd);  
          }else{
            data = new T9ScoreShow();
            data.setParticipant(userId);
            data.setRankman(user.getSeqId() + "");
            data.setRankDate(new Date());
            data.setAnswer(answe);
            data.setMemo(memo);
            data.setScore(score);
            data.setGroupId(Integer.parseInt(groupId));
            data.setCheckFlag(checkFlag);
            data.setCheckEnd(checkEnd);
            data.setScoreTime(ymd);
            dataLogic.addData2(dbConn, data);
          }
        }
      
      String dataStr = "{isSubmit:\"" + checkEnd + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,dataStr);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "添加数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  public String[] getStr(String str,int currPage,int pageSize){
    String[] newStr = new String[2];
    String newPre = "";
    String newEnd= "";
    if(!T9Utility.isNullorEmpty(str)){
      String[] strArray = str.split(",");
      if(strArray.length == 0){
        strArray = new String[str.length()];
      }
      int pageTemp = 0;
      pageTemp = (currPage-1)*pageSize;
      if(pageTemp>strArray.length){
        pageTemp = strArray.length;
      }
      for (int i = 0; i < pageTemp; i++) {
        String perTest = strArray[i];
        if(T9Utility.isNullorEmpty(perTest)){
          perTest = "";
        }
        newPre = newPre + perTest + ",";
      }
      for (int i = (pageTemp+pageSize); i <strArray.length; i++) {
        String endTest = strArray[i];
        if(T9Utility.isNullorEmpty(endTest)){
          endTest = "";
        }
        newEnd = newEnd  + endTest + ",";
      }
    }
    newStr[0]= newPre;
    newStr[1]= newEnd;
    return newStr;
  }
  
  /**
   * 获取手填考核数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreDataStr(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    Connection dbConn = null;
    try{
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      String flowIdStr = request.getParameter("groupId");
      String userId = request.getParameter("userId");
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(flowIdStr)){
        groupId = Integer.parseInt(flowIdStr);
      }
      T9ORM orm = new T9ORM();
      HashMap map = null;
      List<Map> list = new ArrayList();
      StringBuffer sb = new StringBuffer("[");
      String[] filters = new String[]{"GROUP_ID = " + groupId + " and RANKMAN = '" + person.getSeqId() + "' and PARTICIPANT = '" + userId + "'" };
      List funcList = new ArrayList();
      funcList.add("scoreShow");
      map = (HashMap)orm.loadDataSingle(dbConn, funcList, filters);
      list.addAll((List<Map>) map.get("SCORE_SHOW"));
      for(Map ms : list){
        String memo = (String) ms.get("memo");
        memo = T9Utility.encodeSpecial(T9Utility.null2Empty(memo));
        sb.append("{");
        sb.append("seqId:\"" + ms.get("seqId") + "\"");
        sb.append(",score:\"" + (ms.get("score") == null ? "" : ms.get("score")) + "\"");
        sb.append(",memo:\"" + memo + "\"");
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
   * 添加考核数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String addScoreData(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String groupIdStr = request.getParameter("groupId");
      String participant = request.getParameter("participant");
      String checkFlag = request.getParameter("checkFlag");
      String score = request.getParameter("score");
      String memo = request.getParameter("memo");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      String rankDate = T9Utility.getCurDateTimeStr();
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(groupIdStr)){
        groupId = Integer.parseInt(groupIdStr);
      }
      this.logic.addScoreData(dbConn, groupId, String.valueOf(person.getSeqId()), participant, score, memo, checkFlag, year, month);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      //request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 修改考核数据
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateScoreData(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupIdStr = request.getParameter("groupId");
      String participant = request.getParameter("participant");
      String checkFlag = request.getParameter("checkFlag");
      String score = request.getParameter("score");
      String memo = request.getParameter("memo");
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(groupIdStr)){
        groupId = Integer.parseInt(groupIdStr);
      }
      this.logic.updateScoreDate(dbConn, groupId, score, memo, participant, person, checkFlag);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"添加成功");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取查看工作日志、工作安排--wyw
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getGroupRefer(HttpServletRequest request,HttpServletResponse response) throws Exception{
    String groupIdStr = request.getParameter("groupId");
    int groupId = 0;
    if (!T9Utility.isNullorEmpty(groupIdStr)) {
      groupId = Integer.parseInt(groupIdStr);
    }
    Connection dbConn = null;
    try {
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getGroupReferLogic(dbConn,groupId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"获取数据成功");
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      
    }catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      throw e;
    }
    return "/core/inc/rtjson.jsp";
    
  }
  
  public String getSelectOption(HttpServletRequest request, HttpServletResponse response) throws Exception { 
    String userIdStr = request.getParameter("userId"); 
    if (T9Utility.isNullorEmpty(userIdStr)) { 
      userIdStr = ""; 
    } 
    Connection dbConn; 
    try { 
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
      dbConn = requestDbConn.getSysDbConn(); 
      String data = this.logic.getScoreGroupSelect(dbConn, userIdStr); 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功返回结果！"); 
      request.setAttribute(T9ActionKeys.RET_DATA, data); 
    } catch (Exception e) { 
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    } 
    return "/core/inc/rtjson.jsp"; 
  }
  
  /**
   * 获取groupId
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getGroupId(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    String roleId = request.getParameter("roleId");
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String data = this.logic.getGroupId(dbConn, roleId);
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
   * 获取个人考核项目分数
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreShow(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      double directorScore = this.logic.getScoreShow(dbConn, year, month, userId);
      String data = String.valueOf(directorScore);
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
   * 判断人员考核数据录入 时是否有数据 如果有data=1,没有data=0
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getOperationFlag(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      String userId = request.getParameter("userId");
      String groupIdStr = request.getParameter("groupId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(groupIdStr)){
        groupId = Integer.parseInt(groupIdStr);
      }
      boolean bool = this.logic.getOperationFlag(dbConn, person, userId, groupId, year, month);
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
   * 获取是选择考核还是手动填写考核项目 1-选择，0-手动填写
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getGroupFlag(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupIdStr = request.getParameter("groupId");
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(groupIdStr)){
        groupId = Integer.parseInt(groupIdStr);
      }
      String data = this.logic.getGroupFlag(dbConn, groupId);
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
   * 获取是选择考核还是手动填写考核项目 1-选择，0-手动填写
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCheckEnd(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupIdStr = request.getParameter("groupId");
      String userIdStr = request.getParameter("userId");
      String year = request.getParameter("year");
      String month = request.getParameter("month");
      int groupId = 0;
      if(!T9Utility.isNullorEmpty(groupIdStr)){
        groupId = Integer.parseInt(groupIdStr);
      }
      if(T9Utility.isNullorEmpty(userIdStr)){
        userIdStr = "0";
      }
      String data = this.logic.getCheckEnd(dbConn, groupId, userIdStr, year, month);
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
   * 获取考核指标集标题
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getScoreGroupName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String groupId = request.getParameter("seqId");
      int seqId = 0;
      if(!T9Utility.isNullorEmpty(groupId)){
        seqId = Integer.parseInt(groupId);
      }
      String data = this.logic.getScoreGroupName(dbConn, seqId);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + T9Utility.encodeSpecial(data) + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
}
