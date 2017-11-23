package t9.subsys.oa.officeProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;


public class commentOffice {
	/**
	 * 查找所属部门
	 * @param dbConn
	 * @param user
	 * @param depts
	 * @return
	 * @throws Exception
	 */
	public String findDept(Connection dbConn, T9Person user,String depts) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String addDeptName="";
		if (!T9Utility.isNullorEmpty(depts)) {
			
		} else {
		  return addDeptName;
		}
    String tmp = " SEQ_ID in ("+ depts + ")";
    if (depts.indexOf(",") == -1) {
      tmp = " SEQ_ID = '" + depts + "'";
    }
		List list = new ArrayList();
		String sql = "select DEPT_NAME from department where " + tmp;
		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
		   
			while (rs.next()) {
				 list.add(rs.getString("DEPT_NAME")); 
			}
			if(list.size()>0){
				for(int i=0; i<list.size(); i++){
					addDeptName	+= (String) list.get(i)+",";
				}
				addDeptName = addDeptName.substring(0, addDeptName.length() - 1);
			}
			/*if(deleteStr != null && !"".equals(deleteStr)){
		        String[] deleteStrs = deleteStr.split(",");
		        deleteStr = "";
		        for(int i = 0 ;i < deleteStrs.length ; i++){
		           deleteStr +=  "'" + deleteStrs[i] + "'" + ",";
		        }
		           deleteStr = deleteStr.substring(0, deleteStr.length() - 1);
		    }*/
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return addDeptName;
	}
	/**
	 * 查找仓库管理员
	 * @param dbConn
	 * @param user
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public String findManager(Connection dbConn, T9Person user,String manager) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String managers="";
		if ("".equals(manager)) {
		  return managers;
		}
		String tmp = " SEQ_ID in ("+ manager + ")";
		if (manager != null && manager.indexOf(",") == -1) {
		  tmp = " SEQ_ID = '" + manager + "'";
		}
//		System.out.println(tmp);
		String sql = "select USER_NAME from person where " + tmp;
		try {
			ps = dbConn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
		
			while (rs.next()) {
				 list.add(rs.getString("USER_NAME")); 
			}
			
			if(list.size()>0){
				for(int i=0; i<list.size(); i++){
					managers	+= (String) list.get(i)+",";
				}
				managers = managers.substring(0, managers.length() - 1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return managers;
	}
	/**
	 * 查找物品调度员
	 * @param dbConn
	 * @param user
	 * @param manager etProKeeper
	 * @return
	 * @throws Exception
	 */
	public String findProKeeper(Connection dbConn, T9Person user,String proKeeper) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String proKeepers = "";
    if (T9Utility.isNullorEmpty(proKeeper)) {
      return proKeepers;
    }
    String tmp = " SEQ_ID in ("+ proKeeper + ")";
    if (proKeeper.indexOf(",") == -1) {
      tmp = " SEQ_ID = '" + proKeeper + "'";
    }
		String sql = "select USER_NAME from person where " + tmp;
		try {
			ps = dbConn.prepareStatement(sql,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
		
			while (rs.next()) {
				 list.add(rs.getString("USER_NAME")); 
			}
			if(list.size()>0){
				for(int i=0; i<list.size(); i++){
					proKeepers	+= (String) list.get(i)+",";
				}
				proKeepers = proKeepers.substring(0, proKeepers.length() - 1);
			}
			
		} catch (SQLException e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, null);
		}
		return proKeepers;
	}
}
