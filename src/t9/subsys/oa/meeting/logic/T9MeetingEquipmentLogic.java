package t9.subsys.oa.meeting.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.meeting.data.T9MeetingEquipment;
import t9.subsys.oa.meeting.data.T9MeetingRoom;

public class T9MeetingEquipmentLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.meeting.logic.T9MeetingEquipmentLogic.java");

	/**
	 * 设备管理列表--cc
	 * @param dbConn
	 * @param request
	 * @param cycleNo
	 * @return
	 * @throws Exception
	 */
	public String getMeetingEquipmentList(Connection dbConn, Map request, String cycleNo) throws Exception {
    String sql = "select " 
            + "  SEQ_ID" 
            + ", GROUP_NO" 
            + ", EQUIPMENT_NO" 
            + ", EQUIPMENT_NAME" 
            + ", MR_ID" 
            + ", EQUIPMENT_STATUS" 
            + ", GROUP_YN"
            + ", REMARK"
            + " from MEETING_EQUIPMENT order by MR_ID";
    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
    return pageDataList.toJson();
  }
	
	/**
	 * 获取小编码表内容--同类设备名称--cc
	 * @param conn
	 * @param classCode
	 * @param classNo
	 * @return
	 * @throws Exception
	 */
	public String getCodeNameLogic(Connection conn, String classCode) throws Exception {
    String result = "";
    String sql = " select CLASS_DESC from CODE_ITEM where CLASS_CODE = '" + classCode + "' and CLASS_NO = 'MEETING_EQUIPMENT'";
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
	
	
	 /**
   * 删除单个设备管理--cc
   * 
   * @param conn
   * @param seqId
   * @throws Exception
   */
  public void deleteSingle(Connection conn, int seqId) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.deleteSingle(conn, T9MeetingEquipment.class, seqId);
    } catch (Exception ex) {
      throw ex;
    } finally {
    }
  }

  /**
   * 设备查询--cc
   * @param dbConn
   * @param request
   * @param person
   * @param mName
   * @param mProposer
   * @param beginDate
   * @param endDate
   * @param mRoom
   * @param mStatus
   * @return
   * @throws Exception
   */
  public String getMeetingEquiomentSearchJson(Connection dbConn, Map request,  String equipmentNo, String equipmentName, String equipmentStatus,
      String mrId, String remark) throws Exception {
    String sql = "select " 
      + "  SEQ_ID" 
      + ", GROUP_NO" 
      + ", EQUIPMENT_NO" 
      + ", EQUIPMENT_NAME" 
      + ", MR_ID" 
      + ", EQUIPMENT_STATUS" 
      + ", GROUP_YN"
      + ", REMARK"
      + " from MEETING_EQUIPMENT where 1=1";

    if (!T9Utility.isNullorEmpty(equipmentNo)) {
      sql = sql + " and EQUIPMENT_NO like '%" + equipmentNo + "%'" + T9DBUtility.escapeLike();
    }
    if (!T9Utility.isNullorEmpty(equipmentName)) {
      sql = sql + " and EQUIPMENT_NAME like '%" + equipmentName + "%'" + T9DBUtility.escapeLike();
    }
    
    if (!T9Utility.isNullorEmpty(equipmentStatus)) {
      sql = sql + " and EQUIPMENT_STATUS = '" + equipmentStatus + "'";
    }

    if (!T9Utility.isNullorEmpty(mrId)) {
      sql = sql + " and MR_ID ='" + mrId + "'";
    }

    if (!T9Utility.isNullorEmpty(remark)) {
      sql = sql + " and REMARK like '%" + remark + "%'" + T9DBUtility.escapeLike();
    }
    sql = sql + " order by EQUIPMENT_NO";

    T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
    T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);

    return pageDataList.toJson();
  }
	/**
	 * 获取会议室名称
	 * 
	 * @param dbConn
	 * @return
	 * @throws Exception
	 */
	public String getMRoomNameLogic(Connection dbConn) throws Exception {
		StringBuffer buffer = new StringBuffer("[");
		try {
			List<T9MeetingRoom> meetingRooms = this.getMRoomList(dbConn);
			boolean isHave = false;
			if (meetingRooms != null && meetingRooms.size() > 0) {
				for (T9MeetingRoom meetingRoom : meetingRooms) {
					int dbSeqId = meetingRoom.getSeqId();
					String mrName = T9Utility.null2Empty(meetingRoom.getMrName());
					buffer.append("{");
					buffer.append("value:" + dbSeqId);
					buffer.append(",text:\"" + T9Utility.encodeSpecial(mrName) + "\"");
					buffer.append("},");
					isHave = true;
				}
			}
			if (isHave) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
			buffer.append("]");
		} catch (Exception e) {
			throw e;
		}
		return buffer.toString();
	}

	public List<T9MeetingRoom> getMRoomList(Connection dbConn) throws Exception {
		T9ORM orm = new T9ORM();
		try {
			Map map = new HashMap();
			return orm.loadListSingle(dbConn, T9MeetingRoom.class, map);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 添加设备信息
	 * 
	 * @param conn
	 * @param equipment
	 * @throws Exception
	 */
	public void addEquipmentLogic(Connection dbConn, T9MeetingEquipment equipment) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.saveSingle(dbConn, equipment);
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 根据会议室seqId获取设备信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getEquipmentByIdLogic(Connection dbConn, int mRooId) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{selectDiv:\"");
		boolean isHaveBuffer = this.getEquipmentCheckBoxLogic(dbConn, mRooId, buffer);
		
		T9ORM orm =new T9ORM();
		String[] filters = { " SEQ_ID in(select min(SEQ_ID) from MEETING_EQUIPMENT where GROUP_YN='1' and EQUIPMENT_STATUS='1' and MR_ID=" + mRooId + " group by GROUP_NO)" };
		List<T9MeetingEquipment > equipments=orm.loadListSingle(dbConn, T9MeetingEquipment.class, filters);
	
		if (equipments!=null && equipments.size()>0) {
			
			for(T9MeetingEquipment equipment:equipments){
				int groupNoCount =0;
				String  groupNo1= T9Utility.null2Empty(equipment.getGroupNo());
				
				String[] queryStr = {" GROUP_YN='1' and EQUIPMENT_STATUS='1' and MR_ID=" + mRooId + " and GROUP_NO='" + groupNo1 + "'"};
				List<T9MeetingEquipment > equipments2 = orm.loadListSingle(dbConn, T9MeetingEquipment.class, queryStr);
				
				boolean isHave = false;
				if (equipments2 != null && equipments2.size() > 0) {
					for(T9MeetingEquipment equ:equipments2){
						int equId = equ.getSeqId();
						String equipmentName = T9Utility.null2Empty(equ.getEquipmentName());
//						String remark = T9Utility.null2Empty(equ.getRemark());
						groupNoCount ++;
						
						if (groupNoCount == 1) {
							String classDesc = this.getEquClassDescName(dbConn, groupNo1);
							buffer.append(" &nbsp;&nbsp;<select name='checkSelectStr' class=''> ");
							buffer.append("  <option value=''>选择" + classDesc + "</option>");
							isHave = true;
						}
						buffer.append("<option value='" + equId + "' title='" + equId + "' > " + T9Utility.encodeSpecial(equipmentName) + " </option>" );
					}
					if (isHave) {
						buffer.append("</select>");
					}
				}
			}
			buffer.append( "\"}");
		}else {
			if (isHaveBuffer) {
				buffer.append( "\"}");			
			}else {
				buffer.append("0");
				buffer.append( "\"}");			
			}
		}
		return buffer.toString();
	}
	/**
	 * 根据会议室seqId获取设备复选框信息
	 * @return
	 * @throws Exception
	 */
	public boolean getEquipmentCheckBoxLogic(Connection dbConn, int mRooId,StringBuffer buffer) throws Exception {
		T9ORM orm =new T9ORM();
		boolean isHave = false;
		String[] filters = { " GROUP_YN='0' and EQUIPMENT_STATUS='1' and MR_ID=" + mRooId + " order by EQUIPMENT_NO" };
		List<T9MeetingEquipment > equipments=orm.loadListSingle(dbConn, T9MeetingEquipment.class, filters);
		if (equipments!=null && equipments.size()>0) {
			for(T9MeetingEquipment equipment:equipments){
				int equId = equipment.getSeqId();
				String equipmentName = T9Utility.null2Empty(equipment.getEquipmentName());
				buffer.append("<input type='checkbox' name='checkStr' id='SB_" + equId + "' value='" + equId + "' > ");
				buffer.append("<label title='' for='SB_" + equId + "'>");
				buffer.append(T9Utility.encodeSpecial(equipmentName));
				buffer.append("</label>");
				isHave = true;
			}
		}
		return isHave;
	}
	
	

	
	

	/**
	 * 获取下拉列表值	 * @param dbConn
	 * @param parentNo
	 * @return
	 * @throws Exception
	 */
	public String getSelectOption(Connection dbConn, String classNo) throws Exception {
		String data = "";
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		StringBuffer sb = new StringBuffer("[");
		classNo = T9Utility.null2Empty(classNo);
		String query = "select SEQ_ID,CLASS_CODE,CLASS_DESC from CODE_ITEM where CLASS_NO='" + classNo + "'";
		Statement stm1 = null;
		ResultSet rs1 = null;
		try {
			stm1 = dbConn.createStatement();
			rs1 = stm1.executeQuery(query);
			while (rs1.next()) {
				Map<Object, Object> objMap = new HashMap<Object, Object>();
				objMap.put("seqId", rs1.getInt("SEQ_ID"));
				objMap.put("codeNo", T9Utility.null2Empty(rs1.getString("CLASS_CODE")));
				objMap.put("codeName", T9Utility.null2Empty(rs1.getString("CLASS_DESC")));
				list.add(objMap);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			T9DBUtility.close(stm1, rs1, log);
		}
		boolean isHave = false;
		if (list != null && list.size() > 0) {
			for (Map<Object, Object> map : list) {
				int seqId = (Integer) map.get("seqId");
				String codeNo = (String) map.get("codeNo");
				String codeName = (String) map.get("codeName");
				sb.append("{");
				sb.append("seqId:\"" + seqId + "\"");
				sb.append(",value:\"" + T9Utility.encodeSpecial(codeNo) + "\"");
				sb.append(",text:\"" + T9Utility.encodeSpecial(codeName) + "\"");
				sb.append("},");
				isHave = true;
			}
			if (isHave) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append("]");
		} else {
			sb.append("]");
		}
		data = sb.toString();
		return data;
	}
	
	/**
	 * 获取设备详细信息--wyw
	 * @param conn
	 * @param seqId
	 * @return
	 * @throws Exception
	 */
	public T9MeetingEquipment getEquipmentLogic(Connection dbConn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9MeetingEquipment) orm.loadObjSingle(dbConn, T9MeetingEquipment.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} 
	}
	
	/**
	 * 更新设备信息--wyw
	 * @param dbConn
	 * @param equipment
	 * @throws Exception 
	 */
	public void updateEquipmentLogic(Connection dbConn,T9MeetingEquipment equipment) throws Exception{
		T9ORM orm = new T9ORM();
		try {
			orm.updateSingle(dbConn, equipment);
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 获取CODE_ITEM表设备中的CLASS_CODE-- wyw
	 * @param dbConn
	 * @param classCode
	 * @return
	 * @throws Exception
	 */
	public String getEquClassDescName(Connection dbConn,String classCode) throws Exception{
		String returnData = "";
		if (T9Utility.isNullorEmpty(classCode)) {
			classCode = "";
		}
		String sql  = "select SEQ_ID,CLASS_CODE,CLASS_DESC from CODE_ITEM where CLASS_NO='MEETING_EQUIPMENT' and CLASS_CODE='" + classCode + "'";
		PreparedStatement stmt =null;
		ResultSet rs=null;
		try {
			stmt =dbConn.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				returnData = T9Utility.null2Empty(rs.getString("CLASS_DESC"));
			}
		} catch (Exception e) {
			throw e;
		}finally{
			T9DBUtility.close(stmt, rs, log);
		}
		
		return returnData;
	}
	
	
	
	
	

}
