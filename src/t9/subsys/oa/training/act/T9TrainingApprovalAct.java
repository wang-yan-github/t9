package t9.subsys.oa.training.act;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.mobilesms.logic.T9MobileSms2Logic;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.training.data.T9HrTrainingPlan;
import t9.subsys.oa.training.logic.T9TrainingApprovalLogic;
public class T9TrainingApprovalAct {
	
	public static final String attachmentFolder = "training";
	private T9TrainingApprovalLogic logic = new T9TrainingApprovalLogic();

	/**
   * 培训计划名称-模糊查找--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String getTrainingApprovalJson(HttpServletRequest request,  HttpServletResponse response) throws Exception, SQLException{    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;   
    try{
      dbConn = requestDbConn.getSysDbConn();
      String search = request.getParameter("condition");
        search = T9Utility.decodeURL(search); //解码
      if(T9Utility.isNullorEmpty(search)){
        search = "";
      }
      String userId = request.getParameter("userId");
      StringBuffer sb = new StringBuffer("[");
      T9Person user = null;
      if(!T9Utility.isNullorEmpty(userId)){
        user = new T9Person();
        user.setSeqId(Integer.parseInt(userId));//从页面中传过来的用户信息
      }else{
        user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      }
      List<T9HrTrainingPlan> hrTrainingPlanList = this.logic.findTrainingPlanNo(dbConn, user, search);
      
      for(int i = 0; i < hrTrainingPlanList.size(); i++){
        T9HrTrainingPlan plan = hrTrainingPlanList.get(i);
        sb.append("{");
        sb.append("seqId:\"" +  plan.getSeqId() + "\"");
        sb.append(",tPlanNo:\"" + (plan.getTPlanNo() == null ? "" : T9Utility.encodeSpecial(plan.getTPlanNo()))+ "\"");
        sb.append(",tPlanName:\"" + (plan.getTPlanName() == null ? "" : T9Utility.encodeSpecial(plan.getTPlanName()))+ "\"");
        sb.append(",tInstitutionName:\"" + (plan.getTInstitutionName() == null ? "" : T9Utility.encodeSpecial(plan.getTInstitutionName())) + "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (hrTrainingPlanList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());    
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 获取受训人选择框的用户信息--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   * @throws SQLException
   */
  public String getTrainingUserSelectJson(HttpServletRequest request,  HttpServletResponse response) throws Exception, SQLException{    
    T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR); 
    Connection dbConn = null;   
    try{
      dbConn = requestDbConn.getSysDbConn();
      String search = request.getParameter("condition");
        search = T9Utility.decodeURL(search); //解码
      if(T9Utility.isNullorEmpty(search)){
        search = "";
      }
      String userId = request.getParameter("userId");
      StringBuffer sb = new StringBuffer("[");
      T9Person user = null;
      if(!T9Utility.isNullorEmpty(userId)){
        user = new T9Person();
        user.setSeqId(Integer.parseInt(userId));//从页面中传过来的用户信息
      }else{
        user = (T9Person)request.getSession().getAttribute("LOGIN_USER");
      }
      List<T9Person> personList = this.logic.findTrainingUserSelect(dbConn, user, search);
      
      for(int i = 0; i < personList.size(); i++){
        T9Person plan = personList.get(i);
        sb.append("{");
        sb.append("seqId:\"" + plan.getSeqId() + "\"");
        sb.append(",userId:\"" +plan.getUserId()+ "\"");
        sb.append(",userName:\"" +plan.getUserName()+ "\"");
        sb.append("},");
      }
      sb.deleteCharAt(sb.length() - 1);
      if (personList.size() == 0) {
        sb = new StringBuffer("[");
      }
      sb.append("]");
      request.setAttribute(T9ActionKeys.RET_DATA, sb.toString());    
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    } catch (Exception e){
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
      request.setAttribute(T9ActionKeys.FORWARD_PATH, "/core/inc/error.jsp");
      throw e;
    }
    return "/core/inc/rtjson.jsp";
  }

  
  /**
   *培训计划审批通用列表 --cc
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTrainingApprovalListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String mStatus = request.getParameter("assessingStatus");
      String data = this.logic.getTrainingApprovalListJson(dbConn, request.getParameterMap(), mStatus, person);
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
   * 培训计划详情 --cc
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getPlanDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9HrTrainingPlan meeting = (T9HrTrainingPlan) this.logic.getPlanDetail(dbConn, Integer.parseInt(seqId));
      if (meeting == null) {
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
        request.setAttribute(T9ActionKeys.RET_MSRG, "未找到相应记录");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(meeting);
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
   * 审批人名称 --cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userId = request.getParameter("userId");
      String data = this.logic.getUserNameLogic(dbConn, Integer.parseInt(userId));
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
   * 会议审批通用方法 --cc
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String seqId2 = request.getParameter("seqId");
      int seqId = 0;
      if (!T9Utility.isNullorEmpty(seqId2)) {
        seqId = Integer.parseInt(seqId2);
      }
      String assessingStatus = request.getParameter("assessingStatus");
      String assessingView = request.getParameter("assessingView");
      String content = "";
      if ("1".equals(assessingStatus)) {
        content = "审批通过";
        assessingView = "<font color='green'>批准</font> <b>by " + person.getUserName() + " " +T9Utility.getCurDateTimeStr() +"</b><br/>" + assessingView;
        
      } else {
        content = "审批未通过";
        assessingView = "<font color='green'>驳回</font> <b>by " + person.getUserName() + " " +T9Utility.getCurDateTimeStr() +"</b><br/>" + assessingView;
      }
      //this.logic.updateStatus(dbConn, seqId, assessingStatus, assessingView);
      Map m =new HashMap();
      Date time = new Date();
      m.put("seqId", seqId);
      m.put("assessingStatus", assessingStatus);
      m.put("assessingTime", T9Utility.getCurDateTimeStr());//T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",T9Utility.getCurDateTimeStr())
      m.put("assessingView", assessingView);
      T9ORM orm = new T9ORM();
      orm.updateSingle(dbConn, "hrTrainingPlan", m);
      
      
      //短信提醒
      T9HrTrainingPlan trainingPlan = (T9HrTrainingPlan) orm.loadObjSingle(dbConn, T9HrTrainingPlan.class, seqId);
      T9SmsBack sb = new T9SmsBack();
      T9MobileSms2Logic sbl = new T9MobileSms2Logic();
      String remindUrl = "";
      String smsContent = "";
      if ("1".equals(assessingStatus)) {
        remindUrl = "/subsys/oa/training/plan/planManage.jsp?openFlag=1&openWidth=860&openHeight=650";
        smsContent = person.getUserName() + " 已审批通过您的培训计划 " + trainingPlan.getTPlanName() + "。";
        this.logic.doSmsBackTime(dbConn, smsContent, person.getSeqId(), trainingPlan.getCreateUserId(), "61", remindUrl, new java.util.Date());
        
      }
      if ("1".equals(assessingStatus)) {
        smsContent = person.getUserName() + " 已审批通过您的培训计划 " + trainingPlan.getTPlanName() + "。";
        sbl.remindByMobileSms(dbConn, trainingPlan.getCreateUserId(), person.getSeqId(), smsContent, new java.util.Date());
      }
      
      if ("2".equals(assessingStatus)) {
        remindUrl = "/subsys/oa/training/plan/planManage.jsp?openFlag=1&openWidth=860&openHeight=650";
        smsContent = person.getUserName() + " 已驳回您的培训计划 " + trainingPlan.getTPlanName() + "。";
        this.logic.doSmsBackTime(dbConn, smsContent, person.getSeqId(), trainingPlan.getCreateUserId(), "61", remindUrl, new java.util.Date());
      }
      if ("2".equals(assessingStatus)) {
        smsContent = person.getUserName() + " 已驳回您的培训计划 " + trainingPlan.getTPlanName() + "。";
        sbl.remindByMobileSms(dbConn, trainingPlan.getCreateUserId(), person.getSeqId(), smsContent, new java.util.Date());
      }

      String data = assessingStatus;
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG, "修改成功！");
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 培训计划(审批)查询--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTrainingApprovalSearchList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String tPlanName = T9DBUtility.escapeLike(request.getParameter("tPlanName"));
      String tChannel = T9DBUtility.escapeLike(request.getParameter("tChannel"));
      String assessingOfficer = T9DBUtility.escapeLike(request.getParameter("assessingOfficer"));
      String assessingStatus = T9DBUtility.escapeLike(request.getParameter("assessingStatus"));
      String beginDate = T9DBUtility.escapeLike(request.getParameter("beginDate"));
      String endDate = T9DBUtility.escapeLike(request.getParameter("endDate"));
      String data = "";
      data = this.logic.getTrainingApprovalSearchList(dbConn, request.getParameterMap(), person, tPlanName, tChannel, assessingOfficer, assessingStatus, beginDate, endDate);
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
