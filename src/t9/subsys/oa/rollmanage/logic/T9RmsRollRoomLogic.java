package t9.subsys.oa.rollmanage.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import org.apache.log4j.Logger;

import t9.core.data.T9PageDataList;
import t9.core.data.T9PageQueryParam;
import t9.core.funcs.person.data.T9Person;
import t9.core.load.T9PageLoader;
import t9.core.util.db.T9DBUtility;
import t9.core.util.db.T9ORM;
import t9.core.util.form.T9FOM;
import t9.subsys.oa.rollmanage.data.T9RmsRollRoom;

public class T9RmsRollRoomLogic {
  
 public void add(Connection conn, T9RmsRollRoom rmsRollRoom) throws Exception {
    try {
      T9ORM orm = new T9ORM();
      orm.saveSingle(conn, rmsRollRoom);
    } catch(Exception ex) {
      throw ex;
    } finally {
      
    }
  }
 
 public String getRmsRollRoomJson(Connection dbConn, Map request, T9Person person) throws Exception {
   String sql = "";
   if(person.isAdminRole()){
     sql = "select "
       + "SEQ_ID"
       + ",ROOM_CODE"
       + ",ROOM_NAME"
       + ",DEPT_ID"
       + " from RMS_ROLL_ROOM";
   }else{
     sql = "select "
       + "SEQ_ID"
       + ",ROOM_CODE"
       + ",ROOM_NAME"
       + ",DEPT_ID"
       + " from RMS_ROLL_ROOM where ADD_USER = '" + String.valueOf(person.getSeqId()) + "'";
   }
   T9PageQueryParam queryParam = (T9PageQueryParam)T9FOM.build(request);
   T9PageDataList pageDataList = T9PageLoader.loadPageList(dbConn,queryParam,sql);
   return pageDataList.toJson();
 }
 
 public T9RmsRollRoom getRmsRollRoomDetail(Connection conn,int seqId) throws Exception {
   
   try {
     T9ORM orm = new T9ORM();
     return (T9RmsRollRoom)orm.loadObjSingle(conn, T9RmsRollRoom.class, seqId);
   } catch(Exception ex) {
     throw ex;
   } finally {
     
   }
 }
 
 public void updateRmsRollRoom(Connection conn,T9RmsRollRoom rmsRollRoom) throws Exception {
   try {
     T9ORM orm = new T9ORM();
     orm.updateSingle(conn, rmsRollRoom);
   } catch(Exception ex) {
     throw ex;
   } finally {
   }
 }
 
 /**
  * 删除一条卷库
  * @param conn
  * @param seqId
  * @throws Exception
  */
 public void deleteSingle(Connection conn,int seqId) throws Exception {
   try {
     T9ORM orm = new T9ORM();
     orm.deleteSingle(conn, T9RmsRollRoom.class, seqId);
   } catch(Exception ex) {
     throw ex;
   } finally {
   }
 }
 
 public void deleteAll(Connection conn, String loginUserId, T9Person person) throws Exception {
   String sql = "";
   if(person.isAdminRole()){
     sql = "DELETE FROM RMS_ROLL_ROOM";
   }else{
     sql = "DELETE FROM RMS_ROLL_ROOM WHERE ADD_USER = '" + loginUserId + "'";
   }
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
 
}
