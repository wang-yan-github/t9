package t9.subsys.oa.examManage.act;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import t9.core.data.T9DbRecord;
import t9.core.data.T9RequestDbConn;
import t9.core.funcs.jexcel.util.T9JExcelUtil;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.examManage.data.T9ExamData;
import t9.subsys.oa.examManage.data.T9ExamFlow;
import t9.subsys.oa.examManage.data.T9ExamPaper;
import t9.subsys.oa.examManage.logic.T9ExamFlowLogic;

public class T9ExamFlowAct {
  /**
   *查询所有(分页)通用列表显示数据--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
      String dayTime = sf.format(new Date());
      String data = T9ExamFlowLogic.selectFlow(dbConn, request.getParameterMap(),dayTime);
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
   * 新建--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String add(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ExamFlow flow = (T9ExamFlow)T9FOM.build(request.getParameterMap());

      if (flow.getBeginDate() == null) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        flow.setBeginDate(T9Utility.parseSqlDate(sf.format(new Date())));
      }
      T9ExamFlowLogic.add(dbConn, flow);//添加数据

      String smsSJ = request.getParameter("smsSJ");//手机短信
      String smsflag = request.getParameter("smsflag");//内部短信
      if (smsflag.equals("1")) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("36");
        sb.setContent("请查看考试信息！\n标题：" + flow.getFlowTitle());
        sb.setSendDate(new java.util.Date());
        sb.setFromId(person.getSeqId());
        sb.setToId(flow.getParticipant());
        sb.setRemindUrl("/subsys/oa/examManage/examOnline/index.jsp&openFlag=1&openWidth=820&openHeight=600");
        T9SmsUtil.smsBack(dbConn,sb);
      }
      //手机消息提醒
      if (smsSJ.equals("1")) {
        T9MobileSms2Logic sb2 = new T9MobileSms2Logic();
        sb2.remindByMobileSms(dbConn,flow.getParticipant(),person.getSeqId(),"请查看考试信息！\n标题：" + flow.getFlowTitle() ,new java.util.Date());
      }
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
   * 修改--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      T9ExamFlow flow = (T9ExamFlow)T9FOM.build(request.getParameterMap());

      if (flow.getBeginDate() == null) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        flow.setBeginDate(T9Utility.parseSqlDate(sf.format(new Date())));
      }
      T9ExamFlowLogic.updateFlow(dbConn,flow);//修改数据

      String smsSJ = request.getParameter("smsSJ");//手机短信
      String smsflag = request.getParameter("smsflag");//内部短信
      if (smsflag.equals("1")) {
        T9SmsBack sb = new T9SmsBack();
        sb.setSmsType("36");
        sb.setContent("请查看考试信息！\n标题：" + flow.getFlowTitle());
        sb.setSendDate(new java.util.Date());
        sb.setFromId(person.getSeqId());
        sb.setToId(flow.getParticipant());
        sb.setRemindUrl("/subsys/oa/examManage/examOnline/index.jsp&openFlag=1&openWidth=820&openHeight=600");
        T9SmsUtil.smsBack(dbConn,sb);
      }
      //手机消息提醒
      if (smsSJ.equals("1")) {
        T9MobileSms2Logic sb2 = new T9MobileSms2Logic();
        sb2.remindByMobileSms(dbConn,flow.getParticipant(),person.getSeqId(),"请查看考试信息！\n标题：" + flow.getFlowTitle() ,new java.util.Date());
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 删除--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ExamFlowLogic.deleteFlow(dbConn,seqId);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "删除成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 查询--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showFlow(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        T9ExamFlow flow = (T9ExamFlow)T9ExamFlowLogic.showFlow(dbConn,seqId);
        //定义数组将数据保存到Json中
        String data = "";
        if(flow != null) {
          data = data + T9FOM.toJson(flow);
          data = data.replaceAll("\\n", "");
          data = data.replaceAll("\\r", "");
        }
        data = data + "";
        if(data.equals("")){
          data = "{}";
        }
        //保存查询数据是否成功，保存date
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 参加考试人员查询--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showMan(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String participant = request.getParameter("participant");
      if (!T9Utility.isNullorEmpty(participant)) {
        //String str[] = { " SEQ_ID in (" + participant +") " };
        List<T9Person> list = T9ExamFlowLogic.showMan(dbConn, participant);
        //定义数组将数据保存到Json中
        String data = "[";
        T9Person person = new T9Person(); 
        for (int i = 0; i < list.size(); i++) {
          person = list.get(i);
          data = data + T9FOM.toJson(person).toString()+",";
        }
        if(list.size() > 0){
          data = data.substring(0, data.length() - 1);
        }
        data = data + "]";
        //保存查询数据是否成功，保存date
        request.setAttribute(T9ActionKeys.RET_DATA, data);
      }
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询数据成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  /**
   * 立即终止--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String seqId = request.getParameter("seqId");
      if (!T9Utility.isNullorEmpty(seqId)) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date());
        T9ExamFlowLogic.updateStatus(dbConn, seqId,time);
      }
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 导出考试信息--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String excelReport(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      OutputStream ops = null;
      String fileName = URLEncoder.encode("导出分数.xls","UTF-8");
      fileName = fileName.replaceAll("\\+", "%20");
      response.setHeader("Cache-control","private");
      response.setContentType("application/vnd.ms-excel");
      response.setHeader("Accept-Ranges","bytes");
      response.setHeader("Cache-Control","maxage=3600");
      response.setHeader("Pragma","public");
      
      response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
      ops = response.getOutputStream();

      int sunGrade = 0;
      int count = 0;
      List<T9ExamData> list = new ArrayList<T9ExamData>();
      String seqId = request.getParameter("seqId");
      String paperId = request.getParameter("paperId");
      if (!T9Utility.isNullorEmpty(paperId)) {
        T9ExamPaper paper = T9ExamFlowLogic.selectPaper(dbConn,paperId);
        if (paper != null) {
          sunGrade = paper.getPaperGrade();
          count = paper.getQuestionsCount();
          //sunGrade = paper.getPaperGrade() / paper.getQuestionsCount();//每题多少分
          // System.out.println("SEQ_ID：" + paper.getSeqId() + "  试卷总分：" + paper.getPaperGrade() + "  题数：" + paper.getQuestionsCount() + " 每题：" + sunGrade);
          if (!T9Utility.isNullorEmpty(seqId)) {
            String str[] = { "FLOW_ID = " + seqId + "  and EXAMED=1 " };
            list = T9ExamFlowLogic.selectListData(dbConn,str);
          }
        }
      }
      ArrayList<T9DbRecord> dbL = T9ExamFlowLogic.getDbRecord(dbConn,list,sunGrade,count);
      T9JExcelUtil.writeExc(ops, dbL);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导出数据成功！");
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, "导出数据失败");
      throw e;
    }
    return null;
  }

  /**
   *查询所有(分页)通用列表显示数据--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectList(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowTitle = request.getParameter("flowTitle");
      String participant = request.getParameter("participant");
      String paperId = request.getParameter("paperId");
      String beginDate = request.getParameter("beginDate");
      String beginDate1 = request.getParameter("beginDate1");
      String endDate = request.getParameter("endDate");
      String endDate1 = request.getParameter("endDate1");
      String cd = request.getParameter("cd");

      T9ExamFlow flow = new T9ExamFlow();
      flow.setFlowTitle(flowTitle);
      flow.setParticipant(participant);
      if (!T9Utility.isNullorEmpty(paperId)) {
        flow.setPaperId(Integer.parseInt(paperId));
      }
      if (!T9Utility.isNullorEmpty(beginDate)) {
        flow.setBeginDate(T9Utility.parseDate(beginDate));
      }
      if (!T9Utility.isNullorEmpty(endDate)) {
        flow.setEndDate(T9Utility.parseDate(endDate));
      }
      String data = T9ExamFlowLogic.selectList(dbConn,request.getParameterMap(),flow,beginDate1,endDate1,cd);
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
   *查询所有(分页)通用列表显示数据--lz (考试结果统计)
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectQIZ(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paperId = request.getParameter("paperId");
      String flowId = request.getParameter("flowId");
      String questionsList = T9ExamFlowLogic.selectQuestionsList(dbConn,paperId);
      //System.out.println(questionsList);
      String data = T9ExamFlowLogic.selectQIZ(dbConn,request.getParameterMap(),paperId,questionsList);
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
   *取试卷标题--lz 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showTitle(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String paperId = request.getParameter("paperId");
      String data = "";
      if (!T9Utility.isNullorEmpty(paperId)) {
        T9ExamPaper paper = T9ExamFlowLogic.showTitle(dbConn,paperId);
        //定义数组将数据保存到Json中
        data = "";
        if(paper != null) {
          data = data + T9FOM.toJson(paper);
          data = data.replaceAll("\\n", "");
          data = data.replaceAll("\\r", "");
        }
        data = data + "";
        if(data.equals("")){
          data = "{}";
        }
      }
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

  /**
   * 参加考试人员查询--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showMan2(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      int sunGrade = 0;
      int count = 0;
      List<T9ExamData> list = new ArrayList<T9ExamData>();
      String seqId = request.getParameter("seqId");
      String paperId = request.getParameter("paperId");
      if (!T9Utility.isNullorEmpty(paperId)) {
        T9ExamPaper paper = T9ExamFlowLogic.selectPaper(dbConn,paperId);
        if (paper != null) {
          //sunGrade = paper.getPaperGrade() / paper.getQuestionsCount();//每题多少分
          sunGrade = paper.getPaperGrade();
          count = paper.getQuestionsCount();
          if (!T9Utility.isNullorEmpty(seqId)) {
            String str[] = { "FLOW_ID = " + seqId + "  and EXAMED=1 " };
            list = T9ExamFlowLogic.selectListData(dbConn,str);
          }
        }
      }
      List<T9ExamFlow> listMan = T9ExamFlowLogic.showMan2(dbConn,list,sunGrade,count);
      //定义数组将数据保存到Json中
      String data = "[";
      T9ExamFlow flow = new T9ExamFlow(); 
      for (int i = 0; i < listMan.size(); i++) {
        flow = listMan.get(i);
        data = data + T9FOM.toJson(flow).toString() + ",";
      }
      if(list.size() > 0){
        data = data.substring(0, data.length() - 1);
      }
      data = data + "]";
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


  /**
   * 答题次数--lz
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showCount(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowId = request.getParameter("flowId");
      String data = "0";
      if (!T9Utility.isNullorEmpty(flowId)) {
        data = T9ExamFlowLogic.showCount(dbConn,Integer.parseInt(flowId));
      }
      data = "{showCount:\"" + T9Utility.encodeSpecial(data) + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   * 答题次数错误次数--lz
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String showCountFalse(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String flowId = request.getParameter("flowId");
      String recordIndex = request.getParameter("recordIndex");

      List<T9ExamData> list = new ArrayList<T9ExamData>();
      T9ExamData examData = new T9ExamData();
      //T9ExamPaper paper = T9ExamFlowLogic.selectPaper(dbConn,paperId);
      int sunGrade = 0;
      int count = 0;   
      //      if (paper != null) {
      //        sunGrade = paper.getQuestionsCount();//总题数
      //      }
      if (!T9Utility.isNullorEmpty(flowId)) {
        String str[] = { "FLOW_ID = " + flowId + "  and EXAMED=1 " };
        list = T9ExamFlowLogic.selectListData(dbConn,str);//多少人答题,以及答题错误
        for (int i = 0; i < list.size(); i ++) {
          examData = list.get(i);  
          String scode = examData.getScore();
          String[] scodeArray = null;
          if (!T9Utility.isNullorEmpty(scode)) {
            scodeArray = scode.split(",");
            if(Integer.parseInt(recordIndex) + 1 > scodeArray.length){
              count ++ ;
            } else {
              for (int j = 0 ; j < scodeArray.length; j ++) {
                if (scodeArray[Integer.parseInt(recordIndex)].equals("0")) {
                  count ++ ;
                  break;
                }
              }
            }
          }
        }
      }
      String data = "{showCountFalse:\"" + count + "\"}";
      request.setAttribute(T9ActionKeys.RET_DATA,data);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功！");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }

  /**
   *查询所有(分页)通用列表显示数据--syl 考试在线
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String selectFlowToOnLine(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
      .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
      T9Person user = (T9Person) request.getSession().getAttribute(
          T9Const.LOGIN_USER);
      String dayTime = sf.format(new Date());
      String data = T9ExamFlowLogic.selectFlowOnLine(dbConn, request.getParameterMap(),dayTime,user.getSeqId()+"");
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
}
