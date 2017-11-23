package t9.mobile.attendance.act;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.data.T9RequestDbConn;
import t9.core.funcs.person.data.T9Person;
import t9.core.global.T9BeanKeys;
import t9.core.global.T9Const;
import t9.core.util.db.T9DBUtility;
import t9.mobile.attendance.logic.T9PdaAttendanceLogic;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaAttendanceAct {

	public String data(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(
					T9Const.LOGIN_USER);
			conn = requestDbConn.getSysDbConn();

			String ATYPE = request.getParameter("ATYPE");
			String A = request.getParameter("A");
			String DATE = request.getParameter("DATE");

			String query = "";
			int userId = person.getSeqId();
			T9PdaAttendanceLogic logic = new T9PdaAttendanceLogic();

			if ("refreshList".equals(ATYPE)) {
				if ("loadList".equals(A)) {
					query = "SELECT * FROM `attend_mobile` where M_UID= '" + userId +"'";
					List data= logic.getAttend(conn,request,query);
					T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
				}
			}else if("getDetail".equals(ATYPE)){
				T9DBUtility t9dbu = new T9DBUtility();
				String date1 = DATE.trim() + " 00:00:00";
				String date2 = DATE.trim() + " 23:59:59";
				date1 = t9dbu.getDateFilter("M_TIME", date1, ">=");
				date2 = t9dbu.getDateFilter("M_TIME", date2, "<=");
				query = "SELECT * FROM `attend_mobile` where M_UID= '" + userId +"'";
				if(!DATE.equals("")){
					query += " and " + date1;
					query += " and " + date2;
				}
				List data= logic.getAttend(conn,request,query);
				T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, null, T9MobileUtility.list2Json(data)));
			}
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 添加个人考勤
	 * @param request
	 *		TIME		'创建时间'
	 *		LNG			'地理坐标经度'
	 *		LAT			'地理坐标纬度'
	 *		LOCATION	'定位地点名称'
	 *		REMARK		'备注'
	 *		ISFOOT		'是否为足迹'
	 * @param response
	 * 		rtMsrg		添加成功/添加失败
	 * @return
	 */
	public String add(HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			T9Person person = (T9Person) request.getSession().getAttribute(
					T9Const.LOGIN_USER);
			conn = requestDbConn.getSysDbConn();
			boolean flag = true;

			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String nowDate = sf.format(new Date()); // 今天的日期
			Timestamp t = new Timestamp(new Date().getTime());

			String TIME = request.getParameter("TIME");
			if (T9MobileString.isEmpty(TIME)) { // 是否等于空
				flag = false;
				T9MobileUtility.output(response, T9MobileUtility.getResultJson(1, "请填写签到时间", null));
			} else {
				TIME = nowDate + " " + TIME + ":00";
				t = new Timestamp(sf.parse(TIME).getTime());
			}
			String LNG = request.getParameter("LNG");
			LNG = T9MobileString.showNull(LNG);
			String LAT = request.getParameter("LAT");
			LAT = T9MobileString.showNull(LAT);
			String LOCATION = request.getParameter("LOCATION");
			LOCATION = T9MobileString.showNull(LOCATION);
			String REMARK = request.getParameter("REMARK");
			REMARK = T9MobileString.showNull(REMARK);
			String ISFOOT = request.getParameter("ISFOOT");
			int userId = person.getSeqId();

			T9PdaAttendanceLogic logic = new T9PdaAttendanceLogic();
			if(flag){
				logic.addAttend(request,response,conn,t,LNG,LAT,LOCATION,REMARK,ISFOOT,userId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String delete(HttpServletRequest request,HttpServletResponse response){
		Connection conn = null;
		try {
			T9RequestDbConn requestDbConn = (T9RequestDbConn) request
					.getAttribute(T9BeanKeys.REQUEST_DB_CONN_MGR);
			conn = requestDbConn.getSysDbConn();
			
			String q_id = request.getParameter("QID");
			
			T9PdaAttendanceLogic logic = new T9PdaAttendanceLogic();
			logic.deleteAttend(response,conn,q_id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}
}
