package t9.core.funcs.attendance.personal.act;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.attendance.personal.data.T9AttendMobile;
import t9.core.funcs.attendance.personal.logic.T9AttendMobileLogic;
import t9.core.funcs.calendar.info.logic.T9InfoLogic;
import t9.core.global.T9ActionKeys;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;

public class T9AttendMobileAct {
	/**
	 * 查询在一段时间内手机考勤登记信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getUserMobileInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection dbConn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			String userId = request.getParameter("userId");// 得到指定用户的ID
			String userName = T9InfoLogic.getUserName(userId, dbConn);
			String days = request.getParameter("days");// 得到指定的所有日期
			String[] dayArray = days.split(",");
			List<T9AttendMobile> mobileList = new ArrayList<T9AttendMobile>();

			// 对日期循环
			for (int i = 0; i < dayArray.length; i++) {
				Date dateTemp = T9Utility.parseDate(dayArray[i]);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dateTemp);
				// 查出当天有没有考勤 记录
				List<T9AttendMobile> mobile = getAttendMobile(request,
						response, dayArray[i], userId);
				mobileList.addAll(mobile);
			}
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_OK);
			request.setAttribute(T9ActionKeys.RET_MSRG, userName);
			request.setAttribute(T9ActionKeys.RET_DATA,
					T9MobileUtility.list2Json(mobileList));
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return "/core/inc/rtjson.jsp";
	}

	/**
	 * 得到一天的登记情况
	 * 
	 * @param request
	 * @param response
	 * @param date
	 *            时间字符串
	 * 
	 * @param registerType
	 *            登记类型(1-6)
	 * @param userId
	 *            登记人SeqId
	 * @param config
	 *            排版类型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public List<T9AttendMobile> getAttendMobile(HttpServletRequest request,
			HttpServletResponse response, String date, String userId)
			throws Exception {
		Connection dbConn = null;
		List<T9AttendMobile> mobileList = new ArrayList<T9AttendMobile>();
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			dbConn = requestDbConn.getSysDbConn();
			// 得到指定当天登记的记录

			T9DBUtility t9dbu = new T9DBUtility();
			T9AttendMobileLogic logic = new T9AttendMobileLogic();
			String date1 = date + " 00:00:00";
			String date2 = date + " 23:59:59";
			date1 = t9dbu.getDateFilter("M_TIME", date1, ">=");
			date2 = t9dbu.getDateFilter("M_TIME", date2, "<=");
			mobileList = logic.selectMobile(dbConn, String.valueOf(userId),
					date1, date2);
		} catch (Exception ex) {
			request.setAttribute(T9ActionKeys.RET_STATE, T9Const.RETURN_ERROR);
			request.setAttribute(T9ActionKeys.RET_MSRG, ex.getMessage());
			throw ex;
		}
		return mobileList;
	}

}
