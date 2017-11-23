package t9.project.system.logic;


import java.util.List;
import java.util.HashMap;
import java.util.Map;

import t9.cms.station.data.T9CmsStation;
import t9.core.codeclass.data.T9CodeItem;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.project.system.data.T9ProjPriv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class T9ProjSystemLogic{
	
	
	/**
	 * 根据ClassNo获取类型列表
	 * @param dbConn
	 * @param classNo
	 * @return
	 * @throws Exception
	 */
	public String getStyleList(Connection dbConn,String classNo) throws Exception{
		List<T9CodeItem> codeList = null;
		StringBuffer sb = new StringBuffer("[");
		T9ORM orm=new T9ORM();
		String[] filters={" 1=1 and CLASS_NO = '"+ classNo+"'"};
		codeList=orm.loadListSingle(dbConn, T9CodeItem.class, filters);
		if (codeList != null && codeList.size() > 0) {
			 for (T9CodeItem codeItem : codeList) {
				 sb.append("{");
				 sb.append("\"seqId\":"+codeItem.getSeqId()+",");
				 sb.append("\"classCode\":\""+codeItem.getClassCode()+"\",");
				 sb.append("\"classDesc\":\""+codeItem.getClassDesc()+"\",");
				 sb.append("\"sortNo\":\""+codeItem.getSortNo()+"\",");
				 sb.append("\"classNo\":\""+codeItem.getClassNo()+"\"},");
				 
			 }
			
		 }
		 if(sb.length()>3){
			 sb=sb.deleteCharAt(sb.length()-1);
		 }
		 sb.append("]");
		return sb.toString();
	}

	/**
	 * 设置先建权限
	 * @param dbConn
	 * @param privCode
	 * @param stringInfo
	 * @throws Exception
	 */
	  public void setNewPriv(Connection dbConn,String privCode, String user,String role,String dept) throws Exception {
		 T9ORM orm=new T9ORM();
		 try {
			 T9ProjPriv priv=new T9ProjPriv();
			 
			 priv.setPrivCode(privCode);
			 priv.setPrivUser(user);
			 priv.setPrivRole(role);
			 priv.setPrivDept(dept);
			 Map filters=new HashMap();
			 filters.put("PRIV_CODE", privCode);
			 T9ProjPriv priv2=(T9ProjPriv)orm.loadObjSingle(dbConn, T9ProjPriv.class, filters);
			 if(priv2!=null){
			     priv2.setSeqId(priv2.getSeqId());
				 priv2.setPrivCode(privCode);
				 priv2.setPrivUser(user);
				 priv2.setPrivRole(role);
				 priv2.setPrivDept(dept);
				 orm.updateSingle(dbConn, priv2); 
			 }else{
				 orm.saveSingle(dbConn, priv);
			 }
		 } catch (Exception e) {
	      throw e;
	    } 
	  }
	  
	  /**
	   * 获取新建项目权限
	   * @param dbConn
	   * @param privCode
	   * @return
	   * @throws Exception
	   */
	  public String getNewPriv(Connection dbConn, String privCode) throws Exception {
		    
		    StringBuffer sb = new StringBuffer();
		    PreparedStatement stmt = null;
		    ResultSet rs = null;
		    String sql = "select priv_user,priv_role,priv_dept from proj_priv where priv_code ='"+privCode+"'";
		    try {
		      stmt = dbConn.prepareStatement(sql);
		      rs = stmt.executeQuery();
		      if (rs.next()) {
		        sb.append("{\"deptPer\":\""+ rs.getString(3) +"\""
		                + ",\"deptPerDesc\":\""+ queryDeptname(dbConn, rs.getString(3)) +"\""
		        		    + ",\"privPer\":\""+ rs.getString(2) +"\""
		        		    + ",\"privPerDesc\":\""+ queryPrivname(dbConn, rs.getString(2)) +"\""
		        		    +	",\"userPer\":\""+ rs.getString(1) +"\""
		        		    + ",\"userPerDesc\":\""+ queryUsername(dbConn, rs.getString(1)) +"\"}");
		      }
		    } catch (Exception e) {
		      throw e;
		    } finally {
		      T9DBUtility.close(stmt, rs, null);
		    }
		    if(T9Utility.isNullorEmpty(sb.toString())){
		      sb.append("{\"deptPer\":\"\",\"deptPerDesc\":\"\",\"privPer\":\"\",\"privPerDesc\":\"\",\"userPer\":\"\",\"userPerDesc\":\"\"}");
		    }
		    return sb.toString();
		  }
	  
	  
	  /**
	   * 获取部门名称
	   * 
	   * @param dbConn
	   * @param seqIds
	   * @return String
	   */
	  public String queryDeptname(Connection dbConn, String seqIds) throws Exception {
	    if(T9Utility.isNullorEmpty(seqIds)){
	      return "";
	    }
	    StringBuffer sb = new StringBuffer("");
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "select dept_name from department where seq_id in ("+ seqIds +")";
	    try {
	      stmt = dbConn.prepareStatement(sql);
	      rs = stmt.executeQuery();
	      while (rs.next()) {
	        String deptName = rs.getString("dept_name");
	        sb.append(deptName+",");
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    if(sb.length() > 1){
	      sb = sb.deleteCharAt(sb.length() - 1);
	    }
	    return sb.toString();
	  }
	  
	  /**
	   * 获取角色名称
	   * 
	   * @param dbConn
	   * @param seqIds
	   * @return String
	   */
	  public String queryPrivname(Connection dbConn, String seqIds) throws Exception {
	    if(T9Utility.isNullorEmpty(seqIds)){
	      return "";
	    }
	    StringBuffer sb = new StringBuffer("");
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "select priv_name from user_priv where seq_id in ("+ seqIds +")";
	    try {
	      stmt = dbConn.prepareStatement(sql);
	      rs = stmt.executeQuery();
	      while (rs.next()) {
	        String privName = rs.getString("priv_name");
	        sb.append(privName+",");
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    if(sb.length() > 1){
	      sb = sb.deleteCharAt(sb.length() - 1);
	    }
	    return sb.toString();
	  }
	  
	  /**
	   * 获取人员名称
	   * 
	   * @param dbConn
	   * @param seqIds
	   * @return String
	   */
	  public String queryUsername(Connection dbConn, String seqIds) throws Exception {
	    if(T9Utility.isNullorEmpty(seqIds)){
	      return "";
	    }
	    StringBuffer sb = new StringBuffer("");
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "select user_name from person where seq_id in ("+ seqIds +")";
	    try {
	      stmt = dbConn.prepareStatement(sql);
	      rs = stmt.executeQuery();
	      while (rs.next()) {
	        String userName = rs.getString("user_name");
	        sb.append(userName+",");
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    if(sb.length() > 1){
	      sb = sb.deleteCharAt(sb.length() - 1);
	    }
	    return sb.toString();
	  }
/**
 *设置项目审批权限
 * @param dbConn
 * @param privCode
 * @param stringInfo
 * @throws Exception 
 */
	public void setApprovePriv(Connection dbConn, String privCode,String user,String dept,String seqId) throws Exception {
		T9ORM orm=new T9ORM();
		 try {
			 if(seqId==null || "".equals(seqId)){
				 T9ProjPriv priv=new T9ProjPriv();
				 priv.setPrivCode(privCode);
				 priv.setPrivUser(user);
				 priv.setPrivDept(dept);
			     orm.saveSingle(dbConn, priv);
			 }else{
				 T9ProjPriv priv2=(T9ProjPriv)orm.loadObjSingle(dbConn, T9ProjPriv.class, Integer.parseInt(seqId));
				 priv2.setPrivCode(privCode);
				 priv2.setPrivUser(user);
				 priv2.setPrivDept(dept);
			     orm.updateSingle(dbConn, priv2);
			 }
			 
		 } catch (Exception e) {
	      throw e;
	    } 
	}
/**
 * 获取审批权限规则列表
 * @param dbConn
 * @param privCode
 * @return
 * @throws Exception
 */
public String getApproveList(Connection dbConn, String privCode) throws Exception {
	List<T9ProjPriv> approveList = null;
	StringBuffer sb = new StringBuffer("[");
	T9ORM orm=new T9ORM();
	try{
	String[] filters={" 1=1 and PRIV_CODE = '"+ privCode+"'"};
	approveList=orm.loadListSingle(dbConn, T9ProjPriv.class, filters);
	if (approveList != null && approveList.size() > 0) {
		 for (T9ProjPriv approve : approveList) {
			 sb.append("{");
			 sb.append("\"seqId\":"+approve.getSeqId()+",");
			 sb.append("\"approveUser\":\""+queryUsername(dbConn, approve.getPrivUser())+"\",");
			 if("0".equals(approve.getPrivDept())){
			   sb.append("\"managerDept\":\"全体部门\"},");
			 }else{
			   sb.append("\"managerDept\":\""+queryDeptname(dbConn, approve.getPrivDept())+"\"},");
			 }
		 }
	 }
	 if(sb.length()>3){
		 sb=sb.deleteCharAt(sb.length()-1);
	 }
	 sb.append("]");
	}catch(Exception ex){
		throw ex;
	}
	return sb.toString();
}
/**
 * 获取新建项目权限
 * @param dbConn
 * @param privCode
 * @return
 * @throws Exception
 */
public String getApprovePriv(Connection dbConn, int seqId) throws Exception {
	    
	    StringBuffer sb = new StringBuffer();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = "select priv_user,priv_dept from proj_priv where seq_id ="+seqId;
	    try {
	      stmt = dbConn.prepareStatement(sql);
	      rs = stmt.executeQuery();
	      if (rs.next()) {
	        sb.append("{\"deptPer\":\""+ rs.getString(2) +"\""
	                + ",\"deptPerDesc\":\""+ queryDeptname(dbConn, rs.getString(2)) +"\""
	        		    +	",\"userPer\":\""+ rs.getString(1) +"\""
	        		    + ",\"userPerDesc\":\""+ queryUsername(dbConn, rs.getString(1)) +"\"}");
	      }
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	    if(T9Utility.isNullorEmpty(sb.toString())){
	      sb.append("{\"deptPer\":\"\",\"deptPerDesc\":\"\",\"userPer\":\"\",\"userPerDesc\":\"\"}");
	    }
	    return sb.toString();
	  }
/**
 * 删除审批权限规则
 * @param dbConn
 * @param privCode
 * @param stringInfo
 * @throws Exception
 */
public void delApprovePriv(Connection dbConn,String seqId) throws Exception {
	 T9ORM orm=new T9ORM();
	 try {
		 T9ProjPriv priv=(T9ProjPriv)orm.loadObjSingle(dbConn, T9ProjPriv.class, Integer.parseInt(seqId));
		 orm.deleteSingle(dbConn, priv);
	 } catch (Exception e) {
     throw e;
   } 
 }
 
}