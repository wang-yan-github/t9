package t9.subsys.oa.meeting.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.meeting.data.T9MeetingRoom;

public class T9MeetingRoomLogic {

	private static Logger log = Logger.getLogger("t9.subsys.oa.meeting.logic.T9MeetingRoomLogic.java");

	public String getMeetingRoomJson(Connection dbConn, Map request) throws Exception {
		String sql = "";
		sql = "select " + "  SEQ_ID" + ", MR_NAME" + ", MR_CAPACITY" + ", MR_DEVICE" + ", MR_PLACE" + ", MR_DESC" + ", OPERATOR" + ", TO_ID"
				+ ", SECRET_TO_ID" + " from MEETING_ROOM  order by SEQ_ID";

		T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
		T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, sql);
		return pageDataList.toJson();
	}

	public T9MeetingRoom getMeetingRoomDetail(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			return (T9MeetingRoom) orm.loadObjSingle(conn, T9MeetingRoom.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} finally {

		}
	}

	/**
	 * 编辑案卷
	 * 
	 * @param conn
	 * @param rmsRoll
	 * @throws Exception
	 */
	public void updateMeetingRoom(Connection conn, T9MeetingRoom meetingRoom) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.updateSingle(conn, meetingRoom);
		} catch (Exception ex) {
			throw ex;
		} finally {
		}
	}

	/**
	 * 删除单个会议室记录
	 * 
	 * @param conn
	 * @param seqId
	 * @throws Exception
	 */
	public void deleteSingle(Connection conn, int seqId) throws Exception {
		try {
			T9ORM orm = new T9ORM();
			orm.deleteSingle(conn, T9MeetingRoom.class, seqId);
		} catch (Exception ex) {
			throw ex;
		} finally {
		}
	}

	/**
	 * 全部删除会议室记录
	 * 
	 * @param conn
	 * @throws Exception
	 */
	public void deleteAll(Connection conn) throws Exception {
		String sql = "delete from MEETING_ROOM";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(pstmt, null, null);
		}
	}

	/**
	 * 获取会议详细信息 --wyw
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public String getRoomDetailLogic(Connection dbConn, T9Person person) throws Exception {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		T9ORM orm = new T9ORM();
		StringBuffer buffer=new StringBuffer("[");
		
		try {
			List<T9MeetingRoom> mRooms = orm.loadListSingle(dbConn, T9MeetingRoom.class, new HashMap());
			if (mRooms != null && mRooms.size() > 0) {
				for(T9MeetingRoom mRoom:mRooms){
					String mrName=T9Utility.null2Empty(mRoom.getMrName());
					String mrCapacity=T9Utility.null2Empty(mRoom.getMrCapacity());
					String mrDevice=T9Utility.null2Empty(mRoom.getMrDevice());
					String mrDesc=T9Utility.null2Empty(mRoom.getMrDesc());
					String mrPlace=T9Utility.null2Empty(mRoom.getMrPlace());
					String secretToId=T9Utility.null2Empty(mRoom.getSecretToId());
					String operator=T9Utility.null2Empty(mRoom.getOperator());
					String toId=T9Utility.null2Empty(mRoom.getToId());
					boolean secretToIdPriv=this.isHavePriv(person.getSeqId(), secretToId, dbConn);
					boolean operatorPriv=this.isHavePriv(person.getSeqId(), operator, dbConn);
					boolean toIdPriv=this.getDeptIdPriv(person, toId, dbConn);
					
					if (secretToIdPriv || operatorPriv || toIdPriv || ("".equals(toId) &&  "".equals(secretToId)) ) {
						buffer.append("{");
						buffer.append("seqId:" + mRoom.getSeqId() );
						buffer.append(",mrName:\"" + T9Utility.encodeSpecial(mrName) + "\"");
						buffer.append(",mrCapacity:\"" + T9Utility.encodeSpecial(mrCapacity) + "\"");
						buffer.append(",mrDevice:\"" + T9Utility.encodeSpecial(mrDevice) + "\"");
						buffer.append(",mrDesc:\"" + T9Utility.encodeSpecial(mrDesc) + "\"");
						buffer.append(",mrPlace:\"" + T9Utility.encodeSpecial(mrPlace) + "\"");
						buffer.append("},");
					}
				}
			}
			if(buffer.length() > 1){
				buffer.deleteCharAt(buffer.length() - 1);
      }
			buffer.append("]");

		} catch (Exception e) {
			throw e;
		}
		return buffer.toString();
	}
	
	/**
	 * 根据登录用户的部门Id与权限中的部门Id对比返回boolean
	 * 
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean getDeptIdPriv(T9Person person, String ids, Connection dbConn) throws Exception, Exception {
		boolean flag = false;
		int loginUserDeptId=person.getDeptId();
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
	
	/**
	 * 获取访问权限：根据ids串返回与登录的seqId比较判断是否相等，返回boolean类型。
	 * @param ids
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean isHavePriv(int userSeqId, String ids, Connection dbConn) throws Exception, Exception {
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
}
