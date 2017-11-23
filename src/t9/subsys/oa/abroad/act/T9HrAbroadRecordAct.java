package t9.subsys.oa.abroad.act;

import java.io.PrintWriter;
import java.sql.Connection;
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
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.abroad.data.T9HrAbroadRecord;
import t9.subsys.oa.abroad.logic.T9HrAbroadRecordLogic;
import t9.subsys.oa.fillRegister.logic.T9AttendFillLogic;

public class T9HrAbroadRecordAct {
  public static final String attachmentFolder = "abroad";
  private T9HrAbroadRecordLogic logic = new T9HrAbroadRecordLogic();

  /**
   * 新建出国记录--cc
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
      T9AttendFillLogic afl = new T9AttendFillLogic();
      T9HrAbroadRecord record = (T9HrAbroadRecord) T9FOM.build(map, T9HrAbroadRecord.class, "");
      String stUserId = record.getAbroadUserId();
      String[] staffUserIdStr = stUserId.split(",");
      for(int i = 0; i < staffUserIdStr.length; i++){
        record.setCreateUserId(String.valueOf(person.getSeqId()));
        record.setCreateDeptId(person.getDeptId());
        record.setAbroadUserId(staffUserIdStr[i]);
        this.logic.add(dbConn, record);
        afl.autoAttendFill(dbConn, person, request.getParameter("beginDate"), request.getParameter("endDate"), staffUserIdStr[i], "");
      }
      
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
   * 获取出国记录管理--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getAbroadRecordListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String data = this.logic.getAbroadRecordListJson(dbConn, request.getParameterMap(), person);
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
  
  public String getUserName(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String userIdStr = request.getParameter("userIdStr");
      String data = this.logic.getUserNameLogic(dbConn, userIdStr);
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
   * 获取出国记录信息
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
      T9HrAbroadRecord record = (T9HrAbroadRecord)this.logic.getRecordDetail(dbConn, Integer.parseInt(seqId));
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
   * 编辑出国记录
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
      T9Person person = (T9Person)request.getSession().getAttribute(T9Const.LOGIN_USER);
      Map<String,String[]> map = request.getParameterMap();
      T9HrAbroadRecord record = (T9HrAbroadRecord) T9FOM.build(map, T9HrAbroadRecord.class, "");
      this.logic.updateRecord(dbConn, record);
      T9AttendFillLogic afl = new T9AttendFillLogic();
      String stUserId = record.getAbroadUserId();
      afl.autoAttendFill(dbConn, person, request.getParameter("beginDate"), request.getParameter("endDate"), stUserId, "");
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
	 * 出国记录查询 --wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryAbroadRecordListJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("abroadUserId", T9DBUtility.escapeLike(request.getParameter("abroadUserId")));
			map.put("abroadName", T9DBUtility.escapeLike(request.getParameter("abroadName")));
			map.put("beginDate", T9DBUtility.escapeLike(request.getParameter("beginDate")));
			map.put("beginDate1", T9DBUtility.escapeLike(request.getParameter("beginDate1")));
			map.put("endDate", T9DBUtility.escapeLike(request.getParameter("endDate")));
			map.put("endDate1", T9DBUtility.escapeLike(request.getParameter("endDate1")));
			map.put("remark", T9DBUtility.escapeLike(request.getParameter("remark")));
			String data = "";
			data = this.logic.queryAbroadRecordListJsonLogic(dbConn, request.getParameterMap(), map, person);
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
  
	public String showTime(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String startDateStr = request.getParameter("beginDate");
      String endDateStr = request.getParameter("endDate");
      String dayList = this.logic.getDayList(dbConn, startDateStr, endDateStr);
      String data = "";
      request.setAttribute(T9ActionKeys.RET_DATA, "\"" + data + "\"");
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK); 
      request.setAttribute(T9ActionKeys.RET_MSRG, "成功添加"); 
    } catch (Exception e) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR); 
      request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage()); 
      throw e; 
    }
    return "/core/inc/rtjson.jsp";
  }
  
	public String showMonth(HttpServletRequest request,
      HttpServletResponse response) throws Exception{
    
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn)request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String beginDate = request.getParameter("beginDate");
      String endDate = request.getParameter("endDate");
      String data = "";
      T9AttendFillLogic afl = new T9AttendFillLogic();
      data = afl.showTimeStr(dbConn, beginDate, endDate);
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
  
}
