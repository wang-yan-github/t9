package t9.cms.permissions.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.cms.column.data.T9CmsColumn;
import t9.cms.station.data.T9CmsStation;
import t9.core.data.T9DbRecord;
import t9.core.data.T9PageDataList;
import t9.core.funcs.dept.data.T9Department;
import t9.core.funcs.person.data.T9Person;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;

public class T9PermissionsLogic {
	private static Logger log = Logger.getLogger("t9.cms.column.logic");
  /**
   * 获取站点权限
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @return json
   */
  public String getPermissions(Connection dbConn, String userType, String seqId) throws Exception {
    
    StringBuffer sb = new StringBuffer();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select " + userType + " from cms_station where seq_id="+seqId;
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (rs.next()) {
        String userTypeTemp = rs.getString(userType);
        if(T9Utility.isNullorEmpty(userTypeTemp)) {
          sb.append("{\"deptPer\":\"\",\"deptPerDesc\":\"\",\"privPer\":\"\",\"privPerDesc\":\"\",\"userPer\":\"\",\"userPerDesc\":\"\"}");
          return sb.toString();
        }
        String[] permissions = {"","",""};
        String permissionsTemp[] = userTypeTemp.split("\\|");
        for(int i = 0; i<permissionsTemp.length; i++){
          permissions[i] = permissionsTemp[i];
        }
        sb.append("{\"deptPer\":\""+ permissions[0] +"\""
                + ",\"deptPerDesc\":\""+ queryDeptname(dbConn, permissions[0]) +"\""
        		    + ",\"privPer\":\""+ permissions[1] +"\""
        		    + ",\"privPerDesc\":\""+ queryPrivname(dbConn, permissions[1]) +"\""
        		    +	",\"userPer\":\""+ permissions[2] +"\""
        		    + ",\"userPerDesc\":\""+ queryUsername(dbConn, permissions[2]) +"\"}");
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
   * 设置站点权限
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @param stringInfo
   * @return 
   */
  public void setPermissions(Connection dbConn, String userType, String seqId, String stringInfo) throws Exception {
    
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = " update cms_station set "+ userType +"='"+stringInfo+"' where seq_id="+seqId;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  /**
   * 设置栏目权限--批量设置某一站点下所有栏目
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @param stringInfo
   * @return 
   */
  public void setPermissionsChild(Connection dbConn, String userType, String seqId, String stringInfo) throws Exception {
    
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = " update cms_column set "+ userType +"='"+stringInfo+"' where station_id="+seqId;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  public void delPermissionsChild(Connection dbConn, String userType, String seqId, String stringInfo) throws Exception {
	    
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String sql = " update cms_column set "+ userType +"='"+stringInfo+"' where station_id="+seqId;
	    try {
	      stmt = dbConn.prepareStatement(sql);
	      stmt.executeUpdate();
	    } catch (Exception e) {
	      throw e;
	    } finally {
	      T9DBUtility.close(stmt, rs, null);
	    }
	  }
	  
  /**
   * 获取栏目权限
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @return String
   */
  public String getPermissionsColumn(Connection dbConn, String userType, String seqId) throws Exception {
	    
    StringBuffer sb = new StringBuffer();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = "select " + userType + " from cms_column where seq_id="+seqId;
    try {
      stmt = dbConn.prepareStatement(sql);
      rs = stmt.executeQuery();
      if (rs.next()) {
        String userTypeTemp = rs.getString(userType);
        if(T9Utility.isNullorEmpty(userTypeTemp)) {
          sb.append("{\"deptPer\":\"\",\"deptPerDesc\":\"\",\"privPer\":\"\",\"privPerDesc\":\"\",\"userPer\":\"\",\"userPerDesc\":\"\"}");
          return sb.toString();
        }
        String[] permissions = {"","",""};
        String permissionsTemp[] = userTypeTemp.split("\\|");
        for(int i = 0; i<permissionsTemp.length; i++){
          permissions[i] = permissionsTemp[i];
        }
        sb.append("{\"deptPer\":\""+ permissions[0] +"\""
                + ",\"deptPerDesc\":\""+ queryDeptname(dbConn, permissions[0]) +"\""
        		    + ",\"privPer\":\""+ permissions[1] +"\""
        		    + ",\"privPerDesc\":\""+ queryPrivname(dbConn, permissions[1]) +"\""
        		    +	",\"userPer\":\""+ permissions[2] +"\""
        		    + ",\"userPerDesc\":\""+ queryUsername(dbConn, permissions[2]) +"\"}");
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
   * 设置栏目权限
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @param stringInfo
   * @return 
   */
  public void setPermissionsColumn(Connection dbConn, String userType, String seqId, String stringInfo) throws Exception {
	    
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = " update cms_column set "+ userType +"='"+stringInfo+"' where seq_id="+seqId;
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  /**
   * 设置栏目权限--批量设置某栏目下所有栏目权限
   * 
   * @param dbConn
   * @param userType
   * @param seqId
   * @param stringInfo
   * @return 
   */
  public void setPermissionsChildColumn(Connection dbConn, String userType, String seqId, String stringInfo) throws Exception {
    String seqIds = getChild(dbConn , seqId);
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql = " update cms_column set "+ userType +"='"+stringInfo+"' where seq_id in (0"+seqIds+")";
    try {
      stmt = dbConn.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(stmt, rs, null);
    }
  }
  
  /**
   * 获取某栏目下所有子栏目
   * 
   * @param dbConn
   * @param seqId
   * @return String
   */
  public String getChild(Connection dbConn, String seqId) throws Exception{
	
  	StringBuffer sb = new StringBuffer();
  	T9ORM orm = new T9ORM();
  	String filters[] = {" PARENT_ID =" + seqId};
  	List<T9CmsColumn> columnList = orm.loadListSingle(dbConn, T9CmsColumn.class, filters);
  	if(columnList == null){
  	  return sb.toString();
  	}
  	else{
  	  for(T9CmsColumn column : columnList){
    		sb.append("," + column.getSeqId());
    		sb.append(getChild(dbConn , column.getSeqId()+""));
  	  }
  	}
	  return sb.toString();
  }
  
  /**
   * CMS 获取站点树
   * 
   * @param dbConn
   * @return
   * @throws Exception
   */
  public String getStationTree(Connection dbConn, T9Person person) throws Exception {
    
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer("[");
    boolean isHave = false;
    String filters[] = {" 1=1 order by SEQ_ID asc "};
    List<T9CmsStation> stations = orm.loadListSingle(dbConn, T9CmsStation.class, filters);
    if (stations != null && stations.size() > 0) {
      for (T9CmsStation station : stations) {
        
        int dbSeqId = station.getSeqId();
        String stationName = T9Utility.null2Empty(station.getStationName());
        boolean flag = this.isHaveChild(dbConn, dbSeqId);
        if (flag) {
          sb.append("{");
          sb.append("nodeId:\"" + dbSeqId + ",station\"");
          sb.append(",name:\"" + T9Utility.encodeSpecial(stationName) + "\"");
          sb.append(",isHaveChild:" + 1 + "");
          sb.append(",extData:\"" + "" + "\"");
          sb.append("},");
          isHave = true;
        } else {
          sb.append("{");
          sb.append("nodeId:\"" + dbSeqId + ",station\"");
          sb.append(",name:\"" + T9Utility.encodeSpecial(stationName) + "\"");
          sb.append(",isHaveChild:" + 0 + "");
          sb.append(",extData:\"" + "" + "\"");
          sb.append("},");
          isHave = true;
        }
      }
      if (isHave) {
        sb = sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } else {
      sb.append("]");
    }
    return sb.toString();
  }
  
  /**
   * 站点是否存在子节点
   * 
   */
  public boolean isHaveChild(Connection dbConn, int dbSeqId){
    
    String sql = " select 1 from cms_column where STATION_ID ="+dbSeqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  }
  
  /**
   * 获取栏目树

   * 
   * 
   * @param dbConn
   * @param id
   * @return
   * @throws Exception
   */
  public String getColumnTree(Connection dbConn, String id, String type, T9Person person) throws Exception {
    T9ORM orm = new T9ORM();
    StringBuffer sb = new StringBuffer("[");
    boolean isHave = false;
    try {
      int seqId = 0;
      if (T9Utility.isNumber(id)) {
        seqId = Integer.parseInt(id);
      }
      String filters[];
      if("station".equals(type)){
        String filtersTemp[] = {" STATION_ID =" + seqId + " and PARENT_ID =" + 0 + " order by COLUMN_INDEX desc "};
        filters = filtersTemp;
      }
      else{
        String filtersTemp[] = {" PARENT_ID =" + seqId + " order by COLUMN_INDEX desc "};
        filters = filtersTemp;
      }
      List<T9CmsColumn> columns = orm.loadListSingle(dbConn, T9CmsColumn.class, filters);
      if (columns != null && columns.size() > 0) {
        for (T9CmsColumn column : columns) {
          int dbSeqId = column.getSeqId();
          String columnName = T9Utility.null2Empty(column.getColumnName());
          boolean counter = isHaveChildColumn(dbConn, dbSeqId);
          if (counter) {
            sb.append("{");
            sb.append("nodeId:\"" + dbSeqId + ",column\"");
            sb.append(",name:\"" + T9Utility.encodeSpecial(columnName) + "\"");
            sb.append(",isHaveChild:" + 1 + "");
            sb.append(",extData:\"" + "" + "\"");
            sb.append("},");
            isHave = true;
          } else {
            sb.append("{");
            sb.append("nodeId:\"" + dbSeqId + ",column\"");
            sb.append(",name:\"" + T9Utility.encodeSpecial(columnName) + "\"");
            sb.append(",isHaveChild:" + 0 + "");
            sb.append(",extData:\"" + "" + "\"");
            sb.append("},");
            isHave = true;
          }
        }
        if (isHave) {
          sb = sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
      } else {
        sb.append("]");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return sb.toString();
  }
  
  /**
   * 栏目是否存在子节点
   * 
   */
  public boolean isHaveChildColumn(Connection dbConn, int dbSeqId){
    
    String sql = " select 1 from cms_column where PARENT_ID ="+dbSeqId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = dbConn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return false;
  }
  
  /**
   * 判断列表访问权限
   * 
   * @param T9PageDataList
   * @param person
   * @return T9PageDataList
   */
  public T9PageDataList visitControl(T9PageDataList pageDataList, T9Person person) {
    T9PageDataList pageDataListRe = new T9PageDataList();
    int total = pageDataList.getTotalRecord();
    int newTotal = total;
    for(int i = 0; i < pageDataList.getRecordCnt() ;i++){
      T9DbRecord record = pageDataList.getRecord(i);
      String visitUser = (String)record.getValueByName("visitUser");
      if(T9Utility.isNullorEmpty(visitUser)){
        newTotal--;
        continue;
      }
      else{
        String[] permissions = {"","",""};
        String[] permissionsTemp = visitUser.split("\\|");
        for(int j = 0; j<permissionsTemp.length; j++){
          permissions[j] = permissionsTemp[j];
        }
        String visitDept = permissions[0] == null ? "" : permissions[0];
        visitDept = "," + visitDept + ",";
        String visitPriv = permissions[1] == null ? "" : permissions[1];
        visitPriv = "," + visitPriv + ",";
        String visitPerson = permissions[2] == null ? "" : permissions[2];
        visitPerson = "," + visitPerson + ",";
        
        if(visitDept.contains((","+person.getDeptId()+",")) || ",0,".equals(visitDept)){
          pageDataListRe.addRecord(record);
        }
        else if(visitPriv.contains((","+person.getUserPriv()+","))){
          pageDataListRe.addRecord(record);
        }
        else if(visitPerson.contains((","+person.getSeqId()+","))){
          pageDataListRe.addRecord(record);
        }
        else{
          newTotal--;
          continue;
        }
      }
    }
    pageDataListRe.setTotalRecord(newTotal);
    return pageDataListRe;
  }
  
  /**
   * 判断访问权限
   * 
   * @param T9PageDataList
   * @param person
   * @return T9PageDataList
   */
  public boolean isPermissions(Connection dbConn, String userPermissions, T9Person person){
    
    String[] permissions = {"","",""};
    String[] permissionsTemp = userPermissions.split("\\|");
    for(int j = 0; j < permissionsTemp.length; j++){
      permissions[j] = permissionsTemp[j];
    }
    String permissionsDept = permissions[0] == null ? "" : permissions[0];
    permissionsDept = "," + permissionsDept + ",";
    String permissionsPriv = permissions[1] == null ? "" : permissions[1];
    permissionsPriv = "," + permissionsPriv + ",";
    String permissionsPerson = permissions[2] == null ? "" : permissions[2];
    permissionsPerson = "," + permissionsPerson + ",";
    
    if(permissionsDept.contains((","+person.getDeptId()+",")) || ",0,".equals(permissionsDept)){
      return true;
    }
    else if(permissionsPriv.contains((","+person.getUserPriv()+","))){
      return true;
    }
    else if(permissionsPerson.contains((","+person.getSeqId()+","))){
      return true;
    }
    return false;
  }
	
	public List<T9CmsColumn> getFileSorts(Connection dbConn, Map map) throws Exception {
		T9ORM orm = new T9ORM();
		return (List<T9CmsColumn>) orm.loadListSingle(dbConn, T9CmsColumn.class, map);
	}
	
	public List<T9CmsStation> getFileSorts2(Connection dbConn, Map map) throws Exception {
		T9ORM orm = new T9ORM();
		return (List<T9CmsStation>) orm.loadListSingle(dbConn, T9CmsStation.class, map);
	}
	/**
	 * 批量设置权限（添加权限）
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param setIdStr
	 * @throws Exception
	 */
	public void updateVisitOverrideAdd(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		if("column".equals(flag) || "content".equals(flag)){
			T9CmsColumn cmsColumn = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, seqId);
			getChildColunmAdd(dbConn, cmsColumn, setIdStr, action);
		}else if("station".equals(flag)){
			T9CmsStation cmsStation=(T9CmsStation)orm.loadObjSingle(dbConn, T9CmsStation.class, seqId);
			getChildStationAdd(dbConn, cmsStation, setIdStr, action);
		}
	}
	
	
	public void updateColumnChildAdd(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildColunmAdd(dbConn, sort, setIdStr, action);
		}
	}
	
	
	public void updateStationChildAdd(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("STATION_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildColunmAdd(dbConn, sort, setIdStr, action);
		}
	}
	
	public void updateContentAdd(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildColunmAdd(dbConn, sort, setIdStr, action);
		}
	}
	
	public void updateContentStationAdd(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("STATION_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildColunmAdd(dbConn, sort, setIdStr, action);
		}
	}
	public void getChildColunmAdd(Connection dbConn, T9CmsColumn fileSort, String setIdStr, String action) throws Exception {
		T9CmsColumn fileSort2 = new T9CmsColumn();

		T9ORM orm = new T9ORM();
		int seqId = fileSort.getSeqId();
		if ("VISIT_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setVisitUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			// fileSort.setOwner(idStrArry);

			fileSort.setVisitUser(idStrArry);
		}
		if ("EDIT_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setEditUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			// fileSort.setOwner(idStrArry);

			fileSort.setEditUser(idStrArry);
		}
		if ("DEL_USER".equals(action)) {
      String deptString = "";
      String roleString = "";
      String personString = "";
      fileSort2.setDelUser(setIdStr);
      String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
      String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
      String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

      String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
      String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
      String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

      String deptTem = "";
      String roleTem = "";
      String personTem = "";
      if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
        deptTem = ",";
      }
      if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
        roleTem = ",";
      }
      if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
        personTem = ",";
      }

      String alldeptStr = "";

      if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        deptString = dbDeptIdStrs + deptTem + deptIdStrs;
        alldeptStr = this.delReIdStr(deptString);
      } else {
        alldeptStr = "0";
      }

      roleString = dbRoleIdStrs + roleTem + roleIdStrs;
      personString = dbPersonIdStrs + personTem + personIdStrs;

      String allRoleStr = this.delReIdStr(roleString);
      String personStr = this.delReIdStr(personString);

      String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
      // fileSort.setOwner(idStrArry);
      fileSort.setDelUser(idStrArry);
    }
		if ("NEW_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setNewUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setNewUser(idStrArry);
		}
		if ("REL_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setRelUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setRelUser(idStrArry);
		}
		if ("EDIT_USER_CONTENT".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setEditUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setEditUserContent(idStrArry);
		}
		if ("APPROVAL_USER_CONTENT".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setApprovalUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setApprovalUserContent(idStrArry);
		}
		if ("RELEASE_USER_CONTENT".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setReleaseUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setReleaseUserContent(idStrArry);
		}
		if ("RECEVIE_USER_CONTENT".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setRecevieUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setRecevieUserContent(idStrArry);
		}
		if ("ORDER_CONTENT".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setOrderContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setOrderContent(idStrArry);
		}
		orm.updateSingle(dbConn, fileSort);
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);

		for (T9CmsColumn sort : list) {
			getChildColunmAdd(dbConn, sort, setIdStr, action);
		}

	}
	
	public void getChildStationAdd(Connection dbConn, T9CmsStation fileSort, String setIdStr, String action) throws Exception {
		T9CmsStation fileSort2 = new T9CmsStation();

		T9ORM orm = new T9ORM();
		int seqId = fileSort.getSeqId();
		if ("VISIT_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setVisitUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			// fileSort.setOwner(idStrArry);

			fileSort.setVisitUser(idStrArry);
		}
		if ("EDIT_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setEditUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			// fileSort.setOwner(idStrArry);

			fileSort.setEditUser(idStrArry);
		}
		if ("DEL_USER".equals(action)) {
      String deptString = "";
      String roleString = "";
      String personString = "";
      fileSort2.setDelUser(setIdStr);
      String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
      String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
      String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

      String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
      String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
      String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

      String deptTem = "";
      String roleTem = "";
      String personTem = "";
      if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
        deptTem = ",";
      }
      if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
        roleTem = ",";
      }
      if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
        personTem = ",";
      }

      String alldeptStr = "";

      if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        deptString = dbDeptIdStrs + deptTem + deptIdStrs;
        alldeptStr = this.delReIdStr(deptString);
      } else {
        alldeptStr = "0";
      }

      roleString = dbRoleIdStrs + roleTem + roleIdStrs;
      personString = dbPersonIdStrs + personTem + personIdStrs;

      String allRoleStr = this.delReIdStr(roleString);
      String personStr = this.delReIdStr(personString);

      String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
      fileSort.setDelUser(idStrArry);
    }
		if ("NEW_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setNewUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setNewUser(idStrArry);
		}
		if ("REL_USER".equals(action)) {
			String deptString = "";
			String roleString = "";
			String personString = "";
			fileSort2.setRelUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String deptTem = "";
			String roleTem = "";
			String personTem = "";
			if (!"".equals(dbDeptIdStrs.trim()) && !"".equals(deptIdStrs.trim())) {
				deptTem = ",";
			}
			if (!"".equals(dbRoleIdStrs.trim()) && !"".equals(roleIdStrs.trim())) {
				roleTem = ",";
			}
			if (!"".equals(dbPersonIdStrs.trim()) && !"".equals(personIdStrs.trim())) {
				personTem = ",";
			}

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				deptString = dbDeptIdStrs + deptTem + deptIdStrs;
				alldeptStr = this.delReIdStr(deptString);
			} else {
				alldeptStr = "0";
			}

			roleString = dbRoleIdStrs + roleTem + roleIdStrs;
			personString = dbPersonIdStrs + personTem + personIdStrs;

			String allRoleStr = this.delReIdStr(roleString);
			String personStr = this.delReIdStr(personString);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setRelUser(idStrArry);
		}
		orm.updateSingle(dbConn, fileSort);
	}
	
	public String getDeptIds(Connection dbConn, T9CmsColumn fileSort, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		if (fileSort != null) {
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getEditUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = T9Utility.null2Empty(fileSort.getDelUser());
          String[] idsStrings = idString.split("\\|");
          if (!"".equals(idString.trim()) && idsStrings.length != 0) {
            ids = idsStrings[0];
          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("EDIT_USER_CONTENT".equals(action)) {
				if (fileSort.getEditUserContent() != null) {
					String idString = T9Utility.null2Empty(fileSort.getEditUserContent());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("APPROVAL_USER_CONTENT".equals(action)) {
				if (fileSort.getApprovalUserContent() != null) {
					String idString = T9Utility.null2Empty(fileSort.getApprovalUserContent());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("RELEASE_USER_CONTENT".equals(action)) {
				if (fileSort.getReleaseUserContent() != null) {
					String idString = T9Utility.null2Empty(fileSort.getReleaseUserContent());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("RECEVIE_USER_CONTENT".equals(action)) {
				if (fileSort.getRecevieUserContent() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRecevieUserContent());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("ORDER_CONTENT".equals(action)) {
				if (fileSort.getOrderContent() != null) {
					String idString = T9Utility.null2Empty(fileSort.getOrderContent());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} 
		}
		return ids;
	}
	public String getDeptIds(Connection dbConn, T9CmsStation fileSort, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		// ArrayList<T9CmsColumn> managerList = (ArrayList<T9CmsColumn>)
		// orm.loadListSingle(dbConn, T9CmsColumn.class, map);
		if (fileSort != null) {
			// T9CmsColumn manager = managerList.get(0);
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);

					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getEditUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = T9Utility.null2Empty(fileSort.getDelUser());
          // System.out.println(idString);
          String[] idsStrings = idString.split("\\|");
          // System.out.println(idsStrings);
          if (!"".equals(idString.trim()) && idsStrings.length != 0) {
            ids = idsStrings[0];
          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						ids = idsStrings[0];
					}
				}
			} 
		}
		// System.out.println("ids=====" + ids);
		return ids;
	}
	
	public String getDeptByIds(Connection dbConn, Map map, String action) throws Exception {
		String names = "";
		T9CmsColumn fileSort = this.getFileSortInfoById(dbConn, map);
		String ids = getDeptIds(dbConn, fileSort, action);
		// System.out.println(ids);
		if (!"ALL_DEPT".equals(ids.trim())) {
			names = this.getDeptNameBySeqIdStr(dbConn, ids);
		}
		return names;
	}
	
	public T9CmsColumn getFileSortInfoById(Connection dbConn, Map map) throws Exception {
		T9ORM orm = new T9ORM();
		return (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, map);

	}

	/**
	 * 根据seqId串返回一个部门名字串
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public String getDeptNameBySeqIdStr(Connection dbConn, String ids) throws Exception, Exception {
		String names = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim()) && !"ALL_DEPT".equals(tmp.trim())) {
					T9Department deptName = getDeptById(dbConn, Integer.parseInt(tmp.trim()));
					if (deptName != null) {
						names += deptName.getDeptName() + ",";
					}
				}
			}
		}
		return names;
	}

	/**
	 * 根据登录用户的部门Id与权限中的部门Id对比返回boolean
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getDeptIdStr(int loginUserDeptId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids)) {
			if ("0".equals(ids.trim()) || "ALL_DEPT".equals(ids.trim())) {
				return flag = true;
			}
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim())) {
					if (loginUserDeptId == Integer.parseInt(tmp)) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}
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
	/**
	 * 根据seqId查询部门信息
	 * 
	 * @param dbConn
	 * @param seqId
	 * @return T9Department对象
	 * @throws Exception
	 */
	public T9Department getDeptById(Connection dbConn, int seqId) throws Exception {
		T9ORM orm = new T9ORM();
		T9Department department = (T9Department) orm.loadObjSingle(dbConn, T9Department.class, seqId);
		;
		return department;
	}

	/**
	 * 得到该部门下的全体人员id串(有权限)
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getDeptPersonIdStr(int userSeqId, String ids, Connection dbConn) throws Exception {
		String idStr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim()) && !"0".equals(tmp.trim())) {
					if (Integer.parseInt(tmp) == userSeqId) {
						Map deptIdmMap = new HashMap();
						deptIdmMap.put("DEPT_ID", tmp);
						List<T9Person> list = this.getPersonsByDeptId(dbConn, deptIdmMap);
						if (list != null && list.size() != 0) {
							for (T9Person person : list) {
								idStr += person.getSeqId() + ",";
							}
						}
					}
				} else if (!"".equals(tmp.trim()) && "0".equals(tmp.trim())) {
					// Map deptIdmMap = new HashMap();
					// deptIdmMap.put("DEPT_ID", tmp);
					String[] filters = { "dept_id!=0 and not_login=0" };
					List<T9Person> list = this.getPersonsByDeptIdStr(dbConn, filters);
					if (list != null && list.size() != 0) {
						for (T9Person person : list) {
							idStr += person.getSeqId() + ",";
						}
					}
				}
			}
		}
		return idStr.trim();
	}
	
	
	/**
	 * 得到该部门下的全体人员id串(无权限)
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getDeptPersonIds(String ids, Connection dbConn) throws Exception {
		String idStr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!T9Utility.isNullorEmpty(tmp.trim()) && !"0".equals(tmp.trim())) {
					Map deptIdmMap = new HashMap();
					deptIdmMap.put("DEPT_ID", tmp);
					List<T9Person> list = this.getPersonsByDeptId(dbConn, deptIdmMap);
					if (list != null && list.size() != 0) {
						for (T9Person person : list) {
							idStr += person.getSeqId() + ",";
						}
					}
				} else if (!T9Utility.isNullorEmpty(tmp.trim()) && "0".equals(tmp.trim())) {
					String[] filters = { "dept_id!=0 and not_login=0" };
					List<T9Person> list = this.getPersonsByDeptIdStr(dbConn, filters);
					if (list != null && list.size() != 0) {
						for (T9Person person : list) {
							idStr += person.getSeqId() + ",";
						}
					}
				}
			}
		}
		return idStr.trim();
	}

	public List<T9Person> getPersonsByDeptIdStr(Connection dbConn, String[] filters) throws Exception {
		T9ORM orm = new T9ORM();
		return orm.loadListSingle(dbConn, T9Person.class, filters);
	}

	/**
	 * 得到有权限的部门id串

	 * 
	 * 
	 * 
	 * @param dbConn
	 * @param loginUserDeptId
	 * @param ids
	 * @return
	 */
	public String getPrivDeptIdStr(Connection dbConn, int loginUserDeptId, String ids) {
		String idstr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp.trim()) && !"0".equals(tmp.trim())) {
					if (Integer.parseInt(tmp) == loginUserDeptId) {
						idstr += tmp + ",";
					}
				} else if (!"".equals(tmp.trim()) && "0".equals(tmp.trim())) {

					idstr += tmp + ",";

				}
			}
		}

		return idstr.trim();
	}

	/**
	 * 得到该角色下的全体人员id串(有权限)
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getRolePersonIdStr(int userSeqId, String ids, Connection dbConn) throws Exception {
		String idStr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == userSeqId) {
						Map deptIdmMap = new HashMap();
						deptIdmMap.put("USER_PRIV  ", tmp);
						List<T9Person> list = this.getPersonsByDeptId(dbConn, deptIdmMap);
						if (list != null && list.size() != 0) {
							for (T9Person person : list) {
								idStr += person.getSeqId() + ",";
							}
						}
					}
				}
			}
		}
		return idStr.trim();
	}

	/**
	 * 得到该角色下的全体人员id串(无权限)
	 * 
	 * @param dbConn
	 * @param deptIdStr
	 * @return
	 * @throws Exception
	 */
	public String getRolePersonIds(String ids, Connection dbConn) throws Exception {
		String idStr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					Map deptIdmMap = new HashMap();
					deptIdmMap.put("USER_PRIV  ", tmp);
					List<T9Person> list = this.getPersonsByDeptId(dbConn, deptIdmMap);
					if (list != null && list.size() != 0) {
						for (T9Person person : list) {
							idStr += person.getSeqId() + ",";
						}
					}
				}
			}
		}
		return idStr.trim();
	}

	/**
	 * 得到有权限的角色id串

	 * 
	 * 
	 * 
	 * @param dbConn
	 * @param loginUserDeptId
	 * @param ids
	 * @return
	 */
	public String getPrivRoleIdStr(Connection dbConn, int loginUserRoleId, String ids) {
		String idstr = "";
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == loginUserRoleId) {
						idstr += tmp;
					}
				}
			}
		}

		return idstr.trim();
	}

	public List<T9Person> getPersonsByDeptId(Connection dbConn, Map deptIdmMap) throws Exception {
		T9ORM orm = new T9ORM();
		List list = new ArrayList();

		return orm.loadListComplex(dbConn, T9Person.class, deptIdmMap);

		// return list;
	}

	/**
	 * 获取访问权限：根据ids串返回与登录的seqId比较判断是否相等，返回boolean类型。

	 * 
	 * 
	 * 
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getVisitUserStr(int userSeqId, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		if (ids != null && !"".equals(ids)) {
			String[] aId = ids.split(",");
			for (String tmp : aId) {
				if (!"".equals(tmp)) {
					if (Integer.parseInt(tmp) == userSeqId) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}
	
	
	/*
	 * 得到人员的id字符串

	 */
	public String selectManagerIds(Connection dbConn, T9CmsStation fileSort, String action) throws Exception {
		String ids = "";
		if (fileSort != null) {
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}

					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getEditUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}

					}
				}
			}  else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = T9Utility.null2Empty(fileSort.getDelUser());
          String[] idsStrings = idString.split("\\|");
          // System.out.println(idsStrings);
          if (idsStrings.length != 0) {
            if (idsStrings.length == 2) {
              ids = "";
            } else if (idsStrings.length == 1) {
              ids = "";

            } else {
              ids = idsStrings[2];
            }

          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}
					}

				}
			} else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}
					}

				}
			} 
		}
		// System.out.println("ids=====" + ids);
		return ids;
	}
	/*
	 * 得到人员的id字符串

	 */
	public String selectManagerIds(Connection dbConn, T9CmsColumn fileSort, String action) throws Exception {
		String ids = "";
		if (fileSort != null) {
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}

					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getEditUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}

					}
				}
			}  else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = T9Utility.null2Empty(fileSort.getDelUser());
          String[] idsStrings = idString.split("\\|");
          // System.out.println(idsStrings);
          if (idsStrings.length != 0) {
            if (idsStrings.length == 2) {
              ids = "";
            } else if (idsStrings.length == 1) {
              ids = "";

            } else {
              ids = idsStrings[2];
            }

          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}
					}

				}
			} else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					String[] idsStrings = idString.split("\\|");
					// System.out.println(idsStrings);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 2) {
							ids = "";
						} else if (idsStrings.length == 1) {
							ids = "";

						} else {
							ids = idsStrings[2];
						}
					}

				}
			} 
			 else if ("EDIT_USER_CONTENT".equals(action)) {
			        if (fileSort.getEditUserContent() != null) {
			          String idString = T9Utility.null2Empty(fileSort.getEditUserContent());
			          String[] idsStrings = idString.split("\\|");
			          // System.out.println(idsStrings);
			          if (idsStrings.length != 0) {
			            if (idsStrings.length == 2) {
			              ids = "";
			            } else if (idsStrings.length == 1) {
			              ids = "";

			            } else {
			              ids = idsStrings[2];
			            }

			          }
			        }
			      } else if ("APPROVAL_USER_CONTENT".equals(action)) {
			          if (fileSort.getApprovalUserContent() != null) {
			              String idString = T9Utility.null2Empty(fileSort.getApprovalUserContent());
			              String[] idsStrings = idString.split("\\|");
			              // System.out.println(idsStrings);
			              if (idsStrings.length != 0) {
			                if (idsStrings.length == 2) {
			                  ids = "";
			                } else if (idsStrings.length == 1) {
			                  ids = "";

			                } else {
			                  ids = idsStrings[2];
			                }

			              }
			            }
			          } else if ("RELEASE_USER_CONTENT".equals(action)) {
			              if (fileSort.getReleaseUserContent() != null) {
			                  String idString = T9Utility.null2Empty(fileSort.getReleaseUserContent());
			                  String[] idsStrings = idString.split("\\|");
			                  // System.out.println(idsStrings);
			                  if (idsStrings.length != 0) {
			                    if (idsStrings.length == 2) {
			                      ids = "";
			                    } else if (idsStrings.length == 1) {
			                      ids = "";

			                    } else {
			                      ids = idsStrings[2];
			                    }

			                  }
			                }
			              } else if ("RECEVIE_USER_CONTENT".equals(action)) {
			                  if (fileSort.getRecevieUserContent() != null) {
			                      String idString = T9Utility.null2Empty(fileSort.getRecevieUserContent());
			                      String[] idsStrings = idString.split("\\|");
			                      // System.out.println(idsStrings);
			                      if (idsStrings.length != 0) {
			                        if (idsStrings.length == 2) {
			                          ids = "";
			                        } else if (idsStrings.length == 1) {
			                          ids = "";

			                        } else {
			                          ids = idsStrings[2];
			                        }

			                      }
			                    }
			                  } else if ("ORDER_CONTENT".equals(action)) {
			                      if (fileSort.getOrderContent() != null) {
			                          String idString = T9Utility.null2Empty(fileSort.getOrderContent());
			                          String[] idsStrings = idString.split("\\|");
			                          // System.out.println(idsStrings);
			                          if (idsStrings.length != 0) {
			                            if (idsStrings.length == 2) {
			                              ids = "";
			                            } else if (idsStrings.length == 1) {
			                              ids = "";

			                            } else {
			                              ids = idsStrings[2];
			                            }

			                          }
			                        }
			                      }
		}
		// System.out.println("ids=====" + ids);
		return ids;
	}
	
	
	public String getRoleIds(Connection dbConn, T9CmsStation fileSort, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		// ArrayList<T9CmsColumn> managerList = (ArrayList<T9CmsColumn>)
		// orm.loadListSingle(dbConn, T9CmsColumn.class, map);
		if (fileSort != null) {
			// T9CmsColumn manager = managerList.get(0);
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = fileSort.getEditUser();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}  else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = fileSort.getDelUser();
          // System.out.println(idString);
          String[] idsStrings = idString.split("\\|");
          // System.out.println("角色idsStrings:" + idsStrings.length);
          if (!"".equals(idString.trim()) && idsStrings.length != 0) {
            if (idsStrings.length == 1) {
              ids = "";
            } else {
              ids = idsStrings[1];
            }
          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}  else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}

		}
		// System.out.println("ids=====" + ids);
		return ids;
	}
	
	public String getRoleIds(Connection dbConn, T9CmsColumn fileSort, String action) throws Exception {
		T9ORM orm = new T9ORM();
		String ids = "";
		if (fileSort != null) {
			if ("VISIT_USER".equals(action)) {
				if (fileSort.getVisitUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getVisitUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			} else if ("EDIT_USER".equals(action)) {
				if (fileSort.getEditUser() != null) {
					String idString = fileSort.getEditUser();
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}  else if ("DEL_USER".equals(action)) {
        if (fileSort.getDelUser() != null) {
          String idString = fileSort.getDelUser();
          String[] idsStrings = idString.split("\\|");
          if (!"".equals(idString.trim()) && idsStrings.length != 0) {
            if (idsStrings.length == 1) {
              ids = "";
            } else {
              ids = idsStrings[1];
            }
          }
        }
      } else if ("NEW_USER".equals(action)) {
				if (fileSort.getNewUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getNewUser());
					String[] idsStrings = idString.split("\\|");
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}  else if ("REL_USER".equals(action)) {
				if (fileSort.getRelUser() != null) {
					String idString = T9Utility.null2Empty(fileSort.getRelUser());
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}else if ("EDIT_USER_CONTENT".equals(action)) {
				if (fileSort.getEditUserContent() != null) {
					String idString = fileSort.getEditUserContent();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}else if ("APPROVAL_USER_CONTENT".equals(action)) {
				if (fileSort.getApprovalUserContent() != null) {
					String idString = fileSort.getApprovalUserContent();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}else if ("RELEASE_USER_CONTENT".equals(action)) {
				if (fileSort.getReleaseUserContent() != null) {
					String idString = fileSort.getReleaseUserContent();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}else if ("RECEVIE_USER_CONTENT".equals(action)) {
				if (fileSort.getRecevieUserContent() != null) {
					String idString = fileSort.getRecevieUserContent();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}else if ("ORDER_CONTENT".equals(action)) {
				if (fileSort.getOrderContent() != null) {
					String idString = fileSort.getOrderContent();
					// System.out.println(idString);
					String[] idsStrings = idString.split("\\|");
					// System.out.println("角色idsStrings:" + idsStrings.length);
					if (!"".equals(idString.trim()) && idsStrings.length != 0) {
						if (idsStrings.length == 1) {
							ids = "";
						} else {
							ids = idsStrings[1];
						}
					}
				}
			}

		}
		// System.out.println("ids=====" + ids);
		return ids;
	}
	/**
	 * 批量设置权限（删除权限）
	 * 
	 * @param dbConn
	 * @param seqId
	 * @param setIdStr
	 * @throws Exception
	 */
	public void updateVisitOverrideDel(Connection dbConn, int seqId, String setIdStr, String action,String flag) throws Exception {
		T9ORM orm = new T9ORM();
		if("column".equals(flag)||"content".equals(flag)){
			T9CmsColumn fileSort = (T9CmsColumn) orm.loadObjSingle(dbConn, T9CmsColumn.class, seqId);
			
		  	getChildFolderDel(dbConn, fileSort, setIdStr, action);
		}
		else if("station".equals(flag)){
			T9CmsStation fileSort = (T9CmsStation) orm.loadObjSingle(dbConn, T9CmsStation.class, seqId);
			getChildFolderDel(dbConn, fileSort, setIdStr, action);
		}
	}
	public void updateColumnChildDel(Connection dbConn, int seqId, String setIdStr, String action,String flag)throws Exception{
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildFolderDel(dbConn, sort, setIdStr, action);
		}
	}
	
	public void updateContentDel(Connection dbConn, int seqId, String setIdStr, String action,String flag)throws Exception{
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildFolderDel(dbConn, sort, setIdStr, action);
		}
	}
	
	
	public void updateContentStationDel(Connection dbConn, int seqId, String setIdStr, String action,String flag)throws Exception{
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("STATION_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildFolderDel(dbConn, sort, setIdStr, action);
		}
	}
	
	
	
	public void updateStationChildDel(Connection dbConn, int seqId, String setIdStr, String action,String flag)throws Exception{
		T9ORM orm = new T9ORM();
		Map map = new HashMap();
		map.put("STATION_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);
		for (T9CmsColumn sort : list) {
			getChildFolderDel(dbConn, sort, setIdStr, action);
		}
	}

	public void getChildFolderDel(Connection dbConn, T9CmsStation fileSort, String setIdStr, String action) throws Exception {
		T9CmsStation fileSort2 = new T9CmsStation();

		T9ORM orm = new T9ORM();
		int seqId = fileSort.getSeqId();
		if ("VISIT_USER".equals(action)) {
			fileSort2.setVisitUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setVisitUser(idStrArry);
		}
		if ("EDIT_USER".equals(action)) {
			fileSort2.setEditUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setEditUser(idStrArry);
		}
		if ("DEL_USER".equals(action)) {
      fileSort2.setDelUser(setIdStr);
      String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
      String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
      String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

      String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
      String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
      String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

      String alldeptStr = "";

      if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
      } else if (!"0".equals(deptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        dbDeptIdStrs = this.getAlldept(dbConn);
        alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
      }

      String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
      String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

      String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
      fileSort.setDelUser(idStrArry);
    }
		if ("NEW_USER".equals(action)) {
			fileSort2.setNewUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			fileSort.setNewUser(idStrArry);
		}
		if ("REL_USER".equals(action)) {
			fileSort2.setRelUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			fileSort.setRelUser(idStrArry);
		}
		orm.updateSingle(dbConn, fileSort);

	}
	/**
	 * 更新子栏目权限（批量删除权限）
	 * @param dbConn
	 * @param fileSort
	 * @param setIdStr
	 * @param action
	 * @throws Exception
	 */
	
	public void getChildFolderDel(Connection dbConn, T9CmsColumn fileSort, String setIdStr, String action) throws Exception {
		T9CmsColumn fileSort2 = new T9CmsColumn();

		T9ORM orm = new T9ORM();
		int seqId = fileSort.getSeqId();
		if ("VISIT_USER".equals(action)) {
			fileSort2.setVisitUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setVisitUser(idStrArry);
		}
		if ("EDIT_USER".equals(action)) {
			fileSort2.setEditUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setEditUser(idStrArry);
		}
		if ("DEL_USER".equals(action)) {
      fileSort2.setDelUser(setIdStr);
      String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
      String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
      String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

      String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
      String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
      String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

      String alldeptStr = "";

      if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
      } else if (!"0".equals(deptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
        dbDeptIdStrs = this.getAlldept(dbConn);
        alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
      }

      String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
      String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

      String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
      fileSort.setDelUser(idStrArry);
    }
		if ("NEW_USER".equals(action)) {
			fileSort2.setNewUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			fileSort.setNewUser(idStrArry);
		}
		if ("REL_USER".equals(action)) {
			fileSort2.setRelUser(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;

			fileSort.setRelUser(idStrArry);
		}
		if ("EDIT_USER_CONTENT".equals(action)) {
			fileSort2.setEditUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setEditUserContent(idStrArry);
		}
		if ("APPROVAL_USER_CONTENT".equals(action)) {
			fileSort2.setApprovalUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setApprovalUserContent(idStrArry);
		}
		if ("RELEASE_USER_CONTENT".equals(action)) {
			fileSort2.setReleaseUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setReleaseUserContent(idStrArry);
		}
		if ("RECEVIE_USER_CONTENT".equals(action)) {
			fileSort2.setRecevieUserContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setRecevieUserContent(idStrArry);
		}
		if ("ORDER_CONTENT".equals(action)) {
			fileSort2.setOrderContent(setIdStr);
			String deptIdStrs = this.getDeptIds(dbConn, fileSort2, action);
			String roleIdStrs = this.getRoleIds(dbConn, fileSort2, action);
			String personIdStrs = this.selectManagerIds(dbConn, fileSort2, action);

			String dbDeptIdStrs = this.getDeptIds(dbConn, fileSort, action);
			String dbRoleIdStrs = this.getRoleIds(dbConn, fileSort, action);
			String dbPersonIdStrs = this.selectManagerIds(dbConn, fileSort, action);

			String alldeptStr = "";

			if (!"0".equals(dbDeptIdStrs.trim()) && !"0".equals(deptIdStrs.trim())) {
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			} else if (!"0".equals(deptIdStrs.trim())) {
				dbDeptIdStrs = this.getAlldept(dbConn);
				alldeptStr = this.getDelIdStrs(deptIdStrs, dbDeptIdStrs);
			}

			String allRoleStr = this.getDelIdStrs(roleIdStrs, dbRoleIdStrs);
			String personStr = this.getDelIdStrs(personIdStrs, dbPersonIdStrs);

			String idStrArry = alldeptStr + "|" + allRoleStr + "|" + personStr;
			fileSort.setOrderContent(idStrArry);
		}
		orm.updateSingle(dbConn, fileSort);
		Map map = new HashMap();
		map.put("PARENT_ID", seqId);
		List<T9CmsColumn> list = getFileSorts(dbConn, map);

		for (T9CmsColumn sort : list) {
			getChildFolderDel(dbConn, sort, setIdStr, action);
		}

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

				// String[] dbIdstrArry = dbIdStrs.split(",");
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
	 * 当deptId等于0时调用此方法取得所有deptId
	 * 
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String getAlldept(Connection conn) throws Exception {
		String result = "";
		String sql = "select SEQ_ID FROM DEPARTMENT";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				int deptId = rs.getInt(1);
				if (!"".equals(result)) {
					result += ",";
				}
				result += deptId;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		return result;
	}

}
