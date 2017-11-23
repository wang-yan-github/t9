package t9.mobile.attendance.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.util.T9MobileUtility;
import t9.mobile.util.T9QuickQuery;

public class T9PdaAttendanceLogic {

	public List getAttend(Connection conn, HttpServletRequest request,
			String query) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		List<Map<String, String>> qList = null;
		try {
			qList = T9QuickQuery.quickQueryList(conn, query);
			for (Map<String, String> data : qList) {
				String q_id = data.get("M_ID");
				String u_id = data.get("M_UID");
				// 签到时间
				String t = data.get("M_TIME");
				SimpleDateFormat sf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Timestamp time = new Timestamp(sf.parse(t).getTime());
				String lng = data.get("M_LNG");
				String lat = data.get("M_LAT");
				String location = data.get("M_LOCATION");
				String remark = data.get("M_REMARK");
				String isfoot = data.get("M_ISFOOT");
				// 附件url
				String attachent_id = data.get("ATTACHENT_ID");
				String attachent_name = data.get("ATTACHENT_NAME");
				String aId[] = null;
				String aName[] = null;
				List<String> DOWN_FILE_URL = new ArrayList();
				if (!"".equals(attachent_id) && !"".equals(attachent_id)
						&& null != attachent_id && null != attachent_id) {
					if (!"".equals(attachent_id) && null != attachent_id) {
						attachent_id = attachent_id.substring(0,
								attachent_id.length() - 1);
						aId = attachent_id.split(",");
					}
					if (!"".equals(attachent_name) && null != attachent_name) {
						attachent_name = attachent_name.substring(0,
								attachent_name.length() - 1);
						aName = attachent_name.split("\\*");
					}

					StringBuffer url = request.getRequestURL();
					String localhostUrl = url
							.delete(url.length()
									- request.getRequestURI().length(),
									url.length()).append("/").toString();
					for (int i = 0; i < aName.length; i++) {
						DOWN_FILE_URL
								.add(localhostUrl
										+ "t9/t9/mobile/attach/act/T9PdaAttachmentAct/downFile.act?attachmentName="
										+ aName[i] + "&attachmentId=" + aId[i]
										+ "&module=notify");
					}
				}
				// lp 附件判断
				int has_attachment = 0;
				if (!T9Utility.isNullorEmpty(attachent_id)
						&& !T9Utility.isNullorEmpty(attachent_name))
					has_attachment = 1;
				else
					has_attachment = 0;

				Map map = new HashMap<String, String>();
				map.put("q_id", q_id);
				map.put("u_id", u_id);
				map.put("time", T9Utility.getDateTimeStr(time));
				map.put("lng", T9Utility.null2Empty(lng));
				map.put("lat", lat);
				map.put("location", location);
				map.put("remake", T9Utility.null2Empty(remark));
				map.put("isfoot", T9Utility.null2Empty(isfoot));
				map.put("attachment_id", attachent_id);
				List<Map<String, String>> downFile = new ArrayList<Map<String, String>>();
				Map<String, String> fileUrl = new HashMap();
				for (int i = 0; i < DOWN_FILE_URL.size(); i++) {
					fileUrl.put("attachmentName", aName[i]);
					fileUrl.put("fileUrl", DOWN_FILE_URL.get(i));
					downFile.add(fileUrl);
				}
				map.put("down_file", downFile);
				map.put("has_attachment", has_attachment);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void addAttend(HttpServletRequest request,
			HttpServletResponse response, Connection conn, Timestamp t,
			String LNG, String LAT, String LOCATION, String REMARK,
			String ISFOOT, int userId) throws SQLException {
		String sql = "insert into attend_mobile (M_TIME,M_LNG,M_LAT,M_LOCATION,M_REMARK,M_ISFOOT,M_UID) value(?,?,?,?,?,?,?)";
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setTimestamp(1, t);
			ps.setString(2, LNG);
			ps.setString(3, LAT);
			ps.setString(4, LOCATION);
			ps.setString(5, REMARK);
			ps.setString(6, ISFOOT);
			ps.setInt(7, userId);
			boolean flag = ps.execute();
			conn.commit();
			if (flag) {
				T9MobileUtility.output(response,
						T9MobileUtility.getResultJson(1, "添加失败", null));
			} else {
				T9MobileUtility.output(response,
						T9MobileUtility.getResultJson(1, "添加成功", null));
			}
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		} finally {
			T9DBUtility.close(ps, null, null);
		}
	}

	public void deleteAttend(HttpServletResponse response, Connection conn,
			String q_id) throws SQLException {
		PreparedStatement ps = null;    
	    String sql = "delete from attend_mobile where M_ID='"+ q_id +"'";
	      
	      try{
	        ps = conn.prepareStatement(sql);
	        int i = ps.executeUpdate();
			if (i > 0) {
				T9MobileUtility.output(response,
						T9MobileUtility.getResultJson(1, "删除成功", null));
			} else {
				T9MobileUtility.output(response,
						T9MobileUtility.getResultJson(1, "删除失败", null));
			}
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
		}
	}

}
