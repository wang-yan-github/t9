package t9.subsys.oa.training.act;


import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.address.data.T9Address;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.training.data.T9HrTrainingRecord;
import t9.subsys.oa.training.logic.T9TrainingRecordLogic;

public class T9TrainingRecordAct {
	
	public static final String attachmentFolder = "training";
	private T9TrainingRecordLogic logic = new T9TrainingRecordLogic();
	
	/**
	 * 新建培训记录--cc
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addRecord(HttpServletRequest request,
	     HttpServletResponse response) throws Exception{
	    
	   Connection dbConn = null;
	   try {
	     T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
	     dbConn = requestDbConn.getSysDbConn();
	     T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
	     Map<String,String[]> map = request.getParameterMap();
	     T9HrTrainingRecord record = (T9HrTrainingRecord) T9FOM.build(map, T9HrTrainingRecord.class, "");
	     int countFlag = 0;
	     String stUserId = record.getStaffUserId();
	     String[] staffUserIdStr = stUserId.split(",");
	     for(int i = 0; i < staffUserIdStr.length; i++){
	       if(!this.logic.existsTrainingRecord(dbConn, staffUserIdStr[i], record.getTPlanNo())){
	         record.setCreateUserId(String.valueOf(person.getSeqId()));
	         record.setCreateDeptId(person.getDeptId());
	         record.setStaffUserId(staffUserIdStr[i]);
	         this.logic.add(dbConn, record);
	       }else{
	         countFlag++;
	       }
	     }
	     String data = String.valueOf(countFlag);
	     request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
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
	 * 培训记录信息 列表--cc
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getTrainingRecordListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getTrainingRecordListJson(dbConn, request.getParameterMap(), person);
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
   * 获取用户名称 -cc
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
  
  /**
   * 批量删除--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteAll(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
    
      String seqIdStr = request.getParameter("sumStrs");
      this.logic.deleteAll(dbConn, seqIdStr);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_MSRG,"成功取出数据");
    }catch(Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 培训记录详细信息--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRecordDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {

    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9HrTrainingRecord meeting = (T9HrTrainingRecord) this.logic.getRecordDetail(dbConn, Integer.parseInt(seqId));
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
   * 获取培训记录管理列表--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getTrainingRecordSearchList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String userId = T9DBUtility.escapeLike(request.getParameter("userId"));
      String tPlanNo = T9DBUtility.escapeLike(request.getParameter("tPlanNo"));
      String tInstitutionName = T9DBUtility.escapeLike(request.getParameter("tInstitutionName"));
      String trainningCost = T9DBUtility.escapeLike(request.getParameter("trainningCost"));
      String data = "";
      data = this.logic.getTrainingRecordSearchList(dbConn, request.getParameterMap(), person, userId, tPlanNo, tInstitutionName, trainningCost);
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
   * 获取培训记录信息(编辑)--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getRecordInfoDetail(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      if(T9Utility.isNullorEmpty(seqId)){
        seqId = "0";
      }
      T9HrTrainingRecord record = (T9HrTrainingRecord)this.logic.getRecordDetail(dbConn, Integer.parseInt(seqId));
      if (record == null){
        request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
        request.setAttribute(T9ActionKeys.RET_MSRG, "培训记录不存在");
        return "/core/inc/rtjson.jsp";
      }
      StringBuffer data = T9FOM.toJson(record);
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
   * 编辑培训记录--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String updateRecord(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      Map<String,String[]> map = request.getParameterMap();
      T9HrTrainingRecord record = (T9HrTrainingRecord) T9FOM.build(map, T9HrTrainingRecord.class, "");
      this.logic.updateRecord(dbConn, record);
      
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
}
