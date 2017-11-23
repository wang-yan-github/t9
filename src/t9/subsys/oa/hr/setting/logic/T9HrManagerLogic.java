package t9.subsys.oa.hr.setting.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.subsys.oa.hr.setting.data.T9HrManager;

public class T9HrManagerLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.hr.setting.logic.T9HrManagerLogic.java");

	public void setBatchValueLogic(Connection dbConn, String operation, String userStr, String deptStr) throws Exception {
		if (T9Utility.isNullorEmpty(deptStr)) {
			deptStr = "";
		}
		T9ORM orm = new T9ORM();
		try {
			String deptArry[] = deptStr.split(",");
			if ("0".equals(operation)) {
				if ("0".equals(deptStr.trim())) {
					List<T9HrManager> hrManagers = orm.loadListSingle(dbConn, T9HrManager.class, new HashMap());
					if (hrManagers != null && hrManagers.size() > 0) {
						for (T9HrManager hrManager : hrManagers) {
							String dbDeptHrManager = T9Utility.null2Empty(hrManager.getDeptHrManager());
							String deptTem = "";
							if (!T9Utility.isNullorEmpty(dbDeptHrManager) && !T9Utility.isNullorEmpty(userStr)) {
								deptTem = ",";
							}
							String deptManagerString = dbDeptHrManager + deptTem + userStr;
							String deptHrManager = this.delReIdStr(deptManagerString);
							hrManager.setDeptHrManager(deptHrManager);
							orm.updateSingle(dbConn, hrManager);
						}
					}
				} else {
					for (String deptId : deptArry) {
						Map<Object, Object> map = this.findInSet(dbConn, deptId);
						boolean flag = (Boolean) map.get("flag");
						int dbSeqId = (Integer) map.get("seqId");
						int dbDeptId = (Integer) map.get("deptId");
						String dbDeptHrManager = (String) map.get("deptManagerStr");
						if (flag) {
							String deptTem = "";
							if (!T9Utility.isNullorEmpty(dbDeptHrManager) && !T9Utility.isNullorEmpty(userStr)) {
								deptTem = ",";
							}
							String deptManagerString = dbDeptHrManager + deptTem + userStr;
							String deptHrManager = this.delReIdStr(deptManagerString);
							T9HrManager manager = new T9HrManager();
							manager.setSeqId(dbSeqId);
							manager.setDeptId(dbDeptId);
							manager.setDeptHrManager(deptHrManager);
							orm.updateSingle(dbConn, manager);
						} else {
							// T9HrManager manager = new T9HrManager();
							// manager.setDeptId(Integer.parseInt(deptId));
							// manager.setDeptHrManager(userStr);
							// orm.saveSingle(dbConn, manager);
						}
					}
				}
			} else if ("1".equals(operation.trim())) {
				if ("0".equals(deptStr.trim())) {
					List<T9HrManager> hrManagers = orm.loadListSingle(dbConn, T9HrManager.class, new HashMap());
					if (hrManagers != null && hrManagers.size() > 0) {
						for (T9HrManager hrManager : hrManagers) {
							String dbDeptHrManager = T9Utility.null2Empty(hrManager.getDeptHrManager());
							String deptHrManager = this.getDelIdStrs(userStr, dbDeptHrManager);
							hrManager.setDeptHrManager(deptHrManager);
							orm.updateSingle(dbConn, hrManager);
						}
					}
				} else {
					for (String deptId : deptArry) {
						Map<Object, Object> map = this.findInSet(dbConn, deptId);
						boolean flag = (Boolean) map.get("flag");
						int dbSeqId = (Integer) map.get("seqId");
						int dbDeptId = (Integer) map.get("deptId");
						String dbDeptHrManager = (String) map.get("deptManagerStr");
						if (flag) {
							String deptHrManager = this.getDelIdStrs(userStr, dbDeptHrManager);
							T9HrManager manager = new T9HrManager();
							manager.setSeqId(dbSeqId);
							manager.setDeptId(dbDeptId);
							manager.setDeptHrManager(deptHrManager);
							orm.updateSingle(dbConn, manager);
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 去掉重复id
	 * 
	 * @param idString
	 * @return
	 * @throws Exception
	 */
	public String delReIdStr(String idString) throws Exception {
		String data = "";
		try {
			String[] stringArry = idString.split(",");
			ArrayList arrayList = new ArrayList();
			if (stringArry != null && stringArry.length != 0) {
				for (int i = 0; i < stringArry.length; i++) {
					if (arrayList.contains(stringArry[i]) == false) {
						arrayList.add(stringArry[i]);
						data += stringArry[i] + ",";
					}
				}
			}
			if (data.lastIndexOf(",") != -1) {
				data = data.substring(0, data.lastIndexOf(","));
			}
		} catch (Exception e) {
			throw e;
		}
		return data;
	}

	public Map<Object, Object> findInSet(Connection dbConn, String str) throws Exception {
		boolean flag = false;
		Map<Object, Object> map = new HashMap<Object, Object>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select SEQ_ID,DEPT_HR_MANAGER,DEPT_ID from HR_MANAGER where " + T9DBUtility.findInSet(str, "DEPT_ID");
			stmt = dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int seqId = 0;
			int deptId = 0;
			String deptManagerStr = "";
			if (rs.next()) {
				seqId = rs.getInt("SEQ_ID");
				deptId = rs.getInt("DEPT_ID");
				deptManagerStr = T9Utility.null2Empty(rs.getString("DEPT_HR_MANAGER"));
				flag = true;
			}
			map.put("seqId", seqId);
			map.put("deptId", deptId);
			map.put("deptId", deptId);
			map.put("deptManagerStr", deptManagerStr);
			map.put("flag", flag);
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(stmt, rs, log);
		}
		return map;
	}

	/**
	 * 删除id权限
	 * 
	 * @param fromIdStr
	 * @return
	 * @throws Exception
	 */
	public String getDelIdStrs(String fromIdStr, String dbIdStrs) throws Exception {
		String data = "";
		try {
			if (!"".equals(dbIdStrs.trim()) && dbIdStrs != null && !"".equals(fromIdStr.trim())) {
				String[] fromIdStrArry = fromIdStr.split(",");
				if (dbIdStrs != null && !"".equals(dbIdStrs.trim())) {
					for (int i = 0; i < fromIdStrArry.length; i++) {
						dbIdStrs = this.returnIdStr(fromIdStrArry[i], dbIdStrs);
					}
					data = dbIdStrs;
				}
				if (data.lastIndexOf(",") != -1) {
					data = data.substring(0, data.lastIndexOf(","));
				}
			} else {
				data = dbIdStrs;
			}
		} catch (Exception e) {
			throw e;
		}
		return data;
	}

	public String returnIdStr(String fromIdStr, String idStrs) throws Exception {
		String data = "";
		try {
			if (idStrs != null && !"".equals(idStrs.trim())) {
				String temArry[] = idStrs.split(",");
				if (temArry != null && temArry.length != 0) {
					for (String idString : temArry) {
						if (idString.equals(fromIdStr.trim())) {
							continue;
						}
						data += idString + ",";
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return data;
	}

	/**
	 * 获取人力资源管理员名称
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getHrManagerLogic(Connection dbConn, String deptIdStr) throws Exception {
		String data = "";
		int deptId = 0;
		if (T9Utility.isNullorEmpty(deptIdStr)) {
			deptIdStr = "-1";
		}
		if ("ALL_DEPT".equals(deptIdStr)) {
			deptIdStr = "0";
		}
		if (T9Utility.isNumber(deptIdStr)) {
			deptId = Integer.parseInt(deptIdStr);
		}
		T9ORM orm = new T9ORM();
		try {
			Map map = new HashMap();
			map.put("DEPT_ID", deptId);
			T9HrManager manager = (T9HrManager) orm.loadObjSingle(dbConn, T9HrManager.class, map);
			String deptHrManager = "";
			String userName = "";
			if (manager != null) {
				deptHrManager = T9Utility.null2Empty(manager.getDeptHrManager());
				if (!T9Utility.isNullorEmpty(deptHrManager)) {
					userName = this.getUserNameLogic(dbConn, deptHrManager);
				}
			} else {
				T9HrManager hrManager = new T9HrManager();
				hrManager.setDeptId(deptId);
				orm.saveSingle(dbConn, hrManager);
			}
			data = "{userName:\"" + T9Utility.encodeSpecial(userName) + "\"}";
		} catch (Exception e) {
			throw e;
		}
		return data;

	}

	/**
	 * 获取人力资源管理员Id串
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getHrManagerIdStrLogic(Connection dbConn, String deptIdStr) throws Exception {
		int deptId = 0;
		if (T9Utility.isNullorEmpty(deptIdStr)) {
			deptIdStr = "-1";
		}
		if ("ALL_DEPT".equals(deptIdStr)) {
			deptIdStr = "0";
		}
		if (T9Utility.isNumber(deptIdStr)) {
			deptId = Integer.parseInt(deptIdStr);
		}
		T9ORM orm = new T9ORM();
		try {
			Map map = new HashMap();
			map.put("DEPT_ID", deptId);
			T9HrManager manager = (T9HrManager) orm.loadObjSingle(dbConn, T9HrManager.class, map);
			String deptHrManager = "";
			if (manager != null) {
				deptHrManager = T9Utility.null2Empty(manager.getDeptHrManager());
			}
			String data = "{deptHrManager:\"" + T9Utility.encodeSpecial(deptHrManager) + "\"}";
			return data;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 编辑人力资源管理员Id串
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public void editHrManagerLogic(Connection dbConn, String deptIdStr, String deptHrManagerStr) throws Exception {
		int deptId = 0;
		if (T9Utility.isNullorEmpty(deptIdStr)) {
			deptIdStr = "-1";
		}
		if ("ALL_DEPT".equals(deptIdStr)) {
			deptIdStr = "0";
		}
		if (T9Utility.isNumber(deptIdStr)) {
			deptId = Integer.parseInt(deptIdStr);
		}
		T9ORM orm = new T9ORM();
		try {
			Map map = new HashMap();
			map.put("DEPT_ID", deptId);
			T9HrManager manager = (T9HrManager) orm.loadObjSingle(dbConn, T9HrManager.class, map);
			if (manager != null) {
				manager.setDeptHrManager(deptHrManagerStr);
				orm.updateSingle(dbConn, manager);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 取得人员名称
	 * 
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getUserNameLogic(Connection dbConn, String seqIdStr) throws Exception {
		String result = "";
		if (T9Utility.isNullorEmpty(seqIdStr)) {
			return result;
		}
		if (seqIdStr.endsWith(",")) {
			seqIdStr = seqIdStr.substring(0, seqIdStr.length() - 1);
		}
		String sql = " select USER_NAME from PERSON where SEQ_ID in(" + seqIdStr + ")";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String toId = rs.getString(1);
				if (toId != null) {
					result += toId + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}

}
