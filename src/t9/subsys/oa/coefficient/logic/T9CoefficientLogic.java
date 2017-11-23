package t9.subsys.oa.coefficient.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.coefficient.data.T9Coefficient;

public class T9CoefficientLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.coefficient.logic.T9CoefficientLogic.java");

	/**
	 * 添加系数信息
	 * 
	 * @param dbConn
	 * @param coefficient
	 * @throws Exception
	 */
	public void addCoefficientLogic(Connection dbConn, T9Coefficient coefficient) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			orm.saveSingle(dbConn, coefficient);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 统计记录数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public int isHaveScoreLogic(Connection dbConn) throws Exception {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(SEQ_ID) from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return count;
	}

	/**
	 * 获取seqId值
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public int getSeqIdLogic(Connection dbConn) throws Exception {
		int count = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select SEQ_ID from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return count;
	}

	/**
	 * 更新数据
	 * 
	 * @param dbConn
	 * @param seqId
	 * @throws Exception
	 */
	public void updateCoefficientLogic(Connection dbConn, T9Coefficient fiCoefficient) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			orm.updateSingle(dbConn, fiCoefficient);
		} catch (Exception e) {
			throw e;
		}
	}
	public T9Coefficient getCoefficientLogic(Connection dbConn,int seqId) throws Exception{
		T9ORM orm = new T9ORM();
		try {
			return (T9Coefficient) orm.loadObjSingle(dbConn, T9Coefficient.class, seqId);
		} catch (Exception e) {
			throw e;
		}
		
	}

	/**
	 * 获取年终考核分系数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public double getYearScoreLogic(Connection dbConn) throws Exception {
		double result = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select YEAR_SCORE from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
			if (result == 0) {
				result = 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}

	/**
	 * 获取月考核平均分系数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public double getMonthScoreLogic(Connection dbConn) throws Exception {
		double result = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select MONTH_SCORE from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
			if (result == 0) {
				result = 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}

	/**
	 * 获取处长主观分系数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public double getChiefScoreLogic(Connection dbConn) throws Exception {
		double result = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select CHIEF_SCORE from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
			if (result == 0) {
				result = 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}

	/**
	 * 获取考勤分数系数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public double getCheckScoreLogic(Connection dbConn) throws Exception {
		double result = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select CHECK_SCORE from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
			if (result == 0) {
				result = 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}

	/**
	 * 获取奖惩分系数
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public double getAwardScoreLogic(Connection dbConn) throws Exception {
		double result = 0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select AWARD_SCORE from COEFFICIENT";
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getDouble(1);
			}
			if (result == 0) {
				result = 1;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return result;
	}

}
