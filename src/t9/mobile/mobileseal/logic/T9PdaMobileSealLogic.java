package t9.mobile.mobileseal.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.mobile.mobileseal.data.T9MobileDevice;
import t9.mobile.mobileseal.data.T9MobileSeal;
import t9.mobile.util.T9MobileString;
import t9.mobile.util.T9MobileUtility;

public class T9PdaMobileSealLogic {
  public String getSealData(Connection conn , int seqId) throws Exception{
    String sql = " select SEAL_DATA from mobile_seal where SEQ_ID ="+seqId;
    PreparedStatement ps = null;
    ResultSet rs = null ;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next()){
        return rs.getString(1);
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, null);
    }
    return "";
  }
	/**
	 *通过ID获取 印章名称和印章
	 * @param dbConn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Map getSealById(Connection dbConn,String sql) throws Exception{
		Map map = new HashMap<String,String>();
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        if (rs.next()) {
	        	map.put("SEAL_NAME", rs.getString("SEAL_NAME"));
	        	map.put("SEAL_DATA",rs.getString("SEAL_DATA"));
	        }
	        
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      return map;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	/**
	 * 
	 * @param dbConn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List getDeviceListBySql(Connection dbConn,String sql) throws Exception{
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      List list = new ArrayList();
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	        	String SEQ_ID = rs.getString("SEQ_ID");
	        	String DEVICE_NAME = rs.getString("DEVICE_NAME");
	        	T9MobileDevice md = new T9MobileDevice();
	        	md.setSeqId(rs.getInt("SEQ_ID"));
	        	md.setDeviceName(rs.getString("DEVICE_NAME"));
	        	md.setSubmitTime(rs.getDate("SUBMIT_TIME"));
	        	list.add(md);
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      
	      return list;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	
	public List getSealListBySql(Connection dbConn,String sql) throws Exception{
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      List list = new ArrayList();
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        while (rs.next()) {
	        	T9MobileSeal ms = new T9MobileSeal();
	        	ms.setSeqId(rs.getInt("SEQ_ID"));
	        	ms.setSealName(rs.getString("SEAL_NAME"));
	        	list.add(ms);
	        }
	      } catch (Exception e) {
	        throw e;
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      return list;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	/**
	 * 通过设备list 获取印章list
	 * @param dbConn
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List getSealListByDevList(Connection dbConn,List list) throws Exception{
		List sealList = new ArrayList();
		
		if(list == null || list.size() <1){
			return null;
		}
		for(int i=0;i<list.size();i++){
			T9MobileDevice md = (T9MobileDevice)list.get(i);
			getSealBydeviceId(dbConn, md, sealList);
		}
	    return sealList;
	 }
	/**
	 * 获取 印章
	 * @param dbConn
	 * @param md
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public List getSealBydeviceId(Connection dbConn,T9MobileDevice md,List list) throws Exception{
//		Map map = new HashMap<String,String>();
	
		StringBuffer sb = new StringBuffer();
		if(md == null){
			return null;
		}
		int dId = md.getSeqId();//SELECT * from mobile_seal where find_in_set('$device_id', DEVICE_LIST)
		sb.append("SELECT * from mobile_seal where ");
		sb.append(T9DBUtility.findInSet(String.valueOf(dId),"DEVICE_LIST"));
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      try {
	        ps = dbConn.prepareStatement(sb.toString());
	        rs = ps.executeQuery();
	        String SEAL_ID_TMP = "";
	        while (rs.next()) {
	        	if(T9MobileUtility.find_id(SEAL_ID_TMP, rs.getString("SEQ_ID")))
            		continue;
	          T9MobileSeal ms = new T9MobileSeal();
	        	ms.setSeqId(rs.getInt("SEQ_ID"));
	        	ms.setSealName(rs.getString("SEAL_NAME"));
	        	list.add(ms);
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      return list;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	/**
	 * 通过id获取设备信息
	 * @param dbConn
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public Map getDeviceById(Connection dbConn,String sql) throws Exception{
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      Map map = new HashMap<String,String>();
	      try {
	        ps = dbConn.prepareStatement(sql);
	        rs = ps.executeQuery();
	        if (rs.next()) {
	        	int deType = 0;
	        	if(T9Utility.isInteger(rs.getString("DEVICE_TYPE"))){
	        		deType  =  Integer.parseInt(rs.getString("DEVICE_TYPE"));
	        	}
	        	map.put("seqId",rs.getInt("SEQ_ID") );
	        	map.put("deviceType",rs.getString("DEVICE_TYPE"));
	        	map.put("deviceTypeDesc",changeDeviceDesc(deType) );
	        	map.put("deviceName", rs.getString("DEVICE_NAME"));
	        	map.put("md5cheeck", rs.getString("MD5_CHECK"));
	        	map.put("submitTime",rs.getString("SUBMIT_TIME") );
	        	
	        }
	        
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      
	      return map;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	/**
	 * 取某一表中的某一字段值
	 * @param dbConn
	 * @param tableName
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public String getDateByField(Connection dbConn,String tableName,String field,String sWhere) throws Exception{
	    try {
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      String value = "";
	      if(sWhere == null || "".equals(sWhere)){
	    	  sWhere = " 1=1";
	      }
	      try {
	        ps = dbConn.prepareStatement("select * from "+tableName +" where "+sWhere);
	        rs = ps.executeQuery();
	        if (rs.next()) {
	        	value = rs.getString(field);
	        }
	      } catch (Exception e) {
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, rs, null);
	      }
	      return value;
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	/**
	 * 转换 设备描述
	 * @param DEVICE_TYPE
	 * @return
	 */
	private String changeDeviceDesc(int DEVICE_TYPE){
		String DEVICE_TYPE_DESC = "";
		 switch(DEVICE_TYPE)
	      {
	        case 0:
	            DEVICE_TYPE_DESC = "<span style='color:blue'>待批准</span>";
	            break;
	        case 1:
	            DEVICE_TYPE_DESC = "<span style='color:green'>已批准</span>";
	            break;
	        case 2:
	            DEVICE_TYPE_DESC = "<span style='color:red'>待批准</span>";
	            break;
	      }
		return DEVICE_TYPE_DESC;
	}
	
	/**
	 * 插入设备信息 
	 * @param dbConn
	 * @param md
	 * @throws Exception
	 */
	public void insertDevice(Connection dbConn,T9MobileDevice md,int pid) throws Exception{
	    try {
	      PreparedStatement ps = null;
	      try {
	        ps = dbConn.prepareStatement("insert into MOBILE_DEVICE (UID,SUBMIT_TIME,DEVICE_TYPE,DEVICE_INFO,DEVICE_NAME,MD5_CHECK) VALUES (?,?,?,?,?,?)");
	        ps.setInt(1, pid);
	        ps.setDate(2, new java.sql.Date(new Date().getTime()));
	        ps.setInt(3, md.getDeviceType());
	        ps.setString(4, md.getDeviceInfo());
	        ps.setString(5, md.getDeviceName());
	        ps.setString(6, md.getMd5Check());
	        ps.execute();
	      } catch (Exception e) {
	    	  dbConn.rollback();
	        e.printStackTrace();
	      } finally {
	        T9DBUtility.close(ps, null, null);
	      }
	    } catch (Exception e) {
	      throw e;
	    }
	 }
	
	
}
