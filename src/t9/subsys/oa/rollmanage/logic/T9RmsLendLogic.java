package t9.subsys.oa.rollmanage.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.funcs.sms.data.T9SmsBack;
import t9.core.funcs.sms.logic.T9SmsUtil;
import t9.core.funcs.workflow.util.T9WorkFlowUtility;
import t9.core.load.T9PageLoader;
import t9.core.util.T9Utility;
import t9.core.util.db.T9DBUtility;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsFile;
import t9.subsys.oa.rollmanage.data.T9RmsLend;

public class T9RmsLendLogic {
	private static Logger log = Logger.getLogger("t9.subsys.oa.rollmanage.logic.T9RmsLendLogic.java");

	/**
   * 是否需要审批
   * 
   * @param conn
   * @param secret
   * @return
   * @throws Exception
   */
  public boolean isNeedApprove(Connection conn, int rollId , T9Person user) throws Exception {
    String sql = "select PRIV_USER,PRIV_DEPT,PRIV_ROLE from rms_file where SEQ_ID=" + rollId;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = conn.prepareStatement(sql);
      rs = ps.executeQuery();
      if (rs.next()) {
        String privUser = T9Utility.null2Empty(rs.getString(1));
        String privDept = T9Utility.null2Empty( rs.getString(2));
        String privRole = T9Utility.null2Empty( rs.getString(3));
        
        if( "0".equals(privDept)
            || "ALL_DEPT".equals(privDept)
            || T9WorkFlowUtility.findId(privUser,String.valueOf(user.getSeqId())) 
            || T9WorkFlowUtility.findId(privDept,String.valueOf(user.getDeptId())) 
            || T9WorkFlowUtility.findId(privRole,user.getUserPriv())
            || !T9WorkFlowUtility.checkId(privRole , user.getUserPrivOther() ,true).equals("")
            || !T9WorkFlowUtility.checkId(privDept , user.getDeptIdOther() ,true).equals("")){
          return true;
        }
      }
    } catch (Exception e) {
      throw e;
    } finally {
      T9DBUtility.close(ps, rs, log);
    }
    return false;
  }
	
	/**
	 * 获取审批人	 * 
	 * @param conn
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public String getRmsLendManage(Connection conn, int rollId) throws Exception {
		String result = "";
		String sql = "select MANAGER from RMS_ROLL where SEQ_ID=" + rollId;
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
	 * 借阅时短信提醒	 * 
	 * @param conn
	 * @param content
	 * @param fromId
	 * @param toId
	 * @param type
	 * @param remindUrl
	 * @throws Exception
	 */
	public static void doSmsBack(Connection conn, String content, int fromId, String toId, String type, String remindUrl) throws Exception {
		T9SmsBack sb = new T9SmsBack();
		sb.setContent(content);
		sb.setFromId(fromId);
		sb.setToId(toId);
		sb.setSmsType(type);
		sb.setRemindUrl(remindUrl);
		T9SmsUtil.smsBack(conn, sb);
	}

	/**
	 * 借阅查询
	 * 
	 * @param dbConn
	 * @param request
	 * @param person
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public String queryLendFileLogic(Connection dbConn, Map request, T9Person person, Map<Object, Object> map) throws Exception {

		String roomName = (String) map.get("roomName");
		String rollName = (String) map.get("rollName");
		String fileCode = (String) map.get("fileCode");
		String fileSubject = (String) map.get("fileSubject");

		String fileTitle = (String) map.get("fileTitle");
		String fileTitleo = (String) map.get("fileTitleo");
		String sendUnit = (String) map.get("sendUnit");
		String remark = (String) map.get("remark");
		String sendTimeMin = (String) map.get("sendTimeMin");
    String sendTimeMax = (String) map.get("sendTimeMax");
    String fileWord = (String) map.get("fileWord");
    String fileYear = (String) map.get("fileYear");
    String  issueNum= (String) map.get("issueNum");
    
		String conditionStr = "";
		String roomIdStr = "";
		String rollIdStr = "";
		T9PageDataList pageDataList = null;
		try {
			if (!T9Utility.isNullorEmpty(roomName)) {
				String sql = "SELECT SEQ_ID from RMS_ROLL_ROOM where ROOM_NAME like '%" + T9DBUtility.escapeLike(roomName) + "%'" + T9DBUtility.escapeLike();
				roomIdStr = this.getRoomIdStr(dbConn, sql);
			}
			if (!T9Utility.isNullorEmpty(roomIdStr)) {
				if (!T9Utility.isNullorEmpty(rollName)) {
					String sql = "SELECT SEQ_ID from RMS_ROLL  where ROOM_ID in (" + roomIdStr + ") or ROLL_NAME like '%" + T9DBUtility.escapeLike(rollName)
							+ "%'" + T9DBUtility.escapeLike();
					rollIdStr = this.getRollIdStr(dbConn, sql);
				} else {
					String sql = "SELECT SEQ_ID from RMS_ROLL  where ROOM_ID in (" + roomIdStr + ")";
					rollIdStr = this.getRollIdStr(dbConn, sql);
				}
			} else if (!T9Utility.isNullorEmpty(rollName)) {
				String sql = "SELECT SEQ_ID from RMS_ROLL  where ROLL_NAME like '%" + T9DBUtility.escapeLike(rollName) + "%'" + T9DBUtility.escapeLike();
				rollIdStr = this.getRollIdStr(dbConn, sql);
			} else {
				String sql = "SELECT SEQ_ID from RMS_ROLL ";
				rollIdStr = this.getRollIdStr(dbConn, sql);
			}
			if (!T9Utility.isNullorEmpty(fileCode)) {
				conditionStr += " and FILE_CODE like '%" + T9DBUtility.escapeLike(fileCode) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileSubject)) {
				conditionStr += " and FILE_SUBJECT like '%" + T9DBUtility.escapeLike(fileSubject) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileTitle)) {
				conditionStr += " and FILE_TITLE like '%" + T9DBUtility.escapeLike(fileTitle) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(fileTitleo)) {
				conditionStr += " and FILE_TITLEO like '%" + T9DBUtility.escapeLike(fileTitleo) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(sendUnit)) {
				conditionStr += " and SEND_UNIT like '%" + T9DBUtility.escapeLike(sendUnit) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(remark)) {
				conditionStr += " and REMARK like '%" + T9DBUtility.escapeLike(remark) + "%'" + T9DBUtility.escapeLike();
			}
			if (!T9Utility.isNullorEmpty(sendTimeMin)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("SEND_DATE", sendTimeMin, ">=");
      }
      if (!T9Utility.isNullorEmpty(sendTimeMax)) {
        conditionStr += " and " + T9DBUtility.getDateFilter("SEND_DATE", sendTimeMax, "<=");
      }
			if (!T9Utility.isNullorEmpty(roomName) || !T9Utility.isNullorEmpty(rollName)) {
				if (!T9Utility.isNullorEmpty(rollIdStr)) {
					conditionStr += "  and ROLL_ID in (" + rollIdStr + ")";
				}
			}
			if (!T9Utility.isNullorEmpty(fileWord)) {
        conditionStr += " and FILE_WORD like  '%" + T9DBUtility.escapeLike(fileWord) + "%'" + T9DBUtility.escapeLike();
      }
      if (!T9Utility.isNullorEmpty(fileYear)) {
        conditionStr += " and FILE_YEAR = '" + T9DBUtility.escapeLike(fileYear) + "'";
      }
      if (!T9Utility.isNullorEmpty(issueNum)) {
        conditionStr += " and ISSUE_NUM like  '%" + T9DBUtility.escapeLike(issueNum) + "%'" + T9DBUtility.escapeLike();
      }
			
			String query = "SELECT SEQ_ID,FILE_CODE,FILE_TITLE,SECRET,SEND_UNIT,SEND_DATE,URGENCY,ROLL_ID from RMS_FILE where (DEL_USER='' or del_user is null) "
					+ conditionStr;

			T9PageQueryParam queryParam = (T9PageQueryParam) T9FOM.build(request);
			pageDataList = T9PageLoader.loadPageList(dbConn, queryParam, query);

		} catch (Exception e) {
			throw e;
		}
		return pageDataList.toJson();

	}

	/**
	 * 获取卷库id
	 * 
	 * @param dbConn
	 * @param roomName
	 * @return
	 * @throws Exception
	 */
	public String getRoomIdStr(Connection dbConn, String sql) throws Exception {
		String roomIdStr = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String seqId = rs.getString(1);
				if (seqId != null) {
					roomIdStr += seqId + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}

		if (roomIdStr.trim().endsWith(",")) {
			roomIdStr = roomIdStr.substring(0, roomIdStr.length() - 1);
		}

		return roomIdStr;
	}

	/**
	 * 获取案卷id
	 * 
	 * @param dbConn
	 * @param roomName
	 * @return
	 * @throws Exception
	 */
	public String getRollIdStr(Connection dbConn, String sql) throws Exception {
		String rollIdStr = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = dbConn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String seqId = rs.getString(1);
				if (seqId != null) {
					rollIdStr += seqId + ",";
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			T9DBUtility.close(ps, rs, log);
		}
		if (rollIdStr.trim().endsWith(",")) {
			rollIdStr = rollIdStr.substring(0, rollIdStr.length() - 1);
		}
		return rollIdStr;
	}

	
/**
 * 待批准借阅
 * @param dbConn
 * @param person
 * @return
 * @throws Exception
 */
	public StringBuffer getApprovalToBorrow(Connection dbConn, T9Person person, String allow) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9RmsFile rmsFile = null;
    T9RmsLend rmsLend = null;
    int seqId = 0;
    int fileId = 0;
    String fileCode = "";
    Date addTime = null;
    Date returnTime = null;
    Date allowTime = null;
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    try {
      stmt = dbConn.createStatement();
      String sql = "select "
          + "RMS_LEND.SEQ_ID"
          + ",RMS_LEND.FILE_ID"
          + ",RMS_FILE.FILE_CODE"
          + ",RMS_LEND.ADD_TIME"
          + ",RMS_LEND.RETURN_TIME"
          + ",RMS_LEND.ALLOW_TIME"
          + " from RMS_LEND left join RMS_FILE on RMS_FILE.SEQ_ID = RMS_LEND.FILE_ID where RMS_LEND.USER_ID =" + person.getSeqId() + " and ALLOW = '" + allow + "'";
      if ("0".equals(allow)) {
        sql = sql + " order by RMS_LEND.ADD_TIME desc";
      } else if ("3".equals(allow)) {
        sql = sql + " order by RMS_LEND.RETURN_TIME desc";
      } else {
        sql = sql + " order by RMS_LEND.ALLOW_TIME desc";
      }
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
      	sb.append("{");
        rmsFile = new T9RmsFile();
        rmsLend = new T9RmsLend();
        seqId = rs.getInt("SEQ_ID");
        fileId =  rs.getInt("FILE_ID");
        fileCode = rs.getString("FILE_CODE");
        addTime = rs.getTimestamp("ADD_TIME");
        returnTime = rs.getTimestamp("RETURN_TIME");
        allowTime = rs.getTimestamp("ALLOW_TIME");
        sb.append("seqId:\"" + seqId + "\"");
        sb.append(",fileId:\"" + fileId + "\"");
        sb.append(",fileCode:\"" + (fileCode == null ? "" : T9Utility.encodeSpecial(fileCode)) + "\"");
        sb.append(",addTime:\"" + (addTime == null ? "" : String.valueOf(addTime).subSequence(0, String.valueOf(addTime).length() - 2)) + "\"");
        sb.append(",returnTime:\"" + (returnTime == null ? "" : String.valueOf(returnTime).subSequence(0, String.valueOf(returnTime).length() - 2)) + "\"");
        sb.append(",allowTime:\"" + (allowTime == null ? "" : String.valueOf(allowTime).subSequence(0, String.valueOf(allowTime).length() - 2)) + "\"");
        sb.append("},");
      }
      if(sb.length() > 1){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sb;
  }

	/**
	 * 已批准借阅
	 * @param dbConn
	 * @param person
	 * @return
	 * @throws Exception
	 */
  public StringBuffer getApprovaledLend(Connection dbConn, T9Person person, String allow) throws Exception {
    Statement stmt = null;
    ResultSet rs = null;
    T9RmsFile rmsFile = null;
    T9RmsLend rmsLend = null;
    int seqId = 0;
    int fileId = 0;
    String fileCode = "";
    Date addTime = null;
    Date returnTime = null;
    Date allowTime = null;
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    try {
      stmt = dbConn.createStatement();
      String sql = "select "
          + "RMS_LEND.SEQ_ID"
          + ",RMS_LEND.FILE_ID"
          + ",RMS_FILE.FILE_CODE"
          + ",RMS_LEND.ADD_TIME"
          + ",RMS_LEND.RETURN_TIME"
          + ",RMS_LEND.ALLOW_TIME"
          + " , u.USER_NAME"
          + " from RMS_LEND left join RMS_FILE on RMS_FILE.SEQ_ID = RMS_LEND.FILE_ID left join PERSON u on u.SEQ_ID = RMS_LEND.USER_ID where ALLOW = '" + allow + "' and ((RMS_LEND.APPROVE = '' or RMS_LEND.APPROVE is null) or RMS_LEND.APPROVE = '" + String.valueOf(person.getSeqId()) + "')";
      if ("0".equals(allow)) {
        sql = sql + " order by RMS_LEND.ADD_TIME desc";
      } else if ("3".equals(allow)) {
        sql = sql + " order by RMS_LEND.RETURN_TIME desc";
      } else {
        sql = sql + " order by RMS_LEND.ALLOW_TIME desc";
      }
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        sb.append("{");
        rmsFile = new T9RmsFile();
        rmsLend = new T9RmsLend();
        seqId = rs.getInt("SEQ_ID");
        fileId =  rs.getInt("FILE_ID");
        fileCode = rs.getString("FILE_CODE");
        addTime = rs.getTimestamp("ADD_TIME");
        returnTime = rs.getTimestamp("RETURN_TIME");
        allowTime = rs.getTimestamp("ALLOW_TIME");
        String userName = rs.getString("USER_NAME");
        sb.append("seqId:\"" + seqId + "\"");
        sb.append(",fileId:\"" + fileId + "\"");
        sb.append(",fileCode:\"" + (fileCode == null ? "" : T9Utility.encodeSpecial(fileCode)) + "\"");
        sb.append(",addTime:\"" + (addTime == null ? "" : String.valueOf(addTime).subSequence(0, String.valueOf(addTime).length() - 2)) + "\"");
        sb.append(",returnTime:\"" + (returnTime == null ? "" : String.valueOf(returnTime).subSequence(0, String.valueOf(returnTime).length() - 2)) + "\"");
        sb.append(",allowTime:\"" + (allowTime == null ? "" : String.valueOf(allowTime).subSequence(0, String.valueOf(allowTime).length() - 2)) + "\"");
        sb.append(",userName:\"" + T9Utility.encodeSpecial(T9Utility.null2Empty(userName)) + "\"");
        sb.append("},");
      }
      if(sb.length() > 1){
        sb.deleteCharAt(sb.length() - 1);
      }
      sb.append("]");
    } catch (Exception ex) {
      throw ex;
    } finally {
      T9DBUtility.close(stmt, rs, log);
    }
    return sb;
  }
}
