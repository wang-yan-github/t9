package t9.subsys.oa.meeting.act;

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
import t9.subsys.oa.meeting.data.T9MeetingEquipment;
import t9.subsys.oa.meeting.logic.T9MeetingEquipmentLogic;

public class T9MeetingEquipmentAct {

	private T9MeetingEquipmentLogic logic = new T9MeetingEquipmentLogic();
	
	/**
	 * 设备管理列表--cc
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getMeetingEquipmentList(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;

    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String cycleNo = request.getParameter("cycleNo");
      String flag = request.getParameter("flag");
      String data = this.logic.getMeetingEquipmentList(dbConn, request.getParameterMap(), cycleNo);
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
   * 获取小编码表内容--同类设备名称--cc
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getCodeName(HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request
          .getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      String classCode = request.getParameter("classCode");
      String data = this.logic.getCodeNameLogic(dbConn, classCode);
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
      request.setAttribute(T9ActionKeys.RET_DATA,"\"" + data + "\"");
    } catch (Exception ex) {
      request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
      request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
      throw ex;
    }
    return "/core/inc/rtjson.jsp";
  }
  
  /**
   * 删除单个设备管理--cc
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String deleteSingle(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String seqId = request.getParameter("seqId");
    Connection dbConn = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
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
   * 会议查询 -cc
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  public String getSearchMeetingEquipment(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Connection dbConn = null;
    ArrayList<T9Address> addressList = null;
    try {
      T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
      dbConn = requestDbConn.getSysDbConn();
      T9Person person = (T9Person) request.getSession().getAttribute(T9Const.LOGIN_USER);
      String equipmentNo = T9DBUtility.escapeLike(request.getParameter("equipmentNo"));
      String equipmentName = T9DBUtility.escapeLike(request.getParameter("equipmentName"));
      String equipmentStatus = T9DBUtility.escapeLike(request.getParameter("equipmentStatus"));
      String mrId = T9DBUtility.escapeLike(request.getParameter("mrId"));
      String remark = T9DBUtility.escapeLike(request.getParameter("remark"));
      String data = "";
      data = this.logic.getMeetingEquiomentSearchJson(dbConn, request.getParameterMap(), equipmentNo, equipmentName, equipmentStatus, mrId, remark);
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
	 * 获取所属会议室
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getMRoomName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			String data = this.logic.getMRoomNameLogic(dbConn);

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
	 * 添加设备信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String addEquipment(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();

			Map<String, String[]> map = request.getParameterMap();
			T9MeetingEquipment equipment = (T9MeetingEquipment) T9FOM.build(map, T9MeetingEquipment.class, "");

			this.logic.addEquipmentLogic(dbConn, equipment);

		} catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}

		return "/core/inc/rtjson.jsp";
	}
	
	
	/**
	 * 根据会议室seqId获取设备信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getEquipmentById(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String mRoomStr = request.getParameter("mRoom");
		int mRoom=0;
		if (!T9Utility.isNullorEmpty(mRoomStr)) {
			mRoom=Integer.parseInt(mRoomStr);
		}
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data=this.logic.getEquipmentByIdLogic(dbConn, mRoom);
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
	 * 获取下拉列表值	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getSelectOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String parentNo = request.getParameter("parentNo");
		String optionType = request.getParameter("optionType");
		if (T9Utility.isNullorEmpty(parentNo)) {
			parentNo = "";
		}
		if (T9Utility.isNullorEmpty(optionType)) {
			optionType = "";
		}
		Connection dbConn;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String data = this.logic.getSelectOption(dbConn, parentNo);
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
	 * 根据seqId获取设备信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getEquipmentInfoById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seqIdStr = request.getParameter("seqId");
		int seqId = 0;
		if (!T9Utility.isNullorEmpty(seqIdStr)) {
			seqId = Integer.parseInt(seqIdStr);
		}
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			T9MeetingEquipment equipment=this.logic.getEquipmentLogic(dbConn, seqId);
			if (equipment == null) {
				request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
				request.setAttribute(T9ActionKeys.RET_MSRG, "会议设备信息不存在");
				return "/core/inc/rtjson.jsp";
			}
			StringBuffer data = T9FOM.toJson(equipment);
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
	 * 更新设备信息--wyw
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateEquipmentById(HttpServletRequest request,HttpServletResponse response) throws Exception{
			Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			Map<String, String[]> map = request.getParameterMap();
			T9MeetingEquipment equipment = (T9MeetingEquipment) T9FOM.build(map, T9MeetingEquipment.class, "");
			this.logic.updateEquipmentLogic(dbConn,equipment);
			
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, "查询成功");
		}catch (Exception e) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, e.getMessage());
			throw e;
		}
		return "/core/inc/rtjson.jsp";
	}
}
