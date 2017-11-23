package t9.core.funcs.attendance.personal.logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.attendance.personal.data.T9AttendMobile;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;

public class T9AttendMobileLogic {

	public List<T9AttendMobile> selectMobile(Connection dbConn, String userId,
			String dateValue1, String dateValue2)
			throws Exception {
		List<T9AttendMobile> mobileList = new ArrayList<T9AttendMobile>();
		Statement stmt = null;
		ResultSet rs = null;
		String newUserIds = "";
		if (!userId.trim().equals("")) {
			String[] userIdArray = userId.split(",");
			for (int i = 0; i < userIdArray.length; i++) {
				newUserIds = newUserIds + "'" + userIdArray[i] + "',";
			}
			if (userIdArray.length > 0) {
				newUserIds = newUserIds.substring(0, newUserIds.length() - 1);
			}
		}
		if (!userId.equals("")) {
			String sql = "select * from `attend_mobile`  where M_UID in ("
					+ newUserIds + ") and " + dateValue1;
			if (!dateValue2.equals("")) {
				sql = sql + " and " + dateValue2;
			}
			sql = sql + " order by M_TIME";
			try {
				stmt = dbConn.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					T9AttendMobile mobile = new T9AttendMobile();
					mobile.setSeqId(rs.getInt("M_ID"));
					mobile.setUid(rs.getInt("M_UID"));
					mobile.setTime(T9Utility.parseDate("yyyy-MM-dd HH:mm:ss",
							rs.getString("M_TIME")));
					mobile.setLng(rs.getString("M_LNG"));
					mobile.setLat(rs.getString("M_LAT"));
					mobile.setLocation(rs.getString("M_LOCATION"));
					mobile.setRemark(rs.getString("M_REMARK"));
					mobile.setAttachmentId(rs.getString("ATTACHMENT_ID"));
					mobile.setAttachmentName(rs.getString("ATTACHMENT_NAME"));
					mobileList.add(mobile);
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				T9DBUtility.close(stmt, rs, null);
			}
		}
		return mobileList;
	}
}
