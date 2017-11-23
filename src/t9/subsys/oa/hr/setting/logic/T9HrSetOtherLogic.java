package t9.subsys.oa.hr.setting.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.hr.manage.staffInfo.data.T9HrStaffInfo;

public class T9HrSetOtherLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.hr.setting.logic.T9HrSetOtherLogic.java");

	public String getHrSetUserLogin(Connection conn) throws Exception {
		String result = "";
		String sql = " select PARA_VALUE from SYS_PARA where PARA_NAME = 'HR_SET_USER_LOGIN'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result = toId;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

	public String getHrRetireAge(Connection conn) throws Exception {
		String result = "";
		String sql = " select PARA_VALUE from SYS_PARA where PARA_NAME = 'RETIRE_AGE'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result = toId;
				}
			}
			String manAge = "";
			String womenAge = "";
			if (!T9Utility.isNullorEmpty(result)) {
				String resultArry[] = result.split(",");
				if (resultArry.length > 1) {
					manAge = resultArry[0];
					womenAge = resultArry[1];
				}else if(resultArry.length == 1) {
					manAge = resultArry[0];
				}
			
			}
			String data = "{manAge:\"" + T9Utility.encodeSpecial(manAge) + "\",womenAge:\"" + T9Utility.encodeSpecial(womenAge) + "\"}";
			return data;
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
	}
	
	/**
	 * 设置值
	 * @param dbConn
	 * @param yesOther
	 * @param manAge
	 * @param womenAge
	 * @throws Exception
	 */
	public void setOtherValueLogic(Connection dbConn,String yesOther,String manAge,String womenAge) throws Exception{
		if (T9Utility.isNullorEmpty(manAge)) {
			manAge = "";
		}
		if (T9Utility.isNullorEmpty(womenAge)) {
			womenAge = "";
		}
		String ageStr = manAge + "," + womenAge;
		try {
			
			this.updateYesOther(dbConn, yesOther);
			this.updateAgeValue(dbConn, ageStr);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void updateYesOther(Connection dbConn,String yesOther) throws Exception{
		String sql = "update SYS_PARA set PARA_VALUE=? where PARA_NAME='HR_SET_USER_LOGIN'";
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, yesOther);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, null, log);
		}
	}
	public void updateAgeValue(Connection dbConn,String ageStr) throws Exception{
		String sql = "update SYS_PARA set PARA_VALUE=? where PARA_NAME='RETIRE_AGE'";
		PreparedStatement stmt = null;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, ageStr);
			stmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, null, log);
		}
	}
	
	/**
	 * 根据hr表的seqId获取信息
	 * 2011-4-14
	 * @param dbConn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public int getPersongCountLogic1(Connection dbConn, int seqId) throws Exception {
		String sql = "SELECT count(SEQ_ID) from PERSON where USER_ID=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int personSeqId = 0;
		T9ORM orm = new T9ORM();
		try {
			String userId = "";
			T9HrStaffInfo staffInfo = (T9HrStaffInfo) orm.loadObjSingle(dbConn, T9HrStaffInfo.class, seqId);
			if (staffInfo != null) {
				userId = T9Utility.null2Empty(staffInfo.getUserId());
			}else {
				userId =String.valueOf(seqId);
			}
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, userId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				personSeqId = rs.getInt(1);
			}
			return personSeqId;
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}
	
	/**
	 * 根据person表的userId获取信息
	 * 2011-4-14
	 * @param dbConn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public int getPersongCountLogic2(Connection dbConn, String userId) throws Exception {
		String sql = "SELECT count(SEQ_ID) from PERSON where USER_ID=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int personSeqId = 0;
		try {
			stmt = dbConn.prepareStatement(sql);
			stmt.setString(1, userId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				personSeqId = rs.getInt(1);
			}
			return personSeqId;
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
	}

}
